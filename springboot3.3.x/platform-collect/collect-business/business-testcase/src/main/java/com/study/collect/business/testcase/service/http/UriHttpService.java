package com.study.collect.business.testcase.service.http;

import com.study.collect.business.testcase.constant.CollectionConstants;
import com.study.collect.business.testcase.model.param.CollectParam;
import com.study.collect.business.testcase.model.param.PageParam;
import com.study.collect.business.testcase.model.response.PageResponse;
import com.study.collect.business.testcase.model.response.UriDetail;
import com.study.collect.business.testcase.model.response.VersionInfo;
import com.study.collect.business.testcase.model.response.parse.HttpResponseParser;
import com.study.collect.business.testcase.utils.HttpUtil;
import com.study.collect.business.testcase.utils.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class UriHttpService {

    private final HttpResponseParser<PageResponse<VersionInfo>> versionParser;
    private final HttpResponseParser<List<String>> uriListParser;
    private final HttpResponseParser<List<UriDetail>> uriDetailParser;
    private final HttpResponseParser<Integer> uriCountParser;
    private final RateLimiter rateLimiter;

    @Qualifier("httpExecutor")
    private final ThreadPoolTaskExecutor httpExecutor;

    /**
     * 获取版本列表
     */
    public CompletableFuture<List<VersionInfo>> getVersions(
            String serverUrl, String rootNode, int page, int size) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                rateLimiter.acquire();
                String response = HttpUtil.post(
                        serverUrl + "/api/versions",
                        String.format(
                                "{\"rootNode\":\"%s\",\"page\":%d,\"size\":%d}",
                                rootNode, page, size
                        )
                ).getBody();

                return versionParser.parse(response).getItems();
            } catch (Exception e) {
                log.error("Failed to get versions for rootNode: {}", rootNode, e);
                throw new RuntimeException("Failed to get versions", e);
            }
        }, httpExecutor);
    }

    /**
     * 获取URI列表
     */
    public CompletableFuture<List<String>> getUriList(String serverUrl, String version) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                rateLimiter.acquire();
                String response = HttpUtil.post(
                        serverUrl + "/api/uris",
                        String.format("{\"version\":\"%s\"}", version)
                ).getBody();

                return uriListParser.parse(response);
            } catch (Exception e) {
                log.error("Failed to get URI list for version: {}", version, e);
                throw new RuntimeException("Failed to get URI list", e);
            }
        }, httpExecutor);
    }

    /**
     * 获取URI数量
     */
    public CompletableFuture<Integer> getUriCount(String serverUrl, String version) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                rateLimiter.acquire();
                String response = HttpUtil.post(
                        serverUrl + "/api/uri/count",
                        String.format("{\"version\":\"%s\"}", version)
                ).getBody();

                return uriCountParser.parse(response);
            } catch (Exception e) {
                log.error("Failed to get URI count for version: {}", version, e);
                throw new RuntimeException("Failed to get URI count", e);
            }
        }, httpExecutor);
    }

    /**
     * 批量获取URI详情
     */
    public CompletableFuture<List<UriDetail>> getUriDetails(
            String serverUrl, List<String> uris) {
        if (uris == null || uris.isEmpty()) {
            return CompletableFuture.completedFuture(Collections.emptyList());
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                rateLimiter.acquire();
                StringBuilder jsonBody = new StringBuilder("{\"uris\":[");
                for (int i = 0; i < uris.size(); i++) {
                    if (i > 0) {
                        jsonBody.append(",");
                    }
                    jsonBody.append("\"").append(uris.get(i)).append("\"");
                }
                jsonBody.append("]}");

                String response = HttpUtil.post(
                        serverUrl + "/api/details",
                        jsonBody.toString()
                ).getBody();

                return uriDetailParser.parse(response);
            } catch (Exception e) {
                log.error("Failed to get URI details for {} URIs", uris.size(), e);
                throw new RuntimeException("Failed to get URI details", e);
            }
        }, httpExecutor);
    }
}