@Repository
@Slf4j
public class UriRepository extends BaseMongoRepository<UriEntity> {
    private final MongoOperations mongoOperations;

    // 定义返回类型的枚举
    public enum ReturnType {
        ENTITY(UriEntity.class),
        DETAILS(Map.class),
        URI_HASH(String.class);

        private final Class<?> type;

        ReturnType(Class<?> type) {
            this.type = type;
        }

        public Class<?> getType() {
            return type;
        }
    }

    /**
     * 类型安全的查询方法
     */
    @SuppressWarnings("unchecked")
    public <T> PageResult<T> flexibleQuery(QueryCondition condition, ReturnType returnType, Class<T> resultType) {
        if (!returnType.getType().isAssignableFrom(resultType)) {
            throw new IllegalArgumentException("Result type does not match return type");
        }

        MongoCollection<Document> collection = mongoOperations.getCollection(
                mongoOperations.getCollectionName(UriEntity.class));

        Document query = buildQuery(condition);
        Document projection = buildProjection(returnType);

        List<Document> pipeline = new ArrayList<>();
        pipeline.add(new Document("$match", query));
        pipeline.add(new Document("$project", projection));

        if (condition.getPage() != null && condition.getSize() != null) {
            pipeline.add(new Document("$skip", (long) (condition.getPage() - 1) * condition.getSize()));
            pipeline.add(new Document("$limit", condition.getSize()));
        }

        try {
            long total = collection.countDocuments(query);
            List<T> items = new ArrayList<>();

            AggregateIterable<Document> results = collection.aggregate(pipeline);
            for (Document doc : results) {
                switch (returnType) {
                    case ENTITY:
                        items.add((T) mongoOperations.getConverter().read(UriEntity.class, doc));
                        break;
                    case DETAILS:
                        items.add((T) doc.get("details"));
                        break;
                    case URI_HASH:
                        items.add((T) doc.getString("uriHash"));
                        break;
                }
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

    private Document buildProjection(ReturnType returnType) {
        Document projection = new Document();
        switch (returnType) {
            case ENTITY:
                return new Document();  // 返回所有字段
            case DETAILS:
                projection.append("details", 1);
                break;
            case URI_HASH:
                projection.append("uriHash", 1);
                break;
        }
        projection.append("_id", 0);
        return projection;
    }

    // 类型安全的帮助方法
    public PageResult<String> findUriHashes(QueryCondition condition) {
        return flexibleQuery(condition, ReturnType.URI_HASH, String.class);
    }

    public PageResult<Map<String, Object>> findDetails(QueryCondition condition) {
        // 这里使用原始类型Map，因为Map<String, Object>在运行时会被擦除
        @SuppressWarnings("unchecked")
        PageResult<Map<String, Object>> result = 
            flexibleQuery(condition, ReturnType.DETAILS, Map.class);
        return result;
    }

    public PageResult<UriEntity> findEntities(QueryCondition condition) {
        return flexibleQuery(condition, ReturnType.ENTITY, UriEntity.class);
    }
}