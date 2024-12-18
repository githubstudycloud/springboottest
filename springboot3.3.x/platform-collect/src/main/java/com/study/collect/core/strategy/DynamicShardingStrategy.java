package com.study.collect.core.strategy;

import com.study.collect.domain.entity.task.CollectTask;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 动态分片策略实现
 */
@Slf4j
public class DynamicShardingStrategy implements ShardingStrategy {

    private final LoadBalancer loadBalancer;
    private final MetricsCollector metricsCollector;

    @Override
    public List<CollectTask> shard(CollectTask task, int shardCount) {
        try {
            // 1. 获取系统负载信息
            SystemLoadInfo loadInfo = getSystemLoadInfo();

            // 2. 动态调整分片数量
            int actualShardCount = adjustShardCount(shardCount, loadInfo);

            // 3. 计算每片大小
            DataSizeInfo sizeInfo = getDataSizeInfo(task);
            int shardSize = calculateShardSize(sizeInfo, actualShardCount);

            // 4. 创建分片任务
            return createShardTasks(task, sizeInfo, shardSize, actualShardCount);

        } catch (Exception e) {
            log.error("Dynamic sharding failed", e);
            throw new ShardingException("Dynamic sharding failed: " + e.getMessage());
        }
    }

    private int adjustShardCount(int shardCount, SystemLoadInfo loadInfo) {
        // 根据系统负载调整分片数
        if (loadInfo.getCpuUsage() > 80 || loadInfo.getMemoryUsage() > 80) {
            return Math.max(1, shardCount / 2);
        }

        if (loadInfo.getCpuUsage() < 30 && loadInfo.getMemoryUsage() < 30) {
            return Math.min(shardCount * 2, getMaxShardCount());
        }

        return shardCount;
    }

    private int calculateShardSize(DataSizeInfo sizeInfo, int shardCount) {
        // 根据数据特征计算合适的分片大小
        int baseSize = 1000; // 基础分片大小

        // 根据数据复杂度调整
        if (sizeInfo.getComplexity() == DataComplexity.HIGH) {
            baseSize = 500;
        } else if (sizeInfo.getComplexity() == DataComplexity.LOW) {
            baseSize = 2000;
        }

        // 根据系统性能调整
        SystemPerformance performance = loadBalancer.getSystemPerformance();
        double performanceFactor = calculatePerformanceFactor(performance);

        return (int) (baseSize * performanceFactor);
    }
}
