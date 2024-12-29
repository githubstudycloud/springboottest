https://claude.ai/chat/1e55ffcf-589a-4be7-8fb2-09fd27216558
## Prompt:
参考代码和在完成对5个公共模块的改造设计后，如何对business-enterprise改造以适配新的任务，还有表结构和初始化数据

## Response:
好的,让我帮您设计business-enterprise模块的改造方案。

### 一、目录结构改造

```plaintext
business-enterprise/
├── entity/                          # 实体层
│   ├── Enterprise.java              # 企业实体(改造,继承VersionEntity)
│   ├── EnterpriseData.java         # 企业数据实体  
│   └── EnterpriseMetrics.java      # 企业指标实体
├── collector/                       # 采集层
│   ├── EnterpriseCollector.java    # 企业采集器(改造)
│   ├── EnterpriseCompareCollector.java # 企业对比采集器(新增)
│   └── config/
│       └── CollectorConfig.java     # 采集配置
├── processor/                       # 处理层 
│   ├── EnterpriseProcessor.java     # 企业处理器(改造)
│   ├── EnterpriseDataProcessor.java # 数据处理器
│   ├── EnterpriseDiffProcessor.java # 差异处理器
│   └── chain/
│       └── EnterpriseChainBuilder.java # 处理器链构建
├── repository/                      # 存储层
│   ├── EnterpriseRepository.java    # 企业仓储(改造)
│   ├── EnterpriseDataRepository.java # 数据仓储
│   └── EnterpriseMetricsRepository.java # 指标仓储
├── service/                         # 服务层
│   ├── EnterpriseService.java       # 企业服务(改造)
│   └── EnterpriseMetricsService.java # 指标服务
└── task/                           # 任务层(新增)
    ├── EnterpriseTaskHandler.java   # 任务处理器
    ├── EnterpriseCollectTask.java   # 采集任务
    └── config/
        └── TaskConfig.java          # 任务配置
```

### 二、表结构设计

```sql
-- 1. 企业主表(改造)
CREATE TABLE `t_enterprise` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `code` varchar(50) NOT NULL COMMENT '企业编码',
    `name` varchar(100) NOT NULL COMMENT '企业名称',
    `address` varchar(255) COMMENT '企业地址',
    `contact` varchar(50) COMMENT '联系人',
    `phone` varchar(20) COMMENT '联系电话',
    `industry` varchar(50) COMMENT '所属行业',
    `reg_capital` decimal(20,2) COMMENT '注册资本',  
    `reg_authority` varchar(100) COMMENT '注册机构',
    `est_date` date COMMENT '成立日期',
    `version_code` varchar(32) COMMENT '版本编号',
    `version_name` varchar(50) COMMENT '版本名称', 
    `version_desc` varchar(255) COMMENT '版本说明',
    `version_time` datetime COMMENT '版本时间',
    `is_current` tinyint(1) COMMENT '是否当前版本',
    `status` tinyint COMMENT '状态:0-禁用,1-启用',
    `deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_by` varchar(50) COMMENT '创建人',
    `update_by` varchar(50) COMMENT '更新人',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`),
    KEY `idx_version` (`version_code`)  -- 添加版本索引
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='企业信息表';

-- 2. 企业数据表(新增)
CREATE TABLE `t_enterprise_data` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `enterprise_code` varchar(50) NOT NULL COMMENT '企业编码',
    `data_type` varchar(50) NOT NULL COMMENT '数据类型',
    `data_time` datetime COMMENT '数据时间',
    `data_value` text COMMENT '数据内容(JSON)',
    `source` varchar(50) COMMENT '数据来源',
    `version_code` varchar(32) COMMENT '版本编号',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_enterprise` (`enterprise_code`),
    KEY `idx_type_time` (`data_type`,`data_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='企业数据表';

-- 3. 企业指标表(新增)
CREATE TABLE `t_enterprise_metrics` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `enterprise_code` varchar(50) NOT NULL COMMENT '企业编码',
    `metrics_type` varchar(50) NOT NULL COMMENT '指标类型',
    `metrics_time` datetime COMMENT '指标时间',
    `metrics_value` decimal(20,2) COMMENT '指标值',
    `metrics_unit` varchar(20) COMMENT '指标单位',
    `source` varchar(50) COMMENT '指标来源',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_enterprise` (`enterprise_code`),
    KEY `idx_type_time` (`metrics_type`,`metrics_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='企业指标表';
```

### 三、初始化数据

```sql
-- 1. 初始化任务配置
INSERT INTO `task_config` (
    `task_code`, 
    `task_name`,
    `task_handler`,
    `task_param`,
    `cron_expr`,
    `priority`,
    `retry_count`,
    `timeout`,
    `status`,
    `remark`
) VALUES 
-- 1.1 企业基本信息采集任务
(
    'enterprise_collect',
    '企业信息采集任务',
    'enterprise',
    '{
        "batchSize": 100,
        "enableSplit": true,
        "splitSize": 1000,
        "compareEnabled": true,
        "retryInterval": 300,
        "sourceType": "api"
    }',
    '0 0 1 * * ?',  -- 每天凌晨1点执行
    5,               -- 中等优先级
    3,               -- 最多重试3次
    7200,            -- 2小时超时
    1,               -- 启用状态
    '定时采集企业基本信息'
),
-- 1.2 企业指标采集任务  
(
    'enterprise_metrics_collect',
    '企业指标采集任务',
    'enterprise_metrics',
    '{
        "metricsTypes": ["finance", "employee", "tax"],
        "timeRange": "1d",
        "compareEnabled": true,
        "alertThreshold": 0.1
    }',
    '0 0 2 * * ?',  -- 每天凌晨2点执行
    6,               -- 较高优先级
    3,               -- 最多重试3次 
    3600,            -- 1小时超时
    1,               -- 启用状态
    '定时采集企业关键指标'
);

-- 2. 初始化测试数据
INSERT INTO `t_enterprise` (
    `code`,
    `name`, 
    `industry`,
    `reg_capital`,
    `reg_authority`,
    `est_date`,
    `version_code`,
    `is_current`,
    `status`
) VALUES
('ENT001', '测试企业1', '制造业', 1000000.00, '北京市', '2020-01-01', 'V20240101000001', 1, 1),
('ENT002', '测试企业2', '服务业', 2000000.00, '上海市', '2020-02-01', 'V20240101000001', 1, 1);
```

接下来我可以为您详细实现某个具体模块的改造代码,比如:

1.  Enterprise实体改造(继承VersionEntity)
2.  EnterpriseCollector改造(支持对比采集)
3.  EnterpriseProcessor改造(支持处理链)
4.  EnterpriseTaskHandler改造(适配新任务框架)

您想先看哪部分的具体实现?

## Prompt:
按顺序继续

## Response:
好的,让我们从Enterprise实体改造开始。

### 1\. 企业实体改造

```java
// Enterprise.java - 企业实体
package com.study.collect.business.enterprise.entity;

import com.study.collect.core.storage.entity.VersionEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "enterprise")
public class Enterprise extends VersionEntity {

    private String code;           // 企业编码
    private String name;           // 企业名称
    private String address;        // 企业地址
    private String contact;        // 联系人
    private String phone;          // 联系电话
    private String industry;       // 所属行业
    private BigDecimal regCapital; // 注册资本
    private String regAuthority;   // 注册机构
    private LocalDate estDate;     // 成立日期
    private String status;         // 状态
    
    // 数据来源信息
    private String source;         // 数据来源
    private String sourceId;       // 源系统ID
    private LocalDateTime sourceTime; // 源系统时间
    
    // 数据质量信息
    private Boolean verified;      // 是否已验证
    private String verifyStatus;   // 验证状态
    private String verifyRemark;   // 验证说明
    
    @Override
    public void validate() {
        super.validate();
        // 业务字段校验
        Assert.hasText(code, "企业编码不能为空");
        Assert.hasText(name, "企业名称不能为空");
        Assert.hasText(industry, "所属行业不能为空");
    }
    
    /**
     * 创建数据快照
     */
    public Enterprise createSnapshot() {
        Enterprise snapshot = new Enterprise();
        BeanUtils.copyProperties(this, snapshot);
        
        // 清除ID等字段
        snapshot.setId(null);
        snapshot.setCreateTime(null);
        snapshot.setUpdateTime(null);
        
        return snapshot;
    }
    
    /**
     * 比较两个版本的差异
     */
    public Map<String, Object> compareWith(Enterprise other) {
        Map<String, Object> diffs = new HashMap<>();
        
        // 比较基本信息
        if (!Objects.equals(this.name, other.name)) {
            diffs.put("name", new String[]{this.name, other.name});
        }
        if (!Objects.equals(this.address, other.address)) {
            diffs.put("address", new String[]{this.address, other.address});
        }
        // ... 比较其他字段
        
        return diffs;
    }
}

// EnterpriseData.java - 企业数据实体
package com.study.collect.business.enterprise.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document(collection = "enterprise_data")
public class EnterpriseData {
    
    private String id;
    private String enterpriseCode;  // 企业编码
    private String dataType;        // 数据类型
    private LocalDateTime dataTime; // 数据时间
    private String dataValue;       // 数据内容(JSON)
    private String source;          // 数据来源
    private String versionCode;     // 版本编号
    private LocalDateTime createTime; // 创建时间
    
    /**
     * 获取数据值
     */
    public <T> T getDataValueObject(Class<T> clazz) {
        if (StringUtils.hasText(this.dataValue)) {
            return JsonUtils.fromJson(this.dataValue, clazz);
        }
        return null;
    }
    
    /**
     * 设置数据值
     */
    public void setDataValueObject(Object value) {
        if (value != null) {
            this.dataValue = JsonUtils.toJson(value);
        }
    }
}

// EnterpriseMetrics.java - 企业指标实体
package com.study.collect.business.enterprise.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Document(collection = "enterprise_metrics")
public class EnterpriseMetrics {

    private String id;
    private String enterpriseCode;   // 企业编码
    private String metricsType;      // 指标类型
    private LocalDateTime metricsTime; // 指标时间
    private BigDecimal metricsValue; // 指标值
    private String metricsUnit;      // 指标单位
    private String source;           // 指标来源
    private LocalDateTime createTime; // 创建时间
    
    /**
     * 计算同比变化
     */
    public BigDecimal calculateYearOverYear(BigDecimal lastValue) {
        if (lastValue == null || lastValue.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }
        return this.metricsValue.subtract(lastValue)
                .divide(lastValue, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
    }
    
    /**
     * 验证指标阈值
     */
    public boolean validateThreshold(BigDecimal threshold) {
        return this.metricsValue.abs().compareTo(threshold) > 0;
    }
}
```

### 2\. 仓储层改造

```java
// EnterpriseRepository.java - 企业仓储
package com.study.collect.business.enterprise.repository;

import com.study.collect.core.storage.repository.VersionRepository;
import com.study.collect.business.enterprise.entity.Enterprise;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;

public interface EnterpriseRepository extends VersionRepository<Enterprise> {

    /**
     * 根据编码查询当前版本
     */
    @Query(value = "{'code': ?0, 'current': true}")
    Enterprise findCurrentByCode(String code);
    
    /**
     * 根据行业查询当前版本
     */
    @Query(value = "{'industry': ?0, 'current': true}")
    List<Enterprise> findCurrentByIndustry(String industry);
    
    /**
     * 分片查询
     */
    @Query(value = "{'current': true, '$where': 'Math.abs(this.code.hashCode() % ?1) == ?0'}")
    List<Enterprise> findBySharding(int shardIndex, int shardTotal);
    
    /**
     * 根据版本查询
     */
    List<Enterprise> findByVersionCode(String versionCode);
    
    /**
     * 查询版本历史
     */
    List<Enterprise> findByCodeOrderByVersionTimeDesc(String code);
}

// EnterpriseDataRepository.java - 企业数据仓储
package com.study.collect.business.enterprise.repository;

import com.study.collect.business.enterprise.entity.EnterpriseData;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface EnterpriseDataRepository extends MongoRepository<EnterpriseData, String> {

    /**
     * 根据企业编码和数据类型查询
     */
    List<EnterpriseData> findByEnterpriseCodeAndDataType(
            String enterpriseCode, String dataType);
            
    /**
     * 根据时间范围查询
     */
    List<EnterpriseData> findByEnterpriseCodeAndDataTypeAndDataTimeBetween(
            String enterpriseCode, String dataType, 
            LocalDateTime startTime, LocalDateTime endTime);
            
    /**
     * 根据版本查询
     */
    List<EnterpriseData> findByVersionCode(String versionCode);
}

// EnterpriseMetricsRepository.java - 企业指标仓储
package com.study.collect.business.enterprise.repository;

import com.study.collect.business.enterprise.entity.EnterpriseMetrics;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface EnterpriseMetricsRepository extends MongoRepository<EnterpriseMetrics, String> {

    /**
     * 根据企业编码和指标类型查询
     */
    List<EnterpriseMetrics> findByEnterpriseCodeAndMetricsType(
            String enterpriseCode, String metricsType);
            
    /**
     * 根据时间范围查询
     */
    List<EnterpriseMetrics> findByEnterpriseCodeAndMetricsTypeAndMetricsTimeBetween(
            String enterpriseCode, String metricsType,
            LocalDateTime startTime, LocalDateTime endTime);
            
    /**
     * 聚合统计
     */
    @Aggregation(pipeline = {
        "{ $match: { enterpriseCode: ?0, metricsType: ?1 } }",
        "{ $group: { _id: null, avgValue: { $avg: '$metricsValue' } } }"
    })
    Double calculateAvgValue(String enterpriseCode, String metricsType);
}
```

需要我继续实现收集器(Collector)改造的部分吗？主要包括：

1.  EnterpriseCollector的改造
2.  EnterpriseCompareCollector的实现
3.  相关配置类的实现

## Prompt:
继续

## Response:
好的，让我们继续实现采集器(Collector)相关的改造。

### 1\. EnterpriseCollector改造

```java
// EnterpriseCollector.java - 企业采集器改造
package com.study.collect.business.enterprise.collector;

import com.study.collect.core.collector.template.BatchCollector;
import com.study.collect.core.collector.model.CollectContext;
import com.study.collect.core.collector.model.CollectResult;
import com.study.collect.business.enterprise.entity.Enterprise;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EnterpriseCollector extends BatchCollector<String, Enterprise> {

    private final EnterpriseRepository repository;
    private final EnterpriseApiClient apiClient;    // 假设有个API客户端
    private final CollectorConfig config;

    @Override
    public String getType() {
        return "enterprise";
    }

    @Override
    protected int getBatchSize() {
        return config.getBatchSize();
    }

    @Override
    protected Enterprise collectSingle(String code, CollectContext<List<String>> context) {
        try {
            // 1. 从API获取数据
            EnterpriseDTO dto = apiClient.getEnterpriseInfo(code);
            if (dto == null) {
                log.warn("企业数据不存在: {}", code);
                return null;
            }

            // 2. 转换数据
            Enterprise enterprise = convertToEntity(dto);
            enterprise.setSource("api");
            enterprise.setSourceTime(LocalDateTime.now());

            // 3. 获取当前版本
            Enterprise current = repository.findCurrentByCode(code);
            
            // 4. 判断是否需要新版本
            if (current == null || hasChanged(current, enterprise)) {
                // 创建新版本
                if (current != null) {
                    current.markHistory();
                    repository.save(current);
                }
                enterprise.initVersion();
                repository.save(enterprise);
                
                // 记录变更
                recordDataChange(current, enterprise);
            }

            return enterprise;

        } catch (Exception e) {
            log.error("采集企业数据异常: " + code, e);
            throw new CollectException("采集失败: " + e.getMessage());
        }
    }

    /**
     * 判断数据是否有变化
     */
    private boolean hasChanged(Enterprise current, Enterprise latest) {
        // 比较关键字段
        return !Objects.equals(current.getName(), latest.getName()) ||
               !Objects.equals(current.getAddress(), latest.getAddress()) ||
               !Objects.equals(current.getIndustry(), latest.getIndustry()) ||
               !Objects.equals(current.getRegCapital(), latest.getRegCapital());
    }

    /**
     * 记录数据变更
     */
    private void recordDataChange(Enterprise oldData, Enterprise newData) {
        if (oldData == null || newData == null) {
            return;
        }
        
        // 获取差异字段
        Map<String, Object> diffs = oldData.compareWith(newData);
        if (!diffs.isEmpty()) {
            // 记录变更日志
            DataChangeLog changeLog = new DataChangeLog();
            changeLog.setEntityType("enterprise");
            changeLog.setEntityId(newData.getCode());
            changeLog.setOldVersion(oldData.getVersionCode());
            changeLog.setNewVersion(newData.getVersionCode());
            changeLog.setChanges(diffs);
            changeLog.setChangeTime(LocalDateTime.now());
            
            // 保存变更日志
            changeLogRepository.save(changeLog);
        }
    }
}
```

### 2\. 对比采集器实现

```java
// EnterpriseCompareCollector.java - 企业对比采集器
package com.study.collect.business.enterprise.collector;

import com.study.collect.core.collector.compare.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EnterpriseCompareCollector implements CompareCollector<Enterprise> {

    private final EnterpriseRepository repository;
    private final EnterpriseMetricsRepository metricsRepository;
    private final EnterpriseApiClient apiClient;
    private final CollectorConfig config;

    @Override
    public DiffResult<Enterprise> compare(CompareContext context) {
        DiffResult<Enterprise> result = new DiffResult<>();
        
        try {
            // 1. 获取API数据
            List<EnterpriseDTO> apiData = apiClient.batchGetEnterprises(
                    context.getParams(), context.getCompareTime());
                    
            // 2. 获取本地数据
            List<Enterprise> localData = repository.findByVersionCode(
                    context.getBaseVersion());
                    
            // 3. 对比数据
            Map<String, Enterprise> localMap = localData.stream()
                    .collect(Collectors.toMap(Enterprise::getCode, e -> e));
                    
            // 4. 分析差异
            for (EnterpriseDTO dto : apiData) {
                Enterprise local = localMap.get(dto.getCode());
                Enterprise latest = convertToEntity(dto);
                
                if (local == null) {
                    // 新增的数据
                    result.getAdded().add(latest);
                } else if (hasChanged(local, latest)) {
                    // 变更的数据
                    result.getUpdated().add(latest);
                    localMap.remove(dto.getCode());
                }
            }
            
            // 删除的数据
            result.getDeleted().addAll(localMap.values());
            
            // 5. 统计结果
            result.addCount("total", (long) apiData.size());
            result.addCount("added", (long) result.getAdded().size());
            result.addCount("updated", (long) result.getUpdated().size());
            result.addCount("deleted", (long) result.getDeleted().size());
            result.setChanged(!result.getAdded().isEmpty() || 
                            !result.getUpdated().isEmpty() || 
                            !result.getDeleted().isEmpty());
            
            return result;
            
        } catch (Exception e) {
            log.error("对比数据异常", e);
            throw new CompareException("数据对比失败: " + e.getMessage());
        }
    }

    @Override
    public MetricsData getMetrics(String source) {
        MetricsData metrics = new MetricsData();
        metrics.setSource(source);
        metrics.setMetricsTime(LocalDateTime.now());
        
        try {
            // 1. 获取API数据量
            long apiCount = apiClient.getEnterpriseCount();
            metrics.setValue("api_count", apiCount);
            
            // 2. 获取本地数据量
            long localCount = repository.count();
            metrics.setValue("local_count", localCount);
            
            // 3. 获取更新频率
            double updateRate = calculateUpdateRate();
            metrics.setValue("update_rate", updateRate);
            
            return metrics;
            
        } catch (Exception e) {
            log.error("获取指标异常", e);
            throw new MetricsException("获取指标失败: " + e.getMessage());
        }
    }

    @Override
    public boolean shouldTrigger(MetricsData metrics) {
        // 1. 数据量差异检查
        long apiCount = (long) metrics.getValue("api_count");
        long localCount = (long) metrics.getValue("local_count");
        if (Math.abs(apiCount - localCount) > config.getCountThreshold()) {
            return true;
        }
        
        // 2. 更新率检查
        double updateRate = (double) metrics.getValue("update_rate");
        if (updateRate > config.getUpdateRateThreshold()) {
            return true;
        }
        
        return false;
    }

    @Override
    public CompareType getCompareType() {
        return CompareType.FULL;
    }

    /**
     * 计算更新频率
     */
    private double calculateUpdateRate() {
        LocalDateTime time = LocalDateTime.now().minusHours(24);
        long totalCount = repository.count();
        long updateCount = repository.countByUpdateTimeAfter(time);
        return totalCount > 0 ? (double) updateCount / totalCount : 0;
    }
}
```

### 3\. 配置类实现

```java
// CollectorConfig.java - 采集配置
package com.study.collect.business.enterprise.collector.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "enterprise.collector")
public class CollectorConfig {
    
    // 基础配置
    private int batchSize = 100;
    private int retryCount = 3;
    private int timeout = 60;
    
    // API配置
    private String apiUrl;
    private String apiKey;
    private int apiTimeout = 30;
    
    // 对比配置
    private boolean compareEnabled = true;
    private int countThreshold = 1000;    // 数据量差异阈值
    private double updateRateThreshold = 0.1;  // 更新率阈值
    
    // 版本配置
    private boolean versionEnabled = true;
    private String versionNaming = "V{yyyyMMdd}_{index}";
    private boolean keepHistory = true;
    
    // 监控配置
    private boolean metricsEnabled = true;
    private int metricsInterval = 3600;
}

// CollectorAutoConfiguration.java - 采集自动配置
package com.study.collect.business.enterprise.collector.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(CollectorConfig.class)
public class CollectorAutoConfiguration {

    @Bean
    public EnterpriseApiClient enterpriseApiClient(CollectorConfig config) {
        return new EnterpriseApiClient(
            config.getApiUrl(),
            config.getApiKey(),
            config.getApiTimeout()
        );
    }

    @Bean
    public CollectorMetrics collectorMetrics(MeterRegistry registry) {
        return new CollectorMetrics(registry);
    }

    @Bean
    public CollectorMonitor collectorMonitor(
            List<Collector<?>> collectors,
            CollectorMetrics metrics,
            AlertService alertService) {
        return new CollectorMonitor(collectors, metrics, alertService);
    }
}
```

这样我们完成了企业采集器的改造，主要包括：

1.  基础采集器改造
    *   支持批量采集
    *   版本控制
    *   变更追踪
2.  对比采集器实现
    *   数据对比
    *   指标采集
    *   触发判断
3.  相关配置实现
    *   采集配置
    *   API配置
    *   监控配置

接下来需要我继续实现处理器(Processor)的改造吗？主要包括：

1.  处理器实现
2.  处理器链构建
3.  处理器配置

## Prompt:
继续

## Response:
好的，让我们继续实现处理器(Processor)相关的改造。

### 1\. 处理器实现

```java
// EnterpriseProcessor.java - 主处理器
package com.study.collect.business.enterprise.processor;

import com.study.collect.core.processor.template.AbstractProcessor;
import com.study.collect.business.enterprise.entity.Enterprise;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EnterpriseProcessor extends AbstractProcessor<Enterprise> {
    
    private final ProcessorConfig config;
    private final ProcessorMetrics metrics;

    public EnterpriseProcessor(ProcessorConfig config, ProcessorMetrics metrics) {
        super(metrics, config);
        this.config = config;
        this.metrics = metrics;
    }

    @Override
    protected Enterprise doProcess(Enterprise data, ProcessContext context) {
        // 1. 数据验证
        validateEnterpriseData(data);
        
        // 2. 数据标准化
        standardizeData(data);
        
        // 3. 数据富化
        enrichData(data);
        
        return data;
    }

    private void validateEnterpriseData(Enterprise data) {
        // 验证必填字段
        Assert.hasText(data.getCode(), "企业编码不能为空");
        Assert.hasText(data.getName(), "企业名称不能为空");
        
        // 验证数据格式
        validatePhoneFormat(data.getPhone());
        validateCapitalFormat(data.getRegCapital());
        
        // 验证业务规则
        validateBusinessRules(data);
    }

    private void standardizeData(Enterprise data) {
        // 标准化电话号码
        if (StringUtils.hasText(data.getPhone())) {
            data.setPhone(standardizePhone(data.getPhone()));
        }
        
        // 标准化地址
        if (StringUtils.hasText(data.getAddress())) {
            data.setAddress(standardizeAddress(data.getAddress()));
        }
        
        // 标准化行业代码
        if (StringUtils.hasText(data.getIndustry())) {
            data.setIndustry(standardizeIndustry(data.getIndustry()));
        }
    }

    private void enrichData(Enterprise data) {
        // 补充行业名称
        enrichIndustryName(data);
        
        // 补充区域信息
        enrichRegionInfo(data);
        
        // 补充注册机构信息
        enrichRegAuthority(data);
    }

    @Override
    public String getType() {
        return "enterprise";
    }

    @Override
    public int getOrder() {
        return 1;
    }
}

// EnterpriseDiffProcessor.java - 差异处理器
@Slf4j
@Component
public class EnterpriseDiffProcessor extends AbstractProcessor<Enterprise> {

    private final EnterpriseRepository repository;
    private final DataChangeLogRepository changeLogRepository;

    @Override
    protected Enterprise doProcess(Enterprise data, ProcessContext context) {
        // 1. 获取当前版本数据
        Enterprise current = repository.findCurrentByCode(data.getCode());
        if (current == null) {
            return data;
        }

        // 2. 对比差异
        Map<String, Object> diffs = current.compareWith(data);
        if (diffs.isEmpty()) {
            return null;  // 无变化，不需要处理
        }

        // 3. 记录变更
        recordDataChange(current, data, diffs);

        // 4. 设置版本信息
        data.initVersion();
        current.markHistory();
        
        return data;
    }

    private void recordDataChange(Enterprise oldData, Enterprise newData, 
            Map<String, Object> diffs) {
        DataChangeLog changeLog = new DataChangeLog();
        changeLog.setEntityType("enterprise");
        changeLog.setEntityId(newData.getCode());
        changeLog.setOldVersion(oldData.getVersionCode());
        changeLog.setNewVersion(newData.getVersionCode());
        changeLog.setChanges(diffs);
        changeLog.setChangeTime(LocalDateTime.now());
        
        changeLogRepository.save(changeLog);
        
        log.info("记录数据变更: {}, 变更字段: {}", 
            newData.getCode(), String.join(",", diffs.keySet()));
    }

    @Override
    public String getType() {
        return "enterprise_diff";
    }

    @Override
    public int getOrder() {
        return 2;
    }
}

// EnterpriseDataProcessor.java - 数据处理器
@Slf4j
@Component
public class EnterpriseDataProcessor extends AbstractProcessor<Enterprise> {

    private final EnterpriseDataRepository dataRepository;
    private final EnterpriseMetricsRepository metricsRepository;

    @Override
    protected Enterprise doProcess(Enterprise data, ProcessContext context) {
        // 1. 计算关键指标
        calculateMetrics(data);
        
        // 2. 保存扩展数据
        saveExtraData(data);
        
        // 3. 生成统计数据
        generateStatistics(data);
        
        return data;
    }

    private void calculateMetrics(Enterprise data) {
        // 计算财务指标
        calculateFinancialMetrics(data);
        
        // 计算运营指标
        calculateOperationalMetrics(data);
        
        // 计算风险指标
        calculateRiskMetrics(data);
    }

    private void saveExtraData(Enterprise data) {
        // 保存历史数据
        EnterpriseData historyData = new EnterpriseData();
        historyData.setEnterpriseCode(data.getCode());
        historyData.setDataTime(LocalDateTime.now());
        historyData.setDataType("HISTORY");
        historyData.setDataValueObject(data);
        dataRepository.save(historyData);

        // 保存详细信息
        EnterpriseData detailData = new EnterpriseData();
        // ... 设置详细信息
        dataRepository.save(detailData);
    }

    @Override
    public String getType() {
        return "enterprise_data";
    }

    @Override
    public int getOrder() {
        return 3;
    }
}
```

### 2\. 处理器链构建

```java
// EnterpriseChainBuilder.java - 处理器链构建
package com.study.collect.business.enterprise.processor.chain;

import com.study.collect.core.processor.chain.ProcessorChain;
import com.study.collect.core.processor.chain.ProcessorChainConfig;
import com.study.collect.business.enterprise.entity.Enterprise;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EnterpriseChainBuilder {

    private final EnterpriseProcessor enterpriseProcessor;
    private final EnterpriseDiffProcessor diffProcessor;
    private final EnterpriseDataProcessor dataProcessor;

    /**
     * 构建处理器链
     */
    public ProcessorChain<Enterprise> buildChain() {
        ProcessorChainConfig config = ProcessorChainConfig.builder()
                .failFast(false)
                .breakOnNull(true)
                .alertOnError(true)
                .maxRetries(3)
                .retryInterval(1000)
                .build();

        ProcessorChain<Enterprise> chain = new ProcessorChain<>(config);

        // 添加基础处理器
        chain.addProcessor(enterpriseProcessor);

        // 添加差异处理器(条件:启用版本控制)
        chain.addProcessor(diffProcessor, context -> 
            Boolean.TRUE.equals(context.getAttribute("versionEnabled")));

        // 添加数据处理器(条件:需要处理扩展数据)
        chain.addProcessor(dataProcessor, context ->
            Boolean.TRUE.equals(context.getAttribute("processExtraData")));

        return chain;
    }

    /**
     * 构建简单处理链
     */
    public ProcessorChain<Enterprise> buildSimpleChain() {
        ProcessorChain<Enterprise> chain = new ProcessorChain<>(
            ProcessorChainConfig.defaultConfig());
        chain.addProcessor(enterpriseProcessor);
        return chain;
    }

    /**
     * 构建全量处理链
     */
    public ProcessorChain<Enterprise> buildFullChain() {
        ProcessorChain<Enterprise> chain = new ProcessorChain<>(
            ProcessorChainConfig.defaultConfig());
        
        // 添加所有处理器
        chain.addProcessor(enterpriseProcessor);
        chain.addProcessor(diffProcessor);
        chain.addProcessor(dataProcessor);
        
        return chain;
    }
}
```

### 3\. 处理器配置

```java
// ProcessorConfig.java - 处理器配置
package com.study.collect.business.enterprise.processor.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "enterprise.processor")
public class ProcessorConfig {
    
    // 基础配置
    private boolean enabled = true;
    private boolean validateData = true;
    private boolean keepSource = true;
    
    // 处理配置
    private boolean standardizeData = true;
    private boolean enrichData = true;
    
    // 差异配置
    private boolean compareEnabled = true;
    private boolean recordChanges = true;
    
    // 数据配置
    private boolean saveHistory = true;
    private boolean saveDetail = true;
    private int historyKeepDays = 30;
    
    // 监控配置
    private boolean enableMetrics = true;
    private int metricsInterval = 60;
}

// ProcessorAutoConfiguration.java - 处理器自动配置
@Configuration
@EnableConfigurationProperties(ProcessorConfig.class)
public class ProcessorAutoConfiguration {

    @Bean
    public ProcessorMetrics processorMetrics(MeterRegistry registry) {
        return new ProcessorMetrics(registry);
    }

    @Bean
    public ProcessorMonitor processorMonitor(
            List<Processor<?>> processors,
            ProcessorMetrics metrics,
            AlertService alertService) {
        return new ProcessorMonitor(processors, metrics, alertService);
    }

    @Bean
    public EnterpriseChainBuilder enterpriseChainBuilder(
            EnterpriseProcessor enterpriseProcessor,
            EnterpriseDiffProcessor diffProcessor,
            EnterpriseDataProcessor dataProcessor) {
        return new EnterpriseChainBuilder(
            enterpriseProcessor, diffProcessor, dataProcessor);
    }
}
```

这样我们完成了企业处理器的改造，主要包括：

1.  三个核心处理器:
    *   主处理器(数据验证、标准化、富化)
    *   差异处理器(版本对比、变更记录)
    *   数据处理器(指标计算、数据存储)
2.  处理器链的构建：
    *   支持条件处理
    *   支持多种链类型
    *   完整的错误处理
3.  配置管理：
    *   处理配置
    *   监控配置
    *   自动装配

接下来需要我继续实现任务处理器(TaskHandler)的改造吗？

## Prompt:
继续

## Response:
好的，让我们实现任务处理器(TaskHandler)的改造。

### 1\. 任务处理器实现

```java
// EnterpriseTaskHandler.java - 企业任务处理器
package com.study.collect.business.enterprise.task;

import com.study.collect.core.task.handler.AbstractTaskHandler;
import com.study.collect.core.task.model.*;
import com.study.collect.business.enterprise.collector.EnterpriseCollector;
import com.study.collect.business.enterprise.collector.EnterpriseCompareCollector;
import com.study.collect.business.enterprise.processor.chain.EnterpriseChainBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EnterpriseTaskHandler extends AbstractTaskHandler {

    private final EnterpriseCollector collector;
    private final EnterpriseCompareCollector compareCollector;
    private final EnterpriseChainBuilder chainBuilder;
    private final TaskConfig taskConfig;

    @Override
    public String getType() {
        return "enterprise";
    }

    @Override
    protected TaskResult doExecute(TaskContext context) {
        String taskId = context.getTaskId();
        String instanceId = context.getInstanceId();
        
        try {
            // 1. 解析任务参数
            EnterpriseTaskParam taskParam = parseTaskParam(context.getTaskParam());
            
            // 2. 检查是否需要对比采集
            if (taskParam.isCompareEnabled()) {
                return executeCompareTask(taskParam, context);
            }
            
            // 3. 执行普通采集
            return executeCollectTask(taskParam, context);
            
        } catch (Exception e) {
            log.error("执行企业任务异常: taskId={}, instanceId={}", 
                taskId, instanceId, e);
            return TaskResult.failure(taskId, e.getMessage());
        }
    }

    /**
     * 执行对比任务
     */
    private TaskResult executeCompareTask(EnterpriseTaskParam param, 
            TaskContext context) {
            
        // 1. 构建对比上下文
        CompareContext compareContext = CompareContext.builder()
            .source("api")
            .compareTime(LocalDateTime.now())
            .compareType(param.getCompareType())
            .params(param.getCompareParams())
            .build();
            
        // 2. 执行数据对比
        DiffResult<Enterprise> diffResult = compareCollector.compare(compareContext);
        
        // 3. 处理差异数据
        if (diffResult.isChanged()) {
            // 处理新增数据
            processDiffData(diffResult.getAdded(), context, "ADD");
            
            // 处理更新数据
            processDiffData(diffResult.getUpdated(), context, "UPDATE");
            
            // 处理删除数据
            processDiffData(diffResult.getDeleted(), context, "DELETE");
        }
        
        // 4. 返回结果
        return TaskResult.success(context.getTaskId(), buildDiffStats(diffResult));
    }

    /**
     * 执行采集任务
     */
    private TaskResult executeCollectTask(EnterpriseTaskParam param, 
            TaskContext context) {
            
        // 1. 构建采集上下文
        CollectContext<List<String>> collectContext = new CollectContext<>();
        collectContext.setTaskId(context.getTaskId());
        collectContext.setParams(param.getEnterpriseCodes());
        collectContext.setShardingId(context.getShardIndex());
        collectContext.setShardingTotal(context.getShardTotal());
        
        // 2. 执行数据采集
        CollectResult<List<Enterprise>> collectResult = collector.collect(collectContext);
        
        // 3. 执行数据处理
        if (collectResult.isSuccess() && collectResult.getData() != null) {
            ProcessorChain<Enterprise> chain = chainBuilder.buildChain();
            
            // 处理每条数据
            List<Enterprise> processedData = collectResult.getData().stream()
                .map(data -> processData(data, context, chain))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
                
            // 返回处理结果
            return TaskResult.success(context.getTaskId(), buildCollectStats(processedData));
        } else {
            return TaskResult.failure(context.getTaskId(), collectResult.getErrorMessage());
        }
    }

    /**
     * 处理差异数据
     */
    private void processDiffData(List<Enterprise> dataList, TaskContext context, 
            String operationType) {
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }
        
        // 构建处理链
        ProcessorChain<Enterprise> chain = chainBuilder.buildChain();
        
        // 设置操作类型
        context.setAttribute("operationType", operationType);
        
        // 处理数据
        for (Enterprise data : dataList) {
            try {
                processData(data, context, chain);
            } catch (Exception e) {
                log.error("处理差异数据异常: {}, {}", data.getCode(), operationType, e);
            }
        }
    }

    /**
     * 处理单条数据
     */
    private Enterprise processData(Enterprise data, TaskContext context, 
            ProcessorChain<Enterprise> chain) {
        try {
            // 构建处理上下文
            ProcessContext processContext = buildProcessContext(context);
            
            // 执行处理链
            return chain.process(data, processContext);
            
        } catch (Exception e) {
            log.error("处理数据异常: {}", data.getCode(), e);
            return null;
        }
    }

    /**
     * 构建处理上下文
     */
    private ProcessContext buildProcessContext(TaskContext context) {
        ProcessContext processContext = new ProcessContext();
        processContext.setTaskId(context.getTaskId());
        processContext.setProcessType("enterprise");
        
        // 设置处理参数
        processContext.setAttribute("versionEnabled", 
            taskConfig.isVersionEnabled());
        processContext.setAttribute("processExtraData", 
            taskConfig.isProcessExtraData());
            
        return processContext;
    }

    /**
     * 构建差异统计
     */
    private Map<String, Object> buildDiffStats(DiffResult<Enterprise> diffResult) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", diffResult.getCounts().get("total"));
        stats.put("added", diffResult.getCounts().get("added"));
        stats.put("updated", diffResult.getCounts().get("updated"));
        stats.put("deleted", diffResult.getCounts().get("deleted"));
        stats.put("changed", diffResult.isChanged());
        stats.put("metrics", diffResult.getMetrics());
        return stats;
    }

    /**
     * 构建采集统计
     */
    private Map<String, Object> buildCollectStats(List<Enterprise> dataList) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", dataList.size());
        
        // 按行业统计
        Map<String, Long> industryStats = dataList.stream()
            .collect(Collectors.groupingBy(
                Enterprise::getIndustry, Collectors.counting()));
        stats.put("industryStats", industryStats);
        
        // 按地区统计
        Map<String, Long> regionStats = dataList.stream()
            .collect(Collectors.groupingBy(
                e -> getRegion(e.getAddress()), Collectors.counting()));
        stats.put("regionStats", regionStats);
        
        return stats;
    }
}
```

### 2\. 任务参数定义

```java
// EnterpriseTaskParam.java - 企业任务参数
package com.study.collect.business.enterprise.task;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class EnterpriseTaskParam {
    
    private List<String> enterpriseCodes;  // 企业编码列表
    private boolean compareEnabled;        // 是否启用对比
    private String compareType;            // 对比类型
    private Map<String, Object> compareParams; // 对比参数
    
    private boolean splitEnabled;          // 是否启用分片
    private Integer splitSize;             // 分片大小
    private String splitStrategy;          // 分片策略
    
    private boolean versionEnabled;        // 是否启用版本
    private boolean processExtraData;      // 是否处理扩展数据
    
    // 校验参数
    public void validate() {
        if (!compareEnabled && CollectionUtils.isEmpty(enterpriseCodes)) {
            throw new IllegalArgumentException("企业编码列表不能为空");
        }
        
        if (compareEnabled && compareParams == null) {
            throw new IllegalArgumentException("对比参数不能为空");
        }
        
        if (splitEnabled && (splitSize == null || splitSize <= 0)) {
            throw new IllegalArgumentException("分片大小必须大于0");
        }
    }
}
```

### 3\. 任务配置定义

```java
// TaskConfig.java - 任务配置
package com.study.collect.business.enterprise.task.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "enterprise.task")
public class TaskConfig {
    
    // 基础配置
    private boolean enabled = true;
    private String taskHandler = "enterprise";
    private int maxRetries = 3;
    private int timeout = 7200;  // 2小时
    
    // 版本配置
    private boolean versionEnabled = true;
    private boolean processExtraData = true;
    
    // 分片配置
    private boolean splitEnabled = true;
    private int splitSize = 1000;
    private String splitStrategy = "HASH";
    
    // 对比配置
    private boolean compareEnabled = true;
    private int compareInterval = 3600;  // 1小时
    
    // 监控配置
    private boolean monitorEnabled = true;
    private boolean alertEnabled = true;
}

// TaskAutoConfiguration.java - 任务自动配置
package com.study.collect.business.enterprise.task.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(TaskConfig.class)
public class TaskAutoConfiguration {
    // 可以添加其他配置Bean
}
```

这样我们完成了企业任务处理器的改造，主要包括：

1.  任务处理器实现：
    *   支持对比采集
    *   支持分片执行
    *   完整的处理链
2.  任务参数定义：
    *   灵活的参数配置
    *   参数校验
    *   支持多种模式
3.  任务配置管理：
    *   基础配置
    *   功能开关
    *   监控配置

这个实现：

1.  支持普通采集和对比采集两种模式
2.  可以灵活控制数据处理流程
3.  提供完整的任务执行统计
4.  实现了异常处理和监控

需要我补充任何部分的实现吗？

