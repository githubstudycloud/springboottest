package com.study.collect.business.testcase.service;

import com.study.collect.business.testcase.model.param.CollectParam;
import com.study.collect.business.testcase.repository.UriRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CollectScheduler {
    private final UriHttpService httpService;
    private final UriRepository repository;
    private final UriCollectService collectService;

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

    private void checkRootNode(String rootNode) {
        // 1. 获取版本列表
        List<VersionInfo> versions = httpService.getVersions(
                getServerUrl(), rootNode, 1, Integer.MAX_VALUE).join();

        // 2. 检查每个版本
        for (VersionInfo version : versions) {
            try {
                checkVersion(rootNode, version);
            } catch (Exception e) {
                log.error("Failed to check version: {}", version.getVersion(), e);
            }
        }
    }

    private void checkVersion(String rootNode, VersionInfo version) {
        // 获取接口URI数量
        int apiCount = httpService.getUriCount(getServerUrl(), version.getVersion()).join();

        // 获取数据库URI数量
        long dbCount = repository.countByVersion(rootNode, version.getVersion());

        if (apiCount != dbCount) {
            log.warn("URI count mismatch for version {}: API={}, DB={}",
                    version.getVersion(), apiCount, dbCount);

            // 触发采集
            CollectParam param = CollectParam.builder()
                    .rootNode(rootNode)
                    .version(version.getVersion())
                    .serverUrl(getServerUrl())
                    .build();

            collectService.collectData(param);
        }
    }
}