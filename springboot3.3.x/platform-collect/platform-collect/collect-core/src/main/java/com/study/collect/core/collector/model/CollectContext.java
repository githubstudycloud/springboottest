package com.study.collect.core.collector.model;

import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class CollectContext<T> {
    /**
     * 上下文属性
     */
    private final Map<String, Object> attributes = new ConcurrentHashMap<>();
    /**
     * 任务ID
     */
    private String taskId;
    /**
     * 采集参数
     */
    private T params;
    /**
     * 分片信息
     */
    private Integer shardingId;
    private Integer shardingTotal;

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <V> V getAttribute(String key) {
        return (V) attributes.get(key);
    }
}
