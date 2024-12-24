package com.study.collect.business.finance.service;

import com.study.collect.business.finance.collector.FinanceCollector;
import com.study.collect.business.finance.model.FinanceData;
import com.study.collect.business.finance.processor.FinanceProcessor;
import com.study.collect.business.finance.repository.FinanceRepository;
import com.study.collect.core.processor.model.ProcessContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FinanceService {

    private final FinanceCollector collector;
    private final FinanceProcessor processor;

    private final FinanceRepository financeRepository;

    public FinanceData collectStockData(String stockCode) {
        // 1. 采集数据
        FinanceData data = collector.collect(stockCode);

        // 2. 处理数据
        data = processor.process(data, new ProcessContext());

        // 3. 保存数据
        return financeRepository.save(data);
    }


    // 使用基础功能
    public FinanceData save(FinanceData data) {
        return financeRepository.save(data);
    }

    // 使用通用方法
    public FinanceData getByCode(String code) {
        return financeRepository.findByCode(code);
    }

    // 使用业务方法
    public List<FinanceData> getByStockCode(String stockCode) {
        return financeRepository.findByStockCode(stockCode);
    }

    // 软删除
    public void removeData(String id) {
        financeRepository.softDelete(id);
    }

    // 状态更新
    public void changeStatus(String id, String status) {
        financeRepository.updateStatus(id, status);
    }
}