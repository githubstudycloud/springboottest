package com.study.collect.model.request.query;

// 数据查询

import java.time.LocalDateTime;

/**
 * 数据查询请求
 */
@Data
@Builder
public class DataQueryRequest {

    private String taskId;

    private String type;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @Min(value = 1, message = "页码最小为1")
    private Integer pageNum = 1;

    @Min(value = 1, message = "每页大小最小为1")
    @Max(value = 1000, message = "每页大小最大为1000")
    private Integer pageSize = 10;
}
