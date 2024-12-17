package com.study.collect.common.annotation.collect;

// 处理器注解

import java.lang.annotation.*;

/**
 * 处理器注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Processor {
    /**
     * 处理器类型
     */
    String type();

    /**
     * 处理器顺序
     */
    int order() default 0;

    /**
     * 是否启用
     */
    boolean enabled() default true;
}