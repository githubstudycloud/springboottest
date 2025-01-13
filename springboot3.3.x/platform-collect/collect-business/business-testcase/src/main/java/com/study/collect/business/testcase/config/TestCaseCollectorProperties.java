package com.study.collect.business.testcase.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "collect.testcase")
public class TestCaseCollectorProperties {
    private int batchSize = 100;  // 批量处理大小
    private int threadCount = 4;  // 处理线程数
    private int retryTimes = 3;   // 重试次数
    private int timeout = 3600;   // 超时时间(秒)
}