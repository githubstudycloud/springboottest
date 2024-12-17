# 分布式采集平台开发指南

## 一、项目概述

### 1.1 技术栈
- 框架：Spring Boot 3.2.9
- 构建工具：Maven
- 数据库：MongoDB、Redis
- 消息队列：RabbitMQ
- 调度框架：Quartz
- 监控：Prometheus + Grafana

### 1.2 版本要求
- JDK: 17 及以上
- Maven: 3.8.x 及以上
- Spring Boot: 3.2.9
- MongoDB: 4.4 及以上
- Redis: 6.x 及以上
- RabbitMQ: 3.8.x 及以上

## 二、项目结构

### 2.1 模块划分
```
platform-collect/
├── platform-collect-api                # API模块
├── platform-collect-core               # 核心模块
├── platform-collect-domain             # 领域模块
├── platform-collect-infrastructure     # 基础设施模块
├── platform-collect-common             # 公共模块
└── platform-collect-business           # 业务扩展模块
```

### 2.2 依赖关系
```
business    -->  api
api         -->  core
core        -->  domain
domain      -->  infrastructure
infrastructure --> common
```

## 三、开发规范

### 3.1 代码规范

#### 3.1.1 命名规范
```java
// 1. 类命名
public class CollectorFactory {}        // 工厂类
public interface Collector {}           // 接口
public abstract class AbstractCollector {} // 抽象类
public @interface CollectMonitor {}     // 注解类

// 2. 方法命名
public CollectResult collect();         // 动词开头
public boolean isValid();               // 判断方法
public void setName(String name);       // setter
public String getName();                // getter

// 3. 常量命名
public static final int MAX_RETRY_TIMES = 3;
```

#### 3.1.2 注释规范
```java
/**
 * 采集器接口
 * 
 * @author author
 * @since 1.0.0
 */
public interface Collector {
    /**
     * 执行数据采集
     *
     * @param context 采集上下文
     * @return 采集结果
     * @throws CollectException 采集异常
     */
    CollectResult collect(CollectContext context);
}
```

### 3.2 架构规范

#### 3.2.1 分层规范
- API层：仅包含接口定义和数据传输对象
- Core层：实现核心业务逻辑
- Domain层：定义领域模型和规则
- Infrastructure层：提供技术实现
- Common层：提供公共工具和定义

#### 3.2.2 依赖规范
- 禁止跨层依赖
- 禁止循环依赖
- 遵循单向依赖原则
- 遵循依赖倒置原则

## 四、扩展开发指南

### 4.1 基础组件扩展

#### 4.1.1 采集器扩展
```java
// 1. 继承方式
@Collector(type = "custom")
public class CustomCollector extends AbstractCollector {
    @Override
    protected CollectResult doCollect(CollectContext context) {
        // 实现采集逻辑
        return CollectResult.success(data);
    }
    
    @Override
    protected void beforeCollect(CollectContext context) {
        // 前置处理
    }
    
    @Override
    protected void afterCollect(CollectResult result) {
        // 后置处理
    }
}

// 2. 配置方式
@Configuration
public class CollectorConfig {
    @Bean
    @ConditionalOnProperty(name = "collector.custom.enabled")
    public Collector customCollector() {
        return new CustomCollector();
    }
}
```

#### 4.1.2 处理器扩展
```java
// 1. 处理器实现
@Processor(types = "custom", order = 1)
public class CustomProcessor extends AbstractProcessor {
    @Override
    protected void doProcess(ProcessContext context) {
        // 实现处理逻辑
    }
    
    @Override
    protected boolean shouldProcess(ProcessContext context) {
        // 处理条件判断
        return true;
    }
}

// 2. 处理器配置
@Configuration
public class ProcessorConfig {
    @Bean
    public ProcessorChain processorChain(List<Processor> processors) {
        return new ProcessorChainBuilder()
            .addProcessors(processors)
            .build();
    }
}
```

### 4.2 业务模块开发

#### 4.2.1 目录结构
```
business/
└── enterprise/                # 业务模块
    ├── api/                  
    │   ├── controller/      
    │   └── model/           
    ├── core/                 
    │   ├── collector/       
    │   └── processor/       
    ├── domain/              
    │   ├── entity/         
    │   └── service/        
    └── infrastructure/      
        └── repository/      
```

#### 4.2.2 开发步骤
1. 创建业务配置
```java
@Configuration
@ComponentScan("com.study.collect.business.enterprise")
public class EnterpriseConfig {
    // 业务配置
}
```

2. 实现采集逻辑
```java
@Collector(type = "enterprise")
public class EnterpriseCollector extends AbstractCollector {
    @Override
    protected CollectResult doCollect(CollectContext context) {
        // 实现采集逻辑
    }
}
```

3. 实现业务服务
```java
@Service
public class EnterpriseService {
    @Autowired
    private Collector enterpriseCollector;
    
    @CollectMonitor(name = "enterprise_collect")
    public CollectResult collectEnterpriseData(CollectRequest request) {
        // 业务逻辑实现
    }
}
```

## 五、测试规范

### 5.1 单元测试
```java
@SpringBootTest
public class EnterpriseCollectorTest {
    @Autowired
    private EnterpriseCollector collector;
    
    @Test
    public void testCollect() {
        CollectContext context = new CollectContext();
        context.setParam("type", "enterprise");
        
        CollectResult result = collector.collect(context);
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }
}
```

### 5.2 集成测试
```java
@SpringBootTest
public class EnterpriseServiceIntegrationTest {
    @Autowired
    private EnterpriseService service;
    
    @Test
    public void testCollectFlow() {
        CollectRequest request = new CollectRequest();
        // 设置请求参数
        
        CollectResult result = service.collectEnterpriseData(request);
        
        // 验证结果
        assertNotNull(result);
        verifyDataInMongoDB(result.getData());
        verifyDataInRedis(result.getData());
    }
}
```

## 六、部署指南

### 6.1 环境配置
```yaml
# application.yml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/collect
    redis:
      host: localhost
      port: 6379
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
```

### 6.2 打包部署
```bash
# 打包命令
mvn clean package -DskipTests

# 运行命令
java -jar platform-collect-business.jar --spring.profiles.active=prod
```

## 七、监控运维

### 7.1 监控指标
```java
@Component
public class CollectMetrics {
    private final Counter collectCounter;
    private final Gauge activeTaskGauge;
    
    // 记录采集次数
    public void incrementCollectCount() {
        collectCounter.increment();
    }
    
    // 更新活跃任务数
    public void updateActiveTask(long count) {
        activeTaskGauge.set(count);
    }
}
```

### 7.2 日志规范
```java
@Slf4j
public class EnterpriseCollector extends AbstractCollector {
    @Override
    protected CollectResult doCollect(CollectContext context) {
        log.info("Start collecting enterprise data: {}", context);
        try {
            // 采集逻辑
            log.debug("Collecting data with params: {}", params);
            return result;
        } catch (Exception e) {
            log.error("Failed to collect enterprise data", e);
            throw new CollectException("Collect failed", e);
        }
    }
}
```

## 八、注意事项

### 8.1 开发注意事项
- 必须实现单元测试
- 遵循代码规范
- 及时添加注释
- 做好异常处理
- 添加合适的日志

### 8.2 性能注意事项
- 合理使用缓存
- 注意并发处理
- 避免大事务
- 及时释放资源
- 做好性能测试

## 九、常见问题

### 9.1 开发问题
1. 如何选择正确的扩展方式？
   - 简单场景：使用继承
   - 定制场景：使用配置
   - 复杂场景：完全重写

2. 如何处理并发问题？
   - 使用分布式锁
   - 实现幂等性
   - 合理使用事务

### 9.2 部署问题
1. 如何确保高可用？
   - 多实例部署
   - 使用负载均衡
   - 实现故障转移

2. 如何进行容量规划？
   - 评估数据量
   - 监控系统指标
   - 预留扩展空间

这个开发指南涵盖了：
1. 完整的项目结构
2. 详细的开发规范
3. 具体的扩展示例
4. 测试和部署指南
5. 监控和运维建议

需要我对任何部分进行更详细的说明吗？