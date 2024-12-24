package com.study.collect.core.task.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.study.collect.core.task.mapper")
public class MyBatisConfig {
    // MyBatis的其他配置可以在这里添加
}