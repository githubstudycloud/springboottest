package com.study.collect.infrastructure.monitor.metrics.collector;

import io.micrometer.core.instrument.*;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.threads.TaskQueue;
import org.springframework.stereotype.Component;

/**
 * 基础监控指标收集器
 */
@Component
@RequiredArgsConstructor
public class MetricsCollector {

    private final MeterRegistry registry;

    // 任务计数器
    private final Counter taskCounter = Counter.builder("collect.task.total")
            .description("Total number of collect tasks")
            .register(registry);

    // 成功任务计数器
    private final Counter successCounter = Counter.builder("collect.task.success")
            .description("Number of successful collect tasks")
            .register(registry);

    // 失败任务计数器
    private final Counter failureCounter = Counter.builder("collect.task.failure")
            .description("Number of failed collect tasks")
            .register(registry);

    // 任务处理时间
    private final Timer processTimer = Timer.builder("collect.task.process.time")
            .description("Task processing time")
            .register(registry);

    // 任务队列大小
    private final Gauge queueSize;

    public MetricsCollector(MeterRegistry registry, TaskQueue taskQueue) {
        this.registry = registry;
        this.queueSize = Gauge.builder("collect.task.queue.size", taskQueue::size)
                .description("Current task queue size")
                .register(registry);
    }

    public void incrementTaskCount() {
        taskCounter.increment();
    }

    public void incrementSuccessCount() {
        successCounter.increment();
    }

    public void incrementFailureCount() {
        failureCounter.increment();
    }

    public Timer.Sample startTimer() {
        return Timer.start(registry);
    }

    public void stopTimer(Timer.Sample sample) {
        sample.stop(processTimer);
    }
}
