package com.study.collect.business.testcase.repository;

import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.*;
import com.study.collect.business.testcase.common.constants.CollectionConstants;
import com.study.collect.business.testcase.common.utils.TableNameHelper;
import com.study.collect.business.testcase.entity.UriEntity;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
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
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * URI仓储实现
 */
@Slf4j
@Repository
public class UriRepository {

    private final MongoTemplate mongoTemplate;
    private final MeterRegistry meterRegistry;

    public UriRepository(MongoTemplate mongoTemplate, MeterRegistry meterRegistry) {
        this.mongoTemplate = mongoTemplate;
        this.meterRegistry = meterRegistry;
    }

    /**
     * 批量插入或更新
     */
    public BulkWriteResult batchUpsert(String rootNode, List<UriEntity> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return null;
        }

        Timer.Sample timer = Timer.start(meterRegistry);
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
            BulkWriteResult result = collection.bulkWrite(operations, options);
//            recordMetrics("upsert", timer, entities.size(), result);
            recordMetrics("upsert", timer, entities.size(), result.getModifiedCount());
            return result;
        } catch (Exception e) {
            recordError("upsert");
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

        Timer.Sample timer = Timer.start(meterRegistry);
        String collectionName = TableNameHelper.getTableName(rootNode);
        List<String> uriHashes = uris.stream()
                .map(TableNameHelper::generateUriHash)
                .collect(Collectors.toList());

        Query query = new Query(Criteria.where("uri_hash").in(uriHashes));
        Update update = new Update()
                .set("is_deleted", true)
                .set("update_time", LocalDateTime.now());

        try {
            long count = mongoTemplate.updateMulti(query, update, collectionName)
                    .getModifiedCount();
            recordMetrics("soft_delete", timer, uris.size(), count);
            return count;
        } catch (Exception e) {
            recordError("soft_delete");
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

        Timer.Sample timer = Timer.start(meterRegistry);
        String collectionName = TableNameHelper.getTableName(rootNode);
        List<String> uriHashes = uris.stream()
                .map(TableNameHelper::generateUriHash)
                .collect(Collectors.toList());

        Query query = new Query(Criteria.where("uri_hash").in(uriHashes));

        try {
            long count = mongoTemplate.remove(query, UriEntity.class, collectionName)
                    .getDeletedCount();
            recordMetrics("hard_delete", timer, uris.size(), count);
            return count;
        } catch (Exception e) {
            recordError("hard_delete");
            log.error("Failed to batch hard delete in collection {}", collectionName, e);
            throw new RuntimeException("Batch hard delete failed", e);
        }
    }

    /**
     * 清理不在列表中的URI（软删除）
     */
    public long softDeleteNotInUriHashes(String rootNode, Set<String> validHashes) {
        Timer.Sample timer = Timer.start(meterRegistry);
        String collectionName = TableNameHelper.getTableName(rootNode);
        Query query = new Query(Criteria.where("uri_hash").nin(validHashes)
                .and("is_deleted").is(false));
        Update update = new Update()
                .set("is_deleted", true)
                .set("update_time", LocalDateTime.now());

        try {
            long count = mongoTemplate.updateMulti(query, update, collectionName)
                    .getModifiedCount();
            recordMetrics("soft_delete_cleanup", timer, validHashes.size(), count);
            return count;
        } catch (Exception e) {
            recordError("soft_delete_cleanup");
            log.error("Failed to soft delete URIs not in hash set for collection {}",
                    collectionName, e);
            throw new RuntimeException("Soft delete cleanup failed", e);
        }
    }

    /**
     * 清理不在列表中的URI（硬删除）
     */
    public long deleteNotInUriHashes(String rootNode, Set<String> validHashes) {
        Timer.Sample timer = Timer.start(meterRegistry);
        String collectionName = TableNameHelper.getTableName(rootNode);
        Query query = new Query(Criteria.where("uri_hash").nin(validHashes));

        try {
            long count = mongoTemplate.remove(query, UriEntity.class, collectionName)
                    .getDeletedCount();
            recordMetrics("hard_delete_cleanup", timer, validHashes.size(), count);
            return count;
        } catch (Exception e) {
            recordError("hard_delete_cleanup");
            log.error("Failed to delete URIs not in hash set for collection {}",
                    collectionName, e);
            throw new RuntimeException("Delete cleanup failed", e);
        }
    }

    /**
     * 分页查询
     */
    public Page<UriEntity> findByCondition(QueryParams params) {
        Timer.Sample timer = Timer.start(meterRegistry);
        String collectionName = TableNameHelper.getTableName(params.getRootNode());

        try {
            Criteria criteria = buildCriteria(params);
            Query query = new Query(criteria).with(params.getPageable());

            long total = mongoTemplate.count(query, UriEntity.class, collectionName);
            List<UriEntity> content = mongoTemplate.find(query, UriEntity.class, collectionName);

            recordMetrics("query", timer, content.size(), total);
            return new PageImpl<>(content, params.getPageable(), total);
        } catch (Exception e) {
            recordError("query");
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
            return Collections.emptyList();
        }

        Timer.Sample timer = Timer.start(meterRegistry);
        try {
            // 按rootNode分组URI
            Map<String, List<String>> groupedUris = uris.stream()
                    .collect(Collectors.groupingBy(rootNodeResolver));

            List<UriEntity> results = new ArrayList<>();
            for (Map.Entry<String, List<String>> entry : groupedUris.entrySet()) {
                results.addAll(queryByGroup(entry.getKey(), entry.getValue(), includeDeleted));
            }

            recordMetrics("batch_query", timer, uris.size(), results.size());
            return results;
        } catch (Exception e) {
            recordError("batch_query");
            log.error("Failed to batch query URIs", e);
            throw new RuntimeException("Batch query failed", e);
        }
    }

    private List<UriEntity> queryByGroup(
            String rootNode,
            List<String> uris,
            Boolean includeDeleted
    ) {
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

    private Criteria buildCriteria(QueryParams params) {
        Criteria criteria = new Criteria();

        if (StringUtils.hasText(params.getVersion())) {
            criteria.and("uri_version").is(params.getVersion());
        }
        if (StringUtils.hasText(params.getVersionType())) {
            criteria.and("version_type").is(params.getVersionType());
        }
        if (params.getOnlyDeleted()) {
            criteria.and("is_deleted").is(true);
        } else if (!params.getIncludeDeleted()) {
            criteria.and("is_deleted").is(false);
        }

        return criteria;
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

    private void recordMetrics(String operation, Timer.Sample timer, long requested, long actual) {
        timer.stop(meterRegistry.timer("mongodb.operation", "type", operation));
        meterRegistry.counter("mongodb.operation.total", "type", operation).increment();
        meterRegistry.gauge("mongodb.operation.ratio",
                Tags.of("type", operation),
                actual / (double)requested);
    }

    private void recordError(String operation) {
        meterRegistry.counter("mongodb.operation.error", "type", operation).increment();
    }
}