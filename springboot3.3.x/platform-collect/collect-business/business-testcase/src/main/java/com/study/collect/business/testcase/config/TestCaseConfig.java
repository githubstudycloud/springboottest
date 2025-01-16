package com.study.collect.business.testcase.config;


import com.study.collect.business.testcase.common.constants.TestCaseCollectorProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class TestCaseConfig {
    @Bean
    @ConditionalOnMissingBean
    public com.study.collect.business.testcase.common.constants.TestCaseCollectorProperties testCaseCollectorProperties() {
        return new TestCaseCollectorProperties();
    }
}