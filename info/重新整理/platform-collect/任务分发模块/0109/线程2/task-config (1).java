import lombok.Data;
import lombok.Builder;
import lombok.experimental.Accessors;

import java.time.Duration;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Data
@Builder
@Accessors(chain = true)
public class BatchTaskConfig {
    // 线程池配置
    private int corePoolSize;
    private int maxPoolSize;
    private int queueCapacity;
    private boolean useVirtualThread;
    private String threadNamePrefix;
    private Duration taskTimeout;
    private Duration shutdownTimeout;
    
    // 并发限制配置
    private ConcurrencyConfig concurrencyConfig;
    
    // 监控配置
    private MonitorConfig monitorConfig;
    
    // 重试配置
    private RetryConfig retryConfig;
    
    // 日志和异常处理配置
    private BiConsumer<String, String> logConsumer;
    private Consumer<Throwable> exceptionHandler;
    
    public static BatchTaskConfig defaultConfig() {
        return BatchTaskConfig.builder()
            .corePoolSize(Runtime.getRuntime().availableProcessors())
            .maxPoolSize(Runtime.getRuntime().availableProcessors() * 2)
            .queueCapacity(1000)
            .useVirtualThread(true)
            .threadNamePrefix("batch-task-")
            .taskTimeout(Duration.ofMinutes(5))
            .shutdownTimeout(Duration.ofMinutes(10))
            .concurrencyConfig(ConcurrencyConfig.defaultConfig())
            .monitorConfig(MonitorConfig.defaultConfig())
            .retryConfig(RetryConfig.defaultConfig())
            .logConsumer((level, message) -> System.out.println("[" + level + "] " + message))
            .exceptionHandler(Throwable::printStackTrace)
            .build();
    }
}

@Data
@Builder
@Accessors(chain = true)
public class ConcurrencyConfig {
    private int maxParentTasks;     // 顶层任务最大并发数
    private int maxChildTasks;      // 子任务最大并发数
    private int maxGrandChildTasks; // 孙子任务最大并发数
    private int maxTotalTasks;      // 总任务数限制
    
    public static ConcurrencyConfig defaultConfig() {
        return ConcurrencyConfig.builder()
            .maxParentTasks(5)
            .maxChildTasks(10)
            .maxGrandChildTasks(20)
            .maxTotalTasks(100)
            .build();
    }
}

@Data
@Builder
@Accessors(chain = true)
public class MonitorConfig {
    private boolean enableMetrics;
    private boolean enableTracing;
    private Duration metricsInterval;
    private MetricsCallback metricsCallback;
    
    public static MonitorConfig defaultConfig() {
        return MonitorConfig.builder()
            .enableMetrics(true)
            .enableTracing(true)
            .metricsInterval(Duration.ofSeconds(5))
            .metricsCallback(new DefaultMetricsCallback())
            .build();
    }
    
    @FunctionalInterface
    public interface MetricsCallback {
        void onMetrics(TaskMetrics metrics);
    }
    
    private static class DefaultMetricsCallback implements MetricsCallback {
        @Override
        public void onMetrics(TaskMetrics metrics) {
            System.out.println("Task Metrics: " + metrics);
        }
    }
}

@Data
@Builder
@Accessors(chain = true)
public class RetryConfig {
    private int maxRetries;
    private Duration initialDelay;
    private double backoffMultiplier;
    private Duration maxDelay;
    private Class<? extends Throwable>[] retryableExceptions;
    
    @SuppressWarnings("unchecked")
    public static RetryConfig defaultConfig() {
        return RetryConfig.builder()
            .maxRetries(3)
            .initialDelay(Duration.ofSeconds(1))
            .backoffMultiplier(2.0)
            .maxDelay(Duration.ofMinutes(1))
            .retryableExceptions(new Class[]{Exception.class})
            .build();
    }
}
