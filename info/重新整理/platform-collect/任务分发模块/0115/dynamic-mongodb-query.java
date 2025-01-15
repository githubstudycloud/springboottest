@Service
@Slf4j
public class DynamicUriService {
    
    @Autowired
    private MongoOperations mongoOperations;
    
    @Autowired
    private HashUtil hashUtil;  // 假设已有HashUtil工具类
    
    /**
     * 根据URI列表查询指定表中的detail字段
     * @param uris URI列表，格式为/a/b/c/d
     * @return 查询结果列表
     */
    public List<String> findDetailsByUris(List<String> uris) {
        if (CollectionUtils.isEmpty(uris)) {
            return new ArrayList<>();
        }
        
        // 1. 获取表名（假设所有URI属于同一个表）
        String tableName = determineTableName(uris.get(0));
        if (StringUtils.isEmpty(tableName)) {
            log.error("Cannot determine table name for URIs: {}", uris);
            return new ArrayList<>();
        }
        
        // 2. 将URI转换为uriHash
        List<String> uriHashes = uris.stream()
                .map(hashUtil::hashUri)
                .collect(Collectors.toList());
        
        // 3. 构建查询
        Query query = Query.query(Criteria.where("uriHash").in(uriHashes)
                .and("deleted").is(false));
        query.fields().include("detail").exclude("_id");
        
        // 4. 执行查询，指定具体的集合名称
        List<UriEntity> entities = mongoOperations.find(query, UriEntity.class, tableName);
        
        // 5. 提取并返回detail字段
        return entities.stream()
                .map(UriEntity::getDetail)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据URI确定表名
     * @param uri 输入URI
     * @return 表名
     */
    private String determineTableName(String uri) {
        try {
            // 获取当前数据库中的所有集合名称
            Set<String> collectionNames = mongoOperations.getCollectionNames();
            
            // 查找匹配的表名
            // 假设表名格式为: uri + "/a/b/c/d"
            return collectionNames.stream()
                .filter(collName -> uri.startsWith(collName.replace("/collection/", "/")))
                .findFirst()
                .orElse(null);
            
        } catch (Exception e) {
            log.error("Error determining table name for URI: {}", uri, e);
            return null;
        }
    }
    
    /**
     * HashUtil工具类示例
     */
    @Component
    public static class HashUtil {
        public String hashUri(String uri) {
            // 实现你的哈希算法
            // 例如: return DigestUtils.md5Hex(uri);
            return "";  // 需要替换为实际的哈希实现
        }
    }
}

// URI实体类示例
@Data
@Document
public class UriEntity {
    @Id
    private String id;
    private String uriHash;
    private String detail;
    private boolean deleted;
}
