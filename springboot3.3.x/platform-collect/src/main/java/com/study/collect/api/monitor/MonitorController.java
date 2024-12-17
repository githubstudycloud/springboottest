package com.study.collect.api.monitor;

import com.study.collect.common.annotation.monitor.Alert;
import com.study.collect.common.model.result.Response;
import com.study.collect.infrastructure.monitor.alert.AlertManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 监控控制器
 */
@RestController
@RequestMapping("/api/monitor")
@RequiredArgsConstructor
@Slf4j
public class MonitorController {

    private final MetricsCollector metricsCollector;
    private final AlertManager alertManager;

    /**
     * 获取监控指标
     */
    @GetMapping("/metrics")
    public Response<MetricsData> getMetrics() {
        try {
            MetricsData metrics = metricsCollector.collectMetrics();
            return Response.success(metrics);
        } catch (Exception e) {
            log.error("Get metrics failed", e);
            return Response.error("METRICS_QUERY_FAILED", e.getMessage());
        }
    }

    /**
     * 查询告警信息
     */
    @GetMapping("/alerts")
    public Response<List<Alert>> getAlerts(AlertQueryRequest request) {
        try {
            List<Alert> alerts = alertManager.queryAlerts(request);
            return Response.success(alerts);
        } catch (Exception e) {
            log.error("Query alerts failed", e);
            return Response.error("ALERT_QUERY_FAILED", e.getMessage());
        }
    }
}