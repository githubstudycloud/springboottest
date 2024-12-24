package com.study.collect.core.processor.annotation;

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
     * 处理器描述
     */
    String description() default "";

    /**
     * 是否启用
     */
    boolean enabled() default true;

    /**
     * 处理器执行顺序
     */
    int order() default Integer.MAX_VALUE;
}
