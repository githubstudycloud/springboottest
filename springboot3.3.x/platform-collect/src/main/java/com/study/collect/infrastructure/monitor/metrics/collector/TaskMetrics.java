package com.study.collect.infrastructure.monitor.metrics.collector;

// 任务指标
import io.micrometer.core.instrument.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 任务指标收集器
 */
@Component
@RequiredArgsConstructor
public class TaskMetrics {

    private final MeterRegistry registry;

    // 任务计数器
    private final Counter totalTaskCounter;
    private final Counter successTaskCounter;
    private final Counter failedTaskCounter;

    // 任务执行时间
    private final Timer taskExecutionTimer;

    // 任务队列大小
    private final Gauge taskQueueSize;

    // 处理速率
    private final Gauge processRate;

    public TaskMetrics(MeterRegistry registry) {
        this.registry = registry;

        // 初始化计数器
        this.totalTaskCounter = Counter.builder("collect.task.total")
                .description("Total number of tasks")
                .register(registry);

        this.successTaskCounter = Counter.builder("collect.task.success")
                .description("Number of successful tasks")
                .register(registry);

        this.failedTaskCounter = Counter.builder("collect.task.failed")
                .description("Number of failed tasks")
                .register(registry);

        // 初始化定时器
        this.taskExecutionTimer = Timer.builder("collect.task.execution")
                .description("Task execution time")
                .register(registry);

        // 初始化仪表
        this.taskQueueSize = Gauge.builder("collect.task.queue.size",
                        taskQueue,
                        q -> q.size())
                .description("Current task queue size")
                .register(registry);

        this.processRate = Gauge.builder("collect.task.process.rate",
                        this::calculateProcessRate)
                .description("Task processing rate per minute")
                .register(registry);
    }

    /**
     * 记录任务执行
     */
    public void recordTaskExecution(long timeMs, boolean success) {
        totalTaskCounter.increment();
        taskExecutionTimer.record(timeMs, TimeUnit.MILLISECONDS);

        if (success) {
            successTaskCounter.increment();
        } else {
            failedTaskCounter.increment();
        }
    }

    /**
     * 计算处理速率
     */
    private double calculateProcessRate() {
        long total = totalTaskCounter.count();
        long timeWindow = 60_000; // 1分钟
        return total / (timeWindow / 1000.0);
    }

    /**
     * 获取成功率
     */
    public double getSuccessRate() {
        long total = totalTaskCounter.count();
        long success = successTaskCounter.count();
        return total == 0 ? 1.0 : (double) success / total;
    }
}
