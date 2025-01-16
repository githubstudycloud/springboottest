package com.study.collect.business.testcase.config;

import com.study.collect.business.testcase.common.constants.CollectionConstants;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "collect.testcase")
public class TestCaseCollectorProperties {
    // 基本配置
    private int batchSize = CollectionConstants.Process.DEFAULT_BATCH_SIZE;
    private int threadCount = Runtime.getRuntime().availableProcessors() * 2;
    private int retryTimes = CollectionConstants.Http.MAX_RETRY;
    private int timeout = (int) CollectionConstants.Process.TASK_TIMEOUT;

    // HTTP配置
    private int httpMaxRequestsPerMinute = CollectionConstants.Http.MAX_REQUESTS_PER_MINUTE;
    private int httpConnectTimeout = CollectionConstants.Http.CONNECT_TIMEOUT;
    private int httpReadTimeout = CollectionConstants.Http.READ_TIMEOUT;
    private int httpRetryInterval = (int) CollectionConstants.Http.RETRY_INTERVAL;

    // MongoDB配置
    private int mongoMinPoolSize = CollectionConstants.Database.MONGO_MIN_POOL_SIZE;
    private int mongoMaxPoolSize = CollectionConstants.Database.MONGO_MAX_POOL_SIZE;
    private int mongoBatchSize = CollectionConstants.Database.MONGO_BATCH_SIZE;

    // 任务配置
    private int maxConcurrentTasks = CollectionConstants.Process.MAX_CONCURRENT_TASKS;
    private int taskQueueCapacity = CollectionConstants.Process.TASK_QUEUE_CAPACITY;
    private long taskTimeoutSeconds = CollectionConstants.Process.TASK_TIMEOUT;

    // 对象池配置
    private int poolMaxTotal = CollectionConstants.Pool.MAX_TOTAL;
    private int poolMaxIdle = CollectionConstants.Pool.MAX_IDLE;
    private int poolMinIdle = CollectionConstants.Pool.MIN_IDLE;

    // 版本配置
    private String versionPrefix = CollectionConstants.Collection.VERSION_PREFIX;
    private String versionSeparator = CollectionConstants.Collection.VERSION_SEPARATOR;

    // 增量同步配置
    private boolean enableIncrementalSync = true;
    private boolean enableHardDelete = false;
    private int cleanupBatchSize = CollectionConstants.Process.DEFAULT_BATCH_SIZE;
    private int cleanupThreads = Runtime.getRuntime().availableProcessors();
}