package com.study.scheduler.job;


import com.study.scheduler.entity.JobLog;
import com.study.scheduler.mapper.JobLogMapper;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.InetAddress;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public abstract class BaseJob implements Job {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    protected RedissonClient redissonClient;

    @Autowired
    protected JobLogMapper jobLogMapper;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String jobName = context.getJobDetail().getKey().getName();
        String lockKey = "scheduler:lock:" + jobName;
        RLock lock = redissonClient.getLock(lockKey);

        JobLog jobLog = new JobLog();
        jobLog.setJobName(jobName);
        jobLog.setStartTime(new Date());
        jobLog.setServerIp(getServerIp());

        try {
            // 尝试获取分布式锁，最多等待3秒，10分钟后自动释放
            boolean locked = lock.tryLock(3, 600, TimeUnit.SECONDS);
            if (!locked) {
                jobLog.setStatus(0);
                jobLog.setMessage("获取任务锁失败，任务已在其他节点执行");
                return;
            }

            // 执行具体任务
            doExecute(context);

            jobLog.setStatus(1);
            jobLog.setMessage("执行成功");

        } catch (Exception e) {
            logger.error("任务执行异常", e);
            jobLog.setStatus(2);
            jobLog.setExceptionInfo(e.getMessage());
            throw new JobExecutionException(e);

        } finally {
            jobLog.setEndTime(new Date());
            jobLog.setDuration(jobLog.getEndTime().getTime() - jobLog.getStartTime().getTime());
            jobLogMapper.insert(jobLog);

            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    protected abstract void doExecute(JobExecutionContext context) throws Exception;

    private String getServerIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "unknown";
        }
    }

}
