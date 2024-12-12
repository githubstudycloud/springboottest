package com.study.common.model.task;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * 任务执行记录
 */
@Data
@Builder
public class TaskExecution {
    private String id;
    private String taskDefId;
    private String traceId;
    private TaskStatus status;
    private Date startTime;
    private Date endTime;
    private Long duration;
    private String result;
    private String error;
    private String nodeInfo;
    private Integer retryCount;

    public TaskExecution() {
    }
}