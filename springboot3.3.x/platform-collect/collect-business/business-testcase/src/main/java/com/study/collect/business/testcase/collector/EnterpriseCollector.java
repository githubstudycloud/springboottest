package com.study.collect.business.testcase.collector;

import com.study.collect.business.testcase.model.Enterprise;
import com.study.collect.business.testcase.repository.EnterpriseRepository;
import com.study.collect.common.util.JsonUtils;
import com.study.collect.core.collector.AbstractCollector;
import com.study.collect.core.collector.annotation.Collector;
import com.study.collect.core.collector.model.CollectContext;
import com.study.collect.core.collector.model.CollectResult;
import com.study.collect.core.storage.cache.annotation.Cache;
import com.study.collect.core.storage.cache.annotation.CacheLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@Collector(type = "enterprise")
@RequiredArgsConstructor
public class EnterpriseCollector extends AbstractCollector<String, List<Enterprise>> {

    private final EnterpriseRepository repository;
    private static final int BATCH_SIZE = 100;
    private final AtomicInteger counter = new AtomicInteger(0);

    @Override
    protected void preProcess(CollectContext<String> context) {
        super.preProcess(context);
        // 解析分片参数
        Map<String, Object> params = parseShardingParams(context.getParams());
        context.setAttribute("shardParams", params);

        // 记录开始时间
        context.setAttribute("startTime", LocalDateTime.now());
        counter.set(0);
    }

    @Override
    @Cache(key = "enterprise:collect:#{#context.taskId}")
    @CacheLock(key = "lock:enterprise:collect:#{#context.taskId}")
    protected List<Enterprise> doCollect(CollectContext<String> context) {
        Map<String, Object> params = context.getAttribute("shardParams");
        List<Enterprise> result = new ArrayList<>();

        if (params.containsKey("code")) {
            // 单个企业采集
            String code = (String) params.get("code");
            Enterprise enterprise = collectSingle(code);
            if (enterprise != null) {
                result.add(enterprise);
            }
        } else {
            // 分片批量采集
            int shardTotal = (int) params.get("shardTotal");
            int shardIndex = context.getShardingId();
            result = collectBatch(shardIndex, shardTotal);
        }

        return result;
    }

    @Override
    protected void postProcess(CollectResult<List<Enterprise>> result) {
        super.postProcess(result);
        if (result.getData() != null) {
            // 更新采集进度
            int total = counter.addAndGet(result.getData().size());
            log.info("采集进度: {}/{}", total, result.getData().size());
        }
    }

    /**
     * 采集单个企业数据
     */
    private Enterprise collectSingle(String code) {
        try {
            // 模拟调用外部接口
            Thread.sleep(100);

            Enterprise enterprise = repository.findByCode(code);
            if (enterprise != null) {
//                enterprise.setUpdateTime(LocalDateTime.now());
//                enterprise.setVersion("V" + System.currentTimeMillis());
                return repository.save(enterprise);
            }
            return null;
        } catch (Exception e) {
            log.error("采集企业数据失败: {}", code, e);
            return null;
        }
    }

    /**
     * 批量采集企业数据
     */
    private List<Enterprise> collectBatch(int shardIndex, int shardTotal) {
        List<Enterprise> results = new ArrayList<>();
        int pageNum = 0;

        while (true) {
            // 分页查询数据
            Page<Enterprise> page = repository.findBySharding(
                    shardIndex,
                    shardTotal,
                    PageRequest.of(pageNum, BATCH_SIZE)
            );

            if (!page.hasContent()) {
                break;
            }

            // 处理每页数据
            for (Enterprise enterprise : page.getContent()) {
                try {
                    // 模拟调用外部接口
                    Thread.sleep(50);

//                    enterprise.setUpdateTime(LocalDateTime.now());
//                    enterprise.setVersion("V" + System.currentTimeMillis());
                    results.add(repository.save(enterprise));
                } catch (Exception e) {
                    log.error("采集企业数据失败: {}", enterprise.getCode(), e);
                }
            }

            pageNum++;

            // 记录进度
            counter.addAndGet(page.getContent().size());

            if (!page.hasNext()) {
                break;
            }
        }

        return results;
    }

    /**
     * 解析分片参数
     */
    private Map<String, Object> parseShardingParams(String params) {
        try {
            return JsonUtils.fromJson(params, Map.class);
        } catch (Exception e) {
            log.error("解析分片参数失败: {}", params, e);
            return Map.of();
        }
    }

    @Override
    public String getType() {
        return "";
    }
}