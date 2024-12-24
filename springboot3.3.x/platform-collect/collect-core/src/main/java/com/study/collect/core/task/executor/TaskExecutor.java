package com.study.collect.core.task.executor;

import com.study.collect.core.task.TaskContext;
import com.study.collect.core.task.definition.TaskDefinition;

public interface TaskExecutor {
    void execute(TaskDefinition task, TaskContext context);
}