package com.study.collect.core.config;

import com.study.collect.core.collector.config.CollectorConfiguration;
import com.study.collect.core.mq.config.MQProperties;
import com.study.collect.core.mq.config.RabbitConfig;
import com.study.collect.core.processor.config.ProcessorConfiguration;
import com.study.collect.core.storage.cache.config.CacheAutoConfiguration;
import com.study.collect.core.task.config.MyBatisConfig;
import com.study.collect.core.task.config.TaskConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableConfigurationProperties({
        MQProperties.class
})
@Import({
        // 数据存储配置
//        MongoConfig.class,          // MongoDB
        MyBatisConfig.class,        // MyBatis
        CacheAutoConfiguration.class,// Redis

        // 消息队列配置
        RabbitConfig.class,         // RabbitMQ

        // 业务配置
        TaskConfiguration.class,     // 任务配置
        CollectorConfiguration.class,// 采集器配置
        ProcessorConfiguration.class // 处理器配置
})
public class CollectAutoConfiguration {
}