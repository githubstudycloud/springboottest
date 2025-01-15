package com.study.collect.business.testcase.service.impl;

import com.google.common.collect.Lists;
import com.study.collect.business.testcase.constant.VersionType;
import com.study.collect.business.testcase.entity.UriEntity;
import com.study.collect.business.testcase.model.response.PageResponse;
import com.study.collect.business.testcase.model.response.VersionResponse;
import com.study.collect.business.testcase.model.param.CollectParam;
import com.study.collect.business.testcase.model.param.PageParam;
import com.study.collect.business.testcase.repository.UriRepository;
import com.study.collect.business.testcase.service.UriCollectService;
import com.study.collect.business.testcase.service.http.UriHttpService;
import com.study.collect.business.testcase.utils.HashUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.ObjectPool;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UriCollectServiceImpl implements UriCollectService {
    private final UriHttpService httpService;
    private final UriRepository repository;
    private final ObjectPool<UriEntity> entityPool;
    private final ExecutorService executorService;

    private static final int BATCH_SIZE = 200;
    private static final int PAGE_SIZE = 200;
    private static final int MAX_RETRY = 3;
    private static final long RETRY_INTERVAL = 1000L;

    public UriCollectServiceImpl(
            UriHttpService httpService,
            UriRepository repository,
            ObjectPool<UriEntity> entityPool,
            @Qualifier("collectExecutor") ExecutorService executorService) {
        this.httpService = httpService;
        this.repository = repository;
        this.entityPool = entityPool;
        this.executorService = executorService;
    }

    @Override
    public void collectData(CollectParam param) {
        try {
            // 1. 获取所有版本（分页）
            List<String> allVersions = getAllVersions(param.getRootNode());

            // 2. 按版本类型分组
            Map<String, List<String>> versionGroups = allVersions.stream()
                    .collect(Collectors.groupingBy(this::getVersionType));

            // 3. 如果是增量同步，先进行数据清理
            if (param.getIncremental()) {
                cleanupIncrementalData(param.getRootNode(), allVersions);
            }

            // 4. 优先处理主干版本，然后是分支版本
            if (versionGroups.containsKey(VersionType.TRUNK.name())) {
                processVersionGroup(param.getRootNode(),
                        versionGroups.get(VersionType.TRUNK.name()),
                        param);
            }
            if (versionGroups.containsKey(VersionType.BRANCH.name())) {
                processVersionGroup(param.getRootNode(),
                        versionGroups.get(VersionType.BRANCH.name()),
                        param);
            }

            log.info("Data collection completed for root node: {}", param.getRootNode());
        } catch (Exception e) {
            log.error("Error collecting data for root node: {}", param.getRootNode(), e);
            throw new RuntimeException("Data collection failed", e);
        }
    }

    @Override
    public List<UriEntity> queryUri(String rootNode, String version, String versionType) {
        return repository.findByConditions(rootNode, version, versionType);
    }

    private List<String> getAllVersions(String rootNode) throws IOException {
        List<String> allVersions = new ArrayList<>();
        PageResponse<VersionResponse> firstPage =
                retryWithBackoff(() -> httpService.getVersions(rootNode, new PageParam(1, PAGE_SIZE)));

        // 处理第一页
        processVersionPage(firstPage, allVersions);

        // 计算总页数并处理剩余页
        long totalPages = (firstPage.getTotal() + PAGE_SIZE - 1) / PAGE_SIZE;
        for (int page = 2; page <= totalPages; page++) {
            final int currentPage = page;
            PageResponse<VersionResponse> pageResponse =
                    retryWithBackoff(() -> httpService.getVersions(rootNode, new PageParam(currentPage, PAGE_SIZE)));
            processVersionPage(pageResponse, allVersions);
        }

        log.debug("Retrieved {} versions for root node: {}", allVersions.size(), rootNode);
        return allVersions;
    }

    private void processVersionPage(PageResponse<VersionResponse> pageResponse, List<String> versions) {
        versions.addAll(pageResponse.getItems().stream()
                .map(VersionResponse::getVersion)
                .collect(Collectors.toList()));
    }

    private String getVersionType(String version) {
        // 根据版本号规则判断类型，可以根据实际情况修改
        return version.contains("branch") ? VersionType.BRANCH.name() : VersionType.TRUNK.name();
    }

    private void cleanupIncrementalData(String rootNode, List<String> versions) throws IOException {
        log.info("Starting incremental data cleanup for root node: {}", rootNode);
        Set<String> allUris = new HashSet<>();

        // 获取所有版本的URI
        for (String version : versions) {
            List<String> versionUris = getAllUrisForVersion(version);
            allUris.addAll(versionUris.stream()
                    .map(this::generateUriHash)
                    .collect(Collectors.toSet()));
        }

        // 删除不存在的URI
        repository.deleteByUriHashNotIn(allUris);
        log.info("Completed incremental data cleanup for root node: {}", rootNode);
    }

    private void processVersionGroup(String rootNode, List<String> versions, CollectParam param) {
        log.info("Processing version group for root node: {}, versions count: {}",
                rootNode, versions.size());

        // 串行处理每个版本，但版本内部并行处理
        versions.forEach(version -> {
            try {
                processVersion(rootNode, version, param);
            } catch (Exception e) {
                log.error("Error processing version: {}", version, e);
                // 继续处理其他版本
            }
        });
    }

    private void processVersion(String rootNode, String version, CollectParam param) {
        log.info("Starting to process version: {}", version);
        try {
            List<String> allUris = getAllUrisForVersion(version);

            // 使用分片并行处理URI
            Lists.partition(allUris, BATCH_SIZE)
                    .parallelStream()
                    .forEach(batch -> processBatch(rootNode, version, batch));

            log.info("Completed processing version: {}, processed URI count: {}",
                    version, allUris.size());
        } catch (Exception e) {
            log.error("Error processing version: {}", version, e);
            throw new RuntimeException("Version processing failed", e);
        }
    }

    private List<String> getAllUrisForVersion(String version) throws IOException {
        List<String> allUris = new ArrayList<>();
        PageParam pageParam = new PageParam(1, PAGE_SIZE);

        // 获取第一页和总数
        PageResponse<String> firstPage =
                retryWithBackoff(() -> httpService.getUriList(version, pageParam));
        allUris.addAll(firstPage.getItems());

        // 处理剩余页
        long totalPages = (firstPage.getTotal() + PAGE_SIZE - 1) / PAGE_SIZE;
        for (int page = 2; page <= totalPages; page++) {
            final int currentPage = page;
            PageResponse<String> pageResponse =
                    retryWithBackoff(() -> httpService.getUriList(version, new PageParam(currentPage, PAGE_SIZE)));
            allUris.addAll(pageResponse.getItems());
        }

        return allUris;
    }

    private void processBatch(String rootNode, String version, List<String> uriBatch) {
        List<UriEntity> entities = new ArrayList<>(uriBatch.size());
        List<UriEntity> borrowedEntities = new ArrayList<>(uriBatch.size());

        try {
            // 获取URI详情
            List<Map<String, Object>> details =
                    retryWithBackoff(() -> httpService.getUriDetails(uriBatch));

            // 使用对象池获取实体对象
            for (Map<String, Object> detail : details) {
                UriEntity entity = null;
                try {
                    entity = entityPool.borrowObject();
                    borrowedEntities.add(entity);  // 记录借出的对象
                    fillEntity(entity, rootNode, version, detail);
                    entities.add(entity);
                } catch (Exception e) {
                    log.error("Error borrowing object from pool", e);
                    if (entity != null) {
                        try {
                            entityPool.returnObject(entity);
                            borrowedEntities.remove(entity);
                        } catch (Exception ex) {
                            log.error("Error returning object to pool", ex);
                        }
                    }
                }
            }

            // 批量保存
            repository.saveAll(entities);

        } catch (Exception e) {
            log.error("Error processing URI batch", e);
            throw new RuntimeException("Batch processing failed", e);
        } finally {
            // 确保所有借出的对象都返回池中
            borrowedEntities.forEach(entity -> {
                try {
                    entityPool.returnObject(entity);
                } catch (Exception e) {
                    log.error("Error returning object to pool", e);
                }
            });
        }
    }

    private void fillEntity(UriEntity entity, String rootNode, String version, Map<String, Object> detail) {
        entity.setUri((String) detail.get("uri"));
        entity.setRootNode(rootNode);
        entity.setVersionType(getVersionType(version));
        entity.setUriVersion(version);
        entity.setDetails(detail);
    }

    private String generateUriHash(String uri) {
        return HashUtil.hash(uri);
    }

    private <T> T retryWithBackoff(IOSupplier<T> supplier) throws IOException {
        int retries = 0;
        while (true) {
            try {
                return supplier.get();
            } catch (IOException e) {
                if (++retries == MAX_RETRY) {
                    throw e;
                }
                try {
                    Thread.sleep(RETRY_INTERVAL * (long) Math.pow(2, retries - 1));
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new IOException("Operation interrupted", ie);
                }
            }
        }
    }

    @FunctionalInterface
    private interface IOSupplier<T> {
        T get() throws IOException;
    }
}