package com.study.collect.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RabbitMQUtils {
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQUtils.class);

    private final RabbitTemplate rabbitTemplate;
    private final MessageConverter messageConverter;

    public RabbitMQUtils(RabbitTemplate rabbitTemplate, MessageConverter messageConverter) {
        this.rabbitTemplate = rabbitTemplate;
        this.messageConverter = messageConverter;
    }

    /**
     * 发送消息到指定交换机
     */
    public void sendMessage(String exchange, String routingKey, Object message) {
        String correlationId = UUID.randomUUID().toString();
        CorrelationData correlationData = new CorrelationData(correlationId);

        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, message, correlationData);
            logger.info("Message sent successfully - CorrelationId: {}, Exchange: {}, RoutingKey: {}",
                    correlationId, exchange, routingKey);
        } catch (Exception e) {
            logger.error("Failed to send message - CorrelationId: {}, Exchange: {}, RoutingKey: {}",
                    correlationId, exchange, routingKey, e);
            throw e;
        }
    }

    /**
     * 发送延迟消息
     */
    public void sendDelayedMessage(String exchange, String routingKey, Object message, long delayMillis) {
        MessageProperties properties = new MessageProperties();
        properties.setDelay((int) delayMillis);

        Message amqpMessage = messageConverter.toMessage(message, properties);
        String correlationId = UUID.randomUUID().toString();

        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, amqpMessage, msg -> {
                MessageProperties msgProperties = msg.getMessageProperties();
                msgProperties.setDelay((int) delayMillis);
                return msg;
            }, new CorrelationData(correlationId));

            logger.info("Delayed message sent successfully - CorrelationId: {}, Delay: {}ms",
                    correlationId, delayMillis);
        } catch (Exception e) {
            logger.error("Failed to send delayed message - CorrelationId: {}", correlationId, e);
            throw e;
        }
    }

    /**
     * 发送消息到重试队列
     */
    public void sendToRetryQueue(String exchange, String routingKey, Object message, int retryCount) {
        MessageProperties properties = new MessageProperties();
        properties.setHeader("x-retry-count", retryCount);

        Message amqpMessage = messageConverter.toMessage(message, properties);
        String correlationId = UUID.randomUUID().toString();

        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, amqpMessage, msg -> {
                MessageProperties msgProperties = msg.getMessageProperties();
                msgProperties.setHeader("x-retry-count", retryCount);
                return msg;
            }, new CorrelationData(correlationId));

            logger.info("Message sent to retry queue - CorrelationId: {}, RetryCount: {}",
                    correlationId, retryCount);
        } catch (Exception e) {
            logger.error("Failed to send message to retry queue - CorrelationId: {}", correlationId, e);
            throw e;
        }
    }
}
