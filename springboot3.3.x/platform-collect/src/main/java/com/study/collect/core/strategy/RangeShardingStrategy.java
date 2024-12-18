package com.study.collect.core.strategy;

import com.study.collect.domain.entity.task.CollectTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.util.*;

/**
 * 范围分片策略实现
 */
@Slf4j
public class RangeShardingStrategy implements ShardingStrategy {

    @Override
    public List<CollectTask> shard(CollectTask task, int shardCount) {
        if (shardCount <= 1) {
            return Collections.singletonList(task);
        }

        try {
            // 1. 获取范围信息
            long startNum = Long.parseLong(task.getParams()
                    .getOrDefault("rangeStart", "0").toString());
            long endNum = Long.parseLong(task.getParams()
                    .getOrDefault("rangeEnd", "0").toString());

            // 2. 计算每片大小
            long totalSize = endNum - startNum + 1;
            long shardSize = totalSize / shardCount;

            // 3. 创建分片任务
            List<CollectTask> shardTasks = new ArrayList<>();
            for (int i = 0; i < shardCount; i++) {
                long shardStart = startNum + i * shardSize;
                long shardEnd = (i == shardCount - 1) ? endNum
                        : shardStart + shardSize - 1;

                CollectTask shardTask = createShardTask(task, i, shardStart, shardEnd);
                shardTasks.add(shardTask);
            }

            return shardTasks;

        } catch (Exception e) {
            log.error("Range sharding failed", e);
            throw new ShardingException("Range sharding failed: " + e.getMessage());
        }
    }

    private CollectTask createShardTask(CollectTask task, int shardIndex,
                                        long start, long end) {

        CollectTask shardTask = new CollectTask();
        BeanUtils.copyProperties(task, shardTask);

        // 设置分片特有属性
        shardTask.setId(UUID.randomUUID().toString());
        shardTask.setParentTaskId(task.getId());
        shardTask.setShardIndex(shardIndex);

        // 更新分片参数
        Map<String, Object> params = new HashMap<>(task.getParams());
        params.put("rangeStart", start);
        params.put("rangeEnd", end);
        shardTask.setParams(params);

        return shardTask;
    }
}