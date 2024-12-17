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

/**
 * 树节点仓储实现
 */
@Repository
@RequiredArgsConstructor
public class TreeNodeRepositoryImpl implements TreeNodeRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public TreeNode save(TreeNode node) {
        return mongoTemplate.save(node);
    }

    @Override
    public List<TreeNode> saveAll(Iterable<TreeNode> nodes) {
        return StreamSupport.stream(nodes.spliterator(), false)
                .map(this::save)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        Query query = Query.query(Criteria.where("id").is(id));
        mongoTemplate.remove(query, TreeNode.class);
    }

    @Override
    public Optional<TreeNode> findById(String id) {
        return Optional.ofNullable(mongoTemplate.findById(id, TreeNode.class));
    }

    @Override
    public List<TreeNode> findAll() {
        return mongoTemplate.findAll(TreeNode.class);
    }

    @Override
    public List<TreeNode> findByParentId(String parentId) {
        Query query = Query.query(Criteria.where("parentId").is(parentId));
        return mongoTemplate.find(query, TreeNode.class);
    }

    @Override
    public List<TreeNode> findByPathStartingWith(String path) {
        Query query = Query.query(Criteria.where("path").regex("^" + path));
        return mongoTemplate.find(query, TreeNode.class);
    }

    @Override
    public List<TreeNode> findByType(String type) {
        Query query = Query.query(Criteria.where("type").is(type));
        return mongoTemplate.find(query, TreeNode.class);
    }

    @Override
    public void updateBatch(List<TreeNode> nodes) {
        nodes.forEach(node -> {
            Query query = Query.query(Criteria.where("id").is(node.getId()));
            Update update = Update.update("data", node.getData())
                    .set("updateTime", LocalDateTime.now());
            mongoTemplate.updateFirst(query, update, TreeNode.class);
        });
    }
}