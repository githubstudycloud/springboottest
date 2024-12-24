package com.study.collect.core.task.scheduler;

import com.study.collect.core.task.entity.TaskConfig;
import com.study.collect.core.task.entity.TaskInstance;
import com.study.collect.core.task.model.TaskDefinition;
import com.study.collect.core.task.service.TaskConfigService;
import com.study.collect.core.task.service.TaskExecuteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Component
public class DefaultTaskScheduler extends AbstractTaskScheduler {

    private final TaskScheduler scheduler;
    private final TaskDispatcher taskDispatcher;
    private volatile boolean running = false;

    public DefaultTaskScheduler(TaskConfigService taskConfigService,
                                TaskExecuteService taskExecuteService,
                                TaskScheduler scheduler,
                                TaskDispatcher taskDispatcher) {
        super(taskConfigService, taskExecuteService);
        this.scheduler = scheduler;
        this.taskDispatcher = taskDispatcher;
    }

    @Override
    protected void doStart() {
        if (running) {
            return;
        }
        running = true;

        // 加载所有可用的任务配置并调度
        taskConfigService.getEnabledTaskConfigs().forEach(config -> {
            try {
                TaskDefinition task = convertToDefinition(config);
                scheduleTask(task);
                log.info("Loaded task from config: {}", config.getTaskCode());
            } catch (Exception e) {
                log.error("Failed to load task: {}", config.getTaskCode(), e);
            }
        });

        // 启动定时任务状态检查
        scheduler.scheduleWithFixedDelay(
                this::checkRunningTasks,
                Duration.ofMinutes(1)
        );

        log.info("Task scheduler started successfully");
    }

    @Override
    protected void doStop() {
        if (!running) {
            return;
        }
        running = false;

        // 停止所有运行中的任务
        taskExecuteService.getRunningTasks().forEach(instance -> {
            try {
                taskExecuteService.completeTaskInstance(
                        instance.getInstanceId(),
                        false,
                        "Scheduler stopped"
                );
                log.info("Stopped running task: {}", instance.getInstanceId());
            } catch (Exception e) {
                log.error("Failed to stop task: {}", instance.getInstanceId(), e);
            }
        });

        log.info("Task scheduler stopped successfully");
    }

    @Override
    protected void doAddTask(TaskDefinition task) {
        validateTask(task);
        scheduleTask(task);
        log.info("Added new task: {}", task.getTaskId());
    }

    @Override
    protected void doRemoveTask(String taskId) {
        taskConfigService.disableTask(taskId);
        log.info("Removed task: {}", taskId);
    }

    @Override
    public void pauseTask(String taskId) {
        ScheduledFuture<?> future = scheduledTasks.get(taskId);
        if (future != null) {
            future.cancel(false);
            taskConfigService.disableTask(taskId);
            log.info("Paused task: {}", taskId);
        }
    }

    @Override
    public void resumeTask(String taskId) {
        TaskConfig config = taskConfigService.getTaskConfig(taskId);
        if (config != null) {
            taskConfigService.enableTask(taskId);
            scheduleTask(convertToDefinition(config));
            log.info("Resumed task: {}", taskId);
        }
    }

    private void scheduleTask(TaskDefinition task) {
        // 验证cron表达式
        if (!StringUtils.hasText(task.getCronExpression())) {
            log.warn("Task {} has no cron expression, skipped scheduling", task.getTaskId());
            return;
        }

        try {
            ScheduledFuture<?> future = scheduler.schedule(
                    () -> executeTask(task),
                    new CronTrigger(task.getCronExpression())
            );

            // 如果任务已存在，取消旧的调度
            ScheduledFuture<?> existing = scheduledTasks.put(task.getTaskId(), future);
            if (existing != null) {
                existing.cancel(true);
                log.info("Replaced existing schedule for task: {}", task.getTaskId());
            }

            log.info("Scheduled task: {}, cron: {}", task.getTaskId(), task.getCronExpression());

        } catch (Exception e) {
            log.error("Failed to schedule task: {}", task.getTaskId(), e);
            throw new RuntimeException("Failed to schedule task", e);
        }
    }

    private void executeTask(TaskDefinition task) {
        try {
            log.info("Starting task execution: {}", task.getTaskId());

            // 获取分片配置
            int shardTotal = task.getSharding() != null ? task.getSharding().getTotal() : 1;

            // 创建并分发分片任务
            for (int shardIndex = 0; shardIndex < shardTotal; shardIndex++) {
                String shardParam = createShardParam(shardIndex, shardTotal);
                TaskInstance instance = taskExecuteService.createTaskInstance(
                        task.getTaskId(),
                        shardIndex,
                        shardParam
                );

                taskDispatcher.dispatch(instance);
                log.info("Dispatched task instance: {} - shard {}/{}",
                        instance.getInstanceId(), shardIndex + 1, shardTotal);
            }

        } catch (Exception e) {
            log.error("Task execution failed: {}", task.getTaskId(), e);
        }
    }

    @Scheduled(fixedDelay = 60000)  // 每分钟检查一次
    private void checkRunningTasks() {
        if (!running) {
            return;
        }

        try {
            taskExecuteService.getRunningTasks().forEach(instance -> {
                TaskConfig config = taskConfigService.getTaskConfig(instance.getTaskCode());
                if (config != null && config.getTimeout() > 0) {
                    checkTaskTimeout(instance, config.getTimeout());
                }
            });
        } catch (Exception e) {
            log.error("Failed to check running tasks", e);
        }
    }

    private void checkTaskTimeout(TaskInstance instance, int timeoutSeconds) {
        if (instance.getStartTime() == null) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        if (instance.getStartTime().plusSeconds(timeoutSeconds).isBefore(now)) {
            try {
                taskExecuteService.completeTaskInstance(
                        instance.getInstanceId(),
                        false,
                        "Task execution timed out after " + timeoutSeconds + " seconds"
                );
                log.warn("Task instance timed out: {}", instance.getInstanceId());
            } catch (Exception e) {
                log.error("Failed to handle task timeout: {}", instance.getInstanceId(), e);
            }
        }
    }

    private String createShardParam(int shardIndex, int shardTotal) {
        return String.format("{\"shardIndex\":%d,\"shardTotal\":%d,\"uuid\":\"%s\"}",
                shardIndex, shardTotal, UUID.randomUUID().toString());
    }

    private TaskDefinition convertToDefinition(TaskConfig config) {
        TaskDefinition task = new TaskDefinition();
        task.setTaskId(config.getTaskCode());
        task.setTaskName(config.getTaskName());
        task.setTaskHandler(config.getTaskHandler());
        task.setCronExpression(config.getCronExpr());
        // 设置其他属性...
        return task;
    }

    private void validateTask(TaskDefinition task) {
        if (!StringUtils.hasText(task.getTaskId())) {
            throw new IllegalArgumentException("Task ID cannot be empty");
        }
        if (!StringUtils.hasText(task.getTaskHandler())) {
            throw new IllegalArgumentException("Task handler cannot be empty");
        }
        if (StringUtils.hasText(task.getCronExpression())) {
            try {
                new CronTrigger(task.getCronExpression());
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid cron expression: " + task.getCronExpression());
            }
        }
    }
}