package com.study.collect.core.task.handler;

import com.study.collect.core.task.model.TaskContext;
import com.study.collect.core.task.model.TaskResult;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractTaskHandler implements TaskHandler {

    @Override
    public TaskResult handle(TaskContext context) {
        String taskId = context.getTaskId();
        log.info("Start handling task: {}", taskId);

        try {
            // 前置处理
            beforeHandle(context);

            // 执行处理
            TaskResult result = doHandle(context);

            // 后置处理
            afterHandle(context, result);

            return result;

        } catch (Exception e) {
            log.error("Task handling failed, taskId: {}", taskId, e);
            return handleError(context, e);
        }
    }

    protected void beforeHandle(TaskContext context) {
        // 子类可以覆盖实现具体的前置处理逻辑
    }

    protected abstract TaskResult doHandle(TaskContext context);

    protected void afterHandle(TaskContext context, TaskResult result) {
        // 子类可以覆盖实现具体的后置处理逻辑
    }

    protected TaskResult handleError(TaskContext context, Exception e) {
        return TaskResult.failure(context.getTaskId(), e.getMessage());
    }
}