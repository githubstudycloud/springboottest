package com.study.collect.infrastructure.config.db;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.MongoDatabaseFactory;

/**
 * MongoDB配置
 */
@Configuration
public class MongoConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.data.mongodb")
    public MongoProperties mongoProperties() {
        return new MongoProperties();
    }

    @Bean
    public MongoClient mongoClient(MongoProperties properties) {
        return MongoClients.create(properties.getUri());
    }

    @Bean
    public MongoDatabaseFactory mongoDbFactory(MongoClient mongoClient, MongoProperties properties) {
        return new SimpleMongoClientDatabaseFactory(mongoClient, properties.getDatabase());
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoDatabaseFactory mongoDbFactory) {
        // 创建转换器
        MappingMongoConverter converter = new MappingMongoConverter(
                new DefaultDbRefResolver(mongoDbFactory),
                new MongoMappingContext()
        );
        // 去掉_class字段
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));

        return new MongoTemplate(mongoDbFactory, converter);
    }

    @Data
    public static class MongoProperties {
        private String uri;
        private String database;
        private Boolean autoIndexCreation = true;
    }
}