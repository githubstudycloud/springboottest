package com.study.collect.business.testcase.manager;

import com.study.collect.business.testcase.constant.CollectionConstants;
import com.study.collect.business.testcase.model.response.TaskResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CollectTaskManager {
    private final ConcurrentHashMap<String, TaskResponse> taskMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, ScheduledFuture<?>> timeoutTasks = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduledExecutor;

    public CollectTaskManager(ScheduledExecutorService scheduledExecutor) {
        this.scheduledExecutor = scheduledExecutor;
        // 启动定期清理任务
        this.scheduledExecutor.scheduleAtFixedRate(
                this::cleanupTasks,
                1,
                1,
                TimeUnit.HOURS
        );
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
            if ("COMPLETED".equals(status) || "ERROR".equals(status)) {
                task.setEndTime(LocalDateTime.now());
                cancelTimeout(taskId);
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
     * 调整任务优先级
     */
    public boolean updateTaskPriority(String taskId, int newPriority) {
        TaskResponse task = taskMap.get(taskId);
        if (task != null && "CREATED".equals(task.getStatus())) {
            task.setPriority(newPriority);
            return true;
        }
        return false;
    }

    /**
     * 获取所有活动任务
     */
    public List<TaskResponse> getActiveTasks() {
        return taskMap.values().stream()
                .filter(task -> !"COMPLETED".equals(task.getStatus())
                        && !"ERROR".equals(task.getStatus()))
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

    private String generateTaskId() {
        return UUID.randomUUID().toString();
    }

    private void scheduleTimeout(String taskId) {
        ScheduledFuture<?> timeoutTask = scheduledExecutor.schedule(() -> {
            TaskResponse task = taskMap.get(taskId);
            if (task != null && !"COMPLETED".equals(task.getStatus())
                    && !"ERROR".equals(task.getStatus())) {
                task.setStatus("TIMEOUT");
                task.setEndTime(LocalDateTime.now());
                task.setMessage("Task timeout after " + CollectionConstants.TASK_TIMEOUT + " seconds");
            }
        }, CollectionConstants.TASK_TIMEOUT, TimeUnit.SECONDS);

        timeoutTasks.put(taskId, timeoutTask);
    }

    private void cancelTimeout(String taskId) {
        ScheduledFuture<?> timeoutTask = timeoutTasks.remove(taskId);
        if (timeoutTask != null) {
            timeoutTask.cancel(false);
        }
    }

    private void cleanupTasks() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(7);
        taskMap.entrySet().removeIf(entry -> {
            TaskResponse task = entry.getValue();
            return task.getEndTime() != null && task.getEndTime().isBefore(cutoff);
        });
    }

    @PreDestroy
    public void shutdown() {
        timeoutTasks.values().forEach(task -> task.cancel(true));
        timeoutTasks.clear();
        taskMap.clear();
    }
}