## Prompt:
为我总结这个项目，先从collect-core模块开始，分析里面的文件的功能，以及如何运行，请给出大纲，分段总结

## Response:
我来帮您分析collect-core模块的功能结构和运行机制。

一、核心功能模块概述
==========

collect-core是整个项目的核心基础模块，主要包含以下几个核心功能包：

1.  collector包 - 数据采集核心

*   定义了采集器接口ICollector和抽象实现AbstractCollector
*   提供了采集器注解@Collector用于标记采集器实现类
*   包含CollectorManager用于管理和注册采集器
*   CollectorFactory负责创建采集器实例

2.  processor包 - 数据处理核心

*   定义了处理器接口IProcessor和抽象实现AbstractProcessor
*   提供了处理器注解@Processor用于标记处理器实现类
*   ProcessorManager负责管理处理器链
*   支持多个处理器按顺序处理数据

3.  storage包 - 数据存储层

*   提供了MongoDB存储的基础设施
*   实现了缓存功能(Redis)
*   包含实体审计、版本控制等功能
*   提供分布式锁实现

4.  task包 - 任务调度核心

*   任务定义和配置管理
*   任务实例执行控制
*   任务调度器实现
*   任务分片和分发机制

5.  mq包 - 消息队列集成

*   集成RabbitMQ消息队列
*   实现任务消息的发送和消费
*   处理任务执行结果回调

二、关键运行机制
========

1.  采集流程

```
CollectorFactory -> CollectorManager -> AbstractCollector -> 具体采集器实现
```

*   CollectorFactory根据类型创建采集器实例
*   CollectorManager管理采集器的注册和获取
*   AbstractCollector提供了采集的基础流程控制
*   具体采集器实现特定的采集逻辑

2.  处理流程

```
ProcessorManager -> ProcessChain -> AbstractProcessor -> 具体处理器实现
```

*   ProcessorManager创建和管理处理器链
*   ProcessChain控制多个处理器的执行顺序
*   AbstractProcessor提供了处理的基础流程控制
*   具体处理器实现特定的处理逻辑

3.  任务调度流程

```
TaskScheduler -> TaskDispatcher -> TaskExecuteService -> TaskHandler
```

*   TaskScheduler负责任务的调度控制
*   TaskDispatcher负责任务的分发
*   TaskExecuteService处理任务的具体执行
*   TaskHandler实现具体的任务处理逻辑

三、核心配置要点
========

1.  数据库配置

*   MongoDB配置
*   Redis缓存配置
*   MariaDB配置

2.  消息队列配置

*   RabbitMQ连接配置
*   队列和交换机定义
*   消费者配置

3.  任务配置

*   线程池配置
*   任务执行参数配置
*   重试策略配置

四、扩展机制
======

1.  采集器扩展

*   实现ICollector接口
*   添加@Collector注解
*   注册到CollectorManager

2.  处理器扩展

*   实现IProcessor接口
*   添加@Processor注解
*   注册到ProcessorManager

3.  任务处理器扩展

*   实现TaskHandler接口
*   继承AbstractTaskHandler
*   注册到TaskHandlerManager

这个collect-core模块为整个采集平台提供了强大的基础设施支持，通过这些核心功能的组合，可以快速构建各种数据采集和处理场景的应用。要成功运行该模块，需要正确配置相关的中间件(MongoDB、Redis、RabbitMQ等)，并按照扩展机制开发具体的业务实现。

需要我继续分析其他模块吗？

## Prompt:
继续

## Response:
好的，我继续分析其他模块。

collect-common 模块分析
===================

collect-common 是一个公共基础模块，提供了整个项目通用的工具类和基础组件。

主要组件：
-----

1.  异常处理

*   BaseException: 基础异常类，其他业务异常继承自此类
*   统一的异常处理机制

2.  通用响应模型

*   Response: 统一响应封装类
*   包含状态码、消息和数据三个基本要素
*   提供了success()和error()等静态构建方法

3.  工具类

*   DateUtils: 日期时间处理工具
    *   提供日期格式化
    *   日期解析
    *   常用日期操作方法
*   JsonUtils: JSON处理工具
    *   基于Jackson的序列化和反序列化
    *   统一的JSON转换方法
    *   异常处理封装

collect-business 模块分析
=====================

这是业务实现模块，包含多个子模块，每个子模块负责特定领域的数据采集。

1\. business-enterprise 子模块
---------------------------

负责企业数据采集处理：

1.  核心组件

*   EnterpriseCollector: 企业数据采集器
*   EnterpriseProcessor: 企业数据处理器
*   EnterpriseService: 业务服务层
*   EnterpriseController: 接口控制层

2.  数据模型

*   Enterprise: 企业实体模型
*   EnterpriseGenerateRequest: 数据生成请求
*   EnterpriseQueryRequest: 查询请求
*   EnterpriseQueryResponse: 查询响应

3.  主要功能

*   企业数据采集
*   数据生成测试
*   分页查询
*   增量数据获取

2\. business-finance 子模块
------------------------

负责金融数据采集处理：

1.  核心组件

*   FinanceCollector: 金融数据采集器
*   FinanceProcessor: 金融数据处理器
*   FinanceService: 业务服务
*   FinanceController: 接口控制

2.  数据模型

*   FinanceData: 金融数据实体
*   包含价格、交易量等金融指标

3.  主要功能

*   金融数据实时采集
*   数据验证和转换
*   缓存管理
*   交易监控

3\. business-medical 子模块
------------------------

负责医疗数据采集处理：

1.  核心组件

*   MedicalCollector: 医疗数据采集器
*   MedicalEngine: 医疗数据处理引擎
*   DicomProcessor: DICOM图像处理器
*   ImageProcessor: 图像处理器
*   PrivacyProcessor: 隐私数据处理器

2.  数据模型

*   MedicalData: 医疗数据实体
*   包含患者信息、图像数据等

3.  主要功能

*   医疗图像数据采集
*   DICOM文件处理
*   图像增强和压缩
*   隐私数据保护

collect-starter 模块分析
====================

启动模块，负责整个应用的配置和启动：

1.  核心配置

*   application.yml: 主配置文件
*   application-dev.yml: 开发环境配置
*   application-prod.yml: 生产环境配置

2.  数据库脚本

*   sql.sql: 包含任务相关表结构
*   包括任务配置、任务实例、任务日志等表

3.  主要功能

*   应用启动配置
*   环境配置管理
*   依赖管理
*   打包部署配置

运行机制：

1.  启动流程

```
CollectApplication -> 加载配置 -> 初始化模块 -> 启动服务
```

2.  配置加载

```
application.yml -> profile specific config -> 外部配置
```

3.  模块装配

```
collect-core -> collect-common -> collect-business -> 启动应用
```

要运行此项目：

1.  环境准备

*   JDK 21
*   Maven 3.x
*   MongoDB
*   Redis
*   RabbitMQ
*   MariaDB/MySQL

2.  配置修改

*   修改数据库连接配置
*   修改中间件连接配置
*   调整运行参数

3.  构建部署

```bash
mvn clean package
java -jar collect-starter/target/collect-starter-1.0.0-SNAPSHOT.jar
```

这个项目采用了良好的模块化设计，每个模块职责清晰，通过核心模块提供的基础设施，可以方便地扩展新的业务采集模块。要成功运行项目，需要正确配置相关的中间件，并根据实际需求调整配置参数。

需要我详细讲解某个具体的模块或功能吗？

## Prompt:
细致讲讲collect-core模块，还有如何通过collect-starter启动并测试business-enterprise，给出你的讲述大纲

## Response:
我来详细讲解这部分内容。

一、collect-core模块深入解析
====================

1\. collector包核心实现
------------------

1.  接口设计

```java
// 采集器接口定义
public interface ICollector<T, R> {
    CollectResult<R> collect(CollectContext<T> context);
    String getType();
}

// 抽象基类实现
public abstract class AbstractCollector<T, R> implements ICollector<T, R> {
    protected void preProcess(CollectContext<T> context)
    protected abstract R doCollect(CollectContext<T> context)
    protected void postProcess(CollectResult<R> data)
}
```

2.  采集上下文与结果

```java
// 上下文对象
public class CollectContext<T> {
    private String taskId;         // 任务ID  
    private T params;             // 采集参数
    private Integer shardingId;   // 分片ID
    private Map<String, Object> attributes; // 上下文属性
}

// 结果对象
public class CollectResult<T> {
    private boolean success;      // 是否成功
    private String errorMessage;  // 错误信息
    private T data;              // 采集数据
}
```

2\. processor包核心实现
------------------

1.  处理器接口

```java
public interface IProcessor<T> {
    T process(T data, ProcessContext context);
    String getType();
    int getOrder();
}
```

2.  处理链实现

```java
public class ProcessChain<T> {
    private final List<IProcessor<T>> processors;
    
    public T process(T data) {
        ProcessContext context = new ProcessContext();
        for (IProcessor<T> processor : processors) {
            data = processor.process(data, context);
        }
        return data;
    }
}
```

3\. storage包存储实现
----------------

1.  MongoDB基础支持

```java
public interface IRepository<T extends BaseEntity> {
    T findByCode(String code);
    Page<T> findByDeletedFalse(Pageable pageable);
    void softDelete(String id);
}
```

2.  Redis缓存实现

```java
@Cache(key = "prefix:#{#param}")
@CacheLock(key = "lock:#{#param}")
```

4\. task包任务实现
-------------

1.  任务调度器

```java
public interface TaskScheduler {
    void start();
    void stop();
    void addTask(TaskDefinition task);
    void removeTask(String taskId);
}
```

2.  任务执行器

```java
public class TaskExecuteService {
    public TaskInstance createTaskInstance(String taskCode, Integer shardIndex, String shardParam)
    public void completeTaskInstance(String instanceId, boolean success, String errorMsg)
}
```

二、启动并测试business-enterprise
==========================

1\. 环境准备
--------

```bash
# 1. 启动必要的中间件
docker-compose up -d mongodb redis rabbitmq mariadb

# 2. 初始化数据库
mysql -u root -p < collect-starter/src/main/resources/sql.sql
```

2\. 配置修改
--------

1.  application.yml配置示例

```yaml
spring:
  datasource:
    url: jdbc:mariadb://localhost:3306/test
    username: root
    password: 123456
  
  data:
    mongodb:
      uri: mongodb://localhost:27017/test
    
  redis:
    cluster:
      nodes: localhost:6379
      
  rabbitmq:
    host: localhost
    port: 5672
```

3\. 启动服务
--------

```bash
# 1. 打包
mvn clean package

# 2. 运行
java -jar collect-starter/target/collect-starter-1.0.0-SNAPSHOT.jar
```

4\. 功能测试
--------

1.  生成测试数据

```bash
curl -X POST http://localhost:8080/api/enterprise/generate \
-H "Content-Type: application/json" \
-d '{
    "startCode": 1000,
    "count": 10,
    "industry": "制造业"
}'
```

2.  触发数据采集

```bash
# 单个企业采集
curl -X POST "http://localhost:8080/api/enterprise/collect?code=1000"

# 批量采集
curl -X POST http://localhost:8080/api/enterprise/collect
```

3.  查询采集结果

```bash
# 分页查询
curl "http://localhost:8080/api/enterprise/page?pageNum=1&pageSize=10"

# 查询指定企业
curl http://localhost:8080/api/enterprise/1000
```

5\. 结果验证
--------

1.  检查MongoDB数据

```javascript
use test
db.enterprise.find()
```

2.  查看任务日志

```sql
SELECT * FROM task_log WHERE task_code = 'enterprise';
```

3.  检查采集状态

```sql
SELECT * FROM task_instance WHERE task_code = 'enterprise';
```

6\. 排查问题
--------

1.  检查日志文件

```bash
tail -f logs/collect.log
```

2.  检查MQ消息

```bash
# RabbitMQ Management控制台
http://localhost:15672
```

3.  监控指标

```bash
# Prometheus metrics
http://localhost:8080/actuator/prometheus
```

通过这个流程，我们可以完整测试enterprise模块的数据采集功能。重点需要注意：

1.  中间件连接配置是否正确
2.  数据库表结构是否已初始化
3.  采集参数的正确性
4.  任务执行状态的监控
5.  错误日志的及时检查

需要我详细展开某个具体的部分吗？

## Prompt:
按这个结构顺序细节讲述

## Response:
好的，让我们从头开始详细讲解。

一、collect-core模块深入解析
====================

1\. collector包核心实现
------------------

### 1.1 ICollector接口设计

```java
public interface ICollector<T, R> {
    // T: 输入参数类型
    // R: 返回结果类型
    CollectResult<R> collect(CollectContext<T> context);
    String getType();
}
```

这个接口定义了采集器的核心行为：

*   collect方法：执行具体的采集逻辑
*   getType方法：返回采集器类型标识

### 1.2 AbstractCollector抽象实现

```java
public abstract class AbstractCollector<T, R> implements ICollector<T, R> {
    @Override
    public CollectResult<R> collect(CollectContext<T> context) {
        try {
            // 前置处理
            preProcess(context);
            
            // 执行采集
            R sourceData = doCollect(context);
            CollectResult<R> data = CollectResult.success(sourceData);
            
            // 后置处理
            postProcess(data);
            
            return CollectResult.success(data.getData());
        } catch (Exception e) {
            return CollectResult.failure(e.getMessage());
        }
    }
    
    // 子类必须实现的采集方法
    protected abstract R doCollect(CollectContext<T> context);
}
```

AbstractCollector提供了:

*   统一的异常处理
*   标准的采集流程
*   扩展点（前置/后置处理）

### 1.3 采集上下文设计

```java
@Data
public class CollectContext<T> {
    private String taskId;     // 任务标识
    private T params;         // 采集参数
    private Integer shardingId; // 分片ID
    private Map<String, Object> attributes = new ConcurrentHashMap<>(); // 上下文属性

    // 属性访问方法
    public void setAttribute(String key, Object value)
    public <V> V getAttribute(String key)
}
```

### 1.4 采集结果封装

```java
@Data
public class CollectResult<T> {
    private boolean success;
    private String errorMessage;
    private T data;
    private LocalDateTime finishTime;

    // 静态构建方法
    public static <T> CollectResult<T> success(T data)
    public static <T> CollectResult<T> failure(String errorMessage)
}
```

2\. processor包核心实现
------------------

### 2.1 IProcessor接口

```java
public interface IProcessor<T> {
    // 数据处理方法
    T process(T data, ProcessContext context);
    
    // 处理器类型
    String getType();
    
    // 处理顺序
    int getOrder();
}
```

### 2.2 AbstractProcessor实现

```java
@Slf4j
public abstract class AbstractProcessor<T> implements IProcessor<T> {
    @Override
    public T process(T data, ProcessContext context) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        try {
            // 前置处理
            preProcess(data, context);
            
            // 执行处理
            T result = doProcess(data, context);
            
            // 后置处理
            result = postProcess(result, context);
            
            stopWatch.stop();
            log.info("处理完成: cost={}ms", stopWatch.getTotalTimeMillis());
            
            return result;
        } catch (Exception e) {
            throw new ProcessException("处理失败: " + e.getMessage(), e);
        }
    }

    protected abstract T doProcess(T data, ProcessContext context);
}
```

让我知道是否需要继续详细讲解下一部分，或者需要对以上内容有任何补充说明？

## Prompt:
继续

## Response:
好的，继续详细讲解。

### 2.3 处理器链实现 ProcessChain

```java
@Slf4j
public class ProcessChain<T> {
    private final List<IProcessor<T>> processors;
    private final String chainId;

    public ProcessChain(String chainId) {
        this.chainId = chainId;
        this.processors = new ArrayList<>();
    }

    // 添加处理器并按order排序
    public void addProcessor(IProcessor<T> processor) {
        processors.add(processor);
        processors.sort((p1, p2) -> p1.getOrder() - p2.getOrder());
    }

    // 执行处理链
    public T process(T data) {
        ProcessContext context = new ProcessContext();
        context.setChainId(chainId);

        for (IProcessor<T> processor : processors) {
            try {
                data = processor.process(data, context);
                log.debug("处理器执行成功: processor={}, chainId={}", 
                    processor.getType(), chainId);
            } catch (Exception e) {
                log.error("处理器执行失败", e);
                throw e;
            }
        }
        return data;
    }
}
```

3\. storage包存储实现
----------------

### 3.1 基础实体设计

```java
@Data
public abstract class BaseEntity implements Serializable {
    @Id
    protected String id;
    
    @CreatedDate
    protected LocalDateTime createTime;
    
    @LastModifiedDate
    protected LocalDateTime updateTime;
    
    @Version
    protected Long version;
    
    protected Boolean deleted = false;
}

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class VersionEntity extends BaseEntity {
    protected String versionCode;    // 业务版本号
    protected LocalDateTime versionTime;  // 版本时间

    public void initVersion() {
        this.version = 0L;
        this.versionCode = generateVersionCode();
        this.versionTime = LocalDateTime.now();
    }

    protected String generateVersionCode() {
        return String.format("V%s_%d",
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")),
            this.version);
    }
}
```

### 3.2 MongoDB存储实现

```java
@Slf4j
public class BaseMongoRepository<T extends BaseEntity>
        extends SimpleMongoRepository<T, String> implements IRepository<T> {

    private final MongoOperations mongoOperations;
    private final MongoEntityInformation<T, String> entityInformation;

    @Override
    public T findByCode(String code) {
        Query query = Query.query(
                Criteria.where("code").is(code)
                        .and("deleted").is(false)
        );
        return mongoOperations.findOne(query, entityInformation.getJavaType());
    }

    @Override
    public void softDelete(String id) {
        Query query = Query.query(Criteria.where("id").is(id));
        Update update = Update.update("deleted", true)
                .set("updateTime", LocalDateTime.now());
        mongoOperations.updateFirst(query, update, entityInformation.getJavaType());
    }

    protected Page<T> findAll(Query query, Pageable pageable) {
        long total = mongoOperations.count(query, entityInformation.getJavaType());
        List<T> content = mongoOperations.find(query.with(pageable),
                entityInformation.getJavaType());
        return new PageImpl<>(content, pageable, total);
    }
}
```

### 3.3 缓存实现

```java
@Component
@RequiredArgsConstructor
public class RedisCacheManager implements CacheManager {
    
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public <T> T get(String key, Class<T> type, Supplier<T> supplier, CacheOptions options) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value != null) {
            return convertValue(value, type);
        }

        T newValue = supplier.get();
        if (newValue != null) {
            put(key, newValue, options);
        }
        return newValue;
    }

    @Override
    public <T> void put(String key, T value, CacheOptions options) {
        CacheOptions actualOptions = Optional.ofNullable(options)
                .orElse(CacheOptions.defaultOptions());

        redisTemplate.opsForValue().set(
                key,
                value,
                actualOptions.getExpiration(),
                actualOptions.getTimeUnit()
        );
    }
}
```

### 3.4 分布式锁实现

```java
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisLock implements DistributedLock {
    
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean tryLock(String key, long waitTime, long leaseTime, TimeUnit unit) {
        try {
            long startTime = System.currentTimeMillis();
            long waitMillis = unit.toMillis(waitTime);

            do {
                Boolean success = redisTemplate.opsForValue()
                        .setIfAbsent(key, Thread.currentThread().getId(), 
                            leaseTime, unit);

                if (Boolean.TRUE.equals(success)) {
                    return true;
                }

                Thread.sleep(100);
            } while (System.currentTimeMillis() - startTime < waitMillis);

            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    @Override
    public void unlock(String key) {
        try {
            Long threadId = (Long) redisTemplate.opsForValue().get(key);
            if (threadId != null && threadId.equals(Thread.currentThread().getId())) {
                redisTemplate.delete(key);
            }
        } catch (Exception e) {
            log.error("解锁失败: {}", key, e);
        }
    }
}
```

需要我继续讲解任务包(task package)的实现以及后续的启动测试部分吗？

## Prompt:
继续，重点讲下它怎么处理配置文件配置任务和表里配置任务的，如果2个都配同一个会有什么影响

## Response:
好的，我来重点讲解任务配置和调度的实现。

4\. task包任务实现
-------------

### 4.1 任务配置来源分析

项目支持两种任务配置方式：

1.  配置文件配置(application.yml)

```yaml
collect:
  task:
    configs:
      - task-code: enterprise
        cron: "0 0/30 * * * ?"  # 每30分钟执行
        handler: enterpriseCollector
        shard-total: 4
        retry-times: 3
      - task-code: finance 
        cron: "0 0 1 * * ?"    # 每天1点执行
        handler: financeCollector
```

2.  数据库表配置(task\_config表)

```sql
INSERT INTO task_config (
    task_code, task_name, task_handler, 
    cron_expr, shard_total, status
) VALUES (
    'enterprise', '企业数据采集', 'enterpriseCollector',
    '0 0/30 * * * ?', 4, 1
);
```

### 4.2 任务加载优先级处理

```java
@Service
@Slf4j
public class TaskConfigService {
    private final TaskConfigMapper taskConfigMapper;
    
    // 加载任务配置
    public List<TaskConfig> loadAllTaskConfigs() {
        // 1. 先加载数据库配置
        List<TaskConfig> dbConfigs = taskConfigMapper.selectEnabled();
        Map<String, TaskConfig> configMap = new HashMap<>();
        dbConfigs.forEach(config -> configMap.put(config.getTaskCode(), config));
        
        // 2. 加载配置文件配置，如果数据库已存在则跳过
        List<TaskConfig> fileConfigs = loadFileConfigs();
        fileConfigs.forEach(config -> {
            if (!configMap.containsKey(config.getTaskCode())) {
                configMap.put(config.getTaskCode(), config);
            } else {
                log.warn("任务[{}]在数据库中已配置，跳过配置文件配置", 
                    config.getTaskCode());
            }
        });
        
        return new ArrayList<>(configMap.values());
    }
}
```

### 4.3 任务调度器实现

```java
@Component
public class DefaultTaskScheduler extends AbstractTaskScheduler {
    private final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
    
    @PostConstruct
    public void init() {
        // 启动时加载所有任务配置
        List<TaskConfig> configs = taskConfigService.loadAllTaskConfigs();
        configs.forEach(this::scheduleTask);
    }
    
    private void scheduleTask(TaskConfig config) {
        String taskId = config.getTaskCode();
        
        // 如果任务已经在运行，先停止
        ScheduledFuture<?> existingTask = scheduledTasks.get(taskId);
        if (existingTask != null) {
            log.info("停止已存在的任务调度: {}", taskId);
            existingTask.cancel(false);
        }
        
        // 创建新的调度任务
        ScheduledFuture<?> future = scheduler.schedule(
            () -> executeTask(config),
            new CronTrigger(config.getCronExpr())
        );
        
        scheduledTasks.put(taskId, future);
        log.info("成功调度任务: {}, cron={}", taskId, config.getCronExpr());
    }
    
    private void executeTask(TaskConfig config) {
        try {
            // 1. 创建任务实例
            int shardTotal = config.getShardTotal();
            for (int i = 0; i < shardTotal; i++) {
                TaskInstance instance = taskExecuteService.createTaskInstance(
                    config.getTaskCode(), i, 
                    createShardParam(i, shardTotal)
                );
                
                // 2. 分发任务到消息队列
                taskDispatcher.dispatch(instance);
            }
        } catch (Exception e) {
            log.error("任务执行异常: {}", config.getTaskCode(), e);
        }
    }
}
```

### 4.4 配置冲突处理策略

1.  优先级规则：

*   数据库配置优先级高于配置文件配置
*   同一个taskCode的任务，只会保留优先级高的配置

2.  冲突处理：

```java
public class TaskConflictResolver {
    
    public TaskConfig resolveConflict(TaskConfig dbConfig, TaskConfig fileConfig) {
        if (dbConfig != null && fileConfig != null) {
            // 记录冲突日志
            log.warn("任务配置冲突: taskCode={}, 使用数据库配置", 
                dbConfig.getTaskCode());
            return dbConfig;
        }
        return dbConfig != null ? dbConfig : fileConfig;
    }
    
    public void validateConfig(TaskConfig config) {
        // 验证cron表达式
        try {
            new CronExpression(config.getCronExpr());
        } catch (Exception e) {
            throw new TaskValidationException(
                "无效的cron表达式: " + config.getCronExpr());
        }
        
        // 验证其他参数
        if (config.getShardTotal() < 1) {
            throw new TaskValidationException("分片数必须大于0");
        }
    }
}
```

3.  运行时影响：

*   如果同时配置，只会执行数据库中的任务配置
*   配置文件中的任务会被忽略并记录警告日志
*   已经在运行的任务会被新的配置替换
*   任务实例会正常执行到完成，不会中断

### 4.5 最佳实践建议

1.  配置管理建议：

*   开发/测试环境：使用配置文件方便调试
*   生产环境：使用数据库配置便于动态调整
*   避免同时使用两种配置方式

2.  配置修改流程：

```java
@Service
public class TaskManageService {
    
    @Transactional
    public void updateTaskConfig(TaskConfig config) {
        // 1. 停止现有任务
        taskScheduler.removeTask(config.getTaskCode());
        
        // 2. 更新配置
        taskConfigService.saveTaskConfig(config);
        
        // 3. 重新调度任务
        taskScheduler.addTask(convertToDefinition(config));
    }
}
```

3.  监控告警：

```java
@Component
public class TaskMonitor {
    
    @Scheduled(fixedRate = 60000) // 每分钟检查
    public void checkTaskStatus() {
        // 检查任务配置一致性
        List<TaskConfig> dbConfigs = taskConfigService.getEnabledTaskConfigs();
        Set<String> runningTasks = taskScheduler.getRunningTasks();
        
        // 发现配置不一致
        dbConfigs.forEach(config -> {
            if (!runningTasks.contains(config.getTaskCode())) {
                log.error("任务配置不一致: {}", config.getTaskCode());
                // 发送告警
                alertService.sendAlert("任务配置异常", 
                    "数据库配置的任务未运行: " + config.getTaskCode());
            }
        });
    }
}
```

需要我继续讲解启动和测试部分吗？或者需要对任务配置与调度有任何补充说明？

## Prompt:
继续讲解

## Response:
下面继续讲解启动和测试部分。

二、启动并测试business-enterprise
==========================

1\. 环境准备
--------

### 1.1 Docker环境配置

创建 docker-compose.yml:

```yaml
version: '3.8'
services:
  mongodb:
    image: mongo:latest
    ports:
      - "27017:27017"
    environment:
      MONGO_INITDB_ROOT_USERNAME: root
      MONGO_INITDB_ROOT_PASSWORD: 123456
    volumes:
      - mongodb_data:/data/db

  redis:
    image: redis:latest
    ports:
      - "6379:6379"
    command: redis-server --requirepass 123456
    volumes:
      - redis_data:/data

  rabbitmq:
    image: rabbitmq:management
    ports:
      - "5672:5672"
      - "15672:15672"
    environment:
      RABBITMQ_DEFAULT_USER: admin
      RABBITMQ_DEFAULT_PASS: 123456
    volumes:
      - rabbitmq_data:/var/lib/rabbitmq

  mariadb:
    image: mariadb:latest
    ports:
      - "3306:3306"
    environment:
      MYSQL_ROOT_PASSWORD: 123456
      MYSQL_DATABASE: test
    volumes:
      - mariadb_data:/var/lib/mysql

volumes:
  mongodb_data:
  redis_data:
  rabbitmq_data:
  mariadb_data:
```

### 1.2 数据库初始化

```bash
# 1. 进入MariaDB容器
docker exec -it mariadb mysql -uroot -p123456

# 2. 创建数据库
CREATE DATABASE IF NOT EXISTS test DEFAULT CHARACTER SET utf8mb4;

# 3. 执行初始化脚本
mysql -uroot -p123456 test < sql.sql
```

2\. 应用配置
--------

### 2.1 开发环境配置(application-dev.yml)

```yaml
spring:
  # MongoDB配置
  data:
    mongodb:
      uri: mongodb://root:123456@localhost:27017/test?authSource=admin

  # Redis配置
  redis:
    password: 123456
    timeout: 5000
    cluster:
      nodes:
        - localhost:6379

  # RabbitMQ配置
  rabbitmq:
    host: localhost
    port: 5672
    username: admin
    password: 123456

# 日志配置
logging:
  level:
    com.study.collect: debug
  file:
    name: logs/collect.log

# 采集任务配置
collect:
  task:
    thread-pool:
      core-size: 5
      max-size: 10
    execution:
      retry-times: 3
```

### 2.2 任务配置示例

```sql
-- 插入企业采集任务配置
INSERT INTO task_config (
    task_code, task_name, task_handler,
    cron_expr, shard_total, status, task_param
) VALUES (
    'enterprise_collect',
    '企业数据采集任务',
    'enterpriseCollector',
    '0 0/30 * * * ?',
    4,
    1,
    '{"batchSize":100,"timeout":3600}'
);
```

3\. 启动服务
--------

### 3.1 启动检查清单

```java
@Slf4j
@Component
public class StartupChecker {
    
    @PostConstruct
    public void check() {
        checkMongoDB();
        checkRedis();
        checkRabbitMQ();
        checkMariaDB();
        loadTaskConfigs();
    }
    
    private void checkMongoDB() {
        try {
            mongoTemplate.executeCommand("{ ping: 1 }");
            log.info("MongoDB连接成功");
        } catch (Exception e) {
            log.error("MongoDB连接失败", e);
            throw e;
        }
    }
    
    // 其他中间件检查类似...
}
```

### 3.2 启动命令

```bash
# 开发环境启动
java -jar collect-starter.jar --spring.profiles.active=dev

# 指定JVM参数
java -Xms1g -Xmx2g -jar collect-starter.jar --spring.profiles.active=dev
```

4\. 功能测试
--------

### 4.1 测试数据生成

```java
@Test
public void testGenerateData() {
    EnterpriseGenerateRequest request = new EnterpriseGenerateRequest();
    request.setStartCode(1000);
    request.setCount(10);
    request.setIndustry("制造业");
    
    List<String> codes = enterpriseService.generateEnterprises(
        request.getStartCode(),
        request.getCount(),
        request.getIndustry(),
        request.getRegAuthority()
    );
    
    assertThat(codes).hasSize(10);
}
```

### 4.2 API测试脚本

```bash
#!/bin/bash

# 1. 生成测试数据
curl -X POST http://localhost:8080/api/enterprise/generate \
-H "Content-Type: application/json" \
-d '{
    "startCode": 1000,
    "count": 10,
    "industry": "制造业"
}'

# 2. 触发采集任务
TASK_ID=$(curl -X POST "http://localhost:8080/api/enterprise/collect" | jq -r '.data')

# 3. 轮询检查任务状态
while true; do
    STATUS=$(curl -s "http://localhost:8080/api/enterprise/progress/${TASK_ID}" | jq -r '.data.status')
    if [ "$STATUS" == "SUCCESS" ] || [ "$STATUS" == "FAILED" ]; then
        break
    fi
    sleep 5
done

# 4. 查询结果
curl "http://localhost:8080/api/enterprise/page?pageNum=1&pageSize=10"
```

### 4.3 任务执行监控

```java
@RestController
@RequestMapping("/monitor")
public class MonitorController {

    @GetMapping("/tasks/running")
    public List<TaskInstance> getRunningTasks() {
        return taskExecuteService.getRunningTasks();
    }
    
    @GetMapping("/tasks/stats")
    public Map<String, Object> getTaskStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("runningCount", getRunningTaskCount());
        stats.put("completedCount", getCompletedTaskCount());
        stats.put("failedCount", getFailedTaskCount());
        return stats;
    }
}
```

需要我详细讲解某个具体部分，或继续讲解测试结果验证和问题排查部分吗？

## Prompt:
继续

## Response:
我来讲解测试结果验证和问题排查部分。

5\. 结果验证
--------

### 5.1 数据验证

1.  MongoDB数据检查

```javascript
// 连接MongoDB
use test

// 查看采集的企业数据
db.enterprise.find().pretty()

// 检查特定企业
db.enterprise.findOne({"code": "1000"})

// 统计数量
db.enterprise.countDocuments({"industry": "制造业"})

// 检查版本
db.enterprise.find({}, {code: 1, versionCode: 1, updateTime: 1})
```

2.  任务执行记录检查

```sql
-- 查看任务实例
SELECT * FROM task_instance 
WHERE task_code = 'enterprise_collect' 
ORDER BY create_time DESC;

-- 查看执行日志
SELECT * FROM task_log 
WHERE task_code = 'enterprise_collect'
ORDER BY create_time;

-- 统计任务执行情况
SELECT status, COUNT(*) 
FROM task_instance 
WHERE task_code = 'enterprise_collect'
GROUP BY status;
```

### 5.2 缓存验证

```java
@Service
@Slf4j
public class CacheValidator {
    private final RedisTemplate<String, Object> redisTemplate;
    
    public void validateCache() {
        // 检查企业缓存
        Set<String> keys = redisTemplate.keys("enterprise:*");
        log.info("企业缓存数量: {}", keys.size());
        
        // 检查缓存数据完整性
        keys.forEach(key -> {
            Object value = redisTemplate.opsForValue().get(key);
            if (value == null) {
                log.error("缓存数据缺失: {}", key);
            }
        });
    }
}
```

6\. 问题排查
--------

### 6.1 日志分析

```bash
# 1. 查看应用日志
tail -f logs/collect.log | grep ERROR

# 2. 查看特定任务的日志
grep "enterprise_collect" logs/collect.log

# 3. 提取错误堆栈
awk '/Exception/{p=NR+10}(NR<=p){print}' logs/collect.log
```

### 6.2 常见问题处理

1.  任务不执行

```java
public class TaskTroubleshooter {
    
    public void diagnoseTask(String taskCode) {
        // 1. 检查任务配置
        TaskConfig config = taskConfigMapper.selectByCode(taskCode);
        if (config == null) {
            log.error("任务配置不存在");
            return;
        }
        
        // 2. 验证cron表达式
        try {
            new CronExpression(config.getCronExpr());
        } catch (Exception e) {
            log.error("cron表达式无效: {}", config.getCronExpr());
            return;
        }
        
        // 3. 检查任务状态
        if (config.getStatus() != 1) {
            log.error("任务未启用");
            return;
        }
        
        // 4. 检查调度器状态
        if (!taskScheduler.isTaskScheduled(taskCode)) {
            log.error("任务未被调度");
        }
    }
}
```

2.  数据不一致问题

```java
@Service
public class DataConsistencyChecker {
    
    public void checkConsistency(String enterpriseCode) {
        // 1. 检查MongoDB数据
        Enterprise enterprise = mongoTemplate.findOne(
            Query.query(Criteria.where("code").is(enterpriseCode)),
            Enterprise.class
        );
        
        // 2. 检查缓存数据
        String cacheKey = "enterprise:detail:" + enterpriseCode;
        Enterprise cached = (Enterprise) redisTemplate.opsForValue().get(cacheKey);
        
        // 3. 比对版本
        if (enterprise != null && cached != null &&
            !enterprise.getVersionCode().equals(cached.getVersionCode())) {
            log.error("数据版本不一致: MongoDB={}, Redis={}",
                enterprise.getVersionCode(), cached.getVersionCode());
            
            // 清除缓存
            redisTemplate.delete(cacheKey);
        }
    }
}
```

3.  性能问题排查

```java
@Aspect
@Component
@Slf4j
public class PerformanceMonitor {
    
    @Around("@annotation(org.springframework.scheduling.annotation.Scheduled)")
    public Object monitorTaskPerformance(ProceedingJoinPoint pjp) throws Throwable {
        StopWatch sw = new StopWatch();
        sw.start();
        
        try {
            return pjp.proceed();
        } finally {
            sw.stop();
            
            // 记录执行时间
            if (sw.getTotalTimeMillis() > 10000) { // 超过10秒
                log.warn("任务执行时间过长: {}, cost={}ms",
                    pjp.getSignature().getName(),
                    sw.getTotalTimeMillis());
                
                // 发送告警
                alertService.sendAlert(
                    "任务执行超时",
                    String.format("任务[%s]执行耗时%dms",
                        pjp.getSignature().getName(),
                        sw.getTotalTimeMillis())
                );
            }
        }
    }
}
```

### 6.3 监控指标

```java
@Configuration
public class MetricsConfig {
    
    @Bean
    MeterRegistry registry() {
        return new SimpleMeterRegistry();
    }
    
    @Bean
    TaskMetrics taskMetrics(MeterRegistry registry) {
        return new TaskMetrics(registry);
    }
}

@Component
public class TaskMetrics {
    private final Counter taskStartCounter;
    private final Counter taskCompleteCounter;
    private final Counter taskErrorCounter;
    private final Timer taskExecutionTimer;
    
    public TaskMetrics(MeterRegistry registry) {
        this.taskStartCounter = registry.counter("task.starts");
        this.taskCompleteCounter = registry.counter("task.completes");
        this.taskErrorCounter = registry.counter("task.errors");
        this.taskExecutionTimer = registry.timer("task.execution.time");
    }
    
    public void recordTaskExecution(String taskCode, long timeMs) {
        taskExecutionTimer.record(timeMs, TimeUnit.MILLISECONDS);
    }
}
```

需要我进一步详细讲解某个部分，或者补充其他内容吗？

