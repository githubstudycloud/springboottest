package com.study.collect.business.finance.collector;

import com.study.collect.business.finance.model.FinanceData;
import com.study.collect.core.collector.AbstractCollector;
import com.study.collect.core.annotation.Collector;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Collector(type = "finance")
@Component
@RequiredArgsConstructor
public class FinanceCollector extends AbstractCollector<String, FinanceData> {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String CACHE_PREFIX = "finance:stock:";

    @Override
    protected void preProcess(String stockCode) {
        // 检查缓存是否存在
        String key = CACHE_PREFIX + stockCode;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            throw new CollectException("Data already collected: " + stockCode);
        }
    }

    @Override
    protected FinanceData doCollect(String stockCode) {
        // 模拟从外部API获取数据
        FinanceData data = collectFromExternalApi(stockCode);

        // 缓存数据
        String key = CACHE_PREFIX + stockCode;
        redisTemplate.opsForValue().set(key, data);

        return data;
    }

    @Override
    protected void postProcess(FinanceData data) {
        // 计算衍生指标
        calculateIndicators(data);
    }

    private FinanceData collectFromExternalApi(String stockCode) {
        // 模拟外部API调用
        FinanceData data = new FinanceData();
        data.setStockCode(stockCode);
        data.setTradeTime(LocalDateTime.now());
        return data;
    }

    private void calculateIndicators(FinanceData data) {
        // 计算交易金额
        if (data.getPrice() != null && data.getVolume() != null) {
            data.setAmount(data.getPrice().multiply(data.getVolume()));
        }
    }

    @Override
    public String getType() {
        return "finance";
    }
}