//package com.study.common.util;
//
//import com.github.tomakehurst.wiremock.WireMockServer;
//import org.junit.jupiter.api.*;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//import java.io.IOException;
//import java.time.Duration;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.ExecutionException;
//
//import static com.github.tomakehurst.wiremock.client.WireMock.*;
//import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
//import static org.junit.jupiter.api.Assertions.*;
//
//@ExtendWith(SpringExtension.class)
//@SpringBootTest
//class HttpUtilsTest {
//    private static WireMockServer wireMockServer;
//    private static final int PORT = 8089;
//    private static final String BASE_URL = "http://localhost:" + PORT;
//
//    @BeforeAll
//    static void setUp() {
//        wireMockServer = new WireMockServer(wireMockConfig().port(PORT));
//        wireMockServer.start();
//        configureFor("localhost", PORT);
//    }
//
//    @AfterAll
//    static void tearDown() {
//        wireMockServer.stop();
//    }
//
//    @BeforeEach
//    void setupTest() {
//        wireMockServer.resetAll();
//    }
//
//    @Test
//    @DisplayName("测试GET请求 - 基本功能")
//    void testGetRequest() throws IOException, InterruptedException {
//        // 准备测试数据
//        TestResponse expectedResponse = new TestResponse("success", 200);
//        String responseJson = JsonUtils.toJson(expectedResponse);
//
//        // 配置Mock服务器
//        stubFor(get(urlEqualTo("/api/test"))
//                .willReturn(aResponse()
//                        .withHeader("Content-Type", "application/json")
//                        .withBody(responseJson)));
//
//        // 执行测试
//        TestResponse response = HttpUtils.get(BASE_URL + "/api/test", TestResponse.class);
//
//        // 验证结果
//        assertNotNull(response);
//        assertEquals("success", response.getMessage());
//        assertEquals(200, response.getCode());
//
//        // 验证请求
//        verify(getRequestedFor(urlEqualTo("/api/test")));
//    }
//
//    @Test
//    @DisplayName("测试GET请求 - 带请求头")
//    void testGetRequestWithHeaders() throws IOException, InterruptedException {
//        // 准备测试数据
//        Map<String, String> headers = new HashMap<>();
//        headers.put("Authorization", "Bearer test-token");
//        headers.put("Custom-Header", "test-value");
//
//        // 配置Mock服务器
//        stubFor(get(urlEqualTo("/api/test-headers"))
//                .withHeader("Authorization", equalTo("Bearer test-token"))
//                .withHeader("Custom-Header", equalTo("test-value"))
//                .willReturn(aResponse()
//                        .withStatus(200)
//                        .withHeader("Content-Type", "application/json")
//                        .withBody("{\"message\":\"success\",\"code\":200}")));
//
//        // 执行测试
//        TestResponse response = HttpUtils.get(
//                BASE_URL + "/api/test-headers",
//                TestResponse.class,
//                headers
//        );
//
//        // 验证结果
//        assertNotNull(response);
//        assertEquals("success", response.getMessage());
//
//        // 验证请求头
//        verify(getRequestedFor(urlEqualTo("/api/test-headers"))
//                .withHeader("Authorization", equalTo("Bearer test-token"))
//                .withHeader("Custom-Header", equalTo("test-value")));
//    }
//
//    @Test
//    @DisplayName("测试POST请求 - 基本功能")
//    void testPostRequest() throws IOException, InterruptedException {
//        // 准备测试数据
//        TestRequest request = new TestRequest("test data", 1);
//        TestResponse expectedResponse = new TestResponse("success", 200);
//
//        // 配置Mock服务器
//        stubFor(post(urlEqualTo("/api/test"))
//                .withRequestBody(containing("test data"))
//                .willReturn(aResponse()
//                        .withStatus(200)
//                        .withHeader("Content-Type", "application/json")
//                        .withBody(JsonUtils.toJson(expectedResponse))));
//
//        // 执行测试
//        TestResponse response = HttpUtils.post(
//                BASE_URL + "/api/test",
//                request,
//                TestResponse.class
//        );
//
//        // 验证结果
//        assertNotNull(response);
//        assertEquals("success", response.getMessage());
//        assertEquals(200, response.getCode());
//
//        // 验证请求
//        verify(postRequestedFor(urlEqualTo("/api/test"))
//                .withRequestBody(containing("test data")));
//    }
//
//    @Test
//    @DisplayName("测试异步GET请求")
//    void testAsyncGetRequest() throws ExecutionException, InterruptedException {
//        // 配置Mock服务器
//        stubFor(get(urlEqualTo("/api/async"))
//                .willReturn(aResponse()
//                        .withStatus(200)
//                        .withHeader("Content-Type", "application/json")
//                        .withBody("{\"message\":\"async success\",\"code\":200}")));
//
//        // 执行测试
//        CompletableFuture<TestResponse> future = HttpUtils.getAsync(
//                BASE_URL + "/api/async",
//                TestResponse.class
//        );
//
//        // 等待结果
//        TestResponse response = future.get();
//
//        // 验证结果
//        assertNotNull(response);
//        assertEquals("async success", response.getMessage());
//        assertEquals(200, response.getCode());
//    }
//
//    @Test
//    @DisplayName("测试异步POST请求")
//    void testAsyncPostRequest() throws ExecutionException, InterruptedException {
//        // 准备测试数据
//        TestRequest request = new TestRequest("async test", 2);
//
//        // 配置Mock服务器
//        stubFor(post(urlEqualTo("/api/async"))
//                .willReturn(aResponse()
//                        .withStatus(200)
//                        .withHeader("Content-Type", "application/json")
//                        .withBody("{\"message\":\"async success\",\"code\":200}")));
//
//        // 执行测试
//        CompletableFuture<TestResponse> future = HttpUtils.postAsync(
//                BASE_URL + "/api/async",
//                request,
//                TestResponse.class
//        );
//
//        // 等待结果
//        TestResponse response = future.get();
//
//        // 验证结果
//        assertNotNull(response);
//        assertEquals("async success", response.getMessage());
//    }
//
//    @Test
//    @DisplayName("测试请求超时")
//    void testRequestTimeout() {
//        // 配置Mock服务器返回延迟响应
//        stubFor(get(urlEqualTo("/api/timeout"))
//                .willReturn(aResponse()
//                        .withStatus(200)
//                        .withFixedDelay(3000))); // 3秒延迟
//
//        // 设置1秒超时
//        Duration shortTimeout = Duration.ofSeconds(1);
//
//        // 验证超时异常
//        assertThrows(IOException.class, () ->
//                HttpUtils.get(BASE_URL + "/api/timeout", String.class, null, shortTimeout));
//    }
//
//    @Test
//    @DisplayName("测试错误响应处理")
//    void testErrorResponse() {
//        // 配置Mock服务器返回错误响应
//        stubFor(get(urlEqualTo("/api/error"))
//                .willReturn(aResponse()
//                        .withStatus(404)
//                        .withBody("{\"error\":\"Not Found\"}")));
//
//        // 验证异常
//        HttpUtils.HttpResponseException exception = assertThrows(
//                HttpUtils.HttpResponseException.class,
//                () -> HttpUtils.get(BASE_URL + "/api/error", TestResponse.class)
//        );
//
//        assertEquals(404, exception.getStatusCode());
//        assertTrue(exception.getResponseBody().contains("Not Found"));
//    }
//
//    @Test
//    @DisplayName("测试批量请求")
//    void testBatchRequests() throws ExecutionException, InterruptedException {
//        // 配置Mock服务器
//        stubFor(get(urlPathMatching("/api/batch/\\d+"))
//                .willReturn(aResponse()
//                        .withStatus(200)
//                        .withHeader("Content-Type", "application/json")
//                        .withBody("{\"message\":\"batch success\",\"code\":200}")));
//
//        // 创建多个异步请求
//        List<CompletableFuture<TestResponse>> futures = List.of(
//                HttpUtils.getAsync(BASE_URL + "/api/batch/1", TestResponse.class),
//                HttpUtils.getAsync(BASE_URL + "/api/batch/2", TestResponse.class),
//                HttpUtils.getAsync(BASE_URL + "/api/batch/3", TestResponse.class)
//        );
//
//        // 等待所有请求完成
//        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
//
//        // 验证结果
//        for (CompletableFuture<TestResponse> future : futures) {
//            TestResponse response = future.get();
//            assertNotNull(response);
//            assertEquals("batch success", response.getMessage());
//            assertEquals(200, response.getCode());
//        }
//    }
//
//    // 测试用的请求/响应类
//    private static class TestRequest {
//        private String data;
//        private Integer id;
//
//        public TestRequest(String data, Integer id) {
//            this.data = data;
//            this.id = id;
//        }
//
//        public String getData() {
//            return data;
//        }
//
//        public void setData(String data) {
//            this.data = data;
//        }
//
//        public Integer getId() {
//            return id;
//        }
//
//        public void setId(Integer id) {
//            this.id = id;
//        }
//    }
//
//    private static class TestResponse {
//        private String message;
//        private Integer code;
//
//        public TestResponse() {
//        }
//
//        public TestResponse(String message, Integer code) {
//            this.message = message;
//            this.code = code;
//        }
//
//        public String getMessage() {
//            return message;
//        }
//
//        public void setMessage(String message) {
//            this.message = message;
//        }
//
//        public Integer getCode() {
//            return code;
//        }
//
//        public void setCode(Integer code) {
//            this.code = code;
//        }
//    }
//}
