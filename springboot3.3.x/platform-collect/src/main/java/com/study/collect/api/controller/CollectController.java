package com.study.collect.api.controller;

// 基础采集操作

import com.study.collect.common.enums.collect.TaskStatus;
import com.study.collect.common.model.result.Response;
import com.study.collect.domain.entity.task.CollectTask;
import com.study.collect.domain.repository.task.TaskRepository;
import com.study.collect.model.request.collect.CollectRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 采集任务控制器
 */
@RestController
@RequestMapping("/api/collect")
@RequiredArgsConstructor
@Slf4j
public class CollectController {

    private final CollectTaskService taskService;
    private final TaskRepository taskRepository;

    /**
     * 提交采集任务
     */
    @PostMapping("/submit")
    public Response<String> submitTask(@RequestBody @Valid CollectRequest request) {
        try {
            // 创建任务
            CollectTask task = createTask(request);
            // 提交任务
            taskService.submitTask(task);
            return Response.success(task.getId());
        } catch (Exception e) {
            log.error("Submit task failed", e);
            return Response.error("SUBMIT_FAILED", e.getMessage());
        }
    }

    /**
     * 停止任务
     */
    @PostMapping("/{taskId}/stop")
    public Response<Void> stopTask(@PathVariable String taskId) {
        try {
            taskService.stopTask(taskId);
            return Response.success();
        } catch (Exception e) {
            log.error("Stop task failed", e);
            return Response.error("STOP_FAILED", e.getMessage());
        }
    }

    /**
     * 获取任务状态
     */
    @GetMapping("/{taskId}/status")
    public Response<TaskStatus> getTaskStatus(@PathVariable String taskId) {
        try {
            TaskStatus status = taskService.getTaskStatus(taskId);
            return Response.success(status);
        } catch (Exception e) {
            log.error("Get task status failed", e);
            return Response.error("STATUS_QUERY_FAILED", e.getMessage());
        }
    }

    /**
     * 获取任务结果
     */
    @GetMapping("/{taskId}/result")
    public Response<CollectResult> getTaskResult(@PathVariable String taskId) {
        try {
            CollectResult result = taskService.getTaskResult(taskId);
            return Response.success(result);
        } catch (Exception e) {
            log.error("Get task result failed", e);
            return Response.error("RESULT_QUERY_FAILED", e.getMessage());
        }
    }

    private CollectTask createTask(CollectRequest request) {
        CollectTask task = new CollectTask();
        task.setId(UUID.randomUUID().toString());
        task.setName(request.getName());
        task.setType(request.getType());
        task.setStatus(TaskStatus.WAITING.name());
        task.setPriority(request.getPriority());
        task.setParams(request.getParams());
        task.setRetryTimes(0);
        task.setMaxRetryTimes(request.getMaxRetryTimes());
        task.setCreateTime(LocalDateTime.now());
        return task;
    }
}
