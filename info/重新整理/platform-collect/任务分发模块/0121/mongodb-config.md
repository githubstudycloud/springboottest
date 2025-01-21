# MongoDB分片集群配置方案

## 1. 整体架构

- 1个路由节点(mongos)
- 3个配置节点(configsvr)
- 3个分片节点(shardsvr)，每个分片1个副本集
- 总计10个节点

## 2. 资源分配 (40GB总内存)

### 内存分配方案：
- Mongos (路由): 4GB
- Config Server (每个): 2GB × 3 = 6GB
- Shard (每个主节点): 6GB × 3 = 18GB
- Shard (每个副本节点): 4GB × 3 = 12GB

### 2.1 配置服务器 (ConfigServer) 配置

```yaml
# config1.conf
storage:
  dbPath: /data/configdb
  wiredTiger:
    engineConfig:
      cacheSizeGB: 1.5
      maxCacheOverflowFileSizeGB: 0.5
    collectionConfig:
      blockCompressor: snappy

systemLog:
  destination: file
  path: /var/log/mongodb/config1.log
  logAppend: true

net:
  port: 27019
  bindIp: localhost
  maxIncomingConnections: 1000

replication:
  replSetName: configReplSet

sharding:
  clusterRole: configsvr

operationProfiling:
  slowOpThresholdMs: 100
  mode: slowOp

processManagement:
  fork: true
  pidFilePath: /var/run/mongodb/configsvr1.pid

security:
  keyFile: /etc/mongodb/keyfile
  authorization: enabled

setParameter:
  maxTransactionLockRequestTimeoutMillis: 5000
  cursorTimeoutMillis: 300000
```

### 2.2 分片服务器 (ShardServer) 配置

```yaml
# shard1.conf
storage:
  dbPath: /data/shard1
  wiredTiger:
    engineConfig:
      cacheSizeGB: 5
      maxCacheOverflowFileSizeGB: 1
      checkpointSizeMB: 1024
    collectionConfig:
      blockCompressor: snappy
    indexConfig:
      prefixCompression: true

systemLog:
  destination: file
  path: /var/log/mongodb/shard1.log
  logAppend: true

net:
  port: 27018
  bindIp: localhost
  maxIncomingConnections: 2000

replication:
  replSetName: shard1ReplSet
  oplogSizeMB: 10240

sharding:
  clusterRole: shardsvr

operationProfiling:
  slowOpThresholdMs: 100
  mode: slowOp

processManagement:
  fork: true
  pidFilePath: /var/run/mongodb/shard1.pid

security:
  keyFile: /etc/mongodb/keyfile
  authorization: enabled

setParameter:
  maxTransactionLockRequestTimeoutMillis: 5000
  cursorTimeoutMillis: 300000
  wiredTigerConcurrentReadTransactions: 128
  wiredTigerConcurrentWriteTransactions: 128
```

### 2.3 路由服务器 (Mongos) 配置

```yaml
# mongos.conf
systemLog:
  destination: file
  path: /var/log/mongodb/mongos.log
  logAppend: true

net:
  port: 27017
  bindIp: localhost
  maxIncomingConnections: 4000

sharding:
  configDB: configReplSet/localhost:27019,localhost:27020,localhost:27021

processManagement:
  fork: true
  pidFilePath: /var/run/mongodb/mongos.pid

security:
  keyFile: /etc/mongodb/keyfile

setParameter:
  maxTransactionLockRequestTimeoutMillis: 5000
  cursorTimeoutMillis: 300000
  taskExecutorPoolSize: 16
  ShardingTaskExecutorPoolSize: 8

operationProfiling:
  slowOpThresholdMs: 100
```

## 3. 客户端配置

### 3.1 Spring Boot 客户端配置类

```java
@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Value("${spring.data.mongodb.uri}")
    private String uri;

    @Value("${spring.data.mongodb.database}")
    private String database;

    @Override
    protected String getDatabaseName() {
        return database;
    }

    @Bean
    public MongoClient mongoClient() {
        MongoClientSettings settings = MongoClientSettings.builder()
            .applyConnectionString(new ConnectionString(uri))
            .applyToConnectionPoolSettings(builder -> 
                builder
                    .minSize(20)          // 最小连接数
                    .maxSize(100)         // 最大连接数
                    .maxWaitTime(10000, TimeUnit.MILLISECONDS)
                    .maxConnectionLifeTime(30, TimeUnit.MINUTES)
                    .maxConnectionIdleTime(10, TimeUnit.MINUTES)
                    .maintenanceFrequency(5, TimeUnit.MINUTES)
                    .maintenanceInitialDelay(1, TimeUnit.MINUTES)
            )
            .applyToSocketSettings(builder ->
                builder
                    .connectTimeout(10000, TimeUnit.MILLISECONDS)
                    .readTimeout(15000, TimeUnit.MILLISECONDS)
            )
            .writeConcern(WriteConcern.MAJORITY.withWTimeout(5000, TimeUnit.MILLISECONDS))
            .readPreference(ReadPreference.primaryPreferred())
            .readConcern(ReadConcern.MAJORITY)
            .retryWrites(true)
            .retryReads(true)
            .applyToServerSettings(builder ->
                builder
                    .heartbeatFrequency(10000, TimeUnit.MILLISECONDS)
                    .minHeartbeatFrequency(500, TimeUnit.MILLISECONDS)
            )
            .build();

        return MongoClients.create(settings);
    }

    @Bean
    public MongoDatabaseFactory mongoDatabaseFactory(MongoClient mongoClient) {
        return new SimpleMongoClientDatabaseFactory(mongoClient, getDatabaseName());
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoDatabaseFactory factory) {
        return new MongoTemplate(factory);
    }
}
```

### 3.2 应用程序配置

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://user:password@localhost:27017/dbname?retryWrites=true&w=majority&readPreference=primaryPreferred
      database: dbname
      
      # 连接池配置
      min-pool-size: 20
      max-pool-size: 100
      
      # 超时配置
      connect-timeout: 10000
      socket-timeout: 15000
      max-wait-time: 10000
      
      # 写入关注
      write-concern: MAJORITY
      # 读取关注
      read-concern: MAJORITY
      # 读取偏好
      read-preference: primaryPreferred
```

## 4. 调优建议

### 4.1 分片键选择
- 选择基数高的字段
- 避免单调增长的字段
- 考虑复合分片键

### 4.2 写入优化
- 批量写入使用bulkWrite
- 适当设置writeConcern
- 控制单次批量大小在1000以内

### 4.3 读取优化
- 合理使用索引
- 设置合适的readPreference
- 利用二级索引覆盖查询

### 4.4 连接池配置
- 写入客户端：
  ```java
  minSize: 10
  maxSize: 50
  maxWaitTime: 10000
  ```

- 读取客户端：
  ```java
  minSize: 20
  maxSize: 100
  maxWaitTime: 5000
  ```

### 4.5 监控指标
- 监控连接池使用情况
- 监控慢查询
- 监控分片均衡情况
- 监控操作延迟

## 5. 性能基准

预期性能：
- 写入性能：5000-8000 ops/s
- 读取性能：10000-15000 ops/s
- 平均响应时间：<50ms
- 99th延迟：<200ms

## 6. 注意事项

1. 定期检查和优化索引
2. 监控分片数据分布
3. 避免跨分片查询
4. 控制事务范围和时长
5. 合理设置TTL索引
6. 定期进行备份
