// FinanceDataQueryRequest.java
package com.study.collect.business.finance.api.model.request;

import lombok.Data;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class FinanceDataQueryRequest {
    private String stockCode;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    private Integer pageNum = 1;
    private Integer pageSize = 10;

    // 排序参数
    private String sortField;
    private String sortOrder;

    // 聚合查询参数
    private Boolean needStats = false;
    private String statsType; // min,max,avg,sum
}