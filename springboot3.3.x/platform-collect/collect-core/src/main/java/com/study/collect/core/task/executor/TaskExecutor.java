package com.study.collect.core.task.executor;

// 执行器接口

import com.study.collect.core.task.definition.TaskDefinition;
import com.study.collect.core.task.model.TaskContext;

public interface TaskExecutor {
    /**
     * 执行任务
     *
     * @param task    任务定义
     * @param context 任务上下文
     */
    void execute(TaskDefinition task, TaskContext context);
}

