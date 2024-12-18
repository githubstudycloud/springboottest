package com.study.collect.infrastructure.lock.impl;

// Redis分布式锁
import com.study.collect.common.exception.sync.LockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * Redis分布式锁实现
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisDistributedLock {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String LOCK_PREFIX = "lock:";
    private static final long DEFAULT_TIMEOUT = 30000; // 30秒

    // 释放锁的Lua脚本
    private static final DefaultRedisScript<Long> RELEASE_LOCK_SCRIPT = new DefaultRedisScript<>(
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                    "return redis.call('del', KEYS[1]) " +
                    "else " +
                    "return 0 " +
                    "end",
            Long.class
    );

    /**
     * 获取锁
     */
    public boolean lock(String key, String value, long timeout) {
        String lockKey = LOCK_PREFIX + key;
        long startTime = System.currentTimeMillis();

        try {
            while (System.currentTimeMillis() - startTime < timeout) {
                Boolean success = redisTemplate.opsForValue()
                        .setIfAbsent(lockKey, value, timeout, TimeUnit.MILLISECONDS);

                if (Boolean.TRUE.equals(success)) {
                    return true;
                }

                // 短暂休眠避免频繁重试
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new LockException("Acquire lock interrupted");
        } catch (Exception e) {
            throw new LockException("Acquire lock failed: " + e.getMessage());
        }

        return false;
    }

    /**
     * 释放锁
     */
    public boolean unlock(String key, String value) {
        String lockKey = LOCK_PREFIX + key;
        try {
            Long result = redisTemplate.execute(
                    RELEASE_LOCK_SCRIPT,
                    Collections.singletonList(lockKey),
                    value
            );
            return Long.valueOf(1).equals(result);
        } catch (Exception e) {
            throw new LockException("Release lock failed: " + e.getMessage());
        }
    }

    /**
     * 续期锁
     */
    public boolean renewLock(String key, String value, long timeout) {
        String lockKey = LOCK_PREFIX + key;
        try {
            Boolean success = redisTemplate.opsForValue()
                    .setIfPresent(lockKey, value, timeout, TimeUnit.MILLISECONDS);
            return Boolean.TRUE.equals(success);
        } catch (Exception e) {
            throw new LockException("Renew lock failed: " + e.getMessage());
        }
    }
}
