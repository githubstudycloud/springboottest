package com.study.collect.business.testcase.config;


import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.*;

@Configuration
public class ThreadPoolConfig {
    @Bean(name = "collectExecutor")
    public ExecutorService collectExecutor() {
        return new ThreadPoolExecutor(
                Runtime.getRuntime().availableProcessors() * 2,
                Runtime.getRuntime().availableProcessors() * 4,
                60L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(5000),
                new ThreadFactoryBuilder()
                        .setNameFormat("uri-collect-pool-%d")
                        .setDaemon(true)
                        .build(),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }
}
