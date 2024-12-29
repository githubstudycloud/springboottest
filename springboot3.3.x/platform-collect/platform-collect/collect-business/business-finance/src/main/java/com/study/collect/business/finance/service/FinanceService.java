// FinanceCollectService.java
package com.study.collect.business.finance.service;

import com.study.collect.business.finance.api.model.request.FinanceDataGenerateRequest;
import com.study.collect.business.finance.collector.FinanceCollector;
import com.study.collect.business.finance.model.FinanceData;
import com.study.collect.business.finance.repository.FinanceRepository;
import com.study.collect.core.collector.model.CollectContext;
import com.study.collect.core.task.model.TaskContext;
import com.study.collect.core.task.model.TaskResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FinanceCollectService {

    private final FinanceCollector collector;
    private final FinanceRepository repository;

    public String generateFinanceData(FinanceDataGenerateRequest request) {
        String taskId = UUID.randomUUID().toString();

        // 创建采集上下文
        CollectContext<String> context = new CollectContext<>();
        context.setTaskId(taskId);
        context.setParams(request.getStockCode());

        // 执行采集
        List<FinanceData> dataList = collector.collect(context);

        // 保存数据
        repository.saveAll(dataList);

        return taskId;
    }

    public void syncStockData(String stockCode) {
        // 设置分片采集上下文
        CollectContext<String> context = new CollectContext<>();
        context.setTaskId(UUID.randomUUID().toString());
        context.setParams(stockCode);
        context.setShardIndex(0);
        context.setShardTotal(1);

        // 执行采集和保存
        List<FinanceData> dataList = collector.collect(context);
        repository.saveAll(dataList);
    }

    public TaskResult executeTask(TaskContext context) {
        try {
            // 将任务上下文转换为采集上下文
            CollectContext<String> collectContext = new CollectContext<>();
            collectContext.setTaskId(context.getTaskId());
            collectContext.setParams((String) context.getParameter("stockCode"));
            collectContext.setShardIndex(context.getShardIndex());
            collectContext.setShardTotal(context.getShardTotal());

            // 执行采集
            List<FinanceData> dataList = collector.collect(collectContext);
            repository.saveAll(dataList);

            return TaskResult.success(context.getTaskId(), dataList.size());
        } catch (Exception e) {
            log.error("Task execution failed: {}", context.getTaskId(), e);
            return TaskResult.failure(context.getTaskId(), e.getMessage());
        }
    }
}