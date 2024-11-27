package com.study.scheduler.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
@Document(collection = "crawler_records")
public class CrawlerRecord {
    @Id
    private String id;

    @Indexed
    private String taskId;
    private String url;
    private int statusCode;
    private String responseBody;
    private String errorMessage;
    private boolean success;
    private int retryCount;
    private Date createTime;
    private Date updateTime;
}