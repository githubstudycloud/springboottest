// UriRepository.java
@Repository
@Slf4j
public class UriRepository extends BaseMongoRepository<UriEntity> {
    private final MongoOperations mongoOperations;

    public UriRepository(MongoOperations mongoOperations) {
        super(mongoOperations);
        this.mongoOperations = mongoOperations;
    }

    // 软删除单个URI
    public void deleteByUri(String uri) {
        Query query = Query.query(Criteria.where("uri").is(uri));
        Update update = Update.update("deleted", true)
                            .set("updateTime", LocalDateTime.now());
        UpdateResult result = mongoOperations.updateFirst(query, update, UriEntity.class);
        log.debug("Soft deleted URI {}, matched count: {}", uri, result.getMatchedCount());
    }

    // 软删除多个URI
    public void deleteByUris(Collection<String> uris) {
        Query query = Query.query(Criteria.where("uri").in(uris));
        Update update = Update.update("deleted", true)
                            .set("updateTime", LocalDateTime.now());
        UpdateResult result = mongoOperations.updateMulti(query, update, UriEntity.class);
        log.debug("Soft deleted URIs, count: {}, matched count: {}", uris.size(), result.getMatchedCount());
    }

    // 硬删除单个URI
    public void physicalDeleteByUri(String uri) {
        Query query = Query.query(Criteria.where("uri").is(uri));
        DeleteResult result = mongoOperations.remove(query, UriEntity.class);
        log.debug("Physical deleted URI {}, deleted count: {}", uri, result.getDeletedCount());
    }

    // 硬删除多个URI
    public void physicalDeleteByUris(Collection<String> uris) {
        Query query = Query.query(Criteria.where("uri").in(uris));
        DeleteResult result = mongoOperations.remove(query, UriEntity.class);
        log.debug("Physical deleted URIs, count: {}, deleted count: {}", uris.size(), result.getDeletedCount());
    }

    // 根据uriHash删除
    @Override
    public void deleteByUriHashNotIn(Collection<String> uriHashes) {
        Query query = Query.query(Criteria.where("uriHash").nin(uriHashes));
        Update update = Update.update("deleted", true)
                            .set("updateTime", LocalDateTime.now());
        UpdateResult result = mongoOperations.updateMulti(query, update, UriEntity.class);
        log.debug("Soft deleted URIs not in hashes, matched count: {}", result.getMatchedCount());
    }
}