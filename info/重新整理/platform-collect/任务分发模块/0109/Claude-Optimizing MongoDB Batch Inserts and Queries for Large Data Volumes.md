## Prompt:
当我使用springboot3.2.9时，jdk是openjdk21,批量往mongodb的3分片的集群里插入数据时，插入多了有时会出现连不上mongodb的情况 ，我目前按方式一动态建表了，因为我的大版本可能有1000万数据，小版本可能0-50万，我一般是同小版本分批200一采集后往数据库插入或者更新,查询的时候也是同小版本 分片200一采集往数据库查询，但可以的话我需要查询能支持任意的200分组查询，表名现在都是uri_collect_ 加上uriVersion 版本字段， 大版本一般是/a1/b1/c1 小版本可能是 /a1/b1/c1 /a1/b1/c1/d1 /a1/b1/c1/d2 ... /a1/b1/c1/dn ,因为大版本根目录会有数据，单个数据的uri是唯一的 一把是 /a1/b1/c1/... , /a1/b1/c1/d1/...省略的是 /en/fn/gn.../zn/ ,多级的"/"分割的字符串，可以认为是唯一的 ，但比较长就是，不太适合直接做索引， urihash就是再生成一个唯一键，查询的时候最好能传入uri的list或者versionUri，还有更新开始时间和更新结束时间 指定一下updatetime查询，暂时这3个任意组合， 我的插入/更新还有查询的语句能在怎么优化，速度最好。你还需要确认一些什么？可以帮我更好的完成这个设计

## Response:
我理解您的需求，让我帮您分析一下现有情况和可能的优化方案。我需要先确认一些关键信息：

1.  关于数据的特征：

*   每条记录的完整URI是否有最大长度限制？
*   urihash目前是如何生成的？是用什么算法？
*   更新时间(updatetime)的精度要求是怎样的？精确到秒还是毫秒？

2.  关于查询模式：

*   查询时，uri list的大小一般是多少？
*   是否需要支持URI的前缀匹配查询？比如查找所有以"/a1/b1/c1"开头的记录
*   查询结果是否需要分页？

3.  关于MongoDB配置：

*   MongoDB当前的分片键是如何设计的？
*   当前索引结构是怎样的？
*   连接池的配置参数是什么？

基于目前的信息，我可以先给出一些初步建议：

1.  数据模型优化：

```javascript
{
    _id: ObjectId(),
    urihash: String,  // 建议使用blake3或xxHash3等高性能哈希
    uri: String,
    uriPrefix: String,  // 存储URI的前几级路径，用于优化前缀查询
    version: String,
    updateTime: ISODate,
    // 其他字段...
}
```

2.  索引策略：

```javascript
// 复合索引支持多种查询组合
db.collection.createIndex({ 
    urihash: 1, 
    updateTime: -1 
});

// 如果需要前缀查询
db.collection.createIndex({ 
    uriPrefix: 1, 
    updateTime: -1 
});
```

3.  分片策略建议：

```javascript
// 使用哈希分片，确保数据均匀分布
sh.shardCollection("dbName.collectionName", { urihash: "hashed" });
```

4.  批量操作优化：

```java
// 使用批量写入并设置写入关注点
BulkOperations bulkOps = collection.bulkWrite()
    .setWriteConcern(WriteConcern.MAJORITY);

// 使用ordered: false 提高并行性能
bulkOps.ordered(false);
```

5.  Spring Boot配置优化：

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://host1,host2,host3/database
      options:
        max-connection-idle-time: 60000
        max-connection-life-time: 300000
        max-connection-per-host: 100
        connect-timeout: 10000
        socket-timeout: 60000
        server-selection-timeout: 30000
```

请您确认这些信息，这样我可以提供更具体和针对性的优化方案。同时，我建议监控以下指标：

1.  写入操作的延迟
2.  连接池使用情况
3.  MongoDB的分片平衡状态
4.  索引使用情况

您觉得这些建议如何？需要我针对哪些方面提供更详细的建议？

## Prompt:
请重新根据这些再给出优化的方案，然后再分段实现

## Response:
根据您提供的详细信息，我将分段进行优化设计和实现。

### 1\. 数据模型优化

首先优化实体类设计：

```java
@Document(collection = "uri_collect_#{@uriCollectionStrategy.getCollectionName()}")
@Data
@CompoundIndexes({
    @CompoundIndex(name = "query_optimization_idx", 
                  def = "{'uriHash': 1, 'updateTime': -1, 'uriVersion': 1}"),
    @CompoundIndex(name = "prefix_query_idx", 
                  def = "{'rootNode': 1, 'uri': 1, 'updateTime': -1}"),
    @CompoundIndex(name = "version_query_idx", 
                  def = "{'uriVersion': 1, 'updateTime': -1}")
})
public class UriEntity {
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String uriHash;
    
    private String uri;
    private String rootNode;    // URI的根节点路径
    private String uriPrefix;   // URI的前缀路径
    private String versionType;
    private String uriVersion;
    private Date updateTime;
    private Map<String, Object> details;
    
    // 新增用于优化查询的字段
    private int uriDepth;       // URI的深度
    private List<String> uriParts; // URI分段存储
}
```

### 2\. 集合策略管理

```java
@Component
public class UriCollectionStrategy {
    public String getCollectionName(String uriVersion) {
        return "v_" + uriVersion.replaceAll("[^a-zA-Z0-9]", "_");
    }
    
    public String getCollectionName() {
        // 默认集合名称策略
        return "default";
    }
}
```

### 3\. MongoDB配置优化

```java
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
        MongoClientSettings.Builder builder = MongoClientSettings.builder()
            .applyConnectionString(new ConnectionString(mongoUri))
            .applyToConnectionPoolSettings(builder -> builder
                .maxConnectionIdleTime(60000, TimeUnit.MILLISECONDS)
                .maxWaitTime(15000, TimeUnit.MILLISECONDS)
                .maxConnectionLifeTime(300000, TimeUnit.MILLISECONDS)
                .maxSize(200)   // 增加连接池大小
                .minSize(50)    // 增加最小连接数
            )
            .applyToSocketSettings(builder -> builder
                .connectTimeout(15000, TimeUnit.MILLISECONDS)
                .readTimeout(45000, TimeUnit.MILLISECONDS)
            )
            .readPreference(ReadPreference.primaryPreferred())
            .writeConcern(WriteConcern.MAJORITY)  // 更改写入确认级别
            .readConcern(ReadConcern.MAJORITY)    // 更改读取确认级别
            .retryWrites(true)
            .retryReads(true);
        
        return MongoClients.create(builder.build());
    }
}
```

### 4\. 批量操作优化服务

```java
@Service
@Slf4j
public class UriDataService {
    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Autowired
    private UriCollectionStrategy collectionStrategy;
    
    // 批量插入或更新
    public void bulkUpsert(List<UriEntity> entities, String uriVersion) {
        if (CollectionUtils.isEmpty(entities)) {
            return;
        }
        
        String collectionName = collectionStrategy.getCollectionName(uriVersion);
        BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, 
                                                      UriEntity.class, 
                                                      collectionName);
        
        entities.forEach(entity -> {
            Query query = new Query(Criteria.where("uriHash").is(entity.getUriHash()));
            Update update = new Update()
                .setOnInsert("uri", entity.getUri())
                .setOnInsert("rootNode", entity.getRootNode())
                .setOnInsert("uriPrefix", entity.getUriPrefix())
                .set("updateTime", new Date())
                .set("details", entity.getDetails());
            
            bulkOps.upsert(query, update);
        });
        
        BulkWriteResult result = bulkOps.execute();
        log.info("Bulk upsert completed for version {}: {}", uriVersion, result);
    }

    // 批量查询优化
    public List<UriEntity> batchQuery(List<String> uris, 
                                    String uriVersion, 
                                    Date startTime, 
                                    Date endTime) {
        Criteria criteria = new Criteria();
        
        if (!CollectionUtils.isEmpty(uris)) {
            List<String> uriHashes = uris.stream()
                .map(HashUtil::hash)
                .collect(Collectors.toList());
            criteria.and("uriHash").in(uriHashes);
        }
        
        if (StringUtils.hasText(uriVersion)) {
            criteria.and("uriVersion").is(uriVersion);
        }
        
        if (startTime != null || endTime != null) {
            Criteria timeCriteria = Criteria.where("updateTime");
            if (startTime != null) {
                timeCriteria.gte(startTime);
            }
            if (endTime != null) {
                timeCriteria.lte(endTime);
            }
            criteria.andOperator(timeCriteria);
        }
        
        Query query = new Query(criteria)
            .with(Sort.by(Sort.Direction.DESC, "updateTime"));
        
        return mongoTemplate.find(query, UriEntity.class);
    }
}
```

### 5\. 分片配置脚本

```javascript
// 在MongoDB shell中执行
db.adminCommand({
    shardCollection: "your_database.uri_collect_default",
    key: { uriHash: "hashed" }
});

// 为每个版本集合创建分片
function setupSharding(version) {
    db.adminCommand({
        shardCollection: `your_database.uri_collect_v_${version}`,
        key: { uriHash: "hashed" }
    });
}
```

这个设计实现了以下优化：

1.  数据模型优化：

*   添加了复合索引以支持多种查询场景
*   引入了uriPrefix和uriDepth字段优化前缀查询
*   优化了分片键设计

2.  性能优化：

*   使用UNORDERED批量操作提高并行性能
*   优化了连接池配置
*   实现了高效的批量查询

3.  可靠性优化：

*   提高了写入确认级别
*   添加了重试机制
*   优化了超时设置

需要我详细解释某个部分，或继续优化其他方面吗？

## Prompt:
我集合策略改了干啥，用versionUri是后面还要统计的，uriPrefix和uriDepth和uriParts也没看你用

## Response:
您说得对,我来修正一下设计,去掉无用的字段,专注于核心功能的优化:

### 1\. 实体类优化

```java
@Document(collection = "uri_collect")
@Data
@CompoundIndexes({
    // 优化常用查询组合的复合索引
    @CompoundIndex(name = "query_main_idx", 
                  def = "{'uriVersion': 1, 'uriHash': 1, 'updateTime': -1}"),
    // 支持URI前缀查询的索引                  
    @CompoundIndex(name = "uri_prefix_idx", 
                  def = "{'uriVersion': 1, 'uri': 1, 'updateTime': -1}")
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
```

### 2\. 批量操作服务优化

```java
@Service
@Slf4j
public class UriDataService {
    @Autowired
    private MongoTemplate mongoTemplate;
    
    // 批量插入或更新，使用WriteConcern控制写入确认级别
    public BulkWriteResult bulkUpsert(List<UriEntity> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return null;
        }
        
        try {
            BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, 
                                                          UriEntity.class)
                                                 .setWriteConcern(WriteConcern.MAJORITY);
            
            for (UriEntity entity : entities) {
                Query query = new Query(Criteria.where("uriHash").is(entity.getUriHash()));
                Update update = new Update()
                    .setOnInsert("uri", entity.getUri())
                    .setOnInsert("rootNode", entity.getRootNode())
                    .setOnInsert("versionType", entity.getVersionType())
                    .setOnInsert("uriVersion", entity.getUriVersion())
                    .set("updateTime", new Date())
                    .set("details", entity.getDetails());
                
                bulkOps.upsert(query, update);
            }
            
            return bulkOps.execute();
        } catch (Exception e) {
            log.error("Bulk upsert failed", e);
            throw new RuntimeException("Bulk upsert failed", e);
        }
    }

    // 优化的批量查询方法
    public Page<UriEntity> batchQuery(List<String> uris, 
                                    String uriVersion, 
                                    Date startTime, 
                                    Date endTime,
                                    Pageable pageable) {
        Criteria criteria = new Criteria();
        List<Criteria> conditions = new ArrayList<>();
        
        // URI列表查询
        if (!CollectionUtils.isEmpty(uris)) {
            List<String> uriHashes = uris.stream()
                .map(HashUtil::hash)
                .collect(Collectors.toList());
            conditions.add(Criteria.where("uriHash").in(uriHashes));
        }
        
        // 版本查询
        if (StringUtils.hasText(uriVersion)) {
            conditions.add(Criteria.where("uriVersion").is(uriVersion));
        }
        
        // 时间范围查询
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
        
        if (!conditions.isEmpty()) {
            criteria.andOperator(conditions.toArray(new Criteria[0]));
        }
        
        Query query = new Query(criteria);
        
        // 获取总数
        long total = mongoTemplate.count(query, UriEntity.class);
        
        // 添加分页和排序
        if (pageable != null) {
            query.with(pageable);
        }
        
        // 执行查询
        List<UriEntity> content = mongoTemplate.find(query, UriEntity.class);
        
        return new PageImpl<>(content, pageable, total);
    }

    // 前缀查询方法
    public List<UriEntity> queryByUriPrefix(String uriPrefix, String uriVersion) {
        Criteria criteria = Criteria.where("uri").regex("^" + uriPrefix)
                                   .and("uriVersion").is(uriVersion);
        
        Query query = new Query(criteria)
            .with(Sort.by(Sort.Direction.DESC, "updateTime"));
            
        return mongoTemplate.find(query, UriEntity.class);
    }
}
```

### 3\. MongoDB分片配置

```javascript
// 在MongoDB shell中执行
db.adminCommand({
    shardCollection: "your_database.uri_collect",
    key: { uriVersion: 1, uriHash: "hashed" }
});

// 创建分片索引
db.uri_collect.createIndex({ 
    "uriVersion": 1, 
    "uriHash": "hashed" 
});
```

主要优化点：

1.  减少了不必要的字段,保持数据模型简洁
2.  优化了复合索引以支持主要查询场景
3.  分片键使用{uriVersion: 1, uriHash: "hashed"}组合,既保证数据均匀分布又支持版本查询
4.  批量操作添加了错误处理和WriteConcern控制
5.  查询方法支持分页和多条件组合
6.  添加了专门的前缀查询方法

建议监控以下指标：

1.  批量写入的延迟和成功率
2.  查询响应时间
3.  MongoDB连接池使用情况
4.  分片数据分布情况

您觉得这个优化方案如何？需要针对某个具体场景做更详细的优化吗？

## Prompt:
你这表名没动态设置，我有注解的，文件的，请再整理修改一下，还有同表分片会减缓查询速度吗？还有我动态表很多，你让我一个个执行分片索引也太麻烦了

## Response:
您说得对,我来重新设计一下,结合您提供的注解方案:

1.  首先优化动态表名和分片策略:

```java
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
```

2.  修改实体类:

```java
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
```

3.  添加自动分片配置:

```java
@Component
@Slf4j
public class MongoShardingInitializer implements ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    private MongoTemplate mongoTemplate;
    
    private Set<String> shardedCollections = new ConcurrentHashSet<>();

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // 启动时初始化已有集合的分片
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
```

4.  优化服务层:

```java
@Service
@Slf4j
public class UriDataService {
    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Autowired
    private MongoShardingInitializer shardingInitializer;
    
    @CollectionVersion
    public BulkWriteResult bulkUpsert(String version, List<UriEntity> entities) {
        String collectionName = mongoTemplate.getCollectionName(UriEntity.class);
        // 确保集合已分片
        shardingInitializer.ensureSharding(collectionName);
        
        if (CollectionUtils.isEmpty(entities)) {
            return null;
        }
        
        try {
            BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, 
                                                          UriEntity.class)
                                                 .setWriteConcern(WriteConcern.MAJORITY);
            
            for (UriEntity entity : entities) {
                Query query = new Query(Criteria.where("uriHash").is(entity.getUriHash()));
                Update update = new Update()
                    .setOnInsert("uri", entity.getUri())
                    .setOnInsert("rootNode", entity.getRootNode())
                    .setOnInsert("versionType", entity.getVersionType())
                    .setOnInsert("uriVersion", entity.getUriVersion())
                    .set("updateTime", new Date())
                    .set("details", entity.getDetails());
                
                bulkOps.upsert(query, update);
            }
            
            return bulkOps.execute();
        } catch (Exception e) {
            log.error("Bulk upsert failed for version {}", version, e);
            throw new RuntimeException("Bulk upsert failed", e);
        }
    }

    @CollectionVersion
    public Page<UriEntity> batchQuery(String version,
                                    List<String> uris, 
                                    Date startTime, 
                                    Date endTime,
                                    Pageable pageable) {
        // 查询逻辑保持不变...
    }
}
```

关于您的问题:

1.  同表分片确实可能会影响查询速度,因为需要跨分片查询。但是通过合理的分片键设计(使用uriHash)和索引优化,可以保证大多数查询都是定向到特定分片,减少广播查询。
2.  现在添加了自动分片配置组件,会在以下情况自动处理分片:
    *   应用启动时对已存在的集合进行分片配置
    *   每次操作新集合时自动检查并配置分片

优化建议:

1.  对于查询较多的版本,可以考虑在应用层做缓存
2.  批量操作时可以通过调整批次大小来平衡性能和资源消耗
3.  考虑添加监控指标,及时发现性能问题

需要我对某个部分做更详细的说明吗？

## Prompt:
保证插入就能查询，设置什么级别，如果不是插入就能查询，我怎么知道它几时能插入完

## Response:
为了保证插入后立即可查询，我们需要调整写入关注点(WriteConcern)和读取关注点(ReadConcern)的级别。以下是具体实现：

```java
@Service
@Slf4j
public class UriDataService {
    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Autowired
    private MongoShardingInitializer shardingInitializer;
    
    @CollectionVersion
    public BulkWriteResult bulkUpsert(String version, List<UriEntity> entities) {
        String collectionName = mongoTemplate.getCollectionName(UriEntity.class);
        shardingInitializer.ensureSharding(collectionName);
        
        if (CollectionUtils.isEmpty(entities)) {
            return null;
        }
        
        try {
            // 设置写入关注点为MAJORITY，确保数据写入到大多数节点
            BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, 
                                                          UriEntity.class)
                                                 .setWriteConcern(WriteConcern.MAJORITY);
            
            List<WriteModel<UriEntity>> writeModels = new ArrayList<>();
            
            for (UriEntity entity : entities) {
                ReplaceOneModel<UriEntity> model = new ReplaceOneModel<>(
                    Filters.eq("uriHash", entity.getUriHash()),
                    entity,
                    new ReplaceOptions().upsert(true)
                );
                writeModels.add(model);
            }
            
            BulkWriteResult result = bulkOps.execute();
            
            // 记录写入结果
            log.info("Bulk write completed for version {}: inserted={}, modified={}, matched={}",
                    version, result.getInsertedCount(), result.getModifiedCount(), result.getMatchedCount());
                    
            return result;
            
        } catch (BulkWriteException e) {
            log.error("Bulk write partially failed for version {}", version, e);
            // 获取成功写入的操作数量
            BulkWriteResult partialResult = e.getWriteResult();
            log.error("Partial results: inserted={}, modified={}, matched={}",
                    partialResult.getInsertedCount(), 
                    partialResult.getModifiedCount(), 
                    partialResult.getMatchedCount());
            throw e;
        } catch (Exception e) {
            log.error("Bulk write failed for version {}", version, e);
            throw new RuntimeException("Bulk write failed", e);
        }
    }

    /**
     * 带写入确认的查询
     */
    @CollectionVersion
    public List<UriEntity> batchQueryWithWriteConcern(String version,
                                                     List<String> uris,
                                                     Date startTime,
                                                     Date endTime) {
        Criteria criteria = buildQueryCriteria(uris, startTime, endTime);
        
        // 设置读取关注点为MAJORITY，确保读取到已确认写入的数据
        Query query = new Query(criteria)
            .with(Sort.by(Sort.Direction.DESC, "updateTime"));
        
        // 使用ReadConcern.MAJORITY确保读取到已确认的写入
        return mongoTemplate.find(query, UriEntity.class)
            .withReadConcern(ReadConcern.MAJORITY);
    }

    /**
     * 带写入确认和重试的查询
     */
    @CollectionVersion
    public List<UriEntity> batchQueryWithRetry(String version,
                                             List<String> uris,
                                             Date startTime,
                                             Date endTime,
                                             int maxRetries,
                                             long retryDelayMs) {
        int retryCount = 0;
        while (retryCount < maxRetries) {
            try {
                List<UriEntity> results = batchQueryWithWriteConcern(version, uris, startTime, endTime);
                if (!results.isEmpty()) {
                    return results;
                }
                
                // 如果查询结果为空，且还有重试次数，则等待后重试
                retryCount++;
                if (retryCount < maxRetries) {
                    Thread.sleep(retryDelayMs);
                    log.debug("Retry #{} for version {}", retryCount, version);
                }
                
            } catch (Exception e) {
                log.warn("Query attempt #{} failed for version {}", retryCount, version, e);
                retryCount++;
                if (retryCount >= maxRetries) {
                    throw new RuntimeException("Failed to query after " + maxRetries + " attempts", e);
                }
                try {
                    Thread.sleep(retryDelayMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Query interrupted", ie);
                }
            }
        }
        return Collections.emptyList();
    }

    private Criteria buildQueryCriteria(List<String> uris, Date startTime, Date endTime) {
        Criteria criteria = new Criteria();
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
        
        if (!conditions.isEmpty()) {
            criteria.andOperator(conditions.toArray(new Criteria[0]));
        }
        
        return criteria;
    }
}
```

这个实现提供了几个关键特性：

1.  写入确认

*   使用 `WriteConcern.MAJORITY` 确保数据写入到大多数节点
*   记录详细的写入结果，包括插入、修改和匹配的数量
*   对部分写入失败的情况进行处理

2.  读取确认

*   使用 `ReadConcern.MAJORITY` 确保只读取已确认写入的数据
*   提供带重试机制的查询方法，可以处理数据延迟可见的情况

3.  使用示例：

```java
@Service
public class UriProcessService {
    @Autowired
    private UriDataService uriDataService;
    
    public void processUris(String version, List<UriEntity> entities) {
        // 执行批量写入
        BulkWriteResult writeResult = uriDataService.bulkUpsert(version, entities);
        
        // 立即查询（带重试机制）
        List<UriEntity> results = uriDataService.batchQueryWithRetry(
            version,
            entities.stream().map(UriEntity::getUri).collect(Collectors.toList()),
            null,
            null,
            3,    // 最大重试次数
            1000  // 重试间隔（毫秒）
        );
    }
}
```

配置建议：

1.  在MongoDB配置中添加写入关注点和读取关注点的设置：

```yaml
spring:
  data:
    mongodb:
      write-concern: MAJORITY
      read-concern: MAJORITY
      read-preference: primaryPreferred
```

这样可以：

1.  保证写入操作被确认后才返回
2.  提供重试机制处理数据可见性延迟
3.  记录详细的操作结果供问题排查

您觉得这个实现如何？需要我对某个部分做更详细的说明吗？

