package com.study.collect.core.scheduler.base;

import com.study.collect.common.enums.collect.CollectType;
import com.study.collect.common.enums.collect.TaskStatus;
import com.study.collect.core.engine.CollectContext;
import com.study.collect.core.engine.CollectEngine;
import com.study.collect.domain.entity.task.CollectTask;
import com.study.collect.domain.repository.task.TaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 标准任务调度器
 */
@Component
@Slf4j
public class StandardTaskScheduler extends AbstractTaskScheduler {

    private final ScheduledExecutorService scheduleService = Executors.newSingleThreadScheduledExecutor();

    public StandardTaskScheduler(
            TaskRepository taskRepository,
            CollectEngine collectEngine,
            MetricsCollector metricsCollector,
            @Value("${collect.scheduler.thread-pool}") ThreadPoolProperties properties
    ) {
        super(taskRepository, collectEngine, metricsCollector, properties);
    }

    @Override
    protected void startSchedule() {
        // 定时检查队列中的任务
        scheduleService.scheduleAtFixedRate(() -> {
            try {
                if (!running) {
                    return;
                }

                CollectTask task = taskQueue.poll();
                if (task == null) {
                    return;
                }

                executeTask(task);
            } catch (Exception e) {
                log.error("Schedule task failed", e);
            }
        }, 0, 100, TimeUnit.MILLISECONDS);

        // 定时检查超时任务
        scheduleService.scheduleAtFixedRate(() -> {
            try {
                if (!running) {
                    return;
                }

                checkTimeoutTasks();
            } catch (Exception e) {
                log.error("Check timeout tasks failed", e);
            }
        }, 0, 60, TimeUnit.SECONDS);
    }

    private void executeTask(CollectTask task) {
        executorService.submit(() -> {
            try {
                // 更新任务状态
                task.setStatus(TaskStatus.RUNNING.name());
                taskRepository.save(task);

                // 构建上下文
                CollectContext context = buildContext(task);

                // 执行采集
                CollectResult result = collectEngine.collect(context);

                // 处理结果
                handleResult(task, result);
            } catch (Exception e) {
                log.error("Execute task failed: {}", task.getId(), e);
                handleError(task, e);
            }
        });
    }

    private CollectContext buildContext(CollectTask task) {
        return CollectContext.builder()
                .taskId(task.getId())
                .collectType(CollectType.valueOf(task.getType()))
                .params(task.getParams())
                .timeout(task.getTimeout())
                .build();
    }

    private void handleResult(CollectTask task, CollectResult result) {
        if (result.isSuccess()) {
            task.setStatus(TaskStatus.SUCCESS.name());
            metricsCollector.incrementSuccessCount();
        } else {
            if (task.getRetryTimes() < task.getMaxRetryTimes()) {
                // 重试任务
                task.setRetryTimes(task.getRetryTimes() + 1);
                task.setStatus(TaskStatus.WAITING.name());
                taskQueue.offer(task);
            } else {
                task.setStatus(TaskStatus.FAILED.name());
                metricsCollector.incrementFailureCount();
            }
        }
        task.setEndTime(LocalDateTime.now());
        taskRepository.save(task);
    }

    private void handleError(CollectTask task, Exception e) {
        if (task.getRetryTimes() < task.getMaxRetryTimes()) {
            // 重试任务
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

    private void checkTimeoutTasks() {
        LocalDateTime timeout = LocalDateTime.now().minusMinutes(30); // 30分钟超时
        List<CollectTask> timeoutTasks = taskRepository.findTimeoutTasks(timeout);

        for (CollectTask task : timeoutTasks) {
            log.warn("Task timeout: {}", task.getId());
            task.setStatus(TaskStatus.TIMEOUT.name());
            task.setEndTime(LocalDateTime.now());
            taskRepository.save(task);

            // 如果配置了重试,则重新入队
            if (task.getRetryTimes() < task.getMaxRetryTimes()) {
                task.setRetryTimes(task.getRetryTimes() + 1);
                task.setStatus(TaskStatus.WAITING.name());
                taskQueue.offer(task);
            }
        }
    }
}