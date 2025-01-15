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
                                        log/
                                        mongodb/
                                            CollectionStrategy.java
                                            CollectionVersion.java
                                            CollectionVersionAspect.java
                                    config/
                                        MongoConfig.java
                                        ObjectPoolConfig.java
                                        TestCaseAutoConfiguration.java
                                        TestCaseCollectorProperties.java
                                        TestCaseConfig.java
                                        ThreadPoolConfig.java
                                    constant/
                                        VersionType.java
                                    controller/
                                        UriCollectController.java
                                    entity/
                                        UriEntity.java
                                    model/
                                        PageResult.java
                                        param/
                                            CollectParam.java
                                            PageParam.java
                                            TimeRangeParam.java
                                            VersionParam.java
                                        request/
                                        response/
                                            BaseResponse.java
                                            PageResponse.java
                                            VersionResponse.java
                                            parse/
                                                HttpResponseParser.java
                                                UriDetailResponseParser.java
                                                UriListResponseParser.java
                                                VersionResponseParser.java
                                    repository/
                                        UriRepository.java
                                    service/
                                        UriCollectService.java
                                        http/
                                            UriHttpService.java
                                        impl/
                                            UriCleanupService.java
                                            UriCollectServiceImpl.java
                                    utils/
                                        BatchProcessUtil.java
                                        BatchThreadManager.java
                                        HashUtil.java
                                        HttpUtil.java
                                        ListCompareUtil.java
                                        MongoPageUtil.java
                                        PageUtil.java
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

## MongoConfig.java

```java
package com.study.collect.business.testcase.config;
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

## ObjectPoolConfig.java

```java
package com.study.collect.business.testcase.config;


import com.study.collect.business.testcase.entity.UriEntity;
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

    @Bean
    public GenericObjectPool<UriEntity> uriEntityPool() {
        // 配置对象池参数
        GenericObjectPoolConfig<UriEntity> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxTotal(20);          // 最大对象数
        poolConfig.setMaxIdle(10);           // 最大空闲对象数
        poolConfig.setMinIdle(5);            // 最小空闲对象数
        poolConfig.setTestOnBorrow(true);    // 借用对象时测试
        poolConfig.setTestOnReturn(true);    // 返还对象时测试
        poolConfig.setTestWhileIdle(true);   // 空闲时测试
        poolConfig.setBlockWhenExhausted(true); // 池空时阻塞

        // 创建对象池
        return new GenericObjectPool<>(new BasePooledObjectFactory<>() {
            @Override
            public UriEntity create() throws Exception {
                try {
                    UriEntity entity = new UriEntity();
                    // 初始化基本属性
                    entity.setDeleted(false);
                    entity.setVersion(0L);
                    return entity;
                } catch (Exception e) {
                    log.error("Failed to create UriEntity in pool", e);
                    throw e;
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
                    resetAllFields(entity);
//                    // 重置基本字段
//                    entity.setUri(null);
//                    entity.setUriHash(null);
//                    entity.setRootNode(null);
//                    entity.setVersionType(null);
//                    entity.setUriVersion(null);
//                    entity.setDetails(null);
//                    // 重置继承的字段
//                    entity.setId(null);
//                    entity.setCreateTime(null);
//                    entity.setUpdateTime(null);
//                    entity.setCreateBy(null);
//                    entity.setUpdateBy(null);
//                    entity.setVersion(0L);
//                    entity.setDeleted(false);
//                    entity.setVersionCode(null);
//                    entity.setVersionTime(null);
                } catch (Exception e) {
                    log.error("Failed to reset UriEntity fields", e);
                }
            }

            private void resetAllFields(Object object) {
                Class<?> clazz = object.getClass();
                while (clazz != null && !clazz.equals(Object.class)) {
                    for (Field field : clazz.getDeclaredFields()) {
                        try {
                            if (!Modifier.isStatic(field.getModifiers()) &&
                                    !Modifier.isFinal(field.getModifiers())) {
                                field.setAccessible(true);
                                // 根据字段类型设置默认值
                                if (field.getType().equals(Boolean.class) ||
                                        field.getType().equals(boolean.class)) {
                                    field.set(object, false);
                                } else if (field.getType().equals(Long.class) ||
                                        field.getType().equals(long.class)) {
                                    field.set(object, 0L);
                                } else if (field.getType().equals(Integer.class) ||
                                        field.getType().equals(int.class)) {
                                    field.set(object, 0);
                                } else {
                                    field.set(object, null);
                                }
                            }
                        } catch (Exception e) {
                            log.warn("Failed to reset field: {}", field.getName(), e);
                        }
                    }
                    clazz = clazz.getSuperclass();
                }
            }

            @Override
            public boolean validateObject(PooledObject<UriEntity> p) {
                return p.getObject() != null;
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


import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.*;

@Configuration
public class ThreadPoolConfig {
    @Bean(name = "collectExecutor")
    public ExecutorService collectExecutor() {
        return new ThreadPoolExecutor(
                Runtime.getRuntime().availableProcessors() * 2,
                Runtime.getRuntime().availableProcessors() * 4,
                60L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(5000),
                new ThreadFactoryBuilder()
                        .setNameFormat("uri-collect-pool-%d")
                        .setDaemon(true)
                        .build(),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }
}

```

## VersionType.java

```java
package com.study.collect.business.testcase.constant;


public enum VersionType {
    TRUNK,
    BRANCH
}
```

## UriCollectController.java

```java
package com.study.collect.business.testcase.controller;

import com.study.collect.business.testcase.entity.UriEntity;
import com.study.collect.business.testcase.model.param.CollectParam;
import com.study.collect.business.testcase.service.UriCollectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/collect")
@RequiredArgsConstructor
@Slf4j
public class UriCollectController {
    private final UriCollectService collectService;

    @PostMapping("/sync")
    public ResponseEntity<Void> syncData(@RequestBody CollectParam param) {
        collectService.collectData(param);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/uri")
    public ResponseEntity<List<UriEntity>> queryUri(
            @RequestParam(required = false) String rootNode,
            @RequestParam(required = false) String version,
            @RequestParam(required = false) String versionType) {
        return ResponseEntity.ok(collectService.queryUri(rootNode, version, versionType));
    }
}
```

## UriEntity.java

```java
package com.study.collect.business.testcase.entity;

import com.study.collect.core.storage.entity.VersionEntity;
import com.study.collect.business.testcase.utils.HashUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Document(collection = "#{@collectionStrategy.getCollectionName('uri_collect')}") // 从配置文件中获取集合名称
@Data
@EqualsAndHashCode(callSuper = true)
@CompoundIndexes({
        @CompoundIndex(name = "uri_unique_idx",
                def = "{'uri': 1, 'rootNode': 1, 'versionType': 1, 'uriVersion': 1}",
                unique = true),
        @CompoundIndex(name = "uriHash_idx",
                def = "{'uriHash': 1}",
                unique = true)
})
@NoArgsConstructor  // 添加无参构造器
@AllArgsConstructor
public class UriEntity extends VersionEntity {
    @Indexed(unique = true)
    private String uriHash;

    @Indexed
    private String uri;
    private String rootNode;
    private String versionType;
    private String uriVersion;
    private Map<String, Object> details;

//    @Override
    public void prePersist() {
        if (this.uriHash == null && this.uri != null) {
            this.uriHash = HashUtil.hash(this.uri);
        }
        this.version = 0L;
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

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CollectParam {
    private String rootNode;
    private String version;
    private Boolean incremental = false;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}

```

## PageParam.java

```java
package com.study.collect.business.testcase.model.param;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageParam {
    private int page = 1;
    private int size = 200;
}
```

## TimeRangeParam.java

```java
package com.study.collect.business.testcase.model.param;

public class TimeRangeParam {
}

```

## VersionParam.java

```java
package com.study.collect.business.testcase.model.param;

public class VersionParam {
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

public interface HttpResponseParser<T> {
    T parse(String response) throws IOException;
}

```

## UriDetailResponseParser.java

```java
package com.study.collect.business.testcase.model.response.parse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class UriDetailResponseParser implements HttpResponseParser<List<Map<String, Object>>> {
    private final ObjectMapper objectMapper;

    @Override
    public List<Map<String, Object>> parse(String response) throws IOException {
        JsonNode root = objectMapper.readTree(response);
        List<Map<String, Object>> details = new ArrayList<>();

        JsonNode items = root.path("items");
        if (items.isArray()) {
            items.forEach(item -> {
                try {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> detail = objectMapper.convertValue(item, Map.class);
                    details.add(detail);
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException("Failed to parse URI detail", e);
                }
            });
        }

        return details;
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
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UriListResponseParser implements HttpResponseParser<PageResponse<String>> {
    private final ObjectMapper objectMapper;

    @Override
    public PageResponse<String> parse(String response) throws IOException {
        JsonNode root = objectMapper.readTree(response);

        PageResponse<String> pageResponse = new PageResponse<>();
        pageResponse.setTotal(root.path("total").asLong());

        List<String> uris = new ArrayList<>();
        JsonNode items = root.path("items");
        if (items.isArray()) {
            items.forEach(item -> uris.add(item.path("uri").asText()));
        }

        pageResponse.setItems(uris);
        return pageResponse;
    }
}

```

## VersionResponseParser.java

```java
package com.study.collect.business.testcase.model.response.parse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.collect.business.testcase.model.response.PageResponse;
import com.study.collect.business.testcase.model.response.VersionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class VersionResponseParser implements HttpResponseParser<PageResponse<VersionResponse>> {
    private final ObjectMapper objectMapper;

    @Override
    public PageResponse<VersionResponse> parse(String response) throws IOException {
        return objectMapper.readValue(response,
                objectMapper.getTypeFactory().constructParametricType(
                        PageResponse.class, VersionResponse.class));
    }
}
```

## UriRepository.java

```java
package com.study.collect.business.testcase.repository;

import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.study.collect.business.testcase.aspect.mongodb.CollectionStrategy;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.WriteModel;
import com.study.collect.business.testcase.entity.UriEntity;
import com.study.collect.business.testcase.model.PageResult;
import com.study.collect.core.storage.repository.BaseMongoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Repository
@Slf4j
public class UriRepository extends BaseMongoRepository<UriEntity> {

    private final MongoOperations mongoOperations;
    private final MongoEntityInformation<UriEntity, String> entityInformation;

    public UriRepository(MongoEntityInformation<UriEntity, String> metadata,
                         MongoOperations mongoOperations) {
        super(metadata, mongoOperations);
        this.mongoOperations = mongoOperations;
        this.entityInformation = metadata;
    }

    /**
     * 获取当前集合名
     */
    protected String getCollectionName() {
        String baseCollection = "uri_collect";
        String version = CollectionStrategy.getVersion();
        if (version != null) {
            return baseCollection + "_" + version;
        }
        log.warn("No version found in CollectionStrategy, using base collection name");
        return baseCollection;
    }

    /**
     * 根据URI列表查询
     */
    public List<UriEntity> findByUris(List<String> uris) {
        if (CollectionUtils.isEmpty(uris)) {
            return new ArrayList<>();
        }
        Query query = new Query(Criteria.where("uri").in(uris)
                .and("deleted").is(false));
        return mongoOperations.find(query, entityInformation.getJavaType(), getCollectionName());
    }

    /**
     * 根据URIHash列表查询
     */
    public List<UriEntity> findByUriHashes(List<String> uriHashes) {
        if (CollectionUtils.isEmpty(uriHashes)) {
            return new ArrayList<>();
        }
        Query query = new Query(Criteria.where("uriHash").in(uriHashes)
                .and("deleted").is(false));
        return mongoOperations.find(query, entityInformation.getJavaType(), getCollectionName());
    }

    /**
     * 只返回details字段
     */
    public List<String> findDetailsList(QueryCondition condition) {
        MongoCollection<Document> collection = mongoOperations.getCollection(getCollectionName());

        Document query = buildQuery(condition);
        query.append("deleted", false);
        Document projection = new Document("details", 1).append("_id", 0);

        List<String> detailsList = new ArrayList<>();
        try (MongoCursor<Document> cursor = collection.find(query)
                .projection(projection)
                .iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Object details = doc.get("details");
                if (details != null) {
                    detailsList.add(details.toString());
                }
            }
        }

        return detailsList;
    }

    /**
     * 分页查询uriHash
     */
    public PageResult<String> findUriHashesPage(QueryCondition condition) {
        MongoCollection<Document> collection = mongoOperations.getCollection(getCollectionName());

        Document query = buildQuery(condition);
        query.append("deleted", false);

        List<Document> pipeline = Arrays.asList(
                new Document("$match", query),
                new Document("$project", new Document("uriHash", 1).append("_id", 0)),
                new Document("$skip", (long) (condition.getPage() - 1) * condition.getSize()),
                new Document("$limit", condition.getSize())
        );

        List<String> items = new ArrayList<>();
        try (MongoCursor<Document> cursor = collection.aggregate(pipeline).iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                String uriHash = doc.getString("uriHash");
                if (uriHash != null) {
                    items.add(uriHash);
                }
            }
        }

        long total = collection.countDocuments(query);

        return PageResult.<String>builder()
                .total(total)
                .page(condition.getPage())
                .size(condition.getSize())
                .totalPages((int) Math.ceil((double) total / condition.getSize()))
                .items(items)
                .build();
    }

    @Override
    public <S extends UriEntity> List<S> saveAll(Iterable<S> entities) {
        if (!entities.iterator().hasNext()) {
            return Collections.emptyList();
        }

        String collectionName = getCollectionName();
        MongoCollection<Document> collection = mongoOperations.getCollection(collectionName);

        List<WriteModel<Document>> operations = new ArrayList<>();
        for (S entity : entities) {
            Document query = new Document("uriHash", entity.getUriHash());
            Document update = new Document("$set", convertEntityToDocument(entity));
            operations.add(new UpdateOneModel<>(
                    query,
                    update,
                    new UpdateOptions().upsert(true)
            ));
        }

        try {
            BulkWriteOptions bulkWriteOptions = new BulkWriteOptions().ordered(false);
            BulkWriteResult result = collection.bulkWrite(operations, bulkWriteOptions);
            log.debug("Bulk write to collection {}: matched={}, inserted={}, modified={}",
                    collectionName,
                    result.getMatchedCount(),
                    result.getInsertedCount(),
                    result.getModifiedCount());
        } catch (Exception e) {
            log.error("Failed to bulk write to collection {}", collectionName, e);
            throw new RuntimeException("Bulk write failed", e);
        }

        return StreamSupport.stream(entities.spliterator(), false)
                .collect(Collectors.toList());
    }

    private Document buildQuery(QueryCondition condition) {
        Document query = new Document();

        if (condition.getUriHashes() != null && !condition.getUriHashes().isEmpty()) {
            query.append("uriHash", new Document("$in", condition.getUriHashes()));
        }
        if (condition.getRootNode() != null) {
            query.append("rootNode", condition.getRootNode());
        }
        if (condition.getVersionType() != null) {
            query.append("versionType", condition.getVersionType());
        }
        if (condition.getUriVersion() != null) {
            query.append("uriVersion", condition.getUriVersion());
        }

        return query;
    }

    private Document convertEntityToDocument(UriEntity entity) {
        Document doc = new Document();
        doc.put("uri", entity.getUri());
        doc.put("uriHash", entity.getUriHash());
        doc.put("rootNode", entity.getRootNode());
        doc.put("versionType", entity.getVersionType());
        doc.put("uriVersion", entity.getUriVersion());
        doc.put("details", entity.getDetails());
        doc.put("updateTime", LocalDateTime.now());
        doc.put("deleted", false);
        return doc;
    }
}
```

## UriCollectService.java

```java
package com.study.collect.business.testcase.service;



import com.study.collect.business.testcase.entity.UriEntity;
import com.study.collect.business.testcase.model.param.CollectParam;

import java.util.List;

public interface UriCollectService {
    void collectData(CollectParam param);
    List<UriEntity> queryUri(String rootNode, String version, String versionType);
}
```

## UriHttpService.java

```java
package com.study.collect.business.testcase.service.http;

import com.study.collect.business.testcase.model.response.PageResponse;
import com.study.collect.business.testcase.model.response.VersionResponse;
import com.study.collect.business.testcase.model.response.parse.HttpResponseParser;
import com.study.collect.business.testcase.model.param.PageParam;
import com.study.collect.business.testcase.utils.HttpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class UriHttpService {
    private final HttpUtil httpUtil;
    private final HttpResponseParser<PageResponse<VersionResponse>> versionParser;
    private final HttpResponseParser<PageResponse<String>> uriListParser;
    private final HttpResponseParser<List<Map<String, Object>>> uriDetailParser;

    public PageResponse<VersionResponse> getVersions(String rootNode, PageParam pageParam) throws IOException {
        String response = httpUtil.post("/api/versions",
                Map.of("rootNode", rootNode,
                        "page", pageParam.getPage(),
                        "size", pageParam.getSize()));
        return versionParser.parse(response);
    }

    public PageResponse<String> getUriList(String version, PageParam pageParam) throws IOException {
        String response = httpUtil.post("/api/uris",
                Map.of("version", version,
                        "page", pageParam.getPage(),
                        "size", pageParam.getSize()));
        return uriListParser.parse(response);
    }

    public List<Map<String, Object>> getUriDetails(List<String> uris) throws IOException {
        String response = httpUtil.post("/api/details", Map.of("uris", uris));
        return uriDetailParser.parse(response);
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
                        repository.deleteByUriHashes(batch);
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

import com.google.common.collect.Lists;
import com.study.collect.business.testcase.constant.VersionType;
import com.study.collect.business.testcase.entity.UriEntity;
import com.study.collect.business.testcase.model.response.PageResponse;
import com.study.collect.business.testcase.model.response.VersionResponse;
import com.study.collect.business.testcase.model.param.CollectParam;
import com.study.collect.business.testcase.model.param.PageParam;
import com.study.collect.business.testcase.repository.UriRepository;
import com.study.collect.business.testcase.service.UriCollectService;
import com.study.collect.business.testcase.service.http.UriHttpService;
import com.study.collect.business.testcase.utils.HashUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.ObjectPool;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UriCollectServiceImpl implements UriCollectService {
    private final UriHttpService httpService;
    private final UriRepository repository;
    private final ObjectPool<UriEntity> entityPool;
    private final ExecutorService executorService;

    private static final int BATCH_SIZE = 200;
    private static final int PAGE_SIZE = 200;
    private static final int MAX_RETRY = 3;
    private static final long RETRY_INTERVAL = 1000L;

    public UriCollectServiceImpl(
            UriHttpService httpService,
            UriRepository repository,
            ObjectPool<UriEntity> entityPool,
            @Qualifier("collectExecutor") ExecutorService executorService) {
        this.httpService = httpService;
        this.repository = repository;
        this.entityPool = entityPool;
        this.executorService = executorService;
    }

    @Override
    public void collectData(CollectParam param) {
        try {
            // 1. 获取所有版本（分页）
            List<String> allVersions = getAllVersions(param.getRootNode());

            // 2. 按版本类型分组
            Map<String, List<String>> versionGroups = allVersions.stream()
                    .collect(Collectors.groupingBy(this::getVersionType));

            // 3. 如果是增量同步，先进行数据清理
            if (param.getIncremental()) {
                cleanupIncrementalData(param.getRootNode(), allVersions);
            }

            // 4. 优先处理主干版本，然后是分支版本
            if (versionGroups.containsKey(VersionType.TRUNK.name())) {
                processVersionGroup(param.getRootNode(),
                        versionGroups.get(VersionType.TRUNK.name()),
                        param);
            }
            if (versionGroups.containsKey(VersionType.BRANCH.name())) {
                processVersionGroup(param.getRootNode(),
                        versionGroups.get(VersionType.BRANCH.name()),
                        param);
            }

            log.info("Data collection completed for root node: {}", param.getRootNode());
        } catch (Exception e) {
            log.error("Error collecting data for root node: {}", param.getRootNode(), e);
            throw new RuntimeException("Data collection failed", e);
        }
    }

    @Override
    public List<UriEntity> queryUri(String rootNode, String version, String versionType) {
        return repository.findByConditions(rootNode, version, versionType);
    }

    private List<String> getAllVersions(String rootNode) throws IOException {
        List<String> allVersions = new ArrayList<>();
        PageResponse<VersionResponse> firstPage =
                retryWithBackoff(() -> httpService.getVersions(rootNode, new PageParam(1, PAGE_SIZE)));

        // 处理第一页
        processVersionPage(firstPage, allVersions);

        // 计算总页数并处理剩余页
        long totalPages = (firstPage.getTotal() + PAGE_SIZE - 1) / PAGE_SIZE;
        for (int page = 2; page <= totalPages; page++) {
            final int currentPage = page;
            PageResponse<VersionResponse> pageResponse =
                    retryWithBackoff(() -> httpService.getVersions(rootNode, new PageParam(currentPage, PAGE_SIZE)));
            processVersionPage(pageResponse, allVersions);
        }

        log.debug("Retrieved {} versions for root node: {}", allVersions.size(), rootNode);
        return allVersions;
    }

    private void processVersionPage(PageResponse<VersionResponse> pageResponse, List<String> versions) {
        versions.addAll(pageResponse.getItems().stream()
                .map(VersionResponse::getVersion)
                .collect(Collectors.toList()));
    }

    private String getVersionType(String version) {
        // 根据版本号规则判断类型，可以根据实际情况修改
        return version.contains("branch") ? VersionType.BRANCH.name() : VersionType.TRUNK.name();
    }

    private void cleanupIncrementalData(String rootNode, List<String> versions) throws IOException {
        log.info("Starting incremental data cleanup for root node: {}", rootNode);
        Set<String> allUris = new HashSet<>();

        // 获取所有版本的URI
        for (String version : versions) {
            List<String> versionUris = getAllUrisForVersion(version);
            allUris.addAll(versionUris.stream()
                    .map(this::generateUriHash)
                    .collect(Collectors.toSet()));
        }

        // 删除不存在的URI
        repository.deleteByUriHashNotIn(allUris);
        log.info("Completed incremental data cleanup for root node: {}", rootNode);
    }

    private void processVersionGroup(String rootNode, List<String> versions, CollectParam param) {
        log.info("Processing version group for root node: {}, versions count: {}",
                rootNode, versions.size());

        // 串行处理每个版本，但版本内部并行处理
        versions.forEach(version -> {
            try {
                processVersion(rootNode, version, param);
            } catch (Exception e) {
                log.error("Error processing version: {}", version, e);
                // 继续处理其他版本
            }
        });
    }

    private void processVersion(String rootNode, String version, CollectParam param) {
        log.info("Starting to process version: {}", version);
        try {
            List<String> allUris = getAllUrisForVersion(version);

            // 使用分片并行处理URI
            Lists.partition(allUris, BATCH_SIZE)
                    .parallelStream()
                    .forEach(batch -> processBatch(rootNode, version, batch));

            log.info("Completed processing version: {}, processed URI count: {}",
                    version, allUris.size());
        } catch (Exception e) {
            log.error("Error processing version: {}", version, e);
            throw new RuntimeException("Version processing failed", e);
        }
    }

    private List<String> getAllUrisForVersion(String version) throws IOException {
        List<String> allUris = new ArrayList<>();
        PageParam pageParam = new PageParam(1, PAGE_SIZE);

        // 获取第一页和总数
        PageResponse<String> firstPage =
                retryWithBackoff(() -> httpService.getUriList(version, pageParam));
        allUris.addAll(firstPage.getItems());

        // 处理剩余页
        long totalPages = (firstPage.getTotal() + PAGE_SIZE - 1) / PAGE_SIZE;
        for (int page = 2; page <= totalPages; page++) {
            final int currentPage = page;
            PageResponse<String> pageResponse =
                    retryWithBackoff(() -> httpService.getUriList(version, new PageParam(currentPage, PAGE_SIZE)));
            allUris.addAll(pageResponse.getItems());
        }

        return allUris;
    }

    private void processBatch(String rootNode, String version, List<String> uriBatch) {
        List<UriEntity> entities = new ArrayList<>(uriBatch.size());
        List<UriEntity> borrowedEntities = new ArrayList<>(uriBatch.size());

        try {
            // 获取URI详情
            List<Map<String, Object>> details =
                    retryWithBackoff(() -> httpService.getUriDetails(uriBatch));

            // 使用对象池获取实体对象
            for (Map<String, Object> detail : details) {
                UriEntity entity = null;
                try {
                    entity = entityPool.borrowObject();
                    borrowedEntities.add(entity);  // 记录借出的对象
                    fillEntity(entity, rootNode, version, detail);
                    entities.add(entity);
                } catch (Exception e) {
                    log.error("Error borrowing object from pool", e);
                    if (entity != null) {
                        try {
                            entityPool.returnObject(entity);
                            borrowedEntities.remove(entity);
                        } catch (Exception ex) {
                            log.error("Error returning object to pool", ex);
                        }
                    }
                }
            }

            // 批量保存
            repository.saveAll(entities);

        } catch (Exception e) {
            log.error("Error processing URI batch", e);
            throw new RuntimeException("Batch processing failed", e);
        } finally {
            // 确保所有借出的对象都返回池中
            borrowedEntities.forEach(entity -> {
                try {
                    entityPool.returnObject(entity);
                } catch (Exception e) {
                    log.error("Error returning object to pool", e);
                }
            });
        }
    }

    private void fillEntity(UriEntity entity, String rootNode, String version, Map<String, Object> detail) {
        entity.setUri((String) detail.get("uri"));
        entity.setRootNode(rootNode);
        entity.setVersionType(getVersionType(version));
        entity.setUriVersion(version);
        entity.setDetails(detail);
    }

    private String generateUriHash(String uri) {
        return HashUtil.hash(uri);
    }

    private <T> T retryWithBackoff(IOSupplier<T> supplier) throws IOException {
        int retries = 0;
        while (true) {
            try {
                return supplier.get();
            } catch (IOException e) {
                if (++retries == MAX_RETRY) {
                    throw e;
                }
                try {
                    Thread.sleep(RETRY_INTERVAL * (long) Math.pow(2, retries - 1));
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new IOException("Operation interrupted", ie);
                }
            }
        }
    }

    @FunctionalInterface
    private interface IOSupplier<T> {
        T get() throws IOException;
    }
}
```

## BatchProcessUtil.java

```java
package com.study.collect.business.testcase.utils;


import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Function;

@Slf4j
public class BatchProcessUtil {
    private static final int DEFAULT_BATCH_SIZE = 1000;
    private static final int DEFAULT_THREAD_COUNT = Runtime.getRuntime().availableProcessors() * 2;
    private static final long DEFAULT_TIMEOUT = 1L;
    private static final TimeUnit DEFAULT_TIMEOUT_UNIT = TimeUnit.HOURS;

    /**
     * 处理接口，用于批量处理数据
     */
    @FunctionalInterface
    public interface BatchProcessor<T> {
        void process(List<T> batch) throws Exception;
    }

    /**
     * 异步分批处理列表数据
     *
     * @param list      待处理的列表
     * @param processor 处理器
     * @param <T>       数据类型
     */
    public static <T> void processBatch(List<T> list, BatchProcessor<T> processor) {
        processBatch(list, DEFAULT_BATCH_SIZE, processor);
    }

    /**
     * 异步分批处理列表数据
     *
     * @param list      待处理的列表
     * @param batchSize 批次大小
     * @param processor 处理器
     * @param <T>       数据类型
     */
    public static <T> void processBatch(List<T> list, int batchSize, BatchProcessor<T> processor) {
        processBatch(list, batchSize, DEFAULT_THREAD_COUNT, processor);
    }

    /**
     * 异步分批处理列表数据
     *
     * @param list        待处理的列表
     * @param batchSize   批次大小
     * @param threadCount 线程数
     * @param processor   处理器
     * @param <T>         数据类型
     */
    public static <T> void processBatch(List<T> list, int batchSize, int threadCount, BatchProcessor<T> processor) {
        if (list == null || list.isEmpty()) {
            return;
        }

        // 创建线程池
        ExecutorService executorService = new ThreadPoolExecutor(
                threadCount, threadCount,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(5000),
                new ThreadFactoryBuilder().setNameFormat("batch-process-%d").build(),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );

        try {
            // 计算总批次数
            int totalBatches = (list.size() + batchSize - 1) / batchSize;
            CountDownLatch latch = new CountDownLatch(totalBatches);
            List<Future<?>> futures = new ArrayList<>(totalBatches);

            // 分批提交任务
            for (int i = 0; i < list.size(); i += batchSize) {
                final int start = i;
                final int end = Math.min(start + batchSize, list.size());
                List<T> batch = list.subList(start, end);

                Future<?> future = executorService.submit(() -> {
                    try {
                        processor.process(batch);
                    } catch (Exception e) {
                        log.error("Error processing batch [{}, {}]", start, end, e);
                        throw new RuntimeException(e);
                    } finally {
                        latch.countDown();
                    }
                });
                futures.add(future);
            }

            // 等待所有任务完成
            if (!latch.await(DEFAULT_TIMEOUT, DEFAULT_TIMEOUT_UNIT)) {
                log.warn("Batch processing timeout after {} {}", DEFAULT_TIMEOUT, DEFAULT_TIMEOUT_UNIT);
            }

            // 检查是否有任务异常
            for (Future<?> future : futures) {
                try {
                    future.get(0, TimeUnit.MILLISECONDS);
                } catch (Exception e) {
                    log.error("Task execution failed", e);
                }
            }

        } catch (Exception e) {
            log.error("Error in batch processing", e);
            throw new RuntimeException("Batch processing failed", e);
        } finally {
            executorService.shutdownNow();
        }
    }

    /**
     * MongoDB批量保存工具方法
     *
     * @param list       数据列表
     * @param repository MongoDB仓库
     * @param <T>        实体类型
     */
    public static <T> void saveToMongo(List<T> list, MongoRepository<T, String> repository) {
        processBatch(list, DEFAULT_BATCH_SIZE, batch -> repository.saveAll(batch));
    }

    /**
     * MongoDB批量保存工具方法（支持转换）
     *
     * @param list       数据列表
     * @param converter  转换函数
     * @param repository MongoDB仓库
     * @param <S>        源数据类型
     * @param <T>        目标实体类型
     */
    public static <S, T> void saveToMongo(List<S> list,
                                          Function<S, T> converter,
                                          MongoRepository<T, String> repository) {
        processBatch(list, DEFAULT_BATCH_SIZE, batch -> {
            List<T> entities = new ArrayList<>(batch.size());
            for (S source : batch) {
                entities.add(converter.apply(source));
            }
            repository.saveAll(entities);
        });
    }
}

/// / 使用示例：
//@Service
//public class UriCollectServiceImpl {
//
//    public void saveUriDetails(List<Map<String, Object>> details) {
//        // 方式1：直接使用处理器
//        BatchProcessUtil.processBatch(details, 1000, batch -> {
//            List<UriEntity> entities = new ArrayList<>(batch.size());
//            for (Map<String, Object> detail : batch) {
//                UriEntity entity = convertToEntity(detail);
//                entities.add(entity);
//            }
//            repository.saveAll(entities);
//        });
//
//        // 方式2：使用转换函数
//        BatchProcessUtil.saveToMongo(
//                details,
//                this::convertToEntity,  // 转换函数
//                repository             // MongoDB仓库
//        );
//    }
//
//    private UriEntity convertToEntity(Map<String, Object> detail) {
//        UriEntity entity = new UriEntity();
//        entity.setUri((String) detail.get("uri"));
//        // ... 设置其他字段
//        return entity;
//    }
//}
//

```

## BatchThreadManager.java

```java
package com.study.collect.business.testcase.utils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public class BatchThreadManager<T> {
    private final int totalSize;
    private final int batchSize;
    private final List<T> dataList;
    private final Consumer<List<T>> batchProcessor;
    private final AtomicInteger completedBatches = new AtomicInteger(0);
    private final AtomicLong startTime = new AtomicLong(0);
    private final AtomicLong endTime = new AtomicLong(0);
    private volatile int maxConcurrentThreads;
    private volatile boolean isVirtualThread;
    private volatile ExecutorService executorService;
    private final ConcurrentHashMap<String, ThreadTaskInfo> threadTaskInfoMap = new ConcurrentHashMap<>();

    public static class ThreadTaskInfo {
        private final long startTime;
        private volatile long endTime;
        private final int batchNumber;
        private final int batchSize;
        private volatile TaskStatus status;

        public ThreadTaskInfo(int batchNumber, int batchSize) {
            this.startTime = System.currentTimeMillis();
            this.batchNumber = batchNumber;
            this.batchSize = batchSize;
            this.status = TaskStatus.RUNNING;
        }

        public void complete() {
            this.endTime = System.currentTimeMillis();
            this.status = TaskStatus.COMPLETED;
        }

        public void fail() {
            this.endTime = System.currentTimeMillis();
            this.status = TaskStatus.FAILED;
        }

        @Override
        public String toString() {
            return String.format(
                    "Batch %d (size: %d) - Status: %s, Duration: %dms",
                    batchNumber, batchSize, status,
                    (endTime > 0 ? endTime - startTime : System.currentTimeMillis() - startTime)
            );
        }
    }

    public enum TaskStatus {
        RUNNING, COMPLETED, FAILED
    }

    public BatchThreadManager(List<T> dataList, int batchSize, Consumer<List<T>> batchProcessor,
                              int maxConcurrentThreads, boolean isVirtualThread) {
        this.dataList = dataList;
        this.totalSize = dataList.size();
        this.batchSize = batchSize;
        this.batchProcessor = batchProcessor;
        this.maxConcurrentThreads = maxConcurrentThreads;
        this.isVirtualThread = isVirtualThread;
        initializeExecutor();
    }

    private void initializeExecutor() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }

        if (isVirtualThread) {
            executorService = Executors.newVirtualThreadPerTaskExecutor();
        } else {
            executorService = new ThreadPoolExecutor(
                    maxConcurrentThreads, maxConcurrentThreads,
                    0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<>(),
                    new ThreadPoolExecutor.CallerRunsPolicy()
            );
        }
    }

    public void updateThreadLimit(int newLimit) {
        this.maxConcurrentThreads = newLimit;
        if (!isVirtualThread) {
            initializeExecutor();
        }
    }

    public void switchThreadType(boolean useVirtualThread) {
        this.isVirtualThread = useVirtualThread;
        initializeExecutor();
    }

    public CompletableFuture<Void> executeBatches() {
        startTime.set(System.currentTimeMillis());
        int totalBatches = (totalSize + batchSize - 1) / batchSize;
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (int i = 0; i < totalSize; i += batchSize) {
            int batchNumber = i / batchSize;
            int start = i;
            int end = Math.min(i + batchSize, totalSize);

            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                String threadName = Thread.currentThread().getName();
                ThreadTaskInfo taskInfo = new ThreadTaskInfo(batchNumber, end - start);
                threadTaskInfoMap.put(threadName, taskInfo);

                try {
                    List<T> batch = dataList.subList(start, end);
                    batchProcessor.accept(batch);
                    taskInfo.complete();
                    completedBatches.incrementAndGet();
                } catch (Exception e) {
                    taskInfo.fail();
                    throw new CompletionException(e);
                }
            }, executorService);

            futures.add(future);
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .whenComplete((v, e) -> {
                    endTime.set(System.currentTimeMillis());
                    executorService.shutdown();
                });
    }

    public double getProgress() {
        return (double) completedBatches.get() * batchSize / totalSize * 100;
    }

    public Duration getExecutionTime() {
        long end = endTime.get() > 0 ? endTime.get() : System.currentTimeMillis();
        return Duration.ofMillis(end - startTime.get());
    }

    public List<ThreadTaskInfo> getActiveTaskInfo() {
        return threadTaskInfoMap.values().stream()
                .filter(info -> info.status == TaskStatus.RUNNING)
                .toList();
    }

    public List<ThreadTaskInfo> getCompletedTaskInfo() {
        return threadTaskInfoMap.values().stream()
                .filter(info -> info.status == TaskStatus.COMPLETED)
                .toList();
    }

    public List<ThreadTaskInfo> getFailedTaskInfo() {
        return threadTaskInfoMap.values().stream()
                .filter(info -> info.status == TaskStatus.FAILED)
                .toList();
    }
}

```

## HashUtil.java

```java
package com.study.collect.business.testcase.utils;

import org.apache.commons.codec.digest.DigestUtils;

import org.apache.commons.codec.digest.DigestUtils;

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

## MongoPageUtil.java

```java
package com.study.collect.business.testcase.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.function.Consumer;

// MongoDB分页查询工具类
@Slf4j
public class MongoPageUtil {
    private static final int DEFAULT_BATCH_SIZE = 2000;

    /**
     * 分页查询MongoDB数据
     */
    public static <T> void pageQuery(MongoOperations mongoOperations,
                                     Query query,
                                     Class<T> entityClass,
                                     Consumer<List<T>> consumer) {
        pageQuery(mongoOperations, query, entityClass, DEFAULT_BATCH_SIZE, consumer);
    }

    /**
     * 分页查询MongoDB数据（指定批次大小）
     */
    public static <T> void pageQuery(MongoOperations mongoOperations,
                                     Query query,
                                     Class<T> entityClass,
                                     int batchSize,
                                     Consumer<List<T>> consumer) {
        long total = mongoOperations.count(query, entityClass);
        int pages = (int) Math.ceil((double) total / batchSize);

        for (int page = 0; page < pages; page++) {
            Query pageQuery = Query.from(query)
                    .skip((long) page * batchSize)
                    .limit(batchSize);

            List<T> batch = mongoOperations.find(pageQuery, entityClass);
            try {
                consumer.accept(batch);
            } catch (Exception e) {
                log.error("Error processing batch at page {}", page, e);
                throw new RuntimeException("Error processing batch", e);
            }
        }
    }
}

```

## PageUtil.java

```java
package com.study.collect.business.testcase.utils;


import com.study.collect.business.testcase.model.param.PageParam;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class PageUtil {
    public static Pageable toPageable(PageParam param) {
        return PageRequest.of(param.getPage() - 1, param.getSize());
    }
}
```

## spring.factories

```
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
com.study.collect.business.testcase.config.TestCaseAutoConfiguration
```

