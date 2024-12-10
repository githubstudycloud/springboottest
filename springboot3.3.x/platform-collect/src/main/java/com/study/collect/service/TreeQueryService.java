package com.study.collect.service;


import com.study.collect.entity.TreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TreeQueryService {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 获取项目的完整树结构
     */
    public List<TreeNode> getProjectTree(String projectId) {
        // 获取所有节点
        List<TreeNode> allNodes = mongoTemplate.find(
                Query.query(Criteria.where("projectId").is(projectId)),
                TreeNode.class
        );

        // 构建树结构
        return buildTree(allNodes);
    }

    /**
     * 获取指定节点的子树
     */
    public List<TreeNode> getSubTree(String nodeId) {
        // 先获取目标节点
        TreeNode node = mongoTemplate.findById(nodeId, TreeNode.class);
        if (node == null) {
            return Collections.emptyList();
        }

        // 获取所有子节点
        List<TreeNode> children = mongoTemplate.find(
                Query.query(Criteria.where("path").regex("^" + nodeId)),
                TreeNode.class
        );

        children.add(node);
        return buildTree(children);
    }

    /**
     * 获取指定类型的节点
     */
    public List<TreeNode> getNodesByType(String projectId, TreeNode.NodeType type) {
        return mongoTemplate.find(
                Query.query(Criteria.where("projectId").is(projectId)
                        .and("type").is(type)),
                TreeNode.class
        );
    }

    /**
     * 获取指定路径下的直接子节点
     */
    public List<TreeNode> getDirectChildren(String parentId) {
        return mongoTemplate.find(
                Query.query(Criteria.where("parentId").is(parentId)),
                TreeNode.class
        );
    }

    private List<TreeNode> buildTree(List<TreeNode> nodes) {
        Map<String, TreeNode> nodeMap = new HashMap<>();
        List<TreeNode> roots = new ArrayList<>();

        // 创建节点映射
        nodes.forEach(node -> nodeMap.put(node.getId(), node));

        // 构建树结构
        nodes.forEach(node -> {
            if (node.getParentId() == null) {
                roots.add(node);
            } else {
                TreeNode parent = nodeMap.get(node.getParentId());
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(node);
                }
            }
        });

        return roots;
    }
}
