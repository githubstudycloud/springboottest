package com.study.scheduler.job;

import com.study.scheduler.service.EnhancedJobService;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HttpJob implements Job {
    @Autowired
    private EnhancedJobService jobService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap data = context.getJobDetail().getJobDataMap();
        String taskId = context.getJobDetail().getKey().getName();
        try {
            jobService.executeJob(taskId);
        } catch (Exception e) {
            throw new JobExecutionException(e);
        }
    }
}
