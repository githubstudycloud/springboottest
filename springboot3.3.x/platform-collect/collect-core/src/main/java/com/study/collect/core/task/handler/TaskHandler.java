package com.study.collect.core.task.handler;

import com.study.collect.core.task.model.TaskContext;
import com.study.collect.core.task.model.TaskResult;

public interface TaskHandler {
    /**
     * 处理任务
     *
     * @param context 任务上下文
     * @return 处理结果
     */
    TaskResult handle(TaskContext context);
}
