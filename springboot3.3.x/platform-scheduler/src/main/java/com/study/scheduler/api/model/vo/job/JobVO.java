package com.study.scheduler.api.model.vo.job;

import com.study.scheduler.domain.entity.job.JobDefinition;
import lombok.Data;

@Data
public class JobVO {
    private Long id;
    private String name;
    private String type;
    private String status;

    public static JobVO fromEntity(JobDefinition jobDefinition) {
        JobVO vo = new JobVO();
        vo.setId(jobDefinition.getId());
        vo.setName(jobDefinition.getName());
        vo.setType(jobDefinition.getType());
        vo.setStatus(jobDefinition.getStatus());
        return vo;
    }
}
