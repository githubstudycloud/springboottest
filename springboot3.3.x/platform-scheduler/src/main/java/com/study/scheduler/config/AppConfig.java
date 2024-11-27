package com.study.scheduler.config;

import com.study.scheduler.util.HttpClientUtil;
import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @PreDestroy
    public void onShutdown() {
        HttpClientUtil.close();
    }
}