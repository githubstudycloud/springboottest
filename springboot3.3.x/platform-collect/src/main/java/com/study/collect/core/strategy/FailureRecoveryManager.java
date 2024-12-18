package com.study.collect.core.strategy;

import com.study.collect.domain.entity.task.CollectTask;
import com.study.collect.domain.repository.task.TaskRepository;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.ErrorContext;
import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.stereotype.Component;

import javax.lang.model.type.ErrorType;
import java.util.List;
import java.util.Map;

/**
 * 故障恢复管理器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FailureRecoveryManager {

    private final NodeManager nodeManager;
    private final TaskRepository taskRepository;
    private final ShardRepository shardRepository;
    private final LoadBalancer loadBalancer;
    private final MetricsCollector metricsCollector;

    /**
     * 处理节点故障
     */
    public void handleNodeFailure(NodeInfo failedNode) {
        String nodeId = failedNode.getId();
        log.warn("Handling node failure: {}", nodeId);
        Timer.Sample timer = metricsCollector.startTimer("node_failure_recovery");

        try {
            // 1. 标记节点故障
            markNodeFailure(failedNode);

            // 2. 获取受影响任务
            List<CollectTask> affectedTasks =
                    taskRepository.findTasksByNodeId(nodeId);

            // 3. 迁移任务
            migrateTasks(affectedTasks);

            // 4. 更新统计信息
            updateFailureStats(failedNode);

        } catch (Exception e) {
            log.error("Handle node failure failed: {}", nodeId, e);
            throw new RecoveryException(
                    "Failed to handle node failure: " + e.getMessage());
        } finally {
            metricsCollector.stopTimer(timer);
        }
    }

    /**
     * 处理任务执行异常
     */
    public void handleTaskFailure(CollectTask task, Exception error) {
        String taskId = task.getId();
        log.warn("Handling task failure: {}", taskId);

        try {
            // 1. 分析故障原因
            FailureAnalysis analysis = analyzeFailure(task, error);

            // 2. 选择恢复策略
            RecoveryStrategy strategy = selectRecoveryStrategy(analysis);

            // 3. 执行恢复
            executeRecovery(task, strategy);

            // 4. 验证恢复结果
            verifyRecovery(task);

        } catch (Exception e) {
            log.error("Handle task failure failed: {}", taskId, e);
            handleRecoveryFailure(task, e);
        }
    }

    /**
     * 分析故障原因
     */
    private FailureAnalysis analyzeFailure(CollectTask task, Exception error) {
        // 1. 获取错误上下文
        ErrorContext errorContext = buildErrorContext(task, error);

        // 2. 分类错误
        ErrorType errorType = classifyError(error);

        // 3. 收集诊断信息
        Map<String, Object> diagnostics = collectDiagnostics(task);

        // 4. 分析影响范围
        ImpactAnalysis impact = analyzeImpact(task);

        return FailureAnalysis.builder()
                .taskId(task.getId())
                .errorType(errorType)
                .errorContext(errorContext)
                .diagnostics(diagnostics)
                .impact(impact)
                .build();
    }

    /**
     * 选择恢复策略
     */
    private RecoveryStrategy selectRecoveryStrategy(FailureAnalysis analysis) {
        switch (analysis.getErrorType()) {
            case NODE_FAILURE:
                return new NodeFailureRecoveryStrategy();
            case NETWORK_ERROR:
                return new NetworkErrorRecoveryStrategy();
            case DATA_ERROR:
                return new DataErrorRecoveryStrategy();
            case TIMEOUT:
                return new TimeoutRecoveryStrategy();
            default:
                return new DefaultRecoveryStrategy();
        }
    }

    /**
     * 执行恢复策略
     */
    private void executeRecovery(CollectTask task, RecoveryStrategy strategy) {
        String taskId = task.getId();
        log.info("Executing recovery for task: {}", taskId);

        try {
            // 1. 准备恢复
            RecoveryContext context = prepareRecovery(task);

            // 2. 执行恢复步骤
            strategy.recover(context);

            // 3. 更新任务状态
            updateTaskStatus(task, TaskStatus.RECOVERED);

            // 4. 记录恢复日志
            logRecovery(task, context);

        } catch (Exception e) {
            log.error("Recovery execution failed: {}", taskId, e);
            throw new RecoveryException("Recovery failed: " + e.getMessage());
        }
    }

    /**
     * 处理恢复失败
     */
    private void handleRecoveryFailure(CollectTask task, Exception error) {
        String taskId = task.getId();
        log.error("Recovery failed for task: {}", taskId, error);

        try {
            // 1. 更新任务状态
            updateTaskStatus(task, TaskStatus.RECOVERY_FAILED);

            // 2. 记录失败信息
            logRecoveryFailure(task, error);

            // 3. 发送告警
            sendRecoveryAlert(task, error);

            // 4. 触发人工介入
            triggerManualIntervention(task);

        } catch (Exception e) {
            log.error("Handle recovery failure failed: {}", taskId, e);
        }
    }
}