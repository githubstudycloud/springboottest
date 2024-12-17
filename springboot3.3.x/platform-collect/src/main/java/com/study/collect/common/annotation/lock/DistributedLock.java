package com.study.collect.common.annotation.lock;

// 分布式锁注解

import java.lang.annotation.*;

/**
 * 分布式锁注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DistributedLock {
    /**
     * 锁的key
     */
    String key();

    /**
     * 超时时间(ms)
     */
    long timeout() default 5000;// 30000

    /**
     * 等待时间(ms)
     */
    long waitTime() default 1000;
}

