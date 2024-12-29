package com.study.collect.core.task.service;

import com.study.collect.core.task.enums.TaskStatusEnum;
import com.study.collect.core.task.entity.TaskConfig;
import com.study.collect.core.task.mapper.TaskConfigMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskConfigService {

    private final TaskConfigMapper taskConfigMapper;

    @Transactional(rollbackFor = Exception.class)
    public void saveTaskConfig(TaskConfig config) {
        TaskConfig existConfig = taskConfigMapper.selectByCode(config.getTaskCode());
        if (existConfig == null) {
            log.info("新增任务配置: {}", config.getTaskCode());
            taskConfigMapper.insert(config);
        } else {
            log.info("更新任务配置: {}", config.getTaskCode());
            taskConfigMapper.update(config);
        }
    }

    public TaskConfig getTaskConfig(String taskCode) {
        return taskConfigMapper.selectByCode(taskCode);
    }

    public List<TaskConfig> getEnabledTaskConfigs() {
        return taskConfigMapper.selectEnabled();
    }

    @Transactional(rollbackFor = Exception.class)
    public void enableTask(String taskCode) {
        taskConfigMapper.updateStatus(taskCode, TaskStatusEnum.RUNNING.getCode());
        log.info("启用任务: {}", taskCode);
    }

    @Transactional(rollbackFor = Exception.class)
    public void disableTask(String taskCode) {
        taskConfigMapper.updateStatus(taskCode, TaskStatusEnum.INIT.getCode());
        log.info("禁用任务: {}", taskCode);
    }

    public void validateTaskConfig(TaskConfig config) {
        if (config.getShardTotal() == null || config.getShardTotal() < 1) {
            config.setShardTotal(1);
        }
        if (config.getRetryTimes() == null) {
            config.setRetryTimes(0);
        }
        if (config.getRetryInterval() == null) {
            config.setRetryInterval(0);
        }
        if (config.getTimeout() == null) {
            config.setTimeout(0);
        }
        if (config.getStatus() == null) {
            config.setStatus(TaskStatusEnum.INIT.getCode());
        }
    }
}