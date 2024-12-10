package com.study.collect.service;

import com.study.collect.entity.TreeCollectTask;
import com.study.collect.entity.TreeNode;
import com.study.collect.enums.TaskStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class TreeCollectorService {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public TreeCollectTask startCollection(String projectId) {
        // 创建采集任务
        TreeCollectTask task = new TreeCollectTask();
        task.setProjectId(projectId);
        task.setStatus(TaskStatus.RUNNING);
        task.setCreateTime(new Date());
        task.setStartTime(new Date());

        mongoTemplate.save(task);

        try {
            // 执行采集
            collectProjectTree(projectId, task);

            // 更新任务状态
            task.setStatus(TaskStatus.COMPLETED);
            task.setEndTime(new Date());
        } catch (Exception e) {
            log.error("Failed to collect tree for project: " + projectId, e);
            task.setStatus(TaskStatus.FAILED);
            task.getErrors().add(e.getMessage());
        }

        return mongoTemplate.save(task);
    }

    private void collectProjectTree(String projectId, TreeCollectTask task) {
        // 采集项目根节点
        TreeNode projectNode = new TreeNode();
        projectNode.setId("/p/" + projectId);
        projectNode.setProjectId(projectId);
        projectNode.setName("Project-" + projectId);
        projectNode.setType(TreeNode.NodeType.PROJECT);
        projectNode.setLevel(0);
        projectNode.setPath(List.of(projectNode.getId()));

        mongoTemplate.save(projectNode);

        // 采集Root节点
        collectRootNodes(projectNode, task);
    }

    private void collectRootNodes(TreeNode parent, TreeCollectTask task) {
        // 模拟采集3个根节点
        for (int i = 1; i <= 3; i++) {
            TreeNode root = new TreeNode();
            root.setId(parent.getId() + "/root" + i);
            root.setProjectId(parent.getProjectId());
            root.setParentId(parent.getId());
            root.setName("Root-" + i);
            root.setType(TreeNode.NodeType.ROOT);
            root.setLevel(parent.getLevel() + 1);
            root.setPath(new ArrayList<>(parent.getPath()));
            root.getPath().add(root.getId());

            mongoTemplate.save(root);

            // 为每个Root创建一个基线版本和执行版本
            collectVersions(root, task);
        }
    }

    private void collectVersions(TreeNode root, TreeCollectTask task) {
        // 创建基线版本
        TreeNode baseline = new TreeNode();
        baseline.setId(root.getId() + "/baseline");
        baseline.setProjectId(root.getProjectId());
        baseline.setParentId(root.getId());
        baseline.setName("Baseline");
        baseline.setType(TreeNode.NodeType.BASELINE_VERSION);
        baseline.setLevel(root.getLevel() + 1);
        baseline.setPath(new ArrayList<>(root.getPath()));
        baseline.getPath().add(baseline.getId());

        mongoTemplate.save(baseline);

        // 创建执行版本
        TreeNode execute = new TreeNode();
        execute.setId(root.getId() + "/execute");
        execute.setProjectId(root.getProjectId());
        execute.setParentId(root.getId());
        execute.setName("Execute");
        execute.setType(TreeNode.NodeType.EXECUTE_VERSION);
        execute.setLevel(root.getLevel() + 1);
        execute.setPath(new ArrayList<>(root.getPath()));
        execute.getPath().add(execute.getId());

        mongoTemplate.save(execute);

        // 为各版本创建Case Directory
        collectCaseDirectory(baseline, task);
        collectCaseDirectory(execute, task);
    }

    private void collectCaseDirectory(TreeNode version, TreeCollectTask task) {
        TreeNode caseDir = new TreeNode();
        caseDir.setId(version.getId() + "/cases");
        caseDir.setProjectId(version.getProjectId());
        caseDir.setParentId(version.getId());
        caseDir.setName("Cases");
        caseDir.setType(TreeNode.NodeType.CASE_DIRECTORY);
        caseDir.setLevel(version.getLevel() + 1);
        caseDir.setPath(new ArrayList<>(version.getPath()));
        caseDir.getPath().add(caseDir.getId());

        mongoTemplate.save(caseDir);

        // 创建一些测试用例和目录
        collectTestCasesAndDirectories(caseDir, task, 3);
    }

    private void collectTestCasesAndDirectories(TreeNode parent, TreeCollectTask task, int depth) {
        if (depth <= 0) return;

        // 创建Normal Directory
        TreeNode dir = new TreeNode();
        dir.setId(parent.getId() + "/dir" + depth);
        dir.setProjectId(parent.getProjectId());
        dir.setParentId(parent.getId());
        dir.setName("Directory-" + depth);
        dir.setType(TreeNode.NodeType.NORMAL_DIRECTORY);
        dir.setLevel(parent.getLevel() + 1);
        dir.setPath(new ArrayList<>(parent.getPath()));
        dir.getPath().add(dir.getId());

        mongoTemplate.save(dir);

        // 创建测试用例
        TreeNode testCase = new TreeNode();
        testCase.setId(parent.getId() + "/case" + depth);
        testCase.setProjectId(parent.getProjectId());
        testCase.setParentId(parent.getId());
        testCase.setName("TestCase-" + depth);
        testCase.setType(TreeNode.NodeType.TEST_CASE);
        testCase.setLevel(parent.getLevel() + 1);
        testCase.setPath(new ArrayList<>(parent.getPath()));
        testCase.getPath().add(testCase.getId());

        mongoTemplate.save(testCase);

        // 递归创建下一层
        collectTestCasesAndDirectories(dir, task, depth - 1);
    }
}
