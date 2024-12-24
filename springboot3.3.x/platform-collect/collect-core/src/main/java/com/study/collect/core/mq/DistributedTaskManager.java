package com.study.collect.core.mq;

import com.study.collect.core.mq.message.TaskMessage;
import com.study.collect.core.mq.producer.TaskProducer;
import com.study.collect.core.task.definition.TaskDefinition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DistributedTaskManager {

    private final TaskProducer taskProducer;
    private final TaskScheduler taskScheduler;

    // 提交任务
    public void submitTask(TaskDefinition task) {
        // 1. 构建任务消息
        TaskMessage message = TaskMessage.builder()
                .taskId(task.getTaskId())
                .task(task)
                .build();

        // 2. 判断是否需要分片
        if (isShardingTask(task)) {
            // 分片发送
            taskProducer.sendShardingTask(message, getShardingTotal(task));
        } else {
            // 直接发送
            taskProducer.sendTask(message);
        }
    }
}
