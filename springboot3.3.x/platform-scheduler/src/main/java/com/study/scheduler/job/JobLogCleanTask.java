package com.study.scheduler.job;


import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

@Component
public class JobLogCleanTask extends BaseJob {

    @Value("${scheduler.log.retain-days:30}")
    private int logRetainDays;

    @Override
    protected void doExecute(JobExecutionContext context) throws Exception {
        // 计算需要清理的日期
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -logRetainDays);
        Date cleanupDate = calendar.getTime();

        // 执行清理
        int count = jobLogMapper.cleanupOldLogs(cleanupDate);
        logger.info("清理任务日志完成，清理{}天前的日志，共清理{}条", logRetainDays, count);
    }
}