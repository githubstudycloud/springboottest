package com.study.scheduler.api.controller.monitor;

import com.study.scheduler.api.model.vo.monitor.MetricsVO;
import com.study.scheduler.core.manager.MetricsManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

// 提供监控指标查询接口。
@RestController
@RequestMapping("/api/metrics")
public class MetricsController {

    @Autowired
    private MetricsManager metricsManager;

    @GetMapping
    public MetricsVO getMetrics() {
        return metricsManager.collectMetrics();
    }
}
