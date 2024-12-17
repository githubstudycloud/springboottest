package com.study.collect.infrastructure.persistent.cache.repository;

// 树形缓存仓库

import com.study.collect.domain.entity.data.tree.TreeNode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 树节点缓存仓储
 */
@Repository
@RequiredArgsConstructor
public class TreeNodeCacheRepository extends BaseCacheRepository {

    private static final String NODE_KEY_PREFIX = "node:";
    private static final String NODE_CHILDREN_KEY_PREFIX = "node:children:";
    private static final long DEFAULT_EXPIRE_TIME = 1;

    public void saveNode(String nodeId, TreeNode node) {
        String key = NODE_KEY_PREFIX + nodeId;
        set(key, node, DEFAULT_EXPIRE_TIME, TimeUnit.HOURS);
    }

    public TreeNode getNode(String nodeId) {
        String key = NODE_KEY_PREFIX + nodeId;
        return get(key, TreeNode.class);
    }

    public void deleteNode(String nodeId) {
        String key = NODE_KEY_PREFIX + nodeId;
        delete(key);

        String childrenKey = NODE_CHILDREN_KEY_PREFIX + nodeId;
        delete(childrenKey);
    }

    public void saveChildren(String parentId, List<TreeNode> children) {
        String key = NODE_CHILDREN_KEY_PREFIX + parentId;
        set(key, children, DEFAULT_EXPIRE_TIME, TimeUnit.HOURS);
    }

    public List<TreeNode> getChildren(String parentId) {
        String key = NODE_CHILDREN_KEY_PREFIX + parentId;
        return get(key, List.class);
    }
}
