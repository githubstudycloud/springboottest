package com.study.collect.core.task;

import lombok.Data;

import java.time.LocalDateTime;

// 2. 任务结果
@Data
public class TaskResult {
    private String taskId;          // 任务ID
    private String type;            // 任务类型
    private Boolean success;        // 是否成功
    private String message;         // 结果信息
    private Object data;            // 结果数据
    private LocalDateTime finishTime; // 完成时间
}
