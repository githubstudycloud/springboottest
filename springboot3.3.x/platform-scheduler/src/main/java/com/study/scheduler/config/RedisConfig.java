package com.study.scheduler.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.List;

@Configuration
@AutoConfigureBefore(RedisAutoConfiguration.class)
public class RedisConfig {

    private static final Logger logger = LoggerFactory.getLogger(RedisConfig.class);

    @Value("${spring.data.redis.host:localhost}")
    private String host;

    @Value("${spring.data.redis.port:6379}")
    private int port;

    @Value("${spring.data.redis.password:}")
    private String password;

    @Value("${spring.data.redis.cluster.nodes:}")
    private List<String> clusterNodes;

    @Value("${spring.data.redis.timeout:2000}")
    private long timeout;

    @Value("${spring.data.redis.lettuce.pool.max-active:8}")
    private int maxActive;

    @Value("${spring.data.redis.lettuce.pool.max-idle:8}")
    private int maxIdle;

    @Value("${spring.data.redis.lettuce.pool.min-idle:0}")
    private int minIdle;

    @Value("${spring.data.redis.lettuce.pool.max-wait:-1}")
    private long maxWait;

    @PostConstruct
    public void init() {
        logger.info("=============== Redis Configuration Initializing ===============");
        logger.info("Redis Mode: {}", (clusterNodes == null || clusterNodes.isEmpty()) ? "Standalone" : "Cluster");
        logger.info("Redis Host: {}", host);
        logger.info("Redis Port: {}", port);
        logger.info("Redis Password: {}", password.isEmpty() ? "Not Set" : "Set");
        if (clusterNodes != null && !clusterNodes.isEmpty()) {
            logger.info("Redis Cluster Nodes: {}", clusterNodes);
        }
        logger.info("Redis Pool Config:");
        logger.info("  Max Active: {}", maxActive);
        logger.info("  Max Idle: {}", maxIdle);
        logger.info("  Min Idle: {}", minIdle);
        logger.info("  Max Wait: {}", maxWait);
        logger.info("=============================================================");
    }

    @Bean
    @Primary
    public RedisConnectionFactory redisConnectionFactory() {
        logger.info("Creating RedisConnectionFactory...");
        LettuceClientConfiguration clientConfig = getLettuceClientConfiguration();

        if (clusterNodes != null && !clusterNodes.isEmpty()) {
            return createClusterConnectionFactory(clientConfig);
        } else {
            return createStandaloneConnectionFactory(clientConfig);
        }
    }

    @Bean
    public RedissonClient redissonClient() {
        logger.info("Creating RedissonClient...");
        Config config = new Config();

        if (clusterNodes != null && !clusterNodes.isEmpty()) {
            // 集群模式
            ClusterServersConfig clusterConfig = config.useClusterServers();
            for (String node : clusterNodes) {
                clusterConfig.addNodeAddress("redis://" + node);
            }
            if (!password.isEmpty()) {
                clusterConfig.setPassword(password);
            }
            clusterConfig.setTimeout((int) timeout)
                    .setMasterConnectionPoolSize(maxActive)
                    .setMasterConnectionMinimumIdleSize(minIdle)
                    .setSlaveConnectionPoolSize(maxActive)
                    .setSlaveConnectionMinimumIdleSize(minIdle);
            logger.info("Configured Redisson for cluster mode");
        } else {
            // 单机模式
            SingleServerConfig serverConfig = config.useSingleServer()
                    .setAddress("redis://" + host + ":" + port)
                    .setTimeout((int) timeout)
                    .setConnectionPoolSize(maxActive)
                    .setConnectionMinimumIdleSize(minIdle);
            if (!password.isEmpty()) {
                serverConfig.setPassword(password);
            }
            logger.info("Configured Redisson for standalone mode");
        }

        return Redisson.create(config);
    }

    private LettuceClientConfiguration getLettuceClientConfiguration() {
        GenericObjectPoolConfig<?> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxTotal(maxActive);
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMinIdle(minIdle);
        poolConfig.setMaxWait(Duration.ofMillis(maxWait));

        return LettucePoolingClientConfiguration.builder()
                .commandTimeout(Duration.ofMillis(timeout))
                .poolConfig(poolConfig)
                .build();
    }

    private RedisConnectionFactory createClusterConnectionFactory(LettuceClientConfiguration clientConfig) {
        logger.info("Creating Redis Cluster connection factory");
        RedisClusterConfiguration clusterConfig = new RedisClusterConfiguration(clusterNodes);
        if (!password.isEmpty()) {
            clusterConfig.setPassword(password);
        }
        return new LettuceConnectionFactory(clusterConfig, clientConfig);
    }

    private RedisConnectionFactory createStandaloneConnectionFactory(LettuceClientConfiguration clientConfig) {
        logger.info("Creating Redis Standalone connection factory for {}:{}", host, port);
        RedisStandaloneConfiguration standaloneConfig = new RedisStandaloneConfiguration(host, port);
        if (!password.isEmpty()) {
            standaloneConfig.setPassword(password);
        }
        return new LettuceConnectionFactory(standaloneConfig, clientConfig);
    }

    @Bean
    @Primary
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        logger.info("Initializing RedisTemplate...");
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL);
        mapper.registerModule(new JavaTimeModule());

        GenericJackson2JsonRedisSerializer jsonSerializer =
                new GenericJackson2JsonRedisSerializer(mapper);

        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        logger.info("RedisTemplate initialized successfully");
        return template;
    }
}