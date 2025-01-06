// 在 UriRepository 中添加方法
@Repository
public class UriRepository extends BaseMongoRepository<UriEntity> {
    private final MongoOperations mongoOperations;

    // 只返回 uriHash 的查询方法
    public List<String> findAllUriHashes() {
        Query query = new Query();
        query.fields().include("uriHash").exclude("_id");
        return mongoOperations.find(query, UriEntity.class)
                .stream()
                .map(UriEntity::getUriHash)
                .collect(Collectors.toList());
    }
    
    // 根据条件查询 uriHash
    public List<String> findUriHashesByCondition(String rootNode, String version, String versionType) {
        Query query = new Query();
        if (rootNode != null) {
            query.addCriteria(Criteria.where("rootNode").is(rootNode));
        }
        if (version != null) {
            query.addCriteria(Criteria.where("uriVersion").is(version));
        }
        if (versionType != null) {
            query.addCriteria(Criteria.where("versionType").is(versionType));
        }
        query.fields().include("uriHash").exclude("_id");
        
        return mongoOperations.find(query, UriEntity.class)
                .stream()
                .map(UriEntity::getUriHash)
                .collect(Collectors.toList());
    }

    // 分页查询 uriHash
    public List<String> findUriHashesByPage(int page, int size) {
        Query query = new Query()
                .skip((long) (page - 1) * size)
                .limit(size);
        query.fields().include("uriHash").exclude("_id");
        
        return mongoOperations.find(query, UriEntity.class)
                .stream()
                .map(UriEntity::getUriHash)
                .collect(Collectors.toList());
    }

    // 如果数据量很大，使用流式处理
    public void streamUriHashes(Consumer<String> consumer) {
        Query query = new Query();
        query.fields().include("uriHash").exclude("_id");
        
        mongoOperations.stream(query, UriEntity.class)
                .map(UriEntity::getUriHash)
                .forEach(consumer);
    }

    // 直接使用原生命令查询（性能最好）
    public List<String> findUriHashesNative() {
        return mongoOperations.getCollection(
                mongoOperations.getCollectionName(UriEntity.class))
                .distinct("uriHash", String.class)
                .into(new ArrayList<>());
    }
}
