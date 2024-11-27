package com.study.scheduler.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Map;

@Data
@Document(collection = "crawler_tasks")
public class CrawlerTask {
    @Id
    private String id;

    @Indexed
    private String name;
    private String url;
    private String method;
    private Map<String, String> headers;
    private String requestBody;
    private int retryCount;
    private int maxRetries;
    private long retryInterval;
    private boolean enabled;
    private Date createTime;
    private Date updateTime;
}
