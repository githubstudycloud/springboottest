package com.study.collect.business.finance.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Document(collection = "finance_data")
public class FinanceData {
    @Id
    private String id;
    private String code;
    private String stockCode;
    private String stockName;
    private BigDecimal price;
    private BigDecimal volume;
    private BigDecimal amount;
    private String status;
    private Boolean deleted;
    private LocalDateTime tradeTime;
    private LocalDateTime createTime;
}
