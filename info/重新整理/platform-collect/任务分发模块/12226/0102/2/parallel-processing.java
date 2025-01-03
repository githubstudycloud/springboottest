// UriCollectServiceImpl.java 中的并行处理方法优化
private void processVersion(String rootNode, String version) {
    try {
        List<String> allUris = getAllUrisForVersion(version);
        int totalSize = allUris.size();
        int batchSize = 200;
        int totalBatches = (totalSize + batchSize - 1) / batchSize;
        
        // 创建所有批次的任务
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int i = 0; i < totalBatches; i++) {
            int start = i * batchSize;
            int end = Math.min(start + batchSize, totalSize);
            List<String> batch = allUris.subList(start, end);
            
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                processBatch(rootNode, version, batch);
            }, executorService);
            
            futures.add(future);
        }
        
        // 等待所有批次完成
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .exceptionally(throwable -> {
                log.error("Error processing version {}: {}", version, throwable.getMessage());
                return null;
            })
            .join();
            
        log.info("Completed processing version: {}, processed URI count: {}", 
                version, allUris.size());
    } catch (Exception e) {
        log.error("Error processing version: {}", version, e);
        throw new RuntimeException("Version processing failed", e);
    }
}

// 批次处理方法优化
private void processBatch(String rootNode, String version, List<String> uriBatch) {
    List<UriEntity> entities = new ArrayList<>(uriBatch.size());
    List<UriEntity> borrowedEntities = new ArrayList<>(uriBatch.size());
    
    try {
        // 获取URI详情
        List<Map<String, Object>> details = 
            retryWithBackoff(() -> httpService.getUriDetails(uriBatch));
        
        // 使用对象池获取实体对象并处理
        for (Map<String, Object> detail : details) {
            UriEntity entity = null;
            try {
                entity = entityPool.borrowObject();
                borrowedEntities.add(entity);
                fillEntity(entity, rootNode, version, detail);
                entities.add(entity);
            } catch (Exception e) {
                log.error("Error processing URI detail: {}", detail, e);
                if (entity != null) {
                    try {
                        entityPool.returnObject(entity);
                        borrowedEntities.remove(entity);
                    } catch (Exception ex) {
                        log.error("Error returning object to pool", ex);
                    }
                }
            }
        }
        
        // 批量保存
        if (!entities.isEmpty()) {
            repository.saveAll(entities);
        }
        
    } catch (Exception e) {
        log.error("Error processing URI batch of size {}", uriBatch.size(), e);
        throw new RuntimeException("Batch processing failed", e);
    } finally {
        // 确保所有借出的对象都返回池中
        borrowedEntities.forEach(entity -> {
            try {
                entityPool.returnObject(entity);
            } catch (Exception e) {
                log.error("Error returning object to pool", e);
            }
        });
    }
}

// 线程池配置优化
@Configuration
public class ThreadPoolConfig {
    @Bean(name = "collectExecutor")
    public ExecutorService collectExecutor() {
        int corePoolSize = Runtime.getRuntime().availableProcessors() * 2;
        int maxPoolSize = Runtime.getRuntime().availableProcessors() * 4;
        int queueCapacity = 10000;
        
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("uri-collect-pool-%d")
                .setDaemon(true)
                .build();
        
        return new ThreadPoolExecutor(
            corePoolSize,
            maxPoolSize,
            60L,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(queueCapacity),
            threadFactory,
            new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }
}