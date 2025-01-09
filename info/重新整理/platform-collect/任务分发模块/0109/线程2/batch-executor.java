import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class BatchTaskExecutor implements AutoCloseable {
    private final BatchTaskConfig config;
    private final ThreadPoolTaskExecutor executor;
    private final Map<String, TaskContext> taskContexts = new ConcurrentHashMap<>();
    private final Map<String, Future<?>> taskFutures = new ConcurrentHashMap<>();
    private final TaskMetrics metrics = new TaskMetrics();
    private final ScheduledExecutorService monitorExecutor = Executors.newSingleThreadScheduledExecutor();
    
    // 分层信号量
    private final Semaphore parentSemaphore;
    private final Semaphore childSemaphore;
    private final Semaphore grandChildSemaphore;
    private final Semaphore totalTasksSemaphore;
    
    public BatchTaskExecutor(BatchTaskConfig config) {
        this.config = config;
        this.executor = createExecutor();
        this.parentSemaphore = new Semaphore(config.getConcurrencyConfig().getMaxParentTasks());
        this.childSemaphore = new Semaphore(config.getConcurrencyConfig().getMaxChildTasks());
        this.grandChildSemaphore = new Semaphore(config.getConcurrencyConfig().getMaxGrandChildTasks());
        this.totalTasksSemaphore = new Semaphore(config.getConcurrencyConfig().getMaxTotalTasks());
        
        startMetricsMonitor();
    }
    
    private ThreadPoolTaskExecutor createExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(config.getCorePoolSize());
        executor.setMaxPoolSize(config.getMaxPoolSize());
        executor.setQueueCapacity(config.getQueueCapacity());
        executor.setThreadNamePrefix(config.getThreadNamePrefix());
        
        if (config.isUseVirtualThread()) {
            executor.setTaskExecutor(Executors.newVirtualThreadPerTaskExecutor());
        }
        
        executor.initialize();
        return executor;
    }
    
    private void startMetricsMonitor() {
        if (config.getMonitorConfig().isEnableMetrics()) {
            monitorExecutor.scheduleAtFixedRate(
                () -> config.getMonitorConfig().getMetricsCallback().onMetrics(metrics),
                0,
                config.getMonitorConfig().getMetricsInterval().toMillis(),
                TimeUnit.MILLISECONDS
            );
        }
    }
    
    public <T, P> CompletableFuture<Void> executeTask(
            String taskId,
            List<T> tasks,
            int batchSize,
            BatchTask<T, P> task,
            P params,
            int level
    ) {
        TaskContext context = new TaskContext(taskId, level);
        taskContexts.put(taskId, context);
        
        return CompletableFuture.runAsync(() -> {
            try {
                acquireSemaphore(level);
                metrics.recordTaskStart(level);
                
                List<List<T>> batches = splitIntoBatches(tasks, batchSize);
                List<CompletableFuture<Void>> batchFutures = new ArrayList<>();
                
                for (List<T> batch : batches) {
                    CompletableFuture<Void> batchFuture = executeBatchWithRetry(batch, task, params, context);
                    batchFutures.add(batchFuture);
                }
                
                CompletableFuture.allOf(batchFutures.toArray(new CompletableFuture[0])).join();
                
            } catch (Exception e) {
                context.setError(e);
                throw e;
            } finally {
                Duration executionTime = Duration.between(context.getStartTime(), Instant.now());
                metrics.recordTaskEnd(level, executionTime, context.getError() == null);
                releaseSemaphore(level);
                taskContexts.remove(taskId);
            }
        }, executor);
    }
    
    private <T, P> CompletableFuture<Void> executeBatchWithRetry(
            List<T> batch,
            BatchTask<T, P> task,
            P params,
            TaskContext context
    ) {
        AtomicReference<Throwable> lastError = new AtomicReference<>();
        Ret