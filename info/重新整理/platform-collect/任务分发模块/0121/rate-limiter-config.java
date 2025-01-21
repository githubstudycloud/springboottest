@Configuration
public class RateLimitConfig {
    
    @Bean
    public RateLimiter mongoRateLimiter() {
        return new RateLimiter(
            2000,  // 每分钟允许2000次操作，考虑到分片集群的处理能力
            TimeUnit.MINUTES
        );
    }

    @Bean
    public RateLimiter httpRateLimiter() {
        return new RateLimiter(
            1000,  // 每分钟允许1000次HTTP请求
            TimeUnit.MINUTES
        );
    }
}

// 修改后的RateLimiter类
@Slf4j
@Component
public class RateLimiter {
    private final int permitsPerUnit;
    private final TimeUnit timeUnit;
    private final ConcurrentLinkedQueue<Long> timestamps;
    private final AtomicInteger currentPermits;
    private final ScheduledExecutorService scheduler;

    public RateLimiter(int permitsPerUnit, TimeUnit timeUnit) {
        this.permitsPerUnit = permitsPerUnit;
        this.timeUnit = timeUnit;
        this.timestamps = new ConcurrentLinkedQueue<>();
        this.currentPermits = new AtomicInteger(0);
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r);
            thread.setName("rate-limiter-cleaner");
            thread.setDaemon(true);
            return thread;
        });

        // 定期清理过期的时间戳
        scheduler.scheduleAtFixedRate(
            this::cleanup,
            1,
            1,
            timeUnit
        );
    }

    public void acquire() throws InterruptedException {
        while (!tryAcquire()) {
            Thread.sleep(50); // 缩短等待时间到50ms以提高响应性
        }
    }

    public boolean tryAcquire() {
        cleanup();
        long now = System.currentTimeMillis();
        
        // 添加突发流量处理
        int burstCapacity = (int) (permitsPerUnit * 1.2); // 允许20%的突发流量
        
        if (currentPermits.get() >= burstCapacity) {
            return false;
        }

        if (currentPermits.incrementAndGet() <= burstCapacity) {
            timestamps.offer(now);
            return true;
        } else {
            currentPermits.decrementAndGet();
            return false;
        }
    }

    private void cleanup() {
        long now = System.currentTimeMillis();
        long timeWindow = timeUnit.toMillis(1);
        long cutoff = now - timeWindow;

        // 移除超出时间窗口的时间戳
        while (!timestamps.isEmpty() && timestamps.peek() < cutoff) {
            timestamps.poll();
            currentPermits.decrementAndGet();
        }
    }

    // 监控方法
    public double getCurrentQPS() {
        cleanup();
        return currentPermits.get() / (timeUnit.toSeconds(1));
    }

    public int getAvailablePermits() {
        cleanup();
        return permitsPerUnit - currentPermits.get();
    }

    public Map<String, Object> getMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("currentPermits", currentPermits.get());
        metrics.put("availablePermits", getAvailablePermits());
        metrics.put("qps", getCurrentQPS());
        metrics.put("timeUnit", timeUnit.name());
        metrics.put("permitsPerUnit", permitsPerUnit);
        return metrics;
    }
}

// 在常量类中更新限流相关的配置
public class CollectionConstants {
    // MongoDB操作相关
    public static final int MONGO_BATCH_SIZE = 1000;               // 批量操作大小
    public static final int MONGO_MAX_POOL_SIZE = 100;            // 连接池最大连接数
    public static final int MONGO_MIN_POOL_SIZE = 20;             // 连接池最小连接数
    
    // HTTP请求相关
    public static final int HTTP_MAX_REQUESTS_PER_MINUTE = 1000;  // 每分钟最大HTTP请求数
    public static final int HTTP_CONNECT_TIMEOUT = 10000;         // 连接超时时间（毫秒）
    public static final int HTTP_READ_TIMEOUT = 15000;           // 读取超时时间（毫秒）
    public static final int HTTP_MAX_RETRY = 3;                  // 最大重试次数
    public static final long HTTP_RETRY_INTERVAL = 1000L;        // 重试间隔（毫秒）

    // 批处理相关
    public static final int DEFAULT_BATCH_SIZE = 500;            // 默认批处理大小
    public static final int MAX_BATCH_SIZE = 2000;              // 最大批处理大小
    public static final int MIN_BATCH_SIZE = 100;               // 最小批处理大小
}