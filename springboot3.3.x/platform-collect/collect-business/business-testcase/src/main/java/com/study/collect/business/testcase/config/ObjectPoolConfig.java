package com.study.collect.business.testcase.config;

import com.study.collect.business.testcase.constant.CollectionConstants;
import com.study.collect.business.testcase.entity.UriEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

@Configuration
@Slf4j
public class ObjectPoolConfig {

    @Bean(destroyMethod = "close")
    public GenericObjectPool<UriEntity> uriEntityPool() {
        GenericObjectPoolConfig<UriEntity> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxTotal(CollectionConstants.POOL_MAX_TOTAL);
        poolConfig.setMaxIdle(CollectionConstants.POOL_MAX_IDLE);
        poolConfig.setMinIdle(CollectionConstants.POOL_MIN_IDLE);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setBlockWhenExhausted(true);
        poolConfig.setTimeBetweenEvictionRuns(java.time.Duration.ofMinutes(1));
        poolConfig.setJmxEnabled(true);
        poolConfig.setJmxNamePrefix("uri-entity-pool");

        return new GenericObjectPool<>(new BasePooledObjectFactory<>() {
            @Override
            public UriEntity create() {
                try {
                    return new UriEntity();
                } catch (Exception e) {
                    log.error("Failed to create UriEntity in pool", e);
                    throw new RuntimeException("Failed to create UriEntity", e);
                }
            }

            @Override
            public PooledObject<UriEntity> wrap(UriEntity entity) {
                return new DefaultPooledObject<>(entity);
            }

            @Override
            public void passivateObject(PooledObject<UriEntity> p) {
                try {
                    UriEntity entity = p.getObject();
                    entity.reset();
                } catch (Exception e) {
                    log.error("Failed to reset UriEntity fields", e);
                }
            }

            @Override
            public boolean validateObject(PooledObject<UriEntity> p) {
                return p.getObject() != null;
            }
        }, poolConfig);
    }
}