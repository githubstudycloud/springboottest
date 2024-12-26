## Prompt:
http工具类，支持并发重试，记录，多线程，基于springboot3.2.9

## Response:
Let me help create a robust HTTP utility class based on Spring Boot 3.2.9 with the specified features.

HTTP Client Utility

Click to open code

Key features of this HTTP client utility:

1.  Retry mechanism with exponential backoff
2.  Async operations with CompletableFuture
3.  Thread pool management
4.  Request/response logging
5.  Configurable timeouts
6.  Support for batch operations
7.  Header customization

To use this utility, you'll need these dependencies in your pom.xml:

```xml
<dependency>
    <groupId>org.springframework.retry</groupId>
    <artifactId>spring-retry</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-aspects</artifactId>
</dependency>
```

Enable retry support by adding `@EnableRetry` to your application configuration.

## Prompt:
自动支持http和https，免证书，一个工具类可很容易移动使用，支持各类http请求

## Response:
Portable HTTP Client Utility

Click to open code

使用示例:

```java
// 简单GET请求
HttpResponse response = HttpUtil.get("https://api.example.com/data");

// POST请求带headers
Map<String, String> headers = new HashMap<>();
headers.put("Authorization", "Bearer token");
HttpResponse response = HttpUtil.post("https://api.example.com/data", 
    "{\"key\":\"value\"}", headers);

// 异步请求
CompletableFuture<HttpResponse> future = HttpUtil.asyncGet("https://api.example.com/data");
future.thenAccept(response -> System.out.println(response.getBody()));

// 批量请求
List<String> urls = Arrays.asList("url1", "url2", "url3");
List<HttpResponse> responses = HttpUtil.batchGet(urls);
```

## Prompt:
再优化以及注释一下

## Response:
Enhanced HTTP Client Utility

Click to open code

主要优化点：

1.  增加详细注释和文档
2.  增加请求/响应时间统计
3.  优化异常处理和重试逻辑
4.  增加日志记录
5.  增加请求配置参数
6.  优化线程池配置
7.  代码结构更清晰

使用示例：

```java
// 简单GET请求
HttpResponse response = HttpUtil.get("https://api.example.com/data");
System.out.println("Response time: " + response.getResponseTime() + "ms");

// 带headers的POST请求
Map<String, String> headers = new HashMap<>();
headers.put("Authorization", "Bearer token");
HttpResponse response = HttpUtil.post(
    "https://api.example.com/data",
    "{\"key\":\"value\"}",
    headers
);

// 异步请求处理
HttpUtil.asyncGet("https://api.example.com/data")
    .thenAccept(response -> {
        System.out.println("Status: " + response.getCode());
        System.out.println("Body: " + response.getBody());
    })
    .exceptionally(e -> {
        e.printStackTrace();
        return null;
    });
```

