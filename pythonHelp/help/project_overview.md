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
                                    common/
                                        constants/
                                            CollectionConstants.java
                                            VersionType.java
                                        enums/
                                        utils/
                                            HashUtil.java
                                            HttpUtil.java
                                            ListCompareUtil.java
                                            RateLimiter.java
                                            StreamProcessor.java
                                            TableNameHelper.java
                                    config/
                                        GlobalExceptionHandler.java
                                        HttpConfigBuilder.java
                                        HttpRequestConfig.java
                                        MongoConfig.java
                                        ObjectPoolConfig.java
                                        TestCaseAutoConfiguration.java
                                        TestCaseCollectorProperties.java
                                        TestCaseConfig.java
                                        ThreadPoolConfig.java
                                    controller/
                                        UriCollectController.java
                                    core/
                                        executor/
                                            CollectExecutor.java
                                            DeleteExecutor.java
                                        manager/
                                            QueueManager.java
                                            TaskManager.java
                                        processor/
                                            CollectProcessor.java
                                            DataProcessor.java
                                            DeleteProcessor.java
                                    entity/
                                        BaseEntity.java
                                        UriEntity.java
                                        VersionEntity.java
                                    model/
                                        PageResult.java
                                        param/
                                            CollectParam.java
                                            DeleteParam.java
                                            PageParam.java
                                            QueryParam.java
                                        request/
                                        response/
                                            AsyncResponse.java
                                            BaseResponse.java
                                            PageResponse.java
                                            TaskResponse.java
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
            <groupId>io.micrometer</groupId>
            <artifactId>micrometer-core</artifactId>
            <version>1.7.0</version>
        </dependency>
        <dependency>
            <groupId>javax.validation</groupId>
            <artifactId>validation-api</artifactId>
            <version>2.0.1.Final</version>
        </dependency>
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

## CollectionConstants.java

```java
package com.study.collect.business.testcase.common.constants;

/**
 * 系统常量配置
 */
public final class CollectionConstants {

    // 集合相关
    public static final class Collection {
        public static final String URI_COLLECTION_PREFIX = "uri_collect";
        public static final String VERSION_PREFIX = "V";
        public static final String VERSION_SEPARATOR = "_";

        private Collection() {}
    }

    // HTTP相关
    public static final class Http {
        public static final int MAX_REQUESTS_PER_MINUTE = 500;
        public static final int CONNECT_TIMEOUT = 5000;
        public static final int READ_TIMEOUT = 15000;
        public static final int MAX_RETRY = 3;
        public static final long RETRY_INTERVAL = 1000L;

        private Http() {}
    }

    // 线程池相关
    public static final class ThreadPool {
        // HTTP请求线程池
        public static final int HTTP_CORE_SIZE = Runtime.getRuntime().availableProcessors() * 2;
        public static final int HTTP_MAX_SIZE = Runtime.getRuntime().availableProcessors() * 4;
        public static final int HTTP_QUEUE_SIZE = 5000;
        public static final long HTTP_KEEP_ALIVE = 60L;

        // MongoDB操作线程池
        public static final int MONGO_CORE_SIZE = Runtime.getRuntime().availableProcessors();
        public static final int MONGO_MAX_SIZE = Runtime.getRuntime().availableProcessors() * 2;
        public static final int MONGO_QUEUE_SIZE = 10000;
        public static final long MONGO_KEEP_ALIVE = 60L;

        // 任务处理线程池
        public static final int TASK_CORE_SIZE = 5;
        public static final int TASK_MAX_SIZE = 10;
        public static final int TASK_QUEUE_SIZE = 100;
        public static final long TASK_KEEP_ALIVE = 60L;

        private ThreadPool() {}
    }

    // 数据库相关
    public static final class Database {
        public static final int MONGO_BATCH_SIZE = 1000;
        public static final int MONGO_MAX_POOL_SIZE = 100;
        public static final int MONGO_MIN_POOL_SIZE = 20;

        private Database() {}
    }

    // 处理相关
    public static final class Process {
        public static final int DEFAULT_BATCH_SIZE = 200;
        public static final int MAX_BATCH_SIZE = 1000;
        public static final int MIN_BATCH_SIZE = 50;
        public static final long TASK_TIMEOUT = 3600L;
        public static final int MAX_CONCURRENT_TASKS = 10;
        public static final int TASK_QUEUE_CAPACITY = 100;

        private Process() {}
    }

    // 对象池相关
    public static final class Pool {
        public static final int MAX_TOTAL = 20;
        public static final int MAX_IDLE = 10;
        public static final int MIN_IDLE = 5;

        private Pool() {}
    }

    private CollectionConstants() {}
}
```

## VersionType.java

```java
package com.study.collect.business.testcase.common.constants;

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

## HashUtil.java

```java
package com.study.collect.business.testcase.common.utils;

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
package com.study.collect.business.testcase.common.utils;

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
package com.study.collect.business.testcase.common.utils;

import lombok.extern.slf4j.Slf4j;
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
package com.study.collect.business.testcase.common.utils;


import com.study.collect.business.testcase.common.constants.CollectionConstants;
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
        this.permitsPerMinute = CollectionConstants.Http.MAX_REQUESTS_PER_MINUTE;
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
            Thread.sleep(100);  // 等待100ms后重试
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

## StreamProcessor.java

```java
package com.study.collect.business.testcase.common.utils;

import com.study.collect.business.testcase.common.constants.CollectionConstants;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 通用流式处理器
 * @param <T> 源数据类型
 * @param <R> 结果数据类型
 */
@Slf4j
public class StreamProcessor<T, R> {

    @Data
    @Builder
    public static class ProcessorConfig<T, R> {
        // 基础配置
        private String processorName;
        private int batchSize;
        private int maxConcurrent;
        private long timeoutSeconds;
        private int maxRetries;
        private long retryDelayMs;
        private boolean continueOnError;

        // 线程池
        private ExecutorService processExecutor;
        private ExecutorService saveExecutor;

        // 处理函数
        private Function<Integer, List<T>> dataFetcher;  // 数据获取函数
        private Function<T, R> dataConverter;           // 数据转换函数
        private Consumer<List<R>> dataSaver;           // 数据保存函数
        private Consumer<ProcessMetrics> progressCallback; // 进度回调

        // 验证器
        private Function<T, Boolean> dataValidator;    // 数据验证函数
        private Function<R, Boolean> resultValidator;  // 结果验证函数
    }

    @Data
    @Builder
    public static class ProcessMetrics {
        private String processorName;
        private long totalItems;
        private long processedItems;
        private long failedItems;
        private long startTime;
        private Long endTime;
        private double progressPercentage;
        private Map<String, Object> customMetrics;
        private String currentStage;
        private String statusMessage;
    }

    private final ProcessorConfig<T, R> config;
    private final BlockingQueue<CompletableFuture<?>> processQueue;
    private final AtomicInteger activeProcesses;
    private final AtomicBoolean running;
    private final AtomicReference<ProcessMetrics> currentMetrics;
    private final List<ProcessMetrics> metricsHistory;

    public StreamProcessor(ProcessorConfig<T, R> config) {
        validateConfig(config);
        this.config = config;
        this.processQueue = new ArrayBlockingQueue<>(1000);
        this.activeProcesses = new AtomicInteger(0);
        this.running = new AtomicBoolean(true);
        this.currentMetrics = new AtomicReference<>(initializeMetrics());
        this.metricsHistory = new CopyOnWriteArrayList<>();
    }

    /**
     * 开始处理数据
     */
    public CompletableFuture<ProcessMetrics> process(int offset, int limit) {
        CompletableFuture<ProcessMetrics> resultFuture = new CompletableFuture<>();

        try {
            if (activeProcesses.incrementAndGet() <= config.getMaxConcurrent()) {
                processDataBatches(offset, limit, resultFuture);
            } else {
                activeProcesses.decrementAndGet();
                throw new RejectedExecutionException("Max concurrent processes reached");
            }
        } catch (Exception e) {
            activeProcesses.decrementAndGet();
            resultFuture.completeExceptionally(e);
        }

        return resultFuture;
    }

    private void processDataBatches(
            int offset,
            int limit,
            CompletableFuture<ProcessMetrics> resultFuture
    ) {
        CompletableFuture.runAsync(() -> {
            try {
                int processed = 0;
                updateMetrics("FETCHING", "Starting data fetch", processed, limit);

                while (running.get() && processed < limit) {
                    // 获取一批数据
                    List<T> batch = fetchData(offset + processed);
                    if (batch.isEmpty()) {
                        break;
                    }

                    // 处理这批数据
                    processBatch(batch);
                    processed += batch.size();

                    // 更新进度
                    updateMetrics("PROCESSING",
                            String.format("Processed %d/%d items", processed, limit),
                            processed, limit);
                }

                // 完成处理
                completeProcessing(resultFuture);

            } catch (Exception e) {
                handleProcessingError(e, resultFuture);
            }
        }, config.getProcessExecutor());
    }

    private List<T> fetchData(int offset) {
        int retryCount = 0;
        while (retryCount <= config.getMaxRetries()) {
            try {
                List<T> data = config.getDataFetcher().apply(offset);

                // 验证数据
                if (config.getDataValidator() != null) {
                    data = validateData(data);
                }

                return data;
            } catch (Exception e) {
                if (++retryCount > config.getMaxRetries()) {
                    log.error("Failed to fetch data after {} retries", config.getMaxRetries(), e);
                    if (!config.isContinueOnError()) {
                        throw new RuntimeException("Data fetch failed", e);
                    }
                    return Collections.emptyList();
                }
                sleep(calculateRetryDelay(retryCount));
            }
        }
        return Collections.emptyList();
    }

    private void processBatch(List<T> batch) {
        List<R> convertedBatch = new ArrayList<>();

        // 转换数据
        for (T item : batch) {
            try {
                R converted = config.getDataConverter().apply(item);
                if (converted != null && (config.getResultValidator() == null ||
                        config.getResultValidator().apply(converted))) {
                    convertedBatch.add(converted);
                }
            } catch (Exception e) {
                handleItemError(item, e);
            }
        }

        if (!convertedBatch.isEmpty()) {
            saveBatch(convertedBatch);
        }
    }

    private void saveBatch(List<R> batch) {
        int retryCount = 0;
        while (retryCount <= config.getMaxRetries()) {
            try {
                CompletableFuture<Void> saveFuture = CompletableFuture.runAsync(() ->
                                config.getDataSaver().accept(batch)
                        , config.getSaveExecutor());

                processQueue.put(saveFuture);
                cleanupCompletedTasks();
                return;
            } catch (Exception e) {
                if (++retryCount > config.getMaxRetries()) {
                    log.error("Failed to save batch after {} retries", config.getMaxRetries(), e);
                    if (!config.isContinueOnError()) {
                        throw new RuntimeException("Batch save failed", e);
                    }
                }
                sleep(calculateRetryDelay(retryCount));
            }
        }
    }

    private List<T> validateData(List<T> data) {
        return data.stream()
                .filter(item -> {
                    try {
                        return config.getDataValidator().apply(item);
                    } catch (Exception e) {
                        log.warn("Data validation failed for item: {}", item, e);
                        return false;
                    }
                })
                .toList();
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
                .processorName(config.getProcessorName())
                .startTime(System.currentTimeMillis())
                .totalItems(0)
                .processedItems(0)
                .failedItems(0)
                .progressPercentage(0.0)
                .customMetrics(new ConcurrentHashMap<>())
                .build();
    }

    private void updateMetrics(String stage, String message, long processed, long total) {
        ProcessMetrics metrics = currentMetrics.get();
        metrics.setCurrentStage(stage);
        metrics.setStatusMessage(message);
        metrics.setProcessedItems(processed);
        metrics.setTotalItems(total);
        metrics.setProgressPercentage(total > 0 ? (processed * 100.0) / total : 0.0);

        if (config.getProgressCallback() != null) {
            config.getProgressCallback().accept(metrics);
        }
    }

    private void handleItemError(T item, Exception e) {
        ProcessMetrics metrics = currentMetrics.get();
        metrics.setFailedItems(metrics.getFailedItems() + 1);
        log.error("Error processing item: {}", item, e);
    }

    private void completeProcessing(CompletableFuture<ProcessMetrics> resultFuture) {
        ProcessMetrics metrics = currentMetrics.get();
        metrics.setEndTime(System.currentTimeMillis());
        metricsHistory.add(metrics);
        activeProcesses.decrementAndGet();
        resultFuture.complete(metrics);
    }

    private void handleProcessingError(Exception e, CompletableFuture<ProcessMetrics> resultFuture) {
        ProcessMetrics metrics = currentMetrics.get();
        metrics.setEndTime(System.currentTimeMillis());
        metrics.setStatusMessage("Error: " + e.getMessage());
        metricsHistory.add(metrics);
        activeProcesses.decrementAndGet();
        resultFuture.completeExceptionally(e);
    }

    private long calculateRetryDelay(int retryCount) {
        return config.getRetryDelayMs() * (long)Math.pow(2, retryCount - 1);
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Processing interrupted", e);
        }
    }

    private void validateConfig(ProcessorConfig<T, R> config) {
        Objects.requireNonNull(config.getDataFetcher(), "DataFetcher cannot be null");
        Objects.requireNonNull(config.getDataConverter(), "DataConverter cannot be null");
        Objects.requireNonNull(config.getDataSaver(), "DataSaver cannot be null");
        Objects.requireNonNull(config.getProcessExecutor(), "ProcessExecutor cannot be null");
        Objects.requireNonNull(config.getSaveExecutor(), "SaveExecutor cannot be null");
    }

    // 公共方法
    public void pause() {
        running.set(false);
    }

    public void resume() {
        running.set(true);
    }

    public void shutdown() {
        running.set(false);
        processQueue.clear();
        activeProcesses.set(0);
    }

    public List<ProcessMetrics> getMetricsHistory() {
        return new ArrayList<>(metricsHistory);
    }

    public ProcessMetrics getCurrentMetrics() {
        return currentMetrics.get();
    }

    public boolean isRunning() {
        return running.get();
    }

    public int getActiveProcessCount() {
        return activeProcesses.get();
    }

    public int getQueueSize() {
        return processQueue.size();
    }
}
```

## TableNameHelper.java

```java
package com.study.collect.business.testcase.common.utils;

import com.study.collect.business.testcase.common.constants.CollectionConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * 表名处理工具类
 */
@Slf4j
public class TableNameHelper {

    private static final Map<String, String> TABLE_NAME_CACHE = new ConcurrentHashMap<>();
    private static final Pattern TABLE_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]+$");
    private static final int MAX_TABLE_NAME_LENGTH = 64;

    /**
     * 生成完整表名
     */
    public static String getTableName(String rootNode) {
        Assert.hasText(rootNode, "RootNode must not be empty");

        return TABLE_NAME_CACHE.computeIfAbsent(rootNode, key -> {
            String tableName = CollectionConstants.Collection.URI_COLLECTION_PREFIX + "_" + key;
            validateTableName(tableName);
            return tableName;
        });
    }

    /**
     * 生成带版本的表名
     */
    public static String getVersionedTableName(String rootNode, String version) {
        Assert.hasText(rootNode, "RootNode must not be empty");
        Assert.hasText(version, "Version must not be empty");

        String cacheKey = rootNode + "_" + version;
        return TABLE_NAME_CACHE.computeIfAbsent(cacheKey, key -> {
            String tableName = CollectionConstants.Collection.URI_COLLECTION_PREFIX +
                    "_" + rootNode +
                    "_" + version;
            validateTableName(tableName);
            return tableName;
        });
    }

    /**
     * 从URI中提取rootNode
     */
    public static String extractRootNode(String uri) {
        Assert.hasText(uri, "URI must not be empty");
        int firstSlash = uri.indexOf('/');
        return firstSlash == -1 ? uri : uri.substring(0, firstSlash);
    }

    /**
     * 生成URI哈希值
     */
    public static String generateUriHash(String uri) {
        Assert.hasText(uri, "URI must not be empty");
        return DigestUtils.sha256Hex(uri);
    }

    /**
     * 生成完整的文档ID
     */
    public static String generateDocumentId(String uri, String version) {
        Assert.hasText(uri, "URI must not be empty");
        return version == null ?
                DigestUtils.sha256Hex(uri) :
                DigestUtils.sha256Hex(uri + "_" + version);
    }

    /**
     * 检查URI是否属于指定表
     */
    public static boolean isUriMatchTable(String uri, String tableName) {
        String rootNode = extractRootNode(uri);
        String expectedTableName = getTableName(rootNode);
        return expectedTableName.equals(tableName);
    }

    /**
     * 解析表名中的rootNode
     */
    public static String extractRootNodeFromTableName(String tableName) {
        Assert.hasText(tableName, "Table name must not be empty");
        String prefix = CollectionConstants.Collection.URI_COLLECTION_PREFIX + "_";
        if (!tableName.startsWith(prefix)) {
            throw new IllegalArgumentException("Invalid table name format: " + tableName);
        }
        String remaining = tableName.substring(prefix.length());
        int versionSeparator = remaining.indexOf('_');
        return versionSeparator == -1 ? remaining : remaining.substring(0, versionSeparator);
    }

    /**
     * 验证表名是否合法
     */
    private static void validateTableName(String tableName) {
        if (!TABLE_NAME_PATTERN.matcher(tableName).matches()) {
            throw new IllegalArgumentException("Invalid table name characters: " + tableName);
        }
        if (tableName.length() > MAX_TABLE_NAME_LENGTH) {
            throw new IllegalArgumentException("Table name too long: " + tableName);
        }
    }

    /**
     * 清除表名缓存
     */
    public static void clearCache() {
        TABLE_NAME_CACHE.clear();
    }

    /**
     * 预热表名缓存
     */
    public static void warmupCache(List<String> rootNodes) {
        for (String rootNode : rootNodes) {
            getTableName(rootNode);
        }
    }

    /**
     * 获取缓存大小
     */
    public static int getCacheSize() {
        return TABLE_NAME_CACHE.size();
    }

    /**
     * 验证表名格式
     */
    public static boolean isValidTableName(String tableName) {
        return TABLE_NAME_PATTERN.matcher(tableName).matches() &&
                tableName.length() <= MAX_TABLE_NAME_LENGTH;
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

## HttpConfigBuilder.java

```java
package com.study.collect.business.testcase.config;

import com.study.collect.business.testcase.common.utils.HttpUtil;

/**
 * HTTP配置构建器
 */
public class HttpConfigBuilder {
    public static HttpUtil.HttpConfig buildDefaultConfig() {
        return HttpUtil.HttpConfig.builder()
                .connectTimeout(5000)
                .readTimeout(15000)
                .maxRetries(3)
                .retryInterval(1000)
                .maxConnections(100)
                .maxConnectionsPerRoute(20)
                .enableMetrics(true)
                .enableRateLimiter(true)
                .retryPredicate(response -> response.getCode() >= 500 ||
                        response.getCode() == 429)
                .build();
    }
}
```

## HttpRequestConfig.java

```java
package com.study.collect.business.testcase.common.utils;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * HTTP请求配置
 */
@Data
@Builder
public class HttpRequestConfig {
    private String method;
    private String url;
    private String body;
    private Map<String, String> headers;
    private int timeout;
    private int retries;
    private boolean rateLimit;

    public static HttpRequestConfig.HttpRequestConfigBuilder defaultConfig() {
        return HttpRequestConfig.builder()
                .method("GET")
                .timeout(5000)
                .retries(3)
                .rateLimit(true);
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
import com.mongodb.connection.ClusterSettings;
import com.mongodb.connection.ConnectionPoolSettings;
import com.mongodb.connection.ServerSettings;
import com.mongodb.connection.SocketSettings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
@EnableMongoAuditing
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "spring.data.mongodb", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableMongoRepositories(basePackages = "com.study.collect.business.testcase.repository")
public class MongoConfig extends AbstractMongoClientConfiguration {

    private final TestCaseCollectorProperties properties;

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
    @Primary
    public MongoClient mongoClient() {
        ConnectionString connectionString = new ConnectionString(uri);
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                // 集群设置
                .applyToClusterSettings(builder -> {
                    ClusterSettings.Builder clusterBuilder = ClusterSettings.builder()
                            .serverSelectionTimeout(5000, TimeUnit.MILLISECONDS);
                    builder.applySettings(clusterBuilder.build());
                })
                // 连接池设置
                .applyToConnectionPoolSettings(builder -> {
                    ConnectionPoolSettings.Builder poolBuilder = ConnectionPoolSettings.builder()
                            .minSize(properties.getMongo().getMinPoolSize())
                            .maxSize(properties.getMongo().getMaxPoolSize())
                            .maxWaitTime(10000, TimeUnit.MILLISECONDS)
                            .maxConnectionLifeTime(30, TimeUnit.MINUTES)
                            .maxConnectionIdleTime(5, TimeUnit.MINUTES)
                            .maintenanceInitialDelay(1, TimeUnit.MINUTES)
                            .maintenanceFrequency(1, TimeUnit.MINUTES);
                    builder.applySettings(poolBuilder.build());
                })
                // Socket设置
                .applyToSocketSettings(builder -> {
                    SocketSettings.Builder socketBuilder = SocketSettings.builder()
                            .connectTimeout(properties.getHttp().getConnectTimeout(), TimeUnit.MILLISECONDS)
                            .readTimeout(properties.getHttp().getReadTimeout(), TimeUnit.MILLISECONDS);
                    builder.applySettings(socketBuilder.build());
                })
                // 服务器设置
                .applyToServerSettings(builder -> {
                    ServerSettings.Builder serverBuilder = ServerSettings.builder()
                            .heartbeatFrequency(10000, TimeUnit.MILLISECONDS);
                    builder.applySettings(serverBuilder.build());
                })
                .retryWrites(true)
                .retryReads(true)
                .build();

        return MongoClients.create(settings);
    }

    @Bean
    @Primary
    public MongoTemplate mongoTemplate(MongoClient mongoClient, MongoMappingContext context) {
        MappingMongoConverter converter = new MappingMongoConverter(
                new DefaultDbRefResolver(mongoDbFactory()),
                context
        );
        // 去掉_class字段
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        return new MongoTemplate(mongoDbFactory(), converter);
    }

    @Bean
    public MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }

    /**
     * 监控指标收集器
     */
    @Bean
    public MongoMetricsCollector mongoMetricsCollector(MongoTemplate mongoTemplate) {
        return new MongoMetricsCollector(mongoTemplate, properties);
    }

    /**
     * MongoDB 健康检查器
     */
    @Bean
    public MongoHealthIndicator mongoHealthIndicator(MongoTemplate mongoTemplate) {
        return new MongoHealthIndicator(mongoTemplate, properties);
    }
}

/**
 * MongoDB 监控指标收集器
 */
@Slf4j
@RequiredArgsConstructor
class MongoMetricsCollector {
    private final MongoTemplate mongoTemplate;
    private final TestCaseCollectorProperties properties;

    public void collectMetrics() {
        try {
            Document stats = mongoTemplate.getDb().runCommand(new Document("dbStats", 1));
            log.debug("MongoDB stats: {}", stats);
            // 这里可以将指标发送到监控系统
        } catch (Exception e) {
            log.error("Failed to collect MongoDB metrics", e);
        }
    }
}

/**
 * MongoDB 健康检查器
 */
@Slf4j
@RequiredArgsConstructor
class MongoHealthIndicator {
    private final MongoTemplate mongoTemplate;
    private final TestCaseCollectorProperties properties;

    public boolean isHealthy() {
        try {
            mongoTemplate.executeCommand("{ ping: 1 }");
            return true;
        } catch (Exception e) {
            log.error("MongoDB health check failed", e);
            return false;
        }
    }
}
```

## ObjectPoolConfig.java

```java
package com.study.collect.business.testcase.config;


import com.study.collect.business.testcase.common.constants.CollectionConstants;
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
    private final TestCaseCollectorProperties properties;
    public ObjectPoolConfig(TestCaseCollectorProperties properties) {
        this.properties = properties;
    }
    @Bean(destroyMethod = "close")
    public GenericObjectPool<UriEntity> uriEntityPool() {
        GenericObjectPoolConfig<UriEntity> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxTotal(TestCaseCollectorProperties.Collect.getPoolMaxTotal());
        poolConfig.setMaxIdle(properties.getPoolMaxIdle());
        poolConfig.setMinIdle(properties.getPoolMinIdle());
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setBlockWhenExhausted(true);
        poolConfig.setTimeBetweenEvictionRuns(java.time.Duration.ofMinutes(1));
        poolConfig.setJmxEnabled(true);
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

import com.study.collect.business.testcase.common.constants.CollectionConstants;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
// TestCaseCollectorProperties 修改建议
@Data
@Validated
@ConfigurationProperties(prefix = "collect.testcase")
public class TestCaseCollectorProperties {

    // HTTP相关配置
    private final Http http = new Http();

    // MongoDB相关配置
    private final Mongo mongo = new Mongo();

    // 线程池相关配置
    private final ThreadPool threadPool = new ThreadPool();

    // 任务相关配置
    private final Task task = new Task();

    // URI采集相关配置
    private final Collect collect = new Collect();

    @Data
    public static class Http {
        @Min(100)
        @Max(1000)
        private int maxRequestsPerMinute = CollectionConstants.Http.MAX_REQUESTS_PER_MINUTE;

        @Min(1000)
        @Max(30000)
        private int connectTimeout = CollectionConstants.Http.CONNECT_TIMEOUT;

        @Min(1000)
        @Max(60000)
        private int readTimeout = CollectionConstants.Http.READ_TIMEOUT;

        @Min(0)
        @Max(10)
        private int maxRetries = CollectionConstants.Http.MAX_RETRY;

        @Min(100)
        @Max(5000)
        private long retryInterval = CollectionConstants.Http.RETRY_INTERVAL;
    }

    @Data
    public static class Mongo {
        @Min(10)
        @Max(200)
        private int minPoolSize = CollectionConstants.Database.MONGO_MIN_POOL_SIZE;

        @Min(50)
        @Max(500)
        private int maxPoolSize = CollectionConstants.Database.MONGO_MAX_POOL_SIZE;

        @Min(100)
        @Max(5000)
        private int batchSize = CollectionConstants.Database.MONGO_BATCH_SIZE;

        private boolean enableSharding = false;
        private String shardKey = "rootNode";
        private String defaultCollectionPrefix = "uri_collect";
    }

    @Data
    public static class ThreadPool {
        // HTTP线程池配置
        @Min(1)
        @Max(100)
        private int httpCoreSize = CollectionConstants.ThreadPool.HTTP_CORE_SIZE;

        @Min(1)
        @Max(200)
        private int httpMaxSize = CollectionConstants.ThreadPool.HTTP_MAX_SIZE;

        @Min(100)
        @Max(10000)
        private int httpQueueSize = CollectionConstants.ThreadPool.HTTP_QUEUE_SIZE;

        // MongoDB线程池配置
        @Min(1)
        @Max(50)
        private int mongoCoreSize = CollectionConstants.ThreadPool.MONGO_CORE_SIZE;

        @Min(1)
        @Max(100)
        private int mongoMaxSize = CollectionConstants.ThreadPool.MONGO_MAX_SIZE;

        @Min(100)
        @Max(20000)
        private int mongoQueueSize = CollectionConstants.ThreadPool.MONGO_QUEUE_SIZE;

        // 任务线程池配置
        @Min(1)
        @Max(20)
        private int taskCoreSize = CollectionConstants.ThreadPool.TASK_CORE_SIZE;

        @Min(1)
        @Max(50)
        private int taskMaxSize = CollectionConstants.ThreadPool.TASK_MAX_SIZE;

        @Min(10)
        @Max(1000)
        private int taskQueueSize = CollectionConstants.ThreadPool.TASK_QUEUE_SIZE;

        // 是否启用虚拟线程
        private boolean enableVirtualThread = true;
    }

    @Data
    public static class Task {
        @Min(1)
        @Max(50)
        private int maxConcurrentTasks = CollectionConstants.Process.MAX_CONCURRENT_TASKS;

        @Min(10)
        @Max(1000)
        private int queueCapacity = CollectionConstants.Process.TASK_QUEUE_CAPACITY;

        @Min(60)
        @Max(86400)
        private long timeout = CollectionConstants.Process.TASK_TIMEOUT;

        // 任务优先级相关
        private boolean enablePriority = true;
        private int defaultPriority = 0;
        private int maxPriority = 10;

        // 重试相关
        private boolean enableRetry = true;
        private int maxRetries = 3;
        private long retryDelay = 1000;
    }

    @Data
    public static class Collect {
        @Min(50)
        @Max(2000)
        private int defaultBatchSize = CollectionConstants.Process.DEFAULT_BATCH_SIZE;

        @Min(10)
        @Max(1000)
        private int minBatchSize = CollectionConstants.Process.MIN_BATCH_SIZE;

        @Min(100)
        @Max(5000)
        private int maxBatchSize = CollectionConstants.Process.MAX_BATCH_SIZE;

        // 增量同步相关
        private boolean enableIncremental = true;
        private boolean defaultHardDelete = false;

        // 对象池相关
        private int poolMaxTotal = CollectionConstants.Pool.MAX_TOTAL;
        private int poolMaxIdle = CollectionConstants.Pool.MAX_IDLE;
        private int poolMinIdle = CollectionConstants.Pool.MIN_IDLE;
    }

    /**
     * 运行时动态更新配置
     */
    public void updateHttpConfig(Http newConfig) {
        BeanUtils.copyProperties(newConfig, this.http);
    }

    public void updateThreadPoolConfig(ThreadPool newConfig) {
        BeanUtils.copyProperties(newConfig, this.threadPool);
    }

    // 其他配置更新方法...
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
import com.study.collect.business.testcase.common.constants.CollectionConstants;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Configuration
@EnableAsync
@RequiredArgsConstructor
public class ThreadPoolConfig {

    private final TestCaseCollectorProperties properties;
    private final MeterRegistry meterRegistry;

    /**
     * HTTP请求线程池
     */
    @Bean(name = "httpExecutor")
    public ThreadPoolTaskExecutor httpExecutor() {
        ThreadPoolTaskExecutor executor = createBaseExecutor(
                "http-executor",
                properties.getThreadPool().getHttpCoreSize(),
                properties.getThreadPool().getHttpMaxSize(),
                properties.getThreadPool().getHttpQueueSize()
        );
        // 配置任务装饰器，用于监控和统计
        executor.setTaskDecorator(new MonitoringTaskDecorator("http"));
        return executor;
    }

    /**
     * MongoDB操作线程池
     */
    @Bean(name = "mongoExecutor")
    public ThreadPoolTaskExecutor mongoExecutor() {
        ThreadPoolTaskExecutor executor = createBaseExecutor(
                "mongo-executor",
                properties.getThreadPool().getMongoCoreSize(),
                properties.getThreadPool().getMongoMaxSize(),
                properties.getThreadPool().getMongoQueueSize()
        );
        executor.setTaskDecorator(new MonitoringTaskDecorator("mongo"));
        return executor;
    }

    /**
     * 任务处理线程池
     */
    @Bean(name = "taskExecutor")
    @Primary
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = createBaseExecutor(
                "task-executor",
                properties.getThreadPool().getTaskCoreSize(),
                properties.getThreadPool().getTaskMaxSize(),
                properties.getThreadPool().getTaskQueueSize()
        );
        executor.setTaskDecorator(new MonitoringTaskDecorator("task"));
        // 自定义拒绝策略：记录日志并重试
        executor.setRejectedExecutionHandler(new RetryRejectedExecutionHandler());
        return executor;
    }

    /**
     * 虚拟线程执行器（如果JDK版本支持）
     */
    @Bean(name = "virtualThreadExecutor")
    public ExecutorService virtualThreadExecutor() {
        if (properties.getThreadPool().isEnableVirtualThread()) {
            try {
                return Executors.newVirtualThreadPerTaskExecutor();
            } catch (UnsupportedOperationException e) {
                log.warn("Virtual threads not supported, falling back to normal thread pool");
            }
        }
        return createFallbackExecutor();
    }

    /**
     * 调度线程池
     */
    @Bean(name = "scheduledExecutor")
    public ScheduledExecutorService scheduledExecutor() {
        return new ScheduledThreadPoolExecutor(
                2,
                new ThreadFactoryBuilder()
                        .setNameFormat("scheduled-thread-%d")
                        .setDaemon(true)
                        .build(),
                (r, e) -> log.error("Task rejected from scheduler", new RejectedExecutionException())
        );
    }

    /**
     * 创建基础线程池配置
     */
    private ThreadPoolTaskExecutor createBaseExecutor(
            String threadNamePrefix,
            int coreSize,
            int maxSize,
            int queueCapacity
    ) {
        ThreadPoolTaskExecutor executor = new MonitoredThreadPoolTaskExecutor(meterRegistry, threadNamePrefix);
        executor.setCorePoolSize(coreSize);
        executor.setMaxPoolSize(maxSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds((int)CollectionConstants.ThreadPool.HTTP_KEEP_ALIVE);
        executor.setThreadNamePrefix(threadNamePrefix + "-");
        executor.setAllowCoreThreadTimeOut(true);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        return executor;
    }

    /**
     * 创建降级线程池
     */
    private ExecutorService createFallbackExecutor() {
        return new ThreadPoolExecutor(
                Runtime.getRuntime().availableProcessors(),
                Runtime.getRuntime().availableProcessors() * 2,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000),
                new ThreadFactoryBuilder()
                        .setNameFormat("fallback-thread-%d")
                        .build(),
                new RetryRejectedExecutionHandler()
        );
    }
}

/**
 * 可监控的线程池
 */
@Slf4j
class MonitoredThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {
    private final MeterRegistry meterRegistry;
    private final String poolName;

    public MonitoredThreadPoolTaskExecutor(MeterRegistry meterRegistry, String poolName) {
        this.meterRegistry = meterRegistry;
        this.poolName = poolName;
    }

    @Override
    public void initialize() {
        super.initialize();
        // 注册监控指标
        registerMetrics();
    }

    private void registerMetrics() {
        meterRegistry.gauge(poolName + ".pool.size", this, ThreadPoolTaskExecutor::getPoolSize);
        meterRegistry.gauge(poolName + ".active.count", this, ThreadPoolTaskExecutor::getActiveCount);
        meterRegistry.gauge(poolName + ".queue.size", this, executor ->
//                ((ThreadPoolExecutor) executor).getQueue().size());
                this.getThreadPoolExecutor().getQueue().size());
    }
}

/**
 * 任务监控装饰器
 */
@Slf4j
class MonitoringTaskDecorator implements TaskDecorator {
    private final String poolName;
    private final Map<String, AtomicInteger> taskCounters = new ConcurrentHashMap<>();

    public MonitoringTaskDecorator(String poolName) {
        this.poolName = poolName;
    }

    @Override
    public Runnable decorate(Runnable runnable) {
        String taskName = runnable.getClass().getSimpleName();
        return () -> {
            long startTime = System.currentTimeMillis();
            try {
                incrementTaskCount(taskName);
                runnable.run();
            } finally {
                decrementTaskCount(taskName);
                recordTaskDuration(taskName, System.currentTimeMillis() - startTime);
            }
        };
    }

    private void incrementTaskCount(String taskName) {
        taskCounters.computeIfAbsent(taskName, k -> new AtomicInteger(0))
                .incrementAndGet();
    }

    private void decrementTaskCount(String taskName) {
        taskCounters.get(taskName).decrementAndGet();
    }

    private void recordTaskDuration(String taskName, long duration) {
        log.debug("[{}] Task {} completed in {}ms", poolName, taskName, duration);
    }
}

/**
 * 重试拒绝策略
 */
@Slf4j
class RetryRejectedExecutionHandler implements RejectedExecutionHandler {
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY = 100; // ms

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        int retries = 0;
        while (retries < MAX_RETRIES) {
            try {
                if (!executor.isShutdown()) {
                    Thread.sleep(RETRY_DELAY * (long)Math.pow(2, retries));
                    executor.getQueue().put(r);
                    return;
                }
                break;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                retries++;
                if (retries == MAX_RETRIES) {
                    log.error("Task rejected after {} retries", MAX_RETRIES, e);
                    throw new RejectedExecutionException("Task rejected after " + MAX_RETRIES + " retries", e);
                }
            }
        }
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

## CollectExecutor.java

```java
package com.study.collect.business.testcase.core.executor;

import com.study.collect.business.testcase.common.constants.CollectionConstants;

import com.study.collect.business.testcase.common.utils.RateLimiter;
import com.study.collect.business.testcase.common.utils.StreamProcessor;
import com.study.collect.business.testcase.entity.UriEntity;
import com.study.collect.business.testcase.model.param.CollectParam;
import com.study.collect.business.testcase.model.param.PageParam;
import com.study.collect.business.testcase.model.response.PageResponse;
import com.study.collect.business.testcase.model.response.VersionResponse;
import com.study.collect.business.testcase.repository.UriRepository;
import com.study.collect.business.testcase.service.http.UriHttpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.ObjectPool;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * URI采集执行器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CollectExecutor {

    private final UriHttpService httpService;
    private final UriRepository repository;
    private final ObjectPool<UriEntity> entityPool;
    private final RateLimiter rateLimiter;

    @Qualifier("httpExecutor")
    private final ThreadPoolTaskExecutor httpExecutor;

    @Qualifier("mongoExecutor")
    private final ThreadPoolTaskExecutor mongoExecutor;

    @Qualifier("virtualThreadExecutor")
    private final ExecutorService virtualThreadExecutor;

    /**
     * 执行采集任务
     */
    public CompletableFuture<StreamProcessor.ProcessMetrics> execute(
            CollectParam param,
            Consumer<StreamProcessor.ProcessMetrics> progressCallback
    ) {
        // 1. 构建处理器配置
        StreamProcessor.ProcessorConfig<VersionResponse, List<UriEntity>> config =
                StreamProcessor.ProcessorConfig.<VersionResponse, List<UriEntity>>builder()
                        .processorName("URI-Collect-" + param.getRootNode())
                        .batchSize(param.getBatchSize())
                        .maxConcurrent(CollectionConstants.Process.MAX_CONCURRENT_TASKS)
                        .timeoutSeconds(param.getTimeout())
                        .maxRetries(param.getMaxRetries())
                        .retryDelayMs(CollectionConstants.Http.RETRY_INTERVAL)
                        .processExecutor(httpExecutor.getThreadPoolExecutor())
                        .saveExecutor(mongoExecutor.getThreadPoolExecutor())
                        .continueOnError(true)
                        // 获取版本数据
                        .dataFetcher(offset -> fetchVersions(param, offset))
                        // 处理每个版本的URI
                        .dataConverter(version -> processVersion(param, version))
                        // 保存处理结果
                        .dataSaver(entities -> saveEntities(param.getRootNode(), entities))
                        .progressCallback(progressCallback)
                        .build();

        // 2. 创建并启动处理器
        StreamProcessor<VersionResponse, List<UriEntity>> processor = new StreamProcessor<>(config);
        return processor.process(0, calculateTotalVersions(param));
    }

    /**
     * 获取版本列表
     */
    private List<VersionResponse> fetchVersions(CollectParam param, int offset) {
        try {
            rateLimiter.acquire();
            PageResponse<VersionResponse> response = httpService.getVersionsAsync(
                    param,
                    new PageParam(offset + 1, param.getBatchSize())
            ).get();
            return response.getItems();
        } catch (Exception e) {
            log.error("Error fetching versions for offset: {}", offset, e);
            throw new RuntimeException("Failed to fetch versions", e);
        }
    }

    /**
     * 处理单个版本的URI
     */
    private List<UriEntity> processVersion(CollectParam param, VersionResponse version) {
        try {
            // 一次性获取该版本所有URI
            List<String> allUris = httpService.getAllUrisForVersion(
                    param,
                    version.getVersion()
            );

            // 按批次处理URI详情
            return processUriDetails(param, version.getVersion(), allUris);
        } catch (Exception e) {
            log.error("Error processing version: {}", version.getVersion(), e);
            throw new RuntimeException("Failed to process version", e);
        }
    }

    /**
     * 处理URI详情
     */
    private List<UriEntity> processUriDetails(
            CollectParam param,
            String version,
            List<String> uris
    ) {
        List<UriEntity> results = new ArrayList<>();
        List<List<String>> batches = partition(uris, 200); // 每批200条处理

        for (List<String> batch : batches) {
            try {
                rateLimiter.acquire();
                List<Map<String, Object>> details = httpService.getUriDetailsAsync(
                        param,
                        batch
                ).get();

                // 转换为实体
                List<UriEntity> entities = convertToEntities(
                        param.getRootNode(),
                        version,
                        details
                );

                results.addAll(entities);
            } catch (Exception e) {
                log.error("Error processing URI batch", e);
                if (!param.getAllowDuplicate()) {
                    throw new RuntimeException("Failed to process URI batch", e);
                }
            }
        }

        return results;
    }

    /**
     * 转换为实体对象
     */
    private List<UriEntity> convertToEntities(
            String rootNode,
            String version,
            List<Map<String, Object>> details
    ) {
        List<UriEntity> entities = new ArrayList<>();
        for (Map<String, Object> detail : details) {
            UriEntity entity = null;
            try {
                entity = entityPool.borrowObject();
                fillEntity(entity, rootNode, version, detail);
                entities.add(entity);
            } catch (Exception e) {
                log.error("Error converting to entity", e);
                if (entity != null) {
                    try {
                        entityPool.returnObject(entity);
                    } catch (Exception ex) {
                        log.error("Error returning entity to pool", ex);
                    }
                }
            }
        }
        return entities;
    }

    /**
     * 填充实体信息
     */
    private void fillEntity(
            UriEntity entity,
            String rootNode,
            String version,
            Map<String, Object> detail
    ) {
        entity.setUri((String) detail.get("uri"));
        entity.setRootNode(rootNode);
        entity.setVersionType(getVersionType(version));
        entity.setUriVersion(version);
        entity.setDetails(detail);
    }

    /**
     * 批量保存实体
     */
    private void saveEntities(String rootNode, List<List<UriEntity>> batchEntities) {
        for (List<UriEntity> batch : batchEntities) {
            try {
                repository.batchUpsert(rootNode, batch);
            } finally {
                // 返还对象到对象池
                for (UriEntity entity : batch) {
                    try {
                        entityPool.returnObject(entity);
                    } catch (Exception e) {
                        log.error("Error returning entity to pool", e);
                    }
                }
            }
        }
    }

    /**
     * 获取版本类型
     */
    private String getVersionType(String version) {
        return version.toLowerCase().contains("branch") ? "BRANCH" : "TRUNK";
    }

    /**
     * 计算总版本数
     */
    private int calculateTotalVersions(CollectParam param) {
        try {
            PageResponse<VersionResponse> response = httpService.getVersionsAsync(
                    param,
                    new PageParam(1, 1)
            ).get();
            return response.getTotal().intValue();
        } catch (Exception e) {
            log.error("Error calculating total versions", e);
            return 0;
        }
    }

    /**
     * 分割列表
     */
    private <T> List<List<T>> partition(List<T> list, int size) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        List<List<T>> result = new ArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            result.add(list.subList(i, Math.min(i + size, list.size())));
        }
        return result;
    }
}
```

## DeleteExecutor.java

```java
package com.study.collect.business.testcase.core.executor;

import com.study.collect.business.testcase.common.constants.CollectionConstants;
import com.study.collect.business.testcase.common.utils.StreamProcessor;
import com.study.collect.business.testcase.common.utils.TableNameHelper;

import com.study.collect.business.testcase.model.param.DeleteParam;
import com.study.collect.business.testcase.repository.UriRepository;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * URI删除执行器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteExecutor {

    private final UriRepository repository;

    @Qualifier("mongoExecutor")
    private final ThreadPoolTaskExecutor mongoExecutor;

    @Qualifier("virtualThreadExecutor")
    private final ExecutorService virtualThreadExecutor;

    /**
     * 删除配置
     */
    @Data
    @Builder
    public static class DeleteConfig {
        private String rootNode;
        private List<String> uris;
        private boolean hardDelete;
        private int batchSize;
        private int maxRetries;
        private long retryDelayMs;
        private boolean continueOnError;
        private Consumer<StreamProcessor.ProcessMetrics> progressCallback;
    }

    /**
     * 执行删除任务
     */
    public CompletableFuture<StreamProcessor.ProcessMetrics> execute(
            DeleteParam param,
            Consumer<StreamProcessor.ProcessMetrics> progressCallback
    ) {
        // 1. 解析并验证参数
        DeleteConfig config = buildConfig(param, progressCallback);

        // 2. 按rootNode分组URI
        Map<String, List<String>> groupedUris = groupUrisByRootNode(config);

        // 3. 构建处理器配置
        StreamProcessor.ProcessorConfig<Map.Entry<String, List<String>>, Long> processorConfig =
                StreamProcessor.ProcessorConfig.<Map.Entry<String, List<String>>, Long>builder()
                        .processorName("URI-Delete-" + config.getRootNode())
                        .batchSize(config.getBatchSize())
                        .maxConcurrent(CollectionConstants.Process.MAX_CONCURRENT_TASKS)
                        .timeoutSeconds(CollectionConstants.Process.TASK_TIMEOUT)
                        .maxRetries(config.getMaxRetries())
                        .retryDelayMs(config.getRetryDelayMs())
                        .continueOnError(config.isContinueOnError())
                        .processExecutor(virtualThreadExecutor)
                        .saveExecutor(mongoExecutor.getThreadPoolExecutor())
                        // 数据获取函数
                        .dataFetcher(offset -> fetchBatch(new ArrayList<>(groupedUris.entrySet()), offset))
                        // 数据处理函数
                        .dataConverter(entry -> processDelete(entry, config))
                        // 结果保存函数
                        .dataSaver(this::updateMetrics)
                        // 进度回调
                        .progressCallback(progressCallback)
                        .build();

        // 4. 创建并启动处理器
        StreamProcessor<Map.Entry<String, List<String>>, Long> processor =
                new StreamProcessor<>(processorConfig);

        return processor.process(0, groupedUris.size());
    }

    /**
     * 执行清理任务
     */
    public CompletableFuture<StreamProcessor.ProcessMetrics> executeCleanup(
            String rootNode,
            Set<String> validUriHashes,
            boolean hardDelete,
            Consumer<StreamProcessor.ProcessMetrics> progressCallback
    ) {
        StreamProcessor.ProcessorConfig<Set<String>, Long> config =
                StreamProcessor.ProcessorConfig.<Set<String>, Long>builder()
                        .processorName("URI-Cleanup-" + rootNode)
                        .batchSize(CollectionConstants.Process.DEFAULT_BATCH_SIZE)
                        .maxConcurrent(1) // 清理任务限制并发为1
                        .timeoutSeconds(CollectionConstants.Process.TASK_TIMEOUT)
                        .maxRetries(CollectionConstants.Http.MAX_RETRY)
                        .retryDelayMs(CollectionConstants.Http.RETRY_INTERVAL)
                        .processExecutor(virtualThreadExecutor)
                        .saveExecutor(mongoExecutor.getThreadPoolExecutor())
                        // 数据获取函数
                        .dataFetcher(offset -> Collections.singletonList(validUriHashes))
                        // 数据处理函数
                        .dataConverter(hashes -> processCleanup(rootNode, hashes, hardDelete))
                        // 结果保存函数
                        .dataSaver(this::updateMetrics)
                        // 进度回调
                        .progressCallback(progressCallback)
                        .build();

        // 创建并启动处理器
        StreamProcessor<Set<String>, Long> processor = new StreamProcessor<>(config);
        return processor.process(0, 1);
    }

    private DeleteConfig buildConfig(
            DeleteParam param,
            Consumer<StreamProcessor.ProcessMetrics> progressCallback
    ) {
        return DeleteConfig.builder()
                .rootNode(param.getRootNode())
                .uris(param.getUris())
                .hardDelete(param.getHardDelete())
                .batchSize(getBatchSize(param))
                .maxRetries(CollectionConstants.Http.MAX_RETRY)
                .retryDelayMs(CollectionConstants.Http.RETRY_INTERVAL)
                .continueOnError(true)
                .progressCallback(progressCallback)
                .build();
    }

    private Map<String, List<String>> groupUrisByRootNode(DeleteConfig config) {
        if (config.getRootNode() != null) {
            // 使用指定的rootNode
            return Collections.singletonMap(config.getRootNode(), config.getUris());
        } else {
            // 从URI中提取rootNode
            return config.getUris().stream()
                    .collect(Collectors.groupingBy(TableNameHelper::extractRootNode));
        }
    }

    private List<Map.Entry<String, List<String>>> fetchBatch(
            List<Map.Entry<String, List<String>>> entries,
            int offset
    ) {
        if (offset >= entries.size()) {
            return Collections.emptyList();
        }
        return Collections.singletonList(entries.get(offset));
    }

    private Long processDelete(
            Map.Entry<String, List<String>> entry,
            DeleteConfig config
    ) {
        String rootNode = entry.getKey();
        List<String> uris = entry.getValue();
        List<List<String>> batches = partition(uris, config.getBatchSize());
        long totalDeleted = 0;

        for (List<String> batch : batches) {
            try {
                long count = config.isHardDelete() ?
                        repository.batchHardDelete(rootNode, batch) :
                        repository.batchSoftDelete(rootNode, batch);
                totalDeleted += count;
            } catch (Exception e) {
                log.error("Error deleting batch for rootNode: {}", rootNode, e);
                if (!config.isContinueOnError()) {
                    throw new RuntimeException("Failed to delete batch", e);
                }
            }
        }

        return totalDeleted;
    }

    private Long processCleanup(
            String rootNode,
            Set<String> validHashes,
            boolean hardDelete
    ) {
        try {
            return hardDelete ?
                    repository.deleteNotInUriHashes(rootNode, validHashes) :
                    repository.softDeleteNotInUriHashes(rootNode, validHashes);
        } catch (Exception e) {
            log.error("Error during cleanup for rootNode: {}", rootNode, e);
            throw new RuntimeException("Cleanup failed", e);
        }
    }

    private void updateMetrics(List<Long> counts) {
        long total = counts.stream().mapToLong(Long::longValue).sum();
        log.debug("Processed batch with total count: {}", total);
    }

    private int getBatchSize(DeleteParam param) {
        if (param.getBatchSize() != null) {
            return Math.min(Math.max(param.getBatchSize(),
                            CollectionConstants.Process.MIN_BATCH_SIZE),
                    CollectionConstants.Process.MAX_BATCH_SIZE);
        }
        return CollectionConstants.Process.DEFAULT_BATCH_SIZE;
    }

    private <T> List<List<T>> partition(List<T> list, int size) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        List<List<T>> result = new ArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            result.add(list.subList(i, Math.min(i + size, list.size())));
        }
        return result;
    }
}
```

## QueueManager.java

```java
package com.study.collect.business.testcase.core.manager;

import com.study.collect.business.testcase.common.constants.CollectionConstants;
import com.study.collect.business.testcase.config.TestCaseCollectorProperties;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 队列管理器
 */
@Slf4j
@Component
public class QueueManager {

    private final Map<String, PriorityBlockingQueue<QueueItem<?>>> typeQueues;
    private final ConcurrentHashMap<String, QueueItem<?>> itemMap;
    private final ScheduledExecutorService scheduledExecutor;
    private final ThreadPoolTaskExecutor processorExecutor;
    private final MeterRegistry meterRegistry;
    private final int maxQueueSize;
    private volatile boolean running = true;
    private final AtomicInteger activeProcesses = new AtomicInteger(0);

    /**
     * 队列项状态
     */
    public enum ItemStatus {
        QUEUED,
        PROCESSING,
        COMPLETED,
        CANCELLED,
        ERROR,
        RETRY_WAIT
    }

    /**
     * 队列项信息
     */
    @Data
    @Builder
    private static class QueueItem<T> {
        private final String itemId;
        private final String type;
        private final T item;
        private volatile int priority;
        private final CompletableFuture<Void> future;
        private final Consumer<T> processor;
        private final LocalDateTime createTime;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private volatile ItemStatus status;
        private String statusMessage;
        private Double progress;
        private int retryCount;
        private LocalDateTime lastRetryTime;
        private Map<String, Object> attributes;
        private Long timeoutSeconds;
        private ScheduledFuture<?> timeoutFuture;
    }

    public QueueManager(
            ScheduledExecutorService scheduledExecutor,
            ThreadPoolTaskExecutor processorExecutor,
            MeterRegistry meterRegistry,
            TestCaseCollectorProperties properties
    ) {
        this.typeQueues = new ConcurrentHashMap<>();
        this.itemMap = new ConcurrentHashMap<>();
        this.scheduledExecutor = scheduledExecutor;
        this.processorExecutor = processorExecutor;
        this.meterRegistry = meterRegistry;
        this.maxQueueSize = properties.getTask().getQueueCapacity();

        // 启动监控和清理任务
        startMonitoring();
        startCleanupTask();
        registerMetrics();
    }

    /**
     * 入队
     */
    public <T> CompletableFuture<Void> enqueue(
            String type,
            T item,
            int priority,
            Consumer<T> processor,
            Long timeoutSeconds
    ) {
        validateQueueCapacity();
        String itemId = generateItemId();

        QueueItem<T> queueItem = QueueItem.<T>builder()
                .itemId(itemId)
                .type(type)
                .item(item)
                .priority(priority)
                .future(new CompletableFuture<>())
                .processor(processor)
                .createTime(LocalDateTime.now())
                .status(ItemStatus.QUEUED)
                .progress(0.0)
                .retryCount(0)
                .attributes(new ConcurrentHashMap<>())
                .timeoutSeconds(timeoutSeconds)
                .build();

        if (itemMap.putIfAbsent(itemId, queueItem) != null) {
            throw new IllegalStateException("Item " + itemId + " already exists");
        }

        getOrCreateQueue(type).offer(queueItem);
        scheduleTimeout(queueItem);

        // 启动处理
        processNextItem(type);

        return queueItem.getFuture();
    }

    /**
     * 取消任务
     */
    public boolean cancel(String itemId) {
        QueueItem<?> item = itemMap.get(itemId);
        if (item != null && canCancel(item.getStatus())) {
            PriorityBlockingQueue<QueueItem<?>> queue = typeQueues.get(item.getType());
            if (queue != null && queue.remove(item)) {
                item.setStatus(ItemStatus.CANCELLED);
                item.setEndTime(LocalDateTime.now());
                item.getFuture().cancel(true);
                cancelTimeout(item);
                itemMap.remove(itemId);
                return true;
            }
        }
        return false;
    }

    /**
     * 更新优先级
     */
    public boolean updatePriority(String itemId, int newPriority) {
        QueueItem<?> item = itemMap.get(itemId);
        if (item != null && item.getStatus() == ItemStatus.QUEUED) {
            PriorityBlockingQueue<QueueItem<?>> queue = typeQueues.get(item.getType());
            if (queue != null && queue.remove(item)) {
                item.setPriority(newPriority);
                queue.offer(item);
                return true;
            }
        }
        return false;
    }

    /**
     * 获取队列状态
     */
    public Map<String, Object> getQueueStatus(String itemId) {
        QueueItem<?> item = itemMap.get(itemId);
        if (item != null) {
            Map<String, Object> status = new HashMap<>();
            status.put("itemId", item.getItemId());
            status.put("type", item.getType());
            status.put("status", item.getStatus());
            status.put("statusMessage", item.getStatusMessage());
            status.put("progress", item.getProgress());
            status.put("createTime", item.getCreateTime());
            status.put("startTime", item.getStartTime());
            status.put("endTime", item.getEndTime());
            status.put("priority", item.getPriority());
            status.put("retryCount", item.getRetryCount());
            status.put("attributes", new HashMap<>(item.getAttributes()));
            return status;
        }
        return null;
    }

    private void processNextItem(String type) {
        PriorityBlockingQueue<QueueItem<?>> queue = typeQueues.get(type);
        if (queue == null || queue.isEmpty()) {
            return;
        }

        processorExecutor.execute(() -> {
            while (running && activeProcesses.get() < processorExecutor.getMaxPoolSize()) {
                QueueItem<?> item = queue.poll();
                if (item == null) break;

                if (item.getStatus() == ItemStatus.QUEUED) {
                    processItem(item);
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    private <T> void processItem(QueueItem<T> item) {
        if (!running) return;

        try {
            activeProcesses.incrementAndGet();
            item.setStatus(ItemStatus.PROCESSING);
            item.setStartTime(LocalDateTime.now());

            CompletableFuture.runAsync(() -> {
                try {
                    item.getProcessor().accept(item.getItem());
                    completeItem(item, true, null);
                } catch (Exception e) {
                    handleProcessingError(item, e);
                }
            }, processorExecutor).exceptionally(throwable -> {
                handleProcessingError(item, throwable);
                return null;
            });

        } finally {
            activeProcesses.decrementAndGet();
        }
    }

    private <T> void completeItem(QueueItem<T> item, boolean success, Throwable error) {
        if (success) {
            item.setStatus(ItemStatus.COMPLETED);
            item.getFuture().complete(null);
        } else {
            item.setStatus(ItemStatus.ERROR);
            item.getFuture().completeExceptionally(error);
        }

        item.setEndTime(LocalDateTime.now());
        cancelTimeout(item);

        // 处理下一个任务
        processNextItem(item.getType());
    }

    private <T> void handleProcessingError(QueueItem<T> item, Throwable error) {
        log.error("Error processing item: {}", item.getItemId(), error);

        if (canRetry(item)) {
            scheduleRetry(item);
        } else {
            completeItem(item, false, error);
        }
    }

    private <T> void scheduleRetry(QueueItem<T> item) {
        item.setStatus(ItemStatus.RETRY_WAIT);
        item.setRetryCount(item.getRetryCount() + 1);
        item.setLastRetryTime(LocalDateTime.now());

        long delay = calculateRetryDelay(item.getRetryCount());
        scheduledExecutor.schedule(() -> {
            if (item.getStatus() == ItemStatus.RETRY_WAIT) {
                item.setStatus(ItemStatus.QUEUED);
                getOrCreateQueue(item.getType()).offer(item);
                processNextItem(item.getType());
            }
        }, delay, TimeUnit.MILLISECONDS);
    }

    private <T> void scheduleTimeout(QueueItem<T> item) {
        if (item.getTimeoutSeconds() != null && item.getTimeoutSeconds() > 0) {
            item.setTimeoutFuture(scheduledExecutor.schedule(() -> {
                if (!isTerminalStatus(item.getStatus())) {
                    completeItem(item, false,
                            new TimeoutException("Item processing timed out after " +
                                    item.getTimeoutSeconds() + " seconds"));
                }
            }, item.getTimeoutSeconds(), TimeUnit.SECONDS));
        }
    }

    private <T> void cancelTimeout(QueueItem<T> item) {
        if (item.getTimeoutFuture() != null) {
            item.getTimeoutFuture().cancel(false);
        }
    }

    private PriorityBlockingQueue<QueueItem<?>> getOrCreateQueue(String type) {
        return typeQueues.computeIfAbsent(type, k -> new PriorityBlockingQueue<>(
                maxQueueSize,
                Comparator.<QueueItem<?>>comparingInt(i -> i.priority).reversed()
                        .thenComparing(i -> i.createTime)
        ));
    }

    private void startMonitoring() {
        scheduledExecutor.scheduleAtFixedRate(this::monitorQueues,
                1, 1, TimeUnit.MINUTES);
    }

    private void startCleanupTask() {
        scheduledExecutor.scheduleAtFixedRate(this::cleanup,
                1, 1, TimeUnit.HOURS);
    }

    private void registerMetrics() {
        meterRegistry.gauge("queue.total_items", itemMap, Map::size);
        meterRegistry.gauge("queue.active_processes", activeProcesses);
        typeQueues.forEach((type, queue) ->
                meterRegistry.gauge("queue.size." + type, queue, Queue::size));
    }

    private void monitorQueues() {
        if (!running) return;

        try {
            Map<String, Map<ItemStatus, Long>> statusCounts = new HashMap<>();
            Map<String, List<String>> stuckItems = new HashMap<>();

            LocalDateTime threshold = LocalDateTime.now().minusMinutes(30);

            itemMap.values().forEach(item -> {
                // 统计状态
                statusCounts.computeIfAbsent(item.getType(), k -> new HashMap<>())
                        .merge(item.getStatus(), 1L, Long::sum);

                // 检查卡住的项
                if (item.getStatus() == ItemStatus.PROCESSING &&
                        item.getStartTime().isBefore(threshold)) {
                    stuckItems.computeIfAbsent(item.getType(), k -> new ArrayList<>())
                            .add(item.getItemId());
                }
            });

            log.info("Queue status: {}", statusCounts);
            if (!stuckItems.isEmpty()) {
                log.warn("Stuck items detected: {}", stuckItems);
            }

        } catch (Exception e) {
            log.error("Error monitoring queues", e);
        }
    }

    private void cleanup() {
        if (!running) return;

        try {
            LocalDateTime cutoff = LocalDateTime.now().minusHours(24);
            itemMap.entrySet().removeIf(entry -> {
                QueueItem<?> item = entry.getValue();
                return isTerminalStatus(item.getStatus()) &&
                        item.getEndTime() != null &&
                        item.getEndTime().isBefore(cutoff);
            });
        } catch (Exception e) {
            log.error("Error during cleanup", e);
        }
    }

    private String generateItemId() {
        return UUID.randomUUID().toString();
    }

    private void validateQueueCapacity() {
        if (itemMap.size() >= maxQueueSize) {
            throw new IllegalStateException("Queue capacity exceeded");
        }
    }

    private boolean canCancel(ItemStatus status) {
        return status == ItemStatus.QUEUED || status == ItemStatus.RETRY_WAIT;
    }

    private boolean canRetry(QueueItem<?> item) {
        return item.getRetryCount() < CollectionConstants.Http.MAX_RETRY;
    }

    private boolean isTerminalStatus(ItemStatus status) {
        return status == ItemStatus.COMPLETED ||
                status == ItemStatus.CANCELLED ||
                status == ItemStatus.ERROR;
    }

    private long calculateRetryDelay(int retryCount) {
        return CollectionConstants.Http.RETRY_INTERVAL * (long)Math.pow(2, retryCount - 1);
    }

    @PreDestroy
    public void shutdown() {
        running = false;
        processorExecutor.shutdown();
        clearQueues();
    }

    private void clearQueues() {
        itemMap.values().forEach(item -> {
            if (!isTerminalStatus(item.getStatus())) {
                item.setStatus(ItemStatus.CANCELLED);
                item.setEndTime(LocalDateTime.now());
                item.getFuture().cancel(true);
                cancelTimeout(item);
            }
        });

        typeQueues.clear();
        itemMap.clear();
    }

    /**
     * 获取队列统计信息
     */
    public Map<String, Object> getQueueStats() {
        Map<String, Object> stats = new HashMap<>();

        // 队列大小统计
        Map<String, Integer> queueSizes = new HashMap<>();
        typeQueues.forEach((type, queue) ->
                queueSizes.put(type, queue.size()));

        // 状态统计
        Map<ItemStatus, Long> statusCounts = itemMap.values().stream()
                .collect(Collectors.groupingBy(
                        QueueItem::getStatus,
                        Collectors.counting()
                ));

        stats.put("queueSizes", queueSizes);
        stats.put("statusCounts", statusCounts);
        stats.put("totalItems", itemMap.size());
        stats.put("activeProcesses", activeProcesses.get());

        return stats;
    }
}
```

## TaskManager.java

```java
package com.study.collect.business.testcase.core.manager;

import com.study.collect.business.testcase.common.constants.CollectionConstants;
import com.study.collect.business.testcase.model.response.TaskResponse;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 任务管理器
 */
@Slf4j
@Component
public class TaskManager {

    private final ConcurrentHashMap<String, TaskInfo> taskMap;
    private final ConcurrentHashMap<String, ScheduledFuture<?>> timeoutTasks;
    private final ScheduledExecutorService scheduledExecutor;
    private final Map<String, PriorityBlockingQueue<TaskInfo>> taskTypeQueues;
    private volatile boolean running = true;

    /**
     * 任务信息类
     */
    @Data
    private static class TaskInfo {
        private final String taskId;
        private final String type;
        private final Map<String, Object> params;
        private final int priority;
        private final LocalDateTime createTime;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private String status;
        private String message;
        private Double progress;
        private Long totalCount;
        private Long processedCount;
        private Long failedCount;
        private Map<String, Object> details;
        private CompletableFuture<Void> future;
        private Consumer<TaskInfo> progressCallback;
        private int retryCount;
        private LocalDateTime lastRetryTime;

        public TaskInfo(String taskId, String type, Map<String, Object> params, int priority) {
            this.taskId = taskId;
            this.type = type;
            this.params = params;
            this.priority = priority;
            this.createTime = LocalDateTime.now();
            this.status = "CREATED";
            this.progress = 0.0;
            this.details = new ConcurrentHashMap<>();
            this.retryCount = 0;
        }

        public void setPriority(int priority) {
            this.priority = priority;
        }
    }

    public TaskManager(ScheduledExecutorService scheduledExecutor) {
        this.taskMap = new ConcurrentHashMap<>();
        this.timeoutTasks = new ConcurrentHashMap<>();
        this.scheduledExecutor = scheduledExecutor;
        this.taskTypeQueues = new ConcurrentHashMap<>();

        // 启动定期清理和监控
        startPeriodicCleanup();
        startTaskMonitor();
    }

    /**
     * 创建新任务
     */
    public TaskResponse createTask(String type, Map<String, Object> params, Integer priority) {
        String taskId = generateTaskId();
        TaskInfo taskInfo = new TaskInfo(taskId, type, params, priority != null ? priority : 0);

        taskMap.put(taskId, taskInfo);
        taskTypeQueues.computeIfAbsent(type, k -> new PriorityBlockingQueue<>(
                100,
                Comparator.<TaskInfo>comparingInt(t -> t.priority).reversed()
                        .thenComparing(t -> t.createTime)
        )).offer(taskInfo);

        scheduleTimeout(taskId);

        return convertToResponse(taskInfo);
    }

    /**
     * 开始执行任务
     */
    public void startTask(String taskId, Consumer<TaskInfo> progressCallback) {
        TaskInfo task = taskMap.get(taskId);
        if (task != null && "CREATED".equals(task.getStatus())) {
            task.setProgressCallback(progressCallback);
            task.setStartTime(LocalDateTime.now());
            task.setStatus("PROCESSING");
            notifyProgress(task);
        }
    }

    /**
     * 更新任务状态
     */
    public void updateTaskStatus(String taskId, String status, String message) {
        TaskInfo task = taskMap.get(taskId);
        if (task != null) {
            task.setStatus(status);
            task.setMessage(message);

            if (isTerminalStatus(status)) {
                task.setEndTime(LocalDateTime.now());
                cancelTimeout(taskId);
            }

            notifyProgress(task);
        }
    }

    /**
     * 更新任务进度
     */
    public void updateTaskProgress(String taskId, long processed, long total) {
        TaskInfo task = taskMap.get(taskId);
        if (task != null) {
            task.setProcessedCount(processed);
            task.setTotalCount(total);
            task.setProgress(total > 0 ? (processed * 100.0) / total : 0.0);
            notifyProgress(task);
        }
    }

    /**
     * 添加任务详情
     */
    public void addTaskDetails(String taskId, Map<String, Object> details) {
        TaskInfo task = taskMap.get(taskId);
        if (task != null && task.getDetails() != null) {
            task.getDetails().putAll(details);
        }
    }

    /**
     * 获取任务状态
     */
    public TaskResponse getTaskStatus(String taskId) {
        TaskInfo task = taskMap.get(taskId);
        return task != null ? convertToResponse(task) : null;
    }

    /**
     * 获取所有活动任务
     */
    public List<TaskResponse> getActiveTasks() {
        return taskMap.values().stream()
                .filter(task -> !isTerminalStatus(task.getStatus()))
                .sorted(Comparator.comparing(TaskInfo::getPriority).reversed())
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 取消任务
     */
    public boolean cancelTask(String taskId) {
        TaskInfo task = taskMap.get(taskId);
        if (task != null && !"COMPLETED".equals(task.getStatus())) {
            task.setStatus("CANCELLED");
            task.setEndTime(LocalDateTime.now());
            cancelTimeout(taskId);

            if (task.getFuture() != null) {
                task.getFuture().cancel(true);
            }

            return true;
        }
        return false;
    }

    /**
     * 更新任务优先级
     */
    public boolean updateTaskPriority(String taskId, int priority) {
        TaskInfo task = taskMap.get(taskId);
        if (task != null && !isTerminalStatus(task.getStatus())) {
            // 从原队列中移除并重新入队
            PriorityBlockingQueue<TaskInfo> queue = taskTypeQueues.get(task.getType());
            if (queue != null && queue.remove(task)) {
                task.setPriority(priority);
                queue.offer(task);
                return true;
            }
        }
        return false;
    }

    /**
     * 重试任务
     */
    public boolean retryTask(String taskId) {
        TaskInfo task = taskMap.get(taskId);
        if (task != null && ("ERROR".equals(task.getStatus()) || "TIMEOUT".equals(task.getStatus()))) {
            if (task.getRetryCount() < CollectionConstants.Http.MAX_RETRY) {
                task.setRetryCount(task.getRetryCount() + 1);
                task.setStatus("CREATED");
                task.setLastRetryTime(LocalDateTime.now());
                task.setMessage("Retry attempt " + task.getRetryCount());

                // 重新入队
                taskTypeQueues.get(task.getType()).offer(task);
                return true;
            }
        }
        return false;
    }

    private void notifyProgress(TaskInfo task) {
        if (task.getProgressCallback() != null) {
            task.getProgressCallback().accept(task);
        }
    }

    private String generateTaskId() {
        return UUID.randomUUID().toString();
    }

    private boolean isTerminalStatus(String status) {
        return "COMPLETED".equals(status) || "ERROR".equals(status)
                || "CANCELLED".equals(status) || "TIMEOUT".equals(status);
    }

    private void scheduleTimeout(String taskId) {
        ScheduledFuture<?> timeoutTask = scheduledExecutor.schedule(() -> {
            TaskInfo task = taskMap.get(taskId);
            if (task != null && !isTerminalStatus(task.getStatus())) {
                task.setStatus("TIMEOUT");
                task.setEndTime(LocalDateTime.now());
                task.setMessage("Task timeout after " + CollectionConstants.Process.TASK_TIMEOUT + " seconds");
                notifyProgress(task);
            }
        }, CollectionConstants.Process.TASK_TIMEOUT, TimeUnit.SECONDS);

        timeoutTasks.put(taskId, timeoutTask);
    }

    private void cancelTimeout(String taskId) {
        ScheduledFuture<?> timeoutTask = timeoutTasks.remove(taskId);
        if (timeoutTask != null) {
            timeoutTask.cancel(false);
        }
    }

    private void startPeriodicCleanup() {
        scheduledExecutor.scheduleAtFixedRate(
                this::cleanupTasks,
                1, 1, TimeUnit.HOURS
        );
    }

    private void startTaskMonitor() {
        scheduledExecutor.scheduleAtFixedRate(
                this::monitorTasks,
                1, 1, TimeUnit.MINUTES
        );
    }

    private void cleanupTasks() {
        if (!running) return;

        try {
            LocalDateTime cutoff = LocalDateTime.now().minusDays(7);
            taskMap.entrySet().removeIf(entry -> {
                TaskInfo task = entry.getValue();
                return task.getEndTime() != null && task.getEndTime().isBefore(cutoff);
            });
        } catch (Exception e) {
            log.error("Error during task cleanup", e);
        }
    }

    private void monitorTasks() {
        if (!running) return;

        try {
            Map<String, Long> statusCounts = new HashMap<>();
            Map<String, List<String>> stuckTasks = new HashMap<>();

            LocalDateTime threshold = LocalDateTime.now().minusMinutes(30);

            for (TaskInfo task : taskMap.values()) {
                // 统计状态
                statusCounts.merge(task.getStatus(), 1L, Long::sum);

                // 检查卡住的任务
                if ("PROCESSING".equals(task.getStatus()) &&
                        task.getStartTime().isBefore(threshold)) {
                    stuckTasks.computeIfAbsent(task.getType(), k -> new ArrayList<>())
                            .add(task.getTaskId());
                }
            }

            // 记录监控信息
            log.info("Task status statistics: {}", statusCounts);
            if (!stuckTasks.isEmpty()) {
                log.warn("Stuck tasks detected: {}", stuckTasks);
            }

        } catch (Exception e) {
            log.error("Error during task monitoring", e);
        }
    }

    private TaskResponse convertToResponse(TaskInfo task) {
        return TaskResponse.builder()
                .taskId(task.getTaskId())
                .type(task.getType())
                .status(task.getStatus())
                .message(task.getMessage())
                .progress(task.getProgress())
                .priority(task.getPriority())
                .createTime(task.getCreateTime())
                .startTime(task.getStartTime())
                .endTime(task.getEndTime())
                .totalCount(task.getTotalCount())
                .processedCount(task.getProcessedCount())
                .failedCount(task.getFailedCount())
                .details(task.getDetails())
                .params(task.getParams())
                .build();
    }

    @PreDestroy
    public void shutdown() {
        running = false;
        // 取消所有超时任务
        timeoutTasks.values().forEach(task -> task.cancel(true));
        timeoutTasks.clear();

        // 标记所有未完成任务为已取消
        taskMap.values().stream()
                .filter(task -> !isTerminalStatus(task.getStatus()))
                .forEach(task -> {
                    task.setStatus("CANCELLED");
                    task.setEndTime(LocalDateTime.now());
                    task.setMessage("Task cancelled due to system shutdown");
                    notifyProgress(task);
                });
    }

    /**
     * 获取任务统计信息
     */
    public Map<String, Object> getTaskStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // 统计各状态任务数量
        Map<String, Long> statusCounts = new HashMap<>();
        taskMap.values().forEach(task ->
                statusCounts.merge(task.getStatus(), 1L, Long::sum));

        // 统计各类型任务数量
        Map<String, Long> typeCounts = new HashMap<>();
        taskMap.values().forEach(task ->
                typeCounts.merge(task.getType(), 1L, Long::sum));

        stats.put("statusCounts", statusCounts);
        stats.put("typeCounts", typeCounts);
        stats.put("totalTasks", taskMap.size());
        stats.put("activeTasks", getActiveTasks().size());

        return stats;
    }
}
```

## CollectProcessor.java

```java
package com.study.collect.business.testcase.core.processor;

import com.google.common.collect.Lists;
import com.study.collect.business.testcase.common.utils.RateLimiter;
import com.study.collect.business.testcase.common.utils.StreamProcessor;
import com.study.collect.business.testcase.entity.UriEntity;
import com.study.collect.business.testcase.model.param.CollectParam;
import com.study.collect.business.testcase.model.param.PageParam;
import com.study.collect.business.testcase.model.response.PageResponse;
import com.study.collect.business.testcase.model.response.VersionResponse;
import com.study.collect.business.testcase.repository.UriRepository;
import com.study.collect.business.testcase.service.http.UriHttpService;

import com.study.collect.business.testcase.service.impl.UriCleanupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.ObjectPool;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class CollectProcessor implements DataProcessor<CollectParam, Long> {

    private final UriHttpService httpService;
    private final UriRepository repository;
    private final ObjectPool<UriEntity> entityPool;
    private final RateLimiter rateLimiter;
    private final UriCleanupService uriCleanupService;

    private static final int PAGE_SIZE = 200;
    private static final int BATCH_SIZE = 200;
    private static final int MAX_RETRY = 3;
    private static final long RETRY_INTERVAL = 1000L;

    private final Map<String, ProcessorStatus> taskStatusMap = new ConcurrentHashMap<>();

    @Override
    public CompletableFuture<Long> process(CollectParam param) {
        ProcessorStatus status = new ProcessorStatus();
        taskStatusMap.put(param.getTaskId(), status);

        return CompletableFuture.supplyAsync(() -> {
            try {
                // 1. 获取所有版本
                List<String> allVersions = getAllVersions(param);
                status.update("Got versions", 0.1);

                // 2. 按版本类型分组
                Map<String, List<String>> versionGroups = allVersions.stream()
                        .collect(Collectors.groupingBy(this::getVersionType));

                // 3. 如果是增量同步，先进行数据清理
                if (param.getIncremental()) {
                    uriCleanupService.cleanup(param.getRootNode(), allVersions);
                }

                // 4. 处理各个版本组
                AtomicLong totalProcessed = new AtomicLong(0);

                // 优先处理主干版本
                if (versionGroups.containsKey("TRUNK")) {
                    processVersionGroup(param, versionGroups.get("TRUNK"), totalProcessed);
                }
                // 然后处理分支版本
                if (versionGroups.containsKey("BRANCH")) {
                    processVersionGroup(param, versionGroups.get("BRANCH"), totalProcessed);
                }

                status.update("Completed", 1.0);
                return totalProcessed.get();

            } catch (Exception e) {
                String errorMessage = e.getMessage() != null ? e.getMessage() : "Unknown error";
                status.error(errorMessage);
                throw new RuntimeException("Processing failed: " + errorMessage, e);
            }
        });
    }

    private List<String> getAllVersions(CollectParam param) throws IOException {
        List<String> allVersions = new ArrayList<>();

        // 获取第一页和总数
        PageResponse<VersionResponse> firstPage = retryWithBackoff(() ->
                httpService.getVersionsAsync(param, new PageParam(1, PAGE_SIZE)).join());

        processVersionPage(firstPage, allVersions);

        // 计算总页数并处理剩余页
        long totalPages = (firstPage.getTotal() + PAGE_SIZE - 1) / PAGE_SIZE;
        for (int page = 2; page <= totalPages; page++) {
            final int currentPage = page;
            PageResponse<VersionResponse> pageResponse = retryWithBackoff(() ->
                    httpService.getVersionsAsync(param, new PageParam(currentPage, PAGE_SIZE)).join());
            processVersionPage(pageResponse, allVersions);
        }

        return allVersions;
    }

    private void processVersionGroup(CollectParam param, List<String> versions, AtomicLong totalProcessed) {
        // 使用信号量控制并发数
        Semaphore semaphore = new Semaphore(5);
        CountDownLatch versionLatch = new CountDownLatch(versions.size());

        for (String version : versions) {
            try {
                semaphore.acquire();
                CompletableFuture.runAsync(() -> {
                    try {
                        processVersion(param, version, totalProcessed);
                    } finally {
                        semaphore.release();
                        versionLatch.countDown();
                    }
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Thread interrupted while processing version {}", version, e);
            }
        }

        try {
            versionLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted while waiting for versions to complete", e);
        }
    }

    private void processVersion(CollectParam param, String version, AtomicLong totalProcessed) {
        try {
            List<String> allUris = getAllUrisForVersion(param, version);
            List<List<String>> batches = Lists.partition(allUris, BATCH_SIZE);

            // 处理每个批次
            for (List<String> batch : batches) {
                processBatch(param.getRootNode(), version, batch, totalProcessed);
            }

        } catch (Exception e) {
            log.error("Error processing version {}", version, e);
        }
    }

    private List<String> getAllUrisForVersion(CollectParam param, String version) throws IOException {
        // 一次性获取该版本所有URI
        return retryWithBackoff(() -> httpService.getAllUrisForVersion(param, version));
    }

    private void processBatch(String rootNode, String version, List<String> uriBatch, AtomicLong totalProcessed) {
        List<UriEntity> entities = new ArrayList<>();
        List<UriEntity> borrowedEntities = new ArrayList<>();

        try {
            // 限流控制
            rateLimiter.acquire();

            // 获取URI详情
            List<Map<String, Object>> details = httpService.getUriDetailsAsync(
                    CollectParam.builder().rootNode(rootNode).build(),
                    uriBatch
            ).join();

            // 使用对象池获取实体对象
            for (Map<String, Object> detail : details) {
                UriEntity entity = null;
                try {
                    entity = entityPool.borrowObject();
                    borrowedEntities.add(entity);
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
            repository.batchUpsert(rootNode, entities);
            totalProcessed.addAndGet(entities.size());

        } catch (Exception e) {
            log.error("Error processing URI batch", e);
            throw new RuntimeException("Batch processing failed", e);
        } finally {
            // 归还对象池中的对象
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

    private String getVersionType(String version) {
        return version.toLowerCase().contains("branch") ? "BRANCH" : "TRUNK";
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

    @Override
    public boolean cancel(String taskId) {
        ProcessorStatus status = taskStatusMap.get(taskId);
        if (status != null && !status.isCompleted()) {
            status.cancel();
            return true;
        }
        return false;
    }

    @Override
    public boolean updatePriority(String taskId, int priority) {
        // 采集处理器不支持优先级调整
        return false;
    }

    @Override
    public StreamProcessor.ProcessMetrics getProgress(String taskId) {
        ProcessorStatus status = taskStatusMap.get(taskId);
        return status != null ? status.toMetrics() : null;
    }

    @Override
    public void pause(String taskId) {
        ProcessorStatus status = taskStatusMap.get(taskId);
        if (status != null) {
            status.pause();
        }
    }

    @Override
    public void resume(String taskId) {
        ProcessorStatus status = taskStatusMap.get(taskId);
        if (status != null) {
            status.resume();
        }
    }

    /**
     * 处理器状态类
     */
    private static class ProcessorStatus {
        private String stage;
        private double progress;
        private String error;
        private boolean completed;
        private boolean cancelled;
        private boolean paused;
        private final long startTime;
        private Long endTime;

        ProcessorStatus() {
            this.startTime = System.currentTimeMillis();
            this.progress = 0;
            this.stage = "Initializing";
        }

        void update(String stage, double progress) {
            this.stage = stage;
            this.progress = progress;
            if (progress >= 1.0) {
                this.completed = true;
                this.endTime = System.currentTimeMillis();
            }
        }

        void error(String message) {
            this.error = message;
            this.completed = true;
            this.endTime = System.currentTimeMillis();
        }

        void cancel() {
            this.cancelled = true;
            this.completed = true;
            this.endTime = System.currentTimeMillis();
        }

        void pause() {
            this.paused = true;
        }

        void resume() {
            this.paused = false;
        }

        boolean isCompleted() {
            return completed;
        }

        StreamProcessor.ProcessMetrics toMetrics() {
            return StreamProcessor.ProcessMetrics.builder()
                    .processorName("URI-Collect")
                    .startTime(startTime)
                    .endTime(endTime)
                    .progressPercentage(progress * 100)
                    .customMetrics(Map.of(
                            "stage", stage,
                            "error", error,
                            "cancelled", cancelled,
                            "paused", paused
                    ))
                    .build();
        }
    }
}
```

## DataProcessor.java

```java
package com.study.collect.business.testcase.core.processor;

import com.study.collect.business.testcase.common.utils.StreamProcessor.ProcessMetrics;
import java.util.concurrent.CompletableFuture;

/**
 * 数据处理器接口
 * @param <T> 处理参数类型
 * @param <R> 结果类型
 */
public interface DataProcessor<T, R> {

    /**
     * 异步处理数据
     * @param param 处理参数
     * @return 异步处理结果
     */
    CompletableFuture<R> process(T param);

    /**
     * 取消处理
     * @param taskId 任务ID
     * @return 是否成功取消
     */
    boolean cancel(String taskId);

    /**
     * 更新优先级
     * @param taskId 任务ID
     * @param priority 新优先级
     * @return 是否成功更新
     */
    boolean updatePriority(String taskId, int priority);

    /**
     * 获取处理进度
     * @param taskId 任务ID
     * @return 处理进度指标
     */
    ProcessMetrics getProgress(String taskId);

    /**
     * 暂停处理
     * @param taskId 任务ID
     */
    void pause(String taskId);

    /**
     * 恢复处理
     * @param taskId 任务ID
     */
    void resume(String taskId);
}
```

## DeleteProcessor.java

```java
package com.study.collect.business.testcase.core.processor;

import com.study.collect.business.testcase.common.utils.ListCompareUtil;
import com.study.collect.business.testcase.common.utils.StreamProcessor;
import com.study.collect.business.testcase.model.param.DeleteParam;
import com.study.collect.business.testcase.repository.UriRepository;
import com.study.collect.business.testcase.common.utils.ListCompareUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * URI删除处理器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteProcessor implements DataProcessor<DeleteParam, Long> {

    private final UriRepository repository;
    private final Map<String, ProcessorStatus> taskStatusMap = new ConcurrentHashMap<>();

    @Override
    public CompletableFuture<Long> process(DeleteParam param) {
        // 初始化处理状态
        ProcessorStatus status = new ProcessorStatus();
        taskStatusMap.put(param.getTaskId(), status);

        CompletableFuture<Long> future = new CompletableFuture<>();
        try {
            // 分批处理
            List<List<String>> batches = ListCompareUtil.partition(param.getUris(), param.getBatchSize());
            long totalDeleted = 0;
            long totalBatches = batches.size();

            for (int i = 0; i < batches.size() && !status.cancelled; i++) {
                List<String> batch = batches.get(i);

                // 检查是否暂停
                while (status.paused && !status.cancelled) {
                    Thread.sleep(100);
                }

                if (status.cancelled) {
                    break;
                }

                // 执行删除
                long batchCount = param.getHardDelete() ?
                        repository.batchHardDelete(param.getRootNode(), batch) :
                        repository.batchSoftDelete(param.getRootNode(), batch);

                totalDeleted += batchCount;

                // 更新进度
                double progress = (i + 1.0) / totalBatches;
                status.update(
                        String.format("Processed %d/%d batches", i + 1, totalBatches),
                        progress
                );
            }

            if (status.cancelled) {
                future.complete(totalDeleted);
            } else {
                status.update("Completed", 1.0);
                future.complete(totalDeleted);
            }

        } catch (Exception e) {
            status.error(e.getMessage());
            future.completeExceptionally(e);
        }

        return future;
    }

    @Override
    public boolean cancel(String taskId) {
        ProcessorStatus status = taskStatusMap.get(taskId);
        if (status != null && !status.isCompleted()) {
            status.cancel();
            return true;
        }
        return false;
    }

    @Override
    public boolean updatePriority(String taskId, int priority) {
        // 删除处理器不支持优先级调整
        return false;
    }

    @Override
    public StreamProcessor.ProcessMetrics getProgress(String taskId) {
        ProcessorStatus status = taskStatusMap.get(taskId);
        if (status != null) {
            return status.toMetrics();
        }
        return null;
    }

    @Override
    public void pause(String taskId) {
        ProcessorStatus status = taskStatusMap.get(taskId);
        if (status != null) {
            status.pause();
        }
    }

    @Override
    public void resume(String taskId) {
        ProcessorStatus status = taskStatusMap.get(taskId);
        if (status != null) {
            status.resume();
        }
    }

    /**
     * 处理器状态类
     */
    private static class ProcessorStatus {
        private String stage;
        private double progress;
        private String error;
        private boolean completed;
        private boolean cancelled;
        private boolean paused;
        private final long startTime;
        private Long endTime;

        ProcessorStatus() {
            this.startTime = System.currentTimeMillis();
            this.progress = 0;
            this.stage = "Initializing";
        }

        void update(String stage, double progress) {
            this.stage = stage;
            this.progress = progress;
            if (progress >= 1.0) {
                this.completed = true;
                this.endTime = System.currentTimeMillis();
            }
        }

        void error(String message) {
            this.error = message;
            this.completed = true;
            this.endTime = System.currentTimeMillis();
        }

        void cancel() {
            this.cancelled = true;
            this.completed = true;
            this.endTime = System.currentTimeMillis();
        }

        void pause() {
            this.paused = true;
        }

        void resume() {
            this.paused = false;
        }

        boolean isCompleted() {
            return completed;
        }

        StreamProcessor.ProcessMetrics toMetrics() {
            return StreamProcessor.ProcessMetrics.builder()
                    .processorName("URI-Delete")
                    .startTime(startTime)
                    .endTime(endTime)
                    .progressPercentage(progress * 100)
                    .customMetrics(Map.of(
                            "stage", stage,
                            "error", error,
                            "cancelled", cancelled,
                            "paused", paused
                    ))
                    .build();
        }
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

/**
 * 基础实体类
 */
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

    /**
     * 构造函数
     */
    protected BaseEntity(String id) {
        this.id = id;
        this.createTime = LocalDateTime.now();
        this.updateTime = this.createTime;
        this.version = 0L;
        this.deleted = false;
    }

    /**
     * 创建前处理
     */
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

    /**
     * 更新前处理
     */
    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 重置实体状态
     */
    public void reset() {
        this.id = null;
        this.createTime = null;
        this.updateTime = null;
        this.createBy = null;
        this.updateBy = null;
        this.version = 0L;
        this.deleted = false;
    }
}
```

## UriEntity.java

```java
package com.study.collect.business.testcase.entity;

import com.study.collect.business.testcase.common.utils.HashUtil;
import jakarta.persistence.PrePersist;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

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
        this.uri = null;
        this.uriHash = null;
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


import com.study.collect.business.testcase.common.constants.CollectionConstants;
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
                CollectionConstants.Collection.VERSION_PREFIX,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")),
                CollectionConstants.Collection.VERSION_SEPARATOR,
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

import com.study.collect.business.testcase.common.constants.CollectionConstants;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Validated
public class CollectParam {
    @NotBlank(message = "rootNode cannot be empty")
    private String rootNode;

    private String version;

    @NotBlank(message = "serverUri cannot be empty")
    private String serverUri;

    private Boolean incremental = false;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private List<String> uris;  // 添加 uris 字段

    private Boolean hardDelete = false;  // 添加 hardDelete 字段

    @Min(value = 50, message = "batchSize must be greater than 50")
    @Max(value = 1000, message = "batchSize must be less than 1000")
    private Integer batchSize = CollectionConstants.Process.DEFAULT_BATCH_SIZE;

    private Integer priority = 0;

    private Boolean allowDuplicate = false;

    private Integer maxRetries = CollectionConstants.Http.MAX_RETRY;

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

## UriRepository.java

```java
package com.study.collect.business.testcase.repository;

import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.*;
import com.study.collect.business.testcase.common.constants.CollectionConstants;
import com.study.collect.business.testcase.common.utils.TableNameHelper;
import com.study.collect.business.testcase.entity.UriEntity;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * URI仓储实现
 */
@Slf4j
@Repository
public class UriRepository {

    private final MongoTemplate mongoTemplate;
    private final MeterRegistry meterRegistry;

    public UriRepository(MongoTemplate mongoTemplate, MeterRegistry meterRegistry) {
        this.mongoTemplate = mongoTemplate;
        this.meterRegistry = meterRegistry;
    }

    /**
     * 批量插入或更新
     */
    public BulkWriteResult batchUpsert(String rootNode, List<UriEntity> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return null;
        }

        Timer.Sample timer = Timer.start(meterRegistry);
        String collectionName = TableNameHelper.getTableName(rootNode);
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
            BulkWriteResult result = collection.bulkWrite(operations, options);
//            recordMetrics("upsert", timer, entities.size(), result);
            recordMetrics("upsert", timer, entities.size(), result.getModifiedCount());
            return result;
        } catch (Exception e) {
            recordError("upsert");
            log.error("Failed to batch upsert to collection {}", collectionName, e);
            throw new RuntimeException("Batch upsert failed", e);
        }
    }

    /**
     * 批量软删除
     */
    public long batchSoftDelete(String rootNode, List<String> uris) {
        if (CollectionUtils.isEmpty(uris)) {
            return 0L;
        }

        Timer.Sample timer = Timer.start(meterRegistry);
        String collectionName = TableNameHelper.getTableName(rootNode);
        List<String> uriHashes = uris.stream()
                .map(TableNameHelper::generateUriHash)
                .collect(Collectors.toList());

        Query query = new Query(Criteria.where("uri_hash").in(uriHashes));
        Update update = new Update()
                .set("is_deleted", true)
                .set("update_time", LocalDateTime.now());

        try {
            long count = mongoTemplate.updateMulti(query, update, collectionName)
                    .getModifiedCount();
            recordMetrics("soft_delete", timer, uris.size(), count);
            return count;
        } catch (Exception e) {
            recordError("soft_delete");
            log.error("Failed to batch soft delete in collection {}", collectionName, e);
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

        Timer.Sample timer = Timer.start(meterRegistry);
        String collectionName = TableNameHelper.getTableName(rootNode);
        List<String> uriHashes = uris.stream()
                .map(TableNameHelper::generateUriHash)
                .collect(Collectors.toList());

        Query query = new Query(Criteria.where("uri_hash").in(uriHashes));

        try {
            long count = mongoTemplate.remove(query, UriEntity.class, collectionName)
                    .getDeletedCount();
            recordMetrics("hard_delete", timer, uris.size(), count);
            return count;
        } catch (Exception e) {
            recordError("hard_delete");
            log.error("Failed to batch hard delete in collection {}", collectionName, e);
            throw new RuntimeException("Batch hard delete failed", e);
        }
    }

    /**
     * 清理不在列表中的URI（软删除）
     */
    public long softDeleteNotInUriHashes(String rootNode, Set<String> validHashes) {
        Timer.Sample timer = Timer.start(meterRegistry);
        String collectionName = TableNameHelper.getTableName(rootNode);
        Query query = new Query(Criteria.where("uri_hash").nin(validHashes)
                .and("is_deleted").is(false));
        Update update = new Update()
                .set("is_deleted", true)
                .set("update_time", LocalDateTime.now());

        try {
            long count = mongoTemplate.updateMulti(query, update, collectionName)
                    .getModifiedCount();
            recordMetrics("soft_delete_cleanup", timer, validHashes.size(), count);
            return count;
        } catch (Exception e) {
            recordError("soft_delete_cleanup");
            log.error("Failed to soft delete URIs not in hash set for collection {}",
                    collectionName, e);
            throw new RuntimeException("Soft delete cleanup failed", e);
        }
    }

    /**
     * 清理不在列表中的URI（硬删除）
     */
    public long deleteNotInUriHashes(String rootNode, Set<String> validHashes) {
        Timer.Sample timer = Timer.start(meterRegistry);
        String collectionName = TableNameHelper.getTableName(rootNode);
        Query query = new Query(Criteria.where("uri_hash").nin(validHashes));

        try {
            long count = mongoTemplate.remove(query, UriEntity.class, collectionName)
                    .getDeletedCount();
            recordMetrics("hard_delete_cleanup", timer, validHashes.size(), count);
            return count;
        } catch (Exception e) {
            recordError("hard_delete_cleanup");
            log.error("Failed to delete URIs not in hash set for collection {}",
                    collectionName, e);
            throw new RuntimeException("Delete cleanup failed", e);
        }
    }

    /**
     * 分页查询
     */
    public Page<UriEntity> findByCondition(QueryParams params) {
        Timer.Sample timer = Timer.start(meterRegistry);
        String collectionName = TableNameHelper.getTableName(params.getRootNode());

        try {
            Criteria criteria = buildCriteria(params);
            Query query = new Query(criteria).with(params.getPageable());

            long total = mongoTemplate.count(query, UriEntity.class, collectionName);
            List<UriEntity> content = mongoTemplate.find(query, UriEntity.class, collectionName);

            recordMetrics("query", timer, content.size(), total);
            return new PageImpl<>(content, params.getPageable(), total);
        } catch (Exception e) {
            recordError("query");
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
            return Collections.emptyList();
        }

        Timer.Sample timer = Timer.start(meterRegistry);
        try {
            // 按rootNode分组URI
            Map<String, List<String>> groupedUris = uris.stream()
                    .collect(Collectors.groupingBy(rootNodeResolver));

            List<UriEntity> results = new ArrayList<>();
            for (Map.Entry<String, List<String>> entry : groupedUris.entrySet()) {
                results.addAll(queryByGroup(entry.getKey(), entry.getValue(), includeDeleted));
            }

            recordMetrics("batch_query", timer, uris.size(), results.size());
            return results;
        } catch (Exception e) {
            recordError("batch_query");
            log.error("Failed to batch query URIs", e);
            throw new RuntimeException("Batch query failed", e);
        }
    }

    private List<UriEntity> queryByGroup(
            String rootNode,
            List<String> uris,
            Boolean includeDeleted
    ) {
        String collectionName = TableNameHelper.getTableName(rootNode);
        List<String> uriHashes = uris.stream()
                .map(TableNameHelper::generateUriHash)
                .collect(Collectors.toList());

        Criteria criteria = Criteria.where("uri_hash").in(uriHashes);
        if (!includeDeleted) {
            criteria.and("is_deleted").is(false);
        }

        Query query = new Query(criteria);
        try {
            return mongoTemplate.find(query, UriEntity.class, collectionName);
        } catch (Exception e) {
            log.error("Failed to query collection {} for group", collectionName, e);
            return Collections.emptyList();
        }
    }

    @Data
    @Builder
    public static class QueryParams {
        private String rootNode;
        private String version;
        private String versionType;
        private Boolean includeDeleted;
        private Boolean onlyDeleted;
        private Pageable pageable;
    }

    private Criteria buildCriteria(QueryParams params) {
        Criteria criteria = new Criteria();

        if (StringUtils.hasText(params.getVersion())) {
            criteria.and("uri_version").is(params.getVersion());
        }
        if (StringUtils.hasText(params.getVersionType())) {
            criteria.and("version_type").is(params.getVersionType());
        }
        if (params.getOnlyDeleted()) {
            criteria.and("is_deleted").is(true);
        } else if (!params.getIncludeDeleted()) {
            criteria.and("is_deleted").is(false);
        }

        return criteria;
    }

    private Document convertEntityToDocument(UriEntity entity) {
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

    private void recordMetrics(String operation, Timer.Sample timer, long requested, long actual) {
        timer.stop(meterRegistry.timer("mongodb.operation", "type", operation));
        meterRegistry.counter("mongodb.operation.total", "type", operation).increment();
        meterRegistry.gauge("mongodb.operation.ratio",
                Tags.of("type", operation),
                actual / (double)requested);
    }

    private void recordError(String operation) {
        meterRegistry.counter("mongodb.operation.error", "type", operation).increment();
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
import com.study.collect.business.testcase.common.constants.CollectionConstants;
import com.study.collect.business.testcase.common.utils.HttpUtil;
import com.study.collect.business.testcase.common.utils.RateLimiter;
import com.study.collect.business.testcase.model.param.CollectParam;
import com.study.collect.business.testcase.model.param.PageParam;
import com.study.collect.business.testcase.model.response.PageResponse;
import com.study.collect.business.testcase.model.response.VersionResponse;
import com.study.collect.business.testcase.model.response.parse.HttpResponseParser;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * URI HTTP服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UriHttpService {
    private final HttpResponseParser<PageResponse<VersionResponse>> versionParser;
    private final HttpResponseParser<PageResponse<String>> uriListParser;
    private final HttpResponseParser<List<Map<String, Object>>> uriDetailParser;
    private final RateLimiter rateLimiter;
    private final ObjectMapper objectMapper;
    private final MeterRegistry meterRegistry;

    @Qualifier("httpExecutor")
    private final ThreadPoolTaskExecutor httpExecutor;

    /**
     * 构建请求头
     */
    private Map<String, String> buildHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        return headers;
    }

    /**
     * 获取所有版本（分页）
     */
    public CompletableFuture<PageResponse<VersionResponse>> getVersionsAsync(
            CollectParam param,
            PageParam pageParam
    ) {
        return CompletableFuture.supplyAsync(() -> {
            final Timer.Sample timer = Timer.start(meterRegistry);
            try {
                rateLimiter.acquire();
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("rootNode", param.getRootNode());
                requestBody.put("page", pageParam.getPage());
                requestBody.put("size", pageParam.getSize());

                HttpUtil.HttpResponse response = HttpUtil.post(
                        param.getServerUri() + "/api/versions",
                        objectMapper.writeValueAsString(requestBody),
                        buildHeaders()
                );

                // 处理响应
                handleResponse(response);
                recordMetrics("versions", timer);

                return versionParser.parse(response.getBody());
            } catch (Exception e) {
                recordError("versions");
                log.error("Failed to get versions for rootNode: {}", param.getRootNode(), e);
                throw new RuntimeException("Failed to get versions", e);
            }
        }, httpExecutor);
    }

    /**
     * 获取单个版本的所有URI（不分页）
     */
    public List<String> getAllUrisForVersion(CollectParam param, String version) throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("version", version);
        requestBody.put("page", 1);
        requestBody.put("size", Integer.MAX_VALUE);  // 一次性获取所有URI

        final Timer.Sample timer = Timer.start(meterRegistry);
        try {
            rateLimiter.acquire();
            HttpUtil.HttpResponse response = HttpUtil.post(
                    param.getServerUri() + "/api/uris",
                    objectMapper.writeValueAsString(requestBody),
                    buildHeaders()
            );

            handleResponse(response);
            recordMetrics("uris", timer);

            PageResponse<String> pageResponse = uriListParser.parse(response.getBody());
            return pageResponse.getItems();
        } catch (Exception e) {
            recordError("uris");
            log.error("Failed to get URIs for version: {}", version, e);
            throw new RuntimeException("Failed to get URIs", e);
        }
    }

    /**
     * 批量获取URI详情
     */
    public CompletableFuture<List<Map<String, Object>>> getUriDetailsAsync(
            CollectParam param,
            List<String> uris
    ) {
        if (CollectionUtils.isEmpty(uris)) {
            return CompletableFuture.completedFuture(Collections.emptyList());
        }

        return CompletableFuture.supplyAsync(() -> {
            final Timer.Sample timer = Timer.start(meterRegistry);
            try {
                rateLimiter.acquire();
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("uris", uris);

                HttpUtil.HttpResponse response = HttpUtil.post(
                        param.getServerUri() + "/api/details",
                        objectMapper.writeValueAsString(requestBody),
                        buildHeaders()
                );

                handleResponse(response);
                recordMetrics("details", timer);

                return uriDetailParser.parse(response.getBody());
            } catch (Exception e) {
                recordError("details");
                log.error("Failed to get URI details for {} URIs", uris.size(), e);
                throw new RuntimeException("Failed to get URI details", e);
            }
        }, httpExecutor);
    }

    /**
     * 处理HTTP响应
     */
    private void handleResponse(HttpUtil.HttpResponse response) {
        if (response.getCode() >= 400) {
            String errorMessage = parseErrorMessage(response.getBody());
            throw new RuntimeException("Request failed with code " +
                    response.getCode() + ": " + errorMessage);
        }
    }

    /**
     * 解析错误消息
     */
    private String parseErrorMessage(String responseBody) {
        try {
            return objectMapper.readTree(responseBody)
                    .path("message")
                    .asText("Unknown error");
        } catch (Exception e) {
            return responseBody;
        }
    }

    /**
     * 记录指标
     */
    private void recordMetrics(String operation, Timer.Sample timer) {
        timer.stop(meterRegistry.timer("http.request", "operation", operation));
        meterRegistry.counter("http.request.total", "operation", operation).increment();
    }

    /**
     * 记录错误
     */
    private void recordError(String operation) {
        meterRegistry.counter("http.request.error", "operation", operation).increment();
    }

    /**
     * 健康检查
     */
    public boolean isHealthy(String serverUri) {
        try {
            HttpUtil.HttpResponse response = HttpUtil.get(serverUri + "/health");
            return response.getCode() == 200;
        } catch (Exception e) {
            log.error("Health check failed", e);
            return false;
        }
    }

    /**
     * 获取限流器状态
     */
    public Map<String, Object> getRateLimiterStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("currentRate", rateLimiter.getCurrentRate());
        stats.put("maxRate", CollectionConstants.Http.MAX_REQUESTS_PER_MINUTE);
        return stats;
    }
}
```

## UriCleanupService.java

```java
package com.study.collect.business.testcase.service.impl;

import com.study.collect.business.testcase.common.constants.CollectionConstants;
import com.study.collect.business.testcase.common.utils.StreamProcessor;
import com.study.collect.business.testcase.common.utils.TableNameHelper;
import com.study.collect.business.testcase.repository.UriRepository;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class UriCleanupService {

    private final UriRepository repository;

    @Qualifier("mongoExecutor")
    private final ThreadPoolTaskExecutor mongoExecutor;

    @Qualifier("virtualThreadExecutor")
    private final ExecutorService virtualThreadExecutor;

    @Data
    @Builder
    public static class CleanupParams {
        private String rootNode;
        private List<String> uris;
        private boolean hardDelete;
        @Builder.Default
        private int batchSize = CollectionConstants.Process.DEFAULT_BATCH_SIZE;
        @Builder.Default
        private long timeout = CollectionConstants.Process.TASK_TIMEOUT;
    }

    @Data
    @Builder
    public static class DeleteParams {
        private String rootNode;
        private List<String> uris;
        private boolean hardDelete;
        @Builder.Default
        private int batchSize = CollectionConstants.Process.DEFAULT_BATCH_SIZE;
        @Builder.Default
        private long timeout = CollectionConstants.Process.TASK_TIMEOUT;
    }

    @Data
    @Builder
    public static class CleanupResult {
        private long processedCount;
        private long deletedCount;
        private long errorCount;
        private List<String> failedUris;
        private Map<String, Object> details;
    }

    public CompletableFuture<StreamProcessor.ProcessMetrics> cleanup(
            CleanupParams params,
            Consumer<StreamProcessor.ProcessMetrics> progressCallback
    ) {
        validateParams(params);

        // 创建 Set 用于存储有效的 URI hashes
        Set<String> validUriHashes = new HashSet<>();
        for (String uri : params.getUris()) {
            validUriHashes.add(TableNameHelper.generateUriHash(uri));
        }

        // 创建处理器配置，注意这里修改为使用 Set<String> 作为处理单元
        StreamProcessor.ProcessorConfig<Set<String>, Long> config =
                StreamProcessor.ProcessorConfig.<Set<String>, Long>builder()
                        .processorName("URI-Cleanup-" + params.getRootNode())
                        .batchSize(1) // 因为我们现在是处理整个 Set，所以批次大小为 1
                        .maxConcurrent(1)
                        .timeoutSeconds(params.getTimeout())
                        .maxRetries(CollectionConstants.Http.MAX_RETRY)
                        .retryDelayMs(CollectionConstants.Http.RETRY_INTERVAL)
                        .processExecutor(virtualThreadExecutor)
                        .saveExecutor(mongoExecutor.getThreadPoolExecutor())
                        // 修改数据获取函数，直接返回包含单个 Set 的列表
                        .dataFetcher(offset -> offset == 0 ?
                                Collections.singletonList(validUriHashes) :
                                Collections.emptyList())
                        .dataConverter(uriHashes -> processCleanup(params.getRootNode(), uriHashes,
                                params.isHardDelete()))
                        .dataSaver(this::updateMetrics)
                        .progressCallback(progressCallback)
                        .build();

        StreamProcessor<Set<String>, Long> processor = new StreamProcessor<>(config);
        return processor.process(0, 1); // 只需处理一次
    }

    public CompletableFuture<StreamProcessor.ProcessMetrics> batchDelete(
            DeleteParams params,
            Consumer<StreamProcessor.ProcessMetrics> progressCallback
    ) {
        validateParams(params);

        StreamProcessor.ProcessorConfig<List<String>, Long> config =
                StreamProcessor.ProcessorConfig.<List<String>, Long>builder()
                        .processorName("URI-Delete-" + params.getRootNode())
                        .batchSize(params.getBatchSize())
                        .maxConcurrent(CollectionConstants.Process.MAX_CONCURRENT_TASKS)
                        .timeoutSeconds(params.getTimeout())
                        .maxRetries(CollectionConstants.Http.MAX_RETRY)
                        .retryDelayMs(CollectionConstants.Http.RETRY_INTERVAL)
                        .processExecutor(virtualThreadExecutor)
                        .saveExecutor(mongoExecutor.getThreadPoolExecutor())
                        // 批量获取数据
                        .dataFetcher(offset -> fetchBatch(params.getUris(), offset, params.getBatchSize()))
                        .dataConverter(batch -> processDelete(params.getRootNode(), batch,
                                params.isHardDelete()))
                        .dataSaver(this::updateMetrics)
                        .progressCallback(progressCallback)
                        .build();

        StreamProcessor<List<String>, Long> processor = new StreamProcessor<>(config);
        return processor.process(0, params.getUris().size());
    }

    private List<List<String>> fetchBatch(List<String> allUris, int offset, int batchSize) {
        int endIndex = Math.min(offset + batchSize, allUris.size());
        if (offset < allUris.size()) {
            return Collections.singletonList(allUris.subList(offset, endIndex));
        }
        return Collections.emptyList();
    }

    private Long processCleanup(String rootNode, Set<String> validUriHashes, boolean hardDelete) {
        try {
            return (Long) (hardDelete ?
                                repository.deleteNotInUriHashes(rootNode, validUriHashes) :
                                repository.softDeleteNotInUriHashes(rootNode, validUriHashes));
        } catch (Exception e) {
            log.error("Error during cleanup for rootNode: {}", rootNode, e);
            throw new RuntimeException("Cleanup failed", e);
        }
    }

    private Long processDelete(String rootNode, List<String> uris, boolean hardDelete) {
        try {
            return (Long) (hardDelete ?
                                repository.batchHardDelete(rootNode, uris) :
                                repository.batchSoftDelete(rootNode, uris));
        } catch (Exception e) {
            log.error("Error during delete for rootNode: {}", rootNode, e);
            throw new RuntimeException("Delete failed", e);
        }
    }

    private void updateMetrics(List<Long> counts) {
        long total = counts.stream().mapToLong(Long::longValue).sum();
        log.debug("Processed batch with total count: {}", Optional.of(total));
    }

    private void validateParams(CleanupParams params) {
        Objects.requireNonNull(params.getRootNode(), "RootNode must not be null");
        Objects.requireNonNull(params.getUris(), "URIs list must not be null");

        if (params.getBatchSize() < CollectionConstants.Process.MIN_BATCH_SIZE ||
                params.getBatchSize() > CollectionConstants.Process.MAX_BATCH_SIZE) {
            throw new IllegalArgumentException("Invalid batch size: " + params.getBatchSize());
        }
    }

    private void validateParams(DeleteParams params) {
        Objects.requireNonNull(params.getRootNode(), "RootNode must not be null");
        Objects.requireNonNull(params.getUris(), "URIs list must not be null");

        if (params.getBatchSize() < CollectionConstants.Process.MIN_BATCH_SIZE ||
                params.getBatchSize() > CollectionConstants.Process.MAX_BATCH_SIZE) {
            throw new IllegalArgumentException("Invalid batch size: " + params.getBatchSize());
        }
    }
}
```

## UriCollectServiceImpl.java

```java
package com.study.collect.business.testcase.service.impl;

import com.study.collect.business.testcase.core.executor.CollectExecutor;
import com.study.collect.business.testcase.core.executor.DeleteExecutor;
import com.study.collect.business.testcase.core.manager.QueueManager;
import com.study.collect.business.testcase.core.manager.TaskManager;
import com.study.collect.business.testcase.entity.UriEntity;
import com.study.collect.business.testcase.model.param.CollectParam;
import com.study.collect.business.testcase.model.param.DeleteParam;
import com.study.collect.business.testcase.model.param.QueryParam;
import com.study.collect.business.testcase.model.response.AsyncResponse;
import com.study.collect.business.testcase.model.response.TaskResponse;
import com.study.collect.business.testcase.repository.UriRepository;
import com.study.collect.business.testcase.service.UriCollectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * URI采集服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UriCollectServiceImpl implements UriCollectService {

    private final UriRepository repository;
    private final CollectExecutor collectExecutor;
    private final DeleteExecutor deleteExecutor;
    private final TaskManager taskManager;
    private final QueueManager queueManager;

    @Override
    public AsyncResponse<String> collectData(CollectParam param) {
        validateCollectParam(param);
        try {
            // 1. 创建任务
            Map<String, Object> taskParams = buildTaskParams(param);
            TaskResponse task = taskManager.createTask("COLLECT", taskParams, param.getPriority());
            String taskId = task.getTaskId();
            param.setTaskId(taskId);

            // 2. 将任务加入队列
            queueManager.enqueue(
                    taskId,
                    param,
                    param.getPriority(),
                    this::processCollectTask
            ).exceptionally(throwable -> {
                handleTaskError(taskId, "Collection queuing failed", throwable);
                return null;
            });

            // 3. 返回异步响应
            return AsyncResponse.<String>builder()
                    .taskId(taskId)
                    .status("QUEUED")
                    .message("Data collection task queued successfully")
                    .build();

        } catch (Exception e) {
            log.error("Failed to initiate collection task", e);
            throw new RuntimeException("Failed to start collection task", e);
        }
    }

    private void processCollectTask(CollectParam param) {
        String taskId = param.getTaskId();
        try {
            taskManager.updateTaskStatus(taskId, "PROCESSING", "Starting data collection");

            // 1. 执行采集
            collectExecutor.execute(param, metrics -> {
                taskManager.updateTaskProgress(
                        taskId,
                        metrics.getProcessedItems(),
                        metrics.getTotalItems()
                );
                taskManager.updateTaskStatus(
                        taskId,
                        "PROCESSING",
                        metrics.getStatusMessage()
                );
            }).thenAccept(metrics -> {
                taskManager.updateTaskStatus(
                        taskId,
                        "COMPLETED",
                        String.format("Processed %d URIs", metrics.getProcessedItems())
                );
            }).exceptionally(throwable -> {
                handleTaskError(taskId, "Collection failed", throwable);
                return null;
            });

        } catch (Exception e) {
            handleTaskError(taskId, "Task processing failed", e);
            throw new RuntimeException("Task processing failed", e);
        }
    }

    @Override
    public AsyncResponse<Long> deleteData(DeleteParam param) {
        validateDeleteParam(param);
        try {
            // 1. 创建任务
            Map<String, Object> taskParams = buildDeleteTaskParams(param);
            TaskResponse task = taskManager.createTask("DELETE", taskParams, param.getPriority());
            String taskId = task.getTaskId();
            param.setTaskId(taskId);

            // 2. 将任务加入队列
            queueManager.enqueue(
                    taskId,
                    param,
                    param.getPriority(),
                    this::processDeleteTask
            ).exceptionally(throwable -> {
                handleTaskError(taskId, "Delete queuing failed", throwable);
                return null;
            });

            // 3. 返回异步响应
            return AsyncResponse.<Long>builder()
                    .taskId(taskId)
                    .status("QUEUED")
                    .message("Delete task queued successfully")
                    .build();

        } catch (Exception e) {
            log.error("Failed to initiate delete task", e);
            throw new RuntimeException("Failed to start delete task", e);
        }
    }

    private void processDeleteTask(DeleteParam param) {
        String taskId = param.getTaskId();
        try {
            taskManager.updateTaskStatus(taskId, "PROCESSING", "Starting data deletion");

            deleteExecutor.execute(param, metrics -> {
                taskManager.updateTaskProgress(
                        taskId,
                        metrics.getProcessedItems(),
                        metrics.getTotalItems()
                );
                taskManager.updateTaskStatus(
                        taskId,
                        "PROCESSING",
                        metrics.getStatusMessage()
                );
            }).thenAccept(metrics -> {
                taskManager.updateTaskStatus(
                        taskId,
                        "COMPLETED",
                        String.format("Deleted %d URIs", metrics.getProcessedItems())
                );
            }).exceptionally(throwable -> {
                handleTaskError(taskId, "Deletion failed", throwable);
                return null;
            });

        } catch (Exception e) {
            handleTaskError(taskId, "Task processing failed", e);
            throw new RuntimeException("Task processing failed", e);
        }
    }

    @Override
    public Page<UriEntity> queryUri(QueryParam param) {
        validateQueryParam(param);
        return repository.findByCondition(
                UriRepository.QueryParams.builder()
                        .rootNode(param.getRootNode())
                        .version(param.getVersion())
                        .versionType(param.getVersionType())
                        .includeDeleted(param.getIncludeDeleted())
                        .onlyDeleted(param.getOnlyDeleted())
                        .pageable(PageRequest.of(param.getPage() - 1, param.getSize()))
                        .build()
        );
    }

    @Override
    public List<UriEntity> batchQueryUri(List<String> uris, Boolean includeDeleted) {
        if (CollectionUtils.isEmpty(uris)) {
            return Collections.emptyList();
        }

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
        if (queueManager.cancel(taskId)) {
            taskManager.cancelTask(taskId);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateTaskPriority(String taskId, int priority) {
        // 更新任务优先级
        if (queueManager.updatePriority(taskId, priority)) {
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

    private String extractRootNode(String uri) {
        return StringUtils.hasText(uri) ? uri.split("/")[0] : "";
    }

    private void handleTaskError(String taskId, String message, Throwable throwable) {
        log.error(message + " - Task: {}", taskId, throwable);
        taskManager.updateTaskStatus(
                taskId,
                "ERROR",
                message + ": " + throwable.getMessage()
        );
    }

    private void validateCollectParam(CollectParam param) {
        if (!StringUtils.hasText(param.getRootNode())) {
            throw new IllegalArgumentException("rootNode cannot be empty");
        }
        if (!StringUtils.hasText(param.getServerUri())) {
            throw new IllegalArgumentException("serverUri cannot be empty");
        }
    }

    private void validateDeleteParam(DeleteParam param) {
        if (CollectionUtils.isEmpty(param.getUris())) {
            throw new IllegalArgumentException("uris cannot be empty");
        }
    }

    private void validateQueryParam(QueryParam param) {
        if (!StringUtils.hasText(param.getRootNode()) &&
                CollectionUtils.isEmpty(param.getUris())) {
            throw new IllegalArgumentException("rootNode or uris must be provided");
        }
    }

    private Map<String, Object> buildTaskParams(CollectParam param) {
        Map<String, Object> params = new HashMap<>();
        params.put("rootNode", param.getRootNode());
        params.put("serverUri", param.getServerUri());
        params.put("version", param.getVersion());
        params.put("incremental", param.getIncremental());
        params.put("batchSize", param.getBatchSize());
        return params;
    }

    private Map<String, Object> buildDeleteTaskParams(DeleteParam param) {
        Map<String, Object> params = new HashMap<>();
        params.put("rootNode", param.getRootNode());
        params.put("urisCount", param.getUris().size());
        params.put("hardDelete", param.getHardDelete());
        params.put("batchSize", param.getBatchSize());
        return params;
    }
}
```

## spring.factories

```
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
com.study.collect.business.testcase.config.TestCaseAutoConfiguration
```

