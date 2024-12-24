package com.study.collect.core.task.definition;

// 触发器定义

import lombok.Data;

@Data
public class TaskTrigger {
    private String cronExpression;   // cron表达式
    private Long interval;           // 固定间隔(毫秒)
    private Long delay;              // 初始延迟(毫秒)
    private Boolean repeat;          // 是否重复执行
}
