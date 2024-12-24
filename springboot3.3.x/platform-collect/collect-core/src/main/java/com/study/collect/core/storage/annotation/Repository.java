package com.study.collect.core.storage.annotation;

import java.lang.annotation.*;

// Repository
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Repository {
    /**
     * 仓储类型
     */
    String type();

    /**
     * 存储描述
     */
    String description() default "";
}