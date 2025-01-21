package com.study.collect.core.storage.cache.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CacheLock {
    String key();                 // 锁key

    String prefix() default "";   // 前缀

    long waitTime() default 3L;   // 等待时间

    long leaseTime() default 10L; // 租约时间

    TimeUnit timeUnit() default TimeUnit.SECONDS;  // 时间单位
}