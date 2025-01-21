## Prompt:

分析文件代码模块，并总结，剖析数据处理流程，为我讲解，然后我们再来逐个优化修改

## Response:

让我为您分析这个分布式采集平台的核心模块和数据处理流程。

一、核心模块分析
========

### 1\. 整体架构分层

```
platform-collect/
├── collect-api         # API层：对外接口
├── collect-core        # 核心层：核心业务逻辑
├── collect-domain      # 领域层：领域模型和规则
├── collect-common      # 公共层：工具类和通用功能
└── collect-business    # 业务层：具体业务实现
```

### 2\. 核心组件

1. 采集器(Collector)

* AbstractCollector：抽象基类，提供通用实现
* 支持多种采集模式：树形、列表、复合等
* 可扩展的采集策略

2. 处理器(Processor)

* AbstractProcessor：处理器基类
* 处理器链：支持多个处理器串联
* 处理器注解：@Processor配置

3. 任务调度(Scheduler)

* TaskScheduler：调度器接口
* DefaultScheduler：默认实现
* 支持Cron表达式和分片

4. 存储层(Repository)

* MongoDB：数据存储
* Redis：缓存和分布式锁
* MySQL：任务配置和日志

5. 消息队列(MQ)

* TaskProducer：任务生产者
* TaskConsumer：任务消费者
* 支持消息重试和死信

二、数据处理流程
========

### 1\. 完整流程图

```
用户请求 -> API层接收 
    -> 创建任务实例(TaskInstance)
    -> 发送MQ消息(TaskMessage) 
        -> 消费者接收并处理
            -> 获取任务处理器(TaskHandler)
                -> 调用采集器(Collector)
                    -> 数据处理器(Processor)
                        -> 数据存储(Repository)
                            -> 返回结果(TaskResult)
```

### 2\. 关键流程节点

1. 任务创建与分发

```java
// 1. 创建任务实例
TaskInstance instance = taskExecuteService.createTaskInstance(...);

// 2. 发送任务消息
TaskMessage message = new TaskMessage();
taskProducer.sendTask(message);
```

2. 任务执行

```java
// 1. 消费者处理
@RabbitListener(queues = "${collect.mq.rabbit.task.queue}")
public void onTaskMessage(TaskMessage message) {
    // 获取处理器执行任务
    TaskHandler handler = handlerManager.getHandler(message.getTaskId());
    TaskResult result = handler.execute(context);
}
```

3. 数据采集与处理

```java
// 1. 采集器执行
CollectResult<T> collect(CollectContext context) {
    // 前置处理
    preProcess(context);
    
    // 执行采集
    T result = doCollect(context);
    
    // 后置处理
    postProcess(result);
}

// 2. 处理器处理
T process(T data, ProcessContext context) {
    // 数据处理链式调用
    return processors.stream()
            .reduce(data,
                (d, p) -> p.process(d, context),
                (d1, d2) -> d1);
}
```

三、核心特性
======

1. 可扩展性

* 采集器、处理器可自由扩展
* 支持多种数据源接入
* 插件化的业务模块

2. 高可用性

* 任务分片执行
* 失败重试机制
* 监控告警支持

3. 可靠性

* 事务管理
* 幂等性保证
* 异常处理机制

4. 性能优化

* 数据批量处理
* 缓存优化
* 并行处理支持

您想先从哪个模块开始优化？我们可以针对：

1. 采集器性能优化
2. 处理器链优化
3. 任务调度优化
4. 数据存储优化
5. 监控告警增强

您更关注哪个方面？

## Prompt:

任务调度优化应该如何修改，目前的任务调度优化结构是什么样的？分析文件结构和流程，定时如何触发，手动如何处罚，文件功能分类，从哪里开始修改

## Response:

让我分析一下当前任务调度相关的核心结构和流程。

一、任务调度相关的核心文件结构
===============

```plaintext
core/task/
├── config/                     # 配置类
│   ├── TaskConfiguration.java  # 任务配置
│   └── MyBatisConfig.java     # MyBatis配置
├── scheduler/                  # 调度器
│   ├── AbstractTaskScheduler.java   # 调度器抽象类
│   ├── DefaultTaskScheduler.java    # 默认调度器实现
│   ├── TaskScheduler.java          # 调度器接口
│   └── TaskDispatcher.java         # 任务分发器
├── handler/                    # 任务处理器
│   ├── AbstractTaskHandler.java    # 处理器抽象类
│   ├── TaskHandler.java           # 处理器接口
│   └── TaskHandlerManager.java    # 处理器管理器
├── entity/                     # 实体类
│   ├── TaskConfig.java        # 任务配置实体
│   ├── TaskInstance.java      # 任务实例实体
│   └── TaskLog.java           # 任务日志实体 
├── service/                    # 服务层
│   ├── TaskConfigService.java  # 任务配置服务
│   └── TaskExecuteService.java # 任务执行服务
└── mapper/                     # 数据访问层
    ├── TaskConfigMapper.java   # 任务配置Mapper
    ├── TaskInstanceMapper.java # 任务实例Mapper
    └── TaskLogMapper.java      # 任务日志Mapper
```

二、当前调度流程分析
==========

### 1\. 定时任务触发流程

```
TaskScheduler(定时调度)
  -> TaskConfig(获取配置)
    -> TaskInstance(创建实例)
      -> TaskMessage(生成消息)  
        -> TaskProducer(发送MQ)
          -> TaskConsumer(消费处理)
            -> TaskHandler(执行任务)
```

### 2\. 手动触发流程

```
API调用
  -> TaskExecuteService
    -> TaskInstance(创建实例)
      -> TaskMessage(生成消息)
        -> TaskProducer(发送MQ)
          -> TaskConsumer(消费处理)
            -> TaskHandler(执行任务)
```

三、存在的问题和优化方向
============

### 1\. 任务配置管理

```java
// TaskConfig.java - 当前配置项有限
public class TaskConfig {
    private String taskCode;        
    private String taskName;        
    private String taskHandler;     
    private String taskParam;       
    private String cronExpr;        
    private Integer shardTotal;     
    private Integer retryTimes;     
    private Integer timeout;        
}
```

优化建议：

1. 增加任务优先级配置
2. 添加依赖任务配置
3. 支持更复杂的调度策略(如：每月最后一天)

### 2\. 任务调度器

```java
// DefaultTaskScheduler.java - 当前调度较简单
@Component
public class DefaultTaskScheduler extends AbstractTaskScheduler {
    @Override 
    protected void doStart() {
        // 简单的遍历调度
        taskConfigService.getEnabledTaskConfigs().forEach(config -> {
            scheduleTask(convertToDefinition(config));
        });
    }
}
```

优化建议：

1. 实现任务队列优先级
2. 添加任务依赖关系处理
3. 增加调度策略的扩展性

四、具体优化方案
========

### 1\. 任务配置增强

```java
// 新增TaskConfig配置项
public class TaskConfig {
    // 现有配置...
    
    private Integer priority;        // 任务优先级
    private String[] dependencies;   // 依赖任务
    private String scheduleStrategy; // 调度策略
    private Map<String, Object> scheduleParams; // 调度参数
}
```

### 2\. 调度策略接口

```java
public interface ScheduleStrategy {
    boolean shouldSchedule(TaskConfig config, Date now);
    Date nextExecuteTime(TaskConfig config, Date lastTime);
}

// 具体实现
public class CronScheduleStrategy implements ScheduleStrategy {
    @Override
    public boolean shouldSchedule(TaskConfig config, Date now) {
        return new CronTrigger(config.getCronExpr())
                .nextExecutionTime(triggerContext) != null;
    }
}

public class MonthEndScheduleStrategy implements ScheduleStrategy {
    @Override
    public boolean shouldSchedule(TaskConfig config, Date now) {
        // 实现月末调度逻辑
    }
}
```

### 3\. 优先级任务队列

```java
public class PriorityTaskQueue {
    private final PriorityBlockingQueue<TaskInstance> queue;
    
    public void offer(TaskInstance instance) {
        queue.offer(instance);
    }
    
    public TaskInstance take() {
        return queue.take();
    }
}
```

### 4\. 依赖任务处理

```java
public class DependencyManager {
    private final Map<String, Set<String>> dependencies = new ConcurrentHashMap<>();
    
    public boolean canExecute(String taskCode) {
        Set<String> deps = dependencies.get(taskCode);
        if (deps == null) return true;
        
        return deps.stream()
                .allMatch(this::isCompleted);
    }
    
    public void onTaskComplete(String taskCode) {
        // 处理依赖任务完成逻辑
    }
}
```

### 5\. 改进的调度器

```java
@Component
public class EnhancedTaskScheduler extends AbstractTaskScheduler {
    private final PriorityTaskQueue taskQueue;
    private final DependencyManager dependencyManager;
    private final Map<String, ScheduleStrategy> strategies;
    
    @Override
    protected void doStart() {
        // 启动调度线程
        startScheduleThread();
        // 启动执行线程池
        startExecutorPool();
    }
    
    private void schedule(TaskConfig config) {
        // 1. 检查依赖
        if (!dependencyManager.canExecute(config.getTaskCode())) {
            return;
        }
        
        // 2. 检查调度策略
        ScheduleStrategy strategy = strategies.get(config.getScheduleStrategy());
        if (!strategy.shouldSchedule(config, new Date())) {
            return;
        }
        
        // 3. 创建任务实例
        TaskInstance instance = createTaskInstance(config);
        
        // 4. 加入优先级队列
        taskQueue.offer(instance);
    }
}
```

### 6\. 监控和告警增强

```java
public class TaskMonitor {
    // 任务执行时长监控
    public void recordExecuteTime(String taskCode, long time) {
        // 记录执行时长
    }
    
    // 任务成功率监控
    public void recordTaskResult(String taskCode, boolean success) {
        // 记录执行结果
    }
    
    // 任务积压监控
    public void monitorQueueSize(int size) {
        if (size > threshold) {
            // 发送告警
        }
    }
}
```

五、修改建议
======

建议按以下步骤进行修改：

1. 首先修改 TaskConfig 实体和数据库表，增加新的配置项
2. 实现调度策略接口和具体策略类
3. 实现优先级任务队列
4. 实现依赖任务管理器
5. 改进调度器实现
6. 增加监控和告警功能

需要我详细展开某个具体部分吗？

## Prompt:

需要，请思考我们改造后是一个什么样的架构，支持分布式，任务分解，并发，优先级动态手动被动控流，手动标记任务直接成功（包括失败的和进行的），终止和重试任务和子任务，聚合记录任务执行过程和进度，监控告警，根据服务器资源动态分配任务到服务?还能加入哪些东西，请整理这些思路

## Response:

让我系统地设计一个更完善的分布式任务调度架构。

一、整体架构设计
========

```plaintext
分布式任务调度平台
├── 调度中心(Schedule Center)
│   ├── 任务管理(Task Manager)
│   ├── 调度引擎(Schedule Engine)
│   ├── 分发中心(Dispatch Center)
│   └── 集群管理(Cluster Manager)
├── 执行节点(Execute Node)
│   ├── 任务执行器(Task Executor)
│   ├── 资源监控(Resource Monitor)
│   └── 节点管理(Node Manager)
└── 监控中心(Monitor Center)
    ├── 执行监控(Execution Monitor)
    ├── 资源监控(Resource Monitor)
    └── 告警中心(Alert Center)
```

二、核心功能模块设计
==========

### 1\. 任务模型升级

```java
public class TaskDefinition {
    private String taskId;                // 任务ID
    private String taskName;              // 任务名称
    private Integer priority;             // 优先级(1-10)
    private List<String> dependencies;    // 依赖任务
    private TaskTrigger trigger;          // 触发器配置
    private TaskStrategy strategy;        // 执行策略
    private TaskResource resource;        // 资源需求
    private TaskTimeout timeout;          // 超时配置
    private TaskRetry retry;             // 重试配置
    private TaskCallback callback;        // 回调配置
}

// 任务分解配置
public class TaskStrategy {
    private Boolean enableSplit;          // 是否允许分解
    private Integer maxSplitNum;          // 最大分解数
    private String splitStrategy;         // 分解策略
    private Boolean enableMerge;          // 是否需要合并
    private String mergeStrategy;         // 合并策略
}

// 资源需求配置
public class TaskResource {
    private Integer cpuRequirement;       // CPU需求
    private Integer memoryRequirement;    // 内存需求
    private Integer networkRequirement;   // 网络带宽需求
    private List<String> tags;           // 节点标签需求
}
```

### 2\. 调度引擎增强

```java
public interface ScheduleEngine {
    // 动态调度控制
    void scheduleTask(TaskDefinition task);
    void pauseTask(String taskId);
    void resumeTask(String taskId);
    void terminateTask(String taskId);
    
    // 手动任务控制
    void triggerTask(String taskId);
    void markTaskSuccess(String taskId);
    void markTaskFailed(String taskId);
    
    // 任务分解控制
    void splitTask(String taskId);
    void mergeTask(String taskId);
    
    // 流量控制
    void updateTaskThrottle(String taskId, ThrottleConfig config);
    void updateGlobalThrottle(ThrottleConfig config);
}

// 具体实现
@Service
public class DistributedScheduleEngine implements ScheduleEngine {
    private final TaskQueue taskQueue;
    private final NodeManager nodeManager;
    private final LoadBalancer loadBalancer;
    private final TaskTracker taskTracker;
    private final ThrottleManager throttleManager;
    
    @Override
    public void scheduleTask(TaskDefinition task) {
        // 1. 任务分解
        List<TaskInstance> instances = taskSplitter.split(task);
        
        // 2. 节点选择
        List<ExecutorNode> nodes = selectNodes(instances);
        
        // 3. 负载均衡分配
        Map<ExecutorNode, List<TaskInstance>> distribution = 
            loadBalancer.distribute(instances, nodes);
            
        // 4. 任务分发
        distribution.forEach(this::dispatchTasks);
    }
}
```

### 3\. 资源管理和负载均衡

```java
public class NodeManager {
    // 节点资源监控
    private Map<String, NodeResource> nodeResources;
    
    // 节点健康检查
    private Map<String, NodeHealth> nodeHealth;
    
    // 动态扩缩容
    public void scaleNodes(int delta) {
        if (delta > 0) {
            expandCluster(delta);
        } else {
            shrinkCluster(-delta);
        }
    }
    
    // 资源评估
    public boolean canSchedule(TaskInstance task, String nodeId) {
        NodeResource resource = nodeResources.get(nodeId);
        return resource.canAccommodate(task.getResourceRequirement());
    }
}

// 自适应负载均衡
public class AdaptiveLoadBalancer implements LoadBalancer {
    @Override
    public Map<ExecutorNode, List<TaskInstance>> distribute(
            List<TaskInstance> tasks, List<ExecutorNode> nodes) {
        // 1. 计算节点得分
        Map<ExecutorNode, Double> scores = calculateNodeScores(nodes);
        
        // 2. 任务分类(按资源需求)
        Map<ResourceLevel, List<TaskInstance>> taskGroups = 
            groupTasksByResource(tasks);
            
        // 3. 优先级排序
        PriorityQueue<TaskInstance> sortedTasks = sortByPriority(tasks);
        
        // 4. 分配任务
        return assignTasks(sortedTasks, nodes, scores);
    }
}
```

### 4\. 任务跟踪和进度管理

```java
public class TaskTracker {
    // 任务状态跟踪
    private Map<String, TaskStatus> taskStatus;
    
    // 执行进度跟踪
    private Map<String, TaskProgress> taskProgress;
    
    // 子任务管理
    private Map<String, List<String>> subTasks;
    
    // 更新进度
    public void updateProgress(String taskId, TaskProgress progress) {
        taskProgress.put(taskId, progress);
        notifyProgressListeners(taskId, progress);
    }
    
    // 聚合进度
    public TaskProgress aggregateProgress(String parentTaskId) {
        List<String> children = subTasks.get(parentTaskId);
        return aggregateChildProgress(children);
    }
}

// 进度监听器
public interface ProgressListener {
    void onProgressUpdate(String taskId, TaskProgress progress);
    void onTaskComplete(String taskId, TaskResult result);
    void onTaskError(String taskId, TaskError error);
}
```

### 5\. 监控告警系统

```java
public class MonitorSystem {
    // 指标收集
    private MetricsCollector metricsCollector;
    
    // 告警管理
    private AlertManager alertManager;
    
    // 实时监控面板
    private DashboardManager dashboardManager;
    
    // 监控指标
    public void collectMetrics() {
        // 任务执行指标
        collectTaskMetrics();
        // 资源使用指标
        collectResourceMetrics();
        // 系统性能指标
        collectPerformanceMetrics();
    }
    
    // 告警规则
    public void defineAlertRules() {
        // 任务失败告警
        addTaskFailureRule();
        // 资源不足告警
        addResourceShortageRule();
        // 系统异常告警
        addSystemExceptionRule();
    }
}
```

三、扩展功能建议
========

1. 任务编排功能

```java
public class TaskFlow {
    private String flowId;
    private List<TaskNode> nodes;
    private Map<String, List<String>> edges;
    
    // DAG任务流
    public void validateDAG() {
        // 检查是否有环
        // 验证依赖关系
    }
    
    // 条件分支
    public void addConditionBranch(String nodeId, Condition condition) {
        // 添加条件分支
    }
}
```

2. 灾备切换机制

```java
public class DisasterRecovery {
    // 主备切换
    public void switchToBackup() {
        // 停止主节点调度
        // 激活备用节点
        // 恢复任务状态
    }
    
    // 数据同步
    public void syncTaskData() {
        // 同步任务配置
        // 同步执行状态
        // 同步历史数据
    }
}
```

3. 任务优化建议

```java
public class TaskOptimizer {
    // 执行计划优化
    public ExecutionPlan optimize(TaskDefinition task) {
        // 分析历史执行数据
        // 优化资源分配
        // 调整执行策略
    }
    
    // 性能建议
    public List<Suggestion> suggest(String taskId) {
        // 分析执行瓶颈
        // 提供优化建议
        // 预估优化效果
    }
}
```

4. 安全机制

```java
public class SecurityManager {
    // 访问控制
    public void checkPermission(String userId, String taskId) {
        // 验证用户权限
        // 检查操作限制
    }
    
    // 数据加密
    public void encryptSensitiveData(TaskData data) {
        // 加密敏感信息
        // 脱敏处理
    }
}
```

四、关键技术点
=======

1. 分布式一致性

* 使用ZooKeeper管理调度节点
* 实现分布式锁
* 保证任务不重复执行

2. 高可用设计

* 调度中心多副本部署
* 执行节点动态扩缩容
* 任务失败自动恢复

3. 性能优化

* 任务预热机制
* 批量任务调度
* 资源预分配

4. 可观测性

* 分布式追踪
* 性能指标采集
* 日志聚合分析

需要我详细展开某个具体的模块设计吗？

## Prompt:

分布式分发能使用rabbitmq代替吗？

## Response:

是的，RabbitMQ完全可以用于分布式任务分发。让我设计一个基于RabbitMQ的分布式任务分发架构。

一、RabbitMQ分布式任务分发架构设计
=====================

```plaintext
分布式任务系统(基于RabbitMQ)
├── 调度中心
│   ├── Task Exchange (任务交换机)
│   │   ├── Priority Queue (优先级队列)
│   │   ├── Delayed Queue (延迟队列) 
│   │   └── Dead Letter Queue (死信队列)
├── 执行节点
│   ├── Worker Queues (工作队列)
│   └── Result Queue (结果队列)
└── 监控中心
    ├── Monitor Exchange (监控交换机)
    └── Alert Queue (告警队列)
```

二、核心实现设计
========

### 1\. 交换机和队列配置

```java
@Configuration
public class RabbitMQConfig {
    
    @Bean
    public TopicExchange taskExchange() {
        return ExchangeBuilder.topicExchange("task.exchange")
                .durable(true)
                .build();
    }
    
    @Bean
    public Queue priorityQueue() {
        return QueueBuilder.durable("task.priority.queue")
                .withArguments(new HashMap<String, Object>() {{
                    put("x-max-priority", 10); // 设置最大优先级
                    put("x-queue-mode", "lazy"); // 大任务延迟加载
                }}).build();
    }
    
    @Bean
    public Queue delayedQueue() {
        return QueueBuilder.durable("task.delayed.queue")
                .withArguments(new HashMap<String, Object>() {{
                    put("x-dead-letter-exchange", "task.exchange");
                    put("x-dead-letter-routing-key", "task.ready");
                    put("x-message-ttl", 30000); // 延迟时间
                }}).build();
    }
    
    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable("task.dlq")
                .build();
    }
}
```

### 2\. 任务分发器

```java
@Service
@Slf4j
public class TaskDispatcher {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    /**
     * 分发任务到对应的队列
     */
    public void dispatchTask(TaskDefinition task) {
        String routingKey = determineRoutingKey(task);
        MessageProperties props = new MessageProperties();
        
        // 设置优先级
        props.setPriority(task.getPriority());
        
        // 设置消息属性
        props.setHeader("taskId", task.getTaskId());
        props.setHeader("retryCount", 0);
        
        // 发送消息
        rabbitTemplate.send(
            "task.exchange",
            routingKey,
            createMessage(task, props)
        );
        
        log.info("Task dispatched: {}", task.getTaskId());
    }
    
    /**
     * 根据任务特征决定路由键
     */
    private String determineRoutingKey(TaskDefinition task) {
        if (task.isDelayed()) {
            return "task.delayed";
        }
        if (task.getPriority() > 5) {
            return "task.priority.high";
        }
        return "task.priority.normal";
    }
}
```

### 3\. 工作节点消费者

```java
@Component
@Slf4j
public class TaskWorker {
    
    @Autowired
    private TaskExecutor taskExecutor;
    
    @Autowired 
    private ResourceMonitor resourceMonitor;
    
    /**
     * 消费优先级队列任务
     */
    @RabbitListener(
        queues = "#{priorityQueue.name}",
        containerFactory = "workerContainerFactory"
    )
    public void handlePriorityTask(
            Message message,
            Channel channel) throws IOException {
            
        // 获取任务信息
        TaskDefinition task = parseTask(message);
        
        try {
            // 检查资源是否充足
            if (!resourceMonitor.hasEnoughResource(task)) {
                // 资源不足，拒绝消息并重新入队
                channel.basicReject(
                    message.getMessageProperties().getDeliveryTag(),
                    true
                );
                return;
            }
            
            // 执行任务
            TaskResult result = taskExecutor.execute(task);
            
            // 确认消息
            channel.basicAck(
                message.getMessageProperties().getDeliveryTag(),
                false
            );
            
            // 发送结果
            sendResult(result);
            
        } catch (Exception e) {
            handleExecutionError(message, channel, e);
        }
    }
    
    /**
     * 处理执行异常
     */
    private void handleExecutionError(
            Message message,
            Channel channel,
            Exception e) throws IOException {
            
        MessageProperties props = message.getMessageProperties();
        int retryCount = (int) props.getHeaders().getOrDefault("retryCount", 0);
        
        if (retryCount < 3) {
            // 重试
            props.getHeaders().put("retryCount", retryCount + 1);
            channel.basicReject(props.getDeliveryTag(), true);
        } else {
            // 超过重试次数，发送到死信队列
            channel.basicReject(props.getDeliveryTag(), false);
        }
    }
}
```

### 4\. 资源监控和动态调节

```java
@Component
public class ResourceMonitor {
    
    private final Map<String, WorkerStatus> workerStatus = new ConcurrentHashMap<>();
    
    /**
     * 更新工作节点状态
     */
    @Scheduled(fixedRate = 5000)
    public void updateWorkerStatus() {
        // 收集各节点资源使用情况
        for (String workerId : workerStatus.keySet()) {
            WorkerStatus status = collectWorkerMetrics(workerId);
            workerStatus.put(workerId, status);
        }
        
        // 动态调整消费者数量
        adjustConsumerCount();
    }
    
    /**
     * 动态调整消费者数量
     */
    private void adjustConsumerCount() {
        int totalLoad = calculateTotalLoad();
        
        if (totalLoad > 80) {
            // 负载过高，减少消费者数量
            decreaseConsumers();
        } else if (totalLoad < 40) {
            // 负载较低，增加消费者数量
            increaseConsumers();
        }
    }
    
    /**
     * 检查资源是否足够
     */
    public boolean hasEnoughResource(TaskDefinition task) {
        WorkerStatus status = workerStatus.get(getCurrentWorkerId());
        return status.canHandle(task.getResourceRequirement());
    }
}
```

### 5\. 监控和告警

```java
@Component
public class TaskMonitor {
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    /**
     * 监控队列状态
     */
    @Scheduled(fixedRate = 10000)
    public void monitorQueues() {
        // 检查队列深度
        checkQueueDepth();
        
        // 检查消息处理延迟
        checkProcessingDelay();
        
        // 检查错误率
        checkErrorRate();
    }
    
    /**
     * 发送告警
     */
    private void sendAlert(AlertMessage alert) {
        rabbitTemplate.convertAndSend(
            "monitor.exchange",
            "alert.notification",
            alert
        );
    }
}
```

三、优势和注意事项
=========

### 优势：

1. 消息持久化：RabbitMQ提供消息持久化，防止任务丢失
2. 优先级队列：原生支持消息优先级
3. 延迟队列：支持任务延迟执行
4. 死信队列：处理失败任务
5. 负载均衡：自动在消费者之间分配任务
6. 消息确认：确保任务被正确处理

### 注意事项：

1. 消息大小限制：大任务需要考虑消息体积
2. 任务幂等性：确保任务可重试
3. 消息顺序性：需要时要特别处理
4. 集群配置：保证MQ集群高可用
5. 监控告警：及时发现异常情况

四、扩展功能建议
========

1. 添加消息压缩

```java
public class MessageCompressor {
    public Message compress(TaskDefinition task) {
        byte[] compressed = CompressionUtils.compress(
            JsonUtils.toBytes(task)
        );
        return MessageBuilder
            .withBody(compressed)
            .setHeader("compressed", true)
            .build();
    }
}
```

2. 实现任务批量处理

```java
public class BatchTaskConsumer {
    @RabbitListener(queues = "task.batch.queue")
    public void consumeBatch(List<Message> messages) {
        List<TaskDefinition> tasks = messages.stream()
            .map(this::parseTask)
            .collect(Collectors.toList());
            
        executeBatch(tasks);
    }
}
```

3. 添加任务追踪

```java
public class TaskTracer {
    public Message addTrace(Message message) {
        String traceId = UUID.randomUUID().toString();
        return MessageBuilder
            .fromMessage(message)
            .setHeader("traceId", traceId)
            .setHeader("timestamp", System.currentTimeMillis())
            .build();
    }
}
```

需要我详细展开某个具体实现吗？

## Prompt:

那么重新设置3. 任务调度(Scheduler)和5. 消息队列(MQ)
模块的修改建议，记住任务拆解成小任务可以通过rabbitmq分发，单个小任务也是通过rabbitmq分发，请考虑上述并给出一个在现有结构上稳定可行的修改方案，给出要新增修改删除的文件和目录设计，确认后分段输出代码

## Response:

好的，我来设计一个基于现有结构，结合RabbitMQ的任务调度与分发方案。

一、目录结构设计
========

```plaintext
core/
├── scheduler/                     # 调度模块
│   ├── engine/                   # 调度引擎
│   │   ├── TaskEngine.java      # 任务引擎接口
│   │   └── DistTaskEngine.java  # 分布式任务引擎实现
│   ├── split/                    # 任务拆分
│   │   ├── TaskSplitter.java    # 任务拆分器接口
│   │   ├── DefaultSplitter.java # 默认拆分实现
│   │   └── SplitStrategy.java   # 拆分策略接口
│   ├── dispatch/                 # 任务分发
│   │   ├── TaskDispatcher.java  # 任务分发器
│   │   └── DispatchStrategy.java # 分发策略
│   └── recover/                  # 任务恢复
│       ├── TaskRecover.java     # 任务恢复接口
│       └── RecoverStrategy.java # 恢复策略
├── mq/                           # 消息队列模块
│   ├── config/                   # MQ配置
│   │   ├── RabbitConfig.java    # RabbitMQ配置
│   │   └── QueueConfig.java     # 队列配置
│   ├── producer/                 # 生产者
│   │   ├── TaskProducer.java    # 任务生产者
│   │   └── ResultProducer.java  # 结果生产者
│   ├── consumer/                 # 消费者
│   │   ├── TaskConsumer.java    # 任务消费者
│   │   └── ResultConsumer.java  # 结果消费者
│   └── message/                  # 消息定义
│       ├── TaskMessage.java     # 任务消息
│       ├── SubTaskMessage.java  # 子任务消息
│       └── ResultMessage.java   # 结果消息
└── task/                         # 任务模块
    ├── model/                    # 任务模型
    │   ├── TaskDefinition.java  # 任务定义
    │   ├── SubTaskDefinition.java # 子任务定义
    │   └── TaskResult.java      # 任务结果
    ├── context/                  # 任务上下文
    │   ├── TaskContext.java     # 任务上下文
    │   └── SubTaskContext.java  # 子任务上下文
    └── tracker/                  # 任务跟踪
        ├── TaskTracker.java     # 任务跟踪器
        └── ProgressTracker.java # 进度跟踪器
```

二、需要修改的现有文件
===========

1. TaskScheduler.java (修改)
2. DefaultTaskScheduler.java (修改)
3. TaskDispatcher.java (修改)
4. TaskMessage.java (修改)
5. TaskConsumer.java (修改)

三、主要功能模块
========

### 1\. 调度引擎

* 负责任务调度和生命周期管理
* 支持任务拆分和合并
* 处理任务依赖关系
* 管理任务执行状态

### 2\. 任务拆分

* 支持多种拆分策略
* 管理子任务关系
* 提供拆分进度跟踪
* 处理拆分异常

### 3\. 消息分发

* 基于RabbitMQ的消息路由
* 支持任务优先级
* 提供消息重试机制
* 处理任务超时和失败

### 4\. 任务跟踪

* 跟踪任务执行状态
* 聚合子任务进度
* 提供任务恢复机制
* 记录执行历史

四、关键设计点
=======

1. 任务拆分与状态管理

* 主任务与子任务关系维护
* 状态同步和更新机制
* 进度聚合计算

2. 消息队列设计

* 主任务队列
* 子任务队列
* 结果队列
* 死信队列

3. 错误处理

* 任务重试机制
* 失败恢复策略
* 超时处理

4. 数据一致性

* 分布式事务处理
* 状态一致性保证
* 幂等性设计

请确认这个设计方案，我们可以分模块详细实现代码。您觉得这个方案是否合理？需要调整或补充吗？

## Prompt:

这个设计考虑了现有其它模块去继承和实现各自业务功能吗？感觉不够好，重新设计一下。

## Response:

您说得对，让我重新设计一下，考虑与现有业务模块的整合与扩展。

一、核心设计理念
========

1. 基础框架层：提供统一的调度和分发能力
2. 扩展接口层：定义业务模块需要实现的接口
3. 业务适配层：各业务模块的具体实现

二、目录结构设计
========

```plaintext
platform-collect/
├── collect-core/                          # 核心框架
│   ├── scheduler/                         # 调度模块
│   │   ├── api/                          # 调度接口定义
│   │   │   ├── TaskScheduler.java        # 调度器接口
│   │   │   ├── TaskSplitter.java         # 任务拆分接口
│   │   │   └── TaskTracker.java          # 任务跟踪接口
│   │   ├── support/                      # 调度支持类
│   │   │   ├── AbstractTaskScheduler.java # 抽象调度器
│   │   │   ├── AbstractTaskSplitter.java  # 抽象拆分器
│   │   │   └── AbstractTaskTracker.java   # 抽象跟踪器
│   │   └── engine/                       # 调度引擎
│   │       ├── DefaultScheduleEngine.java # 默认调度引擎
│   │       └── DistributedEngine.java     # 分布式引擎
│   │
│   ├── task/                             # 任务模块
│   │   ├── api/                          # 任务接口定义
│   │   │   ├── TaskProcessor.java        # 任务处理接口
│   │   │   ├── TaskValidator.java        # 任务校验接口
│   │   │   └── TaskCallback.java         # 任务回调接口
│   │   ├── model/                        # 任务模型
│   │   │   ├── BaseTask.java            # 基础任务类
│   │   │   ├── SubTask.java             # 子任务类
│   │   │   └── TaskContext.java         # 任务上下文
│   │   └── support/                      # 任务支持类
│   │       ├── AbstractTaskProcessor.java # 抽象处理器
│   │       └── DefaultTaskValidator.java  # 默认校验器
│   │
│   └── mq/                               # 消息队列模块
│       ├── api/                          # MQ接口定义
│       │   ├── MessagePublisher.java     # 消息发布接口
│       │   └── MessageHandler.java       # 消息处理接口
│       ├── model/                        # 消息模型
│       │   ├── BaseMessage.java         # 基础消息类
│       │   ├── TaskMessage.java         # 任务消息类
│       │   └── ResultMessage.java       # 结果消息类
│       └── rabbit/                      # RabbitMQ实现
│           ├── RabbitPublisher.java     # RabbitMQ发布者
│           └── RabbitConsumer.java      # RabbitMQ消费者
│
├── collect-business/                     # 业务模块
│   ├── business-enterprise/             # 企业数据采集
│   │   ├── task/                       # 企业任务实现
│   │   │   ├── EnterpriseTask.java    # 企业任务
│   │   │   └── EnterpriseSplitter.java # 企业拆分器
│   │   └── processor/                  # 企业处理器
│   │       └── EnterpriseProcessor.java # 企业处理实现
│   │
│   ├── business-finance/               # 金融数据采集
│   │   └── ...                        # 类似结构
│   │
│   └── business-medical/              # 医疗数据采集
│       └── ...                       # 类似结构
```

三、核心接口设计
========

### 1\. 任务基础接口

```java
public interface Task<T extends TaskContext> {
    String getTaskId();
    TaskType getTaskType();
    boolean isSplittable();
    T getContext();
}

public interface TaskProcessor<T extends Task> {
    void process(T task);
    boolean canProcess(TaskType taskType);
}

public interface TaskSplitter<T extends Task> {
    List<T> split(T task);
    boolean canSplit(T task);
    void merge(List<T> subTasks);
}
```

### 2\. 调度接口

```java
public interface TaskScheduler {
    void schedule(Task task);
    void reschedule(String taskId);
    void cancel(String taskId);
    
    // 任务状态控制
    void pause(String taskId);
    void resume(String taskId);
    void markSuccess(String taskId);
    void markFailed(String taskId);
}

public interface TaskTracker {
    void trackProgress(String taskId, int progress);
    void trackStatus(String taskId, TaskStatus status);
    void trackSubTasks(String parentId, List<String> subTaskIds);
}
```

### 3\. 消息队列接口

```java
public interface MessagePublisher {
    void publishTask(TaskMessage message);
    void publishResult(ResultMessage message);
    void publishBatch(List<? extends BaseMessage> messages);
}

public interface MessageHandler<T extends BaseMessage> {
    void handle(T message);
    boolean canHandle(String messageType);
}
```

四、业务模块实现示例
==========

### 1\. 企业数据采集实现

```java
@Component
public class EnterpriseTask extends BaseTask {
    private final EnterpriseConfig config;
    private final List<String> enterpriseCodes;
    
    @Override
    public boolean isSplittable() {
        return enterpriseCodes.size() > config.getBatchSize();
    }
}

@Component
public class EnterpriseSplitter implements TaskSplitter<EnterpriseTask> {
    @Override
    public List<EnterpriseTask> split(EnterpriseTask task) {
        // 按企业代码列表分片
        return splitByBatchSize(task.getEnterpriseCodes());
    }
}

@Component
public class EnterpriseProcessor extends AbstractTaskProcessor<EnterpriseTask> {
    @Override
    public void process(EnterpriseTask task) {
        // 实现企业数据采集逻辑
        collectEnterpriseData(task.getEnterpriseCodes());
    }
}
```

五、扩展点设计
=======

1. 任务扩展点

```java
public interface TaskExtensionPoint {
    void beforeTaskStart(Task task);
    void afterTaskComplete(Task task, TaskResult result);
    void onTaskError(Task task, Exception e);
}
```

2. 处理器扩展点

```java
public interface ProcessorExtensionPoint {
    void beforeProcess(Task task);
    void afterProcess(Task task);
    void onProcessError(Task task, Exception e);
}
```

3. 调度扩展点

```java
public interface SchedulerExtensionPoint {
    boolean beforeSchedule(Task task);
    void afterSchedule(Task task);
    void onScheduleError(Task task, Exception e);
}
```

这个设计：

1. 提供了统一的框架能力
2. 定义了清晰的扩展接口
3. 保持了业务模块的独立性
4. 支持灵活的功能扩展
5. 便于功能复用

是否需要我继续细化某个模块的具体实现？

## Prompt:

说说定时触发和手动接口触发还有任务注册，调用各自业务的逻辑，怎么没有handler了呢？考虑这些重新输出下

## Response:

您说得对，我重新设计加入Handler层和触发机制。

一、修改后的核心目录结构
============

```plaintext
platform-collect/
├── collect-core/                          # 核心框架
│   ├── scheduler/                         # 调度模块
│   │   ├── api/                          # 调度接口定义
│   │   │   ├── TaskScheduler.java        # 调度器接口
│   │   │   └── TaskRegister.java         # 任务注册接口
│   │   ├── trigger/                      # 触发器模块
│   │   │   ├── TaskTrigger.java         # 触发器接口
│   │   │   ├── CronTrigger.java         # CRON触发器
│   │   │   └── ManualTrigger.java       # 手动触发器
│   │   ├── handler/                      # 处理器模块
│   │   │   ├── TaskHandler.java         # 处理器接口
│   │   │   ├── AbstractTaskHandler.java # 抽象处理器
│   │   │   └── HandlerManager.java      # 处理器管理器
│   │   └── engine/                       # 调度引擎
│   │       ├── ScheduleEngine.java      # 调度引擎接口
│   │       └── DefaultScheduleEngine.java # 默认实现
│   │
│   ├── task/                             # 任务模块
│   │   ├── api/                         # 任务接口
│   │   │   ├── Task.java               # 任务接口
│   │   │   └── TaskProcessor.java      # 处理器接口
│   │   ├── registry/                    # 注册中心
│   │   │   ├── TaskRegistry.java       # 任务注册中心
│   │   │   └── RegisterContext.java    # 注册上下文
│   │   └── support/                     # 支持类
│   │       └── DefaultTaskRegistry.java # 默认注册实现
│   │
│   └── mq/                              # 消息队列模块
        ├── dispatcher/                   # 消息分发
        │   ├── TaskDispatcher.java      # 任务分发器
        │   └── ResultDispatcher.java    # 结果分发器
        └── consumer/                     # 消息消费
            ├── TaskConsumer.java        # 任务消费者
            └── ResultConsumer.java      # 结果消费者

├── collect-business/                      # 业务模块
    ├── business-enterprise/              # 企业模块
    │   ├── handler/                      # 处理器
    │   │   └── EnterpriseTaskHandler.java # 企业任务处理器
    │   ├── processor/                    # 处理器
    │   │   └── EnterpriseProcessor.java  # 企业数据处理器
    │   └── config/                       # 配置
    │       └── EnterpriseTaskConfig.java # 企业任务配置
    └── business-finance/                 # 金融模块
        └── ...                          # 类似结构
```

二、核心接口设计
========

### 1\. 任务注册与管理

```java
/**
 * 任务注册接口
 */
public interface TaskRegister {
    // 注册任务
    void register(TaskDefinition definition);
    
    // 注销任务
    void unregister(String taskCode);
    
    // 获取任务定义
    TaskDefinition getTaskDefinition(String taskCode);
}

/**
 * 任务处理器接口
 */
public interface TaskHandler {
    // 获取处理器类型
    String getType();
    
    // 执行任务
    TaskResult handle(TaskContext context);
    
    // 是否可以处理
    boolean canHandle(String taskType);
}

/**
 * 处理器管理器
 */
@Component
public class HandlerManager {
    private final Map<String, TaskHandler> handlers = new ConcurrentHashMap<>();
    
    // 注册处理器
    public void registerHandler(TaskHandler handler) {
        handlers.put(handler.getType(), handler);
    }
    
    // 获取处理器
    public TaskHandler getHandler(String type) {
        return handlers.get(type);
    }
}
```

### 2\. 触发机制

```java
/**
 * 触发器接口
 */
public interface TaskTrigger {
    // 是否应该触发
    boolean shouldTrigger(TaskDefinition task, Date now);
    
    // 计算下次触发时间
    Date nextTriggerTime(TaskDefinition task, Date lastTime);
}

/**
 * CRON触发器
 */
@Component
public class CronTrigger implements TaskTrigger {
    @Override
    public boolean shouldTrigger(TaskDefinition task, Date now) {
        CronExpression cron = new CronExpression(task.getCronExpr());
        return cron.isSatisfiedBy(now);
    }
}

/**
 * 手动触发器
 */
@Component
public class ManualTrigger implements TaskTrigger {
    @Override
    public boolean shouldTrigger(TaskDefinition task, Date now) {
        return task.isManualTriggered();
    }
}
```

### 3\. 调度引擎

```java
/**
 * 调度引擎接口
 */
public interface ScheduleEngine {
    // 调度任务
    void schedule(TaskDefinition task);
    
    // 触发任务
    void trigger(String taskCode);
    
    // 取消任务
    void cancel(String taskCode);
}

/**
 * 默认调度引擎实现
 */
@Service
@Slf4j
public class DefaultScheduleEngine implements ScheduleEngine {
    @Autowired
    private HandlerManager handlerManager;
    
    @Autowired
    private TaskDispatcher taskDispatcher;
    
    @Override
    public void schedule(TaskDefinition task) {
        // 获取处理器
        TaskHandler handler = handlerManager.getHandler(task.getType());
        if (handler == null) {
            throw new TaskException("No handler found for task type: " + task.getType());
        }
        
        // 创建任务上下文
        TaskContext context = createContext(task);
        
        // 分发任务
        taskDispatcher.dispatch(context);
    }
}
```

### 4\. 业务处理器示例

```java
/**
 * 企业任务处理器
 */
@Component
public class EnterpriseTaskHandler extends AbstractTaskHandler {
    @Autowired
    private EnterpriseProcessor processor;
    
    @Override
    public String getType() {
        return "enterprise";
    }
    
    @Override
    public TaskResult handle(TaskContext context) {
        // 1. 任务分解
        List<SubTask> subTasks = splitTask(context);
        
        // 2. 分发子任务
        dispatchSubTasks(subTasks);
        
        // 3. 处理结果聚合
        return aggregateResults(subTasks);
    }
}
```

### 5\. 触发控制器

```java
/**
 * 任务控制器
 */
@RestController
@RequestMapping("/api/task")
public class TaskController {
    @Autowired
    private ScheduleEngine scheduleEngine;
    
    @Autowired
    private TaskRegister taskRegister;
    
    /**
     * 注册任务
     */
    @PostMapping("/register")
    public Response registerTask(@RequestBody TaskDefinition task) {
        taskRegister.register(task);
        return Response.success();
    }
    
    /**
     * 手动触发任务
     */
    @PostMapping("/trigger/{taskCode}")
    public Response triggerTask(@PathVariable String taskCode) {
        scheduleEngine.trigger(taskCode);
        return Response.success();
    }
    
    /**
     * 停止任务
     */
    @PostMapping("/stop/{taskCode}")
    public Response stopTask(@PathVariable String taskCode) {
        scheduleEngine.cancel(taskCode);
        return Response.success();
    }
}
```

三、任务执行流程
========

1. 任务注册流程：

```
业务模块 -> TaskRegistry.register() -> HandlerManager.registerHandler()
```

2. 定时触发流程：

```
CronTrigger.shouldTrigger() 
  -> ScheduleEngine.schedule() 
    -> TaskHandler.handle() 
      -> TaskDispatcher.dispatch()
```

3. 手动触发流程：

```
TaskController.triggerTask() 
  -> ScheduleEngine.trigger() 
    -> TaskHandler.handle() 
      -> TaskDispatcher.dispatch()
```

4. 消息分发流程：

```
TaskDispatcher.dispatch() 
  -> RabbitMQ 
    -> TaskConsumer.onMessage() 
      -> TaskHandler.handle()
```

这样的设计：

1. 支持多种触发方式
2. 便于扩展业务处理器
3. 统一的任务分发机制
4. 灵活的注册管理
5. 清晰的职责划分

需要我详细说明某个部分的实现吗？

## Prompt:

奇怪xml记录和任务表你丢哪些了，请完成输出你要改造后的定时任务和mq部分的目录，以及说明业务流程

## Response:

您说得对，我重新整理下定时任务和MQ相关的完整结构设计。

一、目录结构
======

```
platform-collect/
├── collect-core/
│   ├── task/                               # 任务模块
│   │   ├── config/                         # 配置
│   │   │   ├── TaskConfiguration.java      # 任务配置类
│   │   │   └── SchedulerConfiguration.java # 调度器配置
│   │   ├── mapper/                         # 数据访问层
│   │   │   ├── TaskConfigMapper.java       # 任务配置Mapper
│   │   │   ├── TaskInstanceMapper.java     # 任务实例Mapper
│   │   │   └── TaskLogMapper.java          # 任务日志Mapper
│   │   ├── xml/                            # MyBatis映射文件
│   │   │   ├── TaskConfigMapper.xml        # 任务配置映射
│   │   │   ├── TaskInstanceMapper.xml      # 任务实例映射
│   │   │   └── TaskLogMapper.xml           # 任务日志映射
│   │   ├── entity/                         # 实体类
│   │   │   ├── TaskConfig.java             # 任务配置
│   │   │   ├── TaskInstance.java           # 任务实例 
│   │   │   └── TaskLog.java                # 任务日志
│   │   ├── handler/                        # 处理器
│   │   │   ├── TaskHandler.java            # 处理器接口
│   │   │   ├── AbstractTaskHandler.java    # 抽象处理器
│   │   │   └── HandlerManager.java         # 处理器管理
│   │   └── service/                        # 服务层
│   │       ├── TaskConfigService.java      # 配置服务
│   │       └── TaskExecuteService.java     # 执行服务
│   │
│   ├── mq/                                 # MQ模块 
│   │   ├── config/                         # MQ配置
│   │   │   ├── RabbitConfig.java          # RabbitMQ配置
│   │   │   └── QueueConfig.java           # 队列配置
│   │   ├── constant/                       # 常量定义 
│   │   │   └── QueueConstant.java         # 队列常量
│   │   ├── producer/                       # 生产者
│   │   │   ├── TaskProducer.java          # 任务生产者
│   │   │   └── ResultProducer.java        # 结果生产者
│   │   ├── consumer/                       # 消费者
│   │   │   ├── TaskConsumer.java          # 任务消费者
│   │   │   └── ResultConsumer.java        # 结果消费者
│   │   └── message/                        # 消息定义
│   │       ├── TaskMessage.java            # 任务消息
│   │       └── ResultMessage.java          # 结果消息
```

二、数据库表设计
========

```sql
-- 任务配置表
CREATE TABLE task_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_code VARCHAR(50) NOT NULL COMMENT '任务编码',
    task_name VARCHAR(100) NOT NULL COMMENT '任务名称',
    task_handler VARCHAR(100) NOT NULL COMMENT '任务处理器',
    task_param TEXT COMMENT '任务参数(JSON)',
    cron_expr VARCHAR(50) COMMENT 'cron表达式',
    priority INT DEFAULT 5 COMMENT '优先级(1-10)',
    retry_count INT DEFAULT 0 COMMENT '重试次数',
    timeout INT DEFAULT 0 COMMENT '超时时间(秒)',
    status TINYINT DEFAULT 1 COMMENT '状态:0-禁用,1-启用',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_task_code (task_code)
) COMMENT='任务配置表';

-- 任务实例表
CREATE TABLE task_instance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    instance_id VARCHAR(50) NOT NULL COMMENT '实例ID',
    task_code VARCHAR(50) NOT NULL COMMENT '任务编码',
    parent_id VARCHAR(50) COMMENT '父实例ID',
    shard_index INT COMMENT '分片索引',
    shard_total INT COMMENT '分片总数',  
    status TINYINT DEFAULT 0 COMMENT '状态:0-初始,1-执行中,2-成功,3-失败,4-超时',
    progress INT DEFAULT 0 COMMENT '执行进度(0-100)',
    error_msg TEXT COMMENT '错误信息',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_instance_id (instance_id),
    KEY idx_task_code (task_code),
    KEY idx_parent_id (parent_id)
) COMMENT='任务实例表';

-- 任务日志表
CREATE TABLE task_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY, 
    instance_id VARCHAR(50) NOT NULL COMMENT '实例ID',
    task_code VARCHAR(50) NOT NULL COMMENT '任务编码',
    log_type TINYINT NOT NULL COMMENT '日志类型:1-开始,2-进度,3-结果,4-错误',
    log_content TEXT COMMENT '日志内容',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    KEY idx_instance_id (instance_id),
    KEY idx_task_code (task_code)
) COMMENT='任务日志表';
```

三、主要业务流程
========

### 1\. 任务注册流程

```
1. 系统启动时自动扫描所有@TaskHandler注解的处理器
2. HandlerManager注册所有处理器
3. 任务配置录入到task_config表
```

### 2\. 定时任务触发流程

```
TaskScheduler 
  -> 扫描task_config表获取需要执行的任务
  -> 创建TaskInstance记录
  -> 发送TaskMessage到RabbitMQ
  -> TaskConsumer消费消息
  -> HandlerManager获取对应的TaskHandler
  -> TaskHandler.handle()执行任务
  -> 更新TaskInstance状态和进度
  -> 记录TaskLog执行日志
```

### 3\. 手动触发流程

```
TaskController.trigger()
  -> TaskExecuteService创建TaskInstance
  -> 发送TaskMessage到RabbitMQ
  -> 后续流程同定时触发
```

### 4\. 任务分片流程

```
TaskHandler.handle()
  -> 判断是否需要分片
  -> 创建子任务TaskInstance(parent_id关联)
  -> 发送子任务消息到RabbitMQ
  -> 子任务并行执行
  -> 跟踪子任务进度
  -> 聚合子任务结果
```

### 5\. RabbitMQ队列设计

```
Exchanges:
- task.exchange：任务交换机
- result.exchange：结果交换机

Queues:
- task.main.queue：主任务队列
- task.sub.queue：子任务队列
- task.retry.queue：重试队列
- task.dlq.queue：死信队列
- result.queue：结果队列

RoutingKeys:
- task.main：主任务路由
- task.sub：子任务路由
- task.retry：重试路由
- task.dlq：死信路由
- result：结果路由
```

需要我详细说明某个具体实现吗？比如：

1. 任务处理器的具体实现
2. 消息队列的配置和处理
3. 任务分片的实现
4. 进度跟踪的实现
5. 失败重试机制

## Prompt:

配置表应该如何填写记录，然后顺序开始分段输出代码

## Response:

好的，首先说明任务配置表的关键字段设计和示例记录。

一、任务配置表(task\_config)示例记录
=========================

```sql
INSERT INTO task_config (
    task_code,         -- 任务编码
    task_name,         -- 任务名称
    task_handler,      -- 处理器类型标识
    task_param,        -- JSON格式的任务参数
    cron_expr,         -- CRON表达式
    priority,          -- 优先级(1-10)
    retry_count,       -- 重试次数
    timeout,           -- 超时时间(秒)
    status,           -- 状态:0-禁用,1-启用
    remark            -- 备注
) VALUES 
-- 1. 企业数据采集任务
(
    'enterprise_collect',           
    '企业数据定时采集',              
    'enterprise',                   
    '{
        "batchSize": 100,
        "enableSplit": true,
        "splitSize": 1000,
        "retryInterval": 300,
        "sourceType": "api"
    }',
    '0 0 1 * * ?',                 -- 每天凌晨1点执行
    5,                             -- 中等优先级
    3,                             -- 最多重试3次
    7200,                          -- 2小时超时
    1,                             -- 启用状态
    '每日定时采集企业数据'
),

-- 2. 金融数据增量同步任务
(
    'finance_sync',
    '金融数据增量同步',
    'finance',
    '{
        "syncMode": "incremental",
        "timeRange": "1d",
        "batchSize": 500,
        "enableValidation": true
    }',
    '0 */30 * * * ?',              -- 每30分钟执行
    8,                             -- 较高优先级
    5,                             -- 最多重试5次
    1800,                          -- 30分钟超时
    1,
    '定时增量同步金融数据'
),

-- 3. 医疗数据备份任务
(
    'medical_backup',
    '医疗数据备份',
    'medical',
    '{
        "backupType": "full",
        "compression": true,
        "retention": "7d",
        "validateBackup": true
    }',
    '0 0 2 * * ?',                 -- 每天凌晨2点执行
    3,                             -- 较低优先级
    2,                             -- 最多重试2次
    14400,                         -- 4小时超时
    1,
    '医疗数据定时备份'
);
```

让我们开始分段实现核心代码。首先从任务配置和实体类开始实现，您觉得可以吗？

接下来的实现顺序建议：

1. 任务实体类和Mapper
2. 任务服务层
3. RabbitMQ配置和消息定义
4. 任务处理器抽象类和管理器
5. 任务调度器和触发器
6. 消息生产者和消费者

您觉得这个顺序合适吗？我们可以开始第一部分的代码实现。

## Prompt:

是的

## Response:

好的，让我们从任务相关的实体类和Mapper开始实现。

### 1\. 首先是任务实体类

```java
// 1.1 TaskConfig.java - 任务配置实体
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor 
public class TaskConfig {
    private Long id;
    private String taskCode;        // 任务编码
    private String taskName;        // 任务名称
    private String taskHandler;     // 任务处理器
    private String taskParam;       // 任务参数(JSON)
    private String cronExpr;        // cron表达式
    private Integer priority;       // 优先级(1-10)
    private Integer retryCount;     // 重试次数
    private Integer timeout;        // 超时时间(秒)
    private Integer status;         // 状态:0-禁用,1-启用
    private String remark;          // 备注
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 更新时间

    // 获取任务参数的泛型方法
    public <T> T getTaskParamObject(Class<T> clazz) {
        if (StringUtils.hasText(this.taskParam)) {
            return JsonUtils.fromJson(this.taskParam, clazz);
        }
        return null;
    }
}

// 1.2 TaskInstance.java - 任务实例实体
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskInstance {
    private Long id;
    private String instanceId;      // 实例ID
    private String taskCode;        // 任务编码
    private String parentId;        // 父实例ID
    private Integer shardIndex;     // 分片索引
    private Integer shardTotal;     // 分片总数
    private Integer status;         // 状态
    private Integer progress;       // 进度(0-100)
    private String errorMsg;        // 错误信息
    private String hostName;        // 执行机器
    private LocalDateTime startTime;  // 开始时间
    private LocalDateTime endTime;    // 结束时间
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 更新时间

    // 判断是否为子任务
    public boolean isSubTask() {
        return StringUtils.hasText(this.parentId);
    }

    // 是否执行完成
    public boolean isCompleted() {
        return TaskStatusEnum.isCompleted(this.status);
    }
}

// 1.3 TaskLog.java - 任务日志实体
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskLog {
    private Long id;
    private String instanceId;      // 实例ID
    private String taskCode;        // 任务编码
    private Integer logType;        // 日志类型
    private String logContent;      // 日志内容
    private LocalDateTime createTime; // 创建时间

    // 便捷的日志创建方法
    public static TaskLog of(String instanceId, String taskCode, 
                           LogTypeEnum logType, String content) {
        return TaskLog.builder()
                .instanceId(instanceId)
                .taskCode(taskCode)
                .logType(logType.getCode())
                .logContent(content)
                .createTime(LocalDateTime.now())
                .build();
    }
}
```

### 2\. 相关的枚举类

```java
// 2.1 TaskStatusEnum.java - 任务状态枚举
@Getter
@AllArgsConstructor
public enum TaskStatusEnum {
    INIT(0, "初始化"),
    RUNNING(1, "执行中"),
    SUCCESS(2, "成功"),
    FAILED(3, "失败"),
    TIMEOUT(4, "超时"),
    CANCELLED(5, "已取消");

    private final Integer code;
    private final String desc;

    public static TaskStatusEnum of(Integer code) {
        for (TaskStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown task status code: " + code);
    }

    public static boolean isCompleted(Integer status) {
        return Arrays.asList(
            SUCCESS.code, FAILED.code, TIMEOUT.code, CANCELLED.code
        ).contains(status);
    }
}

// 2.2 LogTypeEnum.java - 日志类型枚举
@Getter
@AllArgsConstructor
public enum LogTypeEnum {
    START(1, "开始执行"),
    PROGRESS(2, "执行进度"),
    RESULT(3, "执行结果"),
    ERROR(4, "执行错误");

    private final Integer code;
    private final String desc;
}
```

### 3\. Mapper接口

```java
// 3.1 TaskConfigMapper.java
@Mapper
public interface TaskConfigMapper {
    // 插入配置
    void insert(TaskConfig config);

    // 更新配置
    void update(TaskConfig config);

    // 根据编码查询
    TaskConfig selectByCode(String taskCode);

    // 查询所有启用的任务
    List<TaskConfig> selectEnabled();

    // 更新任务状态
    void updateStatus(@Param("taskCode") String taskCode, 
                     @Param("status") Integer status);

    // 删除任务
    void deleteByCode(String taskCode);
}

// 3.2 TaskInstanceMapper.java
@Mapper
public interface TaskInstanceMapper {
    // 插入实例
    void insert(TaskInstance instance);

    // 更新状态
    void updateStatus(@Param("instanceId") String instanceId,
                     @Param("status") Integer status,
                     @Param("errorMsg") String errorMsg);

    // 更新进度
    void updateProgress(@Param("instanceId") String instanceId,
                       @Param("progress") Integer progress);

    // 更新结束时间
    void updateEndTime(@Param("instanceId") String instanceId,
                      @Param("endTime") LocalDateTime endTime);

    // 根据实例ID查询
    TaskInstance selectByInstanceId(String instanceId);

    // 查询子任务
    List<TaskInstance> selectByParentId(String parentId);

    // 查询运行中的任务
    List<TaskInstance> selectRunning();
}

// 3.3 TaskLogMapper.java
@Mapper
public interface TaskLogMapper {
    // 插入日志
    void insert(TaskLog log);

    // 批量插入
    void batchInsert(@Param("logs") List<TaskLog> logs);

    // 查询实例日志
    List<TaskLog> selectByInstanceId(String instanceId);

    // 清理历史日志
    int cleanHistoryLogs(@Param("daysBefore") int daysBefore);
}
```

需要我继续提供对应的XML映射文件实现吗？或者我们可以继续下一部分的实现。

## Prompt:

继续

## Response:

好的，让我们继续实现Mapper的XML映射文件。

### 4\. Mapper XML文件

```xml
<!-- 4.1 TaskConfigMapper.xml -->
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" 
    "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.study.collect.core.task.mapper.TaskConfigMapper">
    
    <resultMap id="taskConfigMap" type="TaskConfig">
        <id column="id" property="id"/>
        <result column="task_code" property="taskCode"/>
        <result column="task_name" property="taskName"/>
        <result column="task_handler" property="taskHandler"/>
        <result column="task_param" property="taskParam"/>
        <result column="cron_expr" property="cronExpr"/>
        <result column="priority" property="priority"/>
        <result column="retry_count" property="retryCount"/>
        <result column="timeout" property="timeout"/>
        <result column="status" property="status"/>
        <result column="remark" property="remark"/>
        <result column="create_time" property="createTime"/>
        <result column="update_time" property="updateTime"/>
    </resultMap>

    <insert id="insert" parameterType="TaskConfig" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO task_config (
            task_code, task_name, task_handler, task_param,
            cron_expr, priority, retry_count, timeout,
            status, remark
        ) VALUES (
            #{taskCode}, #{taskName}, #{taskHandler}, #{taskParam},
            #{cronExpr}, #{priority}, #{retryCount}, #{timeout},
            #{status}, #{remark}
        )
    </insert>

    <update id="update" parameterType="TaskConfig">
        UPDATE task_config
        <set>
            <if test="taskName != null">task_name = #{taskName},</if>
            <if test="taskHandler != null">task_handler = #{taskHandler},</if>
            <if test="taskParam != null">task_param = #{taskParam},</if>
            <if test="cronExpr != null">cron_expr = #{cronExpr},</if>
            <if test="priority != null">priority = #{priority},</if>
            <if test="retryCount != null">retry_count = #{retryCount},</if>
            <if test="timeout != null">timeout = #{timeout},</if>
            <if test="status != null">status = #{status},</if>
            <if test="remark != null">remark = #{remark},</if>
            update_time = CURRENT_TIMESTAMP
        </set>
        WHERE task_code = #{taskCode}
    </update>

    <select id="selectByCode" resultMap="taskConfigMap">
        SELECT * FROM task_config WHERE task_code = #{taskCode}
    </select>

    <select id="selectEnabled" resultMap="taskConfigMap">
        SELECT * FROM task_config WHERE status = 1
    </select>

    <update id="updateStatus">
        UPDATE task_config 
        SET status = #{status},
            update_time = CURRENT_TIMESTAMP
        WHERE task_code = #{taskCode}
    </update>

    <delete id="deleteByCode">
        DELETE FROM task_config WHERE task_code = #{taskCode}
    </delete>
</mapper>

<!-- 4.2 TaskInstanceMapper.xml -->
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" 
    "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.study.collect.core.task.mapper.TaskInstanceMapper">
    
    <resultMap id="taskInstanceMap" type="TaskInstance">
        <id column="id" property="id"/>
        <result column="instance_id" property="instanceId"/>
        <result column="task_code" property="taskCode"/>
        <result column="parent_id" property="parentId"/>
        <result column="shard_index" property="shardIndex"/>
        <result column="shard_total" property="shardTotal"/>
        <result column="status" property="status"/>
        <result column="progress" property="progress"/>
        <result column="error_msg" property="errorMsg"/>
        <result column="host_name" property="hostName"/>
        <result column="start_time" property="startTime"/>
        <result column="end_time" property="endTime"/>
        <result column="create_time" property="createTime"/>
        <result column="update_time" property="updateTime"/>
    </resultMap>

    <insert id="insert" parameterType="TaskInstance" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO task_instance (
            instance_id, task_code, parent_id, shard_index,
            shard_total, status, progress, host_name,
            start_time
        ) VALUES (
            #{instanceId}, #{taskCode}, #{parentId}, #{shardIndex},
            #{shardTotal}, #{status}, #{progress}, #{hostName},
            #{startTime}
        )
    </insert>

    <update id="updateStatus">
        UPDATE task_instance
        SET status = #{status},
            <if test="errorMsg != null">error_msg = #{errorMsg},</if>
            update_time = CURRENT_TIMESTAMP
        WHERE instance_id = #{instanceId}
    </update>

    <update id="updateProgress">
        UPDATE task_instance
        SET progress = #{progress},
            update_time = CURRENT_TIMESTAMP
        WHERE instance_id = #{instanceId}
    </update>

    <update id="updateEndTime">
        UPDATE task_instance
        SET end_time = #{endTime},
            update_time = CURRENT_TIMESTAMP
        WHERE instance_id = #{instanceId}
    </update>

    <select id="selectByInstanceId" resultMap="taskInstanceMap">
        SELECT * FROM task_instance WHERE instance_id = #{instanceId}
    </select>

    <select id="selectByParentId" resultMap="taskInstanceMap">
        SELECT * FROM task_instance 
        WHERE parent_id = #{parentId}
        ORDER BY shard_index ASC
    </select>

    <select id="selectRunning" resultMap="taskInstanceMap">
        SELECT * FROM task_instance
        WHERE status = #{@com.study.collect.core.task.enums.TaskStatusEnum@RUNNING.getCode()}
        ORDER BY start_time ASC
    </select>
</mapper>

<!-- 4.3 TaskLogMapper.xml -->
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" 
    "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.study.collect.core.task.mapper.TaskLogMapper">
    
    <resultMap id="taskLogMap" type="TaskLog">
        <id column="id" property="id"/>
        <result column="instance_id" property="instanceId"/>
        <result column="task_code" property="taskCode"/>
        <result column="log_type" property="logType"/>
        <result column="log_content" property="logContent"/>
        <result column="create_time" property="createTime"/>
    </resultMap>

    <insert id="insert" parameterType="TaskLog">
        INSERT INTO task_log (
            instance_id, task_code, log_type, log_content
        ) VALUES (
            #{instanceId}, #{taskCode}, #{logType}, #{logContent}
        )
    </insert>

    <insert id="batchInsert">
        INSERT INTO task_log (
            instance_id, task_code, log_type, log_content
        ) VALUES
        <foreach collection="logs" item="log" separator=",">
            (#{log.instanceId}, #{log.taskCode}, #{log.logType}, #{log.logContent})
        </foreach>
    </insert>

    <select id="selectByInstanceId" resultMap="taskLogMap">
        SELECT * FROM task_log 
        WHERE instance_id = #{instanceId}
        ORDER BY create_time ASC
    </select>

    <delete id="cleanHistoryLogs">
        DELETE FROM task_log 
        WHERE create_time < DATE_SUB(CURRENT_TIMESTAMP, INTERVAL #{daysBefore} DAY)
    </delete>
</mapper>
```

### 5\. MyBatis配置类

```java
@Configuration
@MapperScan("com.study.collect.core.task.mapper")
public class MyBatisConfig {

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        
        // 设置驼峰命名转换
        org.apache.ibatis.session.Configuration configuration = 
            new org.apache.ibatis.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        sessionFactory.setConfiguration(configuration);
        
        // 设置XML映射文件路径
        PathMatchingResourcePatternResolver resolver = 
            new PathMatchingResourcePatternResolver();
        sessionFactory.setMapperLocations(
            resolver.getResources("classpath:mapper/*.xml"));
        
        // 设置类型别名包
        sessionFactory.setTypeAliasesPackage("com.study.collect.core.task.entity");
        
        return sessionFactory.getObject();
    }
}
```

这样我们完成了数据访问层的实现。接下来我们可以开始实现服务层的代码，您要继续吗？

服务层将包含：

1. TaskConfigService - 任务配置服务
2. TaskExecuteService - 任务执行服务
3. TaskLogService - 任务日志服务

## Prompt:

继续

## Response:

好的，让我们实现服务层代码。

### 1\. TaskConfigService - 任务配置服务

```java
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskConfigService {
    
    private final TaskConfigMapper taskConfigMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    
    private static final String TASK_CONFIG_CACHE_KEY = "task:config:";
    private static final long CACHE_EXPIRE_TIME = 3600L; // 1小时

    /**
     * 保存任务配置
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveTaskConfig(TaskConfig config) {
        // 参数校验
        validateTaskConfig(config);
        
        TaskConfig existConfig = taskConfigMapper.selectByCode(config.getTaskCode());
        if (existConfig == null) {
            log.info("新增任务配置: {}", config.getTaskCode());
            taskConfigMapper.insert(config);
        } else {
            log.info("更新任务配置: {}", config.getTaskCode());
            taskConfigMapper.update(config);
        }
        
        // 更新缓存
        refreshCache(config);
    }

    /**
     * 获取任务配置
     */
    public TaskConfig getTaskConfig(String taskCode) {
        // 先从缓存获取
        String cacheKey = TASK_CONFIG_CACHE_KEY + taskCode;
        TaskConfig config = (TaskConfig) redisTemplate.opsForValue().get(cacheKey);
        
        if (config == null) {
            // 缓存未命中，从数据库获取
            config = taskConfigMapper.selectByCode(taskCode);
            if (config != null) {
                // 放入缓存
                redisTemplate.opsForValue().set(cacheKey, config, CACHE_EXPIRE_TIME, TimeUnit.SECONDS);
            }
        }
        
        return config;
    }

    /**
     * 获取所有启用的任务配置
     */
    public List<TaskConfig> getEnabledTaskConfigs() {
        return taskConfigMapper.selectEnabled();
    }

    /**
     * 启用任务
     */
    @Transactional(rollbackFor = Exception.class)
    public void enableTask(String taskCode) {
        taskConfigMapper.updateStatus(taskCode, TaskStatusEnum.INIT.getCode());
        log.info("启用任务: {}", taskCode);
        
        // 刷新缓存
        refreshCache(taskConfigMapper.selectByCode(taskCode));
    }

    /**
     * 禁用任务
     */
    @Transactional(rollbackFor = Exception.class)
    public void disableTask(String taskCode) {
        taskConfigMapper.updateStatus(taskCode, TaskStatusEnum.CANCELLED.getCode());
        log.info("禁用任务: {}", taskCode);
        
        // 刷新缓存
        refreshCache(taskConfigMapper.selectByCode(taskCode));
    }

    /**
     * 删除任务
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteTask(String taskCode) {
        taskConfigMapper.deleteByCode(taskCode);
        log.info("删除任务: {}", taskCode);
        
        // 删除缓存
        redisTemplate.delete(TASK_CONFIG_CACHE_KEY + taskCode);
    }

    /**
     * 刷新缓存
     */
    private void refreshCache(TaskConfig config) {
        String cacheKey = TASK_CONFIG_CACHE_KEY + config.getTaskCode();
        redisTemplate.opsForValue().set(cacheKey, config, CACHE_EXPIRE_TIME, TimeUnit.SECONDS);
    }

    /**
     * 配置参数校验
     */
    private void validateTaskConfig(TaskConfig config) {
        // 必填参数校验
        Assert.hasText(config.getTaskCode(), "任务编码不能为空");
        Assert.hasText(config.getTaskName(), "任务名称不能为空");
        Assert.hasText(config.getTaskHandler(), "任务处理器不能为空");
        
        // CRON表达式校验
        if (StringUtils.hasText(config.getCronExpr())) {
            try {
                new CronExpression(config.getCronExpr());
            } catch (Exception e) {
                throw new IllegalArgumentException("无效的CRON表达式: " + config.getCronExpr());
            }
        }
        
        // 参数范围校验
        if (config.getPriority() != null) {
            Assert.isTrue(config.getPriority() >= 1 && config.getPriority() <= 10, 
                "优先级范围必须在1-10之间");
        }
        
        if (config.getRetryCount() != null) {
            Assert.isTrue(config.getRetryCount() >= 0, "重试次数不能小于0");
        }
        
        if (config.getTimeout() != null) {
            Assert.isTrue(config.getTimeout() > 0, "超时时间必须大于0");
        }
    }
}
```

### 2\. TaskExecuteService - 任务执行服务

```java
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskExecuteService {

    private final TaskInstanceMapper taskInstanceMapper;
    private final TaskLogMapper taskLogMapper;
    private final TaskConfigService taskConfigService;
    private final TaskProducer taskProducer;

    /**
     * 创建任务实例
     */
    @Transactional(rollbackFor = Exception.class)
    public TaskInstance createTaskInstance(String taskCode, Integer shardIndex, String shardParam) {
        // 获取任务配置
        TaskConfig config = taskConfigService.getTaskConfig(taskCode);
        if (config == null) {
            throw new IllegalArgumentException("任务配置不存在: " + taskCode);
        }

        // 创建任务实例
        String instanceId = generateInstanceId(taskCode);
        TaskInstance instance = TaskInstance.builder()
                .instanceId(instanceId)
                .taskCode(taskCode)
                .shardIndex(shardIndex)
                .shardTotal(1)  // 默认不分片
                .status(TaskStatusEnum.INIT.getCode())
                .progress(0)
                .hostName(getHostName())
                .startTime(LocalDateTime.now())
                .build();

        taskInstanceMapper.insert(instance);
        
        // 记录开始日志
        recordTaskLog(instanceId, taskCode, LogTypeEnum.START, "任务开始执行");

        return instance;
    }

    /**
     * 创建子任务实例
     */
    @Transactional(rollbackFor = Exception.class)
    public TaskInstance createSubTaskInstance(String parentId, String taskCode, 
            Integer shardIndex, Integer shardTotal) {
        // 创建子任务实例
        TaskInstance instance = createTaskInstance(taskCode, shardIndex, null);
        instance.setParentId(parentId);
        instance.setShardTotal(shardTotal);
        
        taskInstanceMapper.update(instance);
        return instance;
    }

    /**
     * 更新任务状态
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateTaskStatus(String instanceId, TaskStatusEnum status, String errorMsg) {
        TaskInstance instance = taskInstanceMapper.selectByInstanceId(instanceId);
        if (instance == null) {
            throw new IllegalArgumentException("任务实例不存在: " + instanceId);
        }

        taskInstanceMapper.updateStatus(instanceId, status.getCode(), errorMsg);
        
        if (TaskStatusEnum.isCompleted(status.getCode())) {
            // 记录完成时间
            taskInstanceMapper.updateEndTime(instanceId, LocalDateTime.now());
            
            // 记录结果日志
            LogTypeEnum logType = status == TaskStatusEnum.SUCCESS ? 
                LogTypeEnum.RESULT : LogTypeEnum.ERROR;
            String logContent = status == TaskStatusEnum.SUCCESS ? 
                "任务执行成功" : "任务执行失败: " + errorMsg;
            recordTaskLog(instanceId, instance.getTaskCode(), logType, logContent);
        }
    }

    /**
     * 更新任务进度
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateTaskProgress(String instanceId, int progress) {
        TaskInstance instance = taskInstanceMapper.selectByInstanceId(instanceId);
        if (instance == null) {
            throw new IllegalArgumentException("任务实例不存在: " + instanceId);
        }

        taskInstanceMapper.updateProgress(instanceId, progress);
        
        // 记录进度日志
        recordTaskLog(instanceId, instance.getTaskCode(), LogTypeEnum.PROGRESS, 
            "当前进度: " + progress + "%");
    }

    /**
     * 获取运行中的任务
     */
    public List<TaskInstance> getRunningTasks() {
        return taskInstanceMapper.selectRunning();
    }

    /**
     * 获取子任务列表
     */
    public List<TaskInstance> getSubTasks(String parentId) {
        return taskInstanceMapper.selectByParentId(parentId);
    }

    // ... 其他辅助方法
}
```

### 3\. TaskLogService - 任务日志服务

```java
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskLogService {

    private final TaskLogMapper taskLogMapper;
    
    /**
     * 记录任务日志
     */
    public void recordLog(String instanceId, String taskCode, 
            LogTypeEnum logType, String content) {
        TaskLog taskLog = TaskLog.of(instanceId, taskCode, logType, content);
        taskLogMapper.insert(taskLog);
    }
    
    /**
     * 批量记录日志
     */
    public void recordLogs(List<TaskLog> logs) {
        if (!logs.isEmpty()) {
            taskLogMapper.batchInsert(logs);
        }
    }

    /**
     * 获取任务实例的日志
     */
    public List<TaskLog> getTaskLogs(String instanceId) {
        return taskLogMapper.selectByInstanceId(instanceId);
    }

    /**
     * 清理历史日志
     */
    @Scheduled(cron = "0 0 2 * * ?")  // 每天凌晨2点执行
    public void cleanHistoryLogs() {
        try {
            int days = 30; // 保留30天的日志
            int count = taskLogMapper.cleanHistoryLogs(days);
            log.info("清理历史日志完成, 清理数量: {}", count);
        } catch (Exception e) {
            log.error("清理历史日志失败", e);
        }
    }
}
```

这样我们完成了基础的服务层实现。接下来我们可以开始实现：

1. RabbitMQ配置和消息定义
2. 任务处理器抽象类和管理器

您觉得继续哪部分的实现？

## Prompt:

按顺序继续

## Response:

好的，让我们实现RabbitMQ相关的配置和消息定义。

### 1\. MQ消息模型定义

```java
// 1.1 BaseMessage.java - 基础消息
@Data
public abstract class BaseMessage implements Serializable {
    private String messageId;        // 消息ID
    private String type;            // 消息类型
    private Integer priority;        // 优先级
    private Integer retryCount;      // 重试次数
    private LocalDateTime createTime; // 创建时间
    
    public BaseMessage() {
        this.messageId = UUID.randomUUID().toString();
        this.createTime = LocalDateTime.now();
        this.retryCount = 0;
    }
    
    // 增加重试次数
    public void incrementRetryCount() {
        this.retryCount++;
    }
}

// 1.2 TaskMessage.java - 任务消息
@Data
@EqualsAndHashCode(callSuper = true)
public class TaskMessage extends BaseMessage {
    private String taskCode;         // 任务编码
    private String instanceId;       // 实例ID
    private String parentId;         // 父实例ID
    private Integer shardIndex;      // 分片索引
    private Integer shardTotal;      // 分片总数
    private String shardParam;       // 分片参数
    private String hostName;         // 执行机器
    
    public TaskMessage() {
        super();
        setType("TASK");
    }
}

// 1.3 TaskResultMessage.java - 任务结果消息
@Data
@EqualsAndHashCode(callSuper = true)
public class TaskResultMessage extends BaseMessage {
    private String taskCode;         // 任务编码
    private String instanceId;       // 实例ID
    private Boolean success;         // 是否成功
    private String errorMsg;         // 错误信息
    private Object result;           // 执行结果
    private LocalDateTime finishTime; // 完成时间
    
    public TaskResultMessage() {
        super();
        setType("RESULT");
    }
}
```

### 2\. RabbitMQ常量定义

```java
public class RabbitConstants {
    // 交换机
    public static final String TASK_EXCHANGE = "task.exchange";
    public static final String RESULT_EXCHANGE = "result.exchange";
    
    // 队列
    public static final String TASK_QUEUE = "task.queue";
    public static final String TASK_RETRY_QUEUE = "task.retry.queue";
    public static final String TASK_DLQ = "task.dlq";
    public static final String RESULT_QUEUE = "result.queue";
    
    // 路由键
    public static final String TASK_ROUTING_KEY = "task.#";
    public static final String RESULT_ROUTING_KEY = "result.#";
    public static final String RETRY_ROUTING_KEY = "task.retry";
    public static final String DLQ_ROUTING_KEY = "task.dlq";
    
    // 消息属性
    public static final String PROPERTY_RETRY_COUNT = "retry-count";
    public static final int MAX_RETRY_COUNT = 3;
    public static final int RETRY_INTERVAL = 60000; // 60秒
}
```

### 3\. RabbitMQ配置类

```java
@Configuration
@EnableRabbit
@RequiredArgsConstructor
public class RabbitConfig {

    private final RabbitProperties rabbitProperties;

    /**
     * 配置连接工厂
     */
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(rabbitProperties.getHost());
        connectionFactory.setPort(rabbitProperties.getPort());
        connectionFactory.setUsername(rabbitProperties.getUsername());
        connectionFactory.setPassword(rabbitProperties.getPassword());
        connectionFactory.setVirtualHost(rabbitProperties.getVirtualHost());
        
        // 设置连接池
        connectionFactory.setChannelCacheSize(25);
        connectionFactory.setConnectionCacheSize(1);
        
        // 设置确认回调
        connectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
        connectionFactory.setPublisherReturns(true);
        
        return connectionFactory;
    }

    /**
     * 配置RabbitTemplate
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        
        // 消息转换器
        Jackson2JsonMessageConverter messageConverter = new Jackson2JsonMessageConverter();
        template.setMessageConverter(messageConverter);
        
        // 开启mandatory，确保消息路由到队列，否则触发return回调
        template.setMandatory(true);
        
        // 确认回调
        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                log.error("消息发送失败: {}", cause);
            }
        });
        
        // 返回回调
        template.setReturnsCallback(returned -> {
            log.error("消息路由失败: exchange={}, routingKey={}, replyCode={}, replyText={}",
                    returned.getExchange(), returned.getRoutingKey(),
                    returned.getReplyCode(), returned.getReplyText());
        });
        
        return template;
    }

    /**
     * 任务交换机
     */
    @Bean
    public TopicExchange taskExchange() {
        return ExchangeBuilder.topicExchange(RabbitConstants.TASK_EXCHANGE)
                .durable(true)
                .build();
    }

    /**
     * 结果交换机
     */
    @Bean
    public TopicExchange resultExchange() {
        return ExchangeBuilder.topicExchange(RabbitConstants.RESULT_EXCHANGE)
                .durable(true)
                .build();
    }

    /**
     * 任务队列
     */
    @Bean
    public Queue taskQueue() {
        return QueueBuilder.durable(RabbitConstants.TASK_QUEUE)
                .withArguments(new HashMap<String, Object>() {{
                    // 设置死信交换机
                    put("x-dead-letter-exchange", RabbitConstants.TASK_EXCHANGE);
                    put("x-dead-letter-routing-key", RabbitConstants.DLQ_ROUTING_KEY);
                    // 设置消息过期时间 (24小时)
                    put("x-message-ttl", 86400000);
                    // 设置消息最大优先级
                    put("x-max-priority", 10);
                }})
                .build();
    }

    /**
     * 重试队列
     */
    @Bean
    public Queue retryQueue() {
        return QueueBuilder.durable(RabbitConstants.TASK_RETRY_QUEUE)
                .withArguments(new HashMap<String, Object>() {{
                    put("x-dead-letter-exchange", RabbitConstants.TASK_EXCHANGE);
                    put("x-dead-letter-routing-key", RabbitConstants.TASK_ROUTING_KEY);
                    put("x-message-ttl", RabbitConstants.RETRY_INTERVAL);
                }})
                .build();
    }

    /**
     * 死信队列
     */
    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(RabbitConstants.TASK_DLQ)
                .build();
    }

    /**
     * 结果队列
     */
    @Bean
    public Queue resultQueue() {
        return QueueBuilder.durable(RabbitConstants.RESULT_QUEUE)
                .build();
    }

    /**
     * 绑定关系
     */
    @Bean
    public List<Binding> bindings() {
        return Arrays.asList(
            // 任务队列绑定
            BindingBuilder.bind(taskQueue())
                    .to(taskExchange())
                    .with(RabbitConstants.TASK_ROUTING_KEY),
            // 重试队列绑定    
            BindingBuilder.bind(retryQueue())
                    .to(taskExchange())
                    .with(RabbitConstants.RETRY_ROUTING_KEY),
            // 死信队列绑定    
            BindingBuilder.bind(deadLetterQueue())
                    .to(taskExchange())
                    .with(RabbitConstants.DLQ_ROUTING_KEY),
            // 结果队列绑定    
            BindingBuilder.bind(resultQueue())
                    .to(resultExchange())
                    .with(RabbitConstants.RESULT_ROUTING_KEY)
        );
    }
}
```

### 4\. 消息监听器配置

```java
@Configuration
public class RabbitListenerConfig {

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = 
            new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        
        // 设置消息转换器
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        
        // 设置并发消费者数量
        factory.setConcurrentConsumers(5);
        factory.setMaxConcurrentConsumers(10);
        
        // 设置消费者标签策略
        factory.setConsumerTagStrategy(queue -> queue + "_" + UUID.randomUUID());
        
        // 设置消息确认模式
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        
        // 设置预取数量
        factory.setPrefetchCount(1);
        
        // 设置消费者监听器
        factory.setErrorHandler(throwable -> 
            log.error("消息消费失败", throwable));
            
        return factory;
    }
}
```

现在我们完成了RabbitMQ相关的配置，接下来我们可以实现：

1. 任务处理器抽象类和管理器
2. 消息生产者和消费者

您要继续吗？

## Prompt:

按顺序继续

## Response:

好的，让我们实现任务处理器抽象类和管理器。

### 1\. 任务处理器接口和抽象类

```java
// 1.1 TaskHandler.java - 任务处理器接口
public interface TaskHandler {
    /**
     * 获取处理器类型
     */
    String getType();
    
    /**
     * 执行任务
     */
    TaskResult handle(TaskContext context);
    
    /**
     * 是否可以处理该任务
     */
    boolean canHandle(String taskType);
    
    /**
     * 获取处理器优先级
     */
    default int getOrder() {
        return 0;
    }
}

// 1.2 AbstractTaskHandler.java - 任务处理器抽象类
@Slf4j
public abstract class AbstractTaskHandler implements TaskHandler {

    @Autowired
    protected TaskExecuteService taskExecuteService;
    
    @Override
    public boolean canHandle(String taskType) {
        return getType().equals(taskType);
    }

    @Override
    public TaskResult handle(TaskContext context) {
        String taskId = context.getTaskId();
        String instanceId = context.getInstanceId();
        
        log.info("开始执行任务: taskId={}, instanceId={}", taskId, instanceId);
        
        try {
            // 前置处理
            beforeHandle(context);
            
            // 执行任务
            TaskResult result = doHandle(context);
            
            // 后置处理
            afterHandle(context, result);
            
            log.info("任务执行完成: taskId={}, instanceId={}", taskId, instanceId);
            return result;
            
        } catch (Exception e) {
            log.error("任务执行异常: taskId=" + taskId + ", instanceId=" + instanceId, e);
            return TaskResult.failure(instanceId, e.getMessage());
        }
    }

    /**
     * 前置处理
     */
    protected void beforeHandle(TaskContext context) {
        // 更新任务状态为执行中
        taskExecuteService.updateTaskStatus(
            context.getInstanceId(), 
            TaskStatusEnum.RUNNING, 
            null
        );
    }

    /**
     * 具体任务处理逻辑，由子类实现
     */
    protected abstract TaskResult doHandle(TaskContext context);

    /**
     * 后置处理
     */
    protected void afterHandle(TaskContext context, TaskResult result) {
        // 更新任务状态和进度
        if (result.isSuccess()) {
            taskExecuteService.updateTaskStatus(
                context.getInstanceId(),
                TaskStatusEnum.SUCCESS,
                null
            );
            taskExecuteService.updateTaskProgress(
                context.getInstanceId(),
                100
            );
        } else {
            taskExecuteService.updateTaskStatus(
                context.getInstanceId(),
                TaskStatusEnum.FAILED,
                result.getErrorMessage()
            );
        }
    }

    /**
     * 更新任务进度
     */
    protected void updateProgress(String instanceId, int progress) {
        taskExecuteService.updateTaskProgress(instanceId, progress);
    }
}

// 1.3 TaskContext.java - 任务上下文
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskContext {
    private String taskId;           // 任务ID
    private String instanceId;       // 实例ID
    private String parentId;         // 父实例ID(子任务使用)
    private Integer shardIndex;      // 分片索引
    private Integer shardTotal;      // 分片总数
    private Map<String, Object> params;  // 任务参数
    private LocalDateTime startTime;     // 开始时间

    // 参数获取方法
    @SuppressWarnings("unchecked")
    public <T> T getParam(String key) {
        return params != null ? (T) params.get(key) : null;
    }

    // 设置参数方法
    public void setParam(String key, Object value) {
        if (params == null) {
            params = new HashMap<>();
        }
        params.put(key, value);
    }
}

// 1.4 TaskResult.java - 任务结果
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResult {
    private String instanceId;       // 实例ID
    private boolean success;         // 是否成功
    private String errorMessage;     // 错误信息
    private Object data;            // 结果数据
    private LocalDateTime finishTime; // 完成时间

    public static TaskResult success(String instanceId, Object data) {
        return TaskResult.builder()
                .instanceId(instanceId)
                .success(true)
                .data(data)
                .finishTime(LocalDateTime.now())
                .build();
    }

    public static TaskResult failure(String instanceId, String errorMessage) {
        return TaskResult.builder()
                .instanceId(instanceId)
                .success(false)
                .errorMessage(errorMessage)
                .finishTime(LocalDateTime.now())
                .build();
    }
}
```

### 2\. 任务处理器管理器

```java
@Slf4j
@Component
public class TaskHandlerManager {
    
    /**
     * 处理器注册表
     */
    private final Map<String, TaskHandler> handlers = new ConcurrentHashMap<>();
    
    /**
     * 注册处理器
     */
    public void registerHandler(TaskHandler handler) {
        String type = handler.getType();
        TaskHandler existing = handlers.putIfAbsent(type, handler);
        if (existing != null) {
            if (existing.getOrder() < handler.getOrder()) {
                handlers.put(type, handler);
                log.info("替换任务处理器: type={}, handler={}", type, handler.getClass().getName());
            }
        } else {
            log.info("注册任务处理器: type={}, handler={}", type, handler.getClass().getName());
        }
    }

    /**
     * 注册处理器列表
     */
    public void registerHandlers(List<TaskHandler> handlerList) {
        handlerList.forEach(this::registerHandler);
    }

    /**
     * 获取处理器
     */
    public TaskHandler getHandler(String type) {
        TaskHandler handler = handlers.get(type);
        if (handler == null) {
            throw new IllegalStateException("未找到任务处理器: " + type);
        }
        return handler;
    }

    /**
     * 获取所有处理器
     */
    public Collection<TaskHandler> getAllHandlers() {
        return handlers.values();
    }

    /**
     * 清除所有处理器
     */
    public void clearHandlers() {
        handlers.clear();
    }

    /**
     * Spring容器启动后自动注册处理器
     */
    @Autowired
    public void init(ApplicationContext applicationContext) {
        // 获取所有带@TaskHandler注解的Bean
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(TaskHandler.class);
        beans.values().stream()
                .filter(bean -> bean instanceof TaskHandler)
                .map(bean -> (TaskHandler) bean)
                .forEach(this::registerHandler);
    }
}

// 2.1 @TaskHandler注解
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface TaskHandler {
    /**
     * 处理器类型
     */
    String value() default "";
    
    /**
     * 处理器优先级
     */
    int order() default 0;
    
    /**
     * 是否允许并发执行
     */
    boolean concurrent() default true;
}
```

### 3\. 任务处理器示例

```java
@Slf4j
@TaskHandler(value = "sample", order = 100)
public class SampleTaskHandler extends AbstractTaskHandler {
    
    @Override
    public String getType() {
        return "sample";
    }
    
    @Override
    protected TaskResult doHandle(TaskContext context) {
        String instanceId = context.getInstanceId();
        try {
            // 模拟任务执行
            log.info("开始执行示例任务: instanceId={}", instanceId);
            
            // 更新进度
            updateProgress(instanceId, 50);
            Thread.sleep(1000);
            
            // 更新进度
            updateProgress(instanceId, 100);
            
            return TaskResult.success(instanceId, "任务执行成功");
            
        } catch (Exception e) {
            log.error("示例任务执行失败: " + instanceId, e);
            return TaskResult.failure(instanceId, e.getMessage());
        }
    }
}
```

接下来我们可以实现消息生产者和消费者部分，要继续吗？

主要包含：

1. TaskProducer - 任务消息生产者
2. TaskConsumer - 任务消息消费者
3. ResultProducer - 结果消息生产者
4. ResultConsumer - 结果消息消费者

## Prompt:

继续

## Response:

好的，让我们实现消息生产者和消费者相关的代码。

### 1\. 消息生产者

```java
// 1.1 TaskProducer.java - 任务消息生产者
@Slf4j
@Component
@RequiredArgsConstructor
public class TaskProducer {

    private final RabbitTemplate rabbitTemplate;
    private final TaskExecuteService taskExecuteService;

    /**
     * 发送任务消息
     */
    public void sendTask(TaskMessage message) {
        try {
            String messageId = message.getMessageId();
            log.info("发送任务消息: messageId={}, taskCode={}, instanceId={}", 
                messageId, message.getTaskCode(), message.getInstanceId());

            // 构建CorrelationData用于消息确认
            CorrelationData correlationData = new CorrelationData(messageId);
            correlationData.getFuture().addCallback(
                // 确认成功回调
                result -> {
                    if (result != null && !result.isAck()) {
                        log.error("任务消息发送失败: messageId={}, cause={}", 
                            messageId, result.getReason());
                        handleSendFailure(message);
                    }
                },
                // 确认异常回调
                ex -> {
                    log.error("任务消息发送异常: messageId=" + messageId, ex);
                    handleSendFailure(message);
                }
            );

            // 发送消息，设置消息属性
            rabbitTemplate.convertAndSend(
                RabbitConstants.TASK_EXCHANGE,
                RabbitConstants.TASK_ROUTING_KEY,
                message,
                msg -> {
                    MessageProperties props = msg.getMessageProperties();
                    // 设置消息优先级
                    props.setPriority(message.getPriority());
                    // 设置消息过期时间
                    props.setExpiration(String.valueOf(24 * 60 * 60 * 1000));
                    // 设置消息持久化
                    props.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                    return msg;
                },
                correlationData
            );
            
        } catch (Exception e) {
            log.error("发送任务消息异常: " + message.getMessageId(), e);
            handleSendFailure(message);
        }
    }

    /**
     * 发送重试消息
     */
    public void sendRetry(TaskMessage message) {
        try {
            // 增加重试次数
            message.incrementRetryCount();
            
            log.info("发送重试消息: messageId={}, taskCode={}, retryCount={}", 
                message.getMessageId(), message.getTaskCode(), message.getRetryCount());

            rabbitTemplate.convertAndSend(
                RabbitConstants.TASK_EXCHANGE,
                RabbitConstants.RETRY_ROUTING_KEY,
                message
            );
            
        } catch (Exception e) {
            log.error("发送重试消息异常: " + message.getMessageId(), e);
            handleSendFailure(message);
        }
    }

    /**
     * 处理消息发送失败
     */
    private void handleSendFailure(TaskMessage message) {
        String instanceId = message.getInstanceId();
        // 更新任务状态为失败
        taskExecuteService.updateTaskStatus(
            instanceId,
            TaskStatusEnum.FAILED,
            "消息发送失败"
        );
    }
}

// 1.2 ResultProducer.java - 结果消息生产者
@Slf4j
@Component
@RequiredArgsConstructor
public class ResultProducer {

    private final RabbitTemplate rabbitTemplate;

    /**
     * 发送任务结果消息
     */
    public void sendResult(TaskResultMessage message) {
        try {
            log.info("发送结果消息: messageId={}, taskCode={}, success={}", 
                message.getMessageId(), message.getTaskCode(), message.getSuccess());

            rabbitTemplate.convertAndSend(
                RabbitConstants.RESULT_EXCHANGE,
                RabbitConstants.RESULT_ROUTING_KEY,
                message
            );
            
        } catch (Exception e) {
            log.error("发送结果消息异常: " + message.getMessageId(), e);
        }
    }
}
```

### 2\. 消息消费者

```java
// 2.1 TaskConsumer.java - 任务消息消费者 
@Slf4j
@Component
@RequiredArgsConstructor
public class TaskConsumer {

    private final TaskHandlerManager handlerManager;
    private final TaskExecuteService taskExecuteService;
    private final TaskProducer taskProducer;
    private final ResultProducer resultProducer;

    @RabbitListener(
        queues = RabbitConstants.TASK_QUEUE,
        containerFactory = "rabbitListenerContainerFactory"
    )
    public void onMessage(TaskMessage message, Channel channel, 
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        
        String messageId = message.getMessageId();
        String instanceId = message.getInstanceId();
        
        try {
            log.info("收到任务消息: messageId={}, taskCode={}, instanceId={}", 
                messageId, message.getTaskCode(), instanceId);

            // 获取任务处理器
            TaskHandler handler = handlerManager.getHandler(message.getTaskCode());
            
            // 构建任务上下文
            TaskContext context = buildTaskContext(message);
            
            // 执行任务
            TaskResult result = handler.handle(context);
            
            // 处理执行结果
            handleTaskResult(message, result);
            
            // 确认消息
            channel.basicAck(deliveryTag, false);
            
        } catch (Exception e) {
            log.error("处理任务消息异常: messageId=" + messageId, e);
            // 处理异常情况
            handleError(message, channel, deliveryTag, e);
        }
    }

    /**
     * 构建任务上下文
     */
    private TaskContext buildTaskContext(TaskMessage message) {
        return TaskContext.builder()
                .taskId(message.getTaskCode())
                .instanceId(message.getInstanceId())
                .parentId(message.getParentId())
                .shardIndex(message.getShardIndex())
                .shardTotal(message.getShardTotal())
                .startTime(LocalDateTime.now())
                .build();
    }

    /**
     * 处理任务执行结果
     */
    private void handleTaskResult(TaskMessage message, TaskResult result) {
        // 构建并发送结果消息
        TaskResultMessage resultMessage = new TaskResultMessage();
        resultMessage.setTaskCode(message.getTaskCode());
        resultMessage.setInstanceId(message.getInstanceId());
        resultMessage.setSuccess(result.isSuccess());
        resultMessage.setErrorMsg(result.getErrorMessage());
        resultMessage.setResult(result.getData());
        resultMessage.setFinishTime(LocalDateTime.now());
        
        resultProducer.sendResult(resultMessage);
    }

    /**
     * 处理异常情况
     */
    private void handleError(TaskMessage message, Channel channel, 
            long deliveryTag, Exception e) {
        try {
            // 判断是否需要重试
            if (needRetry(message)) {
                // 拒绝消息并重新入队
                channel.basicReject(deliveryTag, false);
                // 发送重试消息
                taskProducer.sendRetry(message);
            } else {
                // 拒绝消息，不重新入队
                channel.basicReject(deliveryTag, false);
                // 更新任务状态为失败
                taskExecuteService.updateTaskStatus(
                    message.getInstanceId(),
                    TaskStatusEnum.FAILED,
                    e.getMessage()
                );
            }
        } catch (Exception ex) {
            log.error("处理异常情况失败: " + message.getMessageId(), ex);
        }
    }

    /**
     * 判断是否需要重试
     */
    private boolean needRetry(TaskMessage message) {
        return message.getRetryCount() < RabbitConstants.MAX_RETRY_COUNT;
    }
}

// 2.2 ResultConsumer.java - 结果消息消费者
@Slf4j
@Component
@RequiredArgsConstructor
public class ResultConsumer {

    @RabbitListener(
        queues = RabbitConstants.RESULT_QUEUE,
        containerFactory = "rabbitListenerContainerFactory"
    )
    public void onMessage(TaskResultMessage message, Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        try {
            log.info("收到结果消息: messageId={}, taskCode={}, instanceId={}, success={}", 
                message.getMessageId(), message.getTaskCode(), 
                message.getInstanceId(), message.isSuccess());

            // 处理结果消息
            processResultMessage(message);
            
            // 确认消息
            channel.basicAck(deliveryTag, false);
            
        } catch (Exception e) {
            log.error("处理结果消息异常: " + message.getMessageId(), e);
            try {
                // 拒绝消息，不重新入队
                channel.basicReject(deliveryTag, false);
            } catch (IOException ex) {
                log.error("拒绝消息失败", ex);
            }
        }
    }

    /**
     * 处理结果消息
     */
    private void processResultMessage(TaskResultMessage message) {
        // TODO: 实现结果处理逻辑
        // 例如：更新数据库状态、触发后续流程等
    }
}
```

到这里，我们完成了：

1. 消息生产者(TaskProducer, ResultProducer)的实现
2. 消息消费者(TaskConsumer, ResultConsumer)的实现
3. 完整的消息处理流程

主要特点：

1. 支持消息确认和重试机制
2. 异常处理和错误恢复
3. 结果反馈
4. 日志跟踪

是否需要补充任何部分的实现？或者我们可以开始实现调度器部分。

## Prompt:

继续

## Response:

好的，让我们实现调度器相关的代码。

### 1\. 调度器接口和配置

```java
// 1.1 TaskScheduler.java - 调度器接口
public interface TaskScheduler {
    /**
     * 启动调度器
     */
    void start();

    /**
     * 停止调度器
     */
    void stop();

    /**
     * 触发任务
     */
    void triggerTask(String taskCode);

    /**
     * 暂停任务
     */
    void pauseTask(String taskCode);

    /**
     * 恢复任务
     */
    void resumeTask(String taskCode);
}

// 1.2 SchedulerConfiguration.java - 调度器配置
@Configuration
@EnableScheduling
public class SchedulerConfiguration {

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10);
        scheduler.setThreadNamePrefix("TaskScheduler-");
        scheduler.setAwaitTerminationSeconds(60);
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setErrorHandler(throwable -> 
            log.error("调度任务执行异常", throwable));
        scheduler.setRejectedExecutionHandler((runnable, executor) -> 
            log.error("调度任务被拒绝执行"));
        return scheduler;
    }
}
```

### 2\. 调度器核心实现

```java
// 2.1 DefaultTaskScheduler.java - 默认调度器实现
@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultTaskScheduler implements TaskScheduler {

    private final TaskConfigService taskConfigService;
    private final TaskExecuteService taskExecuteService;
    private final TaskProducer taskProducer;
    private final ThreadPoolTaskScheduler scheduler;
    private final ConcurrentMap<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        start();
    }

    @PreDestroy
    public void destroy() {
        stop();
    }

    @Override
    public void start() {
        log.info("Starting task scheduler...");
        // 加载所有启用的任务配置
        List<TaskConfig> enabledTasks = taskConfigService.getEnabledTaskConfigs();
        for (TaskConfig config : enabledTasks) {
            scheduleTask(config);
        }
    }

    @Override
    public void stop() {
        log.info("Stopping task scheduler...");
        // 取消所有调度任务
        scheduledTasks.forEach((taskCode, future) -> {
            future.cancel(true);
            log.info("Cancelled scheduled task: {}", taskCode);
        });
        scheduledTasks.clear();
    }

    @Override
    public void triggerTask(String taskCode) {
        log.info("Manual trigger task: {}", taskCode);
        TaskConfig config = taskConfigService.getTaskConfig(taskCode);
        if (config != null) {
            executeTask(config);
        } else {
            log.warn("Task config not found: {}", taskCode);
        }
    }

    @Override
    public void pauseTask(String taskCode) {
        log.info("Pausing task: {}", taskCode);
        ScheduledFuture<?> future = scheduledTasks.remove(taskCode);
        if (future != null) {
            future.cancel(false);
        }
        taskConfigService.disableTask(taskCode);
    }

    @Override
    public void resumeTask(String taskCode) {
        log.info("Resuming task: {}", taskCode);
        TaskConfig config = taskConfigService.getTaskConfig(taskCode);
        if (config != null) {
            taskConfigService.enableTask(taskCode);
            scheduleTask(config);
        }
    }

    /**
     * 调度任务
     */
    private void scheduleTask(TaskConfig config) {
        String taskCode = config.getTaskCode();
        try {
            // 检查CRON表达式
            if (!StringUtils.hasText(config.getCronExpr())) {
                log.warn("Task {} has no cron expression", taskCode);
                return;
            }

            // 创建触发器
            CronTrigger trigger = new CronTrigger(config.getCronExpr());

            // 调度任务
            ScheduledFuture<?> future = scheduler.schedule(
                () -> executeTask(config),
                trigger
            );

            // 保存调度任务
            ScheduledFuture<?> existingFuture = scheduledTasks.put(taskCode, future);
            if (existingFuture != null && !existingFuture.isCancelled()) {
                existingFuture.cancel(true);
            }

            log.info("Scheduled task: {}, cron: {}", taskCode, config.getCronExpr());

        } catch (Exception e) {
            log.error("Failed to schedule task: " + taskCode, e);
        }
    }

    /**
     * 执行任务
     */
    private void executeTask(TaskConfig config) {
        String taskCode = config.getTaskCode();
        try {
            // 判断是否需要分片执行
            Map<String, Object> taskParam = config.getTaskParamObject(Map.class);
            boolean enableSplit = taskParam != null && 
                                Boolean.TRUE.equals(taskParam.get("enableSplit"));
            
            if (enableSplit) {
                executeShardingTask(config);
            } else {
                executeSingleTask(config);
            }
        } catch (Exception e) {
            log.error("Failed to execute task: " + taskCode, e);
        }
    }

    /**
     * 执行分片任务
     */
    private void executeShardingTask(TaskConfig config) {
        String taskCode = config.getTaskCode();
        Map<String, Object> taskParam = config.getTaskParamObject(Map.class);
        
        // 获取分片参数
        int shardingTotal = taskParam.containsKey("shardingTotal") ? 
            (Integer) taskParam.get("shardingTotal") : 1;
            
        // 创建分片任务
        for (int i = 0; i < shardingTotal; i++) {
            // 创建任务实例
            TaskInstance instance = taskExecuteService.createTaskInstance(
                taskCode, i, shardingTotal);
                
            // 发送任务消息
            TaskMessage message = createTaskMessage(config, instance);
            taskProducer.sendTask(message);
            
            log.info("Created sharding task: {}, shard: {}/{}", 
                taskCode, i + 1, shardingTotal);
        }
    }

    /**
     * 执行单个任务
     */
    private void executeSingleTask(TaskConfig config) {
        // 创建任务实例
        TaskInstance instance = taskExecuteService.createTaskInstance(
            config.getTaskCode(), 0, 1);
            
        // 发送任务消息
        TaskMessage message = createTaskMessage(config, instance);
        taskProducer.sendTask(message);
    }

    /**
     * 创建任务消息
     */
    private TaskMessage createTaskMessage(TaskConfig config, TaskInstance instance) {
        TaskMessage message = new TaskMessage();
        message.setTaskCode(config.getTaskCode());
        message.setInstanceId(instance.getInstanceId());
        message.setShardIndex(instance.getShardIndex());
        message.setShardTotal(instance.getShardTotal());
        message.setPriority(config.getPriority());
        return message;
    }
}
```

### 3\. 任务分片策略接口

```java
// 3.1 ShardingStrategy.java - 分片策略接口
public interface ShardingStrategy {
    /**
     * 计算分片参数
     */
    List<ShardingParam> calculateSharding(TaskConfig config, Object taskParam);
}

// 3.2 ShardingParam.java - 分片参数
@Data
@Builder
public class ShardingParam {
    private int shardIndex;          // 分片索引
    private int shardTotal;          // 分片总数
    private Map<String, Object> params; // 分片参数
}

// 3.3 DefaultShardingStrategy.java - 默认分片策略
@Component
public class DefaultShardingStrategy implements ShardingStrategy {
    
    @Override
    public List<ShardingParam> calculateSharding(TaskConfig config, Object taskParam) {
        if (!(taskParam instanceof Map)) {
            return Collections.singletonList(createDefaultSharding());
        }

        Map<String, Object> params = (Map<String, Object>) taskParam;
        int shardingTotal = getShardingTotal(params);
        
        List<ShardingParam> shardingParams = new ArrayList<>();
        for (int i = 0; i < shardingTotal; i++) {
            shardingParams.add(ShardingParam.builder()
                .shardIndex(i)
                .shardTotal(shardingTotal)
                .params(params)
                .build());
        }
        
        return shardingParams;
    }
    
    private ShardingParam createDefaultSharding() {
        return ShardingParam.builder()
            .shardIndex(0)
            .shardTotal(1)
            .params(new HashMap<>())
            .build();
    }
    
    private int getShardingTotal(Map<String, Object> params) {
        Object total = params.get("shardingTotal");
        if (total instanceof Integer) {
            return (Integer) total;
        }
        return 1;
    }
}
```

这样我们完成了调度器的核心实现，包括：

1. 基础调度功能
2. 任务分片执行
3. 手动触发控制
4. 定时调度管理

需要我继续实现调度器的监控和告警部分吗？主要包括：

1. 调度监控指标收集
2. 任务执行统计
3. 告警规则和通知

## Prompt:

继续

## Response:

好的，让我们实现调度器的监控和告警部分。

### 1\. 监控指标定义和收集

```java
// 1.1 SchedulerMetrics.java - 调度器指标
@Component
@RequiredArgsConstructor
public class SchedulerMetrics {
    private final MeterRegistry registry;
    
    // 任务执行计数器
    private final Counter taskExecuteCounter;
    // 任务成功计数器
    private final Counter taskSuccessCounter;
    // 任务失败计数器
    private final Counter taskFailureCounter;
    // 任务执行时长
    private final Timer taskExecuteTimer;
    // 当前运行任务数
    private final Gauge runningTaskGauge;
    // 任务队列深度
    private final Gauge taskQueueGauge;
    
    @PostConstruct
    public void init() {
        // 初始化指标
        taskExecuteCounter = Counter.builder("task.execute.total")
                .description("任务执行总数")
                .register(registry);
                
        taskSuccessCounter = Counter.builder("task.execute.success")
                .description("任务执行成功数")
                .register(registry);
                
        taskFailureCounter = Counter.builder("task.execute.failure")
                .description("任务执行失败数")
                .register(registry);
                
        taskExecuteTimer = Timer.builder("task.execute.time")
                .description("任务执行时长")
                .register(registry);
                
        runningTaskGauge = Gauge.builder("task.running", 
                () -> getRunningTaskCount())
                .description("当前运行任务数")
                .register(registry);
                
        taskQueueGauge = Gauge.builder("task.queue.size",
                () -> getTaskQueueSize())
                .description("任务队列深度")
                .register(registry);
    }
    
    /**
     * 记录任务执行
     */
    public void recordTaskExecute(String taskCode) {
        taskExecuteCounter.increment();
        // 添加标签
        Tags tags = Tags.of(
            Tag.of("taskCode", taskCode)
        );
        registry.counter("task.execute.total", tags).increment();
    }
    
    /**
     * 记录任务执行结果
     */
    public void recordTaskResult(String taskCode, boolean success, long duration) {
        // 更新计数器
        if (success) {
            taskSuccessCounter.increment();
        } else {
            taskFailureCounter.increment();
        }
        
        // 记录执行时长
        taskExecuteTimer.record(duration, TimeUnit.MILLISECONDS);
        
        // 添加标签
        Tags tags = Tags.of(
            Tag.of("taskCode", taskCode),
            Tag.of("success", String.valueOf(success))
        );
        registry.timer("task.execute.time", tags).record(duration, TimeUnit.MILLISECONDS);
    }
}

// 1.2 TaskExecutionStats.java - 任务执行统计
@Data
@Builder
public class TaskExecutionStats {
    private String taskCode;         // 任务编码
    private long totalCount;         // 总执行次数
    private long successCount;       // 成功次数
    private long failureCount;       // 失败次数
    private double avgDuration;      // 平均执行时长
    private double maxDuration;      // 最大执行时长
    private double minDuration;      // 最小执行时长
    private LocalDateTime startTime; // 统计开始时间
    private LocalDateTime endTime;   // 统计结束时间
}
```

### 2\. 监控服务实现

```java
// 2.1 TaskMonitorService.java - 任务监控服务
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskMonitorService {

    private final TaskExecuteService taskExecuteService;
    private final SchedulerMetrics schedulerMetrics;
    private final AlertService alertService;
    
    /**
     * 监控任务执行状态
     */
    @Scheduled(fixedRate = 60000) // 每分钟执行一次
    public void monitorTaskExecution() {
        try {
            // 获取运行中的任务
            List<TaskInstance> runningTasks = taskExecuteService.getRunningTasks();
            
            // 检查任务执行时间
            for (TaskInstance task : runningTasks) {
                checkTaskTimeout(task);
            }
            
            // 检查任务积压
            checkTaskBacklog();
            
            // 检查系统负载
            checkSystemLoad();
            
        } catch (Exception e) {
            log.error("监控任务执行状态异常", e);
        }
    }
    
    /**
     * 检查任务超时
     */
    private void checkTaskTimeout(TaskInstance task) {
        // 获取任务配置的超时时间
        TaskConfig config = taskConfigService.getTaskConfig(task.getTaskCode());
        if (config == null || config.getTimeout() == null) {
            return;
        }
        
        // 计算任务执行时长
        Duration duration = Duration.between(task.getStartTime(), LocalDateTime.now());
        if (duration.getSeconds() > config.getTimeout()) {
            // 发送任务超时告警
            AlertMessage alert = AlertMessage.builder()
                .type(AlertType.TASK_TIMEOUT)
                .taskCode(task.getTaskCode())
                .instanceId(task.getInstanceId())
                .message(String.format("任务执行超时: 已执行%d秒, 超时时间%d秒",
                    duration.getSeconds(), config.getTimeout()))
                .build();
            alertService.sendAlert(alert);
            
            // 标记任务超时
            taskExecuteService.updateTaskStatus(task.getInstanceId(), 
                TaskStatusEnum.TIMEOUT, "任务执行超时");
        }
    }
    
    /**
     * 检查任务积压
     */
    private void checkTaskBacklog() {
        // TODO: 实现任务积压检查逻辑
    }
    
    /**
     * 检查系统负载
     */
    private void checkSystemLoad() {
        // TODO: 实现系统负载检查逻辑
    }
    
    /**
     * 统计任务执行情况
     */
    public TaskExecutionStats getTaskStats(String taskCode, LocalDateTime startTime, 
            LocalDateTime endTime) {
        // TODO: 实现任务统计逻辑
        return null;
    }
}
```

### 3\. 告警服务实现

```java
// 3.1 AlertMessage.java - 告警消息
@Data
@Builder
public class AlertMessage {
    private AlertType type;          // 告警类型
    private String taskCode;         // 任务编码
    private String instanceId;       // 实例ID
    private String message;          // 告警内容
    private AlertLevel level;        // 告警级别
    private LocalDateTime time;      // 告警时间
}

// 3.2 AlertType.java - 告警类型
public enum AlertType {
    TASK_TIMEOUT("任务超时"),
    TASK_FAILURE("任务失败"),
    TASK_BACKLOG("任务积压"),
    SYSTEM_OVERLOAD("系统过载");
    
    private final String description;
    // ... 构造方法等
}

// 3.3 AlertLevel.java - 告警级别
public enum AlertLevel {
    INFO(1, "提示"),
    WARN(2, "警告"),
    ERROR(3, "错误"),
    CRITICAL(4, "严重");
    
    private final int level;
    private final String description;
    // ... 构造方法等
}

// 3.4 AlertService.java - 告警服务
@Slf4j
@Service
@RequiredArgsConstructor
public class AlertService {

    private final AlertProperties alertProperties;
    private final WebClient webClient;
    private final RabbitTemplate rabbitTemplate;
    
    /**
     * 发送告警
     */
    public void sendAlert(AlertMessage alert) {
        try {
            // 设置默认值
            if (alert.getTime() == null) {
                alert.setTime(LocalDateTime.now());
            }
            
            // 根据配置发送告警
            if (alertProperties.isEnableWebhook()) {
                sendWebhookAlert(alert);
            }
            
            if (alertProperties.isEnableEmail()) {
                sendEmailAlert(alert);
            }
            
            if (alertProperties.isEnableMQ()) {
                sendMQAlert(alert);
            }
            
            log.info("告警发送成功: type={}, taskCode={}, message={}", 
                alert.getType(), alert.getTaskCode(), alert.getMessage());
                
        } catch (Exception e) {
            log.error("告警发送失败: " + alert, e);
        }
    }
    
    /**
     * 发送Webhook告警
     */
    private void sendWebhookAlert(AlertMessage alert) {
        String webhookUrl = alertProperties.getWebhookUrl();
        webClient.post()
            .uri(webhookUrl)
            .bodyValue(alert)
            .retrieve()
            .toBodilessEntity()
            .subscribe();
    }
    
    /**
     * 发送邮件告警
     */
    private void sendEmailAlert(AlertMessage alert) {
        // TODO: 实现邮件告警
    }
    
    /**
     * 发送MQ告警
     */
    private void sendMQAlert(AlertMessage alert) {
        rabbitTemplate.convertAndSend(
            "alert.exchange",
            "alert.notification",
            alert
        );
    }
}
```

### 4\. 告警配置

```java
// 4.1 AlertProperties.java - 告警配置属性
@Data
@ConfigurationProperties(prefix = "task.alert")
public class AlertProperties {
    private boolean enabled = true;          // 是否启用告警
    private boolean enableWebhook = false;   // 是否启用Webhook告警
    private boolean enableEmail = false;     // 是否启用邮件告警
    private boolean enableMQ = true;         // 是否启用MQ告警
    
    private String webhookUrl;               // Webhook URL
    private List<String> emailReceivers;     // 邮件接收人
    private AlertLevel minLevel = AlertLevel.WARN;  // 最小告警级别
    
    private RetryConfig retry = new RetryConfig();  // 重试配置
    
    @Data
    public static class RetryConfig {
        private int maxAttempts = 3;        // 最大重试次数
        private long retryInterval = 1000;   // 重试间隔(毫秒)
    }
}
```

到这里，我们完成了监控和告警部分的实现，包括：

1. 指标收集和统计
2. 监控检查
3. 告警消息定义
4. 多渠道告警通知

需要我补充任何部分的实现吗？

