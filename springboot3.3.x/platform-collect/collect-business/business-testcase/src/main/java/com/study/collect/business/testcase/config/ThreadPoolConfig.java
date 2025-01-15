package com.study.collect.business.testcase.config;

import com.study.collect.business.testcase.constant.CollectionConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.*;

@Configuration
@EnableAsync
@Slf4j
public class ThreadPoolConfig {

    @Bean(name = "collectExecutor")
    public ExecutorService collectExecutor() {
        return new ThreadPoolExecutor(
                CollectionConstants.CORE_POOL_SIZE,
                CollectionConstants.MAX_POOL_SIZE,
                CollectionConstants.KEEP_ALIVE_TIME,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(CollectionConstants.QUEUE_CAPACITY),
                new ThreadFactory() {
                    private int count = 0;
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread thread = new Thread(r);
                        thread.setName("collect-thread-" + count++);
                        thread.setDaemon(true);
                        return thread;
                    }
                },
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    @Bean(name = "httpExecutor")
    public ThreadPoolTaskExecutor httpExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(CollectionConstants.CORE_POOL_SIZE);
        executor.setMaxPoolSize(CollectionConstants.MAX_POOL_SIZE);
        executor.setQueueCapacity(CollectionConstants.QUEUE_CAPACITY);
        executor.setKeepAliveSeconds((int)CollectionConstants.KEEP_ALIVE_TIME);
        executor.setThreadNamePrefix("http-thread-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        return executor;
    }

    @Bean(name = "mongoExecutor")
    public ThreadPoolTaskExecutor mongoExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(CollectionConstants.CORE_POOL_SIZE);
        executor.setMaxPoolSize(CollectionConstants.MAX_POOL_SIZE);
        executor.setQueueCapacity(CollectionConstants.QUEUE_CAPACITY);
        executor.setKeepAliveSeconds((int)CollectionConstants.KEEP_ALIVE_TIME);
        executor.setThreadNamePrefix("mongo-thread-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        return executor;
    }

    @Bean(name = "taskExecutor")
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(CollectionConstants.MAX_CONCURRENT_TASKS);
        executor.setMaxPoolSize(CollectionConstants.MAX_CONCURRENT_TASKS);
        executor.setQueueCapacity(CollectionConstants.TASK_QUEUE_CAPACITY);
        executor.setThreadNamePrefix("task-thread-");
        executor.setRejectedExecutionHandler((r, e) -> {
            log.warn("Task queue is full, task rejected");
            throw new RejectedExecutionException("Task queue is full");
        });
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        return executor;
    }

    @Bean
    public ScheduledExecutorService scheduledExecutor() {
        return Executors.newScheduledThreadPool(2, r -> {
            Thread thread = new Thread(r);
            thread.setName("scheduled-thread");
            thread.setDaemon(true);
            return thread;
        });
    }
}