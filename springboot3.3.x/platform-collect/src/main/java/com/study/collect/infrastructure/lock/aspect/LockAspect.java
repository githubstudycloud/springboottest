package com.study.collect.infrastructure.lock.aspect;

// 锁切面

import com.study.collect.common.annotation.lock.DistributedLock;
import com.study.collect.common.exception.sync.LockException;
import com.study.collect.infrastructure.lock.impl.RedisDistributedLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 分布式锁切面
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class DistributedLockAspect {

    private final RedisDistributedLock lock;

    @Around("@annotation(distributedLock)")
    public Object around(ProceedingJoinPoint point, DistributedLock distributedLock) throws Throwable {
        String key = distributedLock.key();
        String value = UUID.randomUUID().toString();

        try {
            // 获取锁
            boolean acquired = lock.lock(key, value, distributedLock.timeout());
            if (!acquired) {
                throw new LockException("Failed to acquire lock: " + key);
            }

            // 执行业务逻辑
            return point.proceed();
        } finally {
            // 释放锁
            lock.unlock(key, value);
        }
    }
}