package com.study.collect.business.testcase.repository;

import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.*;
import com.study.collect.business.testcase.common.constants.CollectionConstants;
import com.study.collect.business.testcase.common.utils.TableNameHelper;
import com.study.collect.business.testcase.entity.UriEntity;
import lombok.Builder;
import lombok.Data;
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
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * URI仓储实现
 */
@Slf4j
@Repository
public class UriRepository {

    private final MongoTemplate mongoTemplate;

    public UriRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * 批量插入或更新
     */
    public BulkWriteResult batchUpsert(String rootNode, List<UriEntity> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return null;
        }

        String collectionName = TableNameHelper.getTableName(rootNode);
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

        String collectionName = TableNameHelper.getTableName(rootNode);
        List<String> uriHashes = uris.stream()
                .map(TableNameHelper::generateUriHash)
                .collect(Collectors.toList());

        Query query = new Query(Criteria.where("uri_hash").in(uriHashes));
        Update update = new Update()
                .set("is_deleted", true)
                .set("update_time", LocalDateTime.now());

        try {
            return mongoTemplate.updateMulti(query, update, collectionName).getModifiedCount();
        } catch (Exception e) {
            log.error("Failed to batch soft delete in collection {}", collectionName, e);
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

        String collectionName = TableNameHelper.getTableName(rootNode);
        List<String> uriHashes = uris.stream()
                .map(TableNameHelper::generateUriHash)
                .collect(Collectors.toList());

        Query query = new Query(Criteria.where("uri_hash").in(uriHashes));

        try {
            return mongoTemplate.remove(query, UriEntity.class, collectionName).getDeletedCount();
        } catch (Exception e) {
            log.error("Failed to batch hard delete in collection {}", collectionName, e);
            throw new RuntimeException("Batch hard delete failed", e);
        }
    }

    /**
     * 清理不在列表中的URI（软删除）
     */
    public long softDeleteNotInUriHashes(String rootNode, Set<String> validHashes) {
        String collectionName = TableNameHelper.getTableName(rootNode);
        Query query = new Query(Criteria.where("uri_hash").nin(validHashes)
                .and("is_deleted").is(false));
        Update update = new Update()
                .set("is_deleted", true)
                .set("update_time", LocalDateTime.now());

        try {
            return mongoTemplate.updateMulti(query, update, collectionName).getModifiedCount();
        } catch (Exception e) {
            log.error("Failed to soft delete URIs not in hash set for collection {}", collectionName, e);
            throw new RuntimeException("Soft delete cleanup failed", e);
        }
    }

    /**
     * 清理不在列表中的URI（硬删除）
     */
    public long deleteNotInUriHashes(String rootNode, Set<String> validHashes) {
        String collectionName = TableNameHelper.getTableName(rootNode);
        Query query = new Query(Criteria.where("uri_hash").nin(validHashes));

        try {
            return mongoTemplate.remove(query, UriEntity.class, collectionName).getDeletedCount();
        } catch (Exception e) {
            log.error("Failed to delete URIs not in hash set for collection {}", collectionName, e);
            throw new RuntimeException("Delete cleanup failed", e);
        }
    }

    /**
     * 分页查询
     */
    public Page<UriEntity> findByCondition(QueryParams params) {
        Criteria criteria = new Criteria();

        if (StringUtils.hasText(params.getVersion())) {
            criteria.and("uri_version").is(params.getVersion());
        }
        if (StringUtils.hasText(params.getVersionType())) {
            criteria.and("version_type").is(params.getVersionType());
        }
        if (!params.getIncludeDeleted()) {
            criteria.and("is_deleted").is(false);
        }
        if (params.getOnlyDeleted()) {
            criteria.and("is_deleted").is(true);
        }

        Query query = new Query(criteria).with(params.getPageable());
        String collectionName = TableNameHelper.getTableName(params.getRootNode());

        try {
            long total = mongoTemplate.count(query, UriEntity.class, collectionName);
            List<UriEntity> content = mongoTemplate.find(query, UriEntity.class, collectionName);
            return new PageImpl<>(content, params.getPageable(), total);
        } catch (Exception e) {
            log.error("Failed to query collection {}", collectionName, e);
            throw new RuntimeException("Query failed", e);
        }
    }

    /**
     * 批量查询
     */
    public List<UriEntity> batchQuery(List<String> uris, Function<String, String> rootNodeResolver,
                                      Boolean includeDeleted) {
        if (CollectionUtils.isEmpty(uris)) {
            return new ArrayList<>();
        }

        Map<String, List<String>> groupedUris = uris.stream()
                .collect(Collectors.groupingBy(rootNodeResolver));

        List<UriEntity> results = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : groupedUris.entrySet()) {
            results.addAll(queryByGroup(entry.getKey(), entry.getValue(), includeDeleted));
        }

        return results;
    }

    private List<UriEntity> queryByGroup(String rootNode, List<String> uris, Boolean includeDeleted) {
        String collectionName = TableNameHelper.getTableName(rootNode);
        List<String> uriHashes = uris.stream()
                .map(TableNameHelper::generateUriHash)
                .collect(Collectors.toList());

        Criteria criteria = Criteria.where("uri_hash").in(uriHashes);
        if (!includeDeleted) {
            criteria.and("is_deleted").is(false);
        }

        Query query = new Query(criteria);
        try {
            return mongoTemplate.find(query, UriEntity.class, collectionName);
        } catch (Exception e) {
            log.error("Failed to query collection {} for group", collectionName, e);
            return Collections.emptyList();
        }
    }

    /**
     * 查询参数对象
     */
    @Data
    @Builder
    public static class QueryParams {
        private String rootNode;
        private String version;
        private String versionType;
        private Boolean includeDeleted;
        private Boolean onlyDeleted;
        private Pageable pageable;
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