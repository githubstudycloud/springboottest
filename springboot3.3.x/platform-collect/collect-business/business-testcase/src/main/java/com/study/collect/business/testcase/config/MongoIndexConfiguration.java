//package com.study.collect.business.testcase.config;
//
//import com.study.collect.business.testcase.entity.UriEntity;
//import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.domain.Sort;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.index.IndexOperations;
//import jakarta.annotation.PostConstruct;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.domain.Sort;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.index.Index;
//import org.springframework.data.mongodb.core.index.IndexDefinition;
//import org.springframework.data.mongodb.core.index.IndexOperations;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.bson.Document;
//
//@Configuration
//@RequiredArgsConstructor
//public class MongoIndexConfiguration {
//
//    private final MongoTemplate mongoTemplate;
//    private static final Logger log = LoggerFactory.getLogger(MongoIndexConfiguration.class);
//
//    @PostConstruct
//    public void initIndexes() {
//        try {
//            // 确保集合存在
//            if (!mongoTemplate.collectionExists(UriEntity.class)) {
//                mongoTemplate.createCollection(UriEntity.class);
//            }
//
//            // 获取索引操作对象
//            IndexOperations indexOps = mongoTemplate.indexOps(UriEntity.class);
//
//            // 创建复合唯一索引
//            IndexDefinition uriCompositeIndex = new Index()
//                    .on("uri", Sort.Direction.ASC)
//                    .on("root_node", Sort.Direction.ASC)
//                    .on("version_type", Sort.Direction.ASC)
//                    .on("uri_version", Sort.Direction.ASC)
//                    .named("idx_uri_composite")
//                    .unique();
//            indexOps.ensureIndex(uriCompositeIndex);
//
//            // 创建 uri_hash 唯一索引
//            IndexDefinition uriHashIndex = new Index()
//                    .on("uri_hash", Sort.Direction.ASC)
//                    .named("idx_uri_hash")
//                    .unique();
//            indexOps.ensureIndex(uriHashIndex);
//
//            // 查询索引
//            IndexDefinition queryIndex = new Index()
//                    .on("root_node", Sort.Direction.ASC)
//                    .on("version_type", Sort.Direction.ASC)
//                    .on("uri_version", Sort.Direction.ASC)
//                    .on("is_deleted", Sort.Direction.ASC)
//                    .named("idx_query");
//            indexOps.ensureIndex(queryIndex);
//
//            // 输出所有索引信息
//            List<Document> indexes = mongoTemplate.getCollection(mongoTemplate.getCollectionName(UriEntity.class))
//                    .listIndexes()
//                    .into(new ArrayList<>());
//            log.info("Collection indexes after initialization: {}", indexes);
//
//        } catch (Exception e) {
//            log.error("Failed to initialize indexes", e);
//            throw new RuntimeException("Failed to initialize MongoDB indexes", e);
//        }
//    }
//}