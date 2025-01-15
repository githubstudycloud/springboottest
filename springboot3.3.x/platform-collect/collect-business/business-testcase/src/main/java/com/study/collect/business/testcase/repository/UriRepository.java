package com.study.collect.business.testcase.repository;

import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.study.collect.business.testcase.aspect.mongodb.CollectionStrategy;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.WriteModel;
import com.study.collect.business.testcase.entity.UriEntity;
import com.study.collect.business.testcase.model.PageResult;
import com.study.collect.core.storage.repository.BaseMongoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Repository
@Slf4j
public class UriRepository extends BaseMongoRepository<UriEntity> {

    private final MongoOperations mongoOperations;
    private final MongoEntityInformation<UriEntity, String> entityInformation;

    public UriRepository(MongoEntityInformation<UriEntity, String> metadata,
                         MongoOperations mongoOperations) {
        super(metadata, mongoOperations);
        this.mongoOperations = mongoOperations;
        this.entityInformation = metadata;
    }

    /**
     * 获取当前集合名
     */
    protected String getCollectionName() {
        String baseCollection = "uri_collect";
        String version = CollectionStrategy.getVersion();
        if (version != null) {
            return baseCollection + "_" + version;
        }
        log.warn("No version found in CollectionStrategy, using base collection name");
        return baseCollection;
    }

    /**
     * 根据URI列表查询
     */
    public List<UriEntity> findByUris(List<String> uris) {
        if (CollectionUtils.isEmpty(uris)) {
            return new ArrayList<>();
        }
        Query query = new Query(Criteria.where("uri").in(uris)
                .and("deleted").is(false));
        return mongoOperations.find(query, entityInformation.getJavaType(), getCollectionName());
    }

    /**
     * 根据URIHash列表查询
     */
    public List<UriEntity> findByUriHashes(List<String> uriHashes) {
        if (CollectionUtils.isEmpty(uriHashes)) {
            return new ArrayList<>();
        }
        Query query = new Query(Criteria.where("uriHash").in(uriHashes)
                .and("deleted").is(false));
        return mongoOperations.find(query, entityInformation.getJavaType(), getCollectionName());
    }

    /**
     * 只返回details字段
     */
    public List<String> findDetailsList(QueryCondition condition) {
        MongoCollection<Document> collection = mongoOperations.getCollection(getCollectionName());

        Document query = buildQuery(condition);
        query.append("deleted", false);
        Document projection = new Document("details", 1).append("_id", 0);

        List<String> detailsList = new ArrayList<>();
        try (MongoCursor<Document> cursor = collection.find(query)
                .projection(projection)
                .iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Object details = doc.get("details");
                if (details != null) {
                    detailsList.add(details.toString());
                }
            }
        }

        return detailsList;
    }

    /**
     * 分页查询uriHash
     */
    public PageResult<String> findUriHashesPage(QueryCondition condition) {
        MongoCollection<Document> collection = mongoOperations.getCollection(getCollectionName());

        Document query = buildQuery(condition);
        query.append("deleted", false);

        List<Document> pipeline = Arrays.asList(
                new Document("$match", query),
                new Document("$project", new Document("uriHash", 1).append("_id", 0)),
                new Document("$skip", (long) (condition.getPage() - 1) * condition.getSize()),
                new Document("$limit", condition.getSize())
        );

        List<String> items = new ArrayList<>();
        try (MongoCursor<Document> cursor = collection.aggregate(pipeline).iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                String uriHash = doc.getString("uriHash");
                if (uriHash != null) {
                    items.add(uriHash);
                }
            }
        }

        long total = collection.countDocuments(query);

        return PageResult.<String>builder()
                .total(total)
                .page(condition.getPage())
                .size(condition.getSize())
                .totalPages((int) Math.ceil((double) total / condition.getSize()))
                .items(items)
                .build();
    }

    @Override
    public <S extends UriEntity> List<S> saveAll(Iterable<S> entities) {
        if (!entities.iterator().hasNext()) {
            return Collections.emptyList();
        }

        String collectionName = getCollectionName();
        MongoCollection<Document> collection = mongoOperations.getCollection(collectionName);

        List<WriteModel<Document>> operations = new ArrayList<>();
        for (S entity : entities) {
            Document query = new Document("uriHash", entity.getUriHash());
            Document update = new Document("$set", convertEntityToDocument(entity));
            operations.add(new UpdateOneModel<>(
                    query,
                    update,
                    new UpdateOptions().upsert(true)
            ));
        }

        try {
            BulkWriteOptions bulkWriteOptions = new BulkWriteOptions().ordered(false);
            BulkWriteResult result = collection.bulkWrite(operations, bulkWriteOptions);
            log.debug("Bulk write to collection {}: matched={}, inserted={}, modified={}",
                    collectionName,
                    result.getMatchedCount(),
                    result.getInsertedCount(),
                    result.getModifiedCount());
        } catch (Exception e) {
            log.error("Failed to bulk write to collection {}", collectionName, e);
            throw new RuntimeException("Bulk write failed", e);
        }

        return StreamSupport.stream(entities.spliterator(), false)
                .collect(Collectors.toList());
    }

    private Document buildQuery(QueryCondition condition) {
        Document query = new Document();

        if (condition.getUriHashes() != null && !condition.getUriHashes().isEmpty()) {
            query.append("uriHash", new Document("$in", condition.getUriHashes()));
        }
        if (condition.getRootNode() != null) {
            query.append("rootNode", condition.getRootNode());
        }
        if (condition.getVersionType() != null) {
            query.append("versionType", condition.getVersionType());
        }
        if (condition.getUriVersion() != null) {
            query.append("uriVersion", condition.getUriVersion());
        }

        return query;
    }

    private Document convertEntityToDocument(UriEntity entity) {
        Document doc = new Document();
        doc.put("uri", entity.getUri());
        doc.put("uriHash", entity.getUriHash());
        doc.put("rootNode", entity.getRootNode());
        doc.put("versionType", entity.getVersionType());
        doc.put("uriVersion", entity.getUriVersion());
        doc.put("details", entity.getDetails());
        doc.put("updateTime", LocalDateTime.now());
        doc.put("deleted", false);
        return doc;
    }
}