package com.study.collect.business.testcase.core.executor;

import com.study.collect.business.testcase.common.constants.CollectionConstants;
import com.study.collect.business.testcase.common.utils.StreamProcessor;
import com.study.collect.business.testcase.entity.UriEntity;
import com.study.collect.business.testcase.model.param.CollectParam;
import com.study.collect.business.testcase.model.response.PageResponse;
import com.study.collect.business.testcase.repository.UriRepository;
import com.study.collect.business.testcase.service.http.UriHttpService;
import com.study.collect.business.testcase.utils.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.ObjectPool;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

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
        // 1. 创建处理器配置
        StreamProcessor.ProcessorConfig<String, UriEntity> config = StreamProcessor.ProcessorConfig.<String, UriEntity>builder()
                .processorName("URI-Collect-" + param.getRootNode())
                .batchSize(param.getBatchSize())
                .maxConcurrent(CollectionConstants.Process.MAX_CONCURRENT_TASKS)
                .timeoutSeconds(param.getTimeout())
                .maxRetries(param.getMaxRetries())
                .retryDelayMs(CollectionConstants.Http.RETRY_INTERVAL)
                .processExecutor(httpExecutor)
                .saveExecutor(mongoExecutor)
                // 数据获取函数
                .dataFetcher(offset -> fetchUris(param, offset))
                // 数据转换函数
                .dataConverter(uri -> convertToEntity(param, uri))
                // 数据保存函数
                .dataSaver(entities -> saveEntities(param.getRootNode(), entities))
                // 进度回调
                .progressCallback(progressCallback)
                .build();

        // 2. 创建处理器实例
        StreamProcessor<String, UriEntity> processor = new StreamProcessor<>(config);

        // 3. 开始处理
        return processor.process(0, Integer.MAX_VALUE);
    }

    /**
     * 获取URI列表
     */
    private List<String> fetchUris(CollectParam param, int offset) {
        try {
            rateLimiter.acquire(); // 限流控制
            PageResponse<String> response = httpService.getUriListAsync(
                    param,
                    param.getVersion(),
                    new com.study.collect.business.testcase.model.param.PageParam(
                            offset / param.getBatchSize() + 1,
                            param.getBatchSize()
                    )
            ).get();
            return response.getItems();
        } catch (Exception e) {
            log.error("Error fetching URIs", e);
            throw new RuntimeException("Failed to fetch URIs", e);
        }
    }

    /**
     * 转换为实体
     */
    private UriEntity convertToEntity(CollectParam param, String uri) {
        UriEntity entity = null;
        try {
            entity = entityPool.borrowObject();
            rateLimiter.acquire(); // 限流控制

            // 获取URI详情
            List<Map<String, Object>> details = httpService.getUriDetailsAsync(
                    param,
                    Collections.singletonList(uri)
            ).get();

            if (!details.isEmpty()) {
                Map<String, Object> detail = details.get(0);
                fillEntity(entity, param.getRootNode(), param.getVersion(), uri, detail);
            }

            return entity;
        } catch (Exception e) {
            log.error("Error converting URI to entity: {}", uri, e);
            if (entity != null) {
                try {
                    entityPool.returnObject(entity);
                } catch (Exception ex) {
                    log.error("Error returning entity to pool", ex);
                }
            }
            throw new RuntimeException("Failed to convert URI", e);
        }
    }

    /**
     * 保存实体列表
     */
    private void saveEntities(String rootNode, List<UriEntity> entities) {
        try {
            repository.batchUpsert(rootNode, entities);
        } finally {
            // 返还对象到对象池
            for (UriEntity entity : entities) {
                try {
                    entityPool.returnObject(entity);
                } catch (Exception e) {
                    log.error("Error returning entity to pool", e);
                }
            }
        }
    }

    /**
     * 填充实体信息
     */
    private void fillEntity(UriEntity entity, String rootNode, String version,
                            String uri, Map<String, Object> detail) {
        entity.setUri(uri);
        entity.setRootNode(rootNode);
        entity.setVersionType(getVersionType(version));
        entity.setUriVersion(version);
        entity.setDetails(detail);
    }

    private String getVersionType(String version) {
        return version.toLowerCase().contains("branch") ? "BRANCH" : "TRUNK";
    }
}