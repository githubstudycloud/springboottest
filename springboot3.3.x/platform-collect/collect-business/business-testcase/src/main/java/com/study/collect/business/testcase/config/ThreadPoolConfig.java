package com.study.collect.business.testcase.config;

import com.study.collect.business.testcase.common.constants.CollectionConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池配置
 */
@Configuration
@EnableAsync
@Slf4j
public class ThreadPoolConfig {

    /**
     * HTTP请求线程池
     */
    @Bean(name = "httpExecutor")
    public ThreadPoolTaskExecutor httpExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(CollectionConstants.ThreadPool.HTTP_CORE_SIZE);
        executor.setMaxPoolSize(CollectionConstants.ThreadPool.HTTP_MAX_SIZE);
        executor.setQueueCapacity(CollectionConstants.ThreadPool.HTTP_QUEUE_SIZE);
        executor.setKeepAliveSeconds((int) CollectionConstants.ThreadPool.HTTP_KEEP_ALIVE);
        executor.setThreadNamePrefix("http-thread-");
        executor.setRejectedExecutionHandler((r, e) -> {
            log.warn("HTTP thread pool is full, task rejected");
            throw new RejectedExecutionException("HTTP thread pool is full");
        });
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        return executor;
    }

    /**
     * MongoDB操作线程池
     */
    @Bean(name = "mongoExecutor")
    public ThreadPoolTaskExecutor mongoExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(CollectionConstants.ThreadPool.MONGO_CORE_SIZE);
        executor.setMaxPoolSize(CollectionConstants.ThreadPool.MONGO_MAX_SIZE);
        executor.setQueueCapacity(CollectionConstants.ThreadPool.MONGO_QUEUE_SIZE);
        executor.setKeepAliveSeconds((int) CollectionConstants.ThreadPool.MONGO_KEEP_ALIVE);
        executor.setThreadNamePrefix("mongo-thread-");
        executor.setRejectedExecutionHandler((r, e) -> {
            log.warn("MongoDB thread pool is full, task rejected");
            throw new RejectedExecutionException("MongoDB thread pool is full");
        });
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        return executor;
    }

    /**
     * 任务处理线程池
     */
    @Bean(name = "taskExecutor")
    @Primary
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(CollectionConstants.ThreadPool.TASK_CORE_SIZE);
        executor.setMaxPoolSize(CollectionConstants.ThreadPool.TASK_MAX_SIZE);
        executor.setQueueCapacity(CollectionConstants.ThreadPool.TASK_QUEUE_SIZE);
        executor.setKeepAliveSeconds((int) CollectionConstants.ThreadPool.TASK_KEEP_ALIVE);
        executor.setThreadNamePrefix("task-thread-");
        executor.setRejectedExecutionHandler((r, e) -> {
            log.warn("Task thread pool is full, task rejected");
            throw new RejectedExecutionException("Task thread pool is full");
        });
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        return executor;
    }

    /**
     * 通用任务调度线程池
     */
    @Bean(name = "scheduledExecutor")
    public ScheduledExecutorService scheduledExecutor() {
        return Executors.newScheduledThreadPool(2, new ThreadFactory() {
            private final AtomicInteger counter = new AtomicInteger(1);
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("scheduled-thread-" + counter.getAndIncrement());
                thread.setDaemon(true);
                return thread;
            }
        });
    }

    /**
     * 虚拟线程池(如果JDK版本支持)
     */
    @Bean(name = "virtualThreadExecutor")
    public ExecutorService virtualThreadExecutor() {
        try {
            // 尝试使用虚拟线程
            return Executors.newVirtualThreadPerTaskExecutor();
        } catch (UnsupportedOperationException e) {
            // 降级使用普通线程池
            log.warn("Virtual threads not supported, falling back to normal thread pool");
            return new ThreadPoolExecutor(
                    Runtime.getRuntime().availableProcessors(),
                    Runtime.getRuntime().availableProcessors() * 2,
                    60L, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(1000),
                    new ThreadFactory() {
                        private final AtomicInteger counter = new AtomicInteger(1);
                        @Override
                        public Thread newThread(Runnable r) {
                            Thread thread = new Thread(r);
                            thread.setName("fallback-thread-" + counter.getAndIncrement());
                            return thread;
                        }
                    }
            );
        }
    }
}