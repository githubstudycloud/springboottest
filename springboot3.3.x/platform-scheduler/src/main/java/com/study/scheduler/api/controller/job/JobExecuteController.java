package com.study.scheduler.api.controller.job;

import com.study.scheduler.api.model.request.job.JobExecuteRequest;
import com.study.scheduler.core.executor.JobExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

// 处理任务注册和配置。
@RestController
@RequestMapping("/api/jobs/execute")
public class JobExecuteController {

    @Autowired
    private JobExecutor jobExecutor;

    @PostMapping("/{id}")
    public void executeJob(@PathVariable Long id, @RequestBody JobExecuteRequest request) {
        jobExecutor.executeJob(id, request.getVariables());
    }
}
