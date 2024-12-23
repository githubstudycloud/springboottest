package com.study.collect.infrastructure.storage.template;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.index.Index;

import java.util.List;
import java.util.Optional;

@Slf4j
public class MongoOperationTemplateImpl<T> implements MongoOperationTemplate<T> {

    private final MongoTemplate mongoTemplate;
    private final Class<T> entityClass;

    public MongoOperationTemplateImpl(MongoTemplate mongoTemplate, Class<T> entityClass) {
        this.mongoTemplate = mongoTemplate;
        this.entityClass = entityClass;
    }

    @Override
    public T insert(T entity) {
        return mongoTemplate.insert(entity);
    }

    @Override
    public void insertBatch(List<T> entities) {
        mongoTemplate.insertAll(entities);
    }

    @Override
    public T update(Query query, Update update) {
        return mongoTemplate.findAndModify(query, update, entityClass);
    }

    @Override
    public void updateBatch(Query query, Update update) {
        mongoTemplate.updateMulti(query, update, entityClass);
    }

    @Override
    public Optional<T> findOne(Query query) {
        return Optional.ofNullable(mongoTemplate.findOne(query, entityClass));
    }

    @Override
    public List<T> findList(Query query) {
        return mongoTemplate.find(query, entityClass);
    }

    @Override
    public Page<T> findPage(Query query, Pageable pageable) {
        long total = mongoTemplate.count(query, entityClass);
        query.with(pageable);
        List<T> content = mongoTemplate.find(query, entityClass);
        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public long count(Query query) {
        return mongoTemplate.count(query, entityClass);
    }

    @Override
    public void remove(Query query) {
        mongoTemplate.remove(query, entityClass);
    }

    @Override
    public void upsert(Query query, Update update) {
        mongoTemplate.upsert(query, update, entityClass);
    }

    @Override
    public void saveOrUpdateBatch(List<T> entities) {
        entities.forEach(mongoTemplate::save);
    }

    @Override
    public <R> List<R> aggregate(String aggregation, Class<R> resultType) {
        // 实现具体的聚合操作
        return mongoTemplate.aggregate(null, entityClass, resultType).getMappedResults();
    }

    @Override
    public void createIndex(String... fieldNames) {
        Index index = new Index();
        for (String fieldName : fieldNames) {
            index.on(fieldName, Index.Direction.ASC);
        }
        mongoTemplate.indexOps(entityClass).ensureIndex(index);
    }

    @Override
    public void dropIndex(String indexName) {
        mongoTemplate.indexOps(entityClass).dropIndex(indexName);
    }
}
