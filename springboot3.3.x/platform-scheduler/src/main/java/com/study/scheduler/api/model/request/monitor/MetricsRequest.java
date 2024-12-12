package com.study.scheduler.api.model.request.monitor;

import lombok.Data;
// 用于查询监控指标的请求模型。
@Data
public class MetricsRequest {
    private String metricName; // 指标名称
    private Long jobId;        // 可选的任务ID
    private Long startTime;    // 起始时间戳
    private Long endTime;      // 结束时间戳
}
