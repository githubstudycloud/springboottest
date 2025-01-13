package com.study.collect.core.task.service;

import com.study.collect.core.task.enums.LogTypeEnum;
import com.study.collect.core.task.enums.TaskStatusEnum;
import com.study.collect.core.task.utils.InstanceIdGenerator;
import com.study.collect.core.task.entity.TaskConfig;
import com.study.collect.core.task.entity.TaskInstance;
import com.study.collect.core.task.entity.TaskLog;
import com.study.collect.core.task.mapper.TaskInstanceMapper;
import com.study.collect.core.task.mapper.TaskLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.List;

/**
 * TaskConfigService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskExecuteService {

    private final TaskInstanceMapper taskInstanceMapper;
    private final TaskLogMapper taskLogMapper;
    private final TaskConfigService taskConfigService;

    @Transactional(rollbackFor = Exception.class)
    public TaskInstance createTaskInstance(String taskCode, Integer shardIndex, String shardParam) {
        TaskConfig config = taskConfigService.getTaskConfig(taskCode);
        if (config == null) {
            throw new IllegalArgumentException("任务配置不存在: " + taskCode);
        }

        String instanceId = InstanceIdGenerator.generateInstanceId(taskCode);
        TaskInstance instance = new TaskInstance();
        instance.setInstanceId(instanceId);
        instance.setTaskCode(taskCode);
        instance.setShardIndex(shardIndex);
        instance.setShardTotal(config.getShardTotal());
        instance.setShardParam(shardParam);
        instance.setStatus(TaskStatusEnum.INIT.getCode());
        instance.setHostName(getHostName());
        instance.setStartTime(LocalDateTime.now());

        taskInstanceMapper.insert(instance);
        recordTaskLog(instanceId, taskCode, LogTypeEnum.START, "任务开始执行");

        return instance;
    }

    @Transactional(rollbackFor = Exception.class)
    public void completeTaskInstance(String instanceId, boolean success, String errorMsg) {
        TaskInstance instance = taskInstanceMapper.selectByInstanceId(instanceId);
        if (instance == null) {
            throw new IllegalArgumentException("任务实例不存在: " + instanceId);
        }

        LocalDateTime endTime = LocalDateTime.now();
        TaskStatusEnum status = success ? TaskStatusEnum.SUCCESS : TaskStatusEnum.FAILED;

        taskInstanceMapper.updateStatus(instanceId, status.getCode(), errorMsg);
        taskInstanceMapper.updateEndTime(instanceId, endTime);

        LogTypeEnum logType = success ? LogTypeEnum.RESULT : LogTypeEnum.ERROR;
        String logContent = success ? "任务执行成功" : "任务执行失败: " + errorMsg;
        recordTaskLog(instanceId, instance.getTaskCode(), logType, logContent);
    }

    public void recordTaskProgress(String instanceId, String progressInfo) {
        TaskInstance instance = taskInstanceMapper.selectByInstanceId(instanceId);
        if (instance != null) {
            recordTaskLog(instanceId, instance.getTaskCode(), LogTypeEnum.PROGRESS, progressInfo);
        }
    }

    private void recordTaskLog(String instanceId, String taskCode, LogTypeEnum logType, String content) {
        TaskLog log = new TaskLog(instanceId, taskCode, logType.getCode(), content);
        taskLogMapper.insert(log);
    }

    public List<TaskInstance> getRunningTasks() {
        return taskInstanceMapper.selectRunning();
    }

    public List<TaskLog> getTaskLogs(String instanceId) {
        return taskLogMapper.selectByInstanceId(instanceId);
    }

    private String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "unknown";
        }
    }
}