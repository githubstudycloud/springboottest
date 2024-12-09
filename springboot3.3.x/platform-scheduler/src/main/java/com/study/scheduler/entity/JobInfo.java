package com.study.scheduler.entity;

import lombok.Data;

import java.util.Date;

@Data
public class JobInfo {
    private Long id;
    private String jobName;        // 任务名称
    private String jobGroup;       // 任务分组
    private String jobClass;       // 任务类
    private String cronExpression; // cron表达式
    private String parameter;      // 任务参数（JSON格式）
    private String description;    // 任务描述
    private Integer concurrent;    // 是否允许并发执行
    private Integer status;        // 任务状态
    private Date nextFireTime;     // 下次执行时间
    private Date prevFireTime;     // 上次执行时间
    private Date createTime;       // 创建时间
    private Date updateTime;       // 更新时间
}