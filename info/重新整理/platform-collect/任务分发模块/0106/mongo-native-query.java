@Repository
@Slf4j
public class UriRepository extends BaseMongoRepository<UriEntity> {
    private final MongoOperations mongoOperations;

    /**
     * 使用原生命令条件分页查询uriHash
     * @param rootNode 根节点
     * @param version 版本
     * @param versionType 版本类型
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @return uriHash列表
     */
    public List<String> findUriHashesNativeWithPage(String rootNode, 
                                                   String version, 
                                                   String versionType, 
                                                   int page, 
                                                   int size) {
        MongoCollection<Document> collection = mongoOperations.getCollection(
                mongoOperations.getCollectionName(UriEntity.class));

        // 构建查询条件
        Document query = new Document();
        if (rootNode != null) {
            query.append("rootNode", rootNode);
        }
        if (version != null) {
            query.append("uriVersion", version);
        }
        if (versionType != null) {
            query.append("versionType", versionType);
        }

        // 构建聚合管道
        List<Document> pipeline = Arrays.asList(
            new Document("$match", query),
            new Document("$project", new Document("uriHash", 1).append("_id", 0)),
            new Document("$skip", (long) (page - 1) * size),
            new Document("$limit", size)
        );

        try {
            return collection.aggregate(pipeline)
                    .map(doc -> doc.getString("uriHash"))
                    .into(new ArrayList<>());
        } catch (Exception e) {
            log.error("Error executing native query", e);
            throw new RuntimeException("Query execution failed", e);
        }
    }

    /**
     * 获取满足条件的总数
     */
    public long countUriHashesNative(String rootNode, String version, String versionType) {
        MongoCollection<Document> collection = mongoOperations.getCollection(
                mongoOperations.getCollectionName(UriEntity.class));

        Document query = new Document();
        if (rootNode != null) {
            query.append("rootNode", rootNode);
        }
        if (version != null) {
            query.append("uriVersion", version);
        }
        if (versionType != null) {
            query.append("versionType", versionType);
        }

        return collection.countDocuments(query);
    }

    /**
     * 查询并返回分页结果
     */
    public PageResult<String> findUriHashesPage(String rootNode, 
                                              String version, 
                                              String versionType, 
                                              int page, 
                                              int size) {
        long total = countUriHashesNative(rootNode, version, versionType);
        List<String> items = findUriHashesNativeWithPage(rootNode, version, versionType, page, size);
        
        return PageResult.<String>builder()
                .total(total)
                .page(page)
                .size(size)
                .totalPages((int) Math.ceil((double) total / size))
                .items(items)
                .build();
    }

    /**
     * 如果数据量很大，使用流式处理
     */
    public void streamUriHashesNative(String rootNode, 
                                    String version, 
                                    String versionType,
                                    Consumer<String> consumer) {
        MongoCollection<Document> collection = mongoOperations.getCollection(
                mongoOperations.getCollectionName(UriEntity.class));

        Document query = new Document();
        if (rootNode != null) {
            query.append("rootNode", rootNode);
        }
        if (version != null) {
            query.append("uriVersion", version);
        }
        if (versionType != null) {
            query.append("versionType", versionType);
        }

        List<Document> pipeline = Arrays.asList(
            new Document("$match", query),
            new Document("$project", new Document("uriHash", 1).append("_id", 0))
        );

        try (MongoCursor<Document> cursor = collection.aggregate(pipeline).iterator()) {
            while (cursor.hasNext()) {
                consumer.accept(cursor.next().getString("uriHash"));
            }
        }
    }
}

@Data
@Builder
public class PageResult<T> {
    private long total;       // 总记录数
    private int page;         // 当前页码
    private int size;         // 每页大小
    private int totalPages;   // 总页数
    private List<T> items;    // 当前页数据
}