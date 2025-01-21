package com.study.collect.business.testcase.service;

import com.study.collect.business.testcase.model.param.CollectParam;
import com.study.collect.business.testcase.model.response.PageResponse;
import com.study.collect.business.testcase.model.response.VersionInfo;
import com.study.collect.business.testcase.repository.UriRepository;
import com.study.collect.business.testcase.service.http.UriHttpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
@RequiredArgsConstructor
public class CollectScheduler {
    private final UriHttpService httpService;
    private final UriRepository uriRepository;
    private final UriCollectService collectService;

    @Value("${collect.server.url}")
    private String serverUrl;

    @Value("${collect.root-nodes}")
    private List<String> configuredRootNodes = new ArrayList<>();

    @Scheduled(cron = "${collect.check.cron:0 0 * * * *}") // 默认每小时执行
    public void checkCollectStatus() {
        log.info("Starting collect status check");

        try {
            // 获取所有rootNode的配置
            List<String> rootNodes = getRootNodes();

            for (String rootNode : rootNodes) {
                try {
                    checkRootNode(rootNode);
                } catch (Exception e) {
                    log.error("Failed to check rootNode: {}", rootNode, e);
                }
            }
        } catch (Exception e) {
            log.error("Collect status check failed", e);
        }
    }

    private List<String> getRootNodes() {
        // 如果配置了root nodes，则使用配置的值
        if (!configuredRootNodes.isEmpty()) {
            return configuredRootNodes;
        }

        // 否则从数据库或其他来源获取root nodes
        return getDefaultRootNodes();
    }

    private List<String> getDefaultRootNodes() {
        // 实现从数据库或其他来源获取root nodes的逻辑
        // 这里仅作示例，实际实现需要根据具体需求修改
        return new ArrayList<>();
    }

    private void checkRootNode(String rootNode) throws ExecutionException, InterruptedException {
        // 1. 获取版本列表
        List<VersionInfo> versionsResponse = httpService.getVersions(
                serverUrl, rootNode, 1, Integer.MAX_VALUE).get();

        if (versionsResponse == null ) {
            log.warn("No versions found for rootNode: {}", rootNode);
            return;
        }

        List<VersionInfo> versions = versionsResponse;

        // 2. 检查每个版本
        for (VersionInfo version : versions) {
            try {
                checkVersion(rootNode, version);
            } catch (Exception e) {
                log.error("Failed to check version: {} for rootNode: {}",
                        version.getVersion(), rootNode, e);
            }
        }
    }

    private void checkVersion(String rootNode, VersionInfo version)
            throws ExecutionException, InterruptedException {
        // 获取接口URI数量
        int apiCount = httpService.getUriCount(serverUrl, version.getVersion()).get();

        // 获取数据库URI数量
        long dbCount = uriRepository.countByRootNodeAndVersion(rootNode, version.getVersion());

        if (apiCount != dbCount) {
            log.warn("URI count mismatch for version {}: API={}, DB={}",
                    version.getVersion(), apiCount, dbCount);

            // 触发采集
            CollectParam param = CollectParam.builder()
                    .rootNode(rootNode)
                    .version(version.getVersion())
                    .serverUrl(serverUrl)
                    .build();

            collectService.collectData(param);
        } else {
            log.info("URI count match for version {}: count={}",
                    version.getVersion(), apiCount);
        }
    }
}