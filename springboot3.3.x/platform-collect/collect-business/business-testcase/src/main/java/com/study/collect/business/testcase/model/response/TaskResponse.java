package com.study.collect.business.testcase.model.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class TaskResponse {
    private String taskId;
    private String type;
    private String status;
    private Double progress;
    private String message;
    private Integer priority;
    private LocalDateTime createTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long totalCount;
    private Long processedCount;
    private Long failedCount;
    private Map<String, Object> details;
    private Map<String, Object> params;

    public static TaskResponse create(String taskId, String type, Map<String, Object> params) {
        return TaskResponse.builder()
                .taskId(taskId)
                .type(type)
                .status("CREATED")
                .progress(0.0)
                .createTime(LocalDateTime.now())
                .params(params)
                .build();
    }

    public void updateProgress(long processed, long total) {
        this.processedCount = processed;
        this.totalCount = total;
        this.progress = total > 0 ? (processed * 100.0) / total : 0.0;
    }
}