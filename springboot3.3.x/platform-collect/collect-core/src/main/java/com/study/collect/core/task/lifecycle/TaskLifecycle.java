package com.study.collect.core.task.lifecycle;

import com.study.collect.core.task.model.TaskStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TaskLifecycle {

    private final String taskId;
    private TaskStatus currentStatus;
    private LocalDateTime createTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime lastUpdateTime;

    public TaskLifecycle(String taskId) {
        this.taskId = taskId;
        this.createTime = LocalDateTime.now();
        this.currentStatus = TaskStatus.CREATED;
    }

    public void setCurrentStatus(TaskStatus status) {
        this.currentStatus = status;
        this.lastUpdateTime = LocalDateTime.now();

        switch (status) {
            case RUNNING -> this.startTime = LocalDateTime.now();
            case SUCCESS, FAILED, CANCELED -> this.endTime = LocalDateTime.now();
        }
    }
}