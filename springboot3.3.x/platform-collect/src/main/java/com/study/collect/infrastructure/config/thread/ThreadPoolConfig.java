package com.study.collect.infrastructure.config.thread;

// 线程池配置
import jodd.util.concurrent.ThreadFactoryBuilder;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

/**
 * 线程池配置
 */
@Configuration
public class ThreadPoolConfig {

    @Bean
    @ConfigurationProperties(prefix = "collect.thread-pool")
    public ThreadPoolProperties threadPoolProperties() {
        return new ThreadPoolProperties();
    }

    @Bean
    public ExecutorService taskExecutor(ThreadPoolProperties properties) {
        return new ThreadPoolExecutor(
                properties.getCorePoolSize(),
                properties.getMaxPoolSize(),
                properties.getKeepAliveTime(),
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(properties.getQueueCapacity()),
                new ThreadFactoryBuilder()
                        .setNameFormat("task-pool-%d")
                        .setDaemon(true)
                        .build(),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    @Bean
    public ScheduledExecutorService scheduledExecutor() {
        return new ScheduledThreadPoolExecutor(
                1,
                new ThreadFactoryBuilder()
                        .setNameFormat("scheduler-%d")
                        .setDaemon(true)
                        .build()
        );
    }

    @Data
    public static class ThreadPoolProperties {
        private int corePoolSize = 10;
        private int maxPoolSize = 20;
        private int queueCapacity = 200;
        private int keepAliveTime = 60;
    }
}
