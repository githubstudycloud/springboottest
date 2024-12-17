package com.study.collect.common.constant.collect;

// 任务常量

/**
 * 任务相关常量
 */
public final class TaskConstant {
    private TaskConstant() {}

    public static final String TASK_PREFIX = "COLLECT_TASK_";
    public static final int MAX_RETRY_TIMES = 3;
    public static final long DEFAULT_TIMEOUT = 60000L;
    public static final int DEFAULT_BATCH_SIZE = 100;
    public static final String TASK_LOCK_PREFIX = "TASK_LOCK_";
}