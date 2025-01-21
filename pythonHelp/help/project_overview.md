# Project Structure

```
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
                                    aspect/
                                        mongodb/
                                            CollectionStrategy.java
                                            CollectionVersion.java
                                            CollectionVersionAspect.java
                                    config/
                                        DynamicCollectionIndexConfiguration.java
                                        GlobalExceptionHandler.java
                                        MongoConfig.java
                                        MongoIndexChecker.java
                                        MongoIndexConfig.java
                                        MongoIndexConfiguration.java
                                        ObjectPoolConfig.java
                                        TestCaseAutoConfiguration.java
                                        TestCaseCollectorProperties.java
                                        TestCaseConfig.java
                                        ThreadPoolConfig.java
                                    constant/
                                        CollectionConstants.java
                                        VersionType.java
                                    controller/
                                        UriCollectController.java
                                    entity/
                                        BaseEntity.java
                                        CollectTaskEntity.java
                                        UriEntity.java
                                        VersionEntity.java
                                    manager/
                                        CollectTaskManager.java
                                        QueueManager.java
                                    model/
                                        PageResult.java
                                        UriQueryCondition.java
                                        param/
                                            CollectParam.java
                                            DeleteParam.java
                                            PageParam.java
                                            QueryParam.java
                                        response/
                                            AsyncResponse.java
                                            BaseResponse.java
                                            PageResponse.java
                                            TaskResponse.java
                                            UriDetail.java
                                            VersionInfo.java
                                            VersionResponse.java
                                            parse/
                                                HttpResponseParser.java
                                                UriCountResponseParser.java
                                                UriDetailResponseParser.java
                                                UriListResponseParser.java
                                                VersionResponseParser.java
                                    repository/
                                        CollectTaskRepository.java
                                        UriRepository.java
                                        VersionRepository.java
                                    service/
                                        CollectScheduler.java
                                        UriCollectService.java
                                        http/
                                            UriHttpService.java
                                        impl/
                                            IndexChecker.java
                                            UriCleanupService.java
                                            UriCollectServiceImpl.java
                                    utils/
                                        HashUtil.java
                                        HttpUtil.java
                                        ListCompareUtil.java
                                        RateLimiter.java
                                        StreamProcessManager.java
            resources/
                META-INF/
                    spring.factories
```

# File Contents

## pom.xml

```xml
<project xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://maven.apache.org/POM/4.0.0"
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
        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger2</artifactId>
            <version>2.9.2</version>
        </dependency>
        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger-ui</artifactId>
            <version>2.9.2</version>
        </dependency>
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
            <version>3.12.0</version>
        </dependency>
        <dependency>
            <groupId>javax.annotation</groupId>
            <artifactId>javax.annotation-api</artifactId>
            <version>1.3.2</version>
        </dependency>
        <dependency>
            <groupId>javax.validation</groupId>
            <artifactId>validation-api</artifactId>
            <version>2.0.1.Final</version>
        </dependency>
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

        <dependency>
            <groupId>commons-codec</groupId>
            <artifactId>commons-codec</artifactId>
            <version>1.15</version>
        </dependency>
    </dependencies>
</project>

```

## CollectionStrategy.java

```java
package com.study.collect.business.testcase.aspect.mongodb;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class CollectionStrategy implements ApplicationContextAware {
    private static final ThreadLocal<String> versionHolder = new ThreadLocal<>();
    private static ApplicationContext applicationContext;

    public static void setVersion(String version) {
        versionHolder.set(version);
    }

    public static void clearVersion() {
        versionHolder.remove();
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        applicationContext = context;
    }

    public String getCollectionName(String baseCollection) {
        String version = versionHolder.get();
        return version != null ? baseCollection + "_" + version : baseCollection;
    }
}
```

## CollectionVersion.java

```java
package com.study.collect.business.testcase.aspect.mongodb;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 集合版本注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CollectionVersion {
    /**
     * 参数名称，如果指定则按参数名查找
     */
    String paramName() default "";

    /**
     * 参数位置，如果未指定参数名则按位置查找
     */
    int paramIndex() default 0;
}


```

## CollectionVersionAspect.java

```java
package com.study.collect.business.testcase.aspect.mongodb;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 集合版本切面
 */
@Aspect
@Component
@Slf4j
public class CollectionVersionAspect {

    @Around("@annotation(com.study.collect.business.testcase.aspect.mongodb.CollectionVersion)")
    public Object aroundCollectionVersion(ProceedingJoinPoint joinPoint) throws Throwable {
        CollectionVersion annotation = ((MethodSignature) joinPoint.getSignature())
                .getMethod().getAnnotation(CollectionVersion.class);

        try {
            String version = resolveVersion(joinPoint, annotation);
            if (version == null) {
                throw new IllegalArgumentException("Failed to resolve collection version");
            }

            CollectionStrategy.setVersion(version);
            return joinPoint.proceed();
        } finally {
            CollectionStrategy.clearVersion();
        }
    }

    /**
     * 解析版本信息
     */
    private String resolveVersion(ProceedingJoinPoint joinPoint, CollectionVersion annotation) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Object[] args = joinPoint.getArgs();

        // 如果没有参数，抛出异常
        if (args == null || args.length == 0) {
            throw new IllegalArgumentException("No parameters found in method: " + signature.getMethod().getName());
        }

        // 1. 尝试按参数名查找
        if (StringUtils.hasText(annotation.paramName())) {
            String[] parameterNames = signature.getParameterNames();
            for (int i = 0; i < parameterNames.length; i++) {
                if (annotation.paramName().equals(parameterNames[i])) {
                    return resolveVersionFromObject(args[i]);
                }
            }
            log.warn("Parameter name '{}' not found in method: {}",
                    annotation.paramName(), signature.getMethod().getName());
        }

        // 2. 按参数位置查找
        int paramIndex = annotation.paramIndex();
        if (paramIndex >= 0 && paramIndex < args.length) {
            return resolveVersionFromObject(args[paramIndex]);
        }

        // 3. 尝试从第一个参数查找版本信息
        return resolveVersionFromObject(args[0]);
    }

    /**
     * 从对象中解析版本信息
     */
    private String resolveVersionFromObject(Object arg) {
        if (arg == null) {
            return null;
        }

        // 直接是String类型
        if (arg instanceof String) {
            return (String) arg;
        }

        // 如果是请求对象，尝试获取version字段
        try {
            // 通过反射查找version字段
            Field versionField = ReflectionUtils.findField(arg.getClass(), "version");
            if (versionField != null) {
                ReflectionUtils.makeAccessible(versionField);
                Object value = versionField.get(arg);
                return value != null ? value.toString() : null;
            }

            // 尝试通过getter方法获取
            Method getVersion = ReflectionUtils.findMethod(arg.getClass(), "getVersion");
            if (getVersion != null) {
                ReflectionUtils.makeAccessible(getVersion);
                Object value = getVersion.invoke(arg);
                return value != null ? value.toString() : null;
            }

            // 尝试查找uriVersion字段
            Field uriVersionField = ReflectionUtils.findField(arg.getClass(), "uriVersion");
            if (uriVersionField != null) {
                ReflectionUtils.makeAccessible(uriVersionField);
                Object value = uriVersionField.get(arg);
                return value != null ? value.toString() : null;
            }
        } catch (Exception e) {
            log.debug("Failed to resolve version from object: {}", arg, e);
        }

        return null;
    }
}

```

## DynamicCollectionIndexConfiguration.java

```java
package com.study.collect.business.testcase.config;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.study.collect.business.testcase.aspect.mongodb.CollectionStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DynamicCollectionIndexConfiguration {

    private final MongoTemplate mongoTemplate;
    private final CollectionStrategy collectionStrategy;

    /**
     * 创建动态集合的索引
     */
    public void createIndexesForCollection(String baseCollection) {
        String collectionName = collectionStrategy.getCollectionName(baseCollection);

        try {
            // 如果集合不存在，先创建集合
            if (!mongoTemplate.collectionExists(collectionName)) {
                mongoTemplate.createCollection(collectionName);
            }

            MongoCollection<Document> collection = mongoTemplate.getCollection(collectionName);

            // 创建复合唯一索引
            collection.createIndex(
                    Indexes.compoundIndex(
                            Indexes.ascending("uri"),
                            Indexes.ascending("root_node"),
                            Indexes.ascending("version_type"),
                            Indexes.ascending("uri_version")
                    ),
                    new IndexOptions()
                            .name("idx_uri_composite")
                            .unique(true)
                            .background(true)
            );

            // 创建 uri_hash 唯一索引
            collection.createIndex(
                    Indexes.ascending("uri_hash"),
                    new IndexOptions()
                            .name("idx_uri_hash")
                            .unique(true)
                            .background(true)
            );

            // 创建查询索引
            collection.createIndex(
                    Indexes.compoundIndex(
                            Indexes.ascending("root_node"),
                            Indexes.ascending("version_type"),
                            Indexes.ascending("uri_version"),
                            Indexes.ascending("is_deleted")
                    ),
                    new IndexOptions()
                            .name("idx_query")
                            .background(true)
            );

            // 检查并输出索引信息
            checkIndexes(collectionName);

        } catch (Exception e) {
            log.error("Failed to create indexes for collection: {}", collectionName, e);
            throw new RuntimeException("Failed to create indexes", e);
        }
    }

    /**
     * 检查集合的索引
     */
    public void checkIndexes(String collectionName) {
        try {
            List<Document> indexes = mongoTemplate.getCollection(collectionName)
                    .listIndexes()
                    .into(new ArrayList<>());

            log.info("Collection {} indexes:", collectionName);
            indexes.forEach(index -> log.info(index.toJson()));

            // 验证必需的索引是否存在
            Set<String> indexNames = indexes.stream()
                    .map(doc -> doc.getString("name"))
                    .collect(Collectors.toSet());

            List<String> requiredIndexes = Arrays.asList(
                    "idx_uri_composite",
                    "idx_uri_hash",
                    "idx_query"
            );

            for (String requiredIndex : requiredIndexes) {
                if (!indexNames.contains(requiredIndex)) {
                    log.warn("Required index {} is missing in collection {}",
                            requiredIndex, collectionName);
                }
            }

        } catch (Exception e) {
            log.error("Failed to check indexes for collection: {}", collectionName, e);
        }
    }
}

```

## GlobalExceptionHandler.java

```java
package com.study.collect.business.testcase.config;

import com.study.collect.business.testcase.model.response.AsyncResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<AsyncResponse<Void>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        return ResponseEntity
                .badRequest()
                .body(AsyncResponse.<Void>asyncBuilder()
                        .status("ERROR")
                        .message("Validation failed: " + errors)
                        .build());
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<AsyncResponse<Void>> handleBindException(BindException ex) {
        String errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return ResponseEntity
                .badRequest()
                .body(AsyncResponse.<Void>asyncBuilder()
                        .status("ERROR")
                        .message("Invalid parameters: " + errors)
                        .build());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<AsyncResponse<Void>> handleConstraintViolation(
            ConstraintViolationException ex) {
        String errors = ex.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));

        return ResponseEntity
                .badRequest()
                .body(AsyncResponse.<Void>asyncBuilder()
                        .status("ERROR")
                        .message("Validation failed: " + errors)
                        .build());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<AsyncResponse<Void>> handleIllegalArgument(
            IllegalArgumentException ex) {
        return ResponseEntity
                .badRequest()
                .body(AsyncResponse.<Void>asyncBuilder()
                        .status("ERROR")
                        .message("Invalid argument: " + ex.getMessage())
                        .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<AsyncResponse<Void>> handleAllExceptions(Exception ex) {
        log.error("Unexpected error", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(AsyncResponse.<Void>asyncBuilder()
                        .status("ERROR")
                        .message("Internal server error: " + ex.getMessage())
                        .build());
    }
}
```

## MongoConfig.java

```java
package com.study.collect.business.testcase.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.study.collect.business.testcase.constant.CollectionConstants;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableMongoAuditing
@ConditionalOnProperty(prefix = "spring.data.mongodb", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableMongoRepositories(basePackages = "com.study.collect.business.testcase.repository")
@ConfigurationProperties(prefix = "spring.data.mongodb")
@Data
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Value("${spring.data.mongodb.uri}")
    private String uri;

    @Value("${spring.data.mongodb.database}")
    private String database;

    @Value("${spring.data.mongodb.min-pool-size:" + CollectionConstants.MONGO_MIN_POOL_SIZE + "}")
    private Integer minPoolSize;

    @Value("${spring.data.mongodb.max-pool-size:" + CollectionConstants.MONGO_MAX_POOL_SIZE + "}")
    private Integer maxPoolSize;

    @Override
    protected String getDatabaseName() {
        return database;
    }

    @Override
    @Bean
    public MongoClient mongoClient() {
        ConnectionString connectionString = new ConnectionString(uri);

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .applyToConnectionPoolSettings(builder ->
                        builder.minSize(minPoolSize)
                                .maxSize(maxPoolSize)
                                .maxWaitTime(10, TimeUnit.SECONDS)
                                .maxConnectionLifeTime(30, TimeUnit.MINUTES)
                                .maxConnectionIdleTime(5, TimeUnit.MINUTES))
                .applyToSocketSettings(builder ->
                        builder.connectTimeout(5, TimeUnit.SECONDS)
                                .readTimeout(10, TimeUnit.SECONDS))
                .retryWrites(true)
                .retryReads(true)
                .build();

        return MongoClients.create(settings);
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoClient mongoClient) {
        return new MongoTemplate(mongoClient, getDatabaseName());
    }
}
```

## MongoIndexChecker.java

```java
//package com.study.collect.business.testcase.config;
//
//import lombok.RequiredArgsConstructor;
//import org.bson.Document;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.context.ApplicationListener;
//import org.springframework.context.event.ContextRefreshedEvent;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.stereotype.Component;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Component
//@RequiredArgsConstructor
//public class MongoIndexChecker implements ApplicationListener<ContextRefreshedEvent> {
//
//    private final MongoTemplate mongoTemplate;
//    private static final Logger log = LoggerFactory.getLogger(MongoIndexChecker.class);
//
//    @Override
//    public void onApplicationEvent(ContextRefreshedEvent event) {
//        String collectionName = "uri_collect";
//        try {
//            List<Document> indexes = mongoTemplate.getCollection(collectionName)
//                    .listIndexes().into(new ArrayList<>());
//            log.info("Collection {} indexes on startup: {}", collectionName, indexes);
//        } catch (Exception e) {
//            log.error("Failed to check indexes for collection: " + collectionName, e);
//        }
//    }
//}

```

## MongoIndexConfig.java

```java
//package com.study.collect.business.testcase.config;
//
//import jakarta.annotation.PostConstruct;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.domain.Sort;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.index.Index;
//import org.springframework.data.mongodb.core.index.IndexDefinition;
//import org.springframework.data.mongodb.core.index.IndexOperations;
//
//@Configuration
//public class MongoIndexConfig {
//
//    @PostConstruct
//    public void ensureIndexes(MongoTemplate mongoTemplate) {
//        String collectionName = "uri_collect";
//
//        // 创建复合唯一索引
//        IndexOperations indexOps = mongoTemplate.indexOps(collectionName);
//
//        IndexDefinition uriUniqueIndex = new Index()
//                .on("uri", Sort.Direction.ASC)
//                .on("root_node", Sort.Direction.ASC)
//                .on("version_type", Sort.Direction.ASC)
//                .on("uri_version", Sort.Direction.ASC)
//                .unique();
//        indexOps.ensureIndex(uriUniqueIndex);
//
//        // 创建 uri_hash 唯一索引
//        IndexDefinition uriHashIndex = new Index()
//                .on("uri_hash", Sort.Direction.ASC)
//                .unique();
//        indexOps.ensureIndex(uriHashIndex);
//
//        // 创建查询索引
//        IndexDefinition queryIndex = new Index()
//                .on("root_node", Sort.Direction.ASC)
//                .on("version_type", Sort.Direction.ASC)
//                .on("uri_version", Sort.Direction.ASC)
//                .on("is_deleted", Sort.Direction.ASC);
//        indexOps.ensureIndex(queryIndex);
//    }
//}
```

## MongoIndexConfiguration.java

```java
//package com.study.collect.business.testcase.config;
//
//import com.study.collect.business.testcase.entity.UriEntity;
//import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.domain.Sort;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.index.IndexOperations;
//import jakarta.annotation.PostConstruct;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.domain.Sort;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.index.Index;
//import org.springframework.data.mongodb.core.index.IndexDefinition;
//import org.springframework.data.mongodb.core.index.IndexOperations;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.bson.Document;
//
//@Configuration
//@RequiredArgsConstructor
//public class MongoIndexConfiguration {
//
//    private final MongoTemplate mongoTemplate;
//    private static final Logger log = LoggerFactory.getLogger(MongoIndexConfiguration.class);
//
//    @PostConstruct
//    public void initIndexes() {
//        try {
//            // 确保集合存在
//            if (!mongoTemplate.collectionExists(UriEntity.class)) {
//                mongoTemplate.createCollection(UriEntity.class);
//            }
//
//            // 获取索引操作对象
//            IndexOperations indexOps = mongoTemplate.indexOps(UriEntity.class);
//
//            // 创建复合唯一索引
//            IndexDefinition uriCompositeIndex = new Index()
//                    .on("uri", Sort.Direction.ASC)
//                    .on("root_node", Sort.Direction.ASC)
//                    .on("version_type", Sort.Direction.ASC)
//                    .on("uri_version", Sort.Direction.ASC)
//                    .named("idx_uri_composite")
//                    .unique();
//            indexOps.ensureIndex(uriCompositeIndex);
//
//            // 创建 uri_hash 唯一索引
//            IndexDefinition uriHashIndex = new Index()
//                    .on("uri_hash", Sort.Direction.ASC)
//                    .named("idx_uri_hash")
//                    .unique();
//            indexOps.ensureIndex(uriHashIndex);
//
//            // 查询索引
//            IndexDefinition queryIndex = new Index()
//                    .on("root_node", Sort.Direction.ASC)
//                    .on("version_type", Sort.Direction.ASC)
//                    .on("uri_version", Sort.Direction.ASC)
//                    .on("is_deleted", Sort.Direction.ASC)
//                    .named("idx_query");
//            indexOps.ensureIndex(queryIndex);
//
//            // 输出所有索引信息
//            List<Document> indexes = mongoTemplate.getCollection(mongoTemplate.getCollectionName(UriEntity.class))
//                    .listIndexes()
//                    .into(new ArrayList<>());
//            log.info("Collection indexes after initialization: {}", indexes);
//
//        } catch (Exception e) {
//            log.error("Failed to initialize indexes", e);
//            throw new RuntimeException("Failed to initialize MongoDB indexes", e);
//        }
//    }
//}
```

## ObjectPoolConfig.java

```java
package com.study.collect.business.testcase.config;

import com.study.collect.business.testcase.constant.CollectionConstants;
import com.study.collect.business.testcase.entity.UriEntity;
import com.study.collect.business.testcase.utils.HashUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class ObjectPoolConfig {

    @Bean(destroyMethod = "close")
    public GenericObjectPool<UriEntity> uriEntityPool() {
        GenericObjectPoolConfig<UriEntity> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxTotal(CollectionConstants.POOL_MAX_TOTAL);
        poolConfig.setMaxIdle(CollectionConstants.POOL_MAX_IDLE);
        poolConfig.setMinIdle(CollectionConstants.POOL_MIN_IDLE);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setBlockWhenExhausted(true);
        poolConfig.setTimeBetweenEvictionRuns(java.time.Duration.ofMinutes(1));
        poolConfig.setJmxEnabled(false);
//        poolConfig.setJmxEnabled(true);
        poolConfig.setJmxNamePrefix("uri-entity-pool");

        return new GenericObjectPool<>(new BasePooledObjectFactory<>() {
            @Override
            public UriEntity create() {
                try {
                    return new UriEntity();
                } catch (Exception e) {
                    log.error("Failed to create UriEntity in pool", e);
                    throw new RuntimeException("Failed to create UriEntity", e);
                }
            }

            @Override
            public PooledObject<UriEntity> wrap(UriEntity entity) {
                return new DefaultPooledObject<>(entity);
            }

            @Override
            public void passivateObject(PooledObject<UriEntity> p) {
                try {
                    UriEntity entity = p.getObject();
                    entity.reset();
                } catch (Exception e) {
                    log.error("Failed to reset UriEntity fields", e);
                }
            }

            @Override
            public boolean validateObject(PooledObject<UriEntity> p) {
//                return p.getObject() != null;
                UriEntity entity = p.getObject();
                // 确保对象有效且关键字段正确
                return entity != null &&
                        (entity.getUri() == null || // 如果 uri 为空说明是新对象
                                (entity.getUriHash() != null && entity.getUriHash().equals(HashUtil.hash(entity.getUri())))); // 如果有 uri 则验证 hash
            }
        }, poolConfig);
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

## ThreadPoolConfig.java

```java
package com.study.collect.business.testcase.config;

import com.study.collect.business.testcase.constant.CollectionConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.*;

@Configuration
@EnableAsync
@Slf4j
public class ThreadPoolConfig {

    @Bean(name = "collectExecutor")
    public ExecutorService collectExecutor() {
        return new ThreadPoolExecutor(
                CollectionConstants.CORE_POOL_SIZE,
                CollectionConstants.MAX_POOL_SIZE,
                CollectionConstants.KEEP_ALIVE_TIME,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(CollectionConstants.QUEUE_CAPACITY),
                new ThreadFactory() {
                    private int count = 0;

                    @Override
                    public Thread newThread(Runnable r) {
                        Thread thread = new Thread(r);
                        thread.setName("collect-thread-" + count++);
                        thread.setDaemon(true);
                        return thread;
                    }
                },
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    @Bean(name = "httpExecutor")
    public ThreadPoolTaskExecutor httpExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(CollectionConstants.CORE_POOL_SIZE);
        executor.setMaxPoolSize(CollectionConstants.MAX_POOL_SIZE);
        executor.setQueueCapacity(CollectionConstants.QUEUE_CAPACITY);
        executor.setKeepAliveSeconds((int) CollectionConstants.KEEP_ALIVE_TIME);
        executor.setThreadNamePrefix("http-thread-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        return executor;
    }

    @Bean(name = "mongoExecutor")
    public ThreadPoolTaskExecutor mongoExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(CollectionConstants.CORE_POOL_SIZE);
        executor.setMaxPoolSize(CollectionConstants.MAX_POOL_SIZE);
        executor.setQueueCapacity(CollectionConstants.QUEUE_CAPACITY);
        executor.setKeepAliveSeconds((int) CollectionConstants.KEEP_ALIVE_TIME);
        executor.setThreadNamePrefix("mongo-thread-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        return executor;
    }

    @Bean(name = "taskExecutor")
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(CollectionConstants.MAX_CONCURRENT_TASKS);
        executor.setMaxPoolSize(CollectionConstants.MAX_CONCURRENT_TASKS);
        executor.setQueueCapacity(CollectionConstants.TASK_QUEUE_CAPACITY);
        executor.setThreadNamePrefix("task-thread-");
        executor.setRejectedExecutionHandler((r, e) -> {
            log.warn("Task queue is full, task rejected");
            throw new RejectedExecutionException("Task queue is full");
        });
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        return executor;
    }

    @Bean
    public ScheduledExecutorService scheduledExecutor() {
        return Executors.newScheduledThreadPool(2, r -> {
            Thread thread = new Thread(r);
            thread.setName("scheduled-thread");
            thread.setDaemon(true);
            return thread;
        });
    }
}
```

## CollectionConstants.java

```java
package com.study.collect.business.testcase.constant;

/**
 * 集合相关常量
 */
public class CollectionConstants {
    // 集合前缀
    public static final String URI_COLLECTION_PREFIX = "uri_collect";

    // 批处理相关
    public static final int DEFAULT_BATCH_SIZE = 200;
    public static final int MAX_BATCH_SIZE = 1000;
    public static final int MIN_BATCH_SIZE = 50;

    // HTTP请求相关
    public static final int HTTP_MAX_REQUESTS_PER_MINUTE = 200;
    public static final int HTTP_CONNECT_TIMEOUT = 5000;
    public static final int HTTP_READ_TIMEOUT = 15000;
    public static final int HTTP_MAX_RETRY = 3;
    public static final long HTTP_RETRY_INTERVAL = 1000L;

    // 线程池相关
    public static final int CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 2;
    public static final int MAX_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 4;
    public static final int QUEUE_CAPACITY = 5000;
    public static final long KEEP_ALIVE_TIME = 60L;

    // MongoDB相关
    public static final int MONGO_BATCH_SIZE = 1000;
    public static final int MONGO_MAX_POOL_SIZE = 100;
    public static final int MONGO_MIN_POOL_SIZE = 20;

    // 对象池相关
    public static final int POOL_MAX_TOTAL = 20;
    public static final int POOL_MAX_IDLE = 10;
    public static final int POOL_MIN_IDLE = 5;

    // 任务相关
    public static final long TASK_TIMEOUT = 3600L;  // 单位：秒
    public static final int MAX_CONCURRENT_TASKS = 10;
    public static final int TASK_QUEUE_CAPACITY = 100;

    // 版本相关
    public static final String VERSION_PREFIX = "V";
    public static final String VERSION_SEPARATOR = "_";

    private CollectionConstants() {
        // 私有构造函数，防止实例化
    }
}
```

## VersionType.java

```java
package com.study.collect.business.testcase.constant;

/**
 * 版本类型枚举
 */
public enum VersionType {
    TRUNK("主干版本"),
    BRANCH("分支版本");

    private final String description;

    VersionType(String description) {
        this.description = description;
    }

    public static VersionType fromString(String version) {
        return version != null && version.toLowerCase().contains("branch") ?
                BRANCH : TRUNK;
    }

    public String getDescription() {
        return description;
    }
}
```

## UriCollectController.java

```java
package com.study.collect.business.testcase.controller;

import com.study.collect.business.testcase.entity.UriEntity;
import com.study.collect.business.testcase.model.param.CollectParam;
import com.study.collect.business.testcase.model.param.DeleteParam;
import com.study.collect.business.testcase.model.param.QueryParam;
import com.study.collect.business.testcase.model.response.AsyncResponse;
import com.study.collect.business.testcase.model.response.TaskResponse;
import com.study.collect.business.testcase.service.UriCollectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/collect")
@RequiredArgsConstructor
@Api(tags = "URI Collection API")
public class UriCollectController {
    private final UriCollectService collectService;

    @PostMapping("/sync")
    @ApiOperation("Start data collection")
    public ResponseEntity<AsyncResponse<String>> collectData(
            @RequestBody @Valid CollectParam param) {
        log.info("Received collect request for rootNode: {}", param.getRootNode());
        return ResponseEntity.ok(collectService.collectData(param));
    }

    @GetMapping("/versions/{rootNode}")
    @ApiOperation("Get versions by rootNode")
    public ResponseEntity<Page<String>> getVersions(
            @PathVariable @NotNull String rootNode,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        return ResponseEntity.ok(collectService.getVersions(rootNode, page, size));
    }

    @GetMapping("/count/{rootNode}/{version}")
    @ApiOperation("Get URI count for version")
    public ResponseEntity<Long> getUriCount(
            @PathVariable @NotNull String rootNode,
            @PathVariable @NotNull String version) {
        return ResponseEntity.ok(collectService.getUriCount(rootNode, version));
    }

    @PostMapping("/delete")
    @ApiOperation("Delete URI data")
    public ResponseEntity<AsyncResponse<Long>> deleteData(
            @RequestBody @Valid DeleteParam param) {
        log.info("Received delete request for {} URIs", param.getUris().size());
        return ResponseEntity.ok(collectService.deleteData(param));
    }

    @GetMapping("/query")
    @ApiOperation("Query URI data with conditions")
    public ResponseEntity<Page<UriEntity>> queryUri(
            @Valid QueryParam param) {
        return ResponseEntity.ok(collectService.queryUri(param));
    }

    @PostMapping("/batch-query")
    @ApiOperation("Batch query URIs")
    public ResponseEntity<List<UriEntity>> batchQueryUri(
            @RequestBody @NotEmpty(message = "URIs cannot be empty") List<String> uris,
            @RequestParam(required = false) Boolean includeDeleted,
            @RequestParam(required = false) Boolean onlyDetail) {
        return ResponseEntity.ok(collectService.batchQueryUri(uris, includeDeleted, onlyDetail));
    }

    @GetMapping("/incremental/{rootNode}")
    @ApiOperation("Query URIs by update time range")
    public ResponseEntity<Page<UriEntity>> queryByUpdateTime(
            @PathVariable @NotNull String rootNode,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        return ResponseEntity.ok(collectService.queryByUpdateTime(rootNode, startTime, endTime, page, size));
    }

    @GetMapping("/task/{taskId}")
    @ApiOperation("Get task status")
    public ResponseEntity<AsyncResponse<Void>> getTaskStatus(
            @PathVariable @NotNull String taskId) {
        return ResponseEntity.ok(collectService.getTaskStatus(taskId));
    }

    @DeleteMapping("/task/{taskId}")
    @ApiOperation("Cancel task")
    public ResponseEntity<Boolean> cancelTask(
            @PathVariable @NotNull String taskId) {
        return ResponseEntity.ok(collectService.cancelTask(taskId));
    }

    @PutMapping("/task/{taskId}/priority/{priority}")
    @ApiOperation("Update task priority")
    public ResponseEntity<Boolean> updateTaskPriority(
            @PathVariable @NotNull String taskId,
            @PathVariable @ApiParam(value = "New priority (higher number = higher priority)") int priority) {
        return ResponseEntity.ok(collectService.updateTaskPriority(taskId, priority));
    }

    @GetMapping("/tasks")
    @ApiOperation("Get all active tasks")
    public ResponseEntity<List<TaskResponse>> getActiveTasks() {
        return ResponseEntity.ok(collectService.getActiveTasks());
    }

    @GetMapping("/stats/{rootNode}")
    @ApiOperation("Get collection statistics")
    public ResponseEntity<Map<String, Object>> getCollectionStats(
            @PathVariable @NotNull String rootNode) {
        return ResponseEntity.ok(collectService.getCollectionStats(rootNode));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        log.error("Error processing request", e);
        return ResponseEntity.internalServerError()
                .body("Error processing request: " + e.getMessage());
    }
}
```

## BaseEntity.java

```java
package com.study.collect.business.testcase.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Field;
import jakarta.persistence.PrePersist;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public abstract class BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    protected String id;

    @CreatedDate
    @Field("create_time")
    protected LocalDateTime createTime;

    @LastModifiedDate
    @Field("update_time")
    protected LocalDateTime updateTime;

    @CreatedBy
    @Field("create_by")
    protected String createBy;

    @LastModifiedBy
    @Field("update_by")
    protected String updateBy;

    @Version
    protected Long version;

    @Field("is_deleted")
    protected Boolean deleted = false;

    protected BaseEntity(String id) {
        this.id = id;
        this.createTime = LocalDateTime.now();
        this.updateTime = this.createTime;
        this.version = 0L;
        this.deleted = false;
    }

    public void reset() {
        this.id = null;
        this.createTime = null;
        this.updateTime = null;
        this.createBy = null;
        this.updateBy = null;
        this.version = 0L;
        this.deleted = false;
    }

    public abstract String getVersion();

    @PrePersist
    public void prePersist() {
        if (this.createTime == null) {
            this.createTime = LocalDateTime.now();
        }
        if (this.updateTime == null) {
            this.updateTime = this.createTime;
        }
        if (this.version == null) {
            this.version = 0L;
        }
        if (this.deleted == null) {
            this.deleted = false;
        }
    }
}
```

## CollectTaskEntity.java

```java
package com.study.collect.business.testcase.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Document(collection = "collect_tasks")
@Data
@EqualsAndHashCode(callSuper = true)
@CompoundIndexes({
        @CompoundIndex(name = "idx_root_version",
                def = "{'root_node': 1, 'version': 1}", unique = true)
})
public class CollectTaskEntity extends BaseEntity {

    @Indexed(unique = true)
    @Field("task_id")
    private String taskId;

    @Field("root_node")
    private String rootNode;

    private String version;

    private String status; // CREATED, PROCESSING, COMPLETED, FAILED, CANCELLED

    private Integer priority;

    private String message;

    @Field("start_time")
    private LocalDateTime startTime;

    @Field("end_time")
    private LocalDateTime endTime;

    @Field("total_uris")
    private Long totalUris = 0L;

    @Field("processed_uris")
    private Long processedUris = 0L;

    @Field("failed_uris")
    private Long failedUris = 0L;

    @Field("failed_uri_list")
    private List<String> failedUriList = new ArrayList<>();

    @Field("error_details")
    private Map<String, String> errorDetails = new HashMap<>();

    private Double progress = 0.0;

    @Field("is_incremental")
    private Boolean isIncremental = false;

    @Field("increment_start_time")
    private LocalDateTime incrementStartTime;

    @Field("increment_end_time")
    private LocalDateTime incrementEndTime;

    @Field("retry_count")
    private Integer retryCount = 0;

    @Field("last_retry_time")
    private LocalDateTime lastRetryTime;

    public void addFailedUri(String uri, String error) {
        if (failedUriList == null) {
            failedUriList = new ArrayList<>();
        }
        failedUriList.add(uri);

        if (errorDetails == null) {
            errorDetails = new HashMap<>();
        }
        errorDetails.put(uri, error);

        failedUris = (failedUris == null ? 0L : failedUris) + 1;
    }

    public void incrementProcessedCount() {
        processedUris = (processedUris == null ? 0L : processedUris) + 1;
        updateProgress();
    }

    public void updateProgress() {
        if (totalUris != null && totalUris > 0) {
            progress = (double) processedUris / totalUris * 100;
        }
    }

    public boolean canRetry() {
        return retryCount < 3;
    }

    public void incrementRetryCount() {
        retryCount = (retryCount == null ? 0 : retryCount) + 1;
        lastRetryTime = LocalDateTime.now();
    }

    @Override
    public String getVersion() {
        return this.version;
    }

    @Override
    public void reset() {
        super.reset();
        this.taskId = null;
        this.rootNode = null;
        this.version = null;
        this.status = null;
        this.priority = null;
        this.message = null;
        this.startTime = null;
        this.endTime = null;
        this.totalUris = 0L;
        this.processedUris = 0L;
        this.failedUris = 0L;
        this.failedUriList = new ArrayList<>();
        this.errorDetails = new HashMap<>();
        this.progress = 0.0;
        this.isIncremental = false;
        this.incrementStartTime = null;
        this.incrementEndTime = null;
        this.retryCount = 0;
        this.lastRetryTime = null;
    }
}
```

## UriEntity.java

```java
package com.study.collect.business.testcase.entity;

import com.study.collect.business.testcase.utils.HashUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.persistence.PrePersist;
import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "#{@collectionStrategy.getCollectionName('uri_collect')}")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@CompoundIndexes({
        @CompoundIndex(
                name = "uri_unique_idx",
                def = "{'uri': 1, 'root_node': 1, 'version_type': 1, 'uri_version': 1}",
                unique = true,
                background = true
        ),
        @CompoundIndex(
                name = "uri_hash_idx",
                def = "{'uri_hash': 1}",
                unique = true,
                background = true
        ),
        @CompoundIndex(
                name = "query_idx",
                def = "{'root_node': 1, 'version_type': 1, 'uri_version': 1, 'is_deleted': 1}",
                background = true
        ),
        @CompoundIndex(name = "idx_update_time", def = "{'third_party_update_time': -1}", background = true)
})
public class UriEntity extends BaseEntity {

    @Indexed(unique = true, background = true)
    @Field("uri_hash")
    private String uriHash;

    @Indexed(background = true)
    private String uri;

    @Field("root_node")
    private String rootNode;

    @Field("version_type")
    private String versionType;

    @Field("uri_version")
    private String uriVersion;

    @Field("third_party_update_time")
    private LocalDateTime thirdPartyUpdateTime;

    @Field("real_uri")
    private String realUri;

    private String number;
    private String name;
    private Map<String, Object> details;

    public UriEntity(String uri, String rootNode) {
        super(generateId(uri));
        this.uri = uri;
        this.uriHash = generateUriHash(uri);
        this.rootNode = rootNode;
    }

    private static String generateId(String uri) {
        return HashUtil.hash(uri);
    }

    private static String generateUriHash(String uri) {
        return HashUtil.hash(uri);
    }

    @PrePersist
    @Override
    public void prePersist() {
        super.prePersist();
        if (this.uriHash == null && this.uri != null) {
            this.uriHash = generateUriHash(this.uri);
        }
    }

    @Override
    public String getVersion() {
        return this.uriVersion;
    }

    @Override
    public void reset() {
        super.reset();
        this.uri = null;
        this.uriHash = null;
        this.rootNode = null;
        this.versionType = null;
        this.uriVersion = null;
        this.realUri = null;
        this.number = null;
        this.name = null;
        this.details = null;
        this.thirdPartyUpdateTime = null;
    }

    public UriEntity buildFrom(String uri, String rootNode) {
        this.uri = uri;
        this.uriHash = generateUriHash(uri);
        this.rootNode = rootNode;
        this.id = generateId(uri);
        return this;
    }
}
```

## VersionEntity.java

```java
package com.study.collect.business.testcase.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Document(collection = "versions")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@CompoundIndexes({
        @CompoundIndex(name = "idx_root_version",
                def = "{'root_node': 1, 'version': 1}", unique = true)
})
public class VersionEntity extends BaseEntity {

    @Field("version")
    protected String version;

    @Field("root_node")
    protected String rootNode;

    @Field("version_type")
    protected String versionType;

    protected String name;

    protected String description;

    @Field("version_code")
    protected String versionCode;

    @Field("version_time")
    protected LocalDateTime versionTime;

    protected Integer sort;

    @Override
    public String getVersion() {
        return this.version;
    }

    @Override
    public void reset() {
        super.reset();
        this.version = null;
        this.rootNode = null;
        this.versionType = null;
        this.name = null;
        this.description = null;
        this.versionCode = null;
        this.versionTime = null;
        this.sort = null;
    }

    public void initVersion() {
        this.version = "0";
        this.versionCode = generateVersionCode();
        this.versionTime = LocalDateTime.now();
    }

    public void upgradeVersion() {
        int currentVersion = Integer.parseInt(this.version);
        this.version = String.valueOf(currentVersion + 1);
        this.versionCode = generateVersionCode();
        this.versionTime = LocalDateTime.now();
    }

    private String generateVersionCode() {
        return String.format("V%s_%s",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")),
                this.version);
    }
}
```

## CollectTaskManager.java

```java
package com.study.collect.business.testcase.manager;

import com.study.collect.business.testcase.constant.CollectionConstants;
import com.study.collect.business.testcase.model.response.TaskResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CollectTaskManager {
    private final ConcurrentHashMap<String, TaskResponse> taskMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, ScheduledFuture<?>> timeoutTasks = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduledExecutor;

    public CollectTaskManager(ScheduledExecutorService scheduledExecutor) {
        this.scheduledExecutor = scheduledExecutor;
        // 启动定期清理任务
        this.scheduledExecutor.scheduleAtFixedRate(
                this::cleanupTasks,
                1,
                1,
                TimeUnit.HOURS
        );
    }

    /**
     * 创建新任务
     */
    public TaskResponse createTask(String type, Map<String, Object> params, Integer priority) {
        String taskId = generateTaskId();
        TaskResponse task = TaskResponse.builder()
                .taskId(taskId)
                .type(type)
                .status("CREATED")
                .progress(0.0)
                .priority(priority != null ? priority : 0)
                .createTime(LocalDateTime.now())
                .params(params)
                .build();

        taskMap.put(taskId, task);
        scheduleTimeout(taskId);

        return task;
    }

    /**
     * 更新任务状态
     */
    public void updateTaskStatus(String taskId, String status, String message) {
        TaskResponse task = taskMap.get(taskId);
        if (task != null) {
            task.setStatus(status);
            task.setMessage(message);
            if ("COMPLETED".equals(status) || "ERROR".equals(status)) {
                task.setEndTime(LocalDateTime.now());
                cancelTimeout(taskId);
            }
        }
    }

    /**
     * 更新任务进度
     */
    public void updateTaskProgress(String taskId, long processed, long total) {
        TaskResponse task = taskMap.get(taskId);
        if (task != null) {
            task.updateProgress(processed, total);
        }
    }

    /**
     * 获取任务状态
     */
    public TaskResponse getTaskStatus(String taskId) {
        return taskMap.get(taskId);
    }

    /**
     * 调整任务优先级
     */
    public boolean updateTaskPriority(String taskId, int newPriority) {
        TaskResponse task = taskMap.get(taskId);
        if (task != null && "CREATED".equals(task.getStatus())) {
            task.setPriority(newPriority);
            return true;
        }
        return false;
    }

    /**
     * 获取所有活动任务
     */
    public List<TaskResponse> getActiveTasks() {
        return taskMap.values().stream()
                .filter(task -> !"COMPLETED".equals(task.getStatus())
                        && !"ERROR".equals(task.getStatus()))
                .sorted(Comparator.comparing(TaskResponse::getPriority).reversed())
                .collect(Collectors.toList());
    }

    /**
     * 取消任务
     */
    public boolean cancelTask(String taskId) {
        TaskResponse task = taskMap.get(taskId);
        if (task != null && "CREATED".equals(task.getStatus())) {
            task.setStatus("CANCELLED");
            task.setEndTime(LocalDateTime.now());
            cancelTimeout(taskId);
            return true;
        }
        return false;
    }

    private String generateTaskId() {
        return UUID.randomUUID().toString();
    }

    private void scheduleTimeout(String taskId) {
        ScheduledFuture<?> timeoutTask = scheduledExecutor.schedule(() -> {
            TaskResponse task = taskMap.get(taskId);
            if (task != null && !"COMPLETED".equals(task.getStatus())
                    && !"ERROR".equals(task.getStatus())) {
                task.setStatus("TIMEOUT");
                task.setEndTime(LocalDateTime.now());
                task.setMessage("Task timeout after " + CollectionConstants.TASK_TIMEOUT + " seconds");
            }
        }, CollectionConstants.TASK_TIMEOUT, TimeUnit.SECONDS);

        timeoutTasks.put(taskId, timeoutTask);
    }

    private void cancelTimeout(String taskId) {
        ScheduledFuture<?> timeoutTask = timeoutTasks.remove(taskId);
        if (timeoutTask != null) {
            timeoutTask.cancel(false);
        }
    }

    private void cleanupTasks() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(7);
        taskMap.entrySet().removeIf(entry -> {
            TaskResponse task = entry.getValue();
            return task.getEndTime() != null && task.getEndTime().isBefore(cutoff);
        });
    }

    @PreDestroy
    public void shutdown() {
        timeoutTasks.values().forEach(task -> task.cancel(true));
        timeoutTasks.clear();
        taskMap.clear();
    }
}
```

## QueueManager.java

```java
package com.study.collect.business.testcase.manager;

import com.study.collect.business.testcase.constant.CollectionConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.Comparator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Slf4j
@Component
public class QueueManager<T> {
    private final PriorityBlockingQueue<QueueItem<T>> queue;
    private final ConcurrentHashMap<String, QueueItem<T>> itemMap;
    private final ThreadPoolTaskExecutor executor;
    private volatile boolean running = true;


    public QueueManager(ThreadPoolTaskExecutor taskExecutor) {
        this.queue = new PriorityBlockingQueue<>(
                CollectionConstants.TASK_QUEUE_CAPACITY,
                Comparator.comparing(QueueItem<T>::getPriority).reversed()
        );
        this.itemMap = new ConcurrentHashMap<>();
        this.executor = taskExecutor;

        // 启动队列处理线程
        startQueueProcessor();
    }

    /**
     * 添加任务到队列
     */
    public CompletableFuture<Void> enqueue(String id, T item, int priority, Consumer<T> processor) {
        QueueItem<T> queueItem = new QueueItem<>(id, item, priority, processor);
        if (itemMap.putIfAbsent(id, queueItem) != null) {
            throw new IllegalStateException("Item with id " + id + " already exists in queue");
        }
        queue.offer(queueItem);
        return queueItem.future;
    }

    /**
     * 更新任务优先级
     */
    public boolean updatePriority(String id, int newPriority) {
        QueueItem<T> item = itemMap.get(id);
        if (item != null) {
            // 创建新的队列项并重新入队
            QueueItem<T> newItem = new QueueItem<>(id, item.item, newPriority, item.processor);
            if (queue.remove(item)) {
                queue.offer(newItem);
                itemMap.put(id, newItem);
                // 传递future的结果
                item.future.whenComplete((v, e) -> {
                    if (e != null) {
                        newItem.future.completeExceptionally(e);
                    } else {
                        newItem.future.complete(null);
                    }
                });
                return true;
            }
        }
        return false;
    }

    /**
     * 取消任务
     */
    public boolean cancel(String id) {
        QueueItem<T> item = itemMap.remove(id);
        if (item != null) {
            queue.remove(item);
            item.future.cancel(true);
            return true;
        }
        return false;
    }

    /**
     * 获取队列大小
     */
    public int getQueueSize() {
        return queue.size();
    }

    private void startQueueProcessor() {
        executor.execute(() -> {
            while (running) {
                try {
                    QueueItem<T> item = queue.poll(1, TimeUnit.SECONDS);
                    if (item != null) {
                        processItem(item);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    log.error("Error processing queue item", e);
                }
            }
        });
    }

    private void processItem(QueueItem<T> item) {
        try {
            item.processor.accept(item.item);
            item.future.complete(null);
        } catch (Exception e) {
            item.future.completeExceptionally(e);
        } finally {
            itemMap.remove(item.id);
        }
    }

    @PreDestroy
    public void shutdown() {
        running = false;
        // 取消所有未完成的任务
        itemMap.values().forEach(item -> item.future.cancel(true));
        queue.clear();
        itemMap.clear();
    }

    /**
     * 获取任务状态
     */
    public boolean isQueued(String id) {
        return itemMap.containsKey(id);
    }

    private static class QueueItem<T> {
        final String id;
        final T item;
        final CompletableFuture<Void> future;
        final Consumer<T> processor;
        volatile int priority;

        QueueItem(String id, T item, int priority, Consumer<T> processor) {
            this.id = id;
            this.item = item;
            this.priority = priority;
            this.processor = processor;
            this.future = new CompletableFuture<>();
        }

        int getPriority() {
            return priority;
        }
    }
}
```

## PageResult.java

```java
package com.study.collect.business.testcase.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PageResult<T> {
    private long total;       // 总记录数
    private int page;         // 当前页码
    private int size;         // 每页大小
    private int totalPages;   // 总页数
    private List<T> items;    // 当前页数据
}
```

## UriQueryCondition.java

```java
package com.study.collect.business.testcase.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UriQueryCondition {
    private String rootNode;
    private String version;
    private LocalDateTime thirdPartyUpdateTimeStart;
    private LocalDateTime thirdPartyUpdateTimeEnd;
    private Boolean isDeleted;
    private boolean onlyDetail;

    @Builder.Default
    private int page = 1;

    @Builder.Default
    private int size = 20;

    public Pageable getPageable() {
        return PageRequest.of(page - 1, size);
    }
}
```

## CollectParam.java

```java
package com.study.collect.business.testcase.model.param;

import com.study.collect.business.testcase.constant.CollectionConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Validated
public class CollectParam {

    @NotBlank(message = "rootNode cannot be empty")
    private String rootNode;

    private String version;

    @NotBlank(message = "serverUrl cannot be empty")
    private String serverUrl;

    @Builder.Default
    private Boolean incremental = false;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @Min(value = 50, message = "batchSize must be greater than or equal to 50")
    @Max(value = 1000, message = "batchSize must be less than or equal to 1000")
    @Builder.Default
    private Integer batchSize = CollectionConstants.DEFAULT_BATCH_SIZE;

    @Builder.Default
    private Integer priority = 0;

    @Builder.Default
    private Boolean allowDuplicate = false;

    @Builder.Default
    private Integer maxRetries = CollectionConstants.HTTP_MAX_RETRY;

    @Builder.Default
    private Integer timeout = 3600;

    @Builder.Default
    private Boolean hardDelete = false;

    private String taskId;

    @Builder.Default
    private Boolean forceUpdate = false;

    // 验证增量采集参数
    public void validateIncrementalParams() {
        if (Boolean.TRUE.equals(incremental) && startTime == null) {
            throw new IllegalArgumentException("startTime is required for incremental collection");
        }
        if (startTime != null && endTime != null && !startTime.isBefore(endTime)) {
            throw new IllegalArgumentException("startTime must be before endTime");
        }
    }

    // 验证版本号格式
    public void validateVersion() {
        if (version != null && !version.matches("^[\\w.-]+$")) {
            throw new IllegalArgumentException("Invalid version format");
        }
    }

    // 构建复制
    public CollectParam copy() {
        return CollectParam.builder()
                .rootNode(this.rootNode)
                .version(this.version)
                .serverUrl(this.serverUrl)
                .incremental(this.incremental)
                .startTime(this.startTime)
                .endTime(this.endTime)
                .batchSize(this.batchSize)
                .priority(this.priority)
                .allowDuplicate(this.allowDuplicate)
                .maxRetries(this.maxRetries)
                .timeout(this.timeout)
                .hardDelete(this.hardDelete)
                .forceUpdate(this.forceUpdate)
                .build();
    }
}
```

## DeleteParam.java

```java
package com.study.collect.business.testcase.model.param;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@Validated
public class DeleteParam {
    private String rootNode;

    @NotEmpty(message = "uris cannot be empty")
    private List<String> uris;

    private String version;

    private Boolean hardDelete = false;

    private Boolean async = false;

    private Integer priority = 0;

    private Integer batchSize;

    private String taskId;
}
```

## PageParam.java

```java
package com.study.collect.business.testcase.model.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageParam {
    @Min(value = 1, message = "page must be greater than 0")
    private int page = 1;

    @Min(value = 1, message = "size must be greater than 0")
    @Max(value = 1000, message = "size must be less than 1000")
    private int size = 20;
}
```

## QueryParam.java

```java
package com.study.collect.business.testcase.model.param;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Data
@Validated
public class QueryParam {
    private String rootNode;

    private List<String> uris;

    private String version;

    private String versionType;

    private Boolean includeDeleted = false;

    private Boolean onlyDeleted = false;

    private Integer page = 1;

    private Integer size = 20;

    private Boolean async = false;

    private String taskId;
}
```

## AsyncResponse.java

```java
package com.study.collect.business.testcase.model.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class AsyncResponse<T> extends BaseResponse {
    private String taskId;
    private Double progress;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private T result;

    private AsyncResponse() {
        super();
    }

    private AsyncResponse(String code, String message, String status,
                          String taskId, Double progress, LocalDateTime startTime,
                          LocalDateTime endTime, T result) {
        super(code, message, status);
        this.taskId = taskId;
        this.progress = progress;
        this.startTime = startTime;
        this.endTime = endTime;
        this.result = result;
    }

    public static <T> AsyncResponseBuilder<T> asyncBuilder() {
        return new AsyncResponseBuilder<>();
    }

    public static <T> AsyncResponse<T> processing(String taskId) {
        return AsyncResponse.<T>asyncBuilder()
                .taskId(taskId)
                .status("PROCESSING")
                .progress(0.0)
                .startTime(LocalDateTime.now())
                .build();
    }

    public static <T> AsyncResponse<T> success(String taskId, T result) {
        return AsyncResponse.<T>asyncBuilder()
                .taskId(taskId)
                .status("COMPLETED")
                .progress(100.0)
                .result(result)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now())
                .build();
    }

    public static <T> AsyncResponse<T> error(String taskId, String message) {
        return AsyncResponse.<T>asyncBuilder()
                .taskId(taskId)
                .status("ERROR")
                .message(message)
                .endTime(LocalDateTime.now())
                .build();
    }

    public static class AsyncResponseBuilder<T> {
        private String code;
        private String message;
        private String status;
        private String taskId;
        private Double progress;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private T result;

        public AsyncResponseBuilder<T> code(String code) {
            this.code = code;
            return this;
        }

        public AsyncResponseBuilder<T> message(String message) {
            this.message = message;
            return this;
        }

        public AsyncResponseBuilder<T> status(String status) {
            this.status = status;
            return this;
        }

        public AsyncResponseBuilder<T> taskId(String taskId) {
            this.taskId = taskId;
            return this;
        }

        public AsyncResponseBuilder<T> progress(Double progress) {
            this.progress = progress;
            return this;
        }

        public AsyncResponseBuilder<T> startTime(LocalDateTime startTime) {
            this.startTime = startTime;
            return this;
        }

        public AsyncResponseBuilder<T> endTime(LocalDateTime endTime) {
            this.endTime = endTime;
            return this;
        }

        public AsyncResponseBuilder<T> result(T result) {
            this.result = result;
            return this;
        }

        public AsyncResponse<T> build() {
            return new AsyncResponse<>(code, message, status, taskId, progress, startTime, endTime, result);
        }
    }
}
```

## BaseResponse.java

```java
package com.study.collect.business.testcase.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse {
    private String code;
    private String message;
    private String status;

    public static BaseResponseBuilder builder() {
        return new BaseResponseBuilder();
    }

    public static class BaseResponseBuilder {
        protected String code;
        protected String message;
        protected String status;

        public BaseResponseBuilder code(String code) {
            this.code = code;
            return this;
        }

        public BaseResponseBuilder message(String message) {
            this.message = message;
            return this;
        }

        public BaseResponseBuilder status(String status) {
            this.status = status;
            return this;
        }

        public BaseResponse build() {
            return new BaseResponse(code, message, status);
        }
    }
}
```

## PageResponse.java

```java
package com.study.collect.business.testcase.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    private String code;
    private String message;
    private Long total;
    private List<T> items;
    private int page;
    private int size;
    private int totalPages;

    public static <T> PageResponseBuilder<T> builder() {
        return new PageResponseBuilder<>();
    }

    public static class PageResponseBuilder<T> {
        private String code;
        private String message;
        private Long total;
        private List<T> items;
        private int page;
        private int size;
        private int totalPages;

        PageResponseBuilder() {
        }

        public PageResponseBuilder<T> code(String code) {
            this.code = code;
            return this;
        }

        public PageResponseBuilder<T> message(String message) {
            this.message = message;
            return this;
        }

        public PageResponseBuilder<T> total(Long total) {
            this.total = total;
            return this;
        }

        public PageResponseBuilder<T> items(List<T> items) {
            this.items = items;
            return this;
        }

        public PageResponseBuilder<T> page(int page) {
            this.page = page;
            return this;
        }

        public PageResponseBuilder<T> size(int size) {
            this.size = size;
            return this;
        }

        public PageResponseBuilder<T> totalPages(int totalPages) {
            this.totalPages = totalPages;
            return this;
        }

        public PageResponse<T> build() {
            return new PageResponse<>(code, message, total, items, page, size, totalPages);
        }
    }
}
```

## TaskResponse.java

```java
package com.study.collect.business.testcase.model.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class TaskResponse {
    private String taskId;
    private String type;
    private String status;
    private Double progress;
    private String message;
    private Integer priority;
    private LocalDateTime createTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long totalCount;
    private Long processedCount;
    private Long failedCount;
    private Map<String, Object> details;
    private Map<String, Object> params;

    public static TaskResponse create(String taskId, String type, Map<String, Object> params) {
        return TaskResponse.builder()
                .taskId(taskId)
                .type(type)
                .status("CREATED")
                .progress(0.0)
                .createTime(LocalDateTime.now())
                .params(params)
                .build();
    }

    public void updateProgress(long processed, long total) {
        this.processedCount = processed;
        this.totalCount = total;
        this.progress = total > 0 ? (processed * 100.0) / total : 0.0;
    }
}
```

## UriDetail.java

```java
package com.study.collect.business.testcase.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UriDetail {
    private String uri;
    private String realUri;
    private String number;
    private String name;
    private String version;
    private LocalDateTime updateTime;
    private Map<String, Object> details;
}
```

## VersionInfo.java

```java
package com.study.collect.business.testcase.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VersionInfo {
    private String version;
    private String name;
    private String type;  // TRUNK/BRANCH
    private String description;
    private LocalDateTime updateTime;
    private Integer sort;
    private String status;
}
```

## VersionResponse.java

```java
package com.study.collect.business.testcase.model.response;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

//
@Data
@NoArgsConstructor
public class VersionResponse {
    private String code;            // 响应码
    private String message;         // 响应消息
    private String version;         // 版本号
    private String versionType;     // 版本类型 (TRUNK/BRANCH)
    private String description;     // 版本描述
    private LocalDateTime createTime;  // 创建时间
    private LocalDateTime updateTime;  // 更新时间
    private String status;          // 版本状态
    private Integer sort;           // 排序号

    public static VersionResponseBuilder builder() {
        return new VersionResponseBuilder();
    }

    public static class VersionResponseBuilder {
        private String code;
        private String message;
        private String version;
        private String versionType;
        private String description;
        private LocalDateTime createTime;
        private LocalDateTime updateTime;
        private String status;
        private Integer sort;

        VersionResponseBuilder() {
        }

        public VersionResponseBuilder code(String code) {
            this.code = code;
            return this;
        }

        public VersionResponseBuilder message(String message) {
            this.message = message;
            return this;
        }

        public VersionResponseBuilder version(String version) {
            this.version = version;
            return this;
        }

        public VersionResponseBuilder versionType(String versionType) {
            this.versionType = versionType;
            return this;
        }

        public VersionResponseBuilder description(String description) {
            this.description = description;
            return this;
        }

        public VersionResponseBuilder createTime(LocalDateTime createTime) {
            this.createTime = createTime;
            return this;
        }

        public VersionResponseBuilder updateTime(LocalDateTime updateTime) {
            this.updateTime = updateTime;
            return this;
        }

        public VersionResponseBuilder status(String status) {
            this.status = status;
            return this;
        }

        public VersionResponseBuilder sort(Integer sort) {
            this.sort = sort;
            return this;
        }

        public VersionResponse build() {
            VersionResponse response = new VersionResponse();
            response.setCode(code);
            response.setMessage(message);
            response.setVersion(version);
            response.setVersionType(versionType);
            response.setDescription(description);
            response.setCreateTime(createTime);
            response.setUpdateTime(updateTime);
            response.setStatus(status);
            response.setSort(sort);
            return response;
        }

        public String toString() {
            return "VersionResponse.VersionResponseBuilder(code=" + this.code +
                    ", message=" + this.message +
                    ", version=" + this.version +
                    ", versionType=" + this.versionType +
                    ", description=" + this.description +
                    ", createTime=" + this.createTime +
                    ", updateTime=" + this.updateTime +
                    ", status=" + this.status +
                    ", sort=" + this.sort + ")";
        }
    }
}
```

## HttpResponseParser.java

```java
package com.study.collect.business.testcase.model.response.parse;

import java.io.IOException;

/**
 * HTTP响应解析器接口
 */
public interface HttpResponseParser<T> {

    /**
     * 解析HTTP响应
     *
     * @param response 响应字符串
     * @return 解析后的结果
     * @throws IOException 解析异常
     */
    T parse(String response) throws IOException;

    /**
     * 从错误响应中提取错误信息
     *
     * @param errorResponse 错误响应
     * @return 错误信息
     */
    default String parseError(String errorResponse) {
        try {
            return errorResponse;
        } catch (Exception e) {
            return "Failed to parse error response";
        }
    }
}
```

## UriCountResponseParser.java

```java
package com.study.collect.business.testcase.model.response.parse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class UriCountResponseParser implements HttpResponseParser<Integer> {

    private final ObjectMapper objectMapper;

    @Override
    public Integer parse(String response) throws IOException {
        try {
            JsonNode root = objectMapper.readTree(response);
            return root.path("result").path("value").asInt();
        } catch (Exception e) {
            log.error("Failed to parse URI count response: {}", response, e);
            throw new IOException("Failed to parse URI count response", e);
        }
    }
}
```

## UriDetailResponseParser.java

```java
package com.study.collect.business.testcase.model.response.parse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.collect.business.testcase.model.response.UriDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UriDetailResponseParser implements HttpResponseParser<List<UriDetail>> {

    private final ObjectMapper objectMapper;

    @Override
    public List<UriDetail> parse(String response) throws IOException {
        try {
            JsonNode root = objectMapper.readTree(response);
            List<UriDetail> details = new ArrayList<>();

            root.path("result").path("value").forEach(detail -> {
                details.add(UriDetail.builder()
                        .uri(detail.path("uri").asText())
                        .realUri(detail.path("realUri").asText())
                        .number(detail.path("number").asText())
                        .name(detail.path("name").asText())
                        .updateTime(parseDateTime(detail.path("updateTime").asText()))
                        .build());
            });

            return details;
        } catch (Exception e) {
            log.error("Failed to parse URI details response: {}", response, e);
            throw new IOException("Failed to parse URI details response", e);
        }
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {
        try {
            return LocalDateTime.parse(dateTimeStr);
        } catch (Exception e) {
            log.warn("Failed to parse datetime: {}", dateTimeStr);
            return null;
        }
    }
}
```

## UriListResponseParser.java

```java
package com.study.collect.business.testcase.model.response.parse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UriListResponseParser implements HttpResponseParser<List<String>> {

    private final ObjectMapper objectMapper;

    @Override
    public List<String> parse(String response) throws IOException {
        try {
            JsonNode root = objectMapper.readTree(response);
            List<String> uris = new ArrayList<>();

            root.path("result").path("value").forEach(uri ->
                    uris.add(uri.asText())
            );

            return uris;
        } catch (Exception e) {
            log.error("Failed to parse URI list response: {}", response, e);
            throw new IOException("Failed to parse URI list response", e);
        }
    }
}
```

## VersionResponseParser.java

```java
package com.study.collect.business.testcase.model.response.parse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.collect.business.testcase.model.response.PageResponse;
import com.study.collect.business.testcase.model.response.VersionInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class VersionResponseParser implements HttpResponseParser<PageResponse<VersionInfo>> {

    private final ObjectMapper objectMapper;

    @Override
    public PageResponse<VersionInfo> parse(String response) throws IOException {
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode value = root.path("result").path("value");

            List<VersionInfo> versions = new ArrayList<>();
            // 解析嵌套的children结构
            value.path("children").forEach(child -> {
                if ("children".equals(child.path("elementName").asText())) {
                    child.path("children").forEach(version -> {
                        versions.add(parseVersionInfo(version));
                    });
                }
            });

            return PageResponse.<VersionInfo>builder()
                    .items(versions)
                    .build();
        } catch (Exception e) {
            log.error("Failed to parse version response: {}", response, e);
            throw new IOException("Failed to parse version response", e);
        }
    }

    private VersionInfo parseVersionInfo(JsonNode node) {
        return VersionInfo.builder()
                .version(node.path("version").asText())
                .name(node.path("name").asText())
                .type(node.path("type").asText())
                .updateTime(parseDateTime(node.path("updateTime").asText()))
                .build();
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {
        try {
            return LocalDateTime.parse(dateTimeStr);
        } catch (Exception e) {
            log.warn("Failed to parse datetime: {}", dateTimeStr);
            return null;
        }
    }
}
```

## CollectTaskRepository.java

```java
package com.study.collect.business.testcase.repository;

import com.study.collect.business.testcase.entity.CollectTaskEntity;
import com.study.collect.business.testcase.utils.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Repository
public class CollectTaskRepository {

    private static final String COLLECTION_NAME = "collect_tasks";
    private final MongoTemplate mongoTemplate;
    private final RateLimiter mongoRateLimiter;

    public CollectTaskRepository(MongoTemplate mongoTemplate, RateLimiter mongoRateLimiter) {
        this.mongoTemplate = mongoTemplate;
        this.mongoRateLimiter = mongoRateLimiter;
    }

    /**
     * 保存任务
     */
    public CollectTaskEntity save(CollectTaskEntity task) {
        try {
            mongoRateLimiter.acquire();
            return mongoTemplate.save(task, COLLECTION_NAME);
        } catch (Exception e) {
            log.error("Failed to save task: {}", task.getTaskId(), e);
            throw new RuntimeException("Failed to save task", e);
        }
    }

    /**
     * 根据任务ID查询
     */
    public CollectTaskEntity findByTaskId(String taskId) {
        try {
            mongoRateLimiter.acquire();
            Query query = Query.query(Criteria.where("task_id").is(taskId));
            return mongoTemplate.findOne(query, CollectTaskEntity.class, COLLECTION_NAME);
        } catch (Exception e) {
            log.error("Failed to find task: {}", taskId, e);
            throw new RuntimeException("Failed to find task", e);
        }
    }

    /**
     * 查询活跃任务
     */
    public List<CollectTaskEntity> findActiveTasks() {
        try {
            mongoRateLimiter.acquire();
            Query query = Query.query(
                    Criteria.where("status").in("CREATED", "PROCESSING")
            );
            return mongoTemplate.find(query, CollectTaskEntity.class, COLLECTION_NAME);
        } catch (Exception e) {
            log.error("Failed to find active tasks", e);
            throw new RuntimeException("Failed to find active tasks", e);
        }
    }

    /**
     * 查询根节点的最后采集时间
     */
    public LocalDateTime findLastCollectTime(String rootNode) {
        try {
            mongoRateLimiter.acquire();
            Query query = Query.query(
                            Criteria.where("root_node").is(rootNode)
                                    .and("status").is("COMPLETED")
                    )
                    .with(Sort.by(Sort.Direction.DESC, "end_time"))
                    .limit(1);

            CollectTaskEntity task = mongoTemplate.findOne(query, CollectTaskEntity.class, COLLECTION_NAME);
            return task != null ? task.getEndTime() : null;
        } catch (Exception e) {
            log.error("Failed to find last collect time for rootNode: {}", rootNode, e);
            throw new RuntimeException("Failed to find last collect time", e);
        }
    }

    /**
     * 查询失败的任务
     */
    public List<CollectTaskEntity> findFailedTasks(LocalDateTime before) {
        try {
            mongoRateLimiter.acquire();
            Query query = Query.query(
                    Criteria.where("status").is("FAILED")
                            .and("end_time").lt(before)
                            .and("retry_count").lt(3)
            );
            return mongoTemplate.find(query, CollectTaskEntity.class, COLLECTION_NAME);
        } catch (Exception e) {
            log.error("Failed to find failed tasks before: {}", before, e);
            throw new RuntimeException("Failed to find failed tasks", e);
        }
    }

    /**
     * 查询超时任务
     */
    public List<CollectTaskEntity> findTimeoutTasks(LocalDateTime timeoutThreshold) {
        try {
            mongoRateLimiter.acquire();
            Query query = Query.query(
                    Criteria.where("status").is("PROCESSING")
                            .and("start_time").lt(timeoutThreshold)
            );
            return mongoTemplate.find(query, CollectTaskEntity.class, COLLECTION_NAME);
        } catch (Exception e) {
            log.error("Failed to find timeout tasks before: {}", timeoutThreshold, e);
            throw new RuntimeException("Failed to find timeout tasks", e);
        }
    }

    /**
     * 清理历史任务
     */
    public void deleteHistoryTasks(LocalDateTime before) {
        try {
            mongoRateLimiter.acquire();
            Query query = Query.query(
                    Criteria.where("end_time").lt(before)
                            .and("status").in("COMPLETED", "FAILED", "CANCELLED")
            );
            mongoTemplate.remove(query, CollectTaskEntity.class, COLLECTION_NAME);
        } catch (Exception e) {
            log.error("Failed to delete history tasks before: {}", before, e);
            throw new RuntimeException("Failed to delete history tasks", e);
        }
    }
}
```

## UriRepository.java

```java
package com.study.collect.business.testcase.repository;

import com.google.common.collect.Lists;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.WriteModel;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.study.collect.business.testcase.config.DynamicCollectionIndexConfiguration;
import com.study.collect.business.testcase.constant.CollectionConstants;
import com.study.collect.business.testcase.entity.UriEntity;
import com.study.collect.business.testcase.model.UriQueryCondition;
import com.study.collect.business.testcase.model.param.QueryParam;
import com.study.collect.business.testcase.utils.HashUtil;
import com.study.collect.business.testcase.utils.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class UriRepository {
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int HTTP_BATCH_SIZE = 100;
    private final MongoTemplate mongoTemplate;
    private final DynamicCollectionIndexConfiguration indexConfiguration;
    private final RateLimiter mongoRateLimiter;

    public UriRepository(MongoTemplate mongoTemplate,
                         DynamicCollectionIndexConfiguration indexConfiguration,
                         RateLimiter mongoRateLimiter) {
        this.mongoTemplate = mongoTemplate;
        this.indexConfiguration = indexConfiguration;
        this.mongoRateLimiter = mongoRateLimiter;
    }

    /**
     * 查询指定版本的URI列表
     */
    public Page<String> findUrisByVersion(String rootNode, String version, Pageable pageable) {
        try {
            mongoRateLimiter.acquire();

            Query query = new Query(Criteria.where("root_node").is(rootNode)
                    .and("uri_version").is(version)
                    .and("is_deleted").is(false));
            query.with(pageable);
            query.fields().include("uri");

            long total = mongoTemplate.count(query, UriEntity.class, getCollectionName(rootNode));
            List<UriEntity> entities = mongoTemplate.find(query, UriEntity.class, getCollectionName(rootNode));

            List<String> uris = entities.stream()
                    .map(UriEntity::getUri)
                    .collect(Collectors.toList());

            return new PageImpl<>(uris, pageable, total);
        } catch (Exception e) {
            log.error("Failed to find URIs by version for rootNode: {}, version: {}", rootNode, version, e);
            throw new RuntimeException("Failed to find URIs by version", e);
        }
    }

    /**
     * 统计指定根节点和版本的URI数量
     */
    public long countByRootNodeAndVersion(String rootNode, String version) {
        try {
            mongoRateLimiter.acquire();
            Query query = new Query(Criteria.where("root_node").is(rootNode)
                    .and("uri_version").is(version)
                    .and("is_deleted").is(false));
            return mongoTemplate.count(query, UriEntity.class, getCollectionName(rootNode));
        } catch (Exception e) {
            log.error("Failed to count URIs for rootNode: {} and version: {}", rootNode, version, e);
            throw new RuntimeException("Failed to count URIs", e);
        }
    }

    /**
     * 条件查询
     */
    public Page<UriEntity> findByConditions(QueryParam param) {
        try {
            mongoRateLimiter.acquire();

            Criteria criteria = new Criteria();
            if (StringUtils.hasText(param.getRootNode())) {
                criteria.and("root_node").is(param.getRootNode());
            }
            if (StringUtils.hasText(param.getVersion())) {
                criteria.and("uri_version").is(param.getVersion());
            }
            if (StringUtils.hasText(param.getVersionType())) {
                criteria.and("version_type").is(param.getVersionType());
            }
            if (!param.getIncludeDeleted()) {
                criteria.and("is_deleted").is(false);
            }
            if (param.getOnlyDeleted()) {
                criteria.and("is_deleted").is(true);
            }
            if (!CollectionUtils.isEmpty(param.getUris())) {
                List<String> hashes = param.getUris().stream()
                        .map(HashUtil::hash)
                        .collect(Collectors.toList());
                criteria.and("uri_hash").in(hashes);
            }

            Pageable pageable = PageRequest.of(
                    param.getPage() - 1,
                    param.getSize() != null ? param.getSize() : DEFAULT_PAGE_SIZE
            );

            Query query = new Query(criteria).with(pageable);
            String collectionName = getCollectionName(param.getRootNode());

            long total = mongoTemplate.count(query, UriEntity.class, collectionName);
            List<UriEntity> content = mongoTemplate.find(query, UriEntity.class, collectionName);

            return new PageImpl<>(content, pageable, total);
        } catch (Exception e) {
            log.error("Failed to find URIs by conditions: {}", param, e);
            throw new RuntimeException("Failed to find URIs by conditions", e);
        }
    }

//    private String getCollectionName(String rootNode) {
//        return String.format("%s_%s", CollectionConstants.URI_COLLECTION_PREFIX, rootNode);
//    }

    /**
     * 批量更新或插入
     */
    public BulkWriteResult batchUpsert(String rootNode, List<UriEntity> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return null;
        }

        try {
            mongoRateLimiter.acquire();
            String collectionName = getCollectionName(rootNode);

            // 确保表和索引存在
            ensureCollectionAndIndexes(rootNode, collectionName);

            List<WriteModel<Document>> operations = new ArrayList<>();
            for (UriEntity entity : entities) {
                // 确保 uriHash 存在
                if (entity.getUriHash() == null && entity.getUri() != null) {
                    entity.setUriHash(HashUtil.hash(entity.getUri()));
                }

                Document query = new Document("uri_hash", entity.getUriHash());
                Document doc = convertEntityToDocument(entity);

                UpdateOneModel<Document> updateOne = new UpdateOneModel<>(
                        query,
                        new Document("$set", doc),
                        new UpdateOptions().upsert(true)
                );
                operations.add(updateOne);
            }

            BulkWriteOptions options = new BulkWriteOptions()
                    .ordered(false)
                    .bypassDocumentValidation(true);

            return mongoTemplate.getCollection(collectionName)
                    .bulkWrite(operations, options);

        } catch (Exception e) {
            log.error("Failed to batch upsert entities for rootNode: {}", rootNode, e);
            throw new RuntimeException("Batch upsert failed", e);
        }
    }

    /**
     * 分页批量软删除
     */
    public long batchSoftDelete(String rootNode, List<String> uris, int batchSize) {
        if (CollectionUtils.isEmpty(uris)) {
            return 0L;
        }

        long totalDeleted = 0;
        List<List<String>> batches = Lists.partition(uris, batchSize);

        for (List<String> batch : batches) {
            try {
                mongoRateLimiter.acquire();

                List<String> uriHashes = batch.stream()
                        .map(HashUtil::hash)
                        .collect(Collectors.toList());

                Query query = new Query(Criteria.where("uri_hash").in(uriHashes));
                Update update = new Update()
                        .set("is_deleted", true)
                        .set("update_time", LocalDateTime.now());

                UpdateResult result = mongoTemplate.updateMulti(
                        query, update, getCollectionName(rootNode)
                );

                totalDeleted += result.getModifiedCount();

            } catch (Exception e) {
                log.error("Failed to batch soft delete uris for batch size: {}", batch.size(), e);
                throw new RuntimeException("Batch soft delete failed", e);
            }
        }

        return totalDeleted;
    }

    /**
     * 分页批量硬删除
     */
    public long batchHardDelete(String rootNode, List<String> uris, int batchSize) {
        if (CollectionUtils.isEmpty(uris)) {
            return 0L;
        }

        long totalDeleted = 0;
        List<List<String>> batches = Lists.partition(uris, batchSize);

        for (List<String> batch : batches) {
            try {
                mongoRateLimiter.acquire();

                List<String> uriHashes = batch.stream()
                        .map(HashUtil::hash)
                        .collect(Collectors.toList());

                Query query = new Query(Criteria.where("uri_hash").in(uriHashes));
                DeleteResult result = mongoTemplate.remove(
                        query,
                        UriEntity.class,
                        getCollectionName(rootNode)
                );

                totalDeleted += result.getDeletedCount();

            } catch (Exception e) {
                log.error("Failed to batch hard delete uris for batch size: {}", batch.size(), e);
                throw new RuntimeException("Batch hard delete failed", e);
            }
        }

        return totalDeleted;
    }

    /**
     * 条件查询
     */
    public Page<UriEntity> findByConditions(UriQueryCondition condition) {
        try {
            mongoRateLimiter.acquire();

            Criteria criteria = new Criteria();
            if (StringUtils.hasText(condition.getRootNode())) {
                criteria.and("root_node").is(condition.getRootNode());
            }
            if (StringUtils.hasText(condition.getVersion())) {
                criteria.and("uri_version").is(condition.getVersion());
            }
            if (condition.getThirdPartyUpdateTimeStart() != null) {
                criteria.and("third_party_update_time")
                        .gte(condition.getThirdPartyUpdateTimeStart());
            }
            if (condition.getThirdPartyUpdateTimeEnd() != null) {
                criteria.and("third_party_update_time")
                        .lte(condition.getThirdPartyUpdateTimeEnd());
            }
            if (condition.getIsDeleted() != null) {
                criteria.and("is_deleted").is(condition.getIsDeleted());
            }

            Query query = new Query(criteria).with(condition.getPageable());
            if (condition.isOnlyDetail()) {
                query.fields().include("details");
            }

            long total = mongoTemplate.count(query, UriEntity.class,
                    getCollectionName(condition.getRootNode()));
            List<UriEntity> content = mongoTemplate.find(query, UriEntity.class,
                    getCollectionName(condition.getRootNode()));

            return new PageImpl<>(content, condition.getPageable(), total);

        } catch (Exception e) {
            log.error("Failed to query URIs with condition: {}", condition, e);
            throw new RuntimeException("Query failed", e);
        }
    }

    /**
     * 批量查询
     */
    public List<UriEntity> batchQuery(List<String> uris, Boolean includeDeleted, Boolean onlyDetail) {
        if (CollectionUtils.isEmpty(uris)) {
            return Collections.emptyList();
        }

        try {
            mongoRateLimiter.acquire();

            List<String> uriHashes = uris.stream()
                    .map(HashUtil::hash)
                    .collect(Collectors.toList());

            Criteria criteria = Criteria.where("uri_hash").in(uriHashes);
            if (!Boolean.TRUE.equals(includeDeleted)) {
                criteria.and("is_deleted").is(false);
            }

            Query query = new Query(criteria);
            if (Boolean.TRUE.equals(onlyDetail)) {
                query.fields().include("details");
            }

            return mongoTemplate.find(query, UriEntity.class);

        } catch (Exception e) {
            log.error("Failed to batch query URIs", e);
            throw new RuntimeException("Batch query failed", e);
        }
    }

    /**
     * 根据更新时间范围查询
     */
    public Page<UriEntity> findByUpdateTimeRange(String rootNode,
                                                 LocalDateTime startTime,
                                                 LocalDateTime endTime,
                                                 Pageable pageable) {
        try {
            mongoRateLimiter.acquire();

            Criteria criteria = Criteria.where("root_node").is(rootNode)
                    .and("third_party_update_time").gte(startTime);

            if (endTime != null) {
                criteria.and("third_party_update_time").lte(endTime);
            }

            Query query = new Query(criteria).with(pageable);

            long total = mongoTemplate.count(query, UriEntity.class, getCollectionName(rootNode));
            List<UriEntity> content = mongoTemplate.find(query, UriEntity.class,
                    getCollectionName(rootNode));

            return new PageImpl<>(content, pageable, total);

        } catch (Exception e) {
            log.error("Failed to query URIs by update time range for rootNode: {}", rootNode, e);
            throw new RuntimeException("Query by update time failed", e);
        }
    }

    /**
     * 统计根节点下的URI数量
     */
    public long countByRootNode(String rootNode) {
        try {
            mongoRateLimiter.acquire();
            return mongoTemplate.count(
                    Query.query(Criteria.where("root_node").is(rootNode)
                            .and("is_deleted").is(false)),
                    UriEntity.class,
                    getCollectionName(rootNode)
            );
        } catch (Exception e) {
            log.error("Failed to count URIs for rootNode: {}", rootNode, e);
            throw new RuntimeException("Count failed", e);
        }
    }

    /**
     * 获取URI的更新时间
     */
    public Map<String, LocalDateTime> findUpdateTimesByUris(List<String> uris) {
        if (CollectionUtils.isEmpty(uris)) {
            return Collections.emptyMap();
        }

        try {
            mongoRateLimiter.acquire();

            List<String> uriHashes = uris.stream()
                    .map(HashUtil::hash)
                    .collect(Collectors.toList());

            Query query = Query.query(Criteria.where("uri_hash").in(uriHashes));
            query.fields().include("uri", "third_party_update_time");

            List<UriEntity> entities = mongoTemplate.find(query, UriEntity.class);
            return entities.stream()
                    .collect(Collectors.toMap(
                            UriEntity::getUri,
                            UriEntity::getThirdPartyUpdateTime,
                            (existing, replacement) -> existing
                    ));

        } catch (Exception e) {
            log.error("Failed to find update times for URIs", e);
            throw new RuntimeException("Find update times failed", e);
        }
    }

    private String getCollectionName(String rootNode) {
        return String.format("%s_%s", CollectionConstants.URI_COLLECTION_PREFIX, rootNode);
    }

    private void ensureCollectionAndIndexes(String rootNode, String collectionName) {
        if (!mongoTemplate.collectionExists(collectionName)) {
            indexConfiguration.createIndexesForCollection(rootNode);
        }
    }

    private Document convertEntityToDocument(UriEntity entity) {
        Document doc = new Document();
        doc.put("uri", entity.getUri());
        doc.put("uri_hash", entity.getUriHash());
        doc.put("root_node", entity.getRootNode());
        doc.put("version_type", entity.getVersionType());
        doc.put("uri_version", entity.getUriVersion());
        doc.put("real_uri", entity.getRealUri());
        doc.put("number", entity.getNumber());
        doc.put("name", entity.getName());
        doc.put("third_party_update_time", entity.getThirdPartyUpdateTime());
        doc.put("details", entity.getDetails());
        doc.put("is_deleted", false);
        doc.put("update_time", LocalDateTime.now());

        if (entity.getCreateTime() == null) {
            doc.put("create_time", LocalDateTime.now());
        }

        return doc;
    }

    /**
     * 使用原生命令分页查询uri_hash
     *
     * @param rootNode    根节点
     * @param version     版本号
     * @param versionType 版本类型
     * @param page        页码（从1开始）
     * @param size        每页大小
     * @return uri_hash列表
     */
    public List<String> findUriHashesNativeWithPage(String rootNode,
                                                    String version,
                                                    String versionType,
                                                    int page,
                                                    int size) {
        try {
            mongoRateLimiter.acquire();

            String collectionName = getCollectionName(rootNode);
            MongoCollection<Document> collection = mongoTemplate.getCollection(collectionName);

            // 构建查询条件
            Document query = new Document();
            if (StringUtils.hasText(rootNode)) {
                query.append("root_node", rootNode);
            }
            if (StringUtils.hasText(version)) {
                query.append("uri_version", version);
            }
            if (StringUtils.hasText(versionType)) {
                query.append("version_type", versionType);
            }

            // 构建聚合管道
            List<Document> pipeline = Arrays.asList(
                    new Document("$match", query),
                    new Document("$project", new Document("uri_hash", 1).append("_id", 0)),
                    new Document("$skip", (long) (page - 1) * size),
                    new Document("$limit", size)
            );

            List<String> results = new ArrayList<>();
            collection.aggregate(pipeline)
                    .map(doc -> doc.getString("uri_hash"))
                    .into(results);

            return results;
        } catch (Exception e) {
            log.error("Failed to execute native query for rootNode: {}, version: {}", rootNode, version, e);
            throw new RuntimeException("Query execution failed", e);
        }
    }

    /**
     * 统计满足条件的记录总数
     */
    public long countUriHashesNative(String rootNode,
                                     String version,
                                     Boolean isDeleted) {
        try {
            mongoRateLimiter.acquire();

            String collectionName = getCollectionName(rootNode);
            MongoCollection<Document> collection = mongoTemplate.getCollection(collectionName);

            // 构建查询条件
            Document query = new Document();
            if (StringUtils.hasText(rootNode)) {
                query.append("root_node", rootNode);
            }
            if (StringUtils.hasText(version)) {
                query.append("uri_version", version);
            }
            if (isDeleted != null) {
                query.append("is_deleted", isDeleted);
            }

            return collection.countDocuments(query);
        } catch (Exception e) {
            log.error("Failed to count documents for rootNode: {}, version: {}", rootNode, version, e);
            throw new RuntimeException("Count documents failed", e);
        }
    }
}
```

## VersionRepository.java

```java
package com.study.collect.business.testcase.repository;

import com.study.collect.business.testcase.entity.VersionEntity;
import com.study.collect.business.testcase.utils.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Repository
public class VersionRepository {

    private static final String COLLECTION_NAME = "versions";
    private final MongoTemplate mongoTemplate;
    private final RateLimiter mongoRateLimiter;

    public VersionRepository(MongoTemplate mongoTemplate, RateLimiter mongoRateLimiter) {
        this.mongoTemplate = mongoTemplate;
        this.mongoRateLimiter = mongoRateLimiter;
    }

    /**
     * 保存版本信息
     */
    public VersionEntity save(VersionEntity entity) {
        try {
            mongoRateLimiter.acquire();
            return mongoTemplate.save(entity, COLLECTION_NAME);
        } catch (Exception e) {
            log.error("Failed to save version: {}", entity, e);
            throw new RuntimeException("Failed to save version", e);
        }
    }

    /**
     * 批量保存版本信息
     */
    /**
     * 批量保存版本信息
     */
    public List<VersionEntity> saveAll(List<VersionEntity> entities) {
        try {
            mongoRateLimiter.acquire();
            // 使用 insertAll 改为 save，因为可能有更新的情况
            for (VersionEntity entity : entities) {
                mongoTemplate.save(entity, COLLECTION_NAME);
            }
            return entities;
        } catch (Exception e) {
            log.error("Failed to save versions, size: {}", entities.size(), e);
            throw new RuntimeException("Failed to save versions", e);
        }
    }

    /**
     * 根据根节点查询版本列表
     */
    public Page<String> findVersionsByRootNode(String rootNode, Pageable pageable) {
        try {
            mongoRateLimiter.acquire();
            Query query = Query.query(Criteria.where("root_node").is(rootNode))
                    .with(pageable);

            long total = mongoTemplate.count(query, VersionEntity.class, COLLECTION_NAME);
            List<VersionEntity> versions = mongoTemplate.find(query, VersionEntity.class, COLLECTION_NAME);

            return new PageImpl<>(
                    versions.stream().map(VersionEntity::getVersion).toList(),
                    pageable,
                    total
            );
        } catch (Exception e) {
            log.error("Failed to find versions for rootNode: {}", rootNode, e);
            throw new RuntimeException("Failed to find versions", e);
        }
    }

    /**
     * 统计根节点的版本数量
     */
    public long countByRootNode(String rootNode) {
        try {
            mongoRateLimiter.acquire();
            Query query = Query.query(Criteria.where("root_node").is(rootNode));
            return mongoTemplate.count(query, VersionEntity.class, COLLECTION_NAME);
        } catch (Exception e) {
            log.error("Failed to count versions for rootNode: {}", rootNode, e);
            throw new RuntimeException("Failed to count versions", e);
        }
    }

    /**
     * 根据根节点和版本号查询
     */
    public VersionEntity findByRootNodeAndVersion(String rootNode, String version) {
        try {
            mongoRateLimiter.acquire();
            Query query = Query.query(
                    Criteria.where("root_node").is(rootNode)
                            .and("version").is(version)
            );
            return mongoTemplate.findOne(query, VersionEntity.class, COLLECTION_NAME);
        } catch (Exception e) {
            log.error("Failed to find version: rootNode={}, version={}", rootNode, version, e);
            throw new RuntimeException("Failed to find version", e);
        }
    }

    /**
     * 查询最后更新时间
     */
    public LocalDateTime findLastUpdateTime(String rootNode) {
        try {
            mongoRateLimiter.acquire();
            Query query = Query.query(Criteria.where("root_node").is(rootNode))
                    .limit(1)
                    .with(org.springframework.data.domain.Sort.by(
                            org.springframework.data.domain.Sort.Direction.DESC, "update_time"));

            VersionEntity version = mongoTemplate.findOne(query, VersionEntity.class, COLLECTION_NAME);
            return version != null ? version.getUpdateTime() : null;
        } catch (Exception e) {
            log.error("Failed to find last update time for rootNode: {}", rootNode, e);
            throw new RuntimeException("Failed to find last update time", e);
        }
    }
}
```

## CollectScheduler.java

```java
package com.study.collect.business.testcase.service;

import com.study.collect.business.testcase.model.param.CollectParam;
import com.study.collect.business.testcase.model.response.VersionInfo;
import com.study.collect.business.testcase.repository.UriRepository;
import com.study.collect.business.testcase.service.http.UriHttpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CollectScheduler {
    private final UriHttpService httpService;
    private final UriRepository repository;
    private final UriCollectService collectService;

    @Scheduled(cron = "${collect.check.cron:0 0 * * * *}") // 默认每小时执行
    public void checkCollectStatus() {
        log.info("Starting collect status check");

        try {
            // 获取所有rootNode的配置
            List<String> rootNodes = getRootNodes();

            for (String rootNode : rootNodes) {
                try {
                    checkRootNode(rootNode);
                } catch (Exception e) {
                    log.error("Failed to check rootNode: {}", rootNode, e);
                }
            }
        } catch (Exception e) {
            log.error("Collect status check failed", e);
        }
    }

    private void checkRootNode(String rootNode) {
        // 1. 获取版本列表
        List<VersionInfo> versions = httpService.getVersions(
                getServerUrl(), rootNode, 1, Integer.MAX_VALUE).join();

        // 2. 检查每个版本
        for (VersionInfo version : versions) {
            try {
                checkVersion(rootNode, version);
            } catch (Exception e) {
                log.error("Failed to check version: {}", version.getVersion(), e);
            }
        }
    }

    private void checkVersion(String rootNode, VersionInfo version) {
        // 获取接口URI数量
        int apiCount = httpService.getUriCount(getServerUrl(), version.getVersion()).join();

        // 获取数据库URI数量
        long dbCount = repository.countByVersion(rootNode, version.getVersion());

        if (apiCount != dbCount) {
            log.warn("URI count mismatch for version {}: API={}, DB={}",
                    version.getVersion(), apiCount, dbCount);

            // 触发采集
            CollectParam param = CollectParam.builder()
                    .rootNode(rootNode)
                    .version(version.getVersion())
                    .serverUrl(getServerUrl())
                    .build();

            collectService.collectData(param);
        }
    }
}
```

## UriCollectService.java

```java
package com.study.collect.business.testcase.service;

import com.study.collect.business.testcase.entity.UriEntity;
import com.study.collect.business.testcase.model.param.CollectParam;
import com.study.collect.business.testcase.model.param.DeleteParam;
import com.study.collect.business.testcase.model.param.QueryParam;
import com.study.collect.business.testcase.model.response.AsyncResponse;
import com.study.collect.business.testcase.model.response.TaskResponse;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * URI采集服务接口
 */
public interface UriCollectService {

    /**
     * 异步采集数据
     */
    AsyncResponse<String> collectData(CollectParam param);

    /**
     * 获取版本列表
     */
    Page<String> getVersions(String rootNode, Integer page, Integer size);

    /**
     * 获取版本下URI数量
     */
    Long getUriCount(String rootNode, String version);

    /**
     * 异步删除数据
     */
    AsyncResponse<Long> deleteData(DeleteParam param);

    /**
     * 条件查询URI数据
     */
    Page<UriEntity> queryUri(QueryParam param);

    /**
     * 批量查询URI
     */
    List<UriEntity> batchQueryUri(List<String> uris, Boolean includeDeleted, Boolean onlyDetail);

    /**
     * 按更新时间查询URI
     */
    Page<UriEntity> queryByUpdateTime(String rootNode, LocalDateTime startTime, LocalDateTime endTime,
                                      Integer page, Integer size);

    /**
     * 获取任务状态
     */
    AsyncResponse<Void> getTaskStatus(String taskId);

    /**
     * 取消任务
     */
    boolean cancelTask(String taskId);

    /**
     * 更新任务优先级
     */
    boolean updateTaskPriority(String taskId, int priority);

    /**
     * 获取活动任务列表
     */
    List<TaskResponse> getActiveTasks();

    /**
     * 获取采集统计信息
     */
    Map<String, Object> getCollectionStats(String rootNode);
}
```

## UriHttpService.java

```java
package com.study.collect.business.testcase.service.http;

import com.study.collect.business.testcase.model.response.PageResponse;
import com.study.collect.business.testcase.model.response.UriDetail;
import com.study.collect.business.testcase.model.response.VersionInfo;
import com.study.collect.business.testcase.model.response.parse.HttpResponseParser;
import com.study.collect.business.testcase.utils.HttpUtil;
import com.study.collect.business.testcase.utils.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class UriHttpService {

    private final HttpResponseParser<PageResponse<VersionInfo>> versionParser;
    private final HttpResponseParser<List<String>> uriListParser;
    private final HttpResponseParser<List<UriDetail>> uriDetailParser;
    private final HttpResponseParser<Integer> uriCountParser;
    private final RateLimiter rateLimiter;

    @Qualifier("httpExecutor")
    private final ThreadPoolTaskExecutor httpExecutor;

    /**
     * 获取版本列表
     */
    public CompletableFuture<List<VersionInfo>> getVersions(
            String serverUrl, String rootNode, int page, int size) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                rateLimiter.acquire();
                String response = HttpUtil.post(
                        serverUrl + "/api/versions",
                        String.format(
                                "{\"rootNode\":\"%s\",\"page\":%d,\"size\":%d}",
                                rootNode, page, size
                        )
                ).getBody();

                return versionParser.parse(response).getItems();
            } catch (Exception e) {
                log.error("Failed to get versions for rootNode: {}", rootNode, e);
                throw new RuntimeException("Failed to get versions", e);
            }
        }, httpExecutor);
    }

    /**
     * 获取URI列表
     */
    public CompletableFuture<List<String>> getUriList(String serverUrl, String version) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                rateLimiter.acquire();
                String response = HttpUtil.post(
                        serverUrl + "/api/uris",
                        String.format("{\"version\":\"%s\"}", version)
                ).getBody();

                return uriListParser.parse(response);
            } catch (Exception e) {
                log.error("Failed to get URI list for version: {}", version, e);
                throw new RuntimeException("Failed to get URI list", e);
            }
        }, httpExecutor);
    }

    /**
     * 获取URI数量
     */
    public CompletableFuture<Integer> getUriCount(String serverUrl, String version) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                rateLimiter.acquire();
                String response = HttpUtil.post(
                        serverUrl + "/api/uri/count",
                        String.format("{\"version\":\"%s\"}", version)
                ).getBody();

                return uriCountParser.parse(response);
            } catch (Exception e) {
                log.error("Failed to get URI count for version: {}", version, e);
                throw new RuntimeException("Failed to get URI count", e);
            }
        }, httpExecutor);
    }

    /**
     * 批量获取URI详情
     */
    public CompletableFuture<List<UriDetail>> getUriDetails(
            String serverUrl, List<String> uris) {
        if (uris == null || uris.isEmpty()) {
            return CompletableFuture.completedFuture(Collections.emptyList());
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                rateLimiter.acquire();
                StringBuilder jsonBody = new StringBuilder("{\"uris\":[");
                for (int i = 0; i < uris.size(); i++) {
                    if (i > 0) {
                        jsonBody.append(",");
                    }
                    jsonBody.append("\"").append(uris.get(i)).append("\"");
                }
                jsonBody.append("]}");

                String response = HttpUtil.post(
                        serverUrl + "/api/details",
                        jsonBody.toString()
                ).getBody();

                return uriDetailParser.parse(response);
            } catch (Exception e) {
                log.error("Failed to get URI details for {} URIs", uris.size(), e);
                throw new RuntimeException("Failed to get URI details", e);
            }
        }, httpExecutor);
    }
}
```

## IndexChecker.java

```java
package com.study.collect.business.testcase.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class IndexChecker {
    private final MongoTemplate mongoTemplate;

    public void checkIndexes(String collectionName) {
        List<Document> indexes = mongoTemplate.getCollection(collectionName).listIndexes()
                .into(new ArrayList<>());

        log.info("Collection {} indexes: {}", collectionName, indexes);
    }
}
```

## UriCleanupService.java

```java
package com.study.collect.business.testcase.service.impl;

import com.google.common.collect.Lists;
import com.study.collect.business.testcase.repository.UriRepository;
import com.study.collect.business.testcase.utils.HashUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UriCleanupService {

    private static final int PAGE_SIZE = 2000;
    private static final int DELETE_BATCH_SIZE = 2000;
    private final UriRepository repository;

    @Autowired
    public UriCleanupService(UriRepository repository) {
        this.repository = repository;
    }

    /**
     * 清理不在总列表中的URI数据
     *
     * @param allUris    总的URI列表
     * @param rootNode   根节点
     * @param version    版本
     * @param hardDelete 是否硬删除
     */
    public void cleanupUriData(List<String> allUris, String rootNode, String version, boolean hardDelete) {
        try {
            if (allUris == null || allUris.isEmpty()) {
                log.warn("No URIs provided for cleanup");
                return;
            }

            log.info("Starting URI cleanup process for rootNode: {}, version: {}, total URIs: {}",
                    rootNode, version, allUris.size());

            // 将URI转换为hash集合
            Set<String> allHashSet = allUris.stream()
                    .map(HashUtil::hash)
                    .collect(Collectors.toSet());

            // 分页获取数据库中的数据并进行清理
            int page = 1;
            boolean hasMore = true;
            int totalDeleted = 0;

            while (hasMore) {
                List<String> dbUriHashes = repository.findUriHashesNativeWithPage(
                        rootNode,
                        version,
                        null,
                        page,
                        PAGE_SIZE
                );

                if (dbUriHashes.isEmpty()) {
                    break;
                }

                // 找出需要删除的hash
                List<String> toDeleteHashes = dbUriHashes.stream()
                        .filter(hash -> !allHashSet.contains(hash))
                        .collect(Collectors.toList());

                // 分批删除
                if (!toDeleteHashes.isEmpty()) {
                    List<List<String>> batches = Lists.partition(toDeleteHashes, DELETE_BATCH_SIZE);
                    for (List<String> batch : batches) {
                        try {
                            long deletedCount;
                            if (hardDelete) {
                                deletedCount = repository.batchHardDelete(rootNode, batch, DELETE_BATCH_SIZE);
                            } else {
                                deletedCount = repository.batchSoftDelete(rootNode, batch, DELETE_BATCH_SIZE);
                            }
                            totalDeleted += deletedCount;
                            log.info("Deleted {} URIs in batch, total deleted: {}", deletedCount, totalDeleted);
                        } catch (Exception e) {
                            log.error("Error deleting batch of size: {}", batch.size(), e);
                        }
                    }
                }

                hasMore = dbUriHashes.size() >= PAGE_SIZE;
                page++;
            }

            log.info("Cleanup completed for rootNode: {}, version: {}, total deleted: {}",
                    rootNode, version, totalDeleted);

        } catch (Exception e) {
            log.error("Error during cleanup process for rootNode: {}", rootNode, e);
            throw new RuntimeException("Cleanup process failed", e);
        }
    }

    /**
     * 异步执行清理过程
     */
    @Async
    public CompletableFuture<Void> cleanupUriDataAsync(List<String> allUris,
                                                       String rootNode,
                                                       String version,
                                                       boolean hardDelete) {
        return CompletableFuture.runAsync(() -> {
            cleanupUriData(allUris, rootNode, version, hardDelete);
        }).exceptionally(throwable -> {
            log.error("Async cleanup failed for rootNode: {}", rootNode, throwable);
            throw new RuntimeException("Async cleanup failed", throwable);
        });
    }

    /**
     * 获取特定版本的URI数量
     */
    public long getUriCount(String rootNode, String version) {
        try {
            return repository.countUriHashesNative(rootNode, version, null);
        } catch (Exception e) {
            log.error("Failed to get URI count for rootNode: {} and version: {}", rootNode, version, e);
            throw new RuntimeException("Failed to get URI count", e);
        }
    }

    /**
     * 验证数据完整性
     * 检查数据库中的URI数量是否与提供的URI列表数量匹配
     */
    public boolean validateDataIntegrity(String rootNode, String version, int expectedCount) {
        try {
            long actualCount = getUriCount(rootNode, version);
            boolean isValid = actualCount == expectedCount;

            if (!isValid) {
                log.warn("Data integrity check failed for rootNode: {}, version: {}. " +
                        "Expected: {}, Actual: {}", rootNode, version, expectedCount, actualCount);
            }

            return isValid;
        } catch (Exception e) {
            log.error("Failed to validate data integrity for rootNode: {}", rootNode, e);
            return false;
        }
    }

    /**
     * 检查并返回丢失的URI
     */
    public List<String> findMissingUris(List<String> expectedUris, String rootNode, String version) {
        try {
            Set<String> expectedHashes = expectedUris.stream()
                    .map(HashUtil::hash)
                    .collect(Collectors.toSet());

            List<String> missingUris = new ArrayList<>();
            int page = 1;
            boolean hasMore = true;

            while (hasMore) {
                List<String> dbHashes = repository.findUriHashesNativeWithPage(
                        rootNode,
                        version,
                        null,
                        page,
                        PAGE_SIZE
                );

                if (dbHashes.isEmpty()) {
                    break;
                }

                Set<String> dbHashSet = new HashSet<>(dbHashes);
                expectedHashes.removeAll(dbHashSet);

                hasMore = dbHashes.size() >= PAGE_SIZE;
                page++;
            }

            // 将剩余的hash转换回URI
            return expectedUris.stream()
                    .filter(uri -> expectedHashes.contains(HashUtil.hash(uri)))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Failed to find missing URIs for rootNode: {}", rootNode, e);
            throw new RuntimeException("Failed to find missing URIs", e);
        }
    }

    /**
     * 检查数据库健康状态
     */
    public Map<String, Object> checkDatabaseHealth(String rootNode, String version) {
        Map<String, Object> healthStatus = new HashMap<>();
        try {
            long totalCount = getUriCount(rootNode, version);
            long deletedCount = repository.countUriHashesNative(rootNode, version, true);

            healthStatus.put("totalCount", totalCount);
            healthStatus.put("deletedCount", deletedCount);
            healthStatus.put("activeCount", totalCount - deletedCount);
            healthStatus.put("status", "HEALTHY");

        } catch (Exception e) {
            log.error("Health check failed for rootNode: {}", rootNode, e);
            healthStatus.put("status", "UNHEALTHY");
            healthStatus.put("error", e.getMessage());
        }
        return healthStatus;
    }
}
```

## UriCollectServiceImpl.java

```java
package com.study.collect.business.testcase.service.impl;

import com.google.common.collect.Lists;
import com.study.collect.business.testcase.constant.CollectionConstants;
import com.study.collect.business.testcase.entity.CollectTaskEntity;
import com.study.collect.business.testcase.entity.UriEntity;
import com.study.collect.business.testcase.manager.CollectTaskManager;
import com.study.collect.business.testcase.manager.QueueManager;
import com.study.collect.business.testcase.model.param.CollectParam;
import com.study.collect.business.testcase.model.param.DeleteParam;
import com.study.collect.business.testcase.model.param.QueryParam;
import com.study.collect.business.testcase.model.response.AsyncResponse;
import com.study.collect.business.testcase.model.response.TaskResponse;
import com.study.collect.business.testcase.model.response.UriDetail;
import com.study.collect.business.testcase.model.response.VersionInfo;
import com.study.collect.business.testcase.repository.CollectTaskRepository;
import com.study.collect.business.testcase.repository.UriRepository;
import com.study.collect.business.testcase.repository.VersionRepository;
import com.study.collect.business.testcase.service.UriCollectService;
import com.study.collect.business.testcase.service.http.UriHttpService;
import com.study.collect.business.testcase.utils.HashUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.ObjectPool;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class UriCollectServiceImpl implements UriCollectService {
    private static final int HTTP_BATCH_SIZE = CollectionConstants.HTTP_BATCH_SIZE;
    private static final int DEFAULT_PAGE_SIZE = 20;

    private final UriHttpService httpService;
    private final UriRepository uriRepository;
    private final VersionRepository versionRepository;
    private final CollectTaskRepository taskRepository;
    private final ObjectPool<UriEntity> entityPool;
    private final CollectTaskManager taskManager;
    private final QueueManager<CollectParam> collectQueue;
    private final QueueManager<DeleteParam> deleteQueue;

    private final ConcurrentHashMap<String, CollectTaskEntity> activeTasks = new ConcurrentHashMap<>();

    @Override
    public AsyncResponse<String> collectData(CollectParam param) {
        validateCollectParam(param);

        String taskId = UUID.randomUUID().toString();
        param.setTaskId(taskId);

        Map<String, Object> taskParams = new HashMap<>();
        taskParams.put("rootNode", param.getRootNode());
        taskParams.put("version", param.getVersion());
        taskParams.put("serverUrl", param.getServerUrl());
        taskParams.put("incremental", param.getIncremental());

        TaskResponse task = taskManager.createTask("COLLECT", taskParams, param.getPriority());

        collectQueue.enqueue(taskId, param, param.getPriority(), this::processCollectTask)
                .exceptionally(throwable -> {
                    taskManager.updateTaskStatus(taskId, "ERROR", throwable.getMessage());
                    return null;
                });

        return AsyncResponse.<String>builder()
                .taskId(taskId)
                .status("QUEUED")
                .message("Task queued successfully")
                .build();
    }

    private void validateCollectParam(CollectParam param) {
        if (param == null) {
            throw new IllegalArgumentException("CollectParam cannot be null");
        }
        if (!StringUtils.hasText(param.getRootNode())) {
            throw new IllegalArgumentException("RootNode cannot be empty");
        }
        if (!StringUtils.hasText(param.getServerUrl())) {
            throw new IllegalArgumentException("ServerUrl cannot be empty");
        }
        if (param.getIncremental() && param.getStartTime() == null) {
            throw new IllegalArgumentException("StartTime is required for incremental collection");
        }
    }

    private void processCollectTask(CollectParam param) {
        String taskId = param.getTaskId();
        taskManager.updateTaskStatus(taskId, "PROCESSING", "Starting collection");

        try {
            // 获取版本列表
            List<VersionInfo> versions = httpService.getVersions(
                    param.getServerUrl(),
                    param.getRootNode(),
                    1,
                    Integer.MAX_VALUE
            ).get();

            if (StringUtils.hasText(param.getVersion())) {
                versions = versions.stream()
                        .filter(v -> v.getVersion().equals(param.getVersion()))
                        .toList();
            }

            long totalUris = 0;
            for (VersionInfo version : versions) {
                // 获取该版本下的URI数量
                int versionUriCount = httpService.getUriCount(param.getServerUrl(), version.getVersion()).get();
                totalUris += versionUriCount;

                // 处理该版本的URI
                processVersionUris(param, version, versionUriCount, taskId);
            }

            taskManager.updateTaskStatus(taskId, "COMPLETED", "Collection completed successfully");

        } catch (Exception e) {
            log.error("Failed to process collect task: {}", taskId, e);
            taskManager.updateTaskStatus(taskId, "ERROR", "Collection failed: " + e.getMessage());
            throw new RuntimeException("Failed to process collect task", e);
        }
    }

    private void processVersionUris(CollectParam param, VersionInfo version, int totalCount, String taskId) {
        try {
            // 分批获取URI列表
            int offset = 0;
            int batchSize = param.getBatchSize() != null ? param.getBatchSize() : HTTP_BATCH_SIZE;

            while (offset < totalCount) {
                // 获取一批URI
                List<String> uris = httpService.getUriList(param.getServerUrl(), version.getVersion()).get();

                // 获取URI详情
                List<UriDetail> details = httpService.getUriDetails(param.getServerUrl(), uris).get();

                // 转换并保存实体
                List<UriEntity> entities = new ArrayList<>();
                for (UriDetail detail : details) {
                    UriEntity entity = entityPool.borrowObject();
                    try {
                        fillEntity(entity, param.getRootNode(), version, detail);
                        entities.add(entity);
                    } catch (Exception e) {
                        log.error("Failed to process URI: {}", detail.getUri(), e);
                        entityPool.returnObject(entity);
                    }
                }

                if (!entities.isEmpty()) {
                    try {
                        uriRepository.batchUpsert(param.getRootNode(), entities);
                    } finally {
                        // 返还实体到对象池
                        for (UriEntity entity : entities) {
                            entityPool.returnObject(entity);
                        }
                    }
                }

                // 更新进度
                offset += batchSize;
                double progress = (double) offset / totalCount * 100;
                taskManager.updateTaskProgress(taskId, offset, totalCount);
            }

        } catch (Exception e) {
            log.error("Failed to process version: {}", version.getVersion(), e);
            throw new RuntimeException("Failed to process version", e);
        }
    }

    private void fillEntity(UriEntity entity, String rootNode, VersionInfo version, UriDetail detail) {
        entity.setUri(detail.getUri());
        entity.setUriHash(HashUtil.hash(detail.getUri()));
        entity.setRootNode(rootNode);
        entity.setVersionType(version.getType());
        entity.setUriVersion(version.getVersion());
        entity.setRealUri(detail.getRealUri());
        entity.setNumber(detail.getNumber());
        entity.setName(detail.getName());
        entity.setThirdPartyUpdateTime(detail.getUpdateTime());
        entity.setDetails(detail.getDetails());
        entity.setDeleted(false);
    }

    @Override
    public Page<String> getVersions(String rootNode, Integer page, Integer size) {
        int pageNum = page != null ? page : 1;
        int pageSize = size != null ? size : DEFAULT_PAGE_SIZE;
        return versionRepository.findVersionsByRootNode(rootNode, PageRequest.of(pageNum - 1, pageSize));
    }

    @Override
    public Long getUriCount(String rootNode, String version) {
        return uriRepository.countByRootNodeAndVersion(rootNode, version);
    }

    @Override
    public AsyncResponse<Long> deleteData(DeleteParam param) {
        validateDeleteParam(param);

        String taskId = UUID.randomUUID().toString();
        param.setTaskId(taskId);

        Map<String, Object> taskParams = new HashMap<>();
        taskParams.put("rootNode", param.getRootNode());
        taskParams.put("version", param.getVersion());
        taskParams.put("uriCount", param.getUris().size());
        taskParams.put("hardDelete", param.getHardDelete());

        TaskResponse task = taskManager.createTask("DELETE", taskParams, param.getPriority());

        deleteQueue.enqueue(taskId, param, param.getPriority(), this::processDeleteTask)
                .exceptionally(throwable -> {
                    taskManager.updateTaskStatus(taskId, "ERROR", throwable.getMessage());
                    return null;
                });

        return AsyncResponse.<Long>builder()
                .taskId(taskId)
                .status("QUEUED")
                .message("Delete task queued successfully")
                .build();
    }

    private void validateDeleteParam(DeleteParam param) {
        if (param == null) {
            throw new IllegalArgumentException("DeleteParam cannot be null");
        }
        if (param.getUris() == null || param.getUris().isEmpty()) {
            throw new IllegalArgumentException("URIs cannot be empty");
        }
    }

    private void processDeleteTask(DeleteParam param) {
        String taskId = param.getTaskId();
        taskManager.updateTaskStatus(taskId, "PROCESSING", "Starting deletion");

        try {
            int batchSize = param.getBatchSize() != null ? param.getBatchSize() : HTTP_BATCH_SIZE;
            List<List<String>> batches = Lists.partition(param.getUris(), batchSize);

            long totalDeleted = 0;
            long totalBatches = batches.size();

            for (int i = 0; i < batches.size(); i++) {
                List<String> batch = batches.get(i);
                long deletedCount;

                if (param.getHardDelete()) {
                    deletedCount = uriRepository.batchHardDelete(param.getRootNode(), batch, batchSize);
                } else {
                    deletedCount = uriRepository.batchSoftDelete(param.getRootNode(), batch, batchSize);
                }

                totalDeleted += deletedCount;
                double progress = ((i + 1.0) / totalBatches) * 100;
                taskManager.updateTaskProgress(taskId, i + 1, totalBatches);
            }

            taskManager.updateTaskStatus(taskId, "COMPLETED", String.format("Successfully deleted %d URIs", totalDeleted));

        } catch (Exception e) {
            log.error("Failed to process delete task: {}", taskId, e);
            taskManager.updateTaskStatus(taskId, "ERROR", "Deletion failed: " + e.getMessage());
            throw new RuntimeException("Failed to process delete task", e);
        }
    }

    @Override
    public Page<UriEntity> queryUri(QueryParam param) {
        return uriRepository.findByConditions(param);
    }

    @Override
    public List<UriEntity> batchQueryUri(List<String> uris, Boolean includeDeleted, Boolean onlyDetail) {
        return uriRepository.batchQuery(uris, includeDeleted, onlyDetail);
    }

    @Override
    public Page<UriEntity> queryByUpdateTime(String rootNode, LocalDateTime startTime,
                                             LocalDateTime endTime, Integer page, Integer size) {
        int pageNum = page != null ? page : 1;
        int pageSize = size != null ? size : DEFAULT_PAGE_SIZE;
        return uriRepository.findByUpdateTimeRange(
                rootNode,
                startTime,
                endTime,
                PageRequest.of(pageNum - 1, pageSize)
        );
    }

    @Override
    public AsyncResponse<Void> getTaskStatus(String taskId) {
        TaskResponse task = taskManager.getTaskStatus(taskId);
        if (task == null) {
            return AsyncResponse.<Void>builder()
                    .taskId(taskId)
                    .status("NOT_FOUND")
                    .message("Task not found")
                    .build();
        }

        return AsyncResponse.<Void>builder()
                .taskId(taskId)
                .status(task.getStatus())
                .message(task.getMessage())
                .progress(task.getProgress())
                .startTime(task.getCreateTime())
                .endTime(task.getEndTime())
                .build();
    }

    @Override
    public boolean cancelTask(String taskId) {
        return taskManager.cancelTask(taskId);
    }

    @Override
    public boolean updateTaskPriority(String taskId, int priority) {
        return taskManager.updateTaskPriority(taskId, priority);
    }

    @Override
    public List<TaskResponse> getActiveTasks() {
        return taskManager.getActiveTasks();
    }

    @Override
    public Map<String, Object> getCollectionStats(String rootNode) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUris", uriRepository.countByRootNode(rootNode));
        stats.put("totalVersions", versionRepository.countByRootNode(rootNode));
        stats.put("lastCollectTime", taskRepository.findLastCollectTime(rootNode));
        stats.put("activeTasks", taskManager.getActiveTasks().stream()
                .filter(task -> rootNode.equals(task.getParams().get("rootNode")))
                .count());
        return stats;
    }
}
```

## HashUtil.java

```java
package com.study.collect.business.testcase.utils;

import org.apache.commons.codec.digest.DigestUtils;

//
public class HashUtil {
    public static String hash(String input) {
        return DigestUtils.sha256Hex(input);
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
//
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
     * 禁用SSL证书验证
     */
    private static void disableSslVerification() {
        try {
            // 创建信任所有证书的TrustManager
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
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
     *
     * @param method  HTTP方法
     * @param urlStr  请求URL
     * @param body    请求体
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

        public int getCode() {
            return code;
        }

        public String getBody() {
            return body;
        }

        public Map<String, List<String>> getHeaders() {
            return headers;
        }

        public long getResponseTime() {
            return responseTime;
        }

        @Override
        public String toString() {
            return String.format("HttpResponse{code=%d, responseTime=%dms, bodyLength=%d}",
                    code, responseTime, body != null ? body.length() : 0);
        }
    }
}

```

## ListCompareUtil.java

```java
package com.study.collect.business.testcase.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class ListCompareUtil {

    /**
     * 比较两个列表，找出在B中有但在A中没有的元素
     */
    public static <T> List<T> findMissingInA(List<T> listA, List<T> listB) {
        if (CollectionUtils.isEmpty(listB)) {
            return new ArrayList<>();
        }
        if (CollectionUtils.isEmpty(listA)) {
            return new ArrayList<>(listB);
        }

        Set<T> setA = new HashSet<>(listA);
        return listB.stream()
                .filter(item -> !setA.contains(item))
                .collect(Collectors.toList());
    }

    /**
     * 根据指定字段比较两个列表，找出在B中有但在A中没有的元素
     */
    public static <T, R> List<T> findMissingInA(List<T> listA, List<T> listB,
                                                Function<T, R> keyExtractor) {
        if (CollectionUtils.isEmpty(listB)) {
            return new ArrayList<>();
        }
        if (CollectionUtils.isEmpty(listA)) {
            return new ArrayList<>(listB);
        }

        Set<R> keysA = listA.stream()
                .map(keyExtractor)
                .collect(Collectors.toSet());

        return listB.stream()
                .filter(item -> !keysA.contains(keyExtractor.apply(item)))
                .collect(Collectors.toList());
    }

    /**
     * 找出两个列表的交集
     */
    public static <T> List<T> findIntersection(List<T> listA, List<T> listB) {
        if (CollectionUtils.isEmpty(listA) || CollectionUtils.isEmpty(listB)) {
            return new ArrayList<>();
        }

        Set<T> setB = new HashSet<>(listB);
        return listA.stream()
                .filter(setB::contains)
                .collect(Collectors.toList());
    }

    /**
     * 根据指定字段找出两个列表的交集
     */
    public static <T, R> List<T> findIntersection(List<T> listA, List<T> listB,
                                                  Function<T, R> keyExtractor) {
        if (CollectionUtils.isEmpty(listA) || CollectionUtils.isEmpty(listB)) {
            return new ArrayList<>();
        }

        Set<R> keysB = listB.stream()
                .map(keyExtractor)
                .collect(Collectors.toSet());

        return listA.stream()
                .filter(item -> keysB.contains(keyExtractor.apply(item)))
                .collect(Collectors.toList());
    }

    /**
     * 找出两个列表的差集（在A中但不在B中的元素）
     */
    public static <T> List<T> findDifference(List<T> listA, List<T> listB) {
        if (CollectionUtils.isEmpty(listA)) {
            return new ArrayList<>();
        }
        if (CollectionUtils.isEmpty(listB)) {
            return new ArrayList<>(listA);
        }

        Set<T> setB = new HashSet<>(listB);
        return listA.stream()
                .filter(item -> !setB.contains(item))
                .collect(Collectors.toList());
    }

    /**
     * 根据指定字段找出两个列表的差集
     */
    public static <T, R> List<T> findDifference(List<T> listA, List<T> listB,
                                                Function<T, R> keyExtractor) {
        if (CollectionUtils.isEmpty(listA)) {
            return new ArrayList<>();
        }
        if (CollectionUtils.isEmpty(listB)) {
            return new ArrayList<>(listA);
        }

        Set<R> keysB = listB.stream()
                .map(keyExtractor)
                .collect(Collectors.toSet());

        return listA.stream()
                .filter(item -> !keysB.contains(keyExtractor.apply(item)))
                .collect(Collectors.toList());
    }

    /**
     * 分页处理列表
     */
    public static <T> List<List<T>> partition(List<T> list, int size) {
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }

        List<List<T>> result = new ArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            result.add(list.subList(i, Math.min(i + size, list.size())));
        }
        return result;
    }
}


```

## RateLimiter.java

```java
package com.study.collect.business.testcase.utils;

import com.study.collect.business.testcase.constant.CollectionConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class RateLimiter {
    private final int permitsPerMinute;
    private final ConcurrentLinkedQueue<Long> timestamps;
    private final AtomicInteger currentPermits;
    private final ScheduledExecutorService scheduler;

    public RateLimiter() {
        this.permitsPerMinute = CollectionConstants.HTTP_MAX_REQUESTS_PER_MINUTE;
        this.timestamps = new ConcurrentLinkedQueue<>();
        this.currentPermits = new AtomicInteger(0);
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r);
            thread.setName("rate-limiter-cleaner");
            thread.setDaemon(true);
            return thread;
        });

        // 定期清理过期的时间戳
        scheduler.scheduleAtFixedRate(
                this::cleanup,
                1,
                1,
                TimeUnit.MINUTES
        );
    }

    /**
     * 获取许可
     */
    public void acquire() throws InterruptedException {
        while (!tryAcquire()) {
            Thread.sleep(100); // 等待100ms后重试
        }
    }

    /**
     * 尝试获取许可
     */
    public boolean tryAcquire() {
        cleanup(); // 清理过期的时间戳

        long now = System.currentTimeMillis();
        int currentCount = currentPermits.get();

        if (currentCount >= permitsPerMinute) {
            return false;
        }

        if (currentPermits.incrementAndGet() <= permitsPerMinute) {
            timestamps.offer(now);
            return true;
        } else {
            currentPermits.decrementAndGet();
            return false;
        }
    }

    /**
     * 清理过期的时间戳
     */
    private void cleanup() {
        long now = System.currentTimeMillis();
        long oneMinuteAgo = now - TimeUnit.MINUTES.toMillis(1);

        // 移除一分钟前的时间戳
        while (!timestamps.isEmpty() && timestamps.peek() < oneMinuteAgo) {
            timestamps.poll();
            currentPermits.decrementAndGet();
        }
    }

    /**
     * 获取当前速率
     */
    public int getCurrentRate() {
        cleanup();
        return currentPermits.get();
    }

    /**
     * 获取剩余许可数
     */
    public int getAvailablePermits() {
        cleanup();
        return permitsPerMinute - currentPermits.get();
    }

    /**
     * 等待直到有可用许可
     */
    public void waitForPermit() throws InterruptedException {
        while (getCurrentRate() >= permitsPerMinute) {
            Thread.sleep(100);
        }
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
```

## StreamProcessManager.java

```java
package com.study.collect.business.testcase.utils;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 通用流式处理管理器
 *
 * @param <T> 源数据类型
 * @param <R> 结果数据类型
 */
@Slf4j
public class StreamProcessManager<T, R> {
    private final ProcessConfig<T, R> config;
    private final ExecutorService processExecutor;
    private final ExecutorService saveExecutor;
    private final BlockingQueue<CompletableFuture<?>> processQueue;
    private final AtomicInteger activeProcesses;
    private final List<ProcessMetrics> metricsHistory;
    private volatile boolean running = true;

    public StreamProcessManager(ProcessConfig<T, R> config) {
        this.config = config;
        this.processExecutor = createExecutor(config.getProcessThreads(), config.isUseVirtualThreads());
        this.saveExecutor = createExecutor(config.getSaveThreads(), config.isUseVirtualThreads());
        this.processQueue = new ArrayBlockingQueue<>(1000);
        this.activeProcesses = new AtomicInteger(0);
        this.metricsHistory = new CopyOnWriteArrayList<>();

        validateConfig(config);
    }

    /**
     * 开始处理数据
     */
    public CompletableFuture<ProcessMetrics> processData(int offset, int limit) {
        ProcessMetrics metrics = initializeMetrics();
        CompletableFuture<ProcessMetrics> resultFuture = new CompletableFuture<>();

        try {
            activeProcesses.incrementAndGet();
            processDataBatches(offset, limit, metrics, resultFuture);
        } catch (Exception e) {
            activeProcesses.decrementAndGet();
            resultFuture.completeExceptionally(e);
        }

        return resultFuture;
    }

    private void processDataBatches(
            int offset,
            int limit,
            ProcessMetrics metrics,
            CompletableFuture<ProcessMetrics> resultFuture
    ) {
        CompletableFuture.runAsync(() -> {
            try {
                int processed = 0;
                while (running && processed < limit) {
                    // 获取一批数据
                    List<T> batch = config.getDataFetcher().apply(offset + processed);
                    if (batch.isEmpty()) {
                        break;
                    }

                    // 处理这批数据
                    processBatch(batch, metrics);
                    processed += batch.size();

                    // 更新进度
                    updateProgress(metrics, processed, limit);
                }

                // 完成处理
                completeProcessing(metrics, resultFuture);

            } catch (Exception e) {
                handleProcessingError(e, metrics, resultFuture);
            }
        }, processExecutor);
    }

    private void processBatch(List<T> batch, ProcessMetrics metrics) {
        int retryCount = 0;
        while (retryCount <= config.getMaxRetries()) {
            try {
                // 转换数据
                List<R> convertedBatch = batch.stream()
                        .map(config.getDataConverter()::apply)
                        .collect(java.util.stream.Collectors.toList());

                // 异步保存数据
                CompletableFuture<Void> saveFuture = CompletableFuture.runAsync(() -> {
                    config.getDataSaver().accept(convertedBatch);
                }, saveExecutor);

                // 添加到处理队列
                processQueue.put(saveFuture);

                // 检查和清理完成的任务
                cleanupCompletedTasks();
                break;

            } catch (Exception e) {
                if (++retryCount > config.getMaxRetries()) {
                    log.error("Failed to process batch after {} retries", config.getMaxRetries(), e);
                    metrics.setFailedItems(metrics.getFailedItems() + batch.size());
                    throw new RuntimeException("Batch processing failed", e);
                }
                try {
                    Thread.sleep(config.getRetryDelayMs() * (long) Math.pow(2, retryCount - 1));
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Processing interrupted", ie);
                }
            }
        }
    }

    private void cleanupCompletedTasks() {
        processQueue.removeIf(future -> {
            if (future.isDone()) {
                try {
                    future.get(0, TimeUnit.MILLISECONDS);
                    return true;
                } catch (Exception e) {
                    log.error("Task completed with error", e);
                    return true;
                }
            }
            return false;
        });
    }

    private ProcessMetrics initializeMetrics() {
        return ProcessMetrics.builder()
                .processName(config.getProcessName())
                .startTime(System.currentTimeMillis())
                .totalItems(0)
                .processedItems(0)
                .failedItems(0)
                .progressPercentage(0.0)
                .build();
    }

    private void updateProgress(ProcessMetrics metrics, long processed, long total) {
        metrics.setProcessedItems(processed);
        metrics.setTotalItems(total);
        metrics.setProgressPercentage((double) processed / total * 100);

        if (config.getProgressCallback() != null) {
            config.getProgressCallback().accept(metrics);
        }
    }

    private void completeProcessing(
            ProcessMetrics metrics,
            CompletableFuture<ProcessMetrics> resultFuture
    ) {
        metrics.setEndTime(System.currentTimeMillis());
        metricsHistory.add(metrics);
        activeProcesses.decrementAndGet();
        resultFuture.complete(metrics);
    }

    private void handleProcessingError(
            Exception e,
            ProcessMetrics metrics,
            CompletableFuture<ProcessMetrics> resultFuture
    ) {
        log.error("Error processing data", e);
        metrics.setEndTime(System.currentTimeMillis());
        metricsHistory.add(metrics);
        activeProcesses.decrementAndGet();
        resultFuture.completeExceptionally(e);
    }

    private ExecutorService createExecutor(int threads, boolean useVirtualThreads) {
        if (useVirtualThreads) {
            return Executors.newVirtualThreadPerTaskExecutor();
        } else {
            return new ThreadPoolExecutor(
                    threads,
                    threads,
                    60L,
                    TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(1000),
                    new ThreadFactory() {
                        private final AtomicInteger count = new AtomicInteger(0);

                        @Override
                        public Thread newThread(Runnable r) {
                            Thread thread = new Thread(r);
                            thread.setName("stream-processor-" + count.incrementAndGet());
                            thread.setDaemon(true);
                            return thread;
                        }
                    },
                    new ThreadPoolExecutor.CallerRunsPolicy()
            );
        }
    }

    private void validateConfig(ProcessConfig<T, R> config) {
        if (config.getDataFetcher() == null) {
            throw new IllegalArgumentException("DataFetcher cannot be null");
        }
        if (config.getDataConverter() == null) {
            throw new IllegalArgumentException("DataConverter cannot be null");
        }
        if (config.getDataSaver() == null) {
            throw new IllegalArgumentException("DataSaver cannot be null");
        }
    }

    /**
     * 停止处理
     */
    public void shutdown() {
        running = false;
        processExecutor.shutdown();
        saveExecutor.shutdown();
        try {
            if (!processExecutor.awaitTermination(30, TimeUnit.SECONDS)) {
                processExecutor.shutdownNow();
            }
            if (!saveExecutor.awaitTermination(30, TimeUnit.SECONDS)) {
                saveExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            processExecutor.shutdownNow();
            saveExecutor.shutdownNow();
        }
    }

    /**
     * 获取处理指标历史
     */
    public List<ProcessMetrics> getMetricsHistory() {
        return new ArrayList<>(metricsHistory);
    }

    /**
     * 获取当前活动处理数
     */
    public int getActiveProcessCount() {
        return activeProcesses.get();
    }

    /**
     * 获取处理队列大小
     */
    public int getQueueSize() {
        return processQueue.size();
    }

    /**
     * 处理配置
     */
    @Data
    @Builder
    public static class ProcessConfig<T, R> {
        // 基础配置
        private String processName;
        private int batchSize;
        private int processThreads;
        private int saveThreads;
        private boolean useVirtualThreads;

        // 数据处理函数
        private Function<Integer, List<T>> dataFetcher;      // 数据获取函数
        private Function<T, R> dataConverter;                // 数据转换函数
        private Consumer<List<R>> dataSaver;                 // 数据保存函数
        private Consumer<ProcessMetrics> progressCallback;    // 进度回调函数

        // 监控配置
        private long timeoutSeconds;
        private int maxRetries;
        private long retryDelayMs;
    }

    /**
     * 处理指标
     */
    @Data
    @Builder
    public static class ProcessMetrics {
        private String processName;
        private long totalItems;
        private long processedItems;
        private long failedItems;
        private long startTime;
        private long endTime;
        private double progressPercentage;
        private Map<String, Object> customMetrics;
    }
}
```

## spring.factories

```
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
com.study.collect.business.testcase.config.TestCaseAutoConfiguration
```

