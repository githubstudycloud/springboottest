# 项目结构和业务逻辑

## 1. 目录结构
```
src/main/java/com/study/collect/
├── api/                                    # API层
│   ├── controller/
│   │   └── UriCollectController.java       # REST接口控制器
│   └── response/                           # 响应对象
│       ├── BaseResponse.java               # 基础响应
│       ├── PageResponse.java               # 分页响应
│       └── VersionResponse.java            # 版本信息响应
├── core/                                   # 核心配置层
│   ├── config/
│   │   ├── MongoConfig.java               # MongoDB配置
│   │   ├── ThreadPoolConfig.java          # 线程池配置
│   │   └── ObjectPoolConfig.java          # 对象池配置
│   ├── constant/
│   │   └── VersionType.java               # 版本类型枚举(TRUNK/BRANCH)
│   ├── storage/
│   │   ├── entity/
│   │   │   ├── BaseEntity.java           # 基础实体
│   │   │   └── VersionEntity.java        # 版本实体
│   │   └── repository/
│   │       ├── IRepository.java          # 仓储接口
│   │       └── BaseMongoRepository.java   # MongoDB基础仓储实现
│   └── util/
│       ├── HashUtil.java                  # URI哈希工具
│       └── PageUtil.java                  # 分页工具
├── domain/                                 # 领域层
│   ├── entity/
│   │   └── UriEntity.java                 # URI实体定义
│   └── param/                             # 参数对象
│       ├── CollectParam.java              # 采集参数
│       └── PageParam.java                 # 分页参数
├── service/                                # 服务层
│   ├── UriCollectService.java             # 采集服务接口
│   ├── impl/
│   │   └── UriCollectServiceImpl.java     # 采集服务实现
│   └── http/                              # HTTP服务
│       ├── UriHttpService.java            # HTTP请求封装
│       └── response/                       # 响应解析器
│           ├── HttpResponseParser.java     # 解析器接口
│           └── impl/                       # 实现类
│               ├── VersionResponseParser.java
│               ├── UriListResponseParser.java
│               └── UriDetailResponseParser.java
└── repository/                             # 仓储层
    └── UriRepository.java                  # URI仓储实现

resources/
├── application.yml                         # 应用配置
└── logback-spring.xml                     # 日志配置
```

## 2. 数据流转和业务逻辑

### 2.1 数据采集流程

1. 入口调用流程：
```
Controller -> CollectService -> HttpService -> MongoDB存储
```

2. 版本获取和处理：
   - 分页获取所有版本信息
   - 区分主干版本和分支版本
   - 优先处理主干版本，后处理分支版本
   - 版本间串行处理，保证数据一致性

3. URI采集流程：
   - 分页获取版本下所有URI
   - URI列表分批并行处理（200个一批）
   - 批量获取URI详情
   - 使用对象池管理实体对象
   - 批量upsert到MongoDB

### 2.2 数据同步策略

1. 全量同步：
   - 获取所有版本的全部URI
   - 批量更新或插入数据
   - 不删除历史数据

2. 增量同步：
   - 获取指定时间范围的URI变更
   - 对比现有数据进行更新
   - 清理已不存在的URI数据

### 2.3 并发控制

1. 线程池管理：
   - 核心线程数 = CPU核心数 * 2
   - 最大线程数 = CPU核心数 * 4
   - 使用有界队列（5000）控制任务数量
   - 使用CallerRunsPolicy防止任务丢失

2. 并发层次：
```
版本处理：串行
  └── URI列表获取：串行
       └── URI详情处理：并行（分批）
            └── 数据存储：批量
```

### 2.4 数据存储设计

1. MongoDB文档结构：
```json
{
  "uriHash": "SHA256(uri)",      // 主键，唯一索引
  "uri": "原始URI",              // 原始URI
  "rootNode": "根节点",          // 根节点标识
  "versionType": "TRUNK/BRANCH", // 版本类型
  "uriVersion": "版本号",        // URI所属版本
  "details": {                   // URI详情
    "field1": "value1",
    "field2": "value2"
  },
  "createTime": "创建时间",
  "updateTime": "更新时间",
  "version": 0                   // 版本号(预留)
}
```

2. 索引设计：
   - uriHash: 唯一索引
   - rootNode + versionType: 复合索引
   - uri: 普通索引

### 2.5 错误处理机制

1. HTTP请求重试：
   - 最大重试3次
   - 指数退避策略
   - 详细错误日志记录

2. 数据一致性保证：
   - 版本级别事务
   - 批量操作原子性
   - 对象池资源管理

3. 异常恢复：
   - 版本级别的失败不影响其他版本
   - 记录失败版本和原因
   - 支持断点续传

### 2.6 监控和运维

1. 日志记录：
   - 详细的处理进度日志
   - 错误和异常栈跟踪
   - 性能指标记录

2. 运行状态：
   - 线程池使用状况
   - MongoDB连接状态
   - 内存使用监控

## 3. 优化建议

1. 性能优化：
   - 调整批处理大小
   - 优化MongoDB索引
   - 配置合适的线程池参数

2. 可靠性提升：
   - 添加熔断机制
   - 实现优雅停机
   - 增加健康检查

3. 扩展性建议：
   - 支持多数据源
   - 添加缓存层
   - 支持集群部署
