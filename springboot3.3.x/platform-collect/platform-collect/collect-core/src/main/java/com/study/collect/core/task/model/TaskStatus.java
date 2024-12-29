package com.study.collect.core.task.model;

// 任务状态枚举
public enum TaskStatus {
    CREATED("已创建"),
    WAITING("等待中"),
    RUNNING("执行中"),
    SUCCESS("执行成功"),
    FAILED("执行失败"),
    CANCELED("已取消"),
    TIMEOUT("已超时");

    private final String description;

    TaskStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}