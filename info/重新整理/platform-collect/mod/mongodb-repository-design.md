# MongoDB Repository 设计实现方案

## 一、设计目标

1. 继承关系清晰:
```
MongoRepository (Spring Data)
     ↑
IRepository (通用接口，增加共同的业务方法)
     ↑ 
BaseMongoRepository (提供默认实现)
     ↑
FinanceRepository (特定业务接口)
     ↑
FinanceRepositoryImpl (具体实现)
```

2. 功能要求:
- 复用 Spring Data MongoDB 的基础功能
- 支持自定义通用的业务方法
- 支持各业务模块扩展特定方法
- 保持类型安全
- 便于使用

## 二、核心代码实现

### 2.1 基础仓储接口(IRepository)

```java
package com.study.collect.core.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface IRepository<T, ID> extends MongoRepository<T, ID> {
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

关键点说明:
- 继承 MongoRepository 获取基础的 CRUD 功能
- 使用 @NoRepositoryBean 标注这是一个中间仓储接口
- 定义通用的业务方法

### 2.2 基础仓储实现(BaseMongoRepository)

```java
package com.study.collect.core.repository.impl;

import com.study.collect.core.repository.IRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.core.MongoOperations;

public abstract class BaseMongoRepository<T, ID> extends SimpleMongoRepository<T, ID> implements IRepository<T, ID> {
    
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

    @Override
    public void updateStatus(ID id, String status) {
        Query query = new Query(Criteria.where("id").is(id));
        Update update = new Update().set("status", status);
        mongoTemplate.updateFirst(query, update, entityInformation.getJavaType());
    }

    @Override
    public long countByStatus(String status) {
        Query query = new Query(Criteria.where("status").is(status));
        return mongoTemplate.count(query, entityInformation.getJavaType());
    }

    @Override
    public void softDelete(ID id) {
        Query query = new Query(Criteria.where("id").is(id));
        Update update = new Update().set("deleted", true);
        mongoTemplate.updateFirst(query, update, entityInformation.getJavaType());
    }
}
```

关键点说明:
- 继承 SimpleMongoRepository 获取基础实现
- 使用 MongoTemplate 实现自定义方法
- 通过 entityInformation 获取实体信息,保证类型安全

### 2.3 业务仓储接口(以 Finance 为例)

```java
package com.study.collect.business.finance.repository;

import com.study.collect.business.finance.model.FinanceData;
import com.study.collect.core.repository.IRepository;
import java.util.List;

public interface FinanceRepository extends IRepository<FinanceData, String> {
    // 特定于 Finance 的方法
    List<FinanceData> findByStockCode(String stockCode);
    List<FinanceData> findByTradeDateBetween(String startDate, String endDate);
}
```

### 2.4 业务仓储实现

```java
package com.study.collect.business.finance.repository.impl;

import com.study.collect.business.finance.model.FinanceData;
import com.study.collect.business.finance.repository.FinanceRepository;
import com.study.collect.core.repository.impl.BaseMongoRepository;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class FinanceRepositoryImpl extends BaseMongoRepository<FinanceData, String> implements FinanceRepository {

    public FinanceRepositoryImpl(MongoEntityInformation<FinanceData, String> metadata, MongoOperations mongoOperations) {
        super(metadata, mongoOperations);
    }

    @Override
    public List<FinanceData> findByStockCode(String stockCode) {
        Query query = new Query(Criteria.where("stockCode").is(stockCode));
        return mongoTemplate.find(query, FinanceData.class);
    }

    @Override
    public List<FinanceData> findByTradeDateBetween(String startDate, String endDate) {
        Query query = new Query(Criteria.where("tradeDate")
            .gte(startDate)
            .lte(endDate));
        return mongoTemplate.find(query, FinanceData.class);
    }
}
```

## 三、工厂和配置

### 3.1 Repository 工厂

```java
package com.study.collect.core.repository.factory;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryComposition.RepositoryFragments;
import org.springframework.data.repository.core.support.RepositoryFragment;

public class CustomMongoRepositoryFactory extends MongoRepositoryFactory {

    private final MongoOperations mongoOperations;

    public CustomMongoRepositoryFactory(MongoOperations mongoOperations) {
        super(mongoOperations);
        this.mongoOperations = mongoOperations;
    }

    @Override
    protected RepositoryFragments getRepositoryFragments(RepositoryMetadata metadata) {
        RepositoryFragments fragments = super.getRepositoryFragments(metadata);

        if (IRepository.class.isAssignableFrom(metadata.getRepositoryInterface())) {
            MongoEntityInformation<?, ?> entityInformation = getEntityInformation(metadata.getDomainType());
            
            Object impl = instantiateClass(metadata.getRepositoryBaseClass(),
                    entityInformation, mongoOperations);
                    
            fragments = fragments.append(RepositoryFragment.implemented(impl));
        }

        return fragments;
    }
}
```

### 3.2 配置类

```java
package com.study.collect.core.config;

import com.study.collect.core.repository.factory.CustomMongoRepositoryFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(
    basePackages = "com.study.collect",
    repositoryFactoryBeanClass = CustomMongoRepositoryFactoryBean.class
)
public class MongoRepositoryConfig {
}
```

## 四、使用方式

1. 简单使用:
```java
@Autowired
private FinanceRepository financeRepository;

// 使用父接口方法
FinanceData data = financeRepository.findByCode("CODE001");
financeRepository.updateStatus(id, "ACTIVE");
financeRepository.softDelete(id);

// 使用特定业务方法
List<FinanceData> stockData = financeRepository.findByStockCode("AAPL");
List<FinanceData> periodData = financeRepository.findByTradeDateBetween("2024-01-01", "2024-12-31");
```

2. 扩展新的业务仓储:
```java
// 1. 定义接口
public interface NewBusinessRepository extends IRepository<NewBusiness, String> {
    // 添加特定方法
}

// 2. 实现类
@Repository
public class NewBusinessRepositoryImpl extends BaseMongoRepository<NewBusiness, String> implements NewBusinessRepository {
    // 实现特定方法
}
```

## 五、设计优势

1. 代码复用
- 通过继承复用 Spring Data MongoDB 的功能
- BaseMongoRepository 提供了通用方法实现
- 避免重复编写基础代码

2. 类型安全
- 使用泛型保证类型安全
- 通过 MongoEntityInformation 维护实体元数据
- 编译时类型检查

3. 扩展性好
- 可以在 IRepository 添加新的通用方法
- 各业务模块可以定义特有方法
- 支持自定义实现

4. 使用便捷
- Spring 自动注入
- 无需手动创建实现类
- 配置简单

## 六、注意事项

1. 命名规范
- 方法名要符合 Spring Data 规范
- 自定义方法名要见名知意

2. 性能考虑
- 合理使用索引
- 避免无限制查询
- 批量操作优化

3. 事务处理
- 注意 MongoDB 事务限制
- 合理划分事务边界
- 实现事务补偿机制