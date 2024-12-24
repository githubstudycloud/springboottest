package com.study.collect.core.task.executor;

import com.study.collect.core.task.TaskContext;
import com.study.collect.core.task.definition.TaskDefinition;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractTaskExecutor implements TaskExecutor {

    @Override
    public void execute(TaskDefinition task, TaskContext context) {
        try {
            // 1. 前置处理
            beforeExecute(task, context);

            // 2. 执行任务
            doExecute(task, context);

            // 3. 后置处理
            afterExecute(task, context);

        } catch (Exception e) {
            log.error("Task execute error", e);
            onError(task, context, e);
        }
    }

    protected void beforeExecute(TaskDefinition task, TaskContext context) {
        // 默认空实现
    }

    protected abstract void doExecute(TaskDefinition task, TaskContext context);

    protected void afterExecute(TaskDefinition task, TaskContext context) {
        // 默认空实现
    }

    protected void onError(TaskDefinition task, TaskContext context, Exception e) {
        // 默认空实现
    }
}