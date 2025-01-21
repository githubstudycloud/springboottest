package com.study.collect.business.testcase.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class IndexChecker {
    private final MongoTemplate mongoTemplate;

    public void checkIndexes(String collectionName) {
        List<Document> indexes = mongoTemplate.getCollection(collectionName).listIndexes()
                .into(new ArrayList<>());

        log.info("Collection {} indexes: {}", collectionName, indexes);
    }
}