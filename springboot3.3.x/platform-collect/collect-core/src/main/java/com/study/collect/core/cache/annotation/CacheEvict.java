package com.study.collect.core.cache.annotation;

import java.lang.annotation.*;

// 1. CacheEvict注解
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CacheEvict {
    String key() default "";       // 缓存key
    String prefix() default "";    // 前缀
    boolean allEntries() default false;  // 是否清除所有
    boolean beforeInvocation() default false; // 是否在方法执行前清除
}
