package com.study.collect.business.testcase.utils;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 通用流式处理器
 * @param <T> 输入数据类型
 * @param <R> 输出数据类型
 */
@Slf4j
public class StreamProcessor<T, R> {

    @Data
    @Builder
    public static class ProcessorConfig<T, R> {
        private String processorName;
        private int batchSize;
        private int maxConcurrent;
        private long timeoutSeconds;
        private int maxRetries;
        private long retryDelayMs;
        private ExecutorService processExecutor;
        private ExecutorService saveExecutor;

        // 处理函数
        private Function<Integer, List<T>> dataFetcher;
        private Function<T, R> dataConverter;
        private Consumer<List<R>> dataSaver;
        private Consumer<ProcessMetrics> progressCallback;
    }

    @Data
    @Builder
    public static class ProcessMetrics {
        private String processorName;
        private long totalItems;
        private long processedItems;
        private long failedItems;
        private long startTime;
        private long endTime;
        private double progressPercentage;
        private Map<String, Object> customMetrics;
    }

    private final ProcessorConfig<T, R> config;
    private final BlockingQueue<CompletableFuture<?>> processQueue;
    private final AtomicInteger activeProcesses;
    private final AtomicBoolean running;
    private final List<ProcessMetrics> metricsHistory;

    public StreamProcessor(ProcessorConfig<T, R> config) {
        validateConfig(config);
        this.config = config;
        this.processQueue = new ArrayBlockingQueue<>(1000);
        this.activeProcesses = new AtomicInteger(0);
        this.running = new AtomicBoolean(true);
        this.metricsHistory = new CopyOnWriteArrayList<>();
    }

    /**
     * 开始处理数据
     */
    public CompletableFuture<ProcessMetrics> process(int offset, int limit) {
        ProcessMetrics metrics = initializeMetrics();
        CompletableFuture<ProcessMetrics> resultFuture = new CompletableFuture<>();

        try {
            if (activeProcesses.incrementAndGet() <= config.getMaxConcurrent()) {
                processDataBatches(offset, limit, metrics, resultFuture);
            } else {
                activeProcesses.decrementAndGet();
                throw new RejectedExecutionException("Max concurrent processes reached");
            }
        } catch (Exception e) {
            activeProcesses.decrementAndGet();
            resultFuture.completeExceptionally(e);
        }

        return resultFuture;
    }

    private void processDataBatches(int offset, int limit, ProcessMetrics metrics,
                                    CompletableFuture<ProcessMetrics> resultFuture) {
        CompletableFuture.runAsync(() -> {
            try {
                int processed = 0;
                while (running.get() && processed < limit) {
                    List<T> batch = fetchData(offset + processed);
                    if (CollectionUtils.isEmpty(batch)) {
                        break;
                    }

                    processBatch(batch, metrics);
                    processed += batch.size();
                    updateProgress(metrics, processed, limit);
                }

                completeProcessing(metrics, resultFuture);
            } catch (Exception e) {
                handleProcessingError(e, metrics, resultFuture);
            }
        }, config.getProcessExecutor());
    }

    private List<T> fetchData(int offset) {
        int retryCount = 0;
        while (retryCount <= config.getMaxRetries()) {
            try {
                return config.getDataFetcher().apply(offset);
            } catch (Exception e) {
                if (++retryCount > config.getMaxRetries()) {
                    log.error("Failed to fetch data after {} retries", config.getMaxRetries(), e);
                    throw new RuntimeException("Data fetch failed", e);
                }
                sleep(calculateRetryDelay(retryCount));
            }
        }
        return new ArrayList<>();
    }

    private void processBatch(List<T> batch, ProcessMetrics metrics) {
        List<R> convertedBatch = new ArrayList<>();
        for (T item : batch) {
            try {
                R converted = config.getDataConverter().apply(item);
                if (converted != null) {
                    convertedBatch.add(converted);
                }
            } catch (Exception e) {
                log.error("Error converting item", e);
                metrics.setFailedItems(metrics.getFailedItems() + 1);
            }
        }

        if (!convertedBatch.isEmpty()) {
            saveBatch(convertedBatch, metrics);
        }
    }

    private void saveBatch(List<R> batch, ProcessMetrics metrics) {
        int retryCount = 0;
        while (retryCount <= config.getMaxRetries()) {
            try {
                CompletableFuture<Void> saveFuture = CompletableFuture.runAsync(() ->
                                config.getDataSaver().accept(batch)
                        , config.getSaveExecutor());

                processQueue.put(saveFuture);
                cleanupCompletedTasks();
                return;
            } catch (Exception e) {
                if (++retryCount > config.getMaxRetries()) {
                    log.error("Failed to save batch after {} retries", config.getMaxRetries(), e);
                    metrics.setFailedItems(metrics.getFailedItems() + batch.size());
                    throw new RuntimeException("Batch save failed", e);
                }
                sleep(calculateRetryDelay(retryCount));
            }
        }
    }

    private void cleanupCompletedTasks() {
        processQueue.removeIf(future -> {
            if (future.isDone()) {
                try {
                    future.get(0, TimeUnit.MILLISECONDS);
                    return true;
                } catch (Exception e) {
                    log.error("Task completed with error", e);
                    return true;
                }
            }
            return false;
        });
    }

    private ProcessMetrics initializeMetrics() {
        return ProcessMetrics.builder()
                .processorName(config.getProcessorName())
                .startTime(System.currentTimeMillis())
                .totalItems(0)
                .processedItems(0)
                .failedItems(0)
                .progressPercentage(0.0)
                .build();
    }

    private void updateProgress(ProcessMetrics metrics, long processed, long total) {
        metrics.setProcessedItems(processed);
        metrics.setTotalItems(total);
        metrics.setProgressPercentage((double) processed / total * 100);

        if (config.getProgressCallback() != null) {
            config.getProgressCallback().accept(metrics);
        }
    }

    private void completeProcessing(ProcessMetrics metrics, CompletableFuture<ProcessMetrics> resultFuture) {
        metrics.setEndTime(System.currentTimeMillis());
        metricsHistory.add(metrics);
        activeProcesses.decrementAndGet();
        resultFuture.complete(metrics);
    }

    private void handleProcessingError(Exception e, ProcessMetrics metrics,
                                       CompletableFuture<ProcessMetrics> resultFuture) {
        log.error("Error processing data", e);
        metrics.setEndTime(System.currentTimeMillis());
        metricsHistory.add(metrics);
        activeProcesses.decrementAndGet();
        resultFuture.completeExceptionally(e);
    }

    private long calculateRetryDelay(int retryCount) {
        return config.getRetryDelayMs() * (long) Math.pow(2, retryCount - 1);
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Processing interrupted", e);
        }
    }

    private void validateConfig(ProcessorConfig<T, R> config) {
        if (config.getDataFetcher() == null) {
            throw new IllegalArgumentException("DataFetcher cannot be null");
        }
        if (config.getDataConverter() == null) {
            throw new IllegalArgumentException("DataConverter cannot be null");
        }
        if (config.getDataSaver() == null) {
            throw new IllegalArgumentException("DataSaver cannot be null");
        }
        if (config.getProcessExecutor() == null) {
            throw new IllegalArgumentException("ProcessExecutor cannot be null");
        }
        if (config.getSaveExecutor() == null) {
            throw new IllegalArgumentException("SaveExecutor cannot be null");
        }
    }

    /**
     * 暂停处理
     */
    public void pause() {
        running.set(false);
    }

    /**
     * 恢复处理
     */
    public void resume() {
        running.set(true);
    }

    /**
     * 停止处理
     */
    public void shutdown() {
        running.set(false);
        config.getProcessExecutor().shutdown();
        config.getSaveExecutor().shutdown();
        try {
            if (!config.getProcessExecutor().awaitTermination(30, TimeUnit.SECONDS)) {
                config.getProcessExecutor().shutdownNow();
            }
            if (!config.getSaveExecutor().awaitTermination(30, TimeUnit.SECONDS)) {
                config.getSaveExecutor().shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            config.getProcessExecutor().shutdownNow();
            config.getSaveExecutor().shutdownNow();
        }
    }

    /**
     * 获取处理指标历史
     */
    public List<ProcessMetrics> getMetricsHistory() {
        return new ArrayList<>(metricsHistory);
    }

    /**
     * 获取当前活动处理数
     */
    public int getActiveProcessCount() {
        return activeProcesses.get();
    }

    /**
     * 获取处理队列大小
     */
    public int getQueueSize() {
        return processQueue.size();
    }

    /**
     * 是否正在运行
     */
    public boolean isRunning() {
        return running.get();
    }

    /**
     * 清除历史指标
     */
    public void clearMetricsHistory() {
        metricsHistory.clear();
    }
}