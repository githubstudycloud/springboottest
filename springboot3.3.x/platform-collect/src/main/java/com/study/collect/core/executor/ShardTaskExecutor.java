package com.study.collect.core.executor;

import com.study.collect.common.enums.collect.TaskStatus;
import com.study.collect.common.exception.collect.TaskException;
import com.study.collect.domain.entity.task.CollectTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 分片任务执行器(续)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ShardTaskExecutor {

    /**
     * 合并分片结果
     */
    private ShardExecuteResult mergeResults(List<ShardResult> results, CollectTask task) {
        try {
            // 1. 验证结果完整性
            validateResults(results, task);

            // 2. 排序结果
            List<ShardResult> sortedResults = results.stream()
                    .sorted(Comparator.comparingInt(r -> r.getShard().getShardIndex()))
                    .collect(Collectors.toList());

            // 3. 合并数据
            Object mergedData = mergeShardData(sortedResults);

            // 4. 合并统计信息
            Map<String, Object> stats = mergeShardStats(sortedResults);

            // 5. 更新任务状态
            updateTaskStatus(task, TaskStatus.SUCCESS, stats);

            return ShardExecuteResult.success(mergedData)
                    .withStats(stats);

        } catch (Exception e) {
            log.error("Merge results failed: {}", task.getId(), e);
            throw new TaskException("Merge results failed: " + e.getMessage());
        }
    }

    /**
     * 验证结果完整性
     */
    private void validateResults(List<ShardResult> results, CollectTask task) {
        // 1. 检查结果数量
        if (results.size() != task.getShardCount()) {
            throw new TaskException(
                    String.format("Results count mismatch: expected %d, got %d",
                            task.getShardCount(), results.size())
            );
        }

        // 2. 检查结果连续性
        Set<Integer> shardIndexes = results.stream()
                .map(r -> r.getShard().getShardIndex())
                .collect(Collectors.toSet());

        for (int i = 0; i < task.getShardCount(); i++) {
            if (!shardIndexes.contains(i)) {
                throw new TaskException("Missing shard result: " + i);
            }
        }
    }

    /**
     * 合并分片数据
     */
    @SuppressWarnings("unchecked")
    private Object mergeShardData(List<ShardResult> results) {
        if (results.isEmpty()) {
            return null;
        }

        // 获取第一个结果的数据类型
        Object firstData = results.get(0).getData();

        if (firstData instanceof List) {
            // 合并列表数据
            return results.stream()
                    .map(r -> (List<Object>) r.getData())
                    .flatMap(List::stream)
                    .collect(Collectors.toList());

        } else if (firstData instanceof Map) {
            // 合并Map数据
            Map<String, Object> merged = new HashMap<>();
            results.forEach(r ->
                    merged.putAll((Map<String, Object>) r.getData())
            );
            return merged;

        } else {
            throw new TaskException("Unsupported data type for merge: "
                    + firstData.getClass());
        }
    }

    /**
     * 合并统计信息
     */
    private Map<String, Object> mergeShardStats(List<ShardResult> results) {
        Map<String, Object> mergedStats = new HashMap<>();

        // 1. 基础统计
        mergedStats.put("totalShards", results.size());
        mergedStats.put("successShards",
                results.stream().filter(ShardResult::isSuccess).count());

        // 2. 处理时间统计
        OptionalDouble avgProcessTime = results.stream()
                .mapToLong(r -> r.getProcessTime().toMillis())
                .average();
        mergedStats.put("avgProcessTime",
                avgProcessTime.orElse(0));

        // 3. 数据统计
        long totalRecords = results.stream()
                .mapToLong(r -> ((Number) r.getStats()
                        .getOrDefault("recordCount", 0L)).longValue())
                .sum();
        mergedStats.put("totalRecords", totalRecords);

        return mergedStats;
    }

    /**
     * 处理分片错误
     */
    private ShardResult handleShardError(TaskShard shard, Exception e) {
        try {
            // 1. 更新分片状态
            updateShardStatus(shard, ShardStatus.FAILED);

            // 2. 记录错误信息
            saveShardError(shard, e);

            // 3. 判断是否可重试
            boolean retriable = isRetriable(shard, e);

            // 4. 构建错误结果
            return ShardResult.failed(shard, e.getMessage(), retriable);

        } catch (Exception ex) {
            log.error("Handle shard error failed: {}", shard.getId(), ex);
            return ShardResult.failed(shard, "Error handling failed", false);
        }
    }

    /**
     * 判断是否可重试
     */
    private boolean isRetriable(TaskShard shard, Exception e) {
        // 1. 检查重试次数
        if (shard.getRetryCount() >= shard.getMaxRetryTimes()) {
            return false;
        }

        // 2. 检查异常类型
        if (e instanceof NonRetriableException) {
            return false;
        }

        // 3. 检查错误消息
        String errorMessage = e.getMessage();
        if (errorMessage != null && (
                errorMessage.contains("invalid data") ||
                        errorMessage.contains("authorization failed"))) {
            return false;
        }

        return true;
    }

    /**
     * 计算重试延迟
     */
    private long calculateRetryDelay(TaskShard shard) {
        // 指数退避策略
        int retryCount = shard.getRetryCount();
        long baseDelay = 1000L; // 1秒
        long maxDelay = 60000L; // 1分钟

        long delay = baseDelay * (long) Math.pow(2, retryCount);
        return Math.min(delay, maxDelay);
    }

    /**
     * 保存分片错误信息
     */
    private void saveShardError(TaskShard shard, Exception e) {
        ShardError error = ShardError.builder()
                .shardId(shard.getId())
                .taskId(shard.getTaskId())
                .errorType(e.getClass().getSimpleName())
                .errorMessage(e.getMessage())
                .stackTrace(ExceptionUtils.getStackTrace(e))
                .createTime(LocalDateTime.now())
                .build();

        shardErrorRepository.save(error);
    }

    /**
     * 更新任务状态
     */
    private void updateTaskStatus(CollectTask task,
                                  TaskStatus status, Map<String, Object> stats) {

        task.setStatus(status.name());
        task.setEndTime(LocalDateTime.now());
        task.setStats(stats);

        taskRepository.save(task);

        // 发送任务状态变更事件
        TaskStatusChangedEvent event = new TaskStatusChangedEvent(
                task.getId(), status, stats);
        eventPublisher.publishEvent(event);
    }
}