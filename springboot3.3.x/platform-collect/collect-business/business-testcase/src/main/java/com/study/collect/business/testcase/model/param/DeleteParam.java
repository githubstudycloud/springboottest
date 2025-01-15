package com.study.collect.business.testcase.model.param;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@Validated
public class DeleteParam {
    private String rootNode;

    @NotEmpty(message = "uris cannot be empty")
    private List<String> uris;

    private String version;

    private Boolean hardDelete = false;

    private Boolean async = false;

    private Integer priority = 0;

    private Integer batchSize;

    private String taskId;
}