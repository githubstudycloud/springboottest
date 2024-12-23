package com.study.collect.core.task.splitter;

import com.study.collect.core.task.CollectTask;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

// 2. 默认分片实现
@Component
public class DefaultTaskSplitter implements TaskSplitter {

    @Override
    public List<CollectTask> split(CollectTask task, int shardCount) {
        List<CollectTask> tasks = new ArrayList<>();

        // 获取需要分片的参数
        List<?> params = (List<?>) task.getParams().get("dataList");
        if (CollectionUtils.isEmpty(params)) {
            return Collections.singletonList(task);
        }

        // 计算分片
        int size = params.size();
        int shardSize = (size + shardCount - 1) / shardCount;

        // 生成分片任务
        for (int i = 0; i < shardCount; i++) {
            int fromIndex = i * shardSize;
            if (fromIndex >= size) {
                break;
            }

            int toIndex = Math.min((i + 1) * shardSize, size);
            List<?> subParams = params.subList(fromIndex, toIndex);

            CollectTask subTask = new CollectTask();
            BeanUtils.copyProperties(task, subTask);
            subTask.setId(UUID.randomUUID().toString());
            subTask.getParams().put("dataList", subParams);
            subTask.setShardIndex(i);
            subTask.setShardTotal(shardCount);

            tasks.add(subTask);
        }

        return tasks;
    }
}
