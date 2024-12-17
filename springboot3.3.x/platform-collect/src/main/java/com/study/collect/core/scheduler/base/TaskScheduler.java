package com.study.collect.core.scheduler.base;

// 调度器接口

import com.study.collect.domain.entity.task.CollectTask;

/**
 * 任务调度器
 */
public interface TaskScheduler {
    /**
     * 提交任务
     */
    void submit(CollectTask task);

    /**
     * 启动调度器
     */
    void start();

    /**
     * 停止调度器
     */
    void stop();
}

