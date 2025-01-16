package com.study.collect.business.testcase.service.impl;

import com.study.collect.business.testcase.core.executor.CollectExecutor;
import com.study.collect.business.testcase.core.executor.DeleteExecutor;
import com.study.collect.business.testcase.core.manager.QueueManager;
import com.study.collect.business.testcase.core.manager.TaskManager;
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
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * URI采集服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UriCollectServiceImpl implements UriCollectService {

    private final UriRepository repository;
    private final CollectExecutor collectExecutor;
    private final DeleteExecutor deleteExecutor;
    private final TaskManager taskManager;
    private final QueueManager queueManager;

    @Override
    public AsyncResponse<String> collectData(CollectParam param) {
        validateCollectParam(param);
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
            collectExecutor.execute(param, metrics -> {
                taskManager.updateTaskProgress(
                        taskId,
                        metrics.getProcessedItems(),
                        metrics.getTotalItems()
                );
                taskManager.updateTaskStatus(
                        taskId,
                        "PROCESSING",
                        metrics.getStatusMessage()
                );
            }).thenAccept(metrics -> {
                taskManager.updateTaskStatus(
                        taskId,
                        "COMPLETED",
                        String.format("Processed %d URIs", metrics.getProcessedItems())
                );
            }).exceptionally(throwable -> {
                handleTaskError(taskId, "Collection failed", throwable);
                return null;
            });

        } catch (Exception e) {
            handleTaskError(taskId, "Task processing failed", e);
            throw new RuntimeException("Task processing failed", e);
        }
    }

    @Override
    public AsyncResponse<Long> deleteData(DeleteParam param) {
        validateDeleteParam(param);
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

            deleteExecutor.execute(param, metrics -> {
                taskManager.updateTaskProgress(
                        taskId,
                        metrics.getProcessedItems(),
                        metrics.getTotalItems()
                );
                taskManager.updateTaskStatus(
                        taskId,
                        "PROCESSING",
                        metrics.getStatusMessage()
                );
            }).thenAccept(metrics -> {
                taskManager.updateTaskStatus(
                        taskId,
                        "COMPLETED",
                        String.format("Deleted %d URIs", metrics.getProcessedItems())
                );
            }).exceptionally(throwable -> {
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
        validateQueryParam(param);
        return repository.findByCondition(
                UriRepository.QueryParams.builder()
                        .rootNode(param.getRootNode())
                        .version(param.getVersion())
                        .versionType(param.getVersionType())
                        .includeDeleted(param.getIncludeDeleted())
                        .onlyDeleted(param.getOnlyDeleted())
                        .pageable(PageRequest.of(param.getPage() - 1, param.getSize()))
                        .build()
        );
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
        return StringUtils.hasText(uri) ? uri.split("/")[0] : "";
    }

    private void handleTaskError(String taskId, String message, Throwable throwable) {
        log.error(message + " - Task: {}", taskId, throwable);
        taskManager.updateTaskStatus(
                taskId,
                "ERROR",
                message + ": " + throwable.getMessage()
        );
    }

    private void validateCollectParam(CollectParam param) {
        if (!StringUtils.hasText(param.getRootNode())) {
            throw new IllegalArgumentException("rootNode cannot be empty");
        }
        if (!StringUtils.hasText(param.getServerUri())) {
            throw new IllegalArgumentException("serverUri cannot be empty");
        }
    }

    private void validateDeleteParam(DeleteParam param) {
        if (CollectionUtils.isEmpty(param.getUris())) {
            throw new IllegalArgumentException("uris cannot be empty");
        }
    }

    private void validateQueryParam(QueryParam param) {
        if (!StringUtils.hasText(param.getRootNode()) &&
                CollectionUtils.isEmpty(param.getUris())) {
            throw new IllegalArgumentException("rootNode or uris must be provided");
        }
    }

    private Map<String, Object> buildTaskParams(CollectParam param) {
        Map<String, Object> params = new HashMap<>();
        params.put("rootNode", param.getRootNode());
        params.put("serverUri", param.getServerUri());
        params.put("version", param.getVersion());
        params.put("incremental", param.getIncremental());
        params.put("batchSize", param.getBatchSize());
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