@Repository
@Slf4j
public class UriRepository extends BaseMongoRepository<UriEntity> {
    private final MongoOperations mongoOperations;

    /**
     * 灵活查询方法
     */
    @SuppressWarnings("unchecked")
    public <T> PageResult<T> flexibleQuery(QueryCondition condition, String returnType) {
        MongoCollection<Document> collection = mongoOperations.getCollection(
                mongoOperations.getCollectionName(UriEntity.class));

        Document query = buildQuery(condition);
        Document projection = buildProjection(returnType);

        // 构建聚合管道
        List<Document> pipeline = new ArrayList<>();
        pipeline.add(new Document("$match", query));
        pipeline.add(new Document("$project", projection));

        if (condition.getPage() != null && condition.getSize() != null) {
            pipeline.add(new Document("$skip", (long) (condition.getPage() - 1) * condition.getSize()));
            pipeline.add(new Document("$limit", condition.getSize()));
        }

        try {
            long total = collection.countDocuments(query);

            // 根据返回类型执行不同的查询
            List<T> items;
            if ("entity".equalsIgnoreCase(returnType)) {
                // 返回完整实体
                items = (List<T>) collection.aggregate(pipeline)
                    .map(doc -> mongoOperations.getConverter().read(UriEntity.class, doc))
                    .into(new ArrayList<UriEntity>());
            } else if ("details".equalsIgnoreCase(returnType)) {
                // 返回details字段，是一个Map
                items = (List<T>) collection.aggregate(pipeline)
                    .map(doc -> doc.get("details"))
                    .into(new ArrayList<Map<String, Object>>());
            } else {
                // 返回单个字段，如uriHash
                items = (List<T>) collection.aggregate(pipeline)
                    .map(doc -> doc.get(returnType))
                    .into(new ArrayList<String>());
            }

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

    // 使用示例的帮助方法
    public PageResult<String> findUriHashes(QueryCondition condition) {
        return flexibleQuery(condition, "uriHash");
    }

    public PageResult<Map<String, Object>> findDetails(QueryCondition condition) {
        return flexibleQuery(condition, "details");
    }

    public PageResult<UriEntity> findEntities(QueryCondition condition) {
        return flexibleQuery(condition, "entity");
    }
}