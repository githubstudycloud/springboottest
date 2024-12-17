package com.study.collect.common.constant.mq;

// 消息常量
/**
 * 消息相关常量
 */
public final class MessageConstant {
    private MessageConstant() {}

    public static final String TASK_EXCHANGE = "collect.task";
    public static final String TASK_QUEUE = "collect.task.queue";
    public static final String RESULT_EXCHANGE = "collect.result";
    public static final String RESULT_QUEUE = "collect.result.queue";
}
