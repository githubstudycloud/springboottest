@Repository
@Slf4j
public class UriRepository extends BaseMongoRepository<UriEntity> {
    private final MongoOperations mongoOperations;

    /**
     * 简单查询 details 字段返回字符串列表
     */
    public List<String> findDetailsList(QueryCondition condition) {
        MongoCollection<Document> collection = mongoOperations.getCollection(
                mongoOperations.getCollectionName(UriEntity.class));

        // 构建查询条件
        Document query = new Document();
        if (condition.getUriHashes() != null && !condition.getUriHashes().isEmpty()) {
            query.append("uriHash", new Document("$in", condition.getUriHashes()));
        }
        if (condition.getRootNode() != null) {
            query.append("rootNode", condition.getRootNode());
        }

        // 只取 details 字段
        Document projection = new Document("details", 1).append("_id", 0);

        List<String> detailsList = new ArrayList<>();
        try {
            collection.find(query)
                    .projection(projection)
                    .forEach(doc -> {
                        Object details = doc.get("details");
                        if (details != null) {
                            detailsList.add(details.toString());
                        }
                    });
        } catch (Exception e) {
            log.error("Error querying details list", e);
            throw new RuntimeException("Query failed", e);
        }

        return detailsList;
    }
}
