package com.study.collect.core.task.model;

// 任务结果

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskResult {
    private String taskId;          // 任务ID
    private Boolean success;        // 执行结果
    private String errorMessage;    // 错误信息
    private Object data;           // 结果数据
    private LocalDateTime finishTime; // 完成时间

    public static TaskResult success(String taskId, Object data) {
        TaskResult result = new TaskResult();
        result.setTaskId(taskId);
        result.setSuccess(true);
        result.setData(data);
        result.setFinishTime(LocalDateTime.now());
        return result;
    }

    public static TaskResult failure(String taskId, String errorMessage) {
        TaskResult result = new TaskResult();
        result.setTaskId(taskId);
        result.setSuccess(false);
        result.setErrorMessage(errorMessage);
        result.setFinishTime(LocalDateTime.now());
        return result;
    }
}

