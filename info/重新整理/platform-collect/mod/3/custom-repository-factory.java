package com.study.collect.core.repository.factory;

import com.study.collect.core.repository.IRepository;
import com.study.collect.core.repository.impl.BaseMongoRepository;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import java.io.Serializable;

public class CustomMongoRepositoryFactory extends MongoRepositoryFactory {

    private final MongoOperations mongoOperations;

    public CustomMongoRepositoryFactory(MongoOperations mongoOperations) {
        super(mongoOperations);
        this.mongoOperations = mongoOperations;
    }

    @Override
    protected Object getTargetRepository(RepositoryInformation information) {
        // 获取实体信息
        MongoEntityInformation<?, Serializable> entityInformation = 
            getEntityInformation(information.getDomainType());

        // 创建repository实例 - 如果是IRepository接口的实现，使用BaseMongoRepository作为基类
        if (IRepository.class.isAssignableFrom(information.getRepositoryInterface())) {
            return new BaseMongoRepository<>(entityInformation, mongoOperations) {};
        }

        return super.getTargetRepository(information);
    }

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        // 如果repository接口继承了IRepository，返回BaseMongoRepository作为基类
        if (IRepository.class.isAssignableFrom(metadata.getRepositoryInterface())) {
            return BaseMongoRepository.class;
        }
        // 否则使用默认的基类
        return super.getRepositoryBaseClass(metadata);
    }
}
