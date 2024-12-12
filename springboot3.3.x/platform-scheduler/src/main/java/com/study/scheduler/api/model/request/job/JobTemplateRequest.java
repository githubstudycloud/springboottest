package com.study.scheduler.api.model.request.job;

import com.study.scheduler.domain.entity.job.JobTemplate;
import lombok.Data;

// 用于管理任务模板的请求模型。
@Data
public class JobTemplateRequest {
    private String name;
    private String description;
    private String type;

    public JobTemplate toEntity() {
        JobTemplate template = new JobTemplate();
        template.setName(this.name);
        template.setDescription(this.description);
        template.setType(this.type);
        return template;
    }
}
