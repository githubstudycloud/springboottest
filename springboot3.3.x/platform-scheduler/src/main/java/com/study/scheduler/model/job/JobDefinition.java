package com.study.scheduler.model.job;

import jakarta.persistence.Entity;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Entity
@Data
public class JobDefinition {
    @jakarta.persistence.Id
    @Id
    private String id;
    private String name;
    private String description;
    private String jobClass;
    private String cronExpression;
    private String jobData;
    private Integer status;
    private Date createTime;
    private Date updateTime;
}