package com.study.collect.core.repository.impl;

import com.study.collect.core.repository.IRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;
import org.springframework.data.mongodb.core.MongoOperations;
import java.io.Serializable;

public class BaseMongoRepository<T, ID extends Serializable> 
        extends SimpleMongoRepository<T, ID> implements IRepository<T, ID> {
    
    protected final MongoTemplate mongoTemplate;
    protected final MongoEntityInformation<T, ID> entityInformation;

    public BaseMongoRepository(MongoEntityInformation<T, ID> metadata, MongoOperations mongoOperations) {
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
        Query query = new Query(Criteria.where("id").is(id));
        Update update = new Update().set("status", status);
        mongoTemplate.updateFirst(query, update, entityInformation.getJavaType());
    }

    @Override
    public long countByStatus(String status) {
        Query query = new Query(Criteria.where("status").is(status));
        return mongoTemplate.count(query, entityInformation.getJavaType());
    }

    @Override
    public void softDelete(ID id) {
        Query query = new Query(Criteria.where("id").is(id));
        Update update = new Update().set("deleted", true);
        mongoTemplate.updateFirst(query, update, entityInformation.getJavaType());
    }
}
