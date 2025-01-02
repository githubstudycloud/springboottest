// service/impl/UriCollectServiceImpl.java
package com.study.collect.service.impl;

import com.google.common.collect.Lists;
import com.study.collect.api.response.PageResponse;
import com.study.collect.api.response.VersionResponse;
import com.study.collect.core.constant.VersionType;
import com.study.collect.domain.entity.UriEntity;
import com.study.collect.domain.param.CollectParam;
import com.study.collect.domain.param.PageParam;
import com.study.collect.repository.UriRepository;
import com.study.collect.service.UriCollectService;
import com.study.collect.service.http.UriHttpService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.ObjectPool;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import java.util.concurrent.ExecutorService;
import java.util.*;
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
    
    public UriCollectServiceImpl(
            UriHttpService httpService,
            UriRepository repository,
            ObjectPool<UriEntity> entityPool,
            @Qualifier("collectExecutor") ExecutorService executorService) {
        this.repository = repository;
        this.entityPool = entityPool;
        this.executorService = executorService;
    }

    @Override
    public void collectData(CollectParam param) {
        try {
            // 1. 获取所有版本
            List<String> allVersions = getAllVersions(param.getRootNode());
            
            // 2. 按版本类型分组处理
            Map<String, List<String>> versionGroups = allVersions.stream()
                    .collect(Collectors.groupingBy(this::getVersionType));
            
            // 3. 优先处理主干版本，然后是分支版本
            if (versionGroups.containsKey(VersionType.TRUNK.name())) {
                processVersionGroup(param.getRootNode(), versionGroups.get(VersionType.TRUNK.name()));
            }
            if (versionGroups.containsKey(VersionType.BRANCH.name())) {
                processVersionGroup(param.getRootNode(), versionGroups.get(VersionType.BRANCH.name()));
            }
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
            httpService.getVersions(rootNode, new PageParam(1, PAGE_SIZE));
        
        // 处理第一页
        processVersionPage(firstPage, allVersions);
        
        // 处理剩余页
        long totalPages = (firstPage.getTotal() + PAGE_SIZE - 1) / PAGE_SIZE;
        for (int page = 2; page <= totalPages; page++) {
            PageResponse<VersionResponse> pageResponse = 
                httpService.getVersions(rootNode, new PageParam(page, PAGE_SIZE));
            processVersionPage(pageResponse, allVersions);
        }
        
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
    
    private void processVersionGroup(String rootNode, List<String> versions) {
        // 串行处理每个版本，但版本内部并行处理
        versions.forEach(version -> processVersion(rootNode, version));
    }
    
    private void processVersion(String rootNode, String version) {
        try {
            List<String> allUris = new ArrayList<>();
            // 分页获取所有URI
            PageParam pageParam = new PageParam(1, PAGE_SIZE);
            PageResponse<String> firstPage = httpService.getUriList(version, pageParam);
            allUris.addAll(firstPage.getItems());
            
            long totalPages = (firstPage.getTotal() + PAGE_SIZE - 1) / PAGE_SIZE;
            for (int page = 2; page <= totalPages; page++) {
                PageResponse<String> pageResponse = httpService.getUriList(version, new PageParam(page, PAGE_SIZE));
                allUris.addAll(pageResponse.getItems());
            }
            
            // 使用分片并行处理URI
            Lists.partition(allUris, BATCH_SIZE)
                .parallelStream()
                .forEach(batch -> processBatch(rootNode, version, batch));
                
        } catch (Exception e) {
            log.error("Error processing version: {}", version, e);
            throw new RuntimeException("Version processing failed", e);
        }
    }
    
    private void processBatch(String rootNode, String version, List<String> uriBatch) {
        try {
            List<Map<String, Object>> details = httpService.getUriDetails(uriBatch);
            List<UriEntity> entities = new ArrayList<>(uriBatch.size());
            
            // 使用对象池获取实体对象
            for (Map<String, Object> detail : details) {
                UriEntity entity = null;
                try {
                    entity = entityPool.borrowObject();
                    fillEntity(entity, rootNode, version, detail);
                    entities.add(entity);
                } catch (Exception e) {
                    log.error("Error borrowing object from pool", e);
                    if (entity != null) {
                        entityPool.returnObject(entity);
                    }
                }
            }
            
            // 批量保存
            repository.saveAll(entities);
            
            // 归还对象到对象池
            entities.forEach(entity -> {
                try {
                    entityPool.returnObject(entity);
                } catch (Exception e) {
                    log.error("Error returning object to pool", e);
                }
            });
        } catch (Exception e) {
            log.error("Error processing URI batch", e);
            throw new RuntimeException("Batch processing failed", e);
        }
    }
    
    private void fillEntity(UriEntity entity, String rootNode, String version, Map<String, Object> detail) {
        entity.setUri((String) detail.get("uri"));
        entity.setRootNode(rootNode);
        entity.setVersionType(getVersionType(version));
        entity.setUriVersion(version);
        entity.setDetails(detail);
    }httpService = httpService;
        this.