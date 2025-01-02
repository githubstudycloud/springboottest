// 目录结构
/*
src/main/java/com/study/collect/
├── api/
│   ├── controller/
│   │   └── UriCollectController.java       // REST接口
│   └── response/
│       ├── BaseResponse.java               // 基础响应对象
│       ├── PageResponse.java               // 分页响应对象
│       └── VersionResponse.java            // 版本响应对象
├── core/
│   ├── config/
│   │   ├── MongoConfig.java               // MongoDB配置
│   │   ├── ThreadPoolConfig.java          // 线程池配置
│   │   └── ObjectPoolConfig.java          // 对象池配置
│   ├── constant/
│   │   └── VersionType.java               // 版本类型枚举
│   └── util/
│       ├── HashUtil.java                  // URI哈希工具
│       └── PageUtil.java                  // 分页工具
├── domain/
│   ├── entity/
│   │   └── UriEntity.java                 // URI实体
│   └── param/
│       ├── CollectParam.java              // 采集参数
│       └── PageParam.java                 // 分页参数
├── service/
│   ├── UriCollectService.java             // 采集服务接口
│   ├── impl/
│   │   └── UriCollectServiceImpl.java     // 采集服务实现
│   └── http/
│       ├── UriHttpService.java            // HTTP服务
│       └── response/
│           ├── HttpResponseParser.java     // 响应解析器
│           └── impl/
│               ├── VersionResponseParser.java
│               ├── UriListResponseParser.java
│               └── UriDetailResponseParser.java
└── repository/
    └── UriRepository.java                 // MongoDB仓库
*/

// 1. 实体定义
@Document(collection = "uri_collect")
@Data
@EqualsAndHashCode(callSuper = true)
public class UriEntity extends VersionEntity {
    @Indexed
    private String uriHash;                // URI哈希值，主键
    private String uri;                    // 原始URI
    private String rootNode;               // 根节点
    private String versionType;            // TRUNK/BRANCH
    private String uriVersion;             // URI的版本号
    private Map<String, Object> details;   // URI详情
    
    @Override
    public void prePersist() {
        if (this.uriHash == null) {
            this.uriHash = HashUtil.hash(this.uri);
        }
        // 基类version暂时不用，设为0
        this.version = 0L;
    }
}

// 2. MongoDB仓库
@Repository
public interface UriRepository extends BaseMongoRepository<UriEntity> {
    
    @Override
    default <S extends UriEntity> S save(S entity) {
        Query query = new Query(Criteria.where("uriHash").is(entity.getUriHash()));
        Update update = new Update()
                .set("uri", entity.getUri())
                .set("rootNode", entity.getRootNode())
                .set("versionType", entity.getVersionType())
                .set("uriVersion", entity.getUriVersion())
                .set("details", entity.getDetails())
                .set("updateTime", LocalDateTime.now());
        
        // upsert操作：存在则更新，不存在则插入
        mongoOperations.upsert(query, update, UriEntity.class);
        return entity;
    }

    // 批量保存，使用bulkWrite提高性能
    @Override
    default <S extends UriEntity> List<S> saveAll(Iterable<S> entities) {
        List<WriteModel<UriEntity>> operations = StreamSupport.stream(entities.spliterator(), false)
                .map(entity -> {
                    Query query = Query.query(Criteria.where("uriHash").is(entity.getUriHash()));
                    Update update = new Update()
                            .set("uri", entity.getUri())
                            .set("rootNode", entity.getRootNode())
                            .set("versionType", entity.getVersionType())
                            .set("uriVersion", entity.getUriVersion())
                            .set("details", entity.getDetails())
                            .set("updateTime", LocalDateTime.now());
                    return new UpdateOneModel<>(query.getQueryObject(), update.getUpdateObject(), new UpdateOptions().upsert(true));
                })
                .collect(Collectors.toList());

        BulkWriteResult result = mongoOperations.bulkWrite(operations, UriEntity.class);
        return (List<S>) entities;
    }
}

// 3. 响应解析器
public interface HttpResponseParser<T> {
    T parse(String response) throws IOException;
}

@Component
public class VersionResponseParser implements HttpResponseParser<PageResponse<VersionResponse>> {
    private final ObjectMapper objectMapper;

    @Override
    public PageResponse<VersionResponse> parse(String response) throws IOException {
        JsonNode root = objectMapper.readTree(response);
        return PageResponse.<VersionResponse>builder()
                .total(root.path("total").asLong())
                .items(objectMapper.convertValue(root.path("items"),
                        new TypeReference<List<VersionResponse>>() {}))
                .build();
    }
}

// 4. HTTP服务
@Service
@Slf4j
public class UriHttpService {
    private final HttpUtil httpUtil;
    private final VersionResponseParser versionParser;
    private final UriListResponseParser uriListParser;
    private final UriDetailResponseParser uriDetailParser;
    
    public PageResponse<VersionResponse> getVersions(String rootNode, PageParam pageParam) throws IOException {
        String response = httpUtil.post("/api/versions", 
            objectMapper.writeValueAsString(Map.of(
                "rootNode", rootNode,
                "page", pageParam.getPage(),
                "size", pageParam.getSize()
            ))).getBody();
        return versionParser.parse(response);
    }
}

// 5. 对象池配置
@Configuration
public class ObjectPoolConfig {
    @Bean
    public ObjectPool<UriEntity> uriEntityPool() {
        return new GenericObjectPool<>(new BasePooledObjectFactory<>() {
            @Override
            public UriEntity create() {
                return new UriEntity();
            }

            @Override
            public PooledObject<UriEntity> wrap(UriEntity entity) {
                return new DefaultPooledObject<>(entity);
            }
            
            @Override
            public void passivateObject(PooledObject<UriEntity> p) {
                // 重置对象状态
                UriEntity entity = p.getObject();
                entity.setUri(null);
                entity.setDetails(null);
                // ... 重置其他字段
            }
        });
    }
}

// 6. 采集服务实现
@Service
@Slf4j
public class UriCollectServiceImpl implements UriCollectService {
    private final UriHttpService httpService;
    private final UriRepository repository;
    private final ObjectPool<UriEntity> entityPool;
    
    @Autowired
    public UriCollectServiceImpl(
            UriHttpService httpService,
            UriRepository repository,
            ObjectPool<UriEntity> entityPool,
            @Qualifier("collectExecutor") ExecutorService executorService) {
        this.httpService = httpService;
        this.repository = repository;
        this.entityPool = entityPool;
        this.executorService = executorService;
    }

    private static final int BATCH_SIZE = 200;
    private static final int PAGE_SIZE = 200;
    
    @Override
    public void collectData(CollectParam param) {
        // 1. 获取所有版本
        List<String> allVersions = getAllVersions(param.getRootNode());
        
        // 2. 按版本类型分组处理
        Map<String, List<String>> versionGroups = allVersions.stream()
                .collect(Collectors.groupingBy(this::getVersionType));
        
        // 3. 优先处理主干版本，然后是分支版本
        processVersionGroup(param.getRootNode(), versionGroups.get(VersionType.TRUNK.name()));
        processVersionGroup(param.getRootNode(), versionGroups.get(VersionType.BRANCH.name()));
    }
    
    private List<String> getAllVersions(String rootNode) {
        List<String> allVersions = new ArrayList<>();
        PageResponse<VersionResponse> firstPage = 
            httpService.getVersions(rootNode, new PageParam(1, PAGE_SIZE));
        allVersions.addAll(firstPage.getItems().stream()
                .map(VersionResponse::getVersion)
                .collect(Collectors.toList()));
        
        // 分页获取剩余版本
        long totalPages = (firstPage.getTotal() + PAGE_SIZE - 1) / PAGE_SIZE;
        for (int page = 2; page <= totalPages; page++) {
            PageResponse<VersionResponse> pageResponse = 
                httpService.getVersions(rootNode, new PageParam(page, PAGE_SIZE));
            allVersions.addAll(pageResponse.getItems().stream()
                    .map(VersionResponse::getVersion)
                    .collect(Collectors.toList()));
        }
        return allVersions;
    }
    
    private void processVersionGroup(String rootNode, List<String> versions) {
        // 串行处理每个版本，但版本内部并行处理
        versions.forEach(version -> processVersion(rootNode, version));
    }
    
    private void processVersion(String rootNode, String version) {
        List<String> uris = getAllUris(version);  // 分页获取URI列表
        
        // 使用分片进行并行处理
        Lists.partition(uris, BATCH_SIZE)
            .parallelStream()
            .forEach(batch -> {
                List<Map<String, Object>> details = httpService.getUriDetails(batch);
                List<UriEntity> entities = new ArrayList<>(batch.size());
                
                // 使用对象池获取实体对象
                details.forEach(detail -> {
                    try {
                        UriEntity entity = entityPool.borrowObject();
                        fillEntity(entity, rootNode, version, detail);
                        entities.add(entity);
                    } catch (Exception e) {
                        log.error("Error borrowing object from pool", e);
                    }
                });
                
                // 批量保存
                repository.saveAll(entities);
                
                // 归还对象到对象池
                entities.forEach(entity -> {
                    try {
                        entityPool.returnObject(entity);
                    } catch (Exception e) {
                        log.error("Error returning object to pool", e);
                    }
                });
            });
    }
}

// 7. 线程池配置
@Configuration
public class ThreadPoolConfig {
    @Bean(name = "collectExecutor")
    public ExecutorService collectExecutor() {
        return new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors() * 2, // 核心线程数
            Runtime.getRuntime().availableProcessors() * 4, // 最大线程数
            60L,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(5000),                 // 使用有界队列
            new ThreadFactoryBuilder()
                .setNameFormat("uri-collect-pool-%d")
                .setDaemon(true)
                .build(),
            new ThreadPoolExecutor.CallerRunsPolicy()       // 队列满时，提交线程执行任务
        );
    }
}
