package com.study.collect.core.strategy;

import io.micrometer.core.instrument.Timer;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 资源管理器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ResourceManager {

    private final ThreadPoolManager threadPoolManager;
    private final ConnectionPoolManager connectionPoolManager;
    private final MemoryManager memoryManager;
    private final ResourceMonitor resourceMonitor;
    private final MetricsCollector metricsCollector;

    /**
     * 初始化资源池
     */
    @PostConstruct
    public void init() {
        try {
            // 1. 初始化线程池
            initializeThreadPools();

            // 2. 初始化连接池
            initializeConnectionPools();

            // 3. 启动资源监控
            startResourceMonitoring();

        } catch (Exception e) {
            log.error("Resource manager initialization failed", e);
            throw new ResourceInitializationException(
                    "Failed to initialize resources", e);
        }
    }

    /**
     * 资源分配请求
     */
    public ResourceAllocationResult allocateResource(
            ResourceRequest request) {
        String requestId = UUID.randomUUID().toString();
        Timer.Sample timer = metricsCollector.startTimer("resource_allocation");

        try {
            // 1. 验证资源请求
            validateResourceRequest(request);

            // 2. 检查资源可用性
            if (!checkResourceAvailability(request)) {
                return ResourceAllocationResult.failed(
                        "Insufficient resources");
            }

            // 3. 分配资源
            Resource resource = doAllocateResource(request);

            // 4. 记录分配
            recordResourceAllocation(resource, request);

            return ResourceAllocationResult.success(resource);

        } catch (Exception e) {
            log.error("Resource allocation failed: {}",
                    requestId, e);
            return ResourceAllocationResult.error(e.getMessage());
        } finally {
            metricsCollector.stopTimer(timer);
        }
    }

    /**
     * 资源回收
     */
    public void releaseResource(Resource resource) {
        try {
            // 1. 验证资源状态
            if (!isResourceValid(resource)) {
                log.warn("Invalid resource for release: {}",
                        resource.getId());
                return;
            }

            // 2. 执行资源回收
            doReleaseResource(resource);

            // 3. 更新资源状态
            updateResourceStatus(resource, ResourceStatus.RELEASED);

            // 4. 记录回收
            recordResourceRelease(resource);

        } catch (Exception e) {
            log.error("Resource release failed: {}",
                    resource.getId(), e);
            throw new ResourceReleaseException(
                    "Failed to release resource", e);
        }
    }

    /**
     * 资源扩容
     */
    private void expandResources(ResourceType type, int amount) {
        log.info("Expanding resources: type={}, amount={}",
                type, amount);

        try {
            switch (type) {
                case THREAD:
                    expandThreadPool(amount);
                    break;
                case CONNECTION:
                    expandConnectionPool(amount);
                    break;
                case MEMORY:
                    expandMemoryPool(amount);
                    break;
                default:
                    throw new IllegalArgumentException(
                            "Unsupported resource type: " + type);
            }

            // 记录扩容事件
            recordResourceExpansion(type, amount);

        } catch (Exception e) {
            log.error("Resource expansion failed", e);
            throw new ResourceExpansionException(
                    "Failed to expand resources", e);
        }
    }

    /**
     * 资源收缩
     */
    private void shrinkResources(ResourceType type, int amount) {
        log.info("Shrinking resources: type={}, amount={}",
                type, amount);

        try {
            switch (type) {
                case THREAD:
                    shrinkThreadPool(amount);
                    break;
                case CONNECTION:
                    shrinkConnectionPool(amount);
                    break;
                case MEMORY:
                    shrinkMemoryPool(amount);
                    break;
                default:
                    throw new IllegalArgumentException(
                            "Unsupported resource type: " + type);
            }

            // 记录收缩事件
            recordResourceShrink(type, amount);

        } catch (Exception e) {
            log.error("Resource shrink failed", e);
            throw new ResourceShrinkException(
                    "Failed to shrink resources", e);
        }
    }

    /**
     * 资源调整
     */
    @Scheduled(fixedDelay = 60000) // 每分钟执行
    public void adjustResources() {
        try {
            // 1. 收集资源使用情况
            ResourceUsage usage = resourceMonitor.getResourceUsage();

            // 2. 分析资源需求
            ResourceDemand demand = analyzeResourceDemand(usage);

            // 3. 计算调整方案
            List<ResourceAdjustment> adjustments =
                    calculateAdjustments(demand);

            // 4. 执行调整
            executeAdjustments(adjustments);

        } catch (Exception e) {
            log.error("Resource adjustment failed", e);
        }
    }

    /**
     * 分析资源需求
     */
    private ResourceDemand analyzeResourceDemand(ResourceUsage usage) {
        return ResourceDemand.builder()
                .threadDemand(analyzeThreadDemand(usage))
                .connectionDemand(analyzeConnectionDemand(usage))
                .memoryDemand(analyzeMemoryDemand(usage))
                .build();
    }

    /**
     * 分析线程需求
     */
    private ResourceRequirement analyzeThreadDemand(ResourceUsage usage) {
        ThreadPoolStats stats = usage.getThreadPoolStats();

        // 计算线程池利用率
        double utilization = (double) stats.getActiveThreads() /
                stats.getTotalThreads();

        // 计算任务队列占用率
        double queueUtilization = (double) stats.getQueueSize() /
                stats.getQueueCapacity();

        // 根据利用率确定需求
        if (utilization > 0.8 || queueUtilization > 0.7) {
            return ResourceRequirement.builder()
                    .type(ResourceType.THREAD)
                    .action(ResourceAction.EXPAND)
                    .amount(calculateThreadExpansionAmount(stats))
                    .priority(calculatePriority(utilization, queueUtilization))
                    .build();
        } else if (utilization < 0.3 && queueUtilization < 0.2) {
            return ResourceRequirement.builder()
                    .type(ResourceType.THREAD)
                    .action(ResourceAction.SHRINK)
                    .amount(calculateThreadShrinkAmount(stats))
                    .priority(calculatePriority(utilization, queueUtilization))
                    .build();
        }

        return ResourceRequirement.none(ResourceType.THREAD);
    }
}