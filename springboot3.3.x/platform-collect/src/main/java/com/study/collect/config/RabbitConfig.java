package com.study.collect.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableRabbit
public class RabbitConfig {

    @Value("${spring.rabbitmq.host}")
    private String host;

    @Value("${spring.rabbitmq.port}")
    private int port;

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;

    private static final String CRAWLER_EXCHANGE = "crawler.exchange";
    private static final String CRAWLER_TASK_QUEUE = "crawler.task.queue";
    private static final String CRAWLER_RETRY_QUEUE = "crawler.retry.queue";
    private static final String CRAWLER_TASK_ROUTING_KEY = "crawler.task";
    private static final String CRAWLER_RETRY_ROUTING_KEY = "crawler.retry";

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        // 启用发布确认
        connectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
        // 启用发布返回
        connectionFactory.setPublisherReturns(true);
        return connectionFactory;
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();

        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(1000);
        backOffPolicy.setMultiplier(2.0);
        backOffPolicy.setMaxInterval(10000);
        retryTemplate.setBackOffPolicy(backOffPolicy);

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(3);
        retryTemplate.setRetryPolicy(retryPolicy);

        return retryTemplate;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        // 消息发送失败返回监听
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            System.out.println("消息发送失败：" + message + ", replyCode: " + replyCode + ", replyText: " + replyText);
        });
        // 消息确认监听
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                System.out.println("消息发送失败：" + cause);
            }
        });
        return rabbitTemplate;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        // 设置并发消费者数量
        factory.setConcurrentConsumers(3);
        factory.setMaxConcurrentConsumers(10);
        // 设置预取数量
        factory.setPrefetchCount(1);
        // 设置重试策略
        factory.setRetryTemplate(retryTemplate());
        return factory;
    }

    @Bean
    public TopicExchange crawlerExchange() {
        return new TopicExchange(CRAWLER_EXCHANGE);
    }

    @Bean
    public Queue crawlerTaskQueue() {
        return QueueBuilder.durable(CRAWLER_TASK_QUEUE)
                .build();
    }

    @Bean
    public Queue crawlerRetryQueue() {
        Map<String, Object> args = new HashMap<>();
        // 设置死信交换机
        args.put("x-dead-letter-exchange", CRAWLER_EXCHANGE);
        // 设置死信路由键
        args.put("x-dead-letter-routing-key", CRAWLER_TASK_ROUTING_KEY);
        // 设置消息过期时间（30秒）
        args.put("x-message-ttl", 30000);
        return QueueBuilder.durable(CRAWLER_RETRY_QUEUE)
                .withArguments(args)
                .build();
    }

    @Bean
    public Binding bindingCrawlerTask() {
        return BindingBuilder
                .bind(crawlerTaskQueue())
                .to(crawlerExchange())
                .with(CRAWLER_TASK_ROUTING_KEY);
    }

    @Bean
    public Binding bindingCrawlerRetry() {
        return BindingBuilder
                .bind(crawlerRetryQueue())
                .to(crawlerExchange())
                .with(CRAWLER_RETRY_ROUTING_KEY);
    }
}