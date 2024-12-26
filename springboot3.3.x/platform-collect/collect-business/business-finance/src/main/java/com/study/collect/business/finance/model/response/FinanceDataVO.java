// FinanceDataVO.java
package com.study.collect.business.finance.api.model.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FinanceDataVO {
    private String id;
    private String stockCode;
    private String stockName;
    private BigDecimal price;
    private BigDecimal volume;
    private BigDecimal amount;
    private LocalDateTime tradeTime;

    // 统计相关字段
    private BigDecimal highPrice;
    private BigDecimal lowPrice;
    private BigDecimal avgPrice;
    private BigDecimal totalVolume;
    private BigDecimal totalAmount;

    // 涨跌幅等计算字段
    private BigDecimal priceChange;
    private BigDecimal priceChangePercent;
}