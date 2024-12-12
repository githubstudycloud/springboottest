package com.study.scheduler.api.model.vo.monitor;

import lombok.Data;

@Data
public class MetricsVO {
    private String metricName;
    private double value;
}
