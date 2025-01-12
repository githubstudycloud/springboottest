@Service
@Slf4j
public class UriBatchService {
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    /**
     * 批量upsert处理
     * @param entities 待处理的实体列表
     * @return 处理结果
     */
    public BulkWriteResult batchUpsert(List<UriEntity> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return null;
        }
        
        List<WriteModel<Document>> writeModels = new ArrayList<>(entities.size());
        
        for (UriEntity entity : entities) {
            // 确保生成uriHash
            if (entity.getUriHash() == null && entity.getUri() != null) {
                entity.setUriHash(HashUtil.hash(entity.getUri()));
            }
            
            // 构建查询条件 - 使用复合唯一索引
            Document filter = new Document()
                .append("uri", entity.getUri())
                .append("rootNode", entity.getRootNode())
                .append("versionType", entity.getVersionType())
                .append("uriVersion", entity.getUriVersion());
            
            // 构建更新文档
            Document update = new Document("$set", new Document()
                .append("uriHash", entity.getUriHash())
                .append("uri", entity.getUri())
                .append("rootNode", entity.getRootNode())
                .append("versionType", entity.getVersionType())
                .append("uriVersion", entity.getUriVersion())
                .append("details", entity.getDetails())
                .append("version", 0L));
                
            // 创建updateOne操作
            UpdateOneModel<Document> updateModel = new UpdateOneModel<>(
                filter,
                update,
                new UpdateOptions().upsert(true)
            );
            
            writeModels.add(updateModel);
        }
        
        try {
            // 执行批量写入
            BulkWriteOptions bulkWriteOptions = new BulkWriteOptions().ordered(false);
            return mongoTemplate.getCollection("uri_collect")
                .bulkWrite(writeModels, bulkWriteOptions);
        } catch (MongoBulkWriteException e) {
            log.error("Bulk write partially failed: {}", e.getMessage());
            // 处理部分失败的情况
            return e.getWriteResult();
        } catch (Exception e) {
            log.error("Batch upsert failed", e);
            throw new RuntimeException("Failed to process batch upsert", e);
        }
    }
    
    /**
     * 使用重试机制的批量upsert
     */
    @Retryable(
        value = { MongoSocketException.class, MongoTimeoutException.class },
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public BulkWriteResult batchUpsertWithRetry(List<UriEntity> entities) {
        return batchUpsert(entities);
    }
}

// 使用示例
@RestController
@RequestMapping("/api/uri")
public class UriController {
    
    @Autowired
    private UriBatchService uriBatchService;
    
    @PostMapping("/batch")
    public ResponseEntity<?> batchUpsert(@RequestBody List<UriEntity> entities) {
        // 分批处理，每批200条
        int batchSize = 200;
        List<BulkWriteResult> results = new ArrayList<>();
        
        for (int i = 0; i < entities.size(); i += batchSize) {
            int end = Math.min(i + batchSize, entities.size());
            List<UriEntity> batch = entities.subList(i, end);
            
            BulkWriteResult result = uriBatchService.batchUpsertWithRetry(batch);
            results.add(result);
        }
        
        return ResponseEntity.ok(results);
    }
}
