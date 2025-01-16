package com.study.collect.business.testcase.common.utils;

import com.study.collect.business.testcase.common.constants.CollectionConstants;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 通用流式处理器
 * @param <T> 源数据类型
 * @param <R> 结果数据类型
 */
@Slf4j
public class StreamProcessor<T, R> {

    @Data
    @Builder
    public static class ProcessorConfig<T, R> {
        // 基础配置
        private String processorName;
        private int batchSize;
        private int maxConcurrent;
        private long timeoutSeconds;
        private int maxRetries;
        private long retryDelayMs;
        private boolean continueOnError;

        // 线程池
        private ExecutorService processExecutor;
        private ExecutorService saveExecutor;

        // 处理函数
        private Function<Integer, List<T>> dataFetcher;  // 数据获取函数
        private Function<T, R> dataConverter;           // 数据转换函数
        private Consumer<List<R>> dataSaver;           // 数据保存函数
        private Consumer<ProcessMetrics> progressCallback; // 进度回调

        // 验证器
        private Function<T, Boolean> dataValidator;    // 数据验证函数
        private Function<R, Boolean> resultValidator;  // 结果验证函数
    }

    @Data
    @Builder
    public static class ProcessMetrics {
        private String processorName;
        private long totalItems;
        private long processedItems;
        private long failedItems;
        private long startTime;
        private Long endTime;
        private double progressPercentage;
        private Map<String, Object> customMetrics;
        private String currentStage;
        private String statusMessage;
    }

    private final ProcessorConfig<T, R> config;
    private final BlockingQueue<CompletableFuture<?>> processQueue;
    private final AtomicInteger activeProcesses;
    private final AtomicBoolean running;
    private final AtomicReference<ProcessMetrics> currentMetrics;
    private final List<ProcessMetrics> metricsHistory;

    public StreamProcessor(ProcessorConfig<T, R> config) {
        validateConfig(config);
        this.config = config;
        this.processQueue = new ArrayBlockingQueue<>(1000);
        this.activeProcesses = new AtomicInteger(0);
        this.running = new AtomicBoolean(true);
        this.currentMetrics = new AtomicReference<>(initializeMetrics());
        this.metricsHistory = new CopyOnWriteArrayList<>();
    }

    /**
     * 开始处理数据
     */
    public CompletableFuture<ProcessMetrics> process(int offset, int limit) {
        CompletableFuture<ProcessMetrics> resultFuture = new CompletableFuture<>();

        try {
            if (activeProcesses.incrementAndGet() <= config.getMaxConcurrent()) {
                processDataBatches(offset, limit, resultFuture);
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

    private void processDataBatches(
            int offset,
            int limit,
            CompletableFuture<ProcessMetrics> resultFuture
    ) {
        CompletableFuture.runAsync(() -> {
            try {
                int processed = 0;
                updateMetrics("FETCHING", "Starting data fetch", processed, limit);

                while (running.get() && processed < limit) {
                    // 获取一批数据
                    List<T> batch = fetchData(offset + processed);
                    if (batch.isEmpty()) {
                        break;
                    }

                    // 处理这批数据
                    processBatch(batch);
                    processed += batch.size();

                    // 更新进度
                    updateMetrics("PROCESSING",
                            String.format("Processed %d/%d items", processed, limit),
                            processed, limit);
                }

                // 完成处理
                completeProcessing(resultFuture);

            } catch (Exception e) {
                handleProcessingError(e, resultFuture);
            }
        }, config.getProcessExecutor());
    }

    private List<T> fetchData(int offset) {
        int retryCount = 0;
        while (retryCount <= config.getMaxRetries()) {
            try {
                List<T> data = config.getDataFetcher().apply(offset);

                // 验证数据
                if (config.getDataValidator() != null) {
                    data = validateData(data);
                }

                return data;
            } catch (Exception e) {
                if (++retryCount > config.getMaxRetries()) {
                    log.error("Failed to fetch data after {} retries", config.getMaxRetries(), e);
                    if (!config.isContinueOnError()) {
                        throw new RuntimeException("Data fetch failed", e);
                    }
                    return Collections.emptyList();
                }
                sleep(calculateRetryDelay(retryCount));
            }
        }
        return Collections.emptyList();
    }

    private void processBatch(List<T> batch) {
        List<R> convertedBatch = new ArrayList<>();

        // 转换数据
        for (T item : batch) {
            try {
                R converted = config.getDataConverter().apply(item);
                if (converted != null && (config.getResultValidator() == null ||
                        config.getResultValidator().apply(converted))) {
                    convertedBatch.add(converted);
                }
            } catch (Exception e) {
                handleItemError(item, e);
            }
        }

        if (!convertedBatch.isEmpty()) {
            saveBatch(convertedBatch);
        }
    }

    private void saveBatch(List<R> batch) {
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
                    if (!config.isContinueOnError()) {
                        throw new RuntimeException("Batch save failed", e);
                    }
                }
                sleep(calculateRetryDelay(retryCount));
            }
        }
    }

    private List<T> validateData(List<T> data) {
        return data.stream()
                .filter(item -> {
                    try {
                        return config.getDataValidator().apply(item);
                    } catch (Exception e) {
                        log.warn("Data validation failed for item: {}", item, e);
                        return false;
                    }
                })
                .toList();
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
                .customMetrics(new ConcurrentHashMap<>())
                .build();
    }

    private void updateMetrics(String stage, String message, long processed, long total) {
        ProcessMetrics metrics = currentMetrics.get();
        metrics.setCurrentStage(stage);
        metrics.setStatusMessage(message);
        metrics.setProcessedItems(processed);
        metrics.setTotalItems(total);
        metrics.setProgressPercentage(total > 0 ? (processed * 100.0) / total : 0.0);

        if (config.getProgressCallback() != null) {
            config.getProgressCallback().accept(metrics);
        }
    }

    private void handleItemError(T item, Exception e) {
        ProcessMetrics metrics = currentMetrics.get();
        metrics.setFailedItems(metrics.getFailedItems() + 1);
        log.error("Error processing item: {}", item, e);
    }

    private void completeProcessing(CompletableFuture<ProcessMetrics> resultFuture) {
        ProcessMetrics metrics = currentMetrics.get();
        metrics.setEndTime(System.currentTimeMillis());
        metricsHistory.add(metrics);
        activeProcesses.decrementAndGet();
        resultFuture.complete(metrics);
    }

    private void handleProcessingError(Exception e, CompletableFuture<ProcessMetrics> resultFuture) {
        ProcessMetrics metrics = currentMetrics.get();
        metrics.setEndTime(System.currentTimeMillis());
        metrics.setStatusMessage("Error: " + e.getMessage());
        metricsHistory.add(metrics);
        activeProcesses.decrementAndGet();
        resultFuture.completeExceptionally(e);
    }

    private long calculateRetryDelay(int retryCount) {
        return config.getRetryDelayMs() * (long)Math.pow(2, retryCount - 1);
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
        Objects.requireNonNull(config.getDataFetcher(), "DataFetcher cannot be null");
        Objects.requireNonNull(config.getDataConverter(), "DataConverter cannot be null");
        Objects.requireNonNull(config.getDataSaver(), "DataSaver cannot be null");
        Objects.requireNonNull(config.getProcessExecutor(), "ProcessExecutor cannot be null");
        Objects.requireNonNull(config.getSaveExecutor(), "SaveExecutor cannot be null");
    }

    // 公共方法
    public void pause() {
        running.set(false);
    }

    public void resume() {
        running.set(true);
    }

    public void shutdown() {
        running.set(false);
        processQueue.clear();
        activeProcesses.set(0);
    }

    public List<ProcessMetrics> getMetricsHistory() {
        return new ArrayList<>(metricsHistory);
    }

    public ProcessMetrics getCurrentMetrics() {
        return currentMetrics.get();
    }

    public boolean isRunning() {
        return running.get();
    }

    public int getActiveProcessCount() {
        return activeProcesses.get();
    }

    public int getQueueSize() {
        return processQueue.size();
    }
}