好的,我来给出完整的 RabbitMQ 修正配置:

```java
package com.study.collect.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.DefaultMessagePropertiesConverter;
import org.springframework.amqp.support.converter.MessagePropertiesConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class RabbitConfig {

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
        // 开启发布确认
        connectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
        // 开启发布返回
        connectionFactory.setPublisherReturns(true);
        return connectionFactory;
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public MessagePropertiesConverter messagePropertiesConverter() {
        return new DefaultMessagePropertiesConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        template.setMandatory(true);
        
        // 消息发送确认回调
        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                log.error("Message send failed: {}", cause);
            }
        });

        // 消息返回回调
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

    // 队列定义
    @Bean
    public Queue dataCollectQueue() {
        return QueueBuilder.durable("data.collect.queue")
                .withArgument("x-dead-letter-exchange", "data.collect.dlx")
                .withArgument("x-dead-letter-routing-key", "data.collect.dead")
                .build();
    }

    @Bean
    public Queue dataCollectDeadQueue() {
        return QueueBuilder.durable("data.collect.dead.queue").build();
    }

    // 交换机定义
    @Bean
    public DirectExchange dataCollectExchange() {
        return ExchangeBuilder.directExchange("data.collect.exchange")
                .durable(true)
                .build();
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return ExchangeBuilder.directExchange("data.collect.dlx")
                .durable(true)
                .build();
    }

    // 绑定关系定义
    @Bean
    public Binding bindingDataCollect() {
        return BindingBuilder
                .bind(dataCollectQueue())
                .to(dataCollectExchange())
                .with("data.collect");
    }

    @Bean
    public Binding bindingDeadLetter() {
        return BindingBuilder
                .bind(dataCollectDeadQueue())
                .to(deadLetterExchange())
                .with("data.collect.dead");
    }

    // 消息监听容器配置
    @Bean
    public SimpleMessageListenerContainer messageListenerContainer(ConnectionFactory connectionFactory, 
            MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames("data.collect.queue");
        container.setConcurrentConsumers(3);
        container.setMaxConcurrentConsumers(10);
        container.setPrefetchCount(1);
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        container.setMessageListener(listenerAdapter);
        container.setMessagePropertiesConverter(messagePropertiesConverter());
        return container;
    }

    @Bean
    public MessageListenerAdapter listenerAdapter(MessageHandler messageHandler) {
        MessageListenerAdapter adapter = new MessageListenerAdapter(messageHandler);
        adapter.setMessageConverter(messageConverter());
        return adapter;
    }
}

```

然后创建一个消息处理器:

```java
package com.study.collect.handler;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MessageHandler implements ChannelAwareMessageListener {
    
    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        try {
            // 获取消息ID
            long deliveryTag = message.getMessageProperties().getDeliveryTag();
            
            // 获取消息内容
            String msg = new String(message.getBody());
            log.info("Received message: {}", msg);
            
            // 处理消息
            processMessage(msg);
            
            // 手动确认消息
            channel.basicAck(deliveryTag, false);
            
        } catch (Exception e) {
            log.error("Error processing message", e);
            // 消息处理失败，拒绝消息并重新入队
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }
    }
    
    private void processMessage(String message) {
        // 实现具体的消息处理逻辑
    }
}

```

同时需要确保 application.yml 中有正确的 RabbitMQ 配置:

```yaml
spring:
  rabbitmq:
    host: 192.168.80.137
    port: 5672
    username: admin
    password: 123456
    virtual-host: /
    # 消息发送确认
    publisher-confirm-type: correlated
    publisher-returns: true
    template:
      mandatory: true
    # 消费者配置
    listener:
      simple:
        acknowledge-mode: manual
        concurrency: 3
        max-concurrency: 10 
        prefetch: 1

```

这个配置提供了:

1. 完整的连接工厂配置
2. 消息转换器设置
3. 队列和交换机声明
4. 死信队列支持
5. 手动确认机制
6. 并发消费者配置
7. 异常处理机制

使用时只需要注入 RabbitTemplate 即可发送消息:

```java
@Autowired
private RabbitTemplate rabbitTemplate;

public void sendMessage(Object message) {
    rabbitTemplate.convertAndSend("data.collect.exchange", "data.collect", message);
}
```