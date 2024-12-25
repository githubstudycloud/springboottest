## Prompt:
解读当前框架代码，如何在collect-core的条件下，修改business-finance实现采集的分布式并发进行，给出修改的目录和文件，collect-core没有问题的情况下不改。给出动态数据生成接口和数据保存后的查询接口，都写在business-finance中，先给出要修改新增的目录和文件，然后分段顺序进行

## Response:
我来帮你分析和实现business-finance模块的分布式并发数据采集功能。基于现有的collect-core框架,需要在business-finance中新增和修改以下内容:

要修改/新增的目录和文件结构:

```
business-finance/
  └── src/main/java/com/study/collect/business/finance/
      ├── api/
      │   ├── controller/
      │   │   └── FinanceDataController.java (修改)  
      │   └── model/
      │       ├── request/
      │       │   ├── FinanceDataGenerateRequest.java (新增)
      │       │   └── FinanceDataQueryRequest.java (新增)
      │       └── response/
      │           └── FinanceDataVO.java (新增)
      ├── collector/
      │   └── FinanceCollector.java (修改)
      ├── service/
      │   ├── FinanceCollectService.java (修改)
      │   └── FinanceDataService.java (新增)
      ├── repository/
      │   └── FinanceRepository.java (修改)
      └── config/
          └── FinanceConfiguration.java (新增)
```

主要改动说明:

1.  新增动态数据生成接口和查询接口
2.  增强collector实现分布式并发采集
3.  优化服务层处理逻辑
4.  扩展仓储层查询能力
5.  增加配置类管理分布式任务

我们按以下顺序实现这些改动:

1.  先完成API层的请求响应模型
2.  实现Controller接口定义
3.  增强Collector采集能力
4.  完善Service业务逻辑
5.  扩展Repository功能
6.  添加分布式配置

需要我继续提供具体的代码实现吗?

## Prompt:
按目录顺序分段给出完整代码

## Response:
好的,我将按顺序提供完整的代码实现。先从API层的模型开始。

1.  首先是请求和响应模型:

```java
// FinanceDataGenerateRequest.java
package com.study.collect.business.finance.api.model.request;

import lombok.Data;
import java.time.LocalDateTime;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Data
public class FinanceDataGenerateRequest {
    @NotNull(message = "股票代码不能为空")
    private String stockCode;
    
    @NotNull(message = "数据量不能为空") 
    @Min(value = 1, message = "数据量必须大于0")
    private Integer dataCount;
    
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    
    // 可选的数据生成参数
    private Double minPrice;
    private Double maxPrice;
    private Double minVolume; 
    private Double maxVolume;
}
```

```java
// FinanceDataQueryRequest.java
package com.study.collect.business.finance.api.model.request;

import lombok.Data;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class FinanceDataQueryRequest {
    private String stockCode;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")  
    private LocalDateTime endTime;
    
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    
    // 排序参数
    private String sortField;
    private String sortOrder;
    
    // 聚合查询参数
    private Boolean needStats = false;
    private String statsType; // min,max,avg,sum
}
```

```java
// FinanceDataVO.java
package com.study.collect.business.finance.api.model.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FinanceDataVO {
    private String id;
    private String stockCode;
    private String stockName;
    private BigDecimal price;
    private BigDecimal volume;
    private BigDecimal amount;
    private LocalDateTime tradeTime;
    
    // 统计相关字段
    private BigDecimal highPrice;
    private BigDecimal lowPrice;
    private BigDecimal avgPrice;
    private BigDecimal totalVolume;
    private BigDecimal totalAmount;
    
    // 涨跌幅等计算字段
    private BigDecimal priceChange;
    private BigDecimal priceChangePercent;
}
```

2.  接下来是Controller层:

```java
// FinanceDataController.java
package com.study.collect.business.finance.api.controller;

import com.study.collect.business.finance.api.model.request.FinanceDataGenerateRequest;
import com.study.collect.business.finance.api.model.request.FinanceDataQueryRequest;
import com.study.collect.business.finance.api.model.response.FinanceDataVO;
import com.study.collect.business.finance.service.FinanceCollectService;
import com.study.collect.business.finance.service.FinanceDataService;
import com.study.collect.common.model.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/finance")
@RequiredArgsConstructor
public class FinanceDataController {

    private final FinanceCollectService collectService;
    private final FinanceDataService dataService;

    @PostMapping("/generate")
    public Response<String> generateData(@Valid @RequestBody FinanceDataGenerateRequest request) {
        String taskId = collectService.generateFinanceData(request);
        return Response.success(taskId);
    }

    @GetMapping("/query")
    public Response<Page<FinanceDataVO>> queryData(FinanceDataQueryRequest request) {
        Page<FinanceDataVO> result = dataService.queryFinanceData(request);
        return Response.success(result);
    }

    @GetMapping("/stats/{stockCode}")
    public Response<FinanceDataVO> getStockStats(
            @PathVariable String stockCode,
            @RequestParam(required = false) String statsType) {
        FinanceDataVO stats = dataService.getStockStats(stockCode, statsType);
        return Response.success(stats);
    }

    @GetMapping("/realtime/{stockCode}")
    public Response<FinanceDataVO> getRealtimeData(@PathVariable String stockCode) {
        FinanceDataVO data = dataService.getRealtimeData(stockCode);
        return Response.success(data);
    }

    @PostMapping("/sync/{stockCode}")
    public Response<Boolean> syncStockData(@PathVariable String stockCode) {
        collectService.syncStockData(stockCode);
        return Response.success(true);
    }
}
```

这是第一部分代码实现,主要包含API层的模型定义和Controller接口。需要我继续提供后续的实现吗？

## Prompt:
按目录结构继续

## Response:
继续提供collector层和service层的实现。

3.  修改Collector层实现:

```java
// FinanceCollector.java
package com.study.collect.business.finance.collector;

import com.study.collect.business.finance.model.FinanceData;
import com.study.collect.core.collector.AbstractCollector;
import com.study.collect.core.collector.annotation.Collector;
import com.study.collect.core.collector.exception.CollectException;
import com.study.collect.core.collector.model.CollectContext;
import com.study.collect.core.storage.cache.annotation.Cache;
import com.study.collect.core.storage.cache.annotation.CacheLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Collector(type = "finance")
@Component
public class FinanceCollector extends AbstractCollector<String, List<FinanceData>> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final Random random = new Random();

    public FinanceCollector(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void preProcess(CollectContext<String> context) {
        // 分片参数验证
        validateShardingParams(context);
        // 准备采集环境
        prepareCollectEnvironment(context);
    }

    @Override
    @Cache(key = "finance:stock:#{context.params}", expire = 300)
    @CacheLock(key = "lock:finance:#{context.params}", waitTime = 3)
    protected List<FinanceData> doCollect(CollectContext<String> context) {
        String stockCode = context.getParams();
        Integer shardIndex = context.getShardIndex();
        Integer shardTotal = context.getShardTotal();

        // 获取待处理的时间范围
        LocalDateTime[] timeRange = getTimeRange(context);
        LocalDateTime startTime = timeRange[0];
        LocalDateTime endTime = timeRange[1];

        // 根据分片计算当前分片的时间范围
        LocalDateTime shardStartTime = calculateShardTime(startTime, endTime, shardIndex, shardTotal);
        LocalDateTime shardEndTime = calculateShardTime(startTime, endTime, shardIndex + 1, shardTotal);

        // 生成该分片的数据
        return generateFinanceData(stockCode, shardStartTime, shardEndTime);
    }

    @Override
    protected void postProcess(List<FinanceData> data) {
        // 数据校验和补充
        data.forEach(this::enrichFinanceData);
    }

    private void validateShardingParams(CollectContext<String> context) {
        if (context.getShardIndex() == null || context.getShardTotal() == null) {
            throw new CollectException("分片参数不完整");
        }
        if (context.getShardIndex() >= context.getShardTotal()) {
            throw new CollectException("分片索引超出范围");
        }
    }

    private void prepareCollectEnvironment(CollectContext<String> context) {
        // 准备采集环境,如设置超时时间等
        String cacheKey = "finance:collect:" + context.getParams();
        redisTemplate.opsForValue().set(cacheKey, true, 5, TimeUnit.MINUTES);
    }

    private LocalDateTime[] getTimeRange(CollectContext<String> context) {
        // 从上下文中获取时间范围,如果没有则使用默认范围
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusHours(24);
        return new LocalDateTime[]{startTime, endTime};
    }

    private LocalDateTime calculateShardTime(LocalDateTime startTime, LocalDateTime endTime,
                                          int shardIndex, int shardTotal) {
        long totalSeconds = java.time.Duration.between(startTime, endTime).getSeconds();
        long shardSeconds = totalSeconds / shardTotal;
        return startTime.plusSeconds(shardSeconds * shardIndex);
    }

    private List<FinanceData> generateFinanceData(String stockCode, 
                                                LocalDateTime startTime,
                                                LocalDateTime endTime) {
        List<FinanceData> dataList = new ArrayList<>();
        LocalDateTime currentTime = startTime;
        
        while (currentTime.isBefore(endTime)) {
            FinanceData data = new FinanceData();
            data.setStockCode(stockCode);
            data.setTradeTime(currentTime);
            
            // 生成模拟交易数据
            data.setPrice(generateRandomPrice());
            data.setVolume(generateRandomVolume());
            data.setAmount(data.getPrice().multiply(data.getVolume()));
            
            dataList.add(data);
            currentTime = currentTime.plusMinutes(1);
        }
        
        return dataList;
    }

    private BigDecimal generateRandomPrice() {
        double basePrice = 100.0;
        double variation = (random.nextDouble() - 0.5) * 2.0; // -1.0 到 1.0 之间的随机变化
        return BigDecimal.valueOf(basePrice * (1 + variation))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal generateRandomVolume() {
        double baseVolume = 10000.0;
        double variation = random.nextDouble() * 0.5; // 0 到 0.5 之间的随机变化
        return BigDecimal.valueOf(baseVolume * (1 + variation))
                .setScale(0, RoundingMode.HALF_UP);
    }

    private void enrichFinanceData(FinanceData data) {
        // 补充股票名称
        data.setStockName(getStockName(data.getStockCode()));
        // 设置创建时间
        data.setCreateTime(LocalDateTime.now());
    }

    private String getStockName(String stockCode) {
        // 模拟从缓存或其他服务获取股票名称
        return "Stock_" + stockCode;
    }
}
```

4.  服务层实现:

```java
// FinanceCollectService.java
package com.study.collect.business.finance.service;

import com.study.collect.business.finance.api.model.request.FinanceDataGenerateRequest;
import com.study.collect.business.finance.collector.FinanceCollector;
import com.study.collect.business.finance.model.FinanceData;
import com.study.collect.business.finance.repository.FinanceRepository;
import com.study.collect.core.collector.model.CollectContext;
import com.study.collect.core.task.model.TaskContext;
import com.study.collect.core.task.model.TaskResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FinanceCollectService {

    private final FinanceCollector collector;
    private final FinanceRepository repository;

    public String generateFinanceData(FinanceDataGenerateRequest request) {
        String taskId = UUID.randomUUID().toString();
        
        // 创建采集上下文
        CollectContext<String> context = new CollectContext<>();
        context.setTaskId(taskId);
        context.setParams(request.getStockCode());
        
        // 执行采集
        List<FinanceData> dataList = collector.collect(context);
        
        // 保存数据
        repository.saveAll(dataList);
        
        return taskId;
    }

    public void syncStockData(String stockCode) {
        // 设置分片采集上下文
        CollectContext<String> context = new CollectContext<>();
        context.setTaskId(UUID.randomUUID().toString());
        context.setParams(stockCode);
        context.setShardIndex(0);
        context.setShardTotal(1);

        // 执行采集和保存
        List<FinanceData> dataList = collector.collect(context);
        repository.saveAll(dataList);
    }

    public TaskResult executeTask(TaskContext context) {
        try {
            // 将任务上下文转换为采集上下文
            CollectContext<String> collectContext = new CollectContext<>();
            collectContext.setTaskId(context.getTaskId());
            collectContext.setParams((String) context.getParameter("stockCode"));
            collectContext.setShardIndex(context.getShardIndex());
            collectContext.setShardTotal(context.getShardTotal());

            // 执行采集
            List<FinanceData> dataList = collector.collect(collectContext);
            repository.saveAll(dataList);

            return TaskResult.success(context.getTaskId(), dataList.size());
        } catch (Exception e) {
            log.error("Task execution failed: {}", context.getTaskId(), e);
            return TaskResult.failure(context.getTaskId(), e.getMessage());
        }
    }
}
```

```java
// FinanceDataService.java
package com.study.collect.business.finance.service;

import com.study.collect.business.finance.api.model.request.FinanceDataQueryRequest;
import com.study.collect.business.finance.api.model.response.FinanceDataVO;
import com.study.collect.business.finance.model.FinanceData;
import com.study.collect.business.finance.repository.FinanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FinanceDataService {

    private final FinanceRepository repository;

    public Page<FinanceDataVO> queryFinanceData(FinanceDataQueryRequest request) {
        // 构建分页和排序参数
        Sort sort = buildSort(request);
        PageRequest pageRequest = PageRequest.of(
            request.getPageNum() - 1,
            request.getPageSize(),
            sort
        );

        // 执行查询
        Page<FinanceData> dataPage = repository.findByConditions(
            request.getStockCode(),
            request.getStartTime(),
            request.getEndTime(),
            pageRequest
        );

        // 转换为VO
        return dataPage.map(this::convertToVO);
    }

    public FinanceDataVO getStockStats(String stockCode, String statsType) {
        FinanceDataVO stats = new FinanceDataVO();
        stats.setStockCode(stockCode);

        List<FinanceData> dataList = repository.findByStockCode(stockCode);
        if (dataList.isEmpty()) {
            return stats;
        }

        // 计算统计数据
        switch (statsType) {
            case "price" -> calculatePriceStats(dataList, stats);
            case "volume" -> calculateVolumeStats(dataList, stats);
            case "amount" -> calculateAmountStats(dataList, stats);
            default -> calculateAllStats(dataList, stats);
        }

        return stats;
    }

    public FinanceDataVO getRealtimeData(String stockCode) {
        FinanceData latestData = repository.findLatestByStockCode(stockCode);
        if (latestData == null) {
            return new FinanceDataVO();
        }

        FinanceDataVO vo = convertToVO(latestData);
        
        // 计算涨跌幅
        FinanceData previousData = repository.findPreviousByStockCode(stockCode, latestData.getTradeTime());
        if (previousData != null) {
            calculatePriceChange(vo, latestData, previousData);
        }

        return vo;
    }

    private Sort buildSort(FinanceDataQueryRequest request) {
        if (request.getSortField() != null && request.getSortOrder() != null) {
            Sort.Direction direction = "desc".equalsIgnoreCase(request.getSortOrder()) ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
            return Sort.by(direction, request.getSortField());
        }
        return Sort.by(Sort.Direction.DESC, "tradeTime");
    }

    private FinanceDataVO convertToVO(FinanceData data) {
        FinanceDataVO vo = new FinanceDataVO();
        vo.setId(data.getId());
        vo.setStockCode(data.getStockCode());
        vo.setStockName(data.getStockName());
        vo.setPrice(data.getPrice());
        vo.setVolume(data.getVolume());
        vo.setAmount(data.getAmount());
        vo.setTradeTime(data.getTradeTime());
        return vo;
    }

    private void calculatePriceChange(FinanceDataVO vo, FinanceData current, FinanceData previous) {
        BigDecimal priceChange = current.getPrice().subtract(previous.getPrice());
        vo.setPriceChange(priceChange);

        BigDecimal changePercent = priceChange
            .multiply(BigDecimal.valueOf(100))
            .divide(previous.getPrice(), 2, RoundingMode.HALF_UP);
        vo.setPriceChangePercent(changePercent);
    }

    private void calculatePriceStats(List<FinanceData> dataList, FinanceDataVO stats) {
        stats.setHighPrice(findMaxPrice(dataList));
        stats.setLowPrice(findMinPrice(dataList));
        stats.setAvgPrice(calculateAveragePrice(dataList));
    }

    private void calculateVolumeStats(List<FinanceData> dataList, FinanceDataVO stats) {
        stats.setTotalVolume(calculateTotalVolume(dataList));
    }

    private void calculateAmountStats(List<FinanceData> dataList, FinanceDataVO stats) {
        stats.setTotalAmount(calculateTotalAmount(dataList));
    }

    private void calculateAllStats(List<FinanceData> dataList, FinanceDataVO stats) {
        calculatePriceStats(dataList, stats);
        calculateVolumeStats(dataList, stats);
        calculateAmountStats(dataList, stats);
    }

    // 辅助计算方法
    private BigDecimal findMaxPrice(List<FinanceData> dataList) {
        return dataList.stream()
            .map(FinanceData::getPrice)
            .max(BigDecimal::compareTo)
            .orElse(BigDecimal.ZERO);
    }

    private BigDecimal findMinPrice(List<FinanceData> dataList) {
        return dataList.stream()
            .map(FinanceData::getPrice)
            .min(BigDecimal::compareTo)
            .orElse(BigDecimal.ZERO);
    }

    private BigDecimal calculateAveragePrice(List<FinanceData> dataList) {
        return dataList.stream()
            .map(FinanceData::getPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .divide(BigDecimal.valueOf
```

## Prompt:
继续没完成的部分

## Response:
继续完成FinanceDataService中的计算方法和后续的Repository实现:

```java
// FinanceDataService.java (续)
    private BigDecimal calculateAveragePrice(List<FinanceData> dataList) {
        return dataList.stream()
            .map(FinanceData::getPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .divide(BigDecimal.valueOf(dataList.size()), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateTotalVolume(List<FinanceData> dataList) {
        return dataList.stream()
            .map(FinanceData::getVolume)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateTotalAmount(List<FinanceData> dataList) {
        return dataList.stream()
            .map(FinanceData::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
```

5.  修改Repository层:

```java
// FinanceRepository.java
package com.study.collect.business.finance.repository;

import com.study.collect.business.finance.model.FinanceData;
import com.study.collect.core.storage.repository.IRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface FinanceRepository extends IRepository<FinanceData, String>, MongoRepository<FinanceData, String> {
    
    // 基础查询方法
    List<FinanceData> findByStockCode(String stockCode);

    @Query("{'stockCode': ?0, 'tradeTime': {'$gte': ?1, '$lte': ?2}}")
    Page<FinanceData> findByConditions(String stockCode, LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);

    // 获取最新数据
    @Query(value = "{'stockCode': ?0}", sort = "{'tradeTime': -1}")
    FinanceData findLatestByStockCode(String stockCode);

    // 获取指定时间之前的最新数据
    @Query(value = "{'stockCode': ?0, 'tradeTime': {'$lt': ?1}}", sort = "{'tradeTime': -1}")
    FinanceData findPreviousByStockCode(String stockCode, LocalDateTime tradeTime);

    // 批量操作方法
    @Query(value = "{'stockCode': ?0, 'tradeTime': {'$gte': ?1, '$lte': ?2}}", 
           sort = "{'tradeTime': 1}")
    List<FinanceData> findByStockCodeAndTimeBetween(String stockCode, LocalDateTime startTime, LocalDateTime endTime);

    // 统计查询
    @Query(value = "{'stockCode': ?0}", 
           count = true)
    long countByStockCode(String stockCode);

    // 自定义更新操作
    @Query(value = "{'stockCode': ?0}", 
           fields = "{'price': 1, 'volume': 1, 'amount': 1}")
    List<FinanceData> findStatsDataByStockCode(String stockCode);
}
```

6.  最后添加配置类:

```java
// FinanceConfiguration.java
package com.study.collect.business.finance.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableConfigurationProperties(FinanceProperties.class)
public class FinanceConfiguration {

    @Bean
    public void ensureIndexes(MongoTemplate mongoTemplate) {
        IndexOperations indexOps = mongoTemplate.indexOps("finance_data");

        // 创建复合索引
        indexOps.ensureIndex(new Index()
            .on("stockCode", org.springframework.data.domain.Sort.Direction.ASC)
            .on("tradeTime", org.springframework.data.domain.Sort.Direction.DESC));

        // 创建TTL索引
        indexOps.ensureIndex(new Index()
            .on("createTime", org.springframework.data.domain.Sort.Direction.ASC)
            .expire(7, TimeUnit.DAYS));
    }
}

// FinanceProperties.java
package com.study.collect.business.finance.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "finance")
public class FinanceProperties {
    
    private Collector collector = new Collector();
    private Cache cache = new Cache();
    
    @Data
    public static class Collector {
        private int batchSize = 1000;
        private int threadPoolSize = 5;
        private long timeoutSeconds = 300;
    }
    
    @Data
    public static class Cache {
        private long expireSeconds = 300;
        private String prefix = "finance:";
    }
}
```

这样就完成了business-finance模块的全部改造,实现了:

1.  基于分片的分布式采集
2.  动态数据生成接口
3.  灵活的数据查询功能
4.  实时和统计数据查询
5.  缓存和索引优化

主要特点:

1.  利用collect-core的分片能力实现并发采集
2.  使用MongoDB实现高效的数据存储和查询
3.  通过Redis实现分布式锁和缓存
4.  提供了完整的API接口和服务实现

需要我对任何部分做进一步解释吗?

## Prompt:
检查下还有缺少的吗？然后总结下，最后在collect-start启动时配置类如何写，表结构，还有表数据需要初始化吗

## Response:
检查后发现还需要补充以下内容：

1.  MongoDB中需要定义的表结构：

```java
// FinanceStockInfo.java (新增)
package com.study.collect.business.finance.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document(collection = "finance_stock_info")
public class FinanceStockInfo {
    @Id
    private String id;
    private String stockCode;      // 股票代码
    private String stockName;      // 股票名称
    private String industry;       // 所属行业
    private String market;         // 所属市场(主板/创业板等)
    private Boolean enabled;       // 是否启用
    private LocalDateTime listDate;// 上市日期
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
```

2.  初始化数据的SQL文件：

```sql
-- init_finance_data.sql
-- 初始化股票信息
INSERT INTO finance_stock_info (stock_code, stock_name, industry, market, enabled, list_date) VALUES 
('000001', '平安银行', '银行', '主板', true, '1991-04-03'),
('000002', '万科A', '房地产', '主板', true, '1991-01-29'),
('000063', '中兴通讯', '通信设备', '主板', true, '1997-11-18'),
('000066', '中国长城', '计算机设备', '主板', true, '1997-06-02'),
('000069', '华侨城A', '旅游景区', '主板', true, '1997-09-10');

-- 初始化任务配置
INSERT INTO task_config (
    task_code, task_name, task_handler, cron_expr, shard_total, status, remark
) VALUES (
    'finance_collect', '金融数据采集', 'financeCollectHandler', 
    '0 0/5 * * * ?', 4, 1, '每5分钟采集一次金融数据'
);
```

3.  在collect-starter中的配置：

```yaml
# application-finance.yml
finance:
  collector:
    batch-size: 1000
    thread-pool-size: 5
    timeout-seconds: 300
  cache:
    expire-seconds: 300
    prefix: "finance:"

spring:
  data:
    mongodb:
      finance:
        database: finance
        collection: finance_data
        index-prefix: idx_
    redis:
      finance:
        database: 1
```

4.  启动配置类：

```java
// FinanceAutoConfiguration.java
package com.study.collect.starter.config;

import com.study.collect.business.finance.config.FinanceConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
    FinanceConfiguration.class
})
public class FinanceAutoConfiguration {
}

// CollectApplication.java 修改
@SpringBootApplication
@EnableScheduling
@Import({
    FinanceAutoConfiguration.class
})
public class CollectApplication {
    public static void main(String[] args) {
        SpringApplication.run(CollectApplication.class, args);
    }
}
```

总结一下完整的实现：

1.  系统架构：

*   采用分层架构：API层、Service层、Repository层
*   利用collect-core提供的分布式采集框架
*   使用MongoDB存储数据，Redis做缓存
*   支持任务分片执行

2.  核心功能：

*   分布式数据采集
*   实时数据生成
*   历史数据查询
*   统计分析功能
*   数据同步功能

3.  关键特性：

*   支持多分片并行采集
*   采用缓存减少重复采集
*   实现分布式锁避免并发问题
*   提供灵活的查询接口
*   支持数据统计和分析

4.  数据结构：

*   finance\_data：金融交易数据
*   finance\_stock\_info：股票基础信息
*   task\_config：任务配置信息

5.  部署配置：

*   application.yml：基础配置
*   application-finance.yml：金融模块配置
*   init\_finance\_data.sql：初始化数据

6.  使用方式：

```bash
# 启动应用
java -jar collect-starter.jar --spring.profiles.active=dev,finance

# 初始化数据
mysql -u root -p < init_finance_data.sql
```

7.  验证测试：

```bash
# 生成测试数据
curl -X POST http://localhost:8080/api/finance/generate -d '{"stockCode":"000001","dataCount":100}'

# 查询数据
curl http://localhost:8080/api/finance/query?stockCode=000001&pageSize=10&pageNum=1

# 获取统计数据
curl http://localhost:8080/api/finance/stats/000001
```

是否需要我对任何部分进行更详细的说明？

