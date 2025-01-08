@Data
@Builder
public class TaskConfig {
    private String taskId;                      // 任务ID
    private String parentTaskId;                // 父任务ID
    private int level;                          // 任务层级
    private int maxConcurrency;                 // 最大并发数
    private boolean useVirtualThread;           // 是否使用虚拟线程
    private Duration timeout;                   // 任务超时时间
    private RetryConfig retryConfig;            // 重试配置
    private ThrottleConfig throttleConfig;      // 限流配置
    private Map<String, Object> extraParams;    // 扩展参数
    
    @Data
    @Builder
    public static class RetryConfig {
        private int maxRetries;
        private Duration retryDelay;
        private List<Class<? extends Exception>> retryableExceptions;
    }
    
    @Data
    @Builder
    public static class ThrottleConfig {
        private int maxRequestsPerSecond;
        private int concurrencyLimit;
        private Duration cooldownPeriod;
    }
}
