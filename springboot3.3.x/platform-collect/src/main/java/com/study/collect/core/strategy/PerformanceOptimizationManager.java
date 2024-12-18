package com.study.collect.core.strategy;

import com.google.common.cache.CacheStats;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 性能优化管理器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PerformanceOptimizationManager {

    private final ResourceMonitor resourceMonitor;
    private final ThreadPoolManager threadPoolManager;
    private final ConnectionPoolManager connectionPoolManager;
    private final CacheManager cacheManager;
    private final MetricsCollector metricsCollector;

    /**
     * 执行性能优化
     */
    public OptimizationResult optimize() {
        Timer.Sample timer = metricsCollector.startTimer("performance_optimization");

        try {
            // 1. 收集性能指标
            PerformanceMetrics metrics = collectPerformanceMetrics();

            // 2. 分析性能瓶颈
            List<BottleneckAnalysis> bottlenecks = analyzeBottlenecks(metrics);

            // 3. 生成优化建议
            List<OptimizationSuggestion> suggestions = generateSuggestions(bottlenecks);

            // 4. 应用优化
            return applyOptimizations(suggestions);

        } catch (Exception e) {
            log.error("Performance optimization failed", e);
            throw new OptimizationException("Optimization failed: " + e.getMessage());
        } finally {
            metricsCollector.stopTimer(timer);
        }
    }

    /**
     * 收集性能指标
     */
    private PerformanceMetrics collectPerformanceMetrics() {
        // 1. 收集CPU使用率
        double cpuUsage = resourceMonitor.getCpuUsage();

        // 2. 收集内存使用情况
        MemoryStats memoryStats = resourceMonitor.getMemoryStats();

        // 3. 收集线程池状态
        Map<String, ThreadPoolStats> threadPoolStats =
                threadPoolManager.getPoolStats();

        // 4. 收集连接池状态
        Map<String, ConnectionPoolStats> connectionPoolStats =
                connectionPoolManager.getPoolStats();

        // 5. 收集缓存统计
        CacheStats cacheStats = cacheManager.getCacheStats();

        return PerformanceMetrics.builder()
                .cpuUsage(cpuUsage)
                .memoryStats(memoryStats)
                .threadPoolStats(threadPoolStats)
                .connectionPoolStats(connectionPoolStats)
                .cacheStats(cacheStats)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 分析性能瓶颈
     */
    private List<BottleneckAnalysis> analyzeBottlenecks(PerformanceMetrics metrics) {
        List<BottleneckAnalysis> bottlenecks = new ArrayList<>();

        // 1. 分析CPU瓶颈
        if (metrics.getCpuUsage() > 80) {
            bottlenecks.add(analyzeCpuBottleneck(metrics));
        }

        // 2. 分析内存瓶颈
        if (metrics.getMemoryStats().getUsageRatio() > 0.85) {
            bottlenecks.add(analyzeMemoryBottleneck(metrics));
        }

        // 3. 分析线程池瓶颈
        bottlenecks.addAll(analyzeThreadPoolBottlenecks(metrics));

        // 4. 分析连接池瓶颈
        bottlenecks.addAll(analyzeConnectionPoolBottlenecks(metrics));

        // 5. 分析缓存瓶颈
        if (metrics.getCacheStats().getHitRate() < 0.6) {
            bottlenecks.add(analyzeCacheBottleneck(metrics));
        }

        return bottlenecks;
    }

    /**
     * 生成优化建议
     */
    private List<OptimizationSuggestion> generateSuggestions(
            List<BottleneckAnalysis> bottlenecks) {
        return bottlenecks.stream()
                .map(this::createOptimizationSuggestion)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 创建优化建议
     */
    private OptimizationSuggestion createOptimizationSuggestion(
            BottleneckAnalysis bottleneck) {
        switch (bottleneck.getType()) {
            case CPU:
                return createCpuOptimizationSuggestion(bottleneck);
            case MEMORY:
                return createMemoryOptimizationSuggestion(bottleneck);
            case THREAD_POOL:
                return createThreadPoolOptimizationSuggestion(bottleneck);
            case CONNECTION_POOL:
                return createConnectionPoolOptimizationSuggestion(bottleneck);
            case CACHE:
                return createCacheOptimizationSuggestion(bottleneck);
            default:
                return null;
        }
    }

    /**
     * 线程池优化
     */
    private OptimizationSuggestion createThreadPoolOptimizationSuggestion(
            BottleneckAnalysis bottleneck) {
        ThreadPoolStats stats = (ThreadPoolStats) bottleneck.getStats();

        // 1. 计算理想线程数
        int idealThreads = calculateIdealThreadCount(stats);

        // 2. 计算队列大小
        int idealQueueSize = calculateIdealQueueSize(stats);

        return OptimizationSuggestion.builder()
                .type(OptimizationType.THREAD_POOL)
                .target(stats.getPoolName())
                .changes(Map.of(
                        "corePoolSize", idealThreads,
                        "maxPoolSize", idealThreads * 2,
                        "queueSize", idealQueueSize
                ))
                .priority(calculatePriority(bottleneck))
                .description("Adjust thread pool configuration to optimize performance")
                .build();
    }

    /**
     * 计算理想线程数
     */
    private int calculateIdealThreadCount(ThreadPoolStats stats) {
        // 基于Little's Law计算
        double avgTaskArrivalRate = stats.getTaskArrivalRate();
        double avgProcessingTime = stats.getAvgProcessingTime();

        // N = λ * W
        int idealThreads = (int) Math.ceil(
                avgTaskArrivalRate * avgProcessingTime / 1000);

        // 考虑CPU核心数
        int availableCores = Runtime.getRuntime().availableProcessors();

        // 返回合理范围内的线程数
        return Math.min(Math.max(idealThreads, 1), availableCores * 2);
    }

    /**
     * 应用优化建议
     */
    private void applyThreadPoolOptimization(
            OptimizationSuggestion suggestion) {
        String poolName = suggestion.getTarget();
        Map<String, Object> changes = suggestion.getChanges();

        try {
            // 1. 获取线程池
            ThreadPoolExecutor executor =
                    threadPoolManager.getThreadPool(poolName);

            // 2. 应用新配置
            executor.setCorePoolSize(
                    (int) changes.get("corePoolSize"));
            executor.setMaximumPoolSize(
                    (int) changes.get("maxPoolSize"));

            // 3. 更新队列
            if (executor.getQueue() instanceof ResizableBlockingQueue) {
                ((ResizableBlockingQueue<?>) executor.getQueue())
                        .setCapacity((int) changes.get("queueSize"));
            }

            // 4. 记录变更
            logOptimizationChange(suggestion);

        } catch (Exception e) {
            log.error("Apply thread pool optimization failed: {}",
                    poolName, e);
            throw new OptimizationException(
                    "Failed to apply thread pool optimization", e);
        }
    }
}