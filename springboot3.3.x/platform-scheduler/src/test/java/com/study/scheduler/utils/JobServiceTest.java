package com.study.scheduler.utils;

import com.study.common.model.task.*;
import com.study.scheduler.service.EnhancedJobService;
import org.junit.jupiter.api.Test;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class JobServiceTest {

    @Autowired
    private EnhancedJobService jobService;

    @Autowired
    private Scheduler scheduler;

    /**
     * 测试创建HTTP任务
     */
    @Test
    void testCreateHttpJob() {
        // 创建HTTP任务定义
        TaskDefinition taskDef = TaskDefinition.builder()
                .id(UUID.randomUUID().toString())
                .name("Test HTTP Job")
                .type(TaskType.HTTP)
                .cronExpression("0 */5 * * * ?") // 每5分钟执行
                .httpConfig(createHttpConfig())
                .build();

        // 保存任务
        Object result = jobService.saveJob(taskDef);
        assertTrue(result instanceof TaskResult);
        assertTrue(((TaskResult) result).isSuccess());

        // 验证任务是否存在于调度器中
        try {
            assertTrue(scheduler.checkExists(JobKey.jobKey(taskDef.getId())));
        } catch (Exception e) {
            fail("Failed to check job existence", e);
        }
    }

    /**
     * 测试创建定时任务
     */
    @Test
    void testCreateScheduledJob() {
        // 创建定时任务定义
        TaskDefinition taskDef = TaskDefinition.builder()
                .id(UUID.randomUUID().toString())
                .name("Test Scheduled Job")
                .type(TaskType.SCHEDULED)
                .cronExpression("0 0 12 * * ?") // 每天中午12点执行
                .build();

        // 保存任务
        Object result = jobService.saveJob(taskDef);
        assertTrue(result instanceof TaskResult);
        assertTrue(((TaskResult) result).isSuccess());
    }

    /**
     * 测试执行任务
     */
    @Test
    void testExecuteJob() {
        // 创建一个测试任务
        String jobId = createTestJob();

        // 执行任务
        TaskResult result = jobService.executeJob(jobId);
        assertTrue(result.isSuccess());
    }

    /**
     * 测试更新任务状态
     */
    @Test
    void testUpdateJobStatus() {
        // 创建测试任务
        String jobId = createTestJob();

        // 暂停任务
        Object result = jobService.updateTaskStatus(jobId, TaskStatus.STOPPED);
        assertTrue(result instanceof TaskResult);
        assertTrue(((TaskResult) result).isSuccess());

        // 恢复任务
        result = jobService.updateTaskStatus(jobId, TaskStatus.WAITING);
        assertTrue(result instanceof TaskResult);
        assertTrue(((TaskResult) result).isSuccess());
    }

    private String createTestJob() {
        TaskDefinition taskDef = TaskDefinition.builder()
                .id(UUID.randomUUID().toString())
                .name("Test Job")
                .type(TaskType.HTTP)
                .cronExpression("0 */5 * * * ?")
                .httpConfig(createHttpConfig())
                .build();

        jobService.saveJob(taskDef);
        return taskDef.getId();
    }

    private HttpConfig createHttpConfig() {
        return HttpConfig.builder()
                .url("http://example.com/api")
                .method("GET")
                .build();
    }
}