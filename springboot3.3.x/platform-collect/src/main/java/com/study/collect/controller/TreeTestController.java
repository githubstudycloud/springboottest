package com.study.collect.controller;

import com.study.common.util.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test/tree")
public class TreeTestController {

    @GetMapping("/nodes")
    public Result<Map<String, Object>> getTestTreeData() {
        Map<String, Object> treeData = new HashMap<>();
        treeData.put("projectId", "test-project");
        treeData.put("nodes", generateTestNodes());
        return Result.success(treeData);
    }

    private List<Map<String, Object>> generateTestNodes() {
        List<Map<String, Object>> nodes = new ArrayList<>();

        // 添加ROOT节点
        for (int i = 1; i <= 2; i++) {
            Map<String, Object> root = new HashMap<>();
            root.put("id", "/p/test-project/root" + i);
            root.put("name", "Root-" + i);
            root.put("type", "ROOT");
            root.put("children", generateVersions(root.get("id").toString()));
            nodes.add(root);
        }

        return nodes;
    }

    private List<Map<String, Object>> generateVersions(String parentId) {
        List<Map<String, Object>> versions = new ArrayList<>();

        // 基线版本
        Map<String, Object> baseline = new HashMap<>();
        baseline.put("id", parentId + "/baseline");
        baseline.put("name", "Baseline");
        baseline.put("type", "BASELINE_VERSION");
        baseline.put("children", generateCaseDirectory(baseline.get("id").toString(), true));
        versions.add(baseline);

        // 执行版本
        Map<String, Object> execute = new HashMap<>();
        execute.put("id", parentId + "/execute");
        execute.put("name", "Execute");
        execute.put("type", "EXECUTE_VERSION");
        execute.put("children", generateCaseDirectory(execute.get("id").toString(), false));
        versions.add(execute);

        return versions;
    }

    private List<Map<String, Object>> generateCaseDirectory(String parentId, boolean isBaseline) {
        List<Map<String, Object>> directories = new ArrayList<>();

        Map<String, Object> caseDir = new HashMap<>();
        caseDir.put("id", parentId + "/cases");
        caseDir.put("name", "Cases");
        caseDir.put("type", "CASE_DIRECTORY");

        if (isBaseline) {
            // 基线版本下添加普通目录和用例
            caseDir.put("children", generateNormalDirectories(caseDir.get("id").toString(), 3));
        } else {
            // 执行版本下添加场景节点
            caseDir.put("children", generateScenarios(caseDir.get("id").toString()));
        }

        directories.add(caseDir);
        return directories;
    }

    private List<Map<String, Object>> generateScenarios(String parentId) {
        List<Map<String, Object>> scenarios = new ArrayList<>();

        for (int i = 1; i <= 2; i++) {
            Map<String, Object> scenario = new HashMap<>();
            scenario.put("id", parentId + "/scenario" + i);
            scenario.put("name", "Scenario-" + i);
            scenario.put("type", "SCENARIO");
            scenario.put("children", generateNormalDirectories(scenario.get("id").toString(), 2));
            scenarios.add(scenario);
        }

        return scenarios;
    }

    private List<Map<String, Object>> generateNormalDirectories(String parentId, int depth) {
        List<Map<String, Object>> items = new ArrayList<>();
        if (depth <= 0) return items;

        // 添加目录
        Map<String, Object> dir = new HashMap<>();
        dir.put("id", parentId + "/dir" + depth);
        dir.put("name", "Directory-" + depth);
        dir.put("type", "NORMAL_DIRECTORY");
        dir.put("children", generateNormalDirectories(dir.get("id").toString(), depth - 1));
        items.add(dir);

        // 添加测试用例
        Map<String, Object> testCase = new HashMap<>();
        testCase.put("id", parentId + "/case" + depth);
        testCase.put("name", "TestCase-" + depth);
        testCase.put("type", "TEST_CASE");
        items.add(testCase);

        return items;
    }
}
