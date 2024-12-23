package com.study.collect.core.cache.lock;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

// 2. Redis分布式锁实现
@Component
@RequiredArgsConstructor
public class RedisLock implements DistributedLock {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean tryLock(String key, long waitTime, long leaseTime, TimeUnit unit) {
        try {
            long startTime = System.currentTimeMillis();
            long waitMillis = unit.toMillis(waitTime);

            while (System.currentTimeMillis() - startTime < waitMillis) {
                Boolean success = redisTemplate.opsForValue()
                        .setIfAbsent(key, Thread.currentThread().getId(), leaseTime, unit);

                if (Boolean.TRUE.equals(success)) {
                    return true;
                }

                Thread.sleep(100);
            }
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    @Override
    public void unlock(String key) {
        Long threadId = (Long) redisTemplate.opsForValue().get(key);
        if (threadId != null && threadId.equals(Thread.currentThread().getId())) {
            redisTemplate.delete(key);
        }
    }
}
