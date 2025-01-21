package com.study.collect.business.testcase.model.response;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class AsyncResponse<T> extends BaseResponse {
    private String taskId;
    private Double progress;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private T result;

    public static <T> AsyncResponse<T> processing(String taskId) {
        return AsyncResponse.<T>builder()
                .taskId(taskId)
                .status("PROCESSING")
                .progress(0.0)
                .startTime(LocalDateTime.now())
                .build();
    }

    public static <T> AsyncResponse<T> success(String taskId, T result) {
        return AsyncResponse.<T>builder()
                .taskId(taskId)
                .status("COMPLETED")
                .progress(100.0)
                .result(result)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now())
                .build();
    }

    public static <T> AsyncResponse<T> error(String taskId, String message) {
        return AsyncResponse.<T>builder()
                .taskId(taskId)
                .status("ERROR")
                .message(message)
                .endTime(LocalDateTime.now())
                .build();
    }
}