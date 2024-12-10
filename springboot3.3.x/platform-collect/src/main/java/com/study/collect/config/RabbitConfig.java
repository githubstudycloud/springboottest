package com.study.collect.config;

import com.study.collect.listener.RabbitMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class RabbitConfig {

    // 测试用的交换机、队列和路由键名称
    private static final String TEST_EXCHANGE = "test.exchange";
    private static final String TEST_QUEUE = "test.queue";
    private static final String TEST_ROUTING_KEY = "test.message";
    // 数据采集用的交换机、队列和路由键名称
    private static final String DATA_EXCHANGE = "data.collect.exchange";
    private static final String DATA_QUEUE = "data.collect.queue";
    private static final String DATA_ROUTING_KEY = "data.collect";
    private static final String DEAD_LETTER_EXCHANGE = "data.collect.dlx";
    private static final String DEAD_LETTER_QUEUE = "data.collect.dead.queue";
    private static final String DEAD_LETTER_ROUTING_KEY = "data.collect.dead";
    @Value("${spring.rabbitmq.host}")
    private String host;
    @Value("${spring.rabbitmq.port}")
    private int port;
    @Value("${spring.rabbitmq.username}")
    private String username;
    @Value("${spring.rabbitmq.password}")
    private String password;
    @Value("${spring.rabbitmq.virtual-host}")
    private String virtualHost;

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(virtualHost);
        connectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
        connectionFactory.setPublisherReturns(true);
        return connectionFactory;
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        template.setMandatory(true);

        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                log.error("Message send failed: {}", cause);
            }
        });

        template.setReturnsCallback(returned -> {
            log.error("Message returned: {}, replyText: {}",
                    returned.getMessage(), returned.getReplyText());
        });

        return template;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    // 测试相关的Bean定义
    @Bean
    public DirectExchange testExchange() {
        return new DirectExchange(TEST_EXCHANGE);
    }

    @Bean
    public Queue testQueue() {
//        return new Queue(TEST_QUEUE);
        return new Queue("test.message");
    }

    @Bean
    public Binding testBinding() {
        return BindingBuilder.bind(testQueue())
                .to(testExchange())
                .with(TEST_ROUTING_KEY);
    }

    // 数据采集相关的Bean定义
    @Bean
    public Queue dataCollectQueue() {
        return QueueBuilder.durable(DATA_QUEUE)
                .withArgument("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DEAD_LETTER_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue dataCollectDeadQueue() {
        return QueueBuilder.durable(DEAD_LETTER_QUEUE).build();
    }

    @Bean
    public DirectExchange dataCollectExchange() {
        return new DirectExchange(DATA_EXCHANGE);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DEAD_LETTER_EXCHANGE);
    }

    @Bean
    public Binding dataCollectBinding() {
        return BindingBuilder.bind(dataCollectQueue())
                .to(dataCollectExchange())
                .with(DATA_ROUTING_KEY);
    }

    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(dataCollectDeadQueue())
                .to(deadLetterExchange())
                .with(DEAD_LETTER_ROUTING_KEY);
    }

    @Bean
    public SimpleMessageListenerContainer messageListenerContainer(ConnectionFactory connectionFactory, RabbitMessageListener rabbitMessageListener) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setQueueNames(DATA_QUEUE, TEST_ROUTING_KEY);
        container.setMessageListener(rabbitMessageListener); // 设置消息监听器
        container.setConcurrentConsumers(3);
        container.setMaxConcurrentConsumers(10);
        container.setPrefetchCount(1);
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return container;
    }

}
