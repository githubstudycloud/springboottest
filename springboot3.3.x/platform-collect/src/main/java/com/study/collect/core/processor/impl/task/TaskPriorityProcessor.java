package com.study.collect.core.processor.impl.task;

import com.study.collect.common.enums.collect.ProcessType;
import com.study.collect.common.exception.collect.ProcessException;
import com.study.collect.core.processor.ProcessContext;
import com.study.collect.core.processor.base.AbstractProcessor;
import com.study.collect.domain.entity.task.CollectTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 任务优先级处理器
 */
@Slf4j
@Component
public class TaskPriorityProcessor extends AbstractProcessor {

    @Override
    public ProcessType getType() {
        return ProcessType.PRIORITY;
    }

    @Override
    public int getOrder() {
        return 20;
    }

    @Override
    protected void doProcess(ProcessContext context) {
        List<CollectTask> tasks = (List<CollectTask>) context.getRawData();

        try {
            // 1. 计算任务优先级
            tasks.forEach(this::calculatePriority);

            // 2. 按优先级排序
            List<CollectTask> sortedTasks = tasks.stream()
                    .sorted(Comparator.comparing(CollectTask::getPriority).reversed())
                    .collect(Collectors.toList());

            // 3. 更新上下文
            context.setResult(sortedTasks);

        } catch (Exception e) {
            log.error("Task priority process failed", e);
            throw new ProcessException("Task priority process failed: " + e.getMessage());
        }
    }

    private void calculatePriority(CollectTask task) {
        int basePriority = task.getPriority();

        // 1. 根据任务类型调整
        basePriority += getTypePriorityAdjustment(task.getType());

        // 2. 根据重试次数调整
        basePriority -= task.getRetryTimes() * 2;

        // 3. 根据等待时间调整
        long waitTime = Duration.between(task.getCreateTime(), LocalDateTime.now()).toMinutes();
        basePriority += Math.min(waitTime / 10, 10); // 最多加10分

        task.setPriority(basePriority);
    }

    private int getTypePriorityAdjustment(String type) {
        switch (type) {
            case "HIGH":
                return 10;
            case "MEDIUM":
                return 5;
            case "LOW":
                return 0;
            default:
                return 0;
        }
    }
}
