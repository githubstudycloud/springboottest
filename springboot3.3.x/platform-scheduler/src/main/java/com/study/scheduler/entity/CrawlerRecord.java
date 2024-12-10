package com.study.scheduler.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

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