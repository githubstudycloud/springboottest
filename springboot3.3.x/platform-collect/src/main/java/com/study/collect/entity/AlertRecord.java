package com.study.collect.entity;

import com.study.collect.enums.AlertLevel;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "alert_records")
public class AlertRecord {
    @Id
    private String id;
    private String ruleId;
    private AlertLevel level;
    private String message;
    private boolean acknowledged;
    private Date createTime;
    private Date acknowledgeTime;
}