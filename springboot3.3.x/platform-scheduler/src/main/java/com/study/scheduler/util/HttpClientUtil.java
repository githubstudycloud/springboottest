package com.study.scheduler.util;

import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
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

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class HttpClientUtil {
    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

    private static CloseableHttpClient httpClient;
    private static PoolingHttpClientConnectionManager connectionManager;
    private static RequestConfig requestConfig;
    private static ScheduledExecutorService connectionMonitorExecutor;
    private static final AtomicBoolean isMonitorRunning = new AtomicBoolean(false);

    // Configuration constants
    private static final int MAX_TOTAL_CONNECTIONS = 500;
    private static final int DEFAULT_MAX_PER_ROUTE = 100;
    private static final int REQUEST_TIMEOUT = 10000;
    private static final int CONNECT_TIMEOUT = 10000;
    private static final int SOCKET_TIMEOUT = 10000;
    private static final int DEFAULT_KEEP_ALIVE_TIME_MILLIS = 20 * 1000;
    private static final int CLOSE_IDLE_CONNECTION_WAIT_TIME_SECS = 30;
    private static final int MONITOR_THREAD_POOL_SIZE = 1;
    private static final int MONITOR_PERIOD_SECONDS = 10;
    private static final int SHUTDOWN_TIMEOUT_SECONDS = 5;

    static {
        try {
            init();
        } catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
            logger.error("Failed to initialize HttpClientUtil", e);
            throw new IllegalStateException("Failed to initialize HttpClientUtil", e);
        }
    }

    private HttpClientUtil() {
        // Utility class, hide constructor
    }

    private static void init() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
        initializeSSLComponents();
        initializeConnectionManager();
        initializeHttpClient();
        startIdleConnectionMonitor();
        logger.info("HttpClientUtil initialized successfully");
    }

    private static void initializeSSLComponents() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
        SSLContext sslContext = createSSLContext();
        HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;
        SSLConnectionSocketFactory sslSocketFactory = createSSLSocketFactory(sslContext, hostnameVerifier);
        Registry<ConnectionSocketFactory> socketFactoryRegistry = createSocketFactoryRegistry(sslSocketFactory);

        connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        connectionManager.setMaxTotal(MAX_TOTAL_CONNECTIONS);
        connectionManager.setDefaultMaxPerRoute(DEFAULT_MAX_PER_ROUTE);
    }

    private static SSLContext createSSLContext() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
        return SSLContextBuilder
                .create()
                .loadTrustMaterial((X509Certificate[] chain, String authType) -> true)
                .build();
    }

    private static SSLConnectionSocketFactory createSSLSocketFactory(SSLContext sslContext, HostnameVerifier hostnameVerifier) {
        return new SSLConnectionSocketFactory(
                sslContext,
                new String[]{"TLSv1", "TLSv1.1", "TLSv1.2", "TLSv1.3"},
                null,
                hostnameVerifier);
    }

    private static Registry<ConnectionSocketFactory> createSocketFactoryRegistry(SSLConnectionSocketFactory sslSocketFactory) {
        return RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", sslSocketFactory)
                .build();
    }

    private static void initializeConnectionManager() {
        requestConfig = RequestConfig.custom()
                .setConnectTimeout(CONNECT_TIMEOUT)
                .setSocketTimeout(SOCKET_TIMEOUT)
                .setConnectionRequestTimeout(REQUEST_TIMEOUT)
                .build();
    }

    private static void initializeHttpClient() {
        ConnectionKeepAliveStrategy keepAliveStrategy = createKeepAliveStrategy();
        httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setKeepAliveStrategy(keepAliveStrategy)
                .setDefaultRequestConfig(requestConfig)
                .build();
    }

    private static ConnectionKeepAliveStrategy createKeepAliveStrategy() {
        return (response, context) -> {
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
    }

    private static void startIdleConnectionMonitor() {
        if (!isMonitorRunning.compareAndSet(false, true)) {
            logger.warn("Connection monitor is already running");
            return;
        }
        createAndScheduleMonitor();
    }

    private static void createAndScheduleMonitor() {
        connectionMonitorExecutor = Executors.newScheduledThreadPool(MONITOR_THREAD_POOL_SIZE, r -> {
            Thread thread = new Thread(r, "connection-monitor");
            thread.setDaemon(true);
            return thread;
        });

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

    private static class ConnectionMonitorTask implements Runnable {
        @Override
        public void run() {
            synchronized (connectionManager) {
                connectionManager.closeExpiredConnections();
                connectionManager.closeIdleConnections(
                        CLOSE_IDLE_CONNECTION_WAIT_TIME_SECS,
                        TimeUnit.SECONDS
                );
            }
            logger.debug("Connection maintenance completed");
        }
    }

    private static void shutdownMonitor() {
        if (!isMonitorRunning.get()) {
            return;
        }

        try {
            connectionMonitorExecutor.shutdown();
            if (!connectionMonitorExecutor.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                connectionMonitorExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            connectionMonitorExecutor.shutdownNow();
            logger.error("Connection monitor shutdown interrupted", e);
        } finally {
            isMonitorRunning.set(false);
        }
    }

    public static String doGet(String url) throws IOException {
        HttpGet httpGet = createGetRequest(url);
        return executeRequest(httpGet);
    }

    private static HttpGet createGetRequest(String url) {
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);
        httpGet.setHeader("User-Agent", "Mozilla/5.0");
        httpGet.setHeader("Accept", "*/*");
        return httpGet;
    }

    public static String doPost(String url, String jsonBody) throws IOException {
        HttpPost httpPost = createPostRequest(url, jsonBody);
        return executeRequest(httpPost);
    }

    private static HttpPost createPostRequest(String url, String jsonBody) {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("User-Agent", "Mozilla/5.0");
        httpPost.setHeader("Accept", "*/*");

        if (jsonBody != null) {
            httpPost.setEntity(new StringEntity(jsonBody, StandardCharsets.UTF_8));
        }
        return httpPost;
    }

    private static String executeRequest(HttpGet request) throws IOException {
        logger.debug("Executing GET request to: {}", request.getURI());
        return executeHttpRequest(request);
    }

    private static String executeRequest(HttpPost request) throws IOException {
        logger.debug("Executing POST request to: {}", request.getURI());
        return executeHttpRequest(request);
    }

    private static String executeHttpRequest(org.apache.http.client.methods.HttpRequestBase request) throws IOException {
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

    public static void close() {
        try {
            logger.info("Shutting down HttpClientUtil...");
            shutdownMonitor();
            closeResources();
            logger.info("HttpClientUtil shut down successfully");
        } catch (IOException e) {
            logger.error("Error while shutting down HttpClientUtil", e);
        }
    }

    private static void closeResources() throws IOException {
        if (httpClient != null) {
            httpClient.close();
        }
        if (connectionManager != null) {
            connectionManager.close();
        }
    }
}