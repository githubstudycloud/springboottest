package com.study.collect.core.strategy;

import com.study.collect.domain.entity.task.CollectTask;

import java.util.List;

/**
 * 任务分发策略接口
 */
public interface DispatchStrategy {
    /**
     * 选择执行节点
     */
    String selectNode(List<String> nodes, CollectTask task);
}

