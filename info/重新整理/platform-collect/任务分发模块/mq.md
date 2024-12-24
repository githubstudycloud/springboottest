根据您提供的代码和项目结构，我来详细分析这个自动装配问题并提供解决方案。

问题分析：

通过查看代码中的相关配置，我发现问题出在 RabbitConfig 和 MQProperties 的配置关系上。具体问题在于：

1. `MQProperties` 类使用了 `@ConfigurationProperties(prefix = "collect.mq")` 注解
2. `RabbitConfig` 类使用了 `@EnableConfigurationProperties(MQProperties.class)` 注解
3. 可能在其他地方也存在对 MQProperties 的配置定义

解决方案：

我建议采用以下方式修改配置类：

```java
@Configuration
@EnableConfigurationProperties(MQProperties.class)
public class RabbitConfig {

    @Autowired
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

    // ... 其他配置保持不变
}
```

同时，确保 MQProperties 类的定义如下：

```java
@ConfigurationProperties(prefix = "collect.mq")
public class MQProperties {
    private RabbitMQ rabbit = new RabbitMQ();

    // getter, setter 和其他代码保持不变

    @Data
    public static class RabbitMQ {
        // 现有代码保持不变
    }
}
```

此外，需要检查：

1. 确保在主配置类上添加了对 MQProperties 的启用：

```java
@SpringBootApplication
@EnableConfigurationProperties
public class CollectApplication {
    public static void main(String[] args) {
        SpringApplication.run(CollectApplication.class, args);
    }
}
```

2. 检查 application.yml 中的配置是否正确：

```yaml
collect:
  mq:
    rabbit:
      task:
        exchange: collect.task
        queue: collect.task.queue
        routing-key: collect.task
      result:
        exchange: collect.result
        queue: collect.result.queue
        routing-key: collect.result
```

3. 删除项目中任何其他对 MQProperties 的重复定义。

通过以上修改，应该能够解决自动装配的问题。如果问题仍然存在，建议检查：

1. 项目中是否存在重复的组件扫描配置
2. 是否有其他配置类也在尝试创建 MQProperties 的实例
3. 确保所有相关的配置类都在正确的包路径下

如果需要进一步的帮助或有其他问题，请提供更多的相关代码或配置信息。