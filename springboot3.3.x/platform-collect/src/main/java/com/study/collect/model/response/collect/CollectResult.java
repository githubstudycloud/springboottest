package com.study.collect.model.response.collect;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 采集结果
 */
@Data
@Builder
public class CollectResult {

    private String taskId;

    private Boolean success;

    private String message;

    private Object data;

    private LocalDateTime collectTime;

    public static CollectResult success(Object data) {
        return CollectResult.builder()
                .success(true)
                .data(data)
                .collectTime(LocalDateTime.now())
                .build();
    }

    public static CollectResult error(String message) {
        return CollectResult.builder()
                .success(false)
                .message(message)
                .collectTime(LocalDateTime.now())
                .build();
    }
}



