package com.study.scheduler.service;


import com.study.scheduler.config.RedisConfig;
import com.study.scheduler.entity.JobInfo;
import com.study.scheduler.mapper.JobInfoMapper;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class JobService {
    private static final Logger logger = LoggerFactory.getLogger(RedisConfig.class);
    private static final String JOB_LOCK_KEY_PREFIX = "scheduler:job:lock:";
    @Autowired
    private Scheduler scheduler;
    @Autowired
    private JobInfoMapper jobInfoMapper;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 添加任务
     */
    public void addJob(JobInfo jobInfo) throws Exception {
        // 创建JobDetail
        JobDetail jobDetail = JobBuilder.newJob(getJobClass(jobInfo.getJobClass()))
                .withIdentity(jobInfo.getJobName(), jobInfo.getJobGroup())
                .withDescription(jobInfo.getDescription())
                .build();

        // 创建Trigger
        CronTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(jobInfo.getJobName() + "_trigger", jobInfo.getJobGroup())
                .withSchedule(CronScheduleBuilder.cronSchedule(jobInfo.getCronExpression()))
                .build();

        // 调度任务
        scheduler.scheduleJob(jobDetail, trigger);

        // 保存任务信息
        jobInfoMapper.insert(jobInfo);
    }

    /**
     * 暂停任务
     */
    public void pauseJob(String jobName, String jobGroup) throws SchedulerException {
        scheduler.pauseJob(JobKey.jobKey(jobName, jobGroup));
        JobInfo jobInfo = new JobInfo();
        jobInfo.setStatus(0); // 暂停状态
        jobInfoMapper.updateByJobKey(jobName, jobGroup, jobInfo);
    }

    /**
     * 恢复任务
     */
    public void resumeJob(String jobName, String jobGroup) throws SchedulerException {
        scheduler.resumeJob(JobKey.jobKey(jobName, jobGroup));
        JobInfo jobInfo = new JobInfo();
        jobInfo.setStatus(1); // 运行状态
        jobInfoMapper.updateByJobKey(jobName, jobGroup, jobInfo);
    }

    /**
     * 删除任务
     */
    public void deleteJob(String jobName, String jobGroup) throws SchedulerException {
        scheduler.deleteJob(JobKey.jobKey(jobName, jobGroup));
        jobInfoMapper.deleteByJobKey(jobName, jobGroup);
    }

    /**
     * 修改任务cron表达式
     */
    public void updateJobCron(JobInfo jobInfo) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobInfo.getJobName() + "_trigger", jobInfo.getJobGroup());
        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);

        // 创建新的trigger
        CronTrigger newTrigger = trigger.getTriggerBuilder()
                .withIdentity(triggerKey)
                .withSchedule(CronScheduleBuilder.cronSchedule(jobInfo.getCronExpression()))
                .build();

        scheduler.rescheduleJob(triggerKey, newTrigger);
        jobInfoMapper.updateByJobKey(jobInfo.getJobName(), jobInfo.getJobGroup(), jobInfo);
    }

    private Class<? extends Job> getJobClass(String jobClass) throws Exception {
        return (Class<? extends Job>) Class.forName(jobClass);
    }

    /**
     * 使用Redis分布式锁执行任务
     */
    public void executeJob(String jobName, Runnable task) {
        String lockKey = JOB_LOCK_KEY_PREFIX + jobName;
        Boolean acquired = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, "LOCKED", Duration.ofMinutes(10));

        if (Boolean.TRUE.equals(acquired)) {
            try {
                task.run();
            } finally {
                redisTemplate.delete(lockKey);
            }
        } else {
            logger.warn("Job {} is already running on another instance", jobName);
        }
    }
}
