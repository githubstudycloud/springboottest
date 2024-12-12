package com.study.scheduler.config;

import com.study.common.util.DistributedLockUtil;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class JobConfig {

    @Bean
    public DistributedLockUtil distributedLockUtil(RedissonClient redissonClient) {
        return new DistributedLockUtil(redissonClient);
    }

    @Bean
    public HttpClient httpClient() {
        return HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }
}