package com.study.collect.business.testcase.core.manager;

import com.study.collect.business.testcase.common.constants.CollectionConstants;
import com.study.collect.business.testcase.config.TestCaseCollectorProperties;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 队列管理器
 */
@Slf4j
@Component
public class QueueManager {

    private final Map<String, PriorityBlockingQueue<QueueItem<?>>> typeQueues;
    private final ConcurrentHashMap<String, QueueItem<?>> itemMap;
    private final ScheduledExecutorService scheduledExecutor;
    private final ThreadPoolTaskExecutor processorExecutor;
    private final MeterRegistry meterRegistry;
    private final int maxQueueSize;
    private volatile boolean running = true;
    private final AtomicInteger activeProcesses = new AtomicInteger(0);

    /**
     * 队列项状态
     */
    public enum ItemStatus {
        QUEUED,
        PROCESSING,
        COMPLETED,
        CANCELLED,
        ERROR,
        RETRY_WAIT
    }

    /**
     * 队列项信息
     */
    @Data
    @Builder
    private static class QueueItem<T> {
        private final String itemId;
        private final String type;
        private final T item;
        private volatile int priority;
        private final CompletableFuture<Void> future;
        private final Consumer<T> processor;
        private final LocalDateTime createTime;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private volatile ItemStatus status;
        private String statusMessage;
        private Double progress;
        private int retryCount;
        private LocalDateTime lastRetryTime;
        private Map<String, Object> attributes;
        private Long timeoutSeconds;
        private ScheduledFuture<?> timeoutFuture;
    }

    public QueueManager(
            ScheduledExecutorService scheduledExecutor,
            ThreadPoolTaskExecutor processorExecutor,
            MeterRegistry meterRegistry,
            TestCaseCollectorProperties properties
    ) {
        this.typeQueues = new ConcurrentHashMap<>();
        this.itemMap = new ConcurrentHashMap<>();
        this.scheduledExecutor = scheduledExecutor;
        this.processorExecutor = processorExecutor;
        this.meterRegistry = meterRegistry;
        this.maxQueueSize = properties.getTask().getQueueCapacity();

        // 启动监控和清理任务
        startMonitoring();
        startCleanupTask();
        registerMetrics();
    }

    /**
     * 入队
     */
    public <T> CompletableFuture<Void> enqueue(
            String type,
            T item,
            int priority,
            Consumer<T> processor,
            Long timeoutSeconds
    ) {
        validateQueueCapacity();
        String itemId = generateItemId();

        QueueItem<T> queueItem = QueueItem.<T>builder()
                .itemId(itemId)
                .type(type)
                .item(item)
                .priority(priority)
                .future(new CompletableFuture<>())
                .processor(processor)
                .createTime(LocalDateTime.now())
                .status(ItemStatus.QUEUED)
                .progress(0.0)
                .retryCount(0)
                .attributes(new ConcurrentHashMap<>())
                .timeoutSeconds(timeoutSeconds)
                .build();

        if (itemMap.putIfAbsent(itemId, queueItem) != null) {
            throw new IllegalStateException("Item " + itemId + " already exists");
        }

        getOrCreateQueue(type).offer(queueItem);
        scheduleTimeout(queueItem);

        // 启动处理
        processNextItem(type);

        return queueItem.getFuture();
    }

    /**
     * 取消任务
     */
    public boolean cancel(String itemId) {
        QueueItem<?> item = itemMap.get(itemId);
        if (item != null && canCancel(item.getStatus())) {
            PriorityBlockingQueue<QueueItem<?>> queue = typeQueues.get(item.getType());
            if (queue != null && queue.remove(item)) {
                item.setStatus(ItemStatus.CANCELLED);
                item.setEndTime(LocalDateTime.now());
                item.getFuture().cancel(true);
                cancelTimeout(item);
                itemMap.remove(itemId);
                return true;
            }
        }
        return false;
    }

    /**
     * 更新优先级
     */
    public boolean updatePriority(String itemId, int newPriority) {
        QueueItem<?> item = itemMap.get(itemId);
        if (item != null && item.getStatus() == ItemStatus.QUEUED) {
            PriorityBlockingQueue<QueueItem<?>> queue = typeQueues.get(item.getType());
            if (queue != null && queue.remove(item)) {
                item.setPriority(newPriority);
                queue.offer(item);
                return true;
            }
        }
        return false;
    }

    /**
     * 获取队列状态
     */
    public Map<String, Object> getQueueStatus(String itemId) {
        QueueItem<?> item = itemMap.get(itemId);
        if (item != null) {
            Map<String, Object> status = new HashMap<>();
            status.put("itemId", item.getItemId());
            status.put("type", item.getType());
            status.put("status", item.getStatus());
            status.put("statusMessage", item.getStatusMessage());
            status.put("progress", item.getProgress());
            status.put("createTime", item.getCreateTime());
            status.put("startTime", item.getStartTime());
            status.put("endTime", item.getEndTime());
            status.put("priority", item.getPriority());
            status.put("retryCount", item.getRetryCount());
            status.put("attributes", new HashMap<>(item.getAttributes()));
            return status;
        }
        return null;
    }

    private void processNextItem(String type) {
        PriorityBlockingQueue<QueueItem<?>> queue = typeQueues.get(type);
        if (queue == null || queue.isEmpty()) {
            return;
        }

        processorExecutor.execute(() -> {
            while (running && activeProcesses.get() < processorExecutor.getMaxPoolSize()) {
                QueueItem<?> item = queue.poll();
                if (item == null) break;

                if (item.getStatus() == ItemStatus.QUEUED) {
                    processItem(item);
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    private <T> void processItem(QueueItem<T> item) {
        if (!running) return;

        try {
            activeProcesses.incrementAndGet();
            item.setStatus(ItemStatus.PROCESSING);
            item.setStartTime(LocalDateTime.now());

            CompletableFuture.runAsync(() -> {
                try {
                    item.getProcessor().accept(item.getItem());
                    completeItem(item, true, null);
                } catch (Exception e) {
                    handleProcessingError(item, e);
                }
            }, processorExecutor).exceptionally(throwable -> {
                handleProcessingError(item, throwable);
                return null;
            });

        } finally {
            activeProcesses.decrementAndGet();
        }
    }

    private <T> void completeItem(QueueItem<T> item, boolean success, Throwable error) {
        if (success) {
            item.setStatus(ItemStatus.COMPLETED);
            item.getFuture().complete(null);
        } else {
            item.setStatus(ItemStatus.ERROR);
            item.getFuture().completeExceptionally(error);
        }

        item.setEndTime(LocalDateTime.now());
        cancelTimeout(item);

        // 处理下一个任务
        processNextItem(item.getType());
    }

    private <T> void handleProcessingError(QueueItem<T> item, Throwable error) {
        log.error("Error processing item: {}", item.getItemId(), error);

        if (canRetry(item)) {
            scheduleRetry(item);
        } else {
            completeItem(item, false, error);
        }
    }

    private <T> void scheduleRetry(QueueItem<T> item) {
        item.setStatus(ItemStatus.RETRY_WAIT);
        item.setRetryCount(item.getRetryCount() + 1);
        item.setLastRetryTime(LocalDateTime.now());

        long delay = calculateRetryDelay(item.getRetryCount());
        scheduledExecutor.schedule(() -> {
            if (item.getStatus() == ItemStatus.RETRY_WAIT) {
                item.setStatus(ItemStatus.QUEUED);
                getOrCreateQueue(item.getType()).offer(item);
                processNextItem(item.getType());
            }
        }, delay, TimeUnit.MILLISECONDS);
    }

    private <T> void scheduleTimeout(QueueItem<T> item) {
        if (item.getTimeoutSeconds() != null && item.getTimeoutSeconds() > 0) {
            item.setTimeoutFuture(scheduledExecutor.schedule(() -> {
                if (!isTerminalStatus(item.getStatus())) {
                    completeItem(item, false,
                            new TimeoutException("Item processing timed out after " +
                                    item.getTimeoutSeconds() + " seconds"));
                }
            }, item.getTimeoutSeconds(), TimeUnit.SECONDS));
        }
    }

    private <T> void cancelTimeout(QueueItem<T> item) {
        if (item.getTimeoutFuture() != null) {
            item.getTimeoutFuture().cancel(false);
        }
    }

    private PriorityBlockingQueue<QueueItem<?>> getOrCreateQueue(String type) {
        return typeQueues.computeIfAbsent(type, k -> new PriorityBlockingQueue<>(
                maxQueueSize,
                Comparator.<QueueItem<?>>comparingInt(i -> i.priority).reversed()
                        .thenComparing(i -> i.createTime)
        ));
    }

    private void startMonitoring() {
        scheduledExecutor.scheduleAtFixedRate(this::monitorQueues,
                1, 1, TimeUnit.MINUTES);
    }

    private void startCleanupTask() {
        scheduledExecutor.scheduleAtFixedRate(this::cleanup,
                1, 1, TimeUnit.HOURS);
    }

    private void registerMetrics() {
        meterRegistry.gauge("queue.total_items", itemMap, Map::size);
        meterRegistry.gauge("queue.active_processes", activeProcesses);
        typeQueues.forEach((type, queue) ->
                meterRegistry.gauge("queue.size." + type, queue, Queue::size));
    }

    private void monitorQueues() {
        if (!running) return;

        try {
            Map<String, Map<ItemStatus, Long>> statusCounts = new HashMap<>();
            Map<String, List<String>> stuckItems = new HashMap<>();

            LocalDateTime threshold = LocalDateTime.now().minusMinutes(30);

            itemMap.values().forEach(item -> {
                // 统计状态
                statusCounts.computeIfAbsent(item.getType(), k -> new HashMap<>())
                        .merge(item.getStatus(), 1L, Long::sum);

                // 检查卡住的项
                if (item.getStatus() == ItemStatus.PROCESSING &&
                        item.getStartTime().isBefore(threshold)) {
                    stuckItems.computeIfAbsent(item.getType(), k -> new ArrayList<>())
                            .add(item.getItemId());
                }
            });

            log.info("Queue status: {}", statusCounts);
            if (!stuckItems.isEmpty()) {
                log.warn("Stuck items detected: {}", stuckItems);
            }

        } catch (Exception e) {
            log.error("Error monitoring queues", e);
        }
    }

    private void cleanup() {
        if (!running) return;

        try {
            LocalDateTime cutoff = LocalDateTime.now().minusHours(24);
            itemMap.entrySet().removeIf(entry -> {
                QueueItem<?> item = entry.getValue();
                return isTerminalStatus(item.getStatus()) &&
                        item.getEndTime() != null &&
                        item.getEndTime().isBefore(cutoff);
            });
        } catch (Exception e) {
            log.error("Error during cleanup", e);
        }
    }

    private String generateItemId() {
        return UUID.randomUUID().toString();
    }

    private void validateQueueCapacity() {
        if (itemMap.size() >= maxQueueSize) {
            throw new IllegalStateException("Queue capacity exceeded");
        }
    }

    private boolean canCancel(ItemStatus status) {
        return status == ItemStatus.QUEUED || status == ItemStatus.RETRY_WAIT;
    }

    private boolean canRetry(QueueItem<?> item) {
        return item.getRetryCount() < CollectionConstants.Http.MAX_RETRY;
    }

    private boolean isTerminalStatus(ItemStatus status) {
        return status == ItemStatus.COMPLETED ||
                status == ItemStatus.CANCELLED ||
                status == ItemStatus.ERROR;
    }

    private long calculateRetryDelay(int retryCount) {
        return CollectionConstants.Http.RETRY_INTERVAL * (long)Math.pow(2, retryCount - 1);
    }

    @PreDestroy
    public void shutdown() {
        running = false;
        processorExecutor.shutdown();
        clearQueues();
    }

    private void clearQueues() {
        itemMap.values().forEach(item -> {
            if (!isTerminalStatus(item.getStatus())) {
                item.setStatus(ItemStatus.CANCELLED);
                item.setEndTime(LocalDateTime.now());
                item.getFuture().cancel(true);
                cancelTimeout(item);
            }
        });

        typeQueues.clear();
        itemMap.clear();
    }

    /**
     * 获取队列统计信息
     */
    public Map<String, Object> getQueueStats() {
        Map<String, Object> stats = new HashMap<>();

        // 队列大小统计
        Map<String, Integer> queueSizes = new HashMap<>();
        typeQueues.forEach((type, queue) ->
                queueSizes.put(type, queue.size()));

        // 状态统计
        Map<ItemStatus, Long> statusCounts = itemMap.values().stream()
                .collect(Collectors.groupingBy(
                        QueueItem::getStatus,
                        Collectors.counting()
                ));

        stats.put("queueSizes", queueSizes);
        stats.put("statusCounts", statusCounts);
        stats.put("totalItems", itemMap.size());
        stats.put("activeProcesses", activeProcesses.get());

        return stats;
    }
}