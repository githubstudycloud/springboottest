// 1. 实体类
@Document(collection = "#{@collectionStrategy.getCollectionName()}")
@Data
@CompoundIndexes({
    @CompoundIndex(name = "query_main_idx", 
                  def = "{'uriHash': 1, 'updateTime': -1}"),
    @CompoundIndex(name = "uri_prefix_idx", 
                  def = "{'uri': 1, 'updateTime': -1}")
})
public class UriEntity {
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String uriHash;
    
    @Indexed
    private String uri;
    private String rootNode;
    private String versionType;
    private String uriVersion;
    private Date updateTime;
    private Map<String, Object> details;
}

// 2. 集合策略
@Component
@Slf4j
public class CollectionStrategy {
    private static final ThreadLocal<String> VERSION_HOLDER = new ThreadLocal<>();
    
    public static void setVersion(String version) {
        VERSION_HOLDER.set(version);
    }
    
    public static String getVersion() {
        return VERSION_HOLDER.get();
    }
    
    public static void clearVersion() {
        VERSION_HOLDER.remove();
    }
    
    public String getCollectionName() {
        String version = VERSION_HOLDER.get();
        return version != null ? "uri_collect_" + version : "uri_collect";
    }
}

// 3. MongoDB配置
@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {
    
    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;
    
    @Override
    protected String getDatabaseName() {
        return "your_database";
    }
    
    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create(MongoClientSettings.builder()
            .applyConnectionString(new ConnectionString(mongoUri))
            .applyToConnectionPoolSettings(builder -> builder
                .maxConnectionIdleTime(60000, TimeUnit.MILLISECONDS)
                .maxWaitTime(15000, TimeUnit.MILLISECONDS)
                .maxConnectionLifeTime(300000, TimeUnit.MILLISECONDS)
                .maxSize(200)
                .minSize(50))
            .applyToSocketSettings(builder -> builder
                .connectTimeout(15000, TimeUnit.MILLISECONDS)
                .readTimeout(45000, TimeUnit.MILLISECONDS))
            .retryWrites(true)
            .retryReads(true)
            .build());
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoClient mongoClient) {
        return new MongoTemplate(mongoClient, getDatabaseName());
    }
}

// 4. 自动分片初始化
@Component
@Slf4j
public class MongoShardingInitializer implements ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    private MongoTemplate mongoTemplate;
    
    private Set<String> shardedCollections = new ConcurrentHashSet<>();

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        initializeExistingCollections();
    }

    public void ensureSharding(String collectionName) {
        if (shardedCollections.contains(collectionName)) {
            return;
        }

        try {
            Document result = mongoTemplate.executeCommand(new Document()
                .append("shardCollection", 
                        mongoTemplate.getDb().getName() + "." + collectionName)
                .append("key", new Document("uriHash", "hashed")));

            if (result.getDouble("ok") == 1.0) {
                shardedCollections.add(collectionName);
                log.info("Successfully enabled sharding for collection: {}", collectionName);
            }
        } catch (Exception e) {
            log.warn("Failed to enable sharding for collection: {}", collectionName, e);
        }
    }

    private void initializeExistingCollections() {
        for (String collectionName : mongoTemplate.getCollectionNames()) {
            if (collectionName.startsWith("uri_collect_")) {
                ensureSharding(collectionName);
            }
        }
    }
}

// 5. 数据服务类
@Service
@Slf4j
public class UriDataService {
    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 根据URI检索可能的表名
     */ 
    private Set<String> findPossibleCollections(String uri) {
        Set<String> collections = new HashSet<>();
        String[] parts = uri.split("/");
        StringBuilder path = new StringBuilder();
        for(String part : parts) {
            if(StringUtils.hasLength(part)) {
                path.append("/").append(part);
                String collectionName = "uri_collect_" + path;
                if(mongoTemplate.collectionExists(collectionName)) {
                    collections.add(collectionName);
                }
            }
        }
        
        if(collections.isEmpty()) {
            collections.add("uri_collect");
        }
        return collections;
    }

    /**
     * 批量插入或更新
     */
    public BulkWriteResult bulkUpsert(String version, List<UriEntity> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return null;
        }
        
        CollectionStrategy.setVersion(version);
        try {
            MongoCollection<Document> collection = mongoTemplate.getCollection(
                mongoTemplate.getCollectionName(UriEntity.class));
            
            List<WriteModel<Document>> writeModels = new ArrayList<>();
            
            for (UriEntity entity : entities) {
                Document filter = new Document("uriHash", entity.getUriHash());
                
                Document update = new Document("$set", new Document()
                    .append("uri", entity.getUri())
                    .append("rootNode", entity.getRootNode())
                    .append("versionType", entity.getVersionType())
                    .append("uriVersion", entity.getUriVersion())
                    .append("updateTime", new Date())
                    .append("details", entity.getDetails()));
                
                ReplaceOneModel<Document> model = new ReplaceOneModel<>(
                    filter, 
                    update, 
                    new ReplaceOptions().upsert(true));
                
                writeModels.add(model);
            }
            
            BulkWriteOptions options = new BulkWriteOptions()
                .ordered(false)
                .bypassDocumentValidation(true);
                
            return collection.bulkWrite(writeModels, options);
            
        } catch (BulkWriteException e) {
            log.error("Bulk write partially failed for version {}", version, e);
            throw e;
        } catch (Exception e) {
            log.error("Bulk write failed for version {}", version, e);
            throw new RuntimeException("Bulk write failed", e);
        } finally {
            CollectionStrategy.clearVersion();
        }
    }

    /**
     * 自动检索多个URI
     */
    public List<UriEntity> autoQueryByUris(List<String> uris) {
        if (CollectionUtils.isEmpty(uris)) {
            return Collections.emptyList();
        }

        Set<String> allCollections = new HashSet<>();
        for (String uri : uris) {
            allCollections.addAll(findPossibleCollections(uri));
        }

        List<String> uriHashes = uris.stream()
            .map(HashUtil::hash)
            .collect(Collectors.toList());

        List<UriEntity> results = new ArrayList<>();
        for (String collectionName : allCollections) {
            Query query = new Query(Criteria.where("uriHash").in(uriHashes))
                .with(Sort.by(Sort.Direction.DESC, "updateTime"));

            results.addAll(mongoTemplate.find(query, UriEntity.class, collectionName));
        }

        return results;
    }

    /**
     * 高级查询接口
     */
    public List<UriEntity> advancedQuery(List<String> uris, Date startTime, Date endTime) {
        if (CollectionUtils.isEmpty(uris)) {
            return Collections.emptyList();
        }

        Set<String> allCollections = new HashSet<>();
        for (String uri : uris) {
            allCollections.addAll(findPossibleCollections(uri));
        }

        List<Criteria> conditions = new ArrayList<>();
        
        if (!CollectionUtils.isEmpty(uris)) {
            List<String> uriHashes = uris.stream()
                .map(HashUtil::hash)
                .collect(Collectors.toList());
            conditions.add(Criteria.where("uriHash").in(uriHashes));
        }
        
        if (startTime != null || endTime != null) {
            Criteria timeCriteria = Criteria.where("updateTime");
            if (startTime != null) {
                timeCriteria.gte(startTime);
            }
            if (endTime != null) {
                timeCriteria.lte(endTime);
            }
            conditions.add(timeCriteria);
        }

        Criteria criteria = new Criteria();
        if (!conditions.isEmpty()) {
            criteria.andOperator(conditions.toArray(new Criteria[0]));
        }

        List<UriEntity> results = new ArrayList<>();
        for (String collectionName : allCollections) {
            Query query = new Query(criteria)
                .with(Sort.by(Sort.Direction.DESC, "updateTime"));

            results.addAll(mongoTemplate.find(query, UriEntity.class, collectionName));
        }

        return results;
    }
}

// 6. 工具类
public class HashUtil {
    public static String hash(String input) {
        return DigestUtils.sha256Hex(input);
    }
}

// 7. 配置文件 application.yml
spring:
  data:
    mongodb:
      uri: mongodb://app_user:your_password@mongos1:27017,mongos2:27017/your_database?authSource=admin&replicaSet=configrs
      auto-index-creation: true
      connection-pool-size: 200
      min-connections-per-host: 50
      max-connections-per-host: 200
      server-selection-timeout: 15000
      socket-timeout: 45000
      max-wait-time: 15000
      maintenance-frequency: 5000
      maintenance-initial-delay: 1000
