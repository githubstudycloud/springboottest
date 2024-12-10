package com.study.collect.entity;

import lombok.Data;

import java.util.Map;

@Data
public class TreeCollectRequest {
    private String projectId;
    private String url;         // 树状数据获取URL
    private String method;      // 请求方法(GET/POST)
    private Map<String, String> headers; // 请求头
    private String requestBody; // POST请求体
}
