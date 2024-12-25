package com.study.collect.core.storage.repository.factory;

import com.study.collect.core.storage.entity.BaseEntity;
import com.study.collect.core.storage.repository.BaseMongoRepository;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;

public class CustomMongoRepositoryFactory extends MongoRepositoryFactory {

    private final MongoOperations mongoOperations;

    public CustomMongoRepositoryFactory(MongoOperations mongoOperations) {
        super(mongoOperations);
        this.mongoOperations = mongoOperations;
    }

    @Override
    protected Object getTargetRepository(RepositoryInformation information) {
        Class<?> domainClass = information.getDomainType();
        if (!BaseEntity.class.isAssignableFrom(domainClass)) {
            throw new IllegalArgumentException("Domain class must extend BaseEntity");
        }

        @SuppressWarnings("unchecked")
        MongoEntityInformation<? extends BaseEntity, String> entityInformation =
                getEntityInformation((Class<? extends BaseEntity>) domainClass);

        return getTargetRepositoryViaReflection(information,
                entityInformation, mongoOperations);
    }

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        return BaseMongoRepository.class;
    }
}