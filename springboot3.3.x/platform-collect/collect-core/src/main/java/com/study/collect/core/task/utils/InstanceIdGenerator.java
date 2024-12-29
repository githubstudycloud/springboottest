package com.study.collect.core.task.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public class InstanceIdGenerator {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final AtomicInteger SEQUENCE = new AtomicInteger(0);

    public static String generateInstanceId(String taskCode) {
        // 重置序号,避免无限增长
        if (SEQUENCE.get() > 9999) {
            SEQUENCE.set(0);
        }

        // 格式：taskCode_yyyyMMddHHmmss_XXXX
        return String.format("%s_%s_%04d",
                taskCode,
                LocalDateTime.now().format(FORMATTER),
                SEQUENCE.getAndIncrement());
    }
}