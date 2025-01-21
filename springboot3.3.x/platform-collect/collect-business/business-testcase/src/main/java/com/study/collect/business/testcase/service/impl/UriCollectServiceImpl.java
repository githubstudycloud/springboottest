package com.study.collect.business.testcase.service.impl;

import com.google.common.collect.Lists;
import com.study.collect.business.testcase.constant.CollectionConstants;
import com.study.collect.business.testcase.entity.CollectTaskEntity;
import com.study.collect.business.testcase.entity.UriEntity;
import com.study.collect.business.testcase.manager.CollectTaskManager;
import com.study.collect.business.testcase.manager.QueueManager;
import com.study.collect.business.testcase.model.param.CollectParam;
import com.study.collect.business.testcase.model.param.DeleteParam;
import com.study.collect.business.testcase.model.param.QueryParam;
import com.study.collect.business.testcase.model.response.*;
import com.study.collect.business.testcase.repository.CollectTaskRepository;
import com.study.collect.business.testcase.repository.UriRepository;
import com.study.collect.business.testcase.repository.VersionRepository;
import com.study.collect.business.testcase.service.UriCollectService;
import com.study.collect.business.testcase.service.http.UriHttpService;
import com.study.collect.business.testcase.utils.HashUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.ObjectPool;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class UriCollectServiceImpl implements UriCollectService {
    private static final int HTTP_BATCH_SIZE = CollectionConstants.HTTP_BATCH_SIZE;
    private static final int DEFAULT_PAGE_SIZE = 20;

    private final UriHttpService httpService;
    private final UriRepository uriRepository;
    private final VersionRepository versionRepository;
    private final CollectTaskRepository taskRepository;
    private final ObjectPool<UriEntity> entityPool;
    private final CollectTaskManager taskManager;
    private final QueueManager<CollectParam> collectQueue;
    private final QueueManager<DeleteParam> deleteQueue;

    private final ConcurrentHashMap<String, CollectTaskEntity> activeTasks = new ConcurrentHashMap<>();

    @Override
    public AsyncResponse<String> collectData(CollectParam param) {
        validateCollectParam(param);

        String taskId = UUID.randomUUID().toString();
        param.setTaskId(taskId);

        Map<String, Object> taskParams = new HashMap<>();
        taskParams.put("rootNode", param.getRootNode());
        taskParams.put("version", param.getVersion());
        taskParams.put("serverUrl", param.getServerUrl());
        taskParams.put("incremental", param.getIncremental());

        TaskResponse task = taskManager.createTask("COLLECT", taskParams, param.getPriority());

        collectQueue.enqueue(taskId, param, param.getPriority(), this::processCollectTask)
                .exceptionally(throwable -> {
                    taskManager.updateTaskStatus(taskId, "ERROR", throwable.getMessage());
                    return null;
                });

        return AsyncResponse.<String>builder()
                .taskId(taskId)
                .status("QUEUED")
                .message("Task queued successfully")
                .build();
    }

    private void validateCollectParam(CollectParam param) {
        if (param == null) {
            throw new IllegalArgumentException("CollectParam cannot be null");
        }
        if (!StringUtils.hasText(param.getRootNode())) {
            throw new IllegalArgumentException("RootNode cannot be empty");
        }
        if (!StringUtils.hasText(param.getServerUrl())) {
            throw new IllegalArgumentException("ServerUrl cannot be empty");
        }
        if (param.getIncremental() && param.getStartTime() == null) {
            throw new IllegalArgumentException("StartTime is required for incremental collection");
        }
    }

    private void processCollectTask(CollectParam param) {
        String taskId = param.getTaskId();
        taskManager.updateTaskStatus(taskId, "PROCESSING", "Starting collection");

        try {
            // 获取版本列表
            List<VersionInfo> versions = httpService.getVersions(
                    param.getServerUrl(),
                    param.getRootNode(),
                    1,
                    Integer.MAX_VALUE
            ).get();

            if (StringUtils.hasText(param.getVersion())) {
                versions = versions.stream()
                        .filter(v -> v.getVersion().equals(param.getVersion()))
                        .toList();
            }

            long totalUris = 0;
            for (VersionInfo version : versions) {
                // 获取该版本下的URI数量
                int versionUriCount = httpService.getUriCount(param.getServerUrl(), version.getVersion()).get();
                totalUris += versionUriCount;

                // 处理该版本的URI
                processVersionUris(param, version, versionUriCount, taskId);
            }

            taskManager.updateTaskStatus(taskId, "COMPLETED", "Collection completed successfully");

        } catch (Exception e) {
            log.error("Failed to process collect task: {}", taskId, e);
            taskManager.updateTaskStatus(taskId, "ERROR", "Collection failed: " + e.getMessage());
            throw new RuntimeException("Failed to process collect task", e);
        }
    }

    private void processVersionUris(CollectParam param, VersionInfo version, int totalCount, String taskId) {
        try {
            // 分批获取URI列表
            int offset = 0;
            int batchSize = param.getBatchSize() != null ? param.getBatchSize() : HTTP_BATCH_SIZE;

            while (offset < totalCount) {
                // 获取一批URI
                List<String> uris = httpService.getUriList(param.getServerUrl(), version.getVersion()).get();

                // 获取URI详情
                List<UriDetail> details = httpService.getUriDetails(param.getServerUrl(), uris).get();

                // 转换并保存实体
                List<UriEntity> entities = new ArrayList<>();
                for (UriDetail detail : details) {
                    UriEntity entity = entityPool.borrowObject();
                    try {
                        fillEntity(entity, param.getRootNode(), version, detail);
                        entities.add(entity);
                    } catch (Exception e) {
                        log.error("Failed to process URI: {}", detail.getUri(), e);
                        entityPool.returnObject(entity);
                    }
                }

                if (!entities.isEmpty()) {
                    try {
                        uriRepository.batchUpsert(param.getRootNode(), entities);
                    } finally {
                        // 返还实体到对象池
                        for (UriEntity entity : entities) {
                            entityPool.returnObject(entity);
                        }
                    }
                }

                // 更新进度
                offset += batchSize;
                double progress = (double) offset / totalCount * 100;
                taskManager.updateTaskProgress(taskId, offset, totalCount);
            }

        } catch (Exception e) {
            log.error("Failed to process version: {}", version.getVersion(), e);
            throw new RuntimeException("Failed to process version", e);
        }
    }

    private void fillEntity(UriEntity entity, String rootNode, VersionInfo version, UriDetail detail) {
        entity.setUri(detail.getUri());
        entity.setUriHash(HashUtil.hash(detail.getUri()));
        entity.setRootNode(rootNode);
        entity.setVersionType(version.getType());
        entity.setUriVersion(version.getVersion());
        entity.setRealUri(detail.getRealUri());
        entity.setNumber(detail.getNumber());
        entity.setName(detail.getName());
        entity.setThirdPartyUpdateTime(detail.getUpdateTime());
        entity.setDetails(detail.getDetails());
        entity.setDeleted(false);
    }

    @Override
    public Page<String> getVersions(String rootNode, Integer page, Integer size) {
        int pageNum = page != null ? page : 1;
        int pageSize = size != null ? size : DEFAULT_PAGE_SIZE;
        return versionRepository.findVersionsByRootNode(rootNode, PageRequest.of(pageNum - 1, pageSize));
    }

    @Override
    public Long getUriCount(String rootNode, String version) {
        return uriRepository.countByRootNodeAndVersion(rootNode, version);
    }

    @Override
    public AsyncResponse<Long> deleteData(DeleteParam param) {
        validateDeleteParam(param);

        String taskId = UUID.randomUUID().toString();
        param.setTaskId(taskId);

        Map<String, Object> taskParams = new HashMap<>();
        taskParams.put("rootNode", param.getRootNode());
        taskParams.put("version", param.getVersion());
        taskParams.put("uriCount", param.getUris().size());
        taskParams.put("hardDelete", param.getHardDelete());

        TaskResponse task = taskManager.createTask("DELETE", taskParams, param.getPriority());

        deleteQueue.enqueue(taskId, param, param.getPriority(), this::processDeleteTask)
                .exceptionally(throwable -> {
                    taskManager.updateTaskStatus(taskId, "ERROR", throwable.getMessage());
                    return null;
                });

        return AsyncResponse.<Long>builder()
                .taskId(taskId)
                .status("QUEUED")
                .message("Delete task queued successfully")
                .build();
    }

    private void validateDeleteParam(DeleteParam param) {
        if (param == null) {
            throw new IllegalArgumentException("DeleteParam cannot be null");
        }
        if (param.getUris() == null || param.getUris().isEmpty()) {
            throw new IllegalArgumentException("URIs cannot be empty");
        }
    }

    private void processDeleteTask(DeleteParam param) {
        String taskId = param.getTaskId();
        taskManager.updateTaskStatus(taskId, "PROCESSING", "Starting deletion");

        try {
            int batchSize = param.getBatchSize() != null ? param.getBatchSize() : HTTP_BATCH_SIZE;
            List<List<String>> batches = Lists.partition(param.getUris(), batchSize);

            long totalDeleted = 0;
            long totalBatches = batches.size();

            for (int i = 0; i < batches.size(); i++) {
                List<String> batch = batches.get(i);
                long deletedCount;

                if (param.getHardDelete()) {
                    deletedCount = uriRepository.batchHardDelete(param.getRootNode(), batch, batchSize);
                } else {
                    deletedCount = uriRepository.batchSoftDelete(param.getRootNode(), batch, batchSize);
                }

                totalDeleted += deletedCount;
                double progress = ((i + 1.0) / totalBatches) * 100;
                taskManager.updateTaskProgress(taskId, i + 1, totalBatches);
            }

            taskManager.updateTaskStatus(taskId, "COMPLETED", String.format("Successfully deleted %d URIs", totalDeleted));

        } catch (Exception e) {
            log.error("Failed to process delete task: {}", taskId, e);
            taskManager.updateTaskStatus(taskId, "ERROR", "Deletion failed: " + e.getMessage());
            throw new RuntimeException("Failed to process delete task", e);
        }
    }

    @Override
    public Page<UriEntity> queryUri(QueryParam param) {
        return uriRepository.findByConditions(param);
    }

    @Override
    public List<UriEntity> batchQueryUri(List<String> uris, Boolean includeDeleted, Boolean onlyDetail) {
        return uriRepository.batchQuery(uris, includeDeleted, onlyDetail);
    }

    @Override
    public Page<UriEntity> queryByUpdateTime(String rootNode, LocalDateTime startTime,
                                             LocalDateTime endTime, Integer page, Integer size) {
        int pageNum = page != null ? page : 1;
        int pageSize = size != null ? size : DEFAULT_PAGE_SIZE;
        return uriRepository.findByUpdateTimeRange(
                rootNode,
                startTime,
                endTime,
                PageRequest.of(pageNum - 1, pageSize)
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
                .startTime(task.getCreateTime())
                .endTime(task.getEndTime())
                .build();
    }

    @Override
    public boolean cancelTask(String taskId) {
        return taskManager.cancelTask(taskId);
    }

    @Override
    public boolean updateTaskPriority(String taskId, int priority) {
        return taskManager.updateTaskPriority(taskId, priority);
    }

    @Override
    public List<TaskResponse> getActiveTasks() {
        return taskManager.getActiveTasks();
    }

    @Override
    public Map<String, Object> getCollectionStats(String rootNode) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUris", uriRepository.countByRootNode(rootNode));
        stats.put("totalVersions", versionRepository.countByRootNode(rootNode));
        stats.put("lastCollectTime", taskRepository.findLastCollectTime(rootNode));
        stats.put("activeTasks", taskManager.getActiveTasks().stream()
                .filter(task -> rootNode.equals(task.getParams().get("rootNode")))
                .count());
        return stats;
    }
}