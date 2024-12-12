# Project Structure

```
springboot3.3.x/
    pom.xml
    platform-collect/
        pom.xml
        src/
            main/
                java/
                    com/
                        study/
                            collect/
                                CollectApplication.java
                                annotation/
                                    CollectorFor.java
                                config/
                                    CollectConfig.java
                                    DatabaseConfig.java
                                    MongoConfig.java
                                    RabbitConfig.java
                                    RedisConfig.java
                                controller/
                                    CollectController.java
                                    MonitorController.java
                                    TaskController.java
                                    TestController.java
                                    TreeCollectController.java
                                    TreeController.java
                                    TreeQueryController.java
                                    TreeTestController.java
                                core/
                                    collector/
                                        Collector.java
                                        DistributedCollector.java
                                        TreeCollector.java
                                    executor/
                                        CollectExecutor.java
                                    strategy/
                                        CollectorStrategy.java
                                entity/
                                    AlertLog.java
                                    AlertMessage.java
                                    AlertRecord.java
                                    AlertRule.java
                                    CollectData.java
                                    CollectResult.java
                                    CollectTask.java
                                    MongoTestEntity.java
                                    SystemMetrics.java
                                    TaskLog.java
                                    TaskResult.java
                                    TaskStatusStatistics.java
                                    TestEntity.java
                                    TreeCollectRequest.java
                                    TreeCollectTask.java
                                    TreeNode.java
                                enums/
                                    AlertLevel.java
                                    CollectorType.java
                                    RetryStrategy.java
                                    TaskStatus.java
                                handler/
                                    MessageHandler.java
                                listener/
                                    RabbitMessageListener.java
                                mapper/
                                    CollectDataMapper.java
                                    TaskLogMapper.java
                                    TestMySQLMapper.java
                                model/
                                    TestDocument.java
                                monitor/
                                    MonitorService.java
                                repository/
                                    CollectDataRepository.java
                                    CollectResultRepository.java
                                    TaskRepository.java
                                    TestMongoRepository.java
                                service/
                                    CollectService.java
                                    ConfigurationService.java
                                    TaskManagementService.java
                                    TestService.java
                                    TreeCollectorService.java
                                    TreeQueryService.java
                                utils/
                                    NetworkUtil.java
                                    RabbitMQUtils.java
                                    RetryUtil.java
                                    SystemResourceUtil.java
                resources/
                    application.yml
                    init.sql
                    test.http
                    test.sql
                    testTree.http
                    tree.http
                    TreeHttp.http
                    treequery.http
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

## CollectorFor.java

```java
package com.study.collect.annotation;


import com.study.collect.enums.CollectorType;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CollectorFor {
    CollectorType value();
}
```

## CollectConfig.java

```java
package com.study.collect.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class CollectConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(10))
                .build();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
}
```

## DatabaseConfig.java

```java
package com.study.collect.config;

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

## MongoConfig.java

```java
package com.study.collect.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.study.collect.repository")
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Value("${spring.data.mongodb.database}")
    private String database;

    @Value("${spring.data.mongodb.uri}")
    private String uri;

    @Override
    protected String getDatabaseName() {
        return database;
    }

    @Override
    public MongoClient mongoClient() {
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(uri))
                .build();
        return MongoClients.create(settings);
    }

    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        MongoDatabaseFactory factory = new SimpleMongoClientDatabaseFactory(mongoClient(), getDatabaseName());
        MappingMongoConverter converter = new MappingMongoConverter(
                new DefaultDbRefResolver(factory),
                new MongoMappingContext()
        );
        // 移除_class字段
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        return new MongoTemplate(factory, converter);
    }
}
```

## RabbitConfig.java

```java
package com.study.collect.config;

import com.study.collect.listener.RabbitMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class RabbitConfig {

    // 测试用的交换机、队列和路由键名称
    private static final String TEST_EXCHANGE = "test.exchange";
    private static final String TEST_QUEUE = "test.queue";
    private static final String TEST_ROUTING_KEY = "test.message";
    // 数据采集用的交换机、队列和路由键名称
    private static final String DATA_EXCHANGE = "data.collect.exchange";
    private static final String DATA_QUEUE = "data.collect.queue";
    private static final String DATA_ROUTING_KEY = "data.collect";
    private static final String DEAD_LETTER_EXCHANGE = "data.collect.dlx";
    private static final String DEAD_LETTER_QUEUE = "data.collect.dead.queue";
    private static final String DEAD_LETTER_ROUTING_KEY = "data.collect.dead";
    @Value("${spring.rabbitmq.host}")
    private String host;
    @Value("${spring.rabbitmq.port}")
    private int port;
    @Value("${spring.rabbitmq.username}")
    private String username;
    @Value("${spring.rabbitmq.password}")
    private String password;
    @Value("${spring.rabbitmq.virtual-host}")
    private String virtualHost;

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(virtualHost);
        connectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
        connectionFactory.setPublisherReturns(true);
        return connectionFactory;
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        template.setMandatory(true);

        template.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) {
                log.error("Message send failed: {}", cause);
            }
        });

        template.setReturnsCallback(returned -> {
            log.error("Message returned: {}, replyText: {}",
                    returned.getMessage(), returned.getReplyText());
        });

        return template;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    // 测试相关的Bean定义
    @Bean
    public DirectExchange testExchange() {
        return new DirectExchange(TEST_EXCHANGE);
    }

    @Bean
    public Queue testQueue() {
//        return new Queue(TEST_QUEUE);
        return new Queue("test.message");
    }

    @Bean
    public Binding testBinding() {
        return BindingBuilder.bind(testQueue())
                .to(testExchange())
                .with(TEST_ROUTING_KEY);
    }

    // 数据采集相关的Bean定义
    @Bean
    public Queue dataCollectQueue() {
        return QueueBuilder.durable(DATA_QUEUE)
                .withArgument("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DEAD_LETTER_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue dataCollectDeadQueue() {
        return QueueBuilder.durable(DEAD_LETTER_QUEUE).build();
    }

    @Bean
    public DirectExchange dataCollectExchange() {
        return new DirectExchange(DATA_EXCHANGE);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DEAD_LETTER_EXCHANGE);
    }

    @Bean
    public Binding dataCollectBinding() {
        return BindingBuilder.bind(dataCollectQueue())
                .to(dataCollectExchange())
                .with(DATA_ROUTING_KEY);
    }

    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(dataCollectDeadQueue())
                .to(deadLetterExchange())
                .with(DEAD_LETTER_ROUTING_KEY);
    }

    @Bean
    public SimpleMessageListenerContainer messageListenerContainer(ConnectionFactory connectionFactory, RabbitMessageListener rabbitMessageListener) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setQueueNames(DATA_QUEUE, TEST_ROUTING_KEY);
        container.setMessageListener(rabbitMessageListener); // 设置消息监听器
        container.setConcurrentConsumers(3);
        container.setMaxConcurrentConsumers(10);
        container.setPrefetchCount(1);
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return container;
    }

}

```

## RedisConfig.java

```java
package com.study.collect.config;

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
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 配置序列化器
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();
        StringRedisSerializer stringSerializer = new StringRedisSerializer();

        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        return template;
    }
}
```

## CollectController.java

```java
package com.study.collect.controller;

import com.study.collect.service.CollectService;
import com.study.common.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/collect")
public class CollectController {

    @Autowired
    private CollectService collectService;

    @PostMapping("/data")
    public Result<com.study.collect.entity.CollectData> saveData(@RequestBody com.study.collect.entity.CollectData data) {
        collectService.saveData(data);
        return Result.success(data);
    }

    @GetMapping("/data")
    public Result<List<com.study.collect.entity.CollectData>> queryData(
            @RequestParam String deviceId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime) {
        List<com.study.collect.entity.CollectData> data = collectService.queryData(deviceId, startTime, endTime);
        return Result.success(data);
    }

    @GetMapping("/data/high-temperature")
    public Result<List<com.study.collect.entity.CollectData>> findHighTemperatureData(
            @RequestParam(defaultValue = "30.0") Double threshold) {
        List<com.study.collect.entity.CollectData> data = collectService.findHighTemperatureData(threshold);
        return Result.success(data);
    }
}
```

## MonitorController.java

```java
package com.study.collect.controller;

import com.study.collect.entity.AlertMessage;
import com.study.collect.entity.SystemMetrics;
import com.study.collect.entity.TaskStatusStatistics;
import com.study.collect.monitor.MonitorService;
import com.study.common.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/monitor")
public class MonitorController {

    @Autowired
    private MonitorService monitorService;

    @GetMapping("/metrics")
    public Result<SystemMetrics> getSystemMetrics() {
        try {
            SystemMetrics metrics = monitorService.collectSystemMetrics();
            return Result.success(metrics);
        } catch (Exception e) {
            return Result.error("Failed to get system metrics: " + e.getMessage());
        }
    }

    @GetMapping("/tasks/status")
    public Result<TaskStatusStatistics> getTaskStatusStatistics() {
        try {
            TaskStatusStatistics stats = monitorService.getTaskStatusStatistics();
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error("Failed to get task statistics: " + e.getMessage());
        }
    }

    @GetMapping("/alerts")
    public Result<List<AlertMessage>> getActiveAlerts() {
        try {
            List<AlertMessage> alerts = monitorService.getActiveAlerts();
            return Result.success(alerts);
        } catch (Exception e) {
            return Result.error("Failed to get alerts: " + e.getMessage());
        }
    }

    @PostMapping("/alerts/{alertId}/acknowledge")
    public Result<Void> acknowledgeAlert(@PathVariable String alertId) {
        try {
            monitorService.acknowledgeAlert(alertId);
            return Result.success();
        } catch (Exception e) {
            return Result.error("Failed to acknowledge alert: " + e.getMessage());
        }
    }
}
```

## TaskController.java

```java
package com.study.collect.controller;

import com.study.collect.entity.CollectTask;
import com.study.collect.entity.TaskResult;
import com.study.collect.enums.TaskStatus;
import com.study.collect.service.TaskManagementService;
import com.study.common.util.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskManagementService taskService;

    public TaskController(TaskManagementService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public Result<CollectTask> createTask(@RequestBody CollectTask task) {
        try {
            CollectTask createdTask = taskService.createTask(task);
            return Result.success(createdTask);
        } catch (Exception e) {
            return Result.error("Failed to create task: " + e.getMessage());
        }
    }

    @PostMapping("/{taskId}/execute")
    public Result<TaskResult> executeTask(@PathVariable String taskId) {
        try {
            TaskResult result = taskService.executeTask(taskId);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("Failed to execute task: " + e.getMessage());
        }
    }

    @PostMapping("/{taskId}/stop")
    public Result<Void> stopTask(@PathVariable String taskId) {
        try {
            taskService.stopTask(taskId);
            return Result.success();
        } catch (Exception e) {
            return Result.error("Failed to stop task: " + e.getMessage());
        }
    }

    @PostMapping("/{taskId}/retry")
    public Result<TaskResult> retryTask(@PathVariable String taskId) {
        try {
            TaskResult result = taskService.retryTask(taskId);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("Failed to retry task: " + e.getMessage());
        }
    }

    @GetMapping("/{taskId}")
    public Result<CollectTask> getTask(@PathVariable String taskId) {
        return taskService.getTask(taskId)
                .map(Result::success)
                .orElse(Result.error("Task not found"));
    }

    @GetMapping("/status/{status}")
    public Result<List<CollectTask>> getTasksByStatus(@PathVariable TaskStatus status) {
        try {
            List<CollectTask> tasks = taskService.getTasksByStatus(status);
            return Result.success(tasks);
        } catch (Exception e) {
            return Result.error("Failed to get tasks: " + e.getMessage());
        }
    }
}


```

## TestController.java

```java
package com.study.collect.controller;

import com.study.collect.entity.MongoTestEntity;
import com.study.collect.entity.TestEntity;
import com.study.collect.service.TestService;
import com.study.common.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private TestService testService;

    @PostMapping("/mysql")
    public Result<TestEntity> testMySQL(@RequestParam("name") String name, @RequestParam("description") String description) {
        try {
            TestEntity result = testService.testMySQL(name, description);
            return Result.success(result);
        } catch (Exception e) {
            log.error("MySQL测试失败", e);
            return Result.error("MySQL测试失败: " + e.getMessage());
        }
    }

    @PostMapping("/redis")
    public Result<Void> testRedis(@RequestParam("key") String key, @RequestParam("value") String value) {
        try {
            testService.testRedis(key, value);
            return Result.success();
        } catch (Exception e) {
            log.error("Redis测试失败", e);
            return Result.error("Redis测试失败: " + e.getMessage());
        }
    }

    @PostMapping("/mongodb")
    public Result<MongoTestEntity> testMongoDB(@RequestParam("name") String name, @RequestParam("description") String description) {
        try {
            MongoTestEntity result = testService.testMongoDB(name, description);
            return Result.success(result);
        } catch (Exception e) {
            log.error("MongoDB测试失败", e);
            return Result.error("MongoDB测试失败: " + e.getMessage());
        }
    }

    @PostMapping("/rabbitmq")
    public Result<Void> testRabbitMQ(@RequestParam("message") String message) {
        try {
            testService.testRabbitMQ(message);
            return Result.success();
        } catch (Exception e) {
            log.error("RabbitMQ测试失败", e);
            return Result.error("RabbitMQ测试失败: " + e.getMessage());
        }
    }
}
```

## TreeCollectController.java

```java
package com.study.collect.controller;

import com.study.collect.entity.CollectTask;
import com.study.collect.entity.TaskResult;
import com.study.collect.entity.TreeCollectRequest;
import com.study.collect.enums.CollectorType;
import com.study.collect.enums.TaskStatus;
import com.study.collect.service.TaskManagementService;
import com.study.common.util.Result;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/api/tree")
public class TreeCollectController {


    private final TaskManagementService taskService;

    public TreeCollectController(TaskManagementService taskService) {
        this.taskService = taskService;
    }


    @PostMapping("/collect")
    public Result<CollectTask> createCollectionTask(@RequestBody TreeCollectRequest request) {
        try {
            CollectTask task = CollectTask.builder()
                    .name("Tree Structure Collection")
                    .url(request.getUrl())
                    .method(request.getMethod())
                    .headers(request.getHeaders())
                    .requestBody(request.getRequestBody())
                    .status(TaskStatus.CREATED)
                    .collectorType(CollectorType.TREE)  // 指定使用树状采集器
                    .maxRetries(3)
                    .retryInterval(1000L)
                    .createTime(new Date())
                    .updateTime(new Date())
                    .build();

            // 先创建任务
            CollectTask createdTask = taskService.createTask(task);

            // 立即执行任务
            TaskResult result = taskService.executeTask(createdTask.getId());

            return Result.success(createdTask);
        } catch (Exception e) {
            return Result.error("Failed to create collection task: " + e.getMessage());
        }
    }


    @PostMapping("/collect/{taskId}/execute")
    public Result<TaskResult> executeCollectionTask(@PathVariable String taskId) {
        try {
            Optional<CollectTask> taskOpt = taskService.getTask(taskId);
            if (taskOpt.isEmpty()) {
                return Result.error("Task not found");
            }

            CollectTask task = taskOpt.get();
            // 检查任务是否可执行
            if (!TaskStatus.CREATED.equals(task.getStatus())) {
                return Result.error("Task is not in CREATED status");
            }

            TaskResult result = taskService.executeTask(taskId);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("Failed to execute task: " + e.getMessage());
        }
    }

//    @PostMapping("/collect")
//    public Result<CollectTask> createTreeCollectionTask(@RequestParam String projectId) {
//        try {
//            // 使用通用任务管理创建采集任务
//            CollectTask task = CollectTask.builder()
//                    .name("Tree Structure Collection")
//                    .parameter("projectId", projectId)
//                    .maxRetries(3)
//                    .retryInterval(1000L)
//                    .build();
//
//            CollectTask createdTask = taskService.createTask(task);
//            return Result.success(createdTask);
//        } catch (Exception e) {
//            return Result.error("Failed to create tree collection task: " + e.getMessage());
//        }
//    }

    @GetMapping("/result/{taskId}")
    public Result<CollectTask> getCollectionResult(@PathVariable String taskId) {
        try {
            Optional<CollectTask> task = taskService.getTask(taskId);
            return task.map(Result::success)
                    .orElse(Result.error("Task not found"));
        } catch (Exception e) {
            return Result.error("Failed to get task result: " + e.getMessage());
        }
    }
}

```

## TreeController.java

```java
package com.study.collect.controller;


import com.study.collect.entity.TreeCollectTask;
import com.study.collect.entity.TreeNode;
import com.study.collect.service.TreeCollectorService;
import com.study.common.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tree")
public class TreeController {

    @Autowired
    private TreeCollectorService treeCollectorService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @PostMapping("/collect/{projectId}")
    public Result<TreeCollectTask> startCollection(@PathVariable String projectId) {
        try {
            TreeCollectTask task = treeCollectorService.startCollection(projectId);
            return Result.success(task);
        } catch (Exception e) {
            return Result.error("Failed to start collection: " + e.getMessage());
        }
    }

    @GetMapping("/nodes/{projectId}")
    public Result<List<TreeNode>> getProjectNodes(@PathVariable String projectId) {
        try {
            List<TreeNode> nodes = mongoTemplate.find(
                    Query.query(Criteria.where("projectId").is(projectId)),
                    TreeNode.class
            );
            return Result.success(nodes);
        } catch (Exception e) {
            return Result.error("Failed to get nodes: " + e.getMessage());
        }
    }
}


```

## TreeQueryController.java

```java
package com.study.collect.controller;

import com.study.collect.entity.TreeNode;
import com.study.collect.service.TreeQueryService;
import com.study.common.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tree/query")
public class TreeQueryController {

    @Autowired
    private TreeQueryService treeQueryService;

    @GetMapping("/project/{projectId}")
    public Result<List<TreeNode>> getProjectTree(@PathVariable String projectId) {
        try {
            List<TreeNode> tree = treeQueryService.getProjectTree(projectId);
            return Result.success(tree);
        } catch (Exception e) {
            return Result.error("Failed to get project tree: " + e.getMessage());
        }
    }

    @GetMapping("/node/{nodeId}")
    public Result<List<TreeNode>> getSubTree(@PathVariable String nodeId) {
        try {
            List<TreeNode> subTree = treeQueryService.getSubTree(nodeId);
            return Result.success(subTree);
        } catch (Exception e) {
            return Result.error("Failed to get sub tree: " + e.getMessage());
        }
    }

    @GetMapping("/type/{projectId}/{type}")
    public Result<List<TreeNode>> getNodesByType(
            @PathVariable String projectId,
            @PathVariable TreeNode.NodeType type) {
        try {
            List<TreeNode> nodes = treeQueryService.getNodesByType(projectId, type);
            return Result.success(nodes);
        } catch (Exception e) {
            return Result.error("Failed to get nodes by type: " + e.getMessage());
        }
    }

    @GetMapping("/children/{parentId}")
    public Result<List<TreeNode>> getDirectChildren(@PathVariable String parentId) {
        try {
            List<TreeNode> children = treeQueryService.getDirectChildren(parentId);
            return Result.success(children);
        } catch (Exception e) {
            return Result.error("Failed to get children: " + e.getMessage());
        }
    }
}

```

## TreeTestController.java

```java
package com.study.collect.controller;

import com.study.common.util.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test/tree")
public class TreeTestController {

    @GetMapping("/nodes")
    public Result<Map<String, Object>> getTestTreeData() {
        Map<String, Object> treeData = new HashMap<>();
        treeData.put("projectId", "test-project");
        treeData.put("nodes", generateTestNodes());
        return Result.success(treeData);
    }

    private List<Map<String, Object>> generateTestNodes() {
        List<Map<String, Object>> nodes = new ArrayList<>();

        // 添加ROOT节点
        for (int i = 1; i <= 2; i++) {
            Map<String, Object> root = new HashMap<>();
            root.put("id", "/p/test-project/root" + i);
            root.put("name", "Root-" + i);
            root.put("type", "ROOT");
            root.put("children", generateVersions(root.get("id").toString()));
            nodes.add(root);
        }

        return nodes;
    }

    private List<Map<String, Object>> generateVersions(String parentId) {
        List<Map<String, Object>> versions = new ArrayList<>();

        // 基线版本
        Map<String, Object> baseline = new HashMap<>();
        baseline.put("id", parentId + "/baseline");
        baseline.put("name", "Baseline");
        baseline.put("type", "BASELINE_VERSION");
        baseline.put("children", generateCaseDirectory(baseline.get("id").toString(), true));
        versions.add(baseline);

        // 执行版本
        Map<String, Object> execute = new HashMap<>();
        execute.put("id", parentId + "/execute");
        execute.put("name", "Execute");
        execute.put("type", "EXECUTE_VERSION");
        execute.put("children", generateCaseDirectory(execute.get("id").toString(), false));
        versions.add(execute);

        return versions;
    }

    private List<Map<String, Object>> generateCaseDirectory(String parentId, boolean isBaseline) {
        List<Map<String, Object>> directories = new ArrayList<>();

        Map<String, Object> caseDir = new HashMap<>();
        caseDir.put("id", parentId + "/cases");
        caseDir.put("name", "Cases");
        caseDir.put("type", "CASE_DIRECTORY");

        if (isBaseline) {
            // 基线版本下添加普通目录和用例
            caseDir.put("children", generateNormalDirectories(caseDir.get("id").toString(), 3));
        } else {
            // 执行版本下添加场景节点
            caseDir.put("children", generateScenarios(caseDir.get("id").toString()));
        }

        directories.add(caseDir);
        return directories;
    }

    private List<Map<String, Object>> generateScenarios(String parentId) {
        List<Map<String, Object>> scenarios = new ArrayList<>();

        for (int i = 1; i <= 2; i++) {
            Map<String, Object> scenario = new HashMap<>();
            scenario.put("id", parentId + "/scenario" + i);
            scenario.put("name", "Scenario-" + i);
            scenario.put("type", "SCENARIO");
            scenario.put("children", generateNormalDirectories(scenario.get("id").toString(), 2));
            scenarios.add(scenario);
        }

        return scenarios;
    }

    private List<Map<String, Object>> generateNormalDirectories(String parentId, int depth) {
        List<Map<String, Object>> items = new ArrayList<>();
        if (depth <= 0) return items;

        // 添加目录
        Map<String, Object> dir = new HashMap<>();
        dir.put("id", parentId + "/dir" + depth);
        dir.put("name", "Directory-" + depth);
        dir.put("type", "NORMAL_DIRECTORY");
        dir.put("children", generateNormalDirectories(dir.get("id").toString(), depth - 1));
        items.add(dir);

        // 添加测试用例
        Map<String, Object> testCase = new HashMap<>();
        testCase.put("id", parentId + "/case" + depth);
        testCase.put("name", "TestCase-" + depth);
        testCase.put("type", "TEST_CASE");
        items.add(testCase);

        return items;
    }
}

```

## Collector.java

```java
package com.study.collect.core.collector;

import com.study.collect.entity.CollectTask;
import com.study.collect.entity.TaskResult;
import com.study.collect.enums.TaskStatus;

public interface Collector {
    TaskResult collect(CollectTask task);

    TaskStatus getTaskStatus(String taskId);

    void stopTask(String taskId);
}
```

## DistributedCollector.java

```java
package com.study.collect.core.collector;

import com.study.collect.annotation.CollectorFor;
import com.study.collect.core.executor.CollectExecutor;
import com.study.collect.entity.CollectTask;
import com.study.collect.entity.TaskResult;
import com.study.collect.enums.CollectorType;
import com.study.collect.enums.TaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


// 核心采集实现
@Component
@CollectorFor(CollectorType.DEFAULT)
public class DistributedCollector implements Collector {
    private static final Logger logger = LoggerFactory.getLogger(DistributedCollector.class);
    private static final String TASK_LOCK_PREFIX = "collect:task:lock:";
    private static final long LOCK_TIMEOUT = 10; // 锁超时时间（分钟）

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private CollectExecutor collectExecutor;

    @Override
    public TaskResult collect(CollectTask task) {
        String lockKey = TASK_LOCK_PREFIX + task.getId();

        // 尝试获取分布式锁
        Boolean acquired = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, "LOCKED", LOCK_TIMEOUT, TimeUnit.MINUTES);

        if (Boolean.TRUE.equals(acquired)) {
            try {
                // 执行采集任务
                return doCollect(task);
            } finally {
                // 释放锁
                redisTemplate.delete(lockKey);
            }
        } else {
            logger.warn("Task {} is already being executed on another instance", task.getId());
            return TaskResult.builder()
                    .taskId(task.getId())
                    .status(TaskStatus.FAILED)
                    .message("Task is already running")
                    .build();
        }
    }

    private TaskResult doCollect(CollectTask task) {
        try {
            logger.info("Starting collection for task: {}", task.getId());

            // 更新任务状态为运行中
            task.setStatus(TaskStatus.RUNNING);
            updateTaskStatus(task);

            // 执行采集
            TaskResult result = collectExecutor.execute(task);

            // 更新任务状态
            task.setStatus(result.isSuccess() ? TaskStatus.COMPLETED : TaskStatus.FAILED);
            updateTaskStatus(task);

            return result;

        } catch (Exception e) {
            logger.error("Error collecting data for task: " + task.getId(), e);

            // 更新任务状态为失败
            task.setStatus(TaskStatus.FAILED);
            updateTaskStatus(task);

            return TaskResult.builder()
                    .taskId(task.getId())
                    .status(TaskStatus.FAILED)
                    .message("Collection failed: " + e.getMessage())
                    .build();
        }
    }

    private void updateTaskStatus(CollectTask task) {
        // 更新状态到Redis
        String statusKey = "collect:task:status:" + task.getId();
        redisTemplate.opsForValue().set(statusKey, task.getStatus(), 24, TimeUnit.HOURS);

        logger.info("Updated task {} status to {}", task.getId(), task.getStatus());
    }

    @Override
    public TaskStatus getTaskStatus(String taskId) {
        String statusKey = "collect:task:status:" + taskId;
        Object status = redisTemplate.opsForValue().get(statusKey);
        return status != null ? (TaskStatus) status : TaskStatus.UNKNOWN;
    }

    @Override
    public void stopTask(String taskId) {
        String lockKey = TASK_LOCK_PREFIX + taskId;
        redisTemplate.delete(lockKey);

        // 更新状态为已停止
        String statusKey = "collect:task:status:" + taskId;
        redisTemplate.opsForValue().set(statusKey, TaskStatus.STOPPED);

        logger.info("Task {} has been stopped", taskId);
    }
}

```

## TreeCollector.java

```java
package com.study.collect.core.collector;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.collect.annotation.CollectorFor;
import com.study.collect.entity.CollectTask;
import com.study.collect.entity.TaskResult;
import com.study.collect.entity.TreeNode;
import com.study.collect.enums.CollectorType;
import com.study.collect.enums.TaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component("treeCollector")
@CollectorFor(CollectorType.TREE)  // 添加CollectorFor注解指定类型
public class TreeCollector implements Collector {

    private static final Logger logger = LoggerFactory.getLogger(TreeCollector.class);
    private static final String LOCK_PREFIX = "collect:task:lock:";
    private static final String STATUS_PREFIX = "collect:task:status:";
    private static final int LOCK_TIMEOUT = 10;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public TreeCollector() {
        logger.info("TreeCollector initialized with type: {}", CollectorType.TREE);
    }

    @Override
    public TaskResult collect(CollectTask task) {
        String lockKey = LOCK_PREFIX + task.getId();

        try {
            Boolean acquired = acquireLock(lockKey);
            if (!acquired) {
                return buildTaskResult(task.getId(), TaskStatus.FAILED, "Task is already running");
            }

            return doCollect(task);
        } catch (Exception e) {
            logger.error("Failed to collect tree for task: {}", task.getId(), e);
            return buildTaskResult(task.getId(), TaskStatus.FAILED, "Collection failed: " + e.getMessage());
        } finally {
            releaseLock(lockKey);
        }
    }

    private Boolean acquireLock(String lockKey) {
        return redisTemplate.opsForValue()
                .setIfAbsent(lockKey, "LOCKED", LOCK_TIMEOUT, TimeUnit.MINUTES);
    }

    private void releaseLock(String lockKey) {
        try {
            redisTemplate.delete(lockKey);
        } catch (Exception e) {
            logger.error("Failed to release lock: {}", lockKey, e);
        }
    }

    private TaskResult doCollect(CollectTask task) {
        try {
            logger.info("Starting tree data collection for task: {}", task.getId());

            ResponseEntity<String> response = fetchTreeData(task);
            if (!response.getStatusCode().is2xxSuccessful()) {
                String message = String.format("Failed to fetch tree data: %s", response.getStatusCode());
                logger.error(message);
                return buildTaskResult(task.getId(), TaskStatus.FAILED, message);
            }

            List<TreeNode> nodes = processTreeData(response.getBody(), task.getId());

            return buildTaskResult(task.getId(), TaskStatus.COMPLETED,
                    String.format("Successfully collected %d nodes", nodes.size()),
                    response.getStatusCode().value());

        } catch (Exception e) {
            throw new RuntimeException("Error collecting tree data", e);
        }
    }

    private ResponseEntity<String> fetchTreeData(CollectTask task) {
        HttpHeaders headers = createHeaders(task.getHeaders());
        HttpEntity<?> requestEntity = new HttpEntity<>(task.getRequestBody(), headers);

        return restTemplate.exchange(
                task.getUrl(),
                HttpMethod.valueOf(task.getMethod()),
                requestEntity,
                String.class
        );
    }

//    private List<TreeNode> processTreeData(String jsonData, String taskId) throws Exception {
//        Map<String, Object> treeData = objectMapper.readValue(jsonData, new TypeReference<Map<String, Object>>() {});
//        String projectId = (String) treeData.get("projectId");
//
//        @SuppressWarnings("unchecked")
//        List<Map<String, Object>> nodes = (List<Map<String, Object>>) treeData.get("nodes");
//
//        // Clear existing nodes for this task
//        clearExistingNodes(taskId);
//
//        // Process and save new nodes
//        List<TreeNode> allNodes = new ArrayList<>();
//        parseNodes(projectId, null, nodes, 0, new ArrayList<>(), allNodes, taskId);
//
//        return saveNodes(allNodes);
//    }

//    private void clearExistingNodes(String taskId) {
//        Query query = Query.query(Criteria.where("taskId").is(taskId));
//        mongoTemplate.remove(query, TreeNode.class);
//        logger.info("Cleared existing nodes for task {}", taskId);
//    }

    private List<TreeNode> saveNodes(List<TreeNode> nodes) {
        // 使用批量插入提升性能
        List<TreeNode> savedNodes = new ArrayList<>(mongoTemplate.insert(nodes, TreeNode.class));
        logger.info("Saved {} nodes to MongoDB", savedNodes.size());
        return savedNodes;
    }

//    private void parseNodes(String projectId, String parentId, List<Map<String, Object>> nodes,
//                            int level, List<String> parentPath, List<TreeNode> allNodes, String taskId) {
//        if (nodes == null) return;
//
//        for (Map<String, Object> nodeData : nodes) {
//            try {
//                TreeNode node = createNode(nodeData, projectId, parentId, level, parentPath, taskId);
//                allNodes.add(node);
//
//                // Process children recursively
//                @SuppressWarnings("unchecked")
//                List<Map<String, Object>> children = (List<Map<String, Object>>) nodeData.get("children");
//                if (children != null) {
//                    List<String> currentPath = new ArrayList<>(parentPath);
//                    currentPath.add(node.getId());
//                    parseNodes(projectId, node.getId(), children, level + 1, currentPath, allNodes, taskId);
//                }
//            } catch (Exception e) {
//                logger.error("Error processing node: {}", nodeData, e);
//            }
//        }
//    }

    private TreeNode createNode(Map<String, Object> nodeData, String projectId, String parentId,
                                int level, List<String> parentPath, String taskId) {
        TreeNode node = new TreeNode();
        node.setId((String) nodeData.get("id"));
        node.setProjectId(projectId);
        node.setTaskId(taskId);
        node.setParentId(parentId);
        node.setName((String) nodeData.get("name"));
        node.setType(TreeNode.NodeType.valueOf((String) nodeData.get("type")));
        node.setLevel(level);

        List<String> currentPath = new ArrayList<>(parentPath);
        currentPath.add(node.getId());
        node.setPath(currentPath);

        Date now = new Date();
        node.setCreateTime(now);
        node.setUpdateTime(now);

        return node;
    }

    private HttpHeaders createHeaders(Map<String, String> headers) {
        HttpHeaders httpHeaders = new HttpHeaders();
        if (headers != null) {
            headers.forEach(httpHeaders::add);
        }
        return httpHeaders;
    }

    private TaskResult buildTaskResult(String taskId, TaskStatus status, String message) {
        return buildTaskResult(taskId, status, message, null);
    }

    private TaskResult buildTaskResult(String taskId, TaskStatus status, String message, Integer statusCode) {
        return TaskResult.builder()
                .taskId(taskId)
                .status(status)
                .message(message)
                .statusCode(statusCode)
                .build();
    }

    @Override
    public TaskStatus getTaskStatus(String taskId) {
        Object status = redisTemplate.opsForValue().get(STATUS_PREFIX + taskId);
        return status != null ? (TaskStatus) status : TaskStatus.UNKNOWN;
    }

    @Override
    public void stopTask(String taskId) {
        redisTemplate.delete(LOCK_PREFIX + taskId);
        redisTemplate.opsForValue().set(STATUS_PREFIX + taskId, TaskStatus.STOPPED);
        logger.info("Task {} has been stopped", taskId);
    }

//    private List<TreeNode> processTreeData(String jsonData, String taskId) throws Exception {
//        try {
//            // 先记录原始数据便于调试
//            logger.debug("Processing tree data for task {}: {}", taskId, jsonData);
//
//            // 使用TypeReference确保正确的类型转换
//            Map<String, Object> treeData = objectMapper.readValue(jsonData,
//                    new TypeReference<Map<String, Object>>() {});
//
//            if (treeData == null) {
//                throw new IllegalArgumentException("Tree data is null");
//            }
//
//            String projectId = (String) treeData.get("projectId");
//            if (projectId == null) {
//                throw new IllegalArgumentException("Project ID is missing in tree data");
//            }
//
//            @SuppressWarnings("unchecked")
//            List<Map<String, Object>> nodes = (List<Map<String, Object>>) treeData.get("nodes");
//            if (nodes == null || nodes.isEmpty()) {
//                logger.warn("No nodes found in tree data for task {}", taskId);
//                return Collections.emptyList();
//            }
//
//            // Clear existing nodes
//            clearExistingNodes(taskId);
//
//            // Process nodes
//            List<TreeNode> allNodes = new ArrayList<>();
//            parseNodes(projectId, null, nodes, 0, new ArrayList<>(), allNodes, taskId);
//
//            if (allNodes.isEmpty()) {
//                logger.warn("No nodes were parsed from the tree data for task {}", taskId);
//                return Collections.emptyList();
//            }
//
//            // Save nodes in batches
//            List<TreeNode> savedNodes = saveNodesInBatches(allNodes, 100);
//            logger.info("Successfully saved {} nodes for task {}", savedNodes.size(), taskId);
//
//            return savedNodes;
//
//        } catch (Exception e) {
//            logger.error("Error processing tree data for task {}: {}", taskId, e.getMessage());
//            throw e;
//        }
//    }

//    private List<TreeNode> processTreeData(String jsonData, String taskId) throws Exception {
//        try {
//            logger.debug("Processing tree data for task {}: {}", taskId, jsonData);
//
//            // 先解析外层响应结构
//            Map<String, Object> response = objectMapper.readValue(jsonData,
//                    new TypeReference<Map<String, Object>>() {});
//
//            if (response == null || !response.containsKey("data")) {
//                throw new IllegalArgumentException("Invalid response format");
//            }
//
//            // 获取data对象
//            @SuppressWarnings("unchecked")
//            Map<String, Object> data = (Map<String, Object>) response.get("data");
//            if (data == null) {
//                throw new IllegalArgumentException("Response data is null");
//            }
//
//            // 从data中获取projectId
//            String projectId = (String) data.get("projectId");
//            if (projectId == null || projectId.isEmpty()) {
//                throw new IllegalArgumentException("Project ID is missing in data");
//            }
//
//            // 从data中获取nodes数组
//            @SuppressWarnings("unchecked")
//            List<Map<String, Object>> nodes = (List<Map<String, Object>>) data.get("nodes");
//            if (nodes == null || nodes.isEmpty()) {
//                logger.warn("No nodes found in tree data for task {}", taskId);
//                return Collections.emptyList();
//            }
//
//            // 清理旧数据
//            clearExistingNodes(taskId);
//
//            // 解析节点
//            List<TreeNode> allNodes = new ArrayList<>();
//            parseNodes(projectId, null, nodes, 0, new ArrayList<>(), allNodes, taskId);
//
//            if (allNodes.isEmpty()) {
//                logger.warn("No nodes were parsed from the tree data for task {}", taskId);
//                return Collections.emptyList();
//            }
//
//            // 批量保存
//            List<TreeNode> savedNodes = saveNodesInBatches(allNodes, 100);
//            logger.info("Successfully saved {} nodes for task {}", savedNodes.size(), taskId);
//
//            return savedNodes;
//
//        } catch (Exception e) {
//            logger.error("Error processing tree data for task {}: {}", taskId, e.getMessage());
//            throw e;
//        }
//    }

    private void clearExistingNodes(String taskId) {
        Query query = Query.query(Criteria.where("taskId").is(taskId));
        mongoTemplate.remove(query, TreeNode.class);
        logger.info("Cleared existing nodes for task {}", taskId);
    }
//
//    private List<TreeNode> saveNodesInBatches(List<TreeNode> nodes, int batchSize) {
//        List<TreeNode> savedNodes = new ArrayList<>();
//
//        for (int i = 0; i < nodes.size(); i += batchSize) {
//            int end = Math.min(nodes.size(), i + batchSize);
//            List<TreeNode> batch = nodes.subList(i, end);
//            try {
//                savedNodes.addAll(mongoTemplate.insert(batch, TreeNode.class));
//                logger.debug("Saved batch of {} nodes, total saved: {}",
//                        batch.size(), savedNodes.size());
//            } catch (Exception e) {
//                logger.error("Error saving batch of nodes: {}", e.getMessage());
//                throw e;
//            }
//        }
//        return savedNodes;
//    }

    private void parseNodes(String projectId, String parentId, List<Map<String, Object>> nodes,
                            int level, List<String> parentPath, List<TreeNode> allNodes, String taskId) {
        if (nodes == null) return;

        for (Map<String, Object> nodeData : nodes) {
            try {
                // 验证必要的字段
                String id = (String) nodeData.get("id");
                String name = (String) nodeData.get("name");
                String type = (String) nodeData.get("type");

                if (id == null || name == null || type == null) {
                    logger.error("Missing required fields in node data: {}", nodeData);
                    continue;
                }

                // 创建节点
                TreeNode node = new TreeNode();
                node.setId(id);
                node.setProjectId(projectId);
                node.setTaskId(taskId);
                node.setParentId(parentId);
                node.setName(name);
                try {
                    node.setType(TreeNode.NodeType.valueOf(type));
                } catch (IllegalArgumentException e) {
                    logger.error("Invalid node type: {} for node: {}", type, id);
                    continue;
                }
                node.setLevel(level);

                // 设置路径
                List<String> currentPath = new ArrayList<>(parentPath);
                currentPath.add(node.getId());
                node.setPath(currentPath);

                // 设置时间戳
                Date now = new Date();
                node.setCreateTime(now);
                node.setUpdateTime(now);

                // 添加到结果列表
                allNodes.add(node);
                logger.debug("Successfully parsed node: {}, type: {}, level: {}",
                        id, type, level);

                // 递归处理子节点
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> children = (List<Map<String, Object>>) nodeData.get("children");
                if (children != null && !children.isEmpty()) {
                    parseNodes(projectId, node.getId(), children, level + 1, currentPath, allNodes, taskId);
                }

            } catch (Exception e) {
                logger.error("Error parsing node data: {}", nodeData, e);
            }
        }
    }

//    private List<TreeNode> saveNodesInBatches(List<TreeNode> nodes, int batchSize) {
//        List<TreeNode> savedNodes = new ArrayList<>();
//        for (int i = 0; i < nodes.size(); i += batchSize) {
//            int end = Math.min(nodes.size(), i + batchSize);
//            List<TreeNode> batch = nodes.subList(i, end);
//            try {
//                savedNodes.addAll(mongoTemplate.insert(batch, TreeNode.class));
//                logger.debug("Saved batch of {} nodes", batch.size());
//            } catch (Exception e) {
//                logger.error("Error saving batch of nodes: {}", e.getMessage());
//                throw e;
//            }
//        }
//        return savedNodes;
//    }

    private List<TreeNode> saveNodesInBatches(List<TreeNode> nodes, int batchSize) {
        List<TreeNode> savedNodes = new ArrayList<>();
        for (int i = 0; i < nodes.size(); i += batchSize) {
            int end = Math.min(nodes.size(), i + batchSize);
            List<TreeNode> batch = nodes.subList(i, end);
            try {
                // 对每个节点进行保存或更新
                batch.forEach(node -> {
                    try {
                        Query query = Query.query(Criteria.where("_id").is(node.getId()));
                        TreeNode existingNode = mongoTemplate.findOne(query, TreeNode.class);

                        if (existingNode != null) {
                            // 如果节点已存在,执行更新操作
                            // 创建更新对象,不包含_id字段
                            Update update = new Update()
                                    .set("projectId", node.getProjectId())
                                    .set("parentId", node.getParentId())
                                    .set("name", node.getName())
                                    .set("type", node.getType())
                                    .set("level", node.getLevel())
                                    .set("path", node.getPath())
                                    .set("taskId", node.getTaskId())
                                    .set("updateTime", new Date());

                            mongoTemplate.updateFirst(query, update, TreeNode.class);
                            savedNodes.add(node);
                            logger.debug("Updated existing node: {}", node.getId());
                        } else {
                            // 如果节点不存在,执行插入操作
                            TreeNode saved = mongoTemplate.insert(node);
                            savedNodes.add(saved);
                            logger.debug("Inserted new node: {}", node.getId());
                        }
                    } catch (Exception e) {
                        logger.error("Error saving/updating node {}: {}", node.getId(), e.getMessage());
                        throw e;
                    }
                });
                logger.debug("Processed batch of {} nodes", batch.size());
            } catch (Exception e) {
                logger.error("Error processing batch of nodes: {}", e.getMessage());
                throw e;
            }
        }
        return savedNodes;
    }

    // 清理已存在节点的方法也需要修改,仅删除不再使用的节点
    private void clearExistingNodes(String taskId, List<String> newNodeIds) {
        try {
            // 获取当前任务的所有现有节点ID
            Query findQuery = Query.query(Criteria.where("taskId").is(taskId));
            findQuery.fields().include("_id");
            List<TreeNode> existingNodes = mongoTemplate.find(findQuery, TreeNode.class);
            List<String> existingIds = existingNodes.stream()
                    .map(TreeNode::getId)
                    .collect(Collectors.toList());

            // 找出需要删除的节点(在现有节点中但不在新节点中的)
            existingIds.removeAll(newNodeIds);
            if (!existingIds.isEmpty()) {
                Query deleteQuery = Query.query(Criteria.where("_id").in(existingIds));
                mongoTemplate.remove(deleteQuery, TreeNode.class);
                logger.info("Removed {} obsolete nodes for task {}", existingIds.size(), taskId);
            }
        } catch (Exception e) {
            logger.error("Failed to clear existing nodes for task {}: {}", taskId, e.getMessage());
            throw e;
        }
    }

    // processTreeData方法也需要相应修改
    private List<TreeNode> processTreeData(String jsonData, String taskId) throws Exception {
        try {
            logger.debug("Processing tree data for task {}: {}", taskId, jsonData);

            Map<String, Object> response = objectMapper.readValue(jsonData,
                    new TypeReference<Map<String, Object>>() {
                    });

            if (response == null || !response.containsKey("data")) {
                throw new IllegalArgumentException("Invalid response format");
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) response.get("data");
            String projectId = (String) data.get("projectId");
            if (projectId == null || projectId.isEmpty()) {
                throw new IllegalArgumentException("Project ID is missing in data");
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> nodes = (List<Map<String, Object>>) data.get("nodes");
            if (nodes == null || nodes.isEmpty()) {
                logger.warn("No nodes found in tree data for task {}", taskId);
                return Collections.emptyList();
            }

            // 收集所有新节点ID
            List<TreeNode> allNodes = new ArrayList<>();
            List<String> newNodeIds = new ArrayList<>();
            parseNodes(projectId, null, nodes, 0, new ArrayList<>(), allNodes, taskId, newNodeIds);

            // 清理不再使用的节点
            clearExistingNodes(taskId, newNodeIds);

            // 批量保存/更新节点
            if (!allNodes.isEmpty()) {
                List<TreeNode> savedNodes = saveNodesInBatches(allNodes, 100);
                logger.info("Successfully saved/updated {} nodes for task {}", savedNodes.size(), taskId);
                return savedNodes;
            }

            return Collections.emptyList();
        } catch (Exception e) {
            logger.error("Error processing tree data for task {}: {}", taskId, e.getMessage());
            throw e;
        }
    }

    // parseNodes方法需要增加收集节点ID的功能
    private void parseNodes(String projectId, String parentId, List<Map<String, Object>> nodes,
                            int level, List<String> parentPath, List<TreeNode> allNodes,
                            String taskId, List<String> nodeIds) {
        if (nodes == null) return;

        for (Map<String, Object> nodeData : nodes) {
            try {
                String id = (String) nodeData.get("id");
                String name = (String) nodeData.get("name");
                String type = (String) nodeData.get("type");

                if (id == null || name == null || type == null) {
                    logger.error("Missing required fields in node data: {}", nodeData);
                    continue;
                }

                // 创建节点并添加到列表
                TreeNode node = createNode(nodeData, projectId, parentId, level, parentPath, taskId);
                allNodes.add(node);
                nodeIds.add(node.getId());

                // 处理子节点
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> children = (List<Map<String, Object>>) nodeData.get("children");
                if (children != null && !children.isEmpty()) {
                    List<String> currentPath = new ArrayList<>(parentPath);
                    currentPath.add(node.getId());
                    parseNodes(projectId, node.getId(), children, level + 1, currentPath,
                            allNodes, taskId, nodeIds);
                }
            } catch (Exception e) {
                logger.error("Error parsing node data: {}", nodeData, e);
            }
        }
    }
}
```

## CollectExecutor.java

```java
package com.study.collect.core.executor;

import com.study.collect.entity.CollectTask;
import com.study.collect.entity.TaskResult;
import com.study.collect.enums.TaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.TimeUnit;

// 采集执行器
@Component
public class CollectExecutor {
    private static final Logger logger = LoggerFactory.getLogger(CollectExecutor.class);

    @Autowired
    private RestTemplate restTemplate;

    public TaskResult execute(CollectTask task) {
        try {
            logger.info("Executing collection task: {}", task.getId());

            // 构建请求头
            HttpHeaders headers = buildHeaders(task.getHeaders());
            HttpEntity<?> requestEntity = new HttpEntity<>(task.getRequestBody(), headers);

            // 发送请求
            ResponseEntity<String> response = restTemplate.exchange(
                    task.getUrl(),
                    HttpMethod.valueOf(task.getMethod()),
                    requestEntity,
                    String.class
            );

            // 处理响应
            return handleResponse(task, response);

        } catch (Exception e) {
            logger.error("Error executing task: " + task.getId(), e);
            return TaskResult.builder()
                    .taskId(task.getId())
                    .status(TaskStatus.FAILED)
                    .message("Execution failed: " + e.getMessage())
                    .build();
        }
    }

    private HttpHeaders buildHeaders(Map<String, String> headerMap) {
        HttpHeaders headers = new HttpHeaders();
        if (headerMap != null) {
            headerMap.forEach(headers::add);
        }
        return headers;
    }

    private TaskResult handleResponse(CollectTask task, ResponseEntity<String> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return TaskResult.builder()
                    .taskId(task.getId())
                    .status(TaskStatus.COMPLETED)
                    .statusCode(response.getStatusCode().value())
                    .responseBody(response.getBody())
                    .build();
        } else {
            return TaskResult.builder()
                    .taskId(task.getId())
                    .status(TaskStatus.FAILED)
                    .statusCode(response.getStatusCode().value())
                    .message("Request failed with status: " + response.getStatusCode())
                    .build();
        }
    }

    public boolean retry(CollectTask task, int maxRetries, long retryInterval) {
        int retryCount = 0;
        while (retryCount < maxRetries) {
            try {
                TimeUnit.MILLISECONDS.sleep(retryInterval);
                TaskResult result = execute(task);
                if (result.isSuccess()) {
                    return true;
                }
                retryCount++;
                logger.warn("Retry {} failed for task {}", retryCount, task.getId());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return false;
    }
}
```

## CollectorStrategy.java

```java
package com.study.collect.core.strategy;

import com.study.collect.annotation.CollectorFor;
import com.study.collect.core.collector.Collector;
import com.study.collect.enums.CollectorType;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CollectorStrategy {
    private static final Logger logger = LoggerFactory.getLogger(CollectorStrategy.class);
    private final Map<CollectorType, Collector> collectorMap;

    public CollectorStrategy(List<Collector> collectors) {
        logger.info("Initializing CollectorStrategy with collectors: {}",
                collectors.stream().map(c -> c.getClass().getSimpleName()).collect(Collectors.toList()));

        collectorMap = collectors.stream()
                .filter(collector -> collector.getClass().isAnnotationPresent(CollectorFor.class))
                .collect(Collectors.toMap(
                        collector -> collector.getClass().getAnnotation(CollectorFor.class).value(),
                        collector -> collector
                ));

        logger.info("Initialized collector mappings: {}",
                collectorMap.entrySet().stream()
                        .map(e -> e.getKey() + " -> " + e.getValue().getClass().getSimpleName())
                        .collect(Collectors.toList()));
    }

    @PostConstruct
    public void init() {
        logger.info("=== Collector Strategy Initialization ===");
        logger.info("Available collector types: {}", CollectorType.values());
        logger.info("Registered collectors: {}",
                collectorMap.entrySet().stream()
                        .map(e -> e.getKey() + " -> " + e.getValue().getClass().getSimpleName())
                        .collect(Collectors.toList()));
        logger.info("=======================================");
    }

    public Collector getCollector(CollectorType type) {
        Collector collector = collectorMap.get(type);
        logger.debug("Getting collector for type {}, found: {}",
                type, collector != null ? collector.getClass().getSimpleName() : "null");
        return collector;
    }
}
```

## AlertLog.java

```java
package com.study.collect.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

// 添加告警日志实体类
@Data
@Document(collection = "alert_logs")
public class AlertLog {
    @Id
    private String id;
    private String alertId;
    private String operation;
    private Date operateTime;
}
```

## AlertMessage.java

```java
package com.study.collect.entity;

import com.study.collect.enums.AlertLevel;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "alert_messages")
public class AlertMessage {
    @Id
    private String id;
    private AlertLevel level;
    private String source;
    private String message;
    private boolean acknowledged;
    private Date createTime;
    private Date acknowledgeTime;
}
```

## AlertRecord.java

```java
package com.study.collect.entity;

import com.study.collect.enums.AlertLevel;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "alert_records")
public class AlertRecord {
    @Id
    private String id;
    private String ruleId;
    private AlertLevel level;
    private String message;
    private boolean acknowledged;
    private Date createTime;
    private Date acknowledgeTime;
}
```

## AlertRule.java

```java
package com.study.collect.entity;

import com.study.collect.enums.AlertLevel;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "alert_rules")
public class AlertRule {
    @Id
    private String id;
    private String name;
    private String metric;
    private String condition;
    private Double threshold;
    private AlertLevel level;
    private boolean enabled;
    private Date createTime;
    private Date updateTime;
}
```

## CollectData.java

```java
package com.study.collect.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "collect_data")
public class CollectData {
    @Id
    private String id;

    private Long mysqlId; // MySQL中的ID
    private String deviceId;
    private String deviceName;
    private Double temperature;
    private Double humidity;
    private String location;
    private Date collectTime;
    private Date createTime;
}
```

## CollectResult.java

```java
package com.study.collect.entity;

import com.study.collect.enums.TaskStatus;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "collect_results")
public class CollectResult {
    @Id
    private String id;
    private String taskId;
    private TaskStatus status;
    private Integer statusCode;
    private String responseBody;
    private String errorMessage;
    private Date createTime;
    private Integer retryCount;
}
```

## CollectTask.java

```java
package com.study.collect.entity;

import com.study.collect.enums.CollectorType;
import com.study.collect.enums.TaskStatus;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Map;


@Data
@Builder
@Document(collection = "collect_tasks")
public class CollectTask {
    @Id
    private String id;
    private String name;
    private String url;
    private String method;
    private Map<String, String> headers;
    private String requestBody;
    private TaskStatus status;
    private Integer retryCount;
    private Integer maxRetries;
    private Long retryInterval;
    private Date createTime;
    private Date updateTime;
    private CollectorType collectorType = CollectorType.DEFAULT; // 默认类型
    private String lastError;

}



```

## MongoTestEntity.java

```java
package com.study.collect.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "test_mongo")
public class MongoTestEntity {
    @Id
    private String id;
    private String name;
    private String description;
    private Date createTime;
    private Date updateTime;
}

```

## SystemMetrics.java

```java
package com.study.collect.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "system_metrics")
public class SystemMetrics {
    @Id
    private String id;
    private double cpuUsage;
    private double memoryUsage;
    private double diskUsage;
    private long totalTasks;
    private long runningTasks;
    private long failedTasks;
    private Date createTime;
}

```

## TaskLog.java

```java
package com.study.collect.entity;

import com.study.collect.enums.TaskStatus;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "task_logs")
public class TaskLog {
    @Id
    private String id;
    private String taskId;
    private TaskStatus status;
    private String message;
    private Integer responseCode;
    private Long executionTime;
    private Date createTime;
    private String serverIp;
}
```

## TaskResult.java

```java
package com.study.collect.entity;

import com.study.collect.enums.TaskStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaskResult {
    private String taskId;
    private TaskStatus status;
    private Integer statusCode;
    private String responseBody;
    private String message;

    public boolean isSuccess() {
        return TaskStatus.COMPLETED.equals(status);
    }
}
```

## TaskStatusStatistics.java

```java
package com.study.collect.entity;

import lombok.Data;

import java.util.Date;

@Data
public class TaskStatusStatistics {
    private long totalTasks;
    private long runningTasks;
    private long completedTasks;
    private long failedTasks;
    private long stoppedTasks;
    private Date statisticsTime;
}

```

## TestEntity.java

```java
package com.study.collect.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TestEntity implements Serializable {
    private Long id;
    private String name;
    private String description;
    private Date createTime;
    private Date updateTime;
}


```

## TreeCollectRequest.java

```java
package com.study.collect.entity;

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

## TreeCollectTask.java

```java
package com.study.collect.entity;

import com.study.collect.enums.TaskStatus;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@Document(collection = "tree_collect_tasks")
public class TreeCollectTask {
    @Id
    private String id;
    private String projectId;
    private TaskStatus status;
    private int totalCount;
    private int currentCount;
    private List<String> errors;
    private Date startTime;
    private Date endTime;
    private Date createTime;
}

```

## TreeNode.java

```java
package com.study.collect.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@Document(collection = "tree_nodes")
public class TreeNode {
    @Id
    private String id;           // 节点路径作为ID
    private String projectId;    // 项目ID
    private String parentId;     // 父节点ID
    private String name;         // 节点名称
    private NodeType type;       // 节点类型
    private Integer level;       // 节点层级
    private List<String> path;   // 完整路径
    private Date createTime;
    private Date updateTime;
    private List<TreeNode> children;
    private String taskId;


    public enum NodeType {
        PROJECT,
        ROOT,
        BASELINE_VERSION,
        EXECUTE_VERSION,
        CASE_DIRECTORY,
        SCENARIO,
        NORMAL_DIRECTORY,
        TEST_CASE
    }
}

```

## AlertLevel.java

```java
package com.study.collect.enums;


public enum AlertLevel {
    INFO,
    WARNING,
    ERROR,
    CRITICAL
}
```

## CollectorType.java

```java
package com.study.collect.enums;

public enum CollectorType {
    TREE,           // 树状结构采集
    DISTRIBUTED,    // 分布式采集
    DEFAULT        // 默认采集器
}
```

## RetryStrategy.java

```java
package com.study.collect.enums;

public enum RetryStrategy {
    FIXED_INTERVAL,    // 固定时间间隔重试
    EXPONENTIAL_BACKOFF, // 指数退避重试
    LINEAR_BACKOFF      // 线性退避重试
}
```

## TaskStatus.java

```java
package com.study.collect.enums;

public enum TaskStatus {
    CREATED,
    RUNNING,
    COMPLETED,
    FAILED,
    STOPPED,
    UNKNOWN
}



```

## MessageHandler.java

```java
package com.study.collect.handler;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MessageHandler implements ChannelAwareMessageListener {

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        try {
            // 获取消息ID
            long deliveryTag = message.getMessageProperties().getDeliveryTag();

            // 获取消息内容
            String msg = new String(message.getBody());
            log.info("Received message: {}", msg);

            // 处理消息
            processMessage(msg);

            // 手动确认消息
            channel.basicAck(deliveryTag, false);

        } catch (Exception e) {
            log.error("Error processing message", e);
            // 消息处理失败，拒绝消息并重新入队
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }
    }

    private void processMessage(String message) {
        // 实现具体的消息处理逻辑
    }
}
```

## RabbitMessageListener.java

```java
package com.study.collect.listener;


import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RabbitMessageListener implements ChannelAwareMessageListener {

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        try {
            long deliveryTag = message.getMessageProperties().getDeliveryTag();
            String msg = new String(message.getBody());
            log.info("Received message: {}", msg);

            // TODO: 处理消息的业务逻辑

            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("处理消息时发生错误", e);
            // 消息处理失败时，将消息重新放回队列
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }
    }
}

```

## CollectDataMapper.java

```java
package com.study.collect.mapper;

import com.study.collect.entity.CollectData;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

@Mapper
public interface CollectDataMapper {

    @Insert("""
                INSERT INTO collect_data (
                    device_id, device_name, temperature, humidity,
                    location, collect_time, create_time
                ) VALUES (
                    #{deviceId}, #{deviceName}, #{temperature}, #{humidity},
                    #{location}, #{collectTime}, #{createTime}
                )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "mysqlId")
    int insert(CollectData data);

    @Select("""
                SELECT * FROM collect_data 
                WHERE device_id = #{deviceId}
                AND collect_time BETWEEN #{startTime} AND #{endTime}
            """)
    List<CollectData> findByDeviceAndTimeRange(
            @Param("deviceId") String deviceId,
            @Param("startTime") Date startTime,
            @Param("endTime") Date endTime
    );
}
```

## TaskLogMapper.java

```java
package com.study.collect.mapper;

import com.study.collect.entity.TaskLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

@Mapper
public interface TaskLogMapper {
    @Insert("""
                INSERT INTO task_logs (
                    task_id, status, message, response_code,
                    execution_time, create_time
                ) VALUES (
                    #{taskId}, #{status}, #{message}, #{responseCode},
                    #{executionTime}, #{createTime}
                )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(TaskLog log);

    @Select("SELECT * FROM task_logs WHERE task_id = #{taskId} ORDER BY create_time DESC")
    List<TaskLog> findByTaskId(String taskId);

    @Select("""
                SELECT * FROM task_logs 
                WHERE create_time BETWEEN #{startTime} AND #{endTime}
                ORDER BY create_time DESC
            """)
    List<TaskLog> findByTimeRange(Date startTime, Date endTime);
}
```

## TestMySQLMapper.java

```java
package com.study.collect.mapper;

import com.study.collect.entity.TestEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TestMySQLMapper {

    @Insert("INSERT INTO test_table (name, description, create_time, update_time) " +
            "VALUES (#{name}, #{description}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(TestEntity entity);

    @Select("SELECT * FROM test_table WHERE id = #{id}")
    TestEntity findById(Long id);

    @Select("SELECT * FROM test_table")
    List<TestEntity> findAll();

    @Update("UPDATE test_table SET name = #{name}, description = #{description}, " +
            "update_time = #{updateTime} WHERE id = #{id}")
    int update(TestEntity entity);

    @Delete("DELETE FROM test_table WHERE id = #{id}")
    int deleteById(Long id);
}
```

## TestDocument.java

```java
package com.study.collect.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "test_collection")
public class TestDocument {
    @Id
    private String id;
    private String name;
    private String description;
    private Date createTime;
    private Date updateTime;
}

```

## MonitorService.java

```java
package com.study.collect.monitor;

import com.study.collect.entity.*;
import com.study.collect.enums.TaskStatus;
import com.study.collect.repository.TaskRepository;
import com.study.collect.utils.SystemResourceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class MonitorService {
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public SystemMetrics collectSystemMetrics() {
        SystemMetrics metrics = new SystemMetrics();

        // 收集系统资源使用情况
        metrics.setCpuUsage(SystemResourceUtil.getCpuUsage());
        metrics.setMemoryUsage(SystemResourceUtil.getMemoryUsage());
        metrics.setDiskUsage(SystemResourceUtil.getDiskUsage());

        // 收集任务统计信息
        metrics.setTotalTasks(taskRepository.count());
        metrics.setRunningTasks(taskRepository.countByStatus(TaskStatus.RUNNING));
        metrics.setFailedTasks(taskRepository.countByStatus(TaskStatus.FAILED));

        metrics.setCreateTime(new Date());

        // 保存指标数据
        return mongoTemplate.save(metrics);
    }

    public TaskStatusStatistics getTaskStatusStatistics() {
        TaskStatusStatistics stats = new TaskStatusStatistics();

        stats.setTotalTasks(taskRepository.count());
        stats.setRunningTasks(taskRepository.countByStatus(TaskStatus.RUNNING));
        stats.setCompletedTasks(taskRepository.countByStatus(TaskStatus.COMPLETED));
        stats.setFailedTasks(taskRepository.countByStatus(TaskStatus.FAILED));
        stats.setStoppedTasks(taskRepository.countByStatus(TaskStatus.STOPPED));
        stats.setStatisticsTime(new Date());

        return stats;
    }

    public List<AlertMessage> getActiveAlerts() {
        Query query = new Query();
        query.addCriteria(Criteria.where("acknowledged").is(false));
        query.with(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createTime"));

        return mongoTemplate.find(query, AlertMessage.class);
    }

    // 系统资源监控
    private double getDiskUsage() {
        return SystemResourceUtil.getDiskUsage();
    }

    private double getCpuUsage() {
        return SystemResourceUtil.getCpuUsage();
    }

    private double getMemoryUsage() {
        return SystemResourceUtil.getMemoryUsage();
    }

    public void acknowledgeAlert(String alertId) {
        Query query = new Query(Criteria.where("_id").is(alertId));
        Update update = new Update()
                .set("acknowledged", true)
                .set("acknowledgeTime", new Date());

        // 更新告警状态
        mongoTemplate.updateFirst(query, update, AlertMessage.class);

        // 记录操作日志
        AlertLog alertLog = new AlertLog();
        alertLog.setAlertId(alertId);
        alertLog.setOperation("ACKNOWLEDGE");
        alertLog.setOperateTime(new Date());
        mongoTemplate.save(alertLog);
    }

    // 告警规则评估
    public class AlertRuleImpl extends AlertRule {

        public boolean evaluate(SystemMetrics metrics) {
            switch (getMetric()) {
                case "cpu":
                    return evaluateThreshold(metrics.getCpuUsage());
                case "memory":
                    return evaluateThreshold(metrics.getMemoryUsage());
                case "disk":
                    return evaluateThreshold(metrics.getDiskUsage());
                case "failedTasks":
                    return evaluateThreshold((double) metrics.getFailedTasks());
                default:
                    return false;
            }
        }

        private boolean evaluateThreshold(double value) {
            switch (getCondition()) {
                case ">":
                    return value > getThreshold();
                case ">=":
                    return value >= getThreshold();
                case "<":
                    return value < getThreshold();
                case "<=":
                    return value <= getThreshold();
                case "==":
                    return value == getThreshold();
                default:
                    return false;
            }
        }

        public String generateAlertMessage(SystemMetrics metrics) {
            return String.format("Alert: %s - %s %s %s (current value: %s)",
                    getName(),
                    getMetric(),
                    getCondition(),
                    getThreshold(),
                    getMetricValue(metrics));
        }

        private double getMetricValue(SystemMetrics metrics) {
            switch (getMetric()) {
                case "cpu":
                    return metrics.getCpuUsage();
                case "memory":
                    return metrics.getMemoryUsage();
                case "disk":
                    return metrics.getDiskUsage();
                case "failedTasks":
                    return metrics.getFailedTasks();
                default:
                    return 0.0;
            }
        }
    }
}
```

## CollectDataRepository.java

```java
package com.study.collect.repository;

import com.study.collect.entity.CollectData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface CollectDataRepository extends MongoRepository<CollectData, String> {

    List<CollectData> findByDeviceIdAndCollectTimeBetween(
            String deviceId, Date startTime, Date endTime);

    @Query("{'temperature': {$gte: ?0}}")
    List<CollectData> findByTemperatureGreaterThan(Double temperature);
}
```

## CollectResultRepository.java

```java
package com.study.collect.repository;

import com.study.collect.entity.CollectResult;
import com.study.collect.enums.TaskStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollectResultRepository extends MongoRepository<CollectResult, String> {
    List<CollectResult> findByTaskId(String taskId);

    List<CollectResult> findByTaskIdAndStatus(String taskId, TaskStatus status);

    void deleteByTaskId(String taskId);
}
```

## TaskRepository.java

```java
package com.study.collect.repository;

import com.study.collect.entity.CollectTask;
import com.study.collect.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TaskRepository extends MongoRepository<CollectTask, String> {
    List<CollectTask> findByStatus(TaskStatus status);

    List<CollectTask> findByStatusAndUpdateTimeBefore(TaskStatus status, Date updateTime);

    Page<CollectTask> findByStatusIn(List<TaskStatus> statuses, Pageable pageable);

    // 添加计数方法
    long countByStatus(TaskStatus status);

    @Query(value = "{ 'status' : ?0 }", count = true)
    long getTaskCountByStatus(TaskStatus status);

    // 添加时间范围查询方法
    @Query("{'status' : ?0, 'createTime' : { $gte: ?1, $lte: ?2 }}")
    List<CollectTask> findByStatusAndTimeRange(TaskStatus status, Date startTime, Date endTime);
}
```

## TestMongoRepository.java

```java
package com.study.collect.repository;

import com.study.collect.entity.MongoTestEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestMongoRepository extends MongoRepository<MongoTestEntity, String> {
    // 基本的CRUD方法由MongoRepository提供
}
```

## CollectService.java

```java
package com.study.collect.service;

import com.study.collect.entity.CollectData;
import com.study.collect.mapper.CollectDataMapper;
import com.study.collect.repository.CollectDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class CollectService {
    private static final Logger logger = LoggerFactory.getLogger(CollectService.class);

    @Autowired
    private CollectDataMapper mysqlMapper;

    @Autowired
    private CollectDataRepository mongoRepository;

    @Transactional
    public void saveData(CollectData data) {
        try {
            // 设置时间
            Date now = new Date();
            if (data.getCollectTime() == null) {
                data.setCollectTime(now);
            }
            data.setCreateTime(now);

            // 保存到MySQL
            mysqlMapper.insert(data);
            logger.info("Data saved to MySQL with id: {}", data.getMysqlId());

            // 保存到MongoDB
            mongoRepository.save(data);
            logger.info("Data saved to MongoDB with id: {}", data.getId());

        } catch (Exception e) {
            logger.error("Error saving data", e);
            throw e;
        }
    }

    public List<CollectData> queryData(String deviceId, Date startTime, Date endTime) {
        // 从MySQL查询
        List<CollectData> mysqlData = mysqlMapper.findByDeviceAndTimeRange(
                deviceId, startTime, endTime);
        logger.info("Found {} records in MySQL", mysqlData.size());

        // 从MongoDB查询
        List<CollectData> mongoData = mongoRepository
                .findByDeviceIdAndCollectTimeBetween(deviceId, startTime, endTime);
        logger.info("Found {} records in MongoDB", mongoData.size());

        return mongoData; // 这里返回MongoDB的数据，因为它可能包含更多信息
    }

    public List<CollectData> findHighTemperatureData(Double threshold) {
        return mongoRepository.findByTemperatureGreaterThan(threshold);
    }
}
```

## ConfigurationService.java

```java
package com.study.collect.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ConfigurationService {

    private static final String CONFIG_KEY_PREFIX = "collect:config:";
    private final Map<String, ConfigChangeListener> listeners = new ConcurrentHashMap<>();
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    public void setConfig(String key, Object value) {
        String redisKey = CONFIG_KEY_PREFIX + key;
        String oldValue = getConfigValue(key);

        redisTemplate.opsForValue().set(redisKey, objectMapper.valueToTree(value).toString());

        // 通知配置变更
        notifyConfigChange(key, oldValue, value);
    }

    public <T> Optional<T> getConfig(String key, Class<T> type) {
        String value = getConfigValue(key);
        if (value == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(objectMapper.readValue(value, type));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private String getConfigValue(String key) {
        String redisKey = CONFIG_KEY_PREFIX + key;
        Object value = redisTemplate.opsForValue().get(redisKey);
        return value != null ? value.toString() : null;
    }

    public void deleteConfig(String key) {
        String redisKey = CONFIG_KEY_PREFIX + key;
        String oldValue = getConfigValue(key);

        redisTemplate.delete(redisKey);

        // 通知配置删除
        notifyConfigChange(key, oldValue, null);
    }

    public void addListener(String key, ConfigChangeListener listener) {
        listeners.put(key, listener);
    }

    public void removeListener(String key) {
        listeners.remove(key);
    }

    private void notifyConfigChange(String key, String oldValue, Object newValue) {
        ConfigChangeListener listener = listeners.get(key);
        if (listener != null) {
            ConfigChangeEvent event = new ConfigChangeEvent(key, oldValue, newValue);
            listener.onConfigChange(event);
        }
    }

    public interface ConfigChangeListener {
        void onConfigChange(ConfigChangeEvent event);
    }

    @Data
    public static class ConfigChangeEvent {
        private final String key;
        private final String oldValue;
        private final Object newValue;

        public ConfigChangeEvent(String key, String oldValue, Object newValue) {
            this.key = key;
            this.oldValue = oldValue;
            this.newValue = newValue;
        }
    }
}
```

## TaskManagementService.java

```java
package com.study.collect.service;

import com.study.collect.core.collector.Collector;
import com.study.collect.core.strategy.CollectorStrategy;
import com.study.collect.entity.CollectTask;
import com.study.collect.entity.TaskResult;
import com.study.collect.enums.CollectorType;
import com.study.collect.enums.TaskStatus;
import com.study.collect.repository.TaskRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TaskManagementService {

    private static final Logger logger = LoggerFactory.getLogger(TaskManagementService.class);

    private final CollectorStrategy collectorStrategy;
    private final TaskRepository taskRepository;
    private final ApplicationContext applicationContext;
    private final Map<String, Collector> collectorMap;

    // 使用构造函数注入替代@Autowired
    public TaskManagementService(CollectorStrategy collectorStrategy,
                                 TaskRepository taskRepository,
                                 ApplicationContext applicationContext,
                                 Map<String, Collector> collectorMap) {
        this.collectorStrategy = collectorStrategy;
        this.taskRepository = taskRepository;
        this.applicationContext = applicationContext;
        this.collectorMap = collectorMap;
    }

    // 添加启动时的测试代码
    @PostConstruct
    public void init() {
        logger.info("=== Testing Collector Registration ===");
        // 输出所有注册的CollectorType
        logger.info("Available Collector Types: {}", Arrays.toString(CollectorType.values()));

        // 尝试通过Spring Context获取所有Collector
        Map<String, Collector> collectors = applicationContext.getBeansOfType(Collector.class);
        logger.info("Registered Collectors in Spring Context: {}", collectors.keySet());

        // 测试每个类型的collector是否可用
        for (CollectorType type : CollectorType.values()) {
            try {
                Collector collector = getCollectorForType(type);
                logger.info("Collector for type {}: {}", type, collector);
            } catch (Exception e) {
                logger.error("Error getting collector for type {}: {}", type, e.getMessage());
            }
        }
        logger.info("=== Collector Registration Test Complete ===");
    }

    private Collector getCollectorForType(CollectorType type) {
        // 首先尝试从Spring容器获取
        try {
            String beanName = type.name().toLowerCase() + "Collector";
            logger.debug("Trying to get collector bean: {}", beanName);
            if (applicationContext.containsBean(beanName)) {
                return applicationContext.getBean(beanName, Collector.class);
            }
        } catch (Exception e) {
            logger.debug("Could not get collector from Spring context: {}", e.getMessage());
        }

        // 然后尝试从CollectorStrategy获取
        Collector collector = collectorStrategy.getCollector(type);
        if (collector != null) {
            return collector;
        }

        throw new IllegalStateException("No collector found for type: " + type);
    }

    public TaskResult executeTask(String taskId) {
        // 1. 获取并验证任务
        Optional<CollectTask> taskOpt = getTask(taskId);
        if (taskOpt.isEmpty()) {
            logger.warn("Task not found with ID: {}", taskId);
//            return buildFailedResult(taskId, "Task not found");
            return TaskResult.builder()
                    .taskId(taskId)
                    .status(TaskStatus.FAILED)
                    .message("Task not found")
                    .build();
        }
// 2. 任务状态预检查
        CollectTask task = taskOpt.get();
        TaskResult validationResult = validateTaskExecution(task);
        if (validationResult != null) {
            return validationResult;
        }

        try {
            // 3. 更新任务为执行中状态
            updateTaskStatus(task, TaskStatus.RUNNING);

            // 4. 根据任务类型获取对应的采集器并执行
            // 使用新的获取collector方法
            Collector collector;
            try {
                collector = getCollectorForType(task.getCollectorType());
                logger.info("Found collector {} for type {}", collector.getClass().getSimpleName(),
                        task.getCollectorType());
            } catch (Exception e) {
                logger.error("Failed to get collector: {}", e.getMessage());
                throw new IllegalStateException("No collector found for type: " + task.getCollectorType());
            }

            TaskResult result = collector.collect(task);

            // 3. 根据执行结果更新最终状态
            // 更新状态和处理结果
            TaskStatus finalStatus = result.isSuccess() ? TaskStatus.COMPLETED : TaskStatus.FAILED;
            updateTaskStatus(task, finalStatus);
            handleTaskResult(task, result);

            return result;

        } catch (Exception e) {
            logger.error("Error executing task {}: {}", taskId, e.getMessage(), e);
            updateTaskStatus(task, TaskStatus.FAILED);
            handleTaskError(task, e);
            return buildFailedResult(taskId, "Task execution failed: " + e.getMessage());
        }
    }


    private void handleTaskError(CollectTask task, Exception e) {
        // 更新任务状态和错误信息
        task.setStatus(TaskStatus.FAILED);
        task.setUpdateTime(new Date());
        task.setRetryCount(task.getRetryCount() + 1);
        task.setLastError(e.getMessage());

        // 检查重试次数
        if (task.getRetryCount() >= task.getMaxRetries()) {
            logger.error("Task {} failed with max retries exceeded. Last error: {}",
                    task.getId(), e.getMessage());
            task.setStatus(TaskStatus.FAILED);
        } else {
            logger.warn("Task {} failed with error: {}. Retry count: {}/{}",
                    task.getId(), e.getMessage(), task.getRetryCount(), task.getMaxRetries());
        }

        try {
            taskRepository.save(task);
        } catch (Exception saveException) {
            logger.error("Failed to save task error state for task {}: {}",
                    task.getId(), saveException.getMessage(), saveException);
        }

        // 可能需要触发告警或通知
        notifyTaskError(task, e);
    }

    private void notifyTaskError(CollectTask task, Exception e) {
        try {
            // TODO: 实现错误通知逻辑，比如发送邮件或消息
            // 这里先用日志记录，后续可以扩展
            logger.error("Task execution failed - ID: {}, Type: {}, Error: {}",
                    task.getId(),
                    task.getCollectorType(),
                    e.getMessage());
        } catch (Exception notifyException) {
            logger.error("Failed to send error notification for task {}: {}",
                    task.getId(), notifyException.getMessage(), notifyException);
        }
    }

    private TaskResult validateTaskExecution(CollectTask task) {
        // 检查任务状态,使用equals避免NPE
        if (TaskStatus.RUNNING.equals(task.getStatus())) {
            logger.warn("Task {} is already running", task.getId());
            return buildFailedResult(task.getId(), "Task is already running");
        }

        // 检查重试次数
        if (task.getRetryCount() >= task.getMaxRetries()) {
            logger.warn("Task {} has exceeded max retry count", task.getId());
            return buildFailedResult(task.getId(), "Max retries exceeded");
        }

        return null;
    }

    private void handleTaskResult(CollectTask task, TaskResult result) {
        task.setStatus(result.getStatus());
        task.setUpdateTime(new Date());
        if (TaskStatus.FAILED.equals(result.getStatus())) {
            task.setRetryCount(task.getRetryCount() + 1);
            task.setLastError(result.getMessage());
        }
        taskRepository.save(task);

        logger.info("Task {} completed with status: {}", task.getId(), result.getStatus());
    }

    public CollectTask createTask(CollectTask task) {
        task.setStatus(TaskStatus.CREATED);
        task.setCreateTime(new Date());
        task.setUpdateTime(new Date());
        task.setRetryCount(0); // 初始化重试次数

        CollectTask savedTask = taskRepository.save(task);
        logger.info("Created new task with ID: {}", savedTask.getId());

        return savedTask;
    }

    public List<CollectTask> getTasksByStatus(TaskStatus status) {
        return taskRepository.findByStatus(status);
    }

    public Optional<CollectTask> getTask(String taskId) {
        return taskRepository.findById(taskId);
    }

    public void stopTask(String taskId) {
        Optional<CollectTask> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isPresent()) {
            CollectTask task = taskOpt.get();
            // 使用对应类型的collector
            Collector collector = collectorMap.get(task.getCollectorType());
            if (collector != null) {
                collector.stopTask(taskId);
            }

            updateTaskStatus(task, TaskStatus.STOPPED);
            logger.info("Stopped task: {}", taskId);
        }
    }

    private void updateTaskStatus(CollectTask task, TaskStatus status) {
        task.setStatus(status);
        task.setUpdateTime(new Date());
        taskRepository.save(task);
        logger.info("Updated task {} status to: {}", task.getId(), status);
    }

    private TaskResult buildFailedResult(String taskId, String message) {
        return TaskResult.builder()
                .taskId(taskId)
                .status(TaskStatus.FAILED)
                .message(message)
                .build();
    }

    public TaskResult retryTask(String taskId) {
        Optional<CollectTask> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isEmpty()) {
            return TaskResult.builder()
                    .taskId(taskId)
                    .status(TaskStatus.FAILED)
                    .message("Task not found")
                    .build();
        }

        CollectTask task = taskOpt.get();
        if (task.getRetryCount() >= task.getMaxRetries()) {
            return TaskResult.builder()
                    .taskId(taskId)
                    .status(TaskStatus.FAILED)
                    .message("Max retries exceeded")
                    .build();
        }

        task.setRetryCount(task.getRetryCount() + 1);
        task.setUpdateTime(new Date());
        taskRepository.save(task);

        return executeTask(taskId);
    }
}
```

## TestService.java

```java
package com.study.collect.service;

import com.study.collect.entity.MongoTestEntity;
import com.study.collect.entity.TestEntity;
import com.study.collect.mapper.TestMySQLMapper;
import com.study.collect.repository.TestMongoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class TestService {

    private static final String REDIS_KEY_PREFIX = "test:entity:";
    private static final String EXCHANGE_NAME = "test.exchange";
    private static final String ROUTING_KEY = "test.message";
    @Autowired
    private TestMySQLMapper mysqlMapper;
    @Autowired
    private TestMongoRepository mongoRepository;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    // MySQL测试方法
    @Transactional
    public TestEntity testMySQL(String name, String description) {
        TestEntity entity = new TestEntity();
        entity.setName(name);
        entity.setDescription(description);
        entity.setCreateTime(new Date());
        entity.setUpdateTime(new Date());

        mysqlMapper.insert(entity);
        log.info("MySQL插入数据成功: {}", entity);

        // 测试查询
        TestEntity found = mysqlMapper.findById(entity.getId());
        log.info("MySQL查询数据: {}", found);

        // 测试更新
        found.setDescription(description + " - updated");
        found.setUpdateTime(new Date());
        mysqlMapper.update(found);
        log.info("MySQL更新数据成功");

        // 测试删除
        mysqlMapper.deleteById(found.getId());
        log.info("MySQL删除数据成功");

        return found;
    }

    // Redis测试方法
    public void testRedis(String key, String value) {
        String fullKey = REDIS_KEY_PREFIX + key;

        // 测试写入
        redisTemplate.opsForValue().set(fullKey, value, 1, TimeUnit.HOURS);
        log.info("Redis写入数据成功: {} = {}", fullKey, value);

        // 测试读取
        Object stored = redisTemplate.opsForValue().get(fullKey);
        log.info("Redis读取数据: {} = {}", fullKey, stored);

        // 测试删除
        Boolean deleted = redisTemplate.delete(fullKey);
        log.info("Redis删除数据: {}", deleted);
    }

    // MongoDB测试方法
    public MongoTestEntity testMongoDB(String name, String description) {
        MongoTestEntity entity = new MongoTestEntity();
        entity.setName(name);
        entity.setDescription(description);
        entity.setCreateTime(new Date());
        entity.setUpdateTime(new Date());

        // 测试插入
        MongoTestEntity saved = mongoRepository.save(entity);
        log.info("MongoDB插入数据成功: {}", saved);

        // 测试查询
        MongoTestEntity found = mongoRepository.findById(saved.getId()).orElse(null);
        log.info("MongoDB查询数据: {}", found);

        // 测试更新
        found.setDescription(description + " - updated");
        found.setUpdateTime(new Date());
        mongoRepository.save(found);
        log.info("MongoDB更新数据成功");

        // 测试删除
        mongoRepository.deleteById(found.getId());
        log.info("MongoDB删除数据成功");

        return found;
    }

    // RabbitMQ测试方法
    public void testRabbitMQ(String message) {
        // 发送消息
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, message);
        log.info("RabbitMQ发送消息成功: {}", message);

        // 接收消息（这里为了测试，直接接收，实际应该使用监听器）
//        Object received = rabbitTemplate.receiveAndConvert(ROUTING_KEY);
        Object received = rabbitTemplate.receiveAndConvert("test.message");
        log.info("RabbitMQ接收消息: {}", received);
    }
}
```

## TreeCollectorService.java

```java
package com.study.collect.service;

import com.study.collect.entity.TreeCollectTask;
import com.study.collect.entity.TreeNode;
import com.study.collect.enums.TaskStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class TreeCollectorService {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public TreeCollectTask startCollection(String projectId) {
        // 创建采集任务
        TreeCollectTask task = new TreeCollectTask();
        task.setProjectId(projectId);
        task.setStatus(TaskStatus.RUNNING);
        task.setCreateTime(new Date());
        task.setStartTime(new Date());

        mongoTemplate.save(task);

        try {
            // 执行采集
            collectProjectTree(projectId, task);

            // 更新任务状态
            task.setStatus(TaskStatus.COMPLETED);
            task.setEndTime(new Date());
        } catch (Exception e) {
            log.error("Failed to collect tree for project: " + projectId, e);
            task.setStatus(TaskStatus.FAILED);
            task.getErrors().add(e.getMessage());
        }

        return mongoTemplate.save(task);
    }

    private void collectProjectTree(String projectId, TreeCollectTask task) {
        // 采集项目根节点
        TreeNode projectNode = new TreeNode();
        projectNode.setId("/p/" + projectId);
        projectNode.setProjectId(projectId);
        projectNode.setName("Project-" + projectId);
        projectNode.setType(TreeNode.NodeType.PROJECT);
        projectNode.setLevel(0);
        projectNode.setPath(List.of(projectNode.getId()));

        mongoTemplate.save(projectNode);

        // 采集Root节点
        collectRootNodes(projectNode, task);
    }

    private void collectRootNodes(TreeNode parent, TreeCollectTask task) {
        // 模拟采集3个根节点
        for (int i = 1; i <= 3; i++) {
            TreeNode root = new TreeNode();
            root.setId(parent.getId() + "/root" + i);
            root.setProjectId(parent.getProjectId());
            root.setParentId(parent.getId());
            root.setName("Root-" + i);
            root.setType(TreeNode.NodeType.ROOT);
            root.setLevel(parent.getLevel() + 1);
            root.setPath(new ArrayList<>(parent.getPath()));
            root.getPath().add(root.getId());

            mongoTemplate.save(root);

            // 为每个Root创建一个基线版本和执行版本
            collectVersions(root, task);
        }
    }

    private void collectVersions(TreeNode root, TreeCollectTask task) {
        // 创建基线版本
        TreeNode baseline = new TreeNode();
        baseline.setId(root.getId() + "/baseline");
        baseline.setProjectId(root.getProjectId());
        baseline.setParentId(root.getId());
        baseline.setName("Baseline");
        baseline.setType(TreeNode.NodeType.BASELINE_VERSION);
        baseline.setLevel(root.getLevel() + 1);
        baseline.setPath(new ArrayList<>(root.getPath()));
        baseline.getPath().add(baseline.getId());

        mongoTemplate.save(baseline);

        // 创建执行版本
        TreeNode execute = new TreeNode();
        execute.setId(root.getId() + "/execute");
        execute.setProjectId(root.getProjectId());
        execute.setParentId(root.getId());
        execute.setName("Execute");
        execute.setType(TreeNode.NodeType.EXECUTE_VERSION);
        execute.setLevel(root.getLevel() + 1);
        execute.setPath(new ArrayList<>(root.getPath()));
        execute.getPath().add(execute.getId());

        mongoTemplate.save(execute);

        // 为各版本创建Case Directory
        collectCaseDirectory(baseline, task);
        collectCaseDirectory(execute, task);
    }

    private void collectCaseDirectory(TreeNode version, TreeCollectTask task) {
        TreeNode caseDir = new TreeNode();
        caseDir.setId(version.getId() + "/cases");
        caseDir.setProjectId(version.getProjectId());
        caseDir.setParentId(version.getId());
        caseDir.setName("Cases");
        caseDir.setType(TreeNode.NodeType.CASE_DIRECTORY);
        caseDir.setLevel(version.getLevel() + 1);
        caseDir.setPath(new ArrayList<>(version.getPath()));
        caseDir.getPath().add(caseDir.getId());

        mongoTemplate.save(caseDir);

        // 创建一些测试用例和目录
        collectTestCasesAndDirectories(caseDir, task, 3);
    }

    private void collectTestCasesAndDirectories(TreeNode parent, TreeCollectTask task, int depth) {
        if (depth <= 0) return;

        // 创建Normal Directory
        TreeNode dir = new TreeNode();
        dir.setId(parent.getId() + "/dir" + depth);
        dir.setProjectId(parent.getProjectId());
        dir.setParentId(parent.getId());
        dir.setName("Directory-" + depth);
        dir.setType(TreeNode.NodeType.NORMAL_DIRECTORY);
        dir.setLevel(parent.getLevel() + 1);
        dir.setPath(new ArrayList<>(parent.getPath()));
        dir.getPath().add(dir.getId());

        mongoTemplate.save(dir);

        // 创建测试用例
        TreeNode testCase = new TreeNode();
        testCase.setId(parent.getId() + "/case" + depth);
        testCase.setProjectId(parent.getProjectId());
        testCase.setParentId(parent.getId());
        testCase.setName("TestCase-" + depth);
        testCase.setType(TreeNode.NodeType.TEST_CASE);
        testCase.setLevel(parent.getLevel() + 1);
        testCase.setPath(new ArrayList<>(parent.getPath()));
        testCase.getPath().add(testCase.getId());

        mongoTemplate.save(testCase);

        // 递归创建下一层
        collectTestCasesAndDirectories(dir, task, depth - 1);
    }
}

```

## TreeQueryService.java

```java
package com.study.collect.service;


import com.study.collect.entity.TreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TreeQueryService {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 获取项目的完整树结构
     */
    public List<TreeNode> getProjectTree(String projectId) {
        // 获取所有节点
        List<TreeNode> allNodes = mongoTemplate.find(
                Query.query(Criteria.where("projectId").is(projectId)),
                TreeNode.class
        );

        // 构建树结构
        return buildTree(allNodes);
    }

    /**
     * 获取指定节点的子树
     */
    public List<TreeNode> getSubTree(String nodeId) {
        // 先获取目标节点
        TreeNode node = mongoTemplate.findById(nodeId, TreeNode.class);
        if (node == null) {
            return Collections.emptyList();
        }

        // 获取所有子节点
        List<TreeNode> children = mongoTemplate.find(
                Query.query(Criteria.where("path").regex("^" + nodeId)),
                TreeNode.class
        );

        children.add(node);
        return buildTree(children);
    }

    /**
     * 获取指定类型的节点
     */
    public List<TreeNode> getNodesByType(String projectId, TreeNode.NodeType type) {
        return mongoTemplate.find(
                Query.query(Criteria.where("projectId").is(projectId)
                        .and("type").is(type)),
                TreeNode.class
        );
    }

    /**
     * 获取指定路径下的直接子节点
     */
    public List<TreeNode> getDirectChildren(String parentId) {
        return mongoTemplate.find(
                Query.query(Criteria.where("parentId").is(parentId)),
                TreeNode.class
        );
    }

    private List<TreeNode> buildTree(List<TreeNode> nodes) {
        Map<String, TreeNode> nodeMap = new HashMap<>();
        List<TreeNode> roots = new ArrayList<>();

        // 创建节点映射
        nodes.forEach(node -> nodeMap.put(node.getId(), node));

        // 构建树结构
        nodes.forEach(node -> {
            if (node.getParentId() == null) {
                roots.add(node);
            } else {
                TreeNode parent = nodeMap.get(node.getParentId());
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(node);
                }
            }
        });

        return roots;
    }
}

```

## NetworkUtil.java

```java
package com.study.collect.utils;

import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

@Slf4j
public class NetworkUtil {
    public static String getLocalIp() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                if (iface.isLoopback() || !iface.isUp()) continue;

                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (addr.getHostAddress().contains(":")) continue; // Skip IPv6
                    return addr.getHostAddress();
                }
            }
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            log.error("Failed to get local IP", e);
            return "unknown";
        }
    }
}
```

## RabbitMQUtils.java

```java
package com.study.collect.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RabbitMQUtils {
    private static final Logger logger = LoggerFactory.getLogger(RabbitMQUtils.class);

    private final RabbitTemplate rabbitTemplate;
    private final MessageConverter messageConverter;

    public RabbitMQUtils(RabbitTemplate rabbitTemplate, MessageConverter messageConverter) {
        this.rabbitTemplate = rabbitTemplate;
        this.messageConverter = messageConverter;
    }

    /**
     * 发送消息到指定交换机
     */
    public void sendMessage(String exchange, String routingKey, Object message) {
        String correlationId = UUID.randomUUID().toString();
        CorrelationData correlationData = new CorrelationData(correlationId);

        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, message, correlationData);
            logger.info("Message sent successfully - CorrelationId: {}, Exchange: {}, RoutingKey: {}",
                    correlationId, exchange, routingKey);
        } catch (Exception e) {
            logger.error("Failed to send message - CorrelationId: {}, Exchange: {}, RoutingKey: {}",
                    correlationId, exchange, routingKey, e);
            throw e;
        }
    }

    /**
     * 发送延迟消息
     */
    public void sendDelayedMessage(String exchange, String routingKey, Object message, long delayMillis) {
        MessageProperties properties = new MessageProperties();
        properties.setDelay((int) delayMillis);

        Message amqpMessage = messageConverter.toMessage(message, properties);
        String correlationId = UUID.randomUUID().toString();

        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, amqpMessage, msg -> {
                MessageProperties msgProperties = msg.getMessageProperties();
                msgProperties.setDelay((int) delayMillis);
                return msg;
            }, new CorrelationData(correlationId));

            logger.info("Delayed message sent successfully - CorrelationId: {}, Delay: {}ms",
                    correlationId, delayMillis);
        } catch (Exception e) {
            logger.error("Failed to send delayed message - CorrelationId: {}", correlationId, e);
            throw e;
        }
    }

    /**
     * 发送消息到重试队列
     */
    public void sendToRetryQueue(String exchange, String routingKey, Object message, int retryCount) {
        MessageProperties properties = new MessageProperties();
        properties.setHeader("x-retry-count", retryCount);

        Message amqpMessage = messageConverter.toMessage(message, properties);
        String correlationId = UUID.randomUUID().toString();

        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, amqpMessage, msg -> {
                MessageProperties msgProperties = msg.getMessageProperties();
                msgProperties.setHeader("x-retry-count", retryCount);
                return msg;
            }, new CorrelationData(correlationId));

            logger.info("Message sent to retry queue - CorrelationId: {}, RetryCount: {}",
                    correlationId, retryCount);
        } catch (Exception e) {
            logger.error("Failed to send message to retry queue - CorrelationId: {}", correlationId, e);
            throw e;
        }
    }
}

```

## RetryUtil.java

```java
package com.study.collect.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class RetryUtil {
    private static final Logger logger = LoggerFactory.getLogger(RetryUtil.class);

    public static <T> T retry(RetryCallback<T> action, RetryConfig config) {
        int attempts = 0;
        long delay = config.getInitialInterval();
        Exception lastException = null;

        while (attempts < config.getMaxAttempts()) {
            try {
                return action.doWithRetry();
            } catch (Exception e) {
                lastException = e;
                attempts++;
                if (attempts >= config.getMaxAttempts()) break;

                try {
                    TimeUnit.MILLISECONDS.sleep(delay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Retry interrupted", ie);
                }

                delay = calculateNextDelay(delay, config);
                logger.warn("Retry attempt {} failed, next delay: {}ms", attempts, delay, e);
            }
        }

        throw new RuntimeException("All retry attempts failed", lastException);
    }

    private static long calculateNextDelay(long currentDelay, RetryConfig config) {
        long nextDelay = (long) (currentDelay * config.getMultiplier());
        return Math.min(nextDelay, config.getMaxInterval());
    }

    @FunctionalInterface
    public interface RetryCallback<T> {
        T doWithRetry() throws Exception;
    }

    public static class RetryConfig {
        private final int maxAttempts;
        private final long initialInterval;
        private final long maxInterval;
        private final double multiplier;

        private RetryConfig(Builder builder) {
            this.maxAttempts = builder.maxAttempts;
            this.initialInterval = builder.initialInterval;
            this.maxInterval = builder.maxInterval;
            this.multiplier = builder.multiplier;
        }

        // Getters
        public int getMaxAttempts() {
            return maxAttempts;
        }

        public long getInitialInterval() {
            return initialInterval;
        }

        public long getMaxInterval() {
            return maxInterval;
        }

        public double getMultiplier() {
            return multiplier;
        }

        public static class Builder {
            private int maxAttempts = 3;
            private long initialInterval = 1000;
            private long maxInterval = 10000;
            private double multiplier = 2.0;

            public Builder maxAttempts(int maxAttempts) {
                this.maxAttempts = maxAttempts;
                return this;
            }

            public Builder initialInterval(long initialInterval) {
                this.initialInterval = initialInterval;
                return this;
            }

            public Builder maxInterval(long maxInterval) {
                this.maxInterval = maxInterval;
                return this;
            }

            public Builder multiplier(double multiplier) {
                this.multiplier = multiplier;
                return this;
            }

            public RetryConfig build() {
                return new RetryConfig(this);
            }
        }
    }
}
```

## SystemResourceUtil.java

```java
package com.study.collect.utils;

import com.sun.management.OperatingSystemMXBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.management.ManagementFactory;

public class SystemResourceUtil {
    private static final Logger logger = LoggerFactory.getLogger(SystemResourceUtil.class);
    private static final OperatingSystemMXBean osBean =
            (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

    public static double getCpuUsage() {
        try {
            return osBean.getSystemCpuLoad() * 100;
        } catch (Exception e) {
            logger.error("Failed to get CPU usage", e);
            return -1;
        }
    }

    public static double getMemoryUsage() {
        try {
            long totalMemory = osBean.getTotalPhysicalMemorySize();
            long freeMemory = osBean.getFreePhysicalMemorySize();
            return ((double) (totalMemory - freeMemory) / totalMemory) * 100;
        } catch (Exception e) {
            logger.error("Failed to get memory usage", e);
            return -1;
        }
    }

    // 添加磁盘使用率监控
    public static double getDiskUsage() {
        try {
            File root = new File("/");
            long totalSpace = root.getTotalSpace();
            long usableSpace = root.getUsableSpace();
            return ((double) (totalSpace - usableSpace) / totalSpace) * 100;
        } catch (Exception e) {
            logger.error("Failed to get disk usage", e);
            return -1;
        }
    }
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

