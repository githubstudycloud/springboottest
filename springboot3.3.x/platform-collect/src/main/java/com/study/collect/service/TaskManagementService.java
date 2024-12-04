package com.study.collect.service;

import com.study.collect.core.collector.Collector;
import com.study.collect.entity.CollectTask;
import com.study.collect.entity.TaskResult;
import com.study.collect.enums.TaskStatus;
import com.study.collect.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

// 任务管理服务实现
@Service
public class TaskManagementService {
    private static final Logger logger = LoggerFactory.getLogger(TaskManagementService.class);

    @Autowired
    private Collector collector;

    @Autowired
    private TaskRepository taskRepository;

    public CollectTask createTask(CollectTask task) {
        task.setStatus(TaskStatus.CREATED);
        task.setCreateTime(new Date());
        task.setUpdateTime(new Date());

        CollectTask savedTask = taskRepository.save(task);
        logger.info("Created new task with ID: {}", savedTask.getId());

        return savedTask;
    }

    public TaskResult executeTask(String taskId) {
        Optional<CollectTask> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isEmpty()) {
            return TaskResult.builder()
                    .taskId(taskId)
                    .status(TaskStatus.FAILED)
                    .message("Task not found")
                    .build();
        }

        CollectTask task = taskOpt.get();

        // 检查任务状态
        if (TaskStatus.RUNNING.equals(task.getStatus())) {
            return TaskResult.builder()
                    .taskId(taskId)
                    .status(TaskStatus.FAILED)
                    .message("Task is already running")
                    .build();
        }

        // 执行任务
        TaskResult result = collector.collect(task);

        // 更新任务状态
        task.setStatus(result.getStatus());
        task.setUpdateTime(new Date());
        taskRepository.save(task);

        return result;
    }

    public List<CollectTask> getTasksByStatus(TaskStatus status) {
        return taskRepository.findByStatus(status);
    }

    public Optional<CollectTask> getTask(String taskId) {
        return taskRepository.findById(taskId);
    }

    public void stopTask(String taskId) {
        Optional<CollectTask> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isPresent()) {
            CollectTask task = taskOpt.get();
            collector.stopTask(taskId);

            task.setStatus(TaskStatus.STOPPED);
            task.setUpdateTime(new Date());
            taskRepository.save(task);

            logger.info("Stopped task: {}", taskId);
        }
    }

    public List<CollectTask> getFailedTasks() {
        return taskRepository.findByStatus(TaskStatus.FAILED);
    }

    public TaskResult retryTask(String taskId) {
        Optional<CollectTask> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isEmpty()) {
            return TaskResult.builder()
                    .taskId(taskId)
                    .status(TaskStatus.FAILED)
                    .message("Task not found")
                    .build();
        }

        CollectTask task = taskOpt.get();
        if (task.getRetryCount() >= task.getMaxRetries()) {
            return TaskResult.builder()
                    .taskId(taskId)
                    .status(TaskStatus.FAILED)
                    .message("Max retries exceeded")
                    .build();
        }

        task.setRetryCount(task.getRetryCount() + 1);
        task.setUpdateTime(new Date());
        taskRepository.save(task);

        return executeTask(taskId);
    }
}
