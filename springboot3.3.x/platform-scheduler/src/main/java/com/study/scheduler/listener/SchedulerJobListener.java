package com.study.scheduler.listener;


import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SchedulerJobListener implements JobListener {
    private static final Logger logger = LoggerFactory.getLogger(SchedulerJobListener.class);

    @Override
    public String getName() {
        return "GlobalJobListener";
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        String jobName = context.getJobDetail().getKey().toString();
        logger.info("任务准备执行: {}", jobName);
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        String jobName = context.getJobDetail().getKey().toString();
        logger.warn("任务被否决: {}", jobName);
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        String jobName = context.getJobDetail().getKey().toString();
        if (jobException == null) {
            logger.info("任务执行完成: {}", jobName);
        } else {
            logger.error("任务执行失败: {}", jobName, jobException);
        }
    }
}