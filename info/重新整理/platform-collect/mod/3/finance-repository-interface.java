package com.study.collect.business.finance.repository;

import com.study.collect.business.finance.model.FinanceData;
import com.study.collect.core.repository.IRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.List;

public interface FinanceRepository extends IRepository<FinanceData, String> {
    
    @Query("{'stockCode': ?0}")
    List<FinanceData> findByStockCode(String stockCode);
    
    @Query("{'tradeDate': {$gte: ?0, $lte: ?1}}")
    List<FinanceData> findByTradeDateBetween(String startDate, String endDate);
}
