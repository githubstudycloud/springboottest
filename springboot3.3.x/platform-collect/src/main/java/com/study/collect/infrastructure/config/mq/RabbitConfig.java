package com.study.collect.infrastructure.config.mq;

// RabbitMQ配置
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ配置
 */
@Slf4j
@Configuration
public class RabbitConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.rabbitmq")
    public RabbitProperties rabbitProperties() {
        return new RabbitProperties();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(new Jackson2JsonMessageConverter());
        // 消息发送确认回调
        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                // 处理发送失败
                log.error("Message send failed: {}", cause);
            }
        });
        // 消息返回回调
        template.setReturnsCallback(returned -> {
            log.error("Message returned: {}", returned);
        });
        return template;
    }

    @Bean
    public Queue taskQueue() {
        return QueueBuilder.durable("collect.task.queue")
                .withArgument("x-dead-letter-exchange", "collect.task.dlx")
                .withArgument("x-dead-letter-routing-key", "collect.task.dlq")
                .build();
    }

    @Bean
    public Queue resultQueue() {
        return QueueBuilder.durable("collect.result.queue")
                .withArgument("x-dead-letter-exchange", "collect.result.dlx")
                .withArgument("x-dead-letter-routing-key", "collect.result.dlq")
                .build();
    }

    @Bean
    public DirectExchange taskExchange() {
        return new DirectExchange("collect.task");
    }

    @Bean
    public DirectExchange resultExchange() {
        return new DirectExchange("collect.result");
    }

    @Bean
    public Binding taskBinding() {
        return BindingBuilder.bind(taskQueue())
                .to(taskExchange())
                .with("collect.task");
    }

    @Bean
    public Binding resultBinding() {
        return BindingBuilder.bind(resultQueue())
                .to(resultExchange())
                .with("collect.result");
    }

    @Data
    public static class RabbitProperties {
        private String host;
        private Integer port;
        private String username;
        private String password;
        private String virtualHost;
    }
}
//
///**
// * 消息队列配置
// */
//@Configuration
//@EnableRabbit
//public class RabbitConfig {
//
//    @Bean
//    public Queue taskQueue() {
//        return QueueBuilder.durable("collect.task.queue")
//                .withArgument("x-dead-letter-exchange", "collect.task.dlx")
//                .withArgument("x-dead-letter-routing-key", "collect.task.dlq")
//                .withArgument("x-message-ttl", 3600000) // 1小时过期
//                .build();
//    }
//
//    @Bean
//    public Queue resultQueue() {
//        return QueueBuilder.durable("collect.result.queue")
//                .withArgument("x-dead-letter-exchange", "collect.result.dlx")
//                .withArgument("x-dead-letter-routing-key", "collect.result.dlq")
//                .build();
//    }
//
//    @Bean
//    public DirectExchange taskExchange() {
//        return new DirectExchange("collect.task");
//    }
//
//    @Bean
//    public DirectExchange resultExchange() {
//        return new DirectExchange("collect.result");
//    }
//
//    @Bean
//    public Binding taskBinding(Queue taskQueue, DirectExchange taskExchange) {
//        return BindingBuilder.bind(taskQueue)
//                .to(taskExchange)
//                .with("collect.task");
//    }
//
//    @Bean
//    public Binding resultBinding(Queue resultQueue, DirectExchange resultExchange) {
//        return BindingBuilder.bind(resultQueue)
//                .to(resultExchange)
//                .with("collect.result");
//    }
//}

