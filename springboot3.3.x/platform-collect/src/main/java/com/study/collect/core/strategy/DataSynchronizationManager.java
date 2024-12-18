package com.study.collect.core.strategy;

import com.study.collect.common.exception.sync.SyncException;
import com.study.collect.domain.entity.sync.SyncResult;
import com.study.collect.model.request.collect.SyncRequest;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 数据同步管理器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataSynchronizationManager {

    private final NodeManager nodeManager;
    private final DataRepository dataRepository;
    private final VersionManager versionManager;
    private final ConflictResolver conflictResolver;
    private final MetricsCollector metricsCollector;

    /**
     * 执行数据同步
     */
    public SyncResult synchronize(SyncRequest request) {
        String syncId = UUID.randomUUID().toString();
        Timer.Sample timer = metricsCollector.startTimer("data_sync");

        try {
            // 1. 创建同步上下文
            SyncContext context = createSyncContext(request, syncId);

            // 2. 执行同步
            return executeSync(context);

        } catch (Exception e) {
            log.error("Data sync failed: {}", syncId, e);
            throw new SyncException("Sync failed: " + e.getMessage());
        } finally {
            metricsCollector.stopTimer(timer);
        }
    }

    /**
     * 执行同步
     */
    private SyncResult executeSync(SyncContext context) {
        try {
            // 1. 准备同步
            prepareSynchronization(context);

            // 2. 执行同步策略
            SyncStrategy strategy = selectSyncStrategy(context);
            SyncResult result = strategy.execute(context);

            // 3. 处理冲突
            handleSyncConflicts(context, result);

            // 4. 提交同步
            commitSynchronization(context, result);

            return result;

        } catch (Exception e) {
            // 5. 处理同步失败
            handleSyncFailure(context, e);
            throw e;
        }
    }

    /**
     * 准备同步
     */
    private void prepareSynchronization(SyncContext context) {
        // 1. 验证源节点和目标节点
        validateNodes(context);

        // 2. 比较版本
        compareVersions(context);

        // 3. 计算差异
        calculateDifferences(context);

        // 4. 估算同步量
        estimateSyncVolume(context);
    }

    /**
     * 处理同步冲突
     */
    private void handleSyncConflicts(SyncContext context, SyncResult result) {
        if (!result.hasConflicts()) {
            return;
        }

        // 1. 分析冲突
        List<SyncConflict> conflicts = analyzeConflicts(result.getConflicts());

        // 2. 解决冲突
        for (SyncConflict conflict : conflicts) {
            try {
                resolveConflict(conflict, context);
            } catch (Exception e) {
                log.error("Resolve conflict failed: {}", conflict.getId(), e);
                context.addUnresolvedConflict(conflict);
            }
        }

        // 3. 验证冲突解决
        verifyConflictResolution(context);
    }

    /**
     * 提交同步
     */
    private void commitSynchronization(SyncContext context, SyncResult result) {
        // 1. 验证同步结果
        validateSyncResult(result);

        // 2. 更新版本信息
        updateVersions(context);

        // 3. 广播同步完成
        broadcastSyncCompletion(context);

        // 4. 清理同步资源
        cleanupSync(context);
    }

    /**
     * 增量同步策略
     */
    private class IncrementalSyncStrategy implements SyncStrategy {

        @Override
        public SyncResult execute(SyncContext context) {
            // 1. 获取增量数据
            List<DataChange> changes = getIncrementalChanges(context);

            // 2. 过滤变更
            List<DataChange> filteredChanges = filterChanges(changes);

            // 3. 应用变更
            return applyChanges(filteredChanges, context);
        }

        private List<DataChange> getIncrementalChanges(SyncContext context) {
            long lastSyncVersion = context.getLastSyncVersion();
            long currentVersion = context.getCurrentVersion();

            return dataRepository.getChangesBetweenVersions(
                    lastSyncVersion,
                    currentVersion
            );
        }
    }

    /**
     * 全量同步策略
     */
    private class FullSyncStrategy implements SyncStrategy {

        @Override
        public SyncResult execute(SyncContext context) {
            // 1. 准备全量数据
            prepareFullSync(context);

            // 2. 分块传输
            List<DataBlock> blocks = transferDataBlocks(context);

            // 3. 验证数据完整性
            validateDataIntegrity(blocks);

            // 4. 应用数据
            return applyFullSync(blocks, context);
        }
    }
}