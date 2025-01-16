package com.study.collect.business.testcase.core.manager;

import com.study.collect.business.testcase.common.constants.CollectionConstants;
import com.study.collect.business.testcase.model.response.TaskResponse;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 任务管理器
 */
@Slf4j
@Component
public class TaskManager {

    private final ConcurrentHashMap<String, TaskInfo> taskMap;
    private final ConcurrentHashMap<String, ScheduledFuture<?>> timeoutTasks;
    private final ScheduledExecutorService scheduledExecutor;
    private final Map<String, PriorityBlockingQueue<TaskInfo>> taskTypeQueues;
    private volatile boolean running = true;

    /**
     * 任务信息类
     */
    @Data
    private static class TaskInfo {
        private final String taskId;
        private final String type;
        private final Map<String, Object> params;
        private final int priority;
        private final LocalDateTime createTime;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private String status;
        private String message;
        private Double progress;
        private Long totalCount;
        private Long processedCount;
        private Long failedCount;
        private Map<String, Object> details;
        private CompletableFuture<Void> future;
        private Consumer<TaskInfo> progressCallback;
        private int retryCount;
        private LocalDateTime lastRetryTime;

        public TaskInfo(String taskId, String type, Map<String, Object> params, int priority) {
            this.taskId = taskId;
            this.type = type;
            this.params = params;
            this.priority = priority;
            this.createTime = LocalDateTime.now();
            this.status = "CREATED";
            this.progress = 0.0;
            this.details = new ConcurrentHashMap<>();
            this.retryCount = 0;
        }

        public void setPriority(int priority) {
            this.priority = priority;
        }
    }

    public TaskManager(ScheduledExecutorService scheduledExecutor) {
        this.taskMap = new ConcurrentHashMap<>();
        this.timeoutTasks = new ConcurrentHashMap<>();
        this.scheduledExecutor = scheduledExecutor;
        this.taskTypeQueues = new ConcurrentHashMap<>();

        // 启动定期清理和监控
        startPeriodicCleanup();
        startTaskMonitor();
    }

    /**
     * 创建新任务
     */
    public TaskResponse createTask(String type, Map<String, Object> params, Integer priority) {
        String taskId = generateTaskId();
        TaskInfo taskInfo = new TaskInfo(taskId, type, params, priority != null ? priority : 0);

        taskMap.put(taskId, taskInfo);
        taskTypeQueues.computeIfAbsent(type, k -> new PriorityBlockingQueue<>(
                100,
                Comparator.<TaskInfo>comparingInt(t -> t.priority).reversed()
                        .thenComparing(t -> t.createTime)
        )).offer(taskInfo);

        scheduleTimeout(taskId);

        return convertToResponse(taskInfo);
    }

    /**
     * 开始执行任务
     */
    public void startTask(String taskId, Consumer<TaskInfo> progressCallback) {
        TaskInfo task = taskMap.get(taskId);
        if (task != null && "CREATED".equals(task.getStatus())) {
            task.setProgressCallback(progressCallback);
            task.setStartTime(LocalDateTime.now());
            task.setStatus("PROCESSING");
            notifyProgress(task);
        }
    }

    /**
     * 更新任务状态
     */
    public void updateTaskStatus(String taskId, String status, String message) {
        TaskInfo task = taskMap.get(taskId);
        if (task != null) {
            task.setStatus(status);
            task.setMessage(message);

            if (isTerminalStatus(status)) {
                task.setEndTime(LocalDateTime.now());
                cancelTimeout(taskId);
            }

            notifyProgress(task);
        }
    }

    /**
     * 更新任务进度
     */
    public void updateTaskProgress(String taskId, long processed, long total) {
        TaskInfo task = taskMap.get(taskId);
        if (task != null) {
            task.setProcessedCount(processed);
            task.setTotalCount(total);
            task.setProgress(total > 0 ? (processed * 100.0) / total : 0.0);
            notifyProgress(task);
        }
    }

    /**
     * 添加任务详情
     */
    public void addTaskDetails(String taskId, Map<String, Object> details) {
        TaskInfo task = taskMap.get(taskId);
        if (task != null && task.getDetails() != null) {
            task.getDetails().putAll(details);
        }
    }

    /**
     * 获取任务状态
     */
    public TaskResponse getTaskStatus(String taskId) {
        TaskInfo task = taskMap.get(taskId);
        return task != null ? convertToResponse(task) : null;
    }

    /**
     * 获取所有活动任务
     */
    public List<TaskResponse> getActiveTasks() {
        return taskMap.values().stream()
                .filter(task -> !isTerminalStatus(task.getStatus()))
                .sorted(Comparator.comparing(TaskInfo::getPriority).reversed())
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 取消任务
     */
    public boolean cancelTask(String taskId) {
        TaskInfo task = taskMap.get(taskId);
        if (task != null && !"COMPLETED".equals(task.getStatus())) {
            task.setStatus("CANCELLED");
            task.setEndTime(LocalDateTime.now());
            cancelTimeout(taskId);

            if (task.getFuture() != null) {
                task.getFuture().cancel(true);
            }

            return true;
        }
        return false;
    }

    /**
     * 更新任务优先级
     */
    public boolean updateTaskPriority(String taskId, int priority) {
        TaskInfo task = taskMap.get(taskId);
        if (task != null && !isTerminalStatus(task.getStatus())) {
            // 从原队列中移除并重新入队
            PriorityBlockingQueue<TaskInfo> queue = taskTypeQueues.get(task.getType());
            if (queue != null && queue.remove(task)) {
                task.setPriority(priority);
                queue.offer(task);
                return true;
            }
        }
        return false;
    }

    /**
     * 重试任务
     */
    public boolean retryTask(String taskId) {
        TaskInfo task = taskMap.get(taskId);
        if (task != null && ("ERROR".equals(task.getStatus()) || "TIMEOUT".equals(task.getStatus()))) {
            if (task.getRetryCount() < CollectionConstants.Http.MAX_RETRY) {
                task.setRetryCount(task.getRetryCount() + 1);
                task.setStatus("CREATED");
                task.setLastRetryTime(LocalDateTime.now());
                task.setMessage("Retry attempt " + task.getRetryCount());

                // 重新入队
                taskTypeQueues.get(task.getType()).offer(task);
                return true;
            }
        }
        return false;
    }

    private void notifyProgress(TaskInfo task) {
        if (task.getProgressCallback() != null) {
            task.getProgressCallback().accept(task);
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
            TaskInfo task = taskMap.get(taskId);
            if (task != null && !isTerminalStatus(task.getStatus())) {
                task.setStatus("TIMEOUT");
                task.setEndTime(LocalDateTime.now());
                task.setMessage("Task timeout after " + CollectionConstants.Process.TASK_TIMEOUT + " seconds");
                notifyProgress(task);
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

    private void startTaskMonitor() {
        scheduledExecutor.scheduleAtFixedRate(
                this::monitorTasks,
                1, 1, TimeUnit.MINUTES
        );
    }

    private void cleanupTasks() {
        if (!running) return;

        try {
            LocalDateTime cutoff = LocalDateTime.now().minusDays(7);
            taskMap.entrySet().removeIf(entry -> {
                TaskInfo task = entry.getValue();
                return task.getEndTime() != null && task.getEndTime().isBefore(cutoff);
            });
        } catch (Exception e) {
            log.error("Error during task cleanup", e);
        }
    }

    private void monitorTasks() {
        if (!running) return;

        try {
            Map<String, Long> statusCounts = new HashMap<>();
            Map<String, List<String>> stuckTasks = new HashMap<>();

            LocalDateTime threshold = LocalDateTime.now().minusMinutes(30);

            for (TaskInfo task : taskMap.values()) {
                // 统计状态
                statusCounts.merge(task.getStatus(), 1L, Long::sum);

                // 检查卡住的任务
                if ("PROCESSING".equals(task.getStatus()) &&
                        task.getStartTime().isBefore(threshold)) {
                    stuckTasks.computeIfAbsent(task.getType(), k -> new ArrayList<>())
                            .add(task.getTaskId());
                }
            }

            // 记录监控信息
            log.info("Task status statistics: {}", statusCounts);
            if (!stuckTasks.isEmpty()) {
                log.warn("Stuck tasks detected: {}", stuckTasks);
            }

        } catch (Exception e) {
            log.error("Error during task monitoring", e);
        }
    }

    private TaskResponse convertToResponse(TaskInfo task) {
        return TaskResponse.builder()
                .taskId(task.getTaskId())
                .type(task.getType())
                .status(task.getStatus())
                .message(task.getMessage())
                .progress(task.getProgress())
                .priority(task.getPriority())
                .createTime(task.getCreateTime())
                .startTime(task.getStartTime())
                .endTime(task.getEndTime())
                .totalCount(task.getTotalCount())
                .processedCount(task.getProcessedCount())
                .failedCount(task.getFailedCount())
                .details(task.getDetails())
                .params(task.getParams())
                .build();
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
                    notifyProgress(task);
                });
    }

    /**
     * 获取任务统计信息
     */
    public Map<String, Object> getTaskStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // 统计各状态任务数量
        Map<String, Long> statusCounts = new HashMap<>();
        taskMap.values().forEach(task ->
                statusCounts.merge(task.getStatus(), 1L, Long::sum));

        // 统计各类型任务数量
        Map<String, Long> typeCounts = new HashMap<>();
        taskMap.values().forEach(task ->
                typeCounts.merge(task.getType(), 1L, Long::sum));

        stats.put("statusCounts", statusCounts);
        stats.put("typeCounts", typeCounts);
        stats.put("totalTasks", taskMap.size());
        stats.put("activeTasks", getActiveTasks().size());

        return stats;
    }
}