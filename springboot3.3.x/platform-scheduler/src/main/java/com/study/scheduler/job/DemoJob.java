package com.study.scheduler.job;

import com.study.scheduler.entity.JobInfo;
import com.study.scheduler.entity.JobLog;
import com.study.scheduler.mapper.JobLogMapper;
import com.study.scheduler.service.JobService;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DemoJob extends BaseJob {
    // 执行查询日志
    @Autowired
    private JobLogMapper jobLogMapper;
    // 创建新任务
    @Autowired
    private JobService jobService;

    @Override
    protected void doExecute(JobExecutionContext context) throws Exception {
        // 获取任务参数
        String parameter = context.getJobDetail().getJobDataMap().getString("parameter");
        logger.info("执行示例任务，参数：{}", parameter);

        // 模拟任务执行
        Thread.sleep(1000);

        // 任务逻辑...
    }

    public List<JobLog> getJobLogs(String jobName, String jobGroup) {
        return jobLogMapper.findRecentLogs(jobName, jobGroup, 10);
    }

    public void createJob() throws Exception {
        JobInfo jobInfo = new JobInfo();
        jobInfo.setJobName("demoJob");
        jobInfo.setJobGroup("demo");
        jobInfo.setJobClass("com.example.scheduler.job.DemoJob");
        jobInfo.setCronExpression("0 0/5 * * * ?");
        jobInfo.setDescription("示例任务");
        jobInfo.setParameter("{\"key\":\"value\"}");
        jobService.addJob(jobInfo);
    }
}