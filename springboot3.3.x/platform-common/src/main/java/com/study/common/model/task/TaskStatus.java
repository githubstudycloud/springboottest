package com.study.common.model.task;


/**
 * 任务状态
 */
public enum TaskStatus {
    CREATED,
    WAITING,
    RUNNING,
    COMPLETED,
    FAILED,
    STOPPED,
    SKIPPED
}