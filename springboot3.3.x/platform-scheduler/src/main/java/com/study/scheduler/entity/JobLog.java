package com.study.scheduler.entity;

import lombok.Data;

import java.util.Date;

@Data
public class JobLog {
    private Long id;
    private Long jobId;           // 任务ID
    private String jobName;       // 任务名称
    private String jobGroup;      // 任务分组
    private String jobClass;      // 任务类
    private String parameter;     // 执行参数
    private String message;       // 日志信息
    private Integer status;       // 执行状态
    private String exceptionInfo; // 异常信息
    private Date startTime;       // 开始时间
    private Date endTime;         // 结束时间
    private Long duration;        // 执行时长(毫秒)
    private String serverIp;      // 执行服务器IP
}
