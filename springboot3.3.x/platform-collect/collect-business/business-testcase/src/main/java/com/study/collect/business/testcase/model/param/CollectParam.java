package com.study.collect.business.testcase.model.param;

import com.study.collect.business.testcase.common.constants.CollectionConstants;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Validated
public class CollectParam {
    @NotBlank(message = "rootNode cannot be empty")
    private String rootNode;

    private String version;

    @NotBlank(message = "serverUri cannot be empty")
    private String serverUri;

    private Boolean incremental = false;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private List<String> uris;  // 添加 uris 字段

    private Boolean hardDelete = false;  // 添加 hardDelete 字段

    @Min(value = 50, message = "batchSize must be greater than 50")
    @Max(value = 1000, message = "batchSize must be less than 1000")
    private Integer batchSize = CollectionConstants.Process.DEFAULT_BATCH_SIZE;

    private Integer priority = 0;

    private Boolean allowDuplicate = false;

    private Integer maxRetries = CollectionConstants.Http.MAX_RETRY;

    private Integer timeout = 3600;

    private Boolean forceUpdate = false;

    private String taskId;
}