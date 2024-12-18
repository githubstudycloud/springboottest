package com.study.collect.model.response.data;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 监控指标数据
 */
@Data
@Builder
public class MetricsData {

    private Long taskCount;

    private Long successCount;

    private Long failureCount;

    private Double successRate;

    private Double avgProcessTime;

    private Integer queueSize;

    private Double systemLoad;

    private Long memoryUsed;

    private LocalDateTime collectTime;
}