package com.study.collect.business.testcase.manager;

import com.study.collect.business.testcase.constant.CollectionConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.Comparator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Slf4j
@Component
public class QueueManager<T> {
    private final PriorityBlockingQueue<QueueItem<T>> queue;
    private final ConcurrentHashMap<String, QueueItem<T>> itemMap;
    private final ThreadPoolTaskExecutor executor;
    private volatile boolean running = true;


    public QueueManager(ThreadPoolTaskExecutor taskExecutor) {
        this.queue = new PriorityBlockingQueue<>(
                CollectionConstants.TASK_QUEUE_CAPACITY,
                Comparator.comparing(QueueItem<T>::getPriority).reversed()
        );
        this.itemMap = new ConcurrentHashMap<>();
        this.executor = taskExecutor;

        // 启动队列处理线程
        startQueueProcessor();
    }

    /**
     * 添加任务到队列
     */
    public CompletableFuture<Void> enqueue(String id, T item, int priority, Consumer<T> processor) {
        QueueItem<T> queueItem = new QueueItem<>(id, item, priority, processor);
        if (itemMap.putIfAbsent(id, queueItem) != null) {
            throw new IllegalStateException("Item with id " + id + " already exists in queue");
        }
        queue.offer(queueItem);
        return queueItem.future;
    }

    /**
     * 更新任务优先级
     */
    public boolean updatePriority(String id, int newPriority) {
        QueueItem<T> item = itemMap.get(id);
        if (item != null) {
            // 创建新的队列项并重新入队
            QueueItem<T> newItem = new QueueItem<>(id, item.item, newPriority, item.processor);
            if (queue.remove(item)) {
                queue.offer(newItem);
                itemMap.put(id, newItem);
                // 传递future的结果
                item.future.whenComplete((v, e) -> {
                    if (e != null) {
                        newItem.future.completeExceptionally(e);
                    } else {
                        newItem.future.complete(null);
                    }
                });
                return true;
            }
        }
        return false;
    }

    /**
     * 取消任务
     */
    public boolean cancel(String id) {
        QueueItem<T> item = itemMap.remove(id);
        if (item != null) {
            queue.remove(item);
            item.future.cancel(true);
            return true;
        }
        return false;
    }

    /**
     * 获取队列大小
     */
    public int getQueueSize() {
        return queue.size();
    }

    private void startQueueProcessor() {
        executor.execute(() -> {
            while (running) {
                try {
                    QueueItem<T> item = queue.poll(1, TimeUnit.SECONDS);
                    if (item != null) {
                        processItem(item);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    log.error("Error processing queue item", e);
                }
            }
        });
    }

    private void processItem(QueueItem<T> item) {
        try {
            item.processor.accept(item.item);
            item.future.complete(null);
        } catch (Exception e) {
            item.future.completeExceptionally(e);
        } finally {
            itemMap.remove(item.id);
        }
    }

    @PreDestroy
    public void shutdown() {
        running = false;
        // 取消所有未完成的任务
        itemMap.values().forEach(item -> item.future.cancel(true));
        queue.clear();
        itemMap.clear();
    }

    /**
     * 获取任务状态
     */
    public boolean isQueued(String id) {
        return itemMap.containsKey(id);
    }

    private static class QueueItem<T> {
        final String id;
        final T item;
        final CompletableFuture<Void> future;
        final Consumer<T> processor;
        volatile int priority;

        QueueItem(String id, T item, int priority, Consumer<T> processor) {
            this.id = id;
            this.item = item;
            this.priority = priority;
            this.processor = processor;
            this.future = new CompletableFuture<>();
        }

        int getPriority() {
            return priority;
        }
    }
}