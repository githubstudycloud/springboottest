//package com.study.collect.business.testcase.config;
//
//import lombok.RequiredArgsConstructor;
//import org.bson.Document;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.context.ApplicationListener;
//import org.springframework.context.event.ContextRefreshedEvent;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.stereotype.Component;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Component
//@RequiredArgsConstructor
//public class MongoIndexChecker implements ApplicationListener<ContextRefreshedEvent> {
//
//    private final MongoTemplate mongoTemplate;
//    private static final Logger log = LoggerFactory.getLogger(MongoIndexChecker.class);
//
//    @Override
//    public void onApplicationEvent(ContextRefreshedEvent event) {
//        String collectionName = "uri_collect";
//        try {
//            List<Document> indexes = mongoTemplate.getCollection(collectionName)
//                    .listIndexes().into(new ArrayList<>());
//            log.info("Collection {} indexes on startup: {}", collectionName, indexes);
//        } catch (Exception e) {
//            log.error("Failed to check indexes for collection: " + collectionName, e);
//        }
//    }
//}
