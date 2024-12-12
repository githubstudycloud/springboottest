package com.study.scheduler.service;

import com.study.common.model.task.*;
import com.study.common.util.DistributedLockUtil;
import com.study.common.util.JsonUtils;
import com.study.common.util.TaskTraceUtil;
import com.study.common.util.VariableUtil;
import com.study.scheduler.exception.JobException;
import com.study.scheduler.job.CustomJob;
import com.study.scheduler.job.HttpJob;
import com.study.scheduler.job.ScheduledJob;
import com.study.scheduler.model.job.HttpTaskResult;
import com.study.scheduler.model.job.JobDefinition;
import com.study.scheduler.model.job.JobMetrics;
import com.study.scheduler.repository.JobDefinitionRepository;
import com.study.scheduler.repository.JobExecutionRepository;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * 增强的任务服务类
 * 提供任务的创建、执行、状态管理等核心功能
 * 支持HTTP任务、定时任务和自定义任务的处理
 */
@Slf4j
@Service
public class EnhancedJobService {

    private static final int HTTP_TIMEOUT_SECONDS = 10;
    private static final String LOCK_PREFIX_JOB = "job:def:";
    private static final String LOCK_PREFIX_STATUS = "job:status:";

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private DistributedLockUtil lockUtil;

    @Autowired
    private JobDefinitionRepository jobDefinitionRepository;

    @Autowired
    private JobExecutionRepository jobExecutionRepository;

    @Autowired
    private HttpClient httpClient;

    @Autowired
    private MeterRegistry meterRegistry;

    private final JobMetrics jobMetrics = new JobMetrics();

    /**
     * 创建或更新任务
     *
     * @param taskDef 任务定义
     * @return 任务执行结果
     */
    @Transactional
    public Object saveJob(TaskDefinition taskDef) {
        String lockKey = LOCK_PREFIX_JOB + taskDef.getId();
        return lockUtil.executeWithLock(lockKey, () -> {
            try {
                validateTask(taskDef);

                // 保存任务定义
                JobDefinition jobDef = convertToJobDefinition(taskDef);
                jobDefinitionRepository.save(jobDef);

                // 创建Quartz任务
                JobDetail jobDetail = createJobDetail(taskDef);
                Trigger trigger = createTrigger(taskDef);

                // 调度任务
                scheduler.scheduleJob(jobDetail, trigger);

                jobMetrics.incrementJobCreationCount();
                log.info("Successfully saved and scheduled job: {}", taskDef.getId());

                return TaskResult.builder()
                        .success(true)
                        .message("Job saved successfully")
                        .build();

            } catch (SchedulerException e) {
                log.error("Failed to schedule job: {}", taskDef.getId(), e);
                jobMetrics.incrementJobFailureCount();
                return TaskResult.builder()
                        .success(false)
                        .message("Failed to schedule job: " + e.getMessage())
                        .build();
            }
        });
    }

    /**
     * 执行任务
     *
     * @param jobId 任务ID
     * @return 任务执行结果
     */
    public TaskResult executeJob(String jobId) {
        return TaskTraceUtil.executeWithTrace(() -> {
            long startTime = System.currentTimeMillis();
            try {
                // 获取任务定义
                TaskDefinition taskDef = getTaskDefinition(jobId);
                if (taskDef == null) {
                    log.warn("Task not found: {}", jobId);
                    return TaskResult.builder()
                            .success(false)
                            .message("Task not found")
                            .build();
                }

                // 创建执行记录
                TaskExecution execution = createExecution(taskDef);
                log.info("Created execution record for task: {}", jobId);

                // 解析变量
                resolveVariables(taskDef);

                // 执行任务
                Object result = executeTask(taskDef);
                log.info("Task executed successfully: {}", jobId);

                // 更新执行记录
                updateExecutionSuccess(execution, result);

                // 记录执行时间
                long duration = System.currentTimeMillis() - startTime;
                meterRegistry.timer("job.execution.time").record(Duration.ofMillis(duration));

                return TaskResult.builder()
                        .success(true)
                        .data(result)
                        .build();

            } catch (Exception e) {
                log.error("Job execution failed: {}", jobId, e);
                jobMetrics.incrementJobFailureCount();

                long duration = System.currentTimeMillis() - startTime;
                meterRegistry.timer("job.execution.failure.time").record(Duration.ofMillis(duration));

                return TaskResult.builder()
                        .success(false)
                        .message("Execution failed: " + e.getMessage())
                        .build();
            }
        });
    }

    /**
     * 处理HTTP类型任务
     *
     * @param taskDef 任务定义
     * @return 执行结果
     * @throws Exception 执行异常
     */
    private Object executeHttpTask(TaskDefinition taskDef) throws Exception {
        HttpConfig config = taskDef.getHttpConfig();
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(new URI(config.getUrl()))
                .timeout(Duration.ofSeconds(HTTP_TIMEOUT_SECONDS));

        // 设置请求头
        if (config.getHeaders() != null) {
            config.getHeaders().forEach(requestBuilder::header);
        }

        // 设置请求方法和body
        configureHttpMethod(requestBuilder, config);

        log.info("Executing HTTP request: {} {}", config.getMethod(), config.getUrl());

        // 执行请求
        HttpResponse<String> response = httpClient.send(
                requestBuilder.build(),
                HttpResponse.BodyHandlers.ofString()
        );

        // 记录指标
        recordHttpMetrics(response);

        return new HttpTaskResult(
                response.statusCode(),
                response.body(),
                response.headers().map()
        );
    }

    /**
     * 配置HTTP请求方法
     *
     * @param requestBuilder HTTP请求构建器
     * @param config HTTP配置
     */
    private void configureHttpMethod(HttpRequest.Builder requestBuilder, HttpConfig config) {
        switch (config.getMethod().toUpperCase()) {
            case "GET":
                requestBuilder.GET();
                break;
            case "POST":
                requestBuilder.POST(HttpRequest.BodyPublishers.ofString(
                        config.getBody() != null ? config.getBody() : ""));
                break;
            case "PUT":
                requestBuilder.PUT(HttpRequest.BodyPublishers.ofString(
                        config.getBody() != null ? config.getBody() : ""));
                break;
            case "DELETE":
                requestBuilder.DELETE();
                break;
            default:
                throw new JobException("Unsupported HTTP method: " + config.getMethod());
        }
    }

    /**
     * 记录HTTP请求指标
     *
     * @param response HTTP响应
     */
    private void recordHttpMetrics(HttpResponse<String> response) {
        meterRegistry.counter("http.request.count").increment();
        meterRegistry.gauge("http.response.status", response.statusCode());
    }

    /**
     * 更新任务状态
     *
     * @param jobId 任务ID
     * @param newStatus 新状态
     * @return 更新结果
     */
    @Transactional
    public Object updateTaskStatus(String jobId, TaskStatus newStatus) {
        String lockKey = LOCK_PREFIX_STATUS + jobId;
        return lockUtil.executeWithLock(lockKey, () -> {
            try {
                TaskDefinition taskDef = getTaskDefinition(jobId);
                if (taskDef == null) {
                    throw new JobException("Task not found: " + jobId);
                }

                // 更新状态
                taskDef.setStatus(newStatus);
                jobDefinitionRepository.save(convertToJobDefinition(taskDef));
                log.info("Updated task status: {} -> {}", jobId, newStatus);

                // 处理Quartz任务状态
                handleQuartzJobStatus(jobId, newStatus);

                // 记录状态变更
                meterRegistry.counter("job.status." + newStatus.name().toLowerCase()).increment();

                return TaskResult.builder()
                        .success(true)
                        .message("Status updated successfully")
                        .build();

            } catch (SchedulerException e) {
                log.error("Failed to update job status: {}", jobId, e);
                return TaskResult.builder()
                        .success(false)
                        .message("Failed to update status: " + e.getMessage())
                        .build();
            }
        });
    }

    /**
     * 处理Quartz任务状态
     *
     * @param jobId 任务ID
     * @param status 新状态
     * @throws SchedulerException 调度器异常
     */
    private void handleQuartzJobStatus(String jobId, TaskStatus status) throws SchedulerException {
        switch (status) {
            case STOPPED:
                scheduler.pauseJob(JobKey.jobKey(jobId));
                log.info("Paused job: {}", jobId);
                break;
            case WAITING:
                scheduler.resumeJob(JobKey.jobKey(jobId));
                log.info("Resumed job: {}", jobId);
                break;
            default:
                handleOtherStatus(jobId, status);
        }
    }

    /**
     * 获取任务定义
     *
     * @param jobId 任务ID
     * @return 任务定义
     */
    private TaskDefinition getTaskDefinition(String jobId) {
        JobDefinition jobDef = jobDefinitionRepository.findById(jobId)
                .orElseThrow(() -> new JobException("Task not found: " + jobId));
        return JsonUtils.fromJson(jobDef.getJobData(), TaskDefinition.class);
    }

    /**
     * 创建执行记录
     *
     * @param taskDef 任务定义
     * @return 执行记录
     */
    private TaskExecution createExecution(TaskDefinition taskDef) {
        TaskExecution execution = new TaskExecution();
        execution.setId(UUID.randomUUID().toString());
        execution.setTaskDefId(taskDef.getId());
        execution.setStartTime(new Date());
        execution.setStatus(TaskStatus.RUNNING);
        return jobExecutionRepository.save(execution);
    }

    /**
     * 解析任务变量
     *
     * @param taskDef 任务定义
     */
    private void resolveVariables(TaskDefinition taskDef) {
        if (taskDef.getHttpConfig() != null) {
            HttpConfig httpConfig = taskDef.getHttpConfig();
            httpConfig.setUrl(VariableUtil.resolveVariable(httpConfig.getUrl()));

            if (httpConfig.getHeaders() != null) {
                Map<String, String> resolvedHeaders = VariableUtil.resolveVariables(httpConfig.getHeaders());
                httpConfig.setHeaders(resolvedHeaders);
            }

            if (httpConfig.getParameters() != null) {
                Map<String, String> resolvedParams = VariableUtil.resolveVariables(httpConfig.getParameters());
                httpConfig.setParameters(resolvedParams);
            }
        }
    }

    /**
     * 执行任务
     *
     * @param taskDef 任务定义
     * @return 执行结果
     * @throws Exception 执行异常
     */
    private Object executeTask(TaskDefinition taskDef) throws Exception {
        switch (taskDef.getType()) {
            case HTTP:
                return executeHttpTask(taskDef);
            case SCHEDULED:
                return executeScheduledTask(taskDef);
            default:
                return executeCustomTask(taskDef);
        }
    }

    /**
     * 执行定时任务
     *
     * @param taskDef 任务定义
     * @return 执行结果
     */
    private Object executeScheduledTask(TaskDefinition taskDef) {
        log.info("Executing scheduled task: {}", taskDef.getId());
        // 实现定时任务的具体逻辑
        return null;
    }

    /**
     * 执行自定义任务
     *
     * @param taskDef 任务定义
     * @return 执行结果
     */
    private Object executeCustomTask(TaskDefinition taskDef) {
        log.info("Executing custom task: {}", taskDef.getId());
        // 实现自定义任务的具体逻辑
        return null;
    }

    /**
     * 更新执行成功记录
     *
     * @param execution 执行记录
     * @param result 执行结果
     */
    private void updateExecutionSuccess(TaskExecution execution, Object result) {
        execution.setEndTime(new Date());
        execution.setStatus(TaskStatus.COMPLETED);
        execution.setResult(JsonUtils.toJson(result));
        jobExecutionRepository.save(execution);
        log.info("Updated execution success: {}", execution.getId());
    }

    /**
     * 验证任务
     *
     * @param taskDef 任务定义
     */
    private void validateTask(TaskDefinition taskDef) {
        if (taskDef == null || taskDef.getId() == null) {
            throw new JobException("Invalid task definition");
        }

        if (taskDef.getType() == null) {
            throw new JobException("Task type is required");
        }

        if (TaskType.HTTP.equals(taskDef.getType())) {
            validateHttpConfig(taskDef.getHttpConfig());
        }
    }

    /**
     * 验证HTTP配置
     *
     * @param httpConfig HTTP配置
     */
    private void validateHttpConfig(HttpConfig httpConfig) {
        if (httpConfig == null) {
            throw new JobException("HTTP config is required for HTTP task");
        }

        if (httpConfig.getUrl() == null || httpConfig.getUrl().trim().isEmpty()) {

            if (httpConfig.getUrl() == null || httpConfig.getUrl().trim().isEmpty()) {
                throw new JobException("URL is required for HTTP task");
            }

            if (httpConfig.getMethod() == null || httpConfig.getMethod().trim().isEmpty()) {
                throw new JobException("HTTP method is required for HTTP task");
            }
        }
    }

        /**
         * 转换为任务定义
         *
         * @param taskDef 原始任务定义
         * @return 转换后的任务定义
         */
        private JobDefinition convertToJobDefinition(TaskDefinition taskDef) {
            JobDefinition jobDef = new JobDefinition();
            jobDef.setId(taskDef.getId());
            jobDef.setName(taskDef.getName());
            jobDef.setDescription(taskDef.getDescription());
            jobDef.setCronExpression(taskDef.getCronExpression());
            jobDef.setJobData(JsonUtils.toJson(taskDef));
            jobDef.setStatus(taskDef.getStatus().ordinal());
            jobDef.setCreateTime(new Date());
            jobDef.setUpdateTime(new Date());
            return jobDef;
        }

        /**
         * 创建任务详情
         *
         * @param taskDef 任务定义
         * @return 任务详情
         */
        private JobDetail createJobDetail(TaskDefinition taskDef) {
            return JobBuilder.newJob(getJobClass(taskDef.getType()))
                    .withIdentity(taskDef.getId())
                    .withDescription(taskDef.getDescription())
                    .usingJobData(createJobDataMap(taskDef))
                    .build();
        }

        /**
         * 创建任务数据Map
         *
         * @param taskDef 任务定义
         * @return 任务数据Map
         */
        private JobDataMap createJobDataMap(TaskDefinition taskDef) {
            JobDataMap dataMap = new JobDataMap();
            dataMap.put("taskId", taskDef.getId());
            dataMap.put("taskType", taskDef.getType().name());
            return dataMap;
        }

        /**
         * 创建触发器
         *
         * @param taskDef 任务定义
         * @return 触发器
         */
        private Trigger createTrigger(TaskDefinition taskDef) {
            return TriggerBuilder.newTrigger()
                    .withIdentity(taskDef.getId() + "_trigger")
                    .withSchedule(CronScheduleBuilder.cronSchedule(taskDef.getCronExpression()))
                    .withDescription("Trigger for task: " + taskDef.getName())
                    .build();
        }

        /**
         * 获取任务类
         *
         * @param type 任务类型
         * @return 任务类
         */
//        private Class<? extends Job> getJobClass(TaskType type) {
//            switch (type) {
//                case HTTP:
//                    return HttpJob.class;
//                case SCHEDULED:
//                    return ScheduledJob.class;
//                default:
//                    return CustomJob.class;
//            }
//        }
    private Class<? extends Job> getJobClass(TaskType type) {
        return switch (type) {
            case HTTP -> HttpJob.class;
            case SCHEDULED -> ScheduledJob.class;
            case CUSTOM -> CustomJob.class;
            // 添加默认情况处理
            default -> throw new IllegalArgumentException("Unsupported task type: " + type);
        };
    }

        /**
         * 创建默认重试策略
         *
         * @return 重试策略
         */
        private RetryPolicy createDefaultRetryPolicy() {
            return RetryPolicy.builder()
                    .maxRetries(3)
                    .retryInterval(1000L)
                    .exponentialBackoff(true)
                    .build();
        }

        /**
         * 处理其他状态
         *
         * @param jobId 任务ID
         * @param status 状态
         */
        private void handleOtherStatus(String jobId, TaskStatus status) {
            log.info("Handling status {} for job {}", status, jobId);
            switch (status) {
                case RUNNING:
                    handleRunningStatus(jobId);
                    break;
                case COMPLETED:
                    handleCompletedStatus(jobId);
                    break;
                case FAILED:
                    handleFailedStatus(jobId);
                    break;
                case SKIPPED:
                    handleSkippedStatus(jobId);
                    break;
                default:
                    log.warn("Unhandled status {} for job {}", status, jobId);
            }
        }

        /**
         * 处理运行中状态
         *
         * @param jobId 任务ID
         */
        private void handleRunningStatus(String jobId) {
            log.info("Task {} is now running", jobId);
            meterRegistry.counter("job.running.count").increment();
        }

        /**
         * 处理完成状态
         *
         * @param jobId 任务ID
         */
        private void handleCompletedStatus(String jobId) {
            log.info("Task {} has completed", jobId);
            meterRegistry.counter("job.completed.count").increment();
            jobMetrics.incrementJobSuccessCount();
        }

        /**
         * 处理失败状态
         *
         * @param jobId 任务ID
         */
        private void handleFailedStatus(String jobId) {
            log.error("Task {} has failed", jobId);
            meterRegistry.counter("job.failed.count").increment();
            jobMetrics.incrementJobFailureCount();
        }

        /**
         * 处理跳过状态
         *
         * @param jobId 任务ID
         */
        private void handleSkippedStatus(String jobId) {
            log.info("Task {} has been skipped", jobId);
            meterRegistry.counter("job.skipped.count").increment();
        }
    }