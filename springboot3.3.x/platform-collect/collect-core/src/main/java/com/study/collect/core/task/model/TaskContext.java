package com.study.collect.core.task.model;

// 任务上下文

import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class TaskContext {
    private String taskId;           // 任务ID
    private Integer shardingId;      // 分片ID
    private Integer shardingTotal;   // 分片总数
    private Map<String, Object> attributes = new ConcurrentHashMap<>(); // 上下文属性

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key) {
        return (T) attributes.get(key);
    }
}