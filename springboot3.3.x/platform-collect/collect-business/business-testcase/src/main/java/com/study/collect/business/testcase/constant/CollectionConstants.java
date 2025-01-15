package com.study.collect.business.testcase.common.constants;

/**
 * 系统常量配置
 */
public final class CollectionConstants {

    // 集合相关
    public static final class Collection {
        public static final String URI_COLLECTION_PREFIX = "uri_collect";
        public static final String VERSION_PREFIX = "V";
        public static final String VERSION_SEPARATOR = "_";

        private Collection() {}
    }

    // HTTP相关
    public static final class Http {
        public static final int MAX_REQUESTS_PER_MINUTE = 500;
        public static final int CONNECT_TIMEOUT = 5000;
        public static final int READ_TIMEOUT = 15000;
        public static final int MAX_RETRY = 3;
        public static final long RETRY_INTERVAL = 1000L;

        private Http() {}
    }

    // 线程池相关
    public static final class ThreadPool {
        // HTTP请求线程池
        public static final int HTTP_CORE_SIZE = Runtime.getRuntime().availableProcessors() * 2;
        public static final int HTTP_MAX_SIZE = Runtime.getRuntime().availableProcessors() * 4;
        public static final int HTTP_QUEUE_SIZE = 5000;
        public static final long HTTP_KEEP_ALIVE = 60L;

        // MongoDB操作线程池
        public static final int MONGO_CORE_SIZE = Runtime.getRuntime().availableProcessors();
        public static final int MONGO_MAX_SIZE = Runtime.getRuntime().availableProcessors() * 2;
        public static final int MONGO_QUEUE_SIZE = 10000;
        public static final long MONGO_KEEP_ALIVE = 60L;

        // 任务处理线程池
        public static final int TASK_CORE_SIZE = 5;
        public static final int TASK_MAX_SIZE = 10;
        public static final int TASK_QUEUE_SIZE = 100;
        public static final long TASK_KEEP_ALIVE = 60L;

        private ThreadPool() {}
    }

    // 数据库相关
    public static final class Database {
        public static final int MONGO_BATCH_SIZE = 1000;
        public static final int MONGO_MAX_POOL_SIZE = 100;
        public static final int MONGO_MIN_POOL_SIZE = 20;

        private Database() {}
    }

    // 处理相关
    public static final class Process {
        public static final int DEFAULT_BATCH_SIZE = 200;
        public static final int MAX_BATCH_SIZE = 1000;
        public static final int MIN_BATCH_SIZE = 50;
        public static final long TASK_TIMEOUT = 3600L;
        public static final int MAX_CONCURRENT_TASKS = 10;
        public static final int TASK_QUEUE_CAPACITY = 100;

        private Process() {}
    }

    // 对象池相关
    public static final class Pool {
        public static final int MAX_TOTAL = 20;
        public static final int MAX_IDLE = 10;
        public static final int MIN_IDLE = 5;

        private Pool() {}
    }

    private CollectionConstants() {}
}