从错误日志可以看出,问题出在保存树节点时遇到了主键冲突错误,这是因为当前的实现只做了插入操作,没有处理更新的情况。我会修改 TreeCollector 中的相关代码来处理更新情况:



```java
package com.study.collect.core.collector;

// ... 前面的导入语句保持不变

@Component("treeCollector") 
@CollectorFor(CollectorType.TREE)
public class TreeCollector implements Collector {
    
    // ... 其他代码保持不变
    
    private List<TreeNode> saveNodesInBatches(List<TreeNode> nodes, int batchSize) {
        List<TreeNode> savedNodes = new ArrayList<>();
        for (int i = 0; i < nodes.size(); i += batchSize) {
            int end = Math.min(nodes.size(), i + batchSize);
            List<TreeNode> batch = nodes.subList(i, end);
            try {
                // 对每个节点进行保存或更新
                batch.forEach(node -> {
                    try {
                        Query query = Query.query(Criteria.where("_id").is(node.getId()));
                        TreeNode existingNode = mongoTemplate.findOne(query, TreeNode.class);
                        
                        if (existingNode != null) {
                            // 如果节点已存在,执行更新操作
                            // 创建更新对象,不包含_id字段
                            Update update = new Update()
                                .set("projectId", node.getProjectId())
                                .set("parentId", node.getParentId())
                                .set("name", node.getName())
                                .set("type", node.getType())
                                .set("level", node.getLevel())
                                .set("path", node.getPath())
                                .set("taskId", node.getTaskId())
                                .set("updateTime", new Date());

                            mongoTemplate.updateFirst(query, update, TreeNode.class);
                            savedNodes.add(node);
                            logger.debug("Updated existing node: {}", node.getId());
                        } else {
                            // 如果节点不存在,执行插入操作
                            TreeNode saved = mongoTemplate.insert(node);
                            savedNodes.add(saved);
                            logger.debug("Inserted new node: {}", node.getId());
                        }
                    } catch (Exception e) {
                        logger.error("Error saving/updating node {}: {}", node.getId(), e.getMessage());
                        throw e;
                    }
                });
                logger.debug("Processed batch of {} nodes", batch.size());
            } catch (Exception e) {
                logger.error("Error processing batch of nodes: {}", e.getMessage());
                throw e;
            }
        }
        return savedNodes;
    }

    // 清理已存在节点的方法也需要修改,仅删除不再使用的节点
    private void clearExistingNodes(String taskId, List<String> newNodeIds) {
        try {
            // 获取当前任务的所有现有节点ID
            Query findQuery = Query.query(Criteria.where("taskId").is(taskId));
            findQuery.fields().include("_id");
            List<TreeNode> existingNodes = mongoTemplate.find(findQuery, TreeNode.class);
            List<String> existingIds = existingNodes.stream()
                .map(TreeNode::getId)
                .collect(Collectors.toList());

            // 找出需要删除的节点(在现有节点中但不在新节点中的)
            existingIds.removeAll(newNodeIds);
            if (!existingIds.isEmpty()) {
                Query deleteQuery = Query.query(Criteria.where("_id").in(existingIds));
                mongoTemplate.remove(deleteQuery, TreeNode.class);
                logger.info("Removed {} obsolete nodes for task {}", existingIds.size(), taskId);
            }
        } catch (Exception e) {
            logger.error("Failed to clear existing nodes for task {}: {}", taskId, e.getMessage());
            throw e;
        }
    }

    // processTreeData方法也需要相应修改
    private List<TreeNode> processTreeData(String jsonData, String taskId) throws Exception {
        try {
            logger.debug("Processing tree data for task {}: {}", taskId, jsonData);

            Map<String, Object> response = objectMapper.readValue(jsonData,
                    new TypeReference<Map<String, Object>>() {});

            if (response == null || !response.containsKey("data")) {
                throw new IllegalArgumentException("Invalid response format");
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) response.get("data");
            String projectId = (String) data.get("projectId");
            if (projectId == null || projectId.isEmpty()) {
                throw new IllegalArgumentException("Project ID is missing in data");
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> nodes = (List<Map<String, Object>>) data.get("nodes");
            if (nodes == null || nodes.isEmpty()) {
                logger.warn("No nodes found in tree data for task {}", taskId);
                return Collections.emptyList();
            }

            // 收集所有新节点ID
            List<TreeNode> allNodes = new ArrayList<>();
            List<String> newNodeIds = new ArrayList<>();
            parseNodes(projectId, null, nodes, 0, new ArrayList<>(), allNodes, taskId, newNodeIds);

            // 清理不再使用的节点
            clearExistingNodes(taskId, newNodeIds);

            // 批量保存/更新节点
            if (!allNodes.isEmpty()) {
                List<TreeNode> savedNodes = saveNodesInBatches(allNodes, 100);
                logger.info("Successfully saved/updated {} nodes for task {}", savedNodes.size(), taskId);
                return savedNodes;
            }

            return Collections.emptyList();
        } catch (Exception e) {
            logger.error("Error processing tree data for task {}: {}", taskId, e.getMessage());
            throw e;
        }
    }

    // parseNodes方法需要增加收集节点ID的功能
    private void parseNodes(String projectId, String parentId, List<Map<String, Object>> nodes,
                         int level, List<String> parentPath, List<TreeNode> allNodes, 
                         String taskId, List<String> nodeIds) {
        if (nodes == null) return;

        for (Map<String, Object> nodeData : nodes) {
            try {
                String id = (String) nodeData.get("id");
                String name = (String) nodeData.get("name");
                String type = (String) nodeData.get("type");

                if (id == null || name == null || type == null) {
                    logger.error("Missing required fields in node data: {}", nodeData);
                    continue;
                }

                // 创建节点并添加到列表
                TreeNode node = createNode(nodeData, projectId, parentId, level, parentPath, taskId);
                allNodes.add(node);
                nodeIds.add(node.getId());

                // 处理子节点
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> children = (List<Map<String, Object>>) nodeData.get("children");
                if (children != null && !children.isEmpty()) {
                    List<String> currentPath = new ArrayList<>(parentPath);
                    currentPath.add(node.getId());
                    parseNodes(projectId, node.getId(), children, level + 1, currentPath, 
                             allNodes, taskId, nodeIds);
                }
            } catch (Exception e) {
                logger.error("Error parsing node data: {}", nodeData, e);
            }
        }
    }
}

```

主要修改包括:

1. saveNodesInBatches 方法:
- 改为单个节点处理以支持更新操作
- 对每个节点先检查是否存在
- 存在则更新,不存在则插入
- 使用 Update 对象来指定更新的字段

2. clearExistingNodes 方法:
- 改为只删除不再使用的节点
- 通过比对现有节点和新节点ID来确定需要删除的节点

3. processTreeData 方法:
- 增加了收集新节点ID的功能
- 在保存前先清理不再使用的节点

4. parseNodes 方法:
- 增加了收集节点ID的参数
- 在处理每个节点时同时收集节点ID

这样修改后,系统就能正确处理树节点的更新操作,避免主键冲突错误,同时也能清理掉不再使用的节点。

要测试这些修改,你可以:
1. 多次提交相同的树结构,确认不会出现主键冲突
2. 修改已存在节点的属性,确认更新成功
3. 删除某些节点,确认旧节点被正确清理

需要我提供测试用例或者其他帮助吗?