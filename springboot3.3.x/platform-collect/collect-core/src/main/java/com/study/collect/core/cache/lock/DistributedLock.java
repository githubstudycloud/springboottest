package com.study.collect.core.cache.lock;

import java.util.concurrent.TimeUnit;

// 4. DistributedLock接口
public interface DistributedLock {
    /**
     * 获取锁
     */
    boolean tryLock(String key, long waitTime, long leaseTime, TimeUnit unit);

    /**
     * 释放锁
     */
    void unlock(String key);
}