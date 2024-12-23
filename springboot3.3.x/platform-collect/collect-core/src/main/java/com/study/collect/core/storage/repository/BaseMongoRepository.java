package com.study.collect.core.storage.repository;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;

import java.io.Serializable;
import java.util.List;

public class BaseMongoRepository<T, ID extends Serializable>
        extends SimpleMongoRepository<T, ID> implements IRepository<T, ID> {

    protected final MongoTemplate mongoTemplate;
    protected final MongoEntityInformation<T, ID> entityInformation;

    public BaseMongoRepository(MongoEntityInformation<T, ID> metadata,
                               MongoOperations mongoOperations) {
        super(metadata, mongoOperations);
        this.mongoTemplate = (MongoTemplate) mongoOperations;
        this.entityInformation = metadata;
    }

    @Override
    public T findByCode(String code) {
        Query query = new Query(Criteria.where("code").is(code));
        return mongoTemplate.findOne(query, entityInformation.getJavaType());
    }

    @Override
    public void updateStatus(ID id, String status) {

    }

    @Override
    public long countByStatus(String status) {
        return 0;
    }

    @Override
    public void softDelete(ID id) {

    }
//
//    // 其他方法实现...
//    // 版本查询
//    List<T> findByVersion(String version);
//    // 增量查询
//    List<T> findIncrementalData(String version);
}