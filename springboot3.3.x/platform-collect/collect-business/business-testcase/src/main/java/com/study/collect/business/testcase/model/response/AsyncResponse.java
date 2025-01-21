package com.study.collect.business.testcase.model.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class AsyncResponse<T> extends BaseResponse {
    private String taskId;
    private Double progress;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private T result;

    private AsyncResponse() {
        super();
    }

    private AsyncResponse(String code, String message, String status,
                          String taskId, Double progress, LocalDateTime startTime,
                          LocalDateTime endTime, T result) {
        super(code, message, status);
        this.taskId = taskId;
        this.progress = progress;
        this.startTime = startTime;
        this.endTime = endTime;
        this.result = result;
    }

    public static <T> AsyncResponseBuilder<T> asyncBuilder() {
        return new AsyncResponseBuilder<>();
    }

    public static <T> AsyncResponse<T> processing(String taskId) {
        return AsyncResponse.<T>asyncBuilder()
                .taskId(taskId)
                .status("PROCESSING")
                .progress(0.0)
                .startTime(LocalDateTime.now())
                .build();
    }

    public static <T> AsyncResponse<T> success(String taskId, T result) {
        return AsyncResponse.<T>asyncBuilder()
                .taskId(taskId)
                .status("COMPLETED")
                .progress(100.0)
                .result(result)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now())
                .build();
    }

    public static <T> AsyncResponse<T> error(String taskId, String message) {
        return AsyncResponse.<T>asyncBuilder()
                .taskId(taskId)
                .status("ERROR")
                .message(message)
                .endTime(LocalDateTime.now())
                .build();
    }

    public static class AsyncResponseBuilder<T> {
        private String code;
        private String message;
        private String status;
        private String taskId;
        private Double progress;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private T result;

        public AsyncResponseBuilder<T> code(String code) {
            this.code = code;
            return this;
        }

        public AsyncResponseBuilder<T> message(String message) {
            this.message = message;
            return this;
        }

        public AsyncResponseBuilder<T> status(String status) {
            this.status = status;
            return this;
        }

        public AsyncResponseBuilder<T> taskId(String taskId) {
            this.taskId = taskId;
            return this;
        }

        public AsyncResponseBuilder<T> progress(Double progress) {
            this.progress = progress;
            return this;
        }

        public AsyncResponseBuilder<T> startTime(LocalDateTime startTime) {
            this.startTime = startTime;
            return this;
        }

        public AsyncResponseBuilder<T> endTime(LocalDateTime endTime) {
            this.endTime = endTime;
            return this;
        }

        public AsyncResponseBuilder<T> result(T result) {
            this.result = result;
            return this;
        }

        public AsyncResponse<T> build() {
            return new AsyncResponse<>(code, message, status, taskId, progress, startTime, endTime, result);
        }
    }
}