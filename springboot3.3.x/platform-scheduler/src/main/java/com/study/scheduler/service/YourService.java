package com.study.scheduler.service;

import com.study.common.model.task.HttpConfig;
import com.study.common.model.task.TaskDefinition;
import com.study.common.model.task.TaskResult;
import com.study.common.model.task.TaskType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class YourService {

    @Autowired
    private EnhancedJobService jobService;

    public void createDailyJob() {
        // 创建每天执行的定时任务
        TaskDefinition taskDef = TaskDefinition.builder()
                .id("daily-report-job")
                .name("Daily Report Generation")
                .type(TaskType.SCHEDULED)
                .cronExpression("0 0 1 * * ?") // 每天凌晨1点执行
                .build();

        jobService.saveJob(taskDef);
    }

    public void createHttpCallJob() {
        // 创建HTTP调用任务
        TaskDefinition taskDef = TaskDefinition.builder()
                .id("api-sync-job")
                .name("API Synchronization")
                .type(TaskType.HTTP)
                .cronExpression("0 */30 * * * ?") // 每30分钟执行
                .httpConfig(HttpConfig.builder()
                        .url("http://api.example.com/sync")
                        .method("POST")
                        .headers(Map.of("Authorization", "${api.token}"))
                        .build())
                .build();

        jobService.saveJob(taskDef);
    }

    public void monitorJob(String jobId) {
        // 检查任务执行状态
        TaskResult result = jobService.executeJob(jobId);
        if (!result.isSuccess()) {
            // 处理失败情况
            handleJobFailure(jobId, result.getMessage());
        }
    }

    private void handleJobFailure(String jobId, String message) {
        log.error("Job {} failed: {}", jobId, message);
        // 发送告警等操作
    }
}
