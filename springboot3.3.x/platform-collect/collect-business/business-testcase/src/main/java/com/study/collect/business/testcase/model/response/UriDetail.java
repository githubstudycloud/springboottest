package com.study.collect.business.testcase.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UriDetail {
    private String uri;
    private String realUri;
    private String number;
    private String name;
    private String version;
    private LocalDateTime updateTime;
    private Map<String, Object> details;
}