package com.study.collect.core.task.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
public class TaskContext {
    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 任务实例ID
     */
    private String instanceId;

    /**
     * 分片索引，从0开始
     */
    private Integer shardIndex;

    /**
     * 分片总数
     */
    private Integer shardTotal;

    /**
     * 分片参数，JSON格式
     */
    private String shardParam;

    /**
     * 执行开始时间
     */
    private LocalDateTime startTime;

    /**
     * 执行超时时间（秒）
     */
    private Integer timeout;

    /**
     * 执行机器
     */
    private String hostName;

    /**
     * 上下文参数，用于在执行过程中传递数据
     */
    private Map<String, Object> parameters;

    public TaskContext() {
        this.startTime = LocalDateTime.now();
        this.parameters = new HashMap<>();
    }

    /**
     * 设置上下文参数
     */
    public void setParameter(String key, Object value) {
        this.parameters.put(key, value);
    }

    /**
     * 获取上下文参数
     */
    @SuppressWarnings("unchecked")
    public <T> T getParameter(String key) {
        return (T) this.parameters.get(key);
    }

    /**
     * 移除上下文参数
     */
    public void removeParameter(String key) {
        this.parameters.remove(key);
    }

    /**
     * 清空所有上下文参数
     */
    public void clearParameters() {
        this.parameters.clear();
    }
}