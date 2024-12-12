package com.study.scheduler.core.executor;

import com.study.scheduler.domain.entity.job.JobContext;

public interface JobExecutor {
    void execute(JobContext context);
}
