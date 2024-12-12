package com.study.scheduler.api.model.request.job;

import lombok.Data;

import java.util.List;
// 用于批量任务操作的请求模型。
@Data
public class BatchJobRequest {
    private List<Long> jobIds;
    private String action; // 操作类型，如 "start", "stop", "delete"
}
