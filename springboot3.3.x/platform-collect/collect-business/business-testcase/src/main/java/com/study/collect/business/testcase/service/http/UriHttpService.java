package com.study.collect.business.testcase.service.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.collect.business.testcase.constant.CollectionConstants;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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

    @Qualifier("httpExecutor")
    private final ThreadPoolTaskExecutor httpExecutor;

    /**
     * 异步获取版本列表
     */
    public CompletableFuture<PageResponse<VersionResponse>> getVersionsAsync(
            CollectParam param, PageParam pageParam) {
        String serverUri = param.getServerUrl();
        String rootNode = param.getRootNode();
        return CompletableFuture.supplyAsync(() -> {
            try {
                rateLimiter.acquire();
                String response = HttpUtil.post(
                        serverUri + "/api/versions",
                        String.format(
                                "{\"rootNode\":\"%s\",\"page\":\"%s\",\"size\":\"%s\"}",
                                rootNode,
                                pageParam.getPage(),
                                pageParam.getSize()
                        )).getBody();
                return versionParser.parse(response);
            } catch (Exception e) {
                log.error("Failed to get versions for rootNode: {}", rootNode, e);
                throw new RuntimeException("Failed to get versions", e);
            }
        }, httpExecutor);
    }

    /**
     * 异步获取URI列表
     */
    public CompletableFuture<PageResponse<String>> getUriListAsync(
            CollectParam param, String version, PageParam pageParam) {
        String serverUri = param.getServerUrl();
        return CompletableFuture.supplyAsync(() -> {
            try {
                rateLimiter.acquire();
                String response = HttpUtil.post(
                        serverUri + "/api/uris",
                        String.format(
                                "{\"version\":\"%s\",\"page\":\"%s\",\"size\":\"%s\"}",
                                version,
                                pageParam.getPage(),
                                pageParam.getSize()
                        )
                ).getBody();
                return uriListParser.parse(response);
            } catch (Exception e) {
                log.error("Failed to get URI list for version: {}", version, e);
                throw new RuntimeException("Failed to get URI list", e);
            }
        }, httpExecutor);
    }

    /**
     * 批量获取URI详情
     */
    public CompletableFuture<List<Map<String, Object>>> getUriDetailsAsync(
            CollectParam param, List<String> uris) {
        String serverUri = param.getServerUrl();
        if (uris == null || uris.isEmpty()) {
            return CompletableFuture.completedFuture(Collections.emptyList());
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                rateLimiter.acquire();
                String response = HttpUtil.post(
                        serverUri + "/api/details",
                        "{\"uris\":" + new ObjectMapper().writeValueAsString(uris) + "}"
                ).getBody();
                return uriDetailParser.parse(response);
            } catch (Exception e) {
                log.error("Failed to get URI details for {} URIs", uris.size(), e);
                throw new RuntimeException("Failed to get URI details", e);
            }
        }, httpExecutor);
    }

    /**
     * 同步获取所有版本
     */
    public List<String> getAllVersions(CollectParam param) throws IOException {
        String rootNode = param.getRootNode();
        List<String> allVersions = new ArrayList<>();
        PageParam pageParam = new PageParam(1, CollectionConstants.DEFAULT_BATCH_SIZE);

        try {
            // 获取第一页和总数
            PageResponse<VersionResponse> firstPage = getVersionsAsync(param, pageParam)
                    .get(30, TimeUnit.SECONDS);

            // 处理第一页
            processVersionPage(firstPage, allVersions);

            // 处理剩余页
            long totalPages = (firstPage.getTotal() + pageParam.getSize() - 1) / pageParam.getSize();
            List<CompletableFuture<Void>> futures = new ArrayList<>();

            for (int page = 2; page <= totalPages; page++) {
                final int currentPage = page;
                CompletableFuture<Void> future = getVersionsAsync(
                        param, new PageParam(currentPage, pageParam.getSize())
                ).thenAccept(pageResponse -> processVersionPage(pageResponse, allVersions));

                futures.add(future);
            }

            // 等待所有请求完成
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .get(5, TimeUnit.MINUTES);

        } catch (Exception e) {
            log.error("Failed to get all versions for rootNode: {}", rootNode, e);
            throw new IOException("Failed to get all versions", e);
        }

        return allVersions;
    }

    /**
     * 获取版本下的所有URI
     */
    public List<String> getAllUrisForVersion(CollectParam param, String version) throws IOException {
        List<String> allUris = new ArrayList<>();
        PageParam pageParam = new PageParam(1, CollectionConstants.DEFAULT_BATCH_SIZE);

        try {
            // 获取第一页和总数
            PageResponse<String> firstPage = getUriListAsync(param, version, pageParam)
                    .get(30, TimeUnit.SECONDS);

            allUris.addAll(firstPage.getItems());

            // 处理剩余页
            long totalPages = (firstPage.getTotal() + pageParam.getSize() - 1) / pageParam.getSize();
            List<CompletableFuture<Void>> futures = new ArrayList<>();

            for (int page = 2; page <= totalPages; page++) {
                final int currentPage = page;
                CompletableFuture<Void> future = getUriListAsync(
                        param, version, new PageParam(currentPage, pageParam.getSize())
                ).thenAccept(pageResponse -> allUris.addAll(pageResponse.getItems()));

                futures.add(future);
            }

            // 等待所有请求完成
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .get(5, TimeUnit.MINUTES);

        } catch (Exception e) {
            log.error("Failed to get all URIs for version: {}", version, e);
            throw new IOException("Failed to get all URIs", e);
        }

        return allUris;
    }

    private void processVersionPage(PageResponse<VersionResponse> pageResponse, List<String> versions) {
        if (pageResponse != null && pageResponse.getItems() != null) {
            versions.addAll(pageResponse.getItems().stream()
                    .map(VersionResponse::getVersion)
                    .collect(Collectors.toList()));
        }
    }

    /**
     * 批量处理URI详情
     */
    public List<Map<String, Object>> batchGetUriDetails(
            CollectParam param, List<String> uris, int batchSize) throws IOException {
        List<Map<String, Object>> allDetails = new ArrayList<>();
        List<List<String>> batches = new ArrayList<>();

        // 分批
        for (int i = 0; i < uris.size(); i += batchSize) {
            batches.add(uris.subList(i, Math.min(i + batchSize, uris.size())));
        }

        try {
            // 并行处理每个批次
            List<CompletableFuture<List<Map<String, Object>>>> futures = batches.stream()
                    .map(batch -> getUriDetailsAsync(param, batch))
                    .collect(Collectors.toList());

            // 等待所有批次完成
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .get(5, TimeUnit.MINUTES);

            // 收集结果
            for (CompletableFuture<List<Map<String, Object>>> future : futures) {
                allDetails.addAll(future.get());
            }

        } catch (Exception e) {
            log.error("Failed to batch get URI details", e);
            throw new IOException("Failed to batch get URI details", e);
        }

        return allDetails;
    }
}