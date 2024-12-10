package com.study.scheduler.job;

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
public class CollectRetryJob extends BaseJob {
    private static final Logger logger = LoggerFactory.getLogger(CollectRetryJob.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Value("${collect.service.url}")
    private String collectServiceUrl;

    @Override
    protected void doExecute(JobExecutionContext context) throws Exception {
        logger.info("Starting failed task retry job");

        try {
            // 1. 获取失败的任务
            String failedTasksJson = HttpClientUtil.doGet(collectServiceUrl + "/api/tasks/status/FAILED");
            TaskResponse<List<CollectTask>> response = objectMapper.readValue(
                    failedTasksJson,
                    objectMapper.getTypeFactory().constructParametricType(
                            TaskResponse.class,
                            objectMapper.getTypeFactory().constructCollectionType(List.class, CollectTask.class)
                    )
            );

            if (response.getCode() == 200 && response.getData() != null) {
                // 2. 重试失败的任务
                for (CollectTask task : response.getData()) {
                    if (task.getRetryCount() < task.getMaxRetries()) {
                        try {
                            HttpClientUtil.doPost(
                                    collectServiceUrl + "/api/tasks/" + task.getId() + "/retry",
                                    null
                            );
                            logger.info("Retried task: {}", task.getId());
                        } catch (Exception e) {
                            logger.error("Failed to retry task: " + task.getId(), e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error in retry job execution", e);
            throw e;
        }
    }

    private boolean error(Integer code) {
        return code == null || code != 200;
    }

    @Data
    public static class TaskResponse<T> {
        private Integer code;
        private String message;
        private T data;
    }

    @Data
    public static class CollectTask {
        private String id;
        private String name;
        private String url;
        private String status;
        private Integer retryCount = 0;
        private Integer maxRetries = 3;
    }
}