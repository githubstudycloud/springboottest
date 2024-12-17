package com.study.collect.common.enums.collect;

// 任务状态
import lombok.Getter;

/**
 * 任务状态枚举
 */
@Getter
public enum TaskStatus {
    WAITING("WAITING", "等待执行"),
    RUNNING("RUNNING", "执行中"),
    SUCCESS("SUCCESS", "执行成功"),
    FAILED("FAILED", "执行失败"),
    TIMEOUT("TIMEOUT", "执行超时"),
    CANCELED("CANCELED", "已取消");

    private final String code;
    private final String desc;

    TaskStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
