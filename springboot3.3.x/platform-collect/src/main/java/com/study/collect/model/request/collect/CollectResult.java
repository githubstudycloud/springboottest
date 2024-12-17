package com.study.collect.model.request.collect;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 采集结果
 */
@Data
@Builder
public class CollectResult {

    private String taskId;

    private Boolean success;

    private String message;

    private Object data;

    private LocalDateTime collectTime;

    public static CollectResult success(Object data) {
        return CollectResult.builder()
                .success(true)
                .data(data)
                .collectTime(LocalDateTime.now())
                .build();
    }

    public static CollectResult error(String message) {
        return CollectResult.builder()
                .success(false)
                .message(message)
                .collectTime(LocalDateTime.now())
                .build();
    }
}

/**
 * 对比结果
 */
@Data
@Builder
public class CompareResult {

    private String sourceId;

    private String targetId;

    private List<Difference> differences;

    private LocalDateTime compareTime;

    @Data
    @Builder
    public static class Difference {
        private String field;
        private Object sourceValue;
        private Object targetValue;
        private String description;
    }
}

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
