package com.study.collect.business.testcase.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.connection.ClusterSettings;
import com.mongodb.connection.ConnectionPoolSettings;
import com.mongodb.connection.ServerSettings;
import com.mongodb.connection.SocketSettings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
@EnableMongoAuditing
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "spring.data.mongodb", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableMongoRepositories(basePackages = "com.study.collect.business.testcase.repository")
public class MongoConfig extends AbstractMongoClientConfiguration {

    private final TestCaseCollectorProperties properties;

    @Value("${spring.data.mongodb.uri}")
    private String uri;

    @Value("${spring.data.mongodb.database}")
    private String database;

    @Override
    protected String getDatabaseName() {
        return database;
    }

    @Override
    @Bean
    @Primary
    public MongoClient mongoClient() {
        ConnectionString connectionString = new ConnectionString(uri);
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                // 集群设置
                .applyToClusterSettings(builder -> {
                    ClusterSettings.Builder clusterBuilder = ClusterSettings.builder()
                            .serverSelectionTimeout(5000, TimeUnit.MILLISECONDS);
                    builder.applySettings(clusterBuilder.build());
                })
                // 连接池设置
                .applyToConnectionPoolSettings(builder -> {
                    ConnectionPoolSettings.Builder poolBuilder = ConnectionPoolSettings.builder()
                            .minSize(properties.getMongo().getMinPoolSize())
                            .maxSize(properties.getMongo().getMaxPoolSize())
                            .maxWaitTime(10000, TimeUnit.MILLISECONDS)
                            .maxConnectionLifeTime(30, TimeUnit.MINUTES)
                            .maxConnectionIdleTime(5, TimeUnit.MINUTES)
                            .maintenanceInitialDelay(1, TimeUnit.MINUTES)
                            .maintenanceFrequency(1, TimeUnit.MINUTES);
                    builder.applySettings(poolBuilder.build());
                })
                // Socket设置
                .applyToSocketSettings(builder -> {
                    SocketSettings.Builder socketBuilder = SocketSettings.builder()
                            .connectTimeout(properties.getHttp().getConnectTimeout(), TimeUnit.MILLISECONDS)
                            .readTimeout(properties.getHttp().getReadTimeout(), TimeUnit.MILLISECONDS);
                    builder.applySettings(socketBuilder.build());
                })
                // 服务器设置
                .applyToServerSettings(builder -> {
                    ServerSettings.Builder serverBuilder = ServerSettings.builder()
                            .heartbeatFrequency(10000, TimeUnit.MILLISECONDS);
                    builder.applySettings(serverBuilder.build());
                })
                .retryWrites(true)
                .retryReads(true)
                .build();

        return MongoClients.create(settings);
    }

    @Bean
    @Primary
    public MongoTemplate mongoTemplate(MongoClient mongoClient, MongoMappingContext context) {
        MappingMongoConverter converter = new MappingMongoConverter(
                new DefaultDbRefResolver(mongoDbFactory()),
                context
        );
        // 去掉_class字段
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        return new MongoTemplate(mongoDbFactory(), converter);
    }

    @Bean
    public MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }

    /**
     * 监控指标收集器
     */
    @Bean
    public MongoMetricsCollector mongoMetricsCollector(MongoTemplate mongoTemplate) {
        return new MongoMetricsCollector(mongoTemplate, properties);
    }

    /**
     * MongoDB 健康检查器
     */
    @Bean
    public MongoHealthIndicator mongoHealthIndicator(MongoTemplate mongoTemplate) {
        return new MongoHealthIndicator(mongoTemplate, properties);
    }
}

/**
 * MongoDB 监控指标收集器
 */
@Slf4j
@RequiredArgsConstructor
class MongoMetricsCollector {
    private final MongoTemplate mongoTemplate;
    private final TestCaseCollectorProperties properties;

    public void collectMetrics() {
        try {
            Document stats = mongoTemplate.getDb().runCommand(new Document("dbStats", 1));
            log.debug("MongoDB stats: {}", stats);
            // 这里可以将指标发送到监控系统
        } catch (Exception e) {
            log.error("Failed to collect MongoDB metrics", e);
        }
    }
}

/**
 * MongoDB 健康检查器
 */
@Slf4j
@RequiredArgsConstructor
class MongoHealthIndicator {
    private final MongoTemplate mongoTemplate;
    private final TestCaseCollectorProperties properties;

    public boolean isHealthy() {
        try {
            mongoTemplate.executeCommand("{ ping: 1 }");
            return true;
        } catch (Exception e) {
            log.error("MongoDB health check failed", e);
            return false;
        }
    }
}