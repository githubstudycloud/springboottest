我来写一个最完整、最可靠的版本，包含了所有必要的配置和完整的错误处理：

```java
package com.study.scheduler.utils;

import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpHost;
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
import org.apache.http.impl.client.HttpClientBuilder;
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
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

public class HttpClientUtil {
    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

    private static CloseableHttpClient httpClient;
    private static PoolingHttpClientConnectionManager connectionManager;
    private static RequestConfig requestConfig;

    // 连接池配置参数
    private static final int MAX_TOTAL_CONNECTIONS = 500;
    private static final int DEFAULT_MAX_PER_ROUTE = 100;
    private static final int REQUEST_TIMEOUT = 10000;
    private static final int CONNECT_TIMEOUT = 10000;
    private static final int SOCKET_TIMEOUT = 10000;
    private static final int DEFAULT_KEEP_ALIVE_TIME_MILLIS = 20 * 1000;
    private static final int CLOSE_IDLE_CONNECTION_WAIT_TIME_SECS = 30;

    static {
        try {
            init();
        } catch (Exception e) {
            logger.error("Failed to initialize HttpClientUtil", e);
            throw new RuntimeException("Failed to initialize HttpClientUtil", e);
        }
    }

    private static void init() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        // 创建SSL上下文
        SSLContext sslContext = SSLContextBuilder
                .create()
                .loadTrustMaterial(new TrustStrategy() {
                    @Override
                    public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        return true;
                    }
                })
                .build();

        // 创建主机名验证器
        HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;

        // 注册SSL连接工厂
        SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(
                sslContext,
                new String[]{"TLSv1", "TLSv1.1", "TLSv1.2", "TLSv1.3"},
                null,
                hostnameVerifier);

        // 注册http和https协议
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", sslSocketFactory)
                .build();

        // 创建连接管理器
        connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        connectionManager.setMaxTotal(MAX_TOTAL_CONNECTIONS);
        connectionManager.setDefaultMaxPerRoute(DEFAULT_MAX_PER_ROUTE);

        // 创建请求配置
        requestConfig = RequestConfig.custom()
                .setConnectTimeout(CONNECT_TIMEOUT)
                .setSocketTimeout(SOCKET_TIMEOUT)
                .setConnectionRequestTimeout(REQUEST_TIMEOUT)
                .build();

        // 创建保活策略
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

        // 创建HttpClient
        httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setKeepAliveStrategy(keepAliveStrategy)
                .setDefaultRequestConfig(requestConfig)
                .setSSLSocketFactory(sslSocketFactory)
                .setSSLContext(sslContext)
                .setSSLHostnameVerifier(hostnameVerifier)
                .build();

        // 启动定时清理空闲连接的线程
        startIdleConnectionMonitor();

        logger.info("HttpClientUtil initialized successfully");
    }

    private static void startIdleConnectionMonitor() {
        Thread monitorThread = new Thread(() -> {
            while (true) {
                try {
                    synchronized (connectionManager) {
                        TimeUnit.SECONDS.sleep(10);
                        // 关闭过期的连接
                        connectionManager.closeExpiredConnections();
                        // 关闭空闲的连接
                        connectionManager.closeIdleConnections(CLOSE_IDLE_CONNECTION_WAIT_TIME_SECS, TimeUnit.SECONDS);
                    }
                } catch (InterruptedException ex) {
                    break;
                } catch (Exception ex) {
                    logger.error("Exception while closing expired connections", ex);
                }
            }
        });
        monitorThread.setDaemon(true);
        monitorThread.start();
    }

    public static String doGet(String url) throws IOException {
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);

        // 添加通用头信息
        httpGet.setHeader("User-Agent", "Mozilla/5.0");
        httpGet.setHeader("Accept", "*/*");

        logger.debug("Executing GET request to: {}", url);
        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

            logger.debug("Response status code: {}", statusCode);
            logger.debug("Response body: {}", responseBody);

            if (statusCode >= 200 && statusCode < 300) {
                return responseBody;
            } else {
                throw new IOException("Unexpected response status: " + statusCode + ", body: " + responseBody);
            }
        } catch (IOException e) {
            logger.error("Failed to execute GET request to {}", url, e);
            throw e;
        }
    }

    public static String doPost(String url, String jsonBody) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);

        // 添加通用头信息
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("User-Agent", "Mozilla/5.0");
        httpPost.setHeader("Accept", "*/*");

        if (jsonBody != null) {
            StringEntity entity = new StringEntity(jsonBody, StandardCharsets.UTF_8);
            httpPost.setEntity(entity);
        }

        logger.debug("Executing POST request to: {}", url);
        logger.debug("Request body: {}", jsonBody);

        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

            logger.debug("Response status code: {}", statusCode);
            logger.debug("Response body: {}", responseBody);

            if (statusCode >= 200 && statusCode < 300) {
                return responseBody;
            } else {
                throw new IOException("Unexpected response status: " + statusCode + ", body: " + responseBody);
            }
        } catch (IOException e) {
            logger.error("Failed to execute POST request to {}", url, e);
            throw e;
        }
    }

    public static void close() {
        try {
            logger.info("Shutting down HttpClientUtil...");
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
}

```

使用方式：

```java
// 1. 在程序主类中
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        // 开启SSL调试（可选）
        System.setProperty("javax.net.debug", "ssl,handshake");
        SpringApplication.run(Application.class, args);
    }
}

// 2. 在需要使用的地方
try {
    String response = HttpClientUtil.doGet("https://your-nginx-server/api");
    System.out.println(response);
} catch (Exception e) {
    e.printStackTrace();
}

// 3. 在配置文件中
@Configuration
public class AppConfig {
    @PreDestroy
    public void onShutdown() {
        HttpClientUtil.close();
    }
}
```

在 application.yml 中添加日志配置：

```yaml
logging:
  level:
    com.study.scheduler.utils: DEBUG
    org.apache.http: DEBUG
    javax.net: DEBUG
```

这个版本的特点：

1. 完整的 SSL 配置
    - 支持所有 TLS 版本
    - 完全禁用证书验证
    - 禁用主机名验证

2. 优化的连接池
    - 更大的连接池容量
    - 更合理的超时设置
    - 自动清理过期连接

3. 完整的错误处理
    - 详细的日志记录
    - 清晰的异常信息
    - 状态码检查

4. 保活机制
    - 连接复用
    - 定时清理
    - Keep-Alive 支持

如果还遇到问题，请：
1. 检查启用了DEBUG日志
2. 查看具体的错误信息
3. 确认URL是否正确（包括协议、端口等）
4. 用curl测试验证问题：
```bash
curl -v -k https://your-nginx-server/api
```