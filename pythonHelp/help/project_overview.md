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
                                            controller/
                                                EnterpriseController.java
                                            model/
                                                Enterprise.java
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
                                    annotation/
                                        Collector.java
                                        Processor.java
                                        Repository.java
                                    cache/
                                        CacheMetrics.java
                                        annotation/
                                            Cache.java
                                            CacheEvict.java
                                            CacheLock.java
                                        config/
                                            RedisConfig.java
                                        handler/
                                            CacheAspect.java
                                            LockAspect.java
                                        lock/
                                            DistributedLock.java
                                            RedisLock.java
                                        manager/
                                            CacheManager.java
                                            RedisCacheManager.java
                                    collector/
                                        AbstractCollector.java
                                        ICollector.java
                                    config/
                                        CollectAutoConfiguration.java
                                        MongoRepositoryConfig.java
                                        RabbitConfiguration.java
                                        RedisConfiguration.java
                                    constant/
                                    mq/
                                        config/
                                            RabbitConfig.java
                                        consumer/
                                            ResultConsumer.java
                                            TaskConsumer.java
                                        message/
                                            ResultMessage.java
                                            TaskMessage.java
                                        producer/
                                            ResultProducer.java
                                            TaskProducer.java
                                    processor/
                                        AbstractProcessor.java
                                        IProcessor.java
                                    storage/
                                        audit/
                                            AuditMetadata.java
                                            EntityAuditor.java
                                        config/
                                            MongoConfig.java
                                        constant/
                                            MongoConstants.java
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
                                        CollectTask.java
                                        TaskResult.java
                                        TaskResultHandler.java
                                        TaskStatus.java
                                        executor/
                                            ParallelExecutor.java
                                            TaskExecutor.java
                                        scheduler/
                                            DefaultScheduler.java
                                            TaskScheduler.java
                                        splitter/
                                            DefaultTaskSplitter.java
                                            TaskSplitter.java
                                    util/
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
```

# File Contents

## pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
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
        <redisson.version>3.27.2</redisson.version>
        <rabbitmq.version>5.20.0</rabbitmq.version>
        <mybatis.version>3.0.3</mybatis.version>
        <jackson.version>2.17.0</jackson.version>
        <prometheus.version>1.12.4</prometheus.version>
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
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
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
        <module>business-finance</module>
        <module>business-medical</module>
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
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
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
import com.study.collect.core.annotation.Collector;
import com.study.collect.core.cache.annotation.Cache;
import com.study.collect.core.cache.annotation.CacheLock;
import com.study.collect.core.collector.AbstractCollector;

//@Collector(type = "enterprise")
//@Component
//@RequiredArgsConstructor
//public class EnterpriseCollector implements ICollector<String, Enterprise> {
//
//    private final EnterpriseRepository repository;
//
//    @Override
//    public Enterprise collect(String code) {
//        return repository.findByCode(code);
//    }
//
//    @Override
//    public String getType() {
//        return "enterprise";
//    }
//}



// 2. Collector - 增加缓存和分布式锁
@Collector(type = "enterprise")
public class EnterpriseCollector extends AbstractCollector<String, Enterprise> {

//    @Cache(key = "enterprise:#{#code}")  // 缓存注解
//    @CacheLock(key = "lock:enterprise:#{#code}")  // 分布式锁注解
//    public Enterprise collect(String code) {
//        // 采集逻辑
//        return doCollect(code);
//    }

    @Cache(key = "enterprise:#{#code}")
    @CacheLock(key = "lock:enterprise:#{#code}")
    @Override
//    protected Enterprise doCollect(String code) {
    public Enterprise collect(String code) {
        // 1. 调用外部接口采集数据
        Enterprise data = collectFromApi(code);
        // 2. 设置版本号
        data.setVersion(generateVersion());
        return data;
    }

    private String generateVersion() {
        return "1.0";
    }

    private Enterprise collectFromApi(String code) {
        return new Enterprise();
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

## EnterpriseController.java

```java
package com.study.collect.business.enterprise.controller;

import com.study.collect.business.enterprise.model.Enterprise;
import com.study.collect.business.enterprise.service.EnterpriseService;
import com.study.collect.common.model.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enterprise")
@RequiredArgsConstructor
public class EnterpriseController {

    private final EnterpriseService enterpriseService;

    @GetMapping("/collect/{code}")
    public Response<Enterprise> collect(@PathVariable String code) {
        Enterprise enterprise = enterpriseService.collectAndProcess(code);
        return Response.success(enterprise);
    }
    @GetMapping("/full")
    public Response<List<Enterprise>> getFullData() {
        return Response.success(enterpriseService.getFullData());
    }

    @GetMapping("/increment")
    public Response<List<Enterprise>> getIncrementalData(
            @RequestParam String version) {
        return Response.success(enterpriseService.getIncrementalData(version));
    }
}


// 1. Controller - 增加全量/增量接口
```

## Enterprise.java

```java
package com.study.collect.business.enterprise.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document(collection = "enterprise")
public class Enterprise {
    @Id
    private String id;
    private String name;
    private String code;
    private String address;
    private String contact;
    private String phone;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String version;
}
```

## EnterpriseProcessor.java

```java
package com.study.collect.business.enterprise.processor;

import com.study.collect.business.enterprise.model.Enterprise;
import com.study.collect.core.annotation.Processor;
import com.study.collect.core.processor.AbstractProcessor;
import org.springframework.stereotype.Component;

@Processor(type = "enterprise", order = 100)
@Component
public class EnterpriseProcessor extends AbstractProcessor<Enterprise> {

    @Override
    protected Enterprise doProcess(Enterprise data) {
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
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface EnterpriseRepository extends IRepository<Enterprise, String>, MongoRepository<Enterprise, String> {
    Enterprise findByCode(String code);

    // 继承基础的版本方法
    List<Enterprise> findByVersion(String version);

    @Query("")
        // MongoDB查询
    List<Enterprise> findIncrementalData(String version);
}

```

## EnterpriseService.java

```java
package com.study.collect.business.enterprise.service;

import com.study.collect.business.enterprise.collector.EnterpriseCollector;
import com.study.collect.business.enterprise.model.Enterprise;
import com.study.collect.business.enterprise.processor.EnterpriseProcessor;
import com.study.collect.business.enterprise.repository.EnterpriseRepository;
import com.study.collect.core.mq.producer.TaskProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EnterpriseService {

    private final EnterpriseCollector collector;
    private final EnterpriseProcessor processor;
    private final EnterpriseRepository repository;

    public Enterprise collectAndProcess(String code) {
        // 1. 采集数据
        Enterprise enterprise = collector.collect(code);
        if (enterprise == null) {
            return null;
        }

        // 2. 处理数据
        enterprise = processor.process(enterprise);

        // 3. 保存数据
        return repository.save(enterprise);
    }

    @Autowired
    private TaskProducer taskProducer;

    // 大批量数据采集
    public void batchCollect(List<String> codes) {
        // 创建采集任务
        CollectTask task = CollectTask.builder()
                .type("enterprise")
                .params(codes)
                .build();

        // 发送到消息队列
        taskProducer.sendTask(task);
    }

    // 结果处理
    @RabbitListener(queues = "#{taskResultQueue.name}")
    public void handleResult(TaskResult result) {
        // 处理采集结果
    }

    // 单条采集
    public Enterprise collect(String code) {
        return collector.collect(code);
    }

    // 批量采集
    public void batchCollect(List<String> codes) {
        CollectTask task = new CollectTask("enterprise", codes);
        taskProducer.sendTask(task);
    }

    // 采集结果处理
    @RabbitListener(queues = "#{taskResultQueue.name}")
    public void handleResult(TaskResult result) {
        // 处理采集结果
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
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
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
import com.study.collect.core.annotation.Collector;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Collector(type = "finance")
@Component
@RequiredArgsConstructor
public class FinanceCollector extends AbstractCollector<String, FinanceData> {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String CACHE_PREFIX = "finance:stock:";

    @Override
    protected void preProcess(String stockCode) {
        // 检查缓存是否存在
        String key = CACHE_PREFIX + stockCode;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            throw new CollectException("Data already collected: " + stockCode);
        }
    }

    @Override
    protected FinanceData doCollect(String stockCode) {
        // 模拟从外部API获取数据
        FinanceData data = collectFromExternalApi(stockCode);

        // 缓存数据
        String key = CACHE_PREFIX + stockCode;
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
import com.study.collect.core.annotation.Processor;
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
    protected FinanceData doProcess(FinanceData data) {
        // 数据验证
        validateData(data);

        // 数据转换
        transformData(data);

        // 数据补充
        enrichData(data);

        return data;
    }

    private void validateData(FinanceData data) {
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
        data = processor.process(data);

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
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
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
import com.study.collect.core.annotation.Collector;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Collector(type = "medical")
@Component
@RequiredArgsConstructor
public class MedicalCollector implements ICollector<String, MedicalData> {

    private final MedicalEngine engine;

    @Override
    public MedicalData collect(String patientId) {
        return engine.process(patientId);
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
import org.springframework.web.bind.annotation.*;

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
package com.study.business.medical.engine;

import com.study.business.medical.model.MedicalData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MedicalEngine {

    private final DicomProcessor dicomProcessor;
    private final ImageProcessor imageProcessor;
    private final PrivacyProcessor privacyProcessor;

    public MedicalData process(String patientId) {
        // 1. 读取DICOM文件
        MedicalData data = dicomProcessor.readDicomData(patientId);

        // 2. 处理图像数据
        data = imageProcessor.process(data);

        // 3. 隐私数据处理
        data = privacyProcessor.process(data);

        return data;
    }
}
```

## MedicalData.java

```java
package com.study.business.medical.model;

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
package com.study.business.medical.repository;


import com.study.business.medical.model.MedicalData;
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
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
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
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
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
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
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
    </dependencies>
</project>
```

## Collector.java

```java
package com.study.collect.core.annotation;

import java.lang.annotation.*;

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
}

```

## Processor.java

```java
package com.study.collect.core.annotation;

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
     * 处理顺序
     */
    int order() default 0;

    /**
     * 是否启用
     */
    boolean enabled() default true;
}

```

## Repository.java

```java
package com.study.collect.core.annotation;

import java.lang.annotation.*;

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

## CacheMetrics.java

```java
package com.study.collect.core.cache;

import com.study.collect.core.cache.manager.CacheManager;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CacheMetrics {

    private final CacheManager cacheManager;
    private Counter cacheHits;
    private Counter cacheMisses;
    private Counter cacheEvictions;

    @PostConstruct
    public void init() {
        // 注册Prometheus指标
        cacheHits = Counter.builder("cache_hits_total")
                .description("Cache hits total")
                .register(Metrics.globalRegistry);

        cacheMisses = Counter.builder("cache_misses_total")
                .description("Cache misses total")
                .register(Metrics.globalRegistry);

        cacheEvictions = Counter.builder("cache_evictions_total")
                .description("Cache evictions total")
                .register(Metrics.globalRegistry);
    }

    public void recordCacheHit() {
        cacheHits.increment();
    }

    public void recordCacheMiss() {
        cacheMisses.increment();
    }

    public void recordCacheEviction() {
        cacheEvictions.increment();
    }
}
```

## Cache.java

```java
package com.study.collect.core.cache.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

// 1. Cache注解
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
package com.study.collect.core.cache.annotation;

import java.lang.annotation.*;

// 1. CacheEvict注解
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
package com.study.collect.core.cache.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

// 2. CacheLock注解
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

## RedisConfig.java

```java
package com.study.collect.core.cache.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.study.collect.core.cache.lock.DistributedLock;
import com.study.collect.core.cache.lock.RedisLock;
import com.study.collect.core.cache.manager.CacheManager;
import com.study.collect.core.cache.manager.RedisCacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // 设置key/value序列化方式
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public CacheManager cacheManager(RedisTemplate<String, Object> redisTemplate) {
        return new RedisCacheManager(redisTemplate, objectMapper());
    }

    @Bean
    public DistributedLock distributedLock(RedisTemplate<String, Object> redisTemplate) {
        return new RedisLock(redisTemplate);
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
}
```

## CacheAspect.java

```java
package com.study.collect.core.cache.handler;

import com.study.collect.core.cache.annotation.Cache;
import com.study.collect.core.cache.annotation.CacheEvict;
import com.study.collect.core.cache.manager.CacheManager;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.apache.commons.lang3.StringUtils;

// 1. 缓存切面
@Aspect
@Component
@RequiredArgsConstructor
public class CacheAspect {

    private final CacheManager cacheManager;
    private final SpelExpressionParser parser = new SpelExpressionParser();

    @Around("@annotation(cache)")
    public Object doCache(ProceedingJoinPoint point, Cache cache) throws Throwable {
        // 1. 解析缓存key
        String key = parseKey(cache.prefix(), cache.key(), point);

        // 2. 尝试获取缓存
        Class<?> returnType = ((MethodSignature)point.getSignature()).getReturnType();
        Object value = cacheManager.get(key, returnType);
        if (value != null) {
            return value;
        }

        // 3. 执行方法
        value = point.proceed();

        // 4. 设置缓存
        if (value != null) {
            cacheManager.set(key, value, cache.expire(), cache.timeUnit());
        }

        return value;
    }

    @Around("@annotation(cacheEvict)")
    public Object doEvict(ProceedingJoinPoint point, CacheEvict cacheEvict) throws Throwable {
        // 是否在方法执行前清除缓存
        if (cacheEvict.beforeInvocation()) {
            evictCache(cacheEvict, point);
            return point.proceed();
        }

        try {
            Object result = point.proceed();
            evictCache(cacheEvict, point);
            return result;
        } catch (Throwable e) {
            if (cacheEvict.beforeInvocation()) {
                evictCache(cacheEvict, point);
            }
            throw e;
        }
    }

    private void evictCache(CacheEvict cacheEvict, ProceedingJoinPoint point) {
        if (cacheEvict.allEntries()) {
            // 清除前缀下所有缓存
            cacheManager.deleteByPrefix(cacheEvict.prefix());
        } else {
            // 清除指定key的缓存
            String key = parseKey(cacheEvict.prefix(), cacheEvict.key(), point);
            cacheManager.delete(key);
        }
    }

    private String parseKey(String prefix, String key, ProceedingJoinPoint point) {
        // SpEL解析key表达式
        if (StringUtils.isEmpty(key)) {
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
        return StringUtils.isEmpty(prefix) ? parsedKey : prefix + ":" + parsedKey;
    }
}
```

## LockAspect.java

```java
package com.study.collect.core.cache.handler;

import com.study.collect.core.cache.annotation.CacheLock;
import com.study.collect.core.cache.lock.DistributedLock;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

// 2. 分布式锁切面
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
            boolean isLocked = lock.tryLock(key,
                    cacheLock.waitTime(),
                    cacheLock.leaseTime(),
                    cacheLock.timeUnit());

            if (!isLocked) {
//                throw new LockException("Get lock failed: " + key);
                throw new RuntimeException("Get lock failed: " + key);
            }

            return point.proceed();
        } finally {
            lock.unlock(key);
        }
    }

    private String parseKey(String prefix, String key, ProceedingJoinPoint point) {
        // 同CacheAspect中的解析逻辑
        // SpEL解析key表达式
        if (StringUtils.isEmpty(key)) {
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
        return StringUtils.isEmpty(prefix) ? parsedKey : prefix + ":" + parsedKey;
    }
}
```

## DistributedLock.java

```java
package com.study.collect.core.cache.lock;

import java.util.concurrent.TimeUnit;

// 4. DistributedLock接口
public interface DistributedLock {
    /**
     * 获取锁
     */
    boolean tryLock(String key, long waitTime, long leaseTime, TimeUnit unit);

    /**
     * 释放锁
     */
    void unlock(String key);
}
```

## RedisLock.java

```java
package com.study.collect.core.cache.lock;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

// 2. Redis分布式锁实现
@Component
@RequiredArgsConstructor
public class RedisLock implements DistributedLock {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean tryLock(String key, long waitTime, long leaseTime, TimeUnit unit) {
        try {
            long startTime = System.currentTimeMillis();
            long waitMillis = unit.toMillis(waitTime);

            while (System.currentTimeMillis() - startTime < waitMillis) {
                Boolean success = redisTemplate.opsForValue()
                        .setIfAbsent(key, Thread.currentThread().getId(), leaseTime, unit);

                if (Boolean.TRUE.equals(success)) {
                    return true;
                }

                Thread.sleep(100);
            }
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    @Override
    public void unlock(String key) {
        Long threadId = (Long) redisTemplate.opsForValue().get(key);
        if (threadId != null && threadId.equals(Thread.currentThread().getId())) {
            redisTemplate.delete(key);
        }
    }
}

```

## CacheManager.java

```java
package com.study.collect.core.cache.manager;

import java.util.concurrent.TimeUnit;

// 3. CacheManager接口
public interface CacheManager {
    /**
     * 设置缓存
     */
    <T> void set(String key, T value, long expire, TimeUnit timeUnit);

    /**
     * 获取缓存
     */
    <T> T get(String key, Class<T> type);

    /**
     * 删除缓存
     */
    void delete(String key);

    /**
     * 清除前缀
     */
    void deleteByPrefix(String prefix);
}
```

## RedisCacheManager.java

```java
package com.study.collect.core.cache.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;

// 1. Redis缓存管理实现
@Component
@RequiredArgsConstructor
public class RedisCacheManager implements CacheManager {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public <T> void set(String key, T value, long expire, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, expire, timeUnit);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> type) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return null;
        }

        if (type.isInstance(value)) {
            return (T) value;
        }

        return objectMapper.convertValue(value, type);
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public void deleteByPrefix(String prefix) {
        Set<String> keys = redisTemplate.keys(prefix + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}
```

## AbstractCollector.java

```java
package com.study.collect.core.collector;

import com.study.collect.core.annotation.Collector;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractCollector<T, R> implements ICollector<T, R> {

    @Override
    public R collect(T param) {
        try {
            // 1. 前置处理
            preProcess(param);

            // 2. 执行采集
            R result = doCollect(param);

            // 3. 后置处理
            postProcess(result);

            return result;
        } catch (Exception e) {
            log.error("Collect failed", e);
//            throw new CollectException("Collect failed: " + e.getMessage());
            throw new RuntimeException("Collect failed: " + e.getMessage());
        }
    }

    /**
     * 前置处理
     */
    protected void preProcess(T param) {
        // 默认空实现
    }

    /**
     * 执行采集
     */
    protected abstract R doCollect(T param);

    /**
     * 后置处理
     */
    protected void postProcess(R result) {
        // 默认空实现
    }
}
```

## ICollector.java

```java
package com.study.collect.core.collector;

import com.study.collect.core.cache.annotation.Cache;
import com.study.collect.core.cache.annotation.CacheLock;

public interface ICollector<T, R> {
    /**
     * 执行采集
     */
    // 采集数据
    @Cache(prefix = "collect")              // 缓存支持
    @CacheLock(prefix = "collect_lock")     // 分布式锁
    R collect(T param);

    /**
     * 获取采集器类型
     */
    String getType();
}

```

## CollectAutoConfiguration.java

```java
package com.study.collect.core.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan("com.study.collect.core")
@Import({RedisConfiguration.class, RabbitConfiguration.class})
public class CollectAutoConfiguration {
// 核心配置
}
```

## MongoRepositoryConfig.java

```java
package com.study.collect.core.config;

import com.study.collect.core.storage.repository.factory.CustomMongoRepositoryFactoryBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * MongoRepository配置
 */

@Configuration
@EnableMongoRepositories(
        basePackages = "com.study.collect",
        repositoryFactoryBeanClass = CustomMongoRepositoryFactoryBean.class
)
public class MongoRepositoryConfig {
}
```

## RabbitConfiguration.java

```java
package com.study.collect.core.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(new Jackson2JsonMessageConverter());
        return template;
    }

    @Bean
    public Queue taskQueue() {
        return new Queue("collect.task.queue", true);
    }

    @Bean
    public Queue resultQueue() {
        return new Queue("collect.result.queue", true);
    }
}
```

## RedisConfiguration.java

```java
package com.study.collect.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfiguration {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 配置序列化器
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));

        template.afterPropertiesSet();
        return template;
    }
}
```

## RabbitConfig.java

```java
package com.study.collect.core.mq.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Value("${mq.task.exchange}")
    private String taskExchange;

    @Value("${mq.task.queue}")
    private String taskQueue;

    @Value("${mq.task.routing-key}")
    private String taskRoutingKey;

    @Value("${mq.result.exchange}")
    private String resultExchange;

    @Value("${mq.result.queue}")
    private String resultQueue;

    @Value("${mq.result.routing-key}")
    private String resultRoutingKey;

    // 任务交换机
    @Bean
    public DirectExchange taskExchange() {
        return new DirectExchange(taskExchange);
    }

    // 任务队列
    @Bean
    public Queue taskQueue() {
        return QueueBuilder.durable(taskQueue)
                .withArgument("x-dead-letter-exchange", taskExchange + ".dlx")
                .withArgument("x-dead-letter-routing-key", taskRoutingKey + ".dlx")
                .build();
    }

    // 任务绑定关系
    @Bean
    public Binding taskBinding() {
        return BindingBuilder.bind(taskQueue())
                .to(taskExchange())
                .with(taskRoutingKey);
    }

    // 结果交换机
    @Bean
    public DirectExchange resultExchange() {
        return new DirectExchange(resultExchange);
    }

    // 结果队列
    @Bean
    public Queue resultQueue() {
        return QueueBuilder.durable(resultQueue)
                .withArgument("x-dead-letter-exchange", resultExchange + ".dlx")
                .withArgument("x-dead-letter-routing-key", resultRoutingKey + ".dlx")
                .build();
    }

    // 结果绑定关系
    @Bean
    public Binding resultBinding() {
        return BindingBuilder.bind(resultQueue())
                .to(resultExchange())
                .with(resultRoutingKey);
    }

    // 消息转换器
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // RabbitTemplate配置
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}

```

## ResultConsumer.java

```java
package com.study.collect.core.mq.consumer;

public class ResultConsumer {
}

```

## TaskConsumer.java

```java
package com.study.collect.core.mq.consumer;


import com.study.collect.core.collector.ICollector;
import com.study.collect.core.task.CollectTask;
import com.study.collect.core.task.TaskResult;
import com.study.collect.core.task.TaskResultHandler;
import com.study.collect.core.task.TaskStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

// 2. 任务消费者
@Component
@RequiredArgsConstructor
@Slf4j
public class TaskConsumer {

    private final Map<String, ICollector> collectors;
    private final TaskResultHandler resultHandler;

    @RabbitListener(queues = "${mq.task.queue}")
    public void handleTask(CollectTask task) {
        try {
            // 1. 获取对应的采集器
            ICollector collector = collectors.get(task.getType());
            if (collector == null) {
                throw new IllegalArgumentException("Unknown task type: " + task.getType());
            }

            // 2. 执行采集
            task.setStatus(TaskStatus.RUNNING);
            Object result = collector.collect(task.getParams());

            // 3. 处理结果
            TaskResult taskResult = new TaskResult();
            taskResult.setTaskId(task.getId());
            taskResult.setType(task.getType());
            taskResult.setSuccess(true);
            taskResult.setData(result);
            taskResult.setFinishTime(LocalDateTime.now());

            resultHandler.handleResult(taskResult);

        } catch (Exception e) {
            log.error("Task execution failed: " + task.getId(), e);

            // 4. 处理异常
            TaskResult taskResult = new TaskResult();
            taskResult.setTaskId(task.getId());
            taskResult.setType(task.getType());
            taskResult.setSuccess(false);
            taskResult.setMessage(e.getMessage());
            taskResult.setFinishTime(LocalDateTime.now());

            resultHandler.handleResult(taskResult);
        }
    }
```

## ResultMessage.java

```java
package com.study.collect.core.mq.message;

public class ResultMessage {
}

```

## TaskMessage.java

```java
package com.study.collect.core.mq.message;

public class TaskMessage {
}

```

## ResultProducer.java

```java
package com.study.collect.core.mq.producer;

public class ResultProducer {
}

```

## TaskProducer.java

```java
package com.study.collect.core.mq.producer;

import com.study.collect.core.task.CollectTask;
import com.study.collect.core.task.TaskResult;
import com.study.collect.core.task.TaskResultHandler;
import com.study.collect.core.task.TaskStatus;
import com.study.collect.core.task.splitter.TaskSplitter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

// 1. 任务生产者
@Component
@RequiredArgsConstructor
public class TaskProducer {

    private final RabbitTemplate rabbitTemplate;
    private final TaskSplitter taskSplitter;

    @Value("${mq.task.exchange}")
    private String taskExchange;

    @Value("${mq.task.routing-key}")
    private String taskRoutingKey;

    public void sendTask(CollectTask task, int shardCount) {
        // 1. 任务分片
        List<CollectTask> tasks = taskSplitter.split(task, shardCount);

        // 2. 发送任务
        tasks.forEach(subTask -> {
            rabbitTemplate.convertAndSend(taskExchange, taskRoutingKey, subTask);
        });
    }
}


}


```

## AbstractProcessor.java

```java
package com.study.collect.core.processor;

public abstract class AbstractProcessor<T> implements IProcessor<T> {

    @Override
    public T process(T data) {
        try {
            // 1. 前置处理
            preProcess(data);

            // 2. 执行处理
            T result = doProcess(data);

            // 3. 后置处理
            postProcess(result);

            return result;
        } catch (Exception e) {
            throw new ProcessException("Process failed: " + e.getMessage());
        }
    }

    protected void preProcess(T data) {
        // 默认空实现
    }

    protected abstract T doProcess(T data);

    protected void postProcess(T result) {
        // 默认空实现
    }
}

```

## IProcessor.java

```java

package com.study.collect.core.processor;

public interface IProcessor<T> {
    /**
     * 处理数据
     */
    T process(T data);

    /**
     * 获取处理器类型
     */
    String getType();

    /**
     * 获取处理顺序
     */
    int getOrder();
}

```

## AuditMetadata.java

```java
package com.study.collect.core.storage.audit;

public class AuditMetadata {
}

```

## EntityAuditor.java

```java
package com.study.collect.core.storage.audit;

public class EntityAuditor {
}

```

## MongoConfig.java

```java
package com.study.collect.core.storage.config;

public class MongoConfig {
}

```

## MongoConstants.java

```java
package com.study.collect.core.storage.constant;

public class MongoConstants {
}

```

## BaseEntity.java

```java
package com.study.collect.core.storage.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

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

    protected Boolean deleted = false;
}
```

## VersionEntity.java

```java
package com.study.collect.core.storage.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Version;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class VersionEntity extends BaseEntity {

    @Version
    private Long version;

    private String versionCode; // 业务版本号,用于增量同步

    private LocalDateTime versionTime; // 版本时间戳

    // 版本初始化
    public void initVersion() {
        this.version = 0L;
        this.versionCode = generateVersionCode();
        this.versionTime = LocalDateTime.now();
    }

    // 版本更新
    public void upgradeVersion() {
        this.version = this.version + 1;
        this.versionCode = generateVersionCode();
        this.versionTime = LocalDateTime.now();
    }

    // 生成版本号
    private String generateVersionCode() {
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
import org.springframework.data.mongodb.core.mapping.event.AfterConvertEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class EntityEventListener<T extends BaseEntity> extends AbstractMongoEventListener<T> {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public void onBeforeConvert(BeforeConvertEvent<T> event) {
        T entity = event.getSource();

        // 处理审计字段
        LocalDateTime now = LocalDateTime.now();
        if (entity.getCreateTime() == null) {
            entity.setCreateTime(now);
            entity.setUpdateTime(now);
            // 发布保存前事件
            eventPublisher.publishEvent(new EntityEvents.BeforeSaveEvent<>(entity));
        } else {
            entity.setUpdateTime(now);
            // 发布更新前事件
            eventPublisher.publishEvent(new EntityEvents.BeforeUpdateEvent<>(entity));
        }

        // 处理版本
        if (entity instanceof VersionEntity versionEntity) {
            String oldVersion = versionEntity.getVersionCode();
            if (oldVersion == null) {
                versionEntity.initVersion();
            } else {
                versionEntity.upgradeVersion();
                // 发布版本更新事件
                eventPublisher.publishEvent(new EntityEvents.VersionUpgradeEvent<>(
                        entity, oldVersion, versionEntity.getVersionCode()));
            }
        }
    }

    @Override
    public void onAfterConvert(AfterConvertEvent<T> event) {
        T entity = event.getSource();
        // 发布更新后事件
        eventPublisher.publishEvent(new EntityEvents.AfterUpdateEvent<>(entity));
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

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;

import java.io.Serializable;
import java.util.List;

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

    @Override
    public void updateStatus(ID id, String status) {

    }

    @Override
    public long countByStatus(String status) {
        return 0;
    }

    @Override
    public void softDelete(ID id) {

    }
//
//    // 其他方法实现...
//    // 版本查询
//    List<T> findByVersion(String version);
//    // 增量查询
//    List<T> findIncrementalData(String version);
}
```

## IRepository.java

```java
package com.study.collect.core.storage.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;

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

## VersionRepository.java

```java
package com.study.collect.core.storage.repository;

public class VersionRepository {
}

```

## CustomMongoRepositoryFactory.java

```java
package com.study.collect.core.storage.repository.factory;

import com.study.collect.core.storage.repository.BaseMongoRepository;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;

import java.io.Serializable;

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

## CustomMongoRepositoryFactoryBean.java

```java
package com.study.collect.core.storage.repository.factory;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactoryBean;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import java.io.Serializable;

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

## CollectTask.java

```java
package com.study.collect.core.task;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

// 1. 采集任务
@Data
public class CollectTask {
    private String id;              // 任务ID
    private String type;            // 任务类型
    private Map<String, Object> params;  // 任务参数
    private Integer shardIndex;     // 分片索引
    private Integer shardTotal;     // 分片总数
    private TaskStatus status;      // 任务状态
    private LocalDateTime createTime; // 创建时间

    public static CollectTask create(String type, Map<String, Object> params) {
        CollectTask task = new CollectTask();
        task.setId(UUID.randomUUID().toString());
        task.setType(type);
        task.setParams(params);
        task.setStatus(TaskStatus.CREATED);
        task.setCreateTime(LocalDateTime.now());
        return task;
    }
}

```

## TaskResult.java

```java
package com.study.collect.core.task;

import lombok.Data;

import java.time.LocalDateTime;

// 2. 任务结果
@Data
public class TaskResult {
    private String taskId;          // 任务ID
    private String type;            // 任务类型
    private Boolean success;        // 是否成功
    private String message;         // 结果信息
    private Object data;            // 结果数据
    private LocalDateTime finishTime; // 完成时间
}

```

## TaskResultHandler.java

```java
package com.study.collect.core.task;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// 3. 结果处理器
@Component
@RequiredArgsConstructor
public class TaskResultHandler {

    private final RabbitTemplate rabbitTemplate;

    @Value("${mq.result.exchange}")
    private String resultExchange;

    @Value("${mq.result.routing-key}")
    private String resultRoutingKey;

    public void handleResult(TaskResult result) {
        // 发送结果到结果队列
        rabbitTemplate.convertAndSend(resultExchange, resultRoutingKey, result);
    }
}

```

## TaskStatus.java

```java
package com.study.collect.core.task;

// 3. 任务状态枚举
public enum TaskStatus {
    CREATED,    // 已创建
    RUNNING,    // 执行中
    SUCCESS,    // 执行成功
    FAILED,     // 执行失败
    CANCELED    // 已取消
}
```

## ParallelExecutor.java

```java
package com.study.collect.core.task.executor;

public class ParallelExecutor {
}

```

## TaskExecutor.java

```java
package com.study.collect.core.task.executor;

public class TaskExecutor {
}

```

## DefaultScheduler.java

```java
package com.study.collect.core.task.scheduler;

public class DefaultScheduler {
}

```

## TaskScheduler.java

```java
package com.study.collect.core.task.scheduler;

import com.study.collect.core.task.CollectTask;
import com.study.collect.core.task.TaskResult;

// 3. 任务接口
public interface TaskExecutor {
    // 执行任务
    void execute(CollectTask task);
    // 处理结果
    void handleResult(TaskResult result);
}

```

## DefaultTaskSplitter.java

```java
package com.study.collect.core.task.splitter;

import com.study.collect.core.task.CollectTask;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

// 2. 默认分片实现
@Component
public class DefaultTaskSplitter implements TaskSplitter {

    @Override
    public List<CollectTask> split(CollectTask task, int shardCount) {
        List<CollectTask> tasks = new ArrayList<>();

        // 获取需要分片的参数
        List<?> params = (List<?>) task.getParams().get("dataList");
        if (CollectionUtils.isEmpty(params)) {
            return Collections.singletonList(task);
        }

        // 计算分片
        int size = params.size();
        int shardSize = (size + shardCount - 1) / shardCount;

        // 生成分片任务
        for (int i = 0; i < shardCount; i++) {
            int fromIndex = i * shardSize;
            if (fromIndex >= size) {
                break;
            }

            int toIndex = Math.min((i + 1) * shardSize, size);
            List<?> subParams = params.subList(fromIndex, toIndex);

            CollectTask subTask = new CollectTask();
            BeanUtils.copyProperties(task, subTask);
            subTask.setId(UUID.randomUUID().toString());
            subTask.getParams().put("dataList", subParams);
            subTask.setShardIndex(i);
            subTask.setShardTotal(shardCount);

            tasks.add(subTask);
        }

        return tasks;
    }
}

```

## TaskSplitter.java

```java
package com.study.collect.core.task.splitter;

import com.study.collect.core.task.CollectTask;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

// 1. 任务分片接口
public interface TaskSplitter {
    List<CollectTask> split(CollectTask task, int shardCount);
}


```

## pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
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
        <dependency>
            <groupId>com.study</groupId>
            <artifactId>business-finance</artifactId>
            <version>${project.version}</version>
        </dependency>
        <dependency>
            <groupId>com.study</groupId>
            <artifactId>business-medical</artifactId>
            <version>${project.version}</version>
        </dependency>

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
            <version>2.3.0</version>
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

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CollectApplication {
    public static void main(String[] args) {
        SpringApplication.run(CollectApplication.class, args);
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
url: jdbc:mysql://localhost:3306/collect?useUnicode=true&characterEncoding=utf8
username: root
password: root
driver-class-name: com.mysql.cj.jdbc.Driver

# Redis配置
redis:
host: localhost
port: 6379
database: 0

# MongoDB配置
data:
mongodb:
uri: mongodb://localhost:27017/collect

# RabbitMQ配置
rabbitmq:
host: localhost
port: 5672
username: guest
password: guest

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
```

