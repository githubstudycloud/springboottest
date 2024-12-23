package com.study.collect.core.task.scheduler;

import com.study.collect.core.task.CollectTask;
import com.study.collect.core.task.TaskResult;

// 3. 任务接口
public interface TaskExecutor {
    // 执行任务
    void execute(CollectTask task);
    // 处理结果
    void handleResult(TaskResult result);
}
