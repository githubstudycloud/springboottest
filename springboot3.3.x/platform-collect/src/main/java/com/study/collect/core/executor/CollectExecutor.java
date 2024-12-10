package com.study.collect.core.executor;

import com.study.collect.entity.CollectTask;
import com.study.collect.entity.TaskResult;
import com.study.collect.enums.TaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.TimeUnit;

// 采集执行器
@Component
public class CollectExecutor {
    private static final Logger logger = LoggerFactory.getLogger(CollectExecutor.class);

    @Autowired
    private RestTemplate restTemplate;

    public TaskResult execute(CollectTask task) {
        try {
            logger.info("Executing collection task: {}", task.getId());

            // 构建请求头
            HttpHeaders headers = buildHeaders(task.getHeaders());
            HttpEntity<?> requestEntity = new HttpEntity<>(task.getRequestBody(), headers);

            // 发送请求
            ResponseEntity<String> response = restTemplate.exchange(
                    task.getUrl(),
                    HttpMethod.valueOf(task.getMethod()),
                    requestEntity,
                    String.class
            );

            // 处理响应
            return handleResponse(task, response);

        } catch (Exception e) {
            logger.error("Error executing task: " + task.getId(), e);
            return TaskResult.builder()
                    .taskId(task.getId())
                    .status(TaskStatus.FAILED)
                    .message("Execution failed: " + e.getMessage())
                    .build();
        }
    }

    private HttpHeaders buildHeaders(Map<String, String> headerMap) {
        HttpHeaders headers = new HttpHeaders();
        if (headerMap != null) {
            headerMap.forEach(headers::add);
        }
        return headers;
    }

    private TaskResult handleResponse(CollectTask task, ResponseEntity<String> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return TaskResult.builder()
                    .taskId(task.getId())
                    .status(TaskStatus.COMPLETED)
                    .statusCode(response.getStatusCode().value())
                    .responseBody(response.getBody())
                    .build();
        } else {
            return TaskResult.builder()
                    .taskId(task.getId())
                    .status(TaskStatus.FAILED)
                    .statusCode(response.getStatusCode().value())
                    .message("Request failed with status: " + response.getStatusCode())
                    .build();
        }
    }

    public boolean retry(CollectTask task, int maxRetries, long retryInterval) {
        int retryCount = 0;
        while (retryCount < maxRetries) {
            try {
                TimeUnit.MILLISECONDS.sleep(retryInterval);
                TaskResult result = execute(task);
                if (result.isSuccess()) {
                    return true;
                }
                retryCount++;
                logger.warn("Retry {} failed for task {}", retryCount, task.getId());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return false;
    }
}