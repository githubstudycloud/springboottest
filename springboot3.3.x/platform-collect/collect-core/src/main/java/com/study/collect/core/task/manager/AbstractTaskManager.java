package com.study.collect.core.task.manager;

import com.study.collect.core.mq.producer.TaskProducer;
import com.study.collect.core.task.definition.TaskDefinition;
import com.study.collect.core.task.model.TaskResult;
import com.study.collect.core.task.scheduler.TaskScheduler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractTaskManager implements TaskManager {

    protected final TaskProducer taskProducer;
    protected final TaskScheduler taskScheduler;

    protected AbstractTaskManager(TaskProducer taskProducer, TaskScheduler taskScheduler) {
        this.taskProducer = taskProducer;
        this.taskScheduler = taskScheduler;
    }

    @Override
    public TaskResult submitTask(TaskDefinition task) {
        try {
            validateTask(task);
            beforeSubmit(task);

            // 提交任务到调度器
            if (isScheduledTask(task)) {
                taskScheduler.addTask(task);
                return TaskResult.success(task.getTaskId(), "Task scheduled successfully");
            }

            // 发送任务到消息队列
            if (task.getSharding() != null && task.getSharding().isEnabled()) {
                taskProducer.sendShardingTask(task, task.getSharding().getTotal());
            } else {
                taskProducer.sendTask(task);
            }

            afterSubmit(task);
            return TaskResult.success(task.getTaskId(), "Task submitted successfully");

        } catch (Exception e) {
            log.error("Failed to submit task, taskId: {}", task.getTaskId(), e);
            return TaskResult.failure(task.getTaskId(), e.getMessage());
        }
    }

    @Override
    public void cancelTask(String taskId) {
        log.info("Canceling task: {}", taskId);
        taskScheduler.removeTask(taskId);
        doCancelTask(taskId);
    }

    @Override
    public void pauseTask(String taskId) {
        log.info("Pausing task: {}", taskId);
        taskScheduler.pauseTask(taskId);
        doPauseTask(taskId);
    }

    @Override
    public void resumeTask(String taskId) {
        log.info("Resuming task: {}", taskId);
        taskScheduler.resumeTask(taskId);
        doResumeTask(taskId);
    }

    protected void validateTask(TaskDefinition task) {
        // 任务基础校验
        if (task == null) {
            throw new IllegalArgumentException("Task definition cannot be null");
        }
        if (task.getTaskId() == null || task.getTaskId().trim().isEmpty()) {
            throw new IllegalArgumentException("Task ID cannot be empty");
        }
        if (task.getTaskHandler() == null || task.getTaskHandler().trim().isEmpty()) {
            throw new IllegalArgumentException("Task handler cannot be empty");
        }
    }

    protected boolean isScheduledTask(TaskDefinition task) {
        return task.getCronExpression() != null && !task.getCronExpression().trim().isEmpty();
    }

    protected void beforeSubmit(TaskDefinition task) {
        // 子类可以覆盖此方法实现提交前的处理逻辑
    }

    protected void afterSubmit(TaskDefinition task) {
        // 子类可以覆盖此方法实现提交后的处理逻辑
    }

    protected abstract void doCancelTask(String taskId);

    protected abstract void doPauseTask(String taskId);

    protected abstract void doResumeTask(String taskId);
}
