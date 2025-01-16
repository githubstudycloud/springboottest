package com.study.collect.business.testcase.service.impl;

import com.study.collect.business.testcase.common.constants.CollectionConstants;
import com.study.collect.business.testcase.common.utils.StreamProcessor;
import com.study.collect.business.testcase.common.utils.TableNameHelper;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class UriCleanupService {

    private final UriRepository repository;

    @Qualifier("mongoExecutor")
    private final ThreadPoolTaskExecutor mongoExecutor;

    @Qualifier("virtualThreadExecutor")
    private final ExecutorService virtualThreadExecutor;

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

    @Data
    @Builder
    public static class CleanupResult {
        private long processedCount;
        private long deletedCount;
        private long errorCount;
        private List<String> failedUris;
        private Map<String, Object> details;
    }

    public CompletableFuture<StreamProcessor.ProcessMetrics> cleanup(
            CleanupParams params,
            Consumer<StreamProcessor.ProcessMetrics> progressCallback
    ) {
        validateParams(params);

        // 创建 Set 用于存储有效的 URI hashes
        Set<String> validUriHashes = new HashSet<>();
        for (String uri : params.getUris()) {
            validUriHashes.add(TableNameHelper.generateUriHash(uri));
        }

        // 创建处理器配置，注意这里修改为使用 Set<String> 作为处理单元
        StreamProcessor.ProcessorConfig<Set<String>, Long> config =
                StreamProcessor.ProcessorConfig.<Set<String>, Long>builder()
                        .processorName("URI-Cleanup-" + params.getRootNode())
                        .batchSize(1) // 因为我们现在是处理整个 Set，所以批次大小为 1
                        .maxConcurrent(1)
                        .timeoutSeconds(params.getTimeout())
                        .maxRetries(CollectionConstants.Http.MAX_RETRY)
                        .retryDelayMs(CollectionConstants.Http.RETRY_INTERVAL)
                        .processExecutor(virtualThreadExecutor)
                        .saveExecutor(mongoExecutor.getThreadPoolExecutor())
                        // 修改数据获取函数，直接返回包含单个 Set 的列表
                        .dataFetcher(offset -> offset == 0 ?
                                Collections.singletonList(validUriHashes) :
                                Collections.emptyList())
                        .dataConverter(uriHashes -> processCleanup(params.getRootNode(), uriHashes,
                                params.isHardDelete()))
                        .dataSaver(this::updateMetrics)
                        .progressCallback(progressCallback)
                        .build();

        StreamProcessor<Set<String>, Long> processor = new StreamProcessor<>(config);
        return processor.process(0, 1); // 只需处理一次
    }

    public CompletableFuture<StreamProcessor.ProcessMetrics> batchDelete(
            DeleteParams params,
            Consumer<StreamProcessor.ProcessMetrics> progressCallback
    ) {
        validateParams(params);

        StreamProcessor.ProcessorConfig<List<String>, Long> config =
                StreamProcessor.ProcessorConfig.<List<String>, Long>builder()
                        .processorName("URI-Delete-" + params.getRootNode())
                        .batchSize(params.getBatchSize())
                        .maxConcurrent(CollectionConstants.Process.MAX_CONCURRENT_TASKS)
                        .timeoutSeconds(params.getTimeout())
                        .maxRetries(CollectionConstants.Http.MAX_RETRY)
                        .retryDelayMs(CollectionConstants.Http.RETRY_INTERVAL)
                        .processExecutor(virtualThreadExecutor)
                        .saveExecutor(mongoExecutor.getThreadPoolExecutor())
                        // 批量获取数据
                        .dataFetcher(offset -> fetchBatch(params.getUris(), offset, params.getBatchSize()))
                        .dataConverter(batch -> processDelete(params.getRootNode(), batch,
                                params.isHardDelete()))
                        .dataSaver(this::updateMetrics)
                        .progressCallback(progressCallback)
                        .build();

        StreamProcessor<List<String>, Long> processor = new StreamProcessor<>(config);
        return processor.process(0, params.getUris().size());
    }

    private List<List<String>> fetchBatch(List<String> allUris, int offset, int batchSize) {
        int endIndex = Math.min(offset + batchSize, allUris.size());
        if (offset < allUris.size()) {
            return Collections.singletonList(allUris.subList(offset, endIndex));
        }
        return Collections.emptyList();
    }

    private Long processCleanup(String rootNode, Set<String> validUriHashes, boolean hardDelete) {
        try {
            return (Long) (hardDelete ?
                                repository.deleteNotInUriHashes(rootNode, validUriHashes) :
                                repository.softDeleteNotInUriHashes(rootNode, validUriHashes));
        } catch (Exception e) {
            log.error("Error during cleanup for rootNode: {}", rootNode, e);
            throw new RuntimeException("Cleanup failed", e);
        }
    }

    private Long processDelete(String rootNode, List<String> uris, boolean hardDelete) {
        try {
            return (Long) (hardDelete ?
                                repository.batchHardDelete(rootNode, uris) :
                                repository.batchSoftDelete(rootNode, uris));
        } catch (Exception e) {
            log.error("Error during delete for rootNode: {}", rootNode, e);
            throw new RuntimeException("Delete failed", e);
        }
    }

    private void updateMetrics(List<Long> counts) {
        long total = counts.stream().mapToLong(Long::longValue).sum();
        log.debug("Processed batch with total count: {}", Optional.of(total));
    }

    private void validateParams(CleanupParams params) {
        Objects.requireNonNull(params.getRootNode(), "RootNode must not be null");
        Objects.requireNonNull(params.getUris(), "URIs list must not be null");

        if (params.getBatchSize() < CollectionConstants.Process.MIN_BATCH_SIZE ||
                params.getBatchSize() > CollectionConstants.Process.MAX_BATCH_SIZE) {
            throw new IllegalArgumentException("Invalid batch size: " + params.getBatchSize());
        }
    }

    private void validateParams(DeleteParams params) {
        Objects.requireNonNull(params.getRootNode(), "RootNode must not be null");
        Objects.requireNonNull(params.getUris(), "URIs list must not be null");

        if (params.getBatchSize() < CollectionConstants.Process.MIN_BATCH_SIZE ||
                params.getBatchSize() > CollectionConstants.Process.MAX_BATCH_SIZE) {
            throw new IllegalArgumentException("Invalid batch size: " + params.getBatchSize());
        }
    }
}