package com.study.scheduler.model.job;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.springframework.stereotype.Component;

/**
 * 任务执行指标统计类
 */
@Component
public class JobMetrics {

    private Counter jobCreationCounter = null;
    private Counter jobSuccessCounter = null;
    private Counter jobFailureCounter = null;

    public JobMetrics(MeterRegistry registry) {
        // 初始化计数器
        this.jobCreationCounter = Counter.builder("job.creation")
                .description("Number of jobs created")
                .register(registry);

        this.jobSuccessCounter = Counter.builder("job.execution.success")
                .description("Number of successful job executions")
                .register(registry);

        this.jobFailureCounter = Counter.builder("job.execution.failure")
                .description("Number of failed job executions")
                .register(registry);
    }

    // 默认构造函数
    public JobMetrics() {
        // 使用NoopCounter或空实现进行初始化
        this.jobCreationCounter = Counter.builder("noop").register(new SimpleMeterRegistry());
        this.jobSuccessCounter = Counter.builder("noop").register(new SimpleMeterRegistry());
        this.jobFailureCounter = Counter.builder("noop").register(new SimpleMeterRegistry());
    }

    public void incrementJobCreationCount() {
        if(jobCreationCounter != null) {
            jobCreationCounter.increment();
        }
    }

    public void incrementJobSuccessCount() {
        if(jobSuccessCounter != null) {
            jobSuccessCounter.increment();
        }
    }

    public void incrementJobFailureCount() {
        if(jobFailureCounter != null) {
            jobFailureCounter.increment();
        }
    }
}