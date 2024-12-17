package com.study.collect.domain.repository.task;

// 统计仓储

import com.study.collect.domain.entity.task.TaskStats;
import com.study.collect.domain.repository.BaseRepository;
import java.util.Optional;

/**
 * 任务统计仓储接口
 */
public interface TaskStatsRepository extends BaseRepository<TaskStats, String> {
    /**
     * 根据任务ID查询统计
     */
    Optional<TaskStats> findByTaskId(String taskId);

    /**
     * 更新统计数据
     */
    void updateStats(TaskStats stats);

    /**
     * 增加成功计数
     */
    void incrementSuccessCount(String taskId);

    /**
     * 增加失败计数
     */
    void incrementFailCount(String taskId);
}
