package com.study.scheduler.api.model.vo.job;

import com.study.scheduler.domain.entity.job.JobLog;
import lombok.Data;

import java.util.Date;
// 任务日志视图对象。
@Data
public class JobLogVO {
    private Long id;
    private Long jobId;
    private String status;
    private String result;
    private String error;
    private Date startTime;
    private Date endTime;

    public static JobLogVO fromEntity(JobLog jobLog) {
        JobLogVO vo = new JobLogVO();
        vo.setId(jobLog.getId());
        vo.setJobId(jobLog.getJobId());
        vo.setStatus(jobLog.getStatus());
        vo.setResult(jobLog.getResult());
        vo.setError(jobLog.getError());
        vo.setStartTime(jobLog.getStartTime());
        vo.setEndTime(jobLog.getEndTime());
        return vo;
    }
}
