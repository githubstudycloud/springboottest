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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
            String serverUri,
            String rootNode,
            PageParam pageParam
    ) {
        return CompletableFuture.supplyAsync(() -> {
            final Timer.Sample timer = Timer.start(meterRegistry);
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

                // 处理响应
                handleResponse(response);
                recordMetrics("versions", timer);

                return versionParser.parse(response.getBody());
            } catch (Exception e) {
                recordError("versions");
                log.error("Failed to get versions for rootNode: {}", rootNode, e);
                throw new RuntimeException("Failed to get versions", e);
            }
        }, httpExecutor);
    }

    /**
     * 获取单个版本的所有URI（不分页）
     */
    public List<String> getAllUrisForVersion(String serverUri, String version) throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("version", version);
        requestBody.put("page", 1);
        requestBody.put("size", Integer.MAX_VALUE);  // 一次性获取所有URI

        final Timer.Sample timer = Timer.start(meterRegistry);
        try {
            rateLimiter.acquire();
            HttpUtil.HttpResponse response = HttpUtil.post(
                    serverUri + "/api/uris",
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
            String serverUri,
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
                        serverUri + "/api/details",
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