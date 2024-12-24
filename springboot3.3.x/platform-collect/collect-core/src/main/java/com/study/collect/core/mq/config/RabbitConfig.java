package com.study.collect.core.mq.config;

// RabbitMQ配置

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MQProperties.class)
public class RabbitConfig {

    @Bean
    public DirectExchange taskExchange(MQProperties properties) {
        return new DirectExchange(properties.getRabbit().getTask().getExchange());
    }

    @Bean
    public Queue taskQueue(MQProperties properties) {
        return QueueBuilder.durable(properties.getRabbit().getTask().getQueue())
                .withArgument("x-dead-letter-exchange", properties.getRabbit().getTask().getExchange() + ".dlx")
                .withArgument("x-dead-letter-routing-key", properties.getRabbit().getTask().getRoutingKey() + ".dlx")
                .build();
    }

    @Bean
    public Binding taskBinding(Queue taskQueue, DirectExchange taskExchange, MQProperties properties) {
        return BindingBuilder.bind(taskQueue)
                .to(taskExchange)
                .with(properties.getRabbit().getTask().getRoutingKey());
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(new Jackson2JsonMessageConverter());
        return template;
    }
}