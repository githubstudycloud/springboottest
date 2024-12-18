package com.study.collect.infrastructure.persistent.cache.repository;

// 树形缓存仓库

import com.study.collect.domain.entity.data.tree.TreeNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 树节点缓存仓储实现
 */
@Slf4j
@Component
public class TreeNodeCacheRepository {

    private final CacheManager cacheManager;
    private static final String NODE_PREFIX = "node:";
    private static final String CHILDREN_PREFIX = "children:";

    public TreeNodeCacheRepository(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    /**
     * 缓存树节点
     */
    public void cacheNode(TreeNode node) {
        String key = NODE_PREFIX + node.getId();
        cacheManager.set(key, node);

        if (node.getChildren() != null && !node.getChildren().isEmpty()) {
            String childrenKey = CHILDREN_PREFIX + node.getId();
            cacheManager.set(childrenKey, node.getChildren());
        }
    }

    /**
     * 获取缓存的树节点
     */
    public TreeNode getNode(String nodeId) {
        String key = NODE_PREFIX + nodeId;
        return cacheManager.get(key, TreeNode.class);
    }

    /**
     * 获取缓存的子节点
     */
    public List<TreeNode> getChildren(String nodeId) {
        String key = CHILDREN_PREFIX + nodeId;
        return cacheManager.get(key, List.class);
    }
}