package com.study.scheduler.utils;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MongoDBUtils {
    private final MongoTemplate mongoTemplate;

    public MongoDBUtils(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public <T> T save(T entity) {
        return mongoTemplate.save(entity);
    }

    public <T> List<T> findAll(Class<T> entityClass) {
        return mongoTemplate.findAll(entityClass);
    }

    public <T> T findById(String id, Class<T> entityClass) {
        return mongoTemplate.findById(id, entityClass);
    }

    public <T> List<T> find(Query query, Class<T> entityClass) {
        return mongoTemplate.find(query, entityClass);
    }

    public <T> T findOne(Query query, Class<T> entityClass) {
        return mongoTemplate.findOne(query, entityClass);
    }

    public <T> UpdateResult update(Query query, Update update, Class<T> entityClass) {
        return mongoTemplate.updateMulti(query, update, entityClass);
    }

    public <T> DeleteResult delete(Query query, Class<T> entityClass) {
        return mongoTemplate.remove(query, entityClass);
    }

    public <T> long count(Query query, Class<T> entityClass) {
        return mongoTemplate.count(query, entityClass);
    }

    public <T> List<T> findByPage(Query query, Class<T> entityClass, int page, int size) {
        query.skip((long) (page - 1) * size).limit(size);
        return mongoTemplate.find(query, entityClass);
    }

    public <T> List<T> findBySort(Query query, Class<T> entityClass, Sort sort) {
        query.with(sort);
        return mongoTemplate.find(query, entityClass);
    }
}