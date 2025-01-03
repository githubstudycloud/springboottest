@Configuration
public class ThreadPoolConfig {
    @Bean(name = "collectExecutor")
    public ExecutorService collectExecutor() {
        return new ThreadPoolExecutor(
            2,                     // 核心线程数
            16,                    // 最大线程数
            60L, TimeUnit.SECONDS, // 线程空闲超时
            new LinkedBlockingQueue<>(100000), // 大容量队列
            new ThreadFactoryBuilder()
                .setNameFormat("uri-collect-pool-%d")
                .setDaemon(true)
                .build(),
            new ThreadPoolExecutor.CallerRunsPolicy());
    }
}

@Service
@Slf4j
public class UriCollectServiceImpl implements UriCollectService {
    private final UriHttpService httpService;
    private final UriRepository repository;
    private final ObjectPool<UriEntity> entityPool;
    private final ExecutorService executorService;
    private final CountDownLatch latch;
    private static final int BATCH_SIZE = 200;
    
    @Override
    public void collectData(CollectParam param) {
        try {
            // 获取所有版本
            List<String> allVersions = getAllVersions(param.getRootNode());
            
            // 任务总数（用于创建CountDownLatch）
            AtomicInteger totalTasks = new AtomicInteger(0);
            
            // 预处理：计算总任务数
            for (String version : allVersions) {
                List<String> uris = getAllUrisForVersion(version);
                int batchCount = (uris.size() + BATCH_SIZE - 1) / BATCH_SIZE;
                totalTasks.addAndGet(batchCount);
            }
            
            // 创建CountDownLatch
            CountDownLatch latch = new CountDownLatch(totalTasks.get());
            
            // 处理所有版本
            for (String version : allVersions) {
                processVersion(param.getRootNode(), version, latch);
            }
            
            // 等待所有任务完成
            if (!latch.await(1, TimeUnit.HOURS)) {
                log.warn("Data collection timeout after 1 hour");
            }
            
        } catch (Exception e) {
            log.error("Error in data collection", e);
            throw new RuntimeException("Data collection failed", e);
        }
    }
    
    private void processVersion(String rootNode, String version, CountDownLatch latch) {
        try {
            List<String> allUris = getAllUrisForVersion(version);
            
            // 分批提交任务
            for (int i = 0; i < allUris.size(); i += BATCH_SIZE) {
                final int start = i;
                final int end = Math.min(start + BATCH_SIZE, allUris.size());
                List<String> batch = allUris.subList(start, end);
                
                executorService.execute(() -> {
                    try {
                        processBatch(rootNode, version, batch);
                    } finally {
                        latch.countDown();
                    }
                });
            }
        } catch (Exception e) {
            log.error("Error processing version: {}", version, e);
            throw new RuntimeException("Version processing failed", e);
        }
    }
    
    private void processBatch(String rootNode, String version, List<String> uriBatch) {
        List<UriEntity> entities = new ArrayList<>(uriBatch.size());
        List<UriEntity> borrowedEntities = new ArrayList<>(uriBatch.size());
        
        try {
            // 批量获取URI详情
            List<Map<String, Object>> details = httpService.getUriDetails(uriBatch);
            
            // 批量处理
            for (Map<String, Object> detail : details) {
                UriEntity entity = null;
                try {
                    entity = entityPool.borrowObject();
                    borrowedEntities.add(entity);
                    fillEntity(entity, rootNode, version, detail);
                    entities.add(entity);
                } catch (Exception e) {
                    if (entity != null) {
                        entityPool.returnObject(entity);
                        borrowedEntities.remove(entity);
                    }
                }
            }
            
            // 批量保存（如果有数据）
            if (!entities.isEmpty()) {
                repository.saveAll(entities);
            }
            
        } catch (Exception e) {
            log.error("Batch processing error for version: {}, batch size: {}", 
                     version, uriBatch.size(), e);
        } finally {
            // 归还对象到对象池
            borrowedEntities.forEach(entity -> {
                try {
                    entityPool.returnObject(entity);
                } catch (Exception e) {
                    log.error("Error returning object to pool", e);
                }
            });
        }
    }

    private void fillEntity(UriEntity entity, String rootNode, String version, Map<String, Object> detail) {
        try {
            entity.setUri((String) detail.get("uri"));
            entity.setRootNode(rootNode);
            entity.setVersionType(getVersionType(version));
            entity.setUriVersion(version);
            entity.setDetails(detail);
        } catch (Exception e) {
            log.error("Error filling entity: {}", detail, e);
            throw new RuntimeException("Entity filling failed", e);
        }
    }
}