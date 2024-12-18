package com.study.collect.core.strategy;

import com.study.collect.domain.entity.task.CollectTask;
import io.micrometer.core.instrument.Timer;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 任务分片策略管理器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ShardingStrategyManager {

    private final LoadBalancer loadBalancer;
    private final MetricsCollector metricsCollector;

    private final Map<String, ShardingStrategy> strategies = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        // 注册默认策略
        registerStrategy("RANGE", new RangeShardingStrategy());
        registerStrategy("HASH", new HashShardingStrategy());
        registerStrategy("DYNAMIC", new DynamicShardingStrategy());
    }

    /**
     * 执行任务分片
     */
    public List<CollectTask> shard(CollectTask task) {
        Timer.Sample timer = metricsCollector.startTimer("task_sharding");
        try {
            // 1. 获取分片策略
            ShardingStrategy strategy = getStrategy(task);

            // 2. 计算分片数量
            int shardCount = calculateShardCount(task);

            // 3. 执行分片
            return strategy.shard(task, shardCount);

        } finally {
            metricsCollector.stopTimer(timer);
        }
    }

    private ShardingStrategy getStrategy(CollectTask task) {
        // 根据任务类型和参数选择策略
        if (task.getParams().containsKey("shardingStrategy")) {
            String strategyName = task.getParams().get("shardingStrategy").toString();
            return strategies.getOrDefault(strategyName,
                    strategies.get("RANGE")); // 默认使用范围分片
        }
        return strategies.get("RANGE");
    }

    private int calculateShardCount(CollectTask task) {
        // 1. 获取系统配置的最大分片数
        int maxShards = getMaxShardCount();

        // 2. 获取可用节点数
        int availableNodes = loadBalancer.getAvailableNodes().size();

        // 3. 根据数据量计算建议分片数
        int suggestedShards = calculateSuggestedShards(task);

        // 4. 取三者最小值
        return Math.min(Math.min(maxShards, availableNodes), suggestedShards);
    }

    private int calculateSuggestedShards(CollectTask task) {
        // 根据数据量计算建议分片数
        long dataSize = getDataSize(task);
        int shardSize = getShardSize(task);

        if (dataSize <= 0 || shardSize <= 0) {
            return 1;
        }

        return (int) Math.ceil((double) dataSize / shardSize);
    }
}

