https://claude.ai/chat/462958a9-34fa-4367-b2b8-3fce4d04c796
## Prompt:
文件是我搭建mongodb的讨论过程，我需要搭建一个能支持单表5000万，最少能100万张表，写入速度插入并更新最少速度2万/s，单条记录5kb，读取速度最小20万/s，这个集群要求可动态扩展，那么这个硬件需求最低多少，以及各种需要的指标和达到的指标分析下

## Response:
让我基于您的需求进行详细分析和规划。

一、需求分析

1.  数据规模

*   单表5000万条记录，每条5KB = 250GB/表
*   100万张表 = 250TB总数据量
*   写入速度：2万/秒（含更新）
*   读取速度：20万/秒

2.  性能需求转换

*   写入带宽：2万/秒 × 5KB = 100MB/秒
*   读取带宽：20万/秒 × 5KB = 1GB/秒

二、最低硬件配置建议

1.  分片服务器（每个分片）

```
CPU: 32核心，3.0GHz以上
内存: 256GB
磁盘: 
- 系统盘：256GB SSD
- 数据盘：4TB NVMe SSD（RAID 10）
网络：10Gbps网卡
```

2.  配置服务器（每个节点）

```
CPU: 16核心
内存: 64GB
磁盘: 512GB SSD
网络：10Gbps网卡
```

3.  mongos路由节点（每个节点）

```
CPU: 16核心
内存: 64GB
系统盘：256GB SSD
网络：10Gbps网卡
```

三、集群规模建议

最小起始配置：

```
- 4个分片（每个分片2节点主从）：8台服务器
- 3个配置服务器：3台服务器
- 2个mongos路由节点：2台服务器
总计：13台服务器
```

四、性能指标和配置参数

1.  WiredTiger缓存配置

```yaml
storage:
  wiredTiger:
    engineConfig:
      cacheSizeGB: 128
      checkpointSizeMB: 4096
    collectionConfig:
      blockCompressor: zstd
```

2.  系统配置

```bash
# 系统参数
vm.swappiness = 1
vm.dirty_ratio = 15
vm.dirty_background_ratio = 5
net.core.somaxconn = 65535
net.ipv4.tcp_max_syn_backlog = 65535
```

3.  分片配置

```javascript
// 设置chunk大小为128MB以减少迁移频率
use config
db.settings.updateOne(
    { _id: "chunksize" },
    { $set: { value: 128 } },
    { upsert: true }
)
```

五、扩展性规划

1.  垂直扩展能力

*   每个分片服务器最大可扩展到：
    *   CPU: 64核心
    *   内存: 512GB
    *   磁盘: 8TB NVMe SSD

2.  水平扩展能力

*   分片数可以根据需求增加
*   建议每次增加2-4个分片
*   单分片承载能力约50TB数据

六、预期性能指标

1.  写入性能

*   单分片写入：5000-8000 ops/s
*   4分片总写入：2-3万 ops/s
*   可通过增加分片线性提升

2.  读取性能

*   单分片读取：5-8万 ops/s
*   4分片总读取：20-30万 ops/s
*   通过读写分离可进一步提升

七、监控指标要求

1.  系统监控

*   CPU使用率 < 70%
*   内存使用率 < 80%
*   磁盘IO等待 < 10ms
*   网络带宽使用率 < 60%

2.  MongoDB指标

*   写入延迟 < 50ms
*   读取延迟 < 10ms
*   连接数使用率 < 85%
*   复制延迟 < 2秒

这个配置能满足您的基本需求，并且具有良好的扩展性。需要我详细说明任何部分吗？

## Prompt:
如果前期单表最多1000万，写入要求3000/s，读取要求10万，后期再水平扩展到之前的设计可以吗

## Response:
这是一个非常好的增长策略。让我设计一个分阶段扩展方案。

第一阶段（1000万级别）配置：

1.  初始硬件配置（3分片集群）

```
分片服务器（每个分片）：
- CPU: 16核心
- 内存: 64GB
- 数据盘: 1TB NVMe SSD
- 网络: 10Gbps

配置服务器（3节点）：
- CPU: 8核心
- 内存: 32GB
- 磁盘: 256GB SSD

mongos路由（2节点）：
- CPU: 8核心
- 内存: 32GB
- 系统盘: 128GB SSD
```

2.  存储引擎配置

```yaml
storage:
  wiredTiger:
    engineConfig:
      cacheSizeGB: 30  # 预留系统内存
      checkpointSizeMB: 2048
    collectionConfig:
      blockCompressor: zstd
```

3.  性能预期

*   写入：3000-5000 ops/s
*   读取：10-15万 ops/s
*   单表数据量：1000万 × 5KB = 50GB

升级路径：

1.  垂直扩展阶段

```
- 增加分片服务器内存到128GB
- 增加数据盘到2TB
- 升级CPU到32核心
```

2.  水平扩展阶段

```
- 增加分片数量到4-6个
- 增加mongos路由节点
- 逐步过渡到最终配置
```

关键监控指标：

```
写入延迟 < 30ms
读取延迟 < 5ms
CPU使用率 < 60%
内存使用率 < 75%
```

这样的配置既满足当前需求，又预留了扩展空间。要我详细说明扩展过程中的具体步骤吗？

## Prompt:
能支持多机器的docker互联部署，还有能限制服务器内存都只使用4gb以及限制使用的数据盘大小吗，方便把闲置的内存不够，但数据还有300g的服务器利用起来，并且指定某些数据不分布上去

## Response:
让我设计一个适合在有限资源服务器上使用Docker部署的MongoDB分片集群方案。

1.  Docker网络设置（跨主机）

```bash
# 在每台服务器上创建overlay网络
docker network create --driver overlay \
  --subnet=10.10.0.0/16 \
  --attachable \
  mongocluster
```

2.  资源限制配置 对每个容器进行资源限制：

```bash
# 分片服务器启动命令（限制资源）
docker run -d --name mongo-shard1 \
  --network mongocluster \
  --memory=4g \
  --memory-swap=8g \
  --memory-reservation=3g \
  --storage-opt size=300G \
  -v /data/mongodb/shard1:/data/db \
  -v /opt/mongodb/shard/shardsvr.conf:/etc/mongod.conf:ro \
  -v /opt/mongodb/keyfile:/mongodb/keyfile:ro \
  -v /var/log/mongodb/shard:/var/log/mongodb \
  mongo:latest mongod --config /etc/mongod.conf
```

3.  存储引擎配置优化（针对低内存）

```yaml
# /opt/mongodb/shard/shardsvr.conf
storage:
  wiredTiger:
    engineConfig:
      cacheSizeGB: 2  # 限制WiredTiger缓存
      checkpointSizeMB: 1024
    collectionConfig:
      blockCompressor: zstd  # 更好的压缩比

operationProfiling:
  mode: slowOp
  slowOpThresholdMs: 100

net:
  bindIp: 0.0.0.0
  port: 27018
  maxIncomingConnections: 5000

setParameter:
  wiredTigerConcurrentReadTransactions: 64
  wiredTigerConcurrentWriteTransactions: 64
```

4.  标记分片以控制数据分布

```javascript
// 给分片添加标签
sh.addShardTag("shard1rs", "highperf")
sh.addShardTag("shard2rs", "lowperf")

// 给特定数据范围添加标签
sh.addTagRange(
  "yourdb.yourcollection",
  { priority: "high" },  // 根据您的分片键调整
  { priority: "high", maxKey: 1 },
  "highperf"
)
```

5.  完整的分片部署示例（跨主机）

```bash
# 主机A（高性能服务器）
docker run -d --name mongo-config1 \
  --network mongocluster \
  --memory=4g \
  --memory-swap=8g \
  -v /data/mongodb/config1:/data/db \
  -v /opt/mongodb/config/configsvr.conf:/etc/mongod.conf:ro \
  mongo:latest mongod --config /etc/mongod.conf

# 主机B（低性能服务器）
docker run -d --name mongo-shard2 \
  --network mongocluster \
  --memory=4g \
  --memory-swap=8g \
  --storage-opt size=300G \
  -v /data/mongodb/shard2:/data/db \
  -v /opt/mongodb/shard/shardsvr.conf:/etc/mongod.conf:ro \
  mongo:latest mongod --config /etc/mongod.conf
```

6.  数据分布控制

```javascript
// 为重要数据集合设置分片策略
sh.enableSharding("important_db")

// 使用标签确保重要数据只分布在高性能分片上
sh.shardCollection(
    "important_db.critical_collection",
    { "_id": "hashed" }
)

sh.addTagRange(
    "important_db.critical_collection",
    { "_id": MinKey },
    { "_id": MaxKey },
    "highperf"
)

// 普通数据允许分布在所有分片上
sh.shardCollection(
    "normal_db.regular_collection",
    { "_id": "hashed" }
)
```

7.  监控关键指标

```javascript
// 监控分片存储使用情况
db.adminCommand({ listShards: 1 })

// 检查数据分布
db.collection.getShardDistribution()

// 监控内存使用
db.serverStatus().wiredTiger.cache
```

这个配置方案的特点：

1.  通过Docker限制每个节点的资源使用
2.  使用标签系统控制数据分布
3.  针对低内存环境优化了存储引擎配置
4.  支持跨主机的Docker部署
5.  可以区分高低性能服务器并合理分配数据

需要我详细解释任何部分吗？比如如何更细致地控制数据分布，或者如何设置监控阈值？

## Prompt:
限制这些数据，同时有什么管理界面能监控到这些吗

## Response:
我来介绍几种流行的MongoDB集群监控方案。

1.  MongoDB Compass (官方工具)

```bash
# 不需要部署，直接连接mongos的ip和端口即可访问
mongodb://admin:password@mongos1:27017/
```

优点：

*   直观的图形界面
*   可以查看分片状态
*   可以执行查询和管理操作
*   免费使用

2.  Mongo Express (轻量级Web界面)

```bash
# 使用Docker部署
docker run -d \
  --name mongo-express \
  --network mongocluster \
  -p 8081:8081 \
  -e ME_CONFIG_MONGODB_SERVER=mongos1 \
  -e ME_CONFIG_MONGODB_PORT=27017 \
  -e ME_CONFIG_MONGODB_ADMINUSERNAME=admin \
  -e ME_CONFIG_MONGODB_ADMINPASSWORD=password \
  -e ME_CONFIG_BASICAUTH_USERNAME=admin \
  -e ME_CONFIG_BASICAUTH_PASSWORD=password \
  mongo-express
```

可监控内容：

*   数据库状态
*   集合信息
*   基本的查询功能
*   系统指标

3.  Grafana + Prometheus (专业监控方案)

首先部署mongodb-exporter：

```bash
docker run -d \
  --name mongodb-exporter \
  --network mongocluster \
  -p 9216:9216 \
  -e MONGODB_URI="mongodb://admin:password@mongos1:27017" \
  percona/mongodb_exporter:latest
```

部署Prometheus：

```bash
# prometheus.yml配置
scrape_configs:
  - job_name: 'mongodb'
    static_configs:
      - targets: ['mongodb-exporter:9216']
```

```bash
docker run -d \
  --name prometheus \
  --network mongocluster \
  -p 9090:9090 \
  -v /path/to/prometheus.yml:/etc/prometheus/prometheus.yml \
  prom/prometheus
```

部署Grafana：

```bash
docker run -d \
  --name grafana \
  --network mongocluster \
  -p 3000:3000 \
  grafana/grafana
```

这个组合可以监控的指标：

*   CPU使用率
*   内存使用情况
*   磁盘空间使用
*   连接数
*   操作延迟
*   QPS统计
*   分片均衡状态
*   复制集状态
*   各种自定义告警

4.  具体监控指标和告警设置

例如在Grafana中设置的关键监控项：

```
1. 资源使用告警
- 内存使用超过3.5GB
- 磁盘使用超过250GB
- CPU使用率超过80%

2. 性能指标告警
- 写入延迟超过100ms
- 读取延迟超过50ms
- 连接数超过80%

3. 分片状态监控
- Chunk分布不均衡
- 复制集延迟过高
- 平衡器状态
```

推荐使用方案：

1.  开发环境：使用MongoDB Compass即可
2.  测试环境：MongoDB Compass + Mongo Express
3.  生产环境：完整的Grafana + Prometheus方案

需要我详细说明如何设置具体的监控面板或告警规则吗？

## Prompt:
先介绍下

## Response:
让我介绍下这几种主要的MongoDB监控工具及其特点：

1.  MongoDB Compass（推荐日常使用） 特点：

*   官方工具，界面美观直观
*   零部署，直接连接使用
*   实时性能指标监控
*   可视化的查询构建器

主要监控内容：

```
- 服务器状态和健康度
- 数据库大小和集合统计
- 查询性能分析器
- 索引建议和使用情况
- CPU和内存使用状况
- 实时操作统计
```

2.  Mongo Express（轻量级选择） 特点：

*   部署简单、资源占用小
*   Web界面操作方便
*   基础的管理功能齐全

可监控项目：

```
- 数据库基本信息
- 集合数据浏览
- 简单的性能指标
- 基础的查询功能
```

3.  Grafana + Prometheus（专业监控方案） 特点：

*   企业级监控解决方案
*   自定义度高
*   支持复杂告警规则
*   历史数据趋势分析

核心监控指标：

```
系统资源：
- CPU使用率和负载
- 内存使用和分配
- 磁盘IO和使用率
- 网络流量和延迟

MongoDB指标：
- 操作QPS统计
- 连接数和队列长度
- 复制集状态
- 分片均衡状况

性能指标：
- 查询响应时间
- 写入延迟
- 索引使用效率
- 慢查询统计
```

各种方案的使用建议：

1.  开发测试：优先使用MongoDB Compass
2.  小规模生产：Mongo Express足够
3.  大规模生产：建议部署完整的Grafana方案

需要我详细介绍某个方案的具体配置和使用方法吗？

## Prompt:
介绍看看

## Response:
让我详细介绍每种监控方案的具体配置和使用方法。

1.  MongoDB Compass (最简单的入门方案)

连接配置：

```
# 标准连接串
mongodb://admin:password@mongos1:27017/

# 启用认证的连接串
mongodb://admin:password@mongos1:27017/?authSource=admin&readPreference=primary&ssl=false
```

主要功能：

*   仪表盘视图：显示实时性能指标
*   集合浏览器：查看和编辑数据
*   性能洞察：索引建议和查询分析
*   Schema分析：数据结构可视化

2.  Mongo Express (轻量级Web监控)

部署配置：

```bash
# 创建docker-compose.yml
version: '3.8'
services:
  mongo-express:
    image: mongo-express:latest
    container_name: mongo-express
    ports:
      - "8081:8081"
    environment:
      ME_CONFIG_MONGODB_SERVER: mongos1
      ME_CONFIG_MONGODB_PORT: 27017
      ME_CONFIG_MONGODB_ADMINUSERNAME: admin
      ME_CONFIG_MONGODB_ADMINPASSWORD: your_password
      ME_CONFIG_BASICAUTH_USERNAME: admin
      ME_CONFIG_BASICAUTH_PASSWORD: your_password
      ME_CONFIG_MONGODB_ENABLE_ADMIN: "true"
    networks:
      - mongocluster
    restart: always
```

3.  Grafana + Prometheus (专业监控方案)

第一步：部署MongoDB Exporter

```yaml
version: '3.8'
services:
  mongodb-exporter:
    image: percona/mongodb_exporter:latest
    command:
      - '--mongodb.uri=mongodb://admin:password@mongos1:27017'
      - '--collecting.database=true'
      - '--collecting.collection=true'
    ports:
      - "9216:9216"
    networks:
      - mongocluster
    restart: always
```

第二步：配置Prometheus

```yaml
# prometheus.yml
global:
  scrape_interval: 15s
  evaluation_interval: 15s

scrape_configs:
  - job_name: 'mongodb'
    static_configs:
      - targets: ['mongodb-exporter:9216']
    metrics_path: /metrics

  - job_name: 'mongodb_instances'
    static_configs:
      - targets:
        - mongos1:27017
        - mongos2:27017
```

部署Prometheus：

```yaml
services:
  prometheus:
    image: prom/prometheus
    ports:
      - "9090:9090"
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml
    networks:
      - mongocluster
    restart: always
```

第三步：部署Grafana

```yaml
services:
  grafana:
    image: grafana/grafana
    ports:
      - "3000:3000"
    environment:
      GF_SECURITY_ADMIN_PASSWORD: your_password
    volumes:
      - grafana-storage:/var/lib/grafana
    networks:
      - mongocluster
    restart: always
```

Grafana 关键监控面板设置：

1.  系统资源监控

```
- CPU使用率: 
  rate(node_cpu_seconds_total{mode="user"}[5m])
- 内存使用: 
  mongodb_memory{type="resident"}
- 磁盘使用: 
  mongodb_mongod_storage_disk_size_bytes
```

2.  MongoDB性能指标

```
- 操作QPS: 
  rate(mongodb_op_counters_total{type="query"}[5m])
- 连接数: 
  mongodb_connections{state="current"}
- 写入延迟: 
  rate(mongodb_mongod_op_latencies_latency_total{type="write"}[5m])
```

3.  分片集群监控

```
- Chunk分布:
  mongodb_mongos_sharding_chunks_total
- 平衡器状态:
  mongodb_mongos_sharding_balancer_enabled
```

告警配置示例：

```yaml
# Grafana告警规则
- alert: HighCPUUsage
  expr: rate(node_cpu_seconds_total{mode="user"}[5m]) > 0.8
  for: 5m
  labels:
    severity: warning
  annotations:
    description: "CPU使用率超过80%"

- alert: HighMemoryUsage
  expr: mongodb_memory{type="resident"} > 3.5e9
  for: 5m
  labels:
    severity: critical
  annotations:
    description: "内存使用超过3.5GB"
```

要我详细解释某个具体的监控配置或者告警规则的设置吗？

## Prompt:
综上，设计一份完整的基础部署和水平扩展方案

## Response:
我来设计一个完整的MongoDB分片集群部署和扩展方案，包括基础部署和后续扩展步骤。

一、基础环境准备

```bash
# 1. 创建必要目录
mkdir -p /opt/mongodb/{config,shard,mongos,keyfile}
mkdir -p /data/mongodb/{config1,config2,config3,shard1,shard2,shard3}
mkdir -p /var/log/mongodb/{config,shard,mongos}

# 2. 创建密钥文件
openssl rand -base64 756 > /opt/mongodb/keyfile/mongodb.key
chmod 400 /opt/mongodb/keyfile/mongodb.key
chown -R 999:999 /opt/mongodb/keyfile/mongodb.key

# 3. 设置目录权限
chown -R 999:999 /opt/mongodb /data/mongodb /var/log/mongodb
chmod -R 755 /opt/mongodb /data/mongodb /var/log/mongodb

# 4. 创建docker网络
docker network create --driver overlay --subnet=10.10.0.0/16 --attachable mongocluster
```

二、配置文件设置

1.  配置服务器配置 (/opt/mongodb/config/configsvr.conf)：

```yaml
storage:
  dbPath: /data/db
  wiredTiger:
    engineConfig:
      cacheSizeGB: 1
      journalCompressor: snappy
    collectionConfig:
      blockCompressor: snappy

systemLog:
  destination: file
  path: /var/log/mongodb/configsvr.log
  logAppend: true

net:
  bindIp: 0.0.0.0
  port: 27019
  maxIncomingConnections: 20000

security:
  authorization: enabled
  keyFile: /mongodb/keyfile/mongodb.key

replication:
  replSetName: configrs

sharding:
  clusterRole: configsvr

processManagement:
  timeZoneInfo: /usr/share/zoneinfo

setParameter:
  maxTransactionLockRequestTimeoutMillis: 5000
```

2.  分片服务器配置 (/opt/mongodb/shard/shardsvr.conf)：

```yaml
storage:
  dbPath: /data/db
  wiredTiger:
    engineConfig:
      cacheSizeGB: 2
      journalCompressor: zstd
    collectionConfig:
      blockCompressor: zstd

systemLog:
  destination: file
  path: /var/log/mongodb/shardsvr.log
  logAppend: true

net:
  bindIp: 0.0.0.0
  port: 27018
  maxIncomingConnections: 20000

security:
  authorization: enabled
  keyFile: /mongodb/keyfile/mongodb.key

replication:
  replSetName: shard1rs

sharding:
  clusterRole: shardsvr

processManagement:
  timeZoneInfo: /usr/share/zoneinfo

setParameter:
  maxTransactionLockRequestTimeoutMillis: 5000
  wiredTigerConcurrentReadTransactions: 128
  wiredTigerConcurrentWriteTransactions: 128
```

三、基础部署步骤

1.  启动配置服务器：

```bash
# 启动配置服务器1
docker run -d --name mongo-config1 --network mongocluster \
  --memory=4g --memory-swap=8g \
  --restart always \
  -v /data/mongodb/config1:/data/db \
  -v /opt/mongodb/config/configsvr.conf:/etc/mongod.conf:ro \
  -v /opt/mongodb/keyfile:/mongodb/keyfile:ro \
  -v /var/log/mongodb/config:/var/log/mongodb \
  -v /usr/share/zoneinfo:/usr/share/zoneinfo:ro \
  -e TZ=Asia/Shanghai \
  mongo:latest mongod --config /etc/mongod.conf

# 重复上述命令启动config2和config3，修改对应的名称和数据目录
```

2.  初始化配置服务器复制集：

```javascript
docker exec -it mongo-config1 mongosh --port 27019
rs.initiate({
    _id: "configrs",
    configsvr: true,
    members: [
        {_id: 0, host: "mongo-config1:27019", priority: 2},
        {_id: 1, host: "mongo-config2:27019", priority: 1},
        {_id: 2, host: "mongo-config3:27019", priority: 1}
    ]
})
```

3.  启动分片服务器：

```bash
# 启动第一个分片
docker run -d --name mongo-shard1 --network mongocluster \
  --memory=4g --memory-swap=8g \
  --restart always \
  -v /data/mongodb/shard1:/data/db \
  -v /opt/mongodb/shard/shardsvr.conf:/etc/mongod.conf:ro \
  -v /opt/mongodb/keyfile:/mongodb/keyfile:ro \
  -v /var/log/mongodb/shard:/var/log/mongodb \
  -v /usr/share/zoneinfo:/usr/share/zoneinfo:ro \
  -e TZ=Asia/Shanghai \
  mongo:latest mongod --config /etc/mongod.conf

# 为第一个分片启动副本节点
docker run -d --name mongo-shard1-sec ...（类似配置）
```

4.  初始化分片复制集：

```javascript
docker exec -it mongo-shard1 mongosh --port 27018
rs.initiate({
    _id: "shard1rs",
    members: [
        {_id: 0, host: "mongo-shard1:27018", priority: 2},
        {_id: 1, host: "mongo-shard1-sec:27018", priority: 1}
    ]
})
```

5.  启动mongos路由：

```bash
docker run -d --name mongos1 --network mongocluster \
  --memory=4g --memory-swap=8g \
  --restart always \
  -p 27017:27017 \
  -v /opt/mongodb/mongos/mongos.conf:/etc/mongos.conf:ro \
  -v /opt/mongodb/keyfile:/mongodb/keyfile:ro \
  -v /var/log/mongodb/mongos:/var/log/mongodb \
  -v /usr/share/zoneinfo:/usr/share/zoneinfo:ro \
  -e TZ=Asia/Shanghai \
  mongo:latest mongos --config /etc/mongos.conf
```

四、水平扩展方案

1.  添加新分片：

```bash
# 部署新分片服务器
docker run -d --name mongo-shard2 ...（类似配置）

# 初始化新分片复制集
docker exec -it mongo-shard2 mongosh --port 27018
rs.initiate({...})

# 添加新分片到集群
docker exec -it mongos1 mongosh
sh.addShard("shard2rs/mongo-shard2:27018,mongo-shard2-sec:27018")
```

2.  调整分片策略：

```javascript
// 配置chunk大小
use config
db.settings.updateOne(
    { _id: "chunksize" },
    { $set: { value: 32 } },
    { upsert: true }
)

// 为新分片添加标签
sh.addShardTag("shard2rs", "newdata")

// 设置特定集合的分片规则
sh.enableSharding("yourdb")
sh.shardCollection(
    "yourdb.yourcollection",
    { "_id": "hashed" }
)
```

五、监控部署

1.  部署Mongo Express：

```bash
docker run -d --name mongo-express \
  --network mongocluster \
  -p 8081:8081 \
  -e ME_CONFIG_MONGODB_SERVER=mongos1 \
  -e ME_CONFIG_MONGODB_PORT=27017 \
  -e ME_CONFIG_MONGODB_ADMINUSERNAME=admin \
  -e ME_CONFIG_MONGODB_ADMINPASSWORD=password \
  mongo-express
```

六、维护建议

1.  定期检查项：

```javascript
// 检查分片状态
sh.status()

// 检查复制集状态
rs.status()

// 检查均衡器状态
sh.getBalancerState()
```

2.  性能监控阈值：

*   CPU使用率 < 70%
*   内存使用率 < 80%
*   写入延迟 < 100ms
*   读取延迟 < 50ms

需要我详细解释任何部分吗？

## Prompt:
不够细致，没标明可变目录，还有新增的修改，因为我可能用其他数据目录，那么需要改一连串东西，应该使用shell，生成变量，用于批量化，还有跨主机网络也没说明白，请分步思考你要输出的内容，然后一步步输出分段内容

## Response:
您说得对，让我重新组织一个更加系统化和可配置的部署方案。我会分步骤详细说明。

首先，我们先设计环境变量和基础配置部分：

1.  环境变量配置脚本 (mongo-env.sh)：

```bash
#!/bin/bash

# 基础目录配置
export MONGO_BASE="/opt/mongodb"              # 基础配置目录
export MONGO_DATA="/data/mongodb"             # 数据存储目录
export MONGO_LOG="/var/log/mongodb"           # 日志目录

# 集群网络配置
export MONGO_NETWORK="mongocluster"           # docker网络名称
export MONGO_SUBNET="10.10.0.0/16"           # 网络子网
export DOCKER_SWARM_MANAGER="192.168.1.100"   # docker swarm管理节点IP

# 认证配置
export MONGO_ADMIN_USER="admin"
export MONGO_ADMIN_PASS="your_password"

# 资源限制配置
export MONGO_MEMORY_LIMIT="4g"
export MONGO_SWAP_LIMIT="8g"

# 端口配置
export MONGOS_PORT="27017"
export CONFIG_PORT="27019"
export SHARD_PORT="27018"

# 版本配置
export MONGO_VERSION="latest"

# 分片配置
export SHARD_CACHE_SIZE="2"                   # 分片WiredTiger缓存大小(GB)
export CONFIG_CACHE_SIZE="1"                  # 配置服务器缓存大小(GB)
```

需要我继续输出下一部分（目录创建和基础设置）的内容吗？我们可以一步一步完善这个方案。

## Prompt:
继续

## Response:
好的，让我继续设计目录创建和基础设置部分。

2.  基础环境准备脚本 (mongo-prepare.sh)：

```bash
#!/bin/bash

# 加载环境变量
source ./mongo-env.sh

# 创建目录函数
create_directories() {
    local base_dir=$1
    local type=$2
    
    for i in {1..3}; do
        mkdir -p "${base_dir}/${type}${i}"
        echo "Created directory: ${base_dir}/${type}${i}"
    done
}

# 创建基础配置目录
mkdir -p "${MONGO_BASE}/{config,shard,mongos,keyfile}"
echo "Created base configuration directories"

# 创建数据目录
create_directories "$MONGO_DATA" "config"
create_directories "$MONGO_DATA" "shard"
mkdir -p "$MONGO_DATA/mongos"

# 创建日志目录
mkdir -p "${MONGO_LOG}/{config,shard,mongos}"
echo "Created log directories"

# 生成密钥文件
openssl rand -base64 756 > "${MONGO_BASE}/keyfile/mongodb.key"
chmod 400 "${MONGO_BASE}/keyfile/mongodb.key"

# 设置目录权限
chown -R 999:999 "$MONGO_BASE" "$MONGO_DATA" "$MONGO_LOG"
chmod -R 755 "$MONGO_BASE" "$MONGO_DATA" "$MONGO_LOG"

# Docker Swarm网络配置
setup_docker_network() {
    if ! docker network ls | grep -q "$MONGO_NETWORK"; then
        echo "Creating overlay network: $MONGO_NETWORK"
        docker network create \
            --driver overlay \
            --subnet=$MONGO_SUBNET \
            --attachable \
            "$MONGO_NETWORK"
    else
        echo "Network $MONGO_NETWORK already exists"
    fi
}

# 初始化Docker Swarm（如果尚未初始化）
init_docker_swarm() {
    if ! docker info | grep -q "Swarm: active"; then
        echo "Initializing Docker Swarm"
        docker swarm init --advertise-addr "$DOCKER_SWARM_MANAGER"
        
        # 生成工作节点加入命令
        docker swarm join-token worker | grep "docker" > swarm_join_command.txt
        echo "Worker join command saved to swarm_join_command.txt"
    else
        echo "Docker Swarm already initialized"
    fi
}

# 执行网络设置
init_docker_swarm
setup_docker_network

echo "MongoDB cluster environment preparation completed"
```

3.  配置文件生成脚本 (generate-configs.sh)：

```bash
#!/bin/bash

# 加载环境变量
source ./mongo-env.sh

# 生成配置服务器配置
generate_config_server_conf() {
    cat > "${MONGO_BASE}/config/configsvr.conf" <<EOF
storage:
  dbPath: /data/db
  wiredTiger:
    engineConfig:
      cacheSizeGB: ${CONFIG_CACHE_SIZE}
      journalCompressor: snappy
    collectionConfig:
      blockCompressor: snappy

systemLog:
  destination: file
  path: /var/log/mongodb/configsvr.log
  logAppend: true

net:
  bindIp: 0.0.0.0
  port: ${CONFIG_PORT}
  maxIncomingConnections: 20000

security:
  authorization: enabled
  keyFile: /mongodb/keyfile/mongodb.key

replication:
  replSetName: configrs

sharding:
  clusterRole: configsvr

processManagement:
  timeZoneInfo: /usr/share/zoneinfo

setParameter:
  maxTransactionLockRequestTimeoutMillis: 5000
EOF

    echo "Generated config server configuration"
}

# 生成分片服务器配置
generate_shard_server_conf() {
    cat > "${MONGO_BASE}/shard/shardsvr.conf" <<EOF
storage:
  dbPath: /data/db
  wiredTiger:
    engineConfig:
      cacheSizeGB: ${SHARD_CACHE_SIZE}
      journalCompressor: zstd
    collectionConfig:
      blockCompressor: zstd

systemLog:
  destination: file
  path: /var/log/mongodb/shardsvr.log
  logAppend: true

net:
  bindIp: 0.0.0.0
  port: ${SHARD_PORT}
  maxIncomingConnections: 20000

security:
  authorization: enabled
  keyFile: /mongodb/keyfile/mongodb.key

replication:
  replSetName: shard1rs

sharding:
  clusterRole: shardsvr

processManagement:
  timeZoneInfo: /usr/share/zoneinfo

setParameter:
  maxTransactionLockRequestTimeoutMillis: 5000
  wiredTigerConcurrentReadTransactions: 128
  wiredTigerConcurrentWriteTransactions: 128
EOF

    echo "Generated shard server configuration"
}

# 执行配置文件生成
generate_config_server_conf
generate_shard_server_conf

echo "Configuration files generation completed"
```

需要我继续输出容器启动和初始化脚本的部分吗？这部分将包含如何启动各个组件并进行初始化配置。

## Prompt:
继续

## Response:
好的，让我继续设计容器启动和初始化部分的脚本。

4.  配置服务器部署脚本 (deploy-config-servers.sh)：

```bash
#!/bin/bash

# 加载环境变量
source ./mongo-env.sh

# 启动配置服务器函数
start_config_server() {
    local node_id=$1
    local container_name="mongo-config${node_id}"
    local data_dir="${MONGO_DATA}/config${node_id}"
    
    echo "Starting config server ${container_name}..."
    
    docker run -d \
        --name "${container_name}" \
        --hostname "${container_name}" \
        --network "${MONGO_NETWORK}" \
        --memory="${MONGO_MEMORY_LIMIT}" \
        --memory-swap="${MONGO_SWAP_LIMIT}" \
        --restart always \
        -v "${data_dir}:/data/db" \
        -v "${MONGO_BASE}/config/configsvr.conf:/etc/mongod.conf:ro" \
        -v "${MONGO_BASE}/keyfile:/mongodb/keyfile:ro" \
        -v "${MONGO_LOG}/config:/var/log/mongodb" \
        -v /usr/share/zoneinfo:/usr/share/zoneinfo:ro \
        -e TZ=Asia/Shanghai \
        "mongo:${MONGO_VERSION}" mongod --config /etc/mongod.conf
        
    if [ $? -eq 0 ]; then
        echo "Config server ${container_name} started successfully"
    else
        echo "Failed to start config server ${container_name}"
        exit 1
    fi
}

# 启动所有配置服务器
for i in {1..3}; do
    start_config_server $i
done

# 等待服务器启动
echo "Waiting for config servers to start..."
sleep 30

# 初始化配置服务器复制集
init_config_replica_set() {
    docker exec -it mongo-config1 mongosh --port "${CONFIG_PORT}" --eval "
    rs.initiate({
        _id: 'configrs',
        configsvr: true,
        members: [
            {_id: 0, host: 'mongo-config1:${CONFIG_PORT}', priority: 2},
            {_id: 1, host: 'mongo-config2:${CONFIG_PORT}', priority: 1},
            {_id: 2, host: 'mongo-config3:${CONFIG_PORT}', priority: 1}
        ]
    })
    "
    
    echo "Config server replica set initialization complete"
}

init_config_replica_set
```

5.  分片服务器部署脚本 (deploy-shard-servers.sh)：

```bash
#!/bin/bash

# 加载环境变量
source ./mongo-env.sh

# 启动分片服务器函数
start_shard_server() {
    local node_id=$1
    local is_secondary=$2
    local container_name="mongo-shard${node_id}${is_secondary:+-sec}"
    local data_dir="${MONGO_DATA}/shard${node_id}${is_secondary:+-sec}"
    
    echo "Starting shard server ${container_name}..."
    
    docker run -d \
        --name "${container_name}" \
        --hostname "${container_name}" \
        --network "${MONGO_NETWORK}" \
        --memory="${MONGO_MEMORY_LIMIT}" \
        --memory-swap="${MONGO_SWAP_LIMIT}" \
        --restart always \
        -v "${data_dir}:/data/db" \
        -v "${MONGO_BASE}/shard/shardsvr.conf:/etc/mongod.conf:ro" \
        -v "${MONGO_BASE}/keyfile:/mongodb/keyfile:ro" \
        -v "${MONGO_LOG}/shard:/var/log/mongodb" \
        -v /usr/share/zoneinfo:/usr/share/zoneinfo:ro \
        -e TZ=Asia/Shanghai \
        "mongo:${MONGO_VERSION}" mongod --config /etc/mongod.conf
    
    if [ $? -eq 0 ]; then
        echo "Shard server ${container_name} started successfully"
    else
        echo "Failed to start shard server ${container_name}"
        exit 1
    fi
}

# 启动主分片和副本
start_shard_server 1
start_shard_server 1 "secondary"

# 等待服务器启动
echo "Waiting for shard servers to start..."
sleep 30

# 初始化分片复制集
init_shard_replica_set() {
    docker exec -it mongo-shard1 mongosh --port "${SHARD_PORT}" --eval "
    rs.initiate({
        _id: 'shard1rs',
        members: [
            {_id: 0, host: 'mongo-shard1:${SHARD_PORT}', priority: 2},
            {_id: 1, host: 'mongo-shard1-sec:${SHARD_PORT}', priority: 1}
        ]
    })
    "
    
    echo "Shard replica set initialization complete"
}

init_shard_replica_set
```

需要我继续输出路由服务器部署和集群初始化的脚本吗？这部分将包含mongos的部署和整个集群的最终配置。

## Prompt:
继续

## Response:
好的，让我继续设计路由服务器部署和集群最终配置的脚本。

6.  路由服务器部署脚本 (deploy-mongos.sh)：

```bash
#!/bin/bash

# 加载环境变量
source ./mongo-env.sh

# 生成mongos配置文件
generate_mongos_conf() {
    cat > "${MONGO_BASE}/mongos/mongos.conf" <<EOF
systemLog:
  destination: file
  path: /var/log/mongodb/mongos.log
  logAppend: true

net:
  bindIp: 0.0.0.0
  port: ${MONGOS_PORT}
  maxIncomingConnections: 20000

security:
  keyFile: /mongodb/keyfile/mongodb.key

sharding:
  configDB: configrs/mongo-config1:${CONFIG_PORT},mongo-config2:${CONFIG_PORT},mongo-config3:${CONFIG_PORT}

processManagement:
  timeZoneInfo: /usr/share/zoneinfo

setParameter:
  maxTransactionLockRequestTimeoutMillis: 5000
EOF

    echo "Generated mongos configuration"
}

# 启动mongos路由服务器
start_mongos() {
    local container_name="mongos1"
    
    echo "Starting mongos router ${container_name}..."
    
    docker run -d \
        --name "${container_name}" \
        --hostname "${container_name}" \
        --network "${MONGO_NETWORK}" \
        --memory="${MONGO_MEMORY_LIMIT}" \
        --memory-swap="${MONGO_SWAP_LIMIT}" \
        --restart always \
        -p "${MONGOS_PORT}:${MONGOS_PORT}" \
        -v "${MONGO_BASE}/mongos/mongos.conf:/etc/mongos.conf:ro" \
        -v "${MONGO_BASE}/keyfile:/mongodb/keyfile:ro" \
        -v "${MONGO_LOG}/mongos:/var/log/mongodb" \
        -v /usr/share/zoneinfo:/usr/share/zoneinfo:ro \
        -e TZ=Asia/Shanghai \
        "mongo:${MONGO_VERSION}" mongos --config /etc/mongos.conf
        
    if [ $? -eq 0 ]; then
        echo "Mongos router ${container_name} started successfully"
    else
        echo "Failed to start mongos router ${container_name}"
        exit 1
    fi
}

# 执行部署
generate_mongos_conf
start_mongos

echo "Waiting for mongos to start..."
sleep 30
```

7.  集群初始化脚本 (init-cluster.sh)：

```bash
#!/bin/bash

# 加载环境变量
source ./mongo-env.sh

# 初始化管理员用户
init_admin_user() {
    echo "Initializing admin user..."
    docker exec -it mongos1 mongosh --port "${MONGOS_PORT}" --eval "
    use admin
    db.createUser({
        user: '${MONGO_ADMIN_USER}',
        pwd: '${MONGO_ADMIN_PASS}',
        roles: [ { role: 'root', db: 'admin' } ]
    })
    "
}

# 添加分片到集群
add_shard() {
    echo "Adding shard to cluster..."
    docker exec -it mongos1 mongosh --port "${MONGOS_PORT}" -u "${MONGO_ADMIN_USER}" -p "${MONGO_ADMIN_PASS}" --authenticationDatabase admin --eval "
    sh.addShard('shard1rs/mongo-shard1:${SHARD_PORT},mongo-shard1-sec:${SHARD_PORT}')
    "
}

# 配置分片集群参数
configure_cluster() {
    echo "Configuring cluster parameters..."
    docker exec -it mongos1 mongosh --port "${MONGOS_PORT}" -u "${MONGO_ADMIN_USER}" -p "${MONGO_ADMIN_PASS}" --authenticationDatabase admin --eval "
    // 设置chunk大小为32MB
    use config
    db.settings.updateOne(
        { _id: 'chunksize' },
        { \$set: { value: 32 } },
        { upsert: true }
    )
    
    // 配置均衡器
    db.settings.updateOne(
        { _id: 'balancer' },
        { 
            \$set: { 
                activeWindow: null,
                _secondaryThrottle: false,
                maxParallelCopies: 8
            }
        },
        { upsert: true }
    )
    "
}

# 执行初始化步骤
init_admin_user
sleep 5
add_shard
sleep 5
configure_cluster

echo "Cluster initialization completed"
```

8.  集群状态检查脚本 (check-cluster.sh)：

```bash
#!/bin/bash

# 加载环境变量
source ./mongo-env.sh

# 检查集群状态
check_cluster_status() {
    echo "Checking cluster status..."
    docker exec -it mongos1 mongosh --port "${MONGOS_PORT}" -u "${MONGO_ADMIN_USER}" -p "${MONGO_ADMIN_PASS}" --authenticationDatabase admin --eval "
    // 检查分片状态
    sh.status()
    
    // 检查均衡器状态
    sh.getBalancerState()
    
    // 检查数据库状态
    db.serverStatus()
    "
}

# 检查复制集状态
check_replica_status() {
    echo "Checking config servers replica set status..."
    docker exec -it mongo-config1 mongosh --port "${CONFIG_PORT}" -u "${MONGO_ADMIN_USER}" -p "${MONGO_ADMIN_PASS}" --authenticationDatabase admin --eval "rs.status()"
    
    echo "Checking shard replica set status..."
    docker exec -it mongo-shard1 mongosh --port "${SHARD_PORT}" -u "${MONGO_ADMIN_USER}" -p "${MONGO_ADMIN_PASS}" --authenticationDatabase admin --eval "rs.status()"
}

# 执行检查
check_cluster_status
check_replica_status
```

需要我继续输出水平扩展和监控部署的脚本吗？这部分将包含如何添加新的分片和设置监控系统。

## Prompt:
继续

## Response:
好的，让我继续设计水平扩展和监控部署的脚本。

9.  水平扩展脚本 (add-new-shard.sh)：

```bash
#!/bin/bash

# 加载环境变量
source ./mongo-env.sh

# 检查参数
if [ "$#" -ne 1 ]; then
    echo "Usage: $0 <shard_number>"
    echo "Example: $0 2"
    exit 1
fi

SHARD_NUMBER=$1
SHARD_NAME="shard${SHARD_NUMBER}rs"

# 创建新分片的目录
create_shard_directories() {
    echo "Creating directories for new shard..."
    mkdir -p "${MONGO_DATA}/shard${SHARD_NUMBER}"
    mkdir -p "${MONGO_DATA}/shard${SHARD_NUMBER}-sec"
    chown -R 999:999 "${MONGO_DATA}/shard${SHARD_NUMBER}"*
    chmod -R 755 "${MONGO_DATA}/shard${SHARD_NUMBER}"*
}

# 生成新分片的配置
generate_shard_config() {
    local config_file="${MONGO_BASE}/shard/shardsvr${SHARD_NUMBER}.conf"
    cp "${MONGO_BASE}/shard/shardsvr.conf" "$config_file"
    
    # 修改复制集名称
    sed -i "s/replSetName: .*/replSetName: ${SHARD_NAME}/" "$config_file"
    
    echo "Generated configuration for new shard"
}

# 启动新分片服务器
start_new_shard() {
    local is_secondary=$1
    local suffix=${is_secondary:+-sec}
    local container_name="mongo-shard${SHARD_NUMBER}${suffix}"
    local data_dir="${MONGO_DATA}/shard${SHARD_NUMBER}${suffix}"
    local config_file="${MONGO_BASE}/shard/shardsvr${SHARD_NUMBER}.conf"
    
    echo "Starting ${container_name}..."
    
    docker run -d \
        --name "${container_name}" \
        --hostname "${container_name}" \
        --network "${MONGO_NETWORK}" \
        --memory="${MONGO_MEMORY_LIMIT}" \
        --memory-swap="${MONGO_SWAP_LIMIT}" \
        --restart always \
        -v "${data_dir}:/data/db" \
        -v "${config_file}:/etc/mongod.conf:ro" \
        -v "${MONGO_BASE}/keyfile:/mongodb/keyfile:ro" \
        -v "${MONGO_LOG}/shard:/var/log/mongodb" \
        -v /usr/share/zoneinfo:/usr/share/zoneinfo:ro \
        -e TZ=Asia/Shanghai \
        "mongo:${MONGO_VERSION}" mongod --config /etc/mongod.conf
}

# 初始化新分片复制集
init_new_shard_replica() {
    echo "Initializing new shard replica set..."
    sleep 30  # 等待服务器启动
    
    docker exec -it mongo-shard${SHARD_NUMBER} mongosh --port "${SHARD_PORT}" --eval "
    rs.initiate({
        _id: '${SHARD_NAME}',
        members: [
            {_id: 0, host: 'mongo-shard${SHARD_NUMBER}:${SHARD_PORT}', priority: 2},
            {_id: 1, host: 'mongo-shard${SHARD_NUMBER}-sec:${SHARD_PORT}', priority: 1}
        ]
    })
    "
}

# 添加新分片到集群
add_shard_to_cluster() {
    echo "Adding new shard to cluster..."
    sleep 30  # 等待复制集初始化
    
    docker exec -it mongos1 mongosh --port "${MONGOS_PORT}" \
        -u "${MONGO_ADMIN_USER}" \
        -p "${MONGO_ADMIN_PASS}" \
        --authenticationDatabase admin \
        --eval "
        sh.addShard('${SHARD_NAME}/mongo-shard${SHARD_NUMBER}:${SHARD_PORT},mongo-shard${SHARD_NUMBER}-sec:${SHARD_PORT}')
        "
}

# 执行扩展步骤
create_shard_directories
generate_shard_config
start_new_shard
start_new_shard "secondary"
init_new_shard_replica
add_shard_to_cluster

echo "New shard ${SHARD_NAME} has been added to the cluster"
```

10.  监控部署脚本 (deploy-monitoring.sh)：

```bash
#!/bin/bash

# 加载环境变量
source ./mongo-env.sh

# 创建监控配置目录
mkdir -p "${MONGO_BASE}/monitoring/{prometheus,grafana}"

# 生成Prometheus配置
generate_prometheus_config() {
    cat > "${MONGO_BASE}/monitoring/prometheus/prometheus.yml" <<EOF
global:
  scrape_interval: 15s
  evaluation_interval: 15s

scrape_configs:
  - job_name: 'mongodb'
    static_configs:
      - targets: ['mongodb-exporter:9216']
    metrics_path: /metrics

  - job_name: 'mongodb_instances'
    static_configs:
      - targets:
        - 'mongos1:${MONGOS_PORT}'
EOF
}

# 部署MongoDB Exporter
deploy_mongodb_exporter() {
    docker run -d \
        --name mongodb-exporter \
        --network "${MONGO_NETWORK}" \
        -p 9216:9216 \
        -e MONGODB_URI="mongodb://${MONGO_ADMIN_USER}:${MONGO_ADMIN_PASS}@mongos1:${MONGOS_PORT}" \
        percona/mongodb_exporter:latest
}

# 部署Prometheus
deploy_prometheus() {
    docker run -d \
        --name prometheus \
        --network "${MONGO_NETWORK}" \
        -p 9090:9090 \
        -v "${MONGO_BASE}/monitoring/prometheus:/etc/prometheus" \
        prom/prometheus
}

# 部署Grafana
deploy_grafana() {
    docker run -d \
        --name grafana \
        --network "${MONGO_NETWORK}" \
        -p 3000:3000 \
        -e "GF_SECURITY_ADMIN_PASSWORD=admin" \
        -v "${MONGO_BASE}/monitoring/grafana:/var/lib/grafana" \
        grafana/grafana
}

# 部署监控系统
generate_prometheus_config
deploy_mongodb_exporter
deploy_prometheus
deploy_grafana

echo "Monitoring system has been deployed"
```

11.  监控面板配置脚本 (configure-monitoring.sh)：

```bash
#!/bin/bash

# 加载环境变量
source ./mongo-env.sh

# 等待Grafana启动
sleep 30

# 添加Prometheus数据源
curl -X POST \
    -H "Content-Type: application/json" \
    -d '{"name":"Prometheus","type":"prometheus","url":"http://prometheus:9090","access":"proxy"}' \
    http://admin:admin@localhost:3000/api/datasources

# 导入MongoDB仪表板
# 注意：这里需要准备一个预配置的仪表板JSON文件
if [ -f "mongodb-dashboard.json" ]; then
    curl -X POST \
        -H "Content-Type: application/json" \
        -d @mongodb-dashboard.json \
        http://admin:admin@localhost:3000/api/dashboards/db
fi

echo "Monitoring configuration completed"
```

需要我继续输出备份恢复和维护脚本吗？这部分将包含日常维护、备份和恢复的自动化脚本。

## Prompt:
继续

## Response:
好的，让我继续设计备份恢复和维护脚本部分。

12.  备份脚本 (backup-cluster.sh)：

```bash
#!/bin/bash

# 加载环境变量
source ./mongo-env.sh

# 备份配置
BACKUP_ROOT="/backup/mongodb"
BACKUP_DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_RETENTION_DAYS=7

# 创建备份目录
setup_backup_dirs() {
    mkdir -p "${BACKUP_ROOT}/{config,shards,dumps}"
    chmod -R 755 "${BACKUP_ROOT}"
}

# 备份配置文件
backup_configs() {
    echo "Backing up configuration files..."
    local config_backup_dir="${BACKUP_ROOT}/config/${BACKUP_DATE}"
    mkdir -p "$config_backup_dir"
    
    cp -r "${MONGO_BASE}/config" "$config_backup_dir"
    cp -r "${MONGO_BASE}/shard" "$config_backup_dir"
    cp -r "${MONGO_BASE}/mongos" "$config_backup_dir"
}

# 执行mongodump
perform_backup() {
    local db_name=$1
    echo "Backing up database: ${db_name}"
    
    docker run --rm \
        --network "${MONGO_NETWORK}" \
        -v "${BACKUP_ROOT}/dumps:/backup" \
        "mongo:${MONGO_VERSION}" \
        mongodump \
            --uri="mongodb://${MONGO_ADMIN_USER}:${MONGO_ADMIN_PASS}@mongos1:${MONGOS_PORT}/${db_name}?authSource=admin" \
            --out="/backup/${BACKUP_DATE}" \
            --gzip \
            --oplog
}

# 清理旧备份
cleanup_old_backups() {
    echo "Cleaning up old backups..."
    find "${BACKUP_ROOT}" -type d -mtime +${BACKUP_RETENTION_DAYS} -exec rm -rf {} \;
}

# 验证备份
verify_backup() {
    local backup_dir="${BACKUP_ROOT}/dumps/${BACKUP_DATE}"
    echo "Verifying backup in ${backup_dir}"
    
    docker run --rm \
        --network "${MONGO_NETWORK}" \
        -v "${backup_dir}:/backup" \
        "mongo:${MONGO_VERSION}" \
        mongorestore \
            --uri="mongodb://${MONGO_ADMIN_USER}:${MONGO_ADMIN_PASS}@mongos1:${MONGOS_PORT}?authSource=admin" \
            --dry-run \
            --gzip \
            "/backup"
}

# 主备份流程
setup_backup_dirs
backup_configs

# 获取所有数据库列表并备份
DATABASES=$(docker exec -it mongos1 mongosh --quiet --port "${MONGOS_PORT}" \
    -u "${MONGO_ADMIN_USER}" \
    -p "${MONGO_ADMIN_PASS}" \
    --eval "db.adminCommand('listDatabases').databases.map(db => db.name).join(' ')")

for db in $DATABASES; do
    if [[ "$db" != "admin" && "$db" != "config" && "$db" != "local" ]]; then
        perform_backup "$db"
    fi
done

verify_backup
cleanup_old_backups
```

13.  恢复脚本 (restore-cluster.sh)：

```bash
#!/bin/bash

# 加载环境变量
source ./mongo-env.sh

# 检查参数
if [ "$#" -ne 1 ]; then
    echo "Usage: $0 <backup_date>"
    echo "Example: $0 20250108_123000"
    exit 1
fi

BACKUP_DATE=$1
BACKUP_ROOT="/backup/mongodb"

# 验证备份存在
validate_backup() {
    if [ ! -d "${BACKUP_ROOT}/dumps/${BACKUP_DATE}" ]; then
        echo "Backup directory not found: ${BACKUP_ROOT}/dumps/${BACKUP_DATE}"
        exit 1
    fi
}

# 停止均衡器
disable_balancer() {
    echo "Disabling balancer..."
    docker exec -it mongos1 mongosh --port "${MONGOS_PORT}" \
        -u "${MONGO_ADMIN_USER}" \
        -p "${MONGO_ADMIN_PASS}" \
        --eval "sh.stopBalancer()"
}

# 执行恢复
perform_restore() {
    local backup_dir="${BACKUP_ROOT}/dumps/${BACKUP_DATE}"
    echo "Restoring from ${backup_dir}"
    
    docker run --rm \
        --network "${MONGO_NETWORK}" \
        -v "${backup_dir}:/backup" \
        "mongo:${MONGO_VERSION}" \
        mongorestore \
            --uri="mongodb://${MONGO_ADMIN_USER}:${MONGO_ADMIN_PASS}@mongos1:${MONGOS_PORT}?authSource=admin" \
            --gzip \
            --oplogReplay \
            --drop \
            "/backup"
}

# 启用均衡器
enable_balancer() {
    echo "Enabling balancer..."
    docker exec -it mongos1 mongosh --port "${MONGOS_PORT}" \
        -u "${MONGO_ADMIN_USER}" \
        -p "${MONGO_ADMIN_PASS}" \
        --eval "sh.startBalancer()"
}

# 执行恢复流程
validate_backup
disable_balancer
perform_restore
enable_balancer
```

14.  维护脚本 (maintenance.sh)：

```bash
#!/bin/bash

# 加载环境变量
source ./mongo-env.sh

# 检查集群健康状态
check_cluster_health() {
    echo "Checking cluster health..."
    docker exec -it mongos1 mongosh --port "${MONGOS_PORT}" \
        -u "${MONGO_ADMIN_USER}" \
        -p "${MONGO_ADMIN_PASS}" \
        --eval "
        // 检查分片状态
        sh.status();
        // 检查集群成员
        db.adminCommand({ listShards: 1 });
        // 检查均衡器状态
        sh.getBalancerState();
        // 检查复制集状态
        db.adminCommand({ replSetGetStatus: 1 });
        "
}

# 压缩数据库
compact_collections() {
    echo "Compacting collections..."
    docker exec -it mongos1 mongosh --port "${MONGOS_PORT}" \
        -u "${MONGO_ADMIN_USER}" \
        -p "${MONGO_ADMIN_PASS}" \
        --eval "
        db.getMongo().getDBs().databases.forEach(function(db) {
            if(db.name !== 'admin' && db.name !== 'config' && db.name !== 'local') {
                db.getCollectionNames().forEach(function(collName) {
                    db.runCommand({ compact: collName });
                });
            }
        });
        "
}

# 清理孤立文档
cleanup_orphaned() {
    echo "Cleaning up orphaned documents..."
    docker exec -it mongos1 mongosh --port "${MONGOS_PORT}" \
        -u "${MONGO_ADMIN_USER}" \
        -p "${MONGO_ADMIN_PASS}" \
        --eval "
        db.adminCommand({ cleanupOrphaned: '*' });
        "
}

# 分析索引使用情况
analyze_indexes() {
    echo "Analyzing index usage..."
    docker exec -it mongos1 mongosh --port "${MONGOS_PORT}" \
        -u "${MONGO_ADMIN_USER}" \
        -p "${MONGO_ADMIN_PASS}" \
        --eval "
        db.getMongo().getDBs().databases.forEach(function(db) {
            if(db.name !== 'admin' && db.name !== 'config' && db.name !== 'local') {
                db.getCollectionNames().forEach(function(collName) {
                    print('Analyzing indexes for ' + db.name + '.' + collName);
                    db[collName].aggregate([
                        { \$indexStats: { } }
                    ]).forEach(printjson);
                });
            }
        });
        "
}

# 执行维护任务
check_cluster_health
compact_collections
cleanup_orphaned
analyze_indexes
```

需要我继续补充性能优化和故障处理脚本吗？这部分将包含性能调优和常见问题的处理方案。

## Prompt:
继续

## Response:
好的，让我继续设计性能优化和故障处理脚本。

15.  性能优化脚本 (optimize-performance.sh)：

```bash
#!/bin/bash

# 加载环境变量
source ./mongo-env.sh

# 性能诊断
diagnose_performance() {
    echo "Running performance diagnostics..."
    docker exec -it mongos1 mongosh --port "${MONGOS_PORT}" \
        -u "${MONGO_ADMIN_USER}" \
        -p "${MONGO_ADMIN_PASS}" \
        --eval "
        // 获取当前操作
        var currentOps = db.currentOp();
        
        // 分析慢查询
        var slowQueries = db.getSiblingDB('admin').system.profile.find({
            millis: {\$gt: 100}
        }).sort({ts: -1}).limit(10);
        
        // 检查连接状态
        var connectionStatus = db.serverStatus().connections;
        
        // 内存使用情况
        var memoryStatus = db.serverStatus().wiredTiger.cache;
        
        printjson({
            currentOps: currentOps,
            slowQueries: slowQueries,
            connectionStatus: connectionStatus,
            memoryStatus: memoryStatus
        });
        "
}

# 优化分片分布
optimize_chunk_distribution() {
    echo "Optimizing chunk distribution..."
    docker exec -it mongos1 mongosh --port "${MONGOS_PORT}" \
        -u "${MONGO_ADMIN_USER}" \
        -p "${MONGO_ADMIN_PASS}" \
        --eval "
        // 检查chunk分布
        var chunks = db.getSiblingDB('config').chunks.aggregate([
            {\$group: {_id: '\$shard', count: {\$sum: 1}}}
        ]);
        
        // 如果分布不均匀，调整balancer配置
        db.getSiblingDB('config').settings.updateOne(
            {_id: 'balancer'},
            {\$set: {
                activeWindow: null,
                _secondaryThrottle: false,
                maxParallelCopies: 8
            }},
            {upsert: true}
        );
        
        sh.startBalancer();
        "
}

# 索引优化
optimize_indexes() {
    echo "Optimizing indexes..."
    docker exec -it mongos1 mongosh --port "${MONGOS_PORT}" \
        -u "${MONGO_ADMIN_USER}" \
        -p "${MONGO_ADMIN_PASS}" \
        --eval "
        // 分析索引使用情况
        db.getMongo().getDBs().databases.forEach(function(dbName) {
            if(dbName.name !== 'admin' && dbName.name !== 'config' && dbName.name !== 'local') {
                var db = db.getSiblingDB(dbName.name);
                db.getCollectionNames().forEach(function(collName) {
                    // 获取索引使用统计
                    var indexStats = db[collName].aggregate([
                        {\$indexStats: {}}
                    ]).toArray();
                    
                    // 标识未使用的索引
                    indexStats.forEach(function(stat) {
                        if(stat.accesses.ops === 0) {
                            print('Unused index in ' + dbName.name + '.' + collName + ': ' + stat.name);
                        }
                    });
                });
            }
        });
        "
}

# WiredTiger缓存优化
optimize_wiredtiger() {
    echo "Optimizing WiredTiger cache..."
    # 获取系统内存信息
    TOTAL_MEM=$(free -g | awk '/^Mem:/{print $2}')
    # 计算WiredTiger缓存大小（系统内存的60%）
    CACHE_SIZE=$((TOTAL_MEM * 60 / 100))
    
    # 更新配置文件中的缓存设置
    for shard in {1..3}; do
        sed -i "s/cacheSizeGB:.*$/cacheSizeGB: ${CACHE_SIZE}/" \
            "${MONGO_BASE}/shard/shardsvr${shard}.conf"
    done
}

# 执行所有优化
run_all_optimizations() {
    diagnose_performance
    optimize_chunk_distribution
    optimize_indexes
    optimize_wiredtiger
}

# 主函数
main() {
    echo "Starting performance optimization..."
    run_all_optimizations
    echo "Performance optimization completed"
}

main
```

16.  故障处理脚本 (troubleshoot.sh)：

```bash
#!/bin/bash

# 加载环境变量
source ./mongo-env.sh

# 检查容器状态
check_containers() {
    echo "Checking container status..."
    docker ps -a --format "table {{.Names}}\t{{.Status}}\t{{.State}}" | grep "mongo"
}

# 检查日志错误
check_logs() {
    local container_name=$1
    local lines=${2:-100}
    echo "Checking logs for ${container_name}..."
    docker logs --tail $lines $container_name | grep -i "error\|warning\|failed"
}

# 检查复制集状态
check_replica_status() {
    echo "Checking replica set status..."
    for type in "config" "shard"; do
        docker exec -it mongo-${type}1 mongosh --port "${CONFIG_PORT}" \
            -u "${MONGO_ADMIN_USER}" \
            -p "${MONGO_ADMIN_PASS}" \
            --eval "rs.status()"
    done
}

# 检查网络连接
check_network() {
    echo "Checking network connectivity..."
    for container in $(docker ps --format "{{.Names}}" | grep "mongo"); do
        echo "Testing connectivity from $container:"
        docker exec $container ping -c 2 mongos1
    done
}

# 修复复制集问题
fix_replica_set() {
    local rs_name=$1
    local port=$2
    echo "Attempting to fix replica set ${rs_name}..."
    
    docker exec -it mongo-${rs_name}1 mongosh --port $port \
        -u "${MONGO_ADMIN_USER}" \
        -p "${MONGO_ADMIN_PASS}" \
        --eval "
        // 强制重新配置复制集
        cfg = rs.conf();
        rs.reconfig(cfg, {force: true});
        "
}

# 重启容器
restart_container() {
    local container=$1
    echo "Restarting container ${container}..."
    docker restart $container
    sleep 10
    check_logs $container 50
}

# 检查磁盘空间
check_disk_space() {
    echo "Checking disk space..."
    df -h | grep -E "Filesystem|/data/mongodb"
}

# 执行完整检查
full_check() {
    check_containers
    check_disk_space
    check_network
    check_replica_status
    
    # 检查各个组件的日志
    for container in $(docker ps --format "{{.Names}}" | grep "mongo"); do
        check_logs $container
    done
}

# 根据参数执行不同的检查
case "$1" in
    "containers")
        check_containers
        ;;
    "logs")
        [ -z "$2" ] && echo "Please specify container name" && exit 1
        check_logs "$2" "${3:-100}"
        ;;
    "replica")
        check_replica_status
        ;;
    "network")
        check_network
        ;;
    "fix-replica")
        [ -z "$2" ] && echo "Please specify replica set name" && exit 1
        fix_replica_set "$2" "${3:-27018}"
        ;;
    "restart")
        [ -z "$2" ] && echo "Please specify container name" && exit 1
        restart_container "$2"
        ;;
    "disk")
        check_disk_space
        ;;
    *)
        echo "Running full system check..."
        full_check
        ;;
esac
```

需要我继续补充日常运维和监控告警脚本吗？这部分将包含日常运维任务的自动化和监控告警的配置。

## Prompt:
继续

## Response:
让我继续设计日常运维和监控告警脚本。

17.  日常运维自动化脚本 (daily-maintenance.sh)：

```bash
#!/bin/bash

# 加载环境变量
source ./mongo-env.sh

# 日志目录
MAINTENANCE_LOG="${MONGO_BASE}/logs/maintenance.log"

# 日志函数
log_message() {
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] $1" | tee -a "${MAINTENANCE_LOG}"
}

# 清理旧日志文件
cleanup_logs() {
    log_message "Starting log cleanup..."
    
    find "${MONGO_LOG}" -name "*.log" -mtime +30 -exec rm {} \;
    find "${MAINTENANCE_LOG}" -mtime +90 -exec rm {} \;
    
    # 压缩7天前的日志
    find "${MONGO_LOG}" -name "*.log" -mtime +7 -exec gzip {} \;
    
    log_message "Log cleanup completed"
}

# 检查索引使用情况
check_indexes() {
    log_message "Checking index usage..."
    
    docker exec -it mongos1 mongosh --port "${MONGOS_PORT}" \
        -u "${MONGO_ADMIN_USER}" \
        -p "${MONGO_ADMIN_PASS}" \
        --eval "
        db.getMongo().getDBs().databases.forEach(function(dbName) {
            if(![\"admin\", \"config\", \"local\"].includes(dbName.name)) {
                var db = db.getSiblingDB(dbName.name);
                db.getCollectionNames().forEach(function(collName) {
                    var stats = db[collName].aggregate([{\$indexStats: {}}]).toArray();
                    stats.forEach(function(stat) {
                        if(stat.accesses.ops === 0) {
                            print('WARNING: Unused index found - ' + 
                                  dbName.name + '.' + collName + ' - ' + 
                                  stat.name);
                        }
                    });
                });
            }
        });
        " >> "${MAINTENANCE_LOG}"
}

# 检查数据分布
check_data_distribution() {
    log_message "Checking data distribution..."
    
    docker exec -it mongos1 mongosh --port "${MONGOS_PORT}" \
        -u "${MONGO_ADMIN_USER}" \
        -p "${MONGO_ADMIN_PASS}" \
        --eval "
        var chunks = db.getSiblingDB('config').chunks.aggregate([
            {\$group: {_id: '\$shard', count: {\$sum: 1}}}
        ]).toArray();
        
        var maxChunks = Math.max(...chunks.map(c => c.count));
        var minChunks = Math.min(...chunks.map(c => c.count));
        
        if((maxChunks - minChunks) / maxChunks > 0.2) {
            print('WARNING: Uneven chunk distribution detected');
            printjson(chunks);
        }
        " >> "${MAINTENANCE_LOG}"
}

# 检查资源使用情况
check_resources() {
    log_message "Checking system resources..."
    
    # 检查磁盘空间
    df -h | grep -E "Filesystem|/data/mongodb" >> "${MAINTENANCE_LOG}"
    
    # 检查内存使用
    docker stats --no-stream --format "table {{.Container}}\t{{.CPUPerc}}\t{{.MemUsage}}" \
        | grep "mongo" >> "${MAINTENANCE_LOG}"
}

# 检查备份状态
check_backups() {
    log_message "Checking backup status..."
    
    local backup_dir="/backup/mongodb"
    local latest_backup=$(ls -t "${backup_dir}/dumps/" | head -n1)
    
    if [ -n "$latest_backup" ]; then
        local backup_time=$(stat -c %Y "${backup_dir}/dumps/${latest_backup}")
        local current_time=$(date +%s)
        local hours_since_backup=$(( (current_time - backup_time) / 3600 ))
        
        if [ $hours_since_backup -gt 24 ]; then
            log_message "WARNING: Last backup is over 24 hours old"
        fi
    else
        log_message "ERROR: No backups found"
    fi
}

# 主运维函数
main() {
    log_message "Starting daily maintenance tasks"
    
    cleanup_logs
    check_indexes
    check_data_distribution
    check_resources
    check_backups
    
    log_message "Daily maintenance completed"
}

main
```

18.  监控告警配置脚本 (setup-alerts.sh)：

```bash
#!/bin/bash

# 加载环境变量
source ./mongo-env.sh

# Prometheus告警规则配置
setup_prometheus_alerts() {
    cat > "${MONGO_BASE}/monitoring/prometheus/alerts.yml" <<EOF
groups:
- name: mongodb_alerts
  rules:
  - alert: HighCPUUsage
    expr: rate(process_cpu_seconds_total[5m]) * 100 > 80
    for: 5m
    labels:
      severity: warning
    annotations:
      summary: High CPU usage detected
      description: CPU usage is above 80% for 5 minutes

  - alert: HighMemoryUsage
    expr: (node_memory_MemTotal_bytes - node_memory_MemAvailable_bytes) / node_memory_MemTotal_bytes * 100 > 85
    for: 5m
    labels:
      severity: warning
    annotations:
      summary: High memory usage detected
      description: Memory usage is above 85%

  - alert: DiskSpaceRunningLow
    expr: (node_filesystem_size_bytes{mountpoint="/data/mongodb"} - node_filesystem_free_bytes{mountpoint="/data/mongodb"}) / node_filesystem_size_bytes{mountpoint="/data/mongodb"} * 100 > 80
    for: 5m
    labels:
      severity: warning
    annotations:
      summary: Disk space running low
      description: Disk usage is above 80%

  - alert: ReplicationLag
    expr: mongodb_mongod_replset_member_optime_date{state="SECONDARY"} - on(set) group_right mongodb_mongod_replset_member_optime_date{state="PRIMARY"} > 10
    for: 5m
    labels:
      severity: critical
    annotations:
      summary: Replication lag detected
      description: Secondary is more than 10 seconds behind primary

  - alert: TooManyConnections
    expr: mongodb_connections{state="current"} / mongodb_connections{state="available"} * 100 > 80
    for: 5m
    labels:
      severity: warning
    annotations:
      summary: Too many connections
      description: Connection usage is above 80%

  - alert: SlowQueries
    expr: rate(mongodb_op_latencies_latency_total{type="command"}[5m]) > 100
    for: 5m
    labels:
      severity: warning
    annotations:
      summary: Slow queries detected
      description: Operations are taking longer than 100ms on average
EOF
}

# Grafana告警通道配置
setup_grafana_alerts() {
    # Slack通知通道
    curl -X POST \
        -H "Content-Type: application/json" \
        -d '{
            "name": "mongodb_alerts",
            "type": "slack",
            "settings": {
                "url": "YOUR_SLACK_WEBHOOK_URL",
                "recipient": "#mongodb-alerts"
            }
        }' \
        http://admin:admin@localhost:3000/api/alert-notifications
        
    # Email通知通道
    curl -X POST \
        -H "Content-Type: application/json" \
        -d '{
            "name": "mongodb_email_alerts",
            "type": "email",
            "settings": {
                "addresses": "ops@yourdomain.com"
            }
        }' \
        http://admin:admin@localhost:3000/api/alert-notifications
}

# 告警阈值配置
setup_alert_thresholds() {
    cat > "${MONGO_BASE}/monitoring/alert_thresholds.conf" <<EOF
# 系统资源告警阈值
CPU_WARNING_THRESHOLD=80
MEMORY_WARNING_THRESHOLD=85
DISK_WARNING_THRESHOLD=80

# 数据库性能告警阈值
CONNECTIONS_WARNING_THRESHOLD=80
REPLICATION_LAG_WARNING_THRESHOLD=10
QUERY_LATENCY_WARNING_THRESHOLD=100

# 分片均衡告警阈值
CHUNK_IMBALANCE_THRESHOLD=20
EOF
}

# 告警脚本
setup_alert_scripts() {
    cat > "${MONGO_BASE}/monitoring/alert_handler.sh" <<EOF
#!/bin/bash

# 告警处理函数
handle_alert() {
    local alert_type=\$1
    local message=\$2
    
    # 记录告警
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] \${alert_type}: \${message}" >> /var/log/mongodb/alerts.log
    
    # 发送告警通知
    case \$alert_type in
        "critical")
            # 严重告警处理
            send_critical_alert "\${message}"
            ;;
        "warning")
            # 警告级别告警处理
            send_warning_alert "\${message}"
            ;;
    esac
}

# 告警通知函数
send_critical_alert() {
    # 实现告警发送逻辑
    curl -X POST -H "Content-Type: application/json" \
        -d "{\"text\":\"\$1\"}" \
        "\${SLACK_WEBHOOK_URL}"
}

send_warning_alert() {
    # 实现警告发送逻辑
    echo "\$1" | mail -s "MongoDB Warning Alert" \${ALERT_EMAIL}
}
EOF
    chmod +x "${MONGO_BASE}/monitoring/alert_handler.sh"
}

# 主函数
main() {
    echo "Setting up monitoring alerts..."
    
    setup_prometheus_alerts
    setup_grafana_alerts
    setup_alert_thresholds
    setup_alert_scripts
    
    echo "Alert configuration completed"
}

main
```

需要我继续补充集群扩展和性能测试脚本吗？这部分将包含如何进行集群扩展和性能测试的自动化脚本。

## Prompt:
继续

## Response:
好的，让我继续设计集群扩展和性能测试脚本。

19.  集群扩展自动化脚本 (expand-cluster.sh)：

```bash
#!/bin/bash

# 加载环境变量
source ./mongo-env.sh

# 检查输入参数
if [ "$#" -lt 1 ]; then
    echo "Usage: $0 [add-shard|add-mongos] [host_ip]"
    echo "Example: $0 add-shard 192.168.1.10"
    exit 1
fi

ACTION=$1
TARGET_HOST=${2:-"localhost"}

# 检查目标主机可用性
check_host() {
    if ! ping -c 1 $TARGET_HOST &> /dev/null; then
        echo "Error: Host $TARGET_HOST is not reachable"
        exit 1
    fi
}

# 获取新分片ID
get_next_shard_id() {
    local current_shards=$(docker exec -it mongos1 mongosh --quiet --port "${MONGOS_PORT}" \
        -u "${MONGO_ADMIN_USER}" \
        -p "${MONGO_ADMIN_PASS}" \
        --eval "sh.status()" | grep -c "shard")
    echo $((current_shards + 1))
}

# 添加新分片
add_new_shard() {
    local shard_id=$(get_next_shard_id)
    local shard_name="shard${shard_id}rs"
    
    # 创建目录
    ssh $TARGET_HOST "mkdir -p ${MONGO_DATA}/shard${shard_id}/{primary,secondary}"
    
    # 生成分片配置
    cat > "${MONGO_BASE}/shard/shardsvr${shard_id}.conf" <<EOF
storage:
  dbPath: /data/db
  wiredTiger:
    engineConfig:
      cacheSizeGB: ${SHARD_CACHE_SIZE}
      journalCompressor: zstd
    collectionConfig:
      blockCompressor: zstd

systemLog:
  destination: file
  path: /var/log/mongodb/shardsvr.log
  logAppend: true

net:
  bindIp: 0.0.0.0
  port: ${SHARD_PORT}
  maxIncomingConnections: 20000

security:
  keyFile: /mongodb/keyfile/mongodb.key
  authorization: enabled

replication:
  replSetName: ${shard_name}

sharding:
  clusterRole: shardsvr
EOF

    # 复制配置到目标主机
    scp "${MONGO_BASE}/shard/shardsvr${shard_id}.conf" \
        $TARGET_HOST:"${MONGO_BASE}/shard/shardsvr.conf"
    
    # 启动新分片的容器
    ssh $TARGET_HOST "docker run -d \
        --name mongo-shard${shard_id} \
        --hostname mongo-shard${shard_id} \
        --network ${MONGO_NETWORK} \
        --memory=${MONGO_MEMORY_LIMIT} \
        --memory-swap=${MONGO_SWAP_LIMIT} \
        --restart always \
        -v ${MONGO_DATA}/shard${shard_id}/primary:/data/db \
        -v ${MONGO_BASE}/shard/shardsvr.conf:/etc/mongod.conf:ro \
        -v ${MONGO_BASE}/keyfile:/mongodb/keyfile:ro \
        -v ${MONGO_LOG}/shard:/var/log/mongodb \
        mongo:${MONGO_VERSION} mongod --config /etc/mongod.conf"
    
    # 启动副本节点
    ssh $TARGET_HOST "docker run -d \
        --name mongo-shard${shard_id}-sec \
        --hostname mongo-shard${shard_id}-sec \
        --network ${MONGO_NETWORK} \
        --memory=${MONGO_MEMORY_LIMIT} \
        --memory-swap=${MONGO_SWAP_LIMIT} \
        --restart always \
        -v ${MONGO_DATA}/shard${shard_id}/secondary:/data/db \
        -v ${MONGO_BASE}/shard/shardsvr.conf:/etc/mongod.conf:ro \
        -v ${MONGO_BASE}/keyfile:/mongodb/keyfile:ro \
        -v ${MONGO_LOG}/shard:/var/log/mongodb \
        mongo:${MONGO_VERSION} mongod --config /etc/mongod.conf"
    
    # 初始化复制集
    sleep 30
    ssh $TARGET_HOST "docker exec -it mongo-shard${shard_id} mongosh --port ${SHARD_PORT} --eval '
    rs.initiate({
        _id: \"${shard_name}\",
        members: [
            {_id: 0, host: \"mongo-shard${shard_id}:${SHARD_PORT}\", priority: 2},
            {_id: 1, host: \"mongo-shard${shard_id}-sec:${SHARD_PORT}\", priority: 1}
        ]
    })'"
    
    # 添加分片到集群
    sleep 30
    docker exec -it mongos1 mongosh --port "${MONGOS_PORT}" \
        -u "${MONGO_ADMIN_USER}" \
        -p "${MONGO_ADMIN_PASS}" \
        --eval "sh.addShard(\"${shard_name}/mongo-shard${shard_id}:${SHARD_PORT},mongo-shard${shard_id}-sec:${SHARD_PORT}\")"
}

# 添加新的mongos路由器
add_new_mongos() {
    local mongos_count=$(docker ps | grep -c "mongos")
    local new_mongos_id=$((mongos_count + 1))
    local new_port=$((MONGOS_PORT + new_mongos_id - 1))
    
    # 生成mongos配置
    cat > "${MONGO_BASE}/mongos/mongos${new_mongos_id}.conf" <<EOF
systemLog:
  destination: file
  path: /var/log/mongodb/mongos.log
  logAppend: true

net:
  bindIp: 0.0.0.0
  port: ${MONGOS_PORT}
  maxIncomingConnections: 20000

security:
  keyFile: /mongodb/keyfile/mongodb.key

sharding:
  configDB: configrs/mongo-config1:${CONFIG_PORT},mongo-config2:${CONFIG_PORT},mongo-config3:${CONFIG_PORT}
EOF

    # 复制配置到目标主机
    scp "${MONGO_BASE}/mongos/mongos${new_mongos_id}.conf" \
        $TARGET_HOST:"${MONGO_BASE}/mongos/mongos.conf"
    
    # 启动新的mongos
    ssh $TARGET_HOST "docker run -d \
        --name mongos${new_mongos_id} \
        --hostname mongos${new_mongos_id} \
        --network ${MONGO_NETWORK} \
        --memory=${MONGO_MEMORY_LIMIT} \
        --memory-swap=${MONGO_SWAP_LIMIT} \
        --restart always \
        -p ${new_port}:${MONGOS_PORT} \
        -v ${MONGO_BASE}/mongos/mongos.conf:/etc/mongos.conf:ro \
        -v ${MONGO_BASE}/keyfile:/mongodb/keyfile:ro \
        -v ${MONGO_LOG}/mongos:/var/log/mongodb \
        mongo:${MONGO_VERSION} mongos --config /etc/mongos.conf"
}

# 主函数
main() {
    check_host
    
    case $ACTION in
        "add-shard")
            add_new_shard
            ;;
        "add-mongos")
            add_new_mongos
            ;;
        *)
            echo "Invalid action: $ACTION"
            exit 1
            ;;
    esac
}

main
```

让我继续编写性能测试脚本...需要我继续吗？

## Prompt:
继续

## Response:
好的，让我继续设计性能测试脚本。

20.  性能测试脚本 (performance-test.sh)：

```bash
#!/bin/bash

# 加载环境变量
source ./mongo-env.sh

# 测试配置
TEST_DB="perftest"
TEST_COLLECTION="testdata"
DOCUMENT_SIZE=5120  # 5KB
BATCH_SIZE=200
TEST_DURATION=300  # 5分钟

# 创建测试数据生成器
create_test_data() {
    cat > "${MONGO_BASE}/test/generate_data.js" <<EOF
function generateDocument() {
    const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    let doc = {
        timestamp: new Date(),
        counter: NumberInt(Math.floor(Math.random() * 1000000)),
        data: ''
    };
    
    // 填充数据至指定大小
    while (Object.bsonsize(doc) < ${DOCUMENT_SIZE}) {
        doc.data += chars.charAt(Math.floor(Math.random() * chars.length));
    }
    
    return doc;
}

function generateBatch(size) {
    let batch = [];
    for(let i = 0; i < size; i++) {
        batch.push(generateDocument());
    }
    return batch;
}
EOF
}

# 写入性能测试
test_write_performance() {
    local concurrency=$1
    echo "Starting write performance test with $concurrency concurrent operations..."
    
    docker exec -it mongos1 mongosh --port "${MONGOS_PORT}" \
        -u "${MONGO_ADMIN_USER}" \
        -p "${MONGO_ADMIN_PASS}" \
        --eval "
        load('${MONGO_BASE}/test/generate_data.js')
        
        const startTime = new Date();
        const endTime = new Date(startTime.getTime() + ${TEST_DURATION}*1000);
        let totalOps = 0;
        let batchCount = 0;
        
        function writeWorker() {
            while(new Date() < endTime) {
                try {
                    const batch = generateBatch(${BATCH_SIZE});
                    db.${TEST_COLLECTION}.insertMany(batch, {ordered: false});
                    totalOps += batch.length;
                    batchCount++;
                } catch(e) {
                    print('Error: ' + e);
                }
            }
        }
        
        // 启动并发写入
        let workers = [];
        for(let i = 0; i < ${concurrency}; i++) {
            workers.push(writeWorker());
        }
        
        // 等待所有工作完成
        workers.forEach(w => w);
        
        const duration = (new Date() - startTime) / 1000;
        const opsPerSecond = totalOps / duration;
        
        print(\`Write Test Results:
        Total Operations: \${totalOps}
        Duration: \${duration} seconds
        Operations per second: \${opsPerSecond.toFixed(2)}
        Batch Success Rate: \${((batchCount * ${BATCH_SIZE}) / totalOps * 100).toFixed(2)}%\`);
        "
}

# 读取性能测试
test_read_performance() {
    local concurrency=$1
    echo "Starting read performance test with $concurrency concurrent operations..."
    
    docker exec -it mongos1 mongosh --port "${MONGOS_PORT}" \
        -u "${MONGO_ADMIN_USER}" \
        -p "${MONGO_ADMIN_PASS}" \
        --eval "
        const startTime = new Date();
        const endTime = new Date(startTime.getTime() + ${TEST_DURATION}*1000);
        let totalOps = 0;
        
        function readWorker() {
            while(new Date() < endTime) {
                try {
                    // 随机查询测试
                    const randomValue = Math.floor(Math.random() * 1000000);
                    db.${TEST_COLLECTION}.find({
                        counter: {\$gte: randomValue}
                    }).limit(${BATCH_SIZE}).toArray();
                    
                    totalOps += ${BATCH_SIZE};
                } catch(e) {
                    print('Error: ' + e);
                }
            }
        }
        
        // 启动并发读取
        let workers = [];
        for(let i = 0; i < ${concurrency}; i++) {
            workers.push(readWorker());
        }
        
        // 等待所有工作完成
        workers.forEach(w => w);
        
        const duration = (new Date() - startTime) / 1000;
        const opsPerSecond = totalOps / duration;
        
        print(\`Read Test Results:
        Total Operations: \${totalOps}
        Duration: \${duration} seconds
        Operations per second: \${opsPerSecond.toFixed(2)}\`);
        "
}

# 混合负载测试
test_mixed_workload() {
    local write_concurrency=$1
    local read_concurrency=$2
    
    echo "Starting mixed workload test..."
    
    # 启动写入测试
    test_write_performance $write_concurrency &
    
    # 启动读取测试
    test_read_performance $read_concurrency &
    
    # 等待所有测试完成
    wait
}

# 监控资源使用
monitor_resources() {
    echo "Starting resource monitoring..."
    
    while true; do
        date
        echo "Docker Stats:"
        docker stats --no-stream | grep "mongo"
        
        echo "System Resources:"
        top -b -n 1 | head -n 20
        
        sleep 10
    done
}

# 主测试函数
main() {
    local action=$1
    local concurrency=${2:-4}
    local read_concurrency=${3:-$concurrency}
    
    create_test_data
    
    case $action in
        "write")
            test_write_performance $concurrency
            ;;
        "read")
            test_read_performance $concurrency
            ;;
        "mixed")
            test_mixed_workload $concurrency $read_concurrency
            ;;
        *)
            echo "Usage: $0 [write|read|mixed] [write_concurrency] [read_concurrency]"
            exit 1
            ;;
    esac
}

# 启动监控（在后台运行）
monitor_resources &
MONITOR_PID=$!

# 执行测试
main "$@"

# 停止监控
kill $MONITOR_PID
```

需要我继续补充测试结果分析和报告生成脚本吗？这部分将包含如何收集和分析测试数据，并生成可视化报告。

## Prompt:
继续

## Response:
好的，让我继续设计测试结果分析和报告生成脚本。

21.  性能测试结果分析脚本 (analyze-performance.sh)：

```bash
#!/bin/bash

# 加载环境变量
source ./mongo-env.sh

# 结果存储目录
RESULTS_DIR="${MONGO_BASE}/test/results"
REPORT_DIR="${MONGO_BASE}/test/reports"

# 创建必要的目录
mkdir -p "${RESULTS_DIR}" "${REPORT_DIR}"

# 收集性能数据
collect_performance_data() {
    local test_id=$(date +%Y%m%d_%H%M%S)
    local result_file="${RESULTS_DIR}/perf_${test_id}.json"
    
    echo "Collecting performance metrics..."
    
    # 获取服务器状态
    docker exec -it mongos1 mongosh --port "${MONGOS_PORT}" \
        -u "${MONGO_ADMIN_USER}" \
        -p "${MONGO_ADMIN_PASS}" \
        --eval "
        const metrics = {
            serverStatus: db.serverStatus(),
            operationStats: db.currentOp(),
            shardStats: sh.status(),
            collectionStats: db.${TEST_COLLECTION}.stats()
        };
        printjson(metrics);
        " > "${result_file}"
        
    echo "${result_file}"
}

# 分析性能数据
analyze_performance_data() {
    local result_file=$1
    local analysis_file="${RESULTS_DIR}/analysis_$(basename ${result_file%.*}).json"
    
    echo "Analyzing performance data..."
    
    # 使用jq处理JSON数据
    jq '{
        connections: .serverStatus.connections,
        opcounters: .serverStatus.opcounters,
        wiredTiger: {
            cache: .serverStatus.wiredTiger.cache,
            concurrentTransactions: .serverStatus.wiredTiger.concurrentTransactions
        },
        networkStats: .serverStatus.network,
        shardDistribution: .shardStats.shards,
        collectionSize: .collectionStats.size,
        avgObjSize: .collectionStats.avgObjSize
    }' "${result_file}" > "${analysis_file}"
    
    echo "${analysis_file}"
}

# 生成HTML报告
generate_html_report() {
    local analysis_file=$1
    local report_file="${REPORT_DIR}/report_$(basename ${analysis_file%.*}).html"
    
    cat > "${report_file}" <<EOF
<!DOCTYPE html>
<html>
<head>
    <title>MongoDB Performance Report</title>
    <script src="https://cdn.plot.ly/plotly-latest.min.js"></script>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .chart { width: 100%; height: 400px; margin-bottom: 20px; }
        .metric { margin-bottom: 10px; padding: 10px; background-color: #f5f5f5; }
    </style>
</head>
<body>
    <h1>MongoDB Performance Report</h1>
    <div id="operationsChart" class="chart"></div>
    <div id="connectionChart" class="chart"></div>
    <div id="cacheChart" class="chart"></div>
    <div id="shardDistribution" class="chart"></div>
    
    <script>
        const performanceData = $(cat ${analysis_file});
        
        // 操作计数图表
        const opData = {
            labels: Object.keys(performanceData.opcounters),
            values: Object.values(performanceData.opcounters),
            type: 'bar'
        };
        Plotly.newPlot('operationsChart', [opData], {
            title: 'Operation Counts'
        });
        
        // 连接数图表
        const connData = {
            values: [
                performanceData.connections.current,
                performanceData.connections.available
            ],
            labels: ['Current', 'Available'],
            type: 'pie'
        };
        Plotly.newPlot('connectionChart', [connData], {
            title: 'Connection Distribution'
        });
        
        // 缓存使用图表
        const cacheData = {
            values: [
                performanceData.wiredTiger.cache.bytes_currently_in_cache,
                performanceData.wiredTiger.cache.maximum_bytes_configured
            ],
            labels: ['Used', 'Available'],
            type: 'pie'
        };
        Plotly.newPlot('cacheChart', [cacheData], {
            title: 'Cache Usage'
        });
        
        // 分片分布图表
        const shardData = {
            x: Object.keys(performanceData.shardDistribution),
            y: Object.values(performanceData.shardDistribution).map(s => s.dataSizeBytes),
            type: 'bar'
        };
        Plotly.newPlot('shardDistribution', [shardData], {
            title: 'Data Distribution Across Shards'
        });
    </script>
</body>
</html>
EOF

    echo "${report_file}"
}

# 生成性能汇总报告
generate_summary_report() {
    local test_id=$(date +%Y%m%d)
    local summary_file="${REPORT_DIR}/summary_${test_id}.md"
    
    echo "Generating summary report..."
    
    cat > "${summary_file}" <<EOF
# MongoDB Cluster Performance Summary
Date: $(date)

## System Configuration
- Shards: $(docker ps | grep -c "mongo-shard")
- Memory per Shard: ${MONGO_MEMORY_LIMIT}
- Total Collections: $(docker exec -it mongos1 mongosh --quiet --port "${MONGOS_PORT}" -u "${MONGO_ADMIN_USER}" -p "${MONGO_ADMIN_PASS}" --eval "db.getCollectionNames().length")

## Performance Metrics
$(for file in ${RESULTS_DIR}/analysis_*.json; do
    echo "### $(basename ${file%.*})"
    jq -r '. | "- Operations per second: \(.opcounters.query + .opcounters.insert + .opcounters.update + .opcounters.delete)\n- Average object size: \(.avgObjSize) bytes\n- Total data size: \(.collectionSize) bytes"' "$file"
    echo
done)

## Recommendations
$(analyze_recommendations)
EOF

    echo "${summary_file}"
}

# 分析并生成建议
analyze_recommendations() {
    local latest_analysis=$(ls -t ${RESULTS_DIR}/analysis_*.json | head -n1)
    
    jq -r '
    def recommend:
        if .connections.current / .connections.available > 0.8 then
            "- Consider increasing connection limits\n"
        else "" end +
        if .wiredTiger.cache.bytes_currently_in_cache / .wiredTiger.cache.maximum_bytes_configured > 0.9 then
            "- Consider increasing WiredTiger cache size\n"
        else "" end +
        if .opcounters.query / (.opcounters.insert + .opcounters.update + .opcounters.delete) > 10 then
            "- Consider adding more read replicas\n"
        else "" end;
    recommend
    ' "${latest_analysis}"
}

# 主函数
main() {
    local result_file=$(collect_performance_data)
    local analysis_file=$(analyze_performance_data "${result_file}")
    local report_file=$(generate_html_report "${analysis_file}")
    generate_summary_report
    
    echo "Analysis complete. Reports generated:"
    echo "HTML Report: ${report_file}"
    echo "Summary Report: ${REPORT_DIR}/summary_$(date +%Y%m%d).md"
}

main "$@"
```

需要我继续补充测试数据清理和资源释放脚本吗？这部分将包含如何清理测试数据和释放系统资源的自动化脚本。

## Prompt:
继续

## Response:
好的，让我继续设计测试数据清理和资源释放脚本。

22.  测试数据清理和资源释放脚本 (cleanup.sh)：

```bash
#!/bin/bash

# 加载环境变量
source ./mongo-env.sh

# 清理标志
CLEAN_DATA=false
CLEAN_LOGS=false
CLEAN_REPORTS=false
CLEAN_ALL=false

# 帮助信息
show_help() {
    echo "Usage: $0 [options]"
    echo "Options:"
    echo "  --data      Clean test data"
    echo "  --logs      Clean logs"
    echo "  --reports   Clean test reports"
    echo "  --all       Clean everything"
    echo "  --help      Show this help message"
}

# 解析命令行参数
parse_args() {
    while [[ $# -gt 0 ]]; do
        case $1 in
            --data)
                CLEAN_DATA=true
                shift
                ;;
            --logs)
                CLEAN_LOGS=true
                shift
                ;;
            --reports)
                CLEAN_REPORTS=true
                shift
                ;;
            --all)
                CLEAN_ALL=true
                shift
                ;;
            --help)
                show_help
                exit 0
                ;;
            *)
                echo "Unknown option: $1"
                show_help
                exit 1
                ;;
        esac
    done
}

# 清理测试数据
cleanup_test_data() {
    echo "Cleaning up test data..."
    
    docker exec -it mongos1 mongosh --port "${MONGOS_PORT}" \
        -u "${MONGO_ADMIN_USER}" \
        -p "${MONGO_ADMIN_PASS}" \
        --eval "
        // 清理测试数据库
        db.getSiblingDB('${TEST_DB}').dropDatabase();
        
        // 清理测试集合
        db.getSiblingDB('${TEST_DB}').getCollectionNames().forEach(function(collName) {
            db.getSiblingDB('${TEST_DB}')[collName].drop();
        });
        
        // 压缩存储空间
        db.getSiblingDB('${TEST_DB}').runCommand({ compact: '${TEST_COLLECTION}' });
        
        // 清理孤立文档
        sh.cleanupOrphaned();
        "
    
    echo "Test data cleanup completed"
}

# 清理日志文件
cleanup_logs() {
    echo "Cleaning up log files..."
    
    # 清理MongoDB日志
    find "${MONGO_LOG}" -name "*.log" -mtime +7 -delete
    find "${MONGO_LOG}" -name "*.log.gz" -mtime +30 -delete
    
    # 清理测试日志
    find "${MONGO_BASE}/test" -name "*.log" -delete
    
    echo "Log cleanup completed"
}

# 清理测试报告
cleanup_reports() {
    echo "Cleaning up test reports..."
    
    # 保留最近7天的报告
    find "${REPORT_DIR}" -type f -mtime +7 -delete
    find "${RESULTS_DIR}" -type f -mtime +7 -delete
    
    echo "Report cleanup completed"
}

# 释放系统资源
cleanup_resources() {
    echo "Cleaning up system resources..."
    
    # 刷新系统缓存
    sync
    echo 3 > /proc/sys/vm/drop_caches
    
    # 重置MongoDB缓存
    docker exec -it mongos1 mongosh --port "${MONGOS_PORT}" \
        -u "${MONGO_ADMIN_USER}" \
        -p "${MONGO_ADMIN_PASS}" \
        --eval "
        // 清理查询计划缓存
        db.getSiblingDB('${TEST_DB}').getCollectionNames().forEach(function(collName) {
            db.getSiblingDB('${TEST_DB}')[collName].getPlanCache().clear();
        });
        "
    
    echo "Resource cleanup completed"
}

# 检查磁盘空间
check_disk_space() {
    echo "Checking disk space usage..."
    
    df -h "${MONGO_DATA}" "${MONGO_LOG}" "${REPORT_DIR}"
    
    # 如果使用率超过80%，发出警告
    local usage=$(df "${MONGO_DATA}" | tail -1 | awk '{print $5}' | sed 's/%//')
    if [ "$usage" -gt 80 ]; then
        echo "WARNING: Disk usage is above 80% (${usage}%)"
    fi
}

# 压缩旧数据
compress_old_data() {
    echo "Compressing old data..."
    
    # 压缩旧日志
    find "${MONGO_LOG}" -name "*.log" -mtime +1 -exec gzip {} \;
    
    # 压缩旧报告
    find "${REPORT_DIR}" -type f -mtime +30 -exec gzip {} \;
    
    echo "Data compression completed"
}

# 验证清理结果
verify_cleanup() {
    echo "Verifying cleanup results..."
    
    # 检查测试数据库是否存在
    local db_exists=$(docker exec -it mongos1 mongosh --quiet --port "${MONGOS_PORT}" \
        -u "${MONGO_ADMIN_USER}" \
        -p "${MONGO_ADMIN_PASS}" \
        --eval "db.getSiblingDB('${TEST_DB}').stats().ok" || echo "0")
    
    if [ "$db_exists" == "1" ]; then
        echo "WARNING: Test database still exists"
    fi
    
    # 检查磁盘空间变化
    check_disk_space
}

# 清理总结报告
generate_cleanup_report() {
    local report_file="${REPORT_DIR}/cleanup_report_$(date +%Y%m%d_%H%M%S).txt"
    
    {
        echo "Cleanup Report"
        echo "=============="
        echo "Date: $(date)"
        echo
        echo "Actions performed:"
        [ "$CLEAN_DATA" = true ] && echo "- Test data cleaned"
        [ "$CLEAN_LOGS" = true ] && echo "- Logs cleaned"
        [ "$CLEAN_REPORTS" = true ] && echo "- Reports cleaned"
        echo
        echo "Disk space usage:"
        df -h "${MONGO_DATA}" "${MONGO_LOG}" "${REPORT_DIR}"
    } > "${report_file}"
    
    echo "Cleanup report generated: ${report_file}"
}

# 主函数
main() {
    parse_args "$@"
    
    if [ "$CLEAN_ALL" = true ]; then
        CLEAN_DATA=true
        CLEAN_LOGS=true
        CLEAN_REPORTS=true
    fi
    
    [ "$CLEAN_DATA" = true ] && cleanup_test_data
    [ "$CLEAN_LOGS" = true ] && cleanup_logs
    [ "$CLEAN_REPORTS" = true ] && cleanup_reports
    
    cleanup_resources
    compress_old_data
    verify_cleanup
    generate_cleanup_report
}

main "$@"
```

这个脚本提供了全面的清理功能：

1.  清理测试数据
2.  清理日志文件
3.  清理测试报告
4.  释放系统资源
5.  压缩旧数据
6.  验证清理结果
7.  生成清理报告

需要我继续补充其他辅助脚本或工具吗？例如自动化部署脚本或监控告警脚本？

## Prompt:
继续

## Response:
好的，让我继续设计自动化部署和系统恢复脚本。

23.  自动化部署脚本 (deploy-cluster.sh)：

```bash
#!/bin/bash

# 加载环境变量
source ./mongo-env.sh

# 部署状态文件
DEPLOY_STATUS="${MONGO_BASE}/deploy_status.json"

# 部署阶段跟踪
track_deployment() {
    local stage=$1
    local status=$2
    local message=$3
    
    # 更新部署状态
    cat > "${DEPLOY_STATUS}" <<EOF
{
    "stage": "${stage}",
    "status": "${status}",
    "message": "${message}",
    "timestamp": "$(date -u +"%Y-%m-%dT%H:%M:%SZ")"
}
EOF
}

# 检查前置条件
check_prerequisites() {
    track_deployment "prerequisites" "in_progress" "Checking prerequisites"
    
    # 检查Docker
    if ! command -v docker &> /dev/null; then
        track_deployment "prerequisites" "failed" "Docker not found"
        exit 1
    fi

    # 检查所需端口
    for port in $MONGOS_PORT $CONFIG_PORT $SHARD_PORT; do
        if netstat -tuln | grep ":${port}" &> /dev/null; then
            track_deployment "prerequisites" "failed" "Port ${port} already in use"
            exit 1
        fi
    done

    # 检查磁盘空间
    local available_space=$(df "${MONGO_DATA}" | awk 'NR==2 {print $4}')
    if [ "${available_space}" -lt 10485760 ]; then  # 10GB in KB
        track_deployment "prerequisites" "failed" "Insufficient disk space"
        exit 1
    fi

    track_deployment "prerequisites" "completed" "Prerequisites check passed"
}

# 准备环境
prepare_environment() {
    track_deployment "environment" "in_progress" "Preparing environment"
    
    # 创建目录结构
    for dir in config shard mongos keyfile; do
        mkdir -p "${MONGO_BASE}/${dir}"
    done
    
    for i in {1..3}; do
        mkdir -p "${MONGO_DATA}/config${i}"
        mkdir -p "${MONGO_DATA}/shard${i}"
        mkdir -p "${MONGO_LOG}/{config,shard,mongos}"
    done

    # 设置权限
    chown -R 999:999 "${MONGO_BASE}" "${MONGO_DATA}" "${MONGO_LOG}"
    chmod -R 755 "${MONGO_BASE}" "${MONGO_DATA}" "${MONGO_LOG}"

    # 生成密钥文件
    openssl rand -base64 756 > "${MONGO_BASE}/keyfile/mongodb.key"
    chmod 400 "${MONGO_BASE}/keyfile/mongodb.key"
    chown 999:999 "${MONGO_BASE}/keyfile/mongodb.key"

    track_deployment "environment" "completed" "Environment prepared"
}

# 部署配置服务器
deploy_config_servers() {
    track_deployment "config_servers" "in_progress" "Deploying config servers"
    
    for i in {1..3}; do
        docker run -d \
            --name "mongo-config${i}" \
            --hostname "mongo-config${i}" \
            --network "${MONGO_NETWORK}" \
            --memory="${MONGO_MEMORY_LIMIT}" \
            --memory-swap="${MONGO_SWAP_LIMIT}" \
            --restart always \
            -v "${MONGO_DATA}/config${i}:/data/db" \
            -v "${MONGO_BASE}/config/configsvr.conf:/etc/mongod.conf:ro" \
            -v "${MONGO_BASE}/keyfile:/mongodb/keyfile:ro" \
            -v "${MONGO_LOG}/config:/var/log/mongodb" \
            "mongo:${MONGO_VERSION}" mongod --config /etc/mongod.conf
            
        if [ $? -ne 0 ]; then
            track_deployment "config_servers" "failed" "Failed to deploy config server ${i}"
            exit 1
        fi
    done

    track_deployment "config_servers" "completed" "Config servers deployed"
}

# 初始化复制集
init_replica_sets() {
    track_deployment "replica_init" "in_progress" "Initializing replica sets"
    
    # 初始化配置服务器复制集
    sleep 30  # 等待服务器启动
    docker exec -it mongo-config1 mongosh --port "${CONFIG_PORT}" --eval "
    rs.initiate({
        _id: 'configrs',
        configsvr: true,
        members: [
            {_id: 0, host: 'mongo-config1:${CONFIG_PORT}', priority: 2},
            {_id: 1, host: 'mongo-config2:${CONFIG_PORT}', priority: 1},
            {_id: 2, host: 'mongo-config3:${CONFIG_PORT}', priority: 1}
        ]
    })"
    
    if [ $? -ne 0 ]; then
        track_deployment "replica_init" "failed" "Failed to initialize config replica set"
        exit 1
    fi

    track_deployment "replica_init" "completed" "Replica sets initialized"
}

# 部署分片服务器
deploy_shard_servers() {
    track_deployment "shard_servers" "in_progress" "Deploying shard servers"
    
    for i in {1..3}; do
        docker run -d \
            --name "mongo-shard${i}" \
            --hostname "mongo-shard${i}" \
            --network "${MONGO_NETWORK}" \
            --memory="${MONGO_MEMORY_LIMIT}" \
            --memory-swap="${MONGO_SWAP_LIMIT}" \
            --restart always \
            -v "${MONGO_DATA}/shard${i}:/data/db" \
            -v "${MONGO_BASE}/shard/shardsvr.conf:/etc/mongod.conf:ro" \
            -v "${MONGO_BASE}/keyfile:/mongodb/keyfile:ro" \
            -v "${MONGO_LOG}/shard:/var/log/mongodb" \
            "mongo:${MONGO_VERSION}" mongod --config /etc/mongod.conf
            
        if [ $? -ne 0 ]; then
            track_deployment "shard_servers" "failed" "Failed to deploy shard server ${i}"
            exit 1
        fi
    done

    track_deployment "shard_servers" "completed" "Shard servers deployed"
}

需要我继续补充脚本的剩余部分吗？这个脚本还包括部署mongos路由器、配置分片和初始化集群等功能。
```

## Prompt:
继续

## Response:
让我继续完成自动化部署脚本的剩余部分。

```bash
# 部署mongos路由器
deploy_mongos_routers() {
    track_deployment "mongos_routers" "in_progress" "Deploying mongos routers"
    
    docker run -d \
        --name "mongos1" \
        --hostname "mongos1" \
        --network "${MONGO_NETWORK}" \
        --memory="${MONGO_MEMORY_LIMIT}" \
        --memory-swap="${MONGO_SWAP_LIMIT}" \
        --restart always \
        -p "${MONGOS_PORT}:${MONGOS_PORT}" \
        -v "${MONGO_BASE}/mongos/mongos.conf:/etc/mongos.conf:ro" \
        -v "${MONGO_BASE}/keyfile:/mongodb/keyfile:ro" \
        -v "${MONGO_LOG}/mongos:/var/log/mongodb" \
        "mongo:${MONGO_VERSION}" mongos --config /etc/mongos.conf
        
    if [ $? -ne 0 ]; then
        track_deployment "mongos_routers" "failed" "Failed to deploy mongos router"
        exit 1
    fi

    track_deployment "mongos_routers" "completed" "Mongos routers deployed"
}

# 初始化分片集群
init_sharding() {
    track_deployment "sharding_init" "in_progress" "Initializing sharding"
    
    sleep 30  # 等待mongos启动
    
    # 添加分片
    docker exec -it mongos1 mongosh --port "${MONGOS_PORT}" --eval "
    // 添加分片
    sh.addShard('shard1rs/mongo-shard1:${SHARD_PORT}');
    sh.addShard('shard2rs/mongo-shard2:${SHARD_PORT}');
    sh.addShard('shard3rs/mongo-shard3:${SHARD_PORT}');
    
    // 配置分片参数
    db.getSiblingDB('config').settings.updateOne(
        { _id: 'balancer' },
        { \$set: { 
            activeWindow: null,
            _secondaryThrottle: false,
            maxParallelCopies: 8
        }},
        { upsert: true }
    );
    
    // 设置默认chunk大小
    db.getSiblingDB('config').settings.updateOne(
        { _id: 'chunksize' },
        { \$set: { value: 32 }},
        { upsert: true }
    );"
    
    if [ $? -ne 0 ]; then
        track_deployment "sharding_init" "failed" "Failed to initialize sharding"
        exit 1
    fi

    track_deployment "sharding_init" "completed" "Sharding initialized"
}

# 创建管理用户
create_admin_user() {
    track_deployment "admin_user" "in_progress" "Creating admin user"
    
    docker exec -it mongos1 mongosh --port "${MONGOS_PORT}" --eval "
    use admin
    db.createUser({
        user: '${MONGO_ADMIN_USER}',
        pwd: '${MONGO_ADMIN_PASS}',
        roles: [ 
            { role: 'root', db: 'admin' },
            { role: 'clusterAdmin', db: 'admin' }
        ]
    });"
    
    if [ $? -ne 0 ]; then
        track_deployment "admin_user" "failed" "Failed to create admin user"
        exit 1
    fi

    track_deployment "admin_user" "completed" "Admin user created"
}

# 验证部署
verify_deployment() {
    track_deployment "verification" "in_progress" "Verifying deployment"
    
    # 检查所有容器状态
    local containers="mongo-config1 mongo-config2 mongo-config3 mongo-shard1 mongo-shard2 mongo-shard3 mongos1"
    for container in $containers; do
        if [ "$(docker inspect -f '{{.State.Running}}' $container)" != "true" ]; then
            track_deployment "verification" "failed" "Container $container is not running"
            exit 1
        fi
    done
    
    # 测试集群连接
    docker exec -it mongos1 mongosh --port "${MONGOS_PORT}" \
        -u "${MONGO_ADMIN_USER}" \
        -p "${MONGO_ADMIN_PASS}" \
        --eval "
        // 测试分片状态
        let status = sh.status();
        if(!status.ok) { quit(1); }
        
        // 测试写入
        let testDb = db.getSiblingDB('test');
        testDb.test.insertOne({ test: 'deployment' });
        let doc = testDb.test.findOne({ test: 'deployment' });
        if(!doc) { quit(1); }
        testDb.test.deleteOne({ test: 'deployment' });"
        
    if [ $? -ne 0 ]; then
        track_deployment "verification" "failed" "Deployment verification failed"
        exit 1
    fi

    track_deployment "verification" "completed" "Deployment verified successfully"
}

# 生成部署报告
generate_deployment_report() {
    local report_file="${MONGO_BASE}/deployment_report_$(date +%Y%m%d_%H%M%S).txt"
    
    {
        echo "MongoDB Cluster Deployment Report"
        echo "================================"
        echo "Date: $(date)"
        echo
        echo "Deployment Status:"
        cat "${DEPLOY_STATUS}"
        echo
        echo "Container Status:"
        docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}" | grep "mongo"
        echo
        echo "Resource Usage:"
        docker stats --no-stream --format "table {{.Container}}\t{{.CPUPerc}}\t{{.MemUsage}}" | grep "mongo"
        echo
        echo "Cluster Status:"
        docker exec -it mongos1 mongosh --port "${MONGOS_PORT}" \
            -u "${MONGO_ADMIN_USER}" \
            -p "${MONGO_ADMIN_PASS}" \
            --eval "sh.status()"
    } > "${report_file}"
    
    echo "Deployment report generated: ${report_file}"
}

# 主函数
main() {
    echo "Starting MongoDB cluster deployment..."
    
    check_prerequisites
    prepare_environment
    deploy_config_servers
    init_replica_sets
    deploy_shard_servers
    deploy_mongos_routers
    init_sharding
    create_admin_user
    verify_deployment
    generate_deployment_report
    
    echo "MongoDB cluster deployment completed successfully"
}

# 错误处理
trap 'echo "Deployment failed. Check deployment status for details."' ERR

# 执行部署
main "$@"
```

这个脚本完整实现了MongoDB分片集群的自动化部署流程，包括：

1.  环境检查和准备
2.  配置服务器部署
3.  分片服务器部署
4.  路由器部署
5.  复制集初始化
6.  分片配置
7.  用户创建
8.  部署验证
9.  报告生成

需要我继续补充系统恢复和维护脚本吗？

## Prompt:
继续

## Response:
好的，让我继续设计系统恢复和维护脚本。

24.  系统恢复脚本 (recover-cluster.sh)：

```bash
#!/bin/bash

# 加载环境变量
source ./mongo-env.sh

# 恢复状态文件
RECOVERY_STATUS="${MONGO_BASE}/recovery_status.json"

# 恢复阶段跟踪
track_recovery() {
    local stage=$1
    local status=$2
    local message=$3
    
    cat > "${RECOVERY_STATUS}" <<EOF
{
    "stage": "${stage}",
    "status": "${status}",
    "message": "${message}",
    "timestamp": "$(date -u +"%Y-%m-%dT%H:%M:%SZ")"
}
EOF
}

# 检查集群状态
check_cluster_health() {
    track_recovery "health_check" "in_progress" "Checking cluster health"
    
    # 检查所有容器状态
    local unhealthy_containers=$(docker ps -a --format "{{.Names}}" | grep "mongo" | \
        while read container; do
            if [ "$(docker inspect -f '{{.State.Running}}' $container)" != "true" ]; then
                echo "$container"
            fi
        done)
    
    if [ ! -z "$unhealthy_containers" ]; then
        track_recovery "health_check" "warning" "Found unhealthy containers: $unhealthy_containers"
        return 1
    fi
    
    # 检查复制集状态
    docker exec -it mongo-config1 mongosh --port "${CONFIG_PORT}" \
        -u "${MONGO_ADMIN_USER}" \
        -p "${MONGO_ADMIN_PASS}" \
        --eval "rs.status()" > /tmp/rs_status.json
        
    if [ $? -ne 0 ]; then
        track_recovery "health_check" "warning" "Replica set check failed"
        return 1
    fi
    
    track_recovery "health_check" "completed" "Health check completed"
    return 0
}

# 备份当前配置
backup_current_config() {
    local backup_dir="${MONGO_BASE}/backup_$(date +%Y%m%d_%H%M%S)"
    mkdir -p "$backup_dir"
    
    # 备份配置文件
    cp -r "${MONGO_BASE}/config" "$backup_dir/"
    cp -r "${MONGO_BASE}/shard" "$backup_dir/"
    cp -r "${MONGO_BASE}/mongos" "$backup_dir/"
    
    # 导出集群配置
    docker exec -it mongos1 mongosh --port "${MONGOS_PORT}" \
        -u "${MONGO_ADMIN_USER}" \
        -p "${MONGO_ADMIN_PASS}" \
        --eval "
        db.getSiblingDB('config').chunks.find().toArray();
        db.getSiblingDB('config').collections.find().toArray();
        db.getSiblingDB('config').databases.find().toArray();
        " > "${backup_dir}/cluster_config.json"
        
    echo "$backup_dir"
}

# 恢复单个容器
recover_container() {
    local container=$1
    local config_file=$2
    
    track_recovery "container_recovery" "in_progress" "Recovering container ${container}"
    
    # 停止并移除容器
    docker stop "$container" || true
    docker rm "$container" || true
    
    # 重新创建容器
    case "$container" in
        mongo-config*)
            docker run -d \
                --name "$container" \
                --hostname "$container" \
                --network "${MONGO_NETWORK}" \
                --memory="${MONGO_MEMORY_LIMIT}" \
                --memory-swap="${MONGO_SWAP_LIMIT}" \
                --restart always \
                -v "${MONGO_DATA}/${container}:/data/db" \
                -v "$config_file:/etc/mongod.conf:ro" \
                -v "${MONGO_BASE}/keyfile:/mongodb/keyfile:ro" \
                -v "${MONGO_LOG}/config:/var/log/mongodb" \
                "mongo:${MONGO_VERSION}" mongod --config /etc/mongod.conf
            ;;
        mongo-shard*)
            docker run -d \
                --name "$container" \
                --hostname "$container" \
                --network "${MONGO_NETWORK}" \
                --memory="${MONGO_MEMORY_LIMIT}" \
                --memory-swap="${MONGO_SWAP_LIMIT}" \
                --restart always \
                -v "${MONGO_DATA}/${container}:/data/db" \
                -v "$config_file:/etc/mongod.conf:ro" \
                -v "${MONGO_BASE}/keyfile:/mongodb/keyfile:ro" \
                -v "${MONGO_LOG}/shard:/var/log/mongodb" \
                "mongo:${MONGO_VERSION}" mongod --config /etc/mongod.conf
            ;;
        mongos*)
            docker run -d \
                --name "$container" \
                --hostname "$container" \
                --network "${MONGO_NETWORK}" \
                --memory="${MONGO_MEMORY_LIMIT}" \
                --memory-swap="${MONGO_SWAP_LIMIT}" \
                --restart always \
                -p "${MONGOS_PORT}:${MONGOS_PORT}" \
                -v "$config_file:/etc/mongos.conf:ro" \
                -v "${MONGO_BASE}/keyfile:/mongodb/keyfile:ro" \
                -v "${MONGO_LOG}/mongos:/var/log/mongodb" \
                "mongo:${MONGO_VERSION}" mongos --config /etc/mongos.conf
            ;;
    esac
    
    if [ $? -ne 0 ]; then
        track_recovery "container_recovery" "failed" "Failed to recover container ${container}"
        return 1
    fi
    
    track_recovery "container_recovery" "completed" "Container ${container} recovered"
    return 0
}

# 重建复制集
rebuild_replica_sets() {
    track_recovery "replica_rebuild" "in_progress" "Rebuilding replica sets"
    
    # 重新初始化配置服务器复制集
    docker exec -it mongo-config1 mongosh --port "${CONFIG_PORT}" --eval "
    rs.initiate({
        _id: 'configrs',
        configsvr: true,
        members: [
            {_id: 0, host: 'mongo-config1:${CONFIG_PORT}', priority: 2},
            {_id: 1, host: 'mongo-config2:${CONFIG_PORT}', priority: 1},
            {_id: 2, host: 'mongo-config3:${CONFIG_PORT}', priority: 1}
        ]
    })"
    
    # 重新初始化分片复制集
    for i in {1..3}; do
        if [ -d "${MONGO_DATA}/shard${i}" ]; then
            docker exec -it "mongo-shard${i}" mongosh --port "${SHARD_PORT}" --eval "
            rs.initiate({
                _id: 'shard${i}rs',
                members: [
                    {_id: 0, host: 'mongo-shard${i}:${SHARD_PORT}', priority: 2}
                ]
            })"
        fi
    done
}

需要我继续补充脚本的剩余部分吗？这个脚本还包括数据恢复、集群重配置和验证等功能。
```

## Prompt:
继续

## Response:
好的，让我继续完成系统恢复脚本的剩余部分。

```bash
# 恢复数据
restore_data() {
    track_recovery "data_restore" "in_progress" "Restoring data"
    
    local backup_path=$1
    if [ ! -d "$backup_path" ]; then
        track_recovery "data_restore" "failed" "Backup path not found: $backup_path"
        return 1
    }
    
    # 恢复集群配置
    docker exec -it mongos1 mongosh --port "${MONGOS_PORT}" \
        -u "${MONGO_ADMIN_USER}" \
        -p "${MONGO_ADMIN_PASS}" \
        --eval "
        // 停止均衡器
        sh.stopBalancer();
        
        // 恢复配置数据
        load('${backup_path}/cluster_config.json');
        
        // 重启均衡器
        sh.startBalancer();
        "
        
    if [ $? -ne 0 ]; then
        track_recovery "data_restore" "failed" "Failed to restore cluster configuration"
        return 1
    fi
    
    track_recovery "data_restore" "completed" "Data restored successfully"
    return 0
}

# 重配置分片集群
reconfigure_sharding() {
    track_recovery "shard_reconfig" "in_progress" "Reconfiguring sharding"
    
    docker exec -it mongos1 mongosh --port "${MONGOS_PORT}" \
        -u "${MONGO_ADMIN_USER}" \
        -p "${MONGO_ADMIN_PASS}" \
        --eval "
        // 更新chunk大小
        db.getSiblingDB('config').settings.updateOne(
            { _id: 'chunksize' },
            { \$set: { value: 32 } },
            { upsert: true }
        );
        
        // 配置均衡器
        db.getSiblingDB('config').settings.updateOne(
            { _id: 'balancer' },
            { \$set: {
                activeWindow: null,
                _secondaryThrottle: false,
                maxParallelCopies: 8
            }},
            { upsert: true }
        );
        
        // 检查并修复孤立数据
        db.adminCommand({ cleanupOrphaned: '*' });
        "
    
    if [ $? -ne 0 ]; then
        track_recovery "shard_reconfig" "failed" "Failed to reconfigure sharding"
        return 1
    fi
    
    track_recovery "shard_reconfig" "completed" "Sharding reconfigured"
    return 0
}

# 验证恢复
verify_recovery() {
    track_recovery "verification" "in_progress" "Verifying recovery"
    
    # 验证复制集状态
    local replica_status=$(docker exec -it mongo-config1 mongosh --port "${CONFIG_PORT}" \
        -u "${MONGO_ADMIN_USER}" \
        -p "${MONGO_ADMIN_PASS}" \
        --eval "rs.status().ok")
    
    if [ "$replica_status" != "1" ]; then
        track_recovery "verification" "failed" "Replica set verification failed"
        return 1
    fi
    
    # 验证分片状态
    local shard_status=$(docker exec -it mongos1 mongosh --port "${MONGOS_PORT}" \
        -u "${MONGO_ADMIN_USER}" \
        -p "${MONGO_ADMIN_PASS}" \
        --eval "sh.status().ok")
    
    if [ "$shard_status" != "1" ]; then
        track_recovery "verification" "failed" "Sharding verification failed"
        return 1
    fi
    
    # 验证写入
    docker exec -it mongos1 mongosh --port "${MONGOS_PORT}" \
        -u "${MONGO_ADMIN_USER}" \
        -p "${MONGO_ADMIN_PASS}" \
        --eval "
        db.getSiblingDB('test').recovery_test.insertOne({ test: 'recovery' });
        let doc = db.getSiblingDB('test').recovery_test.findOne({ test: 'recovery' });
        if (!doc) { quit(1); }
        db.getSiblingDB('test').recovery_test.deleteOne({ test: 'recovery' });
        "
    
    if [ $? -ne 0 ]; then
        track_recovery "verification" "failed" "Write test failed"
        return 1
    fi
    
    track_recovery "verification" "completed" "Recovery verified successfully"
    return 0
}

# 生成恢复报告
generate_recovery_report() {
    local report_file="${MONGO_BASE}/recovery_report_$(date +%Y%m%d_%H%M%S).txt"
    
    {
        echo "MongoDB Cluster Recovery Report"
        echo "=============================="
        echo "Date: $(date)"
        echo
        echo "Recovery Status:"
        cat "${RECOVERY_STATUS}"
        echo
        echo "Container Status:"
        docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}" | grep "mongo"
        echo
        echo "Cluster Status:"
        docker exec -it mongos1 mongosh --port "${MONGOS_PORT}" \
            -u "${MONGO_ADMIN_USER}" \
            -p "${MONGO_ADMIN_PASS}" \
            --eval "sh.status()"
    } > "${report_file}"
    
    echo "Recovery report generated: ${report_file}"
}

# 主函数
main() {
    local backup_path=""
    
    # 解析命令行参数
    while [[ $# -gt 0 ]]; do
        case $1 in
            --backup-path)
                backup_path="$2"
                shift 2
                ;;
            *)
                echo "Unknown option: $1"
                exit 1
                ;;
        esac
    done
    
    echo "Starting MongoDB cluster recovery..."
    
    # 执行恢复步骤
    check_cluster_health
    local current_backup=$(backup_current_config)
    
    # 如果提供了备份路径，执行数据恢复
    if [ ! -z "$backup_path" ]; then
        restore_data "$backup_path"
    fi
    
    # 恢复不健康的容器
    while read container; do
        if [ "$(docker inspect -f '{{.State.Running}}' $container 2>/dev/null)" != "true" ]; then
            recover_container "$container" "${MONGO_BASE}/config/$(basename $container).conf"
        fi
    done < <(docker ps -a --format "{{.Names}}" | grep "mongo")
    
    rebuild_replica_sets
    reconfigure_sharding
    verify_recovery
    generate_recovery_report
    
    echo "MongoDB cluster recovery completed"
}

# 错误处理
trap 'echo "Recovery failed. Check recovery status for details."' ERR

# 执行恢复
main "$@"
```

这个脚本提供了完整的集群恢复功能，包括：

1.  健康状态检查
2.  配置备份
3.  容器恢复
4.  复制集重建
5.  数据恢复
6.  分片重配置
7.  恢复验证
8.  报告生成

需要我继续提供其他维护或监控脚本吗？

