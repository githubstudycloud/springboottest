package com.study.scheduler.job;

import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

@Component
public class DemoJob extends BaseJob {
    @Override
    protected void doExecute(JobExecutionContext context) throws Exception {
        // 获取任务参数
        String parameter = context.getJobDetail().getJobDataMap().getString("parameter");
        logger.info("执行示例任务，参数：{}", parameter);

        // 模拟任务执行
        Thread.sleep(1000);

        // 任务逻辑...
    }
}