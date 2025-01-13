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
