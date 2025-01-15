package com.study.collect.business.testcase.constant;

/**
 * 集合相关常量
 */
public class CollectionConstants {
    // 集合前缀
    public static final String URI_COLLECTION_PREFIX = "uri_collect";

    // 批处理相关
    public static final int DEFAULT_BATCH_SIZE = 200;
    public static final int MAX_BATCH_SIZE = 1000;
    public static final int MIN_BATCH_SIZE = 50;

    // HTTP请求相关
    public static final int HTTP_MAX_REQUESTS_PER_MINUTE = 500;
    public static final int HTTP_CONNECT_TIMEOUT = 5000;
    public static final int HTTP_READ_TIMEOUT = 15000;
    public static final int HTTP_MAX_RETRY = 3;
    public static final long HTTP_RETRY_INTERVAL = 1000L;

    // 线程池相关
    public static final int CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 2;
    public static final int MAX_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 4;
    public static final int QUEUE_CAPACITY = 5000;
    public static final long KEEP_ALIVE_TIME = 60L;

    // MongoDB相关
    public static final int MONGO_BATCH_SIZE = 1000;
    public static final int MONGO_MAX_POOL_SIZE = 100;
    public static final int MONGO_MIN_POOL_SIZE = 20;

    // 对象池相关
    public static final int POOL_MAX_TOTAL = 20;
    public static final int POOL_MAX_IDLE = 10;
    public static final int POOL_MIN_IDLE = 5;

    // 任务相关
    public static final long TASK_TIMEOUT = 3600L;  // 单位：秒
    public static final int MAX_CONCURRENT_TASKS = 10;
    public static final int TASK_QUEUE_CAPACITY = 100;

    // 版本相关
    public static final String VERSION_PREFIX = "V";
    public static final String VERSION_SEPARATOR = "_";

    private CollectionConstants() {
        // 私有构造函数，防止实例化
    }
}