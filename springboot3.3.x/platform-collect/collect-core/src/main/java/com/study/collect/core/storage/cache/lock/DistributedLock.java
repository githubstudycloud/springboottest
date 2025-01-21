package com.study.collect.core.storage.cache.lock;

import java.util.concurrent.TimeUnit;

public interface DistributedLock {
    /**
     * 获取锁
     *
     * @param key       锁的key
     * @param waitTime  等待时间
     * @param leaseTime 租约时间
     * @param unit      时间单位
     * @return 是否获取成功
     */
    boolean tryLock(String key, long waitTime, long leaseTime, TimeUnit unit);

    /**
     * 释放锁
     *
     * @param key 锁的key
     */
    void unlock(String key);
}
