package com.study.collect.core.repository.factory;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactoryBean;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

public class CustomMongoRepositoryFactoryBean<T extends Repository<S, ID>, S, ID>
    extends MongoRepositoryFactoryBean<T, S, ID> {

    public CustomMongoRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
        super(repositoryInterface);
    }

    @Override
    protected RepositoryFactorySupport getFactoryInstance(MongoOperations operations) {
        return new CustomMongoRepositoryFactory(operations);
    }
}
