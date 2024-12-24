package com.study.collect.core.mq.config;

import jakarta.annotation.Resource;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MQProperties.class)
public class RabbitConfig {

    @Resource
    private MQProperties mqProperties;

    @Bean
    public DirectExchange taskExchange() {
        return new DirectExchange(mqProperties.getRabbit().getTask().getExchange());
    }

    @Bean
    public Queue taskQueue() {
        MQProperties.RabbitMQ.Queue taskQueue = mqProperties.getRabbit().getTask();
        return QueueBuilder.durable(taskQueue.getQueue())
                .withArgument("x-dead-letter-exchange", taskQueue.getExchange() + ".dlx")
                .withArgument("x-dead-letter-routing-key", taskQueue.getRoutingKey() + ".dlx")
                .build();
    }

    @Bean
    public Binding taskBinding(Queue taskQueue, DirectExchange taskExchange, MQProperties properties) {
        return BindingBuilder.bind(taskQueue)
                .to(taskExchange)
                .with(properties.getRabbit().getTask().getRoutingKey());
    }

    @Bean
    public DirectExchange resultExchange(MQProperties properties) {
        return new DirectExchange(properties.getRabbit().getResult().getExchange());
    }

    @Bean
    public Queue resultQueue(MQProperties properties) {
        return QueueBuilder.durable(properties.getRabbit().getResult().getQueue())
                .build();
    }

    @Bean
    public Binding resultBinding(Queue resultQueue, DirectExchange resultExchange, MQProperties properties) {
        return BindingBuilder.bind(resultQueue)
                .to(resultExchange)
                .with(properties.getRabbit().getResult().getRoutingKey());
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(new Jackson2JsonMessageConverter());
        return template;
    }
}