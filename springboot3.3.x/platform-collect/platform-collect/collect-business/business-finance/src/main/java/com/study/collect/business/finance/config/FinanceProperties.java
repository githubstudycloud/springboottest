package com.study.collect.business.finance.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "finance")
public class FinanceProperties {

    private Collector collector = new Collector();
    private Cache cache = new Cache();

    @Data
    public static class Collector {
        private int batchSize = 1000;
        private int threadPoolSize = 5;
        private long timeoutSeconds = 300;
    }

    @Data
    public static class Cache {
        private long expireSeconds = 300;
        private String prefix = "finance:";
    }
}