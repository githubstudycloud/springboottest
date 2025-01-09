@Service
@Slf4j
public class DataCollectionService {

    /**
     * 版本采集配置
     */
    @Data
    @Builder
    public static class VersionCollectionConfig {
        private int parentBatchSize;    // 大版本批次大小
        private int childBatchSize;     // 小版本批次大小
        private int pageBatchSize;      // 分页批次大小
        private Duration timeout;        // 超时时间
    }

    /**
     * 采集入口方法
     */
    public void collectData(List<String> versions, VersionCollectionConfig collectionConfig) {
        // 1. 创建执行器配置
        BatchTaskConfig config = BatchTaskConfig.builder()
            .corePoolSize(4)
            .maxPoolSize(20)
            .queueCapacity(1000)
            .useVirtualThread(true)
            .threadNamePrefix("data-collector-")
            .taskTimeout(collectionConfig.getTimeout())
            .shutdownTimeout(Duration.ofMinutes(10))
            // 配置并发限制
            .concurrencyConfig(ConcurrencyConfig.builder()
                .maxParentTasks(2)      // 同时最多处理2个大版本
                .maxChildTasks(5)       // 每个大版本下最多5个小版本并发
                .maxGrandChildTasks(10) // 每个小版本下最多10个分页并发
                .maxTotalTasks(100)     // 总任务数限制
                .build())
            // 配置监控
            .monitorConfig(MonitorConfig.builder()
                .enableMetrics(true)
                .enableTracing(true)
                .metricsInterval(Duration.ofSeconds(5))
                .metricsCallback(metrics -> {
                    log.info("Collection Progress: {}", metrics);
                })
                .build())
            // 配置重试
            .retryConfig(RetryConfig.builder()
                .maxRetries(3)
                .initialDelay(Duration.ofSeconds(1))
                .backoffMultiplier(2.0)
                .maxDelay(Duration.ofMinutes(1))
                .retryableExceptions(new Class[]{DataAccessException.class})
                .build())
            // 配置日志
            .logConsumer((level, message) -> 
                log.info("[{}] {}", level, message))
            // 配置异常处理
            .exceptionHandler(e -> 
                log.error("Task execution error", e))
            .build();

        // 2. 创建执行器
        try (BatchTaskExecutor executor = new BatchTaskExecutor(config)) {
            // 3. 执行大版本任务
            CompletableFuture<Void> future = executor.executeTask(
                "root-task",            // 任务ID
                versions,               // 版本列表
                collectionConfig.getParentBatchSize(), // 批次大小
                this::processParentVersion,  // 处理函数
                collectionConfig,       // 传递配置参数
                0                       // 层级(顶层)
            );

            // 4. 等待完成
            future.join();
        }
    }

    /**
     * 处理大版本
     */
    private void processParentVersion(List<String> versions, 
                                    VersionCollectionConfig config,
                                    TaskContext context,
                                    BatchTaskExecutor executor) {
        for (String version : versions) {
            // 1. 查询小版本列表
            List<String> minorVersions = queryMinorVersions(version);
            
            // 2. 处理小版本
            CompletableFuture<Void> future = executor.executeTask(
                "minor-" + version,     // 任务ID
                minorVersions,          // 小版本列表
                config.getChildBatchSize(), // 批次大小
                this::processMinorVersion,  // 处理函数
                config,                 // 配置参数
                1                       // 层级(第二层)
            );

            // 等待小版本处理完成
            future.join();
        }
    }

    /**
     * 处理小版本
     */
    private void processMinorVersion(List<String> minorVersions,
                                   VersionCollectionConfig config,
                                   TaskContext context,
                                   BatchTaskExecutor executor) {
        for (String minorVersion : minorVersions) {
            // 1. 获取数据总数
            long total = getDataTotal(minorVersion);
            
            // 2. 生成分页参数
            List<PageRequest> pages = generatePages(total, config.getPageBatchSize());
            
            // 3. 分页采集数据
            CompletableFuture<Void> future = executor.executeTask(
                "page-" + minorVersion, // 任务ID
                pages,                  // 分页参数列表
                config.getPageBatchSize(), // 批次大小
                this::processPage,      // 处理函数
                minorVersion,           // 传入版本号作为参数
                2                       // 层级(第三层)
            );

            // 等待分页处理完成
            future.join();
        }
    }

    /**
     * 处理分页数据
     */
    private void processPage(List<PageRequest> pages,
                           String minorVersion,
                           TaskContext context,
                           BatchTaskExecutor executor) {
        for (PageRequest page : pages) {
            // 执行实际的数据采集
            List<Data> dataList = collectData(minorVersion, page);
            
            // 处理采集到的数据
            processCollectedData(dataList);
        }
    }

    // 模拟方法 - 实际项目中需要实现具体逻辑
    private List<String> queryMinorVersions(String version) {
        return Arrays.asList(version + ".1", version + ".2", version + ".3");
    }

    private long getDataTotal(String minorVersion) {
        return 1000L;
    }

    private List<PageRequest> generatePages(long total, int pageSize) {
        int pages = (int) ((total + pageSize - 1) / pageSize);
        return IntStream.range(0, pages)
            .mapToObj(pageNum -> new PageRequest(pageNum, pageSize))
            .collect(Collectors.toList());
    }

    private List<Data> collectData(String minorVersion, PageRequest page) {
        // 模拟数据采集
        return IntStream.range(0, page.getPageSize())
            .mapToObj(i -> new Data())
            .collect(Collectors.toList());
    }

    private void processCollectedData(List<Data> dataList) {
        // 模拟数据处理
        log.info("Processing {} records", dataList.size());
    }
}
