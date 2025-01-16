package com.study.collect.business.testcase.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.study.collect.business.testcase.common.constants.CollectionConstants;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Configuration
@EnableAsync
@RequiredArgsConstructor
public class ThreadPoolConfig {

    private final TestCaseCollectorProperties properties;
    private final MeterRegistry meterRegistry;

    /**
     * HTTP请求线程池
     */
    @Bean(name = "httpExecutor")
    public ThreadPoolTaskExecutor httpExecutor() {
        ThreadPoolTaskExecutor executor = createBaseExecutor(
                "http-executor",
                properties.getThreadPool().getHttpCoreSize(),
                properties.getThreadPool().getHttpMaxSize(),
                properties.getThreadPool().getHttpQueueSize()
        );
        // 配置任务装饰器，用于监控和统计
        executor.setTaskDecorator(new MonitoringTaskDecorator("http"));
        return executor;
    }

    /**
     * MongoDB操作线程池
     */
    @Bean(name = "mongoExecutor")
    public ThreadPoolTaskExecutor mongoExecutor() {
        ThreadPoolTaskExecutor executor = createBaseExecutor(
                "mongo-executor",
                properties.getThreadPool().getMongoCoreSize(),
                properties.getThreadPool().getMongoMaxSize(),
                properties.getThreadPool().getMongoQueueSize()
        );
        executor.setTaskDecorator(new MonitoringTaskDecorator("mongo"));
        return executor;
    }

    /**
     * 任务处理线程池
     */
    @Bean(name = "taskExecutor")
    @Primary
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = createBaseExecutor(
                "task-executor",
                properties.getThreadPool().getTaskCoreSize(),
                properties.getThreadPool().getTaskMaxSize(),
                properties.getThreadPool().getTaskQueueSize()
        );
        executor.setTaskDecorator(new MonitoringTaskDecorator("task"));
        // 自定义拒绝策略：记录日志并重试
        executor.setRejectedExecutionHandler(new RetryRejectedExecutionHandler());
        return executor;
    }

    /**
     * 虚拟线程执行器（如果JDK版本支持）
     */
    @Bean(name = "virtualThreadExecutor")
    public ExecutorService virtualThreadExecutor() {
        if (properties.getThreadPool().isEnableVirtualThread()) {
            try {
                return Executors.newVirtualThreadPerTaskExecutor();
            } catch (UnsupportedOperationException e) {
                log.warn("Virtual threads not supported, falling back to normal thread pool");
            }
        }
        return createFallbackExecutor();
    }

    /**
     * 调度线程池
     */
    @Bean(name = "scheduledExecutor")
    public ScheduledExecutorService scheduledExecutor() {
        return new ScheduledThreadPoolExecutor(
                2,
                new ThreadFactoryBuilder()
                        .setNameFormat("scheduled-thread-%d")
                        .setDaemon(true)
                        .build(),
                (r, e) -> log.error("Task rejected from scheduler", new RejectedExecutionException())
        );
    }

    /**
     * 创建基础线程池配置
     */
    private ThreadPoolTaskExecutor createBaseExecutor(
            String threadNamePrefix,
            int coreSize,
            int maxSize,
            int queueCapacity
    ) {
        ThreadPoolTaskExecutor executor = new MonitoredThreadPoolTaskExecutor(meterRegistry, threadNamePrefix);
        executor.setCorePoolSize(coreSize);
        executor.setMaxPoolSize(maxSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds((int)CollectionConstants.ThreadPool.HTTP_KEEP_ALIVE);
        executor.setThreadNamePrefix(threadNamePrefix + "-");
        executor.setAllowCoreThreadTimeOut(true);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        return executor;
    }

    /**
     * 创建降级线程池
     */
    private ExecutorService createFallbackExecutor() {
        return new ThreadPoolExecutor(
                Runtime.getRuntime().availableProcessors(),
                Runtime.getRuntime().availableProcessors() * 2,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000),
                new ThreadFactoryBuilder()
                        .setNameFormat("fallback-thread-%d")
                        .build(),
                new RetryRejectedExecutionHandler()
        );
    }
}

/**
 * 可监控的线程池
 */
@Slf4j
class MonitoredThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {
    private final MeterRegistry meterRegistry;
    private final String poolName;

    public MonitoredThreadPoolTaskExecutor(MeterRegistry meterRegistry, String poolName) {
        this.meterRegistry = meterRegistry;
        this.poolName = poolName;
    }

    @Override
    public void initialize() {
        super.initialize();
        // 注册监控指标
        registerMetrics();
    }

    private void registerMetrics() {
        meterRegistry.gauge(poolName + ".pool.size", this, ThreadPoolTaskExecutor::getPoolSize);
        meterRegistry.gauge(poolName + ".active.count", this, ThreadPoolTaskExecutor::getActiveCount);
        meterRegistry.gauge(poolName + ".queue.size", this, executor ->
//                ((ThreadPoolExecutor) executor).getQueue().size());
                this.getThreadPoolExecutor().getQueue().size());
    }
}

/**
 * 任务监控装饰器
 */
@Slf4j
class MonitoringTaskDecorator implements TaskDecorator {
    private final String poolName;
    private final Map<String, AtomicInteger> taskCounters = new ConcurrentHashMap<>();

    public MonitoringTaskDecorator(String poolName) {
        this.poolName = poolName;
    }

    @Override
    public Runnable decorate(Runnable runnable) {
        String taskName = runnable.getClass().getSimpleName();
        return () -> {
            long startTime = System.currentTimeMillis();
            try {
                incrementTaskCount(taskName);
                runnable.run();
            } finally {
                decrementTaskCount(taskName);
                recordTaskDuration(taskName, System.currentTimeMillis() - startTime);
            }
        };
    }

    private void incrementTaskCount(String taskName) {
        taskCounters.computeIfAbsent(taskName, k -> new AtomicInteger(0))
                .incrementAndGet();
    }

    private void decrementTaskCount(String taskName) {
        taskCounters.get(taskName).decrementAndGet();
    }

    private void recordTaskDuration(String taskName, long duration) {
        log.debug("[{}] Task {} completed in {}ms", poolName, taskName, duration);
    }
}

/**
 * 重试拒绝策略
 */
@Slf4j
class RetryRejectedExecutionHandler implements RejectedExecutionHandler {
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY = 100; // ms

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        int retries = 0;
        while (retries < MAX_RETRIES) {
            try {
                if (!executor.isShutdown()) {
                    Thread.sleep(RETRY_DELAY * (long)Math.pow(2, retries));
                    executor.getQueue().put(r);
                    return;
                }
                break;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                retries++;
                if (retries == MAX_RETRIES) {
                    log.error("Task rejected after {} retries", MAX_RETRIES, e);
                    throw new RejectedExecutionException("Task rejected after " + MAX_RETRIES + " retries", e);
                }
            }
        }
    }
}