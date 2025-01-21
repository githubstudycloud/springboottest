// FinanceDataGenerateRequest.java
package com.study.collect.business.finance.api.model.request;

import lombok.Data;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Data
public class FinanceDataGenerateRequest {
    @NotNull(message = "股票代码不能为空")
    private String stockCode;

    @NotNull(message = "数据量不能为空")
    @Min(value = 1, message = "数据量必须大于0")
    private Integer dataCount;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    // 可选的数据生成参数
    private Double minPrice;
    private Double maxPrice;
    private Double minVolume;
    private Double maxVolume;
}