package com.study.collect.core.task.executor;

// 抽象执行器

import com.study.collect.core.task.definition.TaskDefinition;
import com.study.collect.core.task.model.TaskContext;
import com.study.collect.core.task.model.TaskStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

@Slf4j
public abstract class AbstractTaskExecutor implements TaskExecutor {

    @Override
    public void execute(TaskDefinition task, TaskContext context) {
        StopWatch stopWatch = new StopWatch();
        try {
            // 1. 前置处理
            beforeExecute(task, context);
            stopWatch.start();

            // 2. 执行任务
            doExecute(task, context);

            // 3. 后置处理
            stopWatch.stop();
            afterExecute(task, context);

            // 4. 更新任务状态
            updateTaskStatus(task.getTaskId(), TaskStatus.SUCCESS);

        } catch (Exception e) {
            log.error("Task execution failed, taskId: {}", task.getTaskId(), e);
            onError(task, context, e);
            updateTaskStatus(task.getTaskId(), TaskStatus.FAILED);
        } finally {
            log.info("Task execution completed, taskId: {}, cost: {}ms",
                    task.getTaskId(), stopWatch.getTotalTimeMillis());
        }
    }

    /**
     * 任务执行前处理
     */
    protected void beforeExecute(TaskDefinition task, TaskContext context) {
        log.info("Start executing task, taskId: {}", task.getTaskId());
        updateTaskStatus(task.getTaskId(), TaskStatus.RUNNING);
    }

    /**
     * 执行具体任务
     */
    protected abstract void doExecute(TaskDefinition task, TaskContext context);

    /**
     * 任务执行后处理
     */
    protected void afterExecute(TaskDefinition task, TaskContext context) {
        log.info("Task execution completed successfully, taskId: {}", task.getTaskId());
    }

    /**
     * 任务执行异常处理
     */
    protected void onError(TaskDefinition task, TaskContext context, Exception e) {
        log.error("Task execution error handler, taskId: {}", task.getTaskId(), e);
    }

    /**
     * 更新任务状态
     */
    protected void updateTaskStatus(String taskId, TaskStatus status) {
        log.info("Update task status, taskId: {}, status: {}", taskId, status);
        // TODO: 实现具体的状态更新逻辑
    }
}

