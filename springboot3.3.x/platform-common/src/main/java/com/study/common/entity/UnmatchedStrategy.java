package com.study.common.entity;

/**
 * 未匹配项的处理策略
 */
public enum UnmatchedStrategy {
    KEEP_AT_END,     // 未匹配项放在末尾
    KEEP_AT_START,   // 未匹配项放在开头
    KEEP_ORIGINAL    // 未匹配项保持原有顺序
}