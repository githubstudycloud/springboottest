package com.study.collect.business.enterprise.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class EnterpriseConfig {
    @Bean
    @ConditionalOnMissingBean
    public EnterpriseCollectorProperties enterpriseCollectorProperties() {
        return new EnterpriseCollectorProperties();
    }
}