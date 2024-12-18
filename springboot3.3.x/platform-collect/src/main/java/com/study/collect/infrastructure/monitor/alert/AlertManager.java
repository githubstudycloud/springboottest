package com.study.collect.infrastructure.monitor.alert;

// 告警管理器

import com.study.collect.common.annotation.monitor.Alert;
import com.study.collect.common.enums.AlertLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 告警管理器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AlertManager {

    private final AlertRuleRepository ruleRepository;
    private final AlertNotifier notifier;
    private final MetricsCollector metricsCollector;
    private final AlertAggregator aggregator;

    /**
     * 定时检查告警
     */
    @Scheduled(fixedRate = 60000) // 每分钟检查
    public void checkAlerts() {
        try {
            // 1. 获取所有告警规则
            List<AlertRule> rules = ruleRepository.findAllEnabled();

            // 2. 检查每个规则
            rules.forEach(this::checkRule);

            // 3. 聚合告警
            List<Alert> alerts = aggregator.aggregate();

            // 4. 发送告警
            sendAlerts(alerts);

        } catch (Exception e) {
            log.error("Check alerts failed", e);
        }
    }

    private void checkRule(AlertRule rule) {
        try {
            // 1. 获取指标值
            double value = metricsCollector.getMetricValue(rule.getMetric());

            // 2. 检查是否触发
            if (isTriggered(rule, value)) {
                // 3. 创建告警
                Alert alert = createAlert(rule, value);

                // 4. 添加到聚合器
                aggregator.add(alert);
            }
        } catch (Exception e) {
            log.error("Check rule failed: {}", rule.getName(), e);
        }
    }

    private boolean isTriggered(AlertRule rule, double value) {
        switch (rule.getOperator()) {
            case GREATER_THAN:
                return value > rule.getThreshold();
            case LESS_THAN:
                return value < rule.getThreshold();
            case EQUALS:
                return Math.abs(value - rule.getThreshold()) < 0.0001;
            default:
                return false;
        }
    }

    private Alert createAlert(AlertRule rule, double value) {
        return Alert.builder()
                .id(UUID.randomUUID().toString())
                .ruleId(rule.getId())
                .name(rule.getName())
                .level(rule.getLevel())
                .metric(rule.getMetric())
                .value(value)
                .threshold(rule.getThreshold())
                .message(buildAlertMessage(rule, value))
                .createTime(LocalDateTime.now())
                .build();
    }

    private void sendAlerts(List<Alert> alerts) {
        if (CollectionUtils.isEmpty(alerts)) {
            return;
        }

        // 1. 按级别分组
        Map<AlertLevel, List<Alert>> alertsByLevel = alerts.stream()
                .collect(Collectors.groupingBy(Alert::getLevel));

        // 2. 发送不同级别的告警
        alertsByLevel.forEach((level, levelAlerts) -> {
            try {
                switch (level) {
                    case CRITICAL:
                        notifier.sendUrgent(levelAlerts);
                        break;
                    case WARNING:
                        notifier.sendWarning(levelAlerts);
                        break;
                    case INFO:
                        notifier.sendInfo(levelAlerts);
                        break;
                }
            } catch (Exception e) {
                log.error("Send alerts failed, level: {}", level, e);
            }
        });
    }
}
//
///**
// * 告警管理器
// */
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class AlertManager {
//
//    private final AlertNotifier alertNotifier;
//    private final AlertRepository alertRepository;
//    private final AlertAggregator alertAggregator;
//
//    /**
//     * 发送告警
//     */
//    public void sendAlert(Alert alert) {
//        try {
//            // 1. 保存告警记录
//            alertRepository.save(alert);
//
//            // 2. 聚合告警
//            List<Alert> aggregatedAlerts =
//                    alertAggregator.aggregate(alert);
//
//            // 3. 发送通知
//            if (!aggregatedAlerts.isEmpty()) {
//                sendNotification(aggregatedAlerts);
//            }
//
//        } catch (Exception e) {
//            log.error("Send alert failed", e);
//        }
//    }
//
//    /**
//     * 发送告警通知
//     */
//    private void sendNotification(List<Alert> alerts) {
//        // 1. 构建通知内容
//        AlertNotification notification = buildNotification(alerts);
//
//        // 2. 根据级别发送
//        if (isUrgent(alerts)) {
//            alertNotifier.sendUrgent(notification);
//        } else {
//            alertNotifier.sendNormal(notification);
//        }
//    }
//}