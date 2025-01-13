package com.study.collect.business.testcase.config;


import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class TestCaseConfig {
    @Bean
    @ConditionalOnMissingBean
    public TestCaseCollectorProperties testCaseCollectorProperties() {
        return new TestCaseCollectorProperties();
    }
}