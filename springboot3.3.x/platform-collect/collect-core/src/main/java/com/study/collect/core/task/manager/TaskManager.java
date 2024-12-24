package com.study.collect.core.task.manager;

import com.study.collect.core.task.definition.TaskDefinition;
import com.study.collect.core.task.model.TaskResult;

public interface TaskManager {
    /**
     * 提交任务
     *
     * @param task 任务定义
     * @return 任务结果
     */
    TaskResult submitTask(TaskDefinition task);

    /**
     * 取消任务
     *
     * @param taskId 任务ID
     */
    void cancelTask(String taskId);

    /**
     * 暂停任务
     *
     * @param taskId 任务ID
     */
    void pauseTask(String taskId);

    /**
     * 恢复任务
     *
     * @param taskId 任务ID
     */
    void resumeTask(String taskId);

    /**
     * 获取任务状态
     *
     * @param taskId 任务ID
     * @return 任务状态
     */
    TaskResult getTaskStatus(String taskId);
}
