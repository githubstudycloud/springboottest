//package com.study.collect.business.testcase.config;
//
//import jakarta.annotation.PostConstruct;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.domain.Sort;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.index.Index;
//import org.springframework.data.mongodb.core.index.IndexDefinition;
//import org.springframework.data.mongodb.core.index.IndexOperations;
//
//@Configuration
//public class MongoIndexConfig {
//
//    @PostConstruct
//    public void ensureIndexes(MongoTemplate mongoTemplate) {
//        String collectionName = "uri_collect";
//
//        // 创建复合唯一索引
//        IndexOperations indexOps = mongoTemplate.indexOps(collectionName);
//
//        IndexDefinition uriUniqueIndex = new Index()
//                .on("uri", Sort.Direction.ASC)
//                .on("root_node", Sort.Direction.ASC)
//                .on("version_type", Sort.Direction.ASC)
//                .on("uri_version", Sort.Direction.ASC)
//                .unique();
//        indexOps.ensureIndex(uriUniqueIndex);
//
//        // 创建 uri_hash 唯一索引
//        IndexDefinition uriHashIndex = new Index()
//                .on("uri_hash", Sort.Direction.ASC)
//                .unique();
//        indexOps.ensureIndex(uriHashIndex);
//
//        // 创建查询索引
//        IndexDefinition queryIndex = new Index()
//                .on("root_node", Sort.Direction.ASC)
//                .on("version_type", Sort.Direction.ASC)
//                .on("uri_version", Sort.Direction.ASC)
//                .on("is_deleted", Sort.Direction.ASC);
//        indexOps.ensureIndex(queryIndex);
//    }
//}