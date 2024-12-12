package com.study.scheduler.api.controller.job;

import com.study.scheduler.api.model.request.job.JobCreateRequest;
import com.study.scheduler.api.model.request.job.JobUpdateRequest;
import com.study.scheduler.api.model.vo.job.JobVO;
import com.study.scheduler.core.manager.JobManager;
import com.study.scheduler.domain.entity.job.JobDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 负责任务的基础操作，如 CRUD。
@RestController
@RequestMapping("/api/jobs")
public class JobController {

    @Autowired
    private JobManager jobManager;

    @PostMapping
    public JobVO createJob(@RequestBody JobCreateRequest request) {
        JobDefinition jobDefinition = jobManager.createJob(request.toEntity());
        return JobVO.fromEntity(jobDefinition);
    }

    @PutMapping("/{id}")
    public JobVO updateJob(@PathVariable Long id, @RequestBody JobUpdateRequest request) {
        JobDefinition jobDefinition = jobManager.updateJob(id, request.toEntity());
        return JobVO.fromEntity(jobDefinition);
    }

    @DeleteMapping("/{id}")
    public void deleteJob(@PathVariable Long id) {
        jobManager.deleteJob(id);
    }

    @GetMapping("/{id}")
    public JobVO getJob(@PathVariable Long id) {
        return JobVO.fromEntity(jobManager.getJob(id));
    }

    @GetMapping
    public List<JobVO> listJobs() {
        return JobVO.fromEntities(jobManager.listJobs());
    }
}
