# Project Structure

```
platform-collect/
    pom.xml
    src/
        main/
            java/
                com/
                    study/
                        collect/
                            CollectApplication.java
                            api/
                                controller/
                                    CollectController.java
                                    CollectStatsController.java
                                    CollectSyncController.java
                                    CollectTaskController.java
                                data/
                                    DataCompareController.java
                                    DataQueryController.java
                                monitor/
                                    AlertController.java
                                    MetricsController.java
                            common/
                                annotation/
                                    collect/
                                        Collector.java
                                        CollectTrace.java
                                        Processor.java
                                    lock/
                                        CacheLock.java
                                        DistLock.java
                                    monitor/
                                        Alert.java
                                        Metrics.java
                                    validate/
                                        DataValid.java
                                        ParamValid.java
                                constant/
                                    cache/
                                        CacheConstant.java
                                        KeyConstant.java
                                    collect/
                                        CollectConstant.java
                                        TaskConstant.java
                                    data/
                                        DataConstant.java
                                        StatsConstant.java
                                    db/
                                        MongoConstant.java
                                        RedisConstant.java
                                    mq/
                                        MsgConstant.java
                                        QueueConstant.java
                                enums/
                                    collect/
                                        CollectType.java
                                        ProcessType.java
                                        TaskStatus.java
                                    data/
                                        CompareType.java
                                        DataType.java
                                    sync/
                                        SyncStatus.java
                                        SyncType.java
                                exception/
                                    collect/
                                        CollectException.java
                                        ProcessException.java
                                        TaskException.java
                                    data/
                                        DataException.java
                                        ValidateException.java
                                    sync/
                                        LockException.java
                                        SyncException.java
                                    system/
                                        ConfigException.java
                                        SystemException.java
                                model/
                                    dto/
                                        CollectDTO.java
                                        ResultDTO.java
                                        TaskDTO.java
                                    query/
                                        DataQuery.java
                                        StatsQuery.java
                                    result/
                                        PageResult.java
                                        Response.java
                                        TreeResult.java
                                utils/
                                    cache/
                                        CacheUtil.java
                                        KeyUtil.java
                                    collect/
                                        ListUtil.java
                                        TaskUtil.java
                                        TreeUtil.java
                                    common/
                                        DateUtil.java
                                        FileUtil.java
                                        JsonUtil.java
                                        StringUtil.java
                                        ThreadUtil.java
                                        TraceUtil.java
                                    data/
                                        CompareUtil.java
                                        ConvertUtil.java
                                        StatsUtil.java
                                        ValidateUtil.java
                                    lock/
                                        LockUtil.java
                                        SyncUtil.java
                            core/
                                collector/
                                    base/
                                        AbstractCollector.java
                                        CompoundCollector.java
                                        ListCollector.java
                                        TreeCollector.java
                                    compound/
                                        ListDetailCollector.java
                                        MultiSourceCollector.java
                                        TreeDetailCollector.java
                                        TreeListCollector.java
                                    factory/
                                        CollectorFactory.java
                                    list/
                                        IncrListCollector.java
                                        PageListCollector.java
                                        ScrollListCollector.java
                                        StreamListCollector.java
                                    tree/
                                        AsyncTreeCollector.java
                                        LazyTreeCollector.java
                                        RecursiveTreeCollector.java
                                        SimpleTreeCollector.java
                                engine/
                                    AbstractCollectEngine.java
                                    CollectEngine.java
                                executor/
                                    base/
                                        AbstractExecutor.java
                                        CollectExecutor.java
                                    context/
                                        DefaultContext.java
                                        ExecuteContext.java
                                    impl/
                                        AsyncExecutor.java
                                        CompensateExecutor.java
                                        RetryExecutor.java
                                    monitor/
                                        ExecuteMonitor.java
                                        StatusCollector.java
                                impl/
                                    AsyncCollectEngine.java
                                    DistributedEngine.java
                                    IncrementalEngine.java
                                    StandardCollectEngine.java
                                processor/
                                    base/
                                        AbstractProcessor.java
                                        ListProcessor.java
                                        TreeProcessor.java
                                    chain/
                                        ChainBuilder.java
                                        ChainContext.java
                                        ProcessorChain.java
                                    impl/
                                        compare/
                                            ListCompareProcessor.java
                                            TreeCompareProcessor.java
                                        filter/
                                            ListFilter.java
                                            TreeFilter.java
                                        merge/
                                            ListMerger.java
                                            TreeMerger.java
                                        stats/
                                            ListStatsProcessor.java
                                            TreeStatsProcessor.java
                                        storage/
                                            CacheProcessor.java
                                            MongoProcessor.java
                                        sync/
                                            ListSyncProcessor.java
                                            TreeSyncProcessor.java
                                        transform/
                                            ListTransformer.java
                                            TreeTransformer.java
                                        validate/
                                            DataValidator.java
                                            RuleValidator.java
                                scheduler/
                                    base/
                                        AbstractScheduler.java
                                        ScheduleContext.java
                                        TaskScheduler.java
                                    dispatch/
                                        DefaultDispatcher.java
                                        TaskDispatcher.java
                                    monitor/
                                        MetricsCollector.java
                                        ScheduleMonitor.java
                                    strategy/
                                        DispatchStrategy.java
                                        TaskSplitStrategy.java
                                strategy/
                                    balance/
                                        ConsistentHash.java
                                        LoadBalanceStrategy.java
                                        RandomBalance.java
                                    dedup/
                                        DedupStrategy.java
                                        RedisDedupStrategy.java
                                        SimpleDedupStrategy.java
                                    priority/
                                        DynamicPriority.java
                                        PriorityStrategy.java
                                        SimplePriority.java
                                    route/
                                        HashRoute.java
                                        RouteStrategy.java
                            domain/
                                entity/
                                    data/
                                        list/
                                            ListData.java
                                            ListMeta.java
                                        stats/
                                            CollectStats.java
                                            DataStats.java
                                        tree/
                                            TreeMeta.java
                                            TreeNode.java
                                    sync/
                                        SyncConfig.java
                                        SyncResult.java
                                        SyncTask.java
                                    task/
                                        CollectTask.java
                                        SubTask.java
                                        TaskConfig.java
                                        TaskContext.java
                                        TaskResult.java
                                        TaskStats.java
                                    version/
                                        DataVersion.java
                                        VersionMeta.java
                                repository/
                                    data/
                                        ListRepository.java
                                        TreeRepository.java
                                    task/
                                        StatsRepository.java
                                        TaskRepository.java
                                    version/
                                        VersionRepository.java
                                service/
                                    collect/
                                        CollectDomainService.java
                                        StatsQueryService.java
                                        TaskManageService.java
                                    data/
                                        DataQueryService.java
                                        ListDataService.java
                                        TreeDataService.java
                                    sync/
                                        DataSyncService.java
                                        SyncTaskService.java
                                    version/
                                        VersionService.java
                            infrastructure/
                                config/
                                    cache/
                                        RedisConfig.java
                                    db/
                                        MongoConfig.java
                                        MysqlConfig.java
                                    mq/
                                        KafkaConfig.java
                                        RabbitConfig.java
                                    thread/
                                        ThreadPoolConfig.java
                                lock/
                                    aspect/
                                        LockAspect.java
                                    impl/
                                        RedisLock.java
                                        ZkLock.java
                                monitor/
                                    alert/
                                        AlertManager.java
                                        AlertNotifier.java
                                    metrics/
                                        collector/
                                            DataMetrics.java
                                            SystemMetrics.java
                                            TaskMetrics.java
                                        reporter/
                                            MetricsReporter.java
                                            PrometheusReporter.java
                                    trace/
                                        TraceContext.java
                                        TraceManager.java
                                mq/
                                    config/
                                        ExchangeConfig.java
                                        QueueConfig.java
                                    consumer/
                                        ResultConsumer.java
                                        TaskConsumer.java
                                    message/
                                        CollectMessage.java
                                        SyncMessage.java
                                    producer/
                                        ResultProducer.java
                                        TaskProducer.java
                                persistent/
                                    cache/
                                        key/
                                            KeyGenerator.java
                                        manager/
                                            CacheManager.java
                                        repository/
                                            ListCacheRepository.java
                                            TreeCacheRepository.java
                                    mongo/
                                        converter/
                                            ListConverter.java
                                            TreeConverter.java
                                        repository/
                                            ListRepositoryImpl.java
                                            TreeRepositoryImpl.java
                                        template/
                                            ListTemplate.java
                                            TreeTemplate.java
                                schedule/
                                    elastic/
                                        DynamicScheduler.java
                                        LoadBalancer.java
                                    quartz/
                                        config/
                                            JobFactory.java
                                            QuartzConfig.java
                                        job/
                                            CollectJob.java
                            model/
                                request/
                                    collect/
                                        CollectRequest.java
                                        CompareRequest.java
                                        SyncRequest.java
                                    query/
                                        DataQueryRequest.java
                                        StatsQueryRequest.java
                                response/
                                    collect/
                                        CollectStatsVO.java
                                        DataCompareVO.java
                                    data/
                                        ListDataVO.java
                                        TreeDataVO.java
            resources/
                application.yml
                init.sql
                test.http
                test.sql
                testTree.http
                tree.http
                TreeHttp.http
                treequery.http
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
        <groupId>com.study</groupId>
        <artifactId>platform-parent</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <artifactId>platform-collect</artifactId>
    <packaging>jar</packaging>

    <dependencies>
        <!-- 引入公共模块 -->
        <dependency>
            <groupId>com.study</groupId>
            <artifactId>platform-common</artifactId>
            <version>${project.version}</version>
        </dependency>

        <!-- Spring Boot Starters -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-aop</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>

        <!-- RabbitMQ -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-amqp</artifactId>
        </dependency>

        <!-- Redis -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-pool2</artifactId>
        </dependency>

        <!-- MongoDB -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-mongodb</artifactId>
        </dependency>

        <!-- MariaDB -->
        <dependency>
            <groupId>org.mariadb.jdbc</groupId>
            <artifactId>mariadb-java-client</artifactId>
        </dependency>

        <!-- MyBatis -->
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
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

        <!-- Connection Pool -->
        <dependency>
            <groupId>com.zaxxer</groupId>
            <artifactId>HikariCP</artifactId>
        </dependency>

        <!-- Lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- Commons -->
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
            <groupId>org.springframework.amqp</groupId>
            <artifactId>spring-rabbit-test</artifactId>
            <scope>test</scope>
        </dependency>

        <dependency>
            <groupId>org.springframework.amqp</groupId>
            <artifactId>spring-rabbit</artifactId>
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

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.study"})
@MapperScan("com.study.collect.mapper")
public class CollectApplication {
    public static void main(String[] args) {
        SpringApplication.run(CollectApplication.class, args);
    }
}
```

## CollectController.java

```java
package com.study.collect.api.controller;

// 基础采集操作
public class CollectController {
}

```

## CollectStatsController.java

```java
package com.study.collect.api.controller;

// 采集统计查询
public class CollectStatsController {
}

```

## CollectSyncController.java

```java
package com.study.collect.api.controller;

// 同步刷新控制
public class CollectSyncController {
}

```

## CollectTaskController.java

```java
package com.study.collect.api.controller;

// 任务管理
public class CollectTaskController {
}

```

## DataCompareController.java

```java
package com.study.collect.api.data;

// 采集数据查询
public class DataCompareController {
}

```

## DataQueryController.java

```java
package com.study.collect.api.data;

// 数据对比分析
public class DataQueryController {
}

```

## AlertController.java

```java
package com.study.collect.api.monitor;

// 指标监控
public class AlertController {
}

```

## MetricsController.java

```java
package com.study.collect.api.monitor;

// 告警管理
public class MetricsController {
}

```

## Collector.java

```java
package com.study.collect.common.annotation.collect;

//采集器注解
public class Collector {
}

```

## CollectTrace.java

```java
package com.study.collect.common.annotation.collect;

// 采集追踪注解
public class CollectTrace {
}

```

## Processor.java

```java
package com.study.collect.common.annotation.collect;

// 处理器注解
public class Processor {
}

```

## CacheLock.java

```java
package com.study.collect.common.annotation.lock;

// 缓存锁注解
public class CacheLock {
}

```

## DistLock.java

```java
package com.study.collect.common.annotation.lock;

// 分布式锁注解
public class DistLock {
}

```

## Alert.java

```java
package com.study.collect.common.annotation.monitor;

// 告警注解
public class Alert {
}

```

## Metrics.java

```java
package com.study.collect.common.annotation.monitor;

// 指标注解
public class Metrics {
}

```

## DataValid.java

```java
package com.study.collect.common.annotation.validate;

// 数据校验注解
public class DataValid {
}

```

## ParamValid.java

```java
package com.study.collect.common.annotation.validate;

// 参数校验注解
public class ParamValid {
}

```

## CacheConstant.java

```java
package com.study.collect.common.constant.cache;

// 缓存常量
public class CacheConstant {
}

```

## KeyConstant.java

```java
package com.study.collect.common.constant.cache;

// 缓存Key常量
public class KeyConstant {
}

```

## CollectConstant.java

```java
package com.study.collect.common.constant.collect;

// 采集常量
public class CollectConstant {
}

```

## TaskConstant.java

```java
package com.study.collect.common.constant.collect;

// 任务常量
public class TaskConstant {
}

```

## DataConstant.java

```java
package com.study.collect.common.constant.data;

// 数据常量
public class DataConstant {
}

```

## StatsConstant.java

```java
package com.study.collect.common.constant.data;

// 统计常量
public class StatsConstant {
}

```

## MongoConstant.java

```java
package com.study.collect.common.constant.db;

// mongo常量
public class MongoConstant {
}

```

## RedisConstant.java

```java
package com.study.collect.common.constant.db;

// Redis常量
public class RedisConstant {
}

```

## MsgConstant.java

```java
package com.study.collect.common.constant.mq;

// 消息常量
public class MsgConstant {
}

```

## QueueConstant.java

```java
package com.study.collect.common.constant.mq;

// 队列常量
public class QueueConstant {
}

```

## CollectType.java

```java
package com.study.collect.common.enums.collect;

// 采集类型
public class CollectType {
}

```

## ProcessType.java

```java
package com.study.collect.common.enums.collect;

// 处理类型
public class ProcessType {
}

```

## TaskStatus.java

```java
package com.study.collect.common.enums.collect;

// 任务状态
public class TaskStatus {
}

```

## CompareType.java

```java
package com.study.collect.common.enums.data;

// 比较类型
public class CompareType {
}

```

## DataType.java

```java
package com.study.collect.common.enums.data;

// 数据类型
public class DataType {
}

```

## SyncStatus.java

```java
package com.study.collect.common.enums.sync;

// 同步状态
public class SyncStatus {
}

```

## SyncType.java

```java
package com.study.collect.common.enums.sync;

// 同步类型
public class SyncType {
}

```

## CollectException.java

```java
package com.study.collect.common.exception.collect;

// 采集异常
public class CollectException {
}

```

## ProcessException.java

```java
package com.study.collect.common.exception.collect;

// 处理异常
public class ProcessException {
}

```

## TaskException.java

```java
package com.study.collect.common.exception.collect;

// 任务异常
public class TaskException {
}

```

## DataException.java

```java
package com.study.collect.common.exception.data;

// 数据异常
public class DataException {
}

```

## ValidateException.java

```java
package com.study.collect.common.exception.data;

// 校验异常
public class ValidateException {
}

```

## LockException.java

```java
package com.study.collect.common.exception.sync;

// 锁异常
public class LockException {
}

```

## SyncException.java

```java
package com.study.collect.common.exception.sync;

// 同步异常
public class SyncException {
}

```

## ConfigException.java

```java
package com.study.collect.common.exception.system;

// 配置异常
public class ConfigException {
}

```

## SystemException.java

```java
package com.study.collect.common.exception.system;

// 系统异常
public class SystemException {
}

```

## CollectDTO.java

```java
package com.study.collect.common.model.dto;

// 采集DTO
public class CollectDTO {
}

```

## ResultDTO.java

```java
package com.study.collect.common.model.dto;

// 结果DTO
public class ResultDTO {
}

```

## TaskDTO.java

```java
package com.study.collect.common.model.dto;

// 任务DTO
public class TaskDTO {
}

```

## DataQuery.java

```java
package com.study.collect.common.model.query;

// 数据查询
public class DataQuery {
}

```

## StatsQuery.java

```java
package com.study.collect.common.model.query;

// 统计查询
public class StatsQuery {
}

```

## PageResult.java

```java
package com.study.collect.common.model.result;

// 分页结果
public class PageResult {
}

```

## Response.java

```java
package com.study.collect.common.model.result;

// 通用响应
public class Response {
}

```

## TreeResult.java

```java
package com.study.collect.common.model.result;

// 树形结果
public class TreeResult {
}

```

## CacheUtil.java

```java
package com.study.collect.common.utils.cache;

// 缓存工具类
public class CacheUtil {
}

```

## KeyUtil.java

```java
package com.study.collect.common.utils.cache;

// 缓存key工具类
public class KeyUtil {
}

```

## ListUtil.java

```java
package com.study.collect.common.utils.collect;

// 列表工具类
public class ListUtil {
}

```

## TaskUtil.java

```java
package com.study.collect.common.utils.collect;

// 任务工具类
public class TaskUtil {
}

```

## TreeUtil.java

```java
package com.study.collect.common.utils.collect;

// 树工具
public class TreeUtil {
}

```

## DateUtil.java

```java
package com.study.collect.common.utils.common;

// 日期工具类
public class DateUtil {
}

```

## FileUtil.java

```java
package com.study.collect.common.utils.common;

// 文件工具类
public class FileUtil {
}

```

## JsonUtil.java

```java
package com.study.collect.common.utils.common;
// JSON工具类
public class JsonUtil {
}

```

## StringUtil.java

```java
package com.study.collect.common.utils.common;
// 字符串工具类
public class StringUtil {
}

```

## ThreadUtil.java

```java
package com.study.collect.common.utils.common;

// 线程工具
public class ThreadUtil {
}

```

## TraceUtil.java

```java
package com.study.collect.common.utils.common;

// 跟踪工具
public class TraceUtil {
}

```

## CompareUtil.java

```java
package com.study.collect.common.utils.data;

// 比较工具
public class CompareUtil {
}

```

## ConvertUtil.java

```java
package com.study.collect.common.utils.data;

// 转换工具
public class ConvertUtil {
}

```

## StatsUtil.java

```java
package com.study.collect.common.utils.data;

// 统计工具
public class StatsUtil {
}

```

## ValidateUtil.java

```java
package com.study.collect.common.utils.data;

// 数据校验工具
public class ValidateUtil {
}

```

## LockUtil.java

```java
package com.study.collect.common.utils.lock;

// 锁工具
public class LockUtil {
}

```

## SyncUtil.java

```java
package com.study.collect.common.utils.lock;

// 同步工具
public class SyncUtil {
}

```

## AbstractCollector.java

```java
package com.study.collect.core.collector.base;

// 采集器基类
public class AbstractCollector {
}

```

## CompoundCollector.java

```java
package com.study.collect.core.collector.base;

// 复合采集基类
public class CompoundCollector {
}

```

## ListCollector.java

```java
package com.study.collect.core.collector.base;

// 列表采集基类
public class ListCollector {
}

```

## TreeCollector.java

```java
package com.study.collect.core.collector.base;

// 树形采集基类
public class TreeCollector {
}

```

## ListDetailCollector.java

```java
package com.study.collect.core.collector.compound;

// 列表详情收集器
public class ListDetailCollector {
}

```

## MultiSourceCollector.java

```java
package com.study.collect.core.collector.compound;

// 多源收集器
public class MultiSourceCollector {
}

```

## TreeDetailCollector.java

```java
package com.study.collect.core.collector.compound;

// 树形明细收集器
public class TreeDetailCollector {
}

```

## TreeListCollector.java

```java
package com.study.collect.core.collector.compound;

// 树形列表收集器
public class TreeListCollector {
}

```

## CollectorFactory.java

```java
package com.study.collect.core.collector.factory;

public class CollectorFactory {
}

```

## IncrListCollector.java

```java
package com.study.collect.core.collector.list;

// 增量列表收集器
public class IncrListCollector {
}

```

## PageListCollector.java

```java
package com.study.collect.core.collector.list;

// 分页列表收集器
public class PageListCollector {
}

```

## ScrollListCollector.java

```java
package com.study.collect.core.collector.list;

//  滚动列表收集器
public class ScrollListCollector {
}

```

## StreamListCollector.java

```java
package com.study.collect.core.collector.list;

// 列表流收集器
public class StreamListCollector {
}

```

## AsyncTreeCollector.java

```java
package com.study.collect.core.collector.tree;

// 异步树收集器
public class AsyncTreeCollector {
}

```

## LazyTreeCollector.java

```java
package com.study.collect.core.collector.tree;

// 懒加载树收集器
public class LazyTreeCollector {
}

```

## RecursiveTreeCollector.java

```java
package com.study.collect.core.collector.tree;

// 递归树收集器
public class RecursiveTreeCollector {
}

```

## SimpleTreeCollector.java

```java
package com.study.collect.core.collector.tree;

// 简单树收集器
public class SimpleTreeCollector {
}

```

## AbstractCollectEngine.java

```java
package com.study.collect.core.engine;

// 抽象基类
public class AbstractCollectEngine {
}

```

## CollectEngine.java

```java
package com.study.collect.core.engine;

// 引擎接口
public class CollectEngine {
}

```

## AbstractExecutor.java

```java
package com.study.collect.core.executor.base;

// 执行器基类
public class AbstractExecutor {
}

```

## CollectExecutor.java

```java
package com.study.collect.core.executor.base;

// 执行器接口
public class CollectExecutor {
}

```

## DefaultContext.java

```java
package com.study.collect.core.executor.context;

// 默认实现
public class DefaultContext {
}

```

## ExecuteContext.java

```java
package com.study.collect.core.executor.context;

// 上下文接口
public class ExecuteContext {
}

```

## AsyncExecutor.java

```java
package com.study.collect.core.executor.impl;

// 异步执行器
public class AsyncExecutor {
}

```

## CompensateExecutor.java

```java
package com.study.collect.core.executor.impl;
// 补偿执行器
public class CompensateExecutor {
}

```

## RetryExecutor.java

```java
package com.study.collect.core.executor.impl;

// 重试执行器
public class RetryExecutor {
}

```

## ExecuteMonitor.java

```java
package com.study.collect.core.executor.monitor;
// 监控接口
public class ExecuteMonitor {
}

```

## StatusCollector.java

```java
package com.study.collect.core.executor.monitor;
// 状态采集
public class StatusCollector {
}

```

## AsyncCollectEngine.java

```java
package com.study.collect.core.impl;

// 异步实现
public class AsyncCollectEngine {
}

```

## DistributedEngine.java

```java
package com.study.collect.core.impl;

// 分布式实现
public class DistributedEngine {
}

```

## IncrementalEngine.java

```java
package com.study.collect.core.impl;

// 增量采集引擎
public class IncrementalEngine {
}

```

## StandardCollectEngine.java

```java
package com.study.collect.core.impl;

// 标准实现
public class StandardCollectEngine {
}

```

## AbstractProcessor.java

```java
package com.study.collect.core.processor.base;

// 处理器基类
public class AbstractProcessor {
}

```

## ListProcessor.java

```java
package com.study.collect.core.processor.base;

// 列表处理器基类
public class ListProcessor {
}

```

## TreeProcessor.java

```java
package com.study.collect.core.processor.base;

// TreeProcessor
public class TreeProcessor {
}

```

## ChainBuilder.java

```java
package com.study.collect.core.processor.chain;

// 链构建器
public class ChainBuilder {
}

```

## ChainContext.java

```java
package com.study.collect.core.processor.chain;

// 链上下文
public class ChainContext {
}

```

## ProcessorChain.java

```java
package com.study.collect.core.processor.chain;

// 处理器链
public class ProcessorChain {
}

```

## ListCompareProcessor.java

```java
package com.study.collect.core.processor.impl.compare;

public class ListCompareProcessor {
}

```

## TreeCompareProcessor.java

```java
package com.study.collect.core.processor.impl.compare;

public class TreeCompareProcessor {
}

```

## ListFilter.java

```java
package com.study.collect.core.processor.impl.filter;

// 列表过滤器
public class ListFilter {
}

```

## TreeFilter.java

```java
package com.study.collect.core.processor.impl.filter;

// 树形过滤器
public class TreeFilter {
}

```

## ListMerger.java

```java
package com.study.collect.core.processor.impl.merge;

// 列表合并器
public class ListMerger {
}

```

## TreeMerger.java

```java
package com.study.collect.core.processor.impl.merge;

// 树合并
public class TreeMerger {
}

```

## ListStatsProcessor.java

```java
package com.study.collect.core.processor.impl.stats;

// 列表统计处理器
public class ListStatsProcessor {
}

```

## TreeStatsProcessor.java

```java
package com.study.collect.core.processor.impl.stats;

// 树形统计处理器
public class TreeStatsProcessor {
}

```

## CacheProcessor.java

```java
package com.study.collect.core.processor.impl.storage;

// 缓存处理器
public class CacheProcessor {
}

```

## MongoProcessor.java

```java
package com.study.collect.core.processor.impl.storage;

// Mongo处理器
public class MongoProcessor {
}

```

## ListSyncProcessor.java

```java
package com.study.collect.core.processor.impl.sync;

// 列表同步处理器
public class ListSyncProcessor {
}

```

## TreeSyncProcessor.java

```java
package com.study.collect.core.processor.impl.sync;

// 树形同步处理器
public class TreeSyncProcessor {
}

```

## ListTransformer.java

```java
package com.study.collect.core.processor.impl.transform;

// 列表转换器
public class ListTransformer {
}

```

## TreeTransformer.java

```java
package com.study.collect.core.processor.impl.transform;

// 树形转换器
public class TreeTransformer {
}

```

## DataValidator.java

```java
package com.study.collect.core.processor.impl.validate;

// 数据校验器
public class DataValidator {
}

```

## RuleValidator.java

```java
package com.study.collect.core.processor.impl.validate;

// 规则校验器
public class RuleValidator {
}

```

## AbstractScheduler.java

```java
package com.study.collect.core.scheduler.base;

// 调度器基类
public class AbstractScheduler {
}

```

## ScheduleContext.java

```java
package com.study.collect.core.scheduler.base;

// 调度上下文
public class ScheduleContext {
}

```

## TaskScheduler.java

```java
package com.study.collect.core.scheduler.base;

// 调度器接口
public class TaskScheduler {
}

```

## DefaultDispatcher.java

```java
package com.study.collect.core.scheduler.dispatch;

// 默认实现
public class DefaultDispatcher {
}

```

## TaskDispatcher.java

```java
package com.study.collect.core.scheduler.dispatch;

// 分发器接口
public class TaskDispatcher {
}

```

## MetricsCollector.java

```java
package com.study.collect.core.scheduler.monitor;

// 指标采集
public class MetricsCollector {
}

```

## ScheduleMonitor.java

```java
package com.study.collect.core.scheduler.monitor;

// 监控接口
public class ScheduleMonitor {
}

```

## DispatchStrategy.java

```java
package com.study.collect.core.scheduler.strategy;

// 分发策略
public class DispatchStrategy {
}

```

## TaskSplitStrategy.java

```java
package com.study.collect.core.scheduler.strategy;

// 任务分片策略
public class TaskSplitStrategy {
}

```

## ConsistentHash.java

```java
package com.study.collect.core.strategy.balance;

// 一致性哈希
public class ConsistentHash {
}

```

## LoadBalanceStrategy.java

```java
package com.study.collect.core.strategy.balance;

// 负载均衡策略
public class LoadBalanceStrategy {
}

```

## RandomBalance.java

```java
package com.study.collect.core.strategy.balance;

// 随机负载均衡
public class RandomBalance {
}

```

## DedupStrategy.java

```java
package com.study.collect.core.strategy.dedup;

// 策略接口
public class DedupStrategy {
}

```

## RedisDedupStrategy.java

```java
package com.study.collect.core.strategy.dedup;

// Redis去重策略
public class RedisDedupStrategy {
}

```

## SimpleDedupStrategy.java

```java
package com.study.collect.core.strategy.dedup;

// 简单去重策略
public class SimpleDedupStrategy {
}

```

## DynamicPriority.java

```java
package com.study.collect.core.strategy.priority;

// 动态优先级
public class DynamicPriority {
}

```

## PriorityStrategy.java

```java
package com.study.collect.core.strategy.priority;

// 优先级策略
public class PriorityStrategy {
}

```

## SimplePriority.java

```java
package com.study.collect.core.strategy.priority;

// 简单优先级
public class SimplePriority {
}

```

## HashRoute.java

```java
package com.study.collect.core.strategy.route;

// 哈希路由
public class HashRoute {
}

```

## RouteStrategy.java

```java
package com.study.collect.core.strategy.route;

// 路由策略
public class RouteStrategy {
}

```

## ListData.java

```java
package com.study.collect.domain.entity.data.list;

// 列表数据
public class ListData {
}

```

## ListMeta.java

```java
package com.study.collect.domain.entity.data.list;

// 列表元数据
public class ListMeta {
}

```

## CollectStats.java

```java
package com.study.collect.domain.entity.data.stats;

// 收集统计
public class CollectStats {
}

```

## DataStats.java

```java
package com.study.collect.domain.entity.data.stats;

// 数据统计
public class DataStats {
}

```

## TreeMeta.java

```java
package com.study.collect.domain.entity.data.tree;

// 树元数据
public class TreeMeta {
}

```

## TreeNode.java

```java
package com.study.collect.domain.entity.data.tree;

// 树节点
public class TreeNode {
}

```

## SyncConfig.java

```java
package com.study.collect.domain.entity.sync;

// 同步配置
public class SyncConfig {
}

```

## SyncResult.java

```java
package com.study.collect.domain.entity.sync;

// 同步结果
public class SyncResult {
}

```

## SyncTask.java

```java
package com.study.collect.domain.entity.sync;

// 同步任务
public class SyncTask {
}

```

## CollectTask.java

```java
package com.study.collect.domain.entity.task;

// 采集任务
public class CollectTask {
}

```

## SubTask.java

```java
package com.study.collect.domain.entity.task;

// 子任务
public class SubTask {
}

```

## TaskConfig.java

```java
package com.study.collect.domain.entity.task;

// 任务配置
public class TaskConfig {
}

```

## TaskContext.java

```java
package com.study.collect.domain.entity.task;

// 任务上下文
public class TaskContext {
}

```

## TaskResult.java

```java
package com.study.collect.domain.entity.task;

// 任务结果
public class TaskResult {
}

```

## TaskStats.java

```java
package com.study.collect.domain.entity.task;

// 任务统计
public class TaskStats {
}

```

## DataVersion.java

```java
package com.study.collect.domain.entity.version;

// 数据版本
public class DataVersion {
}

```

## VersionMeta.java

```java
package com.study.collect.domain.entity.version;

// 版本元数据
public class VersionMeta {
}

```

## ListRepository.java

```java
package com.study.collect.domain.repository.data;

// 列表仓储
public class ListRepository {
}

```

## TreeRepository.java

```java
package com.study.collect.domain.repository.data;

// 树形结构仓库
public class TreeRepository {
}

```

## StatsRepository.java

```java
package com.study.collect.domain.repository.task;

// 统计仓储
public class StatsRepository {
}

```

## TaskRepository.java

```java
package com.study.collect.domain.repository.task;

// 任务仓储
public class TaskRepository {
}

```

## VersionRepository.java

```java
package com.study.collect.domain.repository.version;

// 版本仓库
public class VersionRepository {
}

```

## CollectDomainService.java

```java
package com.study.collect.domain.service.collect;

// 采集领域服务
public class CollectDomainService {
}

```

## StatsQueryService.java

```java
package com.study.collect.domain.service.collect;

// 统计查询服务
public class StatsQueryService {
}

```

## TaskManageService.java

```java
package com.study.collect.domain.service.collect;

// 任务管理服务
public class TaskManageService {
}

```

## DataQueryService.java

```java
package com.study.collect.domain.service.data;

// 数据查询服务
public class DataQueryService {
}

```

## ListDataService.java

```java
package com.study.collect.domain.service.data;

// 列表数据服务
public class ListDataService {
}

```

## TreeDataService.java

```java
package com.study.collect.domain.service.data;

// 树形数据服务
public class TreeDataService {
}

```

## DataSyncService.java

```java
package com.study.collect.domain.service.sync;

// 数据同步服务
public class DataSyncService {
}

```

## SyncTaskService.java

```java
package com.study.collect.domain.service.sync;

//
public class SyncTaskService {
}

```

## VersionService.java

```java
package com.study.collect.domain.service.version;

// 版本服务
public class VersionService {
}

```

## RedisConfig.java

```java
package com.study.collect.infrastructure.config.cache;

// Redis配置
public class RedisConfig {
}

```

## MongoConfig.java

```java
package com.study.collect.infrastructure.config.db;
// MongoDB配置
public class MongoConfig {
}

```

## MysqlConfig.java

```java
package com.study.collect.infrastructure.config.db;

// Mysql配置
public class MysqlConfig {
}

```

## KafkaConfig.java

```java
package com.study.collect.infrastructure.config.mq;

// Kafka配置
public class KafkaConfig {
}

```

## RabbitConfig.java

```java
package com.study.collect.infrastructure.config.mq;

// RabbitMQ配置
public class RabbitConfig {
}

```

## ThreadPoolConfig.java

```java
package com.study.collect.infrastructure.config.thread;

// 线程池配置
public class ThreadPoolConfig {
}

```

## LockAspect.java

```java
package com.study.collect.infrastructure.lock.aspect;

// 锁切面
public class LockAspect {
}

```

## RedisLock.java

```java
package com.study.collect.infrastructure.lock.impl;

// Redis分布式锁
public class RedisLock {
}

```

## ZkLock.java

```java
package com.study.collect.infrastructure.lock.impl;

// zk锁
public class ZkLock {
}

```

## AlertManager.java

```java
package com.study.collect.infrastructure.monitor.alert;

// 告警管理器
public class AlertManager {
}

```

## AlertNotifier.java

```java
package com.study.collect.infrastructure.monitor.alert;

// 告警通知器
public class AlertNotifier {
}

```

## DataMetrics.java

```java
package com.study.collect.infrastructure.monitor.metrics.collector;

// 数据指标
public class DataMetrics {
}

```

## SystemMetrics.java

```java
package com.study.collect.infrastructure.monitor.metrics.collector;

// 系统指标
public class SystemMetrics {
}

```

## TaskMetrics.java

```java
package com.study.collect.infrastructure.monitor.metrics.collector;

// 任务指标
public class TaskMetrics {
}

```

## MetricsReporter.java

```java
package com.study.collect.infrastructure.monitor.metrics.reporter;

// 指标上报器
public class MetricsReporter {
}

```

## PrometheusReporter.java

```java
package com.study.collect.infrastructure.monitor.metrics.reporter;

// Prometheus上报
public class PrometheusReporter {
}

```

## TraceContext.java

```java
package com.study.collect.infrastructure.monitor.trace;

// 追踪上下文
public class TraceContext {
}

```

## TraceManager.java

```java
package com.study.collect.infrastructure.monitor.trace;

// 追踪管理器
public class TraceManager {
}

```

## ExchangeConfig.java

```java
package com.study.collect.infrastructure.mq.config;

// 交换机配置
public class ExchangeConfig {
}

```

## QueueConfig.java

```java
package com.study.collect.infrastructure.mq.config;

// 队列配置
public class QueueConfig {
}

```

## ResultConsumer.java

```java
package com.study.collect.infrastructure.mq.consumer;

// 结果消费者
public class ResultConsumer {
}

```

## TaskConsumer.java

```java
package com.study.collect.infrastructure.mq.consumer;

// 任务消费者
public class TaskConsumer {
}

```

## CollectMessage.java

```java
package com.study.collect.infrastructure.mq.message;

// 采集消息
public class CollectMessage {
}

```

## SyncMessage.java

```java
package com.study.collect.infrastructure.mq.message;

// 同步消息
public class SyncMessage {
}

```

## ResultProducer.java

```java
package com.study.collect.infrastructure.mq.producer;

// 结果生产者
public class ResultProducer {
}

```

## TaskProducer.java

```java
package com.study.collect.infrastructure.mq.producer;

// 任务生产者
public class TaskProducer {
}

```

## KeyGenerator.java

```java
package com.study.collect.infrastructure.persistent.cache.key;

public class KeyGenerator {
}

```

## CacheManager.java

```java
package com.study.collect.infrastructure.persistent.cache.manager;

public class CacheManager {
}

```

## ListCacheRepository.java

```java
package com.study.collect.infrastructure.persistent.cache.repository;

// 列表缓存仓库
public class ListCacheRepository {
}

```

## TreeCacheRepository.java

```java
package com.study.collect.infrastructure.persistent.cache.repository;

// 树形缓存仓库
public class TreeCacheRepository {
}

```

## ListConverter.java

```java
package com.study.collect.infrastructure.persistent.mongo.converter;

// 列表转换器
public class ListConverter {
}

```

## TreeConverter.java

```java
package com.study.collect.infrastructure.persistent.mongo.converter;

// 树转换器
public class TreeConverter {
}

```

## ListRepositoryImpl.java

```java
package com.study.collect.infrastructure.persistent.mongo.repository;

// 列表仓储实现
public class ListRepositoryImpl {
}

```

## TreeRepositoryImpl.java

```java
package com.study.collect.infrastructure.persistent.mongo.repository;

// 树形结构仓库实现
public class TreeRepositoryImpl {
}

```

## ListTemplate.java

```java
package com.study.collect.infrastructure.persistent.mongo.template;

// 列表模板
public class ListTemplate {
}

```

## TreeTemplate.java

```java
package com.study.collect.infrastructure.persistent.mongo.template;

// 树形模板
public class TreeTemplate {
}

```

## DynamicScheduler.java

```java
package com.study.collect.infrastructure.schedule.elastic;

// 动态调度器
public class DynamicScheduler {
}

```

## LoadBalancer.java

```java
package com.study.collect.infrastructure.schedule.elastic;

// 负载均衡器
public class LoadBalancer {
}

```

## JobFactory.java

```java
package com.study.collect.infrastructure.schedule.quartz.config;

// 任务工厂
public class JobFactory {
}

```

## QuartzConfig.java

```java
package com.study.collect.infrastructure.schedule.quartz.config;

// 定时任务配置
public class QuartzConfig {
}

```

## CollectJob.java

```java
package com.study.collect.infrastructure.schedule.quartz.job;

public class CollectJob {
}

```

## CollectRequest.java

```java
package com.study.collect.model.request.collect;

// 采集请求
public class CollectRequest {
}

```

## CompareRequest.java

```java
package com.study.collect.model.request.collect;

// 对比请求
public class CompareRequest {
}

```

## SyncRequest.java

```java
package com.study.collect.model.request.collect;

// 同步请求
public class SyncRequest {
}

```

## DataQueryRequest.java

```java
package com.study.collect.model.request.query;

// 数据查询
public class DataQueryRequest {
}

```

## StatsQueryRequest.java

```java
package com.study.collect.model.request.query;
// 统计查询
public class StatsQueryRequest {
}

```

## CollectStatsVO.java

```java
package com.study.collect.model.response.collect;

// 采集统计
public class CollectStatsVO {
}

```

## DataCompareVO.java

```java
package com.study.collect.model.response.collect;

// 数据对比
public class DataCompareVO {
}

```

## ListDataVO.java

```java
package com.study.collect.model.response.data;

// 树形数据
public class ListDataVO {
}

```

## TreeDataVO.java

```java
package com.study.collect.model.response.data;

// 列表数据
public class TreeDataVO {
}

```

## application.yml

```yaml
server:
  port: 8082

spring:
  application:
    name: platform-collect

  # 数据源配置
  datasource:
    driver-class-name: org.mariadb.jdbc.Driver
    url: jdbc:mariadb://192.168.80.137:3306/collect?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai
    username: root
    password: 123456
    type: com.zaxxer.hikari.HikariDataSource
    # Hikari 连接池配置
    hikari:
      minimum-idle: 5
      maximum-pool-size: 15
      idle-timeout: 30000
      pool-name: CollectHikariCP
      max-lifetime: 1800000
      connection-timeout: 30000
      connection-test-query: SELECT 1

  # MongoDB配置
  data:
    mongodb:
      uri: mongodb://root:123456@192.168.80.137:27017/collect?authSource=admin
      database: collect
      auto-index-creation: true

    # Redis配置
    redis:
      password: 123456
      timeout: 5000

      # 连接池配置
      lettuce:
        pool:
          max-active: 8
          max-idle: 8
          min-idle: 0
          max-wait: 1000

      # 集群配置
      cluster:
        nodes:
          - 192.168.80.137:6379
          - 192.168.80.137:6380
          - 192.168.80.137:6381
          - 192.168.80.137:6382
          - 192.168.80.137:6383
          - 192.168.80.137:6384

  # RabbitMQ配置
  rabbitmq:
    host: 192.168.80.137
    port: 5672
    username: admin
    password: 123456
    virtual-host: /
    publisher-confirm-type: correlated
    publisher-returns: true
    template:
      mandatory: true
    listener:
      simple:
        acknowledge-mode: manual
        concurrency: 3
        max-concurrency: 10
        prefetch: 1
        retry:
          enabled: true
          initial-interval: 1000
          max-attempts: 3
          max-interval: 10000
          multiplier: 2.0

# MyBatis配置
mybatis:
  mapper-locations: classpath:mapper/*.xml
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.slf4j.Slf4jImpl
# 采集任务配置
collect:
  task:
    retry:
      max-attempts: 3
      initial-interval: 1000
      max-interval: 10000
      multiplier: 2.0
    connection:
      connect-timeout: 5000
      read-timeout: 10000
      max-connections: 100
    monitor:
      metrics-interval: 60000
      cleanup-interval: 86400000
# 监控配置
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: always
  metrics:
    tags:
      application: ${spring.application.name}

logging:
  level:
    com.study: debug
    org.springframework.data.mongodb: debug
    org.springframework.amqp: debug
    org.springframework.data.redis: info
```

## init.sql

```sql
CREATE TABLE collect_data
(
    id           BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    device_id    VARCHAR(50) NOT NULL COMMENT '设备ID',
    device_name  VARCHAR(100) COMMENT '设备名称',
    temperature DOUBLE COMMENT '温度',
    humidity DOUBLE COMMENT '湿度',
    location     VARCHAR(200) COMMENT '位置信息',
    collect_time DATETIME    NOT NULL COMMENT '采集时间',
    create_time  DATETIME    NOT NULL COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY          idx_device_time (device_id, collect_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据采集表';
```

## test.http

```
# curl -X POST "http://localhost:8082/api/test/rabbitmq?message=test_message"curl -X POST "http://localhost:8082/api/test/rabbitmq?message=test_message"
POST http://localhost:8082/api/test/rabbitmq?message=test_message

###

# curl -X POST "http://localhost:8082/api/test/mongodb?name=test&description=test_description"curl -X POST "http://localhost:8082/api/test/mongodb?name=test&description=test_description"
POST http://localhost:8082/api/test/mongodb?name=test&description=test_description

###

# curl -X POST "http://localhost:8082/api/test/redis?key=test_key&value=test_value"curl -X POST "http://localhost:8082/api/test/redis?key=test_key&value=test_value"
POST http://localhost:8082/api/test/redis?key=test_key&value=test_value

###

# curl -X POST "http://localhost:8082/api/test/mysql?name=test&description=test_description"curl -X POST "http://localhost:8082/api/test/mysql?name=test&description=test_description"
POST http://localhost:8082/api/test/mysql?name=test&description=test_description

###


```

## test.sql

```sql
CREATE TABLE test_table
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    description TEXT,
    create_time DATETIME     NOT NULL,
    update_time DATETIME     NOT NULL,
    INDEX       idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='测试表';
```

## testTree.http

```
# curl http://localhost:8082/api/test/tree/nodes
GET http://localhost:8082/api/test/tree/nodes

###
# curl -X POST http://localhost:8082/api/tree/collect
#-H "Content-Type: application/json"
#-d '{
#    "url": "http://localhost:8082/api/test/tree/nodes",
#    "method": "GET"
#}'
POST http://localhost:8082/api/tree/collect
Content-Type: application/json

{
  "url": "http://localhost:8082/api/test/tree/nodes",
  "method": "GET"
}

###
# curl http://localhost:8082/api/tree/result/{taskId}
GET http://localhost:8082/api/tree/result/67561c9620a20e5161c8bc65





#    1. 先调用测试API获取树状数据：
#```bash
#curl http://localhost:8082/api/test/tree/nodes
#```
#
#2. 创建采集任务：
#```bash
#curl -X POST http://localhost:8082/api/tree/collect \
#-H "Content-Type: application/json" \
#-d '{
#    "url": "http://localhost:8082/api/test/tree/nodes",
#    "method": "GET"
#}'
#```
```

## tree.http

```
# curl -X POST http://localhost:8082/api/tree/collect
#-H "Content-Type: application/json"
#-d '{
#    "url": "http://example.com/api/tree-data",
#    "method": "GET",
#    "headers": {
#        "Authorization": "Bearer token123"
#    }
#}'
POST http://localhost:8082/api/tree/collect
Content-Type: application/json

{
  "url": "http://example.com/api/tree-data",
  "method": "GET",
  "headers": {
    "Authorization": "Bearer token123"
  }
}

###



# curl http://localhost:8082/api/tree/result/{taskId}
GET http://localhost:8082/api/tree/result/{taskId}

# curl -X POST "http://localhost:8082/api/tree/collect?projectId=project001"
POST http://localhost:8082/api/tree/collect?projectId=project001









## 创建采集任务
#curl -X POST http://localhost:8082/api/tree/collect \
#-H "Content-Type: application/json" \
#-d '{
#    "url": "http://example.com/api/tree-data",
#    "method": "GET",
#    "headers": {
#        "Authorization": "Bearer token123"
#    }
#}'

###
#
#1. 通过API手动触发：
#```bash
## 创建采集任务
####
#
## curl -X POST "http://localhost:8082/api/tree/collect?projectId=project001
#POST http://localhost:8082/api/tree/collect?projectId=project001
#
####
#
#"
#
## 查看任务结果
#curl http://localhost:8082/api/tree/result/{taskId}
#
#```
#
#2. 通过调度器自动执行：
#```java
#// 创建调度任务
#JobDetail job = JobBuilder.newJob(TreeCollectJob.class)
#    .withIdentity("treeCollect", "defaultGroup")
#    .usingJobData("projectId", "project001")
#    .build();
#
#Trigger trigger = TriggerBuilder.newTrigger()
#    .withSchedule(CronScheduleBuilder.cronSchedule("0 0 1 * * ?"))
#    .build();
#
#scheduler.scheduleJob(job, trigger);
#```
#
#
#


# curl http://localhost:8082/api/tree/nodes/project001curl http://localhost:8082/api/tree/nodes/project001
GET http://localhost:8082/api/tree/nodes/project001

###

# curl -X POST http://localhost:8082/api/tree/collect/project001curl -X POST http://localhost:8082/api/tree/collect/project001
POST http://localhost:8082/api/tree/collect/project001

###

#1. 启动应用后，调用接口创建一个采集任务：
#```bash
#curl -X POST http://localhost:8082/api/tree/collect/project001
#```
#
#2. 查看采集的树状结构：
#```bash
#curl http://localhost:8082/api/tree/nodes/project001
#```




```

## TreeHttp.http

```
### Test Tree Data Requests
# 获取测试树数据
GET http://localhost:8082/api/test/tree/nodes

### Tree Collection Requests
### 创建树结构采集任务
POST http://localhost:8082/api/tree/collect
Content-Type: application/json

{
  "url": "http://localhost:8082/api/test/tree/nodes",
  "method": "GET",
  "headers": {
    "Authorization": "Bearer your-token"
  }
}

### 查看采集任务结果
GET http://localhost:8082/api/tree/result/{{taskId}}

### Tree Node Query Requests
# 获取项目树结构
GET http://localhost:8082/api/tree/query/project/test-project

# 获取指定节点的子树
GET http://localhost:8082/api/tree/query/node/p/test-project/root1

# 按类型查询节点
GET http://localhost:8082/api/tree/query/type/test-project/TEST_CASE

# 查询直接子节点
GET http://localhost:8082/api/tree/query/children/p/test-project/root1/baseline/cases

### Tree Operation Requests
# 获取项目节点
GET http://localhost:8082/api/tree/nodes/test-project

# 启动项目采集
POST http://localhost:8082/api/tree/collect/test-project

### Usage Examples:
# 完整的采集流程示例:
# 1. 先获取测试数据
GET http://localhost:8082/api/test/tree/nodes

### 2. 创建采集任务
POST http://localhost:8082/api/tree/collect
Content-Type: application/json

{
  "url": "http://localhost:8082/api/test/tree/nodes",
  "method": "GET"
}

### 3. 查询采集结果
GET http://localhost:8082/api/tree/result/675622fe1220e3449e70a20d

### 4.手动执行项目采集
POST http://localhost:8082/api/tree/collect/675623111220e3449e70a20e/execute

### Advanced Queries
### 1. 获取完整项目树
GET http://localhost:8082/api/tree/query/project/test-project

### 2. 查看特定节点的子树
GET http://localhost:8082/api/tree/query/node/p/test-project/root1

### 3. 查找特定类型的节点
GET http://localhost:8082/api/tree/query/type/test-project/TEST_CASE

### 4. 获取直接子节点
GET http://localhost:8082/api/tree/query/children/p/test-project/root1/baseline/cases

### Data Storage Info:
##Database: MongoDB
##Collection: tree_nodes
```

## treequery.http

```
# curl http://localhost:8082/api/tree/query/project/test-project
GET http://localhost:8082/api/tree/query/project/test-project

###

# curl http://localhost:8082/api/tree/query/node/p/test-project/root1
GET http://localhost:8082/api/tree/query/node/p/test-project/root1

###

# curl http://localhost:8082/api/tree/query/type/test-project/TEST_CASE
GET http://localhost:8082/api/tree/query/type/test-project/TEST_CASE

###
# curl http://localhost:8082/api/tree/query/children/p/test-project/root1/baseline/cases
GET http://localhost:8082/api/tree/query/children/p/test-project/root1/baseline/cases

###


#
#
#
#    使用方式：
#
#1. 查询完整项目树：
#```bash
#curl http://localhost:8082/api/tree/query/project/test-project
#```
#
#2. 查询特定节点子树：
#```bash
#curl http://localhost:8082/api/tree/query/node/p/test-project/root1
#```
#
#3. 查询特定类型节点：
#```bash
#curl http://localhost:8082/api/tree/query/type/test-project/TEST_CASE
#```
#
#4. 查询直接子节点：
#```bash
#curl http://localhost:8082/api/tree/query/children/p/test-project/root1/baseline/cases
#```
#
#存储位置：
#- 数据库：MongoDB
#- 集合名：tree_nodes
#- 连接配置：在 application.yml 中配置
#
#可以使用MongoDB命令行工具直接查看：
#```javascript
#// 连接到MongoDB
#mongo mongodb://root:123456@192.168.80.137:27017/collect
#
#// 查看所有节点
#db.tree_nodes.find()
#
#// 查看特定项目的节点
#db.tree_nodes.find({"projectId": "test-project"})
#
#// 查看特定类型的节点
#db.tree_nodes.find({"type": "TEST_CASE"})
#
#// 按路径查询
#db.tree_nodes.find({"path": /^\/p\/test-project\/root1/})
#```
#
#这样就可以通过API或直接操作MongoDB来查询树结构数据了。需要更详细的解释吗？
```

