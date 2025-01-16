package com.study.collect.business.testcase.service.impl;

import com.study.collect.business.testcase.common.constants.CollectionConstants;
import com.study.collect.business.testcase.common.utils.StreamProcessor;
import com.study.collect.business.testcase.repository.UriRepository;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

/**
 * URI清理服务
 * 提供URI数据的清理和批量删除功能
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UriCleanupService {

    private final UriRepository repository;

    @Qualifier("mongoExecutor")
    private final ThreadPoolTaskExecutor mongoExecutor;

    @Qualifier("virtualThreadExecutor")
    private final ExecutorService virtualThreadExecutor;

    /**
     * 清理参数
     */
    @Data
    @Builder
    public static class CleanupParams {
        private String rootNode;
        private List<String> uris;
        private boolean hardDelete;
        @Builder.Default
        private int batchSize = CollectionConstants.Process.DEFAULT_BATCH_SIZE;
        @Builder.Default
        private long timeout = CollectionConstants.Process.TASK_TIMEOUT;
    }

    /**
     * 删除参数
     */
    @Data
    @Builder
    public static class DeleteParams {
        private String rootNode;
        private List<String> uris;
        private boolean hardDelete;
        @Builder.Default
        private int batchSize = CollectionConstants.Process.DEFAULT_BATCH_SIZE;
        @Builder.Default
        private long timeout = CollectionConstants.Process.TASK_TIMEOUT;
    }

    /**
     * 清理操作结果
     */
    @Data
    @Builder
    public static class CleanupResult {
        private long processedCount;
        private long deletedCount;
        private long errorCount;
        private List<String> failedUris;
        private Map<String, Object> details;
    }

    /**
     * 执行清理操作
     * 清理不在指定URI列表中的数据
     */
    public CompletableFuture<StreamProcessor.ProcessMetrics> cleanup(
            CleanupParams params,
            Consumer<StreamProcessor.ProcessMetrics> progressCallback
    ) {
        validateParams(params);

        // 创建处理器配置
        StreamProcessor.ProcessorConfig<List<String>, Long> config =
                StreamProcessor.ProcessorConfig.<List<String>, Long>builder()
                        .processorName("URI-Cleanup-" + params.getRootNode())
                        .batchSize(params.getBatchSize())
                        .maxConcurrent(1)  // 清理任务限制并发为1
                        .timeoutSeconds(params.getTimeout())
                        .maxRetries(CollectionConstants.Http.MAX_RETRY)
                        .retryDelayMs(CollectionConstants.Http.RETRY_INTERVAL)
                        .processExecutor(virtualThreadExecutor)
                        .saveExecutor(mongoExecutor)
                        // 数据获取函数
                        .dataFetcher(offset -> fetchBatch(params.getUris(), offset, params.getBatchSize()))
                        // 数据转换函数
                        .dataConverter(batch -> processCleanup(params.getRootNode(), new HashSet<>(batch),
                                params.isHardDelete()))
                        // 数据保存函数
                        .dataSaver(this::updateMetrics)
                        // 进度回调
                        .progressCallback(progressCallback)
                        .build();

        // 创建处理器实例并开始处理
        StreamProcessor<List<String>, Long> processor = new StreamProcessor<>(config);
        return processor.process(0, params.getUris().size());
    }

    /**
     * 批量删除
     * 删除指定的URI列表
     */
    public CompletableFuture<StreamProcessor.ProcessMetrics> batchDelete(
            DeleteParams params,
            Consumer<StreamProcessor.ProcessMetrics> progressCallback
    ) {
        validateParams(params);

        // 创建处理器配置
        StreamProcessor.ProcessorConfig<List<String>, Long> config =
                StreamProcessor.ProcessorConfig.<List<String>, Long>builder()
                        .processorName("URI-Delete-" + params.getRootNode())
                        .batchSize(params.getBatchSize())
                        .maxConcurrent(CollectionConstants.Process.MAX_CONCURRENT_TASKS)
                        .timeoutSeconds(params.getTimeout())
                        .maxRetries(CollectionConstants.Http.MAX_RETRY)
                        .retryDelayMs(CollectionConstants.Http.RETRY_INTERVAL)
                        .processExecutor(virtualThreadExecutor)
                        .saveExecutor(mongoExecutor)
                        // 数据获取函数
                        .dataFetcher(offset -> fetchBatch(params.getUris(), offset, params.getBatchSize()))
                        // 数据转换函数
                        .dataConverter(batch -> processDelete(params.getRootNode(), batch,
                                params.isHardDelete()))
                        // 数据保存函数
                        .dataSaver(this::updateMetrics)
                        // 进度回调
                        .progressCallback(progressCallback)
                        .build();

        // 创建处理器实例并开始处理
        StreamProcessor<List<String>, Long> processor = new StreamProcessor<>(config);
        return processor.process(0, params.getUris().size());
    }

    /**
     * 获取一批数据
     */
    private List<String> fetchBatch(List<String> allUris, int offset, int batchSize) {
        int endIndex = Math.min(offset + batchSize, allUris.size());
        return offset < allUris.size() ?
                allUris.subList(offset, endIndex) :
                Collections.emptyList();
    }

    /**
     * 处理清理操作
     */
    private Long processCleanup(String rootNode, Set<String> validUris, boolean hardDelete) {
        try {
            return hardDelete ?
                    repository.deleteNotInUriHashes(rootNode, validUris) :
                    repository.softDeleteNotInUriHashes(rootNode, validUris);
        } catch (Exception e) {
            log.error("Error during cleanup for rootNode: {}", rootNode, e);
            throw new RuntimeException("Cleanup failed", e);
        }
    }

    /**
     * 处理删除操作
     */
    private Long processDelete(String rootNode, List<String> uris, boolean hardDelete) {
        try {
            return hardDelete ?
                    repository.batchHardDelete(rootNode, uris) :
                    repository.batchSoftDelete(rootNode, uris);
        } catch (Exception e) {
            log.error("Error during delete for rootNode: {}", rootNode, e);
            throw new RuntimeException("Delete failed", e);
        }
    }

    /**
     * 更新处理指标
     */
    private void updateMetrics(List<Long> counts) {
        // 可以实现具体的指标更新逻辑
        long total = counts.stream().mapToLong(Long::longValue).sum();
        log.debug("Processed batch with total count: {}", total);
    }

    /**
     * 验证清理参数
     */
    private void validateParams(CleanupParams params) {
        Objects.requireNonNull(params.getRootNode(), "RootNode must not be null");
        Objects.requireNonNull(params.getUris(), "URIs list must not be null");

        if (params.getBatchSize() < CollectionConstants.Process.MIN_BATCH_SIZE ||
                params.getBatchSize() > CollectionConstants.Process.MAX_BATCH_SIZE) {
            throw new IllegalArgumentException("Invalid batch size: " + params.getBatchSize());
        }
    }

    /**
     * 验证删除参数
     */
    private void validateParams(DeleteParams params) {
        Objects.requireNonNull(params.getRootNode(), "RootNode must not be null");
        Objects.requireNonNull(params.getUris(), "URIs list must not be null");

        if (params.getBatchSize() < CollectionConstants.Process.MIN_BATCH_SIZE ||
                params.getBatchSize() > CollectionConstants.Process.MAX_BATCH_SIZE) {
            throw new IllegalArgumentException("Invalid batch size: " + params.getBatchSize());
        }
    }

    /**
     * 统计清理结果
     */
    private CleanupResult buildResult(List<Long> results, List<String> failedUris) {
        long totalProcessed = results.stream().mapToLong(Long::longValue).sum();
        return CleanupResult.builder()
                .processedCount(totalProcessed)
                .deletedCount(totalProcessed)
                .errorCount(failedUris.size())
                .failedUris(failedUris)
                .details(new HashMap<>())
                .build();
    }
}