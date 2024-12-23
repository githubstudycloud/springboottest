package com.study.collect.infrastructure.storage.template;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.ArrayList;

/**
 * 树形数据操作模板
 * 专门处理树形结构数据的存储和查询
 */
public class TreeTemplate<T> extends BaseMongoTemplate<T> {

    public TreeTemplate(MongoTemplate mongoTemplate, Class<T> entityClass) {
        super(mongoTemplate, entityClass);
    }

    /**
     * 查询子树
     */
    public List<T> findSubTree(String parentId) {
        List<T> result = new ArrayList<>();
        findChildren(parentId, result);
        return result;
    }

    /**
     * 递归查询子节点
     */
    private void findChildren(String parentId, List<T> result) {
        Query query = new Query(Criteria.where("parentId").is(parentId));
        List<T> children = mongoTemplate.find(query, entityClass);
        
        if (!children.isEmpty()) {
            result.addAll(children);
            for (T child : children) {
                // 这里假设实体有getId方法，实际使用时需要调整
                String childId = getNodeId(child);
                findChildren(childId, result);
            }
        }
    }

    /**
     * 获取节点路径
     */
    public List<T> findPath(String nodeId) {
        List<T> path = new ArrayList<>();
        findParents(nodeId, path);
        return path;
    }

    /**
     * 递归查询父节点
     */
    private void findParents(String nodeId, List<T> path) {
        Query query = new Query(Criteria.where("_id").is(nodeId));
        T node = mongoTemplate.findOne(query, entityClass);
        
        if (node != null) {
            path.add(0, node);
            String parentId = getParentId(node);
            if (parentId != null) {
                findParents(parentId, path);
            }
        }
    }

    /**
     * 移动节点
     */
    public void moveNode(String nodeId, String newParentId) {
        // 检查是否形成循环
        if (isCircular(nodeId, newParentId)) {
            throw new IllegalArgumentException("Move operation would create circular reference");
        }

        Query query = new Query(Criteria.where("_id").is(nodeId));
        org.springframework.data.mongodb.core.query.Update update = new org.springframework.data.mongodb.core.query.Update().set("parentId", newParentId);
        mongoTemplate.updateFirst(query, update, entityClass);
    }

    /**
     * 检查移动操作是否会形成循环
     */
    private boolean isCircular(String nodeId, String newParentId) {
        if (nodeId.equals(newParentId)) {
            return true;
        }

        List<T> parentPath = findPath(newParentId);
        for (T node : parentPath) {
            if (nodeId.equals(getNodeId(node))) {
                return true;
            }
        }
        return false;
    }

    // 这些方法需要在具体实现类中实现
    protected abstract String getNodeId(T node);
    protected abstract String getParentId(T node);
}
