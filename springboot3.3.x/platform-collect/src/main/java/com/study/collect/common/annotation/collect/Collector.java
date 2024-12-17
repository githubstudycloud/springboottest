package com.study.collect.common.annotation.collect;

//采集器注解
import java.lang.annotation.*;

/**
 * 采集器注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Collector {
    /**
     * 采集器类型
     */
    String type();

    /**
     * 采集器描述
     */
    String description() default "";

    /**
     * 是否启用
     */
    boolean enabled() default true;
}

