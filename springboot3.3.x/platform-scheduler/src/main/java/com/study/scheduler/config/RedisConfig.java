package com.study.scheduler.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
public class RedisConfig {

    private static final Logger logger = LoggerFactory.getLogger(RedisConfig.class);

    @Value("${spring.redis.host:localhost}")
    private String host;

    @Value("${spring.redis.port:6379}")
    private int port;

    @Value("${spring.redis.password:}")
    private String password;

    @Value("${spring.redis.cluster.nodes:}")
    private List<String> clusterNodes;

    @Value("${spring.redis.timeout:2000}")
    private long timeout;

    @Value("${spring.redis.lettuce.pool.max-active:8}")
    private int maxActive;

    @Value("${spring.redis.lettuce.pool.max-idle:8}")
    private int maxIdle;

    @Value("${spring.redis.lettuce.pool.min-idle:0}")
    private int minIdle;

    @Value("${spring.redis.lettuce.pool.max-wait:-1}")
    private long maxWait;

    @PostConstruct
    public void init() {
        logger.info("Redis配置初始化");
        logger.info("Redis mode: {}", clusterNodes.isEmpty() ? "standalone" : "cluster");
        logger.info("Redis nodes: {}", clusterNodes.isEmpty() ? host + ":" + port : clusterNodes);
        logger.info("Redis password: {}", password.isEmpty() ? "not set" : "set");
        logger.info("Redis pool config - maxActive: {}, maxIdle: {}, minIdle: {}, maxWait: {}",
                maxActive, maxIdle, minIdle, maxWait);
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        LettuceClientConfiguration clientConfig = getLettuceClientConfiguration();

        if (!clusterNodes.isEmpty()) {
            return createClusterConnectionFactory(clientConfig);
        } else {
            return createStandaloneConnectionFactory(clientConfig);
        }
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
        logger.info("Creating Redis Standalone connection factory");
        RedisStandaloneConfiguration standaloneConfig = new RedisStandaloneConfiguration(host, port);
        if (!password.isEmpty()) {
            standaloneConfig.setPassword(password);
        }
        return new LettuceConnectionFactory(standaloneConfig, clientConfig);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        // 创建RedisTemplate对象
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 创建Jackson序列化器
        ObjectMapper mapper = new ObjectMapper();
        // 设置可见性
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 启用默认类型
        mapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL);
        // 注册Java8时间模块
        mapper.registerModule(new JavaTimeModule());

        // 创建JSON序列化器（使用增强的GenericJackson2JsonRedisSerializer）
        GenericJackson2JsonRedisSerializer jsonSerializer =
                new GenericJackson2JsonRedisSerializer(mapper);

        // 设置key和value的序列化器
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }
}