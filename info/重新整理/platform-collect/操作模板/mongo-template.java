package com.study.collect.infrastructure.storage.template;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;
import java.util.Collection;

/**
 * MongoDB操作模板基类
 * 封装通用的MongoDB操作
 */
public abstract class BaseMongoTemplate<T> {
    
    protected final MongoTemplate mongoTemplate;
    protected final Class<T> entityClass;

    public BaseMongoTemplate(MongoTemplate mongoTemplate, Class<T> entityClass) {
        this.mongoTemplate = mongoTemplate;
        this.entityClass = entityClass;
    }

    /**
     * 批量保存
     */
    public List<T> batchSave(Collection<T> entities) {
        return (List<T>) mongoTemplate.insertAll(entities);
    }

    /**
     * 批量更新
     */
    public void batchUpdate(List<Query> queries, List<Update> updates) {
        for (int i = 0; i < queries.size(); i++) {
            mongoTemplate.updateMulti(queries.get(i), updates.get(i), entityClass);
        }
    }

    /**
     * 按条件查询
     */
    public List<T> findByCondition(Criteria criteria) {
        Query query = new Query(criteria);
        return mongoTemplate.find(query, entityClass);
    }

    /**
     * 分页查询
     */
    public List<T> findPage(Query query, int pageNum, int pageSize) {
        long skip = (long) (pageNum - 1) * pageSize;
        query.skip(skip).limit(pageSize);
        return mongoTemplate.find(query, entityClass);
    }

    /**
     * 统计总数
     */
    public long count(Query query) {
        return mongoTemplate.count(query, entityClass);
    }

    /**
     * 批量删除
     */
    public void batchDelete(Query query) {
        mongoTemplate.remove(query, entityClass);
    }
}
