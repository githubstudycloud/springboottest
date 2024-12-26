package com.study.collect.infrastructure.config.mq;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    
    @Value("${spring.rabbitmq.addresses}")
    private String addresses;
    
    @Value("${spring.rabbitmq.username}")
    private String username;
    
    @Value("${spring.rabbitmq.password}")
    private String password;
    
    @Value("${spring.rabbitmq.virtual-host:/}")
    private String virtualHost;
    
    @Value("${spring.rabbitmq.publisher-confirm-type:correlated}")
    private String publisherConfirmType;
    
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses(addresses);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(virtualHost);
        connectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.valueOf(publisherConfirmType.toUpperCase()));
        return connectionFactory;
    }
    
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }
    
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            SimpleRabbitListenerContainerFactoryConfigurer configurer,
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        configurer.configure(factory, connectionFactory);
        return factory;
    }
    
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }
}

// Utility class for RabbitMQ operations
package com.study.collect.infrastructure.mq.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMQUtil {
    
    private final RabbitTemplate rabbitTemplate;
    
    public void sendMessage(String exchange, String routingKey, Object message) {
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, message);
        } catch (Exception e) {
            log.error("Failed to send message to exchange: {}, routingKey: {}", exchange, routingKey, e);
            throw e;
        }
    }
    
    public void sendDelayMessage(String exchange, String routingKey, Object message, int delayMs) {
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, message, msg -> {
                MessageProperties props = msg.getMessageProperties();
                props.setDelay(delayMs);
                return msg;
            });
        } catch (Exception e) {
            log.error("Failed to send delay message to exchange: {}, routingKey: {}", exchange, routingKey, e);
            throw e;
        }
    }
    
    public <T> T receiveMessage(String queueName, Class<T> clazz) {
        try {
            Message message = rabbitTemplate.receive(queueName);
            if (message != null) {
                return rabbitTemplate.getMessageConverter().fromMessage(message, clazz);
            }
            return null;
        } catch (Exception e) {
            log.error("Failed to receive message from queue: {}", queueName, e);
            throw e;
        }
    }
}