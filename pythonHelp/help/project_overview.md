# Project Structure

```
springboot3.3.x/
    pom.xml
    platform-common/
        pom.xml
        src/
            main/
                java/
                    com/
                        study/
                            common/
                                entity/
                                    UnmatchedStrategy.java
                                example/
                                    Example.java
                                model/
                                    task/
                                        HttpConfig.java
                                        RetryPolicy.java
                                        TaskDefinition.java
                                        TaskExecution.java
                                        TaskResult.java
                                        TaskStatus.java
                                        TaskType.java
                                service/
                                    VariableProvider.java
                                util/
                                    BeanSortUtils.java
                                    CollectionUtils.java
                                    DatabaseUtils.java
                                    DateTimeUtils.java
                                    DistributedLockUtil.java
                                    FileUtils.java
                                    HttpUtils.java
                                    IdGenerator.java
                                    JsonUtils.java
                                    Result.java
                                    StringUtils.java
                                    TaskTraceUtil.java
                                    VariableUtil.java
                resources/
                    application.yml
    platform-scheduler/
        pom.xml
        src/
            main/
                java/
                    com/
                        study/
                            scheduler/
                                SchedulerApplication.java
                                SchedulerConfig.java
                                config/
                                    AppConfig.java
                                    DatabaseConfig.java
                                    JobConfig.java
                                    JobConfiguration.java
                                    MongoConfig.java
                                    RedisConfig.java
                                crontroller/
                                    CrawlerController.java
                                    TestCrontroller.java
                                entity/
                                    CrawlerRecord.java
                                    CrawlerTask.java
                                    JobInfo.java
                                    JobLog.java
                                    TreeCollectRequest.java
                                exception/
                                    JobException.java
                                job/
                                    BaseJob.java
                                    CollectRetryJob.java
                                    CollectSyncJob.java
                                    CollectTaskJob.java
                                    CustomJob.java
                                    DemoJob.java
                                    HttpJob.java
                                    JobLogCleanTask.java
                                    ScheduledJob.java
                                    TreeCollectorJob.java
                                listener/
                                    SchedulerJobListener.java
                                mapper/
                                    JobInfoMapper.java
                                    JobLogMapper.java
                                model/
                                    job/
                                        HttpTaskResult.java
                                        JobDefinition.java
                                        JobExecution.java
                                        JobMetrics.java
                                repository/
                                    JobDefinitionRepository.java
                                    JobExecutionRepository.java
                                service/
                                    CrawlerService.java
                                    EnhancedJobService.java
                                    JobService.java
                                    YourService.java
                                utils/
                                    HttpClientUtil.java
                                    MongoDBUtils.java
                resources/
                    application.properties
                    SchedulerDatabaseTables.sql
```

# File Contents

## pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xmlns="http://maven.apache.org/POM/4.0.0"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.study</groupId>
    <artifactId>platform-parent</artifactId>
    <!--        <version>${revision}</version>-->
    <version>1.0.0-SNAPSHOT</version>
    <packaging>pom</packaging>

    <properties>
        <revision>1.0.0-SNAPSHOT</revision>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <java.version>21</java.version>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>

        <!-- Spring 相关版本 -->
        <spring-boot.version>3.2.9</spring-boot.version>
        <spring-cloud.version>2023.0.0</spring-cloud.version>
        <spring-cloud-alibaba.version>2022.0.0.0</spring-cloud-alibaba.version>
        <spring-framework.version>6.1.14</spring-framework.version>

        <!-- 数据库相关 -->
        <!--            <mysql.version>8.0.33</mysql.version>-->
        <!-- 将 mysql.version 替换为 mariadb.version -->
        <mariadb.version>3.3.3</mariadb.version>  <!-- 这是最新的稳定版本 -->
        <mybatis.version>3.0.3</mybatis.version>
        <dbcp2.version>2.9.0</dbcp2.version>
        <mongodb.version>5.0.5</mongodb.version>

        <!-- 中间件相关 -->
        <redis.version>3.2.1</redis.version>
        <kafka.version>3.6.1</kafka.version>
        <rabbitmq.version>3.12.12</rabbitmq.version>

        <!-- 工具类 -->
        <hutool.version>5.8.25</hutool.version>
        <commons-lang3.version>3.14.0</commons-lang3.version>
        <commons-io.version>2.15.1</commons-io.version>
        <guava.version>32.1.3-jre</guava.version>
        <checker-qual.version>3.37.0</checker-qual.version>
        <jackson.version>2.17.2</jackson.version>

        <!-- 日志监控 -->
        <logback.version>1.4.14</logback.version>
        <slf4j.version>2.0.12</slf4j.version>
        <prometheus.version>1.12.1</prometheus.version>

        <!-- 测试相关 -->
        <junit-jupiter.version>5.10.2</junit-jupiter.version>
        <mockito.version>5.11.0</mockito.version>
        <xmlunit.version>2.10.0</xmlunit.version>  <!-- 更新到安全版本 -->
        <wiremock.version>2.35.1</wiremock.version>

        <mongodb-driver.version>4.11.1</mongodb-driver.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <!-- 显式声明xmlunit-core的安全版本 -->
            <dependency>
                <groupId>org.xmlunit</groupId>
                <artifactId>xmlunit-core</artifactId>
                <version>${xmlunit.version}</version>
                <scope>test</scope>
            </dependency>

            <!-- Spring Framework -->
            <dependency>
                <groupId>org.springframework</groupId>
                <artifactId>spring-framework-bom</artifactId>
                <version>${spring-framework.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>

            <!-- Spring Boot -->
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>${spring-boot.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>

            <!-- Spring Cloud -->
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>

            <!-- Spring Cloud Alibaba -->
            <!--                <dependency>-->
            <!--                    <groupId>com.alibaba.cloud</groupId>-->
            <!--                    <artifactId>spring-cloud-alibaba-dependencies</artifactId>-->
            <!--                    <version>${spring-cloud-alibaba.version}</version>-->
            <!--                    <type>pom</type>-->
            <!--                    <scope>import</scope>-->
            <!--                </dependency>-->
            <dependency>
                <groupId>com.alibaba.cloud</groupId>
                <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
                <version>${spring-cloud-alibaba.version}</version>
            </dependency>
            <dependency>
                <groupId>com.alibaba.cloud</groupId>
                <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
                <version>${spring-cloud-alibaba.version}</version>
            </dependency>

            <!-- 数据库相关 -->
            <!--                <dependency>-->
            <!--                    <groupId>mysql</groupId>-->
            <!--                    <artifactId>mysql-connector-java</artifactId>-->
            <!--                    <version>${mysql.version}</version>-->
            <!--                </dependency>-->
            <!-- 将 MySQL 依赖替换为 MariaDB -->
            <dependency>
                <groupId>org.mariadb.jdbc</groupId>
                <artifactId>mariadb-java-client</artifactId>
                <version>${mariadb.version}</version>
            </dependency>
            <dependency>
                <groupId>org.mybatis.spring.boot</groupId>
                <artifactId>mybatis-spring-boot-starter</artifactId>
                <version>${mybatis.version}</version>
            </dependency>
            <dependency>
                <groupId>org.apache.commons</groupId>
                <artifactId>commons-dbcp2</artifactId>
                <version>${dbcp2.version}</version>
            </dependency>

            <!-- 工具类 -->
            <dependency>
                <groupId>cn.hutool</groupId>
                <artifactId>hutool-all</artifactId>
                <version>${hutool.version}</version>
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
            <dependency>
                <groupId>org.checkerframework</groupId>
                <artifactId>checker-qual</artifactId>
                <version>${checker-qual.version}</version>
            </dependency>

            <!-- Jackson -->
            <dependency>
                <groupId>com.fasterxml.jackson.core</groupId>
                <artifactId>jackson-core</artifactId>
                <version>${jackson.version}</version>
            </dependency>
            <dependency>
                <groupId>com.fasterxml.jackson.core</groupId>
                <artifactId>jackson-databind</artifactId>
                <version>${jackson.version}</version>
            </dependency>
            <dependency>
                <groupId>com.fasterxml.jackson.core</groupId>
                <artifactId>jackson-annotations</artifactId>
                <version>${jackson.version}</version>
            </dependency>
            <dependency>
                <groupId>com.fasterxml.jackson.datatype</groupId>
                <artifactId>jackson-datatype-jsr310</artifactId>
                <version>${jackson.version}</version>
            </dependency>
            <dependency>
                <groupId>com.fasterxml.jackson.datatype</groupId>
                <artifactId>jackson-datatype-jdk8</artifactId>
                <version>${jackson.version}</version>
            </dependency>

            <!-- 测试相关 -->
            <dependency>
                <groupId>org.junit.jupiter</groupId>
                <artifactId>junit-jupiter</artifactId>
                <version>${junit-jupiter.version}</version>
                <scope>test</scope>
            </dependency>
            <dependency>
                <groupId>org.mockito</groupId>
                <artifactId>mockito-core</artifactId>
                <version>${mockito.version}</version>
                <scope>test</scope>
            </dependency>
            <dependency>
                <groupId>com.github.tomakehurst</groupId>
                <artifactId>wiremock-jre8-standalone</artifactId>
                <version>${wiremock.version}</version>
                <scope>test</scope>
            </dependency>
            <dependency>
                <groupId>org.quartz-scheduler</groupId>
                <artifactId>quartz</artifactId>
                <version>2.5.0-rc1</version>
                <scope>compile</scope>
            </dependency>

            <!-- MongoDB -->
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-data-mongodb</artifactId>
                <version>${spring-boot.version}</version>
            </dependency>

            <!-- MongoDB Reactive -->
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-data-mongodb-reactive</artifactId>
                <version>${spring-boot.version}</version>
            </dependency>

            <!-- MongoDB Driver (如果需要直接使用MongoDB驱动) -->
            <dependency>
                <groupId>org.mongodb</groupId>
                <artifactId>mongodb-driver-sync</artifactId>
                <version>${mongodb-driver.version}</version>
            </dependency>

            <!-- MongoDB Driver Core -->
            <dependency>
                <groupId>org.mongodb</groupId>
                <artifactId>mongodb-driver-core</artifactId>
                <version>${mongodb-driver.version}</version>
            </dependency>
        </dependencies>


    </dependencyManagement>

    <modules>
        <module>platform-common</module>
        <module>platform-scheduler</module>
        <module>platform-gateway</module>
        <module>platform-collect</module>
        <module>platform-dashboard</module>
    </modules>

    <build>
        <pluginManagement>
            <plugins>
                <plugin>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-maven-plugin</artifactId>
                    <version>${spring-boot.version}</version>
                </plugin>
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-compiler-plugin</artifactId>
                    <version>3.11.0</version>
                    <configuration>
                        <parameters>true</parameters>  <!-- 启用参数名保留 -->
                        <source>${java.version}</source>
                        <target>${java.version}</target>
                    </configuration>
                </plugin>
            </plugins>
        </pluginManagement>
    </build>
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
        <artifactId>platform-parent</artifactId>
        <!--        <version>${revision}</version>-->
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <artifactId>platform-common</artifactId>
    <packaging>jar</packaging>

    <dependencies>
        <!-- Spring Boot 基础依赖 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-aop</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>

        <!-- 注册中心 -->
        <!--        <dependency>-->
        <!--            <groupId>com.alibaba.cloud</groupId>-->
        <!--            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>-->
        <!--        </dependency>-->
        <!--        <dependency>-->
        <!--            <groupId>com.alibaba.cloud</groupId>-->
        <!--            <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>-->
        <!--        </dependency>-->

        <!-- 数据库相关 -->
        <!--        <dependency>-->
        <!--            <groupId>mysql</groupId>-->
        <!--            <artifactId>mysql-connector-java</artifactId>-->
        <!--        </dependency>-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jdbc</artifactId>
        </dependency>

        <dependency>
            <groupId>org.mariadb.jdbc</groupId>
            <artifactId>mariadb-java-client</artifactId>
        </dependency>
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-dbcp2</artifactId>
        </dependency>

        <!-- Redis -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>

        <!-- 工具类 -->
        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-all</artifactId>
        </dependency>
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
        </dependency>
        <dependency>
            <groupId>commons-io</groupId>
            <artifactId>commons-io</artifactId>
        </dependency>
        <dependency>
            <groupId>com.google.guava</groupId>
            <artifactId>guava</artifactId>
        </dependency>

        <!-- 日志 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-logging</artifactId>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.redisson</groupId>
            <artifactId>redisson</artifactId>
            <version>3.27.1</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>org.mockito</groupId>
            <artifactId>mockito-core</artifactId>
        </dependency>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
        </dependency>

    </dependencies>
</project>
```

## UnmatchedStrategy.java

```java
package com.study.common.entity;

/**
 * 未匹配项的处理策略
 */
public enum UnmatchedStrategy {
    KEEP_AT_END,     // 未匹配项放在末尾
    KEEP_AT_START,   // 未匹配项放在开头
    KEEP_ORIGINAL    // 未匹配项保持原有顺序
}
```

## Example.java

```java
package com.study.common.example;

import com.study.common.entity.test.Order;
import com.study.common.entity.test.User;
import com.study.common.util.BeanSortUtils;

import java.util.Arrays;
import java.util.List;

public class Example {
    public static void main(String[] args) {
        // 示例1：排序User对象
        List<User> users = Arrays.asList(
                new User(3L, "Alice"),
                new User(1L, "Bob"),
                new User(4L, "Charlie"),
                new User(2L, "David")
        );
        List<Long> userIds = Arrays.asList(1L, 2L, 3L, 4L);

        // 原地排序
        BeanSortUtils.sortByIds(users, userIds, User::getId);

        // 或者创建新的排序列表
        List<User> sortedUsers = BeanSortUtils.sortedByIds(users, userIds, User::getId);

        // 示例2：排序Order对象
        List<Order> orders = Arrays.asList(
                new Order("order3", 100),
                new Order("order1", 200),
                new Order("order2", 300)
        );
        List<String> orderIds = Arrays.asList("order1", "order2", "order3");

        // 使用方法引用
        BeanSortUtils.sortByIds(orders, orderIds, Order::getOrderId);

        // 或使用lambda表达式
        BeanSortUtils.sortByIds(orders, orderIds, order -> order.getOrderId());
    }
}


```

## HttpConfig.java

```java
package com.study.common.model.task;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * HTTP配置
 */
@Data
@Builder
public class HttpConfig {
    private String url;
    private String method;
    private Map<String, String> headers;
    private String body;
    private Map<String, String> parameters;
}

```

## RetryPolicy.java

```java
package com.study.common.model.task;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * 重试策略
 */
@Data
@Builder
public class RetryPolicy {
    private int maxRetries;
    private long retryInterval;
    private boolean exponentialBackoff;
}

```

## TaskDefinition.java

```java
package com.study.common.model.task;


import lombok.Data;
import lombok.Builder;
import java.util.Date;
import java.util.Map;
import java.util.List;

/**
 * 任务定义
 */
@Data
@Builder
public class TaskDefinition {
    private String id;
    private String name;
    private String description;
    private TaskType type;
    private String cronExpression;
    private HttpConfig httpConfig;
    private List<String> variables;
    private Integer priority;
    private RetryPolicy retryPolicy;
    private TaskStatus status;
    private Date createTime;
    private Date updateTime;
    public TaskDefinition() {
    }
}
```

## TaskExecution.java

```java
package com.study.common.model.task;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

/**
 * 任务执行记录
 */
@Data
@Builder
public class TaskExecution {
    private String id;
    private String taskDefId;
    private String traceId;
    private TaskStatus status;
    private Date startTime;
    private Date endTime;
    private Long duration;
    private String result;
    private String error;
    private String nodeInfo;
    private Integer retryCount;

    public TaskExecution() {
    }
}
```

## TaskResult.java

```java
package com.study.common.model.task;

import lombok.Builder;
import lombok.Data;

/**
 * 任务结果
 */
@Data
@Builder
public class TaskResult {
    private boolean success;
    private String message;
    private Object data;
    private Long cost;
}
```

## TaskStatus.java

```java
package com.study.common.model.task;


/**
 * 任务状态
 */
public enum TaskStatus {
    CREATED,
    WAITING,
    RUNNING,
    COMPLETED,
    FAILED,
    STOPPED,
    SKIPPED
}
```

## TaskType.java

```java
package com.study.common.model.task;

/**
 * 任务类型
 */
public enum TaskType {
    SCHEDULED,
    HTTP,
    CUSTOM
}
```

## VariableProvider.java

```java
package com.study.common.service;

/**
 * 变量提供者接口
 */
public interface VariableProvider {
    String getVariableValue(String key);
}

```

## BeanSortUtils.java

```java
package com.study.common.util;

import com.study.common.entity.UnmatchedStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Bean列表排序工具类
 */
public class BeanSortUtils {
    /**
     * 根据指定的id列表顺序对Bean列表进行排序
     *
     * @param beans      要排序的Bean列表
     * @param ids        id顺序列表
     * @param idFunction 从Bean中获取id的函数
     * @param <T>        Bean类型
     * @param <U>        id类型
     */
    public static <T, U> void sortByIds(List<T> beans, List<U> ids, Function<T, U> idFunction) {
        if (beans == null || ids == null || idFunction == null) {
            return;
        }

        // 创建id到位置的映射
        Map<U, Integer> idxMap = new HashMap<>();
        for (int i = 0; i < ids.size(); i++) {
            idxMap.put(ids.get(i), i);
        }

        // 根据映射排序
        beans.sort((b1, b2) -> {
            Integer idx1 = idxMap.get(idFunction.apply(b1));
            Integer idx2 = idxMap.get(idFunction.apply(b2));

            // 如果两个id都存在于ids列表中，按位置排序
            if (idx1 != null && idx2 != null) {
                return idx1.compareTo(idx2);
            }
            // 如果id不在ids列表中，放到最后
            if (idx1 == null && idx2 == null) {
                return 0;
            }
            return idx1 == null ? 1 : -1;
        });
    }

    /**
     * 根据指定的id列表顺序对Bean列表进行排序，并返回新的已排序列表
     *
     * @param beans      要排序的Bean列表
     * @param ids        id顺序列表
     * @param idFunction 从Bean中获取id的函数
     * @param <T>        Bean类型
     * @param <U>        id类型
     * @return 排序后的新列表
     */
    public static <T, U> List<T> sortedByIds(List<T> beans, List<U> ids, Function<T, U> idFunction) {
        if (beans == null) {
            return new ArrayList<>();
        }
        List<T> result = new ArrayList<>(beans);
        sortByIds(result, ids, idFunction);
        return result;
    }

    /**
     * 根据指定的id列表顺序对Bean列表进行排序，并指定未匹配项的处理策略
     */
    public static <T, U> void sortByIds(List<T> beans, List<U> ids,
                                        Function<T, U> idFunction, UnmatchedStrategy unmatchedStrategy) {
        if (beans == null || ids == null || idFunction == null) {
            return;
        }

        Map<U, Integer> idxMap = new HashMap<>();
        for (int i = 0; i < ids.size(); i++) {
            idxMap.put(ids.get(i), i);
        }

        beans.sort((b1, b2) -> {
            Integer idx1 = idxMap.get(idFunction.apply(b1));
            Integer idx2 = idxMap.get(idFunction.apply(b2));

            if (idx1 != null && idx2 != null) {
                return idx1.compareTo(idx2);
            }

            return switch (unmatchedStrategy) {
                case KEEP_AT_END -> idx1 == null ? 1 : -1;
                case KEEP_AT_START -> idx1 == null ? -1 : 1;
                case KEEP_ORIGINAL -> 0;
            };
        });
    }
}
```

## CollectionUtils.java

```java
package com.study.common.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 集合工具类
 */
public class CollectionUtils {
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    public static <T> List<List<T>> partition(List<T> list, int size) {
        if (isEmpty(list)) {
            return List.of();
        }
        int totalSize = list.size();
        int partitionCount = (totalSize + size - 1) / size;
        return IntStream.range(0, partitionCount)
                .mapToObj(i -> list.subList(i * size, Math.min((i + 1) * size, totalSize)))
                .collect(Collectors.toList());
    }
}

```

## DatabaseUtils.java

```java
package com.study.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

/**
 * 数据库工具类
 */
public class DatabaseUtils {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseUtils.class);
    private static final ThreadLocal<String> DATASOURCE_HOLDER = new ThreadLocal<>();

    public static String getDataSource() {
        return DATASOURCE_HOLDER.get();
    }

    public static void setDataSource(String dataSource) {
        DATASOURCE_HOLDER.set(dataSource);
    }

    public static void clearDataSource() {
        DATASOURCE_HOLDER.remove();
    }

    public static <T> T executeWithDataSource(String dataSource, Supplier<T> supplier) {
        try {
            setDataSource(dataSource);
            return supplier.get();
        } finally {
            clearDataSource();
        }
    }

    public static void executeWithDataSource(String dataSource, Runnable runnable) {
        try {
            setDataSource(dataSource);
            runnable.run();
        } finally {
            clearDataSource();
        }
    }
}

```

## DateTimeUtils.java

```java
package com.study.common.util;

import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * 时间工具类
 */
public class DateTimeUtils {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final ZoneId DEFAULT_ZONE = ZoneId.systemDefault();

    public static String formatDate(LocalDate date) {
        return date.format(DATE_FORMATTER);
    }

    public static String formatTime(LocalTime time) {
        return time.format(TIME_FORMATTER);
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DATETIME_FORMATTER);
    }

    public static LocalDate parseDate(String dateStr) {
        return LocalDate.parse(dateStr, DATE_FORMATTER);
    }

    public static LocalTime parseTime(String timeStr) {
        return LocalTime.parse(timeStr, TIME_FORMATTER);
    }

    public static LocalDateTime parseDateTime(String dateTimeStr) {
        return LocalDateTime.parse(dateTimeStr, DATETIME_FORMATTER);
    }

    public static long toEpochMilli(LocalDateTime dateTime) {
        return dateTime.atZone(DEFAULT_ZONE).toInstant().toEpochMilli();
    }

    public static LocalDateTime fromEpochMilli(long epochMilli) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), DEFAULT_ZONE);
    }
}
```

## DistributedLockUtil.java

```java
package com.study.common.util;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 分布式锁工具类
 */
public class DistributedLockUtil {
    private static final Logger logger = LoggerFactory.getLogger(DistributedLockUtil.class);
    private static final String LOCK_PREFIX = "distributed_lock:";
    private static final long DEFAULT_WAIT_TIME = 5;
    private static final long DEFAULT_LEASE_TIME = 30;

    private final RedissonClient redissonClient;

    public DistributedLockUtil(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    /**
     * 获取锁并执行
     *
     * @param lockKey 锁键
     * @param supplier 执行函数
     * @return 执行结果
     */
    public <T> T executeWithLock(String lockKey, Supplier<T> supplier) {
        return executeWithLock(lockKey, DEFAULT_WAIT_TIME, DEFAULT_LEASE_TIME, supplier);
    }

    /**
     * 获取锁并执行,支持自定义等待和租约时间
     *
     * @param lockKey 锁键
     * @param waitTime 等待时间(秒)
     * @param leaseTime 租约时间(秒)
     * @param supplier 执行函数
     * @return 执行结果
     */
    public <T> T executeWithLock(String lockKey, long waitTime, long leaseTime, Supplier<T> supplier) {
        String fullLockKey = LOCK_PREFIX + lockKey;
        RLock lock = redissonClient.getLock(fullLockKey);
        boolean locked = false;

        try {
            locked = lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);
            if (!locked) {
                throw new RuntimeException("Failed to acquire lock: " + fullLockKey);
            }

            logger.debug("Acquired lock: {}", fullLockKey);
            return supplier.get();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted while acquiring lock", e);
        } finally {
            if (locked) {
                lock.unlock();
                logger.debug("Released lock: {}", fullLockKey);
            }
        }
    }

    /**
     * 尝试获取锁
     */
    public boolean tryLock(String lockKey) {
        return tryLock(lockKey, DEFAULT_WAIT_TIME, DEFAULT_LEASE_TIME);
    }

    /**
     * 尝试获取锁,支持自定义等待和租约时间
     */
    public boolean tryLock(String lockKey, long waitTime, long leaseTime) {
        String fullLockKey = LOCK_PREFIX + lockKey;
        RLock lock = redissonClient.getLock(fullLockKey);

        try {
            return lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * 释放锁
     */
    public void unlock(String lockKey) {
        String fullLockKey = LOCK_PREFIX + lockKey;
        RLock lock = redissonClient.getLock(fullLockKey);

        if (lock != null && lock.isHeldByCurrentThread()) {
            lock.unlock();
            logger.debug("Released lock: {}", fullLockKey);
        }
    }
}
```

## FileUtils.java

```java
package com.study.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.UUID;

/**
 * 文件工具类
 */
public class FileUtils {
    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);
    private static final String[] ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif", ".pdf", ".doc", ".docx", ".xls", ".xlsx"};
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    public static boolean isValidFile(String fileName, long fileSize) {
        if (StringUtils.isEmpty(fileName)) {
            return false;
        }

        String extension = getFileExtension(fileName);
        if (!Arrays.asList(ALLOWED_EXTENSIONS).contains(extension.toLowerCase())) {
            logger.warn("Invalid file extension: {}", extension);
            return false;
        }

        if (fileSize > MAX_FILE_SIZE) {
            logger.warn("File size exceeds limit: {}", fileSize);
            return false;
        }

        return true;
    }

    public static String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        return lastDotIndex > 0 ? fileName.substring(lastDotIndex) : "";
    }

    public static String generateUniqueFileName(String originalFileName) {
        String extension = getFileExtension(originalFileName);
        return UUID.randomUUID().toString() + extension;
    }
}
```

## HttpUtils.java

```java
package com.study.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * HTTP工具类 - 基于java.net.http.HttpClient实现
 */
public class HttpUtils {
    private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    private static final String DEFAULT_CONTENT_TYPE = "application/json";
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(30);

    /**
     * 发送GET请求
     */
    public static <T> T get(String url, Class<T> responseType) throws IOException, InterruptedException {
        return get(url, responseType, null, DEFAULT_TIMEOUT);
    }

    /**
     * 发送GET请求（带请求头）
     */
    public static <T> T get(String url, Class<T> responseType, Map<String, String> headers) throws IOException, InterruptedException {
        return get(url, responseType, headers, DEFAULT_TIMEOUT);
    }

    /**
     * 发送GET请求（带请求头和超时时间）
     */
    public static <T> T get(String url, Class<T> responseType, Map<String, String> headers, Duration timeout) throws IOException, InterruptedException {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .timeout(timeout);

        if (headers != null) {
            headers.forEach(requestBuilder::header);
        }

        HttpResponse<String> response = httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
        return parseResponse(response, responseType);
    }

    /**
     * 异步发送GET请求
     */
    public static <T> CompletableFuture<T> getAsync(String url, Class<T> responseType) {
        return getAsync(url, responseType, null, DEFAULT_TIMEOUT);
    }

    /**
     * 异步发送GET请求（带请求头和超时时间）
     */
    public static <T> CompletableFuture<T> getAsync(String url, Class<T> responseType, Map<String, String> headers, Duration timeout) {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .timeout(timeout);

        if (headers != null) {
            headers.forEach(requestBuilder::header);
        }

        return httpClient.sendAsync(requestBuilder.build(), HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> parseResponse(response, responseType));
    }

    /**
     * 发送POST请求
     */
    public static <T> T post(String url, Object body, Class<T> responseType) throws IOException, InterruptedException {
        return post(url, body, responseType, null, DEFAULT_TIMEOUT);
    }

    /**
     * 发送POST请求（带请求头和超时时间）
     */
    public static <T> T post(String url, Object body, Class<T> responseType, Map<String, String> headers, Duration timeout)
            throws IOException, InterruptedException {
        String jsonBody = JsonUtils.toJson(body);

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .uri(URI.create(url))
                .header("Content-Type", DEFAULT_CONTENT_TYPE)
                .timeout(timeout);

        if (headers != null) {
            headers.forEach(requestBuilder::header);
        }

        HttpResponse<String> response = httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
        return parseResponse(response, responseType);
    }

    /**
     * 异步发送POST请求
     */
    public static <T> CompletableFuture<T> postAsync(String url, Object body, Class<T> responseType) {
        return postAsync(url, body, responseType, null, DEFAULT_TIMEOUT);
    }

    /**
     * 异步发送POST请求（带请求头和超时时间）
     */
    public static <T> CompletableFuture<T> postAsync(String url, Object body, Class<T> responseType, Map<String, String> headers, Duration timeout) {
        String jsonBody = JsonUtils.toJson(body);

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .uri(URI.create(url))
                .header("Content-Type", DEFAULT_CONTENT_TYPE)
                .timeout(timeout);

        if (headers != null) {
            headers.forEach(requestBuilder::header);
        }

        return httpClient.sendAsync(requestBuilder.build(), HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> parseResponse(response, responseType));
    }

    /**
     * 解析HTTP响应
     */
    private static <T> T parseResponse(HttpResponse<String> response, Class<T> responseType) {
        int statusCode = response.statusCode();
        String body = response.body();

        if (statusCode >= 200 && statusCode < 300) {
            if (String.class.equals(responseType)) {
                return (T) body;
            }
            return JsonUtils.fromJson(body, responseType);
        }

        throw new HttpResponseException(statusCode, body);
    }

    /**
     * HTTP响应异常
     */
    public static class HttpResponseException extends RuntimeException {
        private final int statusCode;
        private final String responseBody;

        public HttpResponseException(int statusCode, String responseBody) {
            super("HTTP request failed with status code: " + statusCode);
            this.statusCode = statusCode;
            this.responseBody = responseBody;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public String getResponseBody() {
            return responseBody;
        }
    }

    /**
     * HTTP请求配置构建器
     */
    public static class RequestConfigBuilder {
        private Map<String, String> headers;
        private Duration timeout = DEFAULT_TIMEOUT;
        private String contentType = DEFAULT_CONTENT_TYPE;

        public RequestConfigBuilder headers(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public RequestConfigBuilder timeout(Duration timeout) {
            this.timeout = timeout;
            return this;
        }

        public RequestConfigBuilder contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public Map<String, String> getHeaders() {
            return headers;
        }

        public Duration getTimeout() {
            return timeout;
        }

        public String getContentType() {
            return contentType;
        }
    }
}
```

## IdGenerator.java

```java
package com.study.common.util;

/**
 * ID生成工具类
 */
public class IdGenerator {
    private static final SnowflakeIdWorker snowflake = new SnowflakeIdWorker(1, 1);

    public static long nextId() {
        return snowflake.nextId();
    }

    public static String nextIdStr() {
        return String.valueOf(nextId());
    }

    // 内部类：雪花算法实现
    private static class SnowflakeIdWorker {
        private static final long WORKER_ID_BITS = 5L;
        private static final long DATACENTER_ID_BITS = 5L;
        private static final long SEQUENCE_BITS = 12L;
        private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
        private static final long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS);
        private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
        private static final long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
        private static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;
        private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);
        private final long workerId;
        private final long datacenterId;
        private long sequence = 0L;
        private long lastTimestamp = -1L;

        public SnowflakeIdWorker(long workerId, long datacenterId) {
            if (workerId > MAX_WORKER_ID || workerId < 0) {
                throw new IllegalArgumentException("Worker ID can't be greater than " + MAX_WORKER_ID + " or less than 0");
            }
            if (datacenterId > MAX_DATACENTER_ID || datacenterId < 0) {
                throw new IllegalArgumentException("Datacenter ID can't be greater than " + MAX_DATACENTER_ID + " or less than 0");
            }
            this.workerId = workerId;
            this.datacenterId = datacenterId;
        }

        public synchronized long nextId() {
            long timestamp = timeGen();
            if (timestamp < lastTimestamp) {
                throw new RuntimeException("Clock moved backwards. Refusing to generate id for " + (lastTimestamp - timestamp) + " milliseconds");
            }
            if (lastTimestamp == timestamp) {
                sequence = (sequence + 1) & SEQUENCE_MASK;
                if (sequence == 0) {
                    timestamp = tilNextMillis(lastTimestamp);
                }
            } else {
                sequence = 0L;
            }
            lastTimestamp = timestamp;
            return ((timestamp - 1288834974657L) << TIMESTAMP_LEFT_SHIFT) |
                    (datacenterId << DATACENTER_ID_SHIFT) |
                    (workerId << WORKER_ID_SHIFT) |
                    sequence;
        }

        private long tilNextMillis(long lastTimestamp) {
            long timestamp = timeGen();
            while (timestamp <= lastTimestamp) {
                timestamp = timeGen();
            }
            return timestamp;
        }

        private long timeGen() {
            return System.currentTimeMillis();
        }
    }
}
```

## JsonUtils.java

```java
package com.study.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimeZone;

/**
 * JSON工具类 - 加强版
 */
public class JsonUtils {
    private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);

    private static final ObjectMapper objectMapper = JsonMapper.builder()
            // 添加Java8时间模块
            .addModule(new JavaTimeModule())
            // 设置时区
            .defaultTimeZone(TimeZone.getDefault())
            // 禁用DateTime时间戳
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            // 忽略未知属性
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            // 空对象不报错
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            // 启用清理机制，防止JSON注入
            .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
            .build();

    /**
     * 对象转JSON字符串
     */
    public static String toJson(Object object) {
        try {
            return object instanceof String ? (String) object : objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.error("Convert object to json failed", e);
            return null;
        }
    }

    /**
     * JSON字符串转对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            logger.error("Parse json to object failed", e);
            return null;
        }
    }

    /**
     * JSON字符串转复杂对象
     */
    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            logger.error("Parse json to complex object failed", e);
            return null;
        }
    }

    /**
     * 对象转字节数组
     */
    public static byte[] toBytes(Object object) {
        try {
            return objectMapper.writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            logger.error("Convert object to bytes failed", e);
            return new byte[0];
        }
    }

    /**
     * 字节数组转对象
     */
    public static <T> T fromBytes(byte[] bytes, Class<T> clazz) {
        try {
            return objectMapper.readValue(bytes, clazz);
        } catch (Exception e) {
            logger.error("Parse bytes to object failed", e);
            return null;
        }
    }

    /**
     * 判断是否为有效的JSON字符串
     */
    public static boolean isValidJson(String json) {
        try {
            objectMapper.readTree(json);
            return true;
        } catch (JsonProcessingException e) {
            return false;
        }
    }

    /**
     * 格式化JSON字符串
     */
    public static String formatJson(String json) {
        try {
            Object obj = objectMapper.readValue(json, Object.class);
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            logger.error("Format json failed", e);
            return json;
        }
    }

    /**
     * 对象深度克隆
     */
    @SuppressWarnings("unchecked")
    public static <T> T deepCopy(T object) {
        try {
            if (object == null) {
                return null;
            }
            return (T) objectMapper.readValue(
                    objectMapper.writeValueAsString(object),
                    object.getClass()
            );
        } catch (JsonProcessingException e) {
            logger.error("Deep copy object failed", e);
            return null;
        }
    }
}
```

## Result.java

```java
package com.study.common.util;

/**
 * 结果返回工具类
 */
public class Result<T> {
    // 预定义状态码
    public static final int SUCCESS = 200;
    public static final int PARAM_ERROR = 400;
    public static final int UNAUTHORIZED = 401;
    public static final int SERVER_ERROR = 500;
    private Integer code;
    private String message;
    private T data;
    private Long timestamp;

    public Result() {
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("Success");
        result.setData(data);
        return result;
    }

    public static <T> Result<T> error(String message) {
        return error(500, message);
    }

    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    // getters and setters
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Result<T> code(int code) {
        this.code = code;
        return this;
    }

    public Result<T> message(String message) {
        this.message = message;
        return this;
    }
}
```

## StringUtils.java

```java
package com.study.common.util;

/**
 * 字符串工具类
 */
public class StringUtils {
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static String toSnakeCase(String str) {
        if (isEmpty(str)) {
            return str;
        }
        StringBuilder result = new StringBuilder();
        result.append(Character.toLowerCase(str.charAt(0)));
        for (int i = 1; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (Character.isUpperCase(ch)) {
                result.append('_');
                result.append(Character.toLowerCase(ch));
            } else {
                result.append(ch);
            }
        }
        return result.toString();
    }

    public static String toCamelCase(String str) {
        if (isEmpty(str)) {
            return str;
        }
        StringBuilder result = new StringBuilder();
        boolean nextUpper = false;
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch == '_') {
                nextUpper = true;
            } else {
                result.append(nextUpper ? Character.toUpperCase(ch) : ch);
                nextUpper = false;
            }
        }
        return result.toString();
    }
}

```

## TaskTraceUtil.java

```java
package com.study.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.UUID;
import java.util.function.Supplier;

/**
 * 链路追踪工具类
 */
public class TaskTraceUtil {
    private static final Logger logger = LoggerFactory.getLogger(TaskTraceUtil.class);
    private static final String TRACE_ID = "traceId";
    private static final String SPAN_ID = "spanId";
    private static final String PARENT_SPAN_ID = "parentSpanId";

    /**
     * 生成追踪ID
     */
    public static String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 生成Span ID
     */
    public static String generateSpanId() {
        return Long.toHexString(UUID.randomUUID().getMostSignificantBits());
    }

    /**
     * 开始追踪
     */
    public static void startTrace() {
        startTrace(generateTraceId());
    }

    /**
     * 使用指定的追踪ID开始追踪
     */
    public static void startTrace(String traceId) {
        MDC.put(TRACE_ID, traceId);
        MDC.put(SPAN_ID, generateSpanId());
    }

    /**
     * 开始新的Span
     */
    public static String startSpan() {
        String parentSpanId = MDC.get(SPAN_ID);
        String newSpanId = generateSpanId();
        MDC.put(PARENT_SPAN_ID, parentSpanId);
        MDC.put(SPAN_ID, newSpanId);
        return newSpanId;
    }

    /**
     * 结束当前Span
     */
    public static void endSpan() {
        String parentSpanId = MDC.get(PARENT_SPAN_ID);
        if (parentSpanId != null) {
            MDC.put(SPAN_ID, parentSpanId);
        }
        MDC.remove(PARENT_SPAN_ID);
    }

    /**
     * 清理追踪上下文
     */
    public static void clearTrace() {
        MDC.clear();
    }

    /**
     * 在追踪上下文中执行
     */
    public static <T> T executeWithTrace(Supplier<T> supplier) {
        boolean isNewTrace = MDC.get(TRACE_ID) == null;
        if (isNewTrace) {
            startTrace();
        }

        try {
            return supplier.get();
        } finally {
            if (isNewTrace) {
                clearTrace();
            }
        }
    }

    /**
     * 获取当前追踪ID
     */
    public static String currentTraceId() {
        return MDC.get(TRACE_ID);
    }

    /**
     * 获取当前Span ID
     */
    public static String currentSpanId() {
        return MDC.get(SPAN_ID);
    }
}
```

## VariableUtil.java

```java
package com.study.common.util;

import com.study.common.service.VariableProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 变量处理工具类
 */
public class VariableUtil {
    private static final Logger logger = LoggerFactory.getLogger(VariableUtil.class);
    private static final String VARIABLE_PREFIX = "${";
    private static final String VARIABLE_SUFFIX = "}";

    private static final Map<String, VariableProvider> providers = new ConcurrentHashMap<>();

    /**
     * 注册变量提供者
     */
    public static void registerProvider(String providerName, VariableProvider provider) {
        providers.put(providerName, provider);
        logger.info("Registered variable provider: {}", providerName);
    }

    /**
     * 解析变量
     */
    public static String resolveVariable(String text) {
        if (text == null || !text.contains(VARIABLE_PREFIX)) {
            return text;
        }

        String result = text;
        int startIndex;
        int endIndex;

        while ((startIndex = result.indexOf(VARIABLE_PREFIX)) >= 0
                && (endIndex = result.indexOf(VARIABLE_SUFFIX, startIndex)) >= 0) {

            String variable = result.substring(startIndex + VARIABLE_PREFIX.length(), endIndex);
            String[] parts = variable.split(":", 2);
            String providerName = parts[0];
            String key = parts.length > 1 ? parts[1] : "";

            VariableProvider provider = providers.get(providerName);
            if (provider != null) {
                String value = provider.getVariableValue(key);
                if (value != null) {
                    result = result.substring(0, startIndex) + value
                            + result.substring(endIndex + VARIABLE_SUFFIX.length());
                }
            } else {
                logger.warn("Variable provider not found: {}", providerName);
            }
        }

        return result;
    }

    /**
     * 解析Map中的所有变量
     */
    public static Map<String, String> resolveVariables(Map<String, String> map) {
        Map<String, String> result = new ConcurrentHashMap<>();
        map.forEach((k, v) -> result.put(k, resolveVariable(v)));
        return result;
    }
}


```

## application.yml

```yaml
spring:
  jackson:
    # 日期格式化
    date-format: yyyy-MM-dd HH:mm:ss
    # 设置时区
    time-zone: GMT+8
    # 序列化配置
    serialization:
      FAIL_ON_EMPTY_BEANS: false
      WRITE_DATES_AS_TIMESTAMPS: false
    # 反序列化配置
    deserialization:
      FAIL_ON_UNKNOWN_PROPERTIES: false
    # 属性命名策略
    property-naming-strategy: SNAKE_CASE
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
        <artifactId>platform-parent</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <artifactId>platform-scheduler</artifactId>
    <packaging>jar</packaging>

    <dependencies>
        <!-- 引入公共模块 -->
        <dependency>
            <groupId>com.study</groupId>
            <artifactId>platform-common</artifactId>
            <version>${project.version}</version>
        </dependency>

        <!-- 添加 MariaDB JDBC 驱动依赖 -->
        <dependency>
            <groupId>org.mariadb.jdbc</groupId>
            <artifactId>mariadb-java-client</artifactId>
            <version>${mariadb.version}</version>
        </dependency>

        <!-- 定时任务相关 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-quartz</artifactId>
        </dependency>

        <!-- 分布式锁 -->
        <dependency>
            <groupId>org.redisson</groupId>
            <artifactId>redisson-spring-boot-starter</artifactId>
            <version>3.23.0</version>
        </dependency>

        <!-- 监控 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>io.micrometer</groupId>
            <artifactId>micrometer-registry-prometheus</artifactId>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.30</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>jakarta.annotation</groupId>
            <artifactId>jakarta.annotation-api</artifactId>
            <version>2.1.1</version>
        </dependency>
        <dependency>
            <groupId>org.apache.httpcomponents</groupId>
            <artifactId>httpclient</artifactId>
            <version>4.5.13</version>
        </dependency>
        <dependency>
            <groupId>org.apache.httpcomponents</groupId>
            <artifactId>httpcore</artifactId>
            <version>4.4.13</version>
        </dependency>
        <dependency>
            <groupId>org.apache.httpcomponents</groupId>
            <artifactId>httpmime</artifactId>
            <version>4.5.14</version>
        </dependency>
        <!-- 测试相关依赖 -->
        <!--        <dependency>-->
        <!--            <groupId>org.springframework.boot</groupId>-->
        <!--            <artifactId>spring-boot-starter-test</artifactId>-->
        <!--            <scope>test</scope>-->
        <!--        </dependency>-->
        <!--        <dependency>-->
        <!--            <groupId>org.wiremock</groupId>-->
        <!--            <artifactId>wiremock-standalone</artifactId>-->
        <!--            <version>3.4.2</version>-->
        <!--            <scope>test</scope>-->
        <!--        </dependency>-->
        <!--        <dependency>-->
        <!--            <groupId>org.junit.jupiter</groupId>-->
        <!--            <artifactId>junit-jupiter</artifactId>-->
        <!--            <version>5.10.2</version>-->
        <!--            <scope>test</scope>-->
        <!--        </dependency>-->

        <!-- Spring Boot MongoDB Starter -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-mongodb</artifactId>
        </dependency>

        <!-- 可选：添加MongoDB Reactive支持 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-mongodb-reactive</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- 添加Validation支持 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>

        <!-- 添加测试支持 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

        <!-- 可选：添加嵌入式MongoDB用于测试 -->
        <dependency>
            <groupId>de.flapdoodle.embed</groupId>
            <artifactId>de.flapdoodle.embed.mongo</artifactId>
            <version>4.12.0</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <mainClass>com.study.scheduler.SchedulerApplication</mainClass>
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

## SchedulerApplication.java

```java
package com.study.scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.study",
        "com.study.scheduler.config"  // 确保扫描到配置包
})
public class SchedulerApplication {
    public static void main(String[] args) {

        // 开启SSL调试（可选）
        System.setProperty("javax.net.debug", "ssl,handshake");
        SpringApplication.run(SchedulerApplication.class, args);
    }
}
```

## SchedulerConfig.java

```java
package com.study.scheduler;

import com.study.scheduler.listener.SchedulerJobListener;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.TriggerFiredBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class SchedulerConfig {
    private static final Logger logger = LoggerFactory.getLogger(SchedulerConfig.class);
    private final ApplicationContext applicationContext;
    @Autowired
    private DataSource dataSource;

    public SchedulerConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Bean
    public Properties quartzProperties() {
        logger.info("Configuring Quartz properties...");
        Properties properties = new Properties();

        // 调度器属性
        properties.put("org.quartz.scheduler.instanceName", "QuartzScheduler");
        properties.put("org.quartz.scheduler.instanceId", "AUTO");
        properties.put("org.quartz.scheduler.makeSchedulerThreadDaemon", "true");

        // 线程池属性
        properties.put("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
        properties.put("org.quartz.threadPool.threadCount", "10");
        properties.put("org.quartz.threadPool.threadPriority", "5");
        properties.put("org.quartz.threadPool.makeThreadsDaemons", "true");

        // JobStore属性
//        properties.put("org.quartz.jobStore.class", "org.quartz.impl.jdbcjobstore.JobStoreTX");
        properties.put("org.quartz.jobStore.class", "org.springframework.scheduling.quartz.LocalDataSourceJobStore");
        properties.put("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.StdJDBCDelegate");
        properties.put("org.quartz.jobStore.tablePrefix", "QRTZ_");
        properties.put("org.quartz.jobStore.isClustered", "true");
        properties.put("org.quartz.jobStore.clusterCheckinInterval", "20000");
        properties.put("org.quartz.jobStore.misfireThreshold", "60000");
        properties.put("org.quartz.jobStore.useProperties", "false");

        // 集群配置
        properties.put("org.quartz.scheduler.rmi.export", "false");
        properties.put("org.quartz.scheduler.rmi.proxy", "false");

        logger.info("Quartz properties configured successfully");
        return properties;
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() {
        logger.info("Initializing Quartz SchedulerFactoryBean...");

        SchedulerFactoryBean factory = new SchedulerFactoryBean();

        try {
            // 设置数据源
            factory.setDataSource(dataSource);
            logger.info("DataSource set successfully");

            // 设置Quartz属性
            factory.setQuartzProperties(quartzProperties());
            logger.info("Quartz properties set successfully");

            // 设置JobFactory
            CustomJobFactory jobFactory = new CustomJobFactory();
            jobFactory.setApplicationContext(applicationContext);
            factory.setJobFactory(jobFactory);
            logger.info("JobFactory set successfully");

            // 其他配置
            factory.setOverwriteExistingJobs(true);
            factory.setStartupDelay(10);
            factory.setAutoStartup(true);
            factory.setWaitForJobsToCompleteOnShutdown(true);

            // 事务配置
            factory.setTransactionManager(null); // 使用默认的事务管理

            logger.info("SchedulerFactoryBean initialized successfully");

        } catch (Exception e) {
            logger.error("Error initializing SchedulerFactoryBean", e);
            throw new RuntimeException("Failed to initialize SchedulerFactoryBean", e);
        }

        return factory;
    }

    @Bean
    public Scheduler scheduler(SchedulerFactoryBean factory, SchedulerJobListener jobListener)
            throws SchedulerException {
        logger.info("Creating Quartz Scheduler...");
        Scheduler scheduler = factory.getScheduler();
        scheduler.getListenerManager().addJobListener(jobListener);
        logger.info("Scheduler created and configured with JobListener");
        return scheduler;
    }
}

class CustomJobFactory extends SpringBeanJobFactory implements ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(CustomJobFactory.class);
    private AutowireCapableBeanFactory beanFactory;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        beanFactory = context.getAutowireCapableBeanFactory();
    }

    @Override
    protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
        Object job = super.createJobInstance(bundle);
        beanFactory.autowireBean(job);
        logger.info("Created job instance: {}", job.getClass().getName());
        return job;
    }
}
```

## AppConfig.java

```java
package com.study.scheduler.config;

import com.study.scheduler.utils.HttpClientUtil;
import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @PreDestroy
    public void onShutdown() {
        HttpClientUtil.close();
    }
}
```

## DatabaseConfig.java

```java
package com.study.scheduler.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
public class DatabaseConfig {

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.hikari.minimum-idle}")
    private int minimumIdle;

    @Value("${spring.datasource.hikari.maximum-pool-size}")
    private int maximumPoolSize;

    @Value("${spring.datasource.hikari.idle-timeout}")
    private long idleTimeout;

    @Value("${spring.datasource.hikari.pool-name}")
    private String poolName;

    @Value("${spring.datasource.hikari.max-lifetime}")
    private long maxLifetime;

    @Value("${spring.datasource.hikari.connection-timeout}")
    private long connectionTimeout;

    @Bean
    @Primary
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(driverClassName);
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setMinimumIdle(minimumIdle);
        config.setMaximumPoolSize(maximumPoolSize);
        config.setIdleTimeout(idleTimeout);
        config.setPoolName(poolName);
        config.setMaxLifetime(maxLifetime);
        config.setConnectionTimeout(connectionTimeout);

        // 设置连接测试查询
        config.setConnectionTestQuery("SELECT 1");
        config.setValidationTimeout(5000);

        // 设置其他连接池属性
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");

        return new HikariDataSource(config);
    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
```

## JobConfig.java

```java
package com.study.scheduler.config;

import com.study.common.util.DistributedLockUtil;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class JobConfig {

    @Bean
    public DistributedLockUtil distributedLockUtil(RedissonClient redissonClient) {
        return new DistributedLockUtil(redissonClient);
    }

    @Bean
    public HttpClient httpClient() {
        return HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }
}
```

## JobConfiguration.java

```java
package com.study.scheduler.config;

import com.study.common.model.task.TaskDefinition;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "scheduler.job")
public class JobConfiguration {

    private List<TaskDefinition> tasks;
    private Map<String, String> defaultVariables;
    private RetryProperties retry;
    private MonitorProperties monitor;

    @Data
    public static class RetryProperties {
        private int maxAttempts = 3;
        private long initialInterval = 1000;
        private double multiplier = 2.0;
        private long maxInterval = 10000;
    }

    @Data
    public static class MonitorProperties {
        private boolean enabled = true;
        private long checkInterval = 60000;
        private List<String> metrics;
        private AlertProperties alert;
    }

    @Data
    public static class AlertProperties {
        private boolean enabled = true;
        private String channel;
        private List<String> receivers;
    }

    @PostConstruct
    public void init() {
        // 初始化预定义任务
        if (tasks != null) {
            tasks.forEach(this::initializeTask);
        }
    }

    private void initializeTask(TaskDefinition task) {
        // 应用默认配置
        if (task.getRetryPolicy() == null) {
            task.setRetryPolicy(createDefaultRetryPolicy());
        }

        // 合并默认变量
        if (defaultVariables != null) {
            task.getVariables().addAll(defaultVariables.keySet());
        }
    }
}
```

## MongoConfig.java

```java
package com.study.scheduler.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Value("${spring.data.mongodb.database}")
    private String database;

    @Override
    protected String getDatabaseName() {
        return database;
    }

    @Override
    public MongoClient mongoClient() {
        ConnectionString connectionString = new ConnectionString(mongoUri);
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();
        return MongoClients.create(mongoClientSettings);
    }

    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        MongoTemplate mongoTemplate = new MongoTemplate(mongoClient(), getDatabaseName());

        // 移除_class字段
        ((MappingMongoConverter) mongoTemplate.getConverter()).setTypeMapper(new DefaultMongoTypeMapper(null));

        return mongoTemplate;
    }
}

```

## RedisConfig.java

```java
package com.study.scheduler.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.List;

@Configuration
@AutoConfigureBefore(RedisAutoConfiguration.class)
public class RedisConfig {

    private static final Logger logger = LoggerFactory.getLogger(RedisConfig.class);

    @Value("${spring.data.redis.host:localhost}")
    private String host;

    @Value("${spring.data.redis.port:6379}")
    private int port;

    @Value("${spring.data.redis.password:}")
    private String password;

    @Value("${spring.data.redis.cluster.nodes:}")
    private List<String> clusterNodes;

    @Value("${spring.data.redis.timeout:2000}")
    private long timeout;

    @Value("${spring.data.redis.lettuce.pool.max-active:8}")
    private int maxActive;

    @Value("${spring.data.redis.lettuce.pool.max-idle:8}")
    private int maxIdle;

    @Value("${spring.data.redis.lettuce.pool.min-idle:0}")
    private int minIdle;

    @Value("${spring.data.redis.lettuce.pool.max-wait:-1}")
    private long maxWait;

    @PostConstruct
    public void init() {
        logger.info("=============== Redis Configuration Initializing ===============");
        logger.info("Redis Mode: {}", (clusterNodes == null || clusterNodes.isEmpty()) ? "Standalone" : "Cluster");
        logger.info("Redis Host: {}", host);
        logger.info("Redis Port: {}", port);
        logger.info("Redis Password: {}", password.isEmpty() ? "Not Set" : "Set");
        if (clusterNodes != null && !clusterNodes.isEmpty()) {
            logger.info("Redis Cluster Nodes: {}", clusterNodes);
        }
        logger.info("Redis Pool Config:");
        logger.info("  Max Active: {}", maxActive);
        logger.info("  Max Idle: {}", maxIdle);
        logger.info("  Min Idle: {}", minIdle);
        logger.info("  Max Wait: {}", maxWait);
        logger.info("=============================================================");
    }

    @Bean
    @Primary
    public RedisConnectionFactory redisConnectionFactory() {
        logger.info("Creating RedisConnectionFactory...");
        LettuceClientConfiguration clientConfig = getLettuceClientConfiguration();

        if (clusterNodes != null && !clusterNodes.isEmpty()) {
            return createClusterConnectionFactory(clientConfig);
        } else {
            return createStandaloneConnectionFactory(clientConfig);
        }
    }

    @Bean
    public RedissonClient redissonClient() {
        logger.info("Creating RedissonClient...");
        Config config = new Config();

        if (clusterNodes != null && !clusterNodes.isEmpty()) {
            // 集群模式
            ClusterServersConfig clusterConfig = config.useClusterServers();
            for (String node : clusterNodes) {
                clusterConfig.addNodeAddress("redis://" + node);
            }
            if (!password.isEmpty()) {
                clusterConfig.setPassword(password);
            }
            clusterConfig.setTimeout((int) timeout)
                    .setMasterConnectionPoolSize(maxActive)
                    .setMasterConnectionMinimumIdleSize(minIdle)
                    .setSlaveConnectionPoolSize(maxActive)
                    .setSlaveConnectionMinimumIdleSize(minIdle);
            logger.info("Configured Redisson for cluster mode");
        } else {
            // 单机模式
            SingleServerConfig serverConfig = config.useSingleServer()
                    .setAddress("redis://" + host + ":" + port)
                    .setTimeout((int) timeout)
                    .setConnectionPoolSize(maxActive)
                    .setConnectionMinimumIdleSize(minIdle);
            if (!password.isEmpty()) {
                serverConfig.setPassword(password);
            }
            logger.info("Configured Redisson for standalone mode");
        }

        return Redisson.create(config);
    }

    private LettuceClientConfiguration getLettuceClientConfiguration() {
        GenericObjectPoolConfig<?> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxTotal(maxActive);
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMinIdle(minIdle);
        poolConfig.setMaxWait(Duration.ofMillis(maxWait));

        return LettucePoolingClientConfiguration.builder()
                .commandTimeout(Duration.ofMillis(timeout))
                .poolConfig(poolConfig)
                .build();
    }

    private RedisConnectionFactory createClusterConnectionFactory(LettuceClientConfiguration clientConfig) {
        logger.info("Creating Redis Cluster connection factory");
        RedisClusterConfiguration clusterConfig = new RedisClusterConfiguration(clusterNodes);
        if (!password.isEmpty()) {
            clusterConfig.setPassword(password);
        }
        return new LettuceConnectionFactory(clusterConfig, clientConfig);
    }

    private RedisConnectionFactory createStandaloneConnectionFactory(LettuceClientConfiguration clientConfig) {
        logger.info("Creating Redis Standalone connection factory for {}:{}", host, port);
        RedisStandaloneConfiguration standaloneConfig = new RedisStandaloneConfiguration(host, port);
        if (!password.isEmpty()) {
            standaloneConfig.setPassword(password);
        }
        return new LettuceConnectionFactory(standaloneConfig, clientConfig);
    }

    @Bean
    @Primary
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        logger.info("Initializing RedisTemplate...");
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL);
        mapper.registerModule(new JavaTimeModule());

        GenericJackson2JsonRedisSerializer jsonSerializer =
                new GenericJackson2JsonRedisSerializer(mapper);

        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        logger.info("RedisTemplate initialized successfully");
        return template;
    }
}
```

## CrawlerController.java

```java
package com.study.scheduler.crontroller;

import com.study.scheduler.entity.CrawlerRecord;
import com.study.scheduler.entity.CrawlerTask;
import com.study.scheduler.service.CrawlerService;
import com.study.scheduler.utils.MongoDBUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/crawler")
public class CrawlerController {
    private final CrawlerService crawlerService;
    private final MongoDBUtils mongoDBUtils;

    public CrawlerController(CrawlerService crawlerService, MongoDBUtils mongoDBUtils) {
        this.crawlerService = crawlerService;
        this.mongoDBUtils = mongoDBUtils;
    }

    @PostMapping("/tasks")
    public CrawlerTask createTask(@RequestBody CrawlerTask task) {
        return crawlerService.createTask(task);
    }

    @PostMapping("/tasks/{taskId}/execute")
    public CrawlerRecord executeCrawling(@PathVariable String taskId) {
        CrawlerTask task = mongoDBUtils.findById(taskId, CrawlerTask.class);
        return crawlerService.crawl(task);
    }

    @GetMapping("/records/failed")
    public List<CrawlerRecord> getFailedRecords() {
        return crawlerService.getFailedRecords();
    }

    @PostMapping("/records/{recordId}/retry")
    public void retryFailedRecord(@PathVariable String recordId) {
        crawlerService.retryCrawling(recordId);
    }
}

```

## TestCrontroller.java

```java
package com.study.scheduler.crontroller;

import com.study.scheduler.utils.HttpClientUtil;

public class TestCrontroller {
    public static void main(String[] args) {
        try {
            String response = HttpClientUtil.doGet("https://your-nginx-server/api");
            System.out.println(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

```

## CrawlerRecord.java

```java
package com.study.scheduler.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "crawler_records")
public class CrawlerRecord {
    @Id
    private String id;

    @Indexed
    private String taskId;
    private String url;
    private int statusCode;
    private String responseBody;
    private String errorMessage;
    private boolean success;
    private int retryCount;
    private Date createTime;
    private Date updateTime;
}
```

## CrawlerTask.java

```java
package com.study.scheduler.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Map;

@Data
@Document(collection = "crawler_tasks")
public class CrawlerTask {
    @Id
    private String id;

    @Indexed
    private String name;
    private String url;
    private String method;
    private Map<String, String> headers;
    private String requestBody;
    private int retryCount;
    private int maxRetries;
    private long retryInterval;
    private boolean enabled;
    private Date createTime;
    private Date updateTime;
}

```

## JobInfo.java

```java
package com.study.scheduler.entity;

import lombok.Data;

import java.util.Date;

@Data
public class JobInfo {
    private Long id;
    private String jobName;        // 任务名称
    private String jobGroup;       // 任务分组
    private String jobClass;       // 任务类
    private String cronExpression; // cron表达式
    private String parameter;      // 任务参数（JSON格式）
    private String description;    // 任务描述
    private Integer concurrent;    // 是否允许并发执行
    private Integer status;        // 任务状态
    private Date nextFireTime;     // 下次执行时间
    private Date prevFireTime;     // 上次执行时间
    private Date createTime;       // 创建时间
    private Date updateTime;       // 更新时间
}
```

## JobLog.java

```java
package com.study.scheduler.entity;

import lombok.Data;

import java.util.Date;

@Data
public class JobLog {
    private Long id;
    private Long jobId;           // 任务ID
    private String jobName;       // 任务名称
    private String jobGroup;      // 任务分组
    private String jobClass;      // 任务类
    private String parameter;     // 执行参数
    private String message;       // 日志信息
    private Integer status;       // 执行状态
    private String exceptionInfo; // 异常信息
    private Date startTime;       // 开始时间
    private Date endTime;         // 结束时间
    private Long duration;        // 执行时长(毫秒)
    private String serverIp;      // 执行服务器IP
}

```

## TreeCollectRequest.java

```java
package com.study.scheduler.entity;

import lombok.Data;

import java.util.Map;

@Data
public class TreeCollectRequest {
    private String projectId;
    private String url;         // 树状数据获取URL
    private String method;      // 请求方法(GET/POST)
    private Map<String, String> headers; // 请求头
    private String requestBody; // POST请求体
}

```

## JobException.java

```java
package com.study.scheduler.exception;

public class JobException extends RuntimeException {
    public JobException(String message) {
        super(message);
    }

    public JobException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

## BaseJob.java

```java
package com.study.scheduler.job;


import com.study.scheduler.entity.JobLog;
import com.study.scheduler.mapper.JobLogMapper;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.InetAddress;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public abstract class BaseJob implements Job {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    protected RedissonClient redissonClient;

    @Autowired
    protected JobLogMapper jobLogMapper;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String jobName = context.getJobDetail().getKey().getName();
        String lockKey = "scheduler:lock:" + jobName;
        RLock lock = redissonClient.getLock(lockKey);

        JobLog jobLog = new JobLog();
        jobLog.setJobName(jobName);
        jobLog.setStartTime(new Date());
        jobLog.setServerIp(getServerIp());

        try {
            // 尝试获取分布式锁，最多等待3秒，10分钟后自动释放
            boolean locked = lock.tryLock(3, 600, TimeUnit.SECONDS);
            if (!locked) {
                jobLog.setStatus(0);
                jobLog.setMessage("获取任务锁失败，任务已在其他节点执行");
                return;
            }

            // 执行具体任务
            doExecute(context);

            jobLog.setStatus(1);
            jobLog.setMessage("执行成功");

        } catch (Exception e) {
            logger.error("任务执行异常", e);
            jobLog.setStatus(2);
            jobLog.setExceptionInfo(e.getMessage());
            throw new JobExecutionException(e);

        } finally {
            jobLog.setEndTime(new Date());
            jobLog.setDuration(jobLog.getEndTime().getTime() - jobLog.getStartTime().getTime());
            jobLogMapper.insert(jobLog);

            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    protected abstract void doExecute(JobExecutionContext context) throws Exception;

    private String getServerIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "unknown";
        }
    }

}

```

## CollectRetryJob.java

```java
package com.study.scheduler.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.scheduler.utils.HttpClientUtil;
import lombok.Data;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CollectRetryJob extends BaseJob {
    private static final Logger logger = LoggerFactory.getLogger(CollectRetryJob.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Value("${collect.service.url}")
    private String collectServiceUrl;

    @Override
    protected void doExecute(JobExecutionContext context) throws Exception {
        logger.info("Starting failed task retry job");

        try {
            // 1. 获取失败的任务
            String failedTasksJson = HttpClientUtil.doGet(collectServiceUrl + "/api/tasks/status/FAILED");
            TaskResponse<List<CollectTask>> response = objectMapper.readValue(
                    failedTasksJson,
                    objectMapper.getTypeFactory().constructParametricType(
                            TaskResponse.class,
                            objectMapper.getTypeFactory().constructCollectionType(List.class, CollectTask.class)
                    )
            );

            if (response.getCode() == 200 && response.getData() != null) {
                // 2. 重试失败的任务
                for (CollectTask task : response.getData()) {
                    if (task.getRetryCount() < task.getMaxRetries()) {
                        try {
                            HttpClientUtil.doPost(
                                    collectServiceUrl + "/api/tasks/" + task.getId() + "/retry",
                                    null
                            );
                            logger.info("Retried task: {}", task.getId());
                        } catch (Exception e) {
                            logger.error("Failed to retry task: " + task.getId(), e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error in retry job execution", e);
            throw e;
        }
    }

    private boolean error(Integer code) {
        return code == null || code != 200;
    }

    @Data
    public static class TaskResponse<T> {
        private Integer code;
        private String message;
        private T data;
    }

    @Data
    public static class CollectTask {
        private String id;
        private String name;
        private String url;
        private String status;
        private Integer retryCount = 0;
        private Integer maxRetries = 3;
    }
}
```

## CollectSyncJob.java

```java
package com.study.scheduler.job;

import com.study.scheduler.utils.HttpClientUtil;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class CollectSyncJob extends BaseJob {
    private static final Logger logger = LoggerFactory.getLogger(CollectSyncJob.class);

    @Value("${collect.service.urls}")
    private Set<String> collectServiceUrls;

    @Override
    protected void doExecute(JobExecutionContext context) throws Exception {
        logger.info("Starting collect service health check and sync");

        for (String serviceUrl : collectServiceUrls) {
            try {
                // 1. 健康检查
                String healthResult = HttpClientUtil.doGet(serviceUrl + "/actuator/health");
                if (!healthResult.contains("UP")) {
                    logger.warn("Collect service unhealthy: {}", serviceUrl);
                    continue;
                }

                // 2. 同步配置
                String configResult = HttpClientUtil.doGet(serviceUrl + "/api/config/sync");
                logger.info("Config sync result for {}: {}", serviceUrl, configResult);

                // 3. 同步任务状态
                String taskResult = HttpClientUtil.doGet(serviceUrl + "/api/tasks/sync");
                logger.info("Task sync result for {}: {}", serviceUrl, taskResult);

            } catch (Exception e) {
                logger.error("Failed to sync with collect service: " + serviceUrl, e);
            }
        }
    }
}

```

## CollectTaskJob.java

```java
package com.study.scheduler.job;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.scheduler.utils.HttpClientUtil;
import lombok.Data;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CollectTaskJob extends BaseJob {
    private static final Logger logger = LoggerFactory.getLogger(CollectTaskJob.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Value("${collect.service.url}")
    private String collectServiceUrl;

    @Override
    protected void doExecute(JobExecutionContext context) throws Exception {
        logger.info("Starting collect task distribution job");

        // 1. 获取所有等待执行的采集任务
        String tasksJson = HttpClientUtil.doGet(collectServiceUrl + "/api/tasks/status/CREATED");
        TaskResponse<List<CollectTask>> response = objectMapper.readValue(
                tasksJson,
                new TypeReference<TaskResponse<List<CollectTask>>>() {
                }
        );

        if (response.getCode() == 200 && response.getData() != null) {
            // 2. 分发任务到各个节点执行
            for (CollectTask task : response.getData()) {
                try {
                    // 发送执行请求
                    HttpClientUtil.doPost(
                            collectServiceUrl + "/api/tasks/" + task.getId() + "/execute",
                            null
                    );
                    logger.info("Distributed task: {}", task.getId());
                } catch (Exception e) {
                    logger.error("Failed to distribute task: " + task.getId(), e);
                }
            }
        }
    }

    @Data
    static class TaskResponse<T> {
        private Integer code;
        private String message;
        private T data;
    }

    @Data
    static class CollectTask {
        private String id;
        private String name;
        private String url;
        private String status;
    }
}
```

## CustomJob.java

```java
package com.study.scheduler.job;

import com.study.scheduler.service.EnhancedJobService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomJob implements Job {
    @Autowired
    private EnhancedJobService jobService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String taskId = context.getJobDetail().getKey().getName();
        try {
            jobService.executeJob(taskId);
        } catch (Exception e) {
            throw new JobExecutionException(e);
        }
    }
}
```

## DemoJob.java

```java
package com.study.scheduler.job;

import com.study.scheduler.entity.JobInfo;
import com.study.scheduler.entity.JobLog;
import com.study.scheduler.mapper.JobLogMapper;
import com.study.scheduler.service.JobService;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DemoJob extends BaseJob {
    // 执行查询日志
    @Autowired
    private JobLogMapper jobLogMapper;
    // 创建新任务
    @Autowired
    private JobService jobService;

    @Override
    protected void doExecute(JobExecutionContext context) throws Exception {
        // 获取任务参数
        String parameter = context.getJobDetail().getJobDataMap().getString("parameter");
        logger.info("执行示例任务，参数：{}", parameter);

        // 模拟任务执行
        Thread.sleep(1000);

        // 任务逻辑...
    }

    public List<JobLog> getJobLogs(String jobName, String jobGroup) {
        return jobLogMapper.findRecentLogs(jobName, jobGroup, 10);
    }

    public void createJob() throws Exception {
        JobInfo jobInfo = new JobInfo();
        jobInfo.setJobName("demoJob");
        jobInfo.setJobGroup("demo");
        jobInfo.setJobClass("com.example.scheduler.job.DemoJob");
        jobInfo.setCronExpression("0 0/5 * * * ?");
        jobInfo.setDescription("示例任务");
        jobInfo.setParameter("{\"key\":\"value\"}");
        jobService.addJob(jobInfo);
    }
}
```

## HttpJob.java

```java
package com.study.scheduler.job;

import com.study.scheduler.service.EnhancedJobService;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HttpJob implements Job {
    @Autowired
    private EnhancedJobService jobService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap data = context.getJobDetail().getJobDataMap();
        String taskId = context.getJobDetail().getKey().getName();
        try {
            jobService.executeJob(taskId);
        } catch (Exception e) {
            throw new JobExecutionException(e);
        }
    }
}

```

## JobLogCleanTask.java

```java
package com.study.scheduler.job;


import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

@Component
public class JobLogCleanTask extends BaseJob {

    @Value("${scheduler.log.retain-days:30}")
    private int logRetainDays;

    @Override
    protected void doExecute(JobExecutionContext context) throws Exception {
        // 计算需要清理的日期
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -logRetainDays);
        Date cleanupDate = calendar.getTime();

        // 执行清理
        int count = jobLogMapper.cleanupOldLogs(cleanupDate);
        logger.info("清理任务日志完成，清理{}天前的日志，共清理{}条", logRetainDays, count);
    }
}
```

## ScheduledJob.java

```java
package com.study.scheduler.job;

import com.study.scheduler.service.EnhancedJobService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ScheduledJob implements Job {
    @Autowired
    private EnhancedJobService jobService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String taskId = context.getJobDetail().getKey().getName();
        try {
            jobService.executeJob(taskId);
        } catch (Exception e) {
            throw new JobExecutionException(e);
        }
    }
}
```

## TreeCollectorJob.java

```java
package com.study.scheduler.job;

import com.study.common.util.JsonUtils;
import com.study.scheduler.entity.TreeCollectRequest;
import com.study.scheduler.utils.HttpClientUtil;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class TreeCollectorJob extends BaseJob {

    @Value("${collect.service.url}")
    private String collectServiceUrl;

    @Override
    protected void doExecute(JobExecutionContext context) throws Exception {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        String url = dataMap.getString("url");
        String method = dataMap.getString("method");

        // 构建采集请求
        TreeCollectRequest request = new TreeCollectRequest();
        request.setUrl(url);
        request.setMethod(method);

        // 创建采集任务
        String response = HttpClientUtil.doPost(
                collectServiceUrl + "/api/tree/collect",
                JsonUtils.toJson(request)
        );

        logger.info("Tree collection task created: {}", response);
    }
}
```

## SchedulerJobListener.java

```java
package com.study.scheduler.listener;


import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SchedulerJobListener implements JobListener {
    private static final Logger logger = LoggerFactory.getLogger(SchedulerJobListener.class);

    @Override
    public String getName() {
        return "GlobalJobListener";
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        String jobName = context.getJobDetail().getKey().toString();
        logger.info("任务准备执行: {}", jobName);
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        String jobName = context.getJobDetail().getKey().toString();
        logger.warn("任务被否决: {}", jobName);
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        String jobName = context.getJobDetail().getKey().toString();
        if (jobException == null) {
            logger.info("任务执行完成: {}", jobName);
        } else {
            logger.error("任务执行失败: {}", jobName, jobException);
        }
    }
}
```

## JobInfoMapper.java

```java
package com.study.scheduler.mapper;

import com.study.scheduler.entity.JobInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface JobInfoMapper {
    @Insert("""
                INSERT INTO schedule_job_info(
                    job_name, job_group, job_class, cron_expression,
                    parameter, description, concurrent, status,
                    next_fire_time, prev_fire_time, create_time, update_time
                ) VALUES (
                    #{jobName}, #{jobGroup}, #{jobClass}, #{cronExpression},
                    #{parameter}, #{description}, #{concurrent}, #{status},
                    #{nextFireTime}, #{prevFireTime}, #{createTime}, #{updateTime}
                )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(JobInfo jobInfo);

    @Update("""
                UPDATE schedule_job_info
                SET cron_expression = #{cronExpression},
                    parameter = #{parameter},
                    description = #{description},
                    concurrent = #{concurrent},
                    status = #{status},
                    next_fire_time = #{nextFireTime},
                    prev_fire_time = #{prevFireTime},
                    update_time = #{updateTime}
                WHERE job_name = #{jobName} AND job_group = #{jobGroup}
            """)
    int updateByJobKey(@Param("jobName") String jobName,
                       @Param("jobGroup") String jobGroup,
                       @Param("jobInfo") JobInfo jobInfo);

    @Delete("DELETE FROM schedule_job_info WHERE job_name = #{jobName} AND job_group = #{jobGroup}")
    int deleteByJobKey(@Param("jobName") String jobName,
                       @Param("jobGroup") String jobGroup);

    @Select("SELECT * FROM schedule_job_info WHERE job_name = #{jobName} AND job_group = #{jobGroup}")
    JobInfo findByJobKey(@Param("jobName") String jobName,
                         @Param("jobGroup") String jobGroup);

    @Select("SELECT * FROM schedule_job_info WHERE id = #{id}")
    JobInfo findById(@Param("id") Long id);

    @Select("SELECT * FROM schedule_job_info ORDER BY create_time DESC")
    List<JobInfo> findAll();

    @Select("SELECT * FROM schedule_job_info WHERE status = #{status}")
    List<JobInfo> findByStatus(@Param("status") Integer status);
}


```

## JobLogMapper.java

```java
package com.study.scheduler.mapper;


import com.study.scheduler.entity.JobLog;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

@Mapper
public interface JobLogMapper {
    @Insert("""
                INSERT INTO schedule_job_log(
                    job_id, job_name, job_group, job_class, parameter,
                    message, status, exception_info, start_time,
                    end_time, duration, server_ip
                ) VALUES (
                    #{jobId}, #{jobName}, #{jobGroup}, #{jobClass}, #{parameter},
                    #{message}, #{status}, #{exceptionInfo}, #{startTime},
                    #{endTime}, #{duration}, #{serverIp}
                )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(JobLog jobLog);

    @Select("""
                SELECT * FROM schedule_job_log
                WHERE job_name = #{jobName} AND job_group = #{jobGroup}
                ORDER BY start_time DESC
                LIMIT #{limit}
            """)
    List<JobLog> findRecentLogs(@Param("jobName") String jobName,
                                @Param("jobGroup") String jobGroup,
                                @Param("limit") int limit);

    @Select("""
                SELECT * FROM schedule_job_log
                WHERE job_name = #{jobName}
                  AND job_group = #{jobGroup}
                  AND start_time BETWEEN #{startTime} AND #{endTime}
                ORDER BY start_time DESC
            """)
    List<JobLog> findLogsByTimeRange(@Param("jobName") String jobName,
                                     @Param("jobGroup") String jobGroup,
                                     @Param("startTime") Date startTime,
                                     @Param("endTime") Date endTime);

    @Select("SELECT * FROM schedule_job_log WHERE id = #{id}")
    JobLog findById(@Param("id") Long id);

    @Delete("DELETE FROM schedule_job_log WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    @Delete("""
                DELETE FROM schedule_job_log
                WHERE start_time < #{beforeTime}
            """)
    int cleanupOldLogs(@Param("beforeTime") Date beforeTime);
}


```

## HttpTaskResult.java

```java
package com.study.scheduler.model.job;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class HttpTaskResult {
    private int statusCode;
    private String body;
    private Map<String, List<String>> headers;

    public HttpTaskResult() {
    }

    public HttpTaskResult(int statusCode, String body, Map<String, List<String>> headers) {
        this.statusCode = statusCode;
        this.body = body;
        this.headers = headers;

    }
}
```

## JobDefinition.java

```java
package com.study.scheduler.model.job;

import jakarta.persistence.Entity;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Entity
@Data
public class JobDefinition {
    @jakarta.persistence.Id
    @Id
    private String id;
    private String name;
    private String description;
    private String jobClass;
    private String cronExpression;
    private String jobData;
    private Integer status;
    private Date createTime;
    private Date updateTime;
}
```

## JobExecution.java

```java
package com.study.scheduler.model.job;

import jakarta.persistence.Entity;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Entity
@Data
public class JobExecution {
    @Id
    private String id;
    private String jobDefId;
    private Date startTime;
    private Date endTime;
    private String status;
    private String result;
    private String error;
    private Integer retryCount;
}

```

## JobMetrics.java

```java
package com.study.scheduler.model.job;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.springframework.stereotype.Component;

/**
 * 任务执行指标统计类
 */
@Component
public class JobMetrics {

    private Counter jobCreationCounter = null;
    private Counter jobSuccessCounter = null;
    private Counter jobFailureCounter = null;

    public JobMetrics(MeterRegistry registry) {
        // 初始化计数器
        this.jobCreationCounter = Counter.builder("job.creation")
                .description("Number of jobs created")
                .register(registry);

        this.jobSuccessCounter = Counter.builder("job.execution.success")
                .description("Number of successful job executions")
                .register(registry);

        this.jobFailureCounter = Counter.builder("job.execution.failure")
                .description("Number of failed job executions")
                .register(registry);
    }

    // 默认构造函数
    public JobMetrics() {
        // 使用NoopCounter或空实现进行初始化
        this.jobCreationCounter = Counter.builder("noop").register(new SimpleMeterRegistry());
        this.jobSuccessCounter = Counter.builder("noop").register(new SimpleMeterRegistry());
        this.jobFailureCounter = Counter.builder("noop").register(new SimpleMeterRegistry());
    }

    public void incrementJobCreationCount() {
        if(jobCreationCounter != null) {
            jobCreationCounter.increment();
        }
    }

    public void incrementJobSuccessCount() {
        if(jobSuccessCounter != null) {
            jobSuccessCounter.increment();
        }
    }

    public void incrementJobFailureCount() {
        if(jobFailureCounter != null) {
            jobFailureCounter.increment();
        }
    }
}
```

## JobDefinitionRepository.java

```java
package com.study.scheduler.repository;

import com.study.scheduler.model.job.JobDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobDefinitionRepository extends JpaRepository<JobDefinition, String> {
    // 可以添加自定义查询方法
}

```

## JobExecutionRepository.java

```java
package com.study.scheduler.repository;

import com.study.common.model.task.TaskExecution;
import com.study.scheduler.model.job.JobExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobExecutionRepository extends JpaRepository<TaskExecution, String> {
    List<TaskExecution> findByJobDefId(String jobDefId);
}
```

## CrawlerService.java

```java
package com.study.scheduler.service;

import com.study.scheduler.entity.CrawlerRecord;
import com.study.scheduler.entity.CrawlerTask;
import com.study.scheduler.utils.HttpClientUtil;
import com.study.scheduler.utils.MongoDBUtils;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CrawlerService {
    private final MongoDBUtils mongoDBUtils;

    public CrawlerService(MongoDBUtils mongoDBUtils) {
        this.mongoDBUtils = mongoDBUtils;
    }

    public CrawlerTask createTask(CrawlerTask task) {
        task.setCreateTime(new Date());
        task.setUpdateTime(new Date());
        return mongoDBUtils.save(task);
    }

    public CrawlerRecord crawl(CrawlerTask task) {
        CrawlerRecord record = new CrawlerRecord();
        record.setTaskId(task.getId());
        record.setUrl(task.getUrl());
        record.setCreateTime(new Date());
        record.setUpdateTime(new Date());

        try {
            String response;
            if ("POST".equalsIgnoreCase(task.getMethod())) {
                response = HttpClientUtil.doPost(task.getUrl(), task.getRequestBody());
            } else {
                response = HttpClientUtil.doGet(task.getUrl());
            }

            record.setResponseBody(response);
            record.setSuccess(true);
            record.setStatusCode(200);
        } catch (Exception e) {
            record.setSuccess(false);
            record.setErrorMessage(e.getMessage());
            record.setStatusCode(500);

            if (task.getRetryCount() < task.getMaxRetries()) {
                scheduleRetry(task);
            }
        }

        return mongoDBUtils.save(record);
    }

    private void scheduleRetry(CrawlerTask task) {
        Query query = new Query(Criteria.where("id").is(task.getId()));
        Update update = new Update()
                .inc("retryCount", 1)
                .set("updateTime", new Date());
        mongoDBUtils.update(query, update, CrawlerTask.class);
    }

    public List<CrawlerRecord> getFailedRecords() {
        Query query = new Query(Criteria.where("success").is(false));
        return mongoDBUtils.find(query, CrawlerRecord.class);
    }

    public void retryCrawling(String recordId) {
        CrawlerRecord record = mongoDBUtils.findById(recordId, CrawlerRecord.class);
        if (record != null) {
            CrawlerTask task = mongoDBUtils.findById(record.getTaskId(), CrawlerTask.class);
            if (task != null) {
                crawl(task);
            }
        }
    }
}
```

## EnhancedJobService.java

```java
package com.study.scheduler.service;

import com.study.common.model.task.*;
import com.study.common.util.DistributedLockUtil;
import com.study.common.util.JsonUtils;
import com.study.common.util.TaskTraceUtil;
import com.study.common.util.VariableUtil;
import com.study.scheduler.exception.JobException;
import com.study.scheduler.job.CustomJob;
import com.study.scheduler.job.HttpJob;
import com.study.scheduler.job.ScheduledJob;
import com.study.scheduler.model.job.HttpTaskResult;
import com.study.scheduler.model.job.JobDefinition;
import com.study.scheduler.model.job.JobMetrics;
import com.study.scheduler.repository.JobDefinitionRepository;
import com.study.scheduler.repository.JobExecutionRepository;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * 增强的任务服务类
 * 提供任务的创建、执行、状态管理等核心功能
 * 支持HTTP任务、定时任务和自定义任务的处理
 */
@Slf4j
@Service
public class EnhancedJobService {

    private static final int HTTP_TIMEOUT_SECONDS = 10;
    private static final String LOCK_PREFIX_JOB = "job:def:";
    private static final String LOCK_PREFIX_STATUS = "job:status:";

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private DistributedLockUtil lockUtil;

    @Autowired
    private JobDefinitionRepository jobDefinitionRepository;

    @Autowired
    private JobExecutionRepository jobExecutionRepository;

    @Autowired
    private HttpClient httpClient;

    @Autowired
    private MeterRegistry meterRegistry;

    private final JobMetrics jobMetrics = new JobMetrics();

    /**
     * 创建或更新任务
     *
     * @param taskDef 任务定义
     * @return 任务执行结果
     */
    @Transactional
    public Object saveJob(TaskDefinition taskDef) {
        String lockKey = LOCK_PREFIX_JOB + taskDef.getId();
        return lockUtil.executeWithLock(lockKey, () -> {
            try {
                validateTask(taskDef);

                // 保存任务定义
                JobDefinition jobDef = convertToJobDefinition(taskDef);
                jobDefinitionRepository.save(jobDef);

                // 创建Quartz任务
                JobDetail jobDetail = createJobDetail(taskDef);
                Trigger trigger = createTrigger(taskDef);

                // 调度任务
                scheduler.scheduleJob(jobDetail, trigger);

                jobMetrics.incrementJobCreationCount();
                log.info("Successfully saved and scheduled job: {}", taskDef.getId());

                return TaskResult.builder()
                        .success(true)
                        .message("Job saved successfully")
                        .build();

            } catch (SchedulerException e) {
                log.error("Failed to schedule job: {}", taskDef.getId(), e);
                jobMetrics.incrementJobFailureCount();
                return TaskResult.builder()
                        .success(false)
                        .message("Failed to schedule job: " + e.getMessage())
                        .build();
            }
        });
    }

    /**
     * 执行任务
     *
     * @param jobId 任务ID
     * @return 任务执行结果
     */
    public TaskResult executeJob(String jobId) {
        return TaskTraceUtil.executeWithTrace(() -> {
            long startTime = System.currentTimeMillis();
            try {
                // 获取任务定义
                TaskDefinition taskDef = getTaskDefinition(jobId);
                if (taskDef == null) {
                    log.warn("Task not found: {}", jobId);
                    return TaskResult.builder()
                            .success(false)
                            .message("Task not found")
                            .build();
                }

                // 创建执行记录
                TaskExecution execution = createExecution(taskDef);
                log.info("Created execution record for task: {}", jobId);

                // 解析变量
                resolveVariables(taskDef);

                // 执行任务
                Object result = executeTask(taskDef);
                log.info("Task executed successfully: {}", jobId);

                // 更新执行记录
                updateExecutionSuccess(execution, result);

                // 记录执行时间
                long duration = System.currentTimeMillis() - startTime;
                meterRegistry.timer("job.execution.time").record(Duration.ofMillis(duration));

                return TaskResult.builder()
                        .success(true)
                        .data(result)
                        .build();

            } catch (Exception e) {
                log.error("Job execution failed: {}", jobId, e);
                jobMetrics.incrementJobFailureCount();

                long duration = System.currentTimeMillis() - startTime;
                meterRegistry.timer("job.execution.failure.time").record(Duration.ofMillis(duration));

                return TaskResult.builder()
                        .success(false)
                        .message("Execution failed: " + e.getMessage())
                        .build();
            }
        });
    }

    /**
     * 处理HTTP类型任务
     *
     * @param taskDef 任务定义
     * @return 执行结果
     * @throws Exception 执行异常
     */
    private Object executeHttpTask(TaskDefinition taskDef) throws Exception {
        HttpConfig config = taskDef.getHttpConfig();
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(new URI(config.getUrl()))
                .timeout(Duration.ofSeconds(HTTP_TIMEOUT_SECONDS));

        // 设置请求头
        if (config.getHeaders() != null) {
            config.getHeaders().forEach(requestBuilder::header);
        }

        // 设置请求方法和body
        configureHttpMethod(requestBuilder, config);

        log.info("Executing HTTP request: {} {}", config.getMethod(), config.getUrl());

        // 执行请求
        HttpResponse<String> response = httpClient.send(
                requestBuilder.build(),
                HttpResponse.BodyHandlers.ofString()
        );

        // 记录指标
        recordHttpMetrics(response);

        return new HttpTaskResult(
                response.statusCode(),
                response.body(),
                response.headers().map()
        );
    }

    /**
     * 配置HTTP请求方法
     *
     * @param requestBuilder HTTP请求构建器
     * @param config HTTP配置
     */
    private void configureHttpMethod(HttpRequest.Builder requestBuilder, HttpConfig config) {
        switch (config.getMethod().toUpperCase()) {
            case "GET":
                requestBuilder.GET();
                break;
            case "POST":
                requestBuilder.POST(HttpRequest.BodyPublishers.ofString(
                        config.getBody() != null ? config.getBody() : ""));
                break;
            case "PUT":
                requestBuilder.PUT(HttpRequest.BodyPublishers.ofString(
                        config.getBody() != null ? config.getBody() : ""));
                break;
            case "DELETE":
                requestBuilder.DELETE();
                break;
            default:
                throw new JobException("Unsupported HTTP method: " + config.getMethod());
        }
    }

    /**
     * 记录HTTP请求指标
     *
     * @param response HTTP响应
     */
    private void recordHttpMetrics(HttpResponse<String> response) {
        meterRegistry.counter("http.request.count").increment();
        meterRegistry.gauge("http.response.status", response.statusCode());
    }

    /**
     * 更新任务状态
     *
     * @param jobId 任务ID
     * @param newStatus 新状态
     * @return 更新结果
     */
    @Transactional
    public Object updateTaskStatus(String jobId, TaskStatus newStatus) {
        String lockKey = LOCK_PREFIX_STATUS + jobId;
        return lockUtil.executeWithLock(lockKey, () -> {
            try {
                TaskDefinition taskDef = getTaskDefinition(jobId);
                if (taskDef == null) {
                    throw new JobException("Task not found: " + jobId);
                }

                // 更新状态
                taskDef.setStatus(newStatus);
                jobDefinitionRepository.save(convertToJobDefinition(taskDef));
                log.info("Updated task status: {} -> {}", jobId, newStatus);

                // 处理Quartz任务状态
                handleQuartzJobStatus(jobId, newStatus);

                // 记录状态变更
                meterRegistry.counter("job.status." + newStatus.name().toLowerCase()).increment();

                return TaskResult.builder()
                        .success(true)
                        .message("Status updated successfully")
                        .build();

            } catch (SchedulerException e) {
                log.error("Failed to update job status: {}", jobId, e);
                return TaskResult.builder()
                        .success(false)
                        .message("Failed to update status: " + e.getMessage())
                        .build();
            }
        });
    }

    /**
     * 处理Quartz任务状态
     *
     * @param jobId 任务ID
     * @param status 新状态
     * @throws SchedulerException 调度器异常
     */
    private void handleQuartzJobStatus(String jobId, TaskStatus status) throws SchedulerException {
        switch (status) {
            case STOPPED:
                scheduler.pauseJob(JobKey.jobKey(jobId));
                log.info("Paused job: {}", jobId);
                break;
            case WAITING:
                scheduler.resumeJob(JobKey.jobKey(jobId));
                log.info("Resumed job: {}", jobId);
                break;
            default:
                handleOtherStatus(jobId, status);
        }
    }

    /**
     * 获取任务定义
     *
     * @param jobId 任务ID
     * @return 任务定义
     */
    private TaskDefinition getTaskDefinition(String jobId) {
        JobDefinition jobDef = jobDefinitionRepository.findById(jobId)
                .orElseThrow(() -> new JobException("Task not found: " + jobId));
        return JsonUtils.fromJson(jobDef.getJobData(), TaskDefinition.class);
    }

    /**
     * 创建执行记录
     *
     * @param taskDef 任务定义
     * @return 执行记录
     */
    private TaskExecution createExecution(TaskDefinition taskDef) {
        TaskExecution execution = new TaskExecution();
        execution.setId(UUID.randomUUID().toString());
        execution.setTaskDefId(taskDef.getId());
        execution.setStartTime(new Date());
        execution.setStatus(TaskStatus.RUNNING);
        return jobExecutionRepository.save(execution);
    }

    /**
     * 解析任务变量
     *
     * @param taskDef 任务定义
     */
    private void resolveVariables(TaskDefinition taskDef) {
        if (taskDef.getHttpConfig() != null) {
            HttpConfig httpConfig = taskDef.getHttpConfig();
            httpConfig.setUrl(VariableUtil.resolveVariable(httpConfig.getUrl()));

            if (httpConfig.getHeaders() != null) {
                Map<String, String> resolvedHeaders = VariableUtil.resolveVariables(httpConfig.getHeaders());
                httpConfig.setHeaders(resolvedHeaders);
            }

            if (httpConfig.getParameters() != null) {
                Map<String, String> resolvedParams = VariableUtil.resolveVariables(httpConfig.getParameters());
                httpConfig.setParameters(resolvedParams);
            }
        }
    }

    /**
     * 执行任务
     *
     * @param taskDef 任务定义
     * @return 执行结果
     * @throws Exception 执行异常
     */
    private Object executeTask(TaskDefinition taskDef) throws Exception {
        switch (taskDef.getType()) {
            case HTTP:
                return executeHttpTask(taskDef);
            case SCHEDULED:
                return executeScheduledTask(taskDef);
            default:
                return executeCustomTask(taskDef);
        }
    }

    /**
     * 执行定时任务
     *
     * @param taskDef 任务定义
     * @return 执行结果
     */
    private Object executeScheduledTask(TaskDefinition taskDef) {
        log.info("Executing scheduled task: {}", taskDef.getId());
        // 实现定时任务的具体逻辑
        return null;
    }

    /**
     * 执行自定义任务
     *
     * @param taskDef 任务定义
     * @return 执行结果
     */
    private Object executeCustomTask(TaskDefinition taskDef) {
        log.info("Executing custom task: {}", taskDef.getId());
        // 实现自定义任务的具体逻辑
        return null;
    }

    /**
     * 更新执行成功记录
     *
     * @param execution 执行记录
     * @param result 执行结果
     */
    private void updateExecutionSuccess(TaskExecution execution, Object result) {
        execution.setEndTime(new Date());
        execution.setStatus(TaskStatus.COMPLETED);
        execution.setResult(JsonUtils.toJson(result));
        jobExecutionRepository.save(execution);
        log.info("Updated execution success: {}", execution.getId());
    }

    /**
     * 验证任务
     *
     * @param taskDef 任务定义
     */
    private void validateTask(TaskDefinition taskDef) {
        if (taskDef == null || taskDef.getId() == null) {
            throw new JobException("Invalid task definition");
        }

        if (taskDef.getType() == null) {
            throw new JobException("Task type is required");
        }

        if (TaskType.HTTP.equals(taskDef.getType())) {
            validateHttpConfig(taskDef.getHttpConfig());
        }
    }

    /**
     * 验证HTTP配置
     *
     * @param httpConfig HTTP配置
     */
    private void validateHttpConfig(HttpConfig httpConfig) {
        if (httpConfig == null) {
            throw new JobException("HTTP config is required for HTTP task");
        }

        if (httpConfig.getUrl() == null || httpConfig.getUrl().trim().isEmpty()) {

            if (httpConfig.getUrl() == null || httpConfig.getUrl().trim().isEmpty()) {
                throw new JobException("URL is required for HTTP task");
            }

            if (httpConfig.getMethod() == null || httpConfig.getMethod().trim().isEmpty()) {
                throw new JobException("HTTP method is required for HTTP task");
            }
        }
    }

        /**
         * 转换为任务定义
         *
         * @param taskDef 原始任务定义
         * @return 转换后的任务定义
         */
        private JobDefinition convertToJobDefinition(TaskDefinition taskDef) {
            JobDefinition jobDef = new JobDefinition();
            jobDef.setId(taskDef.getId());
            jobDef.setName(taskDef.getName());
            jobDef.setDescription(taskDef.getDescription());
            jobDef.setCronExpression(taskDef.getCronExpression());
            jobDef.setJobData(JsonUtils.toJson(taskDef));
            jobDef.setStatus(taskDef.getStatus().ordinal());
            jobDef.setCreateTime(new Date());
            jobDef.setUpdateTime(new Date());
            return jobDef;
        }

        /**
         * 创建任务详情
         *
         * @param taskDef 任务定义
         * @return 任务详情
         */
        private JobDetail createJobDetail(TaskDefinition taskDef) {
            return JobBuilder.newJob(getJobClass(taskDef.getType()))
                    .withIdentity(taskDef.getId())
                    .withDescription(taskDef.getDescription())
                    .usingJobData(createJobDataMap(taskDef))
                    .build();
        }

        /**
         * 创建任务数据Map
         *
         * @param taskDef 任务定义
         * @return 任务数据Map
         */
        private JobDataMap createJobDataMap(TaskDefinition taskDef) {
            JobDataMap dataMap = new JobDataMap();
            dataMap.put("taskId", taskDef.getId());
            dataMap.put("taskType", taskDef.getType().name());
            return dataMap;
        }

        /**
         * 创建触发器
         *
         * @param taskDef 任务定义
         * @return 触发器
         */
        private Trigger createTrigger(TaskDefinition taskDef) {
            return TriggerBuilder.newTrigger()
                    .withIdentity(taskDef.getId() + "_trigger")
                    .withSchedule(CronScheduleBuilder.cronSchedule(taskDef.getCronExpression()))
                    .withDescription("Trigger for task: " + taskDef.getName())
                    .build();
        }

        /**
         * 获取任务类
         *
         * @param type 任务类型
         * @return 任务类
         */
//        private Class<? extends Job> getJobClass(TaskType type) {
//            switch (type) {
//                case HTTP:
//                    return HttpJob.class;
//                case SCHEDULED:
//                    return ScheduledJob.class;
//                default:
//                    return CustomJob.class;
//            }
//        }
    private Class<? extends Job> getJobClass(TaskType type) {
        return switch (type) {
            case HTTP -> HttpJob.class;
            case SCHEDULED -> ScheduledJob.class;
            case CUSTOM -> CustomJob.class;
            // 添加默认情况处理
            default -> throw new IllegalArgumentException("Unsupported task type: " + type);
        };
    }

        /**
         * 创建默认重试策略
         *
         * @return 重试策略
         */
        private RetryPolicy createDefaultRetryPolicy() {
            return RetryPolicy.builder()
                    .maxRetries(3)
                    .retryInterval(1000L)
                    .exponentialBackoff(true)
                    .build();
        }

        /**
         * 处理其他状态
         *
         * @param jobId 任务ID
         * @param status 状态
         */
        private void handleOtherStatus(String jobId, TaskStatus status) {
            log.info("Handling status {} for job {}", status, jobId);
            switch (status) {
                case RUNNING:
                    handleRunningStatus(jobId);
                    break;
                case COMPLETED:
                    handleCompletedStatus(jobId);
                    break;
                case FAILED:
                    handleFailedStatus(jobId);
                    break;
                case SKIPPED:
                    handleSkippedStatus(jobId);
                    break;
                default:
                    log.warn("Unhandled status {} for job {}", status, jobId);
            }
        }

        /**
         * 处理运行中状态
         *
         * @param jobId 任务ID
         */
        private void handleRunningStatus(String jobId) {
            log.info("Task {} is now running", jobId);
            meterRegistry.counter("job.running.count").increment();
        }

        /**
         * 处理完成状态
         *
         * @param jobId 任务ID
         */
        private void handleCompletedStatus(String jobId) {
            log.info("Task {} has completed", jobId);
            meterRegistry.counter("job.completed.count").increment();
            jobMetrics.incrementJobSuccessCount();
        }

        /**
         * 处理失败状态
         *
         * @param jobId 任务ID
         */
        private void handleFailedStatus(String jobId) {
            log.error("Task {} has failed", jobId);
            meterRegistry.counter("job.failed.count").increment();
            jobMetrics.incrementJobFailureCount();
        }

        /**
         * 处理跳过状态
         *
         * @param jobId 任务ID
         */
        private void handleSkippedStatus(String jobId) {
            log.info("Task {} has been skipped", jobId);
            meterRegistry.counter("job.skipped.count").increment();
        }
    }
```

## JobService.java

```java
package com.study.scheduler.service;


import com.study.scheduler.config.RedisConfig;
import com.study.scheduler.entity.JobInfo;
import com.study.scheduler.mapper.JobInfoMapper;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class JobService {
    private static final Logger logger = LoggerFactory.getLogger(RedisConfig.class);
    private static final String JOB_LOCK_KEY_PREFIX = "scheduler:job:lock:";
    @Autowired
    private Scheduler scheduler;
    @Autowired
    private JobInfoMapper jobInfoMapper;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 添加任务
     */
    public void addJob(JobInfo jobInfo) throws Exception {
        // 创建JobDetail
        JobDetail jobDetail = JobBuilder.newJob(getJobClass(jobInfo.getJobClass()))
                .withIdentity(jobInfo.getJobName(), jobInfo.getJobGroup())
                .withDescription(jobInfo.getDescription())
                .build();

        // 创建Trigger
        CronTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(jobInfo.getJobName() + "_trigger", jobInfo.getJobGroup())
                .withSchedule(CronScheduleBuilder.cronSchedule(jobInfo.getCronExpression()))
                .build();

        // 调度任务
        scheduler.scheduleJob(jobDetail, trigger);

        // 保存任务信息
        jobInfoMapper.insert(jobInfo);
    }

    /**
     * 暂停任务
     */
    public void pauseJob(String jobName, String jobGroup) throws SchedulerException {
        scheduler.pauseJob(JobKey.jobKey(jobName, jobGroup));
        JobInfo jobInfo = new JobInfo();
        jobInfo.setStatus(0); // 暂停状态
        jobInfoMapper.updateByJobKey(jobName, jobGroup, jobInfo);
    }

    /**
     * 恢复任务
     */
    public void resumeJob(String jobName, String jobGroup) throws SchedulerException {
        scheduler.resumeJob(JobKey.jobKey(jobName, jobGroup));
        JobInfo jobInfo = new JobInfo();
        jobInfo.setStatus(1); // 运行状态
        jobInfoMapper.updateByJobKey(jobName, jobGroup, jobInfo);
    }

    /**
     * 删除任务
     */
    public void deleteJob(String jobName, String jobGroup) throws SchedulerException {
        scheduler.deleteJob(JobKey.jobKey(jobName, jobGroup));
        jobInfoMapper.deleteByJobKey(jobName, jobGroup);
    }

    /**
     * 修改任务cron表达式
     */
    public void updateJobCron(JobInfo jobInfo) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobInfo.getJobName() + "_trigger", jobInfo.getJobGroup());
        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);

        // 创建新的trigger
        CronTrigger newTrigger = trigger.getTriggerBuilder()
                .withIdentity(triggerKey)
                .withSchedule(CronScheduleBuilder.cronSchedule(jobInfo.getCronExpression()))
                .build();

        scheduler.rescheduleJob(triggerKey, newTrigger);
        jobInfoMapper.updateByJobKey(jobInfo.getJobName(), jobInfo.getJobGroup(), jobInfo);
    }

    private Class<? extends Job> getJobClass(String jobClass) throws Exception {
        return (Class<? extends Job>) Class.forName(jobClass);
    }

    /**
     * 使用Redis分布式锁执行任务
     */
    public void executeJob(String jobName, Runnable task) {
        String lockKey = JOB_LOCK_KEY_PREFIX + jobName;
        Boolean acquired = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, "LOCKED", Duration.ofMinutes(10));

        if (Boolean.TRUE.equals(acquired)) {
            try {
                task.run();
            } finally {
                redisTemplate.delete(lockKey);
            }
        } else {
            logger.warn("Job {} is already running on another instance", jobName);
        }
    }
}

```

## YourService.java

```java
package com.study.scheduler.service;

import com.study.common.model.task.HttpConfig;
import com.study.common.model.task.TaskDefinition;
import com.study.common.model.task.TaskResult;
import com.study.common.model.task.TaskType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class YourService {

    @Autowired
    private EnhancedJobService jobService;

    public void createDailyJob() {
        // 创建每天执行的定时任务
        TaskDefinition taskDef = TaskDefinition.builder()
                .id("daily-report-job")
                .name("Daily Report Generation")
                .type(TaskType.SCHEDULED)
                .cronExpression("0 0 1 * * ?") // 每天凌晨1点执行
                .build();

        jobService.saveJob(taskDef);
    }

    public void createHttpCallJob() {
        // 创建HTTP调用任务
        TaskDefinition taskDef = TaskDefinition.builder()
                .id("api-sync-job")
                .name("API Synchronization")
                .type(TaskType.HTTP)
                .cronExpression("0 */30 * * * ?") // 每30分钟执行
                .httpConfig(HttpConfig.builder()
                        .url("http://api.example.com/sync")
                        .method("POST")
                        .headers(Map.of("Authorization", "${api.token}"))
                        .build())
                .build();

        jobService.saveJob(taskDef);
    }

    public void monitorJob(String jobId) {
        // 检查任务执行状态
        TaskResult result = jobService.executeJob(jobId);
        if (!result.isSuccess()) {
            // 处理失败情况
            handleJobFailure(jobId, result.getMessage());
        }
    }

    private void handleJobFailure(String jobId, String message) {
        log.error("Job {} failed: {}", jobId, message);
        // 发送告警等操作
    }
}

```

## HttpClientUtil.java

```java
package com.study.scheduler.utils;

import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class HttpClientUtil {
    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

    // HTTP客户端相关配置
    private static final int MAX_TOTAL_CONNECTIONS = 500;
    private static final int DEFAULT_MAX_PER_ROUTE = 100;
    private static final int REQUEST_TIMEOUT = 10000;
    private static final int CONNECT_TIMEOUT = 10000;
    private static final int SOCKET_TIMEOUT = 10000;
    private static final int DEFAULT_KEEP_ALIVE_TIME_MILLIS = 20 * 1000;
    private static final int CLOSE_IDLE_CONNECTION_WAIT_TIME_SECS = 30;

    // 线程池配置
    private static final int CORE_POOL_SIZE = 1;
    private static final int MAX_POOL_SIZE = 1;
    private static final int QUEUE_CAPACITY = 100;
    private static final int MONITOR_PERIOD_SECONDS = 10;
    private static final int SHUTDOWN_TIMEOUT_SECONDS = 5;
    private static final String MONITOR_THREAD_PREFIX = "http-connection-monitor-";
    private static final AtomicBoolean isMonitorRunning = new AtomicBoolean(false);
    // 实例变量
    private static CloseableHttpClient httpClient;
    private static PoolingHttpClientConnectionManager connectionManager;
    private static RequestConfig requestConfig;
    private static ScheduledThreadPoolExecutor connectionMonitorExecutor;

    static {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) ->
                logger.error("Uncaught exception in thread {}", thread.getName(), throwable));

        try {
            init();
        } catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
            logger.error("Failed to initialize HttpClientUtil", e);
            throw new IllegalStateException("Failed to initialize HttpClientUtil", e);
        }
    }

    private HttpClientUtil() {
        // 工具类禁止实例化
    }

    private static void init() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
        initSslContext();
        initConnectionManager();
        initHttpClient();
        startIdleConnectionMonitor();
        logger.info("HttpClientUtil initialized successfully");
    }

    private static void initSslContext() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
        SSLContext sslContext = SSLContextBuilder.create()
                .loadTrustMaterial(null, (X509Certificate[] chain, String authType) -> true)
                .build();

        SSLConnectionSocketFactory sslFactory = new SSLConnectionSocketFactory(
                sslContext,
                new String[]{"TLSv1", "TLSv1.1", "TLSv1.2", "TLSv1.3"},
                null,
                NoopHostnameVerifier.INSTANCE);

        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", sslFactory)
                .build();

        connectionManager = new PoolingHttpClientConnectionManager(registry);
        connectionManager.setMaxTotal(MAX_TOTAL_CONNECTIONS);
        connectionManager.setDefaultMaxPerRoute(DEFAULT_MAX_PER_ROUTE);
    }

    private static void initConnectionManager() {
        requestConfig = RequestConfig.custom()
                .setConnectTimeout(CONNECT_TIMEOUT)
                .setSocketTimeout(SOCKET_TIMEOUT)
                .setConnectionRequestTimeout(REQUEST_TIMEOUT)
                .build();
    }

    private static void initHttpClient() {
        ConnectionKeepAliveStrategy keepAliveStrategy = (response, context) -> {
            HeaderElementIterator it = new BasicHeaderElementIterator(
                    response.headerIterator(HTTP.CONN_KEEP_ALIVE));
            while (it.hasNext()) {
                HeaderElement he = it.nextElement();
                String param = he.getName();
                String value = he.getValue();
                if (value != null && param.equalsIgnoreCase("timeout")) {
                    return Long.parseLong(value) * 1000;
                }
            }
            return DEFAULT_KEEP_ALIVE_TIME_MILLIS;
        };

        httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setKeepAliveStrategy(keepAliveStrategy)
                .setDefaultRequestConfig(requestConfig)
                .build();
    }

    private static void startIdleConnectionMonitor() {
        if (!isMonitorRunning.compareAndSet(false, true)) {
            logger.warn("Connection monitor is already running");
            return;
        }
        createAndScheduleMonitor();
    }

    private static void createAndScheduleMonitor() {
        ThreadFactory threadFactory = new ThreadFactory() {
            private final AtomicInteger threadNumber = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName(MONITOR_THREAD_PREFIX + threadNumber.getAndIncrement());
                thread.setDaemon(true);
                return thread;
            }
        };

        RejectedExecutionHandler rejectedHandler = (r, executor) ->
                logger.error("Task rejected from connection monitor executor");

        connectionMonitorExecutor = new ScheduledThreadPoolExecutor(
                CORE_POOL_SIZE,
                threadFactory,
                rejectedHandler
        );
        connectionMonitorExecutor.setMaximumPoolSize(MAX_POOL_SIZE);
        connectionMonitorExecutor.setKeepAliveTime(60, TimeUnit.SECONDS);

        try {
            connectionMonitorExecutor.scheduleAtFixedRate(
                    new ConnectionMonitorTask(),
                    0,
                    MONITOR_PERIOD_SECONDS,
                    TimeUnit.SECONDS
            );
            logger.info("Connection monitor scheduled successfully");
        } catch (RejectedExecutionException e) {
            logger.error("Failed to schedule connection monitor", e);
            shutdownMonitor();
        }
    }

    public static String doGet(String url) throws IOException {
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);
        httpGet.setHeader("User-Agent", "Mozilla/5.0");
        httpGet.setHeader("Accept", "*/*");

        return executeRequest(httpGet);
    }

    public static String doPost(String url, String jsonBody) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("User-Agent", "Mozilla/5.0");
        httpPost.setHeader("Accept", "*/*");

        if (jsonBody != null) {
            httpPost.setEntity(new StringEntity(jsonBody, StandardCharsets.UTF_8));
        }

        return executeRequest(httpPost);
    }

    private static String executeRequest(HttpRequestBase request) throws IOException {
        logger.debug("Executing request to: {}", request.getURI());

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

            logger.debug("Response status code: {}", statusCode);
            logger.debug("Response body: {}", responseBody);

            if (statusCode >= 200 && statusCode < 300) {
                return responseBody;
            }
            throw new IOException("Unexpected response status: " + statusCode + ", body: " + responseBody);
        }
    }

    private static void shutdownMonitor() {
        if (!isMonitorRunning.get()) {
            return;
        }

        try {
            if (connectionMonitorExecutor != null) {
                connectionMonitorExecutor.shutdown();
                boolean terminated = connectionMonitorExecutor.awaitTermination(
                        SHUTDOWN_TIMEOUT_SECONDS,
                        TimeUnit.SECONDS
                );
                if (!terminated) {
                    List<Runnable> pendingTasks = connectionMonitorExecutor.shutdownNow();
                    logger.warn("Force shutdown connection monitor, {} tasks not completed",
                            pendingTasks.size());
                    connectionMonitorExecutor.awaitTermination(1, TimeUnit.SECONDS);
                }
            }
        } catch (InterruptedException e) {
            logger.error("Error while shutting down connection monitor", e);
            if (connectionMonitorExecutor != null) {
                List<Runnable> pendingTasks = connectionMonitorExecutor.shutdownNow();
                logger.warn("{} tasks not completed", pendingTasks.size());
            }
        } finally {
            isMonitorRunning.set(false);
        }
    }

    public static void close() {
        try {
            logger.info("Shutting down HttpClientUtil...");
            shutdownMonitor();
            if (httpClient != null) {
                httpClient.close();
            }
            if (connectionManager != null) {
                connectionManager.close();
            }
            logger.info("HttpClientUtil shut down successfully");
        } catch (IOException e) {
            logger.error("Error while shutting down HttpClientUtil", e);
        }
    }

    private static class ConnectionMonitorTask implements Runnable {
        @Override
        public void run() {
            try {
                synchronized (connectionManager) {
                    connectionManager.closeExpiredConnections();
                    connectionManager.closeIdleConnections(
                            CLOSE_IDLE_CONNECTION_WAIT_TIME_SECS,
                            TimeUnit.SECONDS
                    );
                }
                logger.debug("Connection maintenance completed");
            } catch (RuntimeException e) {
                logger.error("Error during connection maintenance", e);
            }
        }
    }
}
```

## MongoDBUtils.java

```java
package com.study.scheduler.utils;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MongoDBUtils {
    private final MongoTemplate mongoTemplate;

    public MongoDBUtils(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public <T> T save(T entity) {
        return mongoTemplate.save(entity);
    }

    public <T> List<T> findAll(Class<T> entityClass) {
        return mongoTemplate.findAll(entityClass);
    }

    public <T> T findById(String id, Class<T> entityClass) {
        return mongoTemplate.findById(id, entityClass);
    }

    public <T> List<T> find(Query query, Class<T> entityClass) {
        return mongoTemplate.find(query, entityClass);
    }

    public <T> T findOne(Query query, Class<T> entityClass) {
        return mongoTemplate.findOne(query, entityClass);
    }

    public <T> UpdateResult update(Query query, Update update, Class<T> entityClass) {
        return mongoTemplate.updateMulti(query, update, entityClass);
    }

    public <T> DeleteResult delete(Query query, Class<T> entityClass) {
        return mongoTemplate.remove(query, entityClass);
    }

    public <T> long count(Query query, Class<T> entityClass) {
        return mongoTemplate.count(query, entityClass);
    }

    public <T> List<T> findByPage(Query query, Class<T> entityClass, int page, int size) {
        query.skip((long) (page - 1) * size).limit(size);
        return mongoTemplate.find(query, entityClass);
    }

    public <T> List<T> findBySort(Query query, Class<T> entityClass, Sort sort) {
        query.with(sort);
        return mongoTemplate.find(query, entityClass);
    }
}
```

## application.properties

```properties
#spring.data.redis.password=123456
#spring.data.redis.timeout=5000
#spring.data.redis.cluster.nodes=192.168.80.137:6379,192.168.80.137:6380,192.168.80.137:6381,192.168.80.137:6382,192.168.80.137:6383,192.168.80.137:6384

# Server
server.port=8081
spring.application.name=platform-scheduler

# DataSource
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver
spring.datasource.url=jdbc:mariadb://192.168.80.137:3306/test?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai
spring.datasource.username=root
spring.datasource.password=123456
spring.datasource.type=com.zaxxer.hikari.HikariDataSource

# Hikari Pool
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.maximum-pool-size=15
spring.datasource.hikari.idle-timeout=30000
spring.datasource.hikari.pool-name=SchedulerHikariCP
spring.datasource.hikari.max-lifetime=1800000
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.connection-test-query=SELECT 1

# Quartz
spring.quartz.job-store-type=jdbc
spring.quartz.jdbc.initialize-schema=always
spring.quartz.properties.org.quartz.jobStore.useProperties=false
spring.quartz.properties.org.quartz.scheduler.instanceName=ClusterScheduler
spring.quartz.properties.org.quartz.scheduler.instanceId=AUTO
spring.quartz.properties.org.quartz.jobStore.class=org.springframework.scheduling.quartz.LocalDataSourceJobStore
spring.quartz.properties.org.quartz.jobStore.driverDelegateClass=org.quartz.impl.jdbcjobstore.StdJDBCDelegate
spring.quartz.properties.org.quartz.jobStore.tablePrefix=QRTZ_
spring.quartz.properties.org.quartz.jobStore.isClustered=true
spring.quartz.properties.org.quartz.jobStore.clusterCheckinInterval=10000
spring.quartz.properties.org.quartz.jobStore.dataSource=quartzDataSource

# Redis
spring.data.redis.password=123456
spring.data.redis.timeout=5000
spring.data.redis.cluster.nodes=192.168.80.137:6379,192.168.80.137:6380,192.168.80.137:6381,192.168.80.137:6382,192.168.80.137:6383,192.168.80.137:6384
spring.data.redis.lettuce.pool.max-active=8
spring.data.redis.lettuce.pool.max-idle=8
spring.data.redis.lettuce.pool.min-idle=0
spring.data.redis.lettuce.pool.max-wait=1000

# MongoDB
spring.data.mongodb.uri=mongodb://root:123456@192.168.80.137:27017
spring.data.mongodb.database=crawler
spring.data.mongodb.auto-index-creation=true

# MyBatis
mybatis.mapper-locations=classpath:mapper/*.xml
mybatis.configuration.map-underscore-to-camel-case=true
mybatis.configuration.log-impl=org.apache.ibatis.logging.slf4j.Slf4jImpl

# Logging
logging.level.com.study=debug
logging.level.org.springframework.jdbc.core=debug
logging.level.org.apache.http=DEBUG
logging.level.javax.net=DEBUG

# Scheduler
scheduler.log.retain-days=30
collect.service.url=http://localhost:8082
collect.service.urls[0]=http://localhost:8082
collect.service.urls[1]=http://localhost:8083
collect.service.urls[2]=http://localhost:8084
collect.scheduler.tasks.collect-distribution.cron=0/30 * * * * ?
collect.scheduler.tasks.health-sync.cron=0 0/5 * * * ?
collect.scheduler.tasks.failed-retry.cron=0 0/10 * * * ?
collect.scheduler.jobs.tree-collection.cron=0 0 1 * * ?
collect.scheduler.jobs.tree-collection.data.url=http://example.com/api/tree-data
collect.scheduler.jobs.tree-collection.data.method=GET
```

## SchedulerDatabaseTables.sql

```sql
-- 任务信息表
CREATE TABLE schedule_job_info
(
    id              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    job_name        VARCHAR(100) NOT NULL COMMENT '任务名称',
    job_group       VARCHAR(100) NOT NULL COMMENT '任务分组',
    job_class       VARCHAR(255) NOT NULL COMMENT '任务类',
    cron_expression VARCHAR(100) NOT NULL COMMENT 'cron表达式',
    parameter       TEXT COMMENT '任务参数（JSON格式）',
    description     VARCHAR(500) COMMENT '任务描述',
    concurrent      TINYINT      NOT NULL DEFAULT 0 COMMENT '是否允许并发执行：0-否，1-是',
    status          TINYINT      NOT NULL DEFAULT 0 COMMENT '任务状态：0-暂停，1-运行',
    next_fire_time  DATETIME COMMENT '下次执行时间',
    prev_fire_time  DATETIME COMMENT '上次执行时间',
    create_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_job_key (job_name, job_group),
    KEY             idx_status (status),
    KEY             idx_next_fire_time (next_fire_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='定时任务信息表';

-- 任务执行日志表
CREATE TABLE schedule_job_log
(
    id             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    job_id         BIGINT COMMENT '任务ID',
    job_name       VARCHAR(100) NOT NULL COMMENT '任务名称',
    job_group      VARCHAR(100) NOT NULL COMMENT '任务分组',
    job_class      VARCHAR(255) NOT NULL COMMENT '任务类',
    parameter      TEXT COMMENT '执行参数',
    message        VARCHAR(500) COMMENT '日志信息',
    status         TINYINT      NOT NULL COMMENT '执行状态：0-失败，1-成功',
    exception_info TEXT COMMENT '异常信息',
    start_time     DATETIME     NOT NULL COMMENT '开始时间',
    end_time       DATETIME COMMENT '结束时间',
    duration       BIGINT COMMENT '执行时长(毫秒)',
    server_ip      VARCHAR(50) COMMENT '执行服务器IP',
    PRIMARY KEY (id),
    KEY            idx_job_name (job_name),
    KEY            idx_job_group (job_group),
    KEY            idx_start_time (start_time),
    KEY            idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='定时任务执行日志表';
```

