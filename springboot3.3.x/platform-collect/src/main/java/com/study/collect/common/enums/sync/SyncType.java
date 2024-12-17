package com.study.collect.common.enums.sync;

// 同步类型

import lombok.Getter;

/**
 * 同步类型枚举
 */
@Getter
public enum SyncType {
    FULL("FULL", "全量同步"),
    INCREMENT("INCREMENT", "增量同步"),
    DELTA("DELTA", "差异同步");

    private final String code;
    private final String desc;

    SyncType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}