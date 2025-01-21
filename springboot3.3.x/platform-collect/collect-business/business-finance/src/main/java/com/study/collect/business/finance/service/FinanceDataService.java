// FinanceDataService.java
package com.study.collect.business.finance.service;

import com.study.collect.business.finance.api.model.request.FinanceDataQueryRequest;
import com.study.collect.business.finance.api.model.response.FinanceDataVO;
import com.study.collect.business.finance.model.FinanceData;
import com.study.collect.business.finance.repository.FinanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FinanceDataService {

    private final FinanceRepository repository;

    public Page<FinanceDataVO> queryFinanceData(FinanceDataQueryRequest request) {
        // 构建分页和排序参数
        Sort sort = buildSort(request);
        PageRequest pageRequest = PageRequest.of(
                request.getPageNum() - 1,
                request.getPageSize(),
                sort
        );

        // 执行查询
        Page<FinanceData> dataPage = repository.findByConditions(
                request.getStockCode(),
                request.getStartTime(),
                request.getEndTime(),
                pageRequest
        );

        // 转换为VO
        return dataPage.map(this::convertToVO);
    }

    public FinanceDataVO getStockStats(String stockCode, String statsType) {
        FinanceDataVO stats = new FinanceDataVO();
        stats.setStockCode(stockCode);

        List<FinanceData> dataList = repository.findByStockCode(stockCode);
        if (dataList.isEmpty()) {
            return stats;
        }

        // 计算统计数据
        switch (statsType) {
            case "price" -> calculatePriceStats(dataList, stats);
            case "volume" -> calculateVolumeStats(dataList, stats);
            case "amount" -> calculateAmountStats(dataList, stats);
            default -> calculateAllStats(dataList, stats);
        }

        return stats;
    }

    public FinanceDataVO getRealtimeData(String stockCode) {
        FinanceData latestData = repository.findLatestByStockCode(stockCode);
        if (latestData == null) {
            return new FinanceDataVO();
        }

        FinanceDataVO vo = convertToVO(latestData);

        // 计算涨跌幅
        FinanceData previousData = repository.findPreviousByStockCode(stockCode, latestData.getTradeTime());
        if (previousData != null) {
            calculatePriceChange(vo, latestData, previousData);
        }

        return vo;
    }

    private Sort buildSort(FinanceDataQueryRequest request) {
        if (request.getSortField() != null && request.getSortOrder() != null) {
            Sort.Direction direction = "desc".equalsIgnoreCase(request.getSortOrder()) ?
                    Sort.Direction.DESC : Sort.Direction.ASC;
            return Sort.by(direction, request.getSortField());
        }
        return Sort.by(Sort.Direction.DESC, "tradeTime");
    }

    private FinanceDataVO convertToVO(FinanceData data) {
        FinanceDataVO vo = new FinanceDataVO();
        vo.setId(data.getId());
        vo.setStockCode(data.getStockCode());
        vo.setStockName(data.getStockName());
        vo.setPrice(data.getPrice());
        vo.setVolume(data.getVolume());
        vo.setAmount(data.getAmount());
        vo.setTradeTime(data.getTradeTime());
        return vo;
    }

    private void calculatePriceChange(FinanceDataVO vo, FinanceData current, FinanceData previous) {
        BigDecimal priceChange = current.getPrice().subtract(previous.getPrice());
        vo.setPriceChange(priceChange);

        BigDecimal changePercent = priceChange
                .multiply(BigDecimal.valueOf(100))
                .divide(previous.getPrice(), 2, RoundingMode.HALF_UP);
        vo.setPriceChangePercent(changePercent);
    }

    private void calculatePriceStats(List<FinanceData> dataList, FinanceDataVO stats) {
        stats.setHighPrice(findMaxPrice(dataList));
        stats.setLowPrice(findMinPrice(dataList));
        stats.setAvgPrice(calculateAveragePrice(dataList));
    }

    private void calculateVolumeStats(List<FinanceData> dataList, FinanceDataVO stats) {
        stats.setTotalVolume(calculateTotalVolume(dataList));
    }

    private void calculateAmountStats(List<FinanceData> dataList, FinanceDataVO stats) {
        stats.setTotalAmount(calculateTotalAmount(dataList));
    }

    private void calculateAllStats(List<FinanceData> dataList, FinanceDataVO stats) {
        calculatePriceStats(dataList, stats);
        calculateVolumeStats(dataList, stats);
        calculateAmountStats(dataList, stats);
    }

    // 辅助计算方法
    private BigDecimal findMaxPrice(List<FinanceData> dataList) {
        return dataList.stream()
                .map(FinanceData::getPrice)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }

    private BigDecimal findMinPrice(List<FinanceData> dataList) {
        return dataList.stream()
                .map(FinanceData::getPrice)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }

//    private BigDecimal calculateAveragePrice(List<FinanceData> dataList) {
//        return dataList.stream()
//                .map(FinanceData::getPrice)
//                .reduce(BigDecimal.ZERO, BigDecimal::add)
//                .divide(BigDecimal.valueOf

    // FinanceDataService.java (续)
    private BigDecimal calculateAveragePrice(List<FinanceData> dataList) {
        return dataList.stream()
                .map(FinanceData::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(dataList.size()), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateTotalVolume(List<FinanceData> dataList) {
        return dataList.stream()
                .map(FinanceData::getVolume)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateTotalAmount(List<FinanceData> dataList) {
        return dataList.stream()
                .map(FinanceData::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}