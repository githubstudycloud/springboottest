package com.study.collect.common.annotation.collect;

import java.lang.annotation.*;

/**
 * 重试注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Retryable {
    /**
     * 最大重试次数
     */
    int maxAttempts() default 3;

    /**
     * 重试间隔(ms)
     */
    long delay() default 1000;

    /**
     * 触发重试的异常
     */
    Class<? extends Throwable>[] include() default {};

    /**
     * 不触发重试的异常
     */
    Class<? extends Throwable>[] exclude() default {};
}
