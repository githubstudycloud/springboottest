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