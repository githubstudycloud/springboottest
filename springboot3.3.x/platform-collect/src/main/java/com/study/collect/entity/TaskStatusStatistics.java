package com.study.collect.entity;

import lombok.Data;

import java.util.Date;

@Data
public class TaskStatusStatistics {
    private long totalTasks;
    private long runningTasks;
    private long completedTasks;
    private long failedTasks;
    private long stoppedTasks;
    private Date statisticsTime;
}
