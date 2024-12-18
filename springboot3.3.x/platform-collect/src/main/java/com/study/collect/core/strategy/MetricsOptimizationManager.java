package com.study.collect.core.strategy;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 监控指标优化管理器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MetricsOptimizationManager {

    private final MetricRegistry metricRegistry;
    private final MetricsPersistenceManager persistenceManager;
    private final MetricsAggregator aggregator;
    private final EventBus eventBus;

    // 本地指标缓冲区
    private final Buffer<MetricPoint> metricBuffer = new RingBuffer<>(10000);

    /**
     * 记录指标
     */
    public void recordMetric(String name, double value, Map<String, String> tags) {
        try {
            // 1. 创建指标点
            MetricPoint point = MetricPoint.builder()
                    .name(name)
                    .value(value)
                    .tags(tags)
                    .timestamp(System.currentTimeMillis())
                    .build();

            // 2. 写入缓冲区
            boolean success = metricBuffer.offer(point);
            if (!success) {
                handleBufferOverflow(point);
            }

            // 3. 检查是否需要刷新
            if (shouldFlush()) {
                flushMetrics();
            }

        } catch (Exception e) {
            log.error("Record metric failed: {}", name, e);
        }
    }

    /**
     * 刷新指标
     */
    @Scheduled(fixedDelay = 10000) // 10秒
    public void flushMetrics() {
        if (metricBuffer.isEmpty()) {
            return;
        }

        try {
            // 1. 获取缓冲区数据
            List<MetricPoint> points = drainBuffer();

            // 2. 预聚合
            List<MetricPoint> aggregated = preAggregate(points);

            // 3. 批量持久化
            persistMetrics(aggregated);

            // 4. 发布指标事件
            publishMetricsEvent(aggregated);

        } catch (Exception e) {
            log.error("Flush metrics failed", e);
        }
    }

    /**
     * 指标聚合
     */
    private List<MetricPoint> preAggregate(List<MetricPoint> points) {
        // 1. 按时间窗口分组
        Map<TimeWindow, List<MetricPoint>> windowedPoints =
                groupByTimeWindow(points);

        // 2. 聚合每个窗口的数据
        return windowedPoints.entrySet().stream()
                .map(entry -> aggregateWindow(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * 聚合时间窗口数据
     */
    private MetricPoint aggregateWindow(TimeWindow window,
                                        List<MetricPoint> points) {
        // 1. 计算基础统计量
        DoubleSummaryStatistics stats = points.stream()
                .mapToDouble(MetricPoint::getValue)
                .summaryStatistics();

        // 2. 创建聚合点
        return MetricPoint.builder()
                .name(points.get(0).getName())
                .timestamp(window.getStartTime())
                .value(stats.getAverage())
                .count(stats.getCount())
                .min(stats.getMin())
                .max(stats.getMax())
                .sum(stats.getSum())
                .build();
    }

    /**
     * 批量持久化指标
     */
    private void persistMetrics(List<MetricPoint> points) {
        // 1. 分片处理
        List<List<MetricPoint>> batches =
                Lists.partition(points, 1000);

        // 2. 并行持久化
        CompletableFuture<?>[] futures = batches.stream()
                .map(batch -> CompletableFuture.runAsync(() ->
                        persistenceManager.saveBatch(batch)))
                .toArray(CompletableFuture[]::new);

        // 3. 等待完成
        try {
            CompletableFuture.allOf(futures).get(30, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Persist metrics failed", e);
            throw new MetricsException("Persist metrics failed", e);
        }
    }

    /**
     * 指标数据压缩
     */
    @Scheduled(cron = "0 0 2 * * ?") // 每天凌晨2点执行
    public void compressMetrics() {
        log.info("Starting metrics compression");

        try {
            // 1. 获取需要压缩的数据
            List<TimeRange> ranges = getCompressionRanges();

            // 2. 并行压缩
            for (TimeRange range : ranges) {
                compressTimeRange(range);
            }

            // 3. 清理原始数据
            cleanupRawMetrics(ranges);

        } catch (Exception e) {
            log.error("Compress metrics failed", e);
        }
    }

    /**
     * 压缩时间范围数据
     */
    private void compressTimeRange(TimeRange range) {
        try {
            // 1. 加载原始数据
            List<MetricPoint> rawPoints =
                    persistenceManager.loadMetrics(range);

            // 2. 按指标名称分组
            Map<String, List<MetricPoint>> pointsByMetric =
                    groupByMetricName(rawPoints);

            // 3. 压缩每个指标的数据
            List<CompressedMetric> compressed = pointsByMetric.entrySet()
                    .stream()
                    .map(entry -> compressMetricPoints(
                            entry.getKey(),
                            entry.getValue(),
                            range))
                    .collect(Collectors.toList());

            // 4. 保存压缩数据
            persistenceManager.saveCompressedMetrics(compressed);

        } catch (Exception e) {
            log.error("Compress time range failed: {}", range, e);
            throw new MetricsException(
                    "Failed to compress time range: " + e.getMessage());
        }
    }

    /**
     * 压缩指标数据点
     */
    private CompressedMetric compressMetricPoints(
            String metricName,
            List<MetricPoint> points,
            TimeRange range) {

        // 1. 计算统计值
        DoubleSummaryStatistics stats = points.stream()
                .mapToDouble(MetricPoint::getValue)
                .summaryStatistics();

        // 2. 计算百分位数
        double[] percentiles = calculatePercentiles(points);

        // 3. 创建压缩指标
        return CompressedMetric.builder()
                .metricName(metricName)
                .timeRange(range)
                .count(stats.getCount())
                .sum(stats.getSum())
                .min(stats.getMin())
                .max(stats.getMax())
                .mean(stats.getAverage())
                .percentiles(percentiles)
                .build();
    }

    /**
     * 处理缓冲区溢出
     */
    private void handleBufferOverflow(MetricPoint point) {
        // 1. 记录溢出事件
        log.warn("Metric buffer overflow for: {}", point.getName());

        // 2. 发布告警事件
        eventBus.post(MetricBufferOverflowEvent.builder()
                .metricName(point.getName())
                .timestamp(System.currentTimeMillis())
                .build());

        // 3. 更新溢出计数器
        metricRegistry.counter("metric.buffer.overflow").inc();
    }
}