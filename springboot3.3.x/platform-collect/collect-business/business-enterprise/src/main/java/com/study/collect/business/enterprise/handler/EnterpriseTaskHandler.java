package com.study.collect.business.enterprise.handler;

import com.study.collect.business.enterprise.collector.EnterpriseCollector;
import com.study.collect.business.enterprise.model.Enterprise;
import com.study.collect.business.enterprise.processor.EnterpriseProcessor;
import com.study.collect.core.collector.model.CollectContext;
import com.study.collect.core.collector.model.CollectResult;
import com.study.collect.core.processor.model.ProcessContext;
import com.study.collect.core.task.handler.AbstractTaskHandler;
import com.study.collect.core.task.model.TaskContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class EnterpriseTaskHandler extends AbstractTaskHandler {

    private final EnterpriseCollector collector;
    private final EnterpriseProcessor processor;

    public EnterpriseTaskHandler(EnterpriseCollector collector,
                                 EnterpriseProcessor processor) {
        this.collector = collector;
        this.processor = processor;
    }

    @Override
    public String getType() {
        // 这个type要和task_config表中的task_code一致
        return "enterprise";
    }

    @Override
    protected Object doExecute(TaskContext context) {
        try {
            // 1. 创建采集上下文
            CollectContext<String> collectContext = new CollectContext<>();
            collectContext.setTaskId(context.getTaskId());
            collectContext.setParams(context.getShardParam());
            collectContext.setShardingId(context.getShardIndex());

            // 2. 执行采集
            CollectResult<List<Enterprise>> collectResult = collector.collect(collectContext);

            // 3. 如果采集成功，进行处理
            if (collectResult.isSuccess() && collectResult.getData() != null) {
                // 创建处理上下文
                ProcessContext processContext = new ProcessContext();
                processContext.setTaskId(context.getTaskId());

                // 对采集的每条数据进行处理
                List<Enterprise> processedData = collectResult.getData().stream()
                        .map(data -> processor.process(data, processContext))
                        .collect(Collectors.toList());

                return processedData;
            } else {
                throw new RuntimeException("采集失败: " +
                        collectResult.getErrorMessage());
            }
        } catch (Exception e) {
            log.error("任务执行异常", e);
            throw new RuntimeException("任务执行失败", e);
        }
    }
}
