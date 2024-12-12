package com.study.common.model.task;

import lombok.Builder;
import lombok.Data;

/**
 * 任务结果
 */
@Data
@Builder
public class TaskResult {
    private boolean success;
    private String message;
    private Object data;
    private Long cost;
}