package com.study.collect.infrastructure.monitor.alert;

// 告警通知器

import com.study.collect.common.annotation.monitor.Alert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 告警通知器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AlertNotifier {

    private final DingTalkClient dingTalkClient;
    private final EmailSender emailSender;
    private final AlertRepository alertRepository;

    /**
     * 发送紧急告警
     */
    public void sendUrgent(List<Alert> alerts) {
        // 1. 保存告警记录
        saveAlerts(alerts);

        // 2. 发送钉钉通知
        sendDingTalkNotification(alerts, true);

        // 3. 发送邮件通知
        sendEmailNotification(alerts, true);
    }

    /**
     * 发送警告级别告警
     */
    public void sendWarning(List<Alert> alerts) {
        // 1. 保存告警记录
        saveAlerts(alerts);

        // 2. 发送钉钉通知
        sendDingTalkNotification(alerts, false);

        // 3. 发送邮件通知
        sendEmailNotification(alerts, false);
    }

    /**
     * 发送信息级别告警
     */
    public void sendInfo(List<Alert> alerts) {
        // 1. 保存告警记录
        saveAlerts(alerts);

        // 2. 只发送钉钉通知
        sendDingTalkNotification(alerts, false);
    }

    private void saveAlerts(List<Alert> alerts) {
        try {
            alertRepository.saveAll(alerts);
        } catch (Exception e) {
            log.error("Save alerts failed", e);
        }
    }

    private void sendDingTalkNotification(List<Alert> alerts, boolean isUrgent) {
        try {
            String message = buildDingTalkMessage(alerts, isUrgent);
            DingTalkMessage dtMessage = DingTalkMessage.builder()
                    .title("系统告警通知")
                    .text(message)
                    .isAtAll(isUrgent)
                    .build();

            dingTalkClient.send(dtMessage);
        } catch (Exception e) {
            log.error("Send DingTalk notification failed", e);
        }
    }

    private void sendEmailNotification(List<Alert> alerts, boolean isUrgent) {
        try {
            String subject = isUrgent ? "【紧急】系统告警通知" : "系统告警通知";
            String content = buildEmailContent(alerts);

            EmailMessage email = EmailMessage.builder()
                    .subject(subject)
                    .content(content)
                    .to(getAlertReceivers(isUrgent))
                    .build();

            emailSender.send(email);
        } catch (Exception e) {
            log.error("Send email notification failed", e);
        }
    }

    private String buildDingTalkMessage(List<Alert> alerts, boolean isUrgent) {
        StringBuilder sb = new StringBuilder();
        sb.append("### ").append(isUrgent ? "【紧急】" : "").append("系统告警通知\n\n");

        alerts.forEach(alert -> {
            sb.append("- **").append(alert.getName()).append("**\n");
            sb.append("  - 级别: ").append(alert.getLevel()).append("\n");
            sb.append("  - 指标: ").append(alert.getMetric()).append("\n");
            sb.append("  - 详情: ").append(alert.getMessage()).append("\n");
            sb.append("  - 时间: ").append(formatDateTime(alert.getCreateTime())).append("\n\n");
        });

        return sb.toString();
    }

    private String buildEmailContent(List<Alert> alerts) {
        StringBuilder sb = new StringBuilder();
        sb.append("<h2>系统告警通知</h2>");
        sb.append("<table border='1' cellspacing='0' cellpadding='5'>");
        sb.append("<tr><th>告警名称</th><th>级别</th><th>指标</th><th>详情</th><th>时间</th></tr>");

        alerts.forEach(alert -> {
            sb.append("<tr>");
            sb.append("<td>").append(alert.getName()).append("</td>");
            sb.append("<td>").append(alert.getLevel()).append("</td>");
            sb.append("<td>").append(alert.getMetric()).append("</td>");
            sb.append("<td>").append(alert.getMessage()).append("</td>");
            sb.append("<td>").append(formatDateTime(alert.getCreateTime())).append("</td>");
            sb.append("</tr>");
        });

        sb.append("</table>");
        return sb.toString();
    }

    private List<String> getAlertReceivers(boolean isUrgent) {
        return isUrgent ?
                Arrays.asList("ops@company.com", "leader@company.com") :
                Collections.singletonList("ops@company.com");
    }
}