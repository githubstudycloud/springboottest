package com.study.collect.core.collector;

import com.study.collect.entity.CollectTask;
import com.study.collect.entity.TaskResult;
import com.study.collect.enums.TaskStatus;

public interface Collector {
    TaskResult collect(CollectTask task);

    TaskStatus getTaskStatus(String taskId);

    void stopTask(String taskId);
}