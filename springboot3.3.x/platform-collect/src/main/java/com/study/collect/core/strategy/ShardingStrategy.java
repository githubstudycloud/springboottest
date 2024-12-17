package com.study.collect.core.strategy;

/**
 * 任务分片策略接口
 */
public interface ShardingStrategy {
    /**
     * 分片任务
     * @return 返回分片后的子任务列表
     */
    List<CollectTask> shard(CollectTask task, int shardCount);
}

