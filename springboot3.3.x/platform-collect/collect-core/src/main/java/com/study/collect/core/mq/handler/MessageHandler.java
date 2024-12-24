package com.study.collect.core.mq.handler;

import com.study.collect.core.mq.message.TaskMessage;
import com.study.collect.core.mq.message.ResultMessage;

public interface MessageHandler {
    void handleTaskMessage(TaskMessage message);
    void handleResultMessage(ResultMessage message);
}
