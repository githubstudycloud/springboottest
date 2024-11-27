package com.study.scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.study",
        "com.study.scheduler.config"  // 确保扫描到配置包
})
public class SchedulerApplication {
    public static void main(String[] args) {

        // 开启SSL调试（可选）
        System.setProperty("javax.net.debug", "ssl,handshake");
        SpringApplication.run(SchedulerApplication.class, args);
    }
}