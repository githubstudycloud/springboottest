import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class UriCleaner {
    
    private final MongoTemplate mongoTemplate;
    private static final int PAGE_SIZE = 1000; // 每页处理的文档数量
    
    /**
     * 删除MongoDB中存在但是validUris中不存在的URI（分页处理）
     * @param collectionName 集合名称
     * @param validUris 有效的URI列表
     * @return 删除的总数量
     */
    public long cleanInvalidUris(String collectionName, List<String> validUris) {
        Set<String> validUriSet = new HashSet<>(validUris);
        long totalDeleted = 0;
        int page = 0;
        
        while (true) {
            // 1. 分页获取URI
            List<String> batchUris = getUrisBatch(collectionName, page, PAGE_SIZE);
            if (batchUris.isEmpty()) {
                break;
            }
            
            // 2. 找出此批次中需要删除的URI
            List<String> urisToDelete = batchUris.stream()
                    .filter(uri -> !validUriSet.contains(uri))
                    .toList();
            
            // 3. 如果有需要删除的URI，执行删除
            if (!urisToDelete.isEmpty()) {
                Query deleteQuery = Query.query(Criteria.where("uri").in(urisToDelete));
                long deletedCount = mongoTemplate.remove(deleteQuery, collectionName).getDeletedCount();
                totalDeleted += deletedCount;
                
                log.info("Batch {}: Deleted {} documents with invalid URIs", page, deletedCount);
            }
            
            page++;
        }
        
        log.info("Total deleted {} documents from collection: {}", totalDeleted, collectionName);
        return totalDeleted;
    }
    
    /**
     * 获取将被删除的URI列表（分页预览）
     * @param collectionName 集合名称
     * @param validUris 有效的URI列表
     * @return 将被删除的URI列表
     */
    public List<String> getUrisToBeDeleted(String collectionName, List<String> validUris) {
        Set<String> validUriSet = new HashSet<>(validUris);
        List<String> urisToDelete = new ArrayList<>();
        int page = 0;
        
        while (true) {
            List<String> batchUris = getUrisBatch(collectionName, page, PAGE_SIZE);
            if (batchUris.isEmpty()) {
                break;
            }
            
            batchUris.stream()
                    .filter(uri -> !validUriSet.contains(uri))
                    .forEach(urisToDelete::add);
            
            page++;
        }
        
        log.info("Found {} URIs to be deleted from collection: {}", urisToDelete.size(), collectionName);
        return urisToDelete;
    }
    
    /**
     * 分页获取URI列表
     */
    private List<String> getUrisBatch(String collectionName, int page, int pageSize) {
        Query query = new Query()
                .with(Sort.by(Sort.Direction.ASC, "uri"))
                .skip((long) page * pageSize)
                .limit(pageSize);
        query.fields().include("uri");
        
        return mongoTemplate.findDistinct(query, "uri", collectionName, String.class);
    }
    
    /**
     * 获取集合中的总记录数
     */
    public long getCollectionCount(String collectionName) {
        return mongoTemplate.count(new Query(), collectionName);
    }
    
    /**
     * 使用游标方式处理大量数据（更高效的方式）
     */
    public long cleanInvalidUrisWithCursor(String collectionName, List<String> validUris) {
        Set<String> validUriSet = new HashSet<>(validUris);
        long totalDeleted = 0;
        
        // 创建一个只查询uri字段的查询
        Query query = new Query();
        query.fields().include("uri");
        
        try {
            // 使用游标遍历所有文档
            try (var cursor = mongoTemplate.stream(query, Document.class, collectionName)) {
                List<String> batchUrisToDelete = new ArrayList<>();
                int batchSize = 0;
                
                while (cursor.hasNext()) {
                    Document doc = cursor.next();
                    String uri = doc.getString("uri");
                    
                    if (uri != null && !validUriSet.contains(uri)) {
                        batchUrisToDelete.add(uri);
                        batchSize++;
                        
                        // 当积累了足够的批次时执行删除
                        if (batchSize >= PAGE_SIZE) {
                            totalDeleted += deleteBatch(collectionName, batchUrisToDelete);
                            batchUrisToDelete.clear();
                            batchSize = 0;
                        }
                    }
                }
                
                // 处理最后一个不完整的批次
                if (!batchUrisToDelete.isEmpty()) {
                    totalDeleted += deleteBatch(collectionName, batchUrisToDelete);
                }
            }
        } catch (Exception e) {
            log.error("Error processing cursor", e);
            throw new RuntimeException("Failed to process cursor", e);
        }
        
        log.info("Total deleted {} documents from collection: {}", totalDeleted, collectionName);
        return totalDeleted;
    }
    
    private long deleteBatch(String collectionName, List<String> urisToDelete) {
        Query deleteQuery = Query.query(Criteria.where("uri").in(urisToDelete));
        long deletedCount = mongoTemplate.remove(deleteQuery, collectionName).getDeletedCount();
        log.info("Deleted batch of {} documents", deletedCount);
        return deletedCount;
    }
}
