package com.study.collect.infrastructure.persistent.mongo.repository;

import com.study.collect.domain.entity.data.tree.TreeNode;
import com.study.collect.domain.repository.data.TreeNodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.mongodb.client.result.UpdateResult;
import com.study.collect.domain.entity.data.tree.TreeNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * MongoDB树节点仓储实现
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class TreeNodeRepositoryImpl implements TreeNodeRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public TreeNode save(TreeNode node) {
        if (node.getCreateTime() == null) {
            node.setCreateTime(LocalDateTime.now());
        }
        node.setUpdateTime(LocalDateTime.now());
        return mongoTemplate.save(node);
    }

    @Override
    public List<TreeNode> saveAll(List<TreeNode> nodes) {
        LocalDateTime now = LocalDateTime.now();
        nodes.forEach(node -> {
            if (node.getCreateTime() == null) {
                node.setCreateTime(now);
            }
            node.setUpdateTime(now);
        });
        return mongoTemplate.insertAll(nodes);
    }

    @Override
    public Optional<TreeNode> findById(String id) {
        return Optional.ofNullable(mongoTemplate.findById(id, TreeNode.class));
    }

    @Override
    public List<TreeNode> findByParentId(String parentId) {
        Query query = Query.query(Criteria.where("parentId").is(parentId));
        return mongoTemplate.find(query, TreeNode.class);
    }

    @Override
    public List<TreeNode> findByPath(String path) {
        Query query = Query.query(Criteria.where("path").regex("^" + path));
        return mongoTemplate.find(query, TreeNode.class);
    }

    @Override
    public void updateNodeData(String nodeId, Object data) {
        Query query = Query.query(Criteria.where("id").is(nodeId));
        Update update = new Update()
                .set("data", data)
                .set("updateTime", LocalDateTime.now());
        UpdateResult result = mongoTemplate.updateFirst(query, update, TreeNode.class);
        if (result.getModifiedCount() == 0) {
            log.warn("No node updated for id: {}", nodeId);
        }
    }

    @Override
    public void deleteNode(String nodeId) {
        // 1. 删除当前节点
        Query nodeQuery = Query.query(Criteria.where("id").is(nodeId));
        mongoTemplate.remove(nodeQuery, TreeNode.class);

        // 2. 删除所有子节点
        Query childrenQuery = Query.query(
                Criteria.where("path").regex("^/.*" + nodeId + "/.*$")
        );
        mongoTemplate.remove(childrenQuery, TreeNode.class);
    }

    @Override
    public void moveNode(String nodeId, String newParentId) {
        // 1. 获取当前节点
        Optional<TreeNode> nodeOpt = findById(nodeId);
        if (nodeOpt.isEmpty()) {
            return;
        }
        TreeNode node = nodeOpt.get();

        // 2. 获取新父节点
        Optional<TreeNode> parentOpt = findById(newParentId);
        if (parentOpt.isEmpty()) {
            return;
        }
        TreeNode parent = parentOpt.get();

        // 3. 更新当前节点
        String oldPath = node.getPath();
        String newPath = parent.getPath() + "/" + node.getId();

        Query nodeQuery = Query.query(Criteria.where("id").is(nodeId));
        Update nodeUpdate = new Update()
                .set("parentId", newParentId)
                .set("path", newPath)
                .set("updateTime", LocalDateTime.now());
        mongoTemplate.updateFirst(nodeQuery, nodeUpdate, TreeNode.class);

        // 4. 更新所有子节点的路径
        Query childrenQuery = Query.query(
                Criteria.where("path").regex("^" + oldPath + "/.*$")
        );
        mongoTemplate.find(childrenQuery, TreeNode.class).forEach(child -> {
            String childNewPath = child.getPath().replace(oldPath, newPath);
            Update childUpdate = new Update()
                    .set("path", childNewPath)
                    .set("updateTime", LocalDateTime.now());
            mongoTemplate.updateFirst(
                    Query.query(Criteria.where("id").is(child.getId())),
                    childUpdate,
                    TreeNode.class
            );
        });
    }
}