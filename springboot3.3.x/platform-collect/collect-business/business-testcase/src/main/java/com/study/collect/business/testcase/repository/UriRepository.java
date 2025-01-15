package com.study.collect.business.testcase.repository;

import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.*;
import com.study.collect.business.testcase.constant.CollectionConstants;
import com.study.collect.business.testcase.entity.UriEntity;
import com.study.collect.business.testcase.utils.HashUtil;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class UriRepository {
    private final MongoTemplate mongoTemplate;

    public UriRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
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

        String collectionName = getCollectionName(rootNode);
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
}