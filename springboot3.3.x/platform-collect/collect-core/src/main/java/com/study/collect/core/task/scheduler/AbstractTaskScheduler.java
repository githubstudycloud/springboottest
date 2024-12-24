package com.study.collect.core.task.scheduler;

// 抽象调度器

import com.study.collect.core.task.definition.TaskDefinition;
import com.study.collect.core.task.executor.TaskExecutor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public abstract class AbstractTaskScheduler implements TaskScheduler {

    protected final TaskExecutor taskExecutor;
    protected final Map<String, TaskDefinition> taskDefinitions = new ConcurrentHashMap<>();

    protected AbstractTaskScheduler(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    @Override
    public void addTask(TaskDefinition task) {
        log.info("Add task to scheduler, taskId: {}", task.getTaskId());
        taskDefinitions.put(task.getTaskId(), task);
        doAddTask(task);
    }

    @Override
    public void removeTask(String taskId) {
        log.info("Remove task from scheduler, taskId: {}", taskId);
        taskDefinitions.remove(taskId);
        doRemoveTask(taskId);
    }

    @Override
    public void pauseTask(String taskId) {
        log.info("Pause task, taskId: {}", taskId);
        doPauseTask(taskId);
    }

    @Override
    public void resumeTask(String taskId) {
        log.info("Resume task, taskId: {}", taskId);
        doResumeTask(taskId);
    }

    protected abstract void doAddTask(TaskDefinition task);

    protected abstract void doRemoveTask(String taskId);

    protected abstract void doPauseTask(String taskId);

    protected abstract void doResumeTask(String taskId);
}
