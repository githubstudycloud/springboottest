package com.study.collect.core.task.definition;

import lombok.Data;

@Data
public class TaskTrigger {
    private String cronExpression;   // cron表达式
    private Long interval;           // 固定间隔
    private Long delay;              // 延迟时间
}
