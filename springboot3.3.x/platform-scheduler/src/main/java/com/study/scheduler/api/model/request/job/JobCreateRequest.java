package com.study.scheduler.api.model.request.job;

import com.study.scheduler.domain.entity.job.JobDefinition;
import lombok.Data;

// 用于创建任务的请求模型。
@Data
public class JobCreateRequest {
    private String name;
    private String description;
    private String type;
    private String cronExpression;
    private String retryPolicy;

    public JobDefinition toEntity() {
        JobDefinition jobDefinition = new JobDefinition();
        jobDefinition.setName(this.name);
        jobDefinition.setDescription(this.description);
        jobDefinition.setType(this.type);
        jobDefinition.setCronExpression(this.cronExpression);
        jobDefinition.setRetryPolicy(this.retryPolicy);
        return jobDefinition;
    }
}
