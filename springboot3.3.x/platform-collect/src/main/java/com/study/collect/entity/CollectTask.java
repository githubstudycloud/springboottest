package com.study.collect.entity;

import com.study.collect.enums.CollectorType;
import com.study.collect.enums.TaskStatus;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Map;


@Data
@Builder
@Document(collection = "collect_tasks")
public class CollectTask {
    @Id
    private String id;
    private String name;
    private String url;
    private String method;
    private Map<String, String> headers;
    private String requestBody;
    private TaskStatus status;
    private Integer retryCount;
    private Integer maxRetries;
    private Long retryInterval;
    private Date createTime;
    private Date updateTime;
    private CollectorType collectorType = CollectorType.DEFAULT; // 默认类型
    private String lastError;

}


