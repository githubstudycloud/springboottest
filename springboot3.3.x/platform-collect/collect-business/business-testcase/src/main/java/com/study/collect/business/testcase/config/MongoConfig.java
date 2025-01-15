package com.study.collect.business.testcase.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.study.collect.business.testcase.constant.CollectionConstants;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableMongoAuditing
@ConditionalOnProperty(prefix = "spring.data.mongodb", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableMongoRepositories(basePackages = "com.study.collect.business.testcase.repository")
@ConfigurationProperties(prefix = "spring.data.mongodb")
@Data
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Value("${spring.data.mongodb.uri}")
    private String uri;

    @Value("${spring.data.mongodb.database}")
    private String database;

    @Value("${spring.data.mongodb.min-pool-size:" + CollectionConstants.MONGO_MIN_POOL_SIZE + "}")
    private Integer minPoolSize;

    @Value("${spring.data.mongodb.max-pool-size:" + CollectionConstants.MONGO_MAX_POOL_SIZE + "}")
    private Integer maxPoolSize;

    @Override
    protected String getDatabaseName() {
        return database;
    }

    @Override
    @Bean
    public MongoClient mongoClient() {
        ConnectionString connectionString = new ConnectionString(uri);

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .applyToConnectionPoolSettings(builder ->
                        builder.minSize(minPoolSize)
                                .maxSize(maxPoolSize)
                                .maxWaitTime(10, TimeUnit.SECONDS)
                                .maxConnectionLifeTime(30, TimeUnit.MINUTES)
                                .maxConnectionIdleTime(5, TimeUnit.MINUTES))
                .applyToSocketSettings(builder ->
                        builder.connectTimeout(5, TimeUnit.SECONDS)
                                .readTimeout(10, TimeUnit.SECONDS))
                .retryWrites(true)
                .retryReads(true)
                .build();

        return MongoClients.create(settings);
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoClient mongoClient) {
        return new MongoTemplate(mongoClient, getDatabaseName());
    }
}