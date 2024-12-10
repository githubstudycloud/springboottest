package com.study.collect.entity;

import com.study.collect.enums.TaskStatus;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "task_logs")
public class TaskLog {
    @Id
    private String id;
    private String taskId;
    private TaskStatus status;
    private String message;
    private Integer responseCode;
    private Long executionTime;
    private Date createTime;
    private String serverIp;
}