package com.study.collect.infrastructure.mq.producer;

import com.study.collect.common.exception.collect.CollectException;
import com.study.collect.domain.entity.task.CollectTask;
import com.study.collect.model.response.collect.CollectResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * 消息生产者
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MessageProducer {

    private final RabbitTemplate rabbitTemplate;
    private static final String TASK_EXCHANGE = "collect.task";
    private static final String RESULT_EXCHANGE = "collect.result";

    /**
     * 发送任务消息
     */
    public void sendTask(CollectTask task) {
        try {
            rabbitTemplate.convertAndSend(TASK_EXCHANGE, "collect.task", task, message -> {
                message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                message.getMessageProperties().setExpiration("3600000"); // 1小时过期
                return message;
            });
            log.info("Send task message success: {}", task.getId());
        } catch (Exception e) {
            log.error("Send task message failed", e);
            throw new CollectException("Send task message failed: " + e.getMessage());
        }
    }

    /**
     * 发送结果消息
     */
    public void sendResult(CollectResult result) {
        try {
            rabbitTemplate.convertAndSend(RESULT_EXCHANGE, "collect.result", result, message -> {
                message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                return message;
            });
            log.info("Send result message success: {}", result.getTaskId());
        } catch (Exception e) {
            log.error("Send result message failed", e);
            throw new CollectException("Send result message failed: " + e.getMessage());
        }
    }
}