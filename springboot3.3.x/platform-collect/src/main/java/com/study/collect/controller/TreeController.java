package com.study.collect.controller;


import com.study.collect.entity.TreeCollectTask;
import com.study.collect.entity.TreeNode;
import com.study.collect.service.TreeCollectorService;
import com.study.common.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tree")
public class TreeController {

    @Autowired
    private TreeCollectorService treeCollectorService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @PostMapping("/collect/{projectId}")
    public Result<TreeCollectTask> startCollection(@PathVariable String projectId) {
        try {
            TreeCollectTask task = treeCollectorService.startCollection(projectId);
            return Result.success(task);
        } catch (Exception e) {
            return Result.error("Failed to start collection: " + e.getMessage());
        }
    }

    @GetMapping("/nodes/{projectId}")
    public Result<List<TreeNode>> getProjectNodes(@PathVariable String projectId) {
        try {
            List<TreeNode> nodes = mongoTemplate.find(
                    Query.query(Criteria.where("projectId").is(projectId)),
                    TreeNode.class
            );
            return Result.success(nodes);
        } catch (Exception e) {
            return Result.error("Failed to get nodes: " + e.getMessage());
        }
    }
}

