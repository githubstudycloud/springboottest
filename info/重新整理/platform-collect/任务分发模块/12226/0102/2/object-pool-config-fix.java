package com.study.collect.core.config;

import com.study.collect.domain.entity.UriEntity;
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

    @Bean
    public GenericObjectPool<UriEntity> uriEntityPool() {
        // 配置对象池参数
        GenericObjectPoolConfig<UriEntity> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxTotal(20);          // 最大对象数
        poolConfig.setMaxIdle(10);           // 最大空闲对象数
        poolConfig.setMinIdle(5);            // 最小空闲对象数
        poolConfig.setTestOnBorrow(true);    // 借用对象时测试
        poolConfig.setTestOnReturn(true);    // 返还对象时测试
        poolConfig.setTestWhileIdle(true);   // 空闲时测试
        poolConfig.setBlockWhenExhausted(true); // 池空时阻塞

        // 创建对象池
        return new GenericObjectPool<>(new BasePooledObjectFactory<>() {
            @Override
            public UriEntity create() throws Exception {
                try {
                    UriEntity entity = new UriEntity();
                    // 初始化基本属性
                    entity.setDeleted(false);
                    entity.setVersion(0L);
                    return entity;
                } catch (Exception e) {
                    log.error("Failed to create UriEntity in pool", e);
                    throw e;
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
                    resetAllFields(entity);
                } catch (Exception e) {
                    log.error("Failed to reset UriEntity fields", e);
                }
            }

            private void resetAllFields(Object object) {
                Class<?> clazz = object.getClass();
                while (clazz != null && !clazz.equals(Object.class)) {
                    for (Field field : clazz.getDeclaredFields()) {
                        try {
                            if (!Modifier.isStatic(field.getModifiers()) && 
                                !Modifier.isFinal(field.getModifiers())) {
                                field.setAccessible(true);
                                // 根据字段类型设置默认值
                                if (field.getType().equals(Boolean.class) || 
                                    field.getType().equals(boolean.class)) {
                                    field.set(object, false);
                                } else if (field.getType().equals(Long.class) || 
                                         field.getType().equals(long.class)) {
                                    field.set(object, 0L);
                                } else if (field.getType().equals(Integer.class) || 
                                         field.getType().equals(int.class)) {
                                    field.set(object, 0);
                                } else {
                                    field.set(object, null);
                                }
                            }
                        } catch (Exception e) {
                            log.warn("Failed to reset field: {}", field.getName(), e);
                        }
                    }
                    clazz = clazz.getSuperclass();
                }
            }

            @Override
            public boolean validateObject(PooledObject<UriEntity> p) {
                return p.getObject() != null;
            }
        }, poolConfig);
    }
}
