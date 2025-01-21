package com.study.collect.core.task.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 任务实例
 */
@Data
public class TaskInstance {
    private Long id;
    private String instanceId;      // 实例ID
    private String taskCode;        // 任务编码
    private Integer shardIndex;     // 分片索引
    private Integer shardTotal;     // 分片总数
    private String shardParam;      // 分片参数
    private Integer status;         // 状态
    private String errorMsg;        // 错误信息
    private String hostName;        // 执行机器
    private LocalDateTime startTime;  // 开始时间
    private LocalDateTime endTime;    // 结束时间
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 更新时间
}