package com.study.collect.infrastructure.storage.template;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

/**
 * 列表数据操作模板
 * 专门处理列表结构数据的存储和查询
 */
public class ListTemplate<T> extends BaseMongoTemplate<T> {

    public ListTemplate(MongoTemplate mongoTemplate, Class<T> entityClass) {
        super(mongoTemplate, entityClass);
    }

    /**
     * 分页排序查询
     */
    public List<T> findPageWithSort(Query query, int pageNum, int pageSize, Sort sort) {
        long skip = (long) (pageNum - 1) * pageSize;
        query.with(sort).skip(skip).limit(pageSize);
        return mongoTemplate.find(query, entityClass);
    }

    /**
     * 滚动查询（适用于大数据量）
     */
    public List<T> scroll(Query query, String lastId, int limit) {
        if (lastId != null) {
            query.addCriteria(org.springframework.data.mongodb.core.query.Criteria.where("_id").gt(lastId));
        }
        query.limit(limit);
        return mongoTemplate.find(query, entityClass);
    }

    /**
     * 批量更新字段
     */
    public void batchUpdateField(Query query, String field, Object value) {
        Update update = new Update().set(field, value);
        mongoTemplate.updateMulti(query, update, entityClass);
    }

    /**
     * 批量递增字段
     */
    public void batchIncrement(Query query, String field, Number incrementBy) {
        Update update = new Update().inc(field, incrementBy);
        mongoTemplate.updateMulti(query, update, entityClass);
    }

    /**
     * 批量添加到数组
     */
    public void batchAddToArray(Query query, String arrayField, Object value) {
        Update update = new Update().push(arrayField).value(value);
        mongoTemplate.updateMulti(query, update, entityClass);
    }

    /**
     * 批量从数组删除
     */
    public void batchRemoveFromArray(Query query, String arrayField, Object value) {
        Update update = new Update().pull(arrayField, value);
        mongoTemplate.updateMulti(query, update, entityClass);
    }

    /**
     * 聚合统计
     */
    public List<T> aggregate(org.springframework.data.mongodb.core.aggregation.Aggregation aggregation) {
        return mongoTemplate.aggregate(aggregation, entityClass, entityClass).getMappedResults();
    }
}
