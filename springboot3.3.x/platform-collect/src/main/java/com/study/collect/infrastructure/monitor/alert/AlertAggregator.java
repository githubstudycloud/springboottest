package com.study.collect.infrastructure.monitor.alert;

import com.study.collect.common.annotation.monitor.Alert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 告警聚合器(续)
 */
@Slf4j
@Component
public class AlertAggregator {

    private List<Alert> aggregateAlerts() {
        // 1. 按规则分组
        Map<String, List<Alert>> alertsByRule = alertQueue.stream()
                .collect(Collectors.groupingBy(Alert::getRuleId));

        // 2. 对每组告警进行聚合
        return alertsByRule.entrySet().stream()
                .map(this::aggregateRuleAlerts)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private Alert aggregateRuleAlerts(Map.Entry<String, List<Alert>> entry) {
        List<Alert> alerts = entry.getValue();
        if (alerts.isEmpty()) {
            return null;
        }

        // 1. 获取第一个告警作为模板
        Alert template = alerts.get(0);

        // 2. 聚合相同规则的告警
        return Alert.builder()
                .ruleId(template.getRuleId())
                .name(template.getName())
                .level(template.getLevel())
                .metric(template.getMetric())
                .count(alerts.size())
                .firstTime(alerts.stream()
                        .map(Alert::getCreateTime)
                        .min(LocalDateTime::compareTo)
                        .orElse(null))
                .lastTime(alerts.stream()
                        .map(Alert::getCreateTime)
                        .max(LocalDateTime::compareTo)
                        .orElse(null))
                .message(buildAggregatedMessage(alerts))
                .build();
    }

    private String buildAggregatedMessage(List<Alert> alerts) {
        if (alerts.size() == 1) {
            return alerts.get(0).getMessage();
        }

        return String.format("%s (Occurred %d times in last %d minutes)",
                alerts.get(0).getMessage(),
                alerts.size(),
                WINDOW_SIZE.toMinutes());
    }
}
