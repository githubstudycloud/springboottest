package com.study.collect.core.collector;

import com.study.collect.annotation.CollectorFor;
import com.study.collect.core.executor.CollectExecutor;
import com.study.collect.entity.CollectTask;
import com.study.collect.entity.TaskResult;
import com.study.collect.enums.CollectorType;
import com.study.collect.enums.TaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


// 核心采集实现
@Component
@CollectorFor(CollectorType.DEFAULT)
public class DistributedCollector implements Collector {
    private static final Logger logger = LoggerFactory.getLogger(DistributedCollector.class);
    private static final String TASK_LOCK_PREFIX = "collect:task:lock:";
    private static final long LOCK_TIMEOUT = 10; // 锁超时时间（分钟）

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private CollectExecutor collectExecutor;

    @Override
    public TaskResult collect(CollectTask task) {
        String lockKey = TASK_LOCK_PREFIX + task.getId();

        // 尝试获取分布式锁
        Boolean acquired = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, "LOCKED", LOCK_TIMEOUT, TimeUnit.MINUTES);

        if (Boolean.TRUE.equals(acquired)) {
            try {
                // 执行采集任务
                return doCollect(task);
            } finally {
                // 释放锁
                redisTemplate.delete(lockKey);
            }
        } else {
            logger.warn("Task {} is already being executed on another instance", task.getId());
            return TaskResult.builder()
                    .taskId(task.getId())
                    .status(TaskStatus.FAILED)
                    .message("Task is already running")
                    .build();
        }
    }

    private TaskResult doCollect(CollectTask task) {
        try {
            logger.info("Starting collection for task: {}", task.getId());

            // 更新任务状态为运行中
            task.setStatus(TaskStatus.RUNNING);
            updateTaskStatus(task);

            // 执行采集
            TaskResult result = collectExecutor.execute(task);

            // 更新任务状态
            task.setStatus(result.isSuccess() ? TaskStatus.COMPLETED : TaskStatus.FAILED);
            updateTaskStatus(task);

            return result;

        } catch (Exception e) {
            logger.error("Error collecting data for task: " + task.getId(), e);

            // 更新任务状态为失败
            task.setStatus(TaskStatus.FAILED);
            updateTaskStatus(task);

            return TaskResult.builder()
                    .taskId(task.getId())
                    .status(TaskStatus.FAILED)
                    .message("Collection failed: " + e.getMessage())
                    .build();
        }
    }

    private void updateTaskStatus(CollectTask task) {
        // 更新状态到Redis
        String statusKey = "collect:task:status:" + task.getId();
        redisTemplate.opsForValue().set(statusKey, task.getStatus(), 24, TimeUnit.HOURS);

        logger.info("Updated task {} status to {}", task.getId(), task.getStatus());
    }

    @Override
    public TaskStatus getTaskStatus(String taskId) {
        String statusKey = "collect:task:status:" + taskId;
        Object status = redisTemplate.opsForValue().get(statusKey);
        return status != null ? (TaskStatus) status : TaskStatus.UNKNOWN;
    }

    @Override
    public void stopTask(String taskId) {
        String lockKey = TASK_LOCK_PREFIX + taskId;
        redisTemplate.delete(lockKey);

        // 更新状态为已停止
        String statusKey = "collect:task:status:" + taskId;
        redisTemplate.opsForValue().set(statusKey, TaskStatus.STOPPED);

        logger.info("Task {} has been stopped", taskId);
    }
}
