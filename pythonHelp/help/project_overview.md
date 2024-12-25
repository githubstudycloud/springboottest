# Project Structure

```
platform-collect/
    pom.xml
    collect-business/
        pom.xml
        business-enterprise/
            pom.xml
            src/
                main/
                    java/
                        com/
                            study/
                                collect/
                                    business/
                                        enterprise/
                                            collector/
                                                EnterpriseCollector.java
                                            config/
                                                EnterpriseAutoConfiguration.java
                                                EnterpriseCollectorProperties.java
                                                EnterpriseConfig.java
                                            controller/
                                                EnterpriseController.java
                                            model/
                                                Enterprise.java
                                                request/
                                                    EnterpriseGenerateRequest.java
                                                    EnterpriseQueryRequest.java
                                                response/
                                                    EnterpriseQueryResponse.java
                                            processor/
                                                EnterpriseProcessor.java
                                            repository/
                                                EnterpriseRepository.java
                                            service/
                                                EnterpriseService.java
                    resources/
                        META-INF/
                            spring.factories
        business-finance/
            pom.xml
            src/
                main/
                    java/
                        com/
                            study/
                                collect/
                                    business/
                                        finance/
                                            collector/
                                                FinanceCollector.java
                                            config/
                                                FinanceAutoConfiguration.java
                                            controller/
                                                FinanceController.java
                                            model/
                                                FinanceData.java
                                            processor/
                                                FinanceProcessor.java
                                            repository/
                                                FinanceRepository.java
                                            service/
                                                FinanceService.java
                    resources/
                        META-INF/
                            spring.factories
        business-medical/
            pom.xml
            src/
                main/
                    java/
                        com/
                            study/
                                collect/
                                    business/
                                        medical/
                                            collector/
                                                MedicalCollector.java
                                            config/
                                                MedicalAutoConfiguration.java
                                            controller/
                                                MedicalController.java
                                            engine/
                                                MedicalEngine.java
                                            model/
                                                MedicalData.java
                                            processor/
                                                DicomProcessor.java
                                                ImageProcessor.java
                                                PrivacyProcessor.java
                                            repository/
                                                medicalRepository.java
                                            service/
                                                MedicalService.java
                    resources/
                        META-INF/
                            spring.factories
    collect-common/
        pom.xml
        src/
            main/
                java/
                    com/
                        study/
                            App.java
                            collect/
                                common/
                                    exception/
                                        BaseException.java
                                    model/
                                        Response.java
                                    util/
                                        DateUtils.java
                                        JsonUtils.java
    collect-core/
        pom.xml
        src/
            main/
                java/
                    com/
                        study/
                            collect/
                                core/
                                    collector/
                                        AbstractCollector.java
                                        ICollector.java
                                        package-info.java
                                        annotation/
                                            Collector.java
                                        config/
                                            CollectorConfiguration.java
                                            CollectorProperties.java
                                        exception/
                                            CollectException.java
                                        factory/
                                            CollectorFactory.java
                                        manager/
                                            CollectorManager.java
                                        model/
                                            CollectContext.java
                                            CollectResult.java
                                    common/
                                    config/
                                        CollectAutoConfiguration.java
                                        package-info.java
                                    mq/
                                        config/
                                            MQProperties.java
                                            RabbitConfig.java
                                        consumer/
                                            TaskConsumer.java
                                        message/
                                            BaseMessage.java
                                            TaskMessage.java
                                            TaskResultMessage.java
                                        producer/
                                            TaskProducer.java
                                    processor/
                                        AbstractProcessor.java
                                        IProcessor.java
                                        package-info.java
                                        annotation/
                                            Processor.java
                                        config/
                                            ProcessorConfiguration.java
                                            ProcessorProperties.java
                                        exception/
                                            ProcessException.java
                                        manager/
                                            ProcessorManager.java
                                        model/
                                            ProcessChain.java
                                            ProcessContext.java
                                    storage/
                                        annotation/
                                            Repository.java
                                        audit/
                                            EntityAuditor.java
                                        cache/
                                            package-info.java
                                            annotation/
                                                Cache.java
                                                CacheEvict.java
                                                CacheLock.java
                                            aspect/
                                                CacheAspect.java
                                                LockAspect.java
                                            config/
                                                CacheAutoConfiguration.java
                                                CacheProperties.java
                                            lock/
                                                DistributedLock.java
                                                package-info.java
                                                RedisLock.java
                                            manager/
                                                CacheManager.java
                                                RedisCacheManager.java
                                            model/
                                                CacheOptions.java
                                        config/
                                            MongoConfig.java
                                        entity/
                                            BaseEntity.java
                                            VersionEntity.java
                                        event/
                                            DefaultEntityEventHandler.java
                                            EntityEvent.java
                                            EntityEventListener.java
                                            impl/
                                                EntityEvents.java
                                        repository/
                                            BaseMongoRepository.java
                                            IRepository.java
                                            VersionRepository.java
                                            factory/
                                                CustomMongoRepositoryFactory.java
                                                CustomMongoRepositoryFactoryBean.java
                                    task/
                                        package-info.java
                                        config/
                                            MyBatisConfig.java
                                            TaskConfiguration.java
                                        definition/
                                            TaskProperties.java
                                        entity/
                                            TaskConfig.java
                                            TaskInstance.java
                                            TaskLog.java
                                        enums/
                                            LogTypeEnum.java
                                            TaskStatusEnum.java
                                        exception/
                                            TaskValidationException.java
                                        handler/
                                            AbstractTaskHandler.java
                                            SampleTaskHandler.java
                                            TaskHandler.java
                                            TaskHandlerManager.java
                                        mapper/
                                            TaskConfigMapper.java
                                            TaskInstanceMapper.java
                                            TaskLogMapper.java
                                        model/
                                            package-info.java
                                            ShardingConfig.java
                                            TaskContext.java
                                            TaskDefinition.java
                                            TaskResult.java
                                            TaskStatus.java
                                        scheduler/
                                            AbstractTaskScheduler.java
                                            DefaultTaskScheduler.java
                                            package-info.java
                                            TaskDispatcher.java
                                            TaskScheduler.java
                                        service/
                                            TaskConfigService.java
                                            TaskExecuteService.java
                                        utils/
                                            InstanceIdGenerator.java
                                    util/
                resources/
                    mapper/
                        TaskConfigMapper.xml
                        TaskInstanceMapper.xml
                        TaskLogMapper.xml
    collect-starter/
        pom.xml
        src/
            main/
                java/
                    com/
                        study/
                            collect/
                                CollectApplication.java
                resources/
                    application-dev.yml
                    application-prod.yml
                    application.yml
                    sql.sql
                    任务.md
```

# File Contents

## pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xmlns="http://maven.apache.org/POM/4.0.0"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.9</version>
        <relativePath/> <!-- 添加这一行 -->
    </parent>

    <groupId>com.study</groupId>
    <artifactId>platform-collect</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>pom</packaging>

    <modules>
        <module>collect-core</module>
        <module>collect-common</module>
        <module>collect-business</module>
        <module>collect-starter</module>
    </modules>

    <properties>
        <java.version>21</java.version>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>

        <!-- 依赖版本管理 -->
        <spring-boot.version>3.2.9</spring-boot.version>
        <mysql.version>8.3.0</mysql.version>
        <mongodb-driver.version>4.11.1</mongodb-driver.version>
        <lettuce.version>6.3.2.RELEASE</lettuce.version>
        <redisson.version>3.39.0</redisson.version>
        <rabbitmq.version>5.20.0</rabbitmq.version>
        <mybatis.version>3.0.3</mybatis.version>
        <mariadb.version>3.3.3</mariadb.version>  <!-- 这是最新的稳定版本 -->
        <jackson.version>2.17.0</jackson.version>
        <prometheus.version>1.12.9</prometheus.version>
        <lombok.version>1.18.30</lombok.version>
        <mapstruct.version>1.5.5.Final</mapstruct.version>
        <commons-lang3.version>3.14.0</commons-lang3.version>
        <commons-io.version>2.15.1</commons-io.version>
        <guava.version>33.1.0-jre</guava.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <!-- 内部模块依赖 -->
            <dependency>
                <groupId>com.study</groupId>
                <artifactId>collect-core</artifactId>
                <version>${project.version}</version>
            </dependency>
            <dependency>
                <groupId>com.study</groupId>
                <artifactId>collect-common</artifactId>
                <version>${project.version}</version>
            </dependency>

            <!-- MySQL -->
            <dependency>
                <groupId>com.mysql</groupId>
                <artifactId>mysql-connector-j</artifactId>
                <version>${mysql.version}</version>
            </dependency>
            <dependency>
                <groupId>org.mybatis.spring.boot</groupId>
                <artifactId>mybatis-spring-boot-starter</artifactId>
                <version>${mybatis.version}</version>
            </dependency>

            <!-- 添加 MariaDB JDBC 驱动依赖 -->
            <dependency>
                <groupId>org.mariadb.jdbc</groupId>
                <artifactId>mariadb-java-client</artifactId>
                <version>${mariadb.version}</version>
            </dependency>

            <!-- MongoDB -->
            <dependency>
                <groupId>org.mongodb</groupId>
                <artifactId>mongodb-driver-sync</artifactId>
                <version>${mongodb-driver.version}</version>
            </dependency>

            <!-- Redis -->
            <dependency>
                <groupId>io.lettuce</groupId>
                <artifactId>lettuce-core</artifactId>
                <version>${lettuce.version}</version>
            </dependency>
            <dependency>
                <groupId>org.redisson</groupId>
                <artifactId>redisson</artifactId>
                <version>${redisson.version}</version>
            </dependency>

            <!-- RabbitMQ -->
            <dependency>
                <groupId>com.rabbitmq</groupId>
                <artifactId>amqp-client</artifactId>
                <version>${rabbitmq.version}</version>
            </dependency>

            <!-- Prometheus -->
            <dependency>
                <groupId>io.micrometer</groupId>
                <artifactId>micrometer-registry-prometheus</artifactId>
                <version>${prometheus.version}</version>
            </dependency>

            <!-- Common -->
            <dependency>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
                <version>${lombok.version}</version>
            </dependency>
            <dependency>
                <groupId>org.mapstruct</groupId>
                <artifactId>mapstruct</artifactId>
                <version>${mapstruct.version}</version>
            </dependency>
            <dependency>
                <groupId>org.apache.commons</groupId>
                <artifactId>commons-lang3</artifactId>
                <version>${commons-lang3.version}</version>
            </dependency>
            <dependency>
                <groupId>commons-io</groupId>
                <artifactId>commons-io</artifactId>
                <version>${commons-io.version}</version>
            </dependency>
            <dependency>
                <groupId>com.google.guava</groupId>
                <artifactId>guava</artifactId>
                <version>${guava.version}</version>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <build>
        <pluginManagement>
            <plugins>
                <plugin>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-maven-plugin</artifactId>
                    <configuration>
                        <excludes>
                            <exclude>
                                <groupId>org.projectlombok</groupId>
                                <artifactId>lombok</artifactId>
                            </exclude>
                        </excludes>
                    </configuration>
                </plugin>
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-compiler-plugin</artifactId>
                    <configuration>
                        <source>${java.version}</source>
                        <target>${java.version}</target>
                        <annotationProcessorPaths>
                            <path>
                                <groupId>org.projectlombok</groupId>
                                <artifactId>lombok</artifactId>
                                <version>${lombok.version}</version>
                            </path>
                            <path>
                                <groupId>org.mapstruct</groupId>
                                <artifactId>mapstruct-processor</artifactId>
                                <version>${mapstruct.version}</version>
                            </path>
                        </annotationProcessorPaths>
                    </configuration>
                </plugin>
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-deploy-plugin</artifactId>
                    <version>3.1.1</version>
                </plugin>

            </plugins>
        </pluginManagement>
    </build>

    <profiles>
        <profile>
            <id>dev</id>
            <properties>
                <profile.active>dev</profile.active>
            </properties>
            <activation>
                <activeByDefault>true</activeByDefault>
            </activation>
        </profile>
        <profile>
            <id>test</id>
            <properties>
                <profile.active>test</profile.active>
            </properties>
        </profile>
        <profile>
            <id>prod</id>
            <properties>
                <profile.active>prod</profile.active>
            </properties>
        </profile>
    </profiles>
</project>
```

## pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xmlns="http://maven.apache.org/POM/4.0.0"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.study</groupId>
        <artifactId>platform-collect</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <artifactId>collect-business</artifactId>
    <packaging>pom</packaging>

    <modules>
        <module>business-enterprise</module>
<!--        <module>business-finance</module>-->
<!--        <module>business-medical</module>-->
    </modules>

    <dependencies>
        <!-- 核心依赖 -->
        <dependency>
            <groupId>com.study</groupId>
            <artifactId>collect-core</artifactId>
        </dependency>
        <dependency>
            <groupId>com.study</groupId>
            <artifactId>collect-common</artifactId>
        </dependency>
    </dependencies>
</project>
```

## pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xmlns="http://maven.apache.org/POM/4.0.0"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.study</groupId>
        <artifactId>collect-business</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <artifactId>business-enterprise</artifactId>

    <dependencies>
        <!-- Spring Boot Web -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- Database -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-mongodb</artifactId>
        </dependency>
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
        </dependency>

        <!-- Cache -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-cache</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>

        <!-- Test -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <scope>provided</scope>
        </dependency>
    </dependencies>
</project>
```

## EnterpriseCollector.java

```java
package com.study.collect.business.enterprise.collector;

import com.study.collect.business.enterprise.model.Enterprise;
import com.study.collect.business.enterprise.repository.EnterpriseRepository;
import com.study.collect.common.util.JsonUtils;
import com.study.collect.core.collector.AbstractCollector;
import com.study.collect.core.collector.annotation.Collector;
import com.study.collect.core.collector.model.CollectContext;
import com.study.collect.core.collector.model.CollectResult;
import com.study.collect.core.storage.cache.annotation.Cache;
import com.study.collect.core.storage.cache.annotation.CacheLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@Collector(type = "enterprise")
@RequiredArgsConstructor
public class EnterpriseCollector extends AbstractCollector<String, List<Enterprise>> {

    private final EnterpriseRepository repository;
    private static final int BATCH_SIZE = 100;
    private final AtomicInteger counter = new AtomicInteger(0);

    @Override
    protected void preProcess(CollectContext<String> context) {
        super.preProcess(context);
        // 解析分片参数
        Map<String, Object> params = parseShardingParams(context.getParams());
        context.setAttribute("shardParams", params);

        // 记录开始时间
        context.setAttribute("startTime", LocalDateTime.now());
        counter.set(0);
    }

    @Override
    @Cache(key = "enterprise:collect:#{#context.taskId}")
    @CacheLock(key = "lock:enterprise:collect:#{#context.taskId}")
    protected List<Enterprise> doCollect(CollectContext<String> context) {
        Map<String, Object> params = context.getAttribute("shardParams");
        List<Enterprise> result = new ArrayList<>();

        if (params.containsKey("code")) {
            // 单个企业采集
            String code = (String) params.get("code");
            Enterprise enterprise = collectSingle(code);
            if (enterprise != null) {
                result.add(enterprise);
            }
        } else {
            // 分片批量采集
            int shardTotal = (int) params.get("shardTotal");
            int shardIndex = context.getShardingId();
            result = collectBatch(shardIndex, shardTotal);
        }

        return result;
    }

    @Override
    protected void postProcess(CollectResult<List<Enterprise>> result) {
        super.postProcess(result);
        if (result.getData() != null) {
            // 更新采集进度
            int total = counter.addAndGet(result.getData().size());
            log.info("采集进度: {}/{}", total, result.getData().size());
        }
    }

    /**
     * 采集单个企业数据
     */
    private Enterprise collectSingle(String code) {
        try {
            // 模拟调用外部接口
            Thread.sleep(100);

            Enterprise enterprise = repository.findByCode(code);
            if (enterprise != null) {
//                enterprise.setUpdateTime(LocalDateTime.now());
//                enterprise.setVersion("V" + System.currentTimeMillis());
                return repository.save(enterprise);
            }
            return null;
        } catch (Exception e) {
            log.error("采集企业数据失败: {}", code, e);
            return null;
        }
    }

    /**
     * 批量采集企业数据
     */
    private List<Enterprise> collectBatch(int shardIndex, int shardTotal) {
        List<Enterprise> results = new ArrayList<>();
        int pageNum = 0;

        while (true) {
            // 分页查询数据
            Page<Enterprise> page = repository.findBySharding(
                    shardIndex,
                    shardTotal,
                    PageRequest.of(pageNum, BATCH_SIZE)
            );

            if (!page.hasContent()) {
                break;
            }

            // 处理每页数据
            for (Enterprise enterprise : page.getContent()) {
                try {
                    // 模拟调用外部接口
                    Thread.sleep(50);

//                    enterprise.setUpdateTime(LocalDateTime.now());
//                    enterprise.setVersion("V" + System.currentTimeMillis());
                    results.add(repository.save(enterprise));
                } catch (Exception e) {
                    log.error("采集企业数据失败: {}", enterprise.getCode(), e);
                }
            }

            pageNum++;

            // 记录进度
            counter.addAndGet(page.getContent().size());

            if (!page.hasNext()) {
                break;
            }
        }

        return results;
    }

    /**
     * 解析分片参数
     */
    private Map<String, Object> parseShardingParams(String params) {
        try {
            return JsonUtils.fromJson(params, Map.class);
        } catch (Exception e) {
            log.error("解析分片参数失败: {}", params, e);
            return Map.of();
        }
    }

    @Override
    public String getType() {
        return "";
    }
}
```

## EnterpriseAutoConfiguration.java

```java
package com.study.collect.business.enterprise.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.study.collect.business.enterprise")
public class EnterpriseAutoConfiguration {
}
```

## EnterpriseCollectorProperties.java

```java
package com.study.collect.business.enterprise.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "collect.enterprise")
public class EnterpriseCollectorProperties {
    private int batchSize = 100;  // 批量处理大小
    private int threadCount = 4;  // 处理线程数
    private int retryTimes = 3;   // 重试次数
    private int timeout = 3600;   // 超时时间(秒)
}
```

## EnterpriseConfig.java

```java
package com.study.collect.business.enterprise.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class EnterpriseConfig {
    @Bean
    @ConditionalOnMissingBean
    public EnterpriseCollectorProperties enterpriseCollectorProperties() {
        return new EnterpriseCollectorProperties();
    }
}
```

## EnterpriseController.java

```java
package com.study.collect.business.enterprise.controller;

import com.study.collect.business.enterprise.model.Enterprise;
import com.study.collect.business.enterprise.model.request.EnterpriseGenerateRequest;
import com.study.collect.business.enterprise.model.request.EnterpriseQueryRequest;
import com.study.collect.business.enterprise.model.response.EnterpriseQueryResponse;
import com.study.collect.business.enterprise.service.EnterpriseService;
import com.study.collect.common.model.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enterprise")
@RequiredArgsConstructor
public class EnterpriseController {

    private final EnterpriseService enterpriseService;

    /**
     * 生成测试数据
     */
    @PostMapping("/generate")
    public Response<List<String>> generateData(@Valid @RequestBody EnterpriseGenerateRequest request) {
        List<String> codes = enterpriseService.generateEnterprises(
                request.getStartCode(),
                request.getCount(),
                request.getIndustry(),
                request.getRegAuthority()
        );
        return Response.success(codes);
    }

    /**
     * 触发数据采集
     */
    @PostMapping("/collect")
    public Response<String> collect(@RequestParam(required = false) String code) {
        String taskId = enterpriseService.startCollect(code);
        return Response.success(taskId);
    }

    /**
     * 分页查询数据
     */
    @GetMapping("/page")
    public Response<EnterpriseQueryResponse> queryPage(@Valid EnterpriseQueryRequest request) {
        EnterpriseQueryResponse response = enterpriseService.queryPage(request);
        return Response.success(response);
    }

    /**
     * 查询采集进度
     */
    @GetMapping("/progress/{taskId}")
    public Response<Object> queryProgress(@PathVariable String taskId) {
        Object progress = enterpriseService.queryProgress(taskId);
        return Response.success(progress);
    }

    /**
     * 获取某个版本之后的增量数据
     */
    @GetMapping("/increment")
    public Response<List<Enterprise>> getIncrementalData(
            @RequestParam(required = false) String version) {
        List<Enterprise> data = enterpriseService.getIncrementalData(version);
        return Response.success(data);
    }

    /**
     * 根据编码获取数据
     */
    @GetMapping("/{code}")
    public Response<Enterprise> getByCode(@PathVariable String code) {
        Enterprise enterprise = enterpriseService.getByCode(code);
        return Response.success(enterprise);
    }
}
```

## Enterprise.java

```java
package com.study.collect.business.enterprise.model;

import com.study.collect.core.storage.entity.BaseEntity;
import com.study.collect.core.storage.entity.VersionEntity;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Document(collection = "enterprise")
public class Enterprise extends VersionEntity {
    @Id
    private String id;

    private String code;           // 企业编码
    private String name;          // 企业名称
    private String address;       // 企业地址
    private String contact;       // 联系人
    private String phone;         // 联系电话
    private String industry;      // 所属行业
    private BigDecimal regCapital; // 注册资本
    private String regAuthority;   // 注册机构
    private LocalDate estDate;     // 成立日期

//    private LocalDateTime createTime;  // 创建时间
//    private LocalDateTime updateTime;  // 更新时间
//    private String version;       // 数据版本
//    private Boolean deleted;      // 是否删除
}
```

## EnterpriseGenerateRequest.java

```java
package com.study.collect.business.enterprise.model.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EnterpriseGenerateRequest {
    @NotNull(message = "起始编码不能为空")
    private Integer startCode;      // 起始编码

    @NotNull(message = "生成数量不能为空")
    @Max(value = 1000, message = "单次生成数量不能超过1000")
    private Integer count;          // 生成数量

    private String industry;        // 指定行业(可选)
    private String regAuthority;    // 指定注册机构(可选)
}
```

## EnterpriseQueryRequest.java

```java
package com.study.collect.business.enterprise.model.request;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class EnterpriseQueryRequest {
    private String code;           // 企业编码
    private String name;           // 企业名称
    private String industry;       // 行业
    private String regAuthority;   // 注册机构

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate estDateStart;  // 成立日期开始

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate estDateEnd;    // 成立日期结束

    private String version;         // 数据版本

    private Integer pageNum = 1;    // 页码
    private Integer pageSize = 10;  // 每页大小
}
```

## EnterpriseQueryResponse.java

```java
package com.study.collect.business.enterprise.model.response;

import com.study.collect.business.enterprise.model.Enterprise;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

@Data
public class EnterpriseQueryResponse {
    private List<Enterprise> list;      // 数据列表
    private long total;                 // 总数量
    private int pages;                  // 总页数
    private int pageNum;                // 当前页
    private int pageSize;               // 每页大小

    private Map<String, Object> summary;  // 汇总信息

    // 构造方法
    public static EnterpriseQueryResponse of(Page<Enterprise> page) {
        EnterpriseQueryResponse response = new EnterpriseQueryResponse();
        response.setList(page.getContent());
        response.setTotal(page.getTotalElements());
        response.setPages(page.getTotalPages());
        response.setPageNum(page.getNumber() + 1);
        response.setPageSize(page.getSize());
        return response;
    }
}
```

## EnterpriseProcessor.java

```java
package com.study.collect.business.enterprise.processor;

import com.study.collect.business.enterprise.model.Enterprise;
import com.study.collect.core.processor.AbstractProcessor;
import com.study.collect.core.processor.annotation.Processor;
import com.study.collect.core.processor.model.ProcessContext;
import org.springframework.stereotype.Component;

@Processor(type = "enterprise", order = 100)
@Component
public class EnterpriseProcessor extends AbstractProcessor<Enterprise> {

    @Override
    protected Enterprise doProcess(Enterprise data, ProcessContext context) {
        // 简单的数据处理
        if (data != null) {
            // 处理电话格式
            if (data.getPhone() != null) {
                data.setPhone(formatPhone(data.getPhone()));
            }
            // 处理地址格式
            if (data.getAddress() != null) {
                data.setAddress(formatAddress(data.getAddress()));
            }
        }
        return data;
    }

    @Override
    public String getType() {
        return "enterprise";
    }

    @Override
    public int getOrder() {
        return 0;
    }

    private String formatPhone(String phone) {
        // 电话号码格式化逻辑
        return phone.replaceAll("(\\d{3})(\\d{4})(\\d{4})", "$1-$2-$3");
    }

    private String formatAddress(String address) {
        // 地址格式化逻辑
        return address.trim().replaceAll("\\s+", " ");
    }


}

```

## EnterpriseRepository.java

```java
package com.study.collect.business.enterprise.repository;

import com.study.collect.business.enterprise.model.Enterprise;
import com.study.collect.core.storage.repository.IRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface EnterpriseRepository extends IRepository<Enterprise>, MongoRepository<Enterprise, String> {

    /**
     * 根据编码查询
     */
    Enterprise findByCode(String code);

    /**
     * 根据名称模糊查询
     */
    List<Enterprise> findByNameLike(String name);

    /**
     * 根据行业查询
     */
    List<Enterprise> findByIndustry(String industry);

    /**
     * 根据注册机构查询
     */
    List<Enterprise> findByRegAuthority(String regAuthority);

    /**
     * 根据成立日期范围查询
     */
    List<Enterprise> findByEstDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * 根据版本号获取增量数据
     */
    @Query("{'version': {$gt: ?0}}")
    List<Enterprise> findByVersionCodeGreaterThan(String version);

    /**
     * 分片查询
     * ABS(HASH(code) % total) = index
     */
    @Query(value = "{'$where': 'Math.abs(this.code.hashCode() % ?1) == ?0'}")
    Page<Enterprise> findBySharding(int shardIndex, int shardTotal, Pageable pageable);

    /**
     * 多条件组合查询
     */
    @Query("{ $and: [ " +
            "?#{ [0] == null ? { $where : '1'} : { 'code': [0] } }, " +
            "?#{ [1] == null ? { $where : '1'} : { 'name': {$regex: [1]} } }, " +
            "?#{ [2] == null ? { $where : '1'} : { 'industry': [2] } }, " +
            "?#{ [3] == null ? { $where : '1'} : { 'regAuthority': [3] } }, " +
            "?#{ [4] == null ? { $where : '1'} : { 'estDate': { $gte: [4] } } }, " +
            "?#{ [5] == null ? { $where : '1'} : { 'estDate': { $lte: [5] } } } " +
            "] }")
    Page<Enterprise> findByConditions(String code,
                                      String name,
                                      String industry,
                                      String regAuthority,
                                      LocalDate estDateStart,
                                      LocalDate estDateEnd,
                                      Pageable pageable);

    /**
     * 按行业统计企业数量
     */
    @Query(value = "{'industry': ?0}", count = true)
    long countByIndustry(String industry);

    /**
     * 按注册机构统计企业数量
     */
    @Query(value = "{'regAuthority': ?0}", count = true)
    long countByRegAuthority(String regAuthority);

    /**
     * 软删除
     */
    @Override
    default void softDelete(String id) {
        // 实现父接口的软删除方法
        update(id, "deleted", true);
    }

    /**
     * 更新指定字段
     */
    @Query(value = "{'_id': ?0}", fields = "{ ?1: ?2 }")
    void update(String id, String field, Object value);
}
```

## EnterpriseService.java

```java
package com.study.collect.business.enterprise.service;

import com.study.collect.business.enterprise.collector.EnterpriseCollector;
import com.study.collect.business.enterprise.model.Enterprise;
import com.study.collect.business.enterprise.model.request.EnterpriseQueryRequest;
import com.study.collect.business.enterprise.model.response.EnterpriseQueryResponse;
import com.study.collect.business.enterprise.repository.EnterpriseRepository;
import com.study.collect.core.mq.message.TaskMessage;
import com.study.collect.core.mq.producer.TaskProducer;
import com.study.collect.core.task.entity.TaskInstance;
import com.study.collect.core.task.model.TaskContext;
import com.study.collect.core.task.service.TaskExecuteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnterpriseService {

    private final EnterpriseRepository repository;
    private final EnterpriseCollector collector;
    private final TaskProducer taskProducer;
    private final TaskExecuteService taskExecuteService;
    private final MongoTemplate mongoTemplate;

    /**
     * 生成测试数据
     */
    public List<String> generateEnterprises(Integer startCode, Integer count,
                                            String industry, String regAuthority) {
        List<String> codes = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String code = String.format("%06d", startCode + i);
            Enterprise enterprise = generateOne(code, industry, regAuthority);
            repository.save(enterprise);
            codes.add(code);
        }
        return codes;
    }

    /**
     * 启动采集任务
     */
    public String startCollect(String code) {
        // 创建任务实例
        TaskInstance instance;
        if (StringUtils.hasText(code)) {
            // 单个企业采集
            instance = taskExecuteService.createTaskInstance(
                    "enterprise",
                    0,
                    "{\"code\":\"" + code + "\"}"
            );
        } else {
            // 全量采集,获取总数计算分片
            long total = repository.count();
            int shardTotal = calculateShardTotal(total);
            instance = taskExecuteService.createTaskInstance(
                    "enterprise",
                    0,
                    "{\"shardTotal\":" + shardTotal + "}"
            );
        }

        // 发送任务消息
        TaskMessage message = new TaskMessage();
        message.setTaskId("enterprise");
        message.setInstanceId(instance.getInstanceId());
        message.setShardIndex(instance.getShardIndex());
        message.setShardTotal(instance.getShardTotal());
        message.setShardParam(instance.getShardParam());
        taskProducer.sendTask(message);

        return instance.getInstanceId();
    }

    /**
     * 分页查询
     */
    public EnterpriseQueryResponse queryPage(EnterpriseQueryRequest request) {
        // 构建查询条件
        Query query = new Query();
        Criteria criteria = new Criteria();

        if (StringUtils.hasText(request.getCode())) {
            criteria.and("code").is(request.getCode());
        }
        if (StringUtils.hasText(request.getName())) {
            criteria.and("name").regex(request.getName());
        }
        if (StringUtils.hasText(request.getIndustry())) {
            criteria.and("industry").is(request.getIndustry());
        }
        if (StringUtils.hasText(request.getRegAuthority())) {
            criteria.and("regAuthority").is(request.getRegAuthority());
        }
        if (request.getEstDateStart() != null) {
            criteria.and("estDate").gte(request.getEstDateStart());
        }
        if (request.getEstDateEnd() != null) {
            criteria.and("estDate").lte(request.getEstDateEnd());
        }

        query.addCriteria(criteria);

        // 执行分页查询
        long total = mongoTemplate.count(query, Enterprise.class);
        PageRequest pageRequest = PageRequest.of(request.getPageNum() - 1,
                request.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createTime"));
        query.with(pageRequest);
        List<Enterprise> list = mongoTemplate.find(query, Enterprise.class);

        // 构建分页结果
        Page<Enterprise> page = new PageImpl<>(list, pageRequest, total);
        return EnterpriseQueryResponse.of(page);
    }

    /**
     * 查询任务进度
     */
    public Object queryProgress(String taskId) {
        return taskExecuteService.getTaskLogs(taskId);
    }

    /**
     * 获取增量数据
     */
    public List<Enterprise> getIncrementalData(String version) {
        if (!StringUtils.hasText(version)) {
            return Collections.emptyList();
        }
        return repository.findByVersionCodeGreaterThan(version);
    }

    /**
     * 根据编码获取数据
     */
    public Enterprise getByCode(String code) {
        return repository.findByCode(code);
    }

    /**
     * 生成单个企业测试数据
     */
    private Enterprise generateOne(String code, String industry, String regAuthority) {
        Enterprise enterprise = new Enterprise();
        enterprise.setCode(code);
        enterprise.setName("企业" + code);
        enterprise.setAddress("测试地址" + code);
        enterprise.setContact("联系人" + code);
        enterprise.setPhone("1234567" + code.substring(code.length() - 4));
        enterprise.setIndustry(industry != null ? industry : randomIndustry());
        enterprise.setRegCapital(new BigDecimal(random.nextInt(1000000)));
        enterprise.setRegAuthority(regAuthority != null ? regAuthority : randomAuthority());
        enterprise.setEstDate(LocalDate.now().minusDays(random.nextInt(3650)));
        enterprise.setCreateTime(LocalDateTime.now());
        enterprise.setUpdateTime(LocalDateTime.now());
//        enterprise.setVersion("V" + System.currentTimeMillis());
        enterprise.setDeleted(false);
        return enterprise;
    }

    private final Random random = new Random();
    private final String[] INDUSTRIES = {"制造业", "服务业", "零售业", "建筑业", "科技业"};
    private final String[] AUTHORITIES = {"北京", "上海", "广州", "深圳", "杭州"};

    private String randomIndustry() {
        return INDUSTRIES[random.nextInt(INDUSTRIES.length)];
    }

    private String randomAuthority() {
        return AUTHORITIES[random.nextInt(AUTHORITIES.length)];
    }

    /**
     * 计算分片数量
     */
    private int calculateShardTotal(long total) {
        if (total <= 1000) return 1;
        if (total <= 5000) return 2;
        if (total <= 10000) return 4;
        if (total <= 50000) return 8;
        return 16;
    }
}
```

## spring.factories

```
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
com.study.collect.business.enterprise.config.EnterpriseAutoConfiguration
```

## pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xmlns="http://maven.apache.org/POM/4.0.0"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.study</groupId>
        <artifactId>collect-business</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <artifactId>business-finance</artifactId>

    <dependencies>
        <!-- Spring Boot Web -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- Financial Libraries -->
        <dependency>
            <groupId>org.ta4j</groupId>
            <artifactId>ta4j-core</artifactId>
            <version>0.15</version>
        </dependency>

        <!-- Storage -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-mongodb</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>

        <!-- Message Queue -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-amqp</artifactId>
        </dependency>

        <!-- Test -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <scope>provided</scope>
        </dependency>
    </dependencies>
</project>
```

## FinanceCollector.java

```java
package com.study.collect.business.finance.collector;

import com.study.collect.business.finance.model.FinanceData;
import com.study.collect.core.collector.AbstractCollector;
import com.study.collect.core.collector.annotation.Collector;
import com.study.collect.core.collector.exception.CollectException;
import com.study.collect.core.collector.model.CollectContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Collector(type = "finance")
@Component
@RequiredArgsConstructor
public class FinanceCollector extends AbstractCollector<String, FinanceData> {

    private static final String CACHE_PREFIX = "finance:stock:";
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    protected void preProcess(CollectContext<String> context) {
        // 检查缓存是否存在
        String key = CACHE_PREFIX + context.getParams();
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            throw new CollectException("Data already collected: " + context.getParams());
        }
    }

    @Override
    protected FinanceData doCollect(CollectContext<String> context) {
        //        // 模拟从外部API获取数据
        FinanceData data = collectFromExternalApi(context.getParams());

        // 缓存数据
        String key = CACHE_PREFIX + context.getParams();
        redisTemplate.opsForValue().set(key, data);

        return data;
    }

    @Override
    protected void postProcess(FinanceData data) {
        // 计算衍生指标
        calculateIndicators(data);
    }

    private FinanceData collectFromExternalApi(String stockCode) {
        // 模拟外部API调用
        FinanceData data = new FinanceData();
        data.setStockCode(stockCode);
        data.setTradeTime(LocalDateTime.now());
        return data;
    }

    private void calculateIndicators(FinanceData data) {
        // 计算交易金额
        if (data.getPrice() != null && data.getVolume() != null) {
            data.setAmount(data.getPrice().multiply(data.getVolume()));
        }
    }

    @Override
    public FinanceData collect(String param) {
        return null;
    }

    @Override
    public String getType() {
        return "finance";
    }
}
```

## FinanceAutoConfiguration.java

```java
package com.study.collect.business.finance.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.study.collect.business.finance")
public class FinanceAutoConfiguration {
}

```

## FinanceController.java

```java
package com.study.collect.business.finance.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/finance")
@RequiredArgsConstructor
public class FinanceController {

//    private final EnterpriseService enterpriseService;
//
//    @GetMapping("/collect/{code}")
//    public Response<Enterprise> collect(@PathVariable String code) {
//        Enterprise enterprise = enterpriseService.collectAndProcess(code);
//        return Response.success(enterprise);
//    }
}
```

## FinanceData.java

```java
package com.study.collect.business.finance.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Document(collection = "finance_data")
public class FinanceData {
    @Id
    private String id;
    private String code;
    private String stockCode;
    private String stockName;
    private BigDecimal price;
    private BigDecimal volume;
    private BigDecimal amount;
    private String status;
    private Boolean deleted;
    private LocalDateTime tradeTime;
    private LocalDateTime createTime;
}

```

## FinanceProcessor.java

```java
package com.study.collect.business.finance.processor;

import com.study.collect.business.finance.model.FinanceData;
import com.study.collect.core.processor.AbstractProcessor;
import com.study.collect.core.processor.annotation.Processor;
import com.study.collect.core.processor.exception.ProcessException;
import com.study.collect.core.processor.model.ProcessContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Slf4j
@Processor(type = "finance", order = 100)
@Component
public class FinanceProcessor extends AbstractProcessor<FinanceData> {

    @Override
    protected FinanceData doProcess(FinanceData data, ProcessContext context) {

        // 数据验证
        validateData(data);

        // 数据转换
        transformData(data);

        // 数据补充
        enrichData(data);

        return data;
    }


    protected void validateData(FinanceData data) {
        if (data.getPrice() == null || data.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ProcessException("Invalid price");
        }
        if (data.getVolume() == null || data.getVolume().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ProcessException("Invalid volume");
        }
    }

    private void transformData(FinanceData data) {
        // 价格保留2位小数
        if (data.getPrice() != null) {
            data.setPrice(data.getPrice().setScale(2, RoundingMode.HALF_UP));
        }

        // 成交量保留0位小数
        if (data.getVolume() != null) {
            data.setVolume(data.getVolume().setScale(0, RoundingMode.HALF_UP));
        }
    }

    private void enrichData(FinanceData data) {
        // 设置创建时间
        data.setCreateTime(LocalDateTime.now());
    }

    @Override
    public String getType() {
        return "finance";
    }

    @Override
    public int getOrder() {
        return 0;
    }
}

```

## FinanceRepository.java

```java
package com.study.collect.business.finance.repository;


import com.study.collect.business.finance.model.FinanceData;
import com.study.collect.core.storage.repository.IRepository;
import org.springframework.data.mongodb.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface FinanceRepository extends IRepository<FinanceData, String> {

    // 方式一：方法名约定
    List<FinanceData> findByStockCode(String stockCode);

    // 方式二：使用@Query注解
    @Query("{'tradeDate': {$gte: ?0, $lte: ?1}}")
    List<FinanceData> findByTradeDateBetween(String startDate, String endDate);

    // 添加特定业务方法
    @Query(value = "{'amount': {$gt: ?0}}", sort = "{'tradeDate': -1}")
    List<FinanceData> findLargeTransactions(BigDecimal threshold);
}
```

## FinanceService.java

```java
package com.study.collect.business.finance.service;

import com.study.collect.business.finance.collector.FinanceCollector;
import com.study.collect.business.finance.model.FinanceData;
import com.study.collect.business.finance.processor.FinanceProcessor;
import com.study.collect.business.finance.repository.FinanceRepository;
import com.study.collect.core.processor.model.ProcessContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FinanceService {

    private final FinanceCollector collector;
    private final FinanceProcessor processor;

    private final FinanceRepository financeRepository;

    public FinanceData collectStockData(String stockCode) {
        // 1. 采集数据
        FinanceData data = collector.collect(stockCode);

        // 2. 处理数据
        data = processor.process(data, new ProcessContext());

        // 3. 保存数据
        return financeRepository.save(data);
    }


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

## spring.factories

```
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
com.study.collect.business.finance.config.FinanceAutoConfiguration
```

## pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xmlns="http://maven.apache.org/POM/4.0.0"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.study</groupId>
        <artifactId>collect-business</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <artifactId>business-medical</artifactId>

    <dependencies>
        <!-- Spring Boot Web -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- DICOM Libraries -->
        <!--        <dependency>-->
        <!--            <groupId>org.dcm4che</groupId>-->
        <!--            <artifactId>dcm4che-core</artifactId>-->
        <!--            <version>5.31.1</version>-->
        <!--        </dependency>-->
        <!--        <dependency>-->
        <!--            <groupId>org.dcm4che</groupId>-->
        <!--            <artifactId>dcm4che-imageio</artifactId>-->
        <!--            <version>5.31.1</version>-->
        <!--        </dependency>-->

        <!-- Storage -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-mongodb</artifactId>
        </dependency>

        <!-- Object Storage -->
        <dependency>
            <groupId>io.minio</groupId>
            <artifactId>minio</artifactId>
            <version>8.5.7</version>
        </dependency>

        <!-- Security -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>

        <!-- Cache -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>

        <!-- Test -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <scope>provided</scope>
        </dependency>
    </dependencies>
</project>
```

## MedicalCollector.java

```java
package com.study.collect.business.medical.collector;

import com.study.collect.business.medical.engine.MedicalEngine;
import com.study.collect.business.medical.model.MedicalData;
import com.study.collect.core.collector.ICollector;
import com.study.collect.core.collector.annotation.Collector;
import com.study.collect.core.collector.model.CollectContext;
import com.study.collect.core.collector.model.CollectResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Collector(type = "medical")
@Component
@RequiredArgsConstructor
public class MedicalCollector implements ICollector<String, MedicalData> {

    private final MedicalEngine engine;

    @Override
    public CollectResult<MedicalData> collect(CollectContext<String> context) {
        return engine.process(context.getParams());
    }


    @Override
    public String getType() {
        return "medical";
    }
}

```

## MedicalAutoConfiguration.java

```java
package com.study.collect.business.medical.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.study.collect.business.medical")
public class MedicalAutoConfiguration {
}
```

## MedicalController.java

```java
package com.study.collect.business.medical.controller;

import com.study.collect.business.medical.model.MedicalData;
import com.study.collect.business.medical.service.MedicalService;
import com.study.collect.common.model.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/medical")
@RequiredArgsConstructor
public class MedicalController {

    private final MedicalService medicalService;

    @GetMapping("/collect/{patientId}")
    public Response<MedicalData> collect(@PathVariable String patientId) {
        MedicalData data = medicalService.collectPatientData(patientId);
        return Response.success(data);
    }
}
```

## MedicalEngine.java

```java
package com.study.collect.business.medical.engine;

import com.study.collect.business.medical.model.MedicalData;
import com.study.collect.core.collector.model.CollectResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MedicalEngine {

    private final com.study.collect.business.medical.processor.DicomProcessor dicomProcessor;
    private final com.study.collect.business.medical.processor.ImageProcessor imageProcessor;
    private final com.study.collect.business.medical.processor.PrivacyProcessor privacyProcessor;

    public CollectResult<MedicalData> process(String patientId) {
        // 1. 读取DICOM文件
        MedicalData data = dicomProcessor.readDicomData(patientId);

        // 2. 处理图像数据
        data = imageProcessor.process(data);

        // 3. 隐私数据处理
        data = privacyProcessor.process(data);
        CollectResult<MedicalData> result = new CollectResult<>();
        result.setData(data);
        return result;
    }
}
```

## MedicalData.java

```java
package com.study.collect.business.medical.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "medical_data")
public class MedicalData {
    @Id
    private String id;
    private String patientId;
    private String patientName;
    private Integer age;
    private String gender;
    private String diagnosis;
    private byte[] imageData;  // 医学影像数据
    private String imageType;  // 影像类型(CT/MRI等)
    private LocalDateTime examTime;
    private LocalDateTime createTime;
}
```

## DicomProcessor.java

```java
package com.study.collect.business.medical.processor;

import com.study.collect.business.medical.model.MedicalData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DicomProcessor {

    public MedicalData readDicomData(String patientId) {
        // 模拟读取DICOM文件
        log.info("Reading DICOM data for patient: {}", patientId);

        MedicalData data = new MedicalData();
        data.setPatientId(patientId);
        // 设置其他数据...
        return data;
    }
}
```

## ImageProcessor.java

```java
package com.study.collect.business.medical.processor;

import com.study.collect.business.medical.model.MedicalData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ImageProcessor {

    public MedicalData process(MedicalData data) {
        if (data.getImageData() != null) {
            // 图像增强
            enhanceImage(data);

            // 图像压缩
            compressImage(data);
        }
        return data;
    }

    private void enhanceImage(MedicalData data) {
        // 图像增强处理逻辑
        log.info("Enhancing image for patient: {}", data.getPatientId());
    }

    private void compressImage(MedicalData data) {
        // 图像压缩处理逻辑
        log.info("Compressing image for patient: {}", data.getPatientId());
    }
}

```

## PrivacyProcessor.java

```java
package com.study.collect.business.medical.processor;

import com.study.collect.business.medical.model.MedicalData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PrivacyProcessor {

    public MedicalData process(MedicalData data) {
        // 脱敏处理
        maskSensitiveData(data);

        // 加密处理
        encryptSensitiveData(data);

        return data;
    }

    private void maskSensitiveData(MedicalData data) {
        // 对敏感字段进行掩码
        if (data.getPatientName() != null) {
            data.setPatientName(maskName(data.getPatientName()));
        }
    }

    private void encryptSensitiveData(MedicalData data) {
        // 加密敏感数据
        log.info("Encrypting sensitive data for patient: {}", data.getPatientId());
    }

    private String maskName(String name) {
        if (name == null || name.length() <= 1) {
            return name;
        }
        return name.substring(0, 1) + "*".repeat(name.length() - 1);
    }
}

```

## medicalRepository.java

```java
package com.study.collect.business.medical.repository;


import com.study.collect.business.medical.model.MedicalData;
import com.study.collect.core.storage.repository.IRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface medicalRepository extends IRepository<MedicalData, String>, MongoRepository<MedicalData, String> {
    MedicalData findByCode(String code);
}

```

## MedicalService.java

```java
package com.study.collect.business.medical.service;

import com.study.collect.business.medical.collector.MedicalCollector;
import com.study.collect.business.medical.model.MedicalData;
import com.study.collect.business.medical.repository.MedicalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MedicalService {

    private final MedicalCollector collector;
    private final MedicalRepository repository;

    public MedicalData collectPatientData(String patientId) {
        // 1. 采集和处理数据
        MedicalData data = collector.collect(patientId);

        // 2. 保存数据
        return repository.save(data);
    }
}
```

## spring.factories

```
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
com.study.collect.business.medical.config.MedicalAutoConfiguration
```

## pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xmlns="http://maven.apache.org/POM/4.0.0"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.study</groupId>
        <artifactId>platform-collect</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <artifactId>collect-common</artifactId>

    <dependencies>
        <!-- Spring Boot -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>

        <!-- Validation -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>

        <!-- Jackson -->
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.datatype</groupId>
            <artifactId>jackson-datatype-jsr310</artifactId>
        </dependency>

        <!-- Utils -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
        </dependency>
        <dependency>
            <groupId>commons-io</groupId>
            <artifactId>commons-io</artifactId>
        </dependency>

        <!-- Test -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
</project>
```

## App.java

```java
package com.study;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        System.out.println("Hello World!");
    }
}

```

## BaseException.java

```java
package com.study.collect.common.exception;

import lombok.Getter;

@Getter
public abstract class BaseException extends RuntimeException {
    private final String code;

    protected BaseException(String code, String message) {
        super(message);
        this.code = code;
    }
}
```

## Response.java

```java
package com.study.collect.common.model;

import lombok.Data;

@Data
public class Response<T> {
    private String code;
    private String message;
    private T data;

    public static <T> Response<T> success(T data) {
        Response<T> response = new Response<>();
        response.setCode("200");
        response.setMessage("success");
        response.setData(data);
        return response;
    }

    public static <T> Response<T> error(String code, String message) {
        Response<T> response = new Response<>();
        response.setCode(code);
        response.setMessage(message);
        return response;
    }
}
```

## DateUtils.java

```java
package com.study.collect.common.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    private static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_PATTERN);

    public static String format(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DEFAULT_FORMATTER) : null;
    }

    public static LocalDateTime parse(String dateStr) {
        return dateStr != null ? LocalDateTime.parse(dateStr, DEFAULT_FORMATTER) : null;
    }
}

```

## JsonUtils.java

```java
package com.study.collect.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonUtils {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static String toJson(Object obj) {
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (Exception e) {
            log.error("Convert to JSON failed", e);
            throw new RuntimeException("Convert to JSON failed", e);
        }
    }

    public static <T> T fromJson(String json, Class<T> type) {
        try {
            return MAPPER.readValue(json, type);
        } catch (Exception e) {
            log.error("Parse JSON failed", e);
            throw new RuntimeException("Parse JSON failed", e);
        }
    }
}
```

## pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xmlns="http://maven.apache.org/POM/4.0.0"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.study</groupId>
        <artifactId>platform-collect</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <artifactId>collect-core</artifactId>

    <dependencies>
        <!-- Spring Boot -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-aop</artifactId>
        </dependency>

        <!-- Redis -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
        <dependency>
            <groupId>org.redisson</groupId>
            <artifactId>redisson</artifactId>
        </dependency>

        <!-- MongoDB -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-mongodb</artifactId>
        </dependency>

        <!-- RabbitMQ -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-amqp</artifactId>
        </dependency>

        <!-- MariaDB JDBC Driver -->
        <dependency>
            <groupId>org.mariadb.jdbc</groupId>
            <artifactId>mariadb-java-client</artifactId>
        </dependency>

        <!-- Metrics -->
        <dependency>
            <groupId>io.micrometer</groupId>
            <artifactId>micrometer-registry-prometheus</artifactId>
        </dependency>

        <!-- Common -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
        </dependency>
        <dependency>
            <groupId>com.google.guava</groupId>
            <artifactId>guava</artifactId>
        </dependency>

        <!-- MyBatis -->
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>

        </dependency>
        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis</artifactId>
            <version>3.5.9</version>
            <scope>compile</scope>
        </dependency>

        <!-- Test -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.datatype</groupId>
            <artifactId>jackson-datatype-jsr310</artifactId>
        </dependency>
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-pool2</artifactId>
        </dependency>
    </dependencies>
</project>
```

## AbstractCollector.java

```java
package com.study.collect.core.collector;

import com.study.collect.core.collector.exception.CollectException;
import com.study.collect.core.collector.model.CollectContext;
import com.study.collect.core.collector.model.CollectResult;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public abstract class AbstractCollector<T, R> implements ICollector<T, R> {

    @Override
    public CollectResult<R> collect(CollectContext<T> context) {
        String taskId = context.getTaskId();
        log.info("开始执行采集任务: taskId={}, type={}", taskId, getType());

        try {
            // 前置处理
            preProcess(context);

            // 执行采集
//            CollectResult<R> data = doCollect(context);
            R sourceData = doCollect(context);
            CollectResult<R> data = CollectResult.success(sourceData);
            // 后置处理
            postProcess(data);

            log.info("采集任务执行完成: taskId={}", taskId);
            return CollectResult.success(data.getData());

        } catch (Exception e) {
            log.error("采集任务执行失败: taskId={}", taskId, e);
            return CollectResult.failure(e.getMessage());
        }
    }

    /**
     * 前置处理
     */
    protected void preProcess(CollectContext<T> context) {
        // 数据校验
        validateContext(context);
        // 准备采集参数
        prepareCollectParams(context);
    }

    /**
     * 执行采集
     */
//    protected abstract CollectResult<R> doCollect(CollectContext<T> context);
    protected abstract R doCollect(CollectContext<T> context);

    /**
     * 后置处理
     */
    protected void postProcess(CollectResult<R> data) {
        // 数据清洗
        cleanCollectData(data);
        // 结果验证
        validateCollectResult(data);
    }

    /**
     * 上下文校验
     */
    protected void validateContext(CollectContext<T> context) {
        if (context == null) {
            throw new CollectException("采集上下文不能为空");
        }
        if (context.getTaskId() == null) {
            throw new CollectException("任务ID不能为空");
        }
        if (context.getParams() == null) {
            throw new CollectException("采集参数不能为空");
        }
    }

    /**
     * 准备采集参数
     */
    protected void prepareCollectParams(CollectContext<T> context) {
        // 子类可覆盖实现具体的参数准备逻辑
    }

    /**
     * 清洗采集数据
     */
    protected void cleanCollectData(CollectResult<R> data) {
        // 子类可覆盖实现具体的数据清洗逻辑
    }

    /**
     * 验证采集结果
     */
    protected void validateCollectResult(CollectResult<R> data) {
        // 子类可覆盖实现具体的结果验证逻辑
    }
}
```

## ICollector.java

```java
package com.study.collect.core.collector;

import com.study.collect.core.collector.model.CollectContext;
import com.study.collect.core.collector.model.CollectResult;

// 采集器接口
public interface ICollector<T, R> {
    /**
     * 执行采集
     */
    CollectResult<R> collect(CollectContext<T> context);

    /**
     * 获取采集器类型
     */
    String getType();


}

```

## package-info.java

```java
/**
 * 采集器
 */
package com.study.collect.core.collector;
```

## Collector.java

```java
package com.study.collect.core.collector.annotation;

import java.lang.annotation.*;

// 采集器注解
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Collector {
    /**
     * 采集器类型
     */
    String type();

    /**
     * 采集器描述
     */
    String description() default "";

    /**
     * 是否启用
     */
    boolean enabled() default true;

    /**
     * 采集器优先级,值越小优先级越高
     */
    int order() default Integer.MAX_VALUE;
}

```

## CollectorConfiguration.java

```java
package com.study.collect.core.collector.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.study.collect.core.collector")
public class CollectorConfiguration {
    // 采集器模块配置
}
```

## CollectorProperties.java

```java
package com.study.collect.core.collector.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "collect.collector")
public class CollectorProperties {
    /**
     * 是否启用采集器
     */
    private boolean enabled = true;

    /**
     * 采集超时时间(秒)
     */
    private int timeout = 300;

    /**
     * 重试次数
     */
    private int retryTimes = 3;

    /**
     * 重试间隔(秒)
     */
    private int retryInterval = 60;
}
```

## CollectException.java

```java
package com.study.collect.core.collector.exception;

public class CollectException extends RuntimeException {

    public CollectException(String message) {
        super(message);
    }

    public CollectException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

## CollectorFactory.java

```java
package com.study.collect.core.collector.factory;

import com.study.collect.core.collector.ICollector;
import com.study.collect.core.collector.manager.CollectorManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CollectorFactory {

    private final CollectorManager collectorManager;

    public <T, R> ICollector<T, R> createCollector(String type) {
        return collectorManager.getCollector(type);
    }
}
```

## CollectorManager.java

```java
package com.study.collect.core.collector.manager;

import com.study.collect.core.collector.ICollector;
import com.study.collect.core.collector.annotation.Collector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class CollectorManager {

    private final Map<String, ICollector<?, ?>> collectors = new ConcurrentHashMap<>();

    @Autowired
    public void registerCollectors(Map<String, Object> beans) {
        beans.values().stream()
                .filter(bean -> bean.getClass().isAnnotationPresent(Collector.class))
                .forEach(bean -> {
                    Collector annotation = bean.getClass().getAnnotation(Collector.class);
                    if (annotation.enabled()) {
                        ICollector<?, ?> collector = (ICollector<?, ?>) bean;
                        collectors.put(collector.getType(), collector);
                        log.info("注册采集器: type={}, class={}",
                                collector.getType(), collector.getClass().getName());
                    }
                });
    }

    @SuppressWarnings("unchecked")
    public <T, R> ICollector<T, R> getCollector(String type) {
        ICollector<?, ?> collector = collectors.get(type);
        if (collector == null) {
            throw new IllegalArgumentException("未找到采集器: " + type);
        }
        return (ICollector<T, R>) collector;
    }
}

```

## CollectContext.java

```java
package com.study.collect.core.collector.model;

import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class CollectContext<T> {
    /**
     * 上下文属性
     */
    private final Map<String, Object> attributes = new ConcurrentHashMap<>();
    /**
     * 任务ID
     */
    private String taskId;
    /**
     * 采集参数
     */
    private T params;
    /**
     * 分片信息
     */
    private Integer shardingId;
    private Integer shardingTotal;

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <V> V getAttribute(String key) {
        return (V) attributes.get(key);
    }
}

```

## CollectResult.java

```java
package com.study.collect.core.collector.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CollectResult<T> {
    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 采集数据
     */
    private T data;

    /**
     * 完成时间
     */
    private LocalDateTime finishTime;

    public static <T> CollectResult<T> success(T data) {
        CollectResult<T> result = new CollectResult<>();
        result.setSuccess(true);
        result.setData(data);
        result.setFinishTime(LocalDateTime.now());
        return result;
    }

    public static <T> CollectResult<T> failure(String errorMessage) {
        CollectResult<T> result = new CollectResult<>();
        result.setSuccess(false);
        result.setErrorMessage(errorMessage);
        result.setFinishTime(LocalDateTime.now());
        return result;
    }
}
```

## CollectAutoConfiguration.java

```java
package com.study.collect.core.config;

import com.study.collect.core.collector.config.CollectorConfiguration;
import com.study.collect.core.mq.config.MQProperties;
import com.study.collect.core.mq.config.RabbitConfig;
import com.study.collect.core.processor.config.ProcessorConfiguration;
import com.study.collect.core.storage.cache.config.CacheAutoConfiguration;
import com.study.collect.core.storage.config.MongoConfig;
import com.study.collect.core.task.config.MyBatisConfig;
import com.study.collect.core.task.config.TaskConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableConfigurationProperties({
        MQProperties.class
})
@Import({
        // 数据存储配置
        MongoConfig.class,          // MongoDB
        MyBatisConfig.class,        // MyBatis
        CacheAutoConfiguration.class,// Redis

        // 消息队列配置
        RabbitConfig.class,         // RabbitMQ

        // 业务配置
        TaskConfiguration.class,     // 任务配置
        CollectorConfiguration.class,// 采集器配置
        ProcessorConfiguration.class // 处理器配置
})
public class CollectAutoConfiguration {
}
```

## package-info.java

```java
/**
 * 核心配置包
 */
package com.study.collect.core.config;
```

## MQProperties.java

```java
package com.study.collect.core.mq.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 消息队列配置属性类
 * 对应配置文件中 collect.mq 前缀的配置项
 */
@Data
@ConfigurationProperties(prefix = "collect.mq")
public class MQProperties {

    /**
     * RabbitMQ相关配置
     */
    private RabbitMQ rabbit = new RabbitMQ();

    @Data
    public static class RabbitMQ {
        /**
         * 服务器地址
         */
        private String host;

        /**
         * 服务器端口
         */
        private Integer port;

        /**
         * 用户名
         */
        private String username;

        /**
         * 密码
         */
        private String password;

        /**
         * 虚拟主机
         */
        private String virtualHost = "/";

        /**
         * 任务队列配置
         */
        private Queue task = new Queue();

        /**
         * 结果队列配置
         */
        private Queue result = new Queue();

        /**
         * 队列配置类
         */
        @Data
        public static class Queue {
            /**
             * 交换机名称
             */
            private String exchange;

            /**
             * 队列名称
             */
            private String queue;

            /**
             * 路由键
             */
            private String routingKey;

            /**
             * 是否持久化
             */
            private boolean durable = true;

            /**
             * 是否自动删除
             */
            private boolean autoDelete = false;
        }
    }
}
```

## RabbitConfig.java

```java
package com.study.collect.core.mq.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "collect.mq.rabbit", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(MQProperties.class)
public class RabbitConfig {

    @Bean
    public DirectExchange taskExchange(MQProperties properties) {
        return new DirectExchange(properties.getRabbit().getTask().getExchange());
    }

    @Bean
    public Queue taskQueue(MQProperties properties) {
        return QueueBuilder.durable(properties.getRabbit().getTask().getQueue())
                .withArgument("x-dead-letter-exchange",
                        properties.getRabbit().getTask().getExchange() + ".dlx")
                .withArgument("x-dead-letter-routing-key",
                        properties.getRabbit().getTask().getRoutingKey() + ".dlx")
                .build();
    }

    @Bean
    public Binding taskBinding(Queue taskQueue, DirectExchange taskExchange,
                               MQProperties properties) {
        return BindingBuilder.bind(taskQueue)
                .to(taskExchange)
                .with(properties.getRabbit().getTask().getRoutingKey());
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(new Jackson2JsonMessageConverter());
        return template;
    }
}
```

## TaskConsumer.java

```java
package com.study.collect.core.mq.consumer;

import com.study.collect.core.mq.message.TaskMessage;
import com.study.collect.core.mq.message.TaskResultMessage;
import com.study.collect.core.mq.producer.TaskProducer;
import com.study.collect.core.task.handler.TaskHandler;
import com.study.collect.core.task.handler.TaskHandlerManager;
import com.study.collect.core.task.model.TaskContext;
import com.study.collect.core.task.model.TaskResult;
import com.study.collect.core.task.service.TaskExecuteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class TaskConsumer {

    private final TaskExecuteService taskExecuteService;
    private final TaskProducer taskProducer;
    private final TaskHandlerManager handlerManager;

    @Autowired
    public TaskConsumer(TaskExecuteService taskExecuteService,
                        TaskProducer taskProducer,
                        TaskHandlerManager handlerManager) {
        this.taskExecuteService = taskExecuteService;
        this.taskProducer = taskProducer;
        this.handlerManager = handlerManager;
    }

    @RabbitListener(queues = "${collect.mq.rabbit.task.queue}")
    public void onTaskMessage(TaskMessage message) {
        String instanceId = message.getInstanceId();
        log.info("Received task message: instanceId={}, taskCode={}, shard={}/{}",
                instanceId,
                message.getTaskId(),
                message.getShardIndex() + 1,
                message.getShardTotal()
        );

        try {
            // 执行任务
            Object result = executeTask(message);

            // 发送成功结果
            sendSuccessResult(message, result);

            // 更新任务状态
            taskExecuteService.completeTaskInstance(instanceId, true, null);

            log.info("Task executed successfully: {}", instanceId);

        } catch (Exception e) {
            log.error("Task execution failed: " + instanceId, e);

            // 发送失败结果
            sendFailureResult(message, e.getMessage());

            // 更新任务状态
            taskExecuteService.completeTaskInstance(instanceId, false, e.getMessage());
        }
    }

//    private Object executeTask(TaskMessage message) {
//        // 实际任务执行逻辑
//        // 这里需要根据具体业务实现，可能需要调用具体的TaskHandler
//        return null;
//    }
    private Object executeTask(TaskMessage message) {
        // 创建任务上下文
        TaskContext context = createTaskContext(message);

        // 获取任务处理器
        TaskHandler handler = handlerManager.getHandler(message.getTaskId());

        // 执行任务
        TaskResult result = handler.execute(context);

        if (!result.getSuccess()) {
            throw new RuntimeException(result.getErrorMessage());
        }

        return result.getData();
    }

    private TaskContext createTaskContext(TaskMessage message) {
        TaskContext context = new TaskContext();
        context.setTaskId(message.getTaskId());
        context.setInstanceId(message.getInstanceId());
        context.setShardIndex(message.getShardIndex());
        context.setShardTotal(message.getShardTotal());
        context.setShardParam(message.getShardParam());
        return context;
    }

    private void sendSuccessResult(TaskMessage message, Object result) {
        TaskResultMessage resultMessage = createResultMessage(message);
        resultMessage.setSuccess(true);
        resultMessage.setResult(result);
        taskProducer.sendResult(resultMessage);
    }

    private void sendFailureResult(TaskMessage message, String errorMsg) {
        TaskResultMessage resultMessage = createResultMessage(message);
        resultMessage.setSuccess(false);
        resultMessage.setErrorMsg(errorMsg);
        taskProducer.sendResult(resultMessage);
    }

    private TaskResultMessage createResultMessage(TaskMessage message) {
        TaskResultMessage resultMessage = new TaskResultMessage();
        resultMessage.setTaskId(message.getTaskId());
        resultMessage.setInstanceId(message.getInstanceId());
        resultMessage.setExecuteHost(message.getHostName());
        resultMessage.setFinishTime(LocalDateTime.now());
        return resultMessage;
    }
}
```

## BaseMessage.java

```java
package com.study.collect.core.mq.message;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 消息基类
 * 定义所有消息共有的属性和行为
 */
@Data
public abstract class BaseMessage implements Serializable {

    /**
     * 消息ID，用于消息追踪
     */
    private String messageId;

    /**
     * 消息类型，用于区分不同消息
     */
    private String type;

    /**
     * 消息创建时间
     */
    private LocalDateTime createTime;

    /**
     * 消息优先级
     */
    private Integer priority;

    /**
     * 消息重试次数
     */
    private Integer retryCount;

    /**
     * 额外属性，用于扩展
     */
    private String properties;

    public BaseMessage() {
        this.messageId = UUID.randomUUID().toString();
        this.createTime = LocalDateTime.now();
        this.retryCount = 0;
    }

    /**
     * 设置消息类型
     * 子类需要在构造函数中调用此方法设置具体的消息类型
     */
    protected void setType(String type) {
        this.type = type;
    }

    /**
     * 增加重试次数
     */
    public void incrementRetryCount() {
        if (this.retryCount == null) {
            this.retryCount = 0;
        }
        this.retryCount++;
    }
}
```

## TaskMessage.java

```java
package com.study.collect.core.mq.message;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TaskMessage extends BaseMessage {
    private String taskId;           // 任务编码
    private String instanceId;       // 实例ID
    private Integer shardIndex;      // 分片索引
    private Integer shardTotal;      // 分片总数
    private String shardParam;       // 分片参数
    private String hostName;         // 执行机器

    public TaskMessage() {
        super();
        setType("TASK");
    }
}
```

## TaskResultMessage.java

```java
package com.study.collect.core.mq.message;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class TaskResultMessage extends BaseMessage {
    private String taskId;           // 任务编码
    private String instanceId;       // 实例ID
    private String executeHost;      // 执行机器
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

## TaskProducer.java

```java
package com.study.collect.core.mq.producer;

import com.study.collect.core.mq.config.MQProperties;
import com.study.collect.core.mq.message.TaskMessage;
import com.study.collect.core.mq.message.TaskResultMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TaskProducer {

    private final RabbitTemplate rabbitTemplate;
    private final MQProperties mqProperties;

    @Autowired
    public TaskProducer(RabbitTemplate rabbitTemplate, MQProperties mqProperties) {
        this.rabbitTemplate = rabbitTemplate;
        this.mqProperties = mqProperties;
    }

    public void sendTask(TaskMessage message) {
        try {
            MQProperties.RabbitMQ.Queue taskQueue = mqProperties.getRabbit().getTask();
            rabbitTemplate.convertAndSend(
                    taskQueue.getExchange(),
                    taskQueue.getRoutingKey(),
                    message
            );
            log.info("Task message sent: instanceId={}, taskCode={}, shard={}/{}",
                    message.getInstanceId(),
                    message.getTaskId(),
                    message.getShardIndex() + 1,
                    message.getShardTotal()
            );
        } catch (Exception e) {
            log.error("Failed to send task message: " + message.getInstanceId(), e);
            throw new RuntimeException("Message sending failed", e);
        }
    }

    public void sendResult(TaskResultMessage message) {
        try {
            MQProperties.RabbitMQ.Queue resultQueue = mqProperties.getRabbit().getResult();
            rabbitTemplate.convertAndSend(
                    resultQueue.getExchange(),
                    resultQueue.getRoutingKey(),
                    message
            );
            log.info("Result message sent: instanceId={}, taskCode={}, success={}",
                    message.getInstanceId(),
                    message.getTaskId(),
                    message.getSuccess()
            );
        } catch (Exception e) {
            log.error("Failed to send result message: " + message.getInstanceId(), e);
            throw new RuntimeException("Message sending failed", e);
        }
    }
}
```

## AbstractProcessor.java

```java
package com.study.collect.core.processor;

// 抽象处理器

import com.study.collect.core.processor.exception.ProcessException;
import com.study.collect.core.processor.model.ProcessContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

@Slf4j
public abstract class AbstractProcessor<T> implements IProcessor<T> {

    @Override
    public T process(T data, ProcessContext context) {
        String taskId = context.getTaskId();
        log.info("开始数据处理: taskId={}, type={}", taskId, getType());

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
            log.info("数据处理完成: taskId={}, cost={}ms", taskId, stopWatch.getTotalTimeMillis());

            return result;

        } catch (Exception e) {
            log.error("数据处理异常: taskId={}", taskId, e);
            throw new ProcessException("数据处理失败: " + e.getMessage(), e);
        }
    }

    /**
     * 前置处理
     */
    protected void preProcess(T data, ProcessContext context) {
        // 数据校验
        validateData(data);
        // 上下文校验
        validateContext(context);
    }

    /**
     * 执行处理
     */
    protected abstract T doProcess(T data, ProcessContext context);

    /**
     * 后置处理
     */
    protected T postProcess(T result, ProcessContext context) {
        return result;
    }

    /**
     * 数据校验
     */
    protected void validateData(T data) {
        if (data == null) {
            throw new ProcessException("处理数据不能为空");
        }
    }

    /**
     * 上下文校验
     */
    protected void validateContext(ProcessContext context) {
        if (context == null) {
            throw new ProcessException("处理上下文不能为空");
        }
        if (context.getTaskId() == null) {
            throw new ProcessException("任务ID不能为空");
        }
    }
}
```

## IProcessor.java

```java
package com.study.collect.core.processor;

// 处理器接口


import com.study.collect.core.processor.model.ProcessContext;

public interface IProcessor<T> {
    /**
     * 执行数据处理
     *
     * @param data    待处理数据
     * @param context 处理上下文
     * @return 处理后的数据
     */
    T process(T data, ProcessContext context);

    /**
     * 获取处理器类型
     */
    String getType();

    /**
     * 获取处理器执行顺序
     */
    int getOrder();
}

```

## package-info.java

```java
/**
 * 处理器模块
 */
package com.study.collect.core.processor;
```

## Processor.java

```java
package com.study.collect.core.processor.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Processor {
    /**
     * 处理器类型
     */
    String type();

    /**
     * 处理器描述
     */
    String description() default "";

    /**
     * 是否启用
     */
    boolean enabled() default true;

    /**
     * 处理器执行顺序
     */
    int order() default Integer.MAX_VALUE;
}

```

## ProcessorConfiguration.java

```java
package com.study.collect.core.processor.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.study.collect.core.processor")
public class ProcessorConfiguration {
    // 处理器模块配置
}
```

## ProcessorProperties.java

```java
package com.study.collect.core.processor.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "collect.processor")
public class ProcessorProperties {
    /**
     * 是否启用处理器
     */
    private boolean enabled = true;

    /**
     * 处理超时时间(秒)
     */
    private int timeout = 60;

    /**
     * 是否异步处理
     */
    private boolean async = false;

    /**
     * 异步处理线程池大小
     */
    private int poolSize = 5;
}

```

## ProcessException.java

```java
package com.study.collect.core.processor.exception;

public class ProcessException extends RuntimeException {

    public ProcessException(String message) {
        super(message);
    }

    public ProcessException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

## ProcessorManager.java

```java
package com.study.collect.core.processor.manager;

import com.study.collect.core.processor.IProcessor;
import com.study.collect.core.processor.annotation.Processor;
import com.study.collect.core.processor.chain.ProcessChain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class ProcessorManager {

    private final Map<String, IProcessor<?>> processors = new ConcurrentHashMap<>();
    private final Map<String, ProcessChain<?>> chains = new ConcurrentHashMap<>();

    @Autowired
    public void registerProcessors(Map<String, Object> beans) {
        beans.values().stream()
                .filter(bean -> bean.getClass().isAnnotationPresent(Processor.class))
                .forEach(bean -> {
                    Processor annotation = bean.getClass().getAnnotation(Processor.class);
                    if (annotation.enabled()) {
                        IProcessor<?> processor = (IProcessor<?>) bean;
                        processors.put(processor.getType(), processor);
                        log.info("注册处理器: type={}, order={}, class={}",
                                processor.getType(), processor.getOrder(),
                                processor.getClass().getName());
                    }
                });
    }

    @SuppressWarnings("unchecked")
    public <T> ProcessChain<T> createChain() {
        String chainId = UUID.randomUUID().toString();
        ProcessChain<T> chain = new ProcessChain<>(chainId);

        processors.values().stream()
                .map(p -> (IProcessor<T>) p)
                .forEach(chain::addProcessor);

        chains.put(chainId, chain);
        return chain;
    }

    @SuppressWarnings("unchecked")
    public <T> IProcessor<T> getProcessor(String type) {
        IProcessor<?> processor = processors.get(type);
        if (processor == null) {
            throw new IllegalArgumentException("未找到处理器: " + type);
        }
        return (IProcessor<T>) processor;
    }
}
```

## ProcessChain.java

```java
package com.study.collect.core.processor.chain;

import com.study.collect.core.processor.IProcessor;
import com.study.collect.core.processor.model.ProcessContext;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ProcessChain<T> {

    private final List<IProcessor<T>> processors;
    private final String chainId;

    public ProcessChain(String chainId) {
        this.chainId = chainId;
        this.processors = new ArrayList<>();
    }

    public void addProcessor(IProcessor<T> processor) {
        processors.add(processor);
        processors.sort((p1, p2) -> p1.getOrder() - p2.getOrder());
    }

    public T process(T data) {
        ProcessContext context = new ProcessContext();
        context.setChainId(chainId);

        for (IProcessor<T> processor : processors) {
            try {
                data = processor.process(data, context);
                log.debug("处理器执行成功: processor={}, chainId={}",
                        processor.getType(), chainId);
            } catch (Exception e) {
                log.error("处理器执行失败: processor={}, chainId={}",
                        processor.getType(), chainId, e);
                throw e;
            }
        }

        return data;
    }
}
```

## ProcessContext.java

```java
package com.study.collect.core.processor.model;

import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class ProcessContext {
    /**
     * 上下文属性
     */
    private final Map<String, Object> attributes = new ConcurrentHashMap<>();
    /**
     * 任务ID
     */
    private String taskId;
    /**
     * 处理器链ID
     */
    private String chainId;

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key) {
        return (T) attributes.get(key);
    }
}
```

## Repository.java

```java
package com.study.collect.core.storage.annotation;

import java.lang.annotation.*;

// Repository
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Repository {
    /**
     * 仓储类型
     */
    String type();

    /**
     * 存储描述
     */
    String description() default "";
}
```

## EntityAuditor.java

```java
package com.study.collect.core.storage.audit;

import com.study.collect.core.storage.entity.BaseEntity;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class EntityAuditor implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        // 获取当前操作用户，可以从SecurityContext或ThreadLocal中获取
        return Optional.of("system");
    }
}

```

## package-info.java

```java
/**
 * 缓存
 */
package com.study.collect.core.storage.cache;
```

## Cache.java

```java
package com.study.collect.core.storage.cache.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Cache {
    String key() default "";       // 缓存key
    String prefix() default "";    // 前缀
    long expire() default 3600L;   // 过期时间
    TimeUnit timeUnit() default TimeUnit.SECONDS;  // 时间单位
}
```

## CacheEvict.java

```java
package com.study.collect.core.storage.cache.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CacheEvict {
    String key() default "";       // 缓存key
    String prefix() default "";    // 前缀
    boolean allEntries() default false;  // 是否清除所有
    boolean beforeInvocation() default false; // 是否在方法执行前清除
}
```

## CacheLock.java

```java
package com.study.collect.core.storage.cache.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CacheLock {
    String key();                 // 锁key
    String prefix() default "";   // 前缀
    long waitTime() default 3L;   // 等待时间
    long leaseTime() default 10L; // 租约时间
    TimeUnit timeUnit() default TimeUnit.SECONDS;  // 时间单位
}
```

## CacheAspect.java

```java
package com.study.collect.core.storage.cache.aspect;

import com.study.collect.core.storage.cache.annotation.Cache;
import com.study.collect.core.storage.cache.annotation.CacheEvict;
import com.study.collect.core.storage.cache.manager.CacheManager;
import com.study.collect.core.storage.cache.model.CacheOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class CacheAspect {

    private final CacheManager cacheManager;
    private final SpelExpressionParser parser = new SpelExpressionParser();

    /**
     * 缓存切面逻辑
     */
    @Around("@annotation(cache)")
    public Object doCache(ProceedingJoinPoint point, Cache cache) throws Throwable {
        // 解析key
        String key = parseKey(cache.prefix(), cache.key(), point);

        // 获取返回类型
        MethodSignature signature = (MethodSignature) point.getSignature();
        Class<?> returnType = signature.getReturnType();

        // 构造缓存参数
        CacheOptions options = CacheOptions.builder()
                .expiration(cache.expire())
                .timeUnit(cache.timeUnit())
                .build();

        // 调用底层缓存逻辑
        try {
            return getFromCache(point, key, returnType, options);
        } catch (Exception e) {
            log.error("Cache operation failed for key: {}", key, e);
            // 缓存异常时，直接执行原方法
            return point.proceed();
        }
    }

    /**
     * 缓存清理切面逻辑
     */
    @Around("@annotation(cacheEvict)")
    public Object doEvict(ProceedingJoinPoint point, CacheEvict cacheEvict) throws Throwable {
        // 如果 beforeInvocation = true，则先清理再执行，否则先执行再清理
        boolean beforeInvocation = cacheEvict.beforeInvocation();
        if (beforeInvocation) {
            evictCache(cacheEvict, point);
        }
        Object result = point.proceed();
        if (!beforeInvocation) {
            evictCache(cacheEvict, point);
        }
        return result;
    }

    /**
     * 执行具体的缓存清理逻辑
     */
    private void evictCache(CacheEvict cacheEvict, ProceedingJoinPoint point) {
        try {
            if (cacheEvict.allEntries()) {
                cacheManager.removeByPrefix(cacheEvict.prefix());
            } else {
                String key = parseKey(cacheEvict.prefix(), cacheEvict.key(), point);
                cacheManager.remove(key);
            }
        } catch (Exception e) {
            log.error("Failed to evict cache", e);
        }
    }

    /**
     * 解析 SpEL 表达式得到缓存 key
     */
    private String parseKey(String prefix, String key, ProceedingJoinPoint point) {
        if (!StringUtils.hasText(key)) {
            return prefix;
        }
        try {
            EvaluationContext context = new StandardEvaluationContext();
            MethodSignature signature = (MethodSignature) point.getSignature();
            String[] paramNames = signature.getParameterNames();
            Object[] args = point.getArgs();
            for (int i = 0; i < paramNames.length; i++) {
                context.setVariable(paramNames[i], args[i]);
            }
            // 根据 SpEL 表达式计算出的 key
            String parsedKey = parser.parseExpression(key).getValue(context, String.class);
            // 如果 prefix 不为空，则使用 prefix:parsedKey
            return StringUtils.hasText(prefix) ? prefix + ":" + parsedKey : parsedKey;
        } catch (Exception e) {
            log.error("Failed to parse cache key: {}", key, e);
            throw new IllegalArgumentException("Invalid cache key expression", e);
        }
    }

    /**
     * 使用泛型方法，避免不安全的类型转换警告
     */
    @SuppressWarnings("unchecked")
    private <T> T getFromCache(ProceedingJoinPoint point, String key, Class<?> returnType, CacheOptions options) {
        return cacheManager.get(key, (Class<T>) returnType, () -> {
            try {
                return (T) point.proceed();
            } catch (Throwable e) {
                log.error("Method execution failed at: {}", point.getSignature(), e);
                throw new IllegalStateException("Cache method execution failed", e);
            }
        }, options);
    }
}

```

## LockAspect.java

```java
package com.study.collect.core.storage.cache.aspect;

import com.study.collect.core.storage.cache.annotation.CacheLock;
import com.study.collect.core.storage.cache.lock.DistributedLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LockAspect {

    private final DistributedLock lock;
    private final SpelExpressionParser parser = new SpelExpressionParser();

    @Around("@annotation(cacheLock)")
    public Object doLock(ProceedingJoinPoint point, CacheLock cacheLock) throws Throwable {
        String key = parseKey(cacheLock.prefix(), cacheLock.key(), point);

        try {
            boolean locked = lock.tryLock(key,
                    cacheLock.waitTime(),
                    cacheLock.leaseTime(),
                    cacheLock.timeUnit());

            if (!locked) {
                throw new RuntimeException("Failed to acquire lock: " + key);
            }

            return point.proceed();
        } finally {
            lock.unlock(key);
        }
    }


    // key解析实现，与CacheAspect中相同
    private String parseKey(String prefix, String key, ProceedingJoinPoint point) {
        if (key.isEmpty()) {
            return prefix;
        }

        EvaluationContext context = new StandardEvaluationContext();
        MethodSignature signature = (MethodSignature) point.getSignature();
        String[] paramNames = signature.getParameterNames();
        Object[] args = point.getArgs();

        for (int i = 0; i < paramNames.length; i++) {
            context.setVariable(paramNames[i], args[i]);
        }

        String parsedKey = parser.parseExpression(key).getValue(context, String.class);
        return prefix.isEmpty() ? parsedKey : prefix + ":" + parsedKey;
    }
}
```

## CacheAutoConfiguration.java

```java
package com.study.collect.core.storage.cache.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
@ConditionalOnProperty(prefix = "spring.data.redis", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(CacheProperties.class)
public class CacheAutoConfiguration {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory connectionFactory,
            ObjectMapper objectMapper) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 使用Jackson2JsonRedisSerializer作为序列化器
        Jackson2JsonRedisSerializer<Object> serializer =
                new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);

        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }
}

```

## CacheProperties.java

```java
package com.study.collect.core.storage.cache.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.TimeUnit;

@Data
@ConfigurationProperties(prefix = "collect.storage.cache")
public class CacheProperties {
    private boolean enabled = true;
    private long defaultExpiration = 3600L;
    private TimeUnit timeUnit = TimeUnit.SECONDS;

    private Redis redis = new Redis();

    @Data
    public static class Redis {
        private String host;
        private int port;
        private String password;
        private int database = 0;

        private Pool pool = new Pool();

        @Data
        public static class Pool {
            private int maxActive = 8;
            private int maxIdle = 8;
            private int minIdle = 0;
            private long maxWait = -1;
        }
    }
}
```

## DistributedLock.java

```java
package com.study.collect.core.storage.cache.lock;

import java.util.concurrent.TimeUnit;

public interface DistributedLock {
    /**
     * 获取锁
     * @param key 锁的key
     * @param waitTime 等待时间
     * @param leaseTime 租约时间
     * @param unit 时间单位
     * @return 是否获取成功
     */
    boolean tryLock(String key, long waitTime, long leaseTime, TimeUnit unit);

    /**
     * 释放锁
     * @param key 锁的key
     */
    void unlock(String key);
}

```

## package-info.java

```java
/**
 * 分布式锁
 */
package com.study.collect.core.storage.cache.lock;
```

## RedisLock.java

```java
package com.study.collect.core.storage.cache.lock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

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
                        .setIfAbsent(key, Thread.currentThread().getId(), leaseTime, unit);

                if (Boolean.TRUE.equals(success)) {
                    return true;
                }

                // 等待一段时间后重试
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
            log.error("Failed to unlock: {}", key, e);
        }
    }
}
```

## CacheManager.java

```java
package com.study.collect.core.storage.cache.manager;

import com.study.collect.core.storage.cache.model.CacheOptions;
import java.util.Collection;
import java.util.function.Supplier;

/**
 * 缓存管理器接口
 * 提供统一的缓存操作能力，支持自动加载、批量操作和类型安全的数据访问
 */
public interface CacheManager {

    /**
     * 获取缓存，如果不存在则通过supplier加载并缓存
     *
     * @param key 缓存键
     * @param type 返回值类型
     * @param supplier 数据加载器
     * @return 缓存的值
     * @param <T> 值类型
     */
    <T> T get(String key, Class<T> type, Supplier<T> supplier);

    /**
     * 使用自定义选项获取缓存
     *
     * @param key 缓存键
     * @param type 返回值类型
     * @param supplier 数据加载器
     * @param options 缓存选项
     * @return 缓存的值
     * @param <T> 值类型
     */
    <T> T get(String key, Class<T> type, Supplier<T> supplier, CacheOptions options);

    /**
     * 批量获取缓存，如果不存在则通过supplier加载并缓存
     *
     * @param keys 缓存键集合
     * @param type 返回值类型
     * @param supplier 数据加载器
     * @return 缓存值集合
     * @param <T> 值类型
     */
    <T> Collection<T> multiGet(Collection<String> keys, Class<T> type, Supplier<Collection<T>> supplier);

    /**
     * 使用自定义选项批量获取缓存
     *
     * @param keys 缓存键集合
     * @param type 返回值类型
     * @param supplier 数据加载器
     * @param options 缓存选项
     * @return 缓存值集合
     * @param <T> 值类型
     */
    <T> Collection<T> multiGet(Collection<String> keys, Class<T> type, Supplier<Collection<T>> supplier, CacheOptions options);

    /**
     * 更新缓存
     *
     * @param key 缓存键
     * @param value 缓存值
     * @param <T> 值类型
     */
    <T> void put(String key, T value);

    /**
     * 使用自定义选项更新缓存
     *
     * @param key 缓存键
     * @param value 缓存值
     * @param options 缓存选项
     * @param <T> 值类型
     */
    <T> void put(String key, T value, CacheOptions options);

    /**
     * 删除指定键的缓存
     *
     * @param key 缓存键
     */
    void remove(String key);

    /**
     * 删除指定前缀的所有缓存
     *
     * @param prefix 缓存键前缀
     */
    void removeByPrefix(String prefix);
}
```

## RedisCacheManager.java

```java
package com.study.collect.core.storage.cache.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.collect.core.storage.cache.config.CacheProperties;
import com.study.collect.core.storage.cache.model.CacheOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Redis缓存管理器实现
 * 提供基于Redis的缓存操作实现，支持数据自动加载、类型转换和批量操作
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisCacheManager implements CacheManager {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final CacheProperties cacheProperties;

    @Override
    public <T> T get(String key, Class<T> type, Supplier<T> supplier) {
        return get(key, type, supplier, null);
    }

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
    public <T> Collection<T> multiGet(Collection<String> keys, Class<T> type, Supplier<Collection<T>> supplier) {
        return multiGet(keys, type, supplier, null);
    }

    @Override
    public <T> Collection<T> multiGet(Collection<String> keys, Class<T> type,
                                      Supplier<Collection<T>> supplier, CacheOptions options) {
        List<Object> values = Optional.ofNullable(redisTemplate.opsForValue().multiGet(keys))
                .orElse(List.of());

        if (!values.isEmpty() && !values.contains(null)) {
            return values.stream()
                    .map(value -> convertValue(value, type))
                    .collect(Collectors.toList());
        }

        Collection<T> newValues = supplier.get();
        if (newValues != null && !newValues.isEmpty()) {
            newValues.forEach(value -> put(generateKey(value), value, options));
        }
        return newValues;
    }

    @Override
    public <T> void put(String key, T value) {
        put(key, value, null);
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

    @Override
    public void remove(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public void removeByPrefix(String prefix) {
        Optional.ofNullable(redisTemplate.keys(prefix + "*"))
                .filter(keys -> !keys.isEmpty())
                .ifPresent(redisTemplate::delete);
    }

    /**
     * 将缓存值转换为指定类型
     */
    @SuppressWarnings("unchecked")
    private <T> T convertValue(Object value, Class<T> type) {
        try {
            if (type.isInstance(value)) {
                return (T) value;
            }
            return objectMapper.convertValue(value, type);
        } catch (Exception e) {
            log.error("缓存值类型转换失败: value={}, targetType={}", value, type, e);
            return null;
        }
    }

    /**
     * 生成缓存键
     */
    private <T> String generateKey(T value) {
        return String.format("%s:%d", value.getClass().getSimpleName(), value.hashCode());
    }
}
```

## CacheOptions.java

```java
package com.study.collect.core.storage.cache.model;

import lombok.Builder;
import lombok.Data;

import java.util.concurrent.TimeUnit;

@Data
@Builder
public class CacheOptions {
    // 过期时间
    private long expiration;

    // 时间单位
    private TimeUnit timeUnit;

    // 是否允许空值缓存
    private boolean cacheNull;

    // 是否使用压缩
    private boolean useCompression;

    // 自定义序列化器
    private String serializer;

    public static CacheOptions defaultOptions() {
        return CacheOptions.builder()
                .expiration(3600)
                .timeUnit(TimeUnit.SECONDS)
                .cacheNull(false)
                .useCompression(false)
                .build();
    }
}
```

## MongoConfig.java

```java
package com.study.collect.core.storage.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.study.collect.core.storage.repository.BaseMongoRepository;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Data
@Configuration
@EnableMongoAuditing
@ConditionalOnProperty(prefix = "spring.data.mongodb", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableMongoRepositories(
        basePackages = "com.study.collect",
        repositoryBaseClass = BaseMongoRepository.class
)
@ConfigurationProperties(prefix = "spring.data.mongodb")
public class MongoConfig extends AbstractMongoClientConfiguration {

    private String uri;
    private String database;

    @Override
    protected String getDatabaseName() {
        return database;
    }

    @Override
    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create(uri);
    }
}

```

## BaseEntity.java

```java
package com.study.collect.core.storage.entity;

import lombok.Data;
import org.springframework.data.annotation.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public abstract class BaseEntity implements Serializable {
    @Id
    protected String id;

    @CreatedDate
    protected LocalDateTime createTime;

    @LastModifiedDate
    protected LocalDateTime updateTime;

    @CreatedBy
    protected String createBy;

    @LastModifiedBy
    protected String updateBy;

    @Version
    protected Long version;

    protected Boolean deleted = false;

}
```

## VersionEntity.java

```java
package com.study.collect.core.storage.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    public void upgradeVersion() {
        this.version = this.version + 1;
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

## DefaultEntityEventHandler.java

```java
package com.study.collect.core.storage.event;

import com.study.collect.core.storage.entity.BaseEntity;
import com.study.collect.core.storage.event.impl.EntityEvents;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 默认实体事件处理器
 */
@Slf4j
@Component
public class DefaultEntityEventHandler {

    @EventListener
    public <T extends BaseEntity> void handleBeforeSave(EntityEvents.BeforeSaveEvent<T> event) {
        log.debug("Entity before save: {}", event.getEntity());
    }

    @EventListener
    public <T extends BaseEntity> void handleAfterSave(EntityEvents.AfterSaveEvent<T> event) {
        log.debug("Entity after save: {}", event.getEntity());
    }

    @EventListener
    public <T extends BaseEntity> void handleBeforeUpdate(EntityEvents.BeforeUpdateEvent<T> event) {
        log.debug("Entity before update: {}", event.getEntity());
    }

    @EventListener
    public <T extends BaseEntity> void handleAfterUpdate(EntityEvents.AfterUpdateEvent<T> event) {
        log.debug("Entity after update: {}", event.getEntity());
    }

    @EventListener
    public <T extends BaseEntity> void handleBeforeDelete(EntityEvents.BeforeDeleteEvent<T> event) {
        log.debug("Entity before delete: {}", event.getEntity());
    }

    @EventListener
    public <T extends BaseEntity> void handleAfterDelete(EntityEvents.AfterDeleteEvent<T> event) {
        log.debug("Entity after delete: {}", event.getEntity());
    }

    @EventListener
    public <T extends BaseEntity> void handleVersionUpgrade(EntityEvents.VersionUpgradeEvent<T> event) {
        log.debug("Entity version upgrade: {} from {} to {}",
                event.getEntity(), event.getOldVersion(), event.getNewVersion());
    }
}
```

## EntityEvent.java

```java
package com.study.collect.core.storage.event;

import com.study.collect.core.storage.entity.BaseEntity;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 实体事件基类
 */
@Getter
public abstract class EntityEvent<T extends BaseEntity> extends ApplicationEvent {

    protected final T entity;

    public EntityEvent(T entity) {
        super(entity);
        this.entity = entity;
    }
}
```

## EntityEventListener.java

```java
package com.study.collect.core.storage.event;

import com.study.collect.core.storage.entity.BaseEntity;
import com.study.collect.core.storage.entity.VersionEntity;
import com.study.collect.core.storage.event.impl.EntityEvents;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EntityEventListener<T extends BaseEntity>
        extends AbstractMongoEventListener<T> {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public void onBeforeConvert(BeforeConvertEvent<T> event) {
        T entity = event.getSource();

        // 处理版本
        if (entity instanceof VersionEntity versionEntity) {
            String oldVersion = versionEntity.getVersionCode();
            if (oldVersion == null) {
                versionEntity.initVersion();
            } else {
                versionEntity.upgradeVersion();
                publishVersionUpgradeEvent(entity, oldVersion,
                        versionEntity.getVersionCode());
            }
        }
    }

    private void publishVersionUpgradeEvent(T entity, String oldVersion,
                                            String newVersion) {
        EntityEvents.VersionUpgradeEvent<T> event = new EntityEvents.VersionUpgradeEvent<>(
                entity, oldVersion, newVersion
        );
        eventPublisher.publishEvent(event);
    }
}
```

## EntityEvents.java

```java
package com.study.collect.core.storage.event.impl;

import com.study.collect.core.storage.entity.BaseEntity;
import com.study.collect.core.storage.event.EntityEvent;
import lombok.Getter;

public class EntityEvents {

    @Getter
    public static class BeforeSaveEvent<T extends BaseEntity> extends EntityEvent<T> {
        public BeforeSaveEvent(T entity) {
            super(entity);
        }
    }

    @Getter
    public static class AfterSaveEvent<T extends BaseEntity> extends EntityEvent<T> {
        public AfterSaveEvent(T entity) {
            super(entity);
        }
    }

    @Getter
    public static class BeforeUpdateEvent<T extends BaseEntity> extends EntityEvent<T> {
        public BeforeUpdateEvent(T entity) {
            super(entity);
        }
    }

    @Getter
    public static class AfterUpdateEvent<T extends BaseEntity> extends EntityEvent<T> {
        public AfterUpdateEvent(T entity) {
            super(entity);
        }
    }

    @Getter
    public static class BeforeDeleteEvent<T extends BaseEntity> extends EntityEvent<T> {
        public BeforeDeleteEvent(T entity) {
            super(entity);
        }
    }

    @Getter
    public static class AfterDeleteEvent<T extends BaseEntity> extends EntityEvent<T> {
        public AfterDeleteEvent(T entity) {
            super(entity);
        }
    }

    @Getter
    public static class VersionUpgradeEvent<T extends BaseEntity> extends EntityEvent<T> {
        private final String oldVersion;
        private final String newVersion;

        public VersionUpgradeEvent(T entity, String oldVersion, String newVersion) {
            super(entity);
            this.oldVersion = oldVersion;
            this.newVersion = newVersion;
        }
    }
}
```

## BaseMongoRepository.java

```java
package com.study.collect.core.storage.repository;

import com.study.collect.core.storage.entity.BaseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
public class BaseMongoRepository<T extends BaseEntity>
        extends SimpleMongoRepository<T, String> implements IRepository<T> {

    private final MongoOperations mongoOperations;
    private final MongoEntityInformation<T, String> entityInformation;

    public BaseMongoRepository(MongoEntityInformation<T, String> metadata,
                               MongoOperations mongoOperations) {
        super(metadata, mongoOperations);
        this.mongoOperations = mongoOperations;
        this.entityInformation = metadata;
    }

    @Override
    public T findByCode(String code) {
        Query query = Query.query(
                Criteria.where("code").is(code)
                        .and("deleted").is(false)
        );
        return mongoOperations.findOne(query, entityInformation.getJavaType());
    }

    @Override
    public Page<T> findByDeletedFalse(Pageable pageable) {
        Query query = Query.query(Criteria.where("deleted").is(false));
        return findAll(query, pageable);
    }

    @Override
    public List<T> findByVersionCodeGreaterThan(String versionCode) {
        Query query = Query.query(Criteria.where("versionCode").gt(versionCode));
        return mongoOperations.find(query, entityInformation.getJavaType());
    }

    @Override
    public void softDelete(String id) {
        Query query = Query.query(Criteria.where("id").is(id));
        Update update = Update.update("deleted", true)
                .set("updateTime", LocalDateTime.now());
        mongoOperations.updateFirst(query, update, entityInformation.getJavaType());
    }

    @Override
    public void softDelete(List<String> ids) {
        Query query = Query.query(Criteria.where("id").in(ids));
        Update update = Update.update("deleted", true)
                .set("updateTime", LocalDateTime.now());
        mongoOperations.updateMulti(query, update, entityInformation.getJavaType());
    }

    @Override
    public void updateStatus(String id, String status) {
        Query query = Query.query(Criteria.where("id").is(id));
        Update update = Update.update("status", status)
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

## IRepository.java

```java
package com.study.collect.core.storage.repository;

import com.study.collect.core.storage.entity.BaseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface IRepository<T extends BaseEntity> extends MongoRepository<T, String> {
    /**
     * 根据业务编码查询
     */
    T findByCode(String code);

    /**
     * 分页查询未删除的数据
     */
    Page<T> findByDeletedFalse(Pageable pageable);

    /**
     * 根据版本号查询数据
     */
    List<T> findByVersionCodeGreaterThan(String versionCode);

    /**
     * 软删除
     */
    void softDelete(String id);

    /**
     * 批量软删除
     */
    void softDelete(List<String> ids);

    /**
     * 更新状态
     */
    void updateStatus(String id, String status);
}
```

## VersionRepository.java

```java
package com.study.collect.core.storage.repository;

public class VersionRepository {
}

```

## CustomMongoRepositoryFactory.java

```java
package com.study.collect.core.storage.repository.factory;

import com.study.collect.core.storage.entity.BaseEntity;
import com.study.collect.core.storage.repository.BaseMongoRepository;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;

public class CustomMongoRepositoryFactory extends MongoRepositoryFactory {

    private final MongoOperations mongoOperations;

    public CustomMongoRepositoryFactory(MongoOperations mongoOperations) {
        super(mongoOperations);
        this.mongoOperations = mongoOperations;
    }

    @Override
    protected Object getTargetRepository(RepositoryInformation information) {
        Class<?> domainClass = information.getDomainType();
        if (!BaseEntity.class.isAssignableFrom(domainClass)) {
            throw new IllegalArgumentException("Domain class must extend BaseEntity");
        }

        @SuppressWarnings("unchecked")
        MongoEntityInformation<? extends BaseEntity, String> entityInformation =
                getEntityInformation((Class<? extends BaseEntity>) domainClass);

        return getTargetRepositoryViaReflection(information,
                entityInformation, mongoOperations);
    }

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        return BaseMongoRepository.class;
    }
}
```

## CustomMongoRepositoryFactoryBean.java

```java
package com.study.collect.core.storage.repository.factory;

import com.study.collect.core.storage.entity.BaseEntity;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactoryBean;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

public class CustomMongoRepositoryFactoryBean<T extends Repository<S, String>, S extends BaseEntity>
        extends MongoRepositoryFactoryBean<T, S, String> {

    public CustomMongoRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
        super(repositoryInterface);
    }

    @Override
    protected RepositoryFactorySupport getFactoryInstance(MongoOperations operations) {
        return new CustomMongoRepositoryFactory(operations);
    }
}
```

## package-info.java

```java
/**
 * 任务模块
 * 这个包包含与任务模型相关的类和接口。
 * 任务模型用于表示和管理应用程序中的各种任务。
 * 这些模型可能包括任务定义、调度信息和执行细节。
 */
package com.study.collect.core.task;
```

## MyBatisConfig.java

```java
package com.study.collect.core.task.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import javax.sql.DataSource;

@Configuration
@ConditionalOnProperty(prefix = "spring.datasource", name = "enabled", havingValue = "true", matchIfMissing = true)
@MapperScan("com.study.collect.core.task.mapper")
public class MyBatisConfig {

    @Bean
    public SqlSessionFactory sqlSessionFactory(@Qualifier("dataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);

        // 设置XML映射文件路径
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sessionFactory.setMapperLocations(resolver.getResources("classpath*:mapper/*.xml"));

        // 设置实体类别名包
        sessionFactory.setTypeAliasesPackage("com.study.collect.core.task.entity");

        return sessionFactory.getObject();
    }
}

```

## TaskConfiguration.java

```java
package com.study.collect.core.task.config;

import com.study.collect.core.task.definition.TaskProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Slf4j
@Configuration
@EnableScheduling
@EnableConfigurationProperties(TaskProperties.class)
public class TaskConfiguration {

    @Bean
    public ThreadPoolTaskScheduler taskScheduler(TaskProperties properties) {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();

        // 配置线程池核心参数
        scheduler.setPoolSize(properties.getThreadPool().getCoreSize());
        scheduler.setThreadNamePrefix("TaskScheduler-");

        // 配置优雅停机
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(properties.getThreadPool().getAwaitTerminationSeconds());

        // 配置异常处理
        scheduler.setErrorHandler(throwable ->
                log.error("Task execution error: {}", throwable.getMessage(), throwable)
        );

        // 配置任务拒绝处理
        scheduler.setRejectedExecutionHandler((runnable, executor) ->
                log.error("Task rejected: thread pool exhausted. Current pool size: {}",
                        executor.getPoolSize())
        );

        return scheduler;
    }
}
```

## TaskProperties.java

```java
package com.study.collect.core.task.definition;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "collect.task")
public class TaskProperties {

    /**
     * 是否启用任务调度
     */
    private boolean enabled = true;

    /**
     * 线程池配置
     */
    private ThreadPool threadPool = new ThreadPool();

    /**
     * 执行配置
     */
    private Execution execution = new Execution();

    @Data
    public static class ThreadPool {
        /**
         * 核心线程数
         */
        private int coreSize = 10;

        /**
         * 最大线程数
         */
        private int maxSize = 20;

        /**
         * 队列容量
         */
        private int queueCapacity = 200;

        /**
         * 线程空闲超时时间（秒）
         */
        private int keepAliveSeconds = 60;

        /**
         * 优雅停机等待时间（秒）
         */
        private int awaitTerminationSeconds = 60;
    }

    @Data
    public static class Execution {
        /**
         * 任务超时时间（秒）
         */
        private int timeout = 3600;

        /**
         * 重试次数
         */
        private int retryTimes = 3;

        /**
         * 重试间隔（秒）
         */
        private int retryInterval = 300;

        /**
         * 是否允许并行执行
         */
        private boolean allowParallel = true;
    }
}
```

## TaskConfig.java

```java
package com.study.collect.core.task.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TaskConfig {
    private Long id;
    private String taskCode;        // 任务编码
    private String taskName;        // 任务名称
    private String taskHandler;     // 任务处理器
    private String taskParam;       // 任务参数(JSON)
    private String cronExpr;        // cron表达式
    private Integer shardTotal;     // 分片总数
    private Integer retryTimes;     // 重试次数
    private Integer retryInterval;  // 重试间隔(秒)
    private Integer timeout;        // 超时时间(秒)
    private Integer status;         // 状态:0-禁用,1-启用
    private String remark;          // 备注
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 更新时间
}
```

## TaskInstance.java

```java
package com.study.collect.core.task.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TaskInstance {
    private Long id;
    private String instanceId;      // 实例ID
    private String taskCode;        // 任务编码
    private Integer shardIndex;     // 分片索引
    private Integer shardTotal;     // 分片总数
    private String shardParam;      // 分片参数
    private Integer status;         // 状态
    private String errorMsg;        // 错误信息
    private String hostName;        // 执行机器
    private LocalDateTime startTime;  // 开始时间
    private LocalDateTime endTime;    // 结束时间
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 更新时间
}
```

## TaskLog.java

```java
package com.study.collect.core.task.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TaskLog {
    private Long id;
    private String instanceId;      // 实例ID
    private String taskCode;        // 任务编码
    private Integer logType;        // 日志类型:1-开始,2-心跳,3-进度,4-结果,5-错误
    private String logContent;      // 日志内容
    private LocalDateTime createTime; // 创建时间

    public TaskLog() {
    }

    public TaskLog(String instanceId, String taskCode, Integer logType, String logContent) {
        this.instanceId = instanceId;
        this.taskCode = taskCode;
        this.logType = logType;
        this.logContent = logContent;
        this.createTime = LocalDateTime.now();
    }
}
```

## LogTypeEnum.java

```java
package com.study.collect.core.task.enums;

import lombok.Getter;

@Getter
public enum LogTypeEnum {
    START(1, "开始执行"),
    HEARTBEAT(2, "心跳检测"),
    PROGRESS(3, "执行进度"),
    RESULT(4, "执行结果"),
    ERROR(5, "执行错误");

    private final Integer code;
    private final String desc;

    LogTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
```

## TaskStatusEnum.java

```java
package com.study.collect.core.task.enums;

import lombok.Getter;

@Getter
public enum TaskStatusEnum {
    INIT(0, "初始化"),
    RUNNING(1, "执行中"),
    SUCCESS(2, "执行成功"),
    FAILED(3, "执行失败"),
    TIMEOUT(4, "执行超时"),
    CANCELED(5, "已取消");

    private final Integer code;
    private final String desc;

    TaskStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static TaskStatusEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (TaskStatusEnum status : TaskStatusEnum.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
```

## TaskValidationException.java

```java
package com.study.collect.core.task.exception;

public class TaskValidationException extends RuntimeException {

    public TaskValidationException(String message) {
        super(message);
    }

    public TaskValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

## AbstractTaskHandler.java

```java
package com.study.collect.core.task.handler;

import com.study.collect.core.task.model.TaskContext;
import com.study.collect.core.task.model.TaskResult;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractTaskHandler implements TaskHandler {

    @Override
    public TaskResult execute(TaskContext context) {
        String taskId = context.getTaskId();
        log.info("开始执行任务: taskId={}, type={}", taskId, getType());

        try {
            // 前置处理
            beforeExecute(context);

            // 执行任务
            Object result = doExecute(context);

            // 后置处理
            afterExecute(context, result);

            log.info("任务执行完成: taskId={}", taskId);
            return TaskResult.success(taskId, result);

        } catch (Exception e) {
            log.error("任务执行失败: taskId={}", taskId, e);
            return TaskResult.failure(taskId, e.getMessage());
        }
    }

    /**
     * 任务执行前的处理
     */
    protected void beforeExecute(TaskContext context) {
        // 子类可以覆盖实现
    }

    /**
     * 执行具体任务
     */
    protected abstract Object doExecute(TaskContext context);

    /**
     * 任务执行后的处理
     */
    protected void afterExecute(TaskContext context, Object result) {
        // 子类可以覆盖实现
    }
}
```

## SampleTaskHandler.java

```java
package com.study.collect.core.task.handler;

import com.study.collect.core.task.model.TaskContext;
import org.springframework.stereotype.Component;

@Component
public class SampleTaskHandler extends AbstractTaskHandler {

    @Override
    public String getType() {
        return "sample";
    }

    @Override
    protected Object doExecute(TaskContext context) {
        // 实现具体的任务处理逻辑
        return "Task executed successfully";
    }
}
```

## TaskHandler.java

```java
package com.study.collect.core.task.handler;

import com.study.collect.core.task.model.TaskContext;
import com.study.collect.core.task.model.TaskResult;

public interface TaskHandler {
    /**
     * 执行任务
     * @param context 任务上下文
     * @return 任务执行结果
     */
    TaskResult execute(TaskContext context);

    /**
     * 获取处理器类型
     * @return 处理器类型标识
     */
    String getType();
}
```

## TaskHandlerManager.java

```java
package com.study.collect.core.task.handler;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
//import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TaskHandlerManager {

    private final Map<String, TaskHandler> handlerMap = new HashMap<>();

    @Autowired
    private List<TaskHandler> handlers;

    @PostConstruct
    public void init() {
        handlers.forEach(handler -> handlerMap.put(handler.getType(), handler));
    }

    public TaskHandler getHandler(String type) {
        TaskHandler handler = handlerMap.get(type);
        if (handler == null) {
            throw new IllegalArgumentException("未找到任务处理器: " + type);
        }
        return handler;
    }
}
```

## TaskConfigMapper.java

```java
package com.study.collect.core.task.mapper;

import com.study.collect.core.task.entity.TaskConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface TaskConfigMapper {

    void insert(TaskConfig config);

    void update(TaskConfig config);

    TaskConfig selectById(@Param("id") Long id);

    TaskConfig selectByCode(@Param("taskCode") String taskCode);

    List<TaskConfig> selectEnabled();

    void updateStatus(@Param("taskCode") String taskCode, @Param("status") Integer status);
}
```

## TaskInstanceMapper.java

```java
package com.study.collect.core.task.mapper;

import com.study.collect.core.task.entity.TaskInstance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface TaskInstanceMapper {

    void insert(TaskInstance instance);

    void updateStatus(@Param("instanceId") String instanceId,
                      @Param("status") Integer status,
                      @Param("errorMsg") String errorMsg);

    void updateEndTime(@Param("instanceId") String instanceId,
                       @Param("endTime") LocalDateTime endTime);

    TaskInstance selectById(@Param("id") Long id);

    TaskInstance selectByInstanceId(@Param("instanceId") String instanceId);

    List<TaskInstance> selectRunning();

    List<TaskInstance> selectByTaskCode(@Param("taskCode") String taskCode,
                                        @Param("startTime") LocalDateTime startTime,
                                        @Param("endTime") LocalDateTime endTime);
}
```

## TaskLogMapper.java

```java
package com.study.collect.core.task.mapper;

import com.study.collect.core.task.entity.TaskLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface TaskLogMapper {

    void insert(TaskLog log);

    void batchInsert(@Param("logs") List<TaskLog> logs);

    List<TaskLog> selectByInstanceId(@Param("instanceId") String instanceId);

    List<TaskLog> selectByTaskCode(@Param("taskCode") String taskCode,
                                   @Param("logType") Integer logType,
                                   @Param("limit") Integer limit);
}
```

## package-info.java

```java
/**
 * 任务模型层
 */
package com.study.collect.core.task.model;
```

## ShardingConfig.java

```java
package com.study.collect.core.task.model;

import lombok.Data;
import java.io.Serializable;

@Data
public class ShardingConfig implements Serializable {
    /**
     * 是否启用分片
     */
    private boolean enabled = false;

    /**
     * 分片总数
     */
    private Integer total = 1;

    /**
     * 分片策略
     */
    private String strategy;

    /**
     * 分片参数
     */
    private String parameter;

    /**
     * 是否允许分片并行执行
     */
    private boolean parallel = true;

    /**
     * 分片超时时间（秒）
     */
    private Integer timeout;

    /**
     * 分片失败处理策略
     * CONTINUE: 继续执行其他分片
     * STOP: 停止所有分片执行
     */
    private String failureStrategy = "CONTINUE";

    /**
     * 验证分片配置
     */
    public void validate() {
        if (enabled) {
            if (total == null || total < 1) {
                throw new IllegalArgumentException("分片总数必须大于0");
            }
            if (timeout != null && timeout < 0) {
                throw new IllegalArgumentException("分片超时时间不能小于0");
            }
        }
    }

    /**
     * 创建默认配置
     */
    public static ShardingConfig createDefault() {
        ShardingConfig config = new ShardingConfig();
        config.setEnabled(false);
        config.setTotal(1);
        config.setStrategy("AVERAGE");
        return config;
    }
}
```

## TaskContext.java

```java
package com.study.collect.core.task.model;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
public class TaskContext {
    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 任务实例ID
     */
    private String instanceId;

    /**
     * 分片索引，从0开始
     */
    private Integer shardIndex;

    /**
     * 分片总数
     */
    private Integer shardTotal;

    /**
     * 分片参数，JSON格式
     */
    private String shardParam;

    /**
     * 执行开始时间
     */
    private LocalDateTime startTime;

    /**
     * 执行超时时间（秒）
     */
    private Integer timeout;

    /**
     * 执行机器
     */
    private String hostName;

    /**
     * 上下文参数，用于在执行过程中传递数据
     */
    private Map<String, Object> parameters;

    public TaskContext() {
        this.startTime = LocalDateTime.now();
        this.parameters = new HashMap<>();
    }

    /**
     * 设置上下文参数
     */
    public void setParameter(String key, Object value) {
        this.parameters.put(key, value);
    }

    /**
     * 获取上下文参数
     */
    @SuppressWarnings("unchecked")
    public <T> T getParameter(String key) {
        return (T) this.parameters.get(key);
    }

    /**
     * 移除上下文参数
     */
    public void removeParameter(String key) {
        this.parameters.remove(key);
    }

    /**
     * 清空所有上下文参数
     */
    public void clearParameters() {
        this.parameters.clear();
    }
}
```

## TaskDefinition.java

```java
package com.study.collect.core.task.model;

// 任务定义


import lombok.Data;

import java.util.Map;

@Data
public class TaskDefinition {
    private String taskId;           // 任务ID
    private String taskName;         // 任务名称
    private String taskHandler;      // 任务处理器
    private String cronExpression;   // 调度表达式
    private ShardingConfig sharding; // 分片配置
    private Map<String, Object> props;// 扩展属性
}

```

## TaskResult.java

```java
package com.study.collect.core.task.model;

// 任务结果

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskResult {
    private String taskId;          // 任务ID
    private Boolean success;        // 执行结果
    private String errorMessage;    // 错误信息
    private Object data;           // 结果数据
    private LocalDateTime finishTime; // 完成时间

    public static TaskResult success(String taskId, Object data) {
        TaskResult result = new TaskResult();
        result.setTaskId(taskId);
        result.setSuccess(true);
        result.setData(data);
        result.setFinishTime(LocalDateTime.now());
        return result;
    }

    public static TaskResult failure(String taskId, String errorMessage) {
        TaskResult result = new TaskResult();
        result.setTaskId(taskId);
        result.setSuccess(false);
        result.setErrorMessage(errorMessage);
        result.setFinishTime(LocalDateTime.now());
        return result;
    }
}


```

## TaskStatus.java

```java
package com.study.collect.core.task.model;

// 任务状态枚举
public enum TaskStatus {
    CREATED("已创建"),
    WAITING("等待中"),
    RUNNING("执行中"),
    SUCCESS("执行成功"),
    FAILED("执行失败"),
    CANCELED("已取消"),
    TIMEOUT("已超时");

    private final String description;

    TaskStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
```

## AbstractTaskScheduler.java

```java
package com.study.collect.core.task.scheduler;


import com.study.collect.core.task.entity.TaskConfig;
import com.study.collect.core.task.model.TaskDefinition;
import com.study.collect.core.task.service.TaskConfigService;
import com.study.collect.core.task.service.TaskExecuteService;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Slf4j
public abstract class AbstractTaskScheduler implements TaskScheduler {

    protected final TaskConfigService taskConfigService;
    protected final TaskExecuteService taskExecuteService;
    protected final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    protected AbstractTaskScheduler(TaskConfigService taskConfigService, TaskExecuteService taskExecuteService) {
        this.taskConfigService = taskConfigService;
        this.taskExecuteService = taskExecuteService;
    }

    @Override
    public void start() {
        log.info("Starting task scheduler...");
        doStart();
    }

    @Override
    public void stop() {
        log.info("Stopping task scheduler...");
        scheduledTasks.values().forEach(future -> future.cancel(true));
        scheduledTasks.clear();
        doStop();
    }

    @Override
    public void addTask(TaskDefinition task) {
        TaskConfig config = convertToConfig(task);
        taskConfigService.saveTaskConfig(config);
        doAddTask(task);
    }

    @Override
    public void removeTask(String taskId) {
        ScheduledFuture<?> future = scheduledTasks.remove(taskId);
        if (future != null) {
            future.cancel(true);
        }
        doRemoveTask(taskId);
    }

    protected abstract void doStart();
    protected abstract void doStop();
    protected abstract void doAddTask(TaskDefinition task);
    protected abstract void doRemoveTask(String taskId);

    // 提供任务配置转换方法
    protected TaskConfig convertToConfig(TaskDefinition task) {
        TaskConfig config = new TaskConfig();
        config.setTaskCode(task.getTaskId());
        config.setTaskName(task.getTaskName());
        config.setTaskHandler(task.getTaskHandler());
        config.setCronExpr(task.getCronExpression());
        config.setShardTotal(task.getSharding() != null ? task.getSharding().getTotal() : 1);
        return config;
    }
}
```

## DefaultTaskScheduler.java

```java
package com.study.collect.core.task.scheduler;

import com.study.collect.core.task.entity.TaskConfig;
import com.study.collect.core.task.entity.TaskInstance;
import com.study.collect.core.task.model.TaskDefinition;
import com.study.collect.core.task.service.TaskConfigService;
import com.study.collect.core.task.service.TaskExecuteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Component
public class DefaultTaskScheduler extends AbstractTaskScheduler {

    private final TaskScheduler scheduler;
    private final TaskDispatcher taskDispatcher;
    private volatile boolean running = false;

    public DefaultTaskScheduler(TaskConfigService taskConfigService,
                                TaskExecuteService taskExecuteService,
                                TaskScheduler scheduler,
                                TaskDispatcher taskDispatcher) {
        super(taskConfigService, taskExecuteService);
        this.scheduler = scheduler;
        this.taskDispatcher = taskDispatcher;
    }

    @Override
    protected void doStart() {
        if (running) {
            return;
        }
        running = true;

        // 加载所有可用的任务配置并调度
        taskConfigService.getEnabledTaskConfigs().forEach(config -> {
            try {
                TaskDefinition task = convertToDefinition(config);
                scheduleTask(task);
                log.info("Loaded task from config: {}", config.getTaskCode());
            } catch (Exception e) {
                log.error("Failed to load task: {}", config.getTaskCode(), e);
            }
        });

        // 启动定时任务状态检查
        scheduler.scheduleWithFixedDelay(
                this::checkRunningTasks,
                Duration.ofMinutes(1)
        );

        log.info("Task scheduler started successfully");
    }

    @Override
    protected void doStop() {
        if (!running) {
            return;
        }
        running = false;

        // 停止所有运行中的任务
        taskExecuteService.getRunningTasks().forEach(instance -> {
            try {
                taskExecuteService.completeTaskInstance(
                        instance.getInstanceId(),
                        false,
                        "Scheduler stopped"
                );
                log.info("Stopped running task: {}", instance.getInstanceId());
            } catch (Exception e) {
                log.error("Failed to stop task: {}", instance.getInstanceId(), e);
            }
        });

        log.info("Task scheduler stopped successfully");
    }

    @Override
    protected void doAddTask(TaskDefinition task) {
        validateTask(task);
        scheduleTask(task);
        log.info("Added new task: {}", task.getTaskId());
    }

    @Override
    protected void doRemoveTask(String taskId) {
        taskConfigService.disableTask(taskId);
        log.info("Removed task: {}", taskId);
    }

    @Override
    public void pauseTask(String taskId) {
        ScheduledFuture<?> future = scheduledTasks.get(taskId);
        if (future != null) {
            future.cancel(false);
            taskConfigService.disableTask(taskId);
            log.info("Paused task: {}", taskId);
        }
    }

    @Override
    public void resumeTask(String taskId) {
        TaskConfig config = taskConfigService.getTaskConfig(taskId);
        if (config != null) {
            taskConfigService.enableTask(taskId);
            scheduleTask(convertToDefinition(config));
            log.info("Resumed task: {}", taskId);
        }
    }

    private void scheduleTask(TaskDefinition task) {
        // 验证cron表达式
        if (!StringUtils.hasText(task.getCronExpression())) {
            log.warn("Task {} has no cron expression, skipped scheduling", task.getTaskId());
            return;
        }

        try {
            ScheduledFuture<?> future = scheduler.schedule(
                    () -> executeTask(task),
                    new CronTrigger(task.getCronExpression())
            );

            // 如果任务已存在，取消旧的调度
            ScheduledFuture<?> existing = scheduledTasks.put(task.getTaskId(), future);
            if (existing != null) {
                existing.cancel(true);
                log.info("Replaced existing schedule for task: {}", task.getTaskId());
            }

            log.info("Scheduled task: {}, cron: {}", task.getTaskId(), task.getCronExpression());

        } catch (Exception e) {
            log.error("Failed to schedule task: {}", task.getTaskId(), e);
            throw new RuntimeException("Failed to schedule task", e);
        }
    }

    private void executeTask(TaskDefinition task) {
        try {
            log.info("Starting task execution: {}", task.getTaskId());

            // 获取分片配置
            int shardTotal = task.getSharding() != null ? task.getSharding().getTotal() : 1;

            // 创建并分发分片任务
            for (int shardIndex = 0; shardIndex < shardTotal; shardIndex++) {
                String shardParam = createShardParam(shardIndex, shardTotal);
                TaskInstance instance = taskExecuteService.createTaskInstance(
                        task.getTaskId(),
                        shardIndex,
                        shardParam
                );

                taskDispatcher.dispatch(instance);
                log.info("Dispatched task instance: {} - shard {}/{}",
                        instance.getInstanceId(), shardIndex + 1, shardTotal);
            }

        } catch (Exception e) {
            log.error("Task execution failed: {}", task.getTaskId(), e);
        }
    }

    @Scheduled(fixedDelay = 60000)  // 每分钟检查一次
    private void checkRunningTasks() {
        if (!running) {
            return;
        }

        try {
            taskExecuteService.getRunningTasks().forEach(instance -> {
                TaskConfig config = taskConfigService.getTaskConfig(instance.getTaskCode());
                if (config != null && config.getTimeout() > 0) {
                    checkTaskTimeout(instance, config.getTimeout());
                }
            });
        } catch (Exception e) {
            log.error("Failed to check running tasks", e);
        }
    }

    private void checkTaskTimeout(TaskInstance instance, int timeoutSeconds) {
        if (instance.getStartTime() == null) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        if (instance.getStartTime().plusSeconds(timeoutSeconds).isBefore(now)) {
            try {
                taskExecuteService.completeTaskInstance(
                        instance.getInstanceId(),
                        false,
                        "Task execution timed out after " + timeoutSeconds + " seconds"
                );
                log.warn("Task instance timed out: {}", instance.getInstanceId());
            } catch (Exception e) {
                log.error("Failed to handle task timeout: {}", instance.getInstanceId(), e);
            }
        }
    }

    private String createShardParam(int shardIndex, int shardTotal) {
        return String.format("{\"shardIndex\":%d,\"shardTotal\":%d,\"uuid\":\"%s\"}",
                shardIndex, shardTotal, UUID.randomUUID().toString());
    }

    private TaskDefinition convertToDefinition(TaskConfig config) {
        TaskDefinition task = new TaskDefinition();
        task.setTaskId(config.getTaskCode());
        task.setTaskName(config.getTaskName());
        task.setTaskHandler(config.getTaskHandler());
        task.setCronExpression(config.getCronExpr());
        // 设置其他属性...
        return task;
    }

    private void validateTask(TaskDefinition task) {
        if (!StringUtils.hasText(task.getTaskId())) {
            throw new IllegalArgumentException("Task ID cannot be empty");
        }
        if (!StringUtils.hasText(task.getTaskHandler())) {
            throw new IllegalArgumentException("Task handler cannot be empty");
        }
        if (StringUtils.hasText(task.getCronExpression())) {
            try {
                new CronTrigger(task.getCronExpression());
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid cron expression: " + task.getCronExpression());
            }
        }
    }
}
```

## package-info.java

```java
/**
 * 调度层
 */
package com.study.collect.core.task.scheduler;
```

## TaskDispatcher.java

```java
package com.study.collect.core.task.scheduler;

import com.study.collect.core.task.enums.TaskStatusEnum;
import com.study.collect.core.mq.message.TaskMessage;
import com.study.collect.core.mq.producer.TaskProducer;
import com.study.collect.core.task.entity.TaskInstance;
import com.study.collect.core.task.mapper.TaskInstanceMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TaskDispatcher {

    private final TaskProducer taskProducer;
    private final TaskInstanceMapper taskInstanceMapper;

    @Autowired
    public TaskDispatcher(TaskProducer taskProducer, TaskInstanceMapper taskInstanceMapper) {
        this.taskProducer = taskProducer;
        this.taskInstanceMapper = taskInstanceMapper;
    }

    public void dispatch(TaskInstance instance) {
        try {
            // 更新任务状态为执行中
            updateTaskStatus(instance.getInstanceId(), TaskStatusEnum.RUNNING, null);

            // 转换并发送消息
            TaskMessage message = convertToMessage(instance);
            taskProducer.sendTask(message);

            log.info("Task dispatched successfully: instanceId={}, taskCode={}, shard={}/{}",
                    instance.getInstanceId(),
                    instance.getTaskCode(),
                    instance.getShardIndex() + 1,
                    instance.getShardTotal()
            );

        } catch (Exception e) {
            log.error("Failed to dispatch task: " + instance.getInstanceId(), e);

            // 更新任务状态为失败
            updateTaskStatus(instance.getInstanceId(), TaskStatusEnum.FAILED, e.getMessage());
            throw new RuntimeException("Task dispatch failed", e);
        }
    }

    private void updateTaskStatus(String instanceId, TaskStatusEnum status, String errorMsg) {
        taskInstanceMapper.updateStatus(instanceId, status.getCode(), errorMsg);
    }

    private TaskMessage convertToMessage(TaskInstance instance) {
        TaskMessage message = new TaskMessage();
        message.setTaskId(instance.getTaskCode());
        message.setInstanceId(instance.getInstanceId());
        message.setShardIndex(instance.getShardIndex());
        message.setShardTotal(instance.getShardTotal());
        message.setShardParam(instance.getShardParam());
        message.setHostName(instance.getHostName());
        return message;
    }
}
```

## TaskScheduler.java

```java
package com.study.collect.core.task.scheduler;

// 调度器接口


import com.study.collect.core.task.model.TaskDefinition;

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
     * 添加任务
     */
    void addTask(TaskDefinition task);

    /**
     * 移除任务
     */
    void removeTask(String taskId);

    /**
     * 暂停任务
     */
    void pauseTask(String taskId);

    /**
     * 恢复任务
     */
    void resumeTask(String taskId);
}

```

## TaskConfigService.java

```java
package com.study.collect.core.task.service;

import com.study.collect.core.task.enums.TaskStatusEnum;
import com.study.collect.core.task.entity.TaskConfig;
import com.study.collect.core.task.mapper.TaskConfigMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskConfigService {

    private final TaskConfigMapper taskConfigMapper;

    @Transactional(rollbackFor = Exception.class)
    public void saveTaskConfig(TaskConfig config) {
        TaskConfig existConfig = taskConfigMapper.selectByCode(config.getTaskCode());
        if (existConfig == null) {
            log.info("新增任务配置: {}", config.getTaskCode());
            taskConfigMapper.insert(config);
        } else {
            log.info("更新任务配置: {}", config.getTaskCode());
            taskConfigMapper.update(config);
        }
    }

    public TaskConfig getTaskConfig(String taskCode) {
        return taskConfigMapper.selectByCode(taskCode);
    }

    public List<TaskConfig> getEnabledTaskConfigs() {
        return taskConfigMapper.selectEnabled();
    }

    @Transactional(rollbackFor = Exception.class)
    public void enableTask(String taskCode) {
        taskConfigMapper.updateStatus(taskCode, TaskStatusEnum.RUNNING.getCode());
        log.info("启用任务: {}", taskCode);
    }

    @Transactional(rollbackFor = Exception.class)
    public void disableTask(String taskCode) {
        taskConfigMapper.updateStatus(taskCode, TaskStatusEnum.INIT.getCode());
        log.info("禁用任务: {}", taskCode);
    }

    public void validateTaskConfig(TaskConfig config) {
        if (config.getShardTotal() == null || config.getShardTotal() < 1) {
            config.setShardTotal(1);
        }
        if (config.getRetryTimes() == null) {
            config.setRetryTimes(0);
        }
        if (config.getRetryInterval() == null) {
            config.setRetryInterval(0);
        }
        if (config.getTimeout() == null) {
            config.setTimeout(0);
        }
        if (config.getStatus() == null) {
            config.setStatus(TaskStatusEnum.INIT.getCode());
        }
    }
}
```

## TaskExecuteService.java

```java
package com.study.collect.core.task.service;

import com.study.collect.core.task.enums.LogTypeEnum;
import com.study.collect.core.task.enums.TaskStatusEnum;
import com.study.collect.core.task.utils.InstanceIdGenerator;
import com.study.collect.core.task.entity.TaskConfig;
import com.study.collect.core.task.entity.TaskInstance;
import com.study.collect.core.task.entity.TaskLog;
import com.study.collect.core.task.mapper.TaskInstanceMapper;
import com.study.collect.core.task.mapper.TaskLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskExecuteService {

    private final TaskInstanceMapper taskInstanceMapper;
    private final TaskLogMapper taskLogMapper;
    private final TaskConfigService taskConfigService;

    @Transactional(rollbackFor = Exception.class)
    public TaskInstance createTaskInstance(String taskCode, Integer shardIndex, String shardParam) {
        TaskConfig config = taskConfigService.getTaskConfig(taskCode);
        if (config == null) {
            throw new IllegalArgumentException("任务配置不存在: " + taskCode);
        }

        String instanceId = InstanceIdGenerator.generateInstanceId(taskCode);
        TaskInstance instance = new TaskInstance();
        instance.setInstanceId(instanceId);
        instance.setTaskCode(taskCode);
        instance.setShardIndex(shardIndex);
        instance.setShardTotal(config.getShardTotal());
        instance.setShardParam(shardParam);
        instance.setStatus(TaskStatusEnum.INIT.getCode());
        instance.setHostName(getHostName());
        instance.setStartTime(LocalDateTime.now());

        taskInstanceMapper.insert(instance);
        recordTaskLog(instanceId, taskCode, LogTypeEnum.START, "任务开始执行");

        return instance;
    }

    @Transactional(rollbackFor = Exception.class)
    public void completeTaskInstance(String instanceId, boolean success, String errorMsg) {
        TaskInstance instance = taskInstanceMapper.selectByInstanceId(instanceId);
        if (instance == null) {
            throw new IllegalArgumentException("任务实例不存在: " + instanceId);
        }

        LocalDateTime endTime = LocalDateTime.now();
        TaskStatusEnum status = success ? TaskStatusEnum.SUCCESS : TaskStatusEnum.FAILED;

        taskInstanceMapper.updateStatus(instanceId, status.getCode(), errorMsg);
        taskInstanceMapper.updateEndTime(instanceId, endTime);

        LogTypeEnum logType = success ? LogTypeEnum.RESULT : LogTypeEnum.ERROR;
        String logContent = success ? "任务执行成功" : "任务执行失败: " + errorMsg;
        recordTaskLog(instanceId, instance.getTaskCode(), logType, logContent);
    }

    public void recordTaskProgress(String instanceId, String progressInfo) {
        TaskInstance instance = taskInstanceMapper.selectByInstanceId(instanceId);
        if (instance != null) {
            recordTaskLog(instanceId, instance.getTaskCode(), LogTypeEnum.PROGRESS, progressInfo);
        }
    }

    private void recordTaskLog(String instanceId, String taskCode, LogTypeEnum logType, String content) {
        TaskLog log = new TaskLog(instanceId, taskCode, logType.getCode(), content);
        taskLogMapper.insert(log);
    }

    public List<TaskInstance> getRunningTasks() {
        return taskInstanceMapper.selectRunning();
    }

    public List<TaskLog> getTaskLogs(String instanceId) {
        return taskLogMapper.selectByInstanceId(instanceId);
    }

    private String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "unknown";
        }
    }
}
```

## InstanceIdGenerator.java

```java
package com.study.collect.core.task.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public class InstanceIdGenerator {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final AtomicInteger SEQUENCE = new AtomicInteger(0);

    public static String generateInstanceId(String taskCode) {
        // 重置序号,避免无限增长
        if (SEQUENCE.get() > 9999) {
            SEQUENCE.set(0);
        }

        // 格式：taskCode_yyyyMMddHHmmss_XXXX
        return String.format("%s_%s_%04d",
                taskCode,
                LocalDateTime.now().format(FORMATTER),
                SEQUENCE.getAndIncrement());
    }
}
```

## TaskConfigMapper.xml

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.study.collect.core.task.mapper.TaskConfigMapper">

    <insert id="insert" parameterType="TaskConfig" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO task_config (
            task_code, task_name, task_handler, task_param,
            cron_expr, shard_total, retry_times, retry_interval,
            timeout, status, remark
        ) VALUES (
                     #{taskCode}, #{taskName}, #{taskHandler}, #{taskParam},
                     #{cronExpr}, #{shardTotal}, #{retryTimes}, #{retryInterval},
                     #{timeout}, #{status}, #{remark}
                 )
    </insert>

    <update id="update" parameterType="TaskConfig">
        UPDATE task_config
        SET task_name = #{taskName},
            task_handler = #{taskHandler},
            task_param = #{taskParam},
            cron_expr = #{cronExpr},
            shard_total = #{shardTotal},
            retry_times = #{retryTimes},
            retry_interval = #{retryInterval},
            timeout = #{timeout},
            status = #{status},
            remark = #{remark}
        WHERE task_code = #{taskCode}
    </update>

    <select id="selectById" resultType="TaskConfig">
        SELECT * FROM task_config WHERE id = #{id}
    </select>

    <select id="selectByCode" resultType="TaskConfig">
        SELECT * FROM task_config WHERE task_code = #{taskCode}
    </select>

    <select id="selectEnabled" resultType="TaskConfig">
        SELECT * FROM task_config WHERE status = 1
    </select>
</mapper>
```

## TaskInstanceMapper.xml

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.study.collect.core.task.mapper.TaskInstanceMapper">

    <insert id="insert" parameterType="TaskInstance" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO task_instance (
            instance_id, task_code, shard_index, shard_total,
            shard_param, status, host_name, start_time
        ) VALUES (
                     #{instanceId}, #{taskCode}, #{shardIndex}, #{shardTotal},
                     #{shardParam}, #{status}, #{hostName}, #{startTime}
                 )
    </insert>

    <update id="updateStatus">
        UPDATE task_instance
        SET status = #{status},
            error_msg = #{errorMsg},
            update_time = CURRENT_TIMESTAMP
        WHERE instance_id = #{instanceId}
    </update>

    <update id="updateEndTime">
        UPDATE task_instance
        SET end_time = #{endTime},
            update_time = CURRENT_TIMESTAMP
        WHERE instance_id = #{instanceId}
    </update>

    <select id="selectRunning" resultType="TaskInstance">
        SELECT * FROM task_instance WHERE status = 1
    </select>
</mapper>
```

## TaskLogMapper.xml

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.study.collect.core.task.mapper.TaskLogMapper">

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

    <select id="selectByInstanceId" resultType="TaskLog">
        SELECT * FROM task_log
        WHERE instance_id = #{instanceId}
        ORDER BY create_time ASC
    </select>
</mapper>
```

## pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xmlns="http://maven.apache.org/POM/4.0.0"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.study</groupId>
        <artifactId>platform-collect</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <artifactId>collect-starter</artifactId>

    <dependencies>
        <!-- 业务模块依赖 -->
        <dependency>
            <groupId>com.study</groupId>
            <artifactId>business-enterprise</artifactId>
            <version>${project.version}</version>
        </dependency>
<!--        <dependency>-->
<!--            <groupId>com.study</groupId>-->
<!--            <artifactId>business-finance</artifactId>-->
<!--            <version>${project.version}</version>-->
<!--        </dependency>-->
<!--        <dependency>-->
<!--            <groupId>com.study</groupId>-->
<!--            <artifactId>business-medical</artifactId>-->
<!--            <version>${project.version}</version>-->
<!--        </dependency>-->

        <!-- Spring Boot Starters -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>

        <!-- Documentation -->
        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
            <version>2.5.0</version>
        </dependency>

        <!-- Monitoring -->
        <dependency>
            <groupId>io.micrometer</groupId>
            <artifactId>micrometer-registry-prometheus</artifactId>
        </dependency>

        <!-- Logging -->
        <dependency>
            <groupId>net.logstash.logback</groupId>
            <artifactId>logstash-logback-encoder</artifactId>
            <version>7.4</version>
        </dependency>

        <!-- Test -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <mainClass>com.study.collect.CollectApplication</mainClass>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
                <executions>
                    <execution>
                        <goals>
                            <goal>repackage</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

## CollectApplication.java

```java
package com.study.collect;

import com.study.collect.business.enterprise.config.EnterpriseCollectorProperties;
import com.study.collect.core.task.scheduler.TaskScheduler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;


@SpringBootApplication
@EnableScheduling
@EnableAsync
@EnableCaching
@EnableConfigurationProperties({
        EnterpriseCollectorProperties.class
})
public class CollectApplication {
    public static void main(String[] args) {
        SpringApplication.run(CollectApplication.class, args);
    }

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10);
        scheduler.setThreadNamePrefix("TaskScheduler-");
        return scheduler;
    }
}
```

## application-dev.yml

```yaml
spring:
# 开发环境数据源
datasource:
url: jdbc:mysql://localhost:3306/collect_dev?useUnicode=true&characterEncoding=utf8
username: dev
password: dev123

# 开发环境Redis
redis:
host: localhost
port: 6379
database: 1

# 开发环境MongoDB
data:
mongodb:
uri: mongodb://localhost:27017/collect_dev

logging:
level:
com.study.collect: debug
```

## application-prod.yml

```yaml
spring:
# 生产环境数据源
datasource:
url: jdbc:mysql://prod-mysql:3306/collect?useUnicode=true&characterEncoding=utf8
username: prod
password: ${MYSQL_PASSWORD}

# 生产环境Redis集群
redis:
cluster:
nodes:
  - redis-1:6379
  - redis-2:6379
  - redis-3:6379
password: ${REDIS_PASSWORD}

# 生产环境MongoDB副本集
data:
mongodb:
uri: mongodb://mongo-1:27017,mongo-2:27017,mongo-3:27017/collect?replicaSet=rs0

# 生产环境RabbitMQ集群
rabbitmq:
addresses: rabbitmq-1:5672,rabbitmq-2:5672,rabbitmq-3:5672
username: prod
password: ${RABBITMQ_PASSWORD}

# 生产环境日志
logging:
level:
root: warn
com.study.collect: info
file:
name: /var/log/collect/collect.log
```

## application.yml

```yaml
server:
  port: 8080

spring:
  application:
    name: platform-collect

  # 数据源配置
  datasource:
    driver-class-name: org.mariadb.jdbc.Driver
    url: jdbc:mariadb://192.168.80.137:3306/test?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai
    username: root
    password: 123456
  # RabbitMQ配置
  rabbitmq:
    host: 192.168.80.137
    port: 5672
    username: admin
    password: 123456
  data:
    redis:
      # 通用配置
      password: 123456
      timeout: 5000
      # 集群配置（如果使用集群模式，则注释掉host和port）
      cluster:
        nodes:
          - 192.168.80.137:6379
          - 192.168.80.137:6380
          - 192.168.80.137:6381
          - 192.168.80.137:6382
          - 192.168.80.137:6383
          - 192.168.80.137:6384
      # 连接池配置
      lettuce:
        pool:
          max-active: 8  # 连接池最大连接数
          max-idle: 8    # 连接池最大空闲连接数
          min-idle: 0    # 连接池最小空闲连接数
          max-wait: 1000 # 连接池最大阻塞等待时间（使用负值表示没有限制）


      # 单机配置（如果使用集群模式，则注释掉这部分）
    #      host: 192.168.80.137
    #      port: 6379
    mongodb:
      uri: mongodb://root:123456@192.168.80.137:27017
      database: crawler
      auto-index-creation: true


# 监控端点配置
management:
  endpoints:
    web:
      exposure:
        include: "*"
  endpoint:
    health:
      show-details: always

# 日志配置
logging:
  level:
    com.study.collect: info
  file:
    name: logs/collect.log

#collect:
#  task:
#    enabled: true  # 是否启用任务
#    tasks:
#      - taskId: "enterprise-collect"
#        taskName: "企业数据采集"
#        taskHandler: "enterpriseCollectHandler"
#        cronExpression: "0 0 1 * * ?"
#        props:
#          collectType: "enterprise"
#          batchSize: 100
#collect:
#  task:
#    enabled: true
#    tasks:
#      - taskId: "enterprise-collect"
#        taskName: "企业数据采集"
#        taskHandler: "enterpriseCollectHandler"
#        cronExpression: "0 0 1 * * ?"
#        sharding:
#          enabled: true
#          total: 4
#  mq:
#    rabbit:
#      enabled: true
#      host: 192.168.80.137
#      port: 5672
#      username: admin
#      password: 123456
#
#      # 任务队列配置
#      task:
#        exchange: collect.task
#        queue: collect.task.queue
#        routing-key: collect.task
#
#      # 结果队列配置
#      result:
#        exchange: collect.result
#        queue: collect.result.queue
#        routing-key: collect.result
#
#      # 分片配置
#      sharding:
#        enabled: true
#        total: 4      # 分片总数

collect:
  task:
    enabled: true
    thread-pool:
      core-size: 10
      max-size: 20
      queue-capacity: 200
    execution:
      timeout: 3600
      retry-times: 3
      retry-interval: 300
```

## sql.sql

```sql
-- 任务配置表
CREATE TABLE `task_config` (
                               `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                               `task_code` varchar(50) NOT NULL COMMENT '任务编码',
                               `task_name` varchar(100) NOT NULL COMMENT '任务名称',
                               `task_handler` varchar(100) NOT NULL COMMENT '任务处理器',
                               `task_param` text COMMENT '任务参数(JSON格式)',
                               `cron_expr` varchar(50) DEFAULT NULL COMMENT 'cron表达式',
                               `shard_total` int DEFAULT '1' COMMENT '分片总数',
                               `retry_times` int DEFAULT '0' COMMENT '重试次数',
                               `retry_interval` int DEFAULT '0' COMMENT '重试间隔(秒)',
                               `timeout` int DEFAULT '0' COMMENT '超时时间(秒)',
                               `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态:0-禁用,1-启用',
                               `remark` varchar(500) DEFAULT NULL COMMENT '备注',
                               `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                               PRIMARY KEY (`id`),
                               UNIQUE KEY `uk_task_code` (`task_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务配置表';

-- 任务实例表
CREATE TABLE `task_instance` (
                                 `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                 `instance_id` varchar(50) NOT NULL COMMENT '实例ID',
                                 `task_code` varchar(50) NOT NULL COMMENT '任务编码',
                                 `shard_index` int DEFAULT NULL COMMENT '分片索引',
                                 `shard_total` int DEFAULT NULL COMMENT '分片总数',
                                 `shard_param` text COMMENT '分片参数',
                                 `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态:0-初始,1-执行中,2-成功,3-失败',
                                 `error_msg` text COMMENT '错误信息',
                                 `host_name` varchar(100) DEFAULT NULL COMMENT '执行机器',
                                 `start_time` datetime DEFAULT NULL COMMENT '开始时间',
                                 `end_time` datetime DEFAULT NULL COMMENT '结束时间',
                                 `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                 `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                 PRIMARY KEY (`id`),
                                 UNIQUE KEY `uk_instance_id` (`instance_id`),
                                 KEY `idx_task_code` (`task_code`),
                                 KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务实例表';

-- 任务日志表
CREATE TABLE `task_log` (
                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                            `instance_id` varchar(50) NOT NULL COMMENT '实例ID',
                            `task_code` varchar(50) NOT NULL COMMENT '任务编码',
                            `log_type` tinyint NOT NULL COMMENT '日志类型:1-开始,2-心跳,3-进度,4-结果,5-错误',
                            `log_content` text COMMENT '日志内容',
                            `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            PRIMARY KEY (`id`),
                            KEY `idx_instance_id` (`instance_id`),
                            KEY `idx_task_code` (`task_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务日志表';
```

## 任务.md

```markdown

```

