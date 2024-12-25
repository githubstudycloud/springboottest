package com.study.collect.core.task.model;

// 任务定义


import lombok.Data;

import java.util.Map;

@Data
public class TaskDefinition {
    private String taskId;           // 任务ID
    private String taskName;         // 任务名称
    private String taskHandler;      // 任务处理器
    private String cronExpression;   // 调度表达式
    private ShardingConfig sharding; // 分片配置
    private Map<String, Object> props;// 扩展属性
}
