package com.study.collect.core.task.manager;

import com.study.collect.core.mq.producer.TaskProducer;
import com.study.collect.core.task.definition.TaskDefinition;
import com.study.collect.core.task.model.TaskResult;
import com.study.collect.core.task.model.TaskStatus;
import com.study.collect.core.task.scheduler.TaskScheduler;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DefaultTaskManager extends AbstractTaskManager {

    // 内存中维护任务状态
    private final Map<String, TaskStatus> taskStatusMap = new ConcurrentHashMap<>();

    public DefaultTaskManager(TaskProducer taskProducer, TaskScheduler taskScheduler) {
        super(taskProducer, taskScheduler);
    }

    @Override
    protected void beforeSubmit(TaskDefinition task) {
        taskStatusMap.put(task.getTaskId(), TaskStatus.CREATED);
    }

    @Override
    protected void doCancelTask(String taskId) {
        taskStatusMap.put(taskId, TaskStatus.CANCELED);
    }

    @Override
    protected void doPauseTask(String taskId) {
        taskStatusMap.put(taskId, TaskStatus.WAITING);
    }

    @Override
    protected void doResumeTask(String taskId) {
        taskStatusMap.put(taskId, TaskStatus.RUNNING);
    }

    @Override
    public TaskResult getTaskStatus(String taskId) {
        TaskStatus status = taskStatusMap.get(taskId);
        if (status == null) {
            return TaskResult.failure(taskId, "Task not found");
        }
        return TaskResult.success(taskId, status);
    }
}