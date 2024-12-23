# MongoDB Repository设计实现方案

## 一、设计思路

### 1.1 核心组件
```
MongoRepository (Spring Data)
      ↑
IRepository (通用接口)
      ↑
BaseMongoRepository (基础实现)  
      ↑
业务Repository (接口定义)
```

### 1.2 关键设计点
1. IRepository 定义通用方法
2. BaseMongoRepository 提供默认实现 
3. 工厂类自动创建代理
4. 业务Repository只需定义接口

## 二、核心代码实现

### 2.1 基础接口(IRepository)
```java
@NoRepositoryBean
public interface IRepository<T, ID extends Serializable> extends MongoRepository<T, ID> {
    /**
     * 根据业务编码查询
     */
    T findByCode(String code);
    
    /**
     * 批量更新状态
     */
    void updateStatus(ID id, String status);
    
    /**
     * 统计状态数量
     */
    long countByStatus(String status);
    
    /**
     * 软删除
     */
    void softDelete(ID id);
}
```

### 2.2 基础实现(BaseMongoRepository)
```java
public class BaseMongoRepository<T, ID extends Serializable> 
    extends SimpleMongoRepository<T, ID> implements IRepository<T, ID> {

    protected final MongoTemplate mongoTemplate;
    protected final MongoEntityInformation<T, ID> entityInformation;
    
    public BaseMongoRepository(MongoEntityInformation<T, ID> metadata, 
                             MongoOperations mongoOperations) {
        super(metadata, mongoOperations);
        this.mongoTemplate = (MongoTemplate) mongoOperations;
        this.entityInformation = metadata;
    }

    @Override
    public T findByCode(String code) {
        Query query = new Query(Criteria.where("code").is(code));
        return mongoTemplate.findOne(query, entityInformation.getJavaType());
    }
    
    // 其他方法实现...
}
```

### 2.3 工厂类(CustomMongoRepositoryFactory)
```java
public class CustomMongoRepositoryFactory extends MongoRepositoryFactory {
    
    private final MongoOperations mongoOperations;

    public CustomMongoRepositoryFactory(MongoOperations mongoOperations) {
        super(mongoOperations);
        this.mongoOperations = mongoOperations;
    }

    @Override
    protected Object getTargetRepository(RepositoryInformation information) {
        MongoEntityInformation<?, Serializable> entityInformation = 
            getEntityInformation(information.getDomainType());

        return new BaseMongoRepository<>(entityInformation, mongoOperations);
    }

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        return BaseMongoRepository.class;
    }
}
```

### 2.4 工厂Bean(CustomMongoRepositoryFactoryBean)
```java
public class CustomMongoRepositoryFactoryBean<T extends Repository<S, ID>, S, ID extends Serializable>
        extends MongoRepositoryFactoryBean<T, S, ID> {

    public CustomMongoRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
        super(repositoryInterface);
    }

    @Override
    protected RepositoryFactorySupport getFactoryInstance(MongoOperations operations) {
        return new CustomMongoRepositoryFactory(operations);
    }
}
```

## 三、配置说明

### 3.1 启用MongoDB Repository
```java
@Configuration
@EnableMongoRepositories(
    basePackages = "com.study.collect",
    repositoryFactoryBeanClass = CustomMongoRepositoryFactoryBean.class
)
public class MongoRepositoryConfig {
}
```

### 3.2 实体定义
```java
@Data
@Document(collection = "finance_data")
public class FinanceData {
    @Id
    private String id;
    private String code;
    private String status;
    private Boolean deleted;
    // 其他业务字段...
}
```

### 3.3 业务Repository定义
```java
public interface FinanceRepository extends IRepository<FinanceData, String> {
    // 方式一：方法名约定
    List<FinanceData> findByStockCode(String stockCode);
    
    // 方式二：@Query注解
    @Query("{'amount': {$gt: ?0}}")
    List<FinanceData> findLargeTransactions(BigDecimal threshold);
}
```

## 四、使用示例

### 4.1 基础用法
```java
@Service
@RequiredArgsConstructor
public class FinanceService {
    private final FinanceRepository financeRepository;
    
    // 基础CRUD
    public FinanceData save(FinanceData data) {
        return financeRepository.save(data);
    }
    
    // 通用方法
    public FinanceData getByCode(String code) {
        return financeRepository.findByCode(code);
    }
    
    // 自定义方法
    public List<FinanceData> getByStockCode(String stockCode) {
        return financeRepository.findByStockCode(stockCode);
    }
}
```

### 4.2 高级用法
```java
// 批量操作
List<FinanceData> saveAll(Iterable<FinanceData> entities)

// 分页查询 
Page<FinanceData> findAll(Pageable pageable)

// 条件查询
@Query("{'status': ?0, 'amount': {$gt: ?1}}")
List<FinanceData> findByStatusAndAmountGreaterThan(String status, BigDecimal amount)
```

## 五、最佳实践

### 5.1 命名规范
- 实体类: XxxData
- Repository接口: XxxRepository
- 查询方法: findByXxx, getByXxx
- 更新方法: updateXxx
- 删除方法: deleteByXxx, removeByXxx

### 5.2 代码规范
1. 实体类
```java
@Data
@Document(collection = "xxx")
public class XxxData {
    @Id
    private String id;
    private String code;    // 业务编码
    private String status;  // 状态
    private Boolean deleted; // 软删除标记
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
```

2. Repository接口
```java
public interface XxxRepository extends IRepository<XxxData, String> {
    // 优先使用方法名约定
    List<XxxData> findByStatus(String status);
    
    // 复杂查询使用@Query
    @Query("{'field': ?0}")
    List<XxxData> customQuery(String param);
}
```

### 5.3 事务处理
```java
@Transactional(rollbackFor = Exception.class)
public void complexOperation() {
    // 1. 保存数据
    XxxData data = repository.save(new XxxData());
    
    // 2. 更新状态
    repository.updateStatus(data.getId(), "ACTIVE");
    
    // 3. 关联处理
    otherRepository.process(data.getId());
}
```

### 5.4 性能优化
1. 索引设计
```java
@Document(collection = "xxx")
@CompoundIndex(def = "{'code': 1, 'status': 1}")
public class XxxData {
    @Indexed(unique = true)
    private String code;
    
    @Indexed
    private String status;
}
```

2. 查询优化
```java
// 使用投影
@Query(value = "{'status': ?0}", fields = "{'code': 1, 'name': 1}")
List<XxxData> findByStatus(String status);

// 限制返回数量
List<XxxData> findTop10ByStatus(String status);
```

## 六、异常处理

### 6.1 Repository异常
```java
try {
    repository.save(data);
} catch (DuplicateKeyException e) {
    throw new BusinessException("数据已存在");
} catch (MongoException e) {
    throw new BusinessException("数据库操作失败");
}
```

### 6.2 乐观锁
```java
@Document(collection = "xxx")
public class XxxData {
    @Version
    private Long version;
}
```

## 七、监控与运维

### 7.1 性能监控
```java
@Aspect
@Component
public class RepositoryMetrics {
    @Around("execution(* com.study.collect..*.repository.*.*(..))")
    public Object metric(ProceedingJoinPoint pjp) {
        long start = System.currentTimeMillis();
        try {
            return pjp.proceed();
        } finally {
            // 记录执行时间
            long time = System.currentTimeMillis() - start;
            log.info("Repository method {} cost {}ms", 
                pjp.getSignature().getName(), time);
        }
    }
}
```

### 7.2 日志追踪
```java
@Slf4j
public class BaseMongoRepository<T, ID> {
    @Override
    public T save(T entity) {
        log.debug("Saving entity: {}", entity);
        try {
            T result = super.save(entity);
            log.debug("Save success: {}", result);
            return result;
        } catch (Exception e) {
            log.error("Save failed: {}", e.getMessage());
            throw e;
        }
    }
}
```