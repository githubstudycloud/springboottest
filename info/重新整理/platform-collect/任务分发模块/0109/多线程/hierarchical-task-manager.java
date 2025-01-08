public class HierarchicalTaskManager {
    private final ConcurrentHashMap<String, TaskExecutor<?>> taskExecutors = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, TaskConfig> taskConfigs = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, TaskMetrics> taskMetrics = new ConcurrentHashMap<>();
    
    @Data
    @Builder
    public static class TaskMetrics {
        private AtomicInteger activeThreads = new AtomicInteger(0);
        private AtomicInteger completedTasks = new AtomicInteger(0);
        private AtomicInteger failedTasks = new AtomicInteger(0);
        private AtomicLong totalProcessingTime = new AtomicLong(0);
        private Map<String, Object> customMetrics = new ConcurrentHashMap<>();
    }
    
    public interface TaskProcessor<T, R> {
        CompletableFuture<R> process(T input, TaskContext context);
    }
    
    @Data
    public static class TaskContext {
        private final String taskId;
        private final String parentTaskId;
        private final int level;
        private final TaskConfig config;
        private final TaskMetrics metrics;
        private final Map<String, Object> contextData = new ConcurrentHashMap<>();
    }
    
    public class TaskExecutor<T> {
        private final TaskConfig config;
        private final TaskMetrics metrics;
        private final ExecutorService executor;
        private final Semaphore throttle;
        private final RateLimiter rateLimiter;
        
        public TaskExecutor(TaskConfig config) {
            this.config = config;
            this.metrics = TaskMetrics.builder().build();
            this.executor = createExecutor(config);
            this.throttle = new Semaphore(config.getThrottleConfig().getConcurrencyLimit());
            this.rateLimiter = RateLimiter.create(config.getThrottleConfig().getMaxRequestsPerSecond());
        }
        
        private ExecutorService createExecutor(TaskConfig config) {
            return config.isUseVirtualThread() ?
                    Executors.newVirtualThreadPerTaskExecutor() :
                    new ThreadPoolExecutor(
                            config.getMaxConcurrency(),
                            config.getMaxConcurrency(),
                            0L, TimeUnit.MILLISECONDS,
                            new LinkedBlockingQueue<>(),
                            new ThreadPoolExecutor.CallerRunsPolicy()
                    );
        }
        
        public <R> CompletableFuture<List<R>> executeBatch(
                List<T> inputs,
                TaskProcessor<T, R> processor,
                TaskContext parentContext
        ) {
            List<CompletableFuture<R>> futures = new ArrayList<>();
            
            for (T input : inputs) {
                CompletableFuture<R> future = CompletableFuture.supplyAsync(() -> {
                    String taskId = UUID.randomUUID().toString();
                    TaskContext context = new TaskContext(taskId, parentContext.getTaskId(), 
                            parentContext.getLevel() + 1, config, metrics);
                    
                    metrics.getActiveThreads().incrementAndGet();
                    long startTime = System.nanoTime();
                    
                    try {
                        throttle.acquire();
                        rateLimiter.acquire();
                        
                        return processor.process(input, context)
                                .orTimeout(config.getTimeout().toMillis(), TimeUnit.MILLISECONDS)
                                .whenComplete((result, error) -> {
                                    metrics.getActiveThreads().decrementAndGet();
                                    if (error != null) {
                                        metrics.getFailedTasks().incrementAndGet();
                                    } else {
                                        metrics.getCompletedTasks().incrementAndGet();
                                    }
                                    metrics.getTotalProcessingTime()
                                            .addAndGet(System.nanoTime() - startTime);
                                    throttle.release();
                                })
                                .get();
                    } catch (Exception e) {
                        metrics.getFailedTasks().incrementAndGet();
                        throw new CompletionException(e);
                    }
                }, executor);
                
                futures.add(future);
            }
            
            return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .thenApply(v -> futures.stream()
                            .map(CompletableFuture::join)
                            .collect(Collectors.toList()));
        }
    }
    
    // 注册任务配置
    public void registerTask(String taskId, TaskConfig config) {
        taskConfigs.put(taskId, config);
        taskExecutors.put(taskId, new TaskExecutor<>(config));
        taskMetrics.put(taskId, TaskMetrics.builder().build());
    }
    
    // 更新任务配置
    public void updateTaskConfig(String taskId, TaskConfig newConfig) {
        TaskExecutor<?> oldExecutor = taskExecutors.get(taskId);
        if (oldExecutor != null) {
            taskConfigs.put(taskId, newConfig);
            taskExecutors.put(taskId, new TaskExecutor<>(newConfig));
        }
    }
    
    // 获取任务指标
    public TaskMetrics getTaskMetrics(String taskId) {
        return taskMetrics.get(taskId);
    }
    
    // 执行多层级任务示例方法
    public <T, R> CompletableFuture<List<R>> executeHierarchicalTask(
            String taskId,
            List<T> inputs,
            TaskProcessor<T, R> processor
    ) {
        TaskConfig config = taskConfigs.get(taskId);
        TaskContext context = new TaskContext(taskId, null, 0, config, taskMetrics.get(taskId));
        TaskExecutor<T> executor = (TaskExecutor<T>) taskExecutors.get(taskId);
        
        return executor.executeBatch(inputs, processor, context);
    }
}
