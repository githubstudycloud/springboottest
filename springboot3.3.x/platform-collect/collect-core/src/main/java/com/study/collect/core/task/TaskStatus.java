package com.study.collect.core.task;

// 3. 任务状态枚举
public enum TaskStatus {
    CREATED,    // 已创建
    RUNNING,    // 执行中
    SUCCESS,    // 执行成功
    FAILED,     // 执行失败
    CANCELED    // 已取消
}