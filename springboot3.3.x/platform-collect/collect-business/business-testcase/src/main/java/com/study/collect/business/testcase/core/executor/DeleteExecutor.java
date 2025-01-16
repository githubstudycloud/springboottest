package com.study.collect.business.testcase.core.executor;

import com.study.collect.business.testcase.common.constants.CollectionConstants;
import com.study.collect.business.testcase.common.utils.StreamProcessor;
import com.study.collect.business.testcase.model.param.DeleteParam;
import com.study.collect.business.testcase.repository.UriRepository;
import com.study.collect.business.testcase.common.utils.ListCompareUtil;
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
        List<String> allUris = new ArrayList<>(param.getUris());

        // 2. 创建处理器配置
        StreamProcessor.ProcessorConfig<List<String>, Long> config =
                StreamProcessor.ProcessorConfig.<List<String>, Long>builder()
                        .processorName("URI-Delete-" + param.getRootNode())
                        .batchSize(getBatchSize(param))
                        .maxConcurrent(CollectionConstants.Process.MAX_CONCURRENT_TASKS)
                        .timeoutSeconds(CollectionConstants.Process.TASK_TIMEOUT)
                        .maxRetries(CollectionConstants.Http.MAX_RETRY)
                        .retryDelayMs(CollectionConstants.Http.RETRY_INTERVAL)
                        .processExecutor(virtualThreadExecutor)
                        .saveExecutor(mongoExecutor.getThreadPoolExecutor())
                        // 数据获取函数
                        .dataFetcher(offset -> fetchBatch(allUris, offset, param.getBatchSize()))
                        // 数据转换函数
                        .dataConverter(batch -> processDelete(param.getRootNode(), batch, param.getHardDelete()))
                        // 数据保存函数 - 这里用于更新删除计数
                        .dataSaver(this::updateDeleteCount)
                        // 进度回调
                        .progressCallback(progressCallback)
                        .build();

        // 3. 创建处理器实例
        StreamProcessor<List<String>, Long> processor = new StreamProcessor<>(config);

        // 4. 开始处理
        return processor.process(0, allUris.size());
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
                        .batchSize(CollectionConstants.Process.DEFAULT_BATCH_SIZE)
                        .maxConcurrent(1) // 清理任务限制并发为1
                        .timeoutSeconds(CollectionConstants.Process.TASK_TIMEOUT)
                        .maxRetries(CollectionConstants.Http.MAX_RETRY)
                        .retryDelayMs(CollectionConstants.Http.RETRY_INTERVAL)
                        .processExecutor(virtualThreadExecutor)
                        .saveExecutor(mongoExecutor.getThreadPoolExecutor())
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
                            CollectionConstants.Process.MIN_BATCH_SIZE),
                    CollectionConstants.Process.MAX_BATCH_SIZE);
        }
        return CollectionConstants.Process.DEFAULT_BATCH_SIZE;
    }

    /**
     * 获取一批待处理数据
     */
    private List<String> fetchBatch(List<String> allUris, int offset, int batchSize) {
        int endIndex = Math.min(offset + batchSize, allUris.size());
        return offset < allUris.size() ? allUris.subList(offset, endIndex) : Collections.emptyList();
    }

    /**
     * 处理删除操作
     */
    private Long processDelete(String rootNode, List<String> uris, boolean hardDelete) {
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
        long total = counts.stream().mapToLong(Long::longValue).sum();
        log.debug("Batch delete completed, total deleted: {}", total);
    }
}