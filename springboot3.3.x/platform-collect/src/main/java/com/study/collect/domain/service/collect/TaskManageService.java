package com.study.collect.domain.service.collect;

// 任务管理服务
import com.study.collect.common.enums.collect.TaskStatus;
import com.study.collect.common.exception.collect.TaskException;
import com.study.collect.core.engine.CollectEngine;
import com.study.collect.domain.entity.task.CollectTask;
import com.study.collect.domain.entity.task.TaskStats;
import com.study.collect.domain.repository.task.TaskRepository;
import com.study.collect.domain.repository.task.TaskStatsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 任务管理服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskManageService {

    private final TaskRepository taskRepository;
    private final TaskStatsRepository statsRepository;
    private final CollectEngine collectEngine;
    private final MetricsCollector metricsCollector;

    /**
     * 提交任务
     */
    @Transactional
    public String submitTask(CollectTask task) {
        try {
            // 1. 校验任务
            validateTask(task);

            // 2. 初始化任务
            initTask(task);

            // 3. 保存任务
            taskRepository.save(task);

            // 4. 创建统计记录
            createTaskStats(task);

            // 5. 提交到引擎
            collectEngine.submit(task);

            return task.getId();
        } catch (Exception e) {
            log.error("Submit task failed", e);
            throw new TaskException("Submit task failed: " + e.getMessage());
        }
    }

    /**
     * 停止任务
     */
    @Transactional
    public void stopTask(String taskId) {
        try {
            // 1. 检查任务状态
            CollectTask task = getTask(taskId);
            if (task.getStatus().equals(TaskStatus.FINISHED.name()) ||
                    task.getStatus().equals(TaskStatus.CANCELED.name())) {
                throw new TaskException("Task already finished or canceled");
            }

            // 2. 更新任务状态
            task.setStatus(TaskStatus.CANCELED.name());
            task.setEndTime(LocalDateTime.now());
            taskRepository.save(task);

            // 3. 通知引擎停止
            collectEngine.stop(taskId);

            // 4. 更新统计
            updateTaskStats(task);

        } catch (Exception e) {
            log.error("Stop task failed", e);
            throw new TaskException("Stop task failed: " + e.getMessage());
        }
    }

    /**
     * 重试任务
     */
    @Transactional
    public void retryTask(String taskId) {
        try {
            // 1. 检查任务状态
            CollectTask task = getTask(taskId);
            if (!task.getStatus().equals(TaskStatus.FAILED.name())) {
                throw new TaskException("Only failed tasks can be retried");
            }

            // 2. 检查重试次数
            if (task.getRetryTimes() >= task.getMaxRetryTimes()) {
                throw new TaskException("Exceeded max retry times");
            }

            // 3. 更新任务状态
            task.setStatus(TaskStatus.WAITING.name());
            task.setRetryTimes(task.getRetryTimes() + 1);
            task.setStartTime(null);
            task.setEndTime(null);
            taskRepository.save(task);

            // 4. 重新提交到引擎
            collectEngine.submit(task);

        } catch (Exception e) {
            log.error("Retry task failed", e);
            throw new TaskException("Retry task failed: " + e.getMessage());
        }
    }

    private void validateTask(CollectTask task) {
        if (task.getMaxRetryTimes() == null) {
            task.setMaxRetryTimes(3);
        }
        if (task.getPriority() == null) {
            task.setPriority(0);
        }
        // 其他校验规则...
    }

    private void initTask(CollectTask task) {
        task.setId(UUID.randomUUID().toString());
        task.setStatus(TaskStatus.WAITING.name());
        task.setRetryTimes(0);
        task.setCreateTime(LocalDateTime.now());
    }

    private void createTaskStats(CollectTask task) {
        TaskStats stats = TaskStats.builder()
                .taskId(task.getId())
                .totalCount(0L)
                .successCount(0L)
                .failCount(0L)
                .startTime(LocalDateTime.now())
                .build();
        statsRepository.save(stats);
    }

    private void updateTaskStats(CollectTask task) {
        TaskStats stats = statsRepository.findByTaskId(task.getId())
                .orElseThrow(() -> new TaskException("Task stats not found"));

        stats.setEndTime(LocalDateTime.now());
        stats.setProcessSpeed(calculateProcessSpeed(stats));
        statsRepository.save(stats);
    }

    private double calculateProcessSpeed(TaskStats stats) {
        if (stats.getStartTime() == null || stats.getEndTime() == null) {
            return 0.0;
        }
        long seconds = Duration.between(stats.getStartTime(), stats.getEndTime()).getSeconds();
        return seconds == 0 ? 0.0 : (double) stats.getTotalCount() / seconds;
    }
}
