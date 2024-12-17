package com.study.collect.infrastructure.monitor.alert;

// 告警管理器

import com.study.collect.common.annotation.monitor.Alert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 告警管理器
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AlertManager {

    private final AlertNotifier notifier;

    @Scheduled(fixedRate = 60000) // 每分钟检查一次
    public void checkMetrics() {
        // 检查任务队列大小
        if (queueSize.value() > 1000) {
            sendAlert("Task queue is too large: " + queueSize.value());
        }

        // 检查失败率
        double failureRate = failureCounter.count() / taskCounter.count();
        if (failureRate > 0.1) {
            sendAlert("Task failure rate is too high: " + String.format("%.2f%%", failureRate * 100));
        }

        // 检查处理时间
        double avgProcessTime = processTimer.mean(TimeUnit.SECONDS);
        if (avgProcessTime > 30) {
            sendAlert("Average process time is too long: " + String.format("%.2f s", avgProcessTime));
        }
    }

    private void sendAlert(String message) {
        try {
            notifier.notify(new Alert(message, AlertLevel.WARNING));
            log.warn("Alert sent: {}", message);
        } catch (Exception e) {
            log.error("Failed to send alert", e);
        }
    }
}