package com.study.collect.core.task.manager;

import com.study.collect.core.mq.producer.TaskProducer;
import com.study.collect.core.task.definition.TaskDefinition;
import com.study.collect.core.task.scheduler.TaskScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DefaultTaskManager implements TaskManager {

    private final TaskProducer taskProducer;
    private final TaskScheduler taskScheduler;

    @Override
    public void submitTask(TaskDefinition task) {
        // 1. 校验任务
        validateTask(task);

        // 2. 分发任务
        if (task.getSharding() != null && task.getSharding().isEnabled()) {
            // 分片执行
            taskProducer.sendShardingTask(task, task.getSharding().getTotal());
        } else {
            // 单节点执行
            taskProducer.sendTask(task);
        }
    }

    @Override
    public void cancelTask(String taskId) {
        taskScheduler.cancelTask(taskId);
    }

    @Override
    public void pauseTask(String taskId) {
        taskScheduler.pauseTask(taskId);
    }

    @Override
    public void resumeTask(String taskId) {
        taskScheduler.resumeTask(taskId);
    }

    private void validateTask(TaskDefinition task) {
        // 任务参数校验
    }
}