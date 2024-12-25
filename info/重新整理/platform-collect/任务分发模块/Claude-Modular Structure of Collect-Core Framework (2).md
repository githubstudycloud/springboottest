## Prompt:
根据文件代码简述collect-core分为几个模块

## Response:
根据代码结构，collect-core模块主要分为以下几个核心模块：

1.  Collector模块（collector包）

*   负责数据采集的核心功能
*   包含采集器接口(ICollector)、抽象基类(AbstractCollector)
*   提供工厂类和管理器来创建和管理采集器
*   支持注解配置和自动注册机制

2.  Processor模块（processor包）

*   负责数据处理的核心功能
*   包含处理器接口(IProcessor)、抽象基类(AbstractProcessor)
*   提供处理器链来组合多个处理器
*   支持处理器的排序和动态组装

3.  Storage模块（storage包）

*   负责数据存储的基础设施层
*   包含MongoDB和Redis存储实现
*   提供缓存注解和分布式锁支持
*   实现实体审计和版本管理

4.  Task模块（task包）

*   负责任务调度和执行
*   包含任务定义、实例和日志管理
*   提供调度器和执行器的实现
*   支持任务分片和重试机制

5.  MQ模块（mq包）

*   负责消息队列集成
*   包含消息生产者和消费者
*   处理任务消息和结果回调
*   提供队列配置和管理

这些模块构成了一个完整的数据采集框架，每个模块负责特定的功能并相互协作。模块间通过接口解耦,便于扩展和维护。

## Prompt:
告诉我代码里配置redis，mysql，mysql的dao和xml的映射，以及mongodb，rabbitmq等配置是否在collectAutoconfiguration中正确配置启用，逐个检查这些类，修改并完整输出，请先列出相关文件，然后按顺序来

## Response:
让我们逐一检查数据存储和消息队列相关的配置。首先列出需要检查的相关文件：

1.  配置类：

*   CollectAutoConfiguration.java (核心配置类)
*   MongoConfig.java (MongoDB配置)
*   CacheAutoConfiguration.java (Redis配置)
*   MyBatisConfig.java (MyBatis配置)
*   RabbitConfig.java (RabbitMQ配置)

2.  配置属性类：

*   CacheProperties.java (Redis属性)
*   MQProperties.java (RabbitMQ属性)

3.  Mapper和XML映射:

*   TaskConfigMapper.java 和 TaskConfigMapper.xml
*   TaskInstanceMapper.java 和 TaskInstanceMapper.xml
*   TaskLogMapper.java 和 TaskLogMapper.xml

让我们先看看当前的 CollectAutoConfiguration：

```java
@Configuration
@ComponentScan("com.study.collect.core")
@Import({
    MongoConfig.class,
    MyBatisConfig.class,
//    MQProperties.class,  // 这里有问题
    CacheAutoConfiguration.class, 
    TaskConfiguration.class,
    CollectorConfiguration.class,
    ProcessorConfiguration.class
})
public class CollectAutoConfiguration {
}
```

需要修改的问题：

1.  缺少对RabbitConfig的导入
2.  MQProperties被注释掉了
3.  缺少必要的条件注解来控制配置的启用

让我修改并输出正确的配置类：

Corrected Core Configuration Classes

Click to open code

主要修改说明：

1.  CollectAutoConfiguration:

*   添加了@EnableConfigurationProperties注解启用配置属性类
*   正确导入了所有基础设施配置类
*   组织了更清晰的导入结构

2.  MyBatisConfig:

*   添加了条件注解控制启用
*   增加了SqlSessionFactory的完整配置
*   配置了XML映射文件路径和实体类别名

3.  MongoConfig:

*   添加了条件注解控制启用
*   完整配置了MongoDB的连接和仓库设置

4.  CacheAutoConfiguration (Redis):

*   添加了条件注解控制启用
*   完善了RedisTemplate的序列化配置

5.  RabbitConfig:

*   添加了条件注解控制启用
*   完整配置了交换机、队列和绑定关系
*   配置了消息转换器

所有组件现在都添加了：

1.  条件注解(@ConditionalOnProperty)来控制是否启用
2.  完整的配置选项
3.  正确的依赖关系

你需要我详细解释每个配置类的具体修改和配置选项吗？或者需要查看任何特定的配置部分？

## Prompt:
其他几个不用改吧？配置文件该如何写，现在这个core其他模块的代码完整正确吗？请分段输出你的结论和改进方案

## Response:
让我分段分析:

1.  配置类改动结论

```
已修改的配置类已经完善，其他几个配置类不需要改动:
- CollectorConfiguration - 已经正确配置采集器扩展点
- ProcessorConfiguration - 已经正确配置处理器机制 
- TaskConfiguration - 已经正确配置任务调度
```

2.  application.yml 配置建议

```yaml
spring:
  # MySQL配置
  datasource:
    enabled: true  # 启用MySQL
    driver-class-name: org.mariadb.jdbc.Driver
    url: jdbc:mariadb://localhost:3306/collect?useUnicode=true&characterEncoding=utf8
    username: root
    password: root

  # MongoDB配置  
  data:
    mongodb:
      enabled: true
      uri: mongodb://localhost:27017/collect
      database: collect

    # Redis配置
    redis:
      enabled: true
      host: localhost
      port: 6379
      password: 
      database: 0
      lettuce:
        pool:
          max-active: 8
          max-idle: 8
          min-idle: 0
          max-wait: -1

  # RabbitMQ配置
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
    virtual-host: /

# 自定义配置
collect:
  # 消息队列配置
  mq:
    rabbit:
      enabled: true
      task:
        exchange: collect.task
        queue: collect.task.queue
        routing-key: collect.task
      result:
        exchange: collect.result
        queue: collect.result.queue
        routing-key: collect.result
        
  # 缓存配置    
  storage:
    cache:
      enabled: true
      default-expiration: 3600
      time-unit: SECONDS
```

3.  core模块代码检查结果

collector包:

```
- AbstractCollector缺少泛型类型约束
- CollectorManager需要添加错误处理机制
- 建议添加采集器生命周期管理
```

processor包:

```
- ProcessChain缺少异常处理机制
- 建议添加处理器执行超时控制
- 建议增加处理结果验证机制
```

storage包:

```
- BaseMongoRepository缺少乐观锁支持
- 缓存模块需要增加本地缓存支持
- 建议添加存储层切面用于审计
```

task包:

```
- TaskExecuteService缺少任务执行监控
- 建议增加任务执行资源控制
- 需要完善任务重试机制
```

4.  改进建议

核心改进:

```
1. 增加核心功能:
- 添加监控和告警机制
- 添加分布式任务协调
- 完善任务执行生命周期

2. 增强健壮性:
- 添加熔断降级机制
- 完善异常处理体系
- 增加重试补偿机制

3. 提升可维护性:
- 统一配置管理
- 完善日志体系
- 增加运维接口
```

代码改进:

```
1. collector模块:
public abstract class AbstractCollector<P, R> implements ICollector<P, R> {
    添加生命周期方法:
    - init()
    - destroy() 
    添加监控埋点
}

2. processor模块:
public class ProcessChain<T> {
    添加:
    - 超时控制
    - 执行监控
    - 异常处理
}

3. storage模块:
- 添加多级缓存
- 增加分布式锁实现
- 完善审计功能

4. task模块:
- 完善任务执行状态机
- 增加资源控制
- 添加监控指标
```

需要我详细展开任何部分的具体实现吗？

## Prompt:
按顺序按模块一个个讲述

## Response:
好的,让我们从第一个模块开始逐个讲解:

1.  Collector模块改进

首先修改AbstractCollector的基础实现:

```java
public abstract class AbstractCollector<P, R> implements ICollector<P, R> {
    
    private CollectorLifecycle lifecycle = CollectorLifecycle.CREATED;
    private CollectorMetrics metrics;

    @PostConstruct
    public void init() {
        lifecycle = CollectorLifecycle.INITIALIZED;
        metrics = new CollectorMetrics(getType());
        doInit();
    }

    protected void doInit() {
        // 子类初始化
    }

    @Override
    public final CollectResult<R> collect(CollectContext<P> context) {
        String taskId = context.getTaskId();
        metrics.incrementCollectCounter();
        
        try {
            // 前置检查
            preCheck(context);
            
            // 执行采集
            long startTime = System.currentTimeMillis();
            R data = doCollect(context);
            metrics.recordCollectTime(System.currentTimeMillis() - startTime);
            
            // 后置处理
            data = postProcess(data);
            
            metrics.incrementSuccessCounter();
            return CollectResult.success(data);
            
        } catch (Exception e) {
            metrics.incrementFailureCounter();
            log.error("Collect failed: {}", taskId, e);
            return CollectResult.failure(e.getMessage());
        }
    }

    protected void preCheck(CollectContext<P> context) {
        // 参数校验
        validateContext(context);
        
        // 状态检查
        if(lifecycle != CollectorLifecycle.INITIALIZED) {
            throw new CollectException("Collector not initialized");
        }
    }

    @PreDestroy 
    public void destroy() {
        lifecycle = CollectorLifecycle.DESTROYED;
        doDestroy();
    }

    protected void doDestroy() {
        // 子类销毁
    }
}
```

CollectorManager也需要增强:

```java
@Component
public class CollectorManager {

    private final Map<String, ICollector<?,?>> collectors = new ConcurrentHashMap<>();
    private final LoadingCache<String, ICollector<?,?>> cache;
    
    public CollectorManager() {
        cache = CacheBuilder.newBuilder()
            .maximumSize(100)
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .build(new CacheLoader<>() {
                @Override 
                public ICollector<?,?> load(String type) {
                    return createCollector(type);
                }
            });
    }

    @SuppressWarnings("unchecked")
    public <P,R> ICollector<P,R> getCollector(String type) {
        try {
            return (ICollector<P,R>) cache.get(type);
        } catch (ExecutionException e) {
            throw new CollectException("Failed to get collector: " + type, e);
        }
    }

    private ICollector<?,?> createCollector(String type) {
        // 工厂方法创建采集器
        // 注册监控
        // 初始化生命周期
    }
}
```

这样改进后:

1.  增加了生命周期管理
2.  添加了监控指标
3.  增强了缓存机制
4.  完善了异常处理

需要我继续讲解Processor模块的改进吗？

## Prompt:
继续

## Response:
继续讲解Processor模块的改进：

1.  首先增强ProcessChain的实现：

```java
public class ProcessChain<T> {
    private final String chainId;
    private final List<IProcessor<T>> processors;
    private final ProcessorMetrics metrics;
    private final int timeoutSeconds;
    
    // 处理器执行状态
    private enum Status {
        READY, PROCESSING, COMPLETED, FAILED
    }
    
    public ProcessChain(String chainId, int timeoutSeconds) {
        this.chainId = chainId;
        this.processors = new ArrayList<>();
        this.metrics = new ProcessorMetrics(chainId);
        this.timeoutSeconds = timeoutSeconds;
    }

    public T process(T data, ProcessContext context) {
        metrics.startChain();
        Status status = Status.READY;
        
        try {
            // 创建执行上下文
            ProcessContext chainContext = createChainContext(context);
            
            // 超时控制
            CompletableFuture<T> future = CompletableFuture.supplyAsync(() -> {
                status = Status.PROCESSING;
                return executeProcessors(data, chainContext);
            });

            // 等待执行完成或超时
            T result = future.get(timeoutSeconds, TimeUnit.SECONDS);
            status = Status.COMPLETED;
            metrics.completeChain(true);
            return result;

        } catch (TimeoutException e) {
            status = Status.FAILED;
            metrics.completeChain(false);
            throw new ProcessException("Chain execution timeout after " + timeoutSeconds + "s");
            
        } catch (Exception e) {
            status = Status.FAILED;
            metrics.completeChain(false);
            throw new ProcessException("Chain execution failed", e);
            
        } finally {
            // 记录执行状态
            context.setAttribute("chainStatus", status);
        }
    }

    private T executeProcessors(T data, ProcessContext context) {
        for (IProcessor<T> processor : processors) {
            try {
                // 处理器执行计时
                long startTime = System.currentTimeMillis();
                data = processor.process(data, context);
                metrics.recordProcessorTime(processor.getType(), 
                    System.currentTimeMillis() - startTime);
                
            } catch (Exception e) {
                metrics.recordProcessorError(processor.getType());
                throw new ProcessException(
                    "Processor execution failed: " + processor.getType(), e);
            }
        }
        return data;
    }

    // 新增方法:验证处理结果
    private void validateResult(T data, ProcessContext context) {
        if (data == null) {
            throw new ProcessException("Process result cannot be null");
        }
        // 可以添加更多验证逻辑
    }
}
```

2.  增强AbstractProcessor的实现：

```java
public abstract class AbstractProcessor<T> implements IProcessor<T> {
    
    protected final ProcessorMetrics metrics;
    protected final ValidatorFactory validatorFactory;

    public AbstractProcessor() {
        this.metrics = new ProcessorMetrics(getType());
        this.validatorFactory = Validation.buildDefaultValidatorFactory();
    }

    @Override
    public final T process(T data, ProcessContext context) {
        String taskId = context.getTaskId();
        metrics.incrementProcessCounter();
        
        try {
            // 参数验证
            validateInput(data);
            
            // 前置处理
            preProcess(data, context);
            
            // 执行处理
            long startTime = System.currentTimeMillis();
            T result = doProcess(data, context);
            metrics.recordProcessTime(System.currentTimeMillis() - startTime);
            
            // 结果验证
            validateOutput(result);
            
            // 后置处理
            result = postProcess(result, context);
            
            metrics.incrementSuccessCounter();
            return result;
            
        } catch (Exception e) {
            metrics.incrementFailureCounter();
            log.error("Process failed: {}", taskId, e);
            throw new ProcessException("Process failed: " + e.getMessage(), e);
        }
    }

    // 输入验证
    protected void validateInput(T data) {
        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<T>> violations = validator.validate(data);
        if (!violations.isEmpty()) {
            throw new ValidationException(violations);
        }
    }

    // 输出验证
    protected void validateOutput(T result) {
        if (result == null) {
            throw new ProcessException("Process result cannot be null");
        }
        // 子类可以覆盖添加更多验证
    }
}
```

3.  新增ProcessorMetrics监控类：

```java
@Component
public class ProcessorMetrics {
    private final String processorType;
    private final Counter processCounter;
    private final Counter successCounter;
    private final Counter failureCounter;
    private final Timer processTimer;

    public ProcessorMetrics(String processorType) {
        this.processorType = processorType;
        this.processCounter = Metrics.counter("processor.process.total", "type", processorType);
        this.successCounter = Metrics.counter("processor.process.success", "type", processorType);
        this.failureCounter = Metrics.counter("processor.process.failure", "type", processorType);
        this.processTimer = Metrics.timer("processor.process.time", "type", processorType);
    }

    public void incrementProcessCounter() {
        processCounter.increment();
    }

    public void incrementSuccessCounter() {
        successCounter.increment();
    }

    public void incrementFailureCounter() {
        failureCounter.increment();
    }

    public void recordProcessTime(long milliseconds) {
        processTimer.record(Duration.ofMillis(milliseconds));
    }
}
```

主要改进点：

1.  处理链（ProcessChain）:

*   添加了超时控制机制
*   增加了处理器执行状态跟踪
*   完善了异常处理机制
*   添加了执行指标收集

2.  处理器基类（AbstractProcessor）:

*   增加了输入输出验证
*   添加了处理计时和监控
*   完善了异常处理
*   规范了处理生命周期

3.  监控指标（ProcessorMetrics）:

*   处理总数统计
*   成功/失败计数
*   处理时间记录
*   按处理器类型分类

这些改进提供了：

1.  更好的执行控制
2.  更完善的异常处理
3.  更详细的监控指标
4.  更规范的处理流程

需要我继续介绍Storage模块的改进吗？

## Prompt:
继续

## Response:
继续讲解Storage模块的改进：

1.  首先增强BaseMongoRepository的实现：

```java
@Slf4j
public class BaseMongoRepository<T extends BaseEntity>
        extends SimpleMongoRepository<T, String> implements IRepository<T> {

    private final MongoOperations mongoOperations;
    private final MongoEntityInformation<T, String> entityInformation;
    private final CacheManager cacheManager;
    private final ApplicationEventPublisher eventPublisher;

    public BaseMongoRepository(MongoEntityInformation<T, String> metadata,
                             MongoOperations mongoOperations,
                             CacheManager cacheManager,
                             ApplicationEventPublisher eventPublisher) {
        super(metadata, mongoOperations);
        this.mongoOperations = mongoOperations;
        this.entityInformation = metadata;
        this.cacheManager = cacheManager;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public <S extends T> S save(S entity) {
        try {
            // 发布保存前事件
            eventPublisher.publishEvent(new EntityEvents.BeforeSaveEvent<>(entity));
            
            // 乐观锁控制
            if (entity.getVersion() != null) {
                Query query = Query.query(Criteria.where("id").is(entity.getId())
                        .and("version").is(entity.getVersion()));
                Update update = getUpdateFromEntity(entity);
                update.inc("version", 1);
                
                FindAndModifyOptions options = new FindAndModifyOptions()
                        .returnNew(true)
                        .upsert(false);
                
                S saved = mongoOperations.findAndModify(query, update, options, 
                        (Class<S>) entity.getClass());
                
                if (saved == null) {
                    throw new OptimisticLockingFailureException(
                        "Version conflict - entity has been modified");
                }
                
                // 清除缓存
                clearCache(entity);
                
                // 发布保存后事件
                eventPublisher.publishEvent(new EntityEvents.AfterSaveEvent<>(saved));
                
                return saved;
            }
            
            // 无版本控制的保存
            return super.save(entity);
            
        } catch (Exception e) {
            log.error("Failed to save entity: {}", entity.getId(), e);
            throw new StorageException("Save failed", e);
        }
    }

    @Override
    @Cacheable(key = "#code", unless = "#result == null")
    public T findByCode(String code) {
        try {
            Query query = Query.query(
                    Criteria.where("code").is(code)
                            .and("deleted").is(false)
            );
            return mongoOperations.findOne(query, entityInformation.getJavaType());
        } catch (Exception e) {
            log.error("Failed to find entity by code: {}", code, e);
            throw new StorageException("Query failed", e);
        }
    }

    @Override
    @Transactional
    public void softDelete(String id) {
        try {
            T entity = findById(id).orElse(null);
            if (entity != null) {
                // 发布删除前事件
                eventPublisher.publishEvent(new EntityEvents.BeforeDeleteEvent<>(entity));
                
                Update update = Update.update("deleted", true)
                        .set("updateTime", LocalDateTime.now());
                
                Query query = Query.query(Criteria.where("id").is(id));
                mongoOperations.updateFirst(query, update, 
                        entityInformation.getJavaType());
                
                // 清除缓存
                clearCache(entity);
                
                // 发布删除后事件
                eventPublisher.publishEvent(new EntityEvents.AfterDeleteEvent<>(entity));
            }
        } catch (Exception e) {
            log.error("Failed to soft delete entity: {}", id, e);
            throw new StorageException("Soft delete failed", e);
        }
    }

    private void clearCache(T entity) {
        try {
            String cacheKey = generateCacheKey(entity);
            cacheManager.getCache("entities").evict(cacheKey);
        } catch (Exception e) {
            log.warn("Failed to clear cache for entity: {}", entity.getId(), e);
        }
    }
}
```

2.  增强缓存管理，添加多级缓存支持：

```java
@Component
public class MultiLevelCacheManager implements CacheManager {

    private final LocalCache localCache;  // Caffeine本地缓存
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public <T> T get(String key, Class<T> type, Supplier<T> supplier) {
        // 先查本地缓存
        T value = localCache.get(key);
        if (value != null) {
            return value;
        }

        // 查Redis缓存
        value = getFromRedis(key, type);
        if (value != null) {
            // 回填本地缓存
            localCache.put(key, value);
            return value;
        }

        // 从数据源加载
        value = supplier.get();
        if (value != null) {
            // 同时写入本地缓存和Redis
            localCache.put(key, value);
            putToRedis(key, value);
        }

        return value;
    }

    @Override
    public <T> void put(String key, T value, CacheOptions options) {
        // 同时写入本地缓存和Redis
        localCache.put(key, value, options);
        putToRedis(key, value, options);
    }

    @Override 
    public void remove(String key) {
        // 同时清除本地缓存和Redis
        localCache.remove(key);
        redisTemplate.delete(key);
    }
}
```

3.  添加审计切面：

```java
@Aspect
@Component
@RequiredArgsConstructor
public class StorageAuditAspect {

    private final AuditLogRepository auditLogRepository;

    @Around("@annotation(audit)")
    public Object doAudit(ProceedingJoinPoint point, Audit audit) throws Throwable {
        String operation = audit.operation();
        String operator = SecurityContextHolder.getContext().getAuthentication().getName();
        
        AuditLog log = new AuditLog();
        log.setOperation(operation);
        log.setOperator(operator);
        log.setStartTime(LocalDateTime.now());
        
        try {
            Object result = point.proceed();
            
            // 记录成功审计
            log.setStatus("SUCCESS");
            log.setEndTime(LocalDateTime.now());
            auditLogRepository.save(log);
            
            return result;
            
        } catch (Exception e) {
            // 记录失败审计
            log.setStatus("FAILED");
            log.setErrorMessage(e.getMessage());
            log.setEndTime(LocalDateTime.now());
            auditLogRepository.save(log);
            
            throw e;
        }
    }
}
```

主要改进点：

1.  BaseMongoRepository:

*   添加了乐观锁支持
*   完善了事务控制
*   增加了事件发布
*   规范了异常处理

2.  多级缓存:

*   本地缓存(Caffeine)
*   分布式缓存(Redis)
*   缓存一致性保证
*   缓存更新策略

3.  审计功能:

*   操作审计记录
*   关键字段变更记录
*   异常操作跟踪
*   审计日志持久化

这些改进提供了：

1.  更强的数据一致性保证
2.  更高的查询性能
3.  更完善的审计追踪
4.  更好的可维护性

需要我继续介绍Task模块的改进吗？

## Prompt:
继续

## Response:
继续讲解Task模块的改进：

1.  首先增强TaskExecuteService的实现：

```java
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskExecuteService {

    private final TaskInstanceMapper taskInstanceMapper;
    private final TaskLogMapper taskLogMapper;
    private final TaskConfigService taskConfigService;
    private final ResourceManager resourceManager;  // 新增资源管理器
    private final ExecutorMetrics metrics;         // 新增监控指标
    private final StateMachineFactory stateMachineFactory;  // 新增状态机工厂

    @Transactional(rollbackFor = Exception.class)
    public TaskInstance createTaskInstance(String taskCode, Integer shardIndex, String shardParam) {
        // 资源检查和预留
        if (!resourceManager.acquireResources(taskCode)) {
            throw new ResourceNotAvailableException("Insufficient resources for task: " + taskCode);
        }

        try {
            // 创建状态机
            StateMachine<TaskStatusEnum, TaskEventEnum> stateMachine = 
                stateMachineFactory.createStateMachine(taskCode);

            // 创建任务实例
            TaskInstance instance = new TaskInstance();
            instance.setInstanceId(InstanceIdGenerator.generateInstanceId(taskCode));
            instance.setTaskCode(taskCode);
            instance.setShardIndex(shardIndex);
            instance.setShardTotal(calculateShardTotal(taskCode));
            instance.setShardParam(shardParam);
            instance.setStatus(TaskStatusEnum.INIT.getCode());
            instance.setHostName(NetUtil.getLocalHostname());
            instance.setStartTime(LocalDateTime.now());

            // 保存实例
            taskInstanceMapper.insert(instance);
            
            // 发送状态转换事件
            stateMachine.sendEvent(TaskEventEnum.START);
            
            // 记录监控指标
            metrics.recordTaskStart(taskCode);
            
            // 记录操作日志
            recordTaskLog(instance.getInstanceId(), taskCode, 
                LogTypeEnum.START, "Task started");

            return instance;

        } catch (Exception e) {
            // 释放资源
            resourceManager.releaseResources(taskCode);
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void completeTaskInstance(String instanceId, boolean success, String errorMsg) {
        TaskInstance instance = getTaskInstance(instanceId);
        
        try {
            // 获取状态机
            StateMachine<TaskStatusEnum, TaskEventEnum> stateMachine = 
                stateMachineFactory.getStateMachine(instance.getTaskCode());

            // 更新状态
            updateInstanceStatus(instance, success, errorMsg, stateMachine);
            
            // 释放资源
            resourceManager.releaseResources(instance.getTaskCode());
            
            // 记录监控指标
            if (success) {
                metrics.recordTaskSuccess(instance.getTaskCode());
            } else {
                metrics.recordTaskFailure(instance.getTaskCode());
            }

        } catch (Exception e) {
            log.error("Failed to complete task instance: {}", instanceId, e);
            metrics.recordTaskError(instance.getTaskCode());
            throw e;
        }
    }

    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public void retryTask(String instanceId) {
        TaskInstance instance = getTaskInstance(instanceId);
        
        // 检查重试次数
        if (instance.getRetryCount() >= getMaxRetryCount(instance.getTaskCode())) {
            handleMaxRetriesExceeded(instance);
            return;
        }

        try {
            // 更新重试信息
            updateRetryInfo(instance);
            
            // 重新执行任务
            executeTask(instance);
            
        } catch (Exception e) {
            log.error("Task retry failed: {}", instanceId, e);
            handleRetryFailure(instance, e);
        }
    }

    private void executeTask(TaskInstance instance) {
        // 获取补偿策略
        CompensationStrategy strategy = getCompensationStrategy(instance.getTaskCode());
        
        // 执行补偿逻辑
        strategy.compensate(instance);
    }
}
```

2.  新增资源管理器：

```java
@Component
public class ResourceManager {

    private final LoadingCache<String, ResourceQuota> quotaCache;
    private final ConcurrentMap<String, Semaphore> resourceLocks;

    public ResourceManager() {
        this.quotaCache = CacheBuilder.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build(new CacheLoader<>() {
                @Override
                public ResourceQuota load(String taskCode) {
                    return loadQuotaConfig(taskCode);
                }
            });
        this.resourceLocks = new ConcurrentHashMap<>();
    }

    public boolean acquireResources(String taskCode) {
        try {
            ResourceQuota quota = quotaCache.get(taskCode);
            Semaphore semaphore = resourceLocks.computeIfAbsent(taskCode,
                k -> new Semaphore(quota.getMaxConcurrent()));

            return semaphore.tryAcquire(quota.getTimeoutSeconds(), TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Failed to acquire resources for task: {}", taskCode, e);
            return false;
        }
    }

    public void releaseResources(String taskCode) {
        Semaphore semaphore = resourceLocks.get(taskCode);
        if (semaphore != null) {
            semaphore.release();
        }
    }
}
```

3.  新增任务状态机：

```java
@Component
public class TaskStateMachineFactory {

    private final StateMachineFactory<TaskStatusEnum, TaskEventEnum> factory;

    public TaskStateMachineFactory() {
        StateMachineBuilder.Builder<TaskStatusEnum, TaskEventEnum> builder = 
            StateMachineBuilder.builder();

        builder.configureStates()
            .withStates()
            .initial(TaskStatusEnum.INIT)
            .states(EnumSet.allOf(TaskStatusEnum.class));

        builder.configureTransitions()
            // 初始化->运行
            .withExternal()
                .source(TaskStatusEnum.INIT)
                .target(TaskStatusEnum.RUNNING)
                .event(TaskEventEnum.START)
                .and()
            // 运行->成功
            .withExternal()
                .source(TaskStatusEnum.RUNNING)
                .target(TaskStatusEnum.SUCCESS)
                .event(TaskEventEnum.SUCCEED)
                .and()
            // 运行->失败
            .withExternal()
                .source(TaskStatusEnum.RUNNING)
                .target(TaskStatusEnum.FAILED)
                .event(TaskEventEnum.FAIL)
                .and()
            // 失败->重试
            .withExternal()
                .source(TaskStatusEnum.FAILED)
                .target(TaskStatusEnum.RUNNING)
                .event(TaskEventEnum.RETRY);

        this.factory = builder.build();
    }

    public StateMachine<TaskStatusEnum, TaskEventEnum> createStateMachine(String taskCode) {
        return factory.getStateMachine(taskCode);
    }
}
```

4.  新增补偿策略：

```java
public interface CompensationStrategy {
    void compensate(TaskInstance instance);
}

@Component
public class DefaultCompensationStrategy implements CompensationStrategy {
    
    @Override
    public void compensate(TaskInstance instance) {
        // 1. 检查任务状态
        // 2. 执行补偿逻辑
        // 3. 记录补偿结果
    }
}
```

主要改进点：

1.  任务执行服务：

*   增加资源管理
*   添加重试机制
*   完善状态管理
*   增加监控指标

2.  资源管理：

*   资源配额控制
*   并发度限制
*   超时控制
*   资源释放保证

3.  状态机：

*   规范状态流转
*   事件驱动转换
*   状态持久化
*   监控和追踪

4.  补偿机制：

*   失败重试策略
*   补偿执行控制
*   结果验证
*   监控告警

这些改进提供了：

1.  更可靠的任务执行
2.  更完善的资源控制
3.  更清晰的状态管理
4.  更强的容错能力

至此，我们完成了所有核心模块的改进。这些改进：

1.  增强了系统的可靠性
2.  提高了系统的可维护性
3.  增强了系统的可扩展性
4.  提供了更好的监控和运维能力

需要我详细解释任何部分吗？

## Prompt:
需要，你给的代码都不完整，请重新从第一个模块开始给出完整的新增修改代码和说明

## Response:
好的，让我从Collector模块开始，给出完整的新增和修改代码。

首先是Collector模块的核心改进，涉及以下文件：

1.  先定义采集器生命周期枚举：

```java
package com.study.collect.core.collector.constants;

public enum CollectorLifecycle {
    CREATED,       // 创建完成
    INITIALIZED,   // 初始化完成
    RUNNING,       // 运行中
    STOPPED,       // 已停止  
    DESTROYED      // 已销毁
}
```

2.  采集器监控指标类：

```java
package com.study.collect.core.collector.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
public class CollectorMetrics {

    private final String collectorType;
    private final Counter collectCounter;
    private final Counter successCounter; 
    private final Counter failureCounter;
    private final Timer collectTimer;

    public CollectorMetrics(MeterRegistry registry, String collectorType) {
        this.collectorType = collectorType;
        
        // 统计采集总次数
        this.collectCounter = Counter.builder("collector.collect.total")
            .tag("type", collectorType)
            .description("Total number of collections")
            .register(registry);

        // 统计成功次数
        this.successCounter = Counter.builder("collector.collect.success")
            .tag("type", collectorType) 
            .description("Number of successful collections")
            .register(registry);

        // 统计失败次数
        this.failureCounter = Counter.builder("collector.collect.failure")
            .tag("type", collectorType)
            .description("Number of failed collections")
            .register(registry);

        // 统计采集耗时
        this.collectTimer = Timer.builder("collector.collect.time")
            .tag("type", collectorType)
            .description("Collection execution time")
            .register(registry);
    }

    public void incrementCollectCounter() {
        try {
            collectCounter.increment();
        } catch (Exception e) {
            log.error("Failed to increment collect counter", e);
        }
    }

    public void incrementSuccessCounter() {
        try {
            successCounter.increment();
        } catch (Exception e) {
            log.error("Failed to increment success counter", e);
        }
    }

    public void incrementFailureCounter() {
        try {
            failureCounter.increment();
        } catch (Exception e) {
            log.error("Failed to increment failure counter", e);
        }
    }

    public void recordCollectTime(long milliseconds) {
        try {
            collectTimer.record(Duration.ofMillis(milliseconds));
        } catch (Exception e) {
            log.error("Failed to record collect time", e);
        }
    }

    // 获取统计数据
    public CollectorStats getStats() {
        return CollectorStats.builder()
            .collectorType(collectorType)
            .totalCount((long) collectCounter.count())
            .successCount((long) successCounter.count())
            .failureCount((long) failureCounter.count())
            .avgTime(collectTimer.mean(java.util.concurrent.TimeUnit.MILLISECONDS))
            .build();
    }
}
```

3.  采集统计数据类：

```java
package com.study.collect.core.collector.metrics;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CollectorStats {
    private String collectorType;    // 采集器类型
    private Long totalCount;         // 采集总次数
    private Long successCount;       // 成功次数  
    private Long failureCount;       // 失败次数
    private Double avgTime;          // 平均耗时(ms)
}
```

4.  修改采集上下文，增加更多控制参数：

```java
package com.study.collect.core.collector.model;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class CollectContext<T> {
    
    // 基础参数
    private String taskId;           // 任务ID
    private T params;               // 采集参数 
    private Integer shardingId;      // 分片ID
    private Integer shardingTotal;   // 总分片数
    
    // 执行控制
    private Integer timeout;         // 超时时间(秒)
    private Integer retryTimes;      // 重试次数
    private boolean async;          // 是否异步执行
    
    // 运行时参数
    private LocalDateTime startTime; // 开始时间
    private String hostName;        // 执行机器
    private Map<String, Object> attributes; // 运行时属性

    public CollectContext() {
        this.startTime = LocalDateTime.now();
        this.attributes = new ConcurrentHashMap<>();
    }

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <V> V getAttribute(String key) {
        return (V) attributes.get(key);
    }

    public void removeAttribute(String key) {
        attributes.remove(key);
    }

    public void clearAttributes() {
        attributes.clear();
    }
}
```

5.  完整的AbstractCollector改进实现：

```java
package com.study.collect.core.collector;

import com.study.collect.core.collector.constants.CollectorLifecycle;
import com.study.collect.core.collector.exception.CollectException;
import com.study.collect.core.collector.metrics.CollectorMetrics;
import com.study.collect.core.collector.model.CollectContext;
import com.study.collect.core.collector.model.CollectResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.concurrent.*;

@Slf4j
public abstract class AbstractCollector<P, R> implements ICollector<P, R> {

    private volatile CollectorLifecycle lifecycle = CollectorLifecycle.CREATED;
    
    @Autowired
    private CollectorMetrics metrics;
    
    private ThreadPoolExecutor executor;

    @PostConstruct
    public void init() {
        try {
            // 初始化线程池
            initExecutor();
            
            // 执行子类初始化
            doInit();
            
            // 更新状态
            lifecycle = CollectorLifecycle.INITIALIZED;
            log.info("Collector initialized: type={}", getType());
            
        } catch (Exception e) {
            log.error("Failed to initialize collector: type={}", getType(), e);
            lifecycle = CollectorLifecycle.DESTROYED;
            throw new CollectException("Collector initialization failed", e);
        }
    }

    protected void initExecutor() {
        executor = new ThreadPoolExecutor(
            5, 10, 60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(100),
            new ThreadFactoryBuilder()
                .setNameFormat("collector-" + getType() + "-%d")
                .build(),
            new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    protected void doInit() {
        // 子类初始化逻辑
    }

    @Override
    public final CollectResult<R> collect(CollectContext<P> context) {
        // 前置检查
        preCheck(context);
        
        String taskId = context.getTaskId();
        metrics.incrementCollectCounter();
        
        try {
            // 是否异步执行
            if (context.isAsync()) {
                return asyncCollect(context);
            }
            
            // 同步执行
            return syncCollect(context);
            
        } catch (Exception e) {
            metrics.incrementFailureCounter();
            log.error("Collection failed: taskId={}", taskId, e);
            return CollectResult.failure(e.getMessage());
        }
    }

    private CollectResult<R> syncCollect(CollectContext<P> context) {
        try {
            // 执行采集
            long startTime = System.currentTimeMillis();
            R data = doCollect(context);
            metrics.recordCollectTime(System.currentTimeMillis() - startTime);
            
            // 结果处理
            data = postProcess(data);
            
            metrics.incrementSuccessCounter();
            return CollectResult.success(data);
            
        } catch (Exception e) {
            // 重试处理
            if (context.getRetryTimes() > 0) {
                return retry(context);
            }
            throw e;
        }
    }

    private CollectResult<R> asyncCollect(CollectContext<P> context) {
        try {
            Future<CollectResult<R>> future = executor.submit(() -> syncCollect(context));
            
            // 等待执行完成或超时
            return future.get(context.getTimeout(), TimeUnit.SECONDS);
            
        } catch (TimeoutException e) {
            throw new CollectException("Collection timeout after " + context.getTimeout() + "s");
        } catch (Exception e) {
            throw new CollectException("Async collection failed", e);
        }
    }

    private CollectResult<R> retry(CollectContext<P> context) {
        int retryTimes = context.getRetryTimes();
        log.info("Retrying collection: taskId={}, retryTimes={}", 
            context.getTaskId(), retryTimes);
            
        // 更新重试次数
        context.setRetryTimes(retryTimes - 1);
        
        // 重新执行
        return collect(context);
    }

    protected void preCheck(CollectContext<P> context) {
        // 基础参数校验
        if (context == null) {
            throw new CollectException("Context cannot be null");
        }
        if (!StringUtils.hasText(context.getTaskId())) {
            throw new CollectException("TaskId cannot be empty");
        }
        if (context.getParams() == null) {
            throw new CollectException("Params cannot be null");
        }
        
        // 状态检查
        if (lifecycle != CollectorLifecycle.INITIALIZED) {
            throw new CollectException(
                String.format("Collector not initialized, current status: %s", lifecycle));
        }
        
        // 超时时间处理
        if (context.getTimeout() == null || context.getTimeout() <= 0) {
            context.setTimeout(300); // 默认5分钟
        }
    }

    protected abstract R doCollect(CollectContext<P> context);

    protected R postProcess(R data) {
        return data;
    }

    @PreDestroy
    public void destroy() {
        try {
            lifecycle = CollectorLifecycle.STOPPED;
            
            // 关闭线程池
            if (executor != null) {
                executor.shutdown();
                if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            }
            
            // 执行子类销毁
            doDestroy();
            
            lifecycle = CollectorLifecycle.DESTROYED;
            log.info("Collector destroyed: type={}", getType());
            
        } catch (Exception e) {
            log.error("Failed to destroy collector: type={}", getType(), e);
            throw new CollectException("Collector destruction failed", e);
        }
    }

    protected void doDestroy() {
        // 子类销毁逻辑
    }
}
```

主要改进包括：

1.  生命周期管理

*   增加生命周期状态定义
*   规范初始化和销毁流程
*   支持子类扩展点

2.  监控指标

*   采集次数统计
*   成功/失败计数
*   执行时间记录
*   指标导出和查询

3.  执行控制

*   同步/异步执行
*   超时控制
*   重试机制
*   线程池管理

4.  异常处理

*   完善异常体系
*   规范错误处理
*   异常监控和统计

5.  上下文增强

*   更多控制参数
*   运行时属性
*   分片支持
*   执行追踪

需要我继续介绍CollectorManager的完整改进实现吗？

## Prompt:
继续

## Response:
好的，让我们继续完善CollectorManager的实现。首先是所需的一些辅助类：

1.  首先定义采集器注册信息类：

```java
package com.study.collect.core.collector.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CollectorRegistration {
    private String type;             // 采集器类型
    private String description;      // 描述
    private boolean enabled;         // 是否启用
    private int order;              // 优先级顺序
    private ICollector<?, ?> instance; // 采集器实例
}
```

2.  采集器工厂接口：

```java
package com.study.collect.core.collector.factory;

import com.study.collect.core.collector.ICollector;

public interface CollectorFactory {
    /**
     * 创建采集器实例
     * @param type 采集器类型
     * @return 采集器实例
     */
    <P, R> ICollector<P, R> createCollector(String type);
    
    /**
     * 注册采集器
     * @param type 采集器类型
     * @param collector 采集器实例
     */
    void registerCollector(String type, ICollector<?, ?> collector);
    
    /**
     * 移除采集器
     * @param type 采集器类型
     */
    void removeCollector(String type);
}
```

3.  默认采集器工厂实现：

```java
package com.study.collect.core.collector.factory;

import com.study.collect.core.collector.ICollector;
import com.study.collect.core.collector.exception.CollectException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class DefaultCollectorFactory implements CollectorFactory {
    
    private final Map<String, ICollector<?, ?>> collectors = new ConcurrentHashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public <P, R> ICollector<P, R> createCollector(String type) {
        ICollector<?, ?> collector = collectors.get(type);
        if (collector == null) {
            throw new CollectException("No collector found for type: " + type);
        }
        return (ICollector<P, R>) collector;
    }

    @Override
    public void registerCollector(String type, ICollector<?, ?> collector) {
        collectors.put(type, collector);
        log.info("Registered collector: type={}, class={}", 
            type, collector.getClass().getName());
    }

    @Override
    public void removeCollector(String type) {
        collectors.remove(type);
        log.info("Removed collector: type={}", type);
    }
}
```

4.  完整的CollectorManager实现：

```java
package com.study.collect.core.collector.manager;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.study.collect.core.collector.ICollector;
import com.study.collect.core.collector.annotation.Collector;
import com.study.collect.core.collector.exception.CollectException;
import com.study.collect.core.collector.factory.CollectorFactory;
import com.study.collect.core.collector.metrics.CollectorMetrics;
import com.study.collect.core.collector.model.CollectorRegistration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class CollectorManager {

    @Autowired
    private ApplicationContext applicationContext;
    
    @Autowired
    private CollectorFactory collectorFactory;
    
    @Autowired
    private CollectorMetrics metrics;

    // 采集器注册信息缓存
    private final Map<String, CollectorRegistration> registrations = new ConcurrentHashMap<>();
    
    // 采集器实例缓存
    private final LoadingCache<String, ICollector<?, ?>> collectorCache;

    public CollectorManager() {
        this.collectorCache = CacheBuilder.newBuilder()
            .maximumSize(100)
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .recordStats()
            .build(new CacheLoader<>() {
                @Override
                public ICollector<?, ?> load(String type) {
                    return createCollector(type);
                }
            });
    }

    @PostConstruct
    public void init() {
        // 扫描并注册所有采集器
        scanCollectors();
        
        // 输出采集器注册信息
        printCollectorInfo();
    }

    private void scanCollectors() {
        // 获取所有标注@Collector的Bean
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(Collector.class);
        
        beans.values().forEach(bean -> {
            if (!(bean instanceof ICollector)) {
                log.warn("Invalid collector bean: {}", bean.getClass().getName());
                return;
            }

            ICollector<?, ?> collector = (ICollector<?, ?>) bean;
            Collector annotation = bean.getClass().getAnnotation(Collector.class);
            
            // 创建注册信息
            CollectorRegistration registration = CollectorRegistration.builder()
                .type(collector.getType())
                .description(annotation.description())
                .enabled(annotation.enabled())
                .order(annotation.order())
                .instance(collector)
                .build();

            // 注册采集器
            registerCollector(registration);
        });
    }

    private void registerCollector(CollectorRegistration registration) {
        String type = registration.getType();
        
        try {
            // 注册到工厂
            collectorFactory.registerCollector(type, registration.getInstance());
            
            // 保存注册信息
            registrations.put(type, registration);
            
            log.info("Registered collector: type={}, description={}, enabled={}, order={}",
                type, registration.getDescription(), 
                registration.isEnabled(), registration.getOrder());
                
        } catch (Exception e) {
            log.error("Failed to register collector: " + type, e);
        }
    }

    @SuppressWarnings("unchecked")
    public <P, R> ICollector<P, R> getCollector(String type) {
        try {
            // 检查是否启用
            CollectorRegistration registration = registrations.get(type);
            if (registration == null || !registration.isEnabled()) {
                throw new CollectException("Collector not available: " + type);
            }
            
            // 从缓存获取实例
            return (ICollector<P, R>) collectorCache.get(type);
            
        } catch (ExecutionException e) {
            log.error("Failed to get collector: " + type, e);
            throw new CollectException("Failed to get collector: " + type, e);
        }
    }

    private ICollector<?, ?> createCollector(String type) {
        return collectorFactory.createCollector(type);
    }

    public List<CollectorRegistration> listCollectors() {
        return new ArrayList<>(registrations.values());
    }

    public void enableCollector(String type) {
        CollectorRegistration registration = registrations.get(type);
        if (registration != null) {
            registration.setEnabled(true);
            log.info("Enabled collector: {}", type);
        }
    }

    public void disableCollector(String type) {
        CollectorRegistration registration = registrations.get(type);
        if (registration != null) {
            registration.setEnabled(false);
            log.info("Disabled collector: {}", type);
        }
    }

    private void printCollectorInfo() {
        List<CollectorRegistration> sorted = new ArrayList<>(registrations.values());
        sorted.sort(Comparator.comparingInt(CollectorRegistration::getOrder));
        
        log.info("Registered collectors:");
        sorted.forEach(reg -> 
            log.info("- type={}, description={}, enabled={}, order={}",
                reg.getType(), reg.getDescription(), 
                reg.isEnabled(), reg.getOrder())
        );
    }

    // 获取采集器统计信息
    public Map<String, Object> getCollectorStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 缓存统计
        stats.put("cacheStats", collectorCache.stats());
        
        // 采集器统计
        stats.put("collectorCount", registrations.size());
        stats.put("enabledCount", 
            registrations.values().stream()
                .filter(CollectorRegistration::isEnabled)
                .count());
                
        return stats;
    }
}
```

主要改进包括：

1.  注册管理

*   自动扫描注册
*   注册信息管理
*   启用/禁用控制
*   优先级排序

2.  实例管理

*   工厂模式创建
*   缓存机制
*   生命周期管理
*   类型安全

3.  监控统计

*   缓存统计
*   实例统计
*   性能监控
*   运行状态

4.  扩展性

*   工厂接口抽象
*   注册信息扩展
*   监控指标扩展
*   生命周期钩子

这样CollectorManager就能更好地:

1.  管理采集器的整个生命周期
2.  提供更好的扩展性
3.  支持运行时的动态管理
4.  提供完善的监控统计

需要我继续介绍Processor模块的完整改进实现吗？

## Prompt:
继续

## Response:
好的，让我们继续实现Processor模块的完整改进。首先是核心接口和基础类：

1.  首先定义处理器状态枚举：

```java
package com.study.collect.core.processor.constants;

public enum ProcessorStatus {
    CREATED,      // 已创建
    READY,        // 就绪
    PROCESSING,   // 处理中
    COMPLETED,    // 已完成
    FAILED,       // 失败
    SUSPENDED     // 已暂停
}
```

2.  处理器执行监控指标：

```java
package com.study.collect.core.processor.metrics;

import io.micrometer.core.instrument.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class ProcessorMetrics {

    private final String processorType;
    private final Counter processCounter;      // 处理总数
    private final Counter successCounter;      // 成功数
    private final Counter failureCounter;      // 失败数
    private final Timer processTimer;          // 处理耗时
    private final AtomicInteger activeCount;   // 当前活跃数
    
    public ProcessorMetrics(MeterRegistry registry, String processorType) {
        this.processorType = processorType;
        
        // 处理计数器
        this.processCounter = Counter.builder("processor.process.total")
            .tag("type", processorType)
            .description("Total number of processed items")
            .register(registry);
            
        // 成功计数器    
        this.successCounter = Counter.builder("processor.process.success")
            .tag("type", processorType)
            .description("Number of successfully processed items")
            .register(registry);
            
        // 失败计数器
        this.failureCounter = Counter.builder("processor.process.failure")
            .tag("type", processorType)
            .description("Number of processing failures")
            .register(registry);
            
        // 处理耗时
        this.processTimer = Timer.builder("processor.process.time")
            .tag("type", processorType)
            .description("Processing time")
            .register(registry);
            
        // 活跃处理数
        this.activeCount = new AtomicInteger(0);
        Gauge.builder("processor.active.count", activeCount::get)
            .tag("type", processorType)
            .description("Number of active processors")
            .register(registry);
    }

    public void incrementProcessCount() {
        processCounter.increment();
    }
    
    public void incrementSuccessCount() {
        successCounter.increment();
    }
    
    public void incrementFailureCount() {
        failureCounter.increment();
    }
    
    public void recordProcessTime(long milliseconds) {
        processTimer.record(Duration.ofMillis(milliseconds));
    }
    
    public void incrementActiveCount() {
        activeCount.incrementAndGet();
    }
    
    public void decrementActiveCount() {
        activeCount.decrementAndGet();
    }
    
    // 获取指标快照
    public ProcessorStats getStats() {
        return ProcessorStats.builder()
            .processorType(processorType)
            .totalCount((long) processCounter.count())
            .successCount((long) successCounter.count())
            .failureCount((long) failureCounter.count())
            .avgTime(processTimer.mean(java.util.concurrent.TimeUnit.MILLISECONDS))
            .activeCount(activeCount.get())
            .build();
    }
}
```

3.  处理器统计数据类：

```java
package com.study.collect.core.processor.metrics;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProcessorStats {
    private String processorType;    // 处理器类型
    private Long totalCount;        // 处理总数
    private Long successCount;      // 成功数
    private Long failureCount;      // 失败数
    private Double avgTime;         // 平均耗时(ms) 
    private Integer activeCount;    // 当前活跃数
}
```

4.  完善处理上下文：

```java
package com.study.collect.core.processor.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class ProcessContext {
    
    // 基础属性
    private String taskId;          // 任务ID
    private String chainId;         // 处理链ID
    private LocalDateTime startTime; // 开始时间
    
    // 执行控制
    private Integer timeout;        // 超时时间(秒)
    private boolean validateResult; // 是否验证结果
    private boolean asyncProcess;   // 是否异步处理
    
    // 运行时属性
    private Map<String, Object> attributes;  // 属性映射
    private ProcessorStatus status;          // 处理状态
    private String errorMessage;             // 错误信息
    
    public ProcessContext() {
        this.startTime = LocalDateTime.now();
        this.attributes = new ConcurrentHashMap<>();
        this.status = ProcessorStatus.CREATED;
    }
    
    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key) {
        return (T) attributes.get(key);
    }
    
    public void removeAttribute(String key) {
        attributes.remove(key);
    }
    
    public void clearAttributes() {
        attributes.clear();
    }
    
    public void setStatus(ProcessorStatus status, String message) {
        this.status = status;
        this.errorMessage = message;
    }
}
```

5.  验证器接口和默认实现：

```java
package com.study.collect.core.processor.validation;

public interface ResultValidator<T> {
    ValidationResult validate(T result);
}

@Component
public class DefaultResultValidator<T> implements ResultValidator<T> {
    
    @Override
    public ValidationResult validate(T result) {
        if (result == null) {
            return ValidationResult.error("Process result cannot be null");
        }
        return ValidationResult.success();
    }
}

@Data
@Builder
public class ValidationResult {
    private boolean valid;
    private String message;
    
    public static ValidationResult success() {
        return ValidationResult.builder()
            .valid(true)
            .build();
    }
    
    public static ValidationResult error(String message) {
        return ValidationResult.builder()
            .valid(false)
            .message(message)
            .build();
    }
}
```

让我继续介绍处理器的核心实现部分，包括AbstractProcessor的完整改进实现。需要我继续吗？

## Prompt:
继续

## Response:
好的，让我们继续处理器的核心实现部分：

1.  首先是改进后的完整AbstractProcessor实现：

```java
package com.study.collect.core.processor;

import com.study.collect.core.processor.exception.ProcessException;
import com.study.collect.core.processor.metrics.ProcessorMetrics;
import com.study.collect.core.processor.model.ProcessContext;
import com.study.collect.core.processor.validation.ResultValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.concurrent.*;

@Slf4j
public abstract class AbstractProcessor<T> implements IProcessor<T> {

    @Autowired
    protected ProcessorMetrics metrics;
    
    @Autowired
    protected ResultValidator<T> validator;
    
    private ThreadPoolExecutor executor;
    
    @PostConstruct
    public void init() {
        try {
            // 初始化线程池
            initExecutor();
            
            // 初始化子类
            doInit();
            
            log.info("Processor initialized: type={}", getType());
            
        } catch (Exception e) {
            log.error("Failed to initialize processor: type={}", getType(), e);
            throw new ProcessException("Processor initialization failed", e);
        }
    }

    protected void initExecutor() {
        executor = new ThreadPoolExecutor(
            5, 10, 60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(100),
            new ThreadFactoryBuilder()
                .setNameFormat("processor-" + getType() + "-%d")
                .build(),
            new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    protected void doInit() {
        // 子类初始化逻辑
    }

    @Override
    public final T process(T data, ProcessContext context) {
        // 前置检查
        preCheck(data, context);
        
        String taskId = context.getTaskId();
        metrics.incrementProcessCount();
        metrics.incrementActiveCount();
        
        StopWatch watch = new StopWatch();
        watch.start();
        
        try {
            // 是否异步处理
            T result = context.isAsyncProcess() ? 
                asyncProcess(data, context) : syncProcess(data, context);
                
            // 记录执行时间    
            watch.stop();
            metrics.recordProcessTime(watch.getTotalTimeMillis());
            
            // 结果验证
            if (context.isValidateResult()) {
                validateResult(result);
            }
            
            metrics.incrementSuccessCount();
            return result;
            
        } catch (Exception e) {
            metrics.incrementFailureCount();
            log.error("Process failed: taskId={}", taskId, e);
            throw new ProcessException("Process failed: " + e.getMessage(), e);
            
        } finally {
            metrics.decrementActiveCount();
        }
    }

    private T syncProcess(T data, ProcessContext context) {
        try {
            // 更新状态
            context.setStatus(ProcessorStatus.PROCESSING, null);
            
            // 前置处理
            data = preProcess(data, context);
            
            // 执行处理
            T result = doProcess(data, context);
            
            // 后置处理
            result = postProcess(result, context);
            
            // 更新状态
            context.setStatus(ProcessorStatus.COMPLETED, null);
            
            return result;
            
        } catch (Exception e) {
            context.setStatus(ProcessorStatus.FAILED, e.getMessage());
            throw e;
        }
    }

    private T asyncProcess(T data, ProcessContext context) {
        try {
            Future<T> future = executor.submit(() -> syncProcess(data, context));
            return future.get(context.getTimeout(), TimeUnit.SECONDS);
            
        } catch (TimeoutException e) {
            throw new ProcessException("Process timeout after " + context.getTimeout() + "s");
            
        } catch (Exception e) {
            throw new ProcessException("Async process failed", e);
        }
    }

    protected void preCheck(T data, ProcessContext context) {
        // 参数校验
        if (data == null) {
            throw new ProcessException("Process data cannot be null");
        }
        if (context == null) {
            throw new ProcessException("Process context cannot be null");
        }
        
        // 超时设置
        if (context.getTimeout() == null || context.getTimeout() <= 0) {
            context.setTimeout(300); // 默认5分钟
        }
        
        // 状态检查
        if (context.getStatus() == ProcessorStatus.PROCESSING) {
            throw new ProcessException("Processor is already processing");
        }
    }

    protected T preProcess(T data, ProcessContext context) {
        return data;
    }
    
    protected abstract T doProcess(T data, ProcessContext context);
    
    protected T postProcess(T result, ProcessContext context) {
        return result;
    }

    protected void validateResult(T result) {
        ValidationResult validationResult = validator.validate(result);
        if (!validationResult.isValid()) {
            throw new ProcessException("Validation failed: " + validationResult.getMessage());
        }
    }

    @PreDestroy
    public void destroy() {
        try {
            // 关闭线程池
            if (executor != null) {
                executor.shutdown();
                if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            }
            
            // 子类销毁
            doDestroy();
            
            log.info("Processor destroyed: type={}", getType());
            
        } catch (Exception e) {
            log.error("Failed to destroy processor: type={}", getType(), e);
            throw new ProcessException("Processor destruction failed", e);
        }
    }

    protected void doDestroy() {
        // 子类销毁逻辑
    }
}
```

2.  处理器链（ProcessChain）的完整实现：

```java
package com.study.collect.core.processor.chain;

import com.study.collect.core.processor.IProcessor;
import com.study.collect.core.processor.exception.ProcessException;
import com.study.collect.core.processor.metrics.ChainMetrics;
import com.study.collect.core.processor.model.ProcessContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
public class ProcessChain<T> {

    private final String chainId;
    private final List<IProcessor<T>> processors;
    private final ChainMetrics metrics;
    private final ExecutorService executor;

    public ProcessChain(String chainId, ChainMetrics metrics) {
        this.chainId = chainId;
        this.processors = new ArrayList<>();
        this.metrics = metrics;
        this.executor = Executors.newCachedThreadPool();
    }

    public void addProcessor(IProcessor<T> processor) {
        processors.add(processor);
        // 按order排序
        processors.sort(Comparator.comparingInt(IProcessor::getOrder));
    }

    public T process(T data, ProcessContext context) {
        context.setChainId(chainId);
        metrics.startChain();
        
        StopWatch watch = new StopWatch();
        watch.start();
        
        try {
            CompletableFuture<T> future = CompletableFuture.supplyAsync(() -> {
                try {
                    return executeProcessors(data, context);
                } catch (Exception e) {
                    throw new CompletionException(e);
                }
            }, executor);

            // 等待处理完成或超时
            T result = future.get(context.getTimeout(), TimeUnit.SECONDS);
            
            watch.stop();
            metrics.completeChain(true, watch.getTotalTimeMillis());
            
            return result;
            
        } catch (TimeoutException e) {
            watch.stop();
            metrics.completeChain(false, watch.getTotalTimeMillis());
            throw new ProcessException("Chain execution timeout after " + 
                context.getTimeout() + "s");
                
        } catch (Exception e) {
            watch.stop();
            metrics.completeChain(false, watch.getTotalTimeMillis());
            throw new ProcessException("Chain execution failed", e);
        }
    }

    private T executeProcessors(T data, ProcessContext context) {
        T result = data;
        
        for (IProcessor<T> processor : processors) {
            try {
                StopWatch watch = new StopWatch();
                watch.start();
                
                // 执行处理
                result = processor.process(result, context);
                
                watch.stop();
                metrics.recordProcessorTime(processor.getType(), 
                    watch.getTotalTimeMillis());
                
            } catch (Exception e) {
                metrics.recordProcessorError(processor.getType());
                throw new ProcessException(
                    "Processor execution failed: " + processor.getType(), e);
            }
        }
        
        return result;
    }

    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
```

需要我继续介绍处理器管理和监控相关的实现吗？

## Prompt:
继续

## Response:
好的，让我们继续处理器管理和监控相关的实现：

1.  首先是处理器链监控指标类：

```java
package com.study.collect.core.processor.metrics;

import io.micrometer.core.instrument.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class ChainMetrics {

    private final String chainId;
    private final Counter chainCounter;        // 链执行次数
    private final Counter successCounter;      // 成功次数
    private final Counter failureCounter;      // 失败次数
    private final Timer chainTimer;            // 链执行时间
    private final AtomicInteger activeChains;  // 活跃链数
    
    // 处理器级别指标
    private final ConcurrentHashMap<String, Timer> processorTimers;
    private final ConcurrentHashMap<String, Counter> processorErrors;

    public ChainMetrics(MeterRegistry registry, String chainId) {
        this.chainId = chainId;
        this.processorTimers = new ConcurrentHashMap<>();
        this.processorErrors = new ConcurrentHashMap<>();
        
        // 链级别指标
        this.chainCounter = Counter.builder("chain.execute.total")
            .tag("chainId", chainId)
            .description("Total number of chain executions")
            .register(registry);
            
        this.successCounter = Counter.builder("chain.execute.success")
            .tag("chainId", chainId)
            .description("Number of successful chain executions")
            .register(registry);
            
        this.failureCounter = Counter.builder("chain.execute.failure")
            .tag("chainId", chainId)
            .description("Number of failed chain executions")
            .register(registry);
            
        this.chainTimer = Timer.builder("chain.execute.time")
            .tag("chainId", chainId)
            .description("Chain execution time")
            .register(registry);
            
        this.activeChains = new AtomicInteger(0);
        Gauge.builder("chain.active.count", activeChains::get)
            .tag("chainId", chainId)
            .description("Number of active chains")
            .register(registry);
    }
    
    public void startChain() {
        chainCounter.increment();
        activeChains.incrementAndGet();
    }
    
    public void completeChain(boolean success, long milliseconds) {
        try {
            if (success) {
                successCounter.increment();
            } else {
                failureCounter.increment();
            }
            chainTimer.record(java.time.Duration.ofMillis(milliseconds));
        } finally {
            activeChains.decrementAndGet();
        }
    }
    
    public void recordProcessorTime(String processorType, long milliseconds) {
        getOrCreateProcessorTimer(processorType)
            .record(java.time.Duration.ofMillis(milliseconds));
    }
    
    public void recordProcessorError(String processorType) {
        getOrCreateProcessorErrorCounter(processorType).increment();
    }

    private Timer getOrCreateProcessorTimer(String processorType) {
        return processorTimers.computeIfAbsent(processorType, type ->
            Timer.builder("processor.execute.time")
                .tag("chainId", chainId)
                .tag("processorType", type)
                .register(registry));
    }

    private Counter getOrCreateProcessorErrorCounter(String processorType) {
        return processorErrors.computeIfAbsent(processorType, type ->
            Counter.builder("processor.execute.error")
                .tag("chainId", chainId)
                .tag("processorType", type)
                .register(registry));
    }
}
```

2.  ProcessorManager的完整实现：

```java
package com.study.collect.core.processor.manager;

import com.study.collect.core.processor.IProcessor;
import com.study.collect.core.processor.annotation.Processor;
import com.study.collect.core.processor.chain.ProcessChain;
import com.study.collect.core.processor.exception.ProcessException;
import com.study.collect.core.processor.metrics.ChainMetrics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class ProcessorManager {

    @Autowired
    private ApplicationContext applicationContext;
    
    @Autowired
    private MeterRegistry meterRegistry;

    // 处理器注册表
    private final Map<String, ProcessorRegistration> processors = new ConcurrentHashMap<>();
    
    // 处理链缓存
    private final Map<String, ProcessChain<?>> chains = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        // 扫描并注册处理器
        scanProcessors();
        
        // 打印处理器信息
        printProcessorInfo();
    }

    private void scanProcessors() {
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(Processor.class);
        
        beans.values().forEach(bean -> {
            if (!(bean instanceof IProcessor)) {
                log.warn("Invalid processor bean: {}", bean.getClass().getName());
                return;
            }

            IProcessor<?> processor = (IProcessor<?>) bean;
            Processor annotation = bean.getClass().getAnnotation(Processor.class);

            ProcessorRegistration registration = ProcessorRegistration.builder()
                .type(processor.getType())
                .description(annotation.description())
                .enabled(annotation.enabled())
                .order(annotation.order())
                .instance(processor)
                .build();

            registerProcessor(registration);
        });
    }

    private void registerProcessor(ProcessorRegistration registration) {
        String type = registration.getType();
        try {
            processors.put(type, registration);
            log.info("Registered processor: type={}, description={}, enabled={}, order={}",
                type, registration.getDescription(),
                registration.isEnabled(), registration.getOrder());
        } catch (Exception e) {
            log.error("Failed to register processor: " + type, e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> ProcessChain<T> createChain(String chainId) {
        return (ProcessChain<T>) chains.computeIfAbsent(chainId, id -> {
            ProcessChain<T> chain = new ProcessChain<>(id, 
                new ChainMetrics(meterRegistry, id));
            
            // 添加所有启用的处理器
            processors.values().stream()
                .filter(ProcessorRegistration::isEnabled)
                .map(reg -> (IProcessor<T>) reg.getInstance())
                .forEach(chain::addProcessor);
                
            return chain;
        });
    }

    public void removeChain(String chainId) {
        ProcessChain<?> chain = chains.remove(chainId);
        if (chain != null) {
            chain.shutdown();
            log.info("Removed processor chain: {}", chainId);
        }
    }

    public List<ProcessorRegistration> listProcessors() {
        return new ArrayList<>(processors.values());
    }

    public void enableProcessor(String type) {
        ProcessorRegistration reg = processors.get(type);
        if (reg != null) {
            reg.setEnabled(true);
            log.info("Enabled processor: {}", type);
            
            // 刷新所有处理链
            refreshChains();
        }
    }

    public void disableProcessor(String type) {
        ProcessorRegistration reg = processors.get(type);
        if (reg != null) {
            reg.setEnabled(false);
            log.info("Disabled processor: {}", type);
            
            // 刷新所有处理链
            refreshChains();
        }
    }

    private void refreshChains() {
        // 重建所有处理链
        chains.forEach((chainId, chain) -> {
            chain.shutdown();
            createChain(chainId);
        });
    }

    private void printProcessorInfo() {
        List<ProcessorRegistration> sorted = new ArrayList<>(processors.values());
        sorted.sort(Comparator.comparingInt(ProcessorRegistration::getOrder));
        
        log.info("Registered processors:");
        sorted.forEach(reg -> 
            log.info("- type={}, description={}, enabled={}, order={}",
                reg.getType(), reg.getDescription(),
                reg.isEnabled(), reg.getOrder()));
    }

    @PreDestroy
    public void destroy() {
        // 关闭所有处理链
        chains.values().forEach(ProcessChain::shutdown);
        chains.clear();
        
        log.info("ProcessorManager destroyed");
    }

    // 获取处理器和处理链统计信息
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("processorCount", processors.size());
        stats.put("enabledCount", 
            processors.values().stream()
                .filter(ProcessorRegistration::isEnabled)
                .count());
        stats.put("chainCount", chains.size());
        return stats;
    }
}
```

主要改进包括：

1.  性能监控

*   链级别指标
*   处理器级别指标
*   详细的时间统计
*   错误计数和分析

2.  资源管理

*   处理器生命周期
*   处理链缓存
*   线程池管理
*   优雅关闭

3.  动态配置

*   处理器启用/禁用
*   处理链刷新
*   参数调整
*   运行时管理

4.  扩展性

*   灵活的注册机制
*   处理器排序
*   链式处理
*   监控扩展

需要我继续展开任何部分的实现细节吗？

## Prompt:
collectormetrics的collectorType无法自动装配，

## Response:
是的,CollectorMetrics的构造方法需要手动传入collectorType。让我修改一下实现方式：

```java
package com.study.collect.core.collector.metrics;

import io.micrometer.core.instrument.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CollectorMetrics {

    private final MeterRegistry registry;
    private Counter collectCounter;
    private Counter successCounter; 
    private Counter failureCounter;
    private Timer collectTimer;

    @Autowired
    public CollectorMetrics(MeterRegistry registry) {
        this.registry = registry;
    }

    // 初始化方法,由具体采集器调用并传入type
    public void init(String collectorType) {
        // 统计采集总次数
        this.collectCounter = Counter.builder("collector.collect.total")
            .tag("type", collectorType)
            .description("Total number of collections")
            .register(registry);

        // 统计成功次数
        this.successCounter = Counter.builder("collector.collect.success")
            .tag("type", collectorType) 
            .description("Number of successful collections")
            .register(registry);

        // 统计失败次数
        this.failureCounter = Counter.builder("collector.collect.failure")
            .tag("type", collectorType)
            .description("Number of failed collections")
            .register(registry);

        // 统计采集耗时
        this.collectTimer = Timer.builder("collector.collect.time")
            .tag("type", collectorType)
            .description("Collection execution time")
            .register(registry);
    }

    // 其他方法保持不变...
}
```

然后在AbstractCollector中进行初始化：

```java
public abstract class AbstractCollector<P, R> implements ICollector<P, R> {
    
    @Autowired
    protected CollectorMetrics metrics;

    @PostConstruct
    public void init() {
        try {
            // 初始化监控指标
            metrics.init(getType());
            
            // 初始化线程池
            initExecutor();
            
            // 执行子类初始化
            doInit();
            
            // 更新状态
            lifecycle = CollectorLifecycle.INITIALIZED;
            log.info("Collector initialized: type={}", getType());
            
        } catch (Exception e) {
            log.error("Failed to initialize collector: type={}", getType(), e);
            lifecycle = CollectorLifecycle.DESTROYED;
            throw new CollectException("Collector initialization failed", e);
        }
    }
    
    // 其他方法保持不变...
}
```

这样修改后：

1.  CollectorMetrics成为一个真正的Spring组件，可以自动注入MeterRegistry
2.  初始化方法由采集器调用时传入具体的type
3.  各个指标在初始化时创建,避免了构造时的type依赖
4.  保持了原有的功能不变

这种方式更符合Spring的依赖注入原则，同时也更容易进行单元测试。需要我继续完善其他相关部分吗？

## Prompt:
abstractcollector提示collectormetrics必须是有效装配成员，没有提供processorregistration代码，chainmetrics和processormanager的方法没有处理好registry传递，有的传有的不传，请修改这几个类

## Response:
好的,让我们依次修改这几个类。

1.  首先是ProcessorRegistration:

```java
package com.study.collect.core.processor.model;

import com.study.collect.core.processor.IProcessor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProcessorRegistration {
    private String type;            // 处理器类型
    private String description;     // 描述信息
    private boolean enabled;        // 是否启用
    private int order;             // 执行顺序
    private IProcessor<?> instance; // 处理器实例
}
```

2.  ChainMetrics改进:

```java
package com.study.collect.core.processor.metrics;

import io.micrometer.core.instrument.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class ChainMetrics {

    private final MeterRegistry registry;
    
    // 链级别指标
    private Counter chainCounter;       
    private Counter successCounter;     
    private Counter failureCounter;     
    private Timer chainTimer;           
    private AtomicInteger activeChains;  
    
    // 处理器级别指标
    private final ConcurrentHashMap<String, Timer> processorTimers;
    private final ConcurrentHashMap<String, Counter> processorErrors;
    
    private String chainId;

    @Autowired
    public ChainMetrics(MeterRegistry registry) {
        this.registry = registry;
        this.processorTimers = new ConcurrentHashMap<>();
        this.processorErrors = new ConcurrentHashMap<>();
    }

    public void init(String chainId) {
        this.chainId = chainId;
        
        // 初始化链级别指标
        this.chainCounter = Counter.builder("chain.execute.total")
            .tag("chainId", chainId)
            .description("Total number of chain executions")
            .register(registry);
            
        this.successCounter = Counter.builder("chain.execute.success")
            .tag("chainId", chainId)
            .description("Number of successful chain executions")
            .register(registry);
            
        this.failureCounter = Counter.builder("chain.execute.failure")
            .tag("chainId", chainId)
            .description("Number of failed chain executions")
            .register(registry);
            
        this.chainTimer = Timer.builder("chain.execute.time")
            .tag("chainId", chainId)
            .description("Chain execution time")
            .register(registry);
            
        this.activeChains = new AtomicInteger(0);
        Gauge.builder("chain.active.count", activeChains::get)
            .tag("chainId", chainId)
            .description("Number of active chains")
            .register(registry);
    }

    // 其他方法保持不变...
}
```

3.  ProcessorManager改进:

```java
package com.study.collect.core.processor.manager;

import com.study.collect.core.processor.IProcessor;
import com.study.collect.core.processor.annotation.Processor;
import com.study.collect.core.processor.chain.ProcessChain;
import com.study.collect.core.processor.metrics.ChainMetrics;
import com.study.collect.core.processor.model.ProcessorRegistration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class ProcessorManager {

    @Autowired
    private ApplicationContext applicationContext;
    
    @Autowired
    private ChainMetrics chainMetrics;

    // 处理器注册表
    private final Map<String, ProcessorRegistration> processors = new ConcurrentHashMap<>();
    
    // 处理链缓存
    private final Map<String, ProcessChain<?>> chains = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        // 扫描并注册处理器
        scanProcessors();
        
        // 打印处理器信息
        printProcessorInfo();
    }

    @SuppressWarnings("unchecked")
    public <T> ProcessChain<T> createChain(String chainId) {
        return (ProcessChain<T>) chains.computeIfAbsent(chainId, id -> {
            // 初始化监控指标
            chainMetrics.init(id);
            
            ProcessChain<T> chain = new ProcessChain<>(id, chainMetrics);
            
            // 添加所有启用的处理器
            processors.values().stream()
                .filter(ProcessorRegistration::isEnabled)
                .map(reg -> (IProcessor<T>) reg.getInstance())
                .forEach(chain::addProcessor);
                
            return chain;
        });
    }

    // 其他方法保持不变...
}
```

4.  AbstractCollector改进:

```java
public abstract class AbstractCollector<P, R> implements ICollector<P, R> {
    
    private volatile CollectorLifecycle lifecycle = CollectorLifecycle.CREATED;

    @Autowired
    private MeterRegistry registry;

    private CollectorMetrics metrics;
    private ThreadPoolExecutor executor;

    @PostConstruct
    public void init() {
        try {
            // 创建监控指标
            metrics = new CollectorMetrics(registry);
            metrics.init(getType());
            
            // 初始化线程池
            initExecutor();
            
            // 执行子类初始化
            doInit();
            
            // 更新状态
            lifecycle = CollectorLifecycle.INITIALIZED;
            log.info("Collector initialized: type={}", getType());
            
        } catch (Exception e) {
            log.error("Failed to initialize collector: type={}", getType(), e);
            lifecycle = CollectorLifecycle.DESTROYED;
            throw new CollectException("Collector initialization failed", e);
        }
    }

    // 其他方法保持不变...
}
```

主要改进点：

1.  AbstractCollector中直接注入MeterRegistry并手动创建CollectorMetrics实例
2.  添加了ProcessorRegistration模型类
3.  ChainMetrics统一采用注入MeterRegistry的方式
4.  ProcessorManager中使用单个ChainMetrics实例
5.  所有metrics类统一采用init方式进行初始化

这样修改后：

1.  所有依赖都能正确注入
2.  监控指标创建方式统一
3.  代码结构更清晰

需要我继续完善其他部分吗？

