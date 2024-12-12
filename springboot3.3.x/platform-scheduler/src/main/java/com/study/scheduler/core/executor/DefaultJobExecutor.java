package com.study.scheduler.core.executor;

import com.study.scheduler.domain.entity.job.JobContext;
import org.springframework.stereotype.Component;

@Component
public class DefaultJobExecutor implements JobExecutor {
    @Override
    public void execute(JobContext context) {
        // 默认任务执行逻辑
        System.out.println("Executing default job: " + context.getJobDefinition().getName());
    }
}
