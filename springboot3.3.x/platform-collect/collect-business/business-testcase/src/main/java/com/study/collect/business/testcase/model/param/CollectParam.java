package com.study.collect.business.testcase.model.param;

import com.study.collect.business.testcase.constant.CollectionConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Validated
public class CollectParam {

    @NotBlank(message = "rootNode cannot be empty")
    private String rootNode;

    private String version;

    @NotBlank(message = "serverUrl cannot be empty")
    private String serverUrl;

    @Builder.Default
    private Boolean incremental = false;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @Min(value = 50, message = "batchSize must be greater than or equal to 50")
    @Max(value = 1000, message = "batchSize must be less than or equal to 1000")
    @Builder.Default
    private Integer batchSize = CollectionConstants.DEFAULT_BATCH_SIZE;

    @Builder.Default
    private Integer priority = 0;

    @Builder.Default
    private Boolean allowDuplicate = false;

    @Builder.Default
    private Integer maxRetries = CollectionConstants.HTTP_MAX_RETRY;

    @Builder.Default
    private Integer timeout = 3600;

    @Builder.Default
    private Boolean hardDelete = false;

    private String taskId;

    @Builder.Default
    private Boolean forceUpdate = false;

    // 验证增量采集参数
    public void validateIncrementalParams() {
        if (Boolean.TRUE.equals(incremental) && startTime == null) {
            throw new IllegalArgumentException("startTime is required for incremental collection");
        }
        if (startTime != null && endTime != null && !startTime.isBefore(endTime)) {
            throw new IllegalArgumentException("startTime must be before endTime");
        }
    }

    // 验证版本号格式
    public void validateVersion() {
        if (version != null && !version.matches("^[\\w.-]+$")) {
            throw new IllegalArgumentException("Invalid version format");
        }
    }

    // 构建复制
    public CollectParam copy() {
        return CollectParam.builder()
                .rootNode(this.rootNode)
                .version(this.version)
                .serverUrl(this.serverUrl)
                .incremental(this.incremental)
                .startTime(this.startTime)
                .endTime(this.endTime)
                .batchSize(this.batchSize)
                .priority(this.priority)
                .allowDuplicate(this.allowDuplicate)
                .maxRetries(this.maxRetries)
                .timeout(this.timeout)
                .hardDelete(this.hardDelete)
                .forceUpdate(this.forceUpdate)
                .build();
    }
}