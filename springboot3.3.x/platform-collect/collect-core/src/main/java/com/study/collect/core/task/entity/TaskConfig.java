package com.study.collect.core.task.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 任务配置
 */
@Data
public class TaskConfig {
    private Long id;
    private String taskCode;        // 任务编码
    private String taskName;        // 任务名称
    private String taskHandler;     // 任务处理器
    private String taskParam;       // 任务参数(JSON)
    private String cronExpr;        // cron表达式
    private Integer shardTotal;     // 分片总数
    private Integer retryTimes;     // 重试次数
    private Integer retryInterval;  // 重试间隔(秒)
    private Integer timeout;        // 超时时间(秒)
    private Integer status;         // 状态:0-禁用,1-启用
    private String remark;          // 备注
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 更新时间
}