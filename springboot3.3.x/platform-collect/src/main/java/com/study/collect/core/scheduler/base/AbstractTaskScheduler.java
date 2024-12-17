package com.study.collect.core.scheduler.base;

// 调度器基类

import com.study.collect.common.enums.collect.TaskStatus;
import com.study.collect.common.exception.collect.TaskException;
import com.study.collect.core.engine.CollectEngine;
import com.study.collect.domain.entity.task.CollectTask;
import com.study.collect.domain.repository.task.TaskRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 抽象任务调度器
 */
@Slf4j
public abstract class AbstractTaskScheduler implements TaskScheduler {

    private volatile boolean running = false;
    protected final BlockingQueue<CollectTask> taskQueue = new LinkedBlockingQueue<>();
    protected final ExecutorService executorService;
    protected final TaskRepository taskRepository;
    protected final CollectEngine collectEngine;
    protected final MetricsCollector metricsCollector;

    protected AbstractTaskScheduler(
            TaskRepository taskRepository,
            CollectEngine collectEngine,
            MetricsCollector metricsCollector,
            ThreadPoolProperties properties
    ) {
        this.taskRepository = taskRepository;
        this.collectEngine = collectEngine;
        this.metricsCollector = metricsCollector;
        this.executorService = new ThreadPoolExecutor(
                properties.getCorePoolSize(),
                properties.getMaxPoolSize(),
                properties.getKeepAliveTime(),
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(properties.getQueueCapacity()),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    @Override
    public void submit(CollectTask task) {
        if (!running) {
            throw new TaskException("Scheduler is not running");
        }
        taskQueue.offer(task);
        metricsCollector.incrementTaskCount();
    }

    @Override
    public void start() {
        if (running) {
            return;
        }
        running = true;
        init();
        startSchedule();
    }

    @Override
    public void stop() {
        if (!running) {
            return;
        }
        running = false;
        executorService.shutdown();
    }

    /**
     * 初始化
     */
    protected void init() {
        // 加载未完成的任务
        List<CollectTask> unfinishedTasks = taskRepository.findByStatus(TaskStatus.RUNNING.name());
        unfinishedTasks.forEach(task -> {
            task.setStatus(TaskStatus.WAITING.name());
            taskRepository.save(task);
            taskQueue.offer(task);
        });
    }

    /**
     * 开始调度
     */
    protected abstract void startSchedule();
}
