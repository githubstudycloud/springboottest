package com.study.scheduler.api.controller.monitor;

import com.study.scheduler.api.model.request.monitor.AlertRequest;
import com.study.scheduler.core.manager.AlertManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

//处理告警的查看和管理。
@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    @Autowired
    private AlertManager alertManager;

    @PostMapping
    public void sendAlert(@RequestBody AlertRequest request) {
        alertManager.handleAlert(request);
    }
}
