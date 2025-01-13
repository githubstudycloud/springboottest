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