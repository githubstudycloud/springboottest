package com.study.collect.core.mq.consumer;

import com.study.collect.core.mq.handler.MessageHandler;
import com.study.collect.core.mq.message.ResultMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResultConsumer {

    private final MessageHandler messageHandler;

    @RabbitListener(queues = "#{@resultQueue.name}")
    public void onMessage(ResultMessage message) {
        try {
            log.info("收到结果消息: taskId={}", message.getTaskId());
            messageHandler.handleResultMessage(message);
        } catch (Exception e) {
            log.error("处理结果消息失败: taskId={}", message.getTaskId(), e);
        }
    }
}