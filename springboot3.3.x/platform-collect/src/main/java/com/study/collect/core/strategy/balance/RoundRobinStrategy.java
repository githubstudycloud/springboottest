package com.study.collect.core.strategy.balance;

import com.study.collect.core.scheduler.strategy.DispatchStrategy;
import com.study.collect.domain.entity.task.CollectTask;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轮询分发策略
 */
@Component
public class RoundRobinStrategy implements DispatchStrategy {

    private final AtomicInteger counter = new AtomicInteger(0);

    @Override
    public String selectNode(List<String> nodes, CollectTask task) {
        if (CollectionUtils.isEmpty(nodes)) {
            return null;
        }
        int index = counter.getAndIncrement() % nodes.size();
        return nodes.get(index);
    }
}
