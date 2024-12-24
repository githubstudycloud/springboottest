package com.study.collect.core.task.config;

import com.study.collect.core.task.definition.TaskProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableConfigurationProperties(TaskProperties.class)
public class TaskConfiguration {

    @Bean
    public ThreadPoolTaskScheduler taskScheduler(TaskProperties properties) {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(properties.getCorePoolSize());
        scheduler.setThreadNamePrefix("TaskScheduler-");
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(60);
        return scheduler;
    }
}