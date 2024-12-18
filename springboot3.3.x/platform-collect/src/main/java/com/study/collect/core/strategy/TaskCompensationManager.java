package com.study.collect.core.strategy;

import com.study.collect.common.annotation.lock.DistributedLock;
import com.study.collect.domain.entity.task.CollectTask;
import com.study.collect.domain.repository.task.TaskRepository;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 任务补偿管理器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TaskCompensationManager {

    private final TaskRepository taskRepository;
    private final ShardRepository shardRepository;
    private final CompensationLogRepository compensationLogRepository;
    private final DistributedLock distributedLock;
    private final MetricsCollector metricsCollector;

    /**
     * 执行补偿
     */
    public CompensationResult compensate(String taskId) {
        String lockKey = "compensation:lock:" + taskId;
        Timer.Sample timer = metricsCollector.startTimer("task_compensation");

        try {
            // 1. 获取补偿锁
            if (!distributedLock.tryLock(lockKey, 30000)) {
                throw new CompensationException("Failed to acquire compensation lock");
            }

            try {
                // 2. 检查补偿必要性
                if (!needsCompensation(taskId)) {
                    return CompensationResult.notNeeded();
                }

                // 3. 创建补偿计划
                CompensationPlan plan = createCompensationPlan(taskId);

                // 4. 执行补偿
                return executeCompensation(plan);

            } finally {
                distributedLock.unlock(lockKey);
            }
        } catch (Exception e) {
            log.error("Task compensation failed: {}", taskId, e);
            throw new CompensationException("Compensation failed: " + e.getMessage());
        } finally {
            metricsCollector.stopTimer(timer);
        }
    }

    /**
     * 检查补偿必要性
     */
    private boolean needsCompensation(String taskId) {
        // 1. 获取任务信息
        CollectTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));

        // 2. 检查任务状态
        if (!isCompensableStatus(task.getStatus())) {
            return false;
        }

        // 3. 检查数据完整性
        DataIntegrityResult integrity = checkDataIntegrity(task);
        if (integrity.isComplete()) {
            return false;
        }

        // 4. 检查补偿次数
        return checkCompensationAttempts(task);
    }

    /**
     * 创建补偿计划
     */
    private CompensationPlan createCompensationPlan(String taskId) {
        // 1. 获取任务信息
        CollectTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));

        // 2. 分析缺失数据
        List<DataRange> missingRanges = analyzeMissingData(task);

        // 3. 规划补偿步骤
        List<CompensationStep> steps = planCompensationSteps(missingRanges);

        return CompensationPlan.builder()
                .taskId(taskId)
                .steps(steps)
                .startTime(LocalDateTime.now())
                .timeout(calculateTimeout(steps))
                .build();
    }

    /**
     * 执行补偿计划
     */
    private CompensationResult executeCompensation(CompensationPlan plan) {
        String taskId = plan.getTaskId();
        CompensationContext context = new CompensationContext(plan);

        try {
            // 1. 初始化补偿
            initializeCompensation(context);

            // 2. 执行补偿步骤
            for (CompensationStep step : plan.getSteps()) {
                executeCompensationStep(step, context);
                if (context.isAborted()) {
                    break;
                }
            }

            // 3. 验证补偿结果
            verifyCompensationResult(context);

            // 4. 提交补偿
            return commitCompensation(context);

        } catch (Exception e) {
            // 5. 处理补偿失败
            handleCompensationFailure(context, e);
            throw e;
        } finally {
            // 6. 清理资源
            cleanupCompensation(context);
        }
    }

    /**
     * 执行补偿步骤
     */
    private void executeCompensationStep(CompensationStep step, CompensationContext context) {
        String stepId = step.getId();
        log.info("Executing compensation step: {}", stepId);

        try {
            // 1. 准备步骤执行
            prepareStepExecution(step, context);

            // 2. 执行补偿逻辑
            step.execute(context);

            // 3. 验证步骤结果
            verifyStepResult(step, context);

            // 4. 记录执行日志
            logStepExecution(step, context);

        } catch (Exception e) {
            log.error("Compensation step failed: {}", stepId, e);
            handleStepFailure(step, context, e);
        }
    }

    /**
     * 提交补偿
     */
    private CompensationResult commitCompensation(CompensationContext context) {
        String taskId = context.getTaskId();
        log.info("Committing compensation for task: {}", taskId);

        try {
            // 1. 保存补偿数据
            saveCompensationData(context);

            // 2. 更新任务状态
            updateTaskAfterCompensation(context);

            // 3. 发送补偿完成事件
            publishCompensationEvent(context);

            return buildCompensationResult(context);

        } catch (Exception e) {
            log.error("Commit compensation failed: {}", taskId, e);
            throw new CompensationException("Failed to commit compensation", e);
        }
    }

    /**
     * 处理补偿失败
     */
    private void handleCompensationFailure(CompensationContext context, Exception error) {
        String taskId = context.getTaskId();
        log.error("Handling compensation failure for task: {}", taskId, error);

        try {
            // 1. 回滚已完成的步骤
            rollbackCompensation(context);

            // 2. 更新任务状态
            updateTaskStatus(taskId, TaskStatus.COMPENSATION_FAILED);

            // 3. 记录失败信息
            logCompensationFailure(context, error);

            // 4. 发送告警
            sendCompensationAlert(context, error);

        } catch (Exception e) {
            log.error("Handle compensation failure failed: {}", taskId, e);
        }
    }
}
