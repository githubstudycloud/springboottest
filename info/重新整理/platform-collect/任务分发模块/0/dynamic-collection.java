// 方式一：通过注解动态设置集合名
@Document(collection = "#{@collectionStrategy.getCollectionName('uri_collect')}")
@Data
@EqualsAndHashCode(callSuper = true)
public class UriEntity extends VersionEntity {
    @Indexed(unique = true)
    private String uriHash;
    private String uri;
    private String rootNode;
    private String versionType;
    private String uriVersion;
    private Map<String, Object> details;
}

// 集合名称策略类
@Component
public class CollectionStrategy implements ApplicationContextAware {
    private static ApplicationContext applicationContext;
    private static final ThreadLocal<String> versionHolder = new ThreadLocal<>();

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        applicationContext = context;
    }

    public String getCollectionName(String baseCollection) {
        String version = versionHolder.get();
        return version != null ? baseCollection + "_" + version : baseCollection;
    }

    public static void setVersion(String version) {
        versionHolder.set(version);
    }

    public static void clearVersion() {
        versionHolder.remove();
    }
}

// 方式二：自定义Repository实现动态集合名
@Repository
public class UriRepository {
    private final MongoOperations mongoOperations;
    private String version;

    public void setVersion(String version) {
        this.version = version;
    }

    private String getCollectionName() {
        return version != null ? "uri_collect_" + version : "uri_collect";
    }

    public void save(UriEntity entity) {
        mongoOperations.save(entity, getCollectionName());
    }

    public void saveAll(List<UriEntity> entities) {
        if (entities.isEmpty()) {
            return;
        }
        mongoOperations.insert(entities, getCollectionName());
    }

    public List<UriEntity> findByCondition(QueryCondition condition) {
        Query query = new Query();
        // ... 设置查询条件
        return mongoOperations.find(query, UriEntity.class, getCollectionName());
    }

    // Native MongoDB command with dynamic collection
    public PageResult<String> findUriHashesPage(QueryCondition condition) {
        MongoCollection<Document> collection = mongoOperations.getCollection(getCollectionName());
        
        Document query = new Document();
        // ... 构建查询条件
        
        List<Document> pipeline = Arrays.asList(
            new Document("$match", query),
            new Document("$project", new Document("uriHash", 1).append("_id", 0)),
            new Document("$skip", (long) (condition.getPage() - 1) * condition.getSize()),
            new Document("$limit", condition.getSize())
        );

        List<String> items = collection.aggregate(pipeline)
                .map(doc -> doc.getString("uriHash"))
                .into(new ArrayList<>());
                
        long total = collection.countDocuments(query);
        
        return PageResult.<String>builder()
                .total(total)
                .page(condition.getPage())
                .size(condition.getSize())
                .totalPages((int) Math.ceil((double) total / condition.getSize()))
                .items(items)
                .build();
    }
}

// 使用示例
@Service
@Slf4j
public class UriService {
    private final UriRepository repository;
    private final CollectionStrategy collectionStrategy;  // 方式一使用

    public void saveWithVersion(String version, UriEntity entity) {
        // 方式一：使用ThreadLocal
        try {
            CollectionStrategy.setVersion(version);
            repository.save(entity);
        } finally {
            CollectionStrategy.clearVersion();
        }
        
        // 方式二：直接设置Repository版本
        repository.setVersion(version);
        repository.save(entity);
    }

    public void batchSaveWithVersion(String version, List<UriEntity> entities) {
        repository.setVersion(version);
        repository.saveAll(entities);
    }

    public List<UriEntity> queryWithVersion(String version, QueryCondition condition) {
        repository.setVersion(version);
        return repository.findByCondition(condition);
    }
}
