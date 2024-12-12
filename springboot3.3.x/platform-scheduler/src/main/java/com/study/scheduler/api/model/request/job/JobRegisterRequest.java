package com.study.scheduler.api.model.request.job;

import com.study.scheduler.domain.entity.job.JobDefinition;
import lombok.Data;
// 用于注册任务的请求模型。
@Data
public class JobRegisterRequest {
    private String name;
    private String type;
    private String cronExpression;
    private String variables;

    public JobDefinition toEntity() {
        JobDefinition jobDefinition = new JobDefinition();
        jobDefinition.setName(this.name);
        jobDefinition.setType(this.type);
        jobDefinition.setCronExpression(this.cronExpression);
        return jobDefinition;
    }
}
