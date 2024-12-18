package com.study.collect.core.scheduler.base;

package com.study.collect.core.scheduler.base;

import com.study.collect.common.enums.collect.TaskStatus;
import com.study.collect.common.exception.collect.CollectException;
import com.study.collect.core.engine.CollectEngine;
import com.study.collect.domain.entity.task.CollectTask;
import com.study.collect.domain.repository.task.TaskRepository;
import com.study.collect.infrastructure.monitor.metrics.collector.MetricsCollector;
import com.study.collect.model.response.collect.CollectResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.*;

/**
 * 标准任务调度器
 */
@Component
@Slf4j
public class StandardTaskScheduler extends AbstractTaskScheduler {

    private final ScheduledExecutorService scheduleService = Executors.newScheduledThreadPool(1);
    private final BlockingQueue<CollectTask> taskQueue;
    private final ExecutorService executorService;
    private final MetricsCollector metricsCollector;

    public StandardTaskScheduler(
            TaskRepository taskRepository,
            CollectEngine collectEngine,
            MetricsCollector metricsCollector,
            @Value("${collect.scheduler.queue-capacity:1000}") int queueCapacity,
            @Value("${collect.scheduler.core-pool-size:10}") int corePoolSize,
            @Value("${collect.scheduler.max-pool-size:20}") int maxPoolSize
    ) {
        super(taskRepository, collectEngine);
        this.taskQueue = new LinkedBlockingQueue<>(queueCapacity);
        this.metricsCollector = metricsCollector;
        this.executorService = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(queueCapacity),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    @Override
    protected void startSchedule() {
        // 1. 定时检查队列任务
        scheduleService.scheduleAtFixedRate(
                this::processQueuedTasks,
                0,
                100,
                TimeUnit.MILLISECONDS
        );

        // 2. 定时检查超时任务
        scheduleService.scheduleAtFixedRate(
                this::checkTimeoutTasks,
                0,
                60,
                TimeUnit.SECONDS
        );
    }

    /**
     * 处理队列中的任务
     */
    private void processQueuedTasks() {
        try {
            CollectTask task = taskQueue.poll(100, TimeUnit.MILLISECONDS);
            if (task != null) {
                executeTask(task);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("Process queued tasks failed", e);
        }
    }

    /**
     * 执行具体任务
     */
    private void executeTask(CollectTask task) {
        executorService.submit(() -> {
            try {
                metricsCollector.incrementTaskCount();

                // 更新任务状态
                task.setStatus(TaskStatus.RUNNING.name());
                task.setStartTime(LocalDateTime.now());
                taskRepository.save(task);

                // 执行采集
                CollectResult result = collectEngine.collect(buildContext(task));

                // 处理结果
                handleResult(task, result);

            } catch (Exception e) {
                log.error("Execute task failed: {}", task.getId(), e);
                handleError(task, e);
            }
        });
    }

    /**
     * 处理任务结果
     */
    private void handleResult(CollectTask task, CollectResult result) {
        if (result.isSuccess()) {
            task.setStatus(TaskStatus.SUCCESS.name());
            metricsCollector.incrementSuccessCount();
        } else {
            handleError(task, new CollectException(result.getMessage()));
        }
        task.setEndTime(LocalDateTime.now());
        taskRepository.save(task);
    }

    /**
     * 处理执行错误
     */
    private void handleError(CollectTask task, Exception e) {
        // 检查重试
        if (task.getRetryTimes() < task.getMaxRetryTimes()) {
            task.setRetryTimes(task.getRetryTimes() + 1);
            task.setStatus(TaskStatus.WAITING.name());
            taskQueue.offer(task);
        } else {
            task.setStatus(TaskStatus.FAILED.name());
            task.setEndTime(LocalDateTime.now());
            taskRepository.save(task);
            metricsCollector.incrementFailureCount();
        }
    }

    @Override
    public void stop() {
        scheduleService.shutdown();
        executorService.shutdown();
        super.stop();
    }
}
