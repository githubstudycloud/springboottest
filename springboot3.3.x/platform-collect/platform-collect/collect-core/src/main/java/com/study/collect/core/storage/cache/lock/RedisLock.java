package com.study.collect.core.storage.cache.lock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisLock implements DistributedLock {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean tryLock(String key, long waitTime, long leaseTime, TimeUnit unit) {
        try {
            long startTime = System.currentTimeMillis();
            long waitMillis = unit.toMillis(waitTime);

            do {
                Boolean success = redisTemplate.opsForValue()
                        .setIfAbsent(key, Thread.currentThread().getId(), leaseTime, unit);

                if (Boolean.TRUE.equals(success)) {
                    return true;
                }

                // 等待一段时间后重试
                Thread.sleep(100);
            } while (System.currentTimeMillis() - startTime < waitMillis);

            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    @Override
    public void unlock(String key) {
        try {
            Long threadId = (Long) redisTemplate.opsForValue().get(key);
            if (threadId != null && threadId.equals(Thread.currentThread().getId())) {
                redisTemplate.delete(key);
            }
        } catch (Exception e) {
            log.error("Failed to unlock: {}", key, e);
        }
    }
}