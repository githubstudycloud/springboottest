package com.study.scheduler.api.controller.admin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 负责系统管理相关的接口，例如健康检查和版本信息。
@RestController
@RequestMapping("/api/system")
public class SystemController {

    @GetMapping("/health")
    public String healthCheck() {
        return "OK";
    }

    @GetMapping("/version")
    public String getVersion() {
        return "Platform-Scheduler v1.0.0";
    }
}
