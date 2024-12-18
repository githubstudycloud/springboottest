package com.study.collect.core.impl;

// 异步实现
import com.study.collect.common.enums.collect.TaskStatus;
import com.study.collect.common.exception.collect.TaskException;
import com.study.collect.core.collector.factory.CollectorFactory;
import com.study.collect.core.engine.AbstractCollectEngine;
import com.study.collect.core.engine.CollectContext;
import com.study.collect.core.engine.CollectResult;
import com.study.collect.domain.entity.task.CollectTask;
import com.study.collect.domain.repository.task.TaskRepository;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 异步采集引擎实现
 */
@Slf4j
@Component("asyncCollectEngine")
@RequiredArgsConstructor
public class AsyncCollectEngine extends AbstractCollectEngine {

    private final ExecutorService executorService;
    private final TaskRepository taskRepository;
    private final CollectorFactory collectorFactory;
    private final MetricsCollector metricsCollector;

    @Override
    protected CollectResult doCollect(CollectContext context) {
        Timer.Sample sample = metricsCollector.startTimer();
        try {
            // 1. 异步执行采集
            CompletableFuture<CollectResult> future = CompletableFuture
                    .supplyAsync(() -> {
                        try {
                            // 获取采集器
                            Collector collector = collectorFactory.getCollector(context.getCollectType());

                            // 执行采集
                            Object data = collector.collect(context);

                            // 处理结果
                            return CollectResult.success(data);
                        } catch (Exception e) {
                            log.error("Collect failed", e);
                            return CollectResult.error(e.getMessage());
                        }
                    }, executorService)
                    .orTimeout(context.getTimeout(), TimeUnit.MILLISECONDS)
                    .exceptionally(e -> {
                        log.error("Collect failed with timeout", e);
                        return CollectResult.error("Collect timeout");
                    });

            // 2. 获取结果
            return future.get();

        } catch (Exception e) {
            log.error("Async collect failed", e);
            return CollectResult.error(e.getMessage());
        } finally {
            metricsCollector.stopTimer(sample);
        }
    }

    @Override
    public void stop(String taskId) {
        try {
            taskRepository.updateStatus(taskId, TaskStatus.CANCELED);
            log.info("Task stopped: {}", taskId);
        } catch (Exception e) {
            log.error("Failed to stop task", e);
            throw new TaskException("Failed to stop task: " + taskId);
        }
    }

    @Override
    public TaskStatus getStatus(String taskId) {
        try {
            Optional<CollectTask> task = taskRepository.findById(taskId);
            return task.map(t -> TaskStatus.valueOf(t.getStatus()))
                    .orElseThrow(() -> new TaskException("Task not found: " + taskId));
        } catch (Exception e) {
            log.error("Failed to get task status", e);
            throw new TaskException("Failed to get task status: " + taskId);
        }
    }
}