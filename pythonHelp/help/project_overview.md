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
                                    config/
                                        CollectAutoConfiguration.java
                                        package-info.java
                                    mq/
                                        package-info.java
                                        config/
                                            MQProperties.java
                                            package-info.java
                                            RabbitConfig.java
                                        consumer/
                                            AbstractConsumer.java
                                            package-info.java
                                            RabbitTaskConsumer.java
                                            ResultConsumer.java
                                            TaskConsumer.java
                                        handler/
                                            DefaultMessageHandler.java
                                            MessageHandler.java
                                            ResultHandler.java
                                        message/
                                            BaseMessage.java
                                            package-info.java
                                            ResultMessage.java
                                            TaskMessage.java
                                            converter/
                                                DefaultMessageConverter.java
                                        producer/
                                            AbstractProducer.java
                                            package-info.java
                                            RabbitTaskProducer.java
                                            ResultProducer.java
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
                                            TaskConfiguration.java
                                        definition/
                                            package-info.java
                                            ShardingConfig.java
                                            TaskDefinition.java
                                            TaskProperties.java
                                            TaskTrigger.java
                                        exception/
                                            TaskValidationException.java
                                        executor/
                                            AbstractTaskExecutor.java
                                            AsyncTaskExecutor.java
                                            DefaultTaskExecutor.java
                                            package-info.java
                                            RetryExecutor.java
                                            TaskExecutor.java
                                        handler/
                                            AbstractTaskHandler.java
                                            TaskHandler.java
                                        lifecycle/
                                            TaskLifecycle.java
                                            TaskLifecycleManager.java
                                        manager/
                                            AbstractTaskManager.java
                                            DefaultTaskManager.java
                                            TaskManager.java
                                        model/
                                            CollectTask.java
                                            package-info.java
                                            TaskContext.java
                                            TaskResult.java
                                            TaskStatus.java
                                        monitor/
                                            TaskMonitor.java
                                        scheduler/
                                            AbstractTaskScheduler.java
                                            DefaultTaskScheduler.java
                                            DynamicTaskScheduler.java
                                            package-info.java
                                            TaskScheduler.java
                                        splitter/
                                            CustomSplitter.java
                                            DefaultSplitter.java
                                            package-info.java
                                            TaskSplitter.java
                                        validator/
                                            TaskValidator.java
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
        <redisson.version>3.27.2</redisson.version>
        <rabbitmq.version>5.20.0</rabbitmq.version>
        <mybatis.version>3.0.3</mybatis.version>
        <mariadb.version>3.3.3</mariadb.version>  <!-- 这是最新的稳定版本 -->
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
import com.study.collect.core.cache.annotation.Cache;
import com.study.collect.core.cache.annotation.CacheLock;
import com.study.collect.core.collector.AbstractCollector;
import com.study.collect.core.collector.annotation.Collector;
import com.study.collect.core.collector.model.CollectContext;

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

    @Override
    public String getType() {
        return "";
    }

    private String generateVersion() {
        return "1.0";
    }

    private Enterprise collectFromApi(String code) {
        return new Enterprise();
    }

    @Override
    protected Enterprise doCollect(CollectContext<String> context) {
        return null;
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
import com.study.collect.core.processor.model.ProcessContext;
import com.study.collect.core.task.model.CollectTask;
import com.study.collect.core.task.model.TaskResult;
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
    @Autowired
    private TaskProducer taskProducer;

    public Enterprise collectAndProcess(String code) {
        // 1. 采集数据
        Enterprise enterprise = collector.collect(code);
        if (enterprise == null) {
            return null;
        }

        // 2. 处理数据
        enterprise = processor.process(enterprise, new ProcessContext());

        // 3. 保存数据
        return repository.save(enterprise);
    }

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

        <!-- 添加 MariaDB JDBC 驱动依赖 -->
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

## AbstractCollector.java

```java
package com.study.collect.core.collector;

import com.study.collect.core.collector.exception.CollectException;
import com.study.collect.core.collector.model.CollectContext;
import com.study.collect.core.collector.model.CollectResult;
import lombok.extern.slf4j.Slf4j;

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
            R data = doCollect(context);

            // 后置处理
            postProcess(data);

            log.info("采集任务执行完成: taskId={}", taskId);
            return CollectResult.success(data);

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
    protected abstract R doCollect(CollectContext<T> context);

    /**
     * 后置处理
     */
    protected void postProcess(R data) {
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
    protected void cleanCollectData(R data) {
        // 子类可覆盖实现具体的数据清洗逻辑
    }

    /**
     * 验证采集结果
     */
    protected void validateCollectResult(R data) {
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

// 自动配置类

package com.study.collect.core.config;

import com.study.collect.core.collector.config.CollectorConfiguration;
import com.study.collect.core.mq.config.MQProperties;
import com.study.collect.core.mq.config.RabbitConfig;
import com.study.collect.core.processor.config.ProcessorConfiguration;
import com.study.collect.core.storage.cache.config.CacheAutoConfiguration;
import com.study.collect.core.storage.config.MongoConfig;
import com.study.collect.core.task.config.TaskConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan("com.study.collect.core")
@Import({
        MongoConfig.class,
        RabbitConfig.class,
        CacheAutoConfiguration.class,
        TaskConfiguration.class,
        CollectorConfiguration.class,
        ProcessorConfiguration.class
})
public class CollectAutoConfiguration {
    // 核心配置
}
```

## package-info.java

```java
/**
 * 核心配置包
 */
package com.study.collect.core.config;
```

## package-info.java

```java
/**
 * 这个包包含与消息队列（MQ）模块相关的类和接口。
 * <p>
 * MQ模块负责处理应用程序中的消息队列操作
 * 它包括发送、接收和处理消息的功能。
 */
package com.study.collect.core.mq;
```

## MQProperties.java

```java
package com.study.collect.core.mq.config;

// 基础配置

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "collect.mq")
public class MQProperties {
    private RabbitMQ rabbit = new RabbitMQ();

    @Data
    public static class RabbitMQ {
        private String host;
        private Integer port;
        private String username;
        private String password;

        private Queue task = new Queue();
        private Queue result = new Queue();

        @Data
        public static class Queue {
            private String exchange;
            private String queue;
            private String routingKey;
        }
    }
}
```

## package-info.java

```java
/**
 * 配置层
 */
package com.study.collect.core.mq.config;
```

## RabbitConfig.java

```java
package com.study.collect.core.mq.config;

// RabbitMQ配置

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MQProperties.class)
public class RabbitConfig {

    @Bean
    public DirectExchange taskExchange(MQProperties properties) {
        return new DirectExchange(properties.getRabbit().getTask().getExchange());
    }

    @Bean
    public Queue taskQueue(MQProperties properties) {
        return QueueBuilder.durable(properties.getRabbit().getTask().getQueue())
                .withArgument("x-dead-letter-exchange", properties.getRabbit().getTask().getExchange() + ".dlx")
                .withArgument("x-dead-letter-routing-key", properties.getRabbit().getTask().getRoutingKey() + ".dlx")
                .build();
    }

    @Bean
    public Binding taskBinding(Queue taskQueue, DirectExchange taskExchange, MQProperties properties) {
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

## AbstractConsumer.java

```java
package com.study.collect.core.mq.consumer;

// 抽象消费者
public class AbstractConsumer {
}

```

## package-info.java

```java
/**
 * 消息模型层
 */
package com.study.collect.core.mq.consumer;
```

## RabbitTaskConsumer.java

```java
package com.study.collect.core.mq.consumer;

// RabbitMQ实现

import com.study.collect.core.mq.config.MQProperties;
import com.study.collect.core.mq.message.TaskMessage;
import com.study.collect.core.task.executor.TaskExecutor;
import com.study.collect.core.task.model.TaskContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitTaskConsumer implements TaskConsumer {

    private final TaskExecutor taskExecutor;
    private final MQProperties properties;

    @Override
    @RabbitListener(queues = "#{@taskQueue.name}")
    public void onMessage(TaskMessage message) {
        try {
            log.info("Receive task message: {}", message);

            // 1. 判断是否是当前节点的分片
            if (!isCurrentShard(message)) {
                log.info("Not current shard task, ignore, taskId: {}, shardingId: {}",
                        message.getTaskId(), message.getShardingId());
                return;
            }

            // 2. 构建任务上下文
            TaskContext context = buildContext(message);

            // 3. 执行任务
            taskExecutor.execute(message.getTaskDefinition(), context);

            log.info("Process task message success, taskId: {}", message.getTaskId());
        } catch (Exception e) {
            log.error("Process task message failed, taskId: {}", message.getTaskId(), e);
            // TODO: 异常处理、重试、死信队列等逻辑
        }
    }

    @Override
    public boolean isCurrentShard(TaskMessage message) {
        // TODO: 实现分片判断逻辑
        return message.getShardingId() == null ||
                message.getShardingId().equals(getCurrentShardingId());
    }

    private Integer getCurrentShardingId() {
        // TODO: 实现获取当前节点分片ID的逻辑
        return 0;
    }
}

```

## ResultConsumer.java

```java
package com.study.collect.core.mq.consumer;

import com.study.collect.core.mq.handler.MessageHandler;
import com.study.collect.core.mq.message.ResultMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResultConsumer {

    private final MessageHandler messageHandler;

    @RabbitListener(queues = "#{@resultQueue.name}")
    public void onMessage(ResultMessage message) {
        try {
            log.info("收到结果消息: taskId={}", message.getTaskId());
            messageHandler.handleResultMessage(message);
        } catch (Exception e) {
            log.error("处理结果消息失败: taskId={}", message.getTaskId(), e);
        }
    }
}
```

## TaskConsumer.java

```java
package com.study.collect.core.mq.consumer;

// 消费者接口

import com.study.collect.core.mq.message.TaskMessage;
import com.study.collect.core.task.model.TaskContext;

public interface TaskConsumer {
    /**
     * 处理任务消息
     *
     * @param message 任务消息
     */
    void onMessage(TaskMessage message);

    /**
     * 判断是否为当前节点的分片
     *
     * @param message 任务消息
     * @return 是否处理
     */
    default boolean isCurrentShard(TaskMessage message) {
        return true;
    }

    /**
     * 构建任务上下文
     *
     * @param message 任务消息
     * @return 任务上下文
     */
    default TaskContext buildContext(TaskMessage message) {
        TaskContext context = new TaskContext();
        context.setTaskId(message.getTaskId());
        context.setShardingId(message.getShardingId());
        context.setShardingTotal(message.getShardingTotal());
        return context;
    }
}

```

## DefaultMessageHandler.java

```java
package com.study.collect.core.mq.handler;

import com.study.collect.core.mq.message.ResultMessage;
import com.study.collect.core.mq.message.TaskMessage;
import com.study.collect.core.task.executor.TaskExecutor;
import com.study.collect.core.task.model.TaskContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 默认消息处理器实现
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultMessageHandler implements MessageHandler {

    private final TaskExecutor taskExecutor;

    @Override
    public void handleTaskMessage(TaskMessage message) {
        log.info("开始处理任务消息: taskId={}", message.getTaskId());

        try {
            TaskContext context = buildTaskContext(message);
            taskExecutor.execute(message.getTaskDefinition(), context);
            log.info("任务消息处理完成: taskId={}", message.getTaskId());

        } catch (Exception e) {
            log.error("任务消息处理失败: taskId={}", message.getTaskId(), e);
            handleTaskError(message, e);
        }
    }

    @Override
    public void handleResultMessage(ResultMessage message) {
        log.info("开始处理结果消息: taskId={}, success={}", message.getTaskId(), message.getSuccess());

        try {
            if (message.getSuccess()) {
                handleTaskSuccess(message);
            } else {
                handleTaskFailure(message);
            }
            log.info("结果消息处理完成: taskId={}", message.getTaskId());

        } catch (Exception e) {
            log.error("结果消息处理失败: taskId={}", message.getTaskId(), e);
        }
    }

    private TaskContext buildTaskContext(TaskMessage message) {
        TaskContext context = new TaskContext();
        context.setTaskId(message.getTaskId());
        context.setShardingId(message.getShardingId());
        context.setShardingTotal(message.getShardingTotal());
        return context;
    }

    private void handleTaskError(TaskMessage message, Exception e) {
        // 任务执行异常处理逻辑
    }

    private void handleTaskSuccess(ResultMessage message) {
        // 任务执行成功处理逻辑
    }

    private void handleTaskFailure(ResultMessage message) {
        // 任务执行失败处理逻辑
    }
}

```

## MessageHandler.java

```java
package com.study.collect.core.mq.handler;

import com.study.collect.core.mq.message.ResultMessage;
import com.study.collect.core.mq.message.TaskMessage;

/**
 * 消息处理器接口
 */
public interface MessageHandler {

    /**
     * 处理任务消息
     *
     * @param message 任务消息
     */
    void handleTaskMessage(TaskMessage message);

    /**
     * 处理结果消息
     *
     * @param message 结果消息
     */
    void handleResultMessage(ResultMessage message);
}
```

## ResultHandler.java

```java
package com.study.collect.core.mq.handler;

import com.study.collect.core.mq.message.ResultMessage;
import com.study.collect.core.mq.producer.ResultProducer;
import com.study.collect.core.task.model.TaskResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 结果处理器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ResultHandler {

    private final ResultProducer resultProducer;

    public void handleTaskResult(String taskId, TaskResult result) {
        log.info("开始处理任务执行结果: taskId={}", taskId);

        try {
            ResultMessage message = createResultMessage(taskId, result);
            resultProducer.sendResult(message);
            log.info("任务执行结果处理完成: taskId={}", taskId);

        } catch (Exception e) {
            log.error("任务执行结果处理失败: taskId={}", taskId, e);
        }
    }

    private ResultMessage createResultMessage(String taskId, TaskResult result) {
        ResultMessage message = new ResultMessage();
        message.setTaskId(taskId);
        message.setSuccess(result.getSuccess());
        message.setResult(result.getData());

        if (!result.getSuccess()) {
            message.setErrorMsg(result.getErrorMessage());
        }

        return message;
    }
}

```

## BaseMessage.java

```java
package com.study.collect.core.mq.message;

// 基础消息

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public abstract class BaseMessage implements Serializable {
    private String messageId;
    private String type;
    private LocalDateTime createTime;

    public BaseMessage() {
        this.createTime = LocalDateTime.now();
    }

    protected void setType(String type) {
        this.type = type;
    }
}
```

## package-info.java

```java
/**
 * 消息模型层
 */
package com.study.collect.core.mq.message;
```

## ResultMessage.java

```java
package com.study.collect.core.mq.message;

// 结果消息

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class ResultMessage extends BaseMessage {
    private String taskId;
    private String nodeId;
    private Boolean success;
    private String errorMsg;
    private Object result;
    private LocalDateTime finishTime;

    public ResultMessage() {
        super();
        setType("RESULT");
        this.finishTime = LocalDateTime.now();
    }
}

```

## TaskMessage.java

```java
package com.study.collect.core.mq.message;

import com.study.collect.core.task.definition.TaskDefinition;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TaskMessage extends BaseMessage {
    private String taskId;
    private String nodeId;
    private Integer shardingId;
    private Integer shardingTotal;
    private TaskDefinition taskDefinition;

    public TaskMessage() {
        super();
        setType("TASK");
    }
}
```

## DefaultMessageConverter.java

```java
// MessageConverter.java
package com.study.collect.core.mq.message.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.stereotype.Component;

@Component
public class DefaultMessageConverter implements MessageConverter {

    private final ObjectMapper objectMapper;

    public DefaultMessageConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Message toMessage(Object object, MessageProperties properties) {
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(object);
            properties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
            properties.setContentEncoding("UTF-8");
            return new Message(bytes, properties);
        } catch (Exception e) {
            throw new RuntimeException("消息转换失败", e);
        }
    }

    @Override
    public Object fromMessage(Message message) {
        try {
            String contentType = message.getMessageProperties().getContentType();
            if (contentType != null && contentType.contains("json")) {
                return objectMapper.readValue(message.getBody(), Object.class);
            }
            return message.getBody();
        } catch (Exception e) {
            throw new RuntimeException("消息反序列化失败", e);
        }
    }
}

```

## AbstractProducer.java

```java
package com.study.collect.core.mq.producer;

// 抽象生产者
public class AbstractProducer {
}

```

## package-info.java

```java
/**
 * 生产者层
 */
package com.study.collect.core.mq.producer;
```

## RabbitTaskProducer.java

```java
package com.study.collect.core.mq.producer;

// RabbitMQ实现

import com.study.collect.core.mq.config.MQProperties;
import com.study.collect.core.mq.message.TaskMessage;
import com.study.collect.core.task.definition.TaskDefinition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitTaskProducer implements TaskProducer {

    private final RabbitTemplate rabbitTemplate;
    private final MQProperties properties;

    @Override
    public void sendTask(TaskDefinition task) {
        TaskMessage message = createTaskMessage(task);
        sendMessage(message);
        log.info("Send task message success, taskId: {}", task.getTaskId());
    }

    @Override
    public void sendShardingTask(TaskDefinition task, int shardingTotal) {
        for (int i = 0; i < shardingTotal; i++) {
            TaskMessage message = createTaskMessage(task);
            message.setShardingId(i);
            message.setShardingTotal(shardingTotal);
            sendMessage(message);
        }
        log.info("Send sharding task message success, taskId: {}, shardingTotal: {}",
                task.getTaskId(), shardingTotal);
    }

    @Override
    public void broadcastTask(TaskDefinition task) {
        TaskMessage message = createTaskMessage(task);
        sendMessage(message);
        log.info("Broadcast task message success, taskId: {}", task.getTaskId());
    }

    private TaskMessage createTaskMessage(TaskDefinition task) {
        TaskMessage message = new TaskMessage();
        message.setMessageId(UUID.randomUUID().toString());
        message.setTaskId(task.getTaskId());
        message.setTaskDefinition(task);
        message.setNodeId(getNodeId());
        return message;
    }

    private void sendMessage(TaskMessage message) {
        MQProperties.RabbitMQ.Queue taskQueue = properties.getRabbit().getTask();
        rabbitTemplate.convertAndSend(
                taskQueue.getExchange(),
                taskQueue.getRoutingKey(),
                message
        );
    }

    private String getNodeId() {
        // TODO: 实现获取当前节点ID的逻辑
        return "NODE-" + UUID.randomUUID().toString().substring(0, 8);
    }
}


```

## ResultProducer.java

```java
package com.study.collect.core.mq.producer;

import com.study.collect.core.mq.config.MQProperties;
import com.study.collect.core.mq.message.ResultMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * 结果消息生产者
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ResultProducer {

    private final RabbitTemplate rabbitTemplate;
    private final MQProperties properties;

    public void sendResult(ResultMessage message) {
        try {
            MQProperties.RabbitMQ.Queue resultQueue = properties.getRabbit().getResult();
            rabbitTemplate.convertAndSend(
                    resultQueue.getExchange(),
                    resultQueue.getRoutingKey(),
                    message
            );
            log.info("结果消息发送成功: taskId={}", message.getTaskId());

        } catch (Exception e) {
            log.error("结果消息发送失败: taskId={}", message.getTaskId(), e);
            throw new RuntimeException("发送结果消息失败", e);
        }
    }
}
```

## TaskProducer.java

```java
package com.study.collect.core.mq.producer;

//生产者接口

import com.study.collect.core.task.definition.TaskDefinition;

public interface TaskProducer {
    /**
     * 发送任务消息
     *
     * @param task 任务定义
     */
    void sendTask(TaskDefinition task);

    /**
     * 发送分片任务消息
     *
     * @param task          任务定义
     * @param shardingTotal 分片总数
     */
    void sendShardingTask(TaskDefinition task, int shardingTotal);

    /**
     * 广播任务消息
     *
     * @param task 任务定义
     */
    void broadcastTask(TaskDefinition task);
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
@EnableConfigurationProperties(CacheProperties.class)
public class CacheAutoConfiguration {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory connectionFactory,
            ObjectMapper objectMapper) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 使用新版本的序列化器配置
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
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Data
@Configuration
@EnableMongoAuditing
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
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;

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
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
public class BaseMongoRepository<T extends BaseEntity, ID extends Serializable>
        extends SimpleMongoRepository<T, ID> implements IRepository<T, ID> {

    private final MongoOperations mongoOperations;
    private final MongoEntityInformation<T, ID> entityInformation;

    public BaseMongoRepository(MongoEntityInformation<T, ID> metadata,
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
        return null;
    }

    @Override
    public List<T> findByVersionCodeGreaterThan(String versionCode) {
        return List.of();
    }

    @Override
    public void softDelete(ID id) {
        Query query = Query.query(Criteria.where("id").is(id));
        Update update = Update.update("deleted", true)
                .set("updateTime", LocalDateTime.now());
        mongoOperations.updateFirst(query, update, entityInformation.getJavaType());
    }

    @Override
    public void softDelete(List<ID> ids) {
        Query query = Query.query(Criteria.where("id").in(ids));
        Update update = Update.update("deleted", true)
                .set("updateTime", LocalDateTime.now());
        mongoOperations.updateMulti(query, update, entityInformation.getJavaType());
    }

    @Override
    public void updateStatus(ID id, String status) {
        Query query = Query.query(Criteria.where("id").is(id));
        Update update = Update.update("status", status)
                .set("updateTime", LocalDateTime.now());
        mongoOperations.updateFirst(query, update, entityInformation.getJavaType());
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

import java.io.Serializable;
import java.util.List;

@NoRepositoryBean
public interface IRepository<T extends BaseEntity, ID extends Serializable>
        extends MongoRepository<T, ID> {

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
    void softDelete(ID id);

    /**
     * 批量软删除
     */
    void softDelete(List<ID> ids);

    /**
     * 更新状态
     */
    void updateStatus(ID id, String status);
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

## TaskConfiguration.java

```java
package com.study.collect.core.task.config;

import com.study.collect.core.task.definition.TaskProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableConfigurationProperties(TaskProperties.class)
public class TaskConfiguration {

    @Bean
    public ThreadPoolTaskScheduler taskScheduler(TaskProperties properties) {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(properties.getCorePoolSize());
        scheduler.setThreadNamePrefix("TaskScheduler-");
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(60);
        return scheduler;
    }
}
```

## package-info.java

```java
/**
 * 任务定义层
 */
package com.study.collect.core.task.definition;
```

## ShardingConfig.java

```java
package com.study.collect.core.task.definition;

// 分片配置

import lombok.Data;

@Data
public class ShardingConfig {
    private boolean enabled;         // 是否启用分片
    private Integer total;           // 分片总数
    private String strategy;         // 分片策略
}

```

## TaskDefinition.java

```java
package com.study.collect.core.task.definition;

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

## TaskProperties.java

```java
package com.study.collect.core.task.definition;

// 任务配置属性

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "collect.task")
public class TaskProperties {
    private boolean enabled = true;  // 是否启用任务
    private int corePoolSize = 5;    // 核心线程数
    private int maxPoolSize = 10;    // 最大线程数
    private int queueCapacity = 100; // 队列容量
    private List<TaskDefinition> tasks = new ArrayList<>(); // 任务配置列表
}

```

## TaskTrigger.java

```java
package com.study.collect.core.task.definition;

// 触发器定义

import lombok.Data;

@Data
public class TaskTrigger {
    private String cronExpression;   // cron表达式
    private Long interval;           // 固定间隔(毫秒)
    private Long delay;              // 初始延迟(毫秒)
    private Boolean repeat;          // 是否重复执行
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

## AbstractTaskExecutor.java

```java
package com.study.collect.core.task.executor;

// 抽象执行器

import com.study.collect.core.task.definition.TaskDefinition;
import com.study.collect.core.task.model.TaskContext;
import com.study.collect.core.task.model.TaskStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

@Slf4j
public abstract class AbstractTaskExecutor implements TaskExecutor {

    @Override
    public void execute(TaskDefinition task, TaskContext context) {
        StopWatch stopWatch = new StopWatch();
        try {
            // 1. 前置处理
            beforeExecute(task, context);
            stopWatch.start();

            // 2. 执行任务
            doExecute(task, context);

            // 3. 后置处理
            stopWatch.stop();
            afterExecute(task, context);

            // 4. 更新任务状态
            updateTaskStatus(task.getTaskId(), TaskStatus.SUCCESS);

        } catch (Exception e) {
            log.error("Task execution failed, taskId: {}", task.getTaskId(), e);
            onError(task, context, e);
            updateTaskStatus(task.getTaskId(), TaskStatus.FAILED);
        } finally {
            log.info("Task execution completed, taskId: {}, cost: {}ms",
                    task.getTaskId(), stopWatch.getTotalTimeMillis());
        }
    }

    /**
     * 任务执行前处理
     */
    protected void beforeExecute(TaskDefinition task, TaskContext context) {
        log.info("Start executing task, taskId: {}", task.getTaskId());
        updateTaskStatus(task.getTaskId(), TaskStatus.RUNNING);
    }

    /**
     * 执行具体任务
     */
    protected abstract void doExecute(TaskDefinition task, TaskContext context);

    /**
     * 任务执行后处理
     */
    protected void afterExecute(TaskDefinition task, TaskContext context) {
        log.info("Task execution completed successfully, taskId: {}", task.getTaskId());
    }

    /**
     * 任务执行异常处理
     */
    protected void onError(TaskDefinition task, TaskContext context, Exception e) {
        log.error("Task execution error handler, taskId: {}", task.getTaskId(), e);
    }

    /**
     * 更新任务状态
     */
    protected void updateTaskStatus(String taskId, TaskStatus status) {
        log.info("Update task status, taskId: {}, status: {}", taskId, status);
        // TODO: 实现具体的状态更新逻辑
    }
}


```

## AsyncTaskExecutor.java

```java
package com.study.collect.core.task.executor;

// 异步执行器

import com.study.collect.core.task.definition.TaskDefinition;
import com.study.collect.core.task.model.TaskContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AsyncTaskExecutor extends AbstractTaskExecutor {

    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;
    private final DefaultTaskExecutor defaultTaskExecutor;

    public AsyncTaskExecutor(ThreadPoolTaskExecutor threadPoolTaskExecutor,
                             DefaultTaskExecutor defaultTaskExecutor) {
        this.threadPoolTaskExecutor = threadPoolTaskExecutor;
        this.defaultTaskExecutor = defaultTaskExecutor;
    }

    @Override
    protected void doExecute(TaskDefinition task, TaskContext context) {
        threadPoolTaskExecutor.execute(() -> {
            try {
                defaultTaskExecutor.execute(task, context);
            } catch (Exception e) {
                log.error("Async task execution failed, taskId: {}",
                        task.getTaskId(), e);
            }
        });
    }

    @Override
    protected void beforeExecute(TaskDefinition task, TaskContext context) {
        super.beforeExecute(task, context);
        log.info("Submit async task, taskId: {}, active threads: {}",
                task.getTaskId(), threadPoolTaskExecutor.getActiveCount());
    }
}
```

## DefaultTaskExecutor.java

```java
package com.study.collect.core.task.executor;

// 默认执行器

import com.study.collect.core.task.definition.TaskDefinition;
import com.study.collect.core.task.handler.TaskHandler;
import com.study.collect.core.task.model.TaskContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class DefaultTaskExecutor extends AbstractTaskExecutor {

    private final Map<String, TaskHandler> taskHandlers;

    public DefaultTaskExecutor(Map<String, TaskHandler> taskHandlers) {
        this.taskHandlers = taskHandlers;
    }

    @Override
    protected void doExecute(TaskDefinition task, TaskContext context) {
        // 1. 获取任务处理器
        TaskHandler handler = getTaskHandler(task);

        // 2. 执行任务处理
        handler.handle(context);
    }

    private TaskHandler getTaskHandler(TaskDefinition task) {
        TaskHandler handler = taskHandlers.get(task.getTaskHandler());
        if (handler == null) {
            throw new IllegalStateException(
                    "Task handler not found: " + task.getTaskHandler());
        }
        return handler;
    }
}

```

## package-info.java

```java
/**
 * 执行引擎层
 */
package com.study.collect.core.task.executor;
```

## RetryExecutor.java

```java
package com.study.collect.core.task.executor;

// 重试执行器
public class RetryExecutor {
}

```

## TaskExecutor.java

```java
package com.study.collect.core.task.executor;

// 执行器接口

import com.study.collect.core.task.definition.TaskDefinition;
import com.study.collect.core.task.model.TaskContext;

public interface TaskExecutor {
    /**
     * 执行任务
     *
     * @param task    任务定义
     * @param context 任务上下文
     */
    void execute(TaskDefinition task, TaskContext context);
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
    public TaskResult handle(TaskContext context) {
        String taskId = context.getTaskId();
        log.info("Start handling task: {}", taskId);

        try {
            // 前置处理
            beforeHandle(context);

            // 执行处理
            TaskResult result = doHandle(context);

            // 后置处理
            afterHandle(context, result);

            return result;

        } catch (Exception e) {
            log.error("Task handling failed, taskId: {}", taskId, e);
            return handleError(context, e);
        }
    }

    protected void beforeHandle(TaskContext context) {
        // 子类可以覆盖实现具体的前置处理逻辑
    }

    protected abstract TaskResult doHandle(TaskContext context);

    protected void afterHandle(TaskContext context, TaskResult result) {
        // 子类可以覆盖实现具体的后置处理逻辑
    }

    protected TaskResult handleError(TaskContext context, Exception e) {
        return TaskResult.failure(context.getTaskId(), e.getMessage());
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
     * 处理任务
     *
     * @param context 任务上下文
     * @return 处理结果
     */
    TaskResult handle(TaskContext context);
}

```

## TaskLifecycle.java

```java
package com.study.collect.core.task.lifecycle;

import com.study.collect.core.task.model.TaskStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TaskLifecycle {

    private final String taskId;
    private TaskStatus currentStatus;
    private LocalDateTime createTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime lastUpdateTime;

    public TaskLifecycle(String taskId) {
        this.taskId = taskId;
        this.createTime = LocalDateTime.now();
        this.currentStatus = TaskStatus.CREATED;
    }

    public void setCurrentStatus(TaskStatus status) {
        this.currentStatus = status;
        this.lastUpdateTime = LocalDateTime.now();

        switch (status) {
            case RUNNING -> this.startTime = LocalDateTime.now();
            case SUCCESS, FAILED, CANCELED -> this.endTime = LocalDateTime.now();
        }
    }
}
```

## TaskLifecycleManager.java

```java
package com.study.collect.core.task.lifecycle;

import com.study.collect.core.task.model.TaskStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class TaskLifecycleManager {

    private final Map<String, TaskLifecycle> lifecycles = new ConcurrentHashMap<>();

    public void createTask(String taskId) {
        TaskLifecycle lifecycle = new TaskLifecycle(taskId);
        lifecycles.put(taskId, lifecycle);
        updateStatus(taskId, TaskStatus.CREATED);
    }

    public void startTask(String taskId) {
        checkLifecycleExists(taskId);
        updateStatus(taskId, TaskStatus.RUNNING);
    }

    public void completeTask(String taskId, boolean success) {
        checkLifecycleExists(taskId);
        updateStatus(taskId, success ? TaskStatus.SUCCESS : TaskStatus.FAILED);
    }

    public void pauseTask(String taskId) {
        checkLifecycleExists(taskId);
        updateStatus(taskId, TaskStatus.WAITING);
    }

    public void cancelTask(String taskId) {
        checkLifecycleExists(taskId);
        updateStatus(taskId, TaskStatus.CANCELED);
    }

    public TaskStatus getTaskStatus(String taskId) {
        TaskLifecycle lifecycle = lifecycles.get(taskId);
        return lifecycle != null ? lifecycle.getCurrentStatus() : null;
    }

    private void updateStatus(String taskId, TaskStatus newStatus) {
        TaskLifecycle lifecycle = lifecycles.get(taskId);
        TaskStatus oldStatus = lifecycle.getCurrentStatus();

        if (isValidStatusTransition(oldStatus, newStatus)) {
            lifecycle.setCurrentStatus(newStatus);
            log.info("任务状态更新 - taskId: {}, {} -> {}", taskId, oldStatus, newStatus);
            publishStatusChangeEvent(taskId, oldStatus, newStatus);
        } else {
            log.warn("非法的状态转换 - taskId: {}, {} -> {}", taskId, oldStatus, newStatus);
        }
    }

    private void checkLifecycleExists(String taskId) {
        if (!lifecycles.containsKey(taskId)) {
            throw new IllegalStateException("任务生命周期不存在: " + taskId);
        }
    }

    private boolean isValidStatusTransition(TaskStatus from, TaskStatus to) {
        // 实现状态转换的合法性检查逻辑
        return true; // 简化实现
    }

    private void publishStatusChangeEvent(String taskId, TaskStatus oldStatus, TaskStatus newStatus) {
        // 发布任务状态变更事件
    }
}

```

## AbstractTaskManager.java

```java
package com.study.collect.core.task.manager;

import com.study.collect.core.mq.producer.TaskProducer;
import com.study.collect.core.task.definition.TaskDefinition;
import com.study.collect.core.task.model.TaskResult;
import com.study.collect.core.task.scheduler.TaskScheduler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractTaskManager implements TaskManager {

    protected final TaskProducer taskProducer;
    protected final TaskScheduler taskScheduler;

    protected AbstractTaskManager(TaskProducer taskProducer, TaskScheduler taskScheduler) {
        this.taskProducer = taskProducer;
        this.taskScheduler = taskScheduler;
    }

    @Override
    public TaskResult submitTask(TaskDefinition task) {
        try {
            validateTask(task);
            beforeSubmit(task);

            // 提交任务到调度器
            if (isScheduledTask(task)) {
                taskScheduler.addTask(task);
                return TaskResult.success(task.getTaskId(), "Task scheduled successfully");
            }

            // 发送任务到消息队列
            if (task.getSharding() != null && task.getSharding().isEnabled()) {
                taskProducer.sendShardingTask(task, task.getSharding().getTotal());
            } else {
                taskProducer.sendTask(task);
            }

            afterSubmit(task);
            return TaskResult.success(task.getTaskId(), "Task submitted successfully");

        } catch (Exception e) {
            log.error("Failed to submit task, taskId: {}", task.getTaskId(), e);
            return TaskResult.failure(task.getTaskId(), e.getMessage());
        }
    }

    @Override
    public void cancelTask(String taskId) {
        log.info("Canceling task: {}", taskId);
        taskScheduler.removeTask(taskId);
        doCancelTask(taskId);
    }

    @Override
    public void pauseTask(String taskId) {
        log.info("Pausing task: {}", taskId);
        taskScheduler.pauseTask(taskId);
        doPauseTask(taskId);
    }

    @Override
    public void resumeTask(String taskId) {
        log.info("Resuming task: {}", taskId);
        taskScheduler.resumeTask(taskId);
        doResumeTask(taskId);
    }

    protected void validateTask(TaskDefinition task) {
        // 任务基础校验
        if (task == null) {
            throw new IllegalArgumentException("Task definition cannot be null");
        }
        if (task.getTaskId() == null || task.getTaskId().trim().isEmpty()) {
            throw new IllegalArgumentException("Task ID cannot be empty");
        }
        if (task.getTaskHandler() == null || task.getTaskHandler().trim().isEmpty()) {
            throw new IllegalArgumentException("Task handler cannot be empty");
        }
    }

    protected boolean isScheduledTask(TaskDefinition task) {
        return task.getCronExpression() != null && !task.getCronExpression().trim().isEmpty();
    }

    protected void beforeSubmit(TaskDefinition task) {
        // 子类可以覆盖此方法实现提交前的处理逻辑
    }

    protected void afterSubmit(TaskDefinition task) {
        // 子类可以覆盖此方法实现提交后的处理逻辑
    }

    protected abstract void doCancelTask(String taskId);

    protected abstract void doPauseTask(String taskId);

    protected abstract void doResumeTask(String taskId);
}

```

## DefaultTaskManager.java

```java
package com.study.collect.core.task.manager;

import com.study.collect.core.mq.producer.TaskProducer;
import com.study.collect.core.task.definition.TaskDefinition;
import com.study.collect.core.task.model.TaskResult;
import com.study.collect.core.task.model.TaskStatus;
import com.study.collect.core.task.scheduler.TaskScheduler;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DefaultTaskManager extends AbstractTaskManager {

    // 内存中维护任务状态
    private final Map<String, TaskStatus> taskStatusMap = new ConcurrentHashMap<>();

    public DefaultTaskManager(TaskProducer taskProducer, TaskScheduler taskScheduler) {
        super(taskProducer, taskScheduler);
    }

    @Override
    protected void beforeSubmit(TaskDefinition task) {
        taskStatusMap.put(task.getTaskId(), TaskStatus.CREATED);
    }

    @Override
    protected void doCancelTask(String taskId) {
        taskStatusMap.put(taskId, TaskStatus.CANCELED);
    }

    @Override
    protected void doPauseTask(String taskId) {
        taskStatusMap.put(taskId, TaskStatus.WAITING);
    }

    @Override
    protected void doResumeTask(String taskId) {
        taskStatusMap.put(taskId, TaskStatus.RUNNING);
    }

    @Override
    public TaskResult getTaskStatus(String taskId) {
        TaskStatus status = taskStatusMap.get(taskId);
        if (status == null) {
            return TaskResult.failure(taskId, "Task not found");
        }
        return TaskResult.success(taskId, status);
    }
}
```

## TaskManager.java

```java
package com.study.collect.core.task.manager;

import com.study.collect.core.task.definition.TaskDefinition;
import com.study.collect.core.task.model.TaskResult;

public interface TaskManager {
    /**
     * 提交任务
     *
     * @param task 任务定义
     * @return 任务结果
     */
    TaskResult submitTask(TaskDefinition task);

    /**
     * 取消任务
     *
     * @param taskId 任务ID
     */
    void cancelTask(String taskId);

    /**
     * 暂停任务
     *
     * @param taskId 任务ID
     */
    void pauseTask(String taskId);

    /**
     * 恢复任务
     *
     * @param taskId 任务ID
     */
    void resumeTask(String taskId);

    /**
     * 获取任务状态
     *
     * @param taskId 任务ID
     * @return 任务状态
     */
    TaskResult getTaskStatus(String taskId);
}

```

## CollectTask.java

```java
package com.study.collect.core.task.model;

// 采集任务实体
public class CollectTask {
}

```

## package-info.java

```java
/**
 * 任务模型层
 */
package com.study.collect.core.task.model;
```

## TaskContext.java

```java
package com.study.collect.core.task.model;

// 任务上下文

import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class TaskContext {
    private String taskId;           // 任务ID
    private Integer shardingId;      // 分片ID
    private Integer shardingTotal;   // 分片总数
    private Map<String, Object> attributes = new ConcurrentHashMap<>(); // 上下文属性

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key) {
        return (T) attributes.get(key);
    }
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

## TaskMonitor.java

```java
package com.study.collect.core.task.monitor;

import com.study.collect.core.task.model.TaskStatus;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

@Slf4j
@Component
public class TaskMonitor {

    private final Map<TaskStatus, Counter> statusCounters = new EnumMap<>(TaskStatus.class);
    private final Counter totalTaskCounter;
    private final Counter failedTaskCounter;
    private final Counter timeoutTaskCounter;

    public TaskMonitor(MeterRegistry registry) {
        // 初始化计数器
        totalTaskCounter = Counter.builder("task.total")
                .description("总任务数")
                .register(registry);

        failedTaskCounter = Counter.builder("task.failed")
                .description("失败任务数")
                .register(registry);

        timeoutTaskCounter = Counter.builder("task.timeout")
                .description("超时任务数")
                .register(registry);

        // 初始化状态计数器
        for (TaskStatus status : TaskStatus.values()) {
            statusCounters.put(status, Counter.builder("task.status")
                    .tag("status", status.name())
                    .description("任务状态统计")
                    .register(registry));
        }
    }

    public void recordTaskSubmit() {
        totalTaskCounter.increment();
    }

    public void recordTaskStatus(TaskStatus status) {
        statusCounters.get(status).increment();
    }

    public void recordTaskFailed() {
        failedTaskCounter.increment();
    }

    public void recordTaskTimeout() {
        timeoutTaskCounter.increment();
    }
}
```

## AbstractTaskScheduler.java

```java
package com.study.collect.core.task.scheduler;

// 抽象调度器

import com.study.collect.core.task.definition.TaskDefinition;
import com.study.collect.core.task.executor.TaskExecutor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public abstract class AbstractTaskScheduler implements TaskScheduler {

    protected final TaskExecutor taskExecutor;
    protected final Map<String, TaskDefinition> taskDefinitions = new ConcurrentHashMap<>();

    protected AbstractTaskScheduler(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    @Override
    public void addTask(TaskDefinition task) {
        log.info("Add task to scheduler, taskId: {}", task.getTaskId());
        taskDefinitions.put(task.getTaskId(), task);
        doAddTask(task);
    }

    @Override
    public void removeTask(String taskId) {
        log.info("Remove task from scheduler, taskId: {}", taskId);
        taskDefinitions.remove(taskId);
        doRemoveTask(taskId);
    }

    @Override
    public void pauseTask(String taskId) {
        log.info("Pause task, taskId: {}", taskId);
        doPauseTask(taskId);
    }

    @Override
    public void resumeTask(String taskId) {
        log.info("Resume task, taskId: {}", taskId);
        doResumeTask(taskId);
    }

    protected abstract void doAddTask(TaskDefinition task);

    protected abstract void doRemoveTask(String taskId);

    protected abstract void doPauseTask(String taskId);

    protected abstract void doResumeTask(String taskId);
}

```

## DefaultTaskScheduler.java

```java
package com.study.collect.core.task.scheduler;

// 默认调度器

import com.study.collect.core.task.definition.TaskDefinition;
import com.study.collect.core.task.executor.TaskExecutor;
import com.study.collect.core.task.model.TaskContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Component
public class DefaultTaskScheduler extends AbstractTaskScheduler {

    private final ThreadPoolTaskScheduler taskScheduler;
    private final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    public DefaultTaskScheduler(TaskExecutor taskExecutor, ThreadPoolTaskScheduler taskScheduler) {
        super(taskExecutor);
        this.taskScheduler = taskScheduler;
    }

    @Override
    public void start() {
        log.info("Starting task scheduler");
        taskScheduler.initialize();

        // 初始化时调度所有已配置的任务
        taskDefinitions.values().forEach(this::scheduleTask);
    }

    @Override
    public void stop() {
        log.info("Stopping task scheduler");
        scheduledTasks.values().forEach(future -> future.cancel(true));
        scheduledTasks.clear();
        taskScheduler.shutdown();
    }

    @Override
    protected void doAddTask(TaskDefinition task) {
        scheduleTask(task);
    }

    @Override
    protected void doRemoveTask(String taskId) {
        ScheduledFuture<?> future = scheduledTasks.remove(taskId);
        if (future != null) {
            future.cancel(true);
        }
    }

    @Override
    protected void doPauseTask(String taskId) {
        ScheduledFuture<?> future = scheduledTasks.get(taskId);
        if (future != null) {
            future.cancel(false);
        }
    }

    @Override
    protected void doResumeTask(String taskId) {
        TaskDefinition task = taskDefinitions.get(taskId);
        if (task != null) {
            scheduleTask(task);
        }
    }

    private void scheduleTask(TaskDefinition task) {
        try {
            // 创建任务上下文
            TaskContext context = new TaskContext();
            context.setTaskId(task.getTaskId());

            // 根据cron表达式调度任务
            ScheduledFuture<?> future = taskScheduler.schedule(
                    () -> taskExecutor.execute(task, context),
                    new CronTrigger(task.getCronExpression())
            );

            // 保存调度任务引用
            scheduledTasks.put(task.getTaskId(), future);
            log.info("Task scheduled successfully, taskId: {}, cron: {}",
                    task.getTaskId(), task.getCronExpression());

        } catch (Exception e) {
            log.error("Failed to schedule task, taskId: {}", task.getTaskId(), e);
        }
    }
}
```

## DynamicTaskScheduler.java

```java
package com.study.collect.core.task.scheduler;

// 动态调度器

import com.study.collect.core.task.definition.TaskDefinition;
import com.study.collect.core.task.executor.TaskExecutor;
import com.study.collect.core.task.model.TaskContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class DynamicTaskScheduler extends AbstractTaskScheduler {

    private final ThreadPoolTaskScheduler taskScheduler;
    private final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
    private final AtomicInteger activeTaskCount = new AtomicInteger(0);

    public DynamicTaskScheduler(TaskExecutor taskExecutor, ThreadPoolTaskScheduler taskScheduler) {
        super(taskExecutor);
        this.taskScheduler = taskScheduler;
    }

    @Override
    public void start() {
        log.info("Starting dynamic task scheduler");
        taskScheduler.initialize();
    }

    @Override
    public void stop() {
        log.info("Stopping dynamic task scheduler, active tasks: {}", activeTaskCount.get());
        scheduledTasks.values().forEach(future -> future.cancel(true));
        scheduledTasks.clear();
        activeTaskCount.set(0);
        taskScheduler.shutdown();
    }

    @Override
    protected void doAddTask(TaskDefinition task) {
        // 动态调整线程池参数
        adjustThreadPool();
        scheduleTask(task);
    }

    @Override
    protected void doRemoveTask(String taskId) {
        ScheduledFuture<?> future = scheduledTasks.remove(taskId);
        if (future != null) {
            future.cancel(true);
            activeTaskCount.decrementAndGet();
            // 重新调整线程池
            adjustThreadPool();
        }
    }

    @Override
    protected void doPauseTask(String taskId) {
        ScheduledFuture<?> future = scheduledTasks.get(taskId);
        if (future != null) {
            future.cancel(false);
            activeTaskCount.decrementAndGet();
            adjustThreadPool();
        }
    }

    @Override
    protected void doResumeTask(String taskId) {
        TaskDefinition task = taskDefinitions.get(taskId);
        if (task != null) {
            scheduleTask(task);
        }
    }

    private void scheduleTask(TaskDefinition task) {
        try {
            // 创建任务上下文
            TaskContext context = new TaskContext();
            context.setTaskId(task.getTaskId());

            // 处理分片配置
            if (task.getSharding() != null && task.getSharding().isEnabled()) {
                scheduleShardingTask(task, context);
            } else {
                scheduleSimpleTask(task, context);
            }

            activeTaskCount.incrementAndGet();
            log.info("Task scheduled successfully, taskId: {}, active tasks: {}",
                    task.getTaskId(), activeTaskCount.get());

        } catch (Exception e) {
            log.error("Failed to schedule task, taskId: {}", task.getTaskId(), e);
        }
    }

    private void scheduleSimpleTask(TaskDefinition task, TaskContext context) {
        ScheduledFuture<?> future = taskScheduler.schedule(
                () -> taskExecutor.execute(task, context),
                new CronTrigger(task.getCronExpression())
        );
        scheduledTasks.put(task.getTaskId(), future);
    }

    private void scheduleShardingTask(TaskDefinition task, TaskContext context) {
        // 为每个分片创建调度任务
        for (int i = 0; i < task.getSharding().getTotal(); i++) {
            context.setShardingId(i);
            context.setShardingTotal(task.getSharding().getTotal());

            String shardTaskId = task.getTaskId() + "_" + i;
            ScheduledFuture<?> future = taskScheduler.schedule(
                    () -> taskExecutor.execute(task, context),
                    new CronTrigger(task.getCronExpression())
            );
            scheduledTasks.put(shardTaskId, future);
        }
    }

    private void adjustThreadPool() {
        // 根据活动任务数动态调整线程池参数
        int currentActive = activeTaskCount.get();
        int corePoolSize = Math.max(5, currentActive / 2);
        int maxPoolSize = Math.max(10, currentActive);

        taskScheduler.setPoolSize(maxPoolSize);
        log.info("Adjusted thread pool, active tasks: {}, core: {}, max: {}",
                currentActive, corePoolSize, maxPoolSize);
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

## TaskScheduler.java

```java
package com.study.collect.core.task.scheduler;

// 调度器接口

import com.study.collect.core.task.definition.TaskDefinition;

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

## CustomSplitter.java

```java
package com.study.collect.core.task.splitter;

// 自定义分片器
public class CustomSplitter {
}

```

## DefaultSplitter.java

```java
package com.study.collect.core.task.splitter;

// 默认分片器
public class DefaultSplitter {
}

```

## package-info.java

```java
/**
 * 分片层
 */
package com.study.collect.core.task.splitter;
```

## TaskSplitter.java

```java
package com.study.collect.core.task.splitter;

// 分片器接口
public class TaskSplitter {
}

```

## TaskValidator.java

```java
package com.study.collect.core.task.validator;

import com.study.collect.core.task.definition.ShardingConfig;
import com.study.collect.core.task.definition.TaskDefinition;
import com.study.collect.core.task.exception.TaskValidationException;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class TaskValidator {

    public void validate(TaskDefinition task) {
        // 基础参数校验
        if (task == null) {
            throw new TaskValidationException("任务定义不能为空");
        }

        if (!StringUtils.hasText(task.getTaskId())) {
            throw new TaskValidationException("任务ID不能为空");
        }

        if (!StringUtils.hasText(task.getTaskHandler())) {
            throw new TaskValidationException("任务处理器不能为空");
        }

        // 调度参数校验
        if (StringUtils.hasText(task.getCronExpression())) {
            validateCronExpression(task.getCronExpression());
        }

        // 分片参数校验
        if (task.getSharding() != null && task.getSharding().isEnabled()) {
            validateShardingConfig(task.getSharding());
        }
    }

    private void validateCronExpression(String cronExpression) {
        try {
            new CronTrigger(cronExpression);
        } catch (IllegalArgumentException e) {
            throw new TaskValidationException("无效的CRON表达式: " + cronExpression);
        }
    }

    private void validateShardingConfig(ShardingConfig sharding) {
        if (sharding.getTotal() == null || sharding.getTotal() <= 0) {
            throw new TaskValidationException("分片总数必须大于0");
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
collect:
  task:
    enabled: true
    tasks:
      - taskId: "enterprise-collect"
        taskName: "企业数据采集"
        taskHandler: "enterpriseCollectHandler"
        cronExpression: "0 0 1 * * ?"
        sharding:
          enabled: true
          total: 4
  mq:
    rabbit:
      enabled: true
      host: 192.168.80.137
      port: 5672
      username: admin
      password: 123456

      # 任务队列配置
      task:
        exchange: collect.task
        queue: collect.task.queue
        routing-key: collect.task

      # 结果队列配置
      result:
        exchange: collect.result
        queue: collect.result.queue
        routing-key: collect.result

      # 分片配置
      sharding:
        enabled: true
        total: 4      # 分片总数
```

## sql.sql

```sql
-- 任务配置表
CREATE TABLE task_config
(
    id              bigint      NOT NULL AUTO_INCREMENT,
    task_id         varchar(64) NOT NULL COMMENT '任务ID',
    task_name       varchar(64) NOT NULL COMMENT '任务名称',
    task_handler    varchar(64) NOT NULL COMMENT '任务处理器',
    cron_expression varchar(64) COMMENT 'cron表达式',
    props           json COMMENT '任务属性',
    status          tinyint     NOT NULL COMMENT '状态:0-禁用,1-启用',
    create_time     datetime    NOT NULL,
    update_time     datetime    NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_task_id (task_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务配置表';
```

## 任务.md

```markdown

```

