package com.study.scheduler.model.job;

import jakarta.persistence.Entity;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Entity
@Data
public class JobExecution {
    @Id
    private String id;
    private String jobDefId;
    private Date startTime;
    private Date endTime;
    private String status;
    private String result;
    private String error;
    private Integer retryCount;
}
