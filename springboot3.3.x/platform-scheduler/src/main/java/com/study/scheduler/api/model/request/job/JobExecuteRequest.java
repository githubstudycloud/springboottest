package com.study.scheduler.api.model.request.job;

import lombok.Data;

import java.util.Map;
// 用于任务执行的请求模型。
@Data
public class JobExecuteRequest {
    private Map<String, String> variables;
}
