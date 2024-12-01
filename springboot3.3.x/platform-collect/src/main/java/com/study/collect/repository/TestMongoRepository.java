package com.study.collect.repository;

import com.study.collect.entity.MongoTestEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestMongoRepository extends MongoRepository<MongoTestEntity, String> {
    // 基本的CRUD方法由MongoRepository提供
}