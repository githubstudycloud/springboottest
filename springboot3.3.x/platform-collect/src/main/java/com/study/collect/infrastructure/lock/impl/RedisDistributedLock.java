package com.study.collect.infrastructure.lock.impl;

// Redis分布式锁
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;

/**
 * Redis分布式锁
 */
@Component
@RequiredArgsConstructor
public class RedisDistributedLock {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String LOCK_PREFIX = "lock:";
    private static final long DEFAULT_TIMEOUT = 30000; // 30秒

    /**
     * 获取锁
     */
    public boolean lock(String key, String value) {
        return lock(key, value, DEFAULT_TIMEOUT);
    }

    /**
     * 获取锁带超时
     */
    public boolean lock(String key, String value, long timeout) {
        String lockKey = LOCK_PREFIX + key;
        long start = System.currentTimeMillis();
        try {
            while (System.currentTimeMillis() - start < timeout) {
                // 尝试获取锁
                Boolean success = redisTemplate.opsForValue()
                        .setIfAbsent(lockKey, value, Duration.ofMillis(timeout));
                if (Boolean.TRUE.equals(success)) {
                    return true;
                }
                // 短暂等待后重试
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return false;
    }

    /**
     * 释放锁
     */
    public boolean unlock(String key, String value) {
        String lockKey = LOCK_PREFIX + key;
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                "return redis.call('del', KEYS[1]) " +
                "else " +
                "return 0 " +
                "end";
        Long result = redisTemplate.execute(new DefaultRedisScript<>(script, Long.class),
                Collections.singletonList(lockKey), value);
        return Long.valueOf(1).equals(result);
    }
}