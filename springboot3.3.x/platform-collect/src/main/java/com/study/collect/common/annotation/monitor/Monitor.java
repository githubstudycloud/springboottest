package com.study.collect.common.annotation.monitor;

import java.lang.annotation.*;

/**
 * 监控注解
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Monitor {
    /**
     * 监控名称
     */
    String name();

    /**
     * 监控描述
     */
    String description() default "";

    /**
     * 是否记录参数
     */
    boolean logParams() default false;

    /**
     * 是否记录结果
     */
    boolean logResult() default false;
}