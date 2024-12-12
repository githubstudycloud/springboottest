package com.study.scheduler.config;

import com.study.common.model.task.TaskDefinition;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "scheduler.job")
public class JobConfiguration {

    private List<TaskDefinition> tasks;
    private Map<String, String> defaultVariables;
    private RetryProperties retry;
    private MonitorProperties monitor;

    @Data
    public static class RetryProperties {
        private int maxAttempts = 3;
        private long initialInterval = 1000;
        private double multiplier = 2.0;
        private long maxInterval = 10000;
    }

    @Data
    public static class MonitorProperties {
        private boolean enabled = true;
        private long checkInterval = 60000;
        private List<String> metrics;
        private AlertProperties alert;
    }

    @Data
    public static class AlertProperties {
        private boolean enabled = true;
        private String channel;
        private List<String> receivers;
    }

    @PostConstruct
    public void init() {
        // 初始化预定义任务
        if (tasks != null) {
            tasks.forEach(this::initializeTask);
        }
    }

    private void initializeTask(TaskDefinition task) {
        // 应用默认配置
        if (task.getRetryPolicy() == null) {
            task.setRetryPolicy(createDefaultRetryPolicy());
        }

        // 合并默认变量
        if (defaultVariables != null) {
            task.getVariables().addAll(defaultVariables.keySet());
        }
    }
}