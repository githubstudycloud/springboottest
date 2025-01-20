# Project Structure

```
collect-core/
    pom.xml
    src/
        main/
            java/
                com/
                    study/
                        collect/
                            core/
                                collector/
                                    AbstractCollector.java
                                    ICollector.java
                                    package-info.java
                                    annotation/
                                        Collector.java
                                    config/
                                        CollectorConfiguration.java
                                        CollectorProperties.java
                                    exception/
                                        CollectException.java
                                    factory/
                                        CollectorFactory.java
                                    manager/
                                        CollectorManager.java
                                    model/
                                        CollectContext.java
                                        CollectResult.java
                                config/
                                    CollectAutoConfiguration.java
                                    package-info.java
                                mq/
                                    RabbitMQErrorHandler.java
                                    config/
                                        MQProperties.java
                                        RabbitConfig.java
                                    consumer/
                                        TaskConsumer.java
                                    message/
                                        BaseMessage.java
                                        TaskMessage.java
                                        TaskResultMessage.java
                                    producer/
                                        TaskProducer.java
                                processor/
                                    AbstractProcessor.java
                                    IProcessor.java
                                    package-info.java
                                    annotation/
                                        Processor.java
                                    config/
                                        ProcessorConfiguration.java
                                        ProcessorProperties.java
                                    exception/
                                        ProcessException.java
                                    manager/
                                        ProcessorManager.java
                                    model/
                                        ProcessChain.java
                                        ProcessContext.java
                                storage/
                                    annotation/
                                        Repository.java
                                    audit/
                                        EntityAuditor.java
                                    cache/
                                        package-info.java
                                        annotation/
                                            Cache.java
                                            CacheEvict.java
                                            CacheLock.java
                                        aspect/
                                            CacheAspect.java
                                            LockAspect.java
                                        config/
                                            CacheAutoConfiguration.java
                                            CacheProperties.java
                                        lock/
                                            DistributedLock.java
                                            package-info.java
                                            RedisLock.java
                                        manager/
                                            CacheManager.java
                                            RedisCacheManager.java
                                        model/
                                            CacheOptions.java
                                    config/
                                        MongoConfig.java
                                    entity/
                                        BaseEntity.java
                                        VersionEntity.java
                                    event/
                                        DefaultEntityEventHandler.java
                                        EntityEvent.java
                                        EntityEventListener.java
                                        impl/
                                            EntityEvents.java
                                    repository/
                                        BaseMongoRepository.java
                                        IRepository.java
                                        VersionRepository.java
                                        factory/
                                            CustomMongoRepositoryFactory.java
                                            CustomMongoRepositoryFactoryBean.java
                                task/
                                    package-info.java
                                    config/
                                        MyBatisConfig.java
                                        SchedulerConfiguration.java
                                        TaskConfiguration.java
                                    definition/
                                        TaskProperties.java
                                    entity/
                                        TaskConfig.java
                                        TaskInstance.java
                                        TaskLog.java
                                    enums/
                                        LogTypeEnum.java
                                        TaskStatusEnum.java
                                    exception/
                                        TaskValidationException.java
                                    handler/
                                        AbstractTaskHandler.java
                                        SampleTaskHandler.java
                                        TaskHandler.java
                                        TaskHandlerManager.java
                                    mapper/
                                        TaskConfigMapper.java
                                        TaskInstanceMapper.java
                                        TaskLogMapper.java
                                    model/
                                        package-info.java
                                        ShardingConfig.java
                                        TaskContext.java
                                        TaskDefinition.java
                                        TaskResult.java
                                        TaskStatus.java
                                    scheduler/
                                        AbstractTaskScheduler.java
                                        DefaultTaskScheduler.java
                                        package-info.java
                                        TaskDispatcher.java
                                        TaskScheduler.java
                                    service/
                                        TaskConfigService.java
                                        TaskExecuteService.java
                                    utils/
                                        InstanceIdGenerator.java
            resources/
                mapper/
                    TaskConfigMapper.xml
                    TaskInstanceMapper.xml
                    TaskLogMapper.xml
```

# File Contents

## pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xmlns="http://maven.apache.org/POM/4.0.0"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.study</groupId>
        <artifactId>platform-collect</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <artifactId>collect-core</artifactId>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <!-- Spring Boot -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-aop</artifactId>
        </dependency>

        <!-- Redis -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
        <dependency>
            <groupId>org.redisson</groupId>
            <artifactId>redisson</artifactId>
        </dependency>

        <!-- MongoDB -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-mongodb</artifactId>
        </dependency>

        <!-- RabbitMQ -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-amqp</artifactId>
        </dependency>

        <!-- MariaDB JDBC Driver -->
        <dependency>
            <groupId>org.mariadb.jdbc</groupId>
            <artifactId>mariadb-java-client</artifactId>
        </dependency>

        <!-- Metrics -->
        <dependency>
            <groupId>io.micrometer</groupId>
            <artifactId>micrometer-registry-prometheus</artifactId>
        </dependency>

        <!-- Common -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
        </dependency>
        <dependency>
            <groupId>com.google.guava</groupId>
            <artifactId>guava</artifactId>
        </dependency>

        <!-- MyBatis -->
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>

        </dependency>
        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis</artifactId>
            <version>3.5.9</version>
            <scope>compile</scope>
        </dependency>

        <!-- Test -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.datatype</groupId>
            <artifactId>jackson-datatype-jsr310</artifactId>
        </dependency>
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-pool2</artifactId>
        </dependency>
    </dependencies>
</project>
```

## AbstractCollector.java

```java
package com.study.collect.core.collector;

import com.study.collect.core.collector.exception.CollectException;
import com.study.collect.core.collector.model.CollectContext;
import com.study.collect.core.collector.model.CollectResult;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public abstract class AbstractCollector<T, R> implements ICollector<T, R> {

    @Override
    public CollectResult<R> collect(CollectContext<T> context) {
        String taskId = context.getTaskId();
        log.info("开始执行采集任务: taskId={}, type={}", taskId, getType());

        try {
            // 前置处理
            preProcess(context);

            // 执行采集
//            CollectResult<R> data = doCollect(context);
            R sourceData = doCollect(context);
            CollectResult<R> data = CollectResult.success(sourceData);
            // 后置处理
            postProcess(data);

            log.info("采集任务执行完成: taskId={}", taskId);
            return CollectResult.success(data.getData());

        } catch (Exception e) {
            log.error("采集任务执行失败: taskId={}", taskId, e);
            return CollectResult.failure(e.getMessage());
        }
    }

    /**
     * 前置处理
     */
    protected void preProcess(CollectContext<T> context) {
        // 数据校验
        validateContext(context);
        // 准备采集参数
        prepareCollectParams(context);
    }

    /**
     * 执行采集
     */
//    protected abstract CollectResult<R> doCollect(CollectContext<T> context);
    protected abstract R doCollect(CollectContext<T> context);

    /**
     * 后置处理
     */
    protected void postProcess(CollectResult<R> data) {
        // 数据清洗
        cleanCollectData(data);
        // 结果验证
        validateCollectResult(data);
    }

    /**
     * 上下文校验
     */
    protected void validateContext(CollectContext<T> context) {
        if (context == null) {
            throw new CollectException("采集上下文不能为空");
        }
        if (context.getTaskId() == null) {
            throw new CollectException("任务ID不能为空");
        }
        if (context.getParams() == null) {
            throw new CollectException("采集参数不能为空");
        }
    }

    /**
     * 准备采集参数
     */
    protected void prepareCollectParams(CollectContext<T> context) {
        // 子类可覆盖实现具体的参数准备逻辑
    }

    /**
     * 清洗采集数据
     */
    protected void cleanCollectData(CollectResult<R> data) {
        // 子类可覆盖实现具体的数据清洗逻辑
    }

    /**
     * 验证采集结果
     */
    protected void validateCollectResult(CollectResult<R> data) {
        // 子类可覆盖实现具体的结果验证逻辑
    }
}
```

## ICollector.java

```java
package com.study.collect.core.collector;

import com.study.collect.core.collector.model.CollectContext;
import com.study.collect.core.collector.model.CollectResult;

// 采集器接口
public interface ICollector<T, R> {
    /**
     * 执行采集
     */
    CollectResult<R> collect(CollectContext<T> context);

    /**
     * 获取采集器类型
     */
    String getType();


}

```

## package-info.java

```java
/**
 * 采集器
 */
package com.study.collect.core.collector;
```

## Collector.java

```java
package com.study.collect.core.collector.annotation;

import java.lang.annotation.*;

// 采集器注解
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Collector {
    /**
     * 采集器类型
     */
    String type();

    /**
     * 采集器描述
     */
    String description() default "";

    /**
     * 是否启用
     */
    boolean enabled() default true;

    /**
     * 采集器优先级,值越小优先级越高
     */
    int order() default Integer.MAX_VALUE;
}

```

## CollectorConfiguration.java

```java
package com.study.collect.core.collector.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.study.collect.core.collector")
public class CollectorConfiguration {
    // 采集器模块配置
}
```

## CollectorProperties.java

```java
package com.study.collect.core.collector.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "collect.collector")
public class CollectorProperties {
    /**
     * 是否启用采集器
     */
    private boolean enabled = true;

    /**
     * 采集超时时间(秒)
     */
    private int timeout = 300;

    /**
     * 重试次数
     */
    private int retryTimes = 3;

    /**
     * 重试间隔(秒)
     */
    private int retryInterval = 60;
}
```

## CollectException.java

```java
package com.study.collect.core.collector.exception;

public class CollectException extends RuntimeException {

    public CollectException(String message) {
        super(message);
    }

    public CollectException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

## CollectorFactory.java

```java
package com.study.collect.core.collector.factory;

import com.study.collect.core.collector.ICollector;
import com.study.collect.core.collector.manager.CollectorManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class CollectorFactory {

    private final CollectorManager collectorManager;

    /**
     * 创建或获取采集器实例
     * 首先尝试从CollectorManager获取已注册的采集器
     * 如果找不到对应的采集器，抛出异常
     */
    public <T, R> ICollector<T, R> createCollector(String type) {
        // 验证参数
        if (!StringUtils.hasText(type)) {
            throw new IllegalArgumentException("采集器类型不能为空");
        }

        try {
            // 从CollectorManager获取已注册的采集器
            return collectorManager.getCollector(type);
        } catch (IllegalStateException e) {
            log.error("创建采集器失败: {}", e.getMessage());
            throw new IllegalArgumentException("无效的采集器类型: " + type);
        }
    }

    /**
     * 检查是否支持指定类型的采集器
     */
    public boolean supportsCollectorType(String type) {
        return collectorManager.hasCollector(type);
    }

    /**
     * 获取所有支持的采集器类型
     */
    public Set<String> getSupportedCollectorTypes() {
        return collectorManager.getCollectorTypes();
    }
}
```

## CollectorManager.java

```java
package com.study.collect.core.collector.manager;

import com.study.collect.core.collector.ICollector;
import com.study.collect.core.collector.annotation.Collector;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class CollectorManager {

    private final Map<String, ICollector<?, ?>> collectors = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        registerCollectors();
    }

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 扫描并注册所有带有@Collector注解的采集器
     */
    private void registerCollectors() {
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(Collector.class);
        beans.values().forEach(bean -> {
            Collector annotation = bean.getClass().getAnnotation(Collector.class);
            if (null != annotation && annotation.enabled()) {
                ICollector<?, ?> collector = (ICollector<?, ?>) bean;
                registerCollector(collector.getType(), collector);
            }
        });
    }

    /**
     * 注册单个采集器
     */
    public void registerCollector(String type, ICollector<?, ?> collector) {
        if (collectors.containsKey(type)) {
            throw new IllegalStateException("采集器类型已存在: " + type);
        }
        collectors.put(type, collector);
        log.info("注册采集器: type={}, class={}", type, collector.getClass().getName());
    }

    /**
     * 检查采集器是否已注册
     */
    public boolean hasCollector(String type) {
        return collectors.containsKey(type);
    }

    /**
     * 获取已注册的采集器
     * 如果采集器未注册，抛出异常
     */
    @SuppressWarnings("unchecked")
    public <T, R> ICollector<T, R> getCollector(String type) {
        ICollector<?, ?> collector = collectors.get(type);
        if (collector == null) {
            throw new IllegalStateException("采集器未注册: " + type);
        }
        return (ICollector<T, R>) collector;
    }

    /**
     * 获取所有已注册的采集器类型
     */
    public Set<String> getCollectorTypes() {
        return Collections.unmodifiableSet(collectors.keySet());
    }
}
```

## CollectContext.java

```java
package com.study.collect.core.collector.model;

import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class CollectContext<T> {
    /**
     * 上下文属性
     */
    private final Map<String, Object> attributes = new ConcurrentHashMap<>();
    /**
     * 任务ID
     */
    private String taskId;
    /**
     * 采集参数
     */
    private T params;
    /**
     * 分片信息
     */
    private Integer shardingId;
    private Integer shardingTotal;

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <V> V getAttribute(String key) {
        return (V) attributes.get(key);
    }
}

```

## CollectResult.java

```java
package com.study.collect.core.collector.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CollectResult<T> {
    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 采集数据
     */
    private T data;

    /**
     * 完成时间
     */
    private LocalDateTime finishTime;

    public static <T> CollectResult<T> success(T data) {
        CollectResult<T> result = new CollectResult<>();
        result.setSuccess(true);
        result.setData(data);
        result.setFinishTime(LocalDateTime.now());
        return result;
    }

    public static <T> CollectResult<T> failure(String errorMessage) {
        CollectResult<T> result = new CollectResult<>();
        result.setSuccess(false);
        result.setErrorMessage(errorMessage);
        result.setFinishTime(LocalDateTime.now());
        return result;
    }
}
```

## CollectAutoConfiguration.java

```java
package com.study.collect.core.config;

import com.study.collect.core.collector.config.CollectorConfiguration;
import com.study.collect.core.mq.config.MQProperties;
import com.study.collect.core.mq.config.RabbitConfig;
import com.study.collect.core.processor.config.ProcessorConfiguration;
import com.study.collect.core.storage.cache.config.CacheAutoConfiguration;
import com.study.collect.core.storage.config.MongoConfig;
import com.study.collect.core.task.config.MyBatisConfig;
import com.study.collect.core.task.config.TaskConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableConfigurationProperties({
        MQProperties.class
})
@Import({
        // 数据存储配置
        MongoConfig.class,          // MongoDB
        MyBatisConfig.class,        // MyBatis
        CacheAutoConfiguration.class,// Redis

        // 消息队列配置
        RabbitConfig.class,         // RabbitMQ

        // 业务配置
        TaskConfiguration.class,     // 任务配置
        CollectorConfiguration.class,// 采集器配置
        ProcessorConfiguration.class // 处理器配置
})
public class CollectAutoConfiguration {
}
```

## package-info.java

```java
/**
 * 核心配置包
 */
package com.study.collect.core.config;
```

## RabbitMQErrorHandler.java

```java
package com.study.collect.core.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.stereotype.Component;
import org.springframework.util.ErrorHandler;

@Component
@Slf4j
public class RabbitMQErrorHandler implements ErrorHandler {

    @Override
    public void handleError(Throwable t) {
        log.error("RabbitMQ message processing error", t);

        if (t instanceof MessageConversionException) {
            // 消息转换错误处理
            log.error("Message conversion failed", t);
        } else if (t instanceof ListenerExecutionFailedException) {
            // 监听器执行错误处理
            log.error("Listener execution failed", t);
        }
    }
}
```

## MQProperties.java

```java
package com.study.collect.core.mq.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 消息队列配置属性类
 * 对应配置文件中 collect.mq 前缀的配置项
 */
@Data
@ConfigurationProperties(prefix = "collect.mq")
public class MQProperties {

    /**
     * RabbitMQ相关配置
     */
    private RabbitMQ rabbit = new RabbitMQ();

    @Data
    public static class RabbitMQ {
        /**
         * 服务器地址
         */
        private String host;

        /**
         * 服务器端口
         */
        private Integer port;

        /**
         * 用户名
         */
        private String username;

        /**
         * 密码
         */
        private String password;

        /**
         * 虚拟主机
         */
        private String virtualHost = "/";

        /**
         * 任务队列配置
         */
        private Queue task = new Queue();

        /**
         * 结果队列配置
         */
        private Queue result = new Queue();

        /**
         * 队列配置类
         */
        @Data
        public static class Queue {
            /**
             * 交换机名称
             */
            private String exchange;

            /**
             * 队列名称
             */
            private String queue;

            /**
             * 路由键
             */
            private String routingKey;

            /**
             * 是否持久化
             */
            private boolean durable = true;

            /**
             * 是否自动删除
             */
            private boolean autoDelete = false;
        }
    }
}
```

## RabbitConfig.java

```java
package com.study.collect.core.mq.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "collect.mq.rabbit", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(MQProperties.class)
public class RabbitConfig {

    @Bean
    public DirectExchange taskExchange(MQProperties properties) {
        return new DirectExchange(properties.getRabbit().getTask().getExchange());
    }

    @Bean
    public Queue taskQueue(MQProperties properties) {
        return QueueBuilder.durable(properties.getRabbit().getTask().getQueue())
                .withArgument("x-dead-letter-exchange",
                        properties.getRabbit().getTask().getExchange() + ".dlx")
                .withArgument("x-dead-letter-routing-key",
                        properties.getRabbit().getTask().getRoutingKey() + ".dlx")
                .build();
    }

    @Bean
    public Binding taskBinding(Queue taskQueue, DirectExchange taskExchange,
                               MQProperties properties) {
        return BindingBuilder.bind(taskQueue)
                .to(taskExchange)
                .with(properties.getRabbit().getTask().getRoutingKey());
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        // 配置消息转换器
        Jackson2JsonMessageConverter messageConverter = new Jackson2JsonMessageConverter();
        template.setMessageConverter(messageConverter);
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);

        // 为消费者配置相同的消息转换器
        Jackson2JsonMessageConverter messageConverter = new Jackson2JsonMessageConverter();
        factory.setMessageConverter(messageConverter);

        return factory;
    }
}
```

## TaskConsumer.java

```java
package com.study.collect.core.mq.consumer;

import com.study.collect.core.mq.message.TaskMessage;
import com.study.collect.core.mq.message.TaskResultMessage;
import com.study.collect.core.mq.producer.TaskProducer;
import com.study.collect.core.task.handler.TaskHandler;
import com.study.collect.core.task.handler.TaskHandlerManager;
import com.study.collect.core.task.model.TaskContext;
import com.study.collect.core.task.model.TaskResult;
import com.study.collect.core.task.service.TaskExecuteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class TaskConsumer {

    private final TaskExecuteService taskExecuteService;
    private final TaskProducer taskProducer;
    private final TaskHandlerManager handlerManager;

    @Autowired
    public TaskConsumer(TaskExecuteService taskExecuteService,
                        TaskProducer taskProducer,
                        TaskHandlerManager handlerManager) {
        this.taskExecuteService = taskExecuteService;
        this.taskProducer = taskProducer;
        this.handlerManager = handlerManager;
    }

    @RabbitListener(
            queues = "${collect.mq.rabbit.task.queue}",
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void onTaskMessage(@Payload TaskMessage message) {
        String instanceId = message.getInstanceId();
        try {
            log.info("Received task message: instanceId={}, taskCode={}, shard={}/{}",
                    message.getInstanceId(),
                    message.getTaskId(),
                    message.getShardIndex() + 1,
                    message.getShardTotal()
            );

//    @RabbitListener(queues = "${collect.mq.rabbit.task.queue}")
//    public void onTaskMessage(TaskMessage message) {

//        log.info("Received task message: instanceId={}, taskCode={}, shard={}/{}",
//                instanceId,
//                message.getTaskId(),
//                message.getShardIndex() + 1,
//                message.getShardTotal()
//        );
//
//        try {
            // 执行任务
            Object result = executeTask(message);

            // 发送成功结果
            sendSuccessResult(message, result);

            // 更新任务状态
            taskExecuteService.completeTaskInstance(instanceId, true, null);

            log.info("Task executed successfully: {}", instanceId);

        } catch (Exception e) {
            log.error("Task execution failed: " + instanceId, e);

            // 发送失败结果
            sendFailureResult(message, e.getMessage());

            // 更新任务状态
            taskExecuteService.completeTaskInstance(instanceId, false, e.getMessage());
        }
    }

//    private Object executeTask(TaskMessage message) {
//        // 实际任务执行逻辑
//        // 这里需要根据具体业务实现，可能需要调用具体的TaskHandler
//        return null;
//    }
    private Object executeTask(TaskMessage message) {
        // 创建任务上下文
        TaskContext context = createTaskContext(message);

        // 获取任务处理器
        TaskHandler handler = handlerManager.getHandler(message.getTaskId());

        // 执行任务
        TaskResult result = handler.execute(context);

        if (result.getSuccess()) {
            // 发送成功结果
            sendSuccessResult(message, result.getData());
        } else {
            // 发送失败结果
            sendFailureResult(message, result.getErrorMessage());
            throw new RuntimeException(result.getErrorMessage());
        }


        return result.getData();
    }

    private TaskContext createTaskContext(TaskMessage message) {
        TaskContext context = new TaskContext();
        context.setTaskId(message.getTaskId());
        context.setInstanceId(message.getInstanceId());
        context.setShardIndex(message.getShardIndex());
        context.setShardTotal(message.getShardTotal());
        context.setShardParam(message.getShardParam());
        return context;
    }

    private void sendSuccessResult(TaskMessage message, Object result) {
        TaskResultMessage resultMessage = createResultMessage(message);
        resultMessage.setSuccess(true);
        resultMessage.setResult(result);
        taskProducer.sendResult(resultMessage);
    }

    private void sendFailureResult(TaskMessage message, String errorMsg) {
        TaskResultMessage resultMessage = createResultMessage(message);
        resultMessage.setSuccess(false);
        resultMessage.setErrorMsg(errorMsg);
        taskProducer.sendResult(resultMessage);
    }

    private TaskResultMessage createResultMessage(TaskMessage message) {
        TaskResultMessage resultMessage = new TaskResultMessage();
        resultMessage.setTaskId(message.getTaskId());
        resultMessage.setInstanceId(message.getInstanceId());
        resultMessage.setExecuteHost(message.getHostName());
        resultMessage.setFinishTime(LocalDateTime.now());
        return resultMessage;
    }
}
```

## BaseMessage.java

```java
package com.study.collect.core.mq.message;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 消息基类
 * 定义所有消息共有的属性和行为
 */
@Data
public abstract class BaseMessage implements Serializable {

    /**
     * 消息ID，用于消息追踪
     */
    private String messageId;

    /**
     * 消息类型，用于区分不同消息
     */
    private String type;

    /**
     * 消息创建时间
     */
    private LocalDateTime createTime;

    /**
     * 消息优先级
     */
    private Integer priority;

    /**
     * 消息重试次数
     */
    private Integer retryCount;

    /**
     * 额外属性，用于扩展
     */
    private String properties;

    public BaseMessage() {
        this.messageId = UUID.randomUUID().toString();
        this.createTime = LocalDateTime.now();
        this.retryCount = 0;
    }

    /**
     * 设置消息类型
     * 子类需要在构造函数中调用此方法设置具体的消息类型
     */
    protected void setType(String type) {
        this.type = type;
    }

    /**
     * 增加重试次数
     */
    public void incrementRetryCount() {
        if (this.retryCount == null) {
            this.retryCount = 0;
        }
        this.retryCount++;
    }
}
```

## TaskMessage.java

```java
package com.study.collect.core.mq.message;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class TaskMessage extends BaseMessage {
    private String taskId;           // 任务编码
    private String instanceId;       // 实例ID
    private Integer shardIndex;      // 分片索引
    private Integer shardTotal;      // 分片总数
    private String shardParam;       // 分片参数
    private String hostName;         // 执行机器

    public TaskMessage() {
        super();
        setType("TASK");
    }
}
```

## TaskResultMessage.java

```java
package com.study.collect.core.mq.message;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class TaskResultMessage extends BaseMessage {
    private String taskId;           // 任务编码
    private String instanceId;       // 实例ID
    private String executeHost;      // 执行机器
    private Boolean success;         // 是否成功
    private String errorMsg;         // 错误信息
    private Object result;           // 执行结果
    private LocalDateTime finishTime; // 完成时间

    public TaskResultMessage() {
        super();
        setType("RESULT");
    }
}
```

## TaskProducer.java

```java
package com.study.collect.core.mq.producer;

import com.study.collect.core.mq.config.MQProperties;
import com.study.collect.core.mq.message.TaskMessage;
import com.study.collect.core.mq.message.TaskResultMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TaskProducer {

    private final RabbitTemplate rabbitTemplate;
    private final MQProperties mqProperties;

    @Autowired
    public TaskProducer(RabbitTemplate rabbitTemplate, MQProperties mqProperties) {
        this.rabbitTemplate = rabbitTemplate;
        this.mqProperties = mqProperties;
    }

//    public void sendTask(TaskMessage message) {
//        try {
//            MQProperties.RabbitMQ.Queue taskQueue = mqProperties.getRabbit().getTask();
//            rabbitTemplate.convertAndSend(
//                    taskQueue.getExchange(),
//                    taskQueue.getRoutingKey(),
//                    message
//            );
//            log.info("Task message sent: instanceId={}, taskCode={}, shard={}/{}",
//                    message.getInstanceId(),
//                    message.getTaskId(),
//                    message.getShardIndex() + 1,
//                    message.getShardTotal()
//            );
//        } catch (Exception e) {
//            log.error("Failed to send task message: " + message.getInstanceId(), e);
//            throw new RuntimeException("Message sending failed", e);
//        }
//    }

    public void sendTask(TaskMessage message) {
        try {
            MQProperties.RabbitMQ.Queue taskQueue = mqProperties.getRabbit().getTask();

            // 设置消息属性
            MessageProperties props = new MessageProperties();
            props.setContentType(MessageProperties.CONTENT_TYPE_JSON);
            props.getHeaders().put("__TypeId__", TaskMessage.class.getName());

            Message amqpMessage = rabbitTemplate.getMessageConverter()
                    .toMessage(message, props);

            rabbitTemplate.send(
                    taskQueue.getExchange(),
                    taskQueue.getRoutingKey(),
                    amqpMessage
            );

            log.info("Task message sent: instanceId={}, taskCode={}, shard={}/{}",
                    message.getInstanceId(),
                    message.getTaskId(),
                    message.getShardIndex() + 1,
                    message.getShardTotal()
            );
        } catch (Exception e) {
            log.error("Failed to send task message: " + message.getInstanceId(), e);
            throw new RuntimeException("Message sending failed", e);
        }
    }


    public void sendResult(TaskResultMessage message) {
        try {
            MQProperties.RabbitMQ.Queue resultQueue = mqProperties.getRabbit().getResult();
            rabbitTemplate.convertAndSend(
                    resultQueue.getExchange(),
                    resultQueue.getRoutingKey(),
                    message
            );
            log.info("Result message sent: instanceId={}, taskCode={}, success={}",
                    message.getInstanceId(),
                    message.getTaskId(),
                    message.getSuccess()
            );
        } catch (Exception e) {
            log.error("Failed to send result message: " + message.getInstanceId(), e);
            throw new RuntimeException("Message sending failed", e);
        }
    }
}
```

## AbstractProcessor.java

```java
package com.study.collect.core.processor;

// 抽象处理器

import com.study.collect.core.processor.exception.ProcessException;
import com.study.collect.core.processor.model.ProcessContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

@Slf4j
public abstract class AbstractProcessor<T> implements IProcessor<T> {

    @Override
    public T process(T data, ProcessContext context) {
        String taskId = context.getTaskId();
        log.info("开始数据处理: taskId={}, type={}", taskId, getType());

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            // 前置处理
            preProcess(data, context);

            // 执行处理
            T result = doProcess(data, context);

            // 后置处理
            result = postProcess(result, context);

            stopWatch.stop();
            log.info("数据处理完成: taskId={}, cost={}ms", taskId, stopWatch.getTotalTimeMillis());

            return result;

        } catch (Exception e) {
            log.error("数据处理异常: taskId={}", taskId, e);
            throw new ProcessException("数据处理失败: " + e.getMessage(), e);
        }
    }

    /**
     * 前置处理
     */
    protected void preProcess(T data, ProcessContext context) {
        // 数据校验
        validateData(data);
        // 上下文校验
        validateContext(context);
    }

    /**
     * 执行处理
     */
    protected abstract T doProcess(T data, ProcessContext context);

    /**
     * 后置处理
     */
    protected T postProcess(T result, ProcessContext context) {
        return result;
    }

    /**
     * 数据校验
     */
    protected void validateData(T data) {
        if (data == null) {
            throw new ProcessException("处理数据不能为空");
        }
    }

    /**
     * 上下文校验
     */
    protected void validateContext(ProcessContext context) {
        if (context == null) {
            throw new ProcessException("处理上下文不能为空");
        }
        if (context.getTaskId() == null) {
            throw new ProcessException("任务ID不能为空");
        }
    }
}
```

## IProcessor.java

```java
package com.study.collect.core.processor;

// 处理器接口


import com.study.collect.core.processor.model.ProcessContext;

public interface IProcessor<T> {
    /**
     * 执行数据处理
     *
     * @param data    待处理数据
     * @param context 处理上下文
     * @return 处理后的数据
     */
    T process(T data, ProcessContext context);

    /**
     * 获取处理器类型
     */
    String getType();

    /**
     * 获取处理器执行顺序
     */
    int getOrder();
}

```

## package-info.java

```java
/**
 * 处理器模块
 */
package com.study.collect.core.processor;
```

## Processor.java

```java
package com.study.collect.core.processor.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Processor {
    /**
     * 处理器类型
     */
    String type();

    /**
     * 处理器描述
     */
    String description() default "";

    /**
     * 是否启用
     */
    boolean enabled() default true;

    /**
     * 处理器执行顺序
     */
    int order() default Integer.MAX_VALUE;
}

```

## ProcessorConfiguration.java

```java
package com.study.collect.core.processor.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.study.collect.core.processor")
public class ProcessorConfiguration {
    // 处理器模块配置
}
```

## ProcessorProperties.java

```java
package com.study.collect.core.processor.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "collect.processor")
public class ProcessorProperties {
    /**
     * 是否启用处理器
     */
    private boolean enabled = true;

    /**
     * 处理超时时间(秒)
     */
    private int timeout = 60;

    /**
     * 是否异步处理
     */
    private boolean async = false;

    /**
     * 异步处理线程池大小
     */
    private int poolSize = 5;
}

```

## ProcessException.java

```java
package com.study.collect.core.processor.exception;

public class ProcessException extends RuntimeException {

    public ProcessException(String message) {
        super(message);
    }

    public ProcessException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

## ProcessorManager.java

```java
package com.study.collect.core.processor.manager;

import com.study.collect.core.processor.IProcessor;
import com.study.collect.core.processor.annotation.Processor;
import com.study.collect.core.processor.chain.ProcessChain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class ProcessorManager {

    private final Map<String, IProcessor<?>> processors = new ConcurrentHashMap<>();
    private final Map<String, ProcessChain<?>> chains = new ConcurrentHashMap<>();

    @Autowired
    public void registerProcessors(Map<String, Object> beans) {
        beans.values().stream()
                .filter(bean -> bean.getClass().isAnnotationPresent(Processor.class))
                .forEach(bean -> {
                    Processor annotation = bean.getClass().getAnnotation(Processor.class);
                    if (annotation.enabled()) {
                        IProcessor<?> processor = (IProcessor<?>) bean;
                        processors.put(processor.getType(), processor);
                        log.info("注册处理器: type={}, order={}, class={}",
                                processor.getType(), processor.getOrder(),
                                processor.getClass().getName());
                    }
                });
    }

    @SuppressWarnings("unchecked")
    public <T> ProcessChain<T> createChain() {
        String chainId = UUID.randomUUID().toString();
        ProcessChain<T> chain = new ProcessChain<>(chainId);

        processors.values().stream()
                .map(p -> (IProcessor<T>) p)
                .forEach(chain::addProcessor);

        chains.put(chainId, chain);
        return chain;
    }

    @SuppressWarnings("unchecked")
    public <T> IProcessor<T> getProcessor(String type) {
        IProcessor<?> processor = processors.get(type);
        if (processor == null) {
            throw new IllegalArgumentException("未找到处理器: " + type);
        }
        return (IProcessor<T>) processor;
    }
}
```

## ProcessChain.java

```java
package com.study.collect.core.processor.chain;

import com.study.collect.core.processor.IProcessor;
import com.study.collect.core.processor.model.ProcessContext;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ProcessChain<T> {

    private final List<IProcessor<T>> processors;
    private final String chainId;

    public ProcessChain(String chainId) {
        this.chainId = chainId;
        this.processors = new ArrayList<>();
    }

    public void addProcessor(IProcessor<T> processor) {
        processors.add(processor);
        processors.sort((p1, p2) -> p1.getOrder() - p2.getOrder());
    }

    public T process(T data) {
        ProcessContext context = new ProcessContext();
        context.setChainId(chainId);

        for (IProcessor<T> processor : processors) {
            try {
                data = processor.process(data, context);
                log.debug("处理器执行成功: processor={}, chainId={}",
                        processor.getType(), chainId);
            } catch (Exception e) {
                log.error("处理器执行失败: processor={}, chainId={}",
                        processor.getType(), chainId, e);
                throw e;
            }
        }

        return data;
    }
}
```

## ProcessContext.java

```java
package com.study.collect.core.processor.model;

import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class ProcessContext {
    /**
     * 上下文属性
     */
    private final Map<String, Object> attributes = new ConcurrentHashMap<>();
    /**
     * 任务ID
     */
    private String taskId;
    /**
     * 处理器链ID
     */
    private String chainId;

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key) {
        return (T) attributes.get(key);
    }
}
```

## Repository.java

```java
package com.study.collect.core.storage.annotation;

import java.lang.annotation.*;

// Repository
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Repository {
    /**
     * 仓储类型
     */
    String type();

    /**
     * 存储描述
     */
    String description() default "";
}
```

## EntityAuditor.java

```java
package com.study.collect.core.storage.audit;

import com.study.collect.core.storage.entity.BaseEntity;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class EntityAuditor implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        // 获取当前操作用户，可以从SecurityContext或ThreadLocal中获取
        return Optional.of("system");
    }
}

```

## package-info.java

```java
/**
 * 缓存
 */
package com.study.collect.core.storage.cache;
```

## Cache.java

```java
package com.study.collect.core.storage.cache.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Cache {
    String key() default "";       // 缓存key
    String prefix() default "";    // 前缀
    long expire() default 3600L;   // 过期时间
    TimeUnit timeUnit() default TimeUnit.SECONDS;  // 时间单位
}
```

## CacheEvict.java

```java
package com.study.collect.core.storage.cache.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CacheEvict {
    String key() default "";       // 缓存key
    String prefix() default "";    // 前缀
    boolean allEntries() default false;  // 是否清除所有
    boolean beforeInvocation() default false; // 是否在方法执行前清除
}
```

## CacheLock.java

```java
package com.study.collect.core.storage.cache.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CacheLock {
    String key();                 // 锁key
    String prefix() default "";   // 前缀
    long waitTime() default 3L;   // 等待时间
    long leaseTime() default 10L; // 租约时间
    TimeUnit timeUnit() default TimeUnit.SECONDS;  // 时间单位
}
```

## CacheAspect.java

```java
package com.study.collect.core.storage.cache.aspect;

import com.study.collect.core.storage.cache.annotation.Cache;
import com.study.collect.core.storage.cache.annotation.CacheEvict;
import com.study.collect.core.storage.cache.manager.CacheManager;
import com.study.collect.core.storage.cache.model.CacheOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class CacheAspect {

    private final CacheManager cacheManager;
    private final SpelExpressionParser parser = new SpelExpressionParser();

    /**
     * 缓存切面逻辑
     */
    @Around("@annotation(cache)")
    public Object doCache(ProceedingJoinPoint point, Cache cache) throws Throwable {
        // 解析key
        String key = parseKey(cache.prefix(), cache.key(), point);

        // 获取返回类型
        MethodSignature signature = (MethodSignature) point.getSignature();
        Class<?> returnType = signature.getReturnType();

        // 构造缓存参数
        CacheOptions options = CacheOptions.builder()
                .expiration(cache.expire())
                .timeUnit(cache.timeUnit())
                .build();

        // 调用底层缓存逻辑
        try {
            return getFromCache(point, key, returnType, options);
        } catch (Exception e) {
            log.error("Cache operation failed for key: {}", key, e);
            // 缓存异常时，直接执行原方法
            return point.proceed();
        }
    }

    /**
     * 缓存清理切面逻辑
     */
    @Around("@annotation(cacheEvict)")
    public Object doEvict(ProceedingJoinPoint point, CacheEvict cacheEvict) throws Throwable {
        // 如果 beforeInvocation = true，则先清理再执行，否则先执行再清理
        boolean beforeInvocation = cacheEvict.beforeInvocation();
        if (beforeInvocation) {
            evictCache(cacheEvict, point);
        }
        Object result = point.proceed();
        if (!beforeInvocation) {
            evictCache(cacheEvict, point);
        }
        return result;
    }

    /**
     * 执行具体的缓存清理逻辑
     */
    private void evictCache(CacheEvict cacheEvict, ProceedingJoinPoint point) {
        try {
            if (cacheEvict.allEntries()) {
                cacheManager.removeByPrefix(cacheEvict.prefix());
            } else {
                String key = parseKey(cacheEvict.prefix(), cacheEvict.key(), point);
                cacheManager.remove(key);
            }
        } catch (Exception e) {
            log.error("Failed to evict cache", e);
        }
    }

    /**
     * 解析 SpEL 表达式得到缓存 key
     */
    private String parseKey(String prefix, String key, ProceedingJoinPoint point) {
        if (!StringUtils.hasText(key)) {
            return prefix;
        }
        try {
            EvaluationContext context = new StandardEvaluationContext();
            MethodSignature signature = (MethodSignature) point.getSignature();
            String[] paramNames = signature.getParameterNames();
            Object[] args = point.getArgs();
            for (int i = 0; i < paramNames.length; i++) {
                context.setVariable(paramNames[i], args[i]);
            }
            // 根据 SpEL 表达式计算出的 key
            String parsedKey = parser.parseExpression(key).getValue(context, String.class);
            // 如果 prefix 不为空，则使用 prefix:parsedKey
            return StringUtils.hasText(prefix) ? prefix + ":" + parsedKey : parsedKey;
        } catch (Exception e) {
            log.error("Failed to parse cache key: {}", key, e);
            throw new IllegalArgumentException("Invalid cache key expression", e);
        }
    }

    /**
     * 使用泛型方法，避免不安全的类型转换警告
     */
    @SuppressWarnings("unchecked")
    private <T> T getFromCache(ProceedingJoinPoint point, String key, Class<?> returnType, CacheOptions options) {
        return cacheManager.get(key, (Class<T>) returnType, () -> {
            try {
                return (T) point.proceed();
            } catch (Throwable e) {
                log.error("Method execution failed at: {}", point.getSignature(), e);
                throw new IllegalStateException("Cache method execution failed", e);
            }
        }, options);
    }
}

```

## LockAspect.java

```java
package com.study.collect.core.storage.cache.aspect;

import com.study.collect.core.storage.cache.annotation.CacheLock;
import com.study.collect.core.storage.cache.lock.DistributedLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LockAspect {

    private final DistributedLock lock;
    private final SpelExpressionParser parser = new SpelExpressionParser();

    @Around("@annotation(cacheLock)")
    public Object doLock(ProceedingJoinPoint point, CacheLock cacheLock) throws Throwable {
        String key = parseKey(cacheLock.prefix(), cacheLock.key(), point);

        try {
            boolean locked = lock.tryLock(key,
                    cacheLock.waitTime(),
                    cacheLock.leaseTime(),
                    cacheLock.timeUnit());

            if (!locked) {
                throw new RuntimeException("Failed to acquire lock: " + key);
            }

            return point.proceed();
        } finally {
            lock.unlock(key);
        }
    }


    // key解析实现，与CacheAspect中相同
    private String parseKey(String prefix, String key, ProceedingJoinPoint point) {
        if (key.isEmpty()) {
            return prefix;
        }

        EvaluationContext context = new StandardEvaluationContext();
        MethodSignature signature = (MethodSignature) point.getSignature();
        String[] paramNames = signature.getParameterNames();
        Object[] args = point.getArgs();

        for (int i = 0; i < paramNames.length; i++) {
            context.setVariable(paramNames[i], args[i]);
        }

        String parsedKey = parser.parseExpression(key).getValue(context, String.class);
        return prefix.isEmpty() ? parsedKey : prefix + ":" + parsedKey;
    }
}
```

## CacheAutoConfiguration.java

```java
package com.study.collect.core.storage.cache.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
@ConditionalOnProperty(prefix = "spring.data.redis", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(CacheProperties.class)
public class CacheAutoConfiguration {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory connectionFactory,
            ObjectMapper objectMapper) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 使用Jackson2JsonRedisSerializer作为序列化器
        Jackson2JsonRedisSerializer<Object> serializer =
                new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);

        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }
}

```

## CacheProperties.java

```java
package com.study.collect.core.storage.cache.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.TimeUnit;

@Data
@ConfigurationProperties(prefix = "collect.storage.cache")
public class CacheProperties {
    private boolean enabled = true;
    private long defaultExpiration = 3600L;
    private TimeUnit timeUnit = TimeUnit.SECONDS;

    private Redis redis = new Redis();

    @Data
    public static class Redis {
        private String host;
        private int port;
        private String password;
        private int database = 0;

        private Pool pool = new Pool();

        @Data
        public static class Pool {
            private int maxActive = 8;
            private int maxIdle = 8;
            private int minIdle = 0;
            private long maxWait = -1;
        }
    }
}
```

## DistributedLock.java

```java
package com.study.collect.core.storage.cache.lock;

import java.util.concurrent.TimeUnit;

public interface DistributedLock {
    /**
     * 获取锁
     * @param key 锁的key
     * @param waitTime 等待时间
     * @param leaseTime 租约时间
     * @param unit 时间单位
     * @return 是否获取成功
     */
    boolean tryLock(String key, long waitTime, long leaseTime, TimeUnit unit);

    /**
     * 释放锁
     * @param key 锁的key
     */
    void unlock(String key);
}

```

## package-info.java

```java
/**
 * 分布式锁
 */
package com.study.collect.core.storage.cache.lock;
```

## RedisLock.java

```java
package com.study.collect.core.storage.cache.lock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisLock implements DistributedLock {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean tryLock(String key, long waitTime, long leaseTime, TimeUnit unit) {
        try {
            long startTime = System.currentTimeMillis();
            long waitMillis = unit.toMillis(waitTime);

            do {
                Boolean success = redisTemplate.opsForValue()
                        .setIfAbsent(key, Thread.currentThread().getId(), leaseTime, unit);

                if (Boolean.TRUE.equals(success)) {
                    return true;
                }

                // 等待一段时间后重试
                Thread.sleep(100);
            } while (System.currentTimeMillis() - startTime < waitMillis);

            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    @Override
    public void unlock(String key) {
        try {
            Long threadId = (Long) redisTemplate.opsForValue().get(key);
            if (threadId != null && threadId.equals(Thread.currentThread().getId())) {
                redisTemplate.delete(key);
            }
        } catch (Exception e) {
            log.error("Failed to unlock: {}", key, e);
        }
    }
}
```

## CacheManager.java

```java
package com.study.collect.core.storage.cache.manager;

import com.study.collect.core.storage.cache.model.CacheOptions;
import java.util.Collection;
import java.util.function.Supplier;

/**
 * 缓存管理器接口
 * 提供统一的缓存操作能力，支持自动加载、批量操作和类型安全的数据访问
 */
public interface CacheManager {

    /**
     * 获取缓存，如果不存在则通过supplier加载并缓存
     *
     * @param key 缓存键
     * @param type 返回值类型
     * @param supplier 数据加载器
     * @return 缓存的值
     * @param <T> 值类型
     */
    <T> T get(String key, Class<T> type, Supplier<T> supplier);

    /**
     * 使用自定义选项获取缓存
     *
     * @param key 缓存键
     * @param type 返回值类型
     * @param supplier 数据加载器
     * @param options 缓存选项
     * @return 缓存的值
     * @param <T> 值类型
     */
    <T> T get(String key, Class<T> type, Supplier<T> supplier, CacheOptions options);

    /**
     * 批量获取缓存，如果不存在则通过supplier加载并缓存
     *
     * @param keys 缓存键集合
     * @param type 返回值类型
     * @param supplier 数据加载器
     * @return 缓存值集合
     * @param <T> 值类型
     */
    <T> Collection<T> multiGet(Collection<String> keys, Class<T> type, Supplier<Collection<T>> supplier);

    /**
     * 使用自定义选项批量获取缓存
     *
     * @param keys 缓存键集合
     * @param type 返回值类型
     * @param supplier 数据加载器
     * @param options 缓存选项
     * @return 缓存值集合
     * @param <T> 值类型
     */
    <T> Collection<T> multiGet(Collection<String> keys, Class<T> type, Supplier<Collection<T>> supplier, CacheOptions options);

    /**
     * 更新缓存
     *
     * @param key 缓存键
     * @param value 缓存值
     * @param <T> 值类型
     */
    <T> void put(String key, T value);

    /**
     * 使用自定义选项更新缓存
     *
     * @param key 缓存键
     * @param value 缓存值
     * @param options 缓存选项
     * @param <T> 值类型
     */
    <T> void put(String key, T value, CacheOptions options);

    /**
     * 删除指定键的缓存
     *
     * @param key 缓存键
     */
    void remove(String key);

    /**
     * 删除指定前缀的所有缓存
     *
     * @param prefix 缓存键前缀
     */
    void removeByPrefix(String prefix);
}
```

## RedisCacheManager.java

```java
package com.study.collect.core.storage.cache.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.collect.core.storage.cache.config.CacheProperties;
import com.study.collect.core.storage.cache.model.CacheOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Redis缓存管理器实现
 * 提供基于Redis的缓存操作实现，支持数据自动加载、类型转换和批量操作
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisCacheManager implements CacheManager {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final CacheProperties cacheProperties;

    @Override
    public <T> T get(String key, Class<T> type, Supplier<T> supplier) {
        return get(key, type, supplier, null);
    }

    @Override
    public <T> T get(String key, Class<T> type, Supplier<T> supplier, CacheOptions options) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value != null) {
            return convertValue(value, type);
        }

        T newValue = supplier.get();
        if (newValue != null) {
            put(key, newValue, options);
        }
        return newValue;
    }

    @Override
    public <T> Collection<T> multiGet(Collection<String> keys, Class<T> type, Supplier<Collection<T>> supplier) {
        return multiGet(keys, type, supplier, null);
    }

    @Override
    public <T> Collection<T> multiGet(Collection<String> keys, Class<T> type,
                                      Supplier<Collection<T>> supplier, CacheOptions options) {
        List<Object> values = Optional.ofNullable(redisTemplate.opsForValue().multiGet(keys))
                .orElse(List.of());

        if (!values.isEmpty() && !values.contains(null)) {
            return values.stream()
                    .map(value -> convertValue(value, type))
                    .collect(Collectors.toList());
        }

        Collection<T> newValues = supplier.get();
        if (newValues != null && !newValues.isEmpty()) {
            newValues.forEach(value -> put(generateKey(value), value, options));
        }
        return newValues;
    }

    @Override
    public <T> void put(String key, T value) {
        put(key, value, null);
    }

    @Override
    public <T> void put(String key, T value, CacheOptions options) {
        CacheOptions actualOptions = Optional.ofNullable(options)
                .orElse(CacheOptions.defaultOptions());

        redisTemplate.opsForValue().set(
                key,
                value,
                actualOptions.getExpiration(),
                actualOptions.getTimeUnit()
        );
    }

    @Override
    public void remove(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public void removeByPrefix(String prefix) {
        Optional.ofNullable(redisTemplate.keys(prefix + "*"))
                .filter(keys -> !keys.isEmpty())
                .ifPresent(redisTemplate::delete);
    }

    /**
     * 将缓存值转换为指定类型
     */
    @SuppressWarnings("unchecked")
    private <T> T convertValue(Object value, Class<T> type) {
        try {
            if (type.isInstance(value)) {
                return (T) value;
            }
            return objectMapper.convertValue(value, type);
        } catch (Exception e) {
            log.error("缓存值类型转换失败: value={}, targetType={}", value, type, e);
            return null;
        }
    }

    /**
     * 生成缓存键
     */
    private <T> String generateKey(T value) {
        return String.format("%s:%d", value.getClass().getSimpleName(), value.hashCode());
    }
}
```

## CacheOptions.java

```java
package com.study.collect.core.storage.cache.model;

import lombok.Builder;
import lombok.Data;

import java.util.concurrent.TimeUnit;

@Data
@Builder
public class CacheOptions {
    // 过期时间
    private long expiration;

    // 时间单位
    private TimeUnit timeUnit;

    // 是否允许空值缓存
    private boolean cacheNull;

    // 是否使用压缩
    private boolean useCompression;

    // 自定义序列化器
    private String serializer;

    public static CacheOptions defaultOptions() {
        return CacheOptions.builder()
                .expiration(3600)
                .timeUnit(TimeUnit.SECONDS)
                .cacheNull(false)
                .useCompression(false)
                .build();
    }
}
```

## MongoConfig.java

```java
package com.study.collect.core.storage.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.study.collect.core.storage.audit.EntityAuditor;
import com.study.collect.core.storage.repository.BaseMongoRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;


@Configuration
@EnableMongoAuditing
@ConditionalOnProperty(prefix = "spring.data.mongodb", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableMongoRepositories(
        basePackages = "com.study.collect",
        repositoryBaseClass = BaseMongoRepository.class
)
@ConfigurationProperties(prefix = "spring.data.mongodb")
public class MongoConfig extends AbstractMongoClientConfiguration {
    @Value("${spring.data.mongodb.uri}")
    private String uri;
    @Value("${spring.data.mongodb.database}")
    private String database;

    @Override
    protected String getDatabaseName() {
        return database;
    }

    @Override
    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create(uri);
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoClient mongoClient) {
        return new MongoTemplate(mongoClient, getDatabaseName());
    }

//    // 添加审计配置
//    @Bean
//    public AuditorAware<String> auditorProvider() {
//        return new EntityAuditor();
//    }
}
```

## BaseEntity.java

```java
package com.study.collect.core.storage.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor  // 添加无参构造器
public abstract class BaseEntity implements Serializable {
    @Id
    protected String id;

    @CreatedDate
    protected LocalDateTime createTime;

    @LastModifiedDate
    protected LocalDateTime updateTime;

    @CreatedBy
    protected String createBy;

    @LastModifiedBy
    protected String updateBy;

    @Version
    protected Long version;

    protected Boolean deleted = false;

}
```

## VersionEntity.java

```java
package com.study.collect.core.storage.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// VersionEntity.java
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor  // 添加无参构造器
public abstract class VersionEntity extends BaseEntity {

    protected String versionCode;    // 业务版本号
    protected LocalDateTime versionTime;  // 版本时间

    public void initVersion() {
        this.version = 0L;
        this.versionCode = generateVersionCode();
        this.versionTime = LocalDateTime.now();
    }

    public void upgradeVersion() {
        this.version = this.version + 1;
        this.versionCode = generateVersionCode();
        this.versionTime = LocalDateTime.now();
    }

    protected String generateVersionCode() {
        return String.format("V%s_%d",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")),
                this.version);
    }
}
```

## DefaultEntityEventHandler.java

```java
package com.study.collect.core.storage.event;

import com.study.collect.core.storage.entity.BaseEntity;
import com.study.collect.core.storage.event.impl.EntityEvents;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 默认实体事件处理器
 */
@Slf4j
@Component
public class DefaultEntityEventHandler {

    @EventListener
    public <T extends BaseEntity> void handleBeforeSave(EntityEvents.BeforeSaveEvent<T> event) {
        log.debug("Entity before save: {}", event.getEntity());
    }

    @EventListener
    public <T extends BaseEntity> void handleAfterSave(EntityEvents.AfterSaveEvent<T> event) {
        log.debug("Entity after save: {}", event.getEntity());
    }

    @EventListener
    public <T extends BaseEntity> void handleBeforeUpdate(EntityEvents.BeforeUpdateEvent<T> event) {
        log.debug("Entity before update: {}", event.getEntity());
    }

    @EventListener
    public <T extends BaseEntity> void handleAfterUpdate(EntityEvents.AfterUpdateEvent<T> event) {
        log.debug("Entity after update: {}", event.getEntity());
    }

    @EventListener
    public <T extends BaseEntity> void handleBeforeDelete(EntityEvents.BeforeDeleteEvent<T> event) {
        log.debug("Entity before delete: {}", event.getEntity());
    }

    @EventListener
    public <T extends BaseEntity> void handleAfterDelete(EntityEvents.AfterDeleteEvent<T> event) {
        log.debug("Entity after delete: {}", event.getEntity());
    }

    @EventListener
    public <T extends BaseEntity> void handleVersionUpgrade(EntityEvents.VersionUpgradeEvent<T> event) {
        log.debug("Entity version upgrade: {} from {} to {}",
                event.getEntity(), event.getOldVersion(), event.getNewVersion());
    }
}
```

## EntityEvent.java

```java
package com.study.collect.core.storage.event;

import com.study.collect.core.storage.entity.BaseEntity;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 实体事件基类
 */
@Getter
public abstract class EntityEvent<T extends BaseEntity> extends ApplicationEvent {

    protected final T entity;

    public EntityEvent(T entity) {
        super(entity);
        this.entity = entity;
    }
}
```

## EntityEventListener.java

```java
package com.study.collect.core.storage.event;

import com.study.collect.core.storage.entity.BaseEntity;
import com.study.collect.core.storage.entity.VersionEntity;
import com.study.collect.core.storage.event.impl.EntityEvents;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EntityEventListener<T extends BaseEntity>
        extends AbstractMongoEventListener<T> {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public void onBeforeConvert(BeforeConvertEvent<T> event) {
        T entity = event.getSource();

        // 处理版本
        if (entity instanceof VersionEntity versionEntity) {
            String oldVersion = versionEntity.getVersionCode();
            if (oldVersion == null) {
                versionEntity.initVersion();
            } else {
                versionEntity.upgradeVersion();
                publishVersionUpgradeEvent(entity, oldVersion,
                        versionEntity.getVersionCode());
            }
        }
    }

    private void publishVersionUpgradeEvent(T entity, String oldVersion,
                                            String newVersion) {
        EntityEvents.VersionUpgradeEvent<T> event = new EntityEvents.VersionUpgradeEvent<>(
                entity, oldVersion, newVersion
        );
        eventPublisher.publishEvent(event);
    }
}
```

## EntityEvents.java

```java
package com.study.collect.core.storage.event.impl;

import com.study.collect.core.storage.entity.BaseEntity;
import com.study.collect.core.storage.event.EntityEvent;
import lombok.Getter;

public class EntityEvents {

    @Getter
    public static class BeforeSaveEvent<T extends BaseEntity> extends EntityEvent<T> {
        public BeforeSaveEvent(T entity) {
            super(entity);
        }
    }

    @Getter
    public static class AfterSaveEvent<T extends BaseEntity> extends EntityEvent<T> {
        public AfterSaveEvent(T entity) {
            super(entity);
        }
    }

    @Getter
    public static class BeforeUpdateEvent<T extends BaseEntity> extends EntityEvent<T> {
        public BeforeUpdateEvent(T entity) {
            super(entity);
        }
    }

    @Getter
    public static class AfterUpdateEvent<T extends BaseEntity> extends EntityEvent<T> {
        public AfterUpdateEvent(T entity) {
            super(entity);
        }
    }

    @Getter
    public static class BeforeDeleteEvent<T extends BaseEntity> extends EntityEvent<T> {
        public BeforeDeleteEvent(T entity) {
            super(entity);
        }
    }

    @Getter
    public static class AfterDeleteEvent<T extends BaseEntity> extends EntityEvent<T> {
        public AfterDeleteEvent(T entity) {
            super(entity);
        }
    }

    @Getter
    public static class VersionUpgradeEvent<T extends BaseEntity> extends EntityEvent<T> {
        private final String oldVersion;
        private final String newVersion;

        public VersionUpgradeEvent(T entity, String oldVersion, String newVersion) {
            super(entity);
            this.oldVersion = oldVersion;
            this.newVersion = newVersion;
        }
    }
}
```

## BaseMongoRepository.java

```java
package com.study.collect.core.storage.repository;

import com.study.collect.core.storage.entity.BaseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
public class BaseMongoRepository<T extends BaseEntity>
        extends SimpleMongoRepository<T, String> implements IRepository<T> {

    private final MongoOperations mongoOperations;
    private final MongoEntityInformation<T, String> entityInformation;

    public BaseMongoRepository(MongoEntityInformation<T, String> metadata,
                               MongoOperations mongoOperations) {
        super(metadata, mongoOperations);
        this.mongoOperations = mongoOperations;
        this.entityInformation = metadata;
    }

    @Override
    public T findByCode(String code) {
        Query query = Query.query(
                Criteria.where("code").is(code)
                        .and("deleted").is(false)
        );
        return mongoOperations.findOne(query, entityInformation.getJavaType());
    }

    @Override
    public Page<T> findByDeletedFalse(Pageable pageable) {
        Query query = Query.query(Criteria.where("deleted").is(false));
        return findAll(query, pageable);
    }

    @Override
    public List<T> findByVersionCodeGreaterThan(String versionCode) {
        Query query = Query.query(Criteria.where("versionCode").gt(versionCode));
        return mongoOperations.find(query, entityInformation.getJavaType());
    }

    @Override
    public void softDelete(String id) {
        Query query = Query.query(Criteria.where("id").is(id));
        Update update = Update.update("deleted", true)
                .set("updateTime", LocalDateTime.now());
        mongoOperations.updateFirst(query, update, entityInformation.getJavaType());
    }

    @Override
    public void softDelete(List<String> ids) {
        Query query = Query.query(Criteria.where("id").in(ids));
        Update update = Update.update("deleted", true)
                .set("updateTime", LocalDateTime.now());
        mongoOperations.updateMulti(query, update, entityInformation.getJavaType());
    }

    @Override
    public void updateStatus(String id, String status) {
        Query query = Query.query(Criteria.where("id").is(id));
        Update update = Update.update("status", status)
                .set("updateTime", LocalDateTime.now());
        mongoOperations.updateFirst(query, update, entityInformation.getJavaType());
    }

    protected Page<T> findAll(Query query, Pageable pageable) {
        long total = mongoOperations.count(query, entityInformation.getJavaType());
        List<T> content = mongoOperations.find(query.with(pageable),
                entityInformation.getJavaType());
        return new PageImpl<>(content, pageable, total);
    }
}
```

## IRepository.java

```java
package com.study.collect.core.storage.repository;

import com.study.collect.core.storage.entity.BaseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface IRepository<T extends BaseEntity> extends MongoRepository<T, String> {
    /**
     * 根据业务编码查询
     */
    T findByCode(String code);

    /**
     * 分页查询未删除的数据
     */
    Page<T> findByDeletedFalse(Pageable pageable);

    /**
     * 根据版本号查询数据
     */
    List<T> findByVersionCodeGreaterThan(String versionCode);

    /**
     * 软删除
     */
    void softDelete(String id);

    /**
     * 批量软删除
     */
    void softDelete(List<String> ids);

    /**
     * 更新状态
     */
    void updateStatus(String id, String status);
}
```

## VersionRepository.java

```java
package com.study.collect.core.storage.repository;

public class VersionRepository {
}

```

## CustomMongoRepositoryFactory.java

```java
package com.study.collect.core.storage.repository.factory;

import com.study.collect.core.storage.entity.BaseEntity;
import com.study.collect.core.storage.repository.BaseMongoRepository;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;

public class CustomMongoRepositoryFactory extends MongoRepositoryFactory {

    private final MongoOperations mongoOperations;

    public CustomMongoRepositoryFactory(MongoOperations mongoOperations) {
        super(mongoOperations);
        this.mongoOperations = mongoOperations;
    }

    @Override
    protected Object getTargetRepository(RepositoryInformation information) {
        Class<?> domainClass = information.getDomainType();
        if (!BaseEntity.class.isAssignableFrom(domainClass)) {
            throw new IllegalArgumentException("Domain class must extend BaseEntity");
        }

        @SuppressWarnings("unchecked")
        MongoEntityInformation<? extends BaseEntity, String> entityInformation =
                getEntityInformation((Class<? extends BaseEntity>) domainClass);

        return getTargetRepositoryViaReflection(information,
                entityInformation, mongoOperations);
    }

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        return BaseMongoRepository.class;
    }
}
```

## CustomMongoRepositoryFactoryBean.java

```java
package com.study.collect.core.storage.repository.factory;

import com.study.collect.core.storage.entity.BaseEntity;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactoryBean;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

public class CustomMongoRepositoryFactoryBean<T extends Repository<S, String>, S extends BaseEntity>
        extends MongoRepositoryFactoryBean<T, S, String> {

    public CustomMongoRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
        super(repositoryInterface);
    }

    @Override
    protected RepositoryFactorySupport getFactoryInstance(MongoOperations operations) {
        return new CustomMongoRepositoryFactory(operations);
    }
}
```

## package-info.java

```java
/**
 * 任务模块
 * 这个包包含与任务模型相关的类和接口。
 * 任务模型用于表示和管理应用程序中的各种任务。
 * 这些模型可能包括任务定义、调度信息和执行细节。
 */
package com.study.collect.core.task;
```

## MyBatisConfig.java

```java
package com.study.collect.core.task.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import javax.sql.DataSource;

@Configuration
@ConditionalOnProperty(prefix = "spring.datasource", name = "enabled", havingValue = "true", matchIfMissing = true)
@MapperScan("com.study.collect.core.task.mapper")
public class MyBatisConfig {

    @Bean
    public SqlSessionFactory sqlSessionFactory(@Qualifier("dataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);

        // 配置驼峰命名转换
        org.apache.ibatis.session.Configuration configuration =
                new org.apache.ibatis.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        sessionFactory.setConfiguration(configuration);

        // 设置XML映射文件路径
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sessionFactory.setMapperLocations(resolver.getResources("classpath*:mapper/*.xml"));

        // 设置实体类别名包
        sessionFactory.setTypeAliasesPackage("com.study.collect.core.task.entity");

        return sessionFactory.getObject();
    }

}

```

## SchedulerConfiguration.java

```java
package com.study.collect.core.task.config;

/**
 * 调度器配置
 */
public class SchedulerConfiguration {
}

```

## TaskConfiguration.java

```java
package com.study.collect.core.task.config;

import com.study.collect.core.task.definition.TaskProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * 任务配置类
 */
@Slf4j
@Configuration
@EnableScheduling
@EnableConfigurationProperties(TaskProperties.class)
public class TaskConfiguration {

    @Bean
    public ThreadPoolTaskScheduler taskScheduler(TaskProperties properties) {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();

        // 配置线程池核心参数
        scheduler.setPoolSize(properties.getThreadPool().getCoreSize());
        scheduler.setThreadNamePrefix("TaskScheduler-");

        // 配置优雅停机
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(properties.getThreadPool().getAwaitTerminationSeconds());

        // 配置异常处理
        scheduler.setErrorHandler(throwable ->
                log.error("Task execution error: {}", throwable.getMessage(), throwable)
        );

        // 配置任务拒绝处理
        scheduler.setRejectedExecutionHandler((runnable, executor) ->
                log.error("Task rejected: thread pool exhausted. Current pool size: {}",
                        executor.getPoolSize())
        );

        return scheduler;
    }
}
```

## TaskProperties.java

```java
package com.study.collect.core.task.definition;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "collect.task")
public class TaskProperties {

    /**
     * 是否启用任务调度
     */
    private boolean enabled = true;

    /**
     * 线程池配置
     */
    private ThreadPool threadPool = new ThreadPool();

    /**
     * 执行配置
     */
    private Execution execution = new Execution();

    @Data
    public static class ThreadPool {
        /**
         * 核心线程数
         */
        private int coreSize = 10;

        /**
         * 最大线程数
         */
        private int maxSize = 20;

        /**
         * 队列容量
         */
        private int queueCapacity = 200;

        /**
         * 线程空闲超时时间（秒）
         */
        private int keepAliveSeconds = 60;

        /**
         * 优雅停机等待时间（秒）
         */
        private int awaitTerminationSeconds = 60;
    }

    @Data
    public static class Execution {
        /**
         * 任务超时时间（秒）
         */
        private int timeout = 3600;

        /**
         * 重试次数
         */
        private int retryTimes = 3;

        /**
         * 重试间隔（秒）
         */
        private int retryInterval = 300;

        /**
         * 是否允许并行执行
         */
        private boolean allowParallel = true;
    }
}
```

## TaskConfig.java

```java
package com.study.collect.core.task.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 任务配置
 */
@Data
public class TaskConfig {
    private Long id;
    private String taskCode;        // 任务编码
    private String taskName;        // 任务名称
    private String taskHandler;     // 任务处理器
    private String taskParam;       // 任务参数(JSON)
    private String cronExpr;        // cron表达式
    private Integer shardTotal;     // 分片总数
    private Integer retryTimes;     // 重试次数
    private Integer retryInterval;  // 重试间隔(秒)
    private Integer timeout;        // 超时时间(秒)
    private Integer status;         // 状态:0-禁用,1-启用
    private String remark;          // 备注
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 更新时间
}
```

## TaskInstance.java

```java
package com.study.collect.core.task.entity;

import lombok.Data;
import java.time.LocalDateTime;
/**
 * 任务实例
 */
@Data
public class TaskInstance {
    private Long id;
    private String instanceId;      // 实例ID
    private String taskCode;        // 任务编码
    private Integer shardIndex;     // 分片索引
    private Integer shardTotal;     // 分片总数
    private String shardParam;      // 分片参数
    private Integer status;         // 状态
    private String errorMsg;        // 错误信息
    private String hostName;        // 执行机器
    private LocalDateTime startTime;  // 开始时间
    private LocalDateTime endTime;    // 结束时间
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 更新时间
}
```

## TaskLog.java

```java
package com.study.collect.core.task.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 任务日志
 */
@Data
public class TaskLog {
    private Long id;
    private String instanceId;      // 实例ID
    private String taskCode;        // 任务编码
    private Integer logType;        // 日志类型:1-开始,2-心跳,3-进度,4-结果,5-错误
    private String logContent;      // 日志内容
    private LocalDateTime createTime; // 创建时间

    public TaskLog() {
    }

    public TaskLog(String instanceId, String taskCode, Integer logType, String logContent) {
        this.instanceId = instanceId;
        this.taskCode = taskCode;
        this.logType = logType;
        this.logContent = logContent;
        this.createTime = LocalDateTime.now();
    }
}
```

## LogTypeEnum.java

```java
package com.study.collect.core.task.enums;

import lombok.Getter;

@Getter
public enum LogTypeEnum {
    START(1, "开始执行"),
    HEARTBEAT(2, "心跳检测"),
    PROGRESS(3, "执行进度"),
    RESULT(4, "执行结果"),
    ERROR(5, "执行错误");

    private final Integer code;
    private final String desc;

    LogTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
```

## TaskStatusEnum.java

```java
package com.study.collect.core.task.enums;

import lombok.Getter;

@Getter
public enum TaskStatusEnum {
    INIT(0, "初始化"),
    RUNNING(1, "执行中"),
    SUCCESS(2, "执行成功"),
    FAILED(3, "执行失败"),
    TIMEOUT(4, "执行超时"),
    CANCELED(5, "已取消");

    private final Integer code;
    private final String desc;

    TaskStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static TaskStatusEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (TaskStatusEnum status : TaskStatusEnum.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
```

## TaskValidationException.java

```java
package com.study.collect.core.task.exception;

public class TaskValidationException extends RuntimeException {

    public TaskValidationException(String message) {
        super(message);
    }

    public TaskValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

## AbstractTaskHandler.java

```java
package com.study.collect.core.task.handler;

import com.study.collect.core.task.model.TaskContext;
import com.study.collect.core.task.model.TaskResult;
import lombok.extern.slf4j.Slf4j;

/**
 * 任务处理器抽象类
 */
@Slf4j
public abstract class AbstractTaskHandler implements TaskHandler {

    @Override
    public TaskResult execute(TaskContext context) {
        String taskId = context.getTaskId();
        log.info("开始执行任务: taskId={}, type={}", taskId, getType());

        try {
            // 前置处理
            beforeExecute(context);

            // 执行任务
            Object result = doExecute(context);

            // 后置处理
            afterExecute(context, result);

            log.info("任务执行完成: taskId={}", taskId);
            return TaskResult.success(taskId, result);

        } catch (Exception e) {
            log.error("任务执行失败: taskId={}", taskId, e);
            return TaskResult.failure(taskId, e.getMessage());
        }
    }

    /**
     * 任务执行前的处理
     */
    protected void beforeExecute(TaskContext context) {
        // 子类可以覆盖实现
    }

    /**
     * 执行具体任务
     */
    protected abstract Object doExecute(TaskContext context);

    /**
     * 任务执行后的处理
     */
    protected void afterExecute(TaskContext context, Object result) {
        // 子类可以覆盖实现
    }
}
```

## SampleTaskHandler.java

```java
package com.study.collect.core.task.handler;

import com.study.collect.core.task.model.TaskContext;
import org.springframework.stereotype.Component;

@Component
public class SampleTaskHandler extends AbstractTaskHandler {

    @Override
    public String getType() {
        return "sample";
    }

    @Override
    protected Object doExecute(TaskContext context) {
        // 实现具体的任务处理逻辑
        return "Task executed successfully";
    }
}
```

## TaskHandler.java

```java
package com.study.collect.core.task.handler;

import com.study.collect.core.task.model.TaskContext;
import com.study.collect.core.task.model.TaskResult;

/**
 * 任务处理器
 */
public interface TaskHandler {
    /**
     * 执行任务
     * @param context 任务上下文
     * @return 任务执行结果
     */
    TaskResult execute(TaskContext context);

    /**
     * 获取处理器类型
     * @return 处理器类型标识
     */
    String getType();
}
```

## TaskHandlerManager.java

```java
package com.study.collect.core.task.handler;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
//import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 任务处理器管理器
 */
@Slf4j
@Component
public class TaskHandlerManager {

//    private final Map<String, TaskHandler> handlerMap = new HashMap<>();

    @Autowired
    private List<TaskHandler> handlers;

    @PostConstruct
    public void init() {
        handlers.forEach(handler -> handlerMap.put(handler.getType(), handler));
    }

//    public TaskHandler getHandler(String type) {
//        TaskHandler handler = handlerMap.get(type);
//        if (handler == null) {
//            throw new IllegalArgumentException("未找到任务处理器: " + type);
//        }
//        return handler;
//    }

    private final Map<String, TaskHandler> handlerMap = new HashMap<>();

    @Autowired
    public TaskHandlerManager(List<TaskHandler> handlers) {
        handlers.forEach(handler -> {
            handlerMap.put(handler.getType(), handler);
            log.info("注册任务处理器: {}", handler.getType());
        });
    }

    public TaskHandler getHandler(String type) {
        TaskHandler handler = handlerMap.get(type);
        if (handler == null) {
            throw new IllegalArgumentException("未找到任务处理器: " + type);
        }
        return handler;
    }
}

```

## TaskConfigMapper.java

```java
package com.study.collect.core.task.mapper;

import com.study.collect.core.task.entity.TaskConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface TaskConfigMapper {
    void insert(TaskConfig config);
    void update(TaskConfig config);
    TaskConfig selectById(Long id);
    TaskConfig selectByCode(String taskCode);
    List<TaskConfig> selectEnabled();
    void updateStatus(@Param("taskCode") String taskCode, @Param("status") Integer status);
    void deleteByCode(String taskCode);
    List<TaskConfig> selectPage(@Param("taskName") String taskName,
                                @Param("status") Integer status,
                                @Param("offset") int offset,
                                @Param("limit") int limit);
    long countTotal(@Param("taskName") String taskName,
                    @Param("status") Integer status);
}
```

## TaskInstanceMapper.java

```java
package com.study.collect.core.task.mapper;

import com.study.collect.core.task.entity.TaskInstance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface TaskInstanceMapper {
    void insert(TaskInstance instance);
    void updateStatus(@Param("instanceId") String instanceId,
                      @Param("status") Integer status,
                      @Param("errorMsg") String errorMsg);
    void updateEndTime(@Param("instanceId") String instanceId,
                       @Param("endTime") LocalDateTime endTime);
    TaskInstance selectById(Long id);
    TaskInstance selectByInstanceId(String instanceId);
    List<TaskInstance> selectRunning();
    List<TaskInstance> selectByTaskCode(@Param("taskCode") String taskCode,
                                        @Param("startTime") LocalDateTime startTime,
                                        @Param("endTime") LocalDateTime endTime);
    List<TaskInstance> selectTimeout(@Param("timeoutMinutes") int timeoutMinutes);
    List<TaskInstance> selectByHostName(String hostName);
    int countByStatus(@Param("taskCode") String taskCode,
                      @Param("status") Integer status);
    int cleanHistoryData(@Param("daysBefore") int daysBefore);
}
```

## TaskLogMapper.java

```java
package com.study.collect.core.task.mapper;

import com.study.collect.core.task.entity.TaskLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface TaskLogMapper {
    void insert(TaskLog log);
    void batchInsert(@Param("logs") List<TaskLog> logs);
    List<TaskLog> selectByInstanceId(String instanceId);
    List<TaskLog> selectByTaskCode(@Param("taskCode") String taskCode,
                                   @Param("logType") Integer logType,
                                   @Param("limit") Integer limit);
    List<TaskLog> selectLatestErrors(@Param("limit") int limit);
    int countLogs(@Param("taskCode") String taskCode,
                  @Param("logType") Integer logType);
    int cleanHistoryLogs(@Param("daysBefore") int daysBefore);
    void deleteByInstanceId(String instanceId);
}
```

## package-info.java

```java
/**
 * 任务模型层
 */
package com.study.collect.core.task.model;
```

## ShardingConfig.java

```java
package com.study.collect.core.task.model;

import lombok.Data;
import java.io.Serializable;

@Data
public class ShardingConfig implements Serializable {
    /**
     * 是否启用分片
     */
    private boolean enabled = false;

    /**
     * 分片总数
     */
    private Integer total = 1;

    /**
     * 分片策略
     */
    private String strategy;

    /**
     * 分片参数
     */
    private String parameter;

    /**
     * 是否允许分片并行执行
     */
    private boolean parallel = true;

    /**
     * 分片超时时间（秒）
     */
    private Integer timeout;

    /**
     * 分片失败处理策略
     * CONTINUE: 继续执行其他分片
     * STOP: 停止所有分片执行
     */
    private String failureStrategy = "CONTINUE";

    /**
     * 验证分片配置
     */
    public void validate() {
        if (enabled) {
            if (total == null || total < 1) {
                throw new IllegalArgumentException("分片总数必须大于0");
            }
            if (timeout != null && timeout < 0) {
                throw new IllegalArgumentException("分片超时时间不能小于0");
            }
        }
    }

    /**
     * 创建默认配置
     */
    public static ShardingConfig createDefault() {
        ShardingConfig config = new ShardingConfig();
        config.setEnabled(false);
        config.setTotal(1);
        config.setStrategy("AVERAGE");
        return config;
    }
}
```

## TaskContext.java

```java
package com.study.collect.core.task.model;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
public class TaskContext {
    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 任务实例ID
     */
    private String instanceId;

    /**
     * 分片索引，从0开始
     */
    private Integer shardIndex;

    /**
     * 分片总数
     */
    private Integer shardTotal;

    /**
     * 分片参数，JSON格式
     */
    private String shardParam;

    /**
     * 执行开始时间
     */
    private LocalDateTime startTime;

    /**
     * 执行超时时间（秒）
     */
    private Integer timeout;

    /**
     * 执行机器
     */
    private String hostName;

    /**
     * 上下文参数，用于在执行过程中传递数据
     */
    private Map<String, Object> parameters;

    public TaskContext() {
        this.startTime = LocalDateTime.now();
        this.parameters = new HashMap<>();
    }

    /**
     * 设置上下文参数
     */
    public void setParameter(String key, Object value) {
        this.parameters.put(key, value);
    }

    /**
     * 获取上下文参数
     */
    @SuppressWarnings("unchecked")
    public <T> T getParameter(String key) {
        return (T) this.parameters.get(key);
    }

    /**
     * 移除上下文参数
     */
    public void removeParameter(String key) {
        this.parameters.remove(key);
    }

    /**
     * 清空所有上下文参数
     */
    public void clearParameters() {
        this.parameters.clear();
    }
}
```

## TaskDefinition.java

```java
package com.study.collect.core.task.model;

// 任务定义


import lombok.Data;

import java.util.Map;

@Data
public class TaskDefinition {
    private String taskId;           // 任务ID
    private String taskName;         // 任务名称
    private String taskHandler;      // 任务处理器
    private String cronExpression;   // 调度表达式
    private ShardingConfig sharding; // 分片配置
    private Map<String, Object> props;// 扩展属性
}

```

## TaskResult.java

```java
package com.study.collect.core.task.model;

// 任务结果

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskResult {
    private String taskId;          // 任务ID
    private Boolean success;        // 执行结果
    private String errorMessage;    // 错误信息
    private Object data;           // 结果数据
    private LocalDateTime finishTime; // 完成时间

    public static TaskResult success(String taskId, Object data) {
        TaskResult result = new TaskResult();
        result.setTaskId(taskId);
        result.setSuccess(true);
        result.setData(data);
        result.setFinishTime(LocalDateTime.now());
        return result;
    }

    public static TaskResult failure(String taskId, String errorMessage) {
        TaskResult result = new TaskResult();
        result.setTaskId(taskId);
        result.setSuccess(false);
        result.setErrorMessage(errorMessage);
        result.setFinishTime(LocalDateTime.now());
        return result;
    }
}


```

## TaskStatus.java

```java
package com.study.collect.core.task.model;

// 任务状态枚举
public enum TaskStatus {
    CREATED("已创建"),
    WAITING("等待中"),
    RUNNING("执行中"),
    SUCCESS("执行成功"),
    FAILED("执行失败"),
    CANCELED("已取消"),
    TIMEOUT("已超时");

    private final String description;

    TaskStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
```

## AbstractTaskScheduler.java

```java
package com.study.collect.core.task.scheduler;


import com.study.collect.core.task.entity.TaskConfig;
import com.study.collect.core.task.model.TaskDefinition;
import com.study.collect.core.task.service.TaskConfigService;
import com.study.collect.core.task.service.TaskExecuteService;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Slf4j
public abstract class AbstractTaskScheduler implements TaskScheduler {

    protected final TaskConfigService taskConfigService;
    protected final TaskExecuteService taskExecuteService;
    protected final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    protected AbstractTaskScheduler(TaskConfigService taskConfigService, TaskExecuteService taskExecuteService) {
        this.taskConfigService = taskConfigService;
        this.taskExecuteService = taskExecuteService;
    }

    @Override
    public void start() {
        log.info("Starting task scheduler...");
        doStart();
    }

    @Override
    public void stop() {
        log.info("Stopping task scheduler...");
        scheduledTasks.values().forEach(future -> future.cancel(true));
        scheduledTasks.clear();
        doStop();
    }

    @Override
    public void addTask(TaskDefinition task) {
        TaskConfig config = convertToConfig(task);
        taskConfigService.saveTaskConfig(config);
        doAddTask(task);
    }

    @Override
    public void removeTask(String taskId) {
        ScheduledFuture<?> future = scheduledTasks.remove(taskId);
        if (future != null) {
            future.cancel(true);
        }
        doRemoveTask(taskId);
    }

    protected abstract void doStart();
    protected abstract void doStop();
    protected abstract void doAddTask(TaskDefinition task);
    protected abstract void doRemoveTask(String taskId);

    // 提供任务配置转换方法
    protected TaskConfig convertToConfig(TaskDefinition task) {
        TaskConfig config = new TaskConfig();
        config.setTaskCode(task.getTaskId());
        config.setTaskName(task.getTaskName());
        config.setTaskHandler(task.getTaskHandler());
        config.setCronExpr(task.getCronExpression());
        config.setShardTotal(task.getSharding() != null ? task.getSharding().getTotal() : 1);
        return config;
    }
}
```

## DefaultTaskScheduler.java

```java
package com.study.collect.core.task.scheduler;

import com.study.collect.core.task.entity.TaskConfig;
import com.study.collect.core.task.entity.TaskInstance;
import com.study.collect.core.task.model.TaskDefinition;
import com.study.collect.core.task.service.TaskConfigService;
import com.study.collect.core.task.service.TaskExecuteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Component
public class DefaultTaskScheduler extends AbstractTaskScheduler {

    private final TaskScheduler scheduler;
    private final TaskDispatcher taskDispatcher;
    private volatile boolean running = false;

    public DefaultTaskScheduler(TaskConfigService taskConfigService,
                                TaskExecuteService taskExecuteService,
                                TaskScheduler scheduler,
                                TaskDispatcher taskDispatcher) {
        super(taskConfigService, taskExecuteService);
        this.scheduler = scheduler;
        this.taskDispatcher = taskDispatcher;
    }

    @Override
    protected void doStart() {
        if (running) {
            return;
        }
        running = true;

        // 加载所有可用的任务配置并调度
        taskConfigService.getEnabledTaskConfigs().forEach(config -> {
            try {
                TaskDefinition task = convertToDefinition(config);
                scheduleTask(task);
                log.info("Loaded task from config: {}", config.getTaskCode());
            } catch (Exception e) {
                log.error("Failed to load task: {}", config.getTaskCode(), e);
            }
        });

        // 启动定时任务状态检查
        scheduler.scheduleWithFixedDelay(
                this::checkRunningTasks,
                Duration.ofMinutes(1)
        );

        log.info("Task scheduler started successfully");
    }

    @Override
    protected void doStop() {
        if (!running) {
            return;
        }
        running = false;

        // 停止所有运行中的任务
        taskExecuteService.getRunningTasks().forEach(instance -> {
            try {
                taskExecuteService.completeTaskInstance(
                        instance.getInstanceId(),
                        false,
                        "Scheduler stopped"
                );
                log.info("Stopped running task: {}", instance.getInstanceId());
            } catch (Exception e) {
                log.error("Failed to stop task: {}", instance.getInstanceId(), e);
            }
        });

        log.info("Task scheduler stopped successfully");
    }

    @Override
    protected void doAddTask(TaskDefinition task) {
        validateTask(task);
        scheduleTask(task);
        log.info("Added new task: {}", task.getTaskId());
    }

    @Override
    protected void doRemoveTask(String taskId) {
        taskConfigService.disableTask(taskId);
        log.info("Removed task: {}", taskId);
    }

    @Override
    public void pauseTask(String taskId) {
        ScheduledFuture<?> future = scheduledTasks.get(taskId);
        if (future != null) {
            future.cancel(false);
            taskConfigService.disableTask(taskId);
            log.info("Paused task: {}", taskId);
        }
    }

    @Override
    public void resumeTask(String taskId) {
        TaskConfig config = taskConfigService.getTaskConfig(taskId);
        if (config != null) {
            taskConfigService.enableTask(taskId);
            scheduleTask(convertToDefinition(config));
            log.info("Resumed task: {}", taskId);
        }
    }

    private void scheduleTask(TaskDefinition task) {
        // 验证cron表达式
        if (!StringUtils.hasText(task.getCronExpression())) {
            log.warn("Task {} has no cron expression, skipped scheduling", task.getTaskId());
            return;
        }

        try {
            ScheduledFuture<?> future = scheduler.schedule(
                    () -> executeTask(task),
                    new CronTrigger(task.getCronExpression())
            );

            // 如果任务已存在，取消旧的调度
            ScheduledFuture<?> existing = scheduledTasks.put(task.getTaskId(), future);
            if (existing != null) {
                existing.cancel(true);
                log.info("Replaced existing schedule for task: {}", task.getTaskId());
            }

            log.info("Scheduled task: {}, cron: {}", task.getTaskId(), task.getCronExpression());

        } catch (Exception e) {
            log.error("Failed to schedule task: {}", task.getTaskId(), e);
            throw new RuntimeException("Failed to schedule task", e);
        }
    }

    private void executeTask(TaskDefinition task) {
        try {
            log.info("Starting task execution: {}", task.getTaskId());

            // 获取分片配置
            int shardTotal = task.getSharding() != null ? task.getSharding().getTotal() : 1;

            // 创建并分发分片任务
            for (int shardIndex = 0; shardIndex < shardTotal; shardIndex++) {
                String shardParam = createShardParam(shardIndex, shardTotal);
                TaskInstance instance = taskExecuteService.createTaskInstance(
                        task.getTaskId(),
                        shardIndex,
                        shardParam
                );

                taskDispatcher.dispatch(instance);
                log.info("Dispatched task instance: {} - shard {}/{}",
                        instance.getInstanceId(), shardIndex + 1, shardTotal);
            }

        } catch (Exception e) {
            log.error("Task execution failed: {}", task.getTaskId(), e);
        }
    }

    @Scheduled(fixedDelay = 60000)  // 每分钟检查一次
    private void checkRunningTasks() {
        if (!running) {
            return;
        }

        try {
            taskExecuteService.getRunningTasks().forEach(instance -> {
                TaskConfig config = taskConfigService.getTaskConfig(instance.getTaskCode());
                if (config != null && config.getTimeout() > 0) {
                    checkTaskTimeout(instance, config.getTimeout());
                }
            });
        } catch (Exception e) {
            log.error("Failed to check running tasks", e);
        }
    }

    private void checkTaskTimeout(TaskInstance instance, int timeoutSeconds) {
        if (instance.getStartTime() == null) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        if (instance.getStartTime().plusSeconds(timeoutSeconds).isBefore(now)) {
            try {
                taskExecuteService.completeTaskInstance(
                        instance.getInstanceId(),
                        false,
                        "Task execution timed out after " + timeoutSeconds + " seconds"
                );
                log.warn("Task instance timed out: {}", instance.getInstanceId());
            } catch (Exception e) {
                log.error("Failed to handle task timeout: {}", instance.getInstanceId(), e);
            }
        }
    }

    private String createShardParam(int shardIndex, int shardTotal) {
        return String.format("{\"shardIndex\":%d,\"shardTotal\":%d,\"uuid\":\"%s\"}",
                shardIndex, shardTotal, UUID.randomUUID().toString());
    }

    private TaskDefinition convertToDefinition(TaskConfig config) {
        TaskDefinition task = new TaskDefinition();
        task.setTaskId(config.getTaskCode());
        task.setTaskName(config.getTaskName());
        task.setTaskHandler(config.getTaskHandler());
        task.setCronExpression(config.getCronExpr());
        // 设置其他属性...
        return task;
    }

    private void validateTask(TaskDefinition task) {
        if (!StringUtils.hasText(task.getTaskId())) {
            throw new IllegalArgumentException("Task ID cannot be empty");
        }
        if (!StringUtils.hasText(task.getTaskHandler())) {
            throw new IllegalArgumentException("Task handler cannot be empty");
        }
        if (StringUtils.hasText(task.getCronExpression())) {
            try {
                new CronTrigger(task.getCronExpression());
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid cron expression: " + task.getCronExpression());
            }
        }
    }
}
```

## package-info.java

```java
/**
 * 调度层
 */
package com.study.collect.core.task.scheduler;
```

## TaskDispatcher.java

```java
package com.study.collect.core.task.scheduler;

import com.study.collect.core.task.enums.TaskStatusEnum;
import com.study.collect.core.mq.message.TaskMessage;
import com.study.collect.core.mq.producer.TaskProducer;
import com.study.collect.core.task.entity.TaskInstance;
import com.study.collect.core.task.mapper.TaskInstanceMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TaskDispatcher {

    private final TaskProducer taskProducer;
    private final TaskInstanceMapper taskInstanceMapper;

    @Autowired
    public TaskDispatcher(TaskProducer taskProducer, TaskInstanceMapper taskInstanceMapper) {
        this.taskProducer = taskProducer;
        this.taskInstanceMapper = taskInstanceMapper;
    }

    public void dispatch(TaskInstance instance) {
        try {
            // 更新任务状态为执行中
            updateTaskStatus(instance.getInstanceId(), TaskStatusEnum.RUNNING, null);

            // 转换并发送消息
            TaskMessage message = convertToMessage(instance);
            taskProducer.sendTask(message);

            log.info("Task dispatched successfully: instanceId={}, taskCode={}, shard={}/{}",
                    instance.getInstanceId(),
                    instance.getTaskCode(),
                    instance.getShardIndex() + 1,
                    instance.getShardTotal()
            );

        } catch (Exception e) {
            log.error("Failed to dispatch task: " + instance.getInstanceId(), e);

            // 更新任务状态为失败
            updateTaskStatus(instance.getInstanceId(), TaskStatusEnum.FAILED, e.getMessage());
            throw new RuntimeException("Task dispatch failed", e);
        }
    }

    private void updateTaskStatus(String instanceId, TaskStatusEnum status, String errorMsg) {
        taskInstanceMapper.updateStatus(instanceId, status.getCode(), errorMsg);
    }

    private TaskMessage convertToMessage(TaskInstance instance) {
        TaskMessage message = new TaskMessage();
        message.setTaskId(instance.getTaskCode());
        message.setInstanceId(instance.getInstanceId());
        message.setShardIndex(instance.getShardIndex());
        message.setShardTotal(instance.getShardTotal());
        message.setShardParam(instance.getShardParam());
        message.setHostName(instance.getHostName());
        return message;
    }
}
```

## TaskScheduler.java

```java
package com.study.collect.core.task.scheduler;

// 调度器接口


import com.study.collect.core.task.model.TaskDefinition;

public interface TaskScheduler {
    /**
     * 启动调度器
     */
    void start();

    /**
     * 停止调度器
     */
    void stop();

    /**
     * 添加任务
     */
    void addTask(TaskDefinition task);

    /**
     * 移除任务
     */
    void removeTask(String taskId);

    /**
     * 暂停任务
     */
    void pauseTask(String taskId);

    /**
     * 恢复任务
     */
    void resumeTask(String taskId);
}

```

## TaskConfigService.java

```java
package com.study.collect.core.task.service;

import com.study.collect.core.task.enums.TaskStatusEnum;
import com.study.collect.core.task.entity.TaskConfig;
import com.study.collect.core.task.mapper.TaskConfigMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * TaskConfigService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskConfigService {

    private final TaskConfigMapper taskConfigMapper;

    @Transactional(rollbackFor = Exception.class)
    public void saveTaskConfig(TaskConfig config) {
        TaskConfig existConfig = taskConfigMapper.selectByCode(config.getTaskCode());
        if (existConfig == null) {
            log.info("新增任务配置: {}", config.getTaskCode());
            taskConfigMapper.insert(config);
        } else {
            log.info("更新任务配置: {}", config.getTaskCode());
            taskConfigMapper.update(config);
        }
    }

    public TaskConfig getTaskConfig(String taskCode) {
        return taskConfigMapper.selectByCode(taskCode);
    }

    public List<TaskConfig> getEnabledTaskConfigs() {
        return taskConfigMapper.selectEnabled();
    }

    @Transactional(rollbackFor = Exception.class)
    public void enableTask(String taskCode) {
        taskConfigMapper.updateStatus(taskCode, TaskStatusEnum.RUNNING.getCode());
        log.info("启用任务: {}", taskCode);
    }

    @Transactional(rollbackFor = Exception.class)
    public void disableTask(String taskCode) {
        taskConfigMapper.updateStatus(taskCode, TaskStatusEnum.INIT.getCode());
        log.info("禁用任务: {}", taskCode);
    }

    public void validateTaskConfig(TaskConfig config) {
        if (config.getShardTotal() == null || config.getShardTotal() < 1) {
            config.setShardTotal(1);
        }
        if (config.getRetryTimes() == null) {
            config.setRetryTimes(0);
        }
        if (config.getRetryInterval() == null) {
            config.setRetryInterval(0);
        }
        if (config.getTimeout() == null) {
            config.setTimeout(0);
        }
        if (config.getStatus() == null) {
            config.setStatus(TaskStatusEnum.INIT.getCode());
        }
    }
}
```

## TaskExecuteService.java

```java
package com.study.collect.core.task.service;

import com.study.collect.core.task.enums.LogTypeEnum;
import com.study.collect.core.task.enums.TaskStatusEnum;
import com.study.collect.core.task.utils.InstanceIdGenerator;
import com.study.collect.core.task.entity.TaskConfig;
import com.study.collect.core.task.entity.TaskInstance;
import com.study.collect.core.task.entity.TaskLog;
import com.study.collect.core.task.mapper.TaskInstanceMapper;
import com.study.collect.core.task.mapper.TaskLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.List;

/**
 * TaskConfigService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskExecuteService {

    private final TaskInstanceMapper taskInstanceMapper;
    private final TaskLogMapper taskLogMapper;
    private final TaskConfigService taskConfigService;

    @Transactional(rollbackFor = Exception.class)
    public TaskInstance createTaskInstance(String taskCode, Integer shardIndex, String shardParam) {
        TaskConfig config = taskConfigService.getTaskConfig(taskCode);
        if (config == null) {
            throw new IllegalArgumentException("任务配置不存在: " + taskCode);
        }

        String instanceId = InstanceIdGenerator.generateInstanceId(taskCode);
        TaskInstance instance = new TaskInstance();
        instance.setInstanceId(instanceId);
        instance.setTaskCode(taskCode);
        instance.setShardIndex(shardIndex);
        instance.setShardTotal(config.getShardTotal());
        instance.setShardParam(shardParam);
        instance.setStatus(TaskStatusEnum.INIT.getCode());
        instance.setHostName(getHostName());
        instance.setStartTime(LocalDateTime.now());

        taskInstanceMapper.insert(instance);
        recordTaskLog(instanceId, taskCode, LogTypeEnum.START, "任务开始执行");

        return instance;
    }

    @Transactional(rollbackFor = Exception.class)
    public void completeTaskInstance(String instanceId, boolean success, String errorMsg) {
        TaskInstance instance = taskInstanceMapper.selectByInstanceId(instanceId);
        if (instance == null) {
            throw new IllegalArgumentException("任务实例不存在: " + instanceId);
        }

        LocalDateTime endTime = LocalDateTime.now();
        TaskStatusEnum status = success ? TaskStatusEnum.SUCCESS : TaskStatusEnum.FAILED;

        taskInstanceMapper.updateStatus(instanceId, status.getCode(), errorMsg);
        taskInstanceMapper.updateEndTime(instanceId, endTime);

        LogTypeEnum logType = success ? LogTypeEnum.RESULT : LogTypeEnum.ERROR;
        String logContent = success ? "任务执行成功" : "任务执行失败: " + errorMsg;
        recordTaskLog(instanceId, instance.getTaskCode(), logType, logContent);
    }

    public void recordTaskProgress(String instanceId, String progressInfo) {
        TaskInstance instance = taskInstanceMapper.selectByInstanceId(instanceId);
        if (instance != null) {
            recordTaskLog(instanceId, instance.getTaskCode(), LogTypeEnum.PROGRESS, progressInfo);
        }
    }

    private void recordTaskLog(String instanceId, String taskCode, LogTypeEnum logType, String content) {
        TaskLog log = new TaskLog(instanceId, taskCode, logType.getCode(), content);
        taskLogMapper.insert(log);
    }

    public List<TaskInstance> getRunningTasks() {
        return taskInstanceMapper.selectRunning();
    }

    public List<TaskLog> getTaskLogs(String instanceId) {
        return taskLogMapper.selectByInstanceId(instanceId);
    }

    private String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "unknown";
        }
    }
}
```

## InstanceIdGenerator.java

```java
package com.study.collect.core.task.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public class InstanceIdGenerator {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final AtomicInteger SEQUENCE = new AtomicInteger(0);

    public static String generateInstanceId(String taskCode) {
        // 重置序号,避免无限增长
        if (SEQUENCE.get() > 9999) {
            SEQUENCE.set(0);
        }

        // 格式：taskCode_yyyyMMddHHmmss_XXXX
        return String.format("%s_%s_%04d",
                taskCode,
                LocalDateTime.now().format(FORMATTER),
                SEQUENCE.getAndIncrement());
    }
}
```

## TaskConfigMapper.xml

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.study.collect.core.task.mapper.TaskConfigMapper">

    <!-- 结果映射 -->
    <resultMap id="taskConfigMap" type="com.study.collect.core.task.entity.TaskConfig">
        <id column="id" property="id"/>
        <result column="task_code" property="taskCode"/>
        <result column="task_name" property="taskName"/>
        <result column="task_handler" property="taskHandler"/>
        <result column="task_param" property="taskParam"/>
        <result column="cron_expr" property="cronExpr"/>
        <result column="shard_total" property="shardTotal"/>
        <result column="retry_times" property="retryTimes"/>
        <result column="retry_interval" property="retryInterval"/>
        <result column="timeout" property="timeout"/>
        <result column="status" property="status"/>
        <result column="remark" property="remark"/>
        <result column="create_time" property="createTime"/>
        <result column="update_time" property="updateTime"/>
    </resultMap>

    <!-- 修改所有select的resultType为resultMap -->
<!--    <select id="selectById" resultMap="taskConfigMap">-->


    <select id="selectPage" resultMap="taskConfigMap">
        SELECT * FROM task_config
        <where>
            <if test="taskName != null and taskName != ''">
                AND task_name LIKE CONCAT('%', #{taskName}, '%')
            </if>
            <if test="status != null">
                AND status = #{status}
            </if>
        </where>
        ORDER BY create_time DESC
        LIMIT #{offset}, #{limit}
    </select>

    <!-- 插入配置 -->
    <insert id="insert" parameterType="TaskConfig" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO task_config (task_code, task_name, task_handler, task_param,
                                 cron_expr, shard_total, retry_times, retry_interval,
                                 timeout, status, remark)
        VALUES (#{taskCode}, #{taskName}, #{taskHandler}, #{taskParam},
                #{cronExpr}, #{shardTotal}, #{retryTimes}, #{retryInterval},
                #{timeout}, #{status}, #{remark})
    </insert>

    <!-- 更新配置 -->
    <update id="update" parameterType="TaskConfig">
        UPDATE task_config
        <set>
            <if test="taskName != null">task_name = #{taskName},</if>
            <if test="taskHandler != null">task_handler = #{taskHandler},</if>
            <if test="taskParam != null">task_param = #{taskParam},</if>
            <if test="cronExpr != null">cron_expr = #{cronExpr},</if>
            <if test="shardTotal != null">shard_total = #{shardTotal},</if>
            <if test="retryTimes != null">retry_times = #{retryTimes},</if>
            <if test="retryInterval != null">retry_interval = #{retryInterval},</if>
            <if test="timeout != null">timeout = #{timeout},</if>
            <if test="status != null">status = #{status},</if>
            <if test="remark != null">remark = #{remark},</if>
            update_time = CURRENT_TIMESTAMP
        </set>
        WHERE task_code = #{taskCode}
    </update>

    <!-- 根据ID查询 -->
    <select id="selectById" resultMap="taskConfigMap">
        SELECT *
        FROM task_config
        WHERE id = #{id}
    </select>

    <!-- 根据编码查询 -->
    <select id="selectByCode" resultMap="taskConfigMap">
        SELECT *
        FROM task_config
        WHERE task_code = #{taskCode}
    </select>

    <!-- 查询所有启用的配置 -->
    <select id="selectEnabled" resultMap="taskConfigMap">
        SELECT *
        FROM task_config
        WHERE status = 1
    </select>

    <!-- 更新状态 -->
    <update id="updateStatus">
        UPDATE task_config
        SET status      = #{status},
            update_time = CURRENT_TIMESTAMP
        WHERE task_code = #{taskCode}
    </update>

    <!-- 删除配置 -->
    <delete id="deleteByCode">
        DELETE
        FROM task_config
        WHERE task_code = #{taskCode}
    </delete>


    <!-- 统计总数 -->
    <select id="countTotal" resultType="long">
        SELECT COUNT(*) FROM task_config
        <where>
            <if test="taskName != null and taskName != ''">
                AND task_name LIKE CONCAT('%', #{taskName}, '%')
            </if>
            <if test="status != null">
                AND status = #{status}
            </if>
        </where>
    </select>
</mapper>
```

## TaskInstanceMapper.xml

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.study.collect.core.task.mapper.TaskInstanceMapper">

    <!-- 结果映射 -->
    <resultMap id="taskInstanceMap" type="com.study.collect.core.task.entity.TaskInstance">
        <id column="id" property="id"/>
        <result column="instance_id" property="instanceId"/>
        <result column="task_code" property="taskCode"/>
        <result column="shard_index" property="shardIndex"/>
        <result column="shard_total" property="shardTotal"/>
        <result column="shard_param" property="shardParam"/>
        <result column="status" property="status"/>
        <result column="error_msg" property="errorMsg"/>
        <result column="host_name" property="hostName"/>
        <result column="start_time" property="startTime"/>
        <result column="end_time" property="endTime"/>
        <result column="create_time" property="createTime"/>
        <result column="update_time" property="updateTime"/>
    </resultMap>

    <!-- 插入实例 -->
    <insert id="insert" parameterType="TaskInstance" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO task_instance (
            instance_id, task_code, shard_index, shard_total,
            shard_param, status, host_name, start_time,
            error_msg
        ) VALUES (
                     #{instanceId}, #{taskCode}, #{shardIndex}, #{shardTotal},
                     #{shardParam}, #{status}, #{hostName}, #{startTime},
                     #{errorMsg}
                 )
    </insert>

    <!-- 更新状态 -->
    <update id="updateStatus">
        UPDATE task_instance
        SET status = #{status},
        <if test="errorMsg != null">error_msg = #{errorMsg},</if>
        update_time = CURRENT_TIMESTAMP
        WHERE instance_id = #{instanceId}
    </update>

    <!-- 更新结束时间 -->
    <update id="updateEndTime">
        UPDATE task_instance
        SET end_time = #{endTime},
            update_time = CURRENT_TIMESTAMP
        WHERE instance_id = #{instanceId}
    </update>

    <!-- 根据ID查询 -->
    <select id="selectById" resultMap="taskInstanceMap">
        SELECT * FROM task_instance WHERE id = #{id}
    </select>

    <!-- 根据实例ID查询 -->
    <select id="selectByInstanceId" resultMap="taskInstanceMap">
        SELECT * FROM task_instance WHERE instance_id = #{instanceId}
    </select>

    <!-- 查询运行中的任务 -->
    <select id="selectRunning" resultMap="taskInstanceMap">
        SELECT * FROM task_instance
        WHERE status = 1
        ORDER BY start_time ASC
    </select>

    <!-- 根据任务编码和时间范围查询 -->
    <select id="selectByTaskCode" resultMap="taskInstanceMap">
        SELECT * FROM task_instance
        WHERE task_code = #{taskCode}
        <if test="startTime != null">
            AND create_time >= #{startTime}
        </if>
        <if test="endTime != null">
            AND create_time &lt;= #{endTime}
        </if>
        ORDER BY create_time DESC
    </select>

    <!-- 查询超时任务 -->
    <select id="selectTimeout" resultMap="taskInstanceMap">
        SELECT * FROM task_instance
        WHERE status = 1
          AND start_time &lt; DATE_SUB(NOW(), INTERVAL #{timeoutMinutes} MINUTE)
    </select>

    <!-- 根据主机名查询任务 -->
    <select id="selectByHostName" resultMap="taskInstanceMap">
        SELECT * FROM task_instance
        WHERE host_name = #{hostName}
          AND status = 1
    </select>

    <!-- 根据状态统计任务数 -->
    <select id="countByStatus" resultType="int">
        SELECT COUNT(*) FROM task_instance
        WHERE task_code = #{taskCode}
          AND status = #{status}
    </select>

    <!-- 清理历史数据 -->
    <delete id="cleanHistoryData">
        DELETE FROM task_instance
        WHERE create_time &lt; DATE_SUB(NOW(), INTERVAL #{daysBefore} DAY)
          AND status IN (2, 3, 4, 5)
    </delete>
</mapper>
```

## TaskLogMapper.xml

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.study.collect.core.task.mapper.TaskLogMapper">

    <!-- 结果映射 -->
    <resultMap id="taskLogMap" type="com.study.collect.core.task.entity.TaskLog">
        <id column="id" property="id"/>
        <result column="instance_id" property="instanceId"/>
        <result column="task_code" property="taskCode"/>
        <result column="log_type" property="logType"/>
        <result column="log_content" property="logContent"/>
        <result column="create_time" property="createTime"/>
    </resultMap>

    <!-- 插入日志 -->
    <insert id="insert" parameterType="TaskLog">
        INSERT INTO task_log (
            instance_id, task_code, log_type, log_content
        ) VALUES (
                     #{instanceId}, #{taskCode}, #{logType}, #{logContent}
                 )
    </insert>

    <!-- 批量插入 -->
    <insert id="batchInsert">
        INSERT INTO task_log (
        instance_id, task_code, log_type, log_content
        ) VALUES
        <foreach collection="logs" item="log" separator=",">
            (#{log.instanceId}, #{log.taskCode}, #{log.logType}, #{log.logContent})
        </foreach>
    </insert>

    <!-- 根据实例ID查询 -->
    <select id="selectByInstanceId" resultMap="taskLogMap">
        SELECT * FROM task_log
        WHERE instance_id = #{instanceId}
        ORDER BY create_time ASC
    </select>

    <!-- 根据任务编码和日志类型查询 -->
    <select id="selectByTaskCode" resultMap="taskLogMap">
        SELECT * FROM task_log
        WHERE task_code = #{taskCode}
        <if test="logType != null">
            AND log_type = #{logType}
        </if>
        ORDER BY create_time DESC
        <if test="limit != null">
            LIMIT #{limit}
        </if>
    </select>

    <!-- 查询最新的错误日志 -->
    <select id="selectLatestErrors" resultMap="taskLogMap">
        SELECT * FROM task_log
        WHERE log_type = 5
        ORDER BY create_time DESC
            LIMIT #{limit}
    </select>

    <!-- 统计日志数量 -->
    <select id="countLogs" resultType="int">
        SELECT COUNT(*) FROM task_log
        WHERE task_code = #{taskCode}
        <if test="logType != null">
            AND log_type = #{logType}
        </if>
    </select>

    <!-- 清理历史日志 -->
    <delete id="cleanHistoryLogs">
        DELETE FROM task_log
        WHERE create_time &lt; DATE_SUB(NOW(), INTERVAL #{daysBefore} DAY)
    </delete>

    <!-- 根据实例ID删除日志 -->
    <delete id="deleteByInstanceId">
        DELETE FROM task_log
        WHERE instance_id = #{instanceId}
    </delete>
</mapper>
```

