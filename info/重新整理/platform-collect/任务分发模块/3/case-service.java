// Case.java
package com.study.collect.business.enterprise.model;

import com.study.collect.core.storage.entity.VersionEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "cases")
@EqualsAndHashCode(callSuper = true)
public class Case extends VersionEntity {
    private String uri;            // 用例URI
    private String version;        // 版本号
    private String detailData;     // 详情数据JSON字符串
}

// CaseRepository.java
package com.study.collect.business.enterprise.repository;

import com.study.collect.business.enterprise.model.Case;
import com.study.collect.core.storage.repository.IRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface CaseRepository extends IRepository<Case> {
    @Query(value = "{'version': ?0}", fields = "{'uri': 1}")
    List<String> findUrisByVersion(String version);
    
    Page<Case> findByVersionAndUriIn(String version, List<String> uris, Pageable pageable);
}

// CaseService.java
package com.study.collect.business.enterprise.service;

import com.study.collect.business.enterprise.model.Case;
import com.study.collect.business.enterprise.repository.CaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CaseService {
    
    private final CaseRepository caseRepository;
    
    /**
     * 获取指定版本的所有URI
     */
    public List<String> getUrisByVersion(String version) {
        try {
            List<String> uris = caseRepository.findUrisByVersion(version);
            return CollectionUtils.isEmpty(uris) ? Collections.emptyList() : uris;
        } catch (Exception e) {
            log.error("Failed to get URIs for version: {}", version, e);
            return Collections.emptyList();
        }
    }
    
    /**
     * 分页获取用例详情并保存
     */
    public void processCaseDetails(String version, List<String> uris, int pageSize) {
        if (CollectionUtils.isEmpty(uris)) {
            log.warn("No URIs to process for version: {}", version);
            return;
        }
        
        int totalPages = (uris.size() + pageSize - 1) / pageSize;
        for (int i = 0; i < totalPages; i++) {
            int fromIndex = i * pageSize;
            int toIndex = Math.min((i + 1) * pageSize, uris.size());
            List<String> pageUris = uris.subList(fromIndex, toIndex);
            
            try {
                // 模拟获取详情数据
                processAndSaveCases(version, pageUris);
                log.info("Processed page {}/{} for version {}", i + 1, totalPages, version);
            } catch (Exception e) {
                log.error("Failed to process page {}/{} for version {}", i + 1, totalPages, version, e);
            }
        }
    }
    
    private void processAndSaveCases(String version, List<String> uris) {
        for (String uri : uris) {
            try {
                // 模拟获取详情数据
                String detailData = fetchCaseDetail(uri);
                
                Case caseEntity = new Case();
                caseEntity.setUri(uri);
                caseEntity.setVersion(version);
                caseEntity.setDetailData(detailData);
                caseEntity.setCreateTime(LocalDateTime.now());
                
                caseRepository.save(caseEntity);
            } catch (Exception e) {
                log.error("Failed to process case: {}", uri, e);
            }
        }
    }
    
    private String fetchCaseDetail(String uri) {
        // 这里应该是实际的获取详情数据的逻辑
        // 目前返回模拟数据
        return String.format("{\"uri\": \"%s\", \"detail\": \"Sample detail data\"}", uri);
    }
}

// CaseController.java
package com.study.collect.business.enterprise.controller;

import com.study.collect.business.enterprise.service.CaseService;
import com.study.collect.common.model.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cases")
@RequiredArgsConstructor
public class CaseController {

    private final CaseService caseService;
    private static final int PAGE_SIZE = 200;

    @GetMapping("/uris/{version}")
    public Response<List<String>> getUrisByVersion(@PathVariable String version) {
        List<String> uris = caseService.getUrisByVersion(version);
        return Response.success(uris);
    }

    @PostMapping("/process/{version}")
    public Response<Void> processCaseDetails(@PathVariable String version) {
        List<String> uris = caseService.getUrisByVersion(version);
        caseService.processCaseDetails(version, uris, PAGE_SIZE);
        return Response.success(null);
    }
}
