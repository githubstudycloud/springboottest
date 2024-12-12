package com.study.common.model.task;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * HTTP配置
 */
@Data
@Builder
public class HttpConfig {
    private String url;
    private String method;
    private Map<String, String> headers;
    private String body;
    private Map<String, String> parameters;
}
