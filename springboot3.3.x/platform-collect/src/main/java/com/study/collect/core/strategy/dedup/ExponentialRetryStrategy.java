package com.study.collect.core.strategy.dedup;

import com.study.collect.domain.entity.task.CollectTask;
import org.springframework.stereotype.Component;

/**
 * 指数退避重试策略
 */
@Component
public class ExponentialRetryStrategy implements RetryStrategy {

    private static final long INITIAL_DELAY = 1000; // 初始1秒
    private static final long MAX_DELAY = 60 * 1000; // 最大1分钟

    @Override
    public boolean shouldRetry(CollectTask task, Exception e) {
        // 超过最大重试次数
        if (task.getRetryTimes() >= task.getMaxRetryTimes()) {
            return false;
        }

        // 只重试特定异常
        return e instanceof CollectException || e instanceof ProcessException;
    }

    @Override
    public long getRetryDelay(CollectTask task) {
        // 计算指数退避延迟
        long delay = INITIAL_DELAY * (1L << task.getRetryTimes());
        return Math.min(delay, MAX_DELAY);
    }
}
