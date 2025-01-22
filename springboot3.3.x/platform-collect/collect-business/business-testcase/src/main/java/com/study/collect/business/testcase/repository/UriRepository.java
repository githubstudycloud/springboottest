package com.study.collect.business.testcase.repository;

import com.google.common.collect.Lists;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.WriteModel;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.study.collect.business.testcase.config.DynamicCollectionIndexConfiguration;
import com.study.collect.business.testcase.constant.CollectionConstants;
import com.study.collect.business.testcase.entity.UriEntity;
import com.study.collect.business.testcase.model.UriQueryCondition;
import com.study.collect.business.testcase.model.param.QueryParam;
import com.study.collect.business.testcase.utils.HashUtil;
import com.study.collect.business.testcase.utils.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class UriRepository {
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int HTTP_BATCH_SIZE = 100;
    private final MongoTemplate mongoTemplate;
    private final DynamicCollectionIndexConfiguration indexConfiguration;
    private final RateLimiter mongoRateLimiter;

    public UriRepository(MongoTemplate mongoTemplate,
                         DynamicCollectionIndexConfiguration indexConfiguration,
                         RateLimiter mongoRateLimiter) {
        this.mongoTemplate = mongoTemplate;
        this.indexConfiguration = indexConfiguration;
        this.mongoRateLimiter = mongoRateLimiter;
    }

    /**
     * 查询指定版本的URI列表
     */
    public Page<String> findUrisByVersion(String rootNode, String version, Pageable pageable) {
        try {
            mongoRateLimiter.acquire();

            Query query = new Query(Criteria.where("root_node").is(rootNode)
                    .and("uri_version").is(version)
                    .and("is_deleted").is(false));
            query.with(pageable);
            query.fields().include("uri");

            long total = mongoTemplate.count(query, UriEntity.class, getCollectionName(rootNode));
            List<UriEntity> entities = mongoTemplate.find(query, UriEntity.class, getCollectionName(rootNode));

            List<String> uris = entities.stream()
                    .map(UriEntity::getUri)
                    .collect(Collectors.toList());

            return new PageImpl<>(uris, pageable, total);
        } catch (Exception e) {
            log.error("Failed to find URIs by version for rootNode: {}, version: {}", rootNode, version, e);
            throw new RuntimeException("Failed to find URIs by version", e);
        }
    }

    /**
     * 统计指定根节点和版本的URI数量
     */
    public long countByRootNodeAndVersion(String rootNode, String version) {
        try {
            mongoRateLimiter.acquire();
            Query query = new Query(Criteria.where("root_node").is(rootNode)
                    .and("uri_version").is(version)
                    .and("is_deleted").is(false));
            return mongoTemplate.count(query, UriEntity.class, getCollectionName(rootNode));
        } catch (Exception e) {
            log.error("Failed to count URIs for rootNode: {} and version: {}", rootNode, version, e);
            throw new RuntimeException("Failed to count URIs", e);
        }
    }

    /**
     * 条件查询
     */
    public Page<UriEntity> findByConditions(QueryParam param) {
        try {
            mongoRateLimiter.acquire();

            Criteria criteria = new Criteria();
            if (StringUtils.hasText(param.getRootNode())) {
                criteria.and("root_node").is(param.getRootNode());
            }
            if (StringUtils.hasText(param.getVersion())) {
                criteria.and("uri_version").is(param.getVersion());
            }
            if (StringUtils.hasText(param.getVersionType())) {
                criteria.and("version_type").is(param.getVersionType());
            }
            if (!param.getIncludeDeleted()) {
                criteria.and("is_deleted").is(false);
            }
            if (param.getOnlyDeleted()) {
                criteria.and("is_deleted").is(true);
            }
            if (!CollectionUtils.isEmpty(param.getUris())) {
                List<String> hashes = param.getUris().stream()
                        .map(HashUtil::hash)
                        .collect(Collectors.toList());
                criteria.and("uri_hash").in(hashes);
            }

            Pageable pageable = PageRequest.of(
                    param.getPage() - 1,
                    param.getSize() != null ? param.getSize() : DEFAULT_PAGE_SIZE
            );

            Query query = new Query(criteria).with(pageable);
            String collectionName = getCollectionName(param.getRootNode());

            long total = mongoTemplate.count(query, UriEntity.class, collectionName);
            List<UriEntity> content = mongoTemplate.find(query, UriEntity.class, collectionName);

            return new PageImpl<>(content, pageable, total);
        } catch (Exception e) {
            log.error("Failed to find URIs by conditions: {}", param, e);
            throw new RuntimeException("Failed to find URIs by conditions", e);
        }
    }

//    private String getCollectionName(String rootNode) {
//        return String.format("%s_%s", CollectionConstants.URI_COLLECTION_PREFIX, rootNode);
//    }

    /**
     * 批量更新或插入
     */
    public BulkWriteResult batchUpsert(String rootNode, List<UriEntity> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return null;
        }

        try {
            mongoRateLimiter.acquire();
            String collectionName = getCollectionName(rootNode);

            // 确保表和索引存在
            ensureCollectionAndIndexes(rootNode, collectionName);

            List<WriteModel<Document>> operations = new ArrayList<>();
            for (UriEntity entity : entities) {
                // 确保 uriHash 存在
                if (entity.getUriHash() == null && entity.getUri() != null) {
                    entity.setUriHash(HashUtil.hash(entity.getUri()));
                }

                Document query = new Document("uri_hash", entity.getUriHash());
                Document doc = convertEntityToDocument(entity);

                UpdateOneModel<Document> updateOne = new UpdateOneModel<>(
                        query,
                        new Document("$set", doc),
                        new UpdateOptions().upsert(true)
                );
                operations.add(updateOne);
            }

            BulkWriteOptions options = new BulkWriteOptions()
                    .ordered(false)
                    .bypassDocumentValidation(true);

            return mongoTemplate.getCollection(collectionName)
                    .bulkWrite(operations, options);

        } catch (Exception e) {
            log.error("Failed to batch upsert entities for rootNode: {}", rootNode, e);
            throw new RuntimeException("Batch upsert failed", e);
        }
    }

    /**
     * 分页批量软删除
     */
    public long batchSoftDelete(String rootNode, List<String> uris, int batchSize) {
        if (CollectionUtils.isEmpty(uris)) {
            return 0L;
        }

        long totalDeleted = 0;
        List<List<String>> batches = Lists.partition(uris, batchSize);

        for (List<String> batch : batches) {
            try {
                mongoRateLimiter.acquire();

                List<String> uriHashes = batch.stream()
                        .map(HashUtil::hash)
                        .collect(Collectors.toList());

                Query query = new Query(Criteria.where("uri_hash").in(uriHashes));
                Update update = new Update()
                        .set("is_deleted", true)
                        .set("update_time", LocalDateTime.now());

                UpdateResult result = mongoTemplate.updateMulti(
                        query, update, getCollectionName(rootNode)
                );

                totalDeleted += result.getModifiedCount();

            } catch (Exception e) {
                log.error("Failed to batch soft delete uris for batch size: {}", batch.size(), e);
                throw new RuntimeException("Batch soft delete failed", e);
            }
        }

        return totalDeleted;
    }

    /**
     * 分页批量硬删除
     */
    public long batchHardDelete(String rootNode, List<String> uris, int batchSize) {
        if (CollectionUtils.isEmpty(uris)) {
            return 0L;
        }

        long totalDeleted = 0;
        List<List<String>> batches = Lists.partition(uris, batchSize);

        for (List<String> batch : batches) {
            try {
                mongoRateLimiter.acquire();

                List<String> uriHashes = batch.stream()
                        .map(HashUtil::hash)
                        .collect(Collectors.toList());

                Query query = new Query(Criteria.where("uri_hash").in(uriHashes));
                DeleteResult result = mongoTemplate.remove(
                        query,
                        UriEntity.class,
                        getCollectionName(rootNode)
                );

                totalDeleted += result.getDeletedCount();

            } catch (Exception e) {
                log.error("Failed to batch hard delete uris for batch size: {}", batch.size(), e);
                throw new RuntimeException("Batch hard delete failed", e);
            }
        }

        return totalDeleted;
    }

    /**
     * 条件查询
     */
    public Page<UriEntity> findByConditions(UriQueryCondition condition) {
        try {
            mongoRateLimiter.acquire();

            Criteria criteria = new Criteria();
            if (StringUtils.hasText(condition.getRootNode())) {
                criteria.and("root_node").is(condition.getRootNode());
            }
            if (StringUtils.hasText(condition.getVersion())) {
                criteria.and("uri_version").is(condition.getVersion());
            }
            if (condition.getThirdPartyUpdateTimeStart() != null) {
                criteria.and("third_party_update_time")
                        .gte(condition.getThirdPartyUpdateTimeStart());
            }
            if (condition.getThirdPartyUpdateTimeEnd() != null) {
                criteria.and("third_party_update_time")
                        .lte(condition.getThirdPartyUpdateTimeEnd());
            }
            if (condition.getIsDeleted() != null) {
                criteria.and("is_deleted").is(condition.getIsDeleted());
            }

            Query query = new Query(criteria).with(condition.getPageable());
            if (condition.isOnlyDetail()) {
                query.fields().include("details");
            }

            long total = mongoTemplate.count(query, UriEntity.class,
                    getCollectionName(condition.getRootNode()));
            List<UriEntity> content = mongoTemplate.find(query, UriEntity.class,
                    getCollectionName(condition.getRootNode()));

            return new PageImpl<>(content, condition.getPageable(), total);

        } catch (Exception e) {
            log.error("Failed to query URIs with condition: {}", condition, e);
            throw new RuntimeException("Query failed", e);
        }
    }

    /**
     * 批量查询
     */
    public List<UriEntity> batchQuery(List<String> uris, Boolean includeDeleted, Boolean onlyDetail) {
        if (CollectionUtils.isEmpty(uris)) {
            return Collections.emptyList();
        }

        try {
            mongoRateLimiter.acquire();

            List<String> uriHashes = uris.stream()
                    .map(HashUtil::hash)
                    .collect(Collectors.toList());

            Criteria criteria = Criteria.where("uri_hash").in(uriHashes);
            if (!Boolean.TRUE.equals(includeDeleted)) {
                criteria.and("is_deleted").is(false);
            }

            Query query = new Query(criteria);
            if (Boolean.TRUE.equals(onlyDetail)) {
                query.fields().include("details");
            }

            return mongoTemplate.find(query, UriEntity.class);

        } catch (Exception e) {
            log.error("Failed to batch query URIs", e);
            throw new RuntimeException("Batch query failed", e);
        }
    }

    /**
     * 根据更新时间范围查询
     */
    public Page<UriEntity> findByUpdateTimeRange(String rootNode,
                                                 LocalDateTime startTime,
                                                 LocalDateTime endTime,
                                                 Pageable pageable) {
        try {
            mongoRateLimiter.acquire();

            Criteria criteria = Criteria.where("root_node").is(rootNode)
                    .and("third_party_update_time").gte(startTime);

            if (endTime != null) {
                criteria.and("third_party_update_time").lte(endTime);
            }

            Query query = new Query(criteria).with(pageable);

            long total = mongoTemplate.count(query, UriEntity.class, getCollectionName(rootNode));
            List<UriEntity> content = mongoTemplate.find(query, UriEntity.class,
                    getCollectionName(rootNode));

            return new PageImpl<>(content, pageable, total);

        } catch (Exception e) {
            log.error("Failed to query URIs by update time range for rootNode: {}", rootNode, e);
            throw new RuntimeException("Query by update time failed", e);
        }
    }

    /**
     * 统计根节点下的URI数量
     */
    public long countByRootNode(String rootNode) {
        try {
            mongoRateLimiter.acquire();
            return mongoTemplate.count(
                    Query.query(Criteria.where("root_node").is(rootNode)
                            .and("is_deleted").is(false)),
                    UriEntity.class,
                    getCollectionName(rootNode)
            );
        } catch (Exception e) {
            log.error("Failed to count URIs for rootNode: {}", rootNode, e);
            throw new RuntimeException("Count failed", e);
        }
    }

    /**
     * 获取URI的更新时间
     */
    public Map<String, LocalDateTime> findUpdateTimesByUris(List<String> uris) {
        if (CollectionUtils.isEmpty(uris)) {
            return Collections.emptyMap();
        }

        try {
            mongoRateLimiter.acquire();

            List<String> uriHashes = uris.stream()
                    .map(HashUtil::hash)
                    .collect(Collectors.toList());

            Query query = Query.query(Criteria.where("uri_hash").in(uriHashes));
            query.fields().include("uri", "third_party_update_time");

            List<UriEntity> entities = mongoTemplate.find(query, UriEntity.class);
            return entities.stream()
                    .collect(Collectors.toMap(
                            UriEntity::getUri,
                            UriEntity::getThirdPartyUpdateTime,
                            (existing, replacement) -> existing
                    ));

        } catch (Exception e) {
            log.error("Failed to find update times for URIs", e);
            throw new RuntimeException("Find update times failed", e);
        }
    }

    private String getCollectionName(String rootNode) {
        return String.format("%s_%s", CollectionConstants.URI_COLLECTION_PREFIX, rootNode);
    }

    private void ensureCollectionAndIndexes(String rootNode, String collectionName) {
        if (!mongoTemplate.collectionExists(collectionName)) {
            indexConfiguration.createIndexesForCollection(collectionName);
        }
    }

    private Document convertEntityToDocument(UriEntity entity) {
        Document doc = new Document();
        doc.put("uri", entity.getUri());
        doc.put("uri_hash", entity.getUriHash());
        doc.put("root_node", entity.getRootNode());
        doc.put("version_type", entity.getVersionType());
        doc.put("uri_version", entity.getUriVersion());
        doc.put("real_uri", entity.getRealUri());
        doc.put("number", entity.getNumber());
        doc.put("name", entity.getName());
        doc.put("third_party_update_time", entity.getThirdPartyUpdateTime());
        doc.put("details", entity.getDetails());
        doc.put("is_deleted", false);
        doc.put("update_time", LocalDateTime.now());

        if (entity.getCreateTime() == null) {
            doc.put("create_time", LocalDateTime.now());
        }

        return doc;
    }

    /**
     * 使用原生命令分页查询uri_hash
     *
     * @param rootNode    根节点
     * @param version     版本号
     * @param versionType 版本类型
     * @param page        页码（从1开始）
     * @param size        每页大小
     * @return uri_hash列表
     */
    public List<String> findUriHashesNativeWithPage(String rootNode,
                                                    String version,
                                                    String versionType,
                                                    int page,
                                                    int size) {
        try {
            mongoRateLimiter.acquire();

            String collectionName = getCollectionName(rootNode);
            MongoCollection<Document> collection = mongoTemplate.getCollection(collectionName);

            // 构建查询条件
            Document query = new Document();
            if (StringUtils.hasText(rootNode)) {
                query.append("root_node", rootNode);
            }
            if (StringUtils.hasText(version)) {
                query.append("uri_version", version);
            }
            if (StringUtils.hasText(versionType)) {
                query.append("version_type", versionType);
            }

            // 构建聚合管道
            List<Document> pipeline = Arrays.asList(
                    new Document("$match", query),
                    new Document("$project", new Document("uri_hash", 1).append("_id", 0)),
                    new Document("$skip", (long) (page - 1) * size),
                    new Document("$limit", size)
            );

            List<String> results = new ArrayList<>();
            collection.aggregate(pipeline)
                    .map(doc -> doc.getString("uri_hash"))
                    .into(results);

            return results;
        } catch (Exception e) {
            log.error("Failed to execute native query for rootNode: {}, version: {}", rootNode, version, e);
            throw new RuntimeException("Query execution failed", e);
        }
    }

    /**
     * 统计满足条件的记录总数
     */
    public long countUriHashesNative(String rootNode,
                                     String version,
                                     Boolean isDeleted) {
        try {
            mongoRateLimiter.acquire();

            String collectionName = getCollectionName(rootNode);
            MongoCollection<Document> collection = mongoTemplate.getCollection(collectionName);

            // 构建查询条件
            Document query = new Document();
            if (StringUtils.hasText(rootNode)) {
                query.append("root_node", rootNode);
            }
            if (StringUtils.hasText(version)) {
                query.append("uri_version", version);
            }
            if (isDeleted != null) {
                query.append("is_deleted", isDeleted);
            }

            return collection.countDocuments(query);
        } catch (Exception e) {
            log.error("Failed to count documents for rootNode: {}, version: {}", rootNode, version, e);
            throw new RuntimeException("Count documents failed", e);
        }
    }
}