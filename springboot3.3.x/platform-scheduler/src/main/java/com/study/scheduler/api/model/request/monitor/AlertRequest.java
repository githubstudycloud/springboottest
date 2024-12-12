package com.study.scheduler.api.model.request.monitor;

import lombok.Data;
// 用于创建告警的请求模型。
@Data
public class AlertRequest {
    private String alertLevel; // 告警级别，例如 "INFO", "WARN", "ERROR"
    private String message;    // 告警信息
    private Long jobId;        // 可选的任务ID
}
