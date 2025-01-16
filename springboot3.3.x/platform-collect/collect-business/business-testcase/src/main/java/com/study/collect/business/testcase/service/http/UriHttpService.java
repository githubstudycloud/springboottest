package com.study.collect.business.testcase.service.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.collect.business.testcase.common.constants.CollectionConstants;
import com.study.collect.business.testcase.config.TestCaseCollectorProperties;
import com.study.collect.business.testcase.model.param.CollectParam;
import com.study.collect.business.testcase.model.param.PageParam;
import com.study.collect.business.testcase.model.response.PageResponse;
import com.study.collect.business.testcase.model.response.VersionResponse;
import com.study.collect.business.testcase.model.response.parse.HttpResponseParser;
import com.study.collect.business.testcase.utils.HttpUtil;
import com.study.collect.business.testcase.utils.RateLimiter;
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