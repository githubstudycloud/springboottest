package com.study.collect.controller;

import com.study.collect.entity.TreeNode;
import com.study.collect.service.TreeQueryService;
import com.study.common.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tree/query")
public class TreeQueryController {

    @Autowired
    private TreeQueryService treeQueryService;

    @GetMapping("/project/{projectId}")
    public Result<List<TreeNode>> getProjectTree(@PathVariable String projectId) {
        try {
            List<TreeNode> tree = treeQueryService.getProjectTree(projectId);
            return Result.success(tree);
        } catch (Exception e) {
            return Result.error("Failed to get project tree: " + e.getMessage());
        }
    }

    @GetMapping("/node/{nodeId}")
    public Result<List<TreeNode>> getSubTree(@PathVariable String nodeId) {
        try {
            List<TreeNode> subTree = treeQueryService.getSubTree(nodeId);
            return Result.success(subTree);
        } catch (Exception e) {
            return Result.error("Failed to get sub tree: " + e.getMessage());
        }
    }

    @GetMapping("/type/{projectId}/{type}")
    public Result<List<TreeNode>> getNodesByType(
            @PathVariable String projectId,
            @PathVariable TreeNode.NodeType type) {
        try {
            List<TreeNode> nodes = treeQueryService.getNodesByType(projectId, type);
            return Result.success(nodes);
        } catch (Exception e) {
            return Result.error("Failed to get nodes by type: " + e.getMessage());
        }
    }

    @GetMapping("/children/{parentId}")
    public Result<List<TreeNode>> getDirectChildren(@PathVariable String parentId) {
        try {
            List<TreeNode> children = treeQueryService.getDirectChildren(parentId);
            return Result.success(children);
        } catch (Exception e) {
            return Result.error("Failed to get children: " + e.getMessage());
        }
    }
}
