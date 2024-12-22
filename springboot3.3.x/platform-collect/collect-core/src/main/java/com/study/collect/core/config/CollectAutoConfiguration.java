package com.study.collect.core.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan("com.study.collect.core")
@Import({RedisConfiguration.class, RabbitConfiguration.class})
public class CollectAutoConfiguration {
// 核心配置
}