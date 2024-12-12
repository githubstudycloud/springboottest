package com.study.common.model.task;


import lombok.Data;
import lombok.Builder;
import java.util.Date;
import java.util.Map;
import java.util.List;

/**
 * 任务定义
 */
@Data
@Builder
public class TaskDefinition {
    private String id;
    private String name;
    private String description;
    private TaskType type;
    private String cronExpression;
    private HttpConfig httpConfig;
    private List<String> variables;
    private Integer priority;
    private RetryPolicy retryPolicy;
    private TaskStatus status;
    private Date createTime;
    private Date updateTime;
    public TaskDefinition() {
    }
}