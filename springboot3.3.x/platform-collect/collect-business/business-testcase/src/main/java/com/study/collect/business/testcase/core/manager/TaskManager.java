package com.study.collect.business.testcase.core.manager;

import com.study.collect.business.testcase.common.constants.CollectionConstants;
import com.study.collect.business.testcase.model.response.TaskResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 任务管理器
 */
@Slf4j
@Component
public class TaskManager {

    private final ConcurrentHashMap<String, TaskResponse> taskMap;
    private final ConcurrentHashMap<String, ScheduledFuture<?>> timeoutTasks;
    private final ScheduledExecutorService scheduledExecutor;
    private volatile boolean running = true;

    public TaskManager(ScheduledExecutorService scheduledExecutor) {
        this.taskMap = new ConcurrentHashMap<>();
        this.timeoutTasks = new ConcurrentHashMap<>();
        this.scheduledExecutor = scheduledExecutor;

        // 启动定期清理
        startPeriodicCleanup();
    }

    /**
     * 创建新任务
     */
    public TaskResponse createTask(String type, Map<String, Object> params, Integer priority) {
        String taskId = generateTaskId();
        TaskResponse task = TaskResponse.builder()
                .taskId(taskId)
                .type(type)
                .status("CREATED")
                .progress(0.0)
                .priority(priority != null ? priority : 0)
                .createTime(LocalDateTime.now())
                .params(params)
                .build();

        taskMap.put(taskId, task);
        scheduleTimeout(taskId);

        return task;
    }

    /**
     * 更新任务状态
     */
    public void updateTaskStatus(String taskId, String status, String message) {
        TaskResponse task = taskMap.get(taskId);
        if (task != null) {
            task.setStatus(status);
            task.setMessage(message);

            if (isTerminalStatus(status)) {
                task.setEndTime(LocalDateTime.now());
                cancelTimeout(taskId);
            }

            if ("PROCESSING".equals(status) && task.getStartTime() == null) {
                task.setStartTime(LocalDateTime.now());
            }
        }
    }

    /**
     * 更新任务进度
     */
    public void updateTaskProgress(String taskId, long processed, long total) {
        TaskResponse task = taskMap.get(taskId);
        if (task != null) {
            task.updateProgress(processed, total);
        }
    }

    /**
     * 获取任务状态
     */
    public TaskResponse getTaskStatus(String taskId) {
        return taskMap.get(taskId);
    }

    /**
     * 获取所有活动任务
     */
    public List<TaskResponse> getActiveTasks() {
        return taskMap.values().stream()
                .filter(task -> !isTerminalStatus(task.getStatus()))
                .sorted(Comparator.comparing(TaskResponse::getPriority).reversed())
                .collect(Collectors.toList());
    }

    /**
     * 取消任务
     */
    public boolean cancelTask(String taskId) {
        TaskResponse task = taskMap.get(taskId);
        if (task != null && "CREATED".equals(task.getStatus())) {
            task.setStatus("CANCELLED");
            task.setEndTime(LocalDateTime.now());
            cancelTimeout(taskId);
            return true;
        }
        return false;
    }

    /**
     * 更新任务优先级
     */
    public boolean updateTaskPriority(String taskId, int newPriority) {
        TaskResponse task = taskMap.get(taskId);
        if (task != null && !isTerminalStatus(task.getStatus())) {
            task.setPriority(newPriority);
            return true;
        }
        return false;
    }

    /**
     * 添加任务详情
     */
    public void addTaskDetails(String taskId, Map<String, Object> details) {
        TaskResponse task = taskMap.get(taskId);
        if (task != null) {
            if (task.getDetails() == null) {
                task.setDetails(new ConcurrentHashMap<>());
            }
            task.getDetails().putAll(details);
        }
    }

    private String generateTaskId() {
        return UUID.randomUUID().toString();
    }

    private boolean isTerminalStatus(String status) {
        return "COMPLETED".equals(status) || "ERROR".equals(status)
                || "CANCELLED".equals(status) || "TIMEOUT".equals(status);
    }

    private void scheduleTimeout(String taskId) {
        ScheduledFuture<?> timeoutTask = scheduledExecutor.schedule(() -> {
            TaskResponse task = taskMap.get(taskId);
            if (task != null && !isTerminalStatus(task.getStatus())) {
                task.setStatus("TIMEOUT");
                task.setEndTime(LocalDateTime.now());
                task.setMessage("Task timeout after " +
                        CollectionConstants.Process.TASK_TIMEOUT + " seconds");
            }
        }, CollectionConstants.Process.TASK_TIMEOUT, TimeUnit.SECONDS);

        timeoutTasks.put(taskId, timeoutTask);
    }

    private void cancelTimeout(String taskId) {
        ScheduledFuture<?> timeoutTask = timeoutTasks.remove(taskId);
        if (timeoutTask != null) {
            timeoutTask.cancel(false);
        }
    }

    private void startPeriodicCleanup() {
        scheduledExecutor.scheduleAtFixedRate(
                this::cleanupTasks,
                1, 1, TimeUnit.HOURS
        );
    }

    private void cleanupTasks() {
        if (!running) {
            return;
        }

        try {
            LocalDateTime cutoff = LocalDateTime.now().minusDays(7);
            taskMap.entrySet().removeIf(entry -> {
                TaskResponse task = entry.getValue();
                return task.getEndTime() != null && task.getEndTime().isBefore(cutoff);
            });

            log.debug("Completed task cleanup, remaining tasks: {}", taskMap.size());
        } catch (Exception e) {
            log.error("Error during task cleanup", e);
        }
    }

    @PreDestroy
    public void shutdown() {
        running = false;
        // 取消所有超时任务
        timeoutTasks.values().forEach(task -> task.cancel(true));
        timeoutTasks.clear();

        // 标记所有未完成任务为已取消
        taskMap.values().stream()
                .filter(task -> !isTerminalStatus(task.getStatus()))
                .forEach(task -> {
                    task.setStatus("CANCELLED");
                    task.setEndTime(LocalDateTime.now());
                    task.setMessage("Task cancelled due to system shutdown");
                });
    }

    /**
     * 获取任务总数
     */
    public long getTotalTaskCount() {
        return taskMap.size();
    }

    /**
     * 获取活动任务数
     */
    public long getActiveTaskCount() {
        return taskMap.values().stream()
                .filter(task -> !isTerminalStatus(task.getStatus()))
                .count();
    }

    /**
     * 获取任务统计信息
     */
    public Map<String, Long> getTaskStatistics() {
        Map<String, Long> stats = new HashMap<>();
        taskMap.values().stream()
                .collect(Collectors.groupingBy(
                        TaskResponse::getStatus,
                        Collectors.counting()
                ))
                .forEach((status, count) -> stats.put("status." + status.toLowerCase(), count));

        stats.put("total", getTotalTaskCount());
        stats.put("active", getActiveTaskCount());

        return stats;
    }
}