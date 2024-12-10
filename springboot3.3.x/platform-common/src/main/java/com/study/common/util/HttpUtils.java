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