package com.study.collect.business.testcase.aspect.mongodb;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 集合版本注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CollectionVersion {
    /**
     * 参数名称，如果指定则按参数名查找
     */
    String paramName() default "";

    /**
     * 参数位置，如果未指定参数名则按位置查找
     */
    int paramIndex() default 0;
}

