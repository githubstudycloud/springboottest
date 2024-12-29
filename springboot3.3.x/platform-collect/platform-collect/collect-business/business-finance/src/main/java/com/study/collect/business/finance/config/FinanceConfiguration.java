package com.study.collect.business.finance.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableConfigurationProperties(FinanceProperties.class)
public class FinanceConfiguration {

    @Bean
    public void ensureIndexes(MongoTemplate mongoTemplate) {
        IndexOperations indexOps = mongoTemplate.indexOps("finance_data");

        // 创建复合索引
        indexOps.ensureIndex(new Index()
                .on("stockCode", org.springframework.data.domain.Sort.Direction.ASC)
                .on("tradeTime", org.springframework.data.domain.Sort.Direction.DESC));

        // 创建TTL索引
        indexOps.ensureIndex(new Index()
                .on("createTime", org.springframework.data.domain.Sort.Direction.ASC)
                .expire(7, TimeUnit.DAYS));
    }
}
