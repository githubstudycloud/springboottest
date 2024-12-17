package com.study.collect.common.enums.collect;

// 处理类型

import lombok.Getter;

/**
 * 处理类型枚举
 */
@Getter
public enum ProcessType {
    TRANSFORM("TRANSFORM", "数据转换"),
    FILTER("FILTER", "数据过滤"),
    VALIDATE("VALIDATE", "数据校验"),
    STORE("STORE", "数据存储"),
    STATISTICS("STATISTICS", "数据统计");

    private final String code;
    private final String desc;

    ProcessType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
