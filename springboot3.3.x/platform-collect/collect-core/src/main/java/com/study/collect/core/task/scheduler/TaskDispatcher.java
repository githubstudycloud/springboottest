package com.study.collect.core.task.scheduler;

import com.study.collect.core.task.enums.TaskStatusEnum;
import com.study.collect.core.mq.message.TaskMessage;
import com.study.collect.core.mq.producer.TaskProducer;
import com.study.collect.core.task.entity.TaskInstance;
import com.study.collect.core.task.service.TaskExecuteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TaskDispatcher {

    private final TaskProducer taskProducer;
    private final TaskExecuteService taskExecuteService;

    @Autowired
    public TaskDispatcher(TaskProducer taskProducer, TaskExecuteService taskExecuteService) {
        this.taskProducer = taskProducer;
        this.taskExecuteService = taskExecuteService;
    }

    public void dispatch(TaskInstance instance) {
        try {
            // 更新任务状态为执行中
            taskExecuteService.updateTaskStatus(
                    instance.getInstanceId(),
                    TaskStatusEnum.RUNNING.getCode(),
                    null
            );

            // 转换并发送消息
            TaskMessage message = convertToMessage(instance);
            taskProducer.sendTask(message);

            log.info("Task dispatched successfully: instanceId={}, taskCode={}, shardIndex={}/{}",
                    instance.getInstanceId(),
                    instance.getTaskCode(),
                    instance.getShardIndex() + 1,
                    instance.getShardTotal()
            );

        } catch (Exception e) {
            log.error("Failed to dispatch task: " + instance.getInstanceId(), e);

            // 更新任务状态为失败
            taskExecuteService.updateTaskStatus(
                    instance.getInstanceId(),
                    TaskStatusEnum.FAILED.getCode(),
                    "Failed to dispatch task: " + e.getMessage()
            );

            throw new RuntimeException("Task dispatch failed", e);
        }
    }

    private TaskMessage convertToMessage(TaskInstance instance) {
        TaskMessage message = new TaskMessage();
        message.setTaskId(instance.getTaskCode());
        message.setInstanceId(instance.getInstanceId());
        message.setShardIndex(instance.getShardIndex());
        message.setShardTotal(instance.getShardTotal());
        message.setShardParam(instance.getShardParam());
        message.setHostName(instance.getHostName());
        return message;
    }
}