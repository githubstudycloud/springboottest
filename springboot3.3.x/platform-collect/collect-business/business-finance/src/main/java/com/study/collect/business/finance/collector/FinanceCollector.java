// FinanceCollector.java
package com.study.collect.business.finance.collector;

import com.study.collect.business.finance.model.FinanceData;
import com.study.collect.core.collector.AbstractCollector;
import com.study.collect.core.collector.annotation.Collector;
import com.study.collect.core.collector.exception.CollectException;
import com.study.collect.core.collector.model.CollectContext;
import com.study.collect.core.storage.cache.annotation.Cache;
import com.study.collect.core.storage.cache.annotation.CacheLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Collector(type = "finance")
@Component
public class FinanceCollector extends AbstractCollector<String, List<FinanceData>> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final Random random = new Random();

    public FinanceCollector(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void preProcess(CollectContext<String> context) {
        // 分片参数验证
        validateShardingParams(context);
        // 准备采集环境
        prepareCollectEnvironment(context);
    }

    @Override
    @Cache(key = "finance:stock:#{context.params}", expire = 300)
    @CacheLock(key = "lock:finance:#{context.params}", waitTime = 3)
    protected List<FinanceData> doCollect(CollectContext<String> context) {
        String stockCode = context.getParams();
        Integer shardIndex = context.getShardIndex();
        Integer shardTotal = context.getShardTotal();

        // 获取待处理的时间范围
        LocalDateTime[] timeRange = getTimeRange(context);
        LocalDateTime startTime = timeRange[0];
        LocalDateTime endTime = timeRange[1];

        // 根据分片计算当前分片的时间范围
        LocalDateTime shardStartTime = calculateShardTime(startTime, endTime, shardIndex, shardTotal);
        LocalDateTime shardEndTime = calculateShardTime(startTime, endTime, shardIndex + 1, shardTotal);

        // 生成该分片的数据
        return generateFinanceData(stockCode, shardStartTime, shardEndTime);
    }

    @Override
    protected void postProcess(List<FinanceData> data) {
        // 数据校验和补充
        data.forEach(this::enrichFinanceData);
    }

    private void validateShardingParams(CollectContext<String> context) {
        if (context.getShardIndex() == null || context.getShardTotal() == null) {
            throw new CollectException("分片参数不完整");
        }
        if (context.getShardIndex() >= context.getShardTotal()) {
            throw new CollectException("分片索引超出范围");
        }
    }

    private void prepareCollectEnvironment(CollectContext<String> context) {
        // 准备采集环境,如设置超时时间等
        String cacheKey = "finance:collect:" + context.getParams();
        redisTemplate.opsForValue().set(cacheKey, true, 5, TimeUnit.MINUTES);
    }

    private LocalDateTime[] getTimeRange(CollectContext<String> context) {
        // 从上下文中获取时间范围,如果没有则使用默认范围
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusHours(24);
        return new LocalDateTime[]{startTime, endTime};
    }

    private LocalDateTime calculateShardTime(LocalDateTime startTime, LocalDateTime endTime,
                                             int shardIndex, int shardTotal) {
        long totalSeconds = java.time.Duration.between(startTime, endTime).getSeconds();
        long shardSeconds = totalSeconds / shardTotal;
        return startTime.plusSeconds(shardSeconds * shardIndex);
    }

    private List<FinanceData> generateFinanceData(String stockCode,
                                                  LocalDateTime startTime,
                                                  LocalDateTime endTime) {
        List<FinanceData> dataList = new ArrayList<>();
        LocalDateTime currentTime = startTime;

        while (currentTime.isBefore(endTime)) {
            FinanceData data = new FinanceData();
            data.setStockCode(stockCode);
            data.setTradeTime(currentTime);

            // 生成模拟交易数据
            data.setPrice(generateRandomPrice());
            data.setVolume(generateRandomVolume());
            data.setAmount(data.getPrice().multiply(data.getVolume()));

            dataList.add(data);
            currentTime = currentTime.plusMinutes(1);
        }

        return dataList;
    }

    private BigDecimal generateRandomPrice() {
        double basePrice = 100.0;
        double variation = (random.nextDouble() - 0.5) * 2.0; // -1.0 到 1.0 之间的随机变化
        return BigDecimal.valueOf(basePrice * (1 + variation))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal generateRandomVolume() {
        double baseVolume = 10000.0;
        double variation = random.nextDouble() * 0.5; // 0 到 0.5 之间的随机变化
        return BigDecimal.valueOf(baseVolume * (1 + variation))
                .setScale(0, RoundingMode.HALF_UP);
    }

    private void enrichFinanceData(FinanceData data) {
        // 补充股票名称
        data.setStockName(getStockName(data.getStockCode()));
        // 设置创建时间
        data.setCreateTime(LocalDateTime.now());
    }

    private String getStockName(String stockCode) {
        // 模拟从缓存或其他服务获取股票名称
        return "Stock_" + stockCode;
    }
}