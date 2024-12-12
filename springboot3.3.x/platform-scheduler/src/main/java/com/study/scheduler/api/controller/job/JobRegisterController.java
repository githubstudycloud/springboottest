package com.study.scheduler.api.controller.job;

import com.study.scheduler.api.model.request.job.JobRegisterRequest;
import com.study.scheduler.api.model.vo.job.JobVO;
import com.study.scheduler.core.manager.JobManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

// 负责任务的执行控制。
@RestController
@RequestMapping("/api/jobs/register")
public class JobRegisterController {

    @Autowired
    private JobManager jobManager;

    @PostMapping
    public JobVO registerJob(@RequestBody JobRegisterRequest request) {
        return JobVO.fromEntity(jobManager.registerJob(request.toEntity()));
    }
}
