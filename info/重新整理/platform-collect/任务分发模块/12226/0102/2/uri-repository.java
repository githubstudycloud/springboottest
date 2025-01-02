@Repository
public class UriRepository extends BaseMongoRepository<UriEntity> {
    
    // MongoOperations 是 Spring Data MongoDB 提供的底层操作接口
    // 通过它可以执行更灵活的 MongoDB 操作
    private final MongoOperations mongoOperations;

    public UriRepository(MongoOperations mongoOperations) {
        super(mongoOperations);
        this.mongoOperations = mongoOperations;
    }

    // 根据单个URI查询
    public UriEntity findByUri(String uri) {
        Query query = Query.query(Criteria.where("uri").is(uri));
        return mongoOperations.findOne(query, UriEntity.class);
    }

    // 根据多个URI批量查询
    public List<UriEntity> findByUris(Collection<String> uris) {
        Query query = Query.query(Criteria.where("uri").in(uris));
        return mongoOperations.find(query, UriEntity.class);
    }

    // 根据URI哈希查询
    public UriEntity findByUriHash(String uriHash) {
        Query query = Query.query(Criteria.where("uriHash").is(uriHash));
        return mongoOperations.findOne(query, UriEntity.class);
    }

    // 根据多个URI哈希批量查询
    public List<UriEntity> findByUriHashes(Collection<String> uriHashes) {
        Query query = Query.query(Criteria.where("uriHash").in(uriHashes));
        return mongoOperations.find(query, UriEntity.class);
    }

    @Override
    public <S extends UriEntity> S save(S entity) {
        Query query = Query.query(Criteria.where("uriHash").is(entity.getUriHash()));
        Update update = new Update()
                .set("uri", entity.getUri())
                .set("rootNode", entity.getRootNode())
                .set("versionType", entity.getVersionType())
                .set("uriVersion", entity.getUriVersion())
                .set("details", entity.getDetails())
                .set("updateTime", LocalDateTime.now());
        
        // upsert: 存在则更新，不存在则插入
        mongoOperations.upsert(query, update, UriEntity.class);
        return entity;
    }
}