package com.study.collect.business.testcase.model.param;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Data
@Validated
public class QueryParam {
    private String rootNode;

    private List<String> uris;

    private String version;

    private String versionType;

    private Boolean includeDeleted = false;

    private Boolean onlyDeleted = false;

    private Integer page = 1;

    private Integer size = 20;

    private Boolean async = false;

    private String taskId;
}