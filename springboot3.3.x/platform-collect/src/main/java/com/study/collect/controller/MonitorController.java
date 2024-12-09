package com.study.collect.controller;

import com.study.collect.entity.AlertMessage;
import com.study.collect.entity.SystemMetrics;
import com.study.collect.entity.TaskStatusStatistics;
import com.study.collect.monitor.MonitorService;
import com.study.common.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/monitor")
public class MonitorController {

    @Autowired
    private MonitorService monitorService;

    @GetMapping("/metrics")
    public Result<SystemMetrics> getSystemMetrics() {
        try {
            SystemMetrics metrics = monitorService.collectSystemMetrics();
            return Result.success(metrics);
        } catch (Exception e) {
            return Result.error("Failed to get system metrics: " + e.getMessage());
        }
    }

    @GetMapping("/tasks/status")
    public Result<TaskStatusStatistics> getTaskStatusStatistics() {
        try {
            TaskStatusStatistics stats = monitorService.getTaskStatusStatistics();
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error("Failed to get task statistics: " + e.getMessage());
        }
    }

    @GetMapping("/alerts")
    public Result<List<AlertMessage>> getActiveAlerts() {
        try {
            List<AlertMessage> alerts = monitorService.getActiveAlerts();
            return Result.success(alerts);
        } catch (Exception e) {
            return Result.error("Failed to get alerts: " + e.getMessage());
        }
    }

    @PostMapping("/alerts/{alertId}/acknowledge")
    public Result<Void> acknowledgeAlert(@PathVariable String alertId) {
        try {
            monitorService.acknowledgeAlert(alertId);
            return Result.success();
        } catch (Exception e) {
            return Result.error("Failed to acknowledge alert: " + e.getMessage());
        }
    }
}