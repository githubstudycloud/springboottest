package com.study.scheduler.api.model.request.job;

import com.study.scheduler.domain.entity.job.JobDefinition;
import lombok.Data;
//用于更新任务的请求模型。
@Data
public class JobUpdateRequest {
    private String name;
    private String description;
    private String cronExpression;
    private String retryPolicy;

    public JobDefinition toEntity() {
        JobDefinition jobDefinition = new JobDefinition();
        jobDefinition.setName(this.name);
        jobDefinition.setDescription(this.description);
        jobDefinition.setCronExpression(this.cronExpression);
        jobDefinition.setRetryPolicy(this.retryPolicy);
        return jobDefinition;
    }
}
