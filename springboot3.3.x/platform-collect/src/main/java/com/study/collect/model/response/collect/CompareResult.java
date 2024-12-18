package com.study.collect.model.response.collect;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 对比结果
 */
@Data
@Builder
public class CompareResult {

    private String sourceId;

    private String targetId;

    private List<Difference> differences;

    private LocalDateTime compareTime;

    @Data
    @Builder
    public static class Difference {
        private String field;
        private Object sourceValue;
        private Object targetValue;
        private String description;
    }
}
