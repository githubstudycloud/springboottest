package com.study.collect.business.finance.repository;


import com.study.collect.business.finance.model.FinanceData;
import com.study.collect.core.storage.repository.IRepository;
import org.springframework.data.mongodb.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface FinanceRepository extends IRepository<FinanceData, String> {

    // 方式一：方法名约定
    List<FinanceData> findByStockCode(String stockCode);

    // 方式二：使用@Query注解
    @Query("{'tradeDate': {$gte: ?0, $lte: ?1}}")
    List<FinanceData> findByTradeDateBetween(String startDate, String endDate);

    // 添加特定业务方法
    @Query(value = "{'amount': {$gt: ?0}}", sort = "{'tradeDate': -1}")
    List<FinanceData> findLargeTransactions(BigDecimal threshold);
}