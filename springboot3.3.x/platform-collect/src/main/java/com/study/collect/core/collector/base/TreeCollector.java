package com.study.collect.core.collector.base;

// 树形采集基类

import com.study.collect.common.enums.collect.CollectType;
import com.study.collect.common.exception.collect.CollectException;
import com.study.collect.core.engine.CollectContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

/**
 * 树形采集器抽象基类
 */
@Slf4j
public abstract class TreeCollector extends AbstractCollector {

    @Override
    public CollectType getType() {
        return CollectType.TREE;
    }

    @Override
    public boolean supports(CollectType type) {
        return CollectType.TREE.equals(type);
    }

    @Override
    protected Object doCollect(CollectContext context) {
        // 获取根节点
        TreeNode root = collectRoot(context);
        if (root == null) {
            return null;
        }

        // 递归采集子节点
        collectChildren(root, context);

        return root;
    }

    /**
     * 采集根节点
     */
    protected abstract TreeNode collectRoot(CollectContext context);

    /**
     * 采集子节点
     */
    protected void collectChildren(TreeNode parent, CollectContext context) {
        try {
            List<TreeNode> children = doCollectChildren(parent, context);
            if (CollectionUtils.isEmpty(children)) {
                return;
            }

            parent.setChildren(children);
            children.forEach(child -> {
                child.setParentId(parent.getId());
                child.setLevel(parent.getLevel() + 1);
                collectChildren(child, context);
            });
        } catch (Exception e) {
            log.error("Collect children failed for node: {}", parent.getId(), e);
            throw new CollectException("Collect children failed", e);
        }
    }

    /**
     * 执行子节点采集
     */
    protected abstract List<TreeNode> doCollectChildren(TreeNode parent, CollectContext context);
}
