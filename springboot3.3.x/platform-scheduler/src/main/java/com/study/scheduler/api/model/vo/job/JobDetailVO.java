package com.study.scheduler.api.model.vo.job;

import com.study.scheduler.domain.entity.job.JobDefinition;
import lombok.Data;

// 任务详情视图对象。
@Data
public class JobDetailVO {
    private Long id;
    private String name;
    private String description;
    private String type;
    private String cronExpression;
    private String status;
    private String retryPolicy;

    public static JobDetailVO fromEntity(JobDefinition jobDefinition) {
        JobDetailVO vo = new JobDetailVO();
        vo.setId(jobDefinition.getId());
        vo.setName(jobDefinition.getName());
        vo.setDescription(jobDefinition.getDescription());
        vo.setType(jobDefinition.getType());
        vo.setCronExpression(jobDefinition.getCronExpression());
        vo.setStatus(jobDefinition.getStatus());
        vo.setRetryPolicy(jobDefinition.getRetryPolicy());
        return vo;
    }
}
