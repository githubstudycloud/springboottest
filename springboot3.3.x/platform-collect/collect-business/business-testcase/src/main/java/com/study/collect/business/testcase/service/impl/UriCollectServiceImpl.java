package com.study.collect.business.testcase.service.impl;

import com.google.common.collect.Lists;
import com.study.collect.business.testcase.constant.CollectionConstants;
import com.study.collect.business.testcase.entity.CollectTaskEntity;
import com.study.collect.business.testcase.entity.UriEntity;
import com.study.collect.business.testcase.entity.VersionEntity;
import com.study.collect.business.testcase.manager.CollectTaskManager;
import com.study.collect.business.testcase.manager.QueueManager;
import com.study.collect.business.testcase.model.param.CollectParam;
import com.study.collect.business.testcase.model.param.DeleteParam;
import com.study.collect.business.testcase.model.param.QueryParam;
import com.study.collect.business.testcase.model.response.AsyncResponse;
import com.study.collect.business.testcase.model.response.TaskResponse;
import com.study.collect.business.testcase.model.response.UriDetail;
import com.study.collect.business.testcase.model.response.VersionInfo;
import com.study.collect.business.testcase.repository.CollectTaskRepository;
import com.study.collect.business.testcase.repository.UriRepository;
import com.study.collect.business.testcase.repository.VersionRepository;
import com.study.collect.business.testcase.service.UriCollectService;
import com.study.collect.business.testcase.service.http.UriHttpService;
import com.study.collect.business.testcase.utils.HashUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.ObjectPool;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UriCollectServiceImpl implements UriCollectService {

    private final UriHttpService httpService;
    private final UriRepository uriRepository;
    private final VersionRepository versionRepository;
    private final CollectTaskRepository taskRepository;
    private final ObjectPool<UriEntity> entityPool;
    private final CollectTaskManager taskManager;
    private final QueueManager<CollectParam> collectQueue;
    private final QueueManager<DeleteParam> deleteQueue;

    // 用于缓存正在处理的任务
    private final ConcurrentHashMap<String, CollectTaskEntity> activeTasks = new ConcurrentHashMap<>();

    @Override
    public AsyncResponse<String> collectData(CollectParam param) {
        validateCollectParam(param);

        // 创建采集任务
        String taskId = UUID.randomUUID().toString();
        CollectTaskEntity task = createCollectTask(param, taskId);
        activeTasks.put(taskId, task);

        // 将任务加入队列
        collectQueue.enqueue(
                taskId,
                param,
                param.getPriority(),
                this::processCollectTask
        ).exceptionally(throwable -> {
            handleTaskError(task, throwable);
            return null;
        });

        return AsyncResponse.<String>builder()
                .taskId(taskId)
                .status("QUEUED")
                .message("Task queued successfully")
                .build();
    }

    private void processCollectTask(CollectParam param) {
        String taskId = param.getTaskId();
        CollectTaskEntity task = activeTasks.get(taskId);

        try {
            // 更新任务状态
            updateTaskStatus(task, "PROCESSING", "Starting collection");

            // 1. 获取并保存版本信息
            List<VersionInfo> versions = getAndSaveVersions(param);
            if (StringUtils.isNotEmpty(param.getVersion())) {
                versions = filterVersions(versions, param.getVersion());
            }

            // 2. 处理每个版本
            for (VersionInfo version : versions) {
                if (!activeTasks.containsKey(taskId)) {
                    log.info("Task {} was cancelled", taskId);
                    return;
                }
                processVersion(task, param, version);
            }

            // 3. 重试失败的URI
            if (!task.getFailedUriList().isEmpty()) {
                retryFailedUris(task, param);
            }

            // 4. 完成任务
            completeTask(task);

        } catch (Exception e) {
            handleTaskError(task, e);
            throw new RuntimeException("Task processing failed", e);
        } finally {
            activeTasks.remove(taskId);
        }
    }

    private void processVersion(CollectTaskEntity task, CollectParam param, VersionInfo version) {
        try {
            log.info("Processing version: {}", version.getVersion());
            updateTaskStatus(task, "PROCESSING", "Processing version: " + version.getVersion());

            // 1. 获取URI列表和总数
            CompletableFuture<List<String>> urisFuture = httpService.getUriList(param.getServerUrl(), version.getVersion());
            CompletableFuture<Integer> countFuture = httpService.getUriCount(param.getServerUrl(), version.getVersion());

            List<String> uris = urisFuture.get();
            int totalCount = countFuture.get();

            // 更新任务进度信息
            updateTaskProgress(task, totalCount);

            // 2. 处理增量场景
            if (param.getIncremental()) {
                uris = filterIncrementalUris(uris, version, param.getStartTime(), param.getEndTime());
            }

            // 3. 删除不存在的URI
            if (!param.getIncremental()) {
                deleteNonExistentUris(param.getRootNode(), version.getVersion(), new HashSet<>(uris), param.getHardDelete());
            }

            // 4. 批量处理URI
            processUrisBatch(task, param, version, uris);

        } catch (Exception e) {
            log.error("Failed to process version: {}", version.getVersion(), e);
            task.addFailedUri("VERSION:" + version.getVersion(), e.getMessage());
            taskRepository.save(task);
        }
    }
    private void processUrisBatch(CollectTaskEntity task, CollectParam param,
                                  VersionInfo version, List<String> uris) {
        List<List<String>> batches = Lists.partition(uris, CollectionConstants.HTTP_BATCH_SIZE);

        for (List<String> batch : batches) {
            if (!activeTasks.containsKey(task.getTaskId())) {
                log.info("Task {} was cancelled during batch processing", task.getTaskId());
                return;
            }

            try {
                // 获取URI详情
                List<UriDetail> details = httpService.getUriDetails(param.getServerUrl(), batch).get();

                // 转换为实体并保存
                List<UriEntity> entities = new ArrayList<>();
                for (UriDetail detail : details) {
                    UriEntity entity = null;
                    try {
                        entity = entityPool.borrowObject();
                        fillEntity(entity, param.getRootNode(), version, detail);
                        entities.add(entity);
                    } catch (Exception e) {
                        log.error("Failed to create entity for URI: {}", detail.getUri(), e);
                        task.addFailedUri(detail.getUri(), "Entity creation failed: " + e.getMessage());
                        if (entity != null) {
                            try {
                                entityPool.returnObject(entity);
                            } catch (Exception ex) {
                                log.error("Failed to return entity to pool", ex);
                            }
                        }
                    }
                }

                if (!entities.isEmpty()) {
                    try {
                        uriRepository.batchUpsert(param.getRootNode(), entities);
                    } finally {
                        // 返还所有实体到对象池
                        for (UriEntity entity : entities) {
                            try {
                                entityPool.returnObject(entity);
                            } catch (Exception e) {
                                log.error("Failed to return entity to pool", e);
                            }
                        }
                    }
                }

                // 更新任务进度
                task.setProcessedUris(task.getProcessedUris() + batch.size());
                task.setProgress(calculateProgress(task.getProcessedUris(), task.getTotalUris()));
                taskRepository.save(task);

            } catch (Exception e) {
                log.error("Failed to process batch for version: {}", version.getVersion(), e);
                batch.forEach(uri -> task.addFailedUri(uri, e.getMessage()));
                taskRepository.save(task);
            }
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

    private void retryFailedUris(CollectTaskEntity task, CollectParam param) {
        if (CollectionUtils.isEmpty(task.getFailedUriList())) {
            return;
        }

        log.info("Retrying {} failed URIs for task: {}", task.getFailedUriList().size(), task.getTaskId());
        updateTaskStatus(task, "PROCESSING", "Retrying failed URIs");

        List<String> retriedUris = new ArrayList<>();
        List<List<String>> batches = Lists.partition(new ArrayList<>(task.getFailedUriList()),
                CollectionConstants.HTTP_BATCH_SIZE);

        for (List<String> batch : batches) {
            try {
                // 过滤掉版本级别的失败记录
                List<String> uris = batch.stream()
                        .filter(uri -> !uri.startsWith("VERSION:"))
                        .collect(Collectors.toList());

                if (!uris.isEmpty()) {
                    List<UriDetail> details = httpService.getUriDetails(param.getServerUrl(), uris).get();
                    if (!details.isEmpty()) {
                        for (UriDetail detail : details) {
                            VersionInfo version = getVersionInfo(detail.getVersion());
                            List<UriEntity> entities = Collections.singletonList(
                                    createEntity(param.getRootNode(), version, detail));
                            uriRepository.batchUpsert(param.getRootNode(), entities);
                            retriedUris.add(detail.getUri());
                        }
                    }
                }
            } catch (Exception e) {
                log.error("Failed to retry batch", e);
            }
        }

        // 更新失败列表
        if (!retriedUris.isEmpty()) {
            task.getFailedUriList().removeAll(retriedUris);
            task.setFailedUris(task.getFailedUris() - retriedUris.size());
            taskRepository.save(task);
        }
    }

    @Override
    public Page<String> getVersions(String rootNode, Integer page, Integer size) {
        int pageNum = page != null ? page : 1;
        int pageSize = size != null ? size : CollectionConstants.DEFAULT_PAGE_SIZE;
        return versionRepository.findVersionsByRootNode(rootNode, PageRequest.of(pageNum - 1, pageSize));
    }

    @Override
    public Long getUriCount(String rootNode, String version) {
        return uriRepository.countByRootNodeAndVersion(rootNode, version);
    }

    @Override
    public AsyncResponse<Long> deleteData(DeleteParam param) {
        String taskId = UUID.randomUUID().toString();
        param.setTaskId(taskId);

        deleteQueue.enqueue(
                taskId,
                param,
                param.getPriority(),
                this::processDeleteTask
        );

        return AsyncResponse.<Long>builder()
                .taskId(taskId)
                .status("QUEUED")
                .message("Delete task queued successfully")
                .build();
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
        int pageSize = size != null ? size : CollectionConstants.DEFAULT_PAGE_SIZE;
        return uriRepository.findByUpdateTimeRange(rootNode, startTime, endTime,
                PageRequest.of(pageNum - 1, pageSize));
    }

    @Override
    public AsyncResponse<Void> getTaskStatus(String taskId) {
        CollectTaskEntity task = activeTasks.get(taskId);
        if (task == null) {
            task = taskRepository.findByTaskId(taskId);
        }

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
        CollectTaskEntity task = activeTasks.remove(taskId);
        if (task != null) {
            task.setStatus("CANCELLED");
            task.setEndTime(LocalDateTime.now());
            taskRepository.save(task);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateTaskPriority(String taskId, int priority) {
        return collectQueue.updatePriority(taskId, priority) ||
                deleteQueue.updatePriority(taskId, priority);
    }

    @Override
    public List<TaskResponse> getActiveTasks() {
        return activeTasks.values().stream()
                .map(this::convertToTaskResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getCollectionStats(String rootNode) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUris", uriRepository.countByRootNode(rootNode));
        stats.put("totalVersions", versionRepository.countByRootNode(rootNode));
        stats.put("lastCollectTime", taskRepository.findLastCollectTime(rootNode));
        stats.put("activeTasks", activeTasks.values().stream()
                .filter(task -> task.getRootNode().equals(rootNode))
                .count());
        return stats;
    }

    // 其他私有辅助方法...
    /**
     * 验证采集参数
     */
    private void validateCollectParam(CollectParam param) {
        if (param == null) {
            throw new IllegalArgumentException("CollectParam cannot be null");
        }
        if (StringUtils.isBlank(param.getRootNode())) {
            throw new IllegalArgumentException("RootNode cannot be empty");
        }
        if (StringUtils.isBlank(param.getServerUrl())) {
            throw new IllegalArgumentException("ServerUrl cannot be empty");
        }
        if (param.getIncremental() && param.getStartTime() == null) {
            throw new IllegalArgumentException("StartTime is required for incremental collection");
        }
    }

    /**
     * 创建采集任务
     */
    private CollectTaskEntity createCollectTask(CollectParam param, String taskId) {
        CollectTaskEntity task = new CollectTaskEntity();
        task.setTaskId(taskId);
        task.setRootNode(param.getRootNode());
        task.setVersion(param.getVersion());
        task.setStatus("CREATED");
        task.setPriority(param.getPriority() != null ? param.getPriority() : 0);
        task.setCreateTime(LocalDateTime.now());
        task.setIsIncremental(param.getIncremental());
        task.setIncrementStartTime(param.getStartTime());
        task.setIncrementEndTime(param.getEndTime());
        task.setProcessedUris(0L);
        task.setFailedUris(0L);
        task.setProgress(0.0);

        return taskRepository.save(task);
    }

    /**
     * 获取并保存版本信息
     */
    private List<VersionInfo> getAndSaveVersions(CollectParam param) throws Exception {
        List<VersionInfo> versions = new ArrayList<>();
        int page = 1;
        int size = 200; // 每页200条

        while (true) {
            List<VersionInfo> pageVersions = httpService.getVersions(
                    param.getServerUrl(),
                    param.getRootNode(),
                    page,
                    size
            ).get();

            if (pageVersions.isEmpty()) {
                break;
            }

            versions.addAll(pageVersions);

            // 保存版本信息
            List<VersionEntity> versionEntities = pageVersions.stream()
                    .map(v -> convertToVersionEntity(v, param.getRootNode()))
                    .collect(Collectors.toList());
            versionRepository.saveAll(versionEntities);

            if (pageVersions.size() < size) {
                break;
            }
            page++;
        }

        return versions;
    }

    /**
     * 过滤版本列表
     */
    private List<VersionInfo> filterVersions(List<VersionInfo> versions, String versionFilter) {
        if (StringUtils.isBlank(versionFilter)) {
            return versions;
        }

        Set<String> filterSet = Arrays.stream(versionFilter.split(","))
                .map(String::trim)
                .collect(Collectors.toSet());

        return versions.stream()
                .filter(v -> filterSet.stream().anyMatch(v.getVersion()::contains))
                .collect(Collectors.toList());
    }

    /**
     * 过滤增量URI
     */
    private List<String> filterIncrementalUris(List<String> uris, VersionInfo version,
                                               LocalDateTime startTime, LocalDateTime endTime) {
        if (CollectionUtils.isEmpty(uris)) {
            return Collections.emptyList();
        }

        // 获取已存在的URI的更新时间
        Map<String, LocalDateTime> existingUriUpdateTimes =
                uriRepository.findUpdateTimesByUris(uris);

        return uris.stream()
                .filter(uri -> {
                    LocalDateTime existingUpdateTime = existingUriUpdateTimes.get(uri);
                    if (existingUpdateTime == null) {
                        return true; // 新URI，需要采集
                    }
                    // 检查更新时间是否在范围内
                    return existingUpdateTime.isAfter(startTime) &&
                            (endTime == null || existingUpdateTime.isBefore(endTime));
                })
                .collect(Collectors.toList());
    }

    /**
     * 删除不存在的URI
     */
    private void deleteNonExistentUris(String rootNode, String version,
                                       Set<String> existingUris, boolean hardDelete) {
        List<String> urisToDelete = new ArrayList<>();
        int page = 0;
        int size = 2000; // 每次处理2000条

        while (true) {
            Page<String> dbUris = uriRepository.findUrisByVersion(
                    rootNode,
                    version,
                    PageRequest.of(page, size)
            );

            if (!dbUris.hasContent()) {
                break;
            }

            urisToDelete.addAll(
                    dbUris.getContent().stream()
                            .filter(uri -> !existingUris.contains(uri))
                            .collect(Collectors.toList())
            );

            // 每积累2000条执行一次删除
            if (urisToDelete.size() >= 2000) {
                executeBatchDelete(rootNode, urisToDelete, hardDelete);
                urisToDelete.clear();
            }

            if (!dbUris.hasNext()) {
                break;
            }
            page++;
        }

        // 处理剩余的URI
        if (!urisToDelete.isEmpty()) {
            executeBatchDelete(rootNode, urisToDelete, hardDelete);
        }
    }

    /**
     * 执行批量删除
     */
    private void executeBatchDelete(String rootNode, List<String> uris, boolean hardDelete) {
        try {
            if (hardDelete) {
                uriRepository.batchHardDelete(rootNode, uris);
            } else {
                uriRepository.batchSoftDelete(rootNode, uris);
            }
        } catch (Exception e) {
            log.error("Failed to delete URIs for rootNode: {}", rootNode, e);
            throw new RuntimeException("Failed to delete URIs", e);
        }
    }

    /**
     * 更新任务状态
     */
    private void updateTaskStatus(CollectTaskEntity task, String status, String message) {
        task.setStatus(status);
        task.setMessage(message);
        if ("PROCESSING".equals(status) && task.getStartTime() == null) {
            task.setStartTime(LocalDateTime.now());
        }
        taskRepository.save(task);
    }

    /**
     * 更新任务进度
     */
    private void updateTaskProgress(CollectTaskEntity task, long totalCount) {
        task.setTotalUris(task.getTotalUris() + totalCount);
        task.setProgress(calculateProgress(task.getProcessedUris(), task.getTotalUris()));
        taskRepository.save(task);
    }

    /**
     * 计算进度百分比
     */
    private double calculateProgress(long processed, long total) {
        if (total == 0) {
            return 0.0;
        }
        return (double) processed / total * 100;
    }

    /**
     * 处理任务错误
     */
    private void handleTaskError(CollectTaskEntity task, Throwable error) {
        log.error("Task {} failed", task.getTaskId(), error);
        task.setStatus("FAILED");
        task.setEndTime(LocalDateTime.now());
        task.setMessage(error.getMessage());
        taskRepository.save(task);
    }

    /**
     * 完成任务
     */
    private void completeTask(CollectTaskEntity task) {
        task.setStatus("COMPLETED");
        task.setEndTime(LocalDateTime.now());
        task.setProgress(100.0);
        task.setMessage("Collection completed successfully");
        taskRepository.save(task);
    }

    /**
     * 转换为任务响应对象
     */
    private TaskResponse convertToTaskResponse(CollectTaskEntity task) {
        return TaskResponse.builder()
                .taskId(task.getTaskId())
                .type("COLLECT")
                .status(task.getStatus())
                .progress(task.getProgress())
                .message(task.getMessage())
                .priority(task.getPriority())
                .createTime(task.getCreateTime())
                .startTime(task.getStartTime())
                .endTime(task.getEndTime())
                .totalCount(task.getTotalUris())
                .processedCount(task.getProcessedUris())
                .failedCount(task.getFailedUris())
                .build();
    }
}