package com.study.collect.infrastructure.storage.template;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;
import java.util.Optional;

/**
 * MongoDB基础操作模板接口
 * @param <T> 实体类型
 */
public interface MongoOperationTemplate<T> {
    
    // 基础CRUD操作
    T insert(T entity);
    
    void insertBatch(List<T> entities);
    
    T update(Query query, Update update);
    
    void updateBatch(Query query, Update update);
    
    Optional<T> findOne(Query query);
    
    List<T> findList(Query query);
    
    Page<T> findPage(Query query, Pageable pageable);
    
    long count(Query query);
    
    void remove(Query query);
    
    // 高级操作
    void upsert(Query query, Update update);
    
    void saveOrUpdateBatch(List<T> entities);
    
    // 聚合操作
    <R> List<R> aggregate(String aggregation, Class<R> resultType);
    
    // 索引操作
    void createIndex(String... fieldNames);
    
    void dropIndex(String indexName);
}
