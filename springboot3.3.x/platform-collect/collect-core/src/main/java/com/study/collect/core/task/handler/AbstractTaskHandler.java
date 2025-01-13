package com.study.collect.core.task.handler;

import com.study.collect.core.task.model.TaskContext;
import com.study.collect.core.task.model.TaskResult;
import lombok.extern.slf4j.Slf4j;

/**
 * 任务处理器抽象类
 */
@Slf4j
public abstract class AbstractTaskHandler implements TaskHandler {

    @Override
    public TaskResult execute(TaskContext context) {
        String taskId = context.getTaskId();
        log.info("开始执行任务: taskId={}, type={}", taskId, getType());

        try {
            // 前置处理
            beforeExecute(context);

            // 执行任务
            Object result = doExecute(context);

            // 后置处理
            afterExecute(context, result);

            log.info("任务执行完成: taskId={}", taskId);
            return TaskResult.success(taskId, result);

        } catch (Exception e) {
            log.error("任务执行失败: taskId={}", taskId, e);
            return TaskResult.failure(taskId, e.getMessage());
        }
    }

    /**
     * 任务执行前的处理
     */
    protected void beforeExecute(TaskContext context) {
        // 子类可以覆盖实现
    }

    /**
     * 执行具体任务
     */
    protected abstract Object doExecute(TaskContext context);

    /**
     * 任务执行后的处理
     */
    protected void afterExecute(TaskContext context, Object result) {
        // 子类可以覆盖实现
    }
}