// FinanceRepository.java
package com.study.collect.business.finance.repository;

import com.study.collect.business.finance.model.FinanceData;
import com.study.collect.core.storage.repository.IRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface FinanceRepository extends IRepository<FinanceData, String>, MongoRepository<FinanceData, String> {

    // 基础查询方法
    List<FinanceData> findByStockCode(String stockCode);

    @Query("{'stockCode': ?0, 'tradeTime': {'$gte': ?1, '$lte': ?2}}")
    Page<FinanceData> findByConditions(String stockCode, LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);

    // 获取最新数据
    @Query(value = "{'stockCode': ?0}", sort = "{'tradeTime': -1}")
    FinanceData findLatestByStockCode(String stockCode);

    // 获取指定时间之前的最新数据
    @Query(value = "{'stockCode': ?0, 'tradeTime': {'$lt': ?1}}", sort = "{'tradeTime': -1}")
    FinanceData findPreviousByStockCode(String stockCode, LocalDateTime tradeTime);

    // 批量操作方法
    @Query(value = "{'stockCode': ?0, 'tradeTime': {'$gte': ?1, '$lte': ?2}}",
            sort = "{'tradeTime': 1}")
    List<FinanceData> findByStockCodeAndTimeBetween(String stockCode, LocalDateTime startTime, LocalDateTime endTime);

    // 统计查询
    @Query(value = "{'stockCode': ?0}",
            count = true)
    long countByStockCode(String stockCode);

    // 自定义更新操作
    @Query(value = "{'stockCode': ?0}",
            fields = "{'price': 1, 'volume': 1, 'amount': 1}")
    List<FinanceData> findStatsDataByStockCode(String stockCode);
}