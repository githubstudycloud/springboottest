package com.study.collect.entity;

import com.study.collect.enums.TaskStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaskResult {
    private String taskId;
    private TaskStatus status;
    private Integer statusCode;
    private String responseBody;
    private String message;

    public boolean isSuccess() {
        return TaskStatus.COMPLETED.equals(status);
    }
}