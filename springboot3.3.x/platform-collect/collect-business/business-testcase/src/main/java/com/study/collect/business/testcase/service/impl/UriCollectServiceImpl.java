package com.study.collect.business.testcase.service.impl;

import com.study.collect.business.testcase.core.executor.CollectExecutor;
import com.study.collect.business.testcase.core.executor.DeleteExecutor;
import com.study.collect.business.testcase.core.manager.QueueManager;
import com.study.collect.business.testcase.core.manager.TaskManager;
import com.study.collect.business.testcase.core.processor.CollectProcessor;
import com.study.collect.business.testcase.core.processor.DeleteProcessor;
import com.study.collect.business.testcase.entity.UriEntity;
import com.study.collect.business.testcase.model.param.CollectParam;
import com.study.collect.business.testcase.model.param.DeleteParam;
import com.study.collect.business.testcase.model.param.QueryParam;
import com.study.collect.business.testcase.model.response.AsyncResponse;
import com.study.collect.business.testcase.model.response.TaskResponse;
import com.study.collect.business.testcase.repository.UriRepository;
import com.study.collect.business.testcase.service.UriCollectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * URI采集服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UriCollectServiceImpl implements UriCollectService {

    private final UriRepository repository;
    private final CollectProcessor collectProcessor;
    private final DeleteProcessor deleteProcessor;
    private final CollectExecutor collectExecutor;
    private final DeleteExecutor deleteExecutor;
    private final TaskManager taskManager;
    private final QueueManager queueManager;
    private final UriCleanupService cleanupService;

    @Override
    public AsyncResponse<String> collectData(CollectParam param) {
        try {
            // 1. 创建任务
            Map<String, Object> taskParams = buildTaskParams(param);
            TaskResponse task = taskManager.createTask("COLLECT", taskParams, param.getPriority());
            String taskId = task.getTaskId();
            param.setTaskId(taskId);

            // 2. 将任务加入队列
            queueManager.enqueue(
                    taskId,
                    param,
                    param.getPriority(),
                    this::processCollectTask
            ).exceptionally(throwable -> {
                handleTaskError(taskId, "Collection queuing failed", throwable);
                return null;
            });

            // 3. 返回异步响应
            return AsyncResponse.<String>builder()
                    .taskId(taskId)
                    .status("QUEUED")
                    .message("Data collection task queued successfully")
                    .build();

        } catch (Exception e) {
            log.error("Failed to initiate collection task", e);
            throw new RuntimeException("Failed to start collection task", e);
        }
    }

    private void processCollectTask(CollectParam param) {
        String taskId = param.getTaskId();
        try {
            taskManager.updateTaskStatus(taskId, "PROCESSING", "Starting data collection");

            // 1. 执行采集
            collectProcessor.process(param)
                    .thenAccept(result -> {
                        // 2. 如果是增量同步，执行清理
                        if (param.getIncremental()) {
                            cleanupIncrementalData(param, taskId);
                        }

                        taskManager.updateTaskStatus(
                                taskId,
                                "COMPLETED",
                                String.format("Processed %d URIs", result)
                        );
                    })
                    .exceptionally(throwable -> {
                        handleTaskError(taskId, "Collection failed", throwable);
                        return null;
                    });

        } catch (Exception e) {
            handleTaskError(taskId, "Task processing failed", e);
            throw new RuntimeException("Task processing failed", e);
        }
    }

    private void cleanupIncrementalData(CollectParam param, String taskId) {
        try {
            // 创建清理参数
            UriCleanupService.CleanupParams cleanupParams = UriCleanupService.CleanupParams.builder()
                    .rootNode(param.getRootNode())
                    .uris(param.getUris())
                    .hardDelete(param.getHardDelete())
                    .batchSize(param.getBatchSize())
                    .build();

            // 执行清理
            cleanupService.cleanup(cleanupParams, metrics -> {
                taskManager.updateTaskStatus(
                        taskId,
                        "CLEANING",
                        String.format("Cleaning up data: %.2f%%", metrics.getProgressPercentage())
                );
            }).exceptionally(throwable -> {
                log.error("Cleanup failed for task: {}", taskId, throwable);
                return null;
            });
        } catch (Exception e) {
            log.error("Error during cleanup for task: {}", taskId, e);
        }
    }

    @Override
    public AsyncResponse<Long> deleteData(DeleteParam param) {
        try {
            // 1. 创建任务
            Map<String, Object> taskParams = buildDeleteTaskParams(param);
            TaskResponse task = taskManager.createTask("DELETE", taskParams, param.getPriority());
            String taskId = task.getTaskId();
            param.setTaskId(taskId);

            // 2. 将任务加入队列
            queueManager.enqueue(
                    taskId,
                    param,
                    param.getPriority(),
                    this::processDeleteTask
            ).exceptionally(throwable -> {
                handleTaskError(taskId, "Delete queuing failed", throwable);
                return null;
            });

            // 3. 返回异步响应
            return AsyncResponse.<Long>builder()
                    .taskId(taskId)
                    .status("QUEUED")
                    .message("Delete task queued successfully")
                    .build();

        } catch (Exception e) {
            log.error("Failed to initiate delete task", e);
            throw new RuntimeException("Failed to start delete task", e);
        }
    }

    private void processDeleteTask(DeleteParam param) {
        String taskId = param.getTaskId();
        try {
            taskManager.updateTaskStatus(taskId, "PROCESSING", "Starting data deletion");

            deleteProcessor.process(param)
                    .thenAccept(result -> {
                        taskManager.updateTaskStatus(
                                taskId,
                                "COMPLETED",
                                String.format("Deleted %d URIs", result)
                        );
                    })
                    .exceptionally(throwable -> {
                        handleTaskError(taskId, "Deletion failed", throwable);
                        return null;
                    });

        } catch (Exception e) {
            handleTaskError(taskId, "Task processing failed", e);
            throw new RuntimeException("Task processing failed", e);
        }
    }

    @Override
    public Page<UriEntity> queryUri(QueryParam param) {
        UriRepository.QueryParams queryParams = UriRepository.QueryParams.builder()
                .rootNode(param.getRootNode())
                .version(param.getVersion())
                .versionType(param.getVersionType())
                .includeDeleted(param.getIncludeDeleted())
                .onlyDeleted(param.getOnlyDeleted())
                .pageable(PageRequest.of(param.getPage() - 1, param.getSize()))
                .build();

        return repository.findByCondition(queryParams);
    }

    @Override
    public List<UriEntity> batchQueryUri(List<String> uris, Boolean includeDeleted) {
        if (CollectionUtils.isEmpty(uris)) {
            return Collections.emptyList();
        }

        return repository.batchQuery(
                uris,
                this::extractRootNode,
                includeDeleted
        );
    }

    @Override
    public AsyncResponse<Void> getTaskStatus(String taskId) {
        TaskResponse task = taskManager.getTaskStatus(taskId);
        if (task == null) {
            return AsyncResponse.<Void>builder()
                    .taskId(taskId)
                    .status("NOT_FOUND")
                    .message("Task not found")
                    .build();
        }

        return AsyncResponse.<Void>builder()
                .taskId(taskId)
                .status(task.getStatus())
                .message(task.getMessage())
                .progress(task.getProgress())
                .startTime(task.getStartTime())
                .endTime(task.getEndTime())
                .build();
    }

    @Override
    public boolean cancelTask(String taskId) {
        // 尝试取消队列中的任务
        if (queueManager.cancel(taskId)) {
            taskManager.cancelTask(taskId);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateTaskPriority(String taskId, int priority) {
        // 更新任务优先级
        if (queueManager.updatePriority(taskId, priority)) {
            return taskManager.updateTaskPriority(taskId, priority);
        }
        return false;
    }

    @Override
    public List<AsyncResponse<Void>> getActiveTasks() {
        return taskManager.getActiveTasks().stream()
                .map(task -> AsyncResponse.<Void>builder()
                        .taskId(task.getTaskId())
                        .status(task.getStatus())
                        .message(task.getMessage())
                        .progress(task.getProgress())
                        .startTime(task.getStartTime())
                        .build())
                .collect(Collectors.toList());
    }

    private String extractRootNode(String uri) {
        return Optional.ofNullable(uri)
                .map(u -> {
                    String[] parts = u.split("/");
                    return parts.length > 0 ? parts[0] : "";
                })
                .orElse("");
    }

    private void handleTaskError(String taskId, String message, Throwable throwable) {
        log.error(message + " - Task: {}", taskId, throwable);
        taskManager.updateTaskStatus(taskId, "ERROR",
                message + ": " + throwable.getMessage());
    }

    private Map<String, Object> buildTaskParams(CollectParam param) {
        Map<String, Object> params = new HashMap<>();
        params.put("rootNode", param.getRootNode());
        params.put("version", param.getVersion());
        params.put("incremental", param.getIncremental());
        params.put("batchSize", param.getBatchSize());
        params.put("serverUri", param.getServerUri());
        return params;
    }

    private Map<String, Object> buildDeleteTaskParams(DeleteParam param) {
        Map<String, Object> params = new HashMap<>();
        params.put("rootNode", param.getRootNode());
        params.put("urisCount", param.getUris().size());
        params.put("hardDelete", param.getHardDelete());
        params.put("batchSize", param.getBatchSize());
        return params;
    }
}