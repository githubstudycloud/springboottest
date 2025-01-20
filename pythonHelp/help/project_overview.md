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
                                        CollectResultEntity.java
                                        UriEntity.java
                                        VersionEntity.java
                                    manager/
                                        CollectTaskManager.java
                                        QueueManager.java
                                    model/
                                        PageResult.java
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
                                            VersionResponse.java
                                            parse/
                                                CommonResponseParser.java
                                                HttpResponseParser.java
                                                UriCountResponseParser.java
                                                UriDetailResponseParser.java
                                                UriListResponseParser.java
                                                VersionResponseParser.java
                                    repository/
                                        CollectResultRepository.java
                                        UriRepository.java
                                    service/
                                        TableSchemaManager.java
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
    private static ApplicationContext applicationContext;
    private static final ThreadLocal<String> versionHolder = new ThreadLocal<>();

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        applicationContext = context;
    }

    public String getCollectionName(String baseCollection) {
        String version = versionHolder.get();
        return version != null ? baseCollection + "_" + version : baseCollection;
    }

    public static void setVersion(String version) {
        versionHolder.set(version);
    }

    public static void clearVersion() {
        versionHolder.remove();
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
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return ResponseEntity
                .badRequest()
                .body(AsyncResponse.<Void>builder()
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
                .body(AsyncResponse.<Void>builder()
                        .status("ERROR")
                        .message("Invalid parameters: " + errors)
                        .build());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<AsyncResponse<Void>> handleConstraintViolation(
            ConstraintViolationException ex) {
        String errors = ex.getConstraintViolations()
                .stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining(", "));

        return ResponseEntity
                .badRequest()
                .body(AsyncResponse.<Void>builder()
                        .status("ERROR")
                        .message("Validation failed: " + errors)
                        .build());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<AsyncResponse<Void>> handleIllegalArgument(
            IllegalArgumentException ex) {
        return ResponseEntity
                .badRequest()
                .body(AsyncResponse.<Void>builder()
                        .status("ERROR")
                        .message("Invalid argument: " + ex.getMessage())
                        .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<AsyncResponse<Void>> handleAllExceptions(Exception ex) {
        log.error("Unexpected error", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(AsyncResponse.<Void>builder()
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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

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
        executor.setKeepAliveSeconds((int)CollectionConstants.KEEP_ALIVE_TIME);
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
        executor.setKeepAliveSeconds((int)CollectionConstants.KEEP_ALIVE_TIME);
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

    public String getDescription() {
        return description;
    }

    public static VersionType fromString(String version) {
        return version != null && version.toLowerCase().contains("branch") ?
                BRANCH : TRUNK;
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
import com.study.collect.business.testcase.service.UriCollectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

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
    public ResponseEntity<AsyncResponse<String>> syncData(
            @RequestBody @Valid CollectParam param) {
        log.info("Received collect request for rootNode: {}", param.getRootNode());
        return ResponseEntity.ok(collectService.collectData(param));
    }

    @PostMapping("/delete")
    @ApiOperation("Delete URI data")
    public ResponseEntity<AsyncResponse<Long>> deleteData(
            @RequestBody @Valid DeleteParam param) {
        log.info("Received delete request for {} URIs", param.getUris().size());
        return ResponseEntity.ok(collectService.deleteData(param));
    }

    @GetMapping("/query")
    @ApiOperation("Query URI data")
    public ResponseEntity<Page<UriEntity>> queryUri(
            @Valid QueryParam param) {
        return ResponseEntity.ok(collectService.queryUri(param));
    }

    @PostMapping("/batch-query")
    @ApiOperation("Batch query URIs")
    public ResponseEntity<List<UriEntity>> batchQueryUri(
            @RequestBody @NotEmpty(message = "URIs cannot be empty") List<String> uris,
            @RequestParam(required = false, defaultValue = "false") Boolean includeDeleted) {
        return ResponseEntity.ok(collectService.batchQueryUri(uris, includeDeleted));
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
    @ApiOperation("Get active tasks")
    public ResponseEntity<List<AsyncResponse<Void>>> getActiveTasks() {
        return ResponseEntity.ok(collectService.getActiveTasks());
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

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Field;

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

    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }
}
```

## CollectResultEntity.java

```java
package com.study.collect.business.testcase.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Document(collection = "collect_result")
@Data
@EqualsAndHashCode(callSuper = true)
@CompoundIndexes({
        @CompoundIndex(
                name = "rootnode_version_idx",
                def = "{'root_node': 1, 'uri_version': 1}",
                unique = true
        )
})
public class CollectResultEntity extends BaseEntity {

    @Field("root_node")
    private String rootNode;

    @Field("uri_version")
    private String uriVersion;

    @Field("total_uri_count")
    private Long totalUriCount;

    @Field("success_count")
    private Long successCount;

    @Field("failed_count")
    private Long failedCount;

    @Field("collect_start_time")
    private LocalDateTime collectStartTime;

    @Field("collect_end_time")
    private LocalDateTime collectEndTime;

    @Field("collect_status")
    private String collectStatus; // PROCESSING, SUCCESS, FAILED

    @Field("failed_uris")
    private List<String> failedUris;

    @Field("error_details")
    private Map<String, Object> errorDetails;

    @Field("retry_count")
    private Integer retryCount;

    @Field("last_retry_time")
    private LocalDateTime lastRetryTime;

    @Field("collect_type")
    private String collectType; // FULL, INCREMENT

    @Field("increment_start_time")
    private LocalDateTime incrementStartTime;

    @Field("increment_end_time")
    private LocalDateTime incrementEndTime;

    public void addFailedUri(String uri) {
        if (this.failedUris == null) {
            this.failedUris = new ArrayList<>();
        }
        this.failedUris.add(uri);
        this.failedCount++;
    }

    public void incrementSuccess() {
        if (this.successCount == null) {
            this.successCount = 0L;
        }
        this.successCount++;
    }

    public void markAsCompleted() {
        this.collectEndTime = LocalDateTime.now();
        this.collectStatus = "SUCCESS";
    }

    public void markAsFailed(String errorMessage) {
        this.collectEndTime = LocalDateTime.now();
        this.collectStatus = "FAILED";
        if (this.errorDetails == null) {
            this.errorDetails = new HashMap<>();
        }
        this.errorDetails.put("lastError", errorMessage);
        this.errorDetails.put("errorTime", LocalDateTime.now());
    }

    public boolean canRetry() {
        return this.retryCount == null || this.retryCount < 3;
    }

    public void incrementRetry() {
        if (this.retryCount == null) {
            this.retryCount = 0;
        }
        this.retryCount++;
        this.lastRetryTime = LocalDateTime.now();
    }
}
```

## UriEntity.java

```java
package com.study.collect.business.testcase.entity;

import com.study.collect.business.testcase.utils.HashUtil;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.PrePersist;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;
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
        )
})
public class UriEntity extends VersionEntity {
    private static final Logger log = LoggerFactory.getLogger(UriEntity.class);

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


    public void reset() {
//        super.reset(); // 调用父类的 reset TODO
        this.uri = null;
        this.uriHash = null;  // 继续清空 uriHash
        this.rootNode = null;
        this.versionType = null;
        this.uriVersion = null;
        this.details = null;
        this.deleted = false;
        this.version = 0L;
        this.versionCode = null;
        this.versionTime = null;
    }
}
```

## VersionEntity.java

```java
package com.study.collect.business.testcase.entity;

import com.study.collect.business.testcase.constant.CollectionConstants;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public abstract class VersionEntity extends BaseEntity {

    @Field("version_code")
    protected String versionCode;

    @Field("version_time")
    protected LocalDateTime versionTime;

    protected VersionEntity(String id) {
        super(id);
        initVersion();
    }

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
        return String.format("%s%s%s%d",
                CollectionConstants.VERSION_PREFIX,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")),
                CollectionConstants.VERSION_SEPARATOR,
                this.version);
    }

    @PrePersist
    @Override
    public void prePersist() {
        super.prePersist();
        if (this.versionCode == null) {
            initVersion();
        }
    }

    @PreUpdate
    @Override
    public void preUpdate() {
        super.preUpdate();
        upgradeVersion();
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
import java.util.*;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.Comparator;
import java.util.concurrent.*;
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

    private static class QueueItem<T> {
        final String id;
        final T item;
        volatile int priority;
        final CompletableFuture<Void> future;
        final Consumer<T> processor;

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

## CollectParam.java

```java
package com.study.collect.business.testcase.model.param;

import com.study.collect.business.testcase.constant.CollectionConstants;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Validated
public class CollectParam {
    @NotBlank(message = "rootNode cannot be empty")
    private String rootNode;

    private String version;

    @NotBlank(message = "serverUrl cannot be empty")
    private String serverUrl;

    private Boolean incremental = false;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @Min(value = 50, message = "batchSize must be greater than 50")
    @Max(value = 1000, message = "batchSize must be less than 1000")
    private Integer batchSize = CollectionConstants.DEFAULT_BATCH_SIZE;

    private Integer priority = 0;

    private Boolean allowDuplicate = false;

    private Integer maxRetries = CollectionConstants.HTTP_MAX_RETRY;

    private Integer timeout = 3600;

    private Boolean forceUpdate = false;

    private String taskId;
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

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AsyncResponse<T> {
    private String taskId;
    private String status;
    private Double progress;
    private String message;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private T result;

    public static <T> AsyncResponse<T> processing(String taskId) {
        return AsyncResponse.<T>builder()
                .taskId(taskId)
                .status("PROCESSING")
                .progress(0.0)
                .startTime(LocalDateTime.now())
                .build();
    }

    public static <T> AsyncResponse<T> success(String taskId, T result) {
        return AsyncResponse.<T>builder()
                .taskId(taskId)
                .status("COMPLETED")
                .progress(100.0)
                .result(result)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now())
                .build();
    }

    public static <T> AsyncResponse<T> error(String taskId, String message) {
        return AsyncResponse.<T>builder()
                .taskId(taskId)
                .status("ERROR")
                .message(message)
                .endTime(LocalDateTime.now())
                .build();
    }
}
```

## BaseResponse.java

```java
package com.study.collect.business.testcase.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// BaseResponse.java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse {
    private String code;
    private String message;

    public static BaseResponseBuilder builder() {
        return new BaseResponseBuilder();
    }

    public static class BaseResponseBuilder {
        private String code;
        private String message;

        BaseResponseBuilder() {
        }

        public BaseResponseBuilder code(String code) {
            this.code = code;
            return this;
        }

        public BaseResponseBuilder message(String message) {
            this.message = message;
            return this;
        }

        public BaseResponse build() {
            return new BaseResponse(code, message);
        }
    }
}


```

## PageResponse.java

```java
package com.study.collect.business.testcase.model.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
//
//
// PageResponse.java
@Data
@NoArgsConstructor
public class PageResponse<T> {
    private String code;
    private String message;
    private Long total;
    private List<T> items;

    public static <T> PageResponseBuilder<T> builder() {
        return new PageResponseBuilder<>();
    }

    public static class PageResponseBuilder<T> {
        private String code;
        private String message;
        private Long total;
        private List<T> items;

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

        public PageResponse<T> build() {
            PageResponse<T> response = new PageResponse<>();
            response.setCode(code);
            response.setMessage(message);
            response.setTotal(total);
            response.setItems(items);
            return response;
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

## VersionResponse.java

```java
package com.study.collect.business.testcase.model.response;


import lombok.Data;

//@Data
//@SuperBuilder
//@EqualsAndHashCode(callSuper = true)
//public class VersionResponse extends BaseResponse {
//    private String version;
//    private String versionType;
//    private String description;
//}
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

## CommonResponseParser.java

```java
package com.study.collect.business.testcase.model.response.parse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.function.Function;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommonResponseParser<T> implements HttpResponseParser<T> {
    private final ObjectMapper objectMapper;
    private final Function<JsonNode, T> valueParser;
    private final String parserName;

    @Override
    public T parse(String response) throws IOException {
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode valueNode = root.path("result").path("value");

            validateResponse(valueNode);

            return valueParser.apply(valueNode);
        } catch (Exception e) {
            log.error("Failed to parse {} response: {}", parserName, response, e);
            throw new IOException("Failed to parse " + parserName + " response", e);
        }
    }

    private void validateResponse(JsonNode valueNode) throws IOException {
        if (valueNode.isMissingNode() || valueNode.isNull()) {
            throw new IOException("Invalid response format: missing or null value");
        }
    }

    @Override
    public String parseError(String errorResponse) {
        try {
            JsonNode root = objectMapper.readTree(errorResponse);
            return root.path("message").asText("Unknown error");
        } catch (Exception e) {
            log.error("Failed to parse error response: {}", errorResponse, e);
            return "Failed to parse error response";
        }
    }

    public static <T> CommonResponseParser<T> create(
            ObjectMapper objectMapper,
            Function<JsonNode, T> valueParser,
            String parserName) {
```

## HttpResponseParser.java

```java
package com.study.collect.business.testcase.model.response.parse;

import java.io.IOException;

/**
 * HTTP响应解析器接口
 * @param <T> 解析结果类型
 */
public interface HttpResponseParser<T> {
    /**
     * 解析HTTP响应
     * @param response 响应字符串
     * @return 解析后的结果
     * @throws IOException 解析异常
     */
    T parse(String response) throws IOException;

    /**
     * 从错误响应中提取错误信息
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
            JsonNode valueNode = root.path("result").path("value");

            validateResponse(valueNode);

            return valueNode.asInt();
        } catch (Exception e) {
            log.error("Failed to parse URI count response: {}", response, e);
            throw new IOException("Failed to parse URI count response", e);
        }
    }

    private void validateResponse(JsonNode valueNode) throws IOException {
        if (!valueNode.isNumber()) {
            throw new IOException("Invalid response format: value must be a number");
        }
    }

    @Override
    public String parseError(String errorResponse) {
        try {
            JsonNode root = objectMapper.readTree(errorResponse);
            return root.path("message").asText("Unknown error");
        } catch (Exception e) {
            log.error("Failed to parse error response: {}", errorResponse, e);
            return "Failed to parse error response";
        }
    }
}
```

## UriDetailResponseParser.java

```java
package com.study.collect.business.testcase.model.response.parse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class UriDetailResponseParser implements HttpResponseParser<List<Map<String, Object>>> {
    private final ObjectMapper objectMapper;

    @Override
    public List<Map<String, Object>> parse(String response) throws IOException {
        try {
            JsonNode root = objectMapper.readTree(response);
            validateResponse(root);

            List<Map<String, Object>> details = new ArrayList<>();
            JsonNode items = root.path("items");
            if (items.isArray()) {
                items.forEach(item -> {
                    try {
                        Map<String, Object> detail = convertToMap(item);
                        if (detail != null && !detail.isEmpty()) {
                            details.add(detail);
                        }
                    } catch (Exception e) {
                        log.error("Failed to parse URI detail item: {}", item, e);
                    }
                });
            }

            return details;
        } catch (Exception e) {
            log.error("Failed to parse URI details response: {}", response, e);
            throw new IOException("Failed to parse URI details response", e);
        }
    }

    private Map<String, Object> convertToMap(JsonNode node) {
        Map<String, Object> result = new LinkedHashMap<>();
        node.fields().forEachRemaining(entry -> {
            String key = entry.getKey();
            JsonNode valueNode = entry.getValue();
            Object value = convertJsonNode(valueNode);
            if (value != null) {
                result.put(key, value);
            }
        });
        return result;
    }

    private Object convertJsonNode(JsonNode node) {
        if (node.isNull()) {
            return null;
        } else if (node.isTextual()) {
            return node.asText();
        } else if (node.isNumber()) {
            return node.numberValue();
        } else if (node.isBoolean()) {
            return node.asBoolean();
        } else if (node.isArray()) {
            List<Object> list = new ArrayList<>();
            node.forEach(item -> {
                Object value = convertJsonNode(item);
                if (value != null) {
                    list.add(value);
                }
            });
            return list;
        } else if (node.isObject()) {
            return convertToMap(node);
        } else {
            return node.toString();
        }
    }

    private void validateResponse(JsonNode root) throws IOException {
        if (!root.has("items")) {
            throw new IOException("Invalid response format: missing items field");
        }
    }

    @Override
    public String parseError(String errorResponse) {
        try {
            JsonNode root = objectMapper.readTree(errorResponse);
            return root.path("message").asText("Unknown error");
        } catch (Exception e) {
            log.error("Failed to parse error response: {}", errorResponse, e);
            return "Failed to parse error response";
        }
    }
}
```

## UriListResponseParser.java

```java
package com.study.collect.business.testcase.model.response.parse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.collect.business.testcase.model.response.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UriListResponseParser implements HttpResponseParser<PageResponse<String>> {
    private final ObjectMapper objectMapper;

    @Override
    public PageResponse<String> parse(String response) throws IOException {
        try {
            JsonNode root = objectMapper.readTree(response);
            validateResponse(root);

            PageResponse<String> pageResponse = new PageResponse<>();
            pageResponse.setCode(root.path("code").asText());
            pageResponse.setMessage(root.path("message").asText());
            pageResponse.setTotal(root.path("total").asLong());

            List<String> uris = new ArrayList<>();
            JsonNode items = root.path("items");
            if (items.isArray()) {
                items.forEach(item -> {
                    String uri = item.path("uri").asText();
                    if (uri != null && !uri.isEmpty()) {
                        uris.add(uri);
                    }
                });
            }

            pageResponse.setItems(uris);
            return pageResponse;
        } catch (Exception e) {
            log.error("Failed to parse URI list response: {}", response, e);
            throw new IOException("Failed to parse URI list response", e);
        }
    }

    private void validateResponse(JsonNode root) throws IOException {
        if (!root.has("code") || !root.has("total") || !root.has("items")) {
            throw new IOException("Invalid response format: missing required fields");
        }
    }

    @Override
    public String parseError(String errorResponse) {
        try {
            JsonNode root = objectMapper.readTree(errorResponse);
            return root.path("message").asText("Unknown error");
        } catch (Exception e) {
            log.error("Failed to parse error response: {}", errorResponse, e);
            return "Failed to parse error response";
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
import com.study.collect.business.testcase.model.response.VersionResponse;
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
public class VersionResponseParser implements HttpResponseParser<PageResponse<VersionResponse>> {
    private final ObjectMapper objectMapper;

    @Override
    public PageResponse<VersionResponse> parse(String response) throws IOException {
        try {
            JsonNode root = objectMapper.readTree(response);
            validateResponse(root);

            PageResponse<VersionResponse> pageResponse = new PageResponse<>();
            pageResponse.setCode(root.path("code").asText());
            pageResponse.setMessage(root.path("message").asText());
            pageResponse.setTotal(root.path("total").asLong());

            List<VersionResponse> versions = new ArrayList<>();
            JsonNode items = root.path("items");
            if (items.isArray()) {
                items.forEach(item -> {
                    try {
                        VersionResponse version = parseVersionItem(item);
                        if (version != null) {
                            versions.add(version);
                        }
                    } catch (Exception e) {
                        log.error("Failed to parse version item: {}", item, e);
                    }
                });
            }

            pageResponse.setItems(versions);
            return pageResponse;
        } catch (Exception e) {
            log.error("Failed to parse version response: {}", response, e);
            throw new IOException("Failed to parse version response", e);
        }
    }

    private VersionResponse parseVersionItem(JsonNode item) {
        return VersionResponse.builder()
                .version(item.path("version").asText())
                .versionType(item.path("versionType").asText())
                .description(item.path("description").asText())
                .createTime(parseDateTime(item.path("createTime").asText()))
                .updateTime(parseDateTime(item.path("updateTime").asText()))
                .status(item.path("status").asText())
                .sort(item.path("sort").asInt())
                .build();
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {
        try {
            return LocalDateTime.parse(dateTimeStr);
        } catch (Exception e) {
            log.debug("Failed to parse datetime: {}", dateTimeStr);
            return null;
        }
    }

    private void validateResponse(JsonNode root) throws IOException {
        if (!root.has("code") || !root.has("total") || !root.has("items")) {
            throw new IOException("Invalid response format: missing required fields");
        }
    }

    @Override
    public String parseError(String errorResponse) {
        try {
            JsonNode root = objectMapper.readTree(errorResponse);
            return root.path("message").asText("Unknown error");
        } catch (Exception e) {
            log.error("Failed to parse error response: {}", errorResponse, e);
            return "Failed to parse error response";
        }
    }
}
```

## CollectResultRepository.java

```java
package com.study.collect.business.testcase.repository;

import com.study.collect.business.testcase.entity.CollectResultEntity;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CollectResultRepository {

    private final MongoTemplate mongoTemplate;
    private final MeterRegistry meterRegistry;

    public CollectResultEntity save(CollectResultEntity entity) {
        Timer.Sample timer = Timer.start(meterRegistry);
        try {
            CollectResultEntity result = mongoTemplate.save(entity);
            recordMetrics("save", timer);
            return result;
        } catch (Exception e) {
            recordError("save");
            log.error("Failed to save collect result", e);
            throw new RuntimeException("Failed to save collect result", e);
        }
    }

    public CollectResultEntity findByRootNodeAndVersion(String rootNode, String version) {
        Timer.Sample timer = Timer.start(meterRegistry);
        try {
            Query query = new Query(Criteria.where("root_node").is(rootNode)
                    .and("uri_version").is(version));
            CollectResultEntity result = mongoTemplate.findOne(query, CollectResultEntity.class);
            recordMetrics("find", timer);
            return result;
        } catch (Exception e) {
            recordError("find");
            log.error("Failed to find collect result", e);
            throw new RuntimeException("Failed to find collect result", e);
        }
    }

    public Page<CollectResultEntity> findByRootNode(String rootNode, Pageable pageable) {
        Timer.Sample timer = Timer.start(meterRegistry);
        try {
            Query query = new Query(Criteria.where("root_node").is(rootNode))
                    .with(pageable);

            long total = mongoTemplate.count(query, CollectResultEntity.class);
            List<CollectResultEntity> content = mongoTemplate.find(query, CollectResultEntity.class);

            recordMetrics("find_page", timer);
            return new PageImpl<>(content, pageable, total);
        } catch (Exception e) {
            recordError("find_page");
            log.error("Failed to find collect results", e);
            throw new RuntimeException("Failed to find collect results", e);
        }
    }

    public List<CollectResultEntity> findFailedCollects(LocalDateTime before) {
        Timer.Sample timer = Timer.start(meterRegistry);
        try {
            Query query = new Query(Criteria.where("collect_status").is("FAILED")
                    .and("collect_end_time").lt(before));

            List<CollectResultEntity> results = mongoTemplate.find(query, CollectResultEntity.class);
            recordMetrics("find_failed", timer);
            return results;
        } catch (Exception e) {
            recordError("find_failed");
            log.error("Failed to find failed collects", e);
            throw new RuntimeException("Failed to find failed collects", e);
        }
    }

    public List<CollectResultEntity> findIncompleteCollects(LocalDateTime before) {
        Timer.Sample timer = Timer.start(meterRegistry);
        try {
            Query query = new Query(Criteria.where("collect_status").is("PROCESSING")
                    .and("collect_start_time").lt(before));

            List<CollectResultEntity> results = mongoTemplate.find(query, CollectResultEntity.class);
            recordMetrics("find_incomplete", timer);
            return results;
        } catch (Exception e) {
            recordError("find_incomplete");
            log.error("Failed to find incomplete collects", e);
            throw new RuntimeException("Failed to find incomplete collects", e);
        }
    }

    public void updateCollectProgress(String rootNode, String version,
                                      long processedCount, String status) {
        Timer.Sample timer = Timer.start(meterRegistry);
        try {
            Query query = new Query(Criteria.where("root_node").is(rootNode)
                    .and("uri_version").is(version));
            Update update = new Update()
                    .set("success_count", processedCount)
                    .set("collect_status", status)
                    .set("update_time", LocalDateTime.now());

            mongoTemplate.updateFirst(query, update, CollectResultEntity.class);
            recordMetrics("update_progress", timer);
        } catch (Exception e) {
            recordError("update_progress");
            log.error("Failed to update collect progress", e);
            throw new RuntimeException("Failed to update collect progress", e);
        }
    }

    public void addFailedUri(String rootNode, String version, String uri) {
        Timer.Sample timer = Timer.start(meterRegistry);
        try {
            Query query = new Query(Criteria.where("root_node").is(rootNode)
                    .and("uri_version").is(version));
            Update update = new Update()
                    .addToSet("failed_uris", uri)
                    .inc("failed_count", 1)
                    .set("update_time", LocalDateTime.now());

            mongoTemplate.updateFirst(query, update, CollectResultEntity.class);
            recordMetrics("add_failed", timer);
        } catch (Exception e) {
            recordError("add_failed");
            log.error("Failed to add failed URI", e);
            throw new RuntimeException("Failed to add failed URI", e);
        }
    }

    public void markAsCompleted(String rootNode, String version,
                                long totalCount, long successCount) {
        Timer.Sample timer = Timer.start(meterRegistry);
        try {
            Query query = new Query(Criteria.where("root_node").is(rootNode)
                    .and("uri_version").is(version));
            Update update = new Update()
                    .set("collect_status", "SUCCESS")
                    .set("total_uri_count", totalCount)
                    .set("success_count", successCount)
                    .set("collect_end_time", LocalDateTime.now())
                    .set("update_time", LocalDateTime.now());

            mongoTemplate.updateFirst(query, update, CollectResultEntity.class);
            recordMetrics("mark_completed", timer);
        } catch (Exception e) {
            recordError("mark_completed");
            log.error("Failed to mark collect as completed", e);
            throw new RuntimeException("Failed to mark collect as completed", e);
        }
    }

    public void markAsFailed(String rootNode, String version, String error) {
        Timer.Sample timer = Timer.start(meterRegistry);
        try {
            Query query = new Query(Criteria.where("root_node").is(rootNode)
                    .and("uri_version").is(version));
            Update update = new Update()
                    .set("collect_status", "FAILED")
                    .set("error_details.lastError", error)
                    .set("error_details.errorTime", LocalDateTime.now())
                    .set("collect_end_time", LocalDateTime.now())
                    .set("update_time", LocalDateTime.now());

            mongoTemplate.updateFirst(query, update, CollectResultEntity.class);
            recordMetrics("mark_failed", timer);
        } catch (Exception e) {
            recordError("mark_failed");
            log.error("Failed to mark collect as failed", e);
            throw new RuntimeException("Failed to mark collect as failed", e);
        }
    }

    private void recordMetrics(String operation, Timer.Sample timer) {
        timer.stop(meterRegistry.timer("mongodb.operation", "type", operation));
        meterRegistry.counter("mongodb.operation.total", "type", operation).increment();
    }

    private void recordError(String operation) {
        meterRegistry.counter("mongodb.operation.error", "type", operation).increment();
    }
}

```

## UriRepository.java

```java
package com.study.collect.business.testcase.repository;

import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.*;
import com.study.collect.business.testcase.config.DynamicCollectionIndexConfiguration;
import com.study.collect.business.testcase.constant.CollectionConstants;
import com.study.collect.business.testcase.entity.UriEntity;
import com.study.collect.business.testcase.model.PageResult;
import com.study.collect.business.testcase.utils.HashUtil;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class UriRepository {
    private final MongoTemplate mongoTemplate;

    private final MongoOperations mongoOperations;
    private final DynamicCollectionIndexConfiguration indexConfiguration;

    public UriRepository(MongoTemplate mongoTemplate,
                   MongoOperations mongoOperations,
                         DynamicCollectionIndexConfiguration indexConfiguration
    ) {
        this.mongoTemplate = mongoTemplate;
        this.mongoOperations = mongoOperations;
        this.indexConfiguration= indexConfiguration;
    }

    /**
     * 生成集合名称
     */
    private String getCollectionName(String rootNode) {
        return String.format("%s_%s", CollectionConstants.URI_COLLECTION_PREFIX, rootNode);
    }

    /**
     * 批量插入或更新
     */
    public BulkWriteResult batchUpsert(String rootNode, List<UriEntity> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return null;
        }


        // 验证所有实体的 uriHash
        entities.forEach(entity -> {
            if (entity.getUriHash() == null && entity.getUri() != null) {
                entity.setUriHash(HashUtil.hash(entity.getUri()));
            }
        });

        String collectionName = getCollectionName(rootNode);

        // 确保索引存在
        ensureIndexes(rootNode,collectionName);
        MongoCollection<Document> collection = mongoTemplate.getCollection(collectionName);

        List<WriteModel<Document>> operations = new ArrayList<>();
        for (UriEntity entity : entities) {
            Document query = new Document("uri_hash", entity.getUriHash());
            Document doc = convertEntityToDocument(entity);
            operations.add(new UpdateOneModel<>(
                    query,
                    new Document("$set", doc),
                    new UpdateOptions().upsert(true)
            ));
        }

        try {
            BulkWriteOptions options = new BulkWriteOptions()
                    .ordered(false)
                    .bypassDocumentValidation(true);
            return collection.bulkWrite(operations, options);
        } catch (Exception e) {
            log.error("Failed to batch upsert to collection {}", collectionName, e);
            throw new RuntimeException("Batch upsert failed", e);
        }
    }


    /**
     * 批量插入或更新
     */
    public BulkWriteResult batchUpsertSync(String rootNode, List<UriEntity> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return null;
        }


        // 验证所有实体的 uriHash
        entities.forEach(entity -> {
            if (entity.getUriHash() == null && entity.getUri() != null) {
                entity.setUriHash(HashUtil.hash(entity.getUri()));
            }
        });

        String collectionName = getCollectionName(rootNode);

        // 确保索引存在
        ensureIndexes(rootNode,collectionName);
        MongoCollection<Document> collection = mongoTemplate.getCollection(collectionName);

        List<WriteModel<Document>> operations = new ArrayList<>();
        for (UriEntity entity : entities) {
            Document query = new Document("uri_hash", entity.getUriHash());
            Document doc = convertEntityToDocument(entity);
            operations.add(new UpdateOneModel<>(
                    query,
                    new Document("$set", doc),
                    new UpdateOptions().upsert(true)
            ));
        }

        try {
            BulkWriteOptions options = new BulkWriteOptions()
//                    .ordered(false)
                    .ordered(true)  // 改为有序执行
                    .bypassDocumentValidation(true);

//            // 记录指标
//            recordMetrics("upsert", timer, entities.size(), result.getModifiedCount());
            // 确保数据已写入
            collection.find(new Document("uri_hash",
                    new Document("$in",
                            entities.stream()
                                    .map(UriEntity::getUriHash)
                                    .collect(Collectors.toList())
                    )
            )).first();
            return collection.bulkWrite(operations, options);
        } catch (Exception e) {
            log.error("Failed to batch upsert to collection {}", collectionName, e);
            throw new RuntimeException("Batch upsert failed", e);
        }
    }


    /**
     * 确保集合索引存在
     */
    private void ensureIndexes(String rootNode,String collectionName) {
        try {
            // 如果集合不存在或索引不完整，创建索引
            if (!mongoTemplate.collectionExists(collectionName)) {
                indexConfiguration.createIndexesForCollection(rootNode);
            } else {
                // 检查索引是否完整
                indexConfiguration.checkIndexes(collectionName);
            }
        } catch (Exception e) {
            log.error("Failed to ensure indexes for rootNode: {}", rootNode, e);
        }
    }

    /**
     * 批量软删除
     */
    public long batchSoftDelete(String rootNode, List<String> uris) {
        if (CollectionUtils.isEmpty(uris)) {
            return 0L;
        }

        List<String> uriHashes = uris.stream()
                .map(HashUtil::hash)
                .collect(Collectors.toList());

        Query query = new Query(Criteria.where("uri_hash").in(uriHashes));
        Update update = new Update()
                .set("is_deleted", true)
                .set("update_time", LocalDateTime.now());

        try {
            return mongoTemplate.updateMulti(
                    query,
                    update,
                    getCollectionName(rootNode)
            ).getModifiedCount();
        } catch (Exception e) {
            log.error("Failed to batch soft delete in collection {}", rootNode, e);
            throw new RuntimeException("Batch soft delete failed", e);
        }
    }

    /**
     * 批量硬删除
     */
    public long batchHardDelete(String rootNode, List<String> uris) {
        if (CollectionUtils.isEmpty(uris)) {
            return 0L;
        }

        List<String> uriHashes = uris.stream()
                .map(HashUtil::hash)
                .collect(Collectors.toList());

        Query query = new Query(Criteria.where("uri_hash").in(uriHashes));

        try {
            return mongoTemplate.remove(
                    query,
                    UriEntity.class,
                    getCollectionName(rootNode)
            ).getDeletedCount();
        } catch (Exception e) {
            log.error("Failed to batch hard delete in collection {}", rootNode, e);
            throw new RuntimeException("Batch hard delete failed", e);
        }
    }

    /**
     * 分页查询
     */
    public Page<UriEntity> findByCondition(
            String rootNode,
            String version,
            String versionType,
            Boolean includeDeleted,
            Pageable pageable
    ) {
        Criteria criteria = new Criteria();

        if (version != null) {
            criteria.and("uri_version").is(version);
        }
        if (versionType != null) {
            criteria.and("version_type").is(versionType);
        }
        if (!includeDeleted) {
            criteria.and("is_deleted").is(false);
        }

        Query query = new Query(criteria).with(pageable);
        String collectionName = getCollectionName(rootNode);

        try {
            long total = mongoTemplate.count(query, UriEntity.class, collectionName);
            List<UriEntity> content = mongoTemplate.find(query, UriEntity.class, collectionName);
            return new PageImpl<>(content, pageable, total);
        } catch (Exception e) {
            log.error("Failed to query collection {}", collectionName, e);
            throw new RuntimeException("Query failed", e);
        }
    }

    /**
     * 批量查询
     */
    public List<UriEntity> batchQuery(
            List<String> uris,
            Function<String, String> rootNodeResolver,
            Boolean includeDeleted
    ) {
        if (CollectionUtils.isEmpty(uris)) {
            return new ArrayList<>();
        }

        // 按rootNode分组
        Map<String, List<String>> groupedUris = uris.stream()
                .collect(Collectors.groupingBy(rootNodeResolver));

        List<UriEntity> results = new ArrayList<>();

        for (Map.Entry<String, List<String>> entry : groupedUris.entrySet()) {
            String rootNode = entry.getKey();
            List<String> uriGroup = entry.getValue();

            List<String> uriHashes = uriGroup.stream()
                    .map(HashUtil::hash)
                    .collect(Collectors.toList());

            Criteria criteria = Criteria.where("uri_hash").in(uriHashes);
            if (!includeDeleted) {
                criteria.and("is_deleted").is(false);
            }

            Query query = new Query(criteria);
            String collectionName = getCollectionName(rootNode);

            try {
                List<UriEntity> groupResults = mongoTemplate.find(
                        query,
                        UriEntity.class,
                        collectionName
                );
                results.addAll(groupResults);
            } catch (Exception e) {
                log.error("Failed to query collection {}", collectionName, e);
                // 继续处理其他分组
            }
        }

        return results;
    }

    /**
     * 删除不存在的URI
     */
    public void deleteNotInUris(String rootNode, Set<String> uriHashes) {
        Query query = new Query(
                Criteria.where("uri_hash").nin(uriHashes)
        );

        try {
            mongoTemplate.remove(
                    query,
                    UriEntity.class,
                    getCollectionName(rootNode)
            );
        } catch (Exception e) {
            log.error("Failed to delete non-existing URIs in collection {}", rootNode, e);
            throw new RuntimeException("Delete non-existing URIs failed", e);
        }
    }

    private Document convertEntityToDocument(UriEntity entity) {
        // 确保 uriHash 存在
        if (entity.getUriHash() == null && entity.getUri() != null) {
            entity.setUriHash(HashUtil.hash(entity.getUri()));
        }
        Document doc = new Document();
        doc.put("uri", entity.getUri());
        doc.put("uri_hash", entity.getUriHash());
        doc.put("root_node", entity.getRootNode());
        doc.put("version_type", entity.getVersionType());
        doc.put("uri_version", entity.getUriVersion());
        doc.put("details", entity.getDetails());
        doc.put("version", entity.getVersion());
        doc.put("version_code", entity.getVersionCode());
        doc.put("version_time", entity.getVersionTime());
        doc.put("update_time", LocalDateTime.now());
        doc.put("is_deleted", false);

        if (entity.getCreateTime() == null) {
            doc.put("create_time", LocalDateTime.now());
        }

        return doc;
    }



    /**
     * 使用原生命令条件分页查询uriHash
     * @param rootNode 根节点
     * @param version 版本
     * @param versionType 版本类型
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @return uriHash列表
     */
    public List<String> findUriHashesNativeWithPage(String rootNode,
                                                    String version,
                                                    String versionType,
                                                    int page,
                                                    int size) {
        String collectionName = getCollectionName(rootNode);
        MongoCollection<Document> collection = mongoTemplate.getCollection(collectionName);

        // 构建查询条件
        Document query = new Document();
        if (rootNode != null) {
            query.append("root_node", rootNode);
        }
        if (version != null) {
            query.append("uri_version", version);
        }
        if (versionType != null) {
            query.append("version_type", versionType);
        }

        // 构建聚合管道
        List<Document> pipeline = Arrays.asList(
                new Document("$match", query),
                new Document("$project", new Document("uri_hash", 1).append("_id", 0)),
                new Document("$skip", (long) (page - 1) * size),
                new Document("$limit", size)
        );

        try {
            return collection.aggregate(pipeline)
                    .map(doc -> doc.getString("uri_hash"))
                    .into(new ArrayList<>());
        } catch (Exception e) {
            log.error("Failed to execute native query in collection {}", collectionName, e);
            throw new RuntimeException("Query execution failed", e);
        }
    }

    /**
     * 获取满足条件的总数
     */
    public long countUriHashesNative(String rootNode, String version, String versionType) {
        String collectionName = getCollectionName(rootNode);
        MongoCollection<Document> collection = mongoTemplate.getCollection(collectionName);

        Document query = new Document();
        if (rootNode != null) {
            query.append("root_node", rootNode);
        }
        if (version != null) {
            query.append("uri_version", version);
        }
        if (versionType != null) {
            query.append("version_type", versionType);
        }

        try {
            return collection.countDocuments(query);
        } catch (Exception e) {
            log.error("Failed to count documents in collection {}", collectionName, e);
            throw new RuntimeException("Count documents failed", e);
        }
    }

    /**
     * 查询并返回分页结果
     */
    public PageResult<String> findUriHashesPage(String rootNode,
                                                String version,
                                                String versionType,
                                                int page,
                                                int size) {
        try {
            long total = countUriHashesNative(rootNode, version, versionType);
            List<String> items = findUriHashesNativeWithPage(rootNode, version, versionType, page, size);

            return PageResult.<String>builder()
                    .total(total)
                    .page(page)
                    .size(size)
                    .totalPages((int) Math.ceil((double) total / size))
                    .items(items)
                    .build();
        } catch (Exception e) {
            log.error("Failed to get paged results for rootNode {}", rootNode, e);
            throw new RuntimeException("Failed to get paged results", e);
        }
    }

    /**
     * 如果数据量很大，使用流式处理
     */
    public void streamUriHashesNative(String rootNode,
                                      String version,
                                      String versionType,
                                      Consumer<String> consumer) {
        String collectionName = getCollectionName(rootNode);
        MongoCollection<Document> collection = mongoTemplate.getCollection(collectionName);

        Document query = new Document();
        if (rootNode != null) {
            query.append("rootNode", rootNode);
        }
        if (version != null) {
            query.append("uri_version", version);
        }
        if (versionType != null) {
            query.append("version_type", versionType);
        }

        List<Document> pipeline = Arrays.asList(
                new Document("$match", query),
                new Document("$project", new Document("uri_hash", 1).append("_id", 0))
        );

        try (MongoCursor<Document> cursor = collection.aggregate(pipeline).iterator()) {
            while (cursor.hasNext()) {
                consumer.accept(cursor.next().getString("uri_hash"));
            }
        } catch (Exception e) {
            log.error("Failed to stream documents from collection {}", collectionName, e);
            throw new RuntimeException("Streaming documents failed", e);
        }
    }
}
```

## TableSchemaManager.java

```java
package com.study.collect.business.testcase.service;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.study.collect.business.testcase.common.utils.TableNameHelper;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 表结构管理器
 * 负责创建和维护集合及其索引
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TableSchemaManager {

    private final MongoTemplate mongoTemplate;
    private final MeterRegistry meterRegistry;
    private final ConcurrentHashMap<String, Boolean> initializedTables = new ConcurrentHashMap<>();

    /**
     * 确保表结构存在并正确
     * @param rootNode 根节点
     */
    public void ensureTableSchema(String rootNode) {
        String tableName = TableNameHelper.getTableName(rootNode);
        if (initializedTables.containsKey(tableName)) {
            return;
        }

        Timer.Sample timer = Timer.start(meterRegistry);
        try {
            // 1. 检查并创建集合
            if (!collectionExists(tableName)) {
                createCollection(tableName);
            }

            // 2. 确保所需索引存在
            ensureIndexes(tableName);

            initializedTables.put(tableName, true);
            recordMetrics("ensure_schema", timer);
            log.info("Successfully ensured table schema for {}", tableName);
        } catch (Exception e) {
            recordError("ensure_schema");
            log.error("Failed to ensure table schema for {}", tableName, e);
            throw new RuntimeException("Failed to ensure table schema", e);
        }
    }

    /**
     * 清理指定时间之前的历史版本表
     */
    public void cleanupHistoryTables(int daysToKeep) {
        Timer.Sample timer = Timer.start(meterRegistry);
        try {
            MongoDatabase db = mongoTemplate.getDb();
            List<String> tablesToDelete = new ArrayList<>();

            // 1. 获取所有集合
            for (String collectionName : db.listCollectionNames()) {
                if (isHistoryTable(collectionName)) {
                    // 检查表的最后修改时间
                    Document stats = db.runCommand(new Document("collStats", collectionName));
                    if (isTableExpired(stats, daysToKeep)) {
                        tablesToDelete.add(collectionName);
                    }
                }
            }

            // 2. 删除过期的表
            for (String tableName : tablesToDelete) {
                try {
                    db.getCollection(tableName).drop();
                    log.info("Dropped expired table: {}", tableName);
                } catch (Exception e) {
                    log.error("Failed to drop table: {}", tableName, e);
                }
            }

            recordMetrics("cleanup_tables", timer);
        } catch (Exception e) {
            recordError("cleanup_tables");
            log.error("Failed to cleanup history tables", e);
            throw new RuntimeException("Failed to cleanup history tables", e);
        }
    }

    private boolean collectionExists(String tableName) {
        return mongoTemplate.collectionExists(tableName);
    }

    private void createCollection(String tableName) {
        mongoTemplate.createCollection(tableName);
    }

    private void ensureIndexes(String tableName) {
        MongoDatabase db = mongoTemplate.getDb();

        // URI索引
        db.getCollection(tableName).createIndex(
                Indexes.ascending("uri"),
                new IndexOptions().background(true)
        );

        // URI哈希索引
        db.getCollection(tableName).createIndex(
                Indexes.ascending("uri_hash"),
                new IndexOptions().unique(true).background(true)
        );

        // 复合查询索引
        db.getCollection(tableName).createIndex(
                Indexes.compoundIndex(
                        Indexes.ascending("root_node"),
                        Indexes.ascending("version_type"),
                        Indexes.ascending("uri_version"),
                        Indexes.ascending("is_deleted")
                ),
                new IndexOptions().background(true)
        );

        // 更新时间索引
        db.getCollection(tableName).createIndex(
                Indexes.ascending("update_time"),
                new IndexOptions().background(true)
        );

        // 版本代码索引
        db.getCollection(tableName).createIndex(
                Indexes.ascending("version_code"),
                new IndexOptions().background(true)
        );
    }

    private boolean isHistoryTable(String tableName) {
        return tableName.startsWith("uri_collect_") && tableName.contains("_history_");
    }

    private boolean isTableExpired(Document stats, int daysToKeep) {
        // MongoDB stats中的时间戳是以秒为单位的
        long modifyTimestamp = stats.getLong("wiredTiger")
                .getDocument("creationTime")
                .getLong("secs");
        long currentTime = System.currentTimeMillis() / 1000;
        long expirationTime = currentTime - TimeUnit.DAYS.toSeconds(daysToKeep);
        return modifyTimestamp < expirationTime;
    }

    private void recordMetrics(String operation, Timer.Sample timer) {
        timer.stop(meterRegistry.timer("mongodb.schema", "type", operation));
        meterRegistry.counter("mongodb.schema.total", "type", operation).increment();
    }

    private void recordError(String operation) {
        meterRegistry.counter("mongodb.schema.error", "type", operation).increment();
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
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * URI采集服务接口
 */
public interface UriCollectService {

    /**
     * 异步采集数据
     * @param param 采集参数
     * @return 异步响应，包含任务ID
     */
    AsyncResponse<String> collectData(CollectParam param);

    /**
     * 异步删除数据
     * @param param 删除参数
     * @return 异步响应，包含删除结果
     */
    AsyncResponse<Long> deleteData(DeleteParam param);

    /**
     * 查询URI数据
     * @param param 查询参数
     * @return 分页结果
     */
    Page<UriEntity> queryUri(QueryParam param);

    /**
     * 批量查询URI数据
     * @param uris URI列表
     * @param includeDeleted 是否包含已删除数据
     * @return URI实体列表
     */
    List<UriEntity> batchQueryUri(List<String> uris, Boolean includeDeleted);

    /**
     * 获取任务状态
     * @param taskId 任务ID
     * @return 任务状态
     */
    AsyncResponse<Void> getTaskStatus(String taskId);

    /**
     * 取消任务
     * @param taskId 任务ID
     * @return 是否成功取消
     */
    boolean cancelTask(String taskId);

    /**
     * 更新任务优先级
     * @param taskId 任务ID
     * @param priority 新优先级
     * @return 是否成功更新
     */
    boolean updateTaskPriority(String taskId, int priority);

    /**
     * 获取活动任务列表
     * @return 活动任务列表
     */
    List<AsyncResponse<Void>> getActiveTasks();
}
```

## UriHttpService.java

```java
package com.study.collect.business.testcase.service.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.collect.business.testcase.constant.CollectionConstants;
import com.study.collect.business.testcase.model.param.CollectParam;
import com.study.collect.business.testcase.model.param.PageParam;
import com.study.collect.business.testcase.model.response.PageResponse;
import com.study.collect.business.testcase.model.response.VersionResponse;
import com.study.collect.business.testcase.model.response.parse.HttpResponseParser;
import com.study.collect.business.testcase.utils.HttpUtil;
import com.study.collect.business.testcase.utils.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UriHttpService {
    private final HttpResponseParser<PageResponse<VersionResponse>> versionParser;
    private final HttpResponseParser<PageResponse<String>> uriListParser;
    private final HttpResponseParser<List<Map<String, Object>>> uriDetailParser;
    private final RateLimiter rateLimiter;

    @Qualifier("httpExecutor")
    private final ThreadPoolTaskExecutor httpExecutor;

    /**
     * 异步获取版本列表
     */
    public CompletableFuture<PageResponse<VersionResponse>> getVersionsAsync(
            CollectParam param, PageParam pageParam) {
        String serverUri = param.getServerUrl();
        String rootNode = param.getRootNode();
        return CompletableFuture.supplyAsync(() -> {
            try {
                rateLimiter.acquire();
                String response = HttpUtil.post(
                        serverUri + "/api/versions",
                        String.format(
                                "{\"rootNode\":\"%s\",\"page\":\"%s\",\"size\":\"%s\"}",
                                rootNode,
                                pageParam.getPage(),
                                pageParam.getSize()
                        )).getBody();
                return versionParser.parse(response);
            } catch (Exception e) {
                log.error("Failed to get versions for rootNode: {}", rootNode, e);
                throw new RuntimeException("Failed to get versions", e);
            }
        }, httpExecutor);
    }

    /**
     * 异步获取URI列表
     */
    public CompletableFuture<PageResponse<String>> getUriListAsync(
            CollectParam param, String version, PageParam pageParam) {
        String serverUri = param.getServerUrl();
        return CompletableFuture.supplyAsync(() -> {
            try {
                rateLimiter.acquire();
                String response = HttpUtil.post(
                        serverUri + "/api/uris",
                        String.format(
                                "{\"version\":\"%s\",\"page\":\"%s\",\"size\":\"%s\"}",
                                version,
                                pageParam.getPage(),
                                pageParam.getSize()
                        )
                ).getBody();
                return uriListParser.parse(response);
            } catch (Exception e) {
                log.error("Failed to get URI list for version: {}", version, e);
                throw new RuntimeException("Failed to get URI list", e);
            }
        }, httpExecutor);
    }

    /**
     * 批量获取URI详情
     */
    public CompletableFuture<List<Map<String, Object>>> getUriDetailsAsync(
            CollectParam param, List<String> uris) {
        String serverUri = param.getServerUrl();
        if (uris == null || uris.isEmpty()) {
            return CompletableFuture.completedFuture(Collections.emptyList());
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                rateLimiter.acquire();
                String response = HttpUtil.post(
                        serverUri + "/api/details",
                        "{\"uris\":" + new ObjectMapper().writeValueAsString(uris) + "}"
                ).getBody();
                return uriDetailParser.parse(response);
            } catch (Exception e) {
                log.error("Failed to get URI details for {} URIs", uris.size(), e);
                throw new RuntimeException("Failed to get URI details", e);
            }
        }, httpExecutor);
    }

    /**
     * 同步获取所有版本
     */
    public List<String> getAllVersions(CollectParam param) throws IOException {
        String rootNode = param.getRootNode();
        List<String> allVersions = new ArrayList<>();
        PageParam pageParam = new PageParam(1, CollectionConstants.DEFAULT_BATCH_SIZE);

        try {
            // 获取第一页和总数
            PageResponse<VersionResponse> firstPage = getVersionsAsync(param, pageParam)
                    .get(30, TimeUnit.SECONDS);

            // 处理第一页
            processVersionPage(firstPage, allVersions);

            // 处理剩余页
            long totalPages = (firstPage.getTotal() + pageParam.getSize() - 1) / pageParam.getSize();
            List<CompletableFuture<Void>> futures = new ArrayList<>();

            for (int page = 2; page <= totalPages; page++) {
                final int currentPage = page;
                CompletableFuture<Void> future = getVersionsAsync(
                        param, new PageParam(currentPage, pageParam.getSize())
                ).thenAccept(pageResponse -> processVersionPage(pageResponse, allVersions));

                futures.add(future);
            }

            // 等待所有请求完成
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .get(5, TimeUnit.MINUTES);

        } catch (Exception e) {
            log.error("Failed to get all versions for rootNode: {}", rootNode, e);
            throw new IOException("Failed to get all versions", e);
        }

        return allVersions;
    }

    /**
     * 获取版本下的所有URI
     */
    public List<String> getAllUrisForVersion(CollectParam param, String version) throws IOException {
        List<String> allUris = new ArrayList<>();
        PageParam pageParam = new PageParam(1, CollectionConstants.DEFAULT_BATCH_SIZE);

        try {
            // 获取第一页和总数
            PageResponse<String> firstPage = getUriListAsync(param, version, pageParam)
                    .get(30, TimeUnit.SECONDS);

            allUris.addAll(firstPage.getItems());

            // 处理剩余页
            long totalPages = (firstPage.getTotal() + pageParam.getSize() - 1) / pageParam.getSize();
            List<CompletableFuture<Void>> futures = new ArrayList<>();

            for (int page = 2; page <= totalPages; page++) {
                final int currentPage = page;
                CompletableFuture<Void> future = getUriListAsync(
                        param, version, new PageParam(currentPage, pageParam.getSize())
                ).thenAccept(pageResponse -> allUris.addAll(pageResponse.getItems()));

                futures.add(future);
            }

            // 等待所有请求完成
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .get(5, TimeUnit.MINUTES);

        } catch (Exception e) {
            log.error("Failed to get all URIs for version: {}", version, e);
            throw new IOException("Failed to get all URIs", e);
        }

        return allUris;
    }

    private void processVersionPage(PageResponse<VersionResponse> pageResponse, List<String> versions) {
        if (pageResponse != null && pageResponse.getItems() != null) {
            versions.addAll(pageResponse.getItems().stream()
                    .map(VersionResponse::getVersion)
                    .collect(Collectors.toList()));
        }
    }

    /**
     * 批量处理URI详情
     */
    public List<Map<String, Object>> batchGetUriDetails(
            CollectParam param, List<String> uris, int batchSize) throws IOException {
        List<Map<String, Object>> allDetails = new ArrayList<>();
        List<List<String>> batches = new ArrayList<>();

        // 分批
        for (int i = 0; i < uris.size(); i += batchSize) {
            batches.add(uris.subList(i, Math.min(i + batchSize, uris.size())));
        }

        try {
            // 并行处理每个批次
            List<CompletableFuture<List<Map<String, Object>>>> futures = batches.stream()
                    .map(batch -> getUriDetailsAsync(param, batch))
                    .collect(Collectors.toList());

            // 等待所有批次完成
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .get(5, TimeUnit.MINUTES);

            // 收集结果
            for (CompletableFuture<List<Map<String, Object>>> future : futures) {
                allDetails.addAll(future.get());
            }

        } catch (Exception e) {
            log.error("Failed to batch get URI details", e);
            throw new IOException("Failed to batch get URI details", e);
        }

        return allDetails;
    }
}
```

## IndexChecker.java

```java
package com.study.collect.business.testcase.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

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

import com.study.collect.business.testcase.model.PageResult;
import com.study.collect.business.testcase.repository.UriRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class UriCleanupService {
    private final UriRepository repository;
    private static final int PAGE_SIZE = 10000;
    private static final int DELETE_BATCH_SIZE = 10000;

    @Autowired
    public UriCleanupService(UriRepository repository) {
        this.repository = repository;
    }

    /**
     * 清理不在总列表中的URI数据
     * @param allUriHashes 总的uriHash列表
     * @param rootNode 根节点
     */
    public void cleanupUriData(List<String> allUriHashes, String rootNode) {
        try {
            log.info("Starting URI cleanup process, total hashes: {}", allUriHashes.size());
            Set<String> allHashSet = new HashSet<>(allUriHashes);
            Set<String> toDeleteHashes = new HashSet<>();

            // 分页查询数据库中的uriHash
            int page = 1;
            PageResult<String> pageResult;
            do {
                pageResult = repository.findUriHashesPage(rootNode, null, null, page, PAGE_SIZE);

                // 找出不在总列表中的hash
                for (String dbHash : pageResult.getItems()) {
                    if (!allHashSet.contains(dbHash)) {
                        toDeleteHashes.add(dbHash);
                    }
                }

                log.info("Processed page {}/{}, found {} hashes to delete",
                        page, pageResult.getTotalPages(), toDeleteHashes.size());
                page++;
            } while (page <= pageResult.getTotalPages());

            // 如果有需要删除的数据，进行批量删除
            if (!toDeleteHashes.isEmpty()) {
                log.info("Starting deletion of {} hashes", toDeleteHashes.size());
                List<String> toDeleteList = new ArrayList<>(toDeleteHashes);

                // 分批删除
                for (int i = 0; i < toDeleteList.size(); i += DELETE_BATCH_SIZE) {
                    int end = Math.min(i + DELETE_BATCH_SIZE, toDeleteList.size());
                    List<String> batch = toDeleteList.subList(i, end);

                    try {
                        repository.batchHardDelete(rootNode,batch);
                        log.info("Deleted batch {}-{} of {}", i, end, toDeleteList.size());
                    } catch (Exception e) {
                        log.error("Error deleting batch {}-{}", i, end, e);
                    }
                }

                log.info("Cleanup completed, deleted {} hashes", toDeleteList.size());
            } else {
                log.info("No hashes need to be deleted");
            }

        } catch (Exception e) {
            log.error("Error during cleanup process", e);
            throw new RuntimeException("Cleanup process failed", e);
        }
    }

    /**
     * 异步执行清理过程
     */
    @Async
    public CompletableFuture<Void> cleanupUriDataAsync(List<String> allUriHashes, String rootNode) {
        return CompletableFuture.runAsync(() -> cleanupUriData(allUriHashes, rootNode));
    }
}
```

## UriCollectServiceImpl.java

```java
package com.study.collect.business.testcase.service.impl;

import com.study.collect.business.testcase.constant.CollectionConstants;
import com.study.collect.business.testcase.entity.UriEntity;
import com.study.collect.business.testcase.manager.CollectTaskManager;
import com.study.collect.business.testcase.manager.QueueManager;
import com.study.collect.business.testcase.model.param.CollectParam;
import com.study.collect.business.testcase.model.param.DeleteParam;
import com.study.collect.business.testcase.model.param.QueryParam;
import com.study.collect.business.testcase.model.response.AsyncResponse;
import com.study.collect.business.testcase.model.response.PageResponse;
import com.study.collect.business.testcase.model.response.TaskResponse;
import com.study.collect.business.testcase.repository.UriRepository;
import com.study.collect.business.testcase.service.UriCollectService;
import com.study.collect.business.testcase.service.http.UriHttpService;
import com.study.collect.business.testcase.utils.HashUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.ObjectPool;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UriCollectServiceImpl implements UriCollectService {
    private final UriHttpService httpService;
    private final UriRepository repository;
    private final ObjectPool<UriEntity> entityPool;
    private final CollectTaskManager taskManager;
    private final UriCleanupService uriCleanupService;
    private final QueueManager<CollectParam> collectQueue;
    private final QueueManager<DeleteParam> deleteQueue;

    @Override
    public AsyncResponse<String> collectData(CollectParam param) {
        // 1. 创建任务
        TaskResponse task = taskManager.createTask(
                "COLLECT",
                Map.of("rootNode", param.getRootNode(),
                        "serverUri", param.getServerUrl(),
                        "version", param.getVersion()),
                param.getPriority()
        );

        // 2. 将任务加入队列
        collectQueue.enqueue(
                task.getTaskId(),
                param,
                param.getPriority(),
                this::processCollectTask
        ).exceptionally(throwable -> {
            taskManager.updateTaskStatus(
                    task.getTaskId(),
                    "ERROR",
                    throwable.getMessage()
            );
            return null;
        });

        // 3. 返回异步响应
        return AsyncResponse.<String>builder()
                .taskId(task.getTaskId())
                .status("QUEUED")
                .message("Task queued successfully")
                .build();
    }

    private void processCollectTask(CollectParam param) {
        String taskId = param.getTaskId();
        try {
            taskManager.updateTaskStatus(taskId, "PROCESSING", "Starting data collection");

            // 1. 获取所有版本
            List<String> allVersions = httpService.getAllVersions(param
            );

            taskManager.updateTaskStatus(
                    taskId,
                    "PROCESSING",
                    String.format("Found %d versions", allVersions.size())
            );

            // 过滤指定版本
            List<String> versions = filterVersions(allVersions, param.getVersion());

            // 2. 如果是增量同步，先清理数据
                cleanupIncrementalData(param, versions);



            // 3. 处理每个版本
            long totalProcessed = 0;
            long estimatedTotal = calculateEstimatedTotal(param, versions);

            taskManager.updateTaskProgress(taskId, totalProcessed, estimatedTotal);

            for (String version : versions) {
                totalProcessed += processVersion(
                        param,
                        version,
                        taskId
                );
                taskManager.updateTaskProgress(taskId, totalProcessed, estimatedTotal);
            }

            taskManager.updateTaskStatus(
                    taskId,
                    "COMPLETED",
                    String.format("Processed %d URIs", totalProcessed)
            );

        } catch (Exception e) {
            log.error("Error processing collect task: {}", taskId, e);
            taskManager.updateTaskStatus(
                    taskId,
                    "ERROR",
                    "Error: " + e.getMessage()
            );
            throw new RuntimeException("Task processing failed", e);
        }
    }

private List<String> filterVersions(List<String> allVersions, String versionFilter) {
        // 检查版本过滤器是否不为空
        if (StringUtils.hasText(versionFilter)) {
            // 按逗号分隔版本过滤器并修剪每个元素
            Set<String> filterSet = Arrays.stream(versionFilter.split(","))
                    .map(String::trim)
                    .collect(Collectors.toSet());
            // 根据过滤器集合过滤版本
            return allVersions.stream()
                    // 检查allVersions中的每个元素是否完全匹配versionFilter中的某个元素 .filter(filterSet::contains)
                    // 检查allVersions中的每个元素是否至少包含versionFilter分隔的字符串中的一个
                    .filter(version -> filterSet.stream().anyMatch(version::contains))
                    .collect(Collectors.toList());
        }
        // 如果没有提供过滤器，则返回所有版本
        return allVersions;
    }

    private long processVersion(
      CollectParam param,
            String version,
            String taskId
    ) throws Exception {
        String rootNode = param.getRootNode();
        // 1. 获取该版本下的所有URI
        List<String> allUris = httpService.getAllUrisForVersion(param, version);
        long totalProcessed = 0;

        // 2. 分批处理
        List<List<String>> batches = partition(
                allUris,
                CollectionConstants.DEFAULT_BATCH_SIZE
        );

        for (List<String> batch : batches) {
            // 获取URI详情
            List<Map<String, Object>> details = httpService.batchGetUriDetails(
                    param,
                    batch,
                    CollectionConstants.DEFAULT_BATCH_SIZE
            );

            // 创建实体并保存
            List<UriEntity> entities = new ArrayList<>();
            for (Map<String, Object> detail : details) {
                UriEntity entity = null;
                try {
                    entity = entityPool.borrowObject();
                    fillEntity(entity, rootNode, version, detail);
                    entities.add(entity);
                } catch (Exception e) {
                    log.error("Error creating entity", e);
                    if (entity != null) {
                        entityPool.returnObject(entity);
                    }
                }
            }

            try {
                if (!entities.isEmpty()) {
                    repository.batchUpsert(rootNode, entities);
                    totalProcessed += entities.size();
                }
            } finally {
                // 返还对象到对象池
                for (UriEntity entity : entities) {
                    try {
                        entityPool.returnObject(entity);
                    } catch (Exception e) {
                        log.error("Error returning entity to pool", e);
                    }
                }
            }

            // 更新任务进度
            taskManager.updateTaskStatus(
                    taskId,
                    "PROCESSING",
                    String.format("Processing version %s: %d/%d",
                            version, totalProcessed, allUris.size())
            );
        }

        return totalProcessed;
    }

    @Override
    public AsyncResponse<Long> deleteData(DeleteParam param) {
        // 1. 创建任务
        TaskResponse task = taskManager.createTask(
                "DELETE",
                Map.of("rootNode", param.getRootNode(),
                        "urisCount", param.getUris().size(),
                        "hardDelete", param.getHardDelete()),
                param.getPriority()
        );

        // 2. 将任务加入队列
        deleteQueue.enqueue(
                task.getTaskId(),
                param,
                param.getPriority(),
                this::processDeleteTask
        ).exceptionally(throwable -> {
            taskManager.updateTaskStatus(
                    task.getTaskId(),
                    "ERROR",
                    throwable.getMessage()
            );
            return null;
        });

        // 3. 返回异步响应
        return AsyncResponse.<Long>builder()
                .taskId(task.getTaskId())
                .status("QUEUED")
                .message("Delete task queued successfully")
                .build();
    }

    private void processDeleteTask(DeleteParam param) {
        String taskId = param.getTaskId();
        try {
            taskManager.updateTaskStatus(taskId, "PROCESSING", "Starting data deletion");

            List<List<String>> batches = partition(
                    param.getUris(),
                    param.getBatchSize() != null ?
                            param.getBatchSize() :
                            CollectionConstants.DEFAULT_BATCH_SIZE
            );

            long totalDeleted = 0;
            for (List<String> batch : batches) {
                long batchCount;
                if (param.getHardDelete()) {
                    batchCount = repository.batchHardDelete(param.getRootNode(), batch);
                } else {
                    batchCount = repository.batchSoftDelete(param.getRootNode(), batch);
                }
                totalDeleted += batchCount;

                taskManager.updateTaskProgress(
                        taskId,
                        totalDeleted,
                        param.getUris().size()
                );
            }

            taskManager.updateTaskStatus(
                    taskId,
                    "COMPLETED",
                    String.format("Deleted %d URIs", totalDeleted)
            );

        } catch (Exception e) {
            log.error("Error processing delete task: {}", taskId, e);
            taskManager.updateTaskStatus(
                    taskId,
                    "ERROR",
                    "Error: " + e.getMessage()
            );
            throw new RuntimeException("Delete task processing failed", e);
        }
    }

    @Override
    public Page<UriEntity> queryUri(QueryParam param) {
        return repository.findByCondition(
                param.getRootNode(),
                param.getVersion(),
                param.getVersionType(),
                param.getIncludeDeleted(),
                PageRequest.of(param.getPage() - 1, param.getSize())
        );
    }

    @Override
    public List<UriEntity> batchQueryUri(List<String> uris, Boolean includeDeleted) {
        if (CollectionUtils.isEmpty(uris)) {
            return Collections.emptyList();
        }

        // 使用第一个URI的rootNode作为默认值
        return repository.batchQuery(
                uris,
                this::extractRootNode,
                includeDeleted
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
                .startTime(task.getStartTime())
                .endTime(task.getEndTime())
                .build();
    }

    @Override
    public boolean cancelTask(String taskId) {
        // 尝试取消队列中的任务
        if (collectQueue.cancel(taskId) || deleteQueue.cancel(taskId)) {
            taskManager.cancelTask(taskId);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateTaskPriority(String taskId, int priority) {
        // 更新任务优先级
        if (collectQueue.updatePriority(taskId, priority) ||
                deleteQueue.updatePriority(taskId, priority)) {
            return taskManager.updateTaskPriority(taskId, priority);
        }
        return false;
    }

    @Override
    public List<AsyncResponse<Void>> getActiveTasks() {
        return taskManager.getActiveTasks().stream()
                .map(task -> AsyncResponse.<Void>builder()
                        .taskId(task.getTaskId())
                        .status(task.getStatus())
                        .message(task.getMessage())
                        .progress(task.getProgress())
                        .startTime(task.getStartTime())
                        .build())
                .collect(Collectors.toList());
    }

    private void cleanupIncrementalData(
            CollectParam param,
            List<String> versions
    ) throws Exception {
        String rootNode = param.getRootNode();
        List<String> allUriHashes = new ArrayList<>();

        // 获取所有版本的URI
        for (String version : versions) {
            List<String> versionUris = httpService.getAllUrisForVersion(param, version);
            allUriHashes.addAll(versionUris.stream()
                    .map(this::generateUriHash)
                    .collect(Collectors.toSet()));
        }

        // 删除不存在的URI //TODO 改成分页批量删除 ,可选软删除或者硬删除，根据param.getHardDelete()来判断
        uriCleanupService.cleanupUriData(allUriHashes,rootNode);
//        repository.deleteNotInUris(rootNode, allUriHashes);
    }

    private String generateUriHash(String uri) {
        return Objects.hash(uri) + "";
    }

    private String extractRootNode(String uri) {
        // 从URI中提取rootNode的逻辑
        String[] parts = uri.split("/");
        return parts.length > 0 ? parts[0] : "";
    }

    private <T> List<List<T>> partition(List<T> list, int size) {
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }

        List<List<T>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            partitions.add(list.subList(i, Math.min(i + size, list.size())));
        }
        return partitions;
    }

    private long calculateEstimatedTotal(CollectParam param, List<String> versions) {
        long total = 0;
        for (String version : versions) {
            try {
                PageResponse<String> response = httpService.getUriListAsync(
                        param,
                        version,
                        new com.study.collect.business.testcase.model.param.PageParam(1, 1)
                ).get();
                total += response.getTotal();
            } catch (Exception e) {
                log.warn("Error calculating total for version: {}", version, e);
            }
        }
        return total;
    }
    private void fillEntity(
            UriEntity entity,
            String rootNode,
            String version,
            Map<String, Object> detail
    ) {
        String uri = (String) detail.get("uri");
        entity.setUri(uri);
        entity.setUriHash(HashUtil.hash(uri));  // 重要：设置完 uri 后立即生成 uriHash
        entity.setRootNode(rootNode);
        entity.setVersionType(getVersionType(version));
        entity.setUriVersion(version);
        entity.setDetails(detail);
    }


    private String getVersionType(String version) {
        return version.toLowerCase().contains("branch") ? "BRANCH" : "TRUNK";
    }
}
```

## HashUtil.java

```java
package com.study.collect.business.testcase.utils;

import org.apache.commons.codec.digest.DigestUtils;

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

## ListCompareUtil.java

```java
package com.study.collect.business.testcase.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.util.CollectionUtils;

import java.util.*;
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

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 限流器实现
 * 使用滑动窗口算法实现限流
 */
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
        scheduler.scheduleAtFixedRate(this::cleanup, 1, 1, TimeUnit.MINUTES);
    }

    /**
     * 获取许可
     */
    public void acquire() throws InterruptedException {
        while (!tryAcquire()) {
            Thread.sleep(5000);  // 等待100ms后重试
        }
    }

    /**
     * 尝试获取许可
     */
    public boolean tryAcquire() {
        cleanup();  // 清理过期的时间戳

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
     * 关闭清理线程
     */
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

import com.study.collect.business.testcase.constant.CollectionConstants;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;

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
}
```

## spring.factories

```
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
com.study.collect.business.testcase.config.TestCaseAutoConfiguration
```

