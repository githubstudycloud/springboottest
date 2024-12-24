package com.study.collect.core.task.scheduler;

// 动态调度器

import com.study.collect.core.task.definition.TaskDefinition;
import com.study.collect.core.task.executor.TaskExecutor;
import com.study.collect.core.task.model.TaskContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class DynamicTaskScheduler extends AbstractTaskScheduler {

    private final ThreadPoolTaskScheduler taskScheduler;
    private final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
    private final AtomicInteger activeTaskCount = new AtomicInteger(0);

    public DynamicTaskScheduler(TaskExecutor taskExecutor, ThreadPoolTaskScheduler taskScheduler) {
        super(taskExecutor);
        this.taskScheduler = taskScheduler;
    }

    @Override
    public void start() {
        log.info("Starting dynamic task scheduler");
        taskScheduler.initialize();
    }

    @Override
    public void stop() {
        log.info("Stopping dynamic task scheduler, active tasks: {}", activeTaskCount.get());
        scheduledTasks.values().forEach(future -> future.cancel(true));
        scheduledTasks.clear();
        activeTaskCount.set(0);
        taskScheduler.shutdown();
    }

    @Override
    protected void doAddTask(TaskDefinition task) {
        // 动态调整线程池参数
        adjustThreadPool();
        scheduleTask(task);
    }

    @Override
    protected void doRemoveTask(String taskId) {
        ScheduledFuture<?> future = scheduledTasks.remove(taskId);
        if (future != null) {
            future.cancel(true);
            activeTaskCount.decrementAndGet();
            // 重新调整线程池
            adjustThreadPool();
        }
    }

    @Override
    protected void doPauseTask(String taskId) {
        ScheduledFuture<?> future = scheduledTasks.get(taskId);
        if (future != null) {
            future.cancel(false);
            activeTaskCount.decrementAndGet();
            adjustThreadPool();
        }
    }

    @Override
    protected void doResumeTask(String taskId) {
        TaskDefinition task = taskDefinitions.get(taskId);
        if (task != null) {
            scheduleTask(task);
        }
    }

    private void scheduleTask(TaskDefinition task) {
        try {
            // 创建任务上下文
            TaskContext context = new TaskContext();
            context.setTaskId(task.getTaskId());

            // 处理分片配置
            if (task.getSharding() != null && task.getSharding().isEnabled()) {
                scheduleShardingTask(task, context);
            } else {
                scheduleSimpleTask(task, context);
            }

            activeTaskCount.incrementAndGet();
            log.info("Task scheduled successfully, taskId: {}, active tasks: {}",
                    task.getTaskId(), activeTaskCount.get());

        } catch (Exception e) {
            log.error("Failed to schedule task, taskId: {}", task.getTaskId(), e);
        }
    }

    private void scheduleSimpleTask(TaskDefinition task, TaskContext context) {
        ScheduledFuture<?> future = taskScheduler.schedule(
                () -> taskExecutor.execute(task, context),
                new CronTrigger(task.getCronExpression())
        );
        scheduledTasks.put(task.getTaskId(), future);
    }

    private void scheduleShardingTask(TaskDefinition task, TaskContext context) {
        // 为每个分片创建调度任务
        for (int i = 0; i < task.getSharding().getTotal(); i++) {
            context.setShardingId(i);
            context.setShardingTotal(task.getSharding().getTotal());

            String shardTaskId = task.getTaskId() + "_" + i;
            ScheduledFuture<?> future = taskScheduler.schedule(
                    () -> taskExecutor.execute(task, context),
                    new CronTrigger(task.getCronExpression())
            );
            scheduledTasks.put(shardTaskId, future);
        }
    }

    private void adjustThreadPool() {
        // 根据活动任务数动态调整线程池参数
        int currentActive = activeTaskCount.get();
        int corePoolSize = Math.max(5, currentActive / 2);
        int maxPoolSize = Math.max(10, currentActive);

        taskScheduler.setPoolSize(maxPoolSize);
        log.info("Adjusted thread pool, active tasks: {}, core: {}, max: {}",
                currentActive, corePoolSize, maxPoolSize);
    }
}