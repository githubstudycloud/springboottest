package com.study.scheduler.api.model.request.job;

import lombok.Data;

// 用于查询任务的请求模型。
@Data
public class JobQueryRequest {
    private String name;
    private String type;
    private String status;
    private String cronExpression;
    private int page = 0; // 分页
    private int size = 10;
}
