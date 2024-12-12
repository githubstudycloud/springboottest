package com.study.scheduler.core.manager;

import com.study.scheduler.api.model.request.monitor.AlertRequest;
import org.springframework.stereotype.Component;

@Component
public class AlertManager {

    public void handleAlert(AlertRequest request) {
        System.out.printf("Alert received: Level=%s, Message=%s%n",
                request.getAlertLevel(), request.getMessage());
        // 添加告警处理逻辑
    }
}
