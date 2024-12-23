# MongoDB Repository设计实现方案与使用指南

## 一、总体设计

### 1.1 设计目标
- 复用Spring Data MongoDB的基础功能
- 统一Repository的实现方式
- 支持通用业务方法扩展
- 保持类型安全
- 简化使用方式

### 1.2 类结构关系
```
MongoRepository (Spring Data)
     ↑
IRepository (通用接口)
     ↑ 
BaseMongoRepository (默认实现)
     ↑
具体业务Repository (接口定义)
```

## 二、核心代码实现

### 2.1 通用Repository接口

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
     * 自定义的统计方法
     */
    long countByStatus(String status);
    
    /**
     * 软删除
     */
    void softDelete(ID id);
}
```

### 2.2 基础Repository实现

```java
public class BaseMongoRepository<T, ID extends Serializable> 
        extends SimpleMongoRepository<T, ID> implements IRepository<T, ID> {
    
    protected final MongoTemplate mongoTemplate;
    protected final MongoEntityInformation<T, ID> entityInformation;

    public BaseMongoRepository(MongoEntityInformation<T, ID> metadata, MongoOperations mongoOperations) {
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

### 2.3 Repository工厂Bean

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

### 2.4 Repository工厂

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

        if (IRepository.class.isAssignableFrom(information.getRepositoryInterface())) {
            return new BaseMongoRepository<>(entityInformation, mongoOperations) {};
        }
        return super.getTargetRepository(information);
    }

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        if (IRepository.class.isAssignableFrom(metadata.getRepositoryInterface())) {
            return BaseMongoRepository.class;
        }
        return super.getRepositoryBaseClass(metadata);
    }
}
```

## 三、配置方式

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

### 3.2 业务Repository定义示例

```java
public interface FinanceRepository extends IRepository<FinanceData, String> {
    
    // 方式一：方法名约定
    List<FinanceData> findByStockCode(String stockCode);
    
    // 方式二：使用@Query注解
    @Query("{'tradeDate': {$gte: ?0, $lte: ?1}}")
    List<FinanceData> findByTradeDateBetween(String startDate, String endDate);
}
```

## 四、使用示例

### 4.1 实体类定义

```java
@Data
@Document(collection = "finance_data")
public class FinanceData {
    @Id
    private String id;
    private String code;
    private String stockCode;
    private BigDecimal amount;
    private LocalDateTime tradeDate;
    private String status;
    private Boolean deleted;
}
```

### 4.2 服务层使用

```java
@Service
@RequiredArgsConstructor
public class FinanceService {
    
    private final FinanceRepository financeRepository;

    // 使用基础功能
    public FinanceData save(FinanceData data) {
        return financeRepository.save(data);
    }
    
    // 使用通用方法
    public FinanceData getByCode(String code) {
        return financeRepository.findByCode(code);
    }
    
    // 使用业务方法
    public List<FinanceData> getByStockCode(String stockCode) {
        return financeRepository.findByStockCode(stockCode);
    }
    
    // 软删除
    public void removeData(String id) {
        financeRepository.softDelete(id);
    }
    
    // 状态更新
    public void changeStatus(String id, String status) {
        financeRepository.updateStatus(id, status);
    }
}
```

## 五、扩展方式

### 5.1 添加通用方法
在IRepository中添加新的方法:

```java
public interface IRepository<T, ID extends Serializable> extends MongoRepository<T, ID> {
    // 添加新的通用方法
    List<T> findByStatus(String status);
}
```

### 5.2 添加业务方法
在具体的Repository接口中添加:

```java
public interface FinanceRepository extends IRepository<FinanceData, String> {
    // 添加特定业务方法
    @Query(value = "{'amount': {$gt: ?0}}", sort = "{'tradeDate': -1}")
    List<FinanceData> findLargeTransactions(BigDecimal threshold);
}
```

## 六、最佳实践

1. 命名规范
- 方法名要符合Spring Data命名规范
- 复杂查询优先使用@Query注解

2. 性能优化
- 建立合适的索引
- 使用合适的查询方式
- 避免大范围查询

3. 类型安全
- ID类型要继承Serializable
- 使用具体的实体类型
- 保持泛型一致性

4. 异常处理
- 统一异常处理
- 适当的错误转换
- 合理的重试机制

5. 事务处理
- 注意MongoDB事务特性
- 合理划分事务边界
- 实现补偿机制