package com.study.collect.model.request.collect;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 告警查询请求
 */
@Data
@Builder
public class AlertQueryRequest {

    private AlertLevel level;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Boolean handled;
}
