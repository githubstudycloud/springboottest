package com.study.collect.business.testcase.service.impl;

import com.study.collect.business.testcase.constant.CollectionConstants;
import com.study.collect.business.testcase.entity.UriEntity;
import com.study.collect.business.testcase.manager.CollectTaskManager;
import com.study.collect.business.testcase.manager.QueueManager;
import com.study.collect.business.testcase.model.param.CollectParam;
import com.study.collect.business.testcase.model.param.DeleteParam;
import com.study.collect.business.testcase.model.param.QueryParam;
import com.study.collect.business.testcase.model.response.AsyncResponse;
import com.study.collect.business.testcase.model.response.PageResponse;
import com.study.collect.business.testcase.model.response.TaskResponse;
import com.study.collect.business.testcase.repository.UriRepository;
import com.study.collect.business.testcase.service.UriCollectService;
import com.study.collect.business.testcase.service.http.UriHttpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.ObjectPool;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UriCollectServiceImpl implements UriCollectService {
    private final UriHttpService httpService;
    private final UriRepository repository;
    private final ObjectPool<UriEntity> entityPool;
    private final CollectTaskManager taskManager;
    private final QueueManager<CollectParam> collectQueue;
    private final QueueManager<DeleteParam> deleteQueue;

    @Override
    public AsyncResponse<String> collectData(CollectParam param) {
        // 1. 创建任务
        TaskResponse task = taskManager.createTask(
                "COLLECT",
                Map.of("rootNode", param.getRootNode(),
                        "serverUri", param.getServerUri(),
                        "version", param.getVersion()),
                param.getPriority()
        );

        // 2. 将任务加入队列
        collectQueue.enqueue(
                task.getTaskId(),
                param,
                param.getPriority(),
                this::processCollectTask
        ).exceptionally(throwable -> {
            taskManager.updateTaskStatus(
                    task.getTaskId(),
                    "ERROR",
                    throwable.getMessage()
            );
            return null;
        });

        // 3. 返回异步响应
        return AsyncResponse.<String>builder()
                .taskId(task.getTaskId())
                .status("QUEUED")
                .message("Task queued successfully")
                .build();
    }

    private void processCollectTask(CollectParam param) {
        String taskId = param.getTaskId();
        try {
            taskManager.updateTaskStatus(taskId, "PROCESSING", "Starting data collection");

            // 1. 获取所有版本
            List<String> allVersions = httpService.getAllVersions(param
            );

            taskManager.updateTaskStatus(
                    taskId,
                    "PROCESSING",
                    String.format("Found %d versions", allVersions.size())
            );

            // 2. 如果是增量同步，先清理数据
            if (param.getIncremental()) {
                cleanupIncrementalData(param, allVersions);
            }

            // 3. 处理每个版本
            long totalProcessed = 0;
            long estimatedTotal = calculateEstimatedTotal(param, allVersions);

            taskManager.updateTaskProgress(taskId, totalProcessed, estimatedTotal);

            for (String version : allVersions) {
                totalProcessed += processVersion(
                        param,
                        version,
                        taskId
                );
                taskManager.updateTaskProgress(taskId, totalProcessed, estimatedTotal);
            }

            taskManager.updateTaskStatus(
                    taskId,
                    "COMPLETED",
                    String.format("Processed %d URIs", totalProcessed)
            );

        } catch (Exception e) {
            log.error("Error processing collect task: {}", taskId, e);
            taskManager.updateTaskStatus(
                    taskId,
                    "ERROR",
                    "Error: " + e.getMessage()
            );
            throw new RuntimeException("Task processing failed", e);
        }
    }

    private long processVersion(
      CollectParam param,
            String version,
            String taskId
    ) throws Exception {
        String rootNode = param.getRootNode();
        // 1. 获取该版本下的所有URI
        List<String> allUris = httpService.getAllUrisForVersion(param, version);
        long totalProcessed = 0;

        // 2. 分批处理
        List<List<String>> batches = partition(
                allUris,
                CollectionConstants.DEFAULT_BATCH_SIZE
        );

        for (List<String> batch : batches) {
            // 获取URI详情
            List<Map<String, Object>> details = httpService.batchGetUriDetails(
                    param,
                    batch,
                    CollectionConstants.DEFAULT_BATCH_SIZE
            );

            // 创建实体并保存
            List<UriEntity> entities = new ArrayList<>();
            for (Map<String, Object> detail : details) {
                UriEntity entity = null;
                try {
                    entity = entityPool.borrowObject();
                    fillEntity(entity, rootNode, version, detail);
                    entities.add(entity);
                } catch (Exception e) {
                    log.error("Error creating entity", e);
                    if (entity != null) {
                        entityPool.returnObject(entity);
                    }
                }
            }

            try {
                if (!entities.isEmpty()) {
                    repository.batchUpsert(rootNode, entities);
                    totalProcessed += entities.size();
                }
            } finally {
                // 返还对象到对象池
                for (UriEntity entity : entities) {
                    try {
                        entityPool.returnObject(entity);
                    } catch (Exception e) {
                        log.error("Error returning entity to pool", e);
                    }
                }
            }

            // 更新任务进度
            taskManager.updateTaskStatus(
                    taskId,
                    "PROCESSING",
                    String.format("Processing version %s: %d/%d",
                            version, totalProcessed, allUris.size())
            );
        }

        return totalProcessed;
    }

    @Override
    public AsyncResponse<Long> deleteData(DeleteParam param) {
        // 1. 创建任务
        TaskResponse task = taskManager.createTask(
                "DELETE",
                Map.of("rootNode", param.getRootNode(),
                        "urisCount", param.getUris().size(),
                        "hardDelete", param.getHardDelete()),
                param.getPriority()
        );

        // 2. 将任务加入队列
        deleteQueue.enqueue(
                task.getTaskId(),
                param,
                param.getPriority(),
                this::processDeleteTask
        ).exceptionally(throwable -> {
            taskManager.updateTaskStatus(
                    task.getTaskId(),
                    "ERROR",
                    throwable.getMessage()
            );
            return null;
        });

        // 3. 返回异步响应
        return AsyncResponse.<Long>builder()
                .taskId(task.getTaskId())
                .status("QUEUED")
                .message("Delete task queued successfully")
                .build();
    }

    private void processDeleteTask(DeleteParam param) {
        String taskId = param.getTaskId();
        try {
            taskManager.updateTaskStatus(taskId, "PROCESSING", "Starting data deletion");

            List<List<String>> batches = partition(
                    param.getUris(),
                    param.getBatchSize() != null ?
                            param.getBatchSize() :
                            CollectionConstants.DEFAULT_BATCH_SIZE
            );

            long totalDeleted = 0;
            for (List<String> batch : batches) {
                long batchCount;
                if (param.getHardDelete()) {
                    batchCount = repository.batchHardDelete(param.getRootNode(), batch);
                } else {
                    batchCount = repository.batchSoftDelete(param.getRootNode(), batch);
                }
                totalDeleted += batchCount;

                taskManager.updateTaskProgress(
                        taskId,
                        totalDeleted,
                        param.getUris().size()
                );
            }

            taskManager.updateTaskStatus(
                    taskId,
                    "COMPLETED",
                    String.format("Deleted %d URIs", totalDeleted)
            );

        } catch (Exception e) {
            log.error("Error processing delete task: {}", taskId, e);
            taskManager.updateTaskStatus(
                    taskId,
                    "ERROR",
                    "Error: " + e.getMessage()
            );
            throw new RuntimeException("Delete task processing failed", e);
        }
    }

    @Override
    public Page<UriEntity> queryUri(QueryParam param) {
        return repository.findByCondition(
                param.getRootNode(),
                param.getVersion(),
                param.getVersionType(),
                param.getIncludeDeleted(),
                PageRequest.of(param.getPage() - 1, param.getSize())
        );
    }

    @Override
    public List<UriEntity> batchQueryUri(List<String> uris, Boolean includeDeleted) {
        if (CollectionUtils.isEmpty(uris)) {
            return Collections.emptyList();
        }

        // 使用第一个URI的rootNode作为默认值
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
        if (collectQueue.cancel(taskId) || deleteQueue.cancel(taskId)) {
            taskManager.cancelTask(taskId);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateTaskPriority(String taskId, int priority) {
        // 更新任务优先级
        if (collectQueue.updatePriority(taskId, priority) ||
                deleteQueue.updatePriority(taskId, priority)) {
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

    private void cleanupIncrementalData(
            CollectParam param,
            List<String> versions
    ) throws Exception {
        String rootNode = param.getRootNode();
        Set<String> allUriHashes = new HashSet<>();

        // 获取所有版本的URI
        for (String version : versions) {
            List<String> versionUris = httpService.getAllUrisForVersion(param, version);
            allUriHashes.addAll(versionUris.stream()
                    .map(this::generateUriHash)
                    .collect(Collectors.toSet()));
        }

        // 删除不存在的URI //TODO 改成分页批量删除 ,可选软删除或者硬删除，根据param.getHardDelete()来判断
        repository.deleteNotInUris(rootNode, allUriHashes);
    }

    private String generateUriHash(String uri) {
        return Objects.hash(uri) + "";
    }

    private String extractRootNode(String uri) {
        // 从URI中提取rootNode的逻辑
        String[] parts = uri.split("/");
        return parts.length > 0 ? parts[0] : "";
    }

    private <T> List<List<T>> partition(List<T> list, int size) {
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }

        List<List<T>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            partitions.add(list.subList(i, Math.min(i + size, list.size())));
        }
        return partitions;
    }

    private long calculateEstimatedTotal(CollectParam param, List<String> versions) {
        long total = 0;
        for (String version : versions) {
            try {
                PageResponse<String> response = httpService.getUriListAsync(
                        param,
                        version,
                        new com.study.collect.business.testcase.model.param.PageParam(1, 1)
                ).get();
                total += response.getTotal();
            } catch (Exception e) {
                log.warn("Error calculating total for version: {}", version, e);
            }
        }
        return total;
    }

    private void fillEntity(UriEntity entity, String rootNode, String version, Map<String, Object> detail) {
        entity.setUri((String) detail.get("uri"));
        entity.setRootNode(rootNode);
        entity.setVersionType(getVersionType(version));
        entity.setUriVersion(version);
        entity.setDetails(detail);
    }

    private String getVersionType(String version) {
        return version.toLowerCase().contains("branch") ? "BRANCH" : "TRUNK";
    }
}