package com.study.scheduler.job;

import com.study.scheduler.utils.HttpClientUtil;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class CollectSyncJob extends BaseJob {
    private static final Logger logger = LoggerFactory.getLogger(CollectSyncJob.class);

    @Value("${collect.service.urls}")
    private Set<String> collectServiceUrls;

    @Override
    protected void doExecute(JobExecutionContext context) throws Exception {
        logger.info("Starting collect service health check and sync");

        for (String serviceUrl : collectServiceUrls) {
            try {
                // 1. 健康检查
                String healthResult = HttpClientUtil.doGet(serviceUrl + "/actuator/health");
                if (!healthResult.contains("UP")) {
                    logger.warn("Collect service unhealthy: {}", serviceUrl);
                    continue;
                }

                // 2. 同步配置
                String configResult = HttpClientUtil.doGet(serviceUrl + "/api/config/sync");
                logger.info("Config sync result for {}: {}", serviceUrl, configResult);

                // 3. 同步任务状态
                String taskResult = HttpClientUtil.doGet(serviceUrl + "/api/tasks/sync");
                logger.info("Task sync result for {}: {}", serviceUrl, taskResult);

            } catch (Exception e) {
                logger.error("Failed to sync with collect service: " + serviceUrl, e);
            }
        }
    }
}
