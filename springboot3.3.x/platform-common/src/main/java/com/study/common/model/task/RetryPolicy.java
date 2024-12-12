package com.study.common.model.task;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * 重试策略
 */
@Data
@Builder
public class RetryPolicy {
    private int maxRetries;
    private long retryInterval;
    private boolean exponentialBackoff;
}
