# Project Structure

```
platform-collect/
    pom.xml
    collect-business/
        pom.xml
        business-testcase/
            pom.xml
            src/
                main/
                    java/
                        com/
                            study/
                                collect/
                                    business/
                                        testcase/
                                            collector/
                                                EnterpriseCollector.java
                                            config/
                                                TestCaseAutoConfiguration.java
                                                TestCaseCollectorProperties.java
                                                TestCaseConfig.java
                                            controller/
                                                EnterpriseController.java
                                            handler/
                                                EnterpriseTaskHandler.java
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
                                            utils/
                                                HttpUtil.java
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
                                        GlobalExceptionHandler.java
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
                                        RabbitMQErrorHandler.java
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
                                            SchedulerConfiguration.java
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
    collect-info/
        架构逻辑.md
        架构分析/
            Claude-Optimizing Distributed Data Collection Platform (1).md
            任务调度优化.md
            架构分析.md
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
<!--        <module>business-enterprise</module>-->
        <module>business-testcase</module>
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
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.study</groupId>
        <artifactId>collect-business</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <artifactId>business-testcase</artifactId>
    <packaging>jar</packaging>

    <name>business-testcase</name>
    <url>http://maven.apache.org</url>
    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>
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
package com.study.collect.business.testcase.collector;

import com.study.collect.business.testcase.model.Enterprise;
import com.study.collect.business.testcase.repository.EnterpriseRepository;
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

## TestCaseAutoConfiguration.java

```java
package com.study.collect.business.testcase.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.study.collect.business.testcase")
public class TestCaseAutoConfiguration {
}
```

## TestCaseCollectorProperties.java

```java
package com.study.collect.business.testcase.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "collect.testcase")
public class TestCaseCollectorProperties {
    private int batchSize = 100;  // 批量处理大小
    private int threadCount = 4;  // 处理线程数
    private int retryTimes = 3;   // 重试次数
    private int timeout = 3600;   // 超时时间(秒)
}
```

## TestCaseConfig.java

```java
package com.study.collect.business.testcase.config;


import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class TestCaseConfig {
    @Bean
    @ConditionalOnMissingBean
    public TestCaseCollectorProperties testCaseCollectorProperties() {
        return new TestCaseCollectorProperties();
    }
}
```

## EnterpriseController.java

```java
package com.study.collect.business.testcase.controller;

import com.study.collect.business.testcase.model.Enterprise;
import com.study.collect.business.testcase.model.request.EnterpriseGenerateRequest;
import com.study.collect.business.testcase.model.request.EnterpriseQueryRequest;
import com.study.collect.business.testcase.model.response.EnterpriseQueryResponse;
import com.study.collect.business.testcase.service.EnterpriseService;
import com.study.collect.common.model.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated  // 添加此注解
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

## EnterpriseTaskHandler.java

```java
package com.study.collect.business.testcase.handler;

import com.study.collect.business.testcase.collector.EnterpriseCollector;
import com.study.collect.business.testcase.model.Enterprise;
import com.study.collect.business.testcase.processor.EnterpriseProcessor;
import com.study.collect.core.collector.model.CollectContext;
import com.study.collect.core.collector.model.CollectResult;
import com.study.collect.core.processor.model.ProcessContext;
import com.study.collect.core.task.handler.AbstractTaskHandler;
import com.study.collect.core.task.model.TaskContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class EnterpriseTaskHandler extends AbstractTaskHandler {

    private final EnterpriseCollector collector;
    private final EnterpriseProcessor processor;

    public EnterpriseTaskHandler(EnterpriseCollector collector,
                                 EnterpriseProcessor processor) {
        this.collector = collector;
        this.processor = processor;
    }

    @Override
    public String getType() {
        // 这个type要和task_config表中的task_code一致
        return "enterprise";
    }

    @Override
    protected Object doExecute(TaskContext context) {
        try {
            // 1. 创建采集上下文
            CollectContext<String> collectContext = new CollectContext<>();
            collectContext.setTaskId(context.getTaskId());
            collectContext.setParams(context.getShardParam());
            collectContext.setShardingId(context.getShardIndex());

            // 2. 执行采集
            CollectResult<List<Enterprise>> collectResult = collector.collect(collectContext);

            // 3. 如果采集成功，进行处理
            if (collectResult.isSuccess() && collectResult.getData() != null) {
                // 创建处理上下文
                ProcessContext processContext = new ProcessContext();
                processContext.setTaskId(context.getTaskId());

                // 对采集的每条数据进行处理
                List<Enterprise> processedData = collectResult.getData().stream()
                        .map(data -> processor.process(data, processContext))
                        .collect(Collectors.toList());

                return processedData;
            } else {
                throw new RuntimeException("采集失败: " +
                        collectResult.getErrorMessage());
            }
        } catch (Exception e) {
            log.error("任务执行异常", e);
            throw new RuntimeException("任务执行失败", e);
        }
    }
}

```

## Enterprise.java

```java
package com.study.collect.business.testcase.model;

import com.study.collect.core.storage.entity.VersionEntity;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Document(collection = "enterprise")
public class Enterprise extends VersionEntity {
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
package com.study.collect.business.testcase.model.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EnterpriseGenerateRequest {
    @NotNull(message = "起始编码不能为空")
    private Integer startCode;

    @NotNull(message = "生成数量不能为空")
    @Max(value = 1000, message = "单次生成数量不能超过1000")
    private Integer count;

    private String industry;
    private String regAuthority;
}
```

## EnterpriseQueryRequest.java

```java
package com.study.collect.business.testcase.model.request;

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
package com.study.collect.business.testcase.model.response;

import com.study.collect.business.testcase.model.Enterprise;
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
package com.study.collect.business.testcase.processor;


import com.study.collect.business.testcase.model.Enterprise;
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
package com.study.collect.business.testcase.repository;

import com.study.collect.business.testcase.model.Enterprise;
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
package com.study.collect.business.testcase.service;

import com.study.collect.business.testcase.collector.EnterpriseCollector;
import com.study.collect.business.testcase.model.Enterprise;
import com.study.collect.business.testcase.model.request.EnterpriseQueryRequest;
import com.study.collect.business.testcase.model.response.EnterpriseQueryResponse;
import com.study.collect.business.testcase.repository.EnterpriseRepository;
import com.study.collect.core.mq.message.TaskMessage;
import com.study.collect.core.mq.producer.TaskProducer;
import com.study.collect.core.task.entity.TaskInstance;
import com.study.collect.core.task.service.TaskExecuteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

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

## HttpUtil.java

```java
package com.study.collect.business.testcase.utils;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * HTTP/HTTPS工具类，支持同步/异步请求，自动重试，SSL证书验证绕过
 * 特性：
 * 1. 支持HTTP/HTTPS，自动绕过SSL证书验证
 * 2. 支持同步/异步请求
 * 3. 自动重试机制
 * 4. 线程池管理
 * 5. 支持批量请求
 * 6. 完整的请求/响应日志
 */
public class HttpUtil {
    private static final Logger logger = Logger.getLogger(HttpUtil.class.getName());
    
    // 配置常量
    private static final int CONNECT_TIMEOUT = 5000; // 连接超时时间
    private static final int READ_TIMEOUT = 15000;   // 读取超时时间
    private static final int MAX_RETRY = 3;          // 最大重试次数
    private static final int RETRY_INTERVAL = 1000;  // 重试间隔基数（毫秒）
    
    // 线程池配置
    private static final ExecutorService executorService = new ThreadPoolExecutor(
            10,                 // 核心线程数
            20,                // 最大线程数
            60L,               // 空闲线程存活时间
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1000), // 工作队列
            new ThreadFactory() {
                private int count = 0;
                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r);
                    thread.setName("HttpUtil-Worker-" + count++);
                    thread.setDaemon(true); // 设置为守护线程
                    return thread;
                }
            },
            new ThreadPoolExecutor.CallerRunsPolicy() // 拒绝策略
    );

    /**
     * 静态初始化：配置SSL，允许所有证书
     */
    static {
        disableSslVerification();
    }

    /**
     * HTTP响应对象
     */
    public static class HttpResponse {
        private final int code;
        private final String body;
        private final Map<String, List<String>> headers;
        private final long responseTime; // 响应时间（毫秒）

        public HttpResponse(int code, String body, Map<String, List<String>> headers, long responseTime) {
            this.code = code;
            this.body = body;
            this.headers = headers;
            this.responseTime = responseTime;
        }

        public int getCode() { return code; }
        public String getBody() { return body; }
        public Map<String, List<String>> getHeaders() { return headers; }
        public long getResponseTime() { return responseTime; }

        @Override
        public String toString() {
            return String.format("HttpResponse{code=%d, responseTime=%dms, bodyLength=%d}",
                    code, responseTime, body != null ? body.length() : 0);
        }
    }

    /**
     * 禁用SSL证书验证
     */
    private static void disableSslVerification() {
        try {
            // 创建信任所有证书的TrustManager
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() { return null; }
                public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                public void checkServerTrusted(X509Certificate[] certs, String authType) {}
            }};

            // 安装自定义的SSLContext
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // 配置主机名验证器
            HostnameVerifier allHostsValid = (hostname, session) -> true;
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "SSL verification disable failed", e);
        }
    }

    /**
     * 执行HTTP请求
     * @param method HTTP方法
     * @param urlStr 请求URL
     * @param body 请求体
     * @param headers 请求头
     * @return HTTP响应对象
     */
    public static HttpResponse request(String method, String urlStr, String body, Map<String, String> headers) throws IOException {
        HttpURLConnection conn = null;
        int retryCount = 0;
        long startTime = System.currentTimeMillis();
        
        while (retryCount < MAX_RETRY) {
            try {
                URL url = new URL(urlStr);
                conn = (HttpURLConnection) url.openConnection();
                configureConnection(conn, method, headers);

                // 写入请求体
                if (shouldWriteBody(method, body)) {
                    writeRequestBody(conn, body);
                }

                // 获取响应
                int responseCode = conn.getResponseCode();
                String responseBody = readResponse(conn, responseCode);
                long responseTime = System.currentTimeMillis() - startTime;

                // 记录请求信息
                logRequest(method, urlStr, headers, body, responseCode, responseTime);

                return new HttpResponse(responseCode, responseBody, conn.getHeaderFields(), responseTime);
                
            } catch (IOException e) {
                handleRetry(++retryCount, e, urlStr);
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }
        
        throw new IOException("Max retries exceeded for URL: " + urlStr);
    }

    /**
     * 配置HTTP连接
     */
    private static void configureConnection(HttpURLConnection conn, String method, Map<String, String> headers) throws ProtocolException {
        conn.setRequestMethod(method);
        conn.setConnectTimeout(CONNECT_TIMEOUT);
        conn.setReadTimeout(READ_TIMEOUT);
        conn.setDoOutput(true);
        conn.setDoInput(true);

        // 设置通用headers
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Content-Type", "application/json");
        
        // 设置自定义headers
        if (headers != null) {
            headers.forEach(conn::setRequestProperty);
        }
    }

    /**
     * 判断是否需要写入请求体
     */
    private static boolean shouldWriteBody(String method, String body) {
        return body != null && !body.isEmpty() && 
               (method.equals("POST") || method.equals("PUT") || method.equals("PATCH"));
    }

    /**
     * 写入请求体
     */
    private static void writeRequestBody(HttpURLConnection conn, String body) throws IOException {
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = body.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
    }

    /**
     * 读取响应内容
     */
    private static String readResponse(HttpURLConnection conn, int responseCode) throws IOException {
        try (InputStream is = (responseCode >= 400) ? conn.getErrorStream() : conn.getInputStream()) {
            if (is != null) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                    return br.lines().collect(Collectors.joining("\n"));
                }
            }
        }
        return "";
    }

    /**
     * 处理重试逻辑
     */
    private static void handleRetry(int retryCount, IOException e, String url) throws IOException {
        if (retryCount == MAX_RETRY) {
            throw e;
        }
        
        long sleepTime = (long) (RETRY_INTERVAL * Math.pow(2, retryCount - 1));
        logger.log(Level.WARNING, String.format("Request failed for URL: %s, retry %d/%d after %dms",
                url, retryCount, MAX_RETRY, sleepTime), e);
                
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new IOException("Request interrupted during retry", ie);
        }
    }

    /**
     * 记录请求日志
     */
    private static void logRequest(String method, String url, Map<String, String> headers, 
                                 String body, int responseCode, long responseTime) {
        logger.log(Level.INFO, String.format("HTTP %s %s - Response: %d, Time: %dms",
                method, url, responseCode, responseTime));
    }

    /**
     * 异步执行HTTP请求
     */
    public static CompletableFuture<HttpResponse> asyncRequest(String method, String url, 
                                                             String body, Map<String, String> headers) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return request(method, url, body, headers);
            } catch (IOException e) {
                throw new CompletionException(e);
            }
        }, executorService);
    }

    // 便捷方法
    public static HttpResponse get(String url) throws IOException {
        return request("GET", url, null, null);
    }

    public static HttpResponse get(String url, Map<String, String> headers) throws IOException {
        return request("GET", url, null, headers);
    }

    public static HttpResponse post(String url, String body) throws IOException {
        return request("POST", url, body, null);
    }

    public static HttpResponse post(String url, String body, Map<String, String> headers) throws IOException {
        return request("POST", url, body, headers);
    }

    public static HttpResponse put(String url, String body) throws IOException {
        return request("PUT", url, body, null);
    }

    public static HttpResponse delete(String url) throws IOException {
        return request("DELETE", url, null, null);
    }

    public static HttpResponse patch(String url, String body) throws IOException {
        return request("PATCH", url, body, null);
    }

    // 异步便捷方法
    public static CompletableFuture<HttpResponse> asyncGet(String url) {
        return asyncRequest("GET", url, null, null);
    }

    public static CompletableFuture<HttpResponse> asyncPost(String url, String body) {
        return asyncRequest("POST", url, body, null);
    }

    /**
     * 批量执行GET请求
     */
    public static List<HttpResponse> batchGet(List<String> urls) {
        List<CompletableFuture<HttpResponse>> futures = urls.stream()
                .map(HttpUtil::asyncGet)
                .collect(Collectors.toList());

        return futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    /**
     * 关闭线程池
     */
    public static void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}

```

## spring.factories

```
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
com.study.collect.business.testcase.config.TestCaseAutoConfiguration
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
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-web</artifactId>
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

## GlobalExceptionHandler.java

```java
package com.study.collect.common.exception;

import com.study.collect.common.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

// 2. 创建全局异常处理器
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 处理参数验证异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response<Void> handleValidationExceptions(MethodArgumentNotValidException ex) {
//        List<String> errors = ex.getBindingResult()
//                .getFieldErrors()
//                .stream()
//                .map(FieldError::getDefaultMessage)
//                .collect(Collectors.toList());
//
//        return Response.error("400", String.join(", ", errors));
//    }
//
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public Response<Void> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError ->
                        String.format("%s: %s",
                                fieldError.getField(),
                                fieldError.getDefaultMessage()))
                .collect(Collectors.toList());

        return Response.error("400", "参数验证失败", errors);
    }

    // 处理参数绑定异常
    @ExceptionHandler(BindException.class)
    public Response<Void> handleBindException(BindException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        return Response.error("400", String.join(", ", errors));
    }


    // 处理其他异常
    @ExceptionHandler(Exception.class)
    public Response<Void> handleAllExceptions(Exception ex) {
        log.error("系统异常", ex);
        return Response.error("500", "系统异常");
    }
}

```

## Response.java

```java
package com.study.collect.common.model;

import lombok.Data;

import java.util.List;

@Data
public class Response<T> {
    private String code;
    private String message;
    private T data;

    private List<String> errors;  // 添加错误详情字段

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

    public static <T> Response<T> error(String code, String message, List<String> errors) {
        Response<T> response = new Response<>();
        response.setCode(code);
        response.setMessage(message);
        response.setErrors(errors);
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class CollectorFactory {

    private final CollectorManager collectorManager;

    /**
     * 创建或获取采集器实例
     * 首先尝试从CollectorManager获取已注册的采集器
     * 如果找不到对应的采集器，抛出异常
     */
    public <T, R> ICollector<T, R> createCollector(String type) {
        // 验证参数
        if (!StringUtils.hasText(type)) {
            throw new IllegalArgumentException("采集器类型不能为空");
        }

        try {
            // 从CollectorManager获取已注册的采集器
            return collectorManager.getCollector(type);
        } catch (IllegalStateException e) {
            log.error("创建采集器失败: {}", e.getMessage());
            throw new IllegalArgumentException("无效的采集器类型: " + type);
        }
    }

    /**
     * 检查是否支持指定类型的采集器
     */
    public boolean supportsCollectorType(String type) {
        return collectorManager.hasCollector(type);
    }

    /**
     * 获取所有支持的采集器类型
     */
    public Set<String> getSupportedCollectorTypes() {
        return collectorManager.getCollectorTypes();
    }
}
```

## CollectorManager.java

```java
package com.study.collect.core.collector.manager;

import com.study.collect.core.collector.ICollector;
import com.study.collect.core.collector.annotation.Collector;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class CollectorManager {

    private final Map<String, ICollector<?, ?>> collectors = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        registerCollectors();
    }

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 扫描并注册所有带有@Collector注解的采集器
     */
    private void registerCollectors() {
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(Collector.class);
        beans.values().forEach(bean -> {
            Collector annotation = bean.getClass().getAnnotation(Collector.class);
            if (null != annotation && annotation.enabled()) {
                ICollector<?, ?> collector = (ICollector<?, ?>) bean;
                registerCollector(collector.getType(), collector);
            }
        });
    }

    /**
     * 注册单个采集器
     */
    public void registerCollector(String type, ICollector<?, ?> collector) {
        if (collectors.containsKey(type)) {
            throw new IllegalStateException("采集器类型已存在: " + type);
        }
        collectors.put(type, collector);
        log.info("注册采集器: type={}, class={}", type, collector.getClass().getName());
    }

    /**
     * 检查采集器是否已注册
     */
    public boolean hasCollector(String type) {
        return collectors.containsKey(type);
    }

    /**
     * 获取已注册的采集器
     * 如果采集器未注册，抛出异常
     */
    @SuppressWarnings("unchecked")
    public <T, R> ICollector<T, R> getCollector(String type) {
        ICollector<?, ?> collector = collectors.get(type);
        if (collector == null) {
            throw new IllegalStateException("采集器未注册: " + type);
        }
        return (ICollector<T, R>) collector;
    }

    /**
     * 获取所有已注册的采集器类型
     */
    public Set<String> getCollectorTypes() {
        return Collections.unmodifiableSet(collectors.keySet());
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

## RabbitMQErrorHandler.java

```java
package com.study.collect.core.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.stereotype.Component;
import org.springframework.util.ErrorHandler;

@Component
@Slf4j
public class RabbitMQErrorHandler implements ErrorHandler {

    @Override
    public void handleError(Throwable t) {
        log.error("RabbitMQ message processing error", t);

        if (t instanceof MessageConversionException) {
            // 消息转换错误处理
            log.error("Message conversion failed", t);
        } else if (t instanceof ListenerExecutionFailedException) {
            // 监听器执行错误处理
            log.error("Listener execution failed", t);
        }
    }
}
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
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
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
        // 配置消息转换器
        Jackson2JsonMessageConverter messageConverter = new Jackson2JsonMessageConverter();
        template.setMessageConverter(messageConverter);
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);

        // 为消费者配置相同的消息转换器
        Jackson2JsonMessageConverter messageConverter = new Jackson2JsonMessageConverter();
        factory.setMessageConverter(messageConverter);

        return factory;
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
import org.springframework.messaging.handler.annotation.Payload;
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

    @RabbitListener(
            queues = "${collect.mq.rabbit.task.queue}",
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void onTaskMessage(@Payload TaskMessage message) {
        String instanceId = message.getInstanceId();
        try {
            log.info("Received task message: instanceId={}, taskCode={}, shard={}/{}",
                    message.getInstanceId(),
                    message.getTaskId(),
                    message.getShardIndex() + 1,
                    message.getShardTotal()
            );

//    @RabbitListener(queues = "${collect.mq.rabbit.task.queue}")
//    public void onTaskMessage(TaskMessage message) {

//        log.info("Received task message: instanceId={}, taskCode={}, shard={}/{}",
//                instanceId,
//                message.getTaskId(),
//                message.getShardIndex() + 1,
//                message.getShardTotal()
//        );
//
//        try {
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

        if (result.getSuccess()) {
            // 发送成功结果
            sendSuccessResult(message, result.getData());
        } else {
            // 发送失败结果
            sendFailureResult(message, result.getErrorMessage());
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

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
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
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
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

//    public void sendTask(TaskMessage message) {
//        try {
//            MQProperties.RabbitMQ.Queue taskQueue = mqProperties.getRabbit().getTask();
//            rabbitTemplate.convertAndSend(
//                    taskQueue.getExchange(),
//                    taskQueue.getRoutingKey(),
//                    message
//            );
//            log.info("Task message sent: instanceId={}, taskCode={}, shard={}/{}",
//                    message.getInstanceId(),
//                    message.getTaskId(),
//                    message.getShardIndex() + 1,
//                    message.getShardTotal()
//            );
//        } catch (Exception e) {
//            log.error("Failed to send task message: " + message.getInstanceId(), e);
//            throw new RuntimeException("Message sending failed", e);
//        }
//    }

    public void sendTask(TaskMessage message) {
        try {
            MQProperties.RabbitMQ.Queue taskQueue = mqProperties.getRabbit().getTask();

            // 设置消息属性
            MessageProperties props = new MessageProperties();
            props.setContentType(MessageProperties.CONTENT_TYPE_JSON);
            props.getHeaders().put("__TypeId__", TaskMessage.class.getName());

            Message amqpMessage = rabbitTemplate.getMessageConverter()
                    .toMessage(message, props);

            rabbitTemplate.send(
                    taskQueue.getExchange(),
                    taskQueue.getRoutingKey(),
                    amqpMessage
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
import com.study.collect.core.storage.audit.EntityAuditor;
import com.study.collect.core.storage.repository.BaseMongoRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;


@Configuration
@EnableMongoAuditing
@ConditionalOnProperty(prefix = "spring.data.mongodb", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableMongoRepositories(
        basePackages = "com.study.collect",
        repositoryBaseClass = BaseMongoRepository.class
)
@ConfigurationProperties(prefix = "spring.data.mongodb")
public class MongoConfig extends AbstractMongoClientConfiguration {
    @Value("${spring.data.mongodb.uri}")
    private String uri;
    @Value("${spring.data.mongodb.database}")
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

    @Bean
    public MongoTemplate mongoTemplate(MongoClient mongoClient) {
        return new MongoTemplate(mongoClient, getDatabaseName());
    }

//    // 添加审计配置
//    @Bean
//    public AuditorAware<String> auditorProvider() {
//        return new EntityAuditor();
//    }
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

        // 配置驼峰命名转换
        org.apache.ibatis.session.Configuration configuration =
                new org.apache.ibatis.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        sessionFactory.setConfiguration(configuration);

        // 设置XML映射文件路径
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sessionFactory.setMapperLocations(resolver.getResources("classpath*:mapper/*.xml"));

        // 设置实体类别名包
        sessionFactory.setTypeAliasesPackage("com.study.collect.core.task.entity");

        return sessionFactory.getObject();
    }

}

```

## SchedulerConfiguration.java

```java
package com.study.collect.core.task.config;

/**
 * 调度器配置
 */
public class SchedulerConfiguration {
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

/**
 * 任务配置类
 */
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

/**
 * 任务配置
 */
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
/**
 * 任务实例
 */
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

/**
 * 任务日志
 */
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

/**
 * 任务处理器抽象类
 */
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

/**
 * 任务处理器
 */
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
//import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 任务处理器管理器
 */
@Slf4j
@Component
public class TaskHandlerManager {

//    private final Map<String, TaskHandler> handlerMap = new HashMap<>();

    @Autowired
    private List<TaskHandler> handlers;

    @PostConstruct
    public void init() {
        handlers.forEach(handler -> handlerMap.put(handler.getType(), handler));
    }

//    public TaskHandler getHandler(String type) {
//        TaskHandler handler = handlerMap.get(type);
//        if (handler == null) {
//            throw new IllegalArgumentException("未找到任务处理器: " + type);
//        }
//        return handler;
//    }

    private final Map<String, TaskHandler> handlerMap = new HashMap<>();

    @Autowired
    public TaskHandlerManager(List<TaskHandler> handlers) {
        handlers.forEach(handler -> {
            handlerMap.put(handler.getType(), handler);
            log.info("注册任务处理器: {}", handler.getType());
        });
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
    TaskConfig selectById(Long id);
    TaskConfig selectByCode(String taskCode);
    List<TaskConfig> selectEnabled();
    void updateStatus(@Param("taskCode") String taskCode, @Param("status") Integer status);
    void deleteByCode(String taskCode);
    List<TaskConfig> selectPage(@Param("taskName") String taskName,
                                @Param("status") Integer status,
                                @Param("offset") int offset,
                                @Param("limit") int limit);
    long countTotal(@Param("taskName") String taskName,
                    @Param("status") Integer status);
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
    TaskInstance selectById(Long id);
    TaskInstance selectByInstanceId(String instanceId);
    List<TaskInstance> selectRunning();
    List<TaskInstance> selectByTaskCode(@Param("taskCode") String taskCode,
                                        @Param("startTime") LocalDateTime startTime,
                                        @Param("endTime") LocalDateTime endTime);
    List<TaskInstance> selectTimeout(@Param("timeoutMinutes") int timeoutMinutes);
    List<TaskInstance> selectByHostName(String hostName);
    int countByStatus(@Param("taskCode") String taskCode,
                      @Param("status") Integer status);
    int cleanHistoryData(@Param("daysBefore") int daysBefore);
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
    List<TaskLog> selectByInstanceId(String instanceId);
    List<TaskLog> selectByTaskCode(@Param("taskCode") String taskCode,
                                   @Param("logType") Integer logType,
                                   @Param("limit") Integer limit);
    List<TaskLog> selectLatestErrors(@Param("limit") int limit);
    int countLogs(@Param("taskCode") String taskCode,
                  @Param("logType") Integer logType);
    int cleanHistoryLogs(@Param("daysBefore") int daysBefore);
    void deleteByInstanceId(String instanceId);
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

/**
 * TaskConfigService
 */
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

/**
 * TaskConfigService
 */
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
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.study.collect.core.task.mapper.TaskConfigMapper">

    <!-- 结果映射 -->
    <resultMap id="taskConfigMap" type="com.study.collect.core.task.entity.TaskConfig">
        <id column="id" property="id"/>
        <result column="task_code" property="taskCode"/>
        <result column="task_name" property="taskName"/>
        <result column="task_handler" property="taskHandler"/>
        <result column="task_param" property="taskParam"/>
        <result column="cron_expr" property="cronExpr"/>
        <result column="shard_total" property="shardTotal"/>
        <result column="retry_times" property="retryTimes"/>
        <result column="retry_interval" property="retryInterval"/>
        <result column="timeout" property="timeout"/>
        <result column="status" property="status"/>
        <result column="remark" property="remark"/>
        <result column="create_time" property="createTime"/>
        <result column="update_time" property="updateTime"/>
    </resultMap>

    <!-- 修改所有select的resultType为resultMap -->
<!--    <select id="selectById" resultMap="taskConfigMap">-->


    <select id="selectPage" resultMap="taskConfigMap">
        SELECT * FROM task_config
        <where>
            <if test="taskName != null and taskName != ''">
                AND task_name LIKE CONCAT('%', #{taskName}, '%')
            </if>
            <if test="status != null">
                AND status = #{status}
            </if>
        </where>
        ORDER BY create_time DESC
        LIMIT #{offset}, #{limit}
    </select>

    <!-- 插入配置 -->
    <insert id="insert" parameterType="TaskConfig" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO task_config (task_code, task_name, task_handler, task_param,
                                 cron_expr, shard_total, retry_times, retry_interval,
                                 timeout, status, remark)
        VALUES (#{taskCode}, #{taskName}, #{taskHandler}, #{taskParam},
                #{cronExpr}, #{shardTotal}, #{retryTimes}, #{retryInterval},
                #{timeout}, #{status}, #{remark})
    </insert>

    <!-- 更新配置 -->
    <update id="update" parameterType="TaskConfig">
        UPDATE task_config
        <set>
            <if test="taskName != null">task_name = #{taskName},</if>
            <if test="taskHandler != null">task_handler = #{taskHandler},</if>
            <if test="taskParam != null">task_param = #{taskParam},</if>
            <if test="cronExpr != null">cron_expr = #{cronExpr},</if>
            <if test="shardTotal != null">shard_total = #{shardTotal},</if>
            <if test="retryTimes != null">retry_times = #{retryTimes},</if>
            <if test="retryInterval != null">retry_interval = #{retryInterval},</if>
            <if test="timeout != null">timeout = #{timeout},</if>
            <if test="status != null">status = #{status},</if>
            <if test="remark != null">remark = #{remark},</if>
            update_time = CURRENT_TIMESTAMP
        </set>
        WHERE task_code = #{taskCode}
    </update>

    <!-- 根据ID查询 -->
    <select id="selectById" resultType="taskConfigMap">
        SELECT *
        FROM task_config
        WHERE id = #{id}
    </select>

    <!-- 根据编码查询 -->
    <select id="selectByCode" resultType="taskConfigMap">
        SELECT *
        FROM task_config
        WHERE task_code = #{taskCode}
    </select>

    <!-- 查询所有启用的配置 -->
    <select id="selectEnabled" resultType="taskConfigMap">
        SELECT *
        FROM task_config
        WHERE status = 1
    </select>

    <!-- 更新状态 -->
    <update id="updateStatus">
        UPDATE task_config
        SET status      = #{status},
            update_time = CURRENT_TIMESTAMP
        WHERE task_code = #{taskCode}
    </update>

    <!-- 删除配置 -->
    <delete id="deleteByCode">
        DELETE
        FROM task_config
        WHERE task_code = #{taskCode}
    </delete>


    <!-- 统计总数 -->
    <select id="countTotal" resultType="long">
        SELECT COUNT(*) FROM task_config
        <where>
            <if test="taskName != null and taskName != ''">
                AND task_name LIKE CONCAT('%', #{taskName}, '%')
            </if>
            <if test="status != null">
                AND status = #{status}
            </if>
        </where>
    </select>
</mapper>
```

## TaskInstanceMapper.xml

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.study.collect.core.task.mapper.TaskInstanceMapper">

    <!-- 结果映射 -->
    <resultMap id="taskInstanceMap" type="com.study.collect.core.task.entity.TaskInstance">
        <id column="id" property="id"/>
        <result column="instance_id" property="instanceId"/>
        <result column="task_code" property="taskCode"/>
        <result column="shard_index" property="shardIndex"/>
        <result column="shard_total" property="shardTotal"/>
        <result column="shard_param" property="shardParam"/>
        <result column="status" property="status"/>
        <result column="error_msg" property="errorMsg"/>
        <result column="host_name" property="hostName"/>
        <result column="start_time" property="startTime"/>
        <result column="end_time" property="endTime"/>
        <result column="create_time" property="createTime"/>
        <result column="update_time" property="updateTime"/>
    </resultMap>

    <!-- 插入实例 -->
    <insert id="insert" parameterType="TaskInstance" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO task_instance (
            instance_id, task_code, shard_index, shard_total,
            shard_param, status, host_name, start_time,
            error_msg
        ) VALUES (
                     #{instanceId}, #{taskCode}, #{shardIndex}, #{shardTotal},
                     #{shardParam}, #{status}, #{hostName}, #{startTime},
                     #{errorMsg}
                 )
    </insert>

    <!-- 更新状态 -->
    <update id="updateStatus">
        UPDATE task_instance
        SET status = #{status},
        <if test="errorMsg != null">error_msg = #{errorMsg},</if>
        update_time = CURRENT_TIMESTAMP
        WHERE instance_id = #{instanceId}
    </update>

    <!-- 更新结束时间 -->
    <update id="updateEndTime">
        UPDATE task_instance
        SET end_time = #{endTime},
            update_time = CURRENT_TIMESTAMP
        WHERE instance_id = #{instanceId}
    </update>

    <!-- 根据ID查询 -->
    <select id="selectById" resultMap="taskInstanceMap">
        SELECT * FROM task_instance WHERE id = #{id}
    </select>

    <!-- 根据实例ID查询 -->
    <select id="selectByInstanceId" resultMap="taskInstanceMap">
        SELECT * FROM task_instance WHERE instance_id = #{instanceId}
    </select>

    <!-- 查询运行中的任务 -->
    <select id="selectRunning" resultMap="taskInstanceMap">
        SELECT * FROM task_instance
        WHERE status = 1
        ORDER BY start_time ASC
    </select>

    <!-- 根据任务编码和时间范围查询 -->
    <select id="selectByTaskCode" resultMap="taskInstanceMap">
        SELECT * FROM task_instance
        WHERE task_code = #{taskCode}
        <if test="startTime != null">
            AND create_time >= #{startTime}
        </if>
        <if test="endTime != null">
            AND create_time &lt;= #{endTime}
        </if>
        ORDER BY create_time DESC
    </select>

    <!-- 查询超时任务 -->
    <select id="selectTimeout" resultMap="taskInstanceMap">
        SELECT * FROM task_instance
        WHERE status = 1
          AND start_time &lt; DATE_SUB(NOW(), INTERVAL #{timeoutMinutes} MINUTE)
    </select>

    <!-- 根据主机名查询任务 -->
    <select id="selectByHostName" resultType="taskInstanceMap">
        SELECT * FROM task_instance
        WHERE host_name = #{hostName}
          AND status = 1
    </select>

    <!-- 根据状态统计任务数 -->
    <select id="countByStatus" resultType="int">
        SELECT COUNT(*) FROM task_instance
        WHERE task_code = #{taskCode}
          AND status = #{status}
    </select>

    <!-- 清理历史数据 -->
    <delete id="cleanHistoryData">
        DELETE FROM task_instance
        WHERE create_time &lt; DATE_SUB(NOW(), INTERVAL #{daysBefore} DAY)
          AND status IN (2, 3, 4, 5)
    </delete>
</mapper>
```

## TaskLogMapper.xml

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.study.collect.core.task.mapper.TaskLogMapper">

    <!-- 结果映射 -->
    <resultMap id="taskLogMap" type="com.study.collect.core.task.entity.TaskLog">
        <id column="id" property="id"/>
        <result column="instance_id" property="instanceId"/>
        <result column="task_code" property="taskCode"/>
        <result column="log_type" property="logType"/>
        <result column="log_content" property="logContent"/>
        <result column="create_time" property="createTime"/>
    </resultMap>

    <!-- 插入日志 -->
    <insert id="insert" parameterType="TaskLog">
        INSERT INTO task_log (
            instance_id, task_code, log_type, log_content
        ) VALUES (
                     #{instanceId}, #{taskCode}, #{logType}, #{logContent}
                 )
    </insert>

    <!-- 批量插入 -->
    <insert id="batchInsert">
        INSERT INTO task_log (
        instance_id, task_code, log_type, log_content
        ) VALUES
        <foreach collection="logs" item="log" separator=",">
            (#{log.instanceId}, #{log.taskCode}, #{log.logType}, #{log.logContent})
        </foreach>
    </insert>

    <!-- 根据实例ID查询 -->
    <select id="selectByInstanceId" resultMap="taskLogMap">
        SELECT * FROM task_log
        WHERE instance_id = #{instanceId}
        ORDER BY create_time ASC
    </select>

    <!-- 根据任务编码和日志类型查询 -->
    <select id="selectByTaskCode" resultMap="taskLogMap">
        SELECT * FROM task_log
        WHERE task_code = #{taskCode}
        <if test="logType != null">
            AND log_type = #{logType}
        </if>
        ORDER BY create_time DESC
        <if test="limit != null">
            LIMIT #{limit}
        </if>
    </select>

    <!-- 查询最新的错误日志 -->
    <select id="selectLatestErrors" resultMap="taskLogMap">
        SELECT * FROM task_log
        WHERE log_type = 5
        ORDER BY create_time DESC
            LIMIT #{limit}
    </select>

    <!-- 清理历史日志 -->
    <delete id="cleanHistoryLogs">
        DELETE FROM task_log
        WHERE create_time &lt; DATE_SUB(NOW(), INTERVAL #{daysBefore} DAY)
    </delete>

    <!-- 根据实例ID删除日志 -->
    <delete id="deleteByInstanceId">
        DELETE FROM task_log
        WHERE instance_id = #{instanceId}
    </delete>
</mapper>
```

## 架构逻辑.md

```markdown
5. 完整的处理流程：
```
1. RabbitMQ接收消息 
   -> TaskConsumer处理消息
   -> TaskHandlerManager找到对应Handler
   -> EnterpriseTaskHandler执行任务
   -> 调用Collector采集数据
   -> 调用Processor处理数据
   -> 返回处理结果
```

6. 数据流转示意图：
```
TaskMessage(MQ) -> TaskContext -> CollectContext -> CollectResult 
-> ProcessContext -> ProcessedData -> TaskResult -> ResultMessage(MQ)
```

这样实现后，系统就能够：
1. 根据消息中的taskId找到对应的处理器
2. 自动完成数据采集和处理
3. 支持分片并行处理
4. 处理异常情况
5. 返回处理结果

需要注意的是：
1. Handler的type要和配置的task_code一致
2. 确保所需的Collector和Processor都已实现
3. 正确处理异常和错误情况
4. 适当的日志记录
5. 合理的事务处理
```

## Claude-Optimizing Distributed Data Collection Platform (1).md

```markdown
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

1.  采集器(Collector)

*   AbstractCollector：抽象基类，提供通用实现
*   支持多种采集模式：树形、列表、复合等
*   可扩展的采集策略

2.  处理器(Processor)

*   AbstractProcessor：处理器基类
*   处理器链：支持多个处理器串联
*   处理器注解：@Processor配置

3.  任务调度(Scheduler)

*   TaskScheduler：调度器接口
*   DefaultScheduler：默认实现
*   支持Cron表达式和分片

4.  存储层(Repository)

*   MongoDB：数据存储
*   Redis：缓存和分布式锁
*   MySQL：任务配置和日志

5.  消息队列(MQ)

*   TaskProducer：任务生产者
*   TaskConsumer：任务消费者
*   支持消息重试和死信

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

1.  任务创建与分发

```java
// 1. 创建任务实例
TaskInstance instance = taskExecuteService.createTaskInstance(...);

// 2. 发送任务消息
TaskMessage message = new TaskMessage();
taskProducer.sendTask(message);
```

2.  任务执行

```java
// 1. 消费者处理
@RabbitListener(queues = "${collect.mq.rabbit.task.queue}")
public void onTaskMessage(TaskMessage message) {
    // 获取处理器执行任务
    TaskHandler handler = handlerManager.getHandler(message.getTaskId());
    TaskResult result = handler.execute(context);
}
```

3.  数据采集与处理

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

1.  可扩展性

*   采集器、处理器可自由扩展
*   支持多种数据源接入
*   插件化的业务模块

2.  高可用性

*   任务分片执行
*   失败重试机制
*   监控告警支持

3.  可靠性

*   事务管理
*   幂等性保证
*   异常处理机制

4.  性能优化

*   数据批量处理
*   缓存优化
*   并行处理支持

您想先从哪个模块开始优化？我们可以针对：

1.  采集器性能优化
2.  处理器链优化
3.  任务调度优化
4.  数据存储优化
5.  监控告警增强

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

1.  增加任务优先级配置
2.  添加依赖任务配置
3.  支持更复杂的调度策略(如：每月最后一天)

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

1.  实现任务队列优先级
2.  添加任务依赖关系处理
3.  增加调度策略的扩展性

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

1.  首先修改 TaskConfig 实体和数据库表，增加新的配置项
2.  实现调度策略接口和具体策略类
3.  实现优先级任务队列
4.  实现依赖任务管理器
5.  改进调度器实现
6.  增加监控和告警功能

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

1.  任务编排功能

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

2.  灾备切换机制

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

3.  任务优化建议

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

4.  安全机制

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

1.  分布式一致性

*   使用ZooKeeper管理调度节点
*   实现分布式锁
*   保证任务不重复执行

2.  高可用设计

*   调度中心多副本部署
*   执行节点动态扩缩容
*   任务失败自动恢复

3.  性能优化

*   任务预热机制
*   批量任务调度
*   资源预分配

4.  可观测性

*   分布式追踪
*   性能指标采集
*   日志聚合分析

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

1.  消息持久化：RabbitMQ提供消息持久化，防止任务丢失
2.  优先级队列：原生支持消息优先级
3.  延迟队列：支持任务延迟执行
4.  死信队列：处理失败任务
5.  负载均衡：自动在消费者之间分配任务
6.  消息确认：确保任务被正确处理

### 注意事项：

1.  消息大小限制：大任务需要考虑消息体积
2.  任务幂等性：确保任务可重试
3.  消息顺序性：需要时要特别处理
4.  集群配置：保证MQ集群高可用
5.  监控告警：及时发现异常情况

四、扩展功能建议
========

1.  添加消息压缩

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

2.  实现任务批量处理

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

3.  添加任务追踪

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
那么重新设置3. 任务调度(Scheduler)和5. 消息队列(MQ)模块的修改建议，记住任务拆解成小任务可以通过rabbitmq分发，单个小任务也是通过rabbitmq分发，请考虑上述并给出一个在现有结构上稳定可行的修改方案，给出要新增修改删除的文件和目录设计，确认后分段输出代码

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

1.  TaskScheduler.java (修改)
2.  DefaultTaskScheduler.java (修改)
3.  TaskDispatcher.java (修改)
4.  TaskMessage.java (修改)
5.  TaskConsumer.java (修改)

三、主要功能模块
========

### 1\. 调度引擎

*   负责任务调度和生命周期管理
*   支持任务拆分和合并
*   处理任务依赖关系
*   管理任务执行状态

### 2\. 任务拆分

*   支持多种拆分策略
*   管理子任务关系
*   提供拆分进度跟踪
*   处理拆分异常

### 3\. 消息分发

*   基于RabbitMQ的消息路由
*   支持任务优先级
*   提供消息重试机制
*   处理任务超时和失败

### 4\. 任务跟踪

*   跟踪任务执行状态
*   聚合子任务进度
*   提供任务恢复机制
*   记录执行历史

四、关键设计点
=======

1.  任务拆分与状态管理

*   主任务与子任务关系维护
*   状态同步和更新机制
*   进度聚合计算

2.  消息队列设计

*   主任务队列
*   子任务队列
*   结果队列
*   死信队列

3.  错误处理

*   任务重试机制
*   失败恢复策略
*   超时处理

4.  数据一致性

*   分布式事务处理
*   状态一致性保证
*   幂等性设计

请确认这个设计方案，我们可以分模块详细实现代码。您觉得这个方案是否合理？需要调整或补充吗？

## Prompt:
这个设计考虑了现有其它模块去继承和实现各自业务功能吗？感觉不够好，重新设计一下。

## Response:
您说得对，让我重新设计一下，考虑与现有业务模块的整合与扩展。

一、核心设计理念
========

1.  基础框架层：提供统一的调度和分发能力
2.  扩展接口层：定义业务模块需要实现的接口
3.  业务适配层：各业务模块的具体实现

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

1.  任务扩展点

```java
public interface TaskExtensionPoint {
    void beforeTaskStart(Task task);
    void afterTaskComplete(Task task, TaskResult result);
    void onTaskError(Task task, Exception e);
}
```

2.  处理器扩展点

```java
public interface ProcessorExtensionPoint {
    void beforeProcess(Task task);
    void afterProcess(Task task);
    void onProcessError(Task task, Exception e);
}
```

3.  调度扩展点

```java
public interface SchedulerExtensionPoint {
    boolean beforeSchedule(Task task);
    void afterSchedule(Task task);
    void onScheduleError(Task task, Exception e);
}
```

这个设计：

1.  提供了统一的框架能力
2.  定义了清晰的扩展接口
3.  保持了业务模块的独立性
4.  支持灵活的功能扩展
5.  便于功能复用

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

1.  任务注册流程：

```
业务模块 -> TaskRegistry.register() -> HandlerManager.registerHandler()
```

2.  定时触发流程：

```
CronTrigger.shouldTrigger() 
  -> ScheduleEngine.schedule() 
    -> TaskHandler.handle() 
      -> TaskDispatcher.dispatch()
```

3.  手动触发流程：

```
TaskController.triggerTask() 
  -> ScheduleEngine.trigger() 
    -> TaskHandler.handle() 
      -> TaskDispatcher.dispatch()
```

4.  消息分发流程：

```
TaskDispatcher.dispatch() 
  -> RabbitMQ 
    -> TaskConsumer.onMessage() 
      -> TaskHandler.handle()
```

这样的设计：

1.  支持多种触发方式
2.  便于扩展业务处理器
3.  统一的任务分发机制
4.  灵活的注册管理
5.  清晰的职责划分

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

1.  任务处理器的具体实现
2.  消息队列的配置和处理
3.  任务分片的实现
4.  进度跟踪的实现
5.  失败重试机制

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

1.  任务实体类和Mapper
2.  任务服务层
3.  RabbitMQ配置和消息定义
4.  任务处理器抽象类和管理器
5.  任务调度器和触发器
6.  消息生产者和消费者

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

1.  TaskConfigService - 任务配置服务
2.  TaskExecuteService - 任务执行服务
3.  TaskLogService - 任务日志服务

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

1.  RabbitMQ配置和消息定义
2.  任务处理器抽象类和管理器

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

1.  任务处理器抽象类和管理器
2.  消息生产者和消费者

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

1.  TaskProducer - 任务消息生产者
2.  TaskConsumer - 任务消息消费者
3.  ResultProducer - 结果消息生产者
4.  ResultConsumer - 结果消息消费者

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

1.  消息生产者(TaskProducer, ResultProducer)的实现
2.  消息消费者(TaskConsumer, ResultConsumer)的实现
3.  完整的消息处理流程

主要特点：

1.  支持消息确认和重试机制
2.  异常处理和错误恢复
3.  结果反馈
4.  日志跟踪

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

1.  基础调度功能
2.  任务分片执行
3.  手动触发控制
4.  定时调度管理

需要我继续实现调度器的监控和告警部分吗？主要包括：

1.  调度监控指标收集
2.  任务执行统计
3.  告警规则和通知

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

1.  指标收集和统计
2.  监控检查
3.  告警消息定义
4.  多渠道告警通知

需要我补充任何部分的实现吗？


```

## 任务调度优化.md

```markdown
让我分析一下当前任务调度相关的核心结构和流程。

# 一、任务调度相关的核心文件结构

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

# 二、当前调度流程分析

### 1. 定时任务触发流程
```
TaskScheduler(定时调度)
  -> TaskConfig(获取配置)
    -> TaskInstance(创建实例)
      -> TaskMessage(生成消息)  
        -> TaskProducer(发送MQ)
          -> TaskConsumer(消费处理)
            -> TaskHandler(执行任务)
```

### 2. 手动触发流程
```
API调用
  -> TaskExecuteService
    -> TaskInstance(创建实例)
      -> TaskMessage(生成消息)
        -> TaskProducer(发送MQ)
          -> TaskConsumer(消费处理)
            -> TaskHandler(执行任务)
```

# 三、存在的问题和优化方向

### 1. 任务配置管理
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

### 2. 任务调度器
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

# 四、具体优化方案

### 1. 任务配置增强
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

### 2. 调度策略接口
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

### 3. 优先级任务队列
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

### 4. 依赖任务处理
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

### 5. 改进的调度器
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

### 6. 监控和告警增强
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

# 五、修改建议

建议按以下步骤进行修改：

1. 首先修改 TaskConfig 实体和数据库表，增加新的配置项
2. 实现调度策略接口和具体策略类
3. 实现优先级任务队列
4. 实现依赖任务管理器
5. 改进调度器实现
6. 增加监控和告警功能

需要我详细展开某个具体部分吗？

让我系统地设计一个更完善的分布式任务调度架构。

# 一、整体架构设计

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

# 二、核心功能模块设计

### 1. 任务模型升级
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

### 2. 调度引擎增强
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

### 3. 资源管理和负载均衡
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

### 4. 任务跟踪和进度管理
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

### 5. 监控告警系统
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

# 三、扩展功能建议

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

# 四、关键技术点

1. 分布式一致性
- 使用ZooKeeper管理调度节点
- 实现分布式锁
- 保证任务不重复执行

2. 高可用设计
- 调度中心多副本部署
- 执行节点动态扩缩容
- 任务失败自动恢复

3. 性能优化
- 任务预热机制
- 批量任务调度
- 资源预分配

4. 可观测性
- 分布式追踪
- 性能指标采集
- 日志聚合分析

需要我详细展开某个具体的模块设计吗？

是的，RabbitMQ完全可以用于分布式任务分发。让我设计一个基于RabbitMQ的分布式任务分发架构。

# 一、RabbitMQ分布式任务分发架构设计

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

# 二、核心实现设计

### 1. 交换机和队列配置
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

### 2. 任务分发器
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

### 3. 工作节点消费者
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

### 4. 资源监控和动态调节
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

### 5. 监控和告警
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

# 三、优势和注意事项

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

# 四、扩展功能建议

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
```

## 架构分析.md

```markdown
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

1.  采集器(Collector)

*   AbstractCollector：抽象基类，提供通用实现
*   支持多种采集模式：树形、列表、复合等
*   可扩展的采集策略

2.  处理器(Processor)

*   AbstractProcessor：处理器基类
*   处理器链：支持多个处理器串联
*   处理器注解：@Processor配置

3.  任务调度(Scheduler)

*   TaskScheduler：调度器接口
*   DefaultScheduler：默认实现
*   支持Cron表达式和分片

4.  存储层(Repository)

*   MongoDB：数据存储
*   Redis：缓存和分布式锁
*   MySQL：任务配置和日志

5.  消息队列(MQ)

*   TaskProducer：任务生产者
*   TaskConsumer：任务消费者
*   支持消息重试和死信

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

1.  任务创建与分发

```java
// 1. 创建任务实例
TaskInstance instance = taskExecuteService.createTaskInstance(...);

// 2. 发送任务消息
TaskMessage message = new TaskMessage();
taskProducer.sendTask(message);
```

2.  任务执行

```java
// 1. 消费者处理
@RabbitListener(queues = "${collect.mq.rabbit.task.queue}")
public void onTaskMessage(TaskMessage message) {
    // 获取处理器执行任务
    TaskHandler handler = handlerManager.getHandler(message.getTaskId());
    TaskResult result = handler.execute(context);
}
```

3.  数据采集与处理

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

1.  可扩展性

*   采集器、处理器可自由扩展
*   支持多种数据源接入
*   插件化的业务模块

2.  高可用性

*   任务分片执行
*   失败重试机制
*   监控告警支持

3.  可靠性

*   事务管理
*   幂等性保证
*   异常处理机制

4.  性能优化

*   数据批量处理
*   缓存优化
*   并行处理支持

您想先从哪个模块开始优化？我们可以针对：

1.  采集器性能优化
2.  处理器链优化
3.  任务调度优化
4.  数据存储优化
5.  监控告警增强

您更关注哪个方面？

好的，我来设计一个基于现有结构，结合RabbitMQ的任务调度与分发方案。

# 一、目录结构设计
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

# 二、需要修改的现有文件
1. TaskScheduler.java (修改)
2. DefaultTaskScheduler.java (修改)
3. TaskDispatcher.java (修改)
4. TaskMessage.java (修改)
5. TaskConsumer.java (修改)

# 三、主要功能模块

### 1. 调度引擎
- 负责任务调度和生命周期管理
- 支持任务拆分和合并
- 处理任务依赖关系
- 管理任务执行状态

### 2. 任务拆分
- 支持多种拆分策略
- 管理子任务关系
- 提供拆分进度跟踪
- 处理拆分异常

### 3. 消息分发
- 基于RabbitMQ的消息路由
- 支持任务优先级
- 提供消息重试机制
- 处理任务超时和失败

### 4. 任务跟踪
- 跟踪任务执行状态
- 聚合子任务进度
- 提供任务恢复机制
- 记录执行历史

# 四、关键设计点

1. 任务拆分与状态管理
- 主任务与子任务关系维护
- 状态同步和更新机制
- 进度聚合计算

2. 消息队列设计
- 主任务队列
- 子任务队列
- 结果队列
- 死信队列

3. 错误处理
- 任务重试机制
- 失败恢复策略
- 超时处理

4. 数据一致性
- 分布式事务处理
- 状态一致性保证
- 幂等性设计

请确认这个设计方案，我们可以分模块详细实现代码。您觉得这个方案是否合理？需要调整或补充吗？
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
<!--        <dependency>-->
<!--            <groupId>com.study</groupId>-->
<!--            <artifactId>business-enterprise</artifactId>-->
<!--            <version>${project.version}</version>-->
<!--        </dependency>-->
        <dependency>
            <groupId>com.study</groupId>
            <artifactId>business-testcase</artifactId>
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

import com.study.collect.business.testcase.config.TestCaseCollectorProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
@EnableAsync
@EnableCaching
@EnableConfigurationProperties({
        TestCaseCollectorProperties.class
})
public class CollectApplication {
    public static void main(String[] args) {
        SpringApplication.run(CollectApplication.class, args);
    }

//    @Bean
//    public ThreadPoolTaskScheduler taskScheduler() {
//        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
//        scheduler.setPoolSize(10);
//        scheduler.setThreadNamePrefix("TaskScheduler-");
//        return scheduler;
//    }
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


  # 允许bean覆盖(解决taskScheduler冲突)
  main:
    allow-bean-definition-overriding: true
  mvc:
    throw-exception-if-no-handler-found: true
  web:
    resources:
      add-mappings: false

  # 数据源配置
  datasource:
    driver-class-name: org.mariadb.jdbc.Driver
    url: jdbc:mariadb://192.168.80.137:3306/collect?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai
    username: root
    password: 123456

  # MongoDB配置
  data:
    mongodb:
      uri: mongodb://root:123456@192.168.80.137:27017
      database: crawler
      auto-index-creation: true

    # Redis配置
    redis:
      password: 123456
      timeout: 5000
      cluster:
        nodes:
          - 192.168.80.137:6379
          - 192.168.80.137:6380
          - 192.168.80.137:6381
          - 192.168.80.137:6382
          - 192.168.80.137:6383
          - 192.168.80.137:6384
      lettuce:
        pool:
          max-active: 8
          max-idle: 8
          min-idle: 0
          max-wait: 1000

  # RabbitMQ配置
  rabbitmq:
    host: 192.168.80.137
    port: 5672
    username: admin
    password: 123456
    listener:
      simple:
        # 配置消费者
        retry:
          enabled: true
          initial-interval: 1000
          max-attempts: 3
          max-interval: 10000
          multiplier: 2.0
        # 设置手动确认
        acknowledge-mode: manual

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

# 采集任务配置
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

  mq:
    rabbit:
      host: 192.168.80.137
      port: 5672
      username: admin
      password: 123456
      enabled: true
      task:
        exchange: collect.task
        queue: collect.task.queue
        routing-key: collect.task
      result:
        exchange: collect.result
        queue: collect.result.queue
        routing-key: collect.result
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

