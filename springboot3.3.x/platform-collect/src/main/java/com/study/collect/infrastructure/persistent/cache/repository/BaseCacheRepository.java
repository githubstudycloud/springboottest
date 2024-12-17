package com.study.collect.infrastructure.persistent.cache.repository;

import com.study.collect.common.utils.common.JsonUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

/**
 * 基础缓存仓储
 */
@Repository
public class BaseCacheRepository {

    public BaseCacheRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private final RedisTemplate<String, Object> redisTemplate;

    protected void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    protected void set(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    protected <T> T get(String key, Class<T> clazz) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return JsonUtils.fromJson((String) value, clazz);
        }
        return (T) value;
    }

    protected void delete(String key) {
        redisTemplate.delete(key);
    }

    protected Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    protected Long increment(String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    protected void expire(String key, long timeout, TimeUnit unit) {
        redisTemplate.expire(key, timeout, unit);
    }
}