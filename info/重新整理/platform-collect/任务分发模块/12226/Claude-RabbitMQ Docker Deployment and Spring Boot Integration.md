## Prompt:
rabbitmq单机和集群docker部署，以及使用springboot3.2.9链接的配置类和工具类

## Response:
Let me help you create the Docker deployment configurations and Spring Boot integration for RabbitMQ.

RabbitMQ Docker Configurations

Click to open code

RabbitMQ Configuration and Utility Classes

Click to open code

Here's the application.yml configuration to add:

```yaml
spring:
  rabbitmq:
    # Single node
    addresses: localhost:5672
    # Cluster nodes
    #addresses: localhost:5672,localhost:5673,localhost:5674
    username: admin
    password: admin123
    virtual-host: /
    publisher-confirm-type: correlated
    publisher-returns: true
    listener:
      simple:
        acknowledge-mode: manual
        prefetch: 1
        concurrency: 3
        max-concurrency: 10
    template:
      mandatory: true
      retry:
        enabled: true
        initial-interval: 1000
        max-attempts: 3
        max-interval: 10000
        multiplier: 1.0
```

Key features implemented:

1.  Docker configurations for both single-node and cluster deployments
2.  Spring Boot 3.2.9 RabbitMQ configuration with connection factory and templates
3.  Message conversion using Jackson2JsonMessageConverter
4.  Manual acknowledgment mode for better control
5.  Utility class for common RabbitMQ operations
6.  Retry and error handling mechanisms
7.  Support for delayed messages

Let me know if you need any clarification or additional features.

