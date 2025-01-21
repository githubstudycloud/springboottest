package com.study.collect.business.testcase.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Document(collection = "collect_tasks")
@Data
@EqualsAndHashCode(callSuper = true)
@CompoundIndexes({
        @CompoundIndex(name = "idx_root_version",
                def = "{'root_node': 1, 'version': 1}", unique = true)
})
public class CollectTaskEntity extends BaseEntity {

    @Indexed(unique = true)
    @Field("task_id")
    private String taskId;

    @Field("root_node")
    private String rootNode;

    private String version;

    private String status; // CREATED, PROCESSING, COMPLETED, FAILED, CANCELLED

    private Integer priority;

    private String message;

    @Field("start_time")
    private LocalDateTime startTime;

    @Field("end_time")
    private LocalDateTime endTime;

    @Field("total_uris")
    private Long totalUris = 0L;

    @Field("processed_uris")
    private Long processedUris = 0L;

    @Field("failed_uris")
    private Long failedUris = 0L;

    @Field("failed_uri_list")
    private List<String> failedUriList = new ArrayList<>();

    @Field("error_details")
    private Map<String, String> errorDetails = new HashMap<>();

    private Double progress = 0.0;

    @Field("is_incremental")
    private Boolean isIncremental = false;

    @Field("increment_start_time")
    private LocalDateTime incrementStartTime;

    @Field("increment_end_time")
    private LocalDateTime incrementEndTime;

    @Field("retry_count")
    private Integer retryCount = 0;

    @Field("last_retry_time")
    private LocalDateTime lastRetryTime;

    public void addFailedUri(String uri, String error) {
        if (failedUriList == null) {
            failedUriList = new ArrayList<>();
        }
        failedUriList.add(uri);

        if (errorDetails == null) {
            errorDetails = new HashMap<>();
        }
        errorDetails.put(uri, error);

        failedUris = (failedUris == null ? 0L : failedUris) + 1;
    }

    public void incrementProcessedCount() {
        processedUris = (processedUris == null ? 0L : processedUris) + 1;
        updateProgress();
    }

    public void updateProgress() {
        if (totalUris != null && totalUris > 0) {
            progress = (double) processedUris / totalUris * 100;
        }
    }

    public boolean canRetry() {
        return retryCount < 3;
    }

    public void incrementRetryCount() {
        retryCount = (retryCount == null ? 0 : retryCount) + 1;
        lastRetryTime = LocalDateTime.now();
    }
}