package com.study.collect.core.processor.impl.task;
import com.study.collect.common.exception.collect.ProcessException;
import com.study.collect.core.processor.ProcessContext;
import com.study.collect.core.processor.base.AbstractProcessor;
import com.study.collect.common.enums.collect.ProcessType;
import com.study.collect.core.strategy.RangeShardingStrategy;
import com.study.collect.domain.entity.task.CollectTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 任务分片处理器
 */
@Slf4j
@Component
public class TaskShardingProcessor extends AbstractProcessor {

    @Override
    public ProcessType getType() {
        return ProcessType.SHARDING;
    }

    @Override
    public int getOrder() {
        return 10;
    }

    @Override
    protected void doProcess(ProcessContext context) {
        CollectTask task = (CollectTask) context.getRawData();

        try {
            // 1. 计算分片策略
            ShardingStrategy strategy = calculateStrategy(task);

            // 2. 生成分片任务
            List<CollectTask> shardTasks = strategy.shard(task);

            // 3. 更新上下文
            context.setResult(shardTasks);
            context.getParams().put("sharded", true);

        } catch (Exception e) {
            log.error("Task sharding failed", e);
            throw new ProcessException("Task sharding failed: " + e.getMessage());
        }
    }

    private ShardingStrategy calculateStrategy(CollectTask task) {
        // 根据任务类型和参数选择分片策略
        if (task.getParams().containsKey("range")) {
            return new RangeShardingStrategy();
        } else if (task.getParams().containsKey("hash")) {
            return new HashShardingStrategy();
        }
        return new SimpleShardingStrategy();
    }
}
