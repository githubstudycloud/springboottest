package com.study.collect.core.storage.cache.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.TimeUnit;

@Data
@ConfigurationProperties(prefix = "collect.storage.cache")
public class CacheProperties {
    private boolean enabled = true;
    private long defaultExpiration = 3600L;
    private TimeUnit timeUnit = TimeUnit.SECONDS;

    private Redis redis = new Redis();

    @Data
    public static class Redis {
        private String host;
        private int port;
        private String password;
        private int database = 0;

        private Pool pool = new Pool();

        @Data
        public static class Pool {
            private int maxActive = 8;
            private int maxIdle = 8;
            private int minIdle = 0;
            private long maxWait = -1;
        }
    }
}