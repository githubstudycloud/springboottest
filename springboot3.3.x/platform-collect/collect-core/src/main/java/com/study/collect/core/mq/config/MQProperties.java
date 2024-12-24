package com.study.collect.core.mq.config;

// 基础配置

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "collect.mq")
public class MQProperties {
    private RabbitMQ rabbit = new RabbitMQ();

    @Data
    public static class RabbitMQ {
        private String host;
        private Integer port;
        private String username;
        private String password;

        private Queue task = new Queue();
        private Queue result = new Queue();

        @Data
        public static class Queue {
            private String exchange;
            private String queue;
            private String routingKey;
        }
    }
}