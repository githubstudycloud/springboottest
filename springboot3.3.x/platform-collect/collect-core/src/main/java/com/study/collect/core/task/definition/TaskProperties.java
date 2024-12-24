package com.study.collect.core.task.definition;

// 任务配置属性

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "collect.task")
public class TaskProperties {
    private boolean enabled = true;  // 是否启用任务
    private int corePoolSize = 5;    // 核心线程数
    private int maxPoolSize = 10;    // 最大线程数
    private int queueCapacity = 100; // 队列容量
    private List<TaskDefinition> tasks = new ArrayList<>(); // 任务配置列表
}
