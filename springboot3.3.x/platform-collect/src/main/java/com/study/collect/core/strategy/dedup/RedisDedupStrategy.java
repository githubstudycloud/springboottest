package com.study.collect.core.strategy.dedup;

// Redis去重策略

import lombok.RequiredArgsConstructor;

import java.util.concurrent.TimeUnit;

/**
 * Redis去重策略
 */
@Component
@RequiredArgsConstructor
public class RedisDedupStrategy implements DedupStrategy {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String DEDUP_KEY_PREFIX = "dedup:";
    private static final long EXPIRE_TIME = 24 * 60 * 60; // 24小时过期

    @Override
    public boolean isDuplicate(String key) {
        String dedupKey = DEDUP_KEY_PREFIX + key;
        return Boolean.TRUE.equals(redisTemplate.hasKey(dedupKey));
    }

    @Override
    public void markProcessed(String key) {
        String dedupKey = DEDUP_KEY_PREFIX + key;
        redisTemplate.opsForValue().set(dedupKey, "1", EXPIRE_TIME, TimeUnit.SECONDS);
    }
}