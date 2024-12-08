package com.study.collect.controller;

import com.study.collect.entity.CollectTask;
import com.study.collect.entity.TaskResult;
import com.study.collect.enums.TaskStatus;
import com.study.collect.service.TaskManagementService;
import com.study.common.util.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskManagementService taskService;

    public TaskController(TaskManagementService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public Result<CollectTask> createTask(@RequestBody CollectTask task) {
        try {
            CollectTask createdTask = taskService.createTask(task);
            return Result.success(createdTask);
        } catch (Exception e) {
            return Result.error("Failed to create task: " + e.getMessage());
        }
    }

    @PostMapping("/{taskId}/execute")
    public Result<TaskResult> executeTask(@PathVariable String taskId) {
        try {
            TaskResult result = taskService.executeTask(taskId);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("Failed to execute task: " + e.getMessage());
        }
    }

    @PostMapping("/{taskId}/stop")
    public Result<Void> stopTask(@PathVariable String taskId) {
        try {
            taskService.stopTask(taskId);
            return Result.success();
        } catch (Exception e) {
            return Result.error("Failed to stop task: " + e.getMessage());
        }
    }

    @PostMapping("/{taskId}/retry")
    public Result<TaskResult> retryTask(@PathVariable String taskId) {
        try {
            TaskResult result = taskService.retryTask(taskId);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("Failed to retry task: " + e.getMessage());
        }
    }

    @GetMapping("/{taskId}")
    public Result<CollectTask> getTask(@PathVariable String taskId) {
        return taskService.getTask(taskId)
                .map(Result::success)
                .orElse(Result.error("Task not found"));
    }

    @GetMapping("/status/{status}")
    public Result<List<CollectTask>> getTasksByStatus(@PathVariable TaskStatus status) {
        try {
            List<CollectTask> tasks = taskService.getTasksByStatus(status);
            return Result.success(tasks);
        } catch (Exception e) {
            return Result.error("Failed to get tasks: " + e.getMessage());
        }
    }
}

