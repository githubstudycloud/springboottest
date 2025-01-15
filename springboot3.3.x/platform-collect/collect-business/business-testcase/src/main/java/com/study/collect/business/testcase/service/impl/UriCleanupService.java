package com.study.collect.business.testcase.service.impl;

import com.study.collect.business.testcase.model.PageResult;
import com.study.collect.business.testcase.repository.UriRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class UriCleanupService {
    private final UriRepository repository;
    private static final int PAGE_SIZE = 10000;
    private static final int DELETE_BATCH_SIZE = 10000;

    @Autowired
    public UriCleanupService(UriRepository repository) {
        this.repository = repository;
    }

    /**
     * 清理不在总列表中的URI数据
     * @param allUriHashes 总的uriHash列表
     * @param rootNode 根节点
     */
    public void cleanupUriData(List<String> allUriHashes, String rootNode) {
        try {
            log.info("Starting URI cleanup process, total hashes: {}", allUriHashes.size());
            Set<String> allHashSet = new HashSet<>(allUriHashes);
            Set<String> toDeleteHashes = new HashSet<>();

            // 分页查询数据库中的uriHash
            int page = 1;
            PageResult<String> pageResult;
            do {
                pageResult = repository.findUriHashesPage(rootNode, null, null, page, PAGE_SIZE);

                // 找出不在总列表中的hash
                for (String dbHash : pageResult.getItems()) {
                    if (!allHashSet.contains(dbHash)) {
                        toDeleteHashes.add(dbHash);
                    }
                }

                log.info("Processed page {}/{}, found {} hashes to delete",
                        page, pageResult.getTotalPages(), toDeleteHashes.size());
                page++;
            } while (page <= pageResult.getTotalPages());

            // 如果有需要删除的数据，进行批量删除
            if (!toDeleteHashes.isEmpty()) {
                log.info("Starting deletion of {} hashes", toDeleteHashes.size());
                List<String> toDeleteList = new ArrayList<>(toDeleteHashes);

                // 分批删除
                for (int i = 0; i < toDeleteList.size(); i += DELETE_BATCH_SIZE) {
                    int end = Math.min(i + DELETE_BATCH_SIZE, toDeleteList.size());
                    List<String> batch = toDeleteList.subList(i, end);

                    try {
                        repository.deleteByUriHashes(batch);
                        log.info("Deleted batch {}-{} of {}", i, end, toDeleteList.size());
                    } catch (Exception e) {
                        log.error("Error deleting batch {}-{}", i, end, e);
                    }
                }

                log.info("Cleanup completed, deleted {} hashes", toDeleteList.size());
            } else {
                log.info("No hashes need to be deleted");
            }

        } catch (Exception e) {
            log.error("Error during cleanup process", e);
            throw new RuntimeException("Cleanup process failed", e);
        }
    }

    /**
     * 异步执行清理过程
     */
    @Async
    public CompletableFuture<Void> cleanupUriDataAsync(List<String> allUriHashes, String rootNode) {
        return CompletableFuture.runAsync(() -> cleanupUriData(allUriHashes, rootNode));
    }
}