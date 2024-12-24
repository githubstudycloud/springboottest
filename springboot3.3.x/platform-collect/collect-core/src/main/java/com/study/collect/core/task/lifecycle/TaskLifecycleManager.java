package com.study.collect.core.task.lifecycle;

import com.study.collect.core.task.model.TaskStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class TaskLifecycleManager {

    private final Map<String, TaskLifecycle> lifecycles = new ConcurrentHashMap<>();

    public void createTask(String taskId) {
        TaskLifecycle lifecycle = new TaskLifecycle(taskId);
        lifecycles.put(taskId, lifecycle);
        updateStatus(taskId, TaskStatus.CREATED);
    }

    public void startTask(String taskId) {
        checkLifecycleExists(taskId);
        updateStatus(taskId, TaskStatus.RUNNING);
    }

    public void completeTask(String taskId, boolean success) {
        checkLifecycleExists(taskId);
        updateStatus(taskId, success ? TaskStatus.SUCCESS : TaskStatus.FAILED);
    }

    public void pauseTask(String taskId) {
        checkLifecycleExists(taskId);
        updateStatus(taskId, TaskStatus.WAITING);
    }

    public void cancelTask(String taskId) {
        checkLifecycleExists(taskId);
        updateStatus(taskId, TaskStatus.CANCELED);
    }

    public TaskStatus getTaskStatus(String taskId) {
        TaskLifecycle lifecycle = lifecycles.get(taskId);
        return lifecycle != null ? lifecycle.getCurrentStatus() : null;
    }

    private void updateStatus(String taskId, TaskStatus newStatus) {
        TaskLifecycle lifecycle = lifecycles.get(taskId);
        TaskStatus oldStatus = lifecycle.getCurrentStatus();

        if (isValidStatusTransition(oldStatus, newStatus)) {
            lifecycle.setCurrentStatus(newStatus);
            log.info("任务状态更新 - taskId: {}, {} -> {}", taskId, oldStatus, newStatus);
            publishStatusChangeEvent(taskId, oldStatus, newStatus);
        } else {
            log.warn("非法的状态转换 - taskId: {}, {} -> {}", taskId, oldStatus, newStatus);
        }
    }

    private void checkLifecycleExists(String taskId) {
        if (!lifecycles.containsKey(taskId)) {
            throw new IllegalStateException("任务生命周期不存在: " + taskId);
        }
    }

    private boolean isValidStatusTransition(TaskStatus from, TaskStatus to) {
        // 实现状态转换的合法性检查逻辑
        return true; // 简化实现
    }

    private void publishStatusChangeEvent(String taskId, TaskStatus oldStatus, TaskStatus newStatus) {
        // 发布任务状态变更事件
    }
}
