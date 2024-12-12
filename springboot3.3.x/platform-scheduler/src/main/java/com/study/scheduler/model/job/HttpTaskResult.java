package com.study.scheduler.model.job;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class HttpTaskResult {
    private int statusCode;
    private String body;
    private Map<String, List<String>> headers;

    public HttpTaskResult() {
    }

    public HttpTaskResult(int statusCode, String body, Map<String, List<String>> headers) {
        this.statusCode = statusCode;
        this.body = body;
        this.headers = headers;

    }
}