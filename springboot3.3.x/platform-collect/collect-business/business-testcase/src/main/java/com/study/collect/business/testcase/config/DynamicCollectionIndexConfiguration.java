package com.study.collect.business.testcase.config;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.study.collect.business.testcase.aspect.mongodb.CollectionStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DynamicCollectionIndexConfiguration {

    private final MongoTemplate mongoTemplate;
    private final CollectionStrategy collectionStrategy;

    /**
     * 创建动态集合的索引
     */
    public void createIndexesForCollection(String baseCollection) {
        String collectionName = collectionStrategy.getCollectionName(baseCollection);

        try {
            // 如果集合不存在，先创建集合
            if (!mongoTemplate.collectionExists(collectionName)) {
                mongoTemplate.createCollection(collectionName);
            }

            MongoCollection<Document> collection = mongoTemplate.getCollection(collectionName);

            // 创建复合唯一索引
            collection.createIndex(
                    Indexes.compoundIndex(
                            Indexes.ascending("uri"),
                            Indexes.ascending("root_node"),
                            Indexes.ascending("version_type"),
                            Indexes.ascending("uri_version")
                    ),
                    new IndexOptions()
                            .name("idx_uri_composite")
                            .unique(true)
                            .background(true)
            );

            // 创建 uri_hash 唯一索引
            collection.createIndex(
                    Indexes.ascending("uri_hash"),
                    new IndexOptions()
                            .name("idx_uri_hash")
                            .unique(true)
                            .background(true)
            );

            // 创建查询索引
            collection.createIndex(
                    Indexes.compoundIndex(
                            Indexes.ascending("root_node"),
                            Indexes.ascending("version_type"),
                            Indexes.ascending("uri_version"),
                            Indexes.ascending("is_deleted")
                    ),
                    new IndexOptions()
                            .name("idx_query")
                            .background(true)
            );

            // 检查并输出索引信息
            checkIndexes(collectionName);

        } catch (Exception e) {
            log.error("Failed to create indexes for collection: {}", collectionName, e);
            throw new RuntimeException("Failed to create indexes", e);
        }
    }

    /**
     * 检查集合的索引
     */
    public void checkIndexes(String collectionName) {
        try {
            List<Document> indexes = mongoTemplate.getCollection(collectionName)
                    .listIndexes()
                    .into(new ArrayList<>());

            log.info("Collection {} indexes:", collectionName);
            indexes.forEach(index -> log.info(index.toJson()));

            // 验证必需的索引是否存在
            Set<String> indexNames = indexes.stream()
                    .map(doc -> doc.getString("name"))
                    .collect(Collectors.toSet());

            List<String> requiredIndexes = Arrays.asList(
                    "idx_uri_composite",
                    "idx_uri_hash",
                    "idx_query"
            );

            for (String requiredIndex : requiredIndexes) {
                if (!indexNames.contains(requiredIndex)) {
                    log.warn("Required index {} is missing in collection {}",
                            requiredIndex, collectionName);
                }
            }

        } catch (Exception e) {
            log.error("Failed to check indexes for collection: {}", collectionName, e);
        }
    }
}
