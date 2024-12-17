package com.study.collect.domain.repository.task;

// 任务仓储

import com.study.collect.domain.entity.task.CollectTask;
import com.study.collect.domain.repository.BaseRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 任务仓储接口
 */
public interface TaskRepository extends BaseRepository<CollectTask, String> {
    /**
     * 根据状态查询任务
     */
    List<CollectTask> findByStatus(String status);

    /**
     * 更新任务状态
     */
    void updateStatus(String taskId, String status);

    /**
     * 增加重试次数
     */
    void incrementRetryTimes(String taskId);

    /**
     * 查询超时任务
     */
    List<CollectTask> findTimeoutTasks(LocalDateTime timeout);
}