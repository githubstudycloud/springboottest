package com.study.scheduler.api.model.vo.job;

import com.study.scheduler.domain.entity.job.JobTemplate;
import lombok.Data;

@Data
public class JobTemplateVO {
    private Long id;
    private String name;
    private String type;

    public static JobTemplateVO fromEntity(JobTemplate template) {
        JobTemplateVO vo = new JobTemplateVO();
        vo.setId(template.getId());
        vo.setName(template.getName());
        vo.setType(template.getType());
        return vo;
    }
}
