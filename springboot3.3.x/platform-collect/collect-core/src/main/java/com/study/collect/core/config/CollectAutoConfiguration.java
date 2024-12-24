
// 自动配置类

package com.study.collect.core.config;

import com.study.collect.core.collector.config.CollectorConfiguration;
import com.study.collect.core.mq.config.MQProperties;
import com.study.collect.core.mq.config.RabbitConfig;
import com.study.collect.core.processor.config.ProcessorConfiguration;
import com.study.collect.core.storage.cache.config.CacheAutoConfiguration;
import com.study.collect.core.storage.config.MongoConfig;
import com.study.collect.core.task.config.TaskConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan("com.study.collect.core")
@Import({
        MongoConfig.class,
        RabbitConfig.class,
        CacheAutoConfiguration.class,
        TaskConfiguration.class,
        CollectorConfiguration.class,
        ProcessorConfiguration.class
})
public class CollectAutoConfiguration {
    // 核心配置
}