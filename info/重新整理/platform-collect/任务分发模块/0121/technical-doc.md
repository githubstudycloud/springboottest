# URI Collection Service Technical Documentation

## 1. 项目概述

这是一个URI数据采集服务项目，主要用于从第三方服务获取URI数据并进行存储和管理。项目具有以下核心功能：

- URI数据的采集与同步
- 版本管理
- 增量更新
- 数据清理
- 异步任务处理
- 批量操作支持

### 1.1 技术栈

- Spring Boot
- MongoDB
- Redis
- Java 并发工具
- RESTful API

## 2. 核心业务流程

### 2.1 URI采集流程

1. **初始化采集任务**
   - 接收采集参数（根节点、版本等）
   - 创建异步任务
   - 返回任务ID

2. **数据采集**
   - 获取版本列表
   - 批量获取URI列表
   - 获取URI详情
   - 数据转换和存储

3. **数据验证**
   - 检查数据完整性
   - 验证URI唯一性
   - 更新索引

### 2.2 版本管理

- 支持主干版本(TRUNK)和分支版本(BRANCH)
- 版本号自动递增
- 版本历史追踪

### 2.3 增量更新机制

- 基于时间戳的增量更新
- 支持自定义时间范围
- 数据对比和同步

## 3. API接口文档

### 3.1 URI采集接口

#### 开始采集
```http
POST /api/collect/sync

Request Body:
{
    "rootNode": "string",      // 根节点
    "version": "string",       // 版本号
    "serverUrl": "string",     // 服务器URL
    "incremental": boolean,    // 是否增量采集
    "startTime": "datetime",   // 增量开始时间
    "endTime": "datetime",     // 增量结束时间
    "batchSize": number,       // 批量大小
    "priority": number         // 优先级
}

Response:
{
    "taskId": "string",
    "status": "string",
    "message": "string"
}
```

#### 查询版本列表
```http
GET /api/collect/versions/{rootNode}?page={page}&size={size}

Response:
{
    "total": number,
    "items": ["version1", "version2", ...]
}
```

#### 获取URI数量
```http
GET /api/collect/count/{rootNode}/{version}

Response:
number
```

### 3.2 任务管理接口

#### 查询任务状态
```http
GET /api/collect/task/{taskId}

Response:
{
    "taskId": "string",
    "status": "string",
    "progress": number,
    "message": "string"
}
```

#### 取消任务
```http
DELETE /api/collect/task/{taskId}

Response:
boolean
```

#### 更新任务优先级
```http
PUT /api/collect/task/{taskId}/priority/{priority}

Response:
boolean
```

## 4. 监控与维护

### 4.1 性能监控

- URI采集速率
- 数据库操作延迟
- 任务队列状态
- 线程池使用情况

### 4.2 数据维护

- 定期数据清理
- 索引优化
- 版本管理
- 错误重试机制

### 4.3 常见问题排查

1. **采集失败**
   - 检查网络连接
   - 验证服务器URL
   - 查看错误日志
   - 检查任务状态

2. **数据不一致**
   - 验证版本信息
   - 检查增量更新时间范围
   - 执行数据完整性检查
   - 比对源数据和目标数据

3. **性能问题**
   - 检查MongoDB索引使用情况
   - 监控线程池状态
   - 优化批处理大小
   - 调整并发参数

## 5. 配置说明

### 5.1 核心配置项

```yaml
collect:
  testcase:
    batchSize: 100           # 批量处理大小
    threadCount: 4           # 处理线程数
    retryTimes: 3           # 重试次数
    timeout: 3600           # 超时时间(秒)

spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/testdb
      database: testdb
      min-pool-size: 20
      max-pool-size: 100
```

### 5.2 常量配置

```java
public class CollectionConstants {
    public static final int DEFAULT_BATCH_SIZE = 200;
    public static final int MAX_BATCH_SIZE = 1000;
    public static final int MIN_BATCH_SIZE = 50;
    public static final int HTTP_MAX_REQUESTS_PER_MINUTE = 200;
    // ... 更多配置
}
```

## 6. 部署指南

### 6.1 环境要求

- JDK 17+
- MongoDB 4.0+
- Redis 5.0+
- 足够的磁盘空间和内存

### 6.2 部署步骤

1. **准备环境**
   - 安装JDK
   - 配置MongoDB
   - 配置Redis

2. **应用部署**
   ```bash
   mvn clean package
   java -jar business-testcase.jar
   ```

3. **验证部署**
   - 检查日志输出
   - 测试健康检查接口
   - 验证数据连接

### 6.3 扩展配置

- 配置日志级别
- 调整线程池参数
- 设置监控告警
- 配置备份策略

## 7. 最佳实践

1. **采集优化**
   - 合理设置批量大小
   - 启用增量更新
   - 适当配置并发度
   - 实现错误重试机制

2. **性能优化**
   - 使用合适的索引
   - 优化查询语句
   - 合理使用缓存
   - 控制并发数量

3. **运维建议**
   - 定期数据备份
   - 监控系统状态
   - 及时清理历史数据
   - 做好容量规划
