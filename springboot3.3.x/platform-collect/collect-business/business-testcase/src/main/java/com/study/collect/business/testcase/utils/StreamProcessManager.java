package com.study.collect.business.testcase.utils;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 通用流式处理管理器
 *
 * @param <T> 源数据类型
 * @param <R> 结果数据类型
 */
@Slf4j
public class StreamProcessManager<T, R> {
    private final ProcessConfig<T, R> config;
    private final ExecutorService processExecutor;
    private final ExecutorService saveExecutor;
    private final BlockingQueue<CompletableFuture<?>> processQueue;
    private final AtomicInteger activeProcesses;
    private final List<ProcessMetrics> metricsHistory;
    private volatile boolean running = true;

    public StreamProcessManager(ProcessConfig<T, R> config) {
        this.config = config;
        this.processExecutor = createExecutor(config.getProcessThreads(), config.isUseVirtualThreads());
        this.saveExecutor = createExecutor(config.getSaveThreads(), config.isUseVirtualThreads());
        this.processQueue = new ArrayBlockingQueue<>(1000);
        this.activeProcesses = new AtomicInteger(0);
        this.metricsHistory = new CopyOnWriteArrayList<>();

        validateConfig(config);
    }

    /**
     * 开始处理数据
     */
    public CompletableFuture<ProcessMetrics> processData(int offset, int limit) {
        ProcessMetrics metrics = initializeMetrics();
        CompletableFuture<ProcessMetrics> resultFuture = new CompletableFuture<>();

        try {
            activeProcesses.incrementAndGet();
            processDataBatches(offset, limit, metrics, resultFuture);
        } catch (Exception e) {
            activeProcesses.decrementAndGet();
            resultFuture.completeExceptionally(e);
        }

        return resultFuture;
    }

    private void processDataBatches(
            int offset,
            int limit,
            ProcessMetrics metrics,
            CompletableFuture<ProcessMetrics> resultFuture
    ) {
        CompletableFuture.runAsync(() -> {
            try {
                int processed = 0;
                while (running && processed < limit) {
                    // 获取一批数据
                    List<T> batch = config.getDataFetcher().apply(offset + processed);
                    if (batch.isEmpty()) {
                        break;
                    }

                    // 处理这批数据
                    processBatch(batch, metrics);
                    processed += batch.size();

                    // 更新进度
                    updateProgress(metrics, processed, limit);
                }

                // 完成处理
                completeProcessing(metrics, resultFuture);

            } catch (Exception e) {
                handleProcessingError(e, metrics, resultFuture);
            }
        }, processExecutor);
    }

    private void processBatch(List<T> batch, ProcessMetrics metrics) {
        int retryCount = 0;
        while (retryCount <= config.getMaxRetries()) {
            try {
                // 转换数据
                List<R> convertedBatch = batch.stream()
                        .map(config.getDataConverter()::apply)
                        .collect(java.util.stream.Collectors.toList());

                // 异步保存数据
                CompletableFuture<Void> saveFuture = CompletableFuture.runAsync(() -> {
                    config.getDataSaver().accept(convertedBatch);
                }, saveExecutor);

                // 添加到处理队列
                processQueue.put(saveFuture);

                // 检查和清理完成的任务
                cleanupCompletedTasks();
                break;

            } catch (Exception e) {
                if (++retryCount > config.getMaxRetries()) {
                    log.error("Failed to process batch after {} retries", config.getMaxRetries(), e);
                    metrics.setFailedItems(metrics.getFailedItems() + batch.size());
                    throw new RuntimeException("Batch processing failed", e);
                }
                try {
                    Thread.sleep(config.getRetryDelayMs() * (long) Math.pow(2, retryCount - 1));
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Processing interrupted", ie);
                }
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
                .processName(config.getProcessName())
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

    private void completeProcessing(
            ProcessMetrics metrics,
            CompletableFuture<ProcessMetrics> resultFuture
    ) {
        metrics.setEndTime(System.currentTimeMillis());
        metricsHistory.add(metrics);
        activeProcesses.decrementAndGet();
        resultFuture.complete(metrics);
    }

    private void handleProcessingError(
            Exception e,
            ProcessMetrics metrics,
            CompletableFuture<ProcessMetrics> resultFuture
    ) {
        log.error("Error processing data", e);
        metrics.setEndTime(System.currentTimeMillis());
        metricsHistory.add(metrics);
        activeProcesses.decrementAndGet();
        resultFuture.completeExceptionally(e);
    }

    private ExecutorService createExecutor(int threads, boolean useVirtualThreads) {
        if (useVirtualThreads) {
            return Executors.newVirtualThreadPerTaskExecutor();
        } else {
            return new ThreadPoolExecutor(
                    threads,
                    threads,
                    60L,
                    TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(1000),
                    new ThreadFactory() {
                        private final AtomicInteger count = new AtomicInteger(0);

                        @Override
                        public Thread newThread(Runnable r) {
                            Thread thread = new Thread(r);
                            thread.setName("stream-processor-" + count.incrementAndGet());
                            thread.setDaemon(true);
                            return thread;
                        }
                    },
                    new ThreadPoolExecutor.CallerRunsPolicy()
            );
        }
    }

    private void validateConfig(ProcessConfig<T, R> config) {
        if (config.getDataFetcher() == null) {
            throw new IllegalArgumentException("DataFetcher cannot be null");
        }
        if (config.getDataConverter() == null) {
            throw new IllegalArgumentException("DataConverter cannot be null");
        }
        if (config.getDataSaver() == null) {
            throw new IllegalArgumentException("DataSaver cannot be null");
        }
    }

    /**
     * 停止处理
     */
    public void shutdown() {
        running = false;
        processExecutor.shutdown();
        saveExecutor.shutdown();
        try {
            if (!processExecutor.awaitTermination(30, TimeUnit.SECONDS)) {
                processExecutor.shutdownNow();
            }
            if (!saveExecutor.awaitTermination(30, TimeUnit.SECONDS)) {
                saveExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            processExecutor.shutdownNow();
            saveExecutor.shutdownNow();
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
     * 处理配置
     */
    @Data
    @Builder
    public static class ProcessConfig<T, R> {
        // 基础配置
        private String processName;
        private int batchSize;
        private int processThreads;
        private int saveThreads;
        private boolean useVirtualThreads;

        // 数据处理函数
        private Function<Integer, List<T>> dataFetcher;      // 数据获取函数
        private Function<T, R> dataConverter;                // 数据转换函数
        private Consumer<List<R>> dataSaver;                 // 数据保存函数
        private Consumer<ProcessMetrics> progressCallback;    // 进度回调函数

        // 监控配置
        private long timeoutSeconds;
        private int maxRetries;
        private long retryDelayMs;
    }

    /**
     * 处理指标
     */
    @Data
    @Builder
    public static class ProcessMetrics {
        private String processName;
        private long totalItems;
        private long processedItems;
        private long failedItems;
        private long startTime;
        private long endTime;
        private double progressPercentage;
        private Map<String, Object> customMetrics;
    }
}