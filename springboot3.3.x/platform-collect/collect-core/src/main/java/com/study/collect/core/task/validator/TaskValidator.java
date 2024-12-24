package com.study.collect.core.task.validator;

import com.study.collect.core.task.definition.ShardingConfig;
import com.study.collect.core.task.definition.TaskDefinition;
import com.study.collect.core.task.exception.TaskValidationException;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class TaskValidator {

    public void validate(TaskDefinition task) {
        // 基础参数校验
        if (task == null) {
            throw new TaskValidationException("任务定义不能为空");
        }

        if (!StringUtils.hasText(task.getTaskId())) {
            throw new TaskValidationException("任务ID不能为空");
        }

        if (!StringUtils.hasText(task.getTaskHandler())) {
            throw new TaskValidationException("任务处理器不能为空");
        }

        // 调度参数校验
        if (StringUtils.hasText(task.getCronExpression())) {
            validateCronExpression(task.getCronExpression());
        }

        // 分片参数校验
        if (task.getSharding() != null && task.getSharding().isEnabled()) {
            validateShardingConfig(task.getSharding());
        }
    }

    private void validateCronExpression(String cronExpression) {
        try {
            new CronTrigger(cronExpression);
        } catch (IllegalArgumentException e) {
            throw new TaskValidationException("无效的CRON表达式: " + cronExpression);
        }
    }

    private void validateShardingConfig(ShardingConfig sharding) {
        if (sharding.getTotal() == null || sharding.getTotal() <= 0) {
            throw new TaskValidationException("分片总数必须大于0");
        }
    }
}