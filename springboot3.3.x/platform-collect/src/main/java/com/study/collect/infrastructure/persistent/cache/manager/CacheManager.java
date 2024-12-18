package com.study.collect.infrastructure.persistent.cache.manager;

import com.study.collect.common.utils.common.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 缓存管理器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CacheManager {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final long DEFAULT_TIMEOUT = 3600; // 1小时

    /**
     * 设置缓存
     */
    public void set(String key, Object value) {
        set(key, value, DEFAULT_TIMEOUT);
    }

    /**
     * 设置缓存带过期时间
     */
    public void set(String key, Object value, long timeout) {
        try {
            if (value instanceof String) {
                redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
            } else {
                String jsonValue = JsonUtils.toJson(value);
                redisTemplate.opsForValue().set(key, jsonValue, timeout, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            log.error("Set cache failed, key: {}", key, e);
        }
    }

    /**
     * 获取缓存
     */
    public <T> T get(String key, Class<T> clazz) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value == null) {
                return null;
            }
            if (value instanceof String) {
                return JsonUtils.fromJson((String) value, clazz);
            }
            return (T) value;
        } catch (Exception e) {
            log.error("Get cache failed, key: {}", key, e);
            return null;
        }
    }

    /**
     * 删除缓存
     */
    public void delete(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.error("Delete cache failed, key: {}", key, e);
        }
    }

    /**
     * 设置过期时间
     */
    public boolean expire(String key, long timeout) {
        try {
            return Boolean.TRUE.equals(redisTemplate.expire(key, timeout, TimeUnit.SECONDS));
        } catch (Exception e) {
            log.error("Set expire failed, key: {}", key, e);
            return false;
        }
    }

    /**
     * 是否存在key
     */
    public boolean hasKey(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("Check key failed, key: {}", key, e);
            return false;
        }
    }

    /**
     * 原子递增
     */
    public long increment(String key, long delta) {
        try {
            return redisTemplate.opsForValue().increment(key, delta);
        } catch (Exception e) {
            log.error("Increment failed, key: {}", key, e);
            return 0;
        }
    }
}