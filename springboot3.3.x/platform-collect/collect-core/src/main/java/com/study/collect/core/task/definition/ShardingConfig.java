package com.study.collect.core.task.definition;

// 分片配置

import lombok.Data;

@Data
public class ShardingConfig {
    private boolean enabled;         // 是否启用分片
    private Integer total;           // 分片总数
    private String strategy;         // 分片策略
}
