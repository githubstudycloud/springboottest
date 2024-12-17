package com.study.collect.infrastructure.mq.producer;

import com.study.collect.domain.entity.task.CollectTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * 消息生产者
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MessageProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendTask(CollectTask task) {
        try {
            rabbitTemplate.convertAndSend("collect.task", "collect.task", task);
            log.info("Send task message success: {}", task.getId());
        } catch (Exception e) {
            log.error("Send task message failed", e);
            throw new MessageException("Send task message failed", e);
        }
    }

    public void sendResult(CollectResult result) {
        try {
            rabbitTemplate.convertAndSend("collect.result", "collect.result", result);
            log.info("Send result message success: {}", result.getTaskId());
        } catch (Exception e) {
            log.error("Send result message failed", e);
            throw new MessageException("Send result message failed", e);
        }
    }
}
