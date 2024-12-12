package com.study.scheduler.job;

import com.study.scheduler.service.EnhancedJobService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomJob implements Job {
    @Autowired
    private EnhancedJobService jobService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String taskId = context.getJobDetail().getKey().getName();
        try {
            jobService.executeJob(taskId);
        } catch (Exception e) {
            throw new JobExecutionException(e);
        }
    }
}