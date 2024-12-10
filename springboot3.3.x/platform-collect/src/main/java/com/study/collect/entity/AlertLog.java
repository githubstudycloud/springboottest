package com.study.collect.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

// 添加告警日志实体类
@Data
@Document(collection = "alert_logs")
public class AlertLog {
    @Id
    private String id;
    private String alertId;
    private String operation;
    private Date operateTime;
}