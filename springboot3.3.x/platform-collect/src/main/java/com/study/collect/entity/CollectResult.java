package com.study.collect.entity;

import com.study.collect.enums.TaskStatus;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "collect_results")
public class CollectResult {
    @Id
    private String id;
    private String taskId;
    private TaskStatus status;
    private Integer statusCode;
    private String responseBody;
    private String errorMessage;
    private Date createTime;
    private Integer retryCount;
}