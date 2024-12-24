package com.study.collect.core.mq.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Value("${collect.mq.rabbit.task.exchange}")
    private String taskExchange;

    @Value("${collect.mq.rabbit.task.queue}")
    private String taskQueue;

    @Value("${collect.mq.rabbit.task.routing-key}")
    private String taskRoutingKey;

    @Bean
    public DirectExchange taskExchange() {
        return new DirectExchange(taskExchange);
    }

    @Bean
    public Queue taskQueue() {
        return QueueBuilder.durable(taskQueue)
                .withArgument("x-dead-letter-exchange", taskExchange + ".dlx")
                .withArgument("x-dead-letter-routing-key", taskRoutingKey + ".dlx")
                .build();
    }

    @Bean
    public Binding taskBinding() {
        return BindingBuilder.bind(taskQueue())
                .to(taskExchange())
                .with(taskRoutingKey);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(new Jackson2JsonMessageConverter());
        return template;
    }
}
