@Repository
@Slf4j
public class UriRepository extends BaseMongoRepository<UriEntity> {
    private final MongoOperations mongoOperations;

    // 返回类型的枚举，为Map类型指定完整的泛型类型
    public enum ReturnType {
        ENTITY(UriEntity.class),
        DETAILS(new TypeReference<Map<String, Object>>() {}.getType()),
        URI_HASH(String.class);

        private final Type type;

        ReturnType(Type type) {
            this.type = type;
        }

        ReturnType(Class<?> type) {
            this.type = type;
        }

        public Type getType() {
            return type;
        }
    }

    /**
     * 类型安全的查询方法
     */
    @SuppressWarnings("unchecked")
    public <T> PageResult<T> flexibleQuery(QueryCondition condition, ReturnType returnType) {
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

            try (MongoCursor<Document> cursor = collection.aggregate(pipeline).iterator()) {
                while (cursor.hasNext()) {
                    Document doc = cursor.next();
                    switch (returnType) {
                        case ENTITY:
                            items.add((T) mongoOperations.getConverter().read(UriEntity.class, doc));
                            break;
                        case DETAILS:
                            // 显式转换为Map<String, Object>
                            if (doc.get("details") instanceof Document) {
                                items.add((T) new HashMap<>((Document) doc.get("details")));
                            } else {
                                items.add((T) doc.get("details"));
                            }
                            break;
                        case URI_HASH:
                            items.add((T) doc.getString("uriHash"));
                            break;
                    }
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

    // 帮助方法，使用显式类型
    public PageResult<String> findUriHashes(QueryCondition condition) {
        return flexibleQuery(condition, ReturnType.URI_HASH);
    }

    public PageResult<Map<String, Object>> findDetails(QueryCondition condition) {
        return flexibleQuery(condition, ReturnType.DETAILS);
    }

    public PageResult<UriEntity> findEntities(QueryCondition condition) {
        return flexibleQuery(condition, ReturnType.ENTITY);
    }

    /**
     * 返回多个字段
     */
    public PageResult<Map<String, Object>> findCustomFields(QueryCondition condition, String... fields) {
        MongoCollection<Document> collection = mongoOperations.getCollection(
                mongoOperations.getCollectionName(UriEntity.class));

        Document query = buildQuery(condition);
        Document projection = new Document();
        for (String field : fields) {
            projection.append(field, 1);
        }
        projection.append("_id", 0);

        List<Document> pipeline = new ArrayList<>();
        pipeline.add(new Document("$match", query));
        pipeline.add(new Document("$project", projection));

        if (condition.getPage() != null && condition.getSize() != null) {
            pipeline.add(new Document("$skip", (long) (condition.getPage() - 1) * condition.getSize()));
            pipeline.add(new Document("$limit", condition.getSize()));
        }

        try {
            long total = collection.countDocuments(query);
            List<Map<String, Object>> items = new ArrayList<>();

            try (MongoCursor<Document> cursor = collection.aggregate(pipeline).iterator()) {
                while (cursor.hasNext()) {
                    Document doc = cursor.next();
                    items.add(new HashMap<>(doc));
                }
            }

            return PageResult.<Map<String, Object>>builder()
                    .total(total)
                    .page(condition.getPage())
                    .size(condition.getSize())
                    .totalPages(condition.getSize() == null ? 1 : 
                              (int) Math.ceil((double) total / condition.getSize()))
                    .items(items)
                    .build();

        } catch (Exception e) {
            log.error("Error executing custom fields query", e);
            throw new RuntimeException("Query execution failed", e);
        }
    }
}