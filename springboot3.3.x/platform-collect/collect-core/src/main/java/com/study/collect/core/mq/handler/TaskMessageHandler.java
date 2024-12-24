package com.study.collect.core.mq.handler;

import com.study.collect.core.mq.message.TaskMessage;
import com.study.collect.core.mq.message.ResultMessage;
import com.study.collect.core.task.executor.TaskExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskMessageHandler implements MessageHandler {

    private final TaskExecutor taskExecutor;

    @Override
    public void handleTaskMessage(TaskMessage message) {
        log.info("Handle task message: {}", message);
        // 任务处理
    }

    @Override
    public void handleResultMessage(ResultMessage message) {
        log.info("Handle result message: {}", message);
        // 结果处理
    }
}