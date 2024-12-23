package com.study.collect.core.cache.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

// 1. Cache注解
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Cache {
    String key() default "";       // 缓存key
    String prefix() default "";    // 前缀
    long expire() default 3600L;   // 过期时间
    TimeUnit timeUnit() default TimeUnit.SECONDS;  // 时间单位
}