package com.study.collect.core.mq.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Value("${mq.task.exchange}")
    private String taskExchange;

    @Value("${mq.task.queue}")
    private String taskQueue;

    @Value("${mq.task.routing-key}")
    private String taskRoutingKey;

    @Value("${mq.result.exchange}")
    private String resultExchange;

    @Value("${mq.result.queue}")
    private String resultQueue;

    @Value("${mq.result.routing-key}")
    private String resultRoutingKey;

    // 任务交换机
    @Bean
    public DirectExchange taskExchange() {
        return new DirectExchange(taskExchange);
    }

    // 任务队列
    @Bean
    public Queue taskQueue() {
        return QueueBuilder.durable(taskQueue)
                .withArgument("x-dead-letter-exchange", taskExchange + ".dlx")
                .withArgument("x-dead-letter-routing-key", taskRoutingKey + ".dlx")
                .build();
    }

    // 任务绑定关系
    @Bean
    public Binding taskBinding() {
        return BindingBuilder.bind(taskQueue())
                .to(taskExchange())
                .with(taskRoutingKey);
    }

    // 结果交换机
    @Bean
    public DirectExchange resultExchange() {
        return new DirectExchange(resultExchange);
    }

    // 结果队列
    @Bean
    public Queue resultQueue() {
        return QueueBuilder.durable(resultQueue)
                .withArgument("x-dead-letter-exchange", resultExchange + ".dlx")
                .withArgument("x-dead-letter-routing-key", resultRoutingKey + ".dlx")
                .build();
    }

    // 结果绑定关系
    @Bean
    public Binding resultBinding() {
        return BindingBuilder.bind(resultQueue())
                .to(resultExchange())
                .with(resultRoutingKey);
    }

    // 消息转换器
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // RabbitTemplate配置
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
