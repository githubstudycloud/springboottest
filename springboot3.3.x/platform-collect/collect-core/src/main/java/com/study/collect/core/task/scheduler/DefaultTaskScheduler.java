package com.study.collect.core.task.scheduler;

// 默认调度器

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

@Slf4j
@Component
public class DefaultTaskScheduler extends AbstractTaskScheduler {

    private final ThreadPoolTaskScheduler taskScheduler;
    private final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    public DefaultTaskScheduler(TaskExecutor taskExecutor, ThreadPoolTaskScheduler taskScheduler) {
        super(taskExecutor);
        this.taskScheduler = taskScheduler;
    }

    @Override
    public void start() {
        log.info("Starting task scheduler");
        taskScheduler.initialize();

        // 初始化时调度所有已配置的任务
        taskDefinitions.values().forEach(this::scheduleTask);
    }

    @Override
    public void stop() {
        log.info("Stopping task scheduler");
        scheduledTasks.values().forEach(future -> future.cancel(true));
        scheduledTasks.clear();
        taskScheduler.shutdown();
    }

    @Override
    protected void doAddTask(TaskDefinition task) {
        scheduleTask(task);
    }

    @Override
    protected void doRemoveTask(String taskId) {
        ScheduledFuture<?> future = scheduledTasks.remove(taskId);
        if (future != null) {
            future.cancel(true);
        }
    }

    @Override
    protected void doPauseTask(String taskId) {
        ScheduledFuture<?> future = scheduledTasks.get(taskId);
        if (future != null) {
            future.cancel(false);
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

            // 根据cron表达式调度任务
            ScheduledFuture<?> future = taskScheduler.schedule(
                    () -> taskExecutor.execute(task, context),
                    new CronTrigger(task.getCronExpression())
            );

            // 保存调度任务引用
            scheduledTasks.put(task.getTaskId(), future);
            log.info("Task scheduled successfully, taskId: {}, cron: {}",
                    task.getTaskId(), task.getCronExpression());

        } catch (Exception e) {
            log.error("Failed to schedule task, taskId: {}", task.getTaskId(), e);
        }
    }
}