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

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 通用流式处理器
 * @param <T> 输入数据类型
 * @param <R> 输出数据类型
 */
@Slf4j
public class StreamProcessor<T, R> {

    @Data
    @Builder
    public static class ProcessorConfig<T, R> {
        private String processorName;
        private int batchSize;
        private int maxConcurrent;
        private long timeoutSeconds;
        private int maxRetries;
        private long retryDelayMs;
        private ExecutorService processExecutor;
        private ExecutorService saveExecutor;

        // 处理函数
        private Function<Integer, List<T>> dataFetcher;
        private Function<T, R> dataConverter;
        private Consumer<List<R>> dataSaver;
        private Consumer<ProcessMetrics> progressCallback;
    }

    @Data
    @Builder
    public static class ProcessMetrics {
        private String processorName;
        private long totalItems;
        private long processedItems;
        private long failedItems;
        private long startTime;
        private long endTime;
        private double progressPercentage;
        private Map<String, Object> customMetrics;
    }

    private final ProcessorConfig<T, R> config;
    private final BlockingQueue<CompletableFuture<?>> processQueue;
    private final AtomicInteger activeProcesses;
    private final AtomicBoolean running;
    private final List<ProcessMetrics> metricsHistory;

    public StreamProcessor(ProcessorConfig<T, R> config) {
        validateConfig(config);
        this.config = config;
        this.processQueue = new ArrayBlockingQueue<>(1000);
        this.activeProcesses = new AtomicInteger(0);
        this.running = new AtomicBoolean(true);
        this.metricsHistory = new CopyOnWriteArrayList<>();
    }

    /**
     * 开始处理数据
     */
    public CompletableFuture<ProcessMetrics> process(int offset, int limit) {
        ProcessMetrics metrics = initializeMetrics();
        CompletableFuture<ProcessMetrics> resultFuture = new CompletableFuture<>();

        try {
            if (activeProcesses.incrementAndGet() <= config.getMaxConcurrent()) {
                processDataBatches(offset, limit, metrics, resultFuture);
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

    private void processDataBatches(int offset, int limit, ProcessMetrics metrics,
                                    CompletableFuture<ProcessMetrics> resultFuture) {
        CompletableFuture.runAsync(() -> {
            try {
                int processed = 0;
                while (running.get() && processed < limit) {
                    List<T> batch = fetchData(offset + processed);
                    if (CollectionUtils.isEmpty(batch)) {
                        break;
                    }

                    processBatch(batch, metrics);
                    processed += batch.size();
                    updateProgress(metrics, processed, limit);
                }

                completeProcessing(metrics, resultFuture);
            } catch (Exception e) {
                handleProcessingError(e, metrics, resultFuture);
            }
        }, config.getProcessExecutor());
    }

    private List<T> fetchData(int offset) {
        int retryCount = 0;
        while (retryCount <= config.getMaxRetries()) {
            try {
                return config.getDataFetcher().apply(offset);
            } catch (Exception e) {
                if (++retryCount > config.getMaxRetries()) {
                    log.error("Failed to fetch data after {} retries", config.getMaxRetries(), e);
                    throw new RuntimeException("Data fetch failed", e);
                }
                sleep(calculateRetryDelay(retryCount));
            }
        }
        return new ArrayList<>();
    }

    private void processBatch(List<T> batch, ProcessMetrics metrics) {
        List<R> convertedBatch = new ArrayList<>();
        for (T item : batch) {
            try {
                R converted = config.getDataConverter().apply(item);
                if (converted != null) {
                    convertedBatch.add(converted);
                }
            } catch (Exception e) {
                log.error("Error converting item", e);
                metrics.setFailedItems(metrics.getFailedItems() + 1);
            }
        }

        if (!convertedBatch.isEmpty()) {
            saveBatch(convertedBatch, metrics);
        }
    }

    private void saveBatch(List<R> batch, ProcessMetrics metrics) {
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
                    metrics.setFailedItems(metrics.getFailedItems() + batch.size());
                    throw new RuntimeException("Batch save failed", e);
                }
                sleep(calculateRetryDelay(retryCount));
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
                .processorName(config.getProcessorName())
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

    private void completeProcessing(ProcessMetrics metrics, CompletableFuture<ProcessMetrics> resultFuture) {
        metrics.setEndTime(System.currentTimeMillis());
        metricsHistory.add(metrics);
        activeProcesses.decrementAndGet();
        resultFuture.complete(metrics);
    }

    private void handleProcessingError(Exception e, ProcessMetrics metrics,
                                       CompletableFuture<ProcessMetrics> resultFuture) {
        log.error("Error processing data", e);
        metrics.setEndTime(System.currentTimeMillis());
        metricsHistory.add(metrics);
        activeProcesses.decrementAndGet();
        resultFuture.completeExceptionally(e);
    }

    private long calculateRetryDelay(int retryCount) {
        return config.getRetryDelayMs() * (long) Math.pow(2, retryCount - 1);
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
        if (config.getDataFetcher() == null) {
            throw new IllegalArgumentException("DataFetcher cannot be null");
        }
        if (config.getDataConverter() == null) {
            throw new IllegalArgumentException("DataConverter cannot be null");
        }
        if (config.getDataSaver() == null) {
            throw new IllegalArgumentException("DataSaver cannot be null");
        }
        if (config.getProcessExecutor() == null) {
            throw new IllegalArgumentException("ProcessExecutor cannot be null");
        }
        if (config.getSaveExecutor() == null) {
            throw new IllegalArgumentException("SaveExecutor cannot be null");
        }
    }

    /**
     * 暂停处理
     */
    public void pause() {
        running.set(false);
    }

    /**
     * 恢复处理
     */
    public void resume() {
        running.set(true);
    }

    /**
     * 停止处理
     */
    public void shutdown() {
        running.set(false);
        config.getProcessExecutor().shutdown();
        config.getSaveExecutor().shutdown();
        try {
            if (!config.getProcessExecutor().awaitTermination(30, TimeUnit.SECONDS)) {
                config.getProcessExecutor().shutdownNow();
            }
            if (!config.getSaveExecutor().awaitTermination(30, TimeUnit.SECONDS)) {
                config.getSaveExecutor().shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            config.getProcessExecutor().shutdownNow();
            config.getSaveExecutor().shutdownNow();
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
     * 是否正在运行
     */
    public boolean isRunning() {
        return running.get();
    }

    /**
     * 清除历史指标
     */
    public void clearMetricsHistory() {
        metricsHistory.clear();
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
     * @param rootNode 根节点
     * @return 完整表名
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
     * 获取rootNode
     * @param uri URI
     * @return rootNode
     */
    public static String extractRootNode(String uri) {
        Assert.hasText(uri, "URI must not be empty");

        int firstSlash = uri.indexOf('/');
        if (firstSlash == -1) {
            return uri;
        }
        return uri.substring(0, firstSlash);
    }

    /**
     * 验证表名是否合法
     */
    private static void validateTableName(String tableName) {
        if (!TABLE_NAME_PATTERN.matcher(tableName).matches()) {
            throw new IllegalArgumentException("Invalid table name: " + tableName);
        }
        if (tableName.length() > MAX_TABLE_NAME_LENGTH) {
            throw new IllegalArgumentException("Table name too long: " + tableName);
        }
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
     * 生成URI哈希值
     */
    public static String generateUriHash(String uri) {
        Assert.hasText(uri, "URI must not be empty");
        return DigestUtils.sha256Hex(uri);
    }

    /**
     * 生成带版本的表名
     */
    public static String getVersionedTableName(String rootNode, String version) {
        Assert.hasText(rootNode, "RootNode must not be empty");
        Assert.hasText(version, "Version must not be empty");

        return TABLE_NAME_CACHE.computeIfAbsent(
                rootNode + "_" + version,
                key -> {
                    String tableName = CollectionConstants.Collection.URI_COLLECTION_PREFIX
                            + "_" + rootNode
                            + "_" + version;
                    validateTableName(tableName);
                    return tableName;
                }
        );
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
     * 清除表名缓存
     */
    public static void clearCache() {
        TABLE_NAME_CACHE.clear();
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

import com.study.collect.business.testcase.common.constants.CollectionConstants;
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

    private final TestCaseCollectorProperties properties;

    @Value("${spring.data.mongodb.uri}")
    private String uri;

    @Value("${spring.data.mongodb.database}")
    private String database;

    @Value("${spring.data.mongodb.min-pool-size:" + CollectionConstants.Database.MONGO_MIN_POOL_SIZE + "}")
    private Integer minPoolSize;

    @Value("${spring.data.mongodb.max-pool-size:" + CollectionConstants.Database.MONGO_MAX_POOL_SIZE + "}")
    private Integer maxPoolSize;

    public MongoConfig(TestCaseCollectorProperties properties) {
        this.properties = properties;
    }

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
                        builder.minSize(properties.getMongoMinPoolSize())
                                .maxSize(properties.getMongoMaxPoolSize())
                                .maxWaitTime(10, TimeUnit.SECONDS)
                                .maxConnectionLifeTime(30, TimeUnit.MINUTES)
                                .maxConnectionIdleTime(5, TimeUnit.MINUTES))
                .applyToSocketSettings(builder ->
                        builder.connectTimeout(properties.getHttpConnectTimeout(), TimeUnit.MILLISECONDS)
                                .readTimeout(properties.getHttpReadTimeout(), TimeUnit.MILLISECONDS))
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
        poolConfig.setMaxTotal(properties.getPoolMaxTotal());
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
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "collect.testcase")
public class TestCaseCollectorProperties {
    // 基本配置
    private int batchSize = CollectionConstants.Process.DEFAULT_BATCH_SIZE;
    private int threadCount = Runtime.getRuntime().availableProcessors() * 2;
    private int retryTimes = CollectionConstants.Http.MAX_RETRY;
    private int timeout = (int) CollectionConstants.Process.TASK_TIMEOUT;

    // HTTP配置
    private int httpMaxRequestsPerMinute = CollectionConstants.Http.MAX_REQUESTS_PER_MINUTE;
    private int httpConnectTimeout = CollectionConstants.Http.CONNECT_TIMEOUT;
    private int httpReadTimeout = CollectionConstants.Http.READ_TIMEOUT;
    private int httpRetryInterval = (int) CollectionConstants.Http.RETRY_INTERVAL;

    // MongoDB配置
    private int mongoMinPoolSize = CollectionConstants.Database.MONGO_MIN_POOL_SIZE;
    private int mongoMaxPoolSize = CollectionConstants.Database.MONGO_MAX_POOL_SIZE;
    private int mongoBatchSize = CollectionConstants.Database.MONGO_BATCH_SIZE;

    // 任务配置
    private int maxConcurrentTasks = CollectionConstants.Process.MAX_CONCURRENT_TASKS;
    private int taskQueueCapacity = CollectionConstants.Process.TASK_QUEUE_CAPACITY;
    private long taskTimeoutSeconds = CollectionConstants.Process.TASK_TIMEOUT;

    // 对象池配置
    private int poolMaxTotal = CollectionConstants.Pool.MAX_TOTAL;
    private int poolMaxIdle = CollectionConstants.Pool.MAX_IDLE;
    private int poolMinIdle = CollectionConstants.Pool.MIN_IDLE;

    // 版本配置
    private String versionPrefix = CollectionConstants.Collection.VERSION_PREFIX;
    private String versionSeparator = CollectionConstants.Collection.VERSION_SEPARATOR;

    // 增量同步配置
    private boolean enableIncrementalSync = true;
    private boolean enableHardDelete = false;
    private int cleanupBatchSize = CollectionConstants.Process.DEFAULT_BATCH_SIZE;
    private int cleanupThreads = Runtime.getRuntime().availableProcessors();
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

import com.study.collect.business.testcase.common.constants.CollectionConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池配置
 */
@Configuration
@EnableAsync
@Slf4j
public class ThreadPoolConfig {

    /**
     * HTTP请求线程池
     */
    @Bean(name = "httpExecutor")
    public ThreadPoolTaskExecutor httpExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(CollectionConstants.ThreadPool.HTTP_CORE_SIZE);
        executor.setMaxPoolSize(CollectionConstants.ThreadPool.HTTP_MAX_SIZE);
        executor.setQueueCapacity(CollectionConstants.ThreadPool.HTTP_QUEUE_SIZE);
        executor.setKeepAliveSeconds((int) CollectionConstants.ThreadPool.HTTP_KEEP_ALIVE);
        executor.setThreadNamePrefix("http-thread-");
        executor.setRejectedExecutionHandler((r, e) -> {
            log.warn("HTTP thread pool is full, task rejected");
            throw new RejectedExecutionException("HTTP thread pool is full");
        });
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        return executor;
    }

    /**
     * MongoDB操作线程池
     */
    @Bean(name = "mongoExecutor")
    public ThreadPoolTaskExecutor mongoExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(CollectionConstants.ThreadPool.MONGO_CORE_SIZE);
        executor.setMaxPoolSize(CollectionConstants.ThreadPool.MONGO_MAX_SIZE);
        executor.setQueueCapacity(CollectionConstants.ThreadPool.MONGO_QUEUE_SIZE);
        executor.setKeepAliveSeconds((int) CollectionConstants.ThreadPool.MONGO_KEEP_ALIVE);
        executor.setThreadNamePrefix("mongo-thread-");
        executor.setRejectedExecutionHandler((r, e) -> {
            log.warn("MongoDB thread pool is full, task rejected");
            throw new RejectedExecutionException("MongoDB thread pool is full");
        });
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        return executor;
    }

    /**
     * 任务处理线程池
     */
    @Bean(name = "taskExecutor")
    @Primary
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(CollectionConstants.ThreadPool.TASK_CORE_SIZE);
        executor.setMaxPoolSize(CollectionConstants.ThreadPool.TASK_MAX_SIZE);
        executor.setQueueCapacity(CollectionConstants.ThreadPool.TASK_QUEUE_SIZE);
        executor.setKeepAliveSeconds((int) CollectionConstants.ThreadPool.TASK_KEEP_ALIVE);
        executor.setThreadNamePrefix("task-thread-");
        executor.setRejectedExecutionHandler((r, e) -> {
            log.warn("Task thread pool is full, task rejected");
            throw new RejectedExecutionException("Task thread pool is full");
        });
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        return executor;
    }

    /**
     * 通用任务调度线程池
     */
    @Bean(name = "scheduledExecutor")
    public ScheduledExecutorService scheduledExecutor() {
        return Executors.newScheduledThreadPool(2, new ThreadFactory() {
            private final AtomicInteger counter = new AtomicInteger(1);
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("scheduled-thread-" + counter.getAndIncrement());
                thread.setDaemon(true);
                return thread;
            }
        });
    }

    /**
     * 虚拟线程池(如果JDK版本支持)
     */
    @Bean(name = "virtualThreadExecutor")
    public ExecutorService virtualThreadExecutor() {
        try {
            // 尝试使用虚拟线程
            return Executors.newVirtualThreadPerTaskExecutor();
        } catch (UnsupportedOperationException e) {
            // 降级使用普通线程池
            log.warn("Virtual threads not supported, falling back to normal thread pool");
            return new ThreadPoolExecutor(
                    Runtime.getRuntime().availableProcessors(),
                    Runtime.getRuntime().availableProcessors() * 2,
                    60L, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(1000),
                    new ThreadFactory() {
                        private final AtomicInteger counter = new AtomicInteger(1);
                        @Override
                        public Thread newThread(Runnable r) {
                            Thread thread = new Thread(r);
                            thread.setName("fallback-thread-" + counter.getAndIncrement());
                            return thread;
                        }
                    }
            );
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
import com.study.collect.business.testcase.common.utils.StreamProcessor;
import com.study.collect.business.testcase.entity.UriEntity;
import com.study.collect.business.testcase.model.param.CollectParam;
import com.study.collect.business.testcase.model.param.PageParam;
import com.study.collect.business.testcase.model.response.PageResponse;
import com.study.collect.business.testcase.repository.UriRepository;
import com.study.collect.business.testcase.service.http.UriHttpService;
import com.study.collect.business.testcase.common.utils.RateLimiter;
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

    public CompletableFuture<StreamProcessor.ProcessMetrics> execute(
            CollectParam param,
            Consumer<StreamProcessor.ProcessMetrics> progressCallback
    ) {
        // 1. 创建处理器配置
        StreamProcessor.ProcessorConfig<String, UriEntity> config = StreamProcessor.ProcessorConfig.<String, UriEntity>builder()
                .processorName("URI-Collect-" + param.getRootNode())
                .batchSize(param.getBatchSize())
                .maxConcurrent(CollectionConstants.Process.MAX_CONCURRENT_TASKS)
                .timeoutSeconds(param.getTimeout())
                .maxRetries(param.getMaxRetries())
                .retryDelayMs(CollectionConstants.Http.RETRY_INTERVAL)
                .processExecutor(httpExecutor.getThreadPoolExecutor())
                .saveExecutor(mongoExecutor.getThreadPoolExecutor())
                // 数据获取函数
                .dataFetcher(offset -> fetchUris(param, offset * param.getBatchSize()))
                // 数据转换函数
                .dataConverter(uri -> convertToEntity(param, uri))
                // 数据保存函数
                .dataSaver(entities -> saveEntities(param.getRootNode(), entities))
                // 进度回调
                .progressCallback(progressCallback)
                .build();

        // 2. 创建处理器实例
        StreamProcessor<String, UriEntity> processor = new StreamProcessor<>(config);

        // 3. 获取总数据量
        int totalCount = countTotalUris(param);

        // 4. 开始处理
        return processor.process(0, totalCount);
    }

    // 获取总数据量
    private int countTotalUris(CollectParam param) {
        try {
            PageResponse<String> firstPage = httpService.getUriListAsync(
                    param,
                    param.getVersion(),
                    new PageParam(1, 1)
            ).get();
            return firstPage.getTotal().intValue();
        } catch (Exception e) {
            log.error("Failed to get total URI count", e);
            throw new RuntimeException("Failed to get total URI count", e);
        }
    }

    /**
     * 获取URI列表
     */
    private List<String> fetchUris(CollectParam param, int offset) {
        try {
            rateLimiter.acquire(); // 限流控制
            PageResponse<String> response = httpService.getUriListAsync(
                    param,
                    param.getVersion(),
                    new com.study.collect.business.testcase.model.param.PageParam(
                            offset / param.getBatchSize() + 1,
                            param.getBatchSize()
                    )
            ).get();
//            return response.getItems();
            return response.getItems() != null ? response.getItems() : new ArrayList<>();
        } catch (Exception e) {
            log.error("Error fetching URIs", e);
            throw new RuntimeException("Failed to fetch URIs", e);
        }
    }

    /**
     * 转换为实体
     */
    private UriEntity convertToEntity(CollectParam param, String uri) {
        UriEntity entity = null;
        try {
            entity = entityPool.borrowObject();
            rateLimiter.acquire(); // 限流控制

            // 获取URI详情
            List<Map<String, Object>> details = httpService.getUriDetailsAsync(
                    param,
                    Collections.singletonList(uri)
            ).get();

            if (!details.isEmpty()) {
                Map<String, Object> detail = details.get(0);
                fillEntity(entity, param.getRootNode(), param.getVersion(), uri, detail);
            }

            return entity;
        } catch (Exception e) {
            log.error("Error converting URI to entity: {}", uri, e);
            if (entity != null) {
                try {
                    entityPool.returnObject(entity);
                } catch (Exception ex) {
                    log.error("Error returning entity to pool", ex);
                }
            }
            throw new RuntimeException("Failed to convert URI", e);
        }
    }

    /**
     * 保存实体列表
     */
    private void saveEntities(String rootNode, List<UriEntity> entities) {
        try {
            repository.batchUpsert(rootNode, entities);
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
    }

    /**
     * 填充实体信息
     */
    private void fillEntity(UriEntity entity, String rootNode, String version,
                            String uri, Map<String, Object> detail) {
        entity.setUri(uri);
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

## DeleteExecutor.java

```java
package com.study.collect.business.testcase.core.executor;

import com.study.collect.business.testcase.model.param.DeleteParam;
import com.study.collect.business.testcase.repository.UriRepository;
import com.study.collect.business.testcase.common.utils.StreamProcessor;
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
     * 执行删除任务
     */
    public CompletableFuture<StreamProcessor.ProcessMetrics> execute(
            DeleteParam param,
            Consumer<StreamProcessor.ProcessMetrics> progressCallback
    ) {
        // 1. 预处理URI列表，按rootNode分组
        Map<String, List<String>> groupedUris = groupUrisByRootNode(param);

        // 2. 创建处理器配置
        StreamProcessor.ProcessorConfig<Map.Entry<String, List<String>>, Long> config =
                StreamProcessor.ProcessorConfig.<Map.Entry<String, List<String>>, Long>builder()
                        .processorName("URI-Delete-" + param.getRootNode())
                        .batchSize(getBatchSize(param))
                        .maxConcurrent(com.study.collect.business.testcase.common.constants.CollectionConstants.Process.MAX_CONCURRENT_TASKS)
                        .timeoutSeconds(com.study.collect.business.testcase.common.constants.CollectionConstants.Process.TASK_TIMEOUT)
                        .maxRetries(com.study.collect.business.testcase.common.constants.CollectionConstants.Http.MAX_RETRY)
                        .retryDelayMs(com.study.collect.business.testcase.common.constants.CollectionConstants.Http.RETRY_INTERVAL)
                        .processExecutor(virtualThreadExecutor)
                        .saveExecutor(mongoExecutor.getThreadPoolExecutor())
                        // 数据获取函数
                        .dataFetcher(offset -> fetchBatch(new ArrayList<>(groupedUris.entrySet()), offset))
                        // 数据转换函数
                        .dataConverter(entry -> processDelete(entry, param.getHardDelete()))
                        // 数据保存函数 - 这里用于更新删除计数
                        .dataSaver(this::updateDeleteCount)
                        // 进度回调
                        .progressCallback(progressCallback)
                        .build();

        // 3. 创建处理器实例
        StreamProcessor<Map.Entry<String, List<String>>, Long> processor = new StreamProcessor<>(config);

        // 4. 开始处理
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
        // 创建处理器配置
        StreamProcessor.ProcessorConfig<Set<String>, Long> config =
                StreamProcessor.ProcessorConfig.<Set<String>, Long>builder()
                        .processorName("URI-Cleanup-" + rootNode)
                        .batchSize(com.study.collect.business.testcase.common.constants.CollectionConstants.Process.DEFAULT_BATCH_SIZE)
                        .maxConcurrent(1) // 清理任务限制并发为1
                        .timeoutSeconds(com.study.collect.business.testcase.common.constants.CollectionConstants.Process.TASK_TIMEOUT)
                        .maxRetries(com.study.collect.business.testcase.common.constants.CollectionConstants.Http.MAX_RETRY)
                        .retryDelayMs(com.study.collect.business.testcase.common.constants.CollectionConstants.Http.RETRY_INTERVAL)
                        .processExecutor(virtualThreadExecutor)
                        .saveExecutor(mongoExecutor.getThreadPoolExecutor())
                        // 数据获取函数
                        .dataFetcher(offset -> Collections.singletonList(validUriHashes))
                        // 数据转换函数
                        .dataConverter(hashes -> processCleanup(rootNode, hashes, hardDelete))
                        // 数据保存函数
                        .dataSaver(this::updateDeleteCount)
                        // 进度回调
                        .progressCallback(progressCallback)
                        .build();

        // 创建处理器实例并开始处理
        StreamProcessor<Set<String>, Long> processor = new StreamProcessor<>(config);
        return processor.process(0, 1);
    }

    /**
     * 按rootNode分组URI
     */
    private Map<String, List<String>> groupUrisByRootNode(DeleteParam param) {
        if (param.getRootNode() != null) {
            // 如果指定了rootNode，使用指定的
            return Collections.singletonMap(param.getRootNode(), param.getUris());
        } else {
            // 否则从URI中提取rootNode
            return param.getUris().stream()
                    .collect(Collectors.groupingBy(com.study.collect.business.testcase.common.utils.TableNameHelper::extractRootNode));
        }
    }

    /**
     * 获取批处理大小
     */
    private int getBatchSize(DeleteParam param) {
        if (param.getBatchSize() != null) {
            return Math.min(Math.max(param.getBatchSize(),
                            com.study.collect.business.testcase.common.constants.CollectionConstants.Process.MIN_BATCH_SIZE),
                    com.study.collect.business.testcase.common.constants.CollectionConstants.Process.MAX_BATCH_SIZE);
        }
        return com.study.collect.business.testcase.common.constants.CollectionConstants.Process.DEFAULT_BATCH_SIZE;
    }

    /**
     * 获取一批待处理数据
     */
    private List<Map.Entry<String, List<String>>> fetchBatch(
            List<Map.Entry<String, List<String>>> entries,
            int offset
    ) {
        int end = Math.min(offset + 1, entries.size());
        return offset < entries.size() ? entries.subList(offset, end) : Collections.emptyList();
    }

    /**
     * 处理删除操作
     */
    private Long processDelete(Map.Entry<String, List<String>> entry, boolean hardDelete) {
        String rootNode = entry.getKey();
        List<String> uris = entry.getValue();

        try {
            if (hardDelete) {
                return (Long) repository.batchHardDelete(rootNode, uris);
            } else {
                return (Long) repository.batchSoftDelete(rootNode, uris);
            }
        } catch (Exception e) {
            log.error("Error deleting URIs for rootNode: {}", rootNode, e);
            throw new RuntimeException("Failed to delete URIs", e);
        }
    }

    /**
     * 处理清理操作
     */
    private Long processCleanup(String rootNode, Set<String> validHashes, boolean hardDelete) {
        try {
            if (hardDelete) {
                return (Long) repository.deleteNotInUriHashes(rootNode, validHashes);
            } else {
                return (Long) repository.softDeleteNotInUriHashes(rootNode, validHashes);
            }
        } catch (Exception e) {
            log.error("Error cleaning up URIs for rootNode: {}", rootNode, e);
            throw new RuntimeException("Failed to cleanup URIs", e);
        }
    }

    /**
     * 更新删除计数（批处理后的回调）
     */
    private void updateDeleteCount(List<Long> counts) {
        // 可以在这里实现删除计数的统计逻辑
        long total = counts.stream().mapToLong(Long::longValue).sum();
        log.debug("Batch delete completed, total deleted: {}", Optional.of(total));
    }
}
```

## QueueManager.java

```java
package com.study.collect.business.testcase.core.manager;

import com.study.collect.business.testcase.common.constants.CollectionConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

@Slf4j
@Component
public class QueueManager {
    private final ThreadPoolTaskExecutor taskExecutor;
    private final PriorityBlockingQueue<QueueItem<?>> taskQueue;
    private final ConcurrentHashMap<String, QueueItem<?>> taskMap;
    private final ScheduledExecutorService scheduledExecutor;
    private volatile boolean running = true;

    /**
     * 队列项状态枚举
     */
    public enum QueueItemStatus {
        QUEUED,         // 已入队
        PROCESSING,     // 处理中
        COMPLETED,      // 已完成
        CANCELLED,      // 已取消
        ERROR,          // 错误
        RETRY_WAIT     // 等待重试
    }

    /**
     * 队列项定义
     */
    private static class QueueItem<T> {
        final String taskId;
        final T task;
        volatile int priority;
        final CompletableFuture<Void> future;
        final Consumer<T> processor;
        final LocalDateTime createTime;
        volatile QueueItemStatus status;
        volatile String statusMessage;
        volatile double progress;
        volatile LocalDateTime startTime;
        volatile LocalDateTime endTime;
        volatile int retryCount;
        final Map<String, Object> attributes;

        QueueItem(String taskId, T task, int priority, Consumer<T> processor) {
            this.taskId = taskId;
            this.task = task;
            this.priority = priority;
            this.processor = processor;
            this.future = new CompletableFuture<>();
            this.createTime = LocalDateTime.now();
            this.status = QueueItemStatus.QUEUED;
            this.progress = 0.0;
            this.retryCount = 0;
            this.attributes = new ConcurrentHashMap<>();
        }

        boolean shouldRetry() {
            return retryCount < CollectionConstants.Http.MAX_RETRY;
        }
    }

    public QueueManager(@Qualifier("taskExecutor") ThreadPoolTaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
        this.taskQueue = new PriorityBlockingQueue<>(
                CollectionConstants.Process.TASK_QUEUE_CAPACITY,
                Comparator.<QueueItem<?>>comparingInt(item -> item.priority)
                        .reversed()
                        .thenComparing(item -> item.createTime)
        );
        this.taskMap = new ConcurrentHashMap<>();
        this.scheduledExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r);
            thread.setName("queue-monitor");
            thread.setDaemon(true);
            return thread;
        });

        startQueueProcessor();
        startQueueMonitor();
    }

    /**
     * 添加任务到队列
     */
    public <T> CompletableFuture<Void> enqueue(
            String taskId,
            T task,
            int priority,
            Consumer<T> processor
    ) {
        QueueItem<T> item = new QueueItem<>(taskId, task, priority, processor);
        if (taskMap.putIfAbsent(taskId, item) != null) {
            throw new IllegalStateException("Task " + taskId + " already exists");
        }
        taskQueue.offer(item);
        log.info("Task {} added to queue with priority {}", taskId, priority);
        return item.future;
    }

    /**
     * 更新任务优先级
     */
    public boolean updatePriority(String taskId, int newPriority) {
        QueueItem<?> item = taskMap.get(taskId);
        if (item != null && item.status == QueueItemStatus.QUEUED) {
            item.priority = newPriority;
            refreshQueue();
            log.info("Updated priority for task {} to {}", taskId, newPriority);
            return true;
        }
        return false;
    }

    /**
     * 取消任务
     */
    public boolean cancel(String taskId) {
        QueueItem<?> item = taskMap.get(taskId);
        if (item != null && (item.status == QueueItemStatus.QUEUED ||
                item.status == QueueItemStatus.RETRY_WAIT)) {
            if (taskQueue.remove(item)) {
                item.status = QueueItemStatus.CANCELLED;
                item.endTime = LocalDateTime.now();
                item.future.cancel(true);
                taskMap.remove(taskId);
                log.info("Task {} cancelled", taskId);
                return true;
            }
        }
        return false;
    }

    /**
     * 获取任务状态
     */
    public Map<String, Object> getTaskStatus(String taskId) {
        QueueItem<?> item = taskMap.get(taskId);
        if (item != null) {
            Map<String, Object> status = new HashMap<>();
            status.put("taskId", item.taskId);
            status.put("status", item.status);
            status.put("statusMessage", item.statusMessage);
            status.put("progress", item.progress);
            status.put("createTime", item.createTime);
            status.put("startTime", item.startTime);
            status.put("endTime", item.endTime);
            status.put("priority", item.priority);
            status.put("retryCount", item.retryCount);
            status.put("attributes", new HashMap<>(item.attributes));
            return status;
        }
        return null;
    }

    /**
     * 启动队列处理器
     */
    private void startQueueProcessor() {
        int processorCount = Runtime.getRuntime().availableProcessors();
        for (int i = 0; i < processorCount; i++) {
            taskExecutor.execute(new QueueProcessor());
        }
    }

    private class QueueProcessor implements Runnable {
        @Override
        public void run() {
            while (running) {
                try {
                    QueueItem<?> item = taskQueue.poll(1, TimeUnit.SECONDS);
                    if (item != null) {
                        processItem(item);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    log.error("Error in queue processor", e);
                }
            }
        }
    }

    private void processItem(QueueItem<?> item) {
        try {
            item.status = QueueItemStatus.PROCESSING;
            item.startTime = LocalDateTime.now();

            processTypedItem(item);

            item.status = QueueItemStatus.COMPLETED;
            item.progress = 100.0;
            item.endTime = LocalDateTime.now();
            item.future.complete(null);
        } catch (Exception e) {
            handleProcessingError(item, e);
        } finally {
            if (item.status != QueueItemStatus.RETRY_WAIT) {
                taskMap.remove(item.taskId);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T> void processTypedItem(QueueItem<T> item) {
        item.processor.accept(item.task);
    }

    private void handleProcessingError(QueueItem<?> item, Exception e) {
        log.error("Error processing task: {}", item.taskId, e);
        if (item.shouldRetry()) {
            scheduleRetry(item);
        } else {
            item.status = QueueItemStatus.ERROR;
            item.statusMessage = e.getMessage();
            item.endTime = LocalDateTime.now();
            item.future.completeExceptionally(e);
        }
    }

    private void scheduleRetry(QueueItem<?> item) {
        item.status = QueueItemStatus.RETRY_WAIT;
        item.retryCount++;
        long delay = CollectionConstants.Http.RETRY_INTERVAL * (1L << (item.retryCount - 1));
        scheduledExecutor.schedule(() -> {
            if (item.status == QueueItemStatus.RETRY_WAIT) {
                item.status = QueueItemStatus.QUEUED;
                taskQueue.offer(item);
            }
        }, delay, TimeUnit.MILLISECONDS);
    }

    /**
     * 启动队列监控
     */
    private void startQueueMonitor() {
        scheduledExecutor.scheduleAtFixedRate(() -> {
            try {
                monitorQueueHealth();
                cleanupCompletedTasks();
            } catch (Exception e) {
                log.error("Error in queue monitor", e);
            }
        }, 1, 1, TimeUnit.MINUTES);
    }

    private void monitorQueueHealth() {
        int queueSize = taskQueue.size();
        int activeTaskCount = (int) taskMap.values().stream()
                .filter(item -> item.status == QueueItemStatus.PROCESSING)
                .count();

        log.info("Queue status - Size: {}, Active tasks: {}", queueSize, activeTaskCount);

        LocalDateTime threshold = LocalDateTime.now().minusHours(1);
        taskMap.values().stream()
                .filter(item -> item.status == QueueItemStatus.QUEUED &&
                        item.createTime.isBefore(threshold))
                .forEach(item ->
                        log.warn("Task {} has been queued for more than 1 hour", item.taskId)
                );
    }

    private void cleanupCompletedTasks() {
        LocalDateTime threshold = LocalDateTime.now().minusHours(24);
        taskMap.entrySet().removeIf(entry -> {
            QueueItem<?> item = entry.getValue();
            return (item.status == QueueItemStatus.COMPLETED ||
                    item.status == QueueItemStatus.ERROR ||
                    item.status == QueueItemStatus.CANCELLED) &&
                    item.endTime != null &&
                    item.endTime.isBefore(threshold);
        });
    }

    private void refreshQueue() {
        List<QueueItem<?>> items = new ArrayList<>();
        taskQueue.drainTo(items);
        taskQueue.addAll(items);
    }

    /**
     * 获取队列统计信息
     */
    public Map<String, Object> getQueueStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("queueSize", taskQueue.size());
        stats.put("activeTaskCount", getActiveTaskCount());
        stats.put("totalTaskCount", taskMap.size());

        Map<QueueItemStatus, Long> statusCounts = new HashMap<>();
        taskMap.values().forEach(item ->
                statusCounts.merge(item.status, 1L, Long::sum)
        );
        stats.put("statusCounts", statusCounts);

        return stats;
    }

    /**
     * 获取队列大小
     */
    public int getQueueSize() {
        return taskQueue.size();
    }

    /**
     * 获取活动任务数
     */
    public int getActiveTaskCount() {
        return (int) taskMap.values().stream()
                .filter(item -> item.status == QueueItemStatus.PROCESSING)
                .count();
    }

    /**
     * 更新任务进度
     */
    public void updateTaskProgress(String taskId, double progress, String message) {
        QueueItem<?> item = taskMap.get(taskId);
        if (item != null) {
            item.progress = progress;
            item.statusMessage = message;
        }
    }

    /**
     * 暂停队列处理
     */
    public void pause() {
        running = false;
    }

    /**
     * 恢复队列处理
     */
    public void resume() {
        running = true;
        startQueueProcessor();
    }

    /**
     * 设置任务属性
     */
    public void setTaskAttribute(String taskId, String key, Object value) {
        QueueItem<?> item = taskMap.get(taskId);
        if (item != null) {
            item.attributes.put(key, value);
        }
    }

    @PreDestroy
    public void shutdown() {
        running = false;
        scheduledExecutor.shutdown();
        try {
            if (!scheduledExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduledExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduledExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        taskMap.values().forEach(item -> {
            if (item.status == QueueItemStatus.QUEUED ||
                    item.status == QueueItemStatus.PROCESSING ||
                    item.status == QueueItemStatus.RETRY_WAIT) {
                item.status = QueueItemStatus.CANCELLED;
                item.endTime = LocalDateTime.now();
                item.future.cancel(true);
            }
        });

        taskQueue.clear();
        taskMap.clear();
    }
}
```

## TaskManager.java

```java
package com.study.collect.business.testcase.core.manager;

import com.study.collect.business.testcase.common.constants.CollectionConstants;
import com.study.collect.business.testcase.model.response.TaskResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 任务管理器
 */
@Slf4j
@Component
public class TaskManager {

    private final ConcurrentHashMap<String, TaskResponse> taskMap;
    private final ConcurrentHashMap<String, ScheduledFuture<?>> timeoutTasks;
    private final ScheduledExecutorService scheduledExecutor;
    private volatile boolean running = true;

    public TaskManager(ScheduledExecutorService scheduledExecutor) {
        this.taskMap = new ConcurrentHashMap<>();
        this.timeoutTasks = new ConcurrentHashMap<>();
        this.scheduledExecutor = scheduledExecutor;

        // 启动定期清理
        startPeriodicCleanup();
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

            if (isTerminalStatus(status)) {
                task.setEndTime(LocalDateTime.now());
                cancelTimeout(taskId);
            }

            if ("PROCESSING".equals(status) && task.getStartTime() == null) {
                task.setStartTime(LocalDateTime.now());
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
     * 获取所有活动任务
     */
    public List<TaskResponse> getActiveTasks() {
        return taskMap.values().stream()
                .filter(task -> !isTerminalStatus(task.getStatus()))
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

    /**
     * 更新任务优先级
     */
    public boolean updateTaskPriority(String taskId, int newPriority) {
        TaskResponse task = taskMap.get(taskId);
        if (task != null && !isTerminalStatus(task.getStatus())) {
            task.setPriority(newPriority);
            return true;
        }
        return false;
    }

    /**
     * 添加任务详情
     */
    public void addTaskDetails(String taskId, Map<String, Object> details) {
        TaskResponse task = taskMap.get(taskId);
        if (task != null) {
            if (task.getDetails() == null) {
                task.setDetails(new ConcurrentHashMap<>());
            }
            task.getDetails().putAll(details);
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
            TaskResponse task = taskMap.get(taskId);
            if (task != null && !isTerminalStatus(task.getStatus())) {
                task.setStatus("TIMEOUT");
                task.setEndTime(LocalDateTime.now());
                task.setMessage("Task timeout after " +
                        CollectionConstants.Process.TASK_TIMEOUT + " seconds");
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

    private void cleanupTasks() {
        if (!running) {
            return;
        }

        try {
            LocalDateTime cutoff = LocalDateTime.now().minusDays(7);
            taskMap.entrySet().removeIf(entry -> {
                TaskResponse task = entry.getValue();
                return task.getEndTime() != null && task.getEndTime().isBefore(cutoff);
            });

            log.debug("Completed task cleanup, remaining tasks: {}", taskMap.size());
        } catch (Exception e) {
            log.error("Error during task cleanup", e);
        }
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
                });
    }

    /**
     * 获取任务总数
     */
    public long getTotalTaskCount() {
        return taskMap.size();
    }

    /**
     * 获取活动任务数
     */
    public long getActiveTaskCount() {
        return taskMap.values().stream()
                .filter(task -> !isTerminalStatus(task.getStatus()))
                .count();
    }

    /**
     * 获取任务统计信息
     */
    public Map<String, Long> getTaskStatistics() {
        Map<String, Long> stats = new HashMap<>();
        taskMap.values().stream()
                .collect(Collectors.groupingBy(
                        TaskResponse::getStatus,
                        Collectors.counting()
                ))
                .forEach((status, count) -> stats.put("status." + status.toLowerCase(), count));

        stats.put("total", getTotalTaskCount());
        stats.put("active", getActiveTaskCount());

        return stats;
    }
}
```

## CollectProcessor.java

```java
package com.study.collect.business.testcase.core.processor;

import com.study.collect.business.testcase.common.utils.StreamProcessor;
import com.study.collect.business.testcase.model.param.CollectParam;
import com.study.collect.business.testcase.repository.UriRepository;
import com.study.collect.business.testcase.service.http.UriHttpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * URI采集处理器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CollectProcessor implements DataProcessor<CollectParam, Long> {

    private final UriHttpService httpService;
    private final UriRepository repository;

    private final Map<String, ProcessorStatus> taskStatusMap = new ConcurrentHashMap<>();

    @Override
    public CompletableFuture<Long> process(CollectParam param) {
        // 初始化处理状态
        ProcessorStatus status = new ProcessorStatus();
        taskStatusMap.put(param.getTaskId(), status);

        return CompletableFuture.supplyAsync(() -> {
            try {
                // 获取版本列表
                List<String> versions = httpService.getAllVersions(param);
                status.update("Getting URIs for versions", 0.2);

                // 获取URI列表
                List<String> allUris = versions.stream()
                        .map(version -> {
                            try {
                                return httpService.getAllUrisForVersion(param, version);
                            } catch (Exception e) {
                                log.error("Error getting URIs for version {}", version, e);
                                return List.<String>of();
                            }
                        })
                        .flatMap(List::stream)
                        .distinct()
                        .collect(Collectors.toList());

                status.update("Getting URI details", 0.4);

                // 获取URI详情
                List<Map<String, Object>> details = httpService.batchGetUriDetails(param, allUris);

                status.update("Saving to database", 0.8);

                // 保存到数据库
                long savedCount = saveToDatabase(param.getRootNode(), details);
                status.update("Completed", 1.0);

                return savedCount;
            } catch (Exception e) {
                String errorMessage = e.getMessage() != null ? e.getMessage() : "Unknown error";
                status.error(errorMessage);
                throw new RuntimeException("Processing failed: " + errorMessage, e);
            }
        });
    }

    private long saveToDatabase(String rootNode, List<Map<String, Object>> details) {
        // 实现保存到数据库的逻辑
//        return repository.batchUpsert(rootNode, details).getModifiedCount();

        // TODO: Optimize the type conversion and method call
        return repository.batchUpsert(rootNode, (List<com.study.collect.business.testcase.entity.UriEntity>) (List<?>) details).getModifiedCount();
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
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * URI仓储实现
 */
@Slf4j
@Repository
public class UriRepository {

    private final MongoTemplate mongoTemplate;

    public UriRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * 批量插入或更新
     */
    public BulkWriteResult batchUpsert(String rootNode, List<UriEntity> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return null;
        }

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
            return collection.bulkWrite(operations, options);
        } catch (Exception e) {
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

        String collectionName = TableNameHelper.getTableName(rootNode);
        List<String> uriHashes = uris.stream()
                .map(TableNameHelper::generateUriHash)
                .collect(Collectors.toList());

        Query query = new Query(Criteria.where("uri_hash").in(uriHashes));
        Update update = new Update()
                .set("is_deleted", true)
                .set("update_time", LocalDateTime.now());

        try {
            return mongoTemplate.updateMulti(query, update, collectionName).getModifiedCount();
        } catch (Exception e) {
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

        String collectionName = TableNameHelper.getTableName(rootNode);
        List<String> uriHashes = uris.stream()
                .map(TableNameHelper::generateUriHash)
                .collect(Collectors.toList());

        Query query = new Query(Criteria.where("uri_hash").in(uriHashes));

        try {
            return mongoTemplate.remove(query, UriEntity.class, collectionName).getDeletedCount();
        } catch (Exception e) {
            log.error("Failed to batch hard delete in collection {}", collectionName, e);
            throw new RuntimeException("Batch hard delete failed", e);
        }
    }

    /**
     * 清理不在列表中的URI（软删除）
     */
    public long softDeleteNotInUriHashes(String rootNode, Set<String> validHashes) {
        String collectionName = TableNameHelper.getTableName(rootNode);
        Query query = new Query(Criteria.where("uri_hash").nin(validHashes)
                .and("is_deleted").is(false));
        Update update = new Update()
                .set("is_deleted", true)
                .set("update_time", LocalDateTime.now());

        try {
            return mongoTemplate.updateMulti(query, update, collectionName).getModifiedCount();
        } catch (Exception e) {
            log.error("Failed to soft delete URIs not in hash set for collection {}", collectionName, e);
            throw new RuntimeException("Soft delete cleanup failed", e);
        }
    }

    /**
     * 清理不在列表中的URI（硬删除）
     */
    public long deleteNotInUriHashes(String rootNode, Set<String> validHashes) {
        String collectionName = TableNameHelper.getTableName(rootNode);
        Query query = new Query(Criteria.where("uri_hash").nin(validHashes));

        try {
            return mongoTemplate.remove(query, UriEntity.class, collectionName).getDeletedCount();
        } catch (Exception e) {
            log.error("Failed to delete URIs not in hash set for collection {}", collectionName, e);
            throw new RuntimeException("Delete cleanup failed", e);
        }
    }

    /**
     * 分页查询
     */
    public Page<UriEntity> findByCondition(QueryParams params) {
        Criteria criteria = new Criteria();

        if (StringUtils.hasText(params.getVersion())) {
            criteria.and("uri_version").is(params.getVersion());
        }
        if (StringUtils.hasText(params.getVersionType())) {
            criteria.and("version_type").is(params.getVersionType());
        }
        if (!params.getIncludeDeleted()) {
            criteria.and("is_deleted").is(false);
        }
        if (params.getOnlyDeleted()) {
            criteria.and("is_deleted").is(true);
        }

        Query query = new Query(criteria).with(params.getPageable());
        String collectionName = TableNameHelper.getTableName(params.getRootNode());

        try {
            long total = mongoTemplate.count(query, UriEntity.class, collectionName);
            List<UriEntity> content = mongoTemplate.find(query, UriEntity.class, collectionName);
            return new PageImpl<>(content, params.getPageable(), total);
        } catch (Exception e) {
            log.error("Failed to query collection {}", collectionName, e);
            throw new RuntimeException("Query failed", e);
        }
    }

    /**
     * 批量查询
     */
    public List<UriEntity> batchQuery(List<String> uris, Function<String, String> rootNodeResolver,
                                      Boolean includeDeleted) {
        if (CollectionUtils.isEmpty(uris)) {
            return new ArrayList<>();
        }

        Map<String, List<String>> groupedUris = uris.stream()
                .collect(Collectors.groupingBy(rootNodeResolver));

        List<UriEntity> results = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : groupedUris.entrySet()) {
            results.addAll(queryByGroup(entry.getKey(), entry.getValue(), includeDeleted));
        }

        return results;
    }

    private List<UriEntity> queryByGroup(String rootNode, List<String> uris, Boolean includeDeleted) {
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

    /**
     * 查询参数对象
     */
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
import com.study.collect.business.testcase.config.TestCaseCollectorProperties;
import com.study.collect.business.testcase.model.param.CollectParam;
import com.study.collect.business.testcase.model.param.PageParam;
import com.study.collect.business.testcase.model.response.PageResponse;
import com.study.collect.business.testcase.model.response.VersionResponse;
import com.study.collect.business.testcase.model.response.parse.HttpResponseParser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
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
    private final ObjectMapper objectMapper;
    private final TestCaseCollectorProperties properties;

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
     * 异步获取版本列表
     */
    public CompletableFuture<PageResponse<VersionResponse>> getVersionsAsync(CollectParam param, PageParam pageParam) {
        String serverUri = param.getServerUri();
        String rootNode = param.getRootNode();

        return CompletableFuture.supplyAsync(() -> {
            try {
                rateLimiter.acquire();
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("rootNode", rootNode);
                requestBody.put("page", pageParam.getPage());
                requestBody.put("size", pageParam.getSize());

                HttpUtil.HttpResponse response = HttpUtil.post(
                        serverUri + "/api/versions",
                        objectMapper.writeValueAsString(requestBody),
                        buildHeaders()
                );

                // 处理响应码
                if (response.getCode() >= 400) {
                    throw new RuntimeException("Failed to get versions: " + versionParser.parseError(response.getBody()));
                }

                return versionParser.parse(response.getBody());
            } catch (Exception e) {
                log.error("Failed to get versions for rootNode: {}", rootNode, e);
                throw new RuntimeException("Failed to get versions", e);
            }
        }, httpExecutor);
    }

    /**
     * 异步获取URI列表
     */
    public CompletableFuture<PageResponse<String>> getUriListAsync(CollectParam param, String version, PageParam pageParam) {
        String serverUri = param.getServerUri();
        return CompletableFuture.supplyAsync(() -> {
            try {
                rateLimiter.acquire();
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("version", version);
                requestBody.put("page", pageParam.getPage());
                requestBody.put("size", pageParam.getSize());

                HttpUtil.HttpResponse response = HttpUtil.post(
                        serverUri + "/api/uris",
                        objectMapper.writeValueAsString(requestBody),
                        buildHeaders()
                );

                if (response.getCode() >= 400) {
                    throw new RuntimeException("Failed to get URIs: " + uriListParser.parseError(response.getBody()));
                }

                return uriListParser.parse(response.getBody());
            } catch (Exception e) {
                log.error("Failed to get URI list for version: {}", version, e);
                throw new RuntimeException("Failed to get URI list", e);
            }
        }, httpExecutor);
    }

    /**
     * 批量获取URI详情
     */
    public CompletableFuture<List<Map<String, Object>>> getUriDetailsAsync(CollectParam param, List<String> uris) {
        if (CollectionUtils.isEmpty(uris)) {
            return CompletableFuture.completedFuture(Collections.emptyList());
        }

        String serverUri = param.getServerUri();
        return CompletableFuture.supplyAsync(() -> {
            try {
                rateLimiter.acquire();
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("uris", uris);

                HttpUtil.HttpResponse response = HttpUtil.post(
                        serverUri + "/api/details",
                        objectMapper.writeValueAsString(requestBody),
                        buildHeaders()
                );

                if (response.getCode() >= 400) {
                    throw new RuntimeException("Failed to get URI details: " + uriDetailParser.parseError(response.getBody()));
                }

                return uriDetailParser.parse(response.getBody());
            } catch (Exception e) {
                log.error("Failed to get URI details for {} URIs", uris.size(), e);
                throw new RuntimeException("Failed to get URI details", e);
            }
        }, httpExecutor);
    }

    /**
     * 同步获取所有版本
     */
    public List<String> getAllVersions(CollectParam param) throws Exception {
        String rootNode = param.getRootNode();
        List<String> allVersions = new ArrayList<>();
        PageParam pageParam = new PageParam(1, CollectionConstants.Process.DEFAULT_BATCH_SIZE);

        try {
            // 获取第一页和总数
            PageResponse<VersionResponse> firstPage = getVersionsAsync(param, pageParam)
                    .get(properties.getHttpReadTimeout(), TimeUnit.MILLISECONDS);

            // 处理第一页
            allVersions.addAll(extractVersions(firstPage));

            // 处理剩余页
            long totalPages = (firstPage.getTotal() + pageParam.getSize() - 1) / pageParam.getSize();
            List<CompletableFuture<Void>> futures = new ArrayList<>();

            for (int page = 2; page <= totalPages; page++) {
                final int currentPage = page;
                CompletableFuture<Void> future = getVersionsAsync(param, new PageParam(currentPage, pageParam.getSize()))
                        .thenAccept(pageResponse -> allVersions.addAll(extractVersions(pageResponse)));
                futures.add(future);
            }

            // 等待所有请求完成
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .get(properties.getTimeout(), TimeUnit.SECONDS);

        } catch (Exception e) {
            log.error("Failed to get all versions for rootNode: {}", rootNode, e);
            throw new RuntimeException("Failed to get all versions", e);
        }

        return allVersions;
    }

    private List<String> extractVersions(PageResponse<VersionResponse> pageResponse) {
        return Optional.ofNullable(pageResponse)
                .map(PageResponse::getItems)
                .orElse(Collections.emptyList())
                .stream()
                .map(VersionResponse::getVersion)
                .collect(Collectors.toList());
    }

    /**
     * 获取版本下的所有URI
     */
    public List<String> getAllUrisForVersion(CollectParam param, String version) throws Exception {
        List<String> allUris = new ArrayList<>();
        PageParam pageParam = new PageParam(1, CollectionConstants.Process.DEFAULT_BATCH_SIZE);

        try {
            // 获取第一页和总数
            PageResponse<String> firstPage = getUriListAsync(param, version, pageParam)
                    .get(properties.getHttpReadTimeout(), TimeUnit.MILLISECONDS);

            allUris.addAll(firstPage.getItems());

            // 处理剩余页
            long totalPages = (firstPage.getTotal() + pageParam.getSize() - 1) / pageParam.getSize();
            List<CompletableFuture<Void>> futures = new ArrayList<>();

            for (int page = 2; page <= totalPages; page++) {
                final int currentPage = page;
                CompletableFuture<Void> future = getUriListAsync(param, version, new PageParam(currentPage, pageParam.getSize()))
                        .thenAccept(pageResponse -> allUris.addAll(pageResponse.getItems()));
                futures.add(future);
            }

            // 等待所有请求完成
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .get(properties.getTimeout(), TimeUnit.SECONDS);

        } catch (Exception e) {
            log.error("Failed to get all URIs for version: {}", version, e);
            throw new RuntimeException("Failed to get all URIs", e);
        }

        return allUris;
    }

    /**
     * 批量处理URI详情
     */
    public List<Map<String, Object>> batchGetUriDetails(CollectParam param, List<String> uris) throws Exception {
        List<Map<String, Object>> allDetails = new ArrayList<>();
        List<List<String>> batches = partition(uris, param.getBatchSize());

        try {
            // 并行处理每个批次
            List<CompletableFuture<List<Map<String, Object>>>> futures = batches.stream()
                    .map(batch -> getUriDetailsAsync(param, batch))
                    .collect(Collectors.toList());

            // 等待所有批次完成
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .get(properties.getTimeout(), TimeUnit.SECONDS);

            // 收集结果
            for (CompletableFuture<List<Map<String, Object>>> future : futures) {
                allDetails.addAll(future.get());
            }

        } catch (Exception e) {
            log.error("Failed to batch get URI details", e);
            throw new RuntimeException("Failed to batch get URI details", e);
        }

        return allDetails;
    }

    private <T> List<List<T>> partition(List<T> list, int size) {
        if (CollectionUtils.isEmpty(list)) {
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
import com.study.collect.business.testcase.core.processor.CollectProcessor;
import com.study.collect.business.testcase.core.processor.DeleteProcessor;
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

import java.util.*;
import java.util.stream.Collectors;

/**
 * URI采集服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UriCollectServiceImpl implements UriCollectService {

    private final UriRepository repository;
    private final CollectProcessor collectProcessor;
    private final DeleteProcessor deleteProcessor;
    private final CollectExecutor collectExecutor;
    private final DeleteExecutor deleteExecutor;
    private final TaskManager taskManager;
    private final QueueManager queueManager;
    private final UriCleanupService cleanupService;

    @Override
    public AsyncResponse<String> collectData(CollectParam param) {
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
            collectProcessor.process(param)
                    .thenAccept(result -> {
                        // 2. 如果是增量同步，执行清理
                        if (param.getIncremental()) {
                            cleanupIncrementalData(param, taskId);
                        }

                        taskManager.updateTaskStatus(
                                taskId,
                                "COMPLETED",
                                String.format("Processed %d URIs", result)
                        );
                    })
                    .exceptionally(throwable -> {
                        handleTaskError(taskId, "Collection failed", throwable);
                        return null;
                    });

        } catch (Exception e) {
            handleTaskError(taskId, "Task processing failed", e);
            throw new RuntimeException("Task processing failed", e);
        }
    }

    private void cleanupIncrementalData(CollectParam param, String taskId) {
        try {
            // 创建清理参数
            UriCleanupService.CleanupParams cleanupParams = UriCleanupService.CleanupParams.builder()
                    .rootNode(param.getRootNode())
                    .uris(param.getUris())
                    .hardDelete(param.getHardDelete())
                    .batchSize(param.getBatchSize())
                    .build();

            // 执行清理
            cleanupService.cleanup(cleanupParams, metrics -> {
                taskManager.updateTaskStatus(
                        taskId,
                        "CLEANING",
                        String.format("Cleaning up data: %.2f%%", metrics.getProgressPercentage())
                );
            }).exceptionally(throwable -> {
                log.error("Cleanup failed for task: {}", taskId, throwable);
                return null;
            });
        } catch (Exception e) {
            log.error("Error during cleanup for task: {}", taskId, e);
        }
    }

    @Override
    public AsyncResponse<Long> deleteData(DeleteParam param) {
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

            deleteProcessor.process(param)
                    .thenAccept(result -> {
                        taskManager.updateTaskStatus(
                                taskId,
                                "COMPLETED",
                                String.format("Deleted %d URIs", result)
                        );
                    })
                    .exceptionally(throwable -> {
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
        UriRepository.QueryParams queryParams = UriRepository.QueryParams.builder()
                .rootNode(param.getRootNode())
                .version(param.getVersion())
                .versionType(param.getVersionType())
                .includeDeleted(param.getIncludeDeleted())
                .onlyDeleted(param.getOnlyDeleted())
                .pageable(PageRequest.of(param.getPage() - 1, param.getSize()))
                .build();

        return repository.findByCondition(queryParams);
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
        return Optional.ofNullable(uri)
                .map(u -> {
                    String[] parts = u.split("/");
                    return parts.length > 0 ? parts[0] : "";
                })
                .orElse("");
    }

    private void handleTaskError(String taskId, String message, Throwable throwable) {
        log.error(message + " - Task: {}", taskId, throwable);
        taskManager.updateTaskStatus(taskId, "ERROR",
                message + ": " + throwable.getMessage());
    }

    private Map<String, Object> buildTaskParams(CollectParam param) {
        Map<String, Object> params = new HashMap<>();
        params.put("rootNode", param.getRootNode());
        params.put("version", param.getVersion());
        params.put("incremental", param.getIncremental());
        params.put("batchSize", param.getBatchSize());
        params.put("serverUri", param.getServerUri());
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

