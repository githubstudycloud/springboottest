package com.study.scheduler.job;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.scheduler.utils.HttpClientUtil;
import lombok.Data;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CollectTaskJob extends BaseJob {
    private static final Logger logger = LoggerFactory.getLogger(CollectTaskJob.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Value("${collect.service.url}")
    private String collectServiceUrl;

    @Override
    protected void doExecute(JobExecutionContext context) throws Exception {
        logger.info("Starting collect task distribution job");

        // 1. 获取所有等待执行的采集任务
        String tasksJson = HttpClientUtil.doGet(collectServiceUrl + "/api/tasks/status/CREATED");
        TaskResponse<List<CollectTask>> response = objectMapper.readValue(
                tasksJson,
                new TypeReference<TaskResponse<List<CollectTask>>>() {
                }
        );

        if (response.getCode() == 200 && response.getData() != null) {
            // 2. 分发任务到各个节点执行
            for (CollectTask task : response.getData()) {
                try {
                    // 发送执行请求
                    HttpClientUtil.doPost(
                            collectServiceUrl + "/api/tasks/" + task.getId() + "/execute",
                            null
                    );
                    logger.info("Distributed task: {}", task.getId());
                } catch (Exception e) {
                    logger.error("Failed to distribute task: " + task.getId(), e);
                }
            }
        }
    }

    @Data
    static class TaskResponse<T> {
        private Integer code;
        private String message;
        private T data;
    }

    @Data
    static class CollectTask {
        private String id;
        private String name;
        private String url;
        private String status;
    }
}