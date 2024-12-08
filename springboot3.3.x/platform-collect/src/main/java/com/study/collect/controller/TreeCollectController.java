package com.study.collect.controller;

import com.study.collect.entity.CollectTask;
import com.study.collect.entity.TreeCollectRequest;
import com.study.collect.enums.CollectorType;
import com.study.collect.enums.TaskStatus;
import com.study.collect.service.TaskManagementService;
import com.study.common.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/api/tree")
public class TreeCollectController {

    @Autowired
    private TaskManagementService taskService;



    @PostMapping("/collect")
    public Result<CollectTask> createCollectionTask(@RequestBody TreeCollectRequest request) {
        try {
            CollectTask task = CollectTask.builder()
                    .name("Tree Structure Collection")
                    .url(request.getUrl())
                    .method(request.getMethod())
                    .headers(request.getHeaders())
                    .requestBody(request.getRequestBody())
                    .status(TaskStatus.CREATED)
                    .collectorType(CollectorType.TREE)  // 指定使用树状采集器
                    .maxRetries(3)
                    .retryInterval(1000L)
                    .createTime(new Date())
                    .updateTime(new Date())
                    .build();

            return Result.success(taskService.createTask(task));
        } catch (Exception e) {
            return Result.error("Failed to create collection task: " + e.getMessage());
        }
    }

//    @PostMapping("/collect")
//    public Result<CollectTask> createTreeCollectionTask(@RequestParam String projectId) {
//        try {
//            // 使用通用任务管理创建采集任务
//            CollectTask task = CollectTask.builder()
//                    .name("Tree Structure Collection")
//                    .parameter("projectId", projectId)
//                    .maxRetries(3)
//                    .retryInterval(1000L)
//                    .build();
//
//            CollectTask createdTask = taskService.createTask(task);
//            return Result.success(createdTask);
//        } catch (Exception e) {
//            return Result.error("Failed to create tree collection task: " + e.getMessage());
//        }
//    }

    @GetMapping("/result/{taskId}")
    public Result<CollectTask> getCollectionResult(@PathVariable String taskId) {
        try {
            Optional<CollectTask> task = taskService.getTask(taskId);
            return task.map(Result::success)
                    .orElse(Result.error("Task not found"));
        } catch (Exception e) {
            return Result.error("Failed to get task result: " + e.getMessage());
        }
    }
}
