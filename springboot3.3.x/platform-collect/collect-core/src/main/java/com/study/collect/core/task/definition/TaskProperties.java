package com.study.collect.core.task.definition;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "collect.task")
public class TaskProperties {

    /**
     * 是否启用任务调度
     */
    private boolean enabled = true;

    /**
     * 线程池配置
     */
    private ThreadPool threadPool = new ThreadPool();

    /**
     * 执行配置
     */
    private Execution execution = new Execution();

    @Data
    public static class ThreadPool {
        /**
         * 核心线程数
         */
        private int coreSize = 10;

        /**
         * 最大线程数
         */
        private int maxSize = 20;

        /**
         * 队列容量
         */
        private int queueCapacity = 200;

        /**
         * 线程空闲超时时间（秒）
         */
        private int keepAliveSeconds = 60;

        /**
         * 优雅停机等待时间（秒）
         */
        private int awaitTerminationSeconds = 60;
    }

    @Data
    public static class Execution {
        /**
         * 任务超时时间（秒）
         */
        private int timeout = 3600;

        /**
         * 重试次数
         */
        private int retryTimes = 3;

        /**
         * 重试间隔（秒）
         */
        private int retryInterval = 300;

        /**
         * 是否允许并行执行
         */
        private boolean allowParallel = true;
    }
}