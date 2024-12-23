package com.study.collect.core.task;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

// 1. 采集任务
@Data
public class CollectTask {
    private String id;              // 任务ID
    private String type;            // 任务类型
    private Map<String, Object> params;  // 任务参数
    private Integer shardIndex;     // 分片索引
    private Integer shardTotal;     // 分片总数
    private TaskStatus status;      // 任务状态
    private LocalDateTime createTime; // 创建时间

    public static CollectTask create(String type, Map<String, Object> params) {
        CollectTask task = new CollectTask();
        task.setId(UUID.randomUUID().toString());
        task.setType(type);
        task.setParams(params);
        task.setStatus(TaskStatus.CREATED);
        task.setCreateTime(LocalDateTime.now());
        return task;
    }
}
