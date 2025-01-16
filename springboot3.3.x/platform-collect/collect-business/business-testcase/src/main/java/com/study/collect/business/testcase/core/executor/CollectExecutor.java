package com.study.collect.business.testcase.core.executor;

import com.study.collect.business.testcase.common.constants.CollectionConstants;

import com.study.collect.business.testcase.common.utils.RateLimiter;
import com.study.collect.business.testcase.common.utils.StreamProcessor;
import com.study.collect.business.testcase.entity.UriEntity;
import com.study.collect.business.testcase.model.param.CollectParam;
import com.study.collect.business.testcase.model.param.PageParam;
import com.study.collect.business.testcase.model.response.PageResponse;
import com.study.collect.business.testcase.model.response.VersionResponse;
import com.study.collect.business.testcase.repository.UriRepository;
import com.study.collect.business.testcase.service.http.UriHttpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.ObjectPool;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * URI采集执行器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CollectExecutor {

    private final UriHttpService httpService;
    private final UriRepository repository;
    private final ObjectPool<UriEntity> entityPool;
    private final RateLimiter rateLimiter;

    @Qualifier("httpExecutor")
    private final ThreadPoolTaskExecutor httpExecutor;

    @Qualifier("mongoExecutor")
    private final ThreadPoolTaskExecutor mongoExecutor;

    @Qualifier("virtualThreadExecutor")
    private final ExecutorService virtualThreadExecutor;

    /**
     * 执行采集任务
     */
    public CompletableFuture<StreamProcessor.ProcessMetrics> execute(
            CollectParam param,
            Consumer<StreamProcessor.ProcessMetrics> progressCallback
    ) {
        // 1. 构建处理器配置
        StreamProcessor.ProcessorConfig<VersionResponse, List<UriEntity>> config =
                StreamProcessor.ProcessorConfig.<VersionResponse, List<UriEntity>>builder()
                        .processorName("URI-Collect-" + param.getRootNode())
                        .batchSize(param.getBatchSize())
                        .maxConcurrent(CollectionConstants.Process.MAX_CONCURRENT_TASKS)
                        .timeoutSeconds(param.getTimeout())
                        .maxRetries(param.getMaxRetries())
                        .retryDelayMs(CollectionConstants.Http.RETRY_INTERVAL)
                        .processExecutor(httpExecutor.getThreadPoolExecutor())
                        .saveExecutor(mongoExecutor.getThreadPoolExecutor())
                        .continueOnError(true)
                        // 获取版本数据
                        .dataFetcher(offset -> fetchVersions(param, offset))
                        // 处理每个版本的URI
                        .dataConverter(version -> processVersion(param, version))
                        // 保存处理结果
                        .dataSaver(entities -> saveEntities(param.getRootNode(), entities))
                        .progressCallback(progressCallback)
                        .build();

        // 2. 创建并启动处理器
        StreamProcessor<VersionResponse, List<UriEntity>> processor = new StreamProcessor<>(config);
        return processor.process(0, calculateTotalVersions(param));
    }

    /**
     * 获取版本列表
     */
    private List<VersionResponse> fetchVersions(CollectParam param, int offset) {
        try {
            rateLimiter.acquire();
            PageResponse<VersionResponse> response = httpService.getVersionsAsync(
                    param,
                    new PageParam(offset + 1, param.getBatchSize())
            ).get();
            return response.getItems();
        } catch (Exception e) {
            log.error("Error fetching versions for offset: {}", offset, e);
            throw new RuntimeException("Failed to fetch versions", e);
        }
    }

    /**
     * 处理单个版本的URI
     */
    private List<UriEntity> processVersion(CollectParam param, VersionResponse version) {
        try {
            // 一次性获取该版本所有URI
            List<String> allUris = httpService.getAllUrisForVersion(
                    param,
                    version.getVersion()
            );

            // 按批次处理URI详情
            return processUriDetails(param, version.getVersion(), allUris);
        } catch (Exception e) {
            log.error("Error processing version: {}", version.getVersion(), e);
            throw new RuntimeException("Failed to process version", e);
        }
    }

    /**
     * 处理URI详情
     */
    private List<UriEntity> processUriDetails(
            CollectParam param,
            String version,
            List<String> uris
    ) {
        List<UriEntity> results = new ArrayList<>();
        List<List<String>> batches = partition(uris, 200); // 每批200条处理

        for (List<String> batch : batches) {
            try {
                rateLimiter.acquire();
                List<Map<String, Object>> details = httpService.getUriDetailsAsync(
                        param,
                        batch
                ).get();

                // 转换为实体
                List<UriEntity> entities = convertToEntities(
                        param.getRootNode(),
                        version,
                        details
                );

                results.addAll(entities);
            } catch (Exception e) {
                log.error("Error processing URI batch", e);
                if (!param.getAllowDuplicate()) {
                    throw new RuntimeException("Failed to process URI batch", e);
                }
            }
        }

        return results;
    }

    /**
     * 转换为实体对象
     */
    private List<UriEntity> convertToEntities(
            String rootNode,
            String version,
            List<Map<String, Object>> details
    ) {
        List<UriEntity> entities = new ArrayList<>();
        for (Map<String, Object> detail : details) {
            UriEntity entity = null;
            try {
                entity = entityPool.borrowObject();
                fillEntity(entity, rootNode, version, detail);
                entities.add(entity);
            } catch (Exception e) {
                log.error("Error converting to entity", e);
                if (entity != null) {
                    try {
                        entityPool.returnObject(entity);
                    } catch (Exception ex) {
                        log.error("Error returning entity to pool", ex);
                    }
                }
            }
        }
        return entities;
    }

    /**
     * 填充实体信息
     */
    private void fillEntity(
            UriEntity entity,
            String rootNode,
            String version,
            Map<String, Object> detail
    ) {
        entity.setUri((String) detail.get("uri"));
        entity.setRootNode(rootNode);
        entity.setVersionType(getVersionType(version));
        entity.setUriVersion(version);
        entity.setDetails(detail);
    }

    /**
     * 批量保存实体
     */
    private void saveEntities(String rootNode, List<List<UriEntity>> batchEntities) {
        for (List<UriEntity> batch : batchEntities) {
            try {
                repository.batchUpsert(rootNode, batch);
            } finally {
                // 返还对象到对象池
                for (UriEntity entity : batch) {
                    try {
                        entityPool.returnObject(entity);
                    } catch (Exception e) {
                        log.error("Error returning entity to pool", e);
                    }
                }
            }
        }
    }

    /**
     * 获取版本类型
     */
    private String getVersionType(String version) {
        return version.toLowerCase().contains("branch") ? "BRANCH" : "TRUNK";
    }

    /**
     * 计算总版本数
     */
    private int calculateTotalVersions(CollectParam param) {
        try {
            PageResponse<VersionResponse> response = httpService.getVersionsAsync(
                    param,
                    new PageParam(1, 1)
            ).get();
            return response.getTotal().intValue();
        } catch (Exception e) {
            log.error("Error calculating total versions", e);
            return 0;
        }
    }

    /**
     * 分割列表
     */
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