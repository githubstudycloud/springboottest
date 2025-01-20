package com.study.collect.business.testcase.repository;

import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.*;
import com.study.collect.business.testcase.config.DynamicCollectionIndexConfiguration;
import com.study.collect.business.testcase.constant.CollectionConstants;
import com.study.collect.business.testcase.entity.UriEntity;
import com.study.collect.business.testcase.model.PageResult;
import com.study.collect.business.testcase.utils.HashUtil;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class UriRepository {
    private final MongoTemplate mongoTemplate;

    private final MongoOperations mongoOperations;
    private final DynamicCollectionIndexConfiguration indexConfiguration;

    public UriRepository(MongoTemplate mongoTemplate,
                   MongoOperations mongoOperations,
                         DynamicCollectionIndexConfiguration indexConfiguration
    ) {
        this.mongoTemplate = mongoTemplate;
        this.mongoOperations = mongoOperations;
        this.indexConfiguration= indexConfiguration;
    }

    /**
     * 生成集合名称
     */
    private String getCollectionName(String rootNode) {
        return String.format("%s_%s", CollectionConstants.URI_COLLECTION_PREFIX, rootNode);
    }

    /**
     * 批量插入或更新
     */
    public BulkWriteResult batchUpsert(String rootNode, List<UriEntity> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return null;
        }


        // 验证所有实体的 uriHash
        entities.forEach(entity -> {
            if (entity.getUriHash() == null && entity.getUri() != null) {
                entity.setUriHash(HashUtil.hash(entity.getUri()));
            }
        });

        String collectionName = getCollectionName(rootNode);

        // 确保索引存在
        ensureIndexes(rootNode,collectionName);
        MongoCollection<Document> collection = mongoTemplate.getCollection(collectionName);

        List<WriteModel<Document>> operations = new ArrayList<>();
        for (UriEntity entity : entities) {
            Document query = new Document("uri_hash", entity.getUriHash());
            Document doc = convertEntityToDocument(entity);
            operations.add(new UpdateOneModel<>(
                    query,
                    new Document("$set", doc),
                    new UpdateOptions().upsert(true)
            ));
        }

        try {
            BulkWriteOptions options = new BulkWriteOptions()
                    .ordered(false)
                    .bypassDocumentValidation(true);
            return collection.bulkWrite(operations, options);
        } catch (Exception e) {
            log.error("Failed to batch upsert to collection {}", collectionName, e);
            throw new RuntimeException("Batch upsert failed", e);
        }
    }


    /**
     * 批量插入或更新
     */
    public BulkWriteResult batchUpsertSync(String rootNode, List<UriEntity> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return null;
        }


        // 验证所有实体的 uriHash
        entities.forEach(entity -> {
            if (entity.getUriHash() == null && entity.getUri() != null) {
                entity.setUriHash(HashUtil.hash(entity.getUri()));
            }
        });

        String collectionName = getCollectionName(rootNode);

        // 确保索引存在
        ensureIndexes(rootNode,collectionName);
        MongoCollection<Document> collection = mongoTemplate.getCollection(collectionName);

        List<WriteModel<Document>> operations = new ArrayList<>();
        for (UriEntity entity : entities) {
            Document query = new Document("uri_hash", entity.getUriHash());
            Document doc = convertEntityToDocument(entity);
            operations.add(new UpdateOneModel<>(
                    query,
                    new Document("$set", doc),
                    new UpdateOptions().upsert(true)
            ));
        }

        try {
            BulkWriteOptions options = new BulkWriteOptions()
//                    .ordered(false)
                    .ordered(true)  // 改为有序执行
                    .bypassDocumentValidation(true);

//            // 记录指标
//            recordMetrics("upsert", timer, entities.size(), result.getModifiedCount());
            // 确保数据已写入
            collection.find(new Document("uri_hash",
                    new Document("$in",
                            entities.stream()
                                    .map(UriEntity::getUriHash)
                                    .collect(Collectors.toList())
                    )
            )).first();
            return collection.bulkWrite(operations, options);
        } catch (Exception e) {
            log.error("Failed to batch upsert to collection {}", collectionName, e);
            throw new RuntimeException("Batch upsert failed", e);
        }
    }


    /**
     * 确保集合索引存在
     */
    private void ensureIndexes(String rootNode,String collectionName) {
        try {
            // 如果集合不存在或索引不完整，创建索引
            if (!mongoTemplate.collectionExists(collectionName)) {
                indexConfiguration.createIndexesForCollection(rootNode);
            } else {
                // 检查索引是否完整
                indexConfiguration.checkIndexes(collectionName);
            }
        } catch (Exception e) {
            log.error("Failed to ensure indexes for rootNode: {}", rootNode, e);
        }
    }

    /**
     * 批量软删除
     */
    public long batchSoftDelete(String rootNode, List<String> uris) {
        if (CollectionUtils.isEmpty(uris)) {
            return 0L;
        }

        List<String> uriHashes = uris.stream()
                .map(HashUtil::hash)
                .collect(Collectors.toList());

        Query query = new Query(Criteria.where("uri_hash").in(uriHashes));
        Update update = new Update()
                .set("is_deleted", true)
                .set("update_time", LocalDateTime.now());

        try {
            return mongoTemplate.updateMulti(
                    query,
                    update,
                    getCollectionName(rootNode)
            ).getModifiedCount();
        } catch (Exception e) {
            log.error("Failed to batch soft delete in collection {}", rootNode, e);
            throw new RuntimeException("Batch soft delete failed", e);
        }
    }

    /**
     * 批量硬删除
     */
    public long batchHardDelete(String rootNode, List<String> uris) {
        if (CollectionUtils.isEmpty(uris)) {
            return 0L;
        }

        List<String> uriHashes = uris.stream()
                .map(HashUtil::hash)
                .collect(Collectors.toList());

        Query query = new Query(Criteria.where("uri_hash").in(uriHashes));

        try {
            return mongoTemplate.remove(
                    query,
                    UriEntity.class,
                    getCollectionName(rootNode)
            ).getDeletedCount();
        } catch (Exception e) {
            log.error("Failed to batch hard delete in collection {}", rootNode, e);
            throw new RuntimeException("Batch hard delete failed", e);
        }
    }

    /**
     * 分页查询
     */
    public Page<UriEntity> findByCondition(
            String rootNode,
            String version,
            String versionType,
            Boolean includeDeleted,
            Pageable pageable
    ) {
        Criteria criteria = new Criteria();

        if (version != null) {
            criteria.and("uri_version").is(version);
        }
        if (versionType != null) {
            criteria.and("version_type").is(versionType);
        }
        if (!includeDeleted) {
            criteria.and("is_deleted").is(false);
        }

        Query query = new Query(criteria).with(pageable);
        String collectionName = getCollectionName(rootNode);

        try {
            long total = mongoTemplate.count(query, UriEntity.class, collectionName);
            List<UriEntity> content = mongoTemplate.find(query, UriEntity.class, collectionName);
            return new PageImpl<>(content, pageable, total);
        } catch (Exception e) {
            log.error("Failed to query collection {}", collectionName, e);
            throw new RuntimeException("Query failed", e);
        }
    }

    /**
     * 批量查询
     */
    public List<UriEntity> batchQuery(
            List<String> uris,
            Function<String, String> rootNodeResolver,
            Boolean includeDeleted
    ) {
        if (CollectionUtils.isEmpty(uris)) {
            return new ArrayList<>();
        }

        // 按rootNode分组
        Map<String, List<String>> groupedUris = uris.stream()
                .collect(Collectors.groupingBy(rootNodeResolver));

        List<UriEntity> results = new ArrayList<>();

        for (Map.Entry<String, List<String>> entry : groupedUris.entrySet()) {
            String rootNode = entry.getKey();
            List<String> uriGroup = entry.getValue();

            List<String> uriHashes = uriGroup.stream()
                    .map(HashUtil::hash)
                    .collect(Collectors.toList());

            Criteria criteria = Criteria.where("uri_hash").in(uriHashes);
            if (!includeDeleted) {
                criteria.and("is_deleted").is(false);
            }

            Query query = new Query(criteria);
            String collectionName = getCollectionName(rootNode);

            try {
                List<UriEntity> groupResults = mongoTemplate.find(
                        query,
                        UriEntity.class,
                        collectionName
                );
                results.addAll(groupResults);
            } catch (Exception e) {
                log.error("Failed to query collection {}", collectionName, e);
                // 继续处理其他分组
            }
        }

        return results;
    }

    /**
     * 删除不存在的URI
     */
    public void deleteNotInUris(String rootNode, Set<String> uriHashes) {
        Query query = new Query(
                Criteria.where("uri_hash").nin(uriHashes)
        );

        try {
            mongoTemplate.remove(
                    query,
                    UriEntity.class,
                    getCollectionName(rootNode)
            );
        } catch (Exception e) {
            log.error("Failed to delete non-existing URIs in collection {}", rootNode, e);
            throw new RuntimeException("Delete non-existing URIs failed", e);
        }
    }

    private Document convertEntityToDocument(UriEntity entity) {
        // 确保 uriHash 存在
        if (entity.getUriHash() == null && entity.getUri() != null) {
            entity.setUriHash(HashUtil.hash(entity.getUri()));
        }
        Document doc = new Document();
        doc.put("uri", entity.getUri());
        doc.put("uri_hash", entity.getUriHash());
        doc.put("root_node", entity.getRootNode());
        doc.put("version_type", entity.getVersionType());
        doc.put("uri_version", entity.getUriVersion());
        doc.put("details", entity.getDetails());
        doc.put("version", entity.getVersion());
        doc.put("version_code", entity.getVersionCode());
        doc.put("version_time", entity.getVersionTime());
        doc.put("update_time", LocalDateTime.now());
        doc.put("is_deleted", false);

        if (entity.getCreateTime() == null) {
            doc.put("create_time", LocalDateTime.now());
        }

        return doc;
    }



    /**
     * 使用原生命令条件分页查询uriHash
     * @param rootNode 根节点
     * @param version 版本
     * @param versionType 版本类型
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @return uriHash列表
     */
    public List<String> findUriHashesNativeWithPage(String rootNode,
                                                    String version,
                                                    String versionType,
                                                    int page,
                                                    int size) {
        String collectionName = getCollectionName(rootNode);
        MongoCollection<Document> collection = mongoTemplate.getCollection(collectionName);

        // 构建查询条件
        Document query = new Document();
        if (rootNode != null) {
            query.append("root_node", rootNode);
        }
        if (version != null) {
            query.append("uri_version", version);
        }
        if (versionType != null) {
            query.append("version_type", versionType);
        }

        // 构建聚合管道
        List<Document> pipeline = Arrays.asList(
                new Document("$match", query),
                new Document("$project", new Document("uri_hash", 1).append("_id", 0)),
                new Document("$skip", (long) (page - 1) * size),
                new Document("$limit", size)
        );

        try {
            return collection.aggregate(pipeline)
                    .map(doc -> doc.getString("uri_hash"))
                    .into(new ArrayList<>());
        } catch (Exception e) {
            log.error("Failed to execute native query in collection {}", collectionName, e);
            throw new RuntimeException("Query execution failed", e);
        }
    }

    /**
     * 获取满足条件的总数
     */
    public long countUriHashesNative(String rootNode, String version, String versionType) {
        String collectionName = getCollectionName(rootNode);
        MongoCollection<Document> collection = mongoTemplate.getCollection(collectionName);

        Document query = new Document();
        if (rootNode != null) {
            query.append("root_node", rootNode);
        }
        if (version != null) {
            query.append("uri_version", version);
        }
        if (versionType != null) {
            query.append("version_type", versionType);
        }

        try {
            return collection.countDocuments(query);
        } catch (Exception e) {
            log.error("Failed to count documents in collection {}", collectionName, e);
            throw new RuntimeException("Count documents failed", e);
        }
    }

    /**
     * 查询并返回分页结果
     */
    public PageResult<String> findUriHashesPage(String rootNode,
                                                String version,
                                                String versionType,
                                                int page,
                                                int size) {
        try {
            long total = countUriHashesNative(rootNode, version, versionType);
            List<String> items = findUriHashesNativeWithPage(rootNode, version, versionType, page, size);

            return PageResult.<String>builder()
                    .total(total)
                    .page(page)
                    .size(size)
                    .totalPages((int) Math.ceil((double) total / size))
                    .items(items)
                    .build();
        } catch (Exception e) {
            log.error("Failed to get paged results for rootNode {}", rootNode, e);
            throw new RuntimeException("Failed to get paged results", e);
        }
    }

    /**
     * 如果数据量很大，使用流式处理
     */
    public void streamUriHashesNative(String rootNode,
                                      String version,
                                      String versionType,
                                      Consumer<String> consumer) {
        String collectionName = getCollectionName(rootNode);
        MongoCollection<Document> collection = mongoTemplate.getCollection(collectionName);

        Document query = new Document();
        if (rootNode != null) {
            query.append("rootNode", rootNode);
        }
        if (version != null) {
            query.append("uri_version", version);
        }
        if (versionType != null) {
            query.append("version_type", versionType);
        }

        List<Document> pipeline = Arrays.asList(
                new Document("$match", query),
                new Document("$project", new Document("uri_hash", 1).append("_id", 0))
        );

        try (MongoCursor<Document> cursor = collection.aggregate(pipeline).iterator()) {
            while (cursor.hasNext()) {
                consumer.accept(cursor.next().getString("uri_hash"));
            }
        } catch (Exception e) {
            log.error("Failed to stream documents from collection {}", collectionName, e);
            throw new RuntimeException("Streaming documents failed", e);
        }
    }
}