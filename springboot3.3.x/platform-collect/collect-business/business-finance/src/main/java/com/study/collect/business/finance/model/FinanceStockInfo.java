package com.study.collect.business.finance.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "finance_stock_info")
public class FinanceStockInfo {
    @Id
    private String id;
    private String stockCode;      // 股票代码
    private String stockName;      // 股票名称
    private String industry;       // 所属行业
    private String market;         // 所属市场(主板/创业板等)
    private Boolean enabled;       // 是否启用
    private LocalDateTime listDate;// 上市日期
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}