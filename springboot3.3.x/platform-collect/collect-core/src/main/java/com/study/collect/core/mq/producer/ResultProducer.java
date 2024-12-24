package com.study.collect.core.mq.producer;

import com.study.collect.core.mq.config.MQProperties;
import com.study.collect.core.mq.message.ResultMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * 结果消息生产者
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ResultProducer {

    private final RabbitTemplate rabbitTemplate;
    private final MQProperties properties;

    public void sendResult(ResultMessage message) {
        try {
            MQProperties.RabbitMQ.Queue resultQueue = properties.getRabbit().getResult();
            rabbitTemplate.convertAndSend(
                    resultQueue.getExchange(),
                    resultQueue.getRoutingKey(),
                    message
            );
            log.info("结果消息发送成功: taskId={}", message.getTaskId());

        } catch (Exception e) {
            log.error("结果消息发送失败: taskId={}", message.getTaskId(), e);
            throw new RuntimeException("发送结果消息失败", e);
        }
    }
}