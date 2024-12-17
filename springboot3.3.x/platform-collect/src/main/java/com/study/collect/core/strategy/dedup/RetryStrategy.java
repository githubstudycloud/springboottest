package com.study.collect.core.strategy.dedup;

/**
 * 重试策略接口
 */
public interface RetryStrategy {
    /**
     * 是否需要重试
     */
    boolean shouldRetry(CollectTask task, Exception e);

    /**
     * 计算重试延迟时间
     */
    long getRetryDelay(CollectTask task);
}