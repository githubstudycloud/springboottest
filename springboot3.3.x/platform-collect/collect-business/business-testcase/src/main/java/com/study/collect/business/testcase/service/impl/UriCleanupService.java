package com.study.collect.business.testcase.service.impl;

import com.google.common.collect.Lists;
import com.study.collect.business.testcase.model.PageResult;
import com.study.collect.business.testcase.repository.UriRepository;
import com.study.collect.business.testcase.utils.HashUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UriCleanupService {

    private final UriRepository repository;
    private static final int PAGE_SIZE = 2000;
    private static final int DELETE_BATCH_SIZE = 2000;

    @Autowired
    public UriCleanupService(UriRepository repository) {
        this.repository = repository;
    }

    /**
     * 清理不在总列表中的URI数据
     * @param allUris 总的URI列表
     * @param rootNode 根节点
     * @param version 版本
     * @param hardDelete 是否硬删除
     */
    public void cleanupUriData(List<String> allUris, String rootNode, String version, boolean hardDelete) {
        try {
            if(allUris == null || allUris.isEmpty()) {
                log.warn("No URIs provided for cleanup");
                return;
            }

            log.info("Starting URI cleanup process for rootNode: {}, version: {}, total URIs: {}",
                    rootNode, version, allUris.size());

            // 将URI转换为hash集合
            Set<String> allHashSet = allUris.stream()
                    .map(HashUtil::hash)
                    .collect(Collectors.toSet());

            // 分页获取数据库中的数据并进行清理
            int page = 1;
            boolean hasMore = true;
            int totalDeleted = 0;

            while (hasMore) {
                List<String> dbUriHashes = repository.findUriHashesNativeWithPage(
                        rootNode,
                        version,
                        null,
                        page,
                        PAGE_SIZE
                );

                if (dbUriHashes.isEmpty()) {
                    break;
                }

                // 找出需要删除的hash
                List<String> toDeleteHashes = dbUriHashes.stream()
                        .filter(hash -> !allHashSet.contains(hash))
                        .collect(Collectors.toList());

                // 分批删除
                if (!toDeleteHashes.isEmpty()) {
                    List<List<String>> batches = Lists.partition(toDeleteHashes, DELETE_BATCH_SIZE);
                    for (List<String> batch : batches) {
                        try {
                            long deletedCount;
                            if (hardDelete) {
                                deletedCount = repository.batchHardDelete(rootNode, batch, DELETE_BATCH_SIZE);
                            } else {
                                deletedCount = repository.batchSoftDelete(rootNode, batch, DELETE_BATCH_SIZE);
                            }
                            totalDeleted += deletedCount;
                            log.info("Deleted {} URIs in batch, total deleted: {}", deletedCount, totalDeleted);
                        } catch (Exception e) {
                            log.error("Error deleting batch of size: {}", batch.size(), e);
                        }
                    }
                }

                hasMore = dbUriHashes.size() >= PAGE_SIZE;
                page++;
            }

            log.info("Cleanup completed for rootNode: {}, version: {}, total deleted: {}",
                    rootNode, version, totalDeleted);

        } catch (Exception e) {
            log.error("Error during cleanup process for rootNode: {}", rootNode, e);
            throw new RuntimeException("Cleanup process failed", e);
        }
    }

    /**
     * 异步执行清理过程
     */
    @Async
    public CompletableFuture<Void> cleanupUriDataAsync(List<String> allUris,
                                                       String rootNode,
                                                       String version,
                                                       boolean hardDelete) {
        return CompletableFuture.runAsync(() -> {
            cleanupUriData(allUris, rootNode, version, hardDelete);
        }).exceptionally(throwable -> {
            log.error("Async cleanup failed for rootNode: {}", rootNode, throwable);
            throw new RuntimeException("Async cleanup failed", throwable);
        });
    }

    /**
     * 获取特定版本的URI数量
     */
    public long getUriCount(String rootNode, String version) {
        try {
            return repository.countUriHashesNative(rootNode, version, null);
        } catch (Exception e) {
            log.error("Failed to get URI count for rootNode: {} and version: {}", rootNode, version, e);
            throw new RuntimeException("Failed to get URI count", e);
        }
    }

    /**
     * 验证数据完整性
     * 检查数据库中的URI数量是否与提供的URI列表数量匹配
     */
    public boolean validateDataIntegrity(String rootNode, String version, int expectedCount) {
        try {
            long actualCount = getUriCount(rootNode, version);
            boolean isValid = actualCount == expectedCount;

            if (!isValid) {
                log.warn("Data integrity check failed for rootNode: {}, version: {}. " +
                        "Expected: {}, Actual: {}", rootNode, version, expectedCount, actualCount);
            }

            return isValid;
        } catch (Exception e) {
            log.error("Failed to validate data integrity for rootNode: {}", rootNode, e);
            return false;
        }
    }

    /**
     * 检查并返回丢失的URI
     */
    public List<String> findMissingUris(List<String> expectedUris, String rootNode, String version) {
        try {
            Set<String> expectedHashes = expectedUris.stream()
                    .map(HashUtil::hash)
                    .collect(Collectors.toSet());

            List<String> missingUris = new ArrayList<>();
            int page = 1;
            boolean hasMore = true;

            while (hasMore) {
                List<String> dbHashes = repository.findUriHashesNativeWithPage(
                        rootNode,
                        version,
                        null,
                        page,
                        PAGE_SIZE
                );

                if (dbHashes.isEmpty()) {
                    break;
                }

                Set<String> dbHashSet = new HashSet<>(dbHashes);
                expectedHashes.removeAll(dbHashSet);

                hasMore = dbHashes.size() >= PAGE_SIZE;
                page++;
            }

            // 将剩余的hash转换回URI
            return expectedUris.stream()
                    .filter(uri -> expectedHashes.contains(HashUtil.hash(uri)))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Failed to find missing URIs for rootNode: {}", rootNode, e);
            throw new RuntimeException("Failed to find missing URIs", e);
        }
    }

    /**
     * 检查数据库健康状态
     */
    public Map<String, Object> checkDatabaseHealth(String rootNode, String version) {
        Map<String, Object> healthStatus = new HashMap<>();
        try {
            long totalCount = getUriCount(rootNode, version);
            long deletedCount = repository.countUriHashesNative(rootNode, version, true);

            healthStatus.put("totalCount", totalCount);
            healthStatus.put("deletedCount", deletedCount);
            healthStatus.put("activeCount", totalCount - deletedCount);
            healthStatus.put("status", "HEALTHY");

        } catch (Exception e) {
            log.error("Health check failed for rootNode: {}", rootNode, e);
            healthStatus.put("status", "UNHEALTHY");
            healthStatus.put("error", e.getMessage());
        }
        return healthStatus;
    }
}