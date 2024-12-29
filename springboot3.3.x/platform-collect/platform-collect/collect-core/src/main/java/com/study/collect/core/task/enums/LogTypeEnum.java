package com.study.collect.core.task.enums;

import lombok.Getter;

@Getter
public enum LogTypeEnum {
    START(1, "开始执行"),
    HEARTBEAT(2, "心跳检测"),
    PROGRESS(3, "执行进度"),
    RESULT(4, "执行结果"),
    ERROR(5, "执行错误");

    private final Integer code;
    private final String desc;

    LogTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}