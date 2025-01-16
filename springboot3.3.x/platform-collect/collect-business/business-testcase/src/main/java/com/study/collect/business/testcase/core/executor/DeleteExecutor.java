package com.study.collect.business.testcase.core.executor;

import com.study.collect.business.testcase.model.param.DeleteParam;
import com.study.collect.business.testcase.repository.UriRepository;
import com.study.collect.business.testcase.common.utils.StreamProcessor;
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
     * 执行删除任务
     */
    public CompletableFuture<StreamProcessor.ProcessMetrics> execute(
            DeleteParam param,
            Consumer<StreamProcessor.ProcessMetrics> progressCallback
    ) {
        // 1. 预处理URI列表，按rootNode分组
        Map<String, List<String>> groupedUris = groupUrisByRootNode(param);

        // 2. 创建处理器配置
        StreamProcessor.ProcessorConfig<Map.Entry<String, List<String>>, Long> config =
                StreamProcessor.ProcessorConfig.<Map.Entry<String, List<String>>, Long>builder()
                        .processorName("URI-Delete-" + param.getRootNode())
                        .batchSize(getBatchSize(param))
                        .maxConcurrent(com.study.collect.business.testcase.common.constants.CollectionConstants.Process.MAX_CONCURRENT_TASKS)
                        .timeoutSeconds(com.study.collect.business.testcase.common.constants.CollectionConstants.Process.TASK_TIMEOUT)
                        .maxRetries(com.study.collect.business.testcase.common.constants.CollectionConstants.Http.MAX_RETRY)
                        .retryDelayMs(com.study.collect.business.testcase.common.constants.CollectionConstants.Http.RETRY_INTERVAL)
                        .processExecutor(virtualThreadExecutor)
                        .saveExecutor(mongoExecutor)
                        // 数据获取函数
                        .dataFetcher(offset -> fetchBatch(new ArrayList<>(groupedUris.entrySet()), offset))
                        // 数据转换函数
                        .dataConverter(entry -> processDelete(entry, param.getHardDelete()))
                        // 数据保存函数 - 这里用于更新删除计数
                        .dataSaver(this::updateDeleteCount)
                        // 进度回调
                        .progressCallback(progressCallback)
                        .build();

        // 3. 创建处理器实例
        StreamProcessor<Map.Entry<String, List<String>>, Long> processor = new StreamProcessor<>(config);

        // 4. 开始处理
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
        // 创建处理器配置
        StreamProcessor.ProcessorConfig<Set<String>, Long> config =
                StreamProcessor.ProcessorConfig.<Set<String>, Long>builder()
                        .processorName("URI-Cleanup-" + rootNode)
                        .batchSize(com.study.collect.business.testcase.common.constants.CollectionConstants.Process.DEFAULT_BATCH_SIZE)
                        .maxConcurrent(1) // 清理任务限制并发为1
                        .timeoutSeconds(com.study.collect.business.testcase.common.constants.CollectionConstants.Process.TASK_TIMEOUT)
                        .maxRetries(com.study.collect.business.testcase.common.constants.CollectionConstants.Http.MAX_RETRY)
                        .retryDelayMs(com.study.collect.business.testcase.common.constants.CollectionConstants.Http.RETRY_INTERVAL)
                        .processExecutor(virtualThreadExecutor)
                        .saveExecutor(mongoExecutor)
                        // 数据获取函数
                        .dataFetcher(offset -> Collections.singleton(validUriHashes))
                        // 数据转换函数
                        .dataConverter(hashes -> processCleanup(rootNode, hashes, hardDelete))
                        // 数据保存函数
                        .dataSaver(this::updateDeleteCount)
                        // 进度回调
                        .progressCallback(progressCallback)
                        .build();

        // 创建处理器实例并开始处理
        StreamProcessor<Set<String>, Long> processor = new StreamProcessor<>(config);
        return processor.process(0, 1);
    }

    /**
     * 按rootNode分组URI
     */
    private Map<String, List<String>> groupUrisByRootNode(DeleteParam param) {
        if (param.getRootNode() != null) {
            // 如果指定了rootNode，使用指定的
            return Collections.singletonMap(param.getRootNode(), param.getUris());
        } else {
            // 否则从URI中提取rootNode
            return param.getUris().stream()
                    .collect(Collectors.groupingBy(com.study.collect.business.testcase.common.utils.TableNameHelper::extractRootNode));
        }
    }

    /**
     * 获取批处理大小
     */
    private int getBatchSize(DeleteParam param) {
        if (param.getBatchSize() != null) {
            return Math.min(Math.max(param.getBatchSize(),
                            com.study.collect.business.testcase.common.constants.CollectionConstants.Process.MIN_BATCH_SIZE),
                    com.study.collect.business.testcase.common.constants.CollectionConstants.Process.MAX_BATCH_SIZE);
        }
        return com.study.collect.business.testcase.common.constants.CollectionConstants.Process.DEFAULT_BATCH_SIZE;
    }

    /**
     * 获取一批待处理数据
     */
    private List<Map.Entry<String, List<String>>> fetchBatch(
            List<Map.Entry<String, List<String>>> entries,
            int offset
    ) {
        int end = Math.min(offset + 1, entries.size());
        return offset < entries.size() ? entries.subList(offset, end) : Collections.emptyList();
    }

    /**
     * 处理删除操作
     */
    private Long processDelete(Map.Entry<String, List<String>> entry, boolean hardDelete) {
        String rootNode = entry.getKey();
        List<String> uris = entry.getValue();

        try {
            if (hardDelete) {
                return repository.batchHardDelete(rootNode, uris);
            } else {
                return repository.batchSoftDelete(rootNode, uris);
            }
        } catch (Exception e) {
            log.error("Error deleting URIs for rootNode: {}", rootNode, e);
            throw new RuntimeException("Failed to delete URIs", e);
        }
    }

    /**
     * 处理清理操作
     */
    private Long processCleanup(String rootNode, Set<String> validHashes, boolean hardDelete) {
        try {
            if (hardDelete) {
                return repository.deleteNotInUriHashes(rootNode, validHashes);
            } else {
                return repository.softDeleteNotInUriHashes(rootNode, validHashes);
            }
        } catch (Exception e) {
            log.error("Error cleaning up URIs for rootNode: {}", rootNode, e);
            throw new RuntimeException("Failed to cleanup URIs", e);
        }
    }

    /**
     * 更新删除计数（批处理后的回调）
     */
    private void updateDeleteCount(List<Long> counts) {
        // 可以在这里实现删除计数的统计逻辑
        long total = counts.stream().mapToLong(Long::longValue).sum();
        log.debug("Batch delete completed, total deleted: {}", total);
    }
}