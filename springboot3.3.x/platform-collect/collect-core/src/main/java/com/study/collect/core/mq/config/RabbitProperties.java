package com.study.collect.core.mq.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "collect.mq.rabbit")
public class RabbitProperties {

    private String host;
    private int port;
    private String username;
    private String password;

    private TaskConfig task = new TaskConfig();
    private ResultConfig result = new ResultConfig();

    @Data
    public static class TaskConfig {
        private String exchange;
        private String queue;
        private String routingKey;
    }

    @Data
    public static class ResultConfig {
        private String exchange;
        private String queue;
        private String routingKey;
    }
}