package com.study.collect.business.testcase.core.manager;

import com.study.collect.business.testcase.common.constants.CollectionConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

@Slf4j
@Component
public class QueueManager {
    private final ThreadPoolTaskExecutor taskExecutor;
    private final PriorityBlockingQueue<QueueItem<?>> taskQueue;
    private final ConcurrentHashMap<String, QueueItem<?>> taskMap;
    private final ScheduledExecutorService scheduledExecutor;
    private volatile boolean running = true;

    /**
     * 队列项状态枚举
     */
    public enum QueueItemStatus {
        QUEUED,         // 已入队
        PROCESSING,     // 处理中
        COMPLETED,      // 已完成
        CANCELLED,      // 已取消
        ERROR,          // 错误
        RETRY_WAIT     // 等待重试
    }

    /**
     * 队列项定义
     */
    private static class QueueItem<T> {
        final String taskId;
        final T task;
        volatile int priority;
        final CompletableFuture<Void> future;
        final Consumer<T> processor;
        final LocalDateTime createTime;
        volatile QueueItemStatus status;
        volatile String statusMessage;
        volatile double progress;
        volatile LocalDateTime startTime;
        volatile LocalDateTime endTime;
        volatile int retryCount;
        final Map<String, Object> attributes;

        QueueItem(String taskId, T task, int priority, Consumer<T> processor) {
            this.taskId = taskId;
            this.task = task;
            this.priority = priority;
            this.processor = processor;
            this.future = new CompletableFuture<>();
            this.createTime = LocalDateTime.now();
            this.status = QueueItemStatus.QUEUED;
            this.progress = 0.0;
            this.retryCount = 0;
            this.attributes = new ConcurrentHashMap<>();
        }

        boolean shouldRetry() {
            return retryCount < CollectionConstants.Http.MAX_RETRY;
        }
    }

    public QueueManager(@Qualifier("taskExecutor") ThreadPoolTaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
        this.taskQueue = new PriorityBlockingQueue<>(
                CollectionConstants.Process.TASK_QUEUE_CAPACITY,
                Comparator.<QueueItem<?>>comparingInt(item -> item.priority)
                        .reversed()
                        .thenComparing(item -> item.createTime)
        );
        this.taskMap = new ConcurrentHashMap<>();
        this.scheduledExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r);
            thread.setName("queue-monitor");
            thread.setDaemon(true);
            return thread;
        });

        startQueueProcessor();
        startQueueMonitor();
    }

    /**
     * 添加任务到队列
     */
    public <T> CompletableFuture<Void> enqueue(
            String taskId,
            T task,
            int priority,
            Consumer<T> processor
    ) {
        QueueItem<T> item = new QueueItem<>(taskId, task, priority, processor);
        if (taskMap.putIfAbsent(taskId, item) != null) {
            throw new IllegalStateException("Task " + taskId + " already exists");
        }
        taskQueue.offer(item);
        log.info("Task {} added to queue with priority {}", taskId, priority);
        return item.future;
    }

    /**
     * 更新任务优先级
     */
    public boolean updatePriority(String taskId, int newPriority) {
        QueueItem<?> item = taskMap.get(taskId);
        if (item != null && item.status == QueueItemStatus.QUEUED) {
            item.priority = newPriority;
            refreshQueue();
            log.info("Updated priority for task {} to {}", taskId, newPriority);
            return true;
        }
        return false;
    }

    /**
     * 取消任务
     */
    public boolean cancel(String taskId) {
        QueueItem<?> item = taskMap.get(taskId);
        if (item != null && (item.status == QueueItemStatus.QUEUED ||
                item.status == QueueItemStatus.RETRY_WAIT)) {
            if (taskQueue.remove(item)) {
                item.status = QueueItemStatus.CANCELLED;
                item.endTime = LocalDateTime.now();
                item.future.cancel(true);
                taskMap.remove(taskId);
                log.info("Task {} cancelled", taskId);
                return true;
            }
        }
        return false;
    }

    /**
     * 获取任务状态
     */
    public Map<String, Object> getTaskStatus(String taskId) {
        QueueItem<?> item = taskMap.get(taskId);
        if (item != null) {
            Map<String, Object> status = new HashMap<>();
            status.put("taskId", item.taskId);
            status.put("status", item.status);
            status.put("statusMessage", item.statusMessage);
            status.put("progress", item.progress);
            status.put("createTime", item.createTime);
            status.put("startTime", item.startTime);
            status.put("endTime", item.endTime);
            status.put("priority", item.priority);
            status.put("retryCount", item.retryCount);
            status.put("attributes", new HashMap<>(item.attributes));
            return status;
        }
        return null;
    }

    /**
     * 启动队列处理器
     */
    private void startQueueProcessor() {
        int processorCount = Runtime.getRuntime().availableProcessors();
        for (int i = 0; i < processorCount; i++) {
            taskExecutor.execute(new QueueProcessor());
        }
    }

    private class QueueProcessor implements Runnable {
        @Override
        public void run() {
            while (running) {
                try {
                    QueueItem<?> item = taskQueue.poll(1, TimeUnit.SECONDS);
                    if (item != null) {
                        processItem(item);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    log.error("Error in queue processor", e);
                }
            }
        }
    }

    private void processItem(QueueItem<?> item) {
        try {
            item.status = QueueItemStatus.PROCESSING;
            item.startTime = LocalDateTime.now();

            processTypedItem(item);

            item.status = QueueItemStatus.COMPLETED;
            item.progress = 100.0;
            item.endTime = LocalDateTime.now();
            item.future.complete(null);
        } catch (Exception e) {
            handleProcessingError(item, e);
        } finally {
            if (item.status != QueueItemStatus.RETRY_WAIT) {
                taskMap.remove(item.taskId);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T> void processTypedItem(QueueItem<T> item) {
        item.processor.accept(item.task);
    }

    private void handleProcessingError(QueueItem<?> item, Exception e) {
        log.error("Error processing task: {}", item.taskId, e);
        if (item.shouldRetry()) {
            scheduleRetry(item);
        } else {
            item.status = QueueItemStatus.ERROR;
            item.statusMessage = e.getMessage();
            item.endTime = LocalDateTime.now();
            item.future.completeExceptionally(e);
        }
    }

    private void scheduleRetry(QueueItem<?> item) {
        item.status = QueueItemStatus.RETRY_WAIT;
        item.retryCount++;
        long delay = CollectionConstants.Http.RETRY_INTERVAL * (1L << (item.retryCount - 1));
        scheduledExecutor.schedule(() -> {
            if (item.status == QueueItemStatus.RETRY_WAIT) {
                item.status = QueueItemStatus.QUEUED;
                taskQueue.offer(item);
            }
        }, delay, TimeUnit.MILLISECONDS);
    }

    /**
     * 启动队列监控
     */
    private void startQueueMonitor() {
        scheduledExecutor.scheduleAtFixedRate(() -> {
            try {
                monitorQueueHealth();
                cleanupCompletedTasks();
            } catch (Exception e) {
                log.error("Error in queue monitor", e);
            }
        }, 1, 1, TimeUnit.MINUTES);
    }

    private void monitorQueueHealth() {
        int queueSize = taskQueue.size();
        int activeTaskCount = (int) taskMap.values().stream()
                .filter(item -> item.status == QueueItemStatus.PROCESSING)
                .count();

        log.info("Queue status - Size: {}, Active tasks: {}", queueSize, activeTaskCount);

        LocalDateTime threshold = LocalDateTime.now().minusHours(1);
        taskMap.values().stream()
                .filter(item -> item.status == QueueItemStatus.QUEUED &&
                        item.createTime.isBefore(threshold))
                .forEach(item ->
                        log.warn("Task {} has been queued for more than 1 hour", item.taskId)
                );
    }

    private void cleanupCompletedTasks() {
        LocalDateTime threshold = LocalDateTime.now().minusHours(24);
        taskMap.entrySet().removeIf(entry -> {
            QueueItem<?> item = entry.getValue();
            return (item.status == QueueItemStatus.COMPLETED ||
                    item.status == QueueItemStatus.ERROR ||
                    item.status == QueueItemStatus.CANCELLED) &&
                    item.endTime != null &&
                    item.endTime.isBefore(threshold);
        });
    }

    private void refreshQueue() {
        List<QueueItem<?>> items = new ArrayList<>();
        taskQueue.drainTo(items);
        taskQueue.addAll(items);
    }

    /**
     * 获取队列统计信息
     */
    public Map<String, Object> getQueueStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("queueSize", taskQueue.size());
        stats.put("activeTaskCount", getActiveTaskCount());
        stats.put("totalTaskCount", taskMap.size());

        Map<QueueItemStatus, Long> statusCounts = new HashMap<>();
        taskMap.values().forEach(item ->
                statusCounts.merge(item.status, 1L, Long::sum)
        );
        stats.put("statusCounts", statusCounts);

        return stats;
    }

    /**
     * 获取队列大小
     */
    public int getQueueSize() {
        return taskQueue.size();
    }

    /**
     * 获取活动任务数
     */
    public int getActiveTaskCount() {
        return (int) taskMap.values().stream()
                .filter(item -> item.status == QueueItemStatus.PROCESSING)
                .count();
    }

    /**
     * 更新任务进度
     */
    public void updateTaskProgress(String taskId, double progress, String message) {
        QueueItem<?> item = taskMap.get(taskId);
        if (item != null) {
            item.progress = progress;
            item.statusMessage = message;
        }
    }

    /**
     * 暂停队列处理
     */
    public void pause() {
        running = false;
    }

    /**
     * 恢复队列处理
     */
    public void resume() {
        running = true;
        startQueueProcessor();
    }

    /**
     * 设置任务属性
     */
    public void setTaskAttribute(String taskId, String key, Object value) {
        QueueItem<?> item = taskMap.get(taskId);
        if (item != null) {
            item.attributes.put(key, value);
        }
    }

    @PreDestroy
    public void shutdown() {
        running = false;
        scheduledExecutor.shutdown();
        try {
            if (!scheduledExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduledExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduledExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        taskMap.values().forEach(item -> {
            if (item.status == QueueItemStatus.QUEUED ||
                    item.status == QueueItemStatus.PROCESSING ||
                    item.status == QueueItemStatus.RETRY_WAIT) {
                item.status = QueueItemStatus.CANCELLED;
                item.endTime = LocalDateTime.now();
                item.future.cancel(true);
            }
        });

        taskQueue.clear();
        taskMap.clear();
    }
}