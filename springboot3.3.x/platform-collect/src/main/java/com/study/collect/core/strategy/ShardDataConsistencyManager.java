package com.study.collect.core.strategy;

import com.study.collect.common.annotation.lock.DistributedLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 分片数据一致性管理器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ShardDataConsistencyManager {

    private final DistributedLock distributedLock;
    private final VersionManager versionManager;
    private final ShardRepository shardRepository;
    private final ConsistencyChecker consistencyChecker;

    /**
     * 检查分片数据一致性
     */
    public ConsistencyCheckResult checkConsistency(String taskId) {
        String lockKey = "consistency_check:" + taskId;
        try {
            // 1. 获取分布式锁
            if (!distributedLock.tryLock(lockKey, 30000)) {
                throw new ConsistencyException("Failed to acquire lock");
            }

            try {
                // 2. 获取所有分片
                List<TaskShard> shards =
                        shardRepository.findByTaskId(taskId);
                if (CollectionUtils.isEmpty(shards)) {
                    return ConsistencyCheckResult.empty();
                }

                // 3. 检查分片完整性
                checkShardIntegrity(shards);

                // 4. 检查数据一致性
                return checkShardDataConsistency(shards);

            } finally {
                distributedLock.unlock(lockKey);
            }
        } catch (Exception e) {
            log.error("Check consistency failed: {}", taskId, e);
            throw new ConsistencyException(
                    "Check consistency failed: " + e.getMessage());
        }
    }

    /**
     * 修复数据不一致
     */
    public void repair(String taskId, ConsistencyCheckResult result) {
        String lockKey = "consistency_repair:" + taskId;
        try {
            // 1. 获取分布式锁
            if (!distributedLock.tryLock(lockKey, 30000)) {
                throw new ConsistencyException("Failed to acquire lock");
            }

            try {
                // 2. 验证修复必要性
                if (!needsRepair(result)) {
                    return;
                }

                // 3. 执行修复
                repairInconsistencies(taskId, result);

                // 4. 验证修复结果
                verifyRepairResult(taskId);

            } finally {
                distributedLock.unlock(lockKey);
            }
        } catch (Exception e) {
            log.error("Repair data failed: {}", taskId, e);
            throw new ConsistencyException(
                    "Repair data failed: " + e.getMessage());
        }
    }

    /**
     * 检查分片完整性
     */
    private void checkShardIntegrity(List<TaskShard> shards) {
        // 1. 检查分片数量
        int expectedCount = shards.get(0).getTotalShards();
        if (shards.size() != expectedCount) {
            throw new ConsistencyException(
                    "Shard count mismatch: " + shards.size() +
                            " vs " + expectedCount);
        }

        // 2. 检查分片连续性
        Set<Integer> indexes = shards.stream()
                .map(TaskShard::getShardIndex)
                .collect(Collectors.toSet());

        for (int i = 0; i < expectedCount; i++) {
            if (!indexes.contains(i)) {
                throw new ConsistencyException(
                        "Missing shard: " + i);
            }
        }

        // 3. 检查分片状态
        List<TaskShard> invalidShards = shards.stream()
                .filter(s -> !isValidShardStatus(s.getStatus()))
                .collect(Collectors.toList());

        if (!invalidShards.isEmpty()) {
            throw new ConsistencyException(
                    "Invalid shard status: " +
                            invalidShards.stream()
                                    .map(TaskShard::getId)
                                    .collect(Collectors.joining(",")));
        }
    }

    /**
     * 检查分片数据一致性
     */
    private ConsistencyCheckResult checkShardDataConsistency(
            List<TaskShard> shards) {
        // 1. 创建检查上下文
        ConsistencyCheckContext context =
                createCheckContext(shards);

        // 2. 检查数据版本
        checkDataVersions(shards, context);

        // 3. 检查数据完整性
        checkDataIntegrity(shards, context);

        // 4. 检查数据一致性
        checkDataConsistency(shards, context);

        // 5. 构建检查结果
        return buildCheckResult(context);
    }

    /**
     * 检查数据版本
     */
    private void checkDataVersions(List<TaskShard> shards,
                                   ConsistencyCheckContext context) {
        // 1. 获取所有分片版本
        Map<String, Long> versions = new HashMap<>();
        for (TaskShard shard : shards) {
            versions.put(shard.getId(),
                    versionManager.getVersion(shard.getId()));
        }

        // 2. 检查版本一致性
        long baseVersion = versions.values().iterator().next();
        List<String> inconsistentShards = versions.entrySet().stream()
                .filter(e -> !e.getValue().equals(baseVersion))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (!inconsistentShards.isEmpty()) {
            context.addVersionInconsistency(
                    baseVersion, versions, inconsistentShards);
        }
    }

    /**
     * 修复数据不一致
     */
    private void repairInconsistencies(String taskId,
                                       ConsistencyCheckResult result) {
        // 1. 获取需要修复的分片
        List<String> shardsToRepair =
                result.getInconsistentShards();

        // 2. 获取基准分片
        TaskShard baselineShard =
                getBaselineShard(taskId, result);

        // 3. 执行修复
        for (String shardId : shardsToRepair) {
            try {
                repairShard(shardId, baselineShard);
            } catch (Exception e) {
                log.error("Repair shard failed: {}", shardId, e);
                // 继续修复其他分片
            }
        }
    }
}