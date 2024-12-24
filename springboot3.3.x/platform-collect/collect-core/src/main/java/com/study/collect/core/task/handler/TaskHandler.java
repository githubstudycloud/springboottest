package com.study.collect.core.task.handler;

import com.study.collect.core.task.TaskContext;

public interface TaskHandler {
    void handle(TaskContext context);
}