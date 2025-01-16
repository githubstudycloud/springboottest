package com.study.collect.business.testcase.core.executor;

import com.study.collect.business.testcase.common.constants.CollectionConstants;
import com.study.collect.business.testcase.common.utils.StreamProcessor;
import com.study.collect.business.testcase.common.utils.TableNameHelper;

import com.study.collect.business.testcase.model.param.DeleteParam;
import com.study.collect.business.testcase.repository.UriRepository;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * URI删除执行器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteExecutor {

    private final UriRepository repository;

    @Qualifier("mongoExecutor")
    private final ThreadPoolTaskExecutor mongoExecutor;

    @Qualifier("virtualThreadExecutor")
    private final ExecutorService virtualThreadExecutor;

    /**
     * 删除配置
     */
    @Data
    @Builder
    public static class DeleteConfig {
        private String rootNode;
        private List<String> uris;
        private boolean hardDelete;
        private int batchSize;
        private int maxRetries;
        private long retryDelayMs;
        private boolean continueOnError;
        private Consumer<StreamProcessor.ProcessMetrics> progressCallback;
    }

    /**
     * 执行删除任务
     */
    public CompletableFuture<StreamProcessor.ProcessMetrics> execute(
            DeleteParam param,
            Consumer<StreamProcessor.ProcessMetrics> progressCallback
    ) {
        // 1. 解析并验证参数
        DeleteConfig config = buildConfig(param, progressCallback);

        // 2. 按rootNode分组URI
        Map<String, List<String>> groupedUris = groupUrisByRootNode(config);

        // 3. 构建处理器配置
        StreamProcessor.ProcessorConfig<Map.Entry<String, List<String>>, Long> processorConfig =
                StreamProcessor.ProcessorConfig.<Map.Entry<String, List<String>>, Long>builder()
                        .processorName("URI-Delete-" + config.getRootNode())
                        .batchSize(config.getBatchSize())
                        .maxConcurrent(CollectionConstants.Process.MAX_CONCURRENT_TASKS)
                        .timeoutSeconds(CollectionConstants.Process.TASK_TIMEOUT)
                        .maxRetries(config.getMaxRetries())
                        .retryDelayMs(config.getRetryDelayMs())
                        .continueOnError(config.isContinueOnError())
                        .processExecutor(virtualThreadExecutor)
                        .saveExecutor(mongoExecutor.getThreadPoolExecutor())
                        // 数据获取函数
                        .dataFetcher(offset -> fetchBatch(new ArrayList<>(groupedUris.entrySet()), offset))
                        // 数据处理函数
                        .dataConverter(entry -> processDelete(entry, config))
                        // 结果保存函数
                        .dataSaver(this::updateMetrics)
                        // 进度回调
                        .progressCallback(progressCallback)
                        .build();

        // 4. 创建并启动处理器
        StreamProcessor<Map.Entry<String, List<String>>, Long> processor =
                new StreamProcessor<>(processorConfig);

        return processor.process(0, groupedUris.size());
    }

    /**
     * 执行清理任务
     */
    public CompletableFuture<StreamProcessor.ProcessMetrics> executeCleanup(
            String rootNode,
            Set<String> validUriHashes,
            boolean hardDelete,
            Consumer<StreamProcessor.ProcessMetrics> progressCallback
    ) {
        StreamProcessor.ProcessorConfig<Set<String>, Long> config =
                StreamProcessor.ProcessorConfig.<Set<String>, Long>builder()
                        .processorName("URI-Cleanup-" + rootNode)
                        .batchSize(CollectionConstants.Process.DEFAULT_BATCH_SIZE)
                        .maxConcurrent(1) // 清理任务限制并发为1
                        .timeoutSeconds(CollectionConstants.Process.TASK_TIMEOUT)
                        .maxRetries(CollectionConstants.Http.MAX_RETRY)
                        .retryDelayMs(CollectionConstants.Http.RETRY_INTERVAL)
                        .processExecutor(virtualThreadExecutor)
                        .saveExecutor(mongoExecutor.getThreadPoolExecutor())
                        // 数据获取函数
                        .dataFetcher(offset -> Collections.singletonList(validUriHashes))
                        // 数据处理函数
                        .dataConverter(hashes -> processCleanup(rootNode, hashes, hardDelete))
                        // 结果保存函数
                        .dataSaver(this::updateMetrics)
                        // 进度回调
                        .progressCallback(progressCallback)
                        .build();

        // 创建并启动处理器
        StreamProcessor<Set<String>, Long> processor = new StreamProcessor<>(config);
        return processor.process(0, 1);
    }

    private DeleteConfig buildConfig(
            DeleteParam param,
            Consumer<StreamProcessor.ProcessMetrics> progressCallback
    ) {
        return DeleteConfig.builder()
                .rootNode(param.getRootNode())
                .uris(param.getUris())
                .hardDelete(param.getHardDelete())
                .batchSize(getBatchSize(param))
                .maxRetries(CollectionConstants.Http.MAX_RETRY)
                .retryDelayMs(CollectionConstants.Http.RETRY_INTERVAL)
                .continueOnError(true)
                .progressCallback(progressCallback)
                .build();
    }

    private Map<String, List<String>> groupUrisByRootNode(DeleteConfig config) {
        if (config.getRootNode() != null) {
            // 使用指定的rootNode
            return Collections.singletonMap(config.getRootNode(), config.getUris());
        } else {
            // 从URI中提取rootNode
            return config.getUris().stream()
                    .collect(Collectors.groupingBy(TableNameHelper::extractRootNode));
        }
    }

    private List<Map.Entry<String, List<String>>> fetchBatch(
            List<Map.Entry<String, List<String>>> entries,
            int offset
    ) {
        if (offset >= entries.size()) {
            return Collections.emptyList();
        }
        return Collections.singletonList(entries.get(offset));
    }

    private Long processDelete(
            Map.Entry<String, List<String>> entry,
            DeleteConfig config
    ) {
        String rootNode = entry.getKey();
        List<String> uris = entry.getValue();
        List<List<String>> batches = partition(uris, config.getBatchSize());
        long totalDeleted = 0;

        for (List<String> batch : batches) {
            try {
                long count = config.isHardDelete() ?
                        repository.batchHardDelete(rootNode, batch) :
                        repository.batchSoftDelete(rootNode, batch);
                totalDeleted += count;
            } catch (Exception e) {
                log.error("Error deleting batch for rootNode: {}", rootNode, e);
                if (!config.isContinueOnError()) {
                    throw new RuntimeException("Failed to delete batch", e);
                }
            }
        }

        return totalDeleted;
    }

    private Long processCleanup(
            String rootNode,
            Set<String> validHashes,
            boolean hardDelete
    ) {
        try {
            return hardDelete ?
                    repository.deleteNotInUriHashes(rootNode, validHashes) :
                    repository.softDeleteNotInUriHashes(rootNode, validHashes);
        } catch (Exception e) {
            log.error("Error during cleanup for rootNode: {}", rootNode, e);
            throw new RuntimeException("Cleanup failed", e);
        }
    }

    private void updateMetrics(List<Long> counts) {
        long total = counts.stream().mapToLong(Long::longValue).sum();
        log.debug("Processed batch with total count: {}", total);
    }

    private int getBatchSize(DeleteParam param) {
        if (param.getBatchSize() != null) {
            return Math.min(Math.max(param.getBatchSize(),
                            CollectionConstants.Process.MIN_BATCH_SIZE),
                    CollectionConstants.Process.MAX_BATCH_SIZE);
        }
        return CollectionConstants.Process.DEFAULT_BATCH_SIZE;
    }

    private <T> List<List<T>> partition(List<T> list, int size) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        List<List<T>> result = new ArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            result.add(list.subList(i, Math.min(i + size, list.size())));
        }
        return result;
    }
}