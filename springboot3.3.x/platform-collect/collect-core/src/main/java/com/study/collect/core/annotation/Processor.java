package com.study.collect.core.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Processor {
    /**
     * 处理器类型
     */
    String type();

    /**
     * 处理顺序
     */
    int order() default 0;

    /**
     * 是否启用
     */
    boolean enabled() default true;
}
