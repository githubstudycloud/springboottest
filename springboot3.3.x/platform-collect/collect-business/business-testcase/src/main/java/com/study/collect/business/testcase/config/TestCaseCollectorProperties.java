package com.study.collect.business.testcase.config;

import com.study.collect.business.testcase.common.constants.CollectionConstants;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
// TestCaseCollectorProperties 修改建议
@Data
@Validated
@ConfigurationProperties(prefix = "collect.testcase")
public class TestCaseCollectorProperties {

    // HTTP相关配置
    private final Http http = new Http();

    // MongoDB相关配置
    private final Mongo mongo = new Mongo();

    // 线程池相关配置
    private final ThreadPool threadPool = new ThreadPool();

    // 任务相关配置
    private final Task task = new Task();

    // URI采集相关配置
    private final Collect collect = new Collect();

    @Data
    public static class Http {
        @Min(100)
        @Max(1000)
        private int maxRequestsPerMinute = CollectionConstants.Http.MAX_REQUESTS_PER_MINUTE;

        @Min(1000)
        @Max(30000)
        private int connectTimeout = CollectionConstants.Http.CONNECT_TIMEOUT;

        @Min(1000)
        @Max(60000)
        private int readTimeout = CollectionConstants.Http.READ_TIMEOUT;

        @Min(0)
        @Max(10)
        private int maxRetries = CollectionConstants.Http.MAX_RETRY;

        @Min(100)
        @Max(5000)
        private long retryInterval = CollectionConstants.Http.RETRY_INTERVAL;
    }

    @Data
    public static class Mongo {
        @Min(10)
        @Max(200)
        private int minPoolSize = CollectionConstants.Database.MONGO_MIN_POOL_SIZE;

        @Min(50)
        @Max(500)
        private int maxPoolSize = CollectionConstants.Database.MONGO_MAX_POOL_SIZE;

        @Min(100)
        @Max(5000)
        private int batchSize = CollectionConstants.Database.MONGO_BATCH_SIZE;

        private boolean enableSharding = false;
        private String shardKey = "rootNode";
        private String defaultCollectionPrefix = "uri_collect";
    }

    @Data
    public static class ThreadPool {
        // HTTP线程池配置
        @Min(1)
        @Max(100)
        private int httpCoreSize = CollectionConstants.ThreadPool.HTTP_CORE_SIZE;

        @Min(1)
        @Max(200)
        private int httpMaxSize = CollectionConstants.ThreadPool.HTTP_MAX_SIZE;

        @Min(100)
        @Max(10000)
        private int httpQueueSize = CollectionConstants.ThreadPool.HTTP_QUEUE_SIZE;

        // MongoDB线程池配置
        @Min(1)
        @Max(50)
        private int mongoCoreSize = CollectionConstants.ThreadPool.MONGO_CORE_SIZE;

        @Min(1)
        @Max(100)
        private int mongoMaxSize = CollectionConstants.ThreadPool.MONGO_MAX_SIZE;

        @Min(100)
        @Max(20000)
        private int mongoQueueSize = CollectionConstants.ThreadPool.MONGO_QUEUE_SIZE;

        // 任务线程池配置
        @Min(1)
        @Max(20)
        private int taskCoreSize = CollectionConstants.ThreadPool.TASK_CORE_SIZE;

        @Min(1)
        @Max(50)
        private int taskMaxSize = CollectionConstants.ThreadPool.TASK_MAX_SIZE;

        @Min(10)
        @Max(1000)
        private int taskQueueSize = CollectionConstants.ThreadPool.TASK_QUEUE_SIZE;

        // 是否启用虚拟线程
        private boolean enableVirtualThread = true;
    }

    @Data
    public static class Task {
        @Min(1)
        @Max(50)
        private int maxConcurrentTasks = CollectionConstants.Process.MAX_CONCURRENT_TASKS;

        @Min(10)
        @Max(1000)
        private int queueCapacity = CollectionConstants.Process.TASK_QUEUE_CAPACITY;

        @Min(60)
        @Max(86400)
        private long timeout = CollectionConstants.Process.TASK_TIMEOUT;

        // 任务优先级相关
        private boolean enablePriority = true;
        private int defaultPriority = 0;
        private int maxPriority = 10;

        // 重试相关
        private boolean enableRetry = true;
        private int maxRetries = 3;
        private long retryDelay = 1000;
    }

    @Data
    public static class Collect {
        @Min(50)
        @Max(2000)
        private int defaultBatchSize = CollectionConstants.Process.DEFAULT_BATCH_SIZE;

        @Min(10)
        @Max(1000)
        private int minBatchSize = CollectionConstants.Process.MIN_BATCH_SIZE;

        @Min(100)
        @Max(5000)
        private int maxBatchSize = CollectionConstants.Process.MAX_BATCH_SIZE;

        // 增量同步相关
        private boolean enableIncremental = true;
        private boolean defaultHardDelete = false;

        // 对象池相关
        private int poolMaxTotal = CollectionConstants.Pool.MAX_TOTAL;
        private int poolMaxIdle = CollectionConstants.Pool.MAX_IDLE;
        private int poolMinIdle = CollectionConstants.Pool.MIN_IDLE;
    }

    /**
     * 运行时动态更新配置
     */
    public void updateHttpConfig(Http newConfig) {
        BeanUtils.copyProperties(newConfig, this.http);
    }

    public void updateThreadPoolConfig(ThreadPool newConfig) {
        BeanUtils.copyProperties(newConfig, this.threadPool);
    }

    // 其他配置更新方法...
}