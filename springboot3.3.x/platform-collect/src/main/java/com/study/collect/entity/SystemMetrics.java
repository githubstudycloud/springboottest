package com.study.collect.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "system_metrics")
public class SystemMetrics {
    @Id
    private String id;
    private double cpuUsage;
    private double memoryUsage;
    private double diskUsage;
    private long totalTasks;
    private long runningTasks;
    private long failedTasks;
    private Date createTime;
}
