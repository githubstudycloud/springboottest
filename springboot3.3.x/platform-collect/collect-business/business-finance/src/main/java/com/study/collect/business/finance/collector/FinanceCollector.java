package com.study.collect.business.finance.collector;

import com.study.collect.business.finance.model.FinanceData;
import com.study.collect.core.collector.AbstractCollector;
import com.study.collect.core.collector.annotation.Collector;
import com.study.collect.core.collector.exception.CollectException;
import com.study.collect.core.collector.model.CollectContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Collector(type = "finance")
@Component
@RequiredArgsConstructor
public class FinanceCollector extends AbstractCollector<String, FinanceData> {

    private static final String CACHE_PREFIX = "finance:stock:";
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    protected void preProcess(CollectContext<String> context) {
        // 检查缓存是否存在
        String key = CACHE_PREFIX + context.getParams();
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            throw new CollectException("Data already collected: " + context.getParams());
        }
    }

    @Override
    protected FinanceData doCollect(CollectContext<String> context) {
        //        // 模拟从外部API获取数据
        FinanceData data = collectFromExternalApi(context.getParams());

        // 缓存数据
        String key = CACHE_PREFIX + context.getParams();
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
    public FinanceData collect(String param) {
        return null;
    }

    @Override
    public String getType() {
        return "finance";
    }
}