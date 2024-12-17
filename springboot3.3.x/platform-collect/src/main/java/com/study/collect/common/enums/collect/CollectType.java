package com.study.collect.common.enums.collect;

// 采集类型

import lombok.Getter;

/**
 * 采集类型枚举
 */
@Getter
public enum CollectType {
    TREE("TREE", "树形结构"),
    LIST("LIST", "列表结构"),
    SINGLE("SINGLE", "单条数据"),
    COMPOUND("COMPOUND", "复合结构");

    private final String code;
    private final String desc;

    CollectType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}