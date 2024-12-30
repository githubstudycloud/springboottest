import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

@Slf4j
@Component
@RequiredArgsConstructor
public class UriCleaner {
    
    private final MongoTemplate mongoTemplate;
    
    /**
     * 删除MongoDB中存在但是validUris中不存在的URI
     * @param collectionName 集合名称
     * @param validUris 有效的URI列表
     * @return 删除的数量
     */
    public long cleanInvalidUris(String collectionName, List<String> validUris) {
        // 1. 获取MongoDB中所有的URIs
        Query query = new Query();
        query.fields().include("uri");
        List<String> existingUris = mongoTemplate.findDistinct(query, "uri", collectionName, String.class);
        
        // 2. 转换validUris为Set以提高查找效率
        Set<String> validUriSet = new HashSet<>(validUris);
        
        // 3. 找出需要删除的URIs
        List<String> urisToDelete = existingUris.stream()
                .filter(uri -> !validUriSet.contains(uri))
                .toList();
        
        // 4. 如果没有需要删除的URI，直接返回
        if (urisToDelete.isEmpty()) {
            log.info("No URIs need to be deleted from collection: {}", collectionName);
            return 0;
        }
        
        // 5. 构建删除查询
        Query deleteQuery = Query.query(Criteria.where("uri").in(urisToDelete));
        
        // 6. 执行删除操作
        long deletedCount = mongoTemplate.remove(deleteQuery, collectionName).getDeletedCount();
        
        log.info("Deleted {} documents with invalid URIs from collection: {}", deletedCount, collectionName);
        
        return deletedCount;
    }
    
    /**
     * 获取将被删除的URI列表（预览）
     * @param collectionName 集合名称
     * @param validUris 有效的URI列表
     * @return 将被删除的URI列表
     */
    public List<String> getUrisToBeDeleted(String collectionName, List<String> validUris) {
        Query query = new Query();
        query.fields().include("uri");
        List<String> existingUris = mongoTemplate.findDistinct(query, "uri", collectionName, String.class);
        
        Set<String> validUriSet = new HashSet<>(validUris);
        
        return existingUris.stream()
                .filter(uri -> !validUriSet.contains(uri))
                .toList();
    }
}
