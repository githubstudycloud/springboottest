@Repository
@Slf4j
public class UriRepository extends BaseMongoRepository<UriEntity> {
    private final MongoOperations mongoOperations;

    @Data
    @Builder
    public static class QueryCondition {
        private Collection<String> ids;         // 支持多个ID
        private Collection<String> uriHashes;   // 支持多个uriHash
        private Collection<String> uris;        // 支持多个uri
        private Collection<String> uriVersions; // 支持多个版本
        private Collection<String> versionTypes;// 支持多个版本类型
        private String rootNode;                // 根节点
        private Boolean deleted;                // 删除标记
        private Integer page;                   // 页码
        private Integer size;                   // 每页大小
    }

    /**
     * 灵活查询方法
     * @param condition 查询条件
     * @param returnType 返回类型 "entity"完整实体/"details"只返回details/"uriHash"等字段名
     * @return PageResult包含查询结果
     */
    public <T> PageResult<T> flexibleQuery(QueryCondition condition, String returnType) {
        MongoCollection<Document> collection = mongoOperations.getCollection(
                mongoOperations.getCollectionName(UriEntity.class));

        // 构建查询条件
        Document query = buildQuery(condition);
        
        // 构建投影
        Document projection = buildProjection(returnType);

        // 构建聚合管道
        List<Document> pipeline = new ArrayList<>();
        pipeline.add(new Document("$match", query));
        pipeline.add(new Document("$project", projection));

        // 如果需要分页
        if (condition.getPage() != null && condition.getSize() != null) {
            pipeline.add(new Document("$skip", (long) (condition.getPage() - 1) * condition.getSize()));
            pipeline.add(new Document("$limit", condition.getSize()));
        }

        try {
            // 计算总数
            long total = collection.countDocuments(query);

            // 执行查询
            List<T> items = collection.aggregate(pipeline)
                    .map(doc -> convertDocument(doc, returnType))
                    .into(new ArrayList<>());

            return PageResult.<T>builder()
                    .total(total)
                    .page(condition.getPage())
                    .size(condition.getSize())
                    .totalPages(condition.getSize() == null ? 1 : 
                              (int) Math.ceil((double) total / condition.getSize()))
                    .items(items)
                    .build();

        } catch (Exception e) {
            log.error("Error executing flexible query", e);
            throw new RuntimeException("Query execution failed", e);
        }
    }

    private Document buildQuery(QueryCondition condition) {
        Document query = new Document();
        
        if (condition.getIds() != null && !condition.getIds().isEmpty()) {
            query.append("_id", new Document("$in", condition.getIds()));
        }
        
        if (condition.getUriHashes() != null && !condition.getUriHashes().isEmpty()) {
            query.append("uriHash", new Document("$in", condition.getUriHashes()));
        }
        
        if (condition.getUris() != null && !condition.getUris().isEmpty()) {
            query.append("uri", new Document("$in", condition.getUris()));
        }
        
        if (condition.getUriVersions() != null && !condition.getUriVersions().isEmpty()) {
            query.append("uriVersion", new Document("$in", condition.getUriVersions()));
        }
        
        if (condition.getVersionTypes() != null && !condition.getVersionTypes().isEmpty()) {
            query.append("versionType", new Document("$in", condition.getVersionTypes()));
        }
        
        if (condition.getRootNode() != null) {
            query.append("rootNode", condition.getRootNode());
        }
        
        if (condition.getDeleted() != null) {
            query.append("deleted", condition.getDeleted());
        }
        
        return query;
    }

    private Document buildProjection(String returnType) {
        Document projection = new Document();
        
        switch (returnType.toLowerCase()) {
            case "entity":
                // 返回所有字段
                return new Document();
            case "details":
                projection.append("details", 1);
                break;
            default:
                // 返回指定字段
                projection.append(returnType, 1);
        }
        
        projection.append("_id", 0);
        return projection;
    }

    @SuppressWarnings("unchecked")
    private <T> T convertDocument(Document doc, String returnType) {
        switch (returnType.toLowerCase()) {
            case "entity":
                return (T) mongoOperations.getConverter().read(UriEntity.class, doc);
            case "details":
                return (T) doc.get("details");
            default:
                return (T) doc.get(returnType);
        }
    }
}
