package com.study.common.util;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 分布式锁工具类
 */
public class DistributedLockUtil {
    private static final Logger logger = LoggerFactory.getLogger(DistributedLockUtil.class);
    private static final String LOCK_PREFIX = "distributed_lock:";
    private static final long DEFAULT_WAIT_TIME = 5;
    private static final long DEFAULT_LEASE_TIME = 30;

    private final RedissonClient redissonClient;

    public DistributedLockUtil(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    /**
     * 获取锁并执行
     *
     * @param lockKey 锁键
     * @param supplier 执行函数
     * @return 执行结果
     */
    public <T> T executeWithLock(String lockKey, Supplier<T> supplier) {
        return executeWithLock(lockKey, DEFAULT_WAIT_TIME, DEFAULT_LEASE_TIME, supplier);
    }

    /**
     * 获取锁并执行,支持自定义等待和租约时间
     *
     * @param lockKey 锁键
     * @param waitTime 等待时间(秒)
     * @param leaseTime 租约时间(秒)
     * @param supplier 执行函数
     * @return 执行结果
     */
    public <T> T executeWithLock(String lockKey, long waitTime, long leaseTime, Supplier<T> supplier) {
        String fullLockKey = LOCK_PREFIX + lockKey;
        RLock lock = redissonClient.getLock(fullLockKey);
        boolean locked = false;

        try {
            locked = lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);
            if (!locked) {
                throw new RuntimeException("Failed to acquire lock: " + fullLockKey);
            }

            logger.debug("Acquired lock: {}", fullLockKey);
            return supplier.get();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted while acquiring lock", e);
        } finally {
            if (locked) {
                lock.unlock();
                logger.debug("Released lock: {}", fullLockKey);
            }
        }
    }

    /**
     * 尝试获取锁
     */
    public boolean tryLock(String lockKey) {
        return tryLock(lockKey, DEFAULT_WAIT_TIME, DEFAULT_LEASE_TIME);
    }

    /**
     * 尝试获取锁,支持自定义等待和租约时间
     */
    public boolean tryLock(String lockKey, long waitTime, long leaseTime) {
        String fullLockKey = LOCK_PREFIX + lockKey;
        RLock lock = redissonClient.getLock(fullLockKey);

        try {
            return lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * 释放锁
     */
    public void unlock(String lockKey) {
        String fullLockKey = LOCK_PREFIX + lockKey;
        RLock lock = redissonClient.getLock(fullLockKey);

        if (lock != null && lock.isHeldByCurrentThread()) {
            lock.unlock();
            logger.debug("Released lock: {}", fullLockKey);
        }
    }
}