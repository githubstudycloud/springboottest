package com.study.collect.core.config;

import com.study.collect.core.storage.repository.factory.CustomMongoRepositoryFactoryBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * MongoRepository配置
 */

@Configuration
@EnableMongoRepositories(
        basePackages = "com.study.collect",
        repositoryFactoryBeanClass = CustomMongoRepositoryFactoryBean.class
)
public class MongoRepositoryConfig {
}