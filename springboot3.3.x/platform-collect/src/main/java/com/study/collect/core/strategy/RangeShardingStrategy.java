package com.study.collect.core.strategy;

import com.study.collect.domain.entity.task.CollectTask;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * 范围分片策略
 */
@Component
public class RangeShardingStrategy implements ShardingStrategy {

    @Override
    public List<CollectTask> shard(CollectTask task, int shardCount) {
        if (shardCount <= 1) {
            return Collections.singletonList(task);
        }

        // 获取总范围
        long start = Long.parseLong(task.getParams().get("rangeStart").toString());
        long end = Long.parseLong(task.getParams().get("rangeEnd").toString());
        long total = end - start + 1;

        // 计算每片大小
        long shardSize = total / shardCount;
        List<CollectTask> subTasks = new ArrayList<>(shardCount);

        for (int i = 0; i < shardCount; i++) {
            long shardStart = start + i * shardSize;
            long shardEnd = i == shardCount - 1 ? end : shardStart + shardSize - 1;

            // 创建子任务
            CollectTask subTask = createSubTask(task, shardStart, shardEnd, i);
            subTasks.add(subTask);
        }

        return subTasks;
    }

    private CollectTask createSubTask(CollectTask parentTask, long start, long end, int shardIndex) {
        CollectTask subTask = new CollectTask();
        BeanUtils.copyProperties(parentTask, subTask);

        // 设置子任务特有属性
        subTask.setId(UUID.randomUUID().toString());
        subTask.getParams().put("rangeStart", start);
        subTask.getParams().put("rangeEnd", end);
        subTask.getParams().put("shardIndex", shardIndex);
        subTask.setParentTaskId(parentTask.getId());

        return subTask;
    }
}
