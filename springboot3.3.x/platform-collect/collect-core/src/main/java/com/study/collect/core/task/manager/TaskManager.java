package com.study.collect.core.task.manager;

import com.study.collect.core.task.definition.TaskDefinition;

public interface TaskManager {
    void submitTask(TaskDefinition task);
    void cancelTask(String taskId);
    void pauseTask(String taskId);
    void resumeTask(String taskId);
}
