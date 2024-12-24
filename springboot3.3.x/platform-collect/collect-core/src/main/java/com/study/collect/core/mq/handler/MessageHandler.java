package com.study.collect.core.mq.handler;

import com.study.collect.core.mq.message.ResultMessage;
import com.study.collect.core.mq.message.TaskMessage;

/**
 * 消息处理器接口
 */
public interface MessageHandler {

    /**
     * 处理任务消息
     *
     * @param message 任务消息
     */
    void handleTaskMessage(TaskMessage message);

    /**
     * 处理结果消息
     *
     * @param message 结果消息
     */
    void handleResultMessage(ResultMessage message);
}