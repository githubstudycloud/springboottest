package com.study.collect.core.task.handler;

import com.study.collect.core.task.model.TaskContext;
import com.study.collect.core.task.model.TaskResult;

/**
 * 任务处理器
 */
public interface TaskHandler {
    /**
     * 执行任务
     *
     * @param context 任务上下文
     * @return 任务执行结果
     */
    TaskResult execute(TaskContext context);

    /**
     * 获取处理器类型
     *
     * @return 处理器类型标识
     */
    String getType();
}