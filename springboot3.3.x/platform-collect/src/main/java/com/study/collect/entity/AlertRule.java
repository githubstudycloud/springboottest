package com.study.collect.entity;

import com.study.collect.enums.AlertLevel;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "alert_rules")
public class AlertRule {
    @Id
    private String id;
    private String name;
    private String metric;
    private String condition;
    private Double threshold;
    private AlertLevel level;
    private boolean enabled;
    private Date createTime;
    private Date updateTime;
}