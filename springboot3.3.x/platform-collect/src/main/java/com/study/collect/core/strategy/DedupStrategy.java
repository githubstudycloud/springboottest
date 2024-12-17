package com.study.collect.core.strategy;

/**
 * 去重策略接口
 */
public interface DedupStrategy {
    /**
     * 检查是否重复
     */
    boolean isDuplicate(String key);

    /**
     * 标记为已处理
     */
    void markProcessed(String key);
}
