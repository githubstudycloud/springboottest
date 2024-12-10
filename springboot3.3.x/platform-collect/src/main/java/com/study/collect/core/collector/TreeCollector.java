package com.study.collect.core.collector;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.collect.annotation.CollectorFor;
import com.study.collect.entity.CollectTask;
import com.study.collect.entity.TaskResult;
import com.study.collect.entity.TreeNode;
import com.study.collect.enums.CollectorType;
import com.study.collect.enums.TaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component("treeCollector")
@CollectorFor(CollectorType.TREE)  // 添加CollectorFor注解指定类型
public class TreeCollector implements Collector {

    private static final Logger logger = LoggerFactory.getLogger(TreeCollector.class);
    private static final String LOCK_PREFIX = "collect:task:lock:";
    private static final String STATUS_PREFIX = "collect:task:status:";
    private static final int LOCK_TIMEOUT = 10;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public TreeCollector() {
        logger.info("TreeCollector initialized with type: {}", CollectorType.TREE);
    }

    @Override
    public TaskResult collect(CollectTask task) {
        String lockKey = LOCK_PREFIX + task.getId();

        try {
            Boolean acquired = acquireLock(lockKey);
            if (!acquired) {
                return buildTaskResult(task.getId(), TaskStatus.FAILED, "Task is already running");
            }

            return doCollect(task);
        } catch (Exception e) {
            logger.error("Failed to collect tree for task: {}", task.getId(), e);
            return buildTaskResult(task.getId(), TaskStatus.FAILED, "Collection failed: " + e.getMessage());
        } finally {
            releaseLock(lockKey);
        }
    }

    private Boolean acquireLock(String lockKey) {
        return redisTemplate.opsForValue()
                .setIfAbsent(lockKey, "LOCKED", LOCK_TIMEOUT, TimeUnit.MINUTES);
    }

    private void releaseLock(String lockKey) {
        try {
            redisTemplate.delete(lockKey);
        } catch (Exception e) {
            logger.error("Failed to release lock: {}", lockKey, e);
        }
    }

    private TaskResult doCollect(CollectTask task) {
        try {
            logger.info("Starting tree data collection for task: {}", task.getId());

            ResponseEntity<String> response = fetchTreeData(task);
            if (!response.getStatusCode().is2xxSuccessful()) {
                String message = String.format("Failed to fetch tree data: %s", response.getStatusCode());
                logger.error(message);
                return buildTaskResult(task.getId(), TaskStatus.FAILED, message);
            }

            List<TreeNode> nodes = processTreeData(response.getBody(), task.getId());

            return buildTaskResult(task.getId(), TaskStatus.COMPLETED,
                    String.format("Successfully collected %d nodes", nodes.size()),
                    response.getStatusCode().value());

        } catch (Exception e) {
            throw new RuntimeException("Error collecting tree data", e);
        }
    }

    private ResponseEntity<String> fetchTreeData(CollectTask task) {
        HttpHeaders headers = createHeaders(task.getHeaders());
        HttpEntity<?> requestEntity = new HttpEntity<>(task.getRequestBody(), headers);

        return restTemplate.exchange(
                task.getUrl(),
                HttpMethod.valueOf(task.getMethod()),
                requestEntity,
                String.class
        );
    }

//    private List<TreeNode> processTreeData(String jsonData, String taskId) throws Exception {
//        Map<String, Object> treeData = objectMapper.readValue(jsonData, new TypeReference<Map<String, Object>>() {});
//        String projectId = (String) treeData.get("projectId");
//
//        @SuppressWarnings("unchecked")
//        List<Map<String, Object>> nodes = (List<Map<String, Object>>) treeData.get("nodes");
//
//        // Clear existing nodes for this task
//        clearExistingNodes(taskId);
//
//        // Process and save new nodes
//        List<TreeNode> allNodes = new ArrayList<>();
//        parseNodes(projectId, null, nodes, 0, new ArrayList<>(), allNodes, taskId);
//
//        return saveNodes(allNodes);
//    }

//    private void clearExistingNodes(String taskId) {
//        Query query = Query.query(Criteria.where("taskId").is(taskId));
//        mongoTemplate.remove(query, TreeNode.class);
//        logger.info("Cleared existing nodes for task {}", taskId);
//    }

    private List<TreeNode> saveNodes(List<TreeNode> nodes) {
        // 使用批量插入提升性能
        List<TreeNode> savedNodes = new ArrayList<>(mongoTemplate.insert(nodes, TreeNode.class));
        logger.info("Saved {} nodes to MongoDB", savedNodes.size());
        return savedNodes;
    }

//    private void parseNodes(String projectId, String parentId, List<Map<String, Object>> nodes,
//                            int level, List<String> parentPath, List<TreeNode> allNodes, String taskId) {
//        if (nodes == null) return;
//
//        for (Map<String, Object> nodeData : nodes) {
//            try {
//                TreeNode node = createNode(nodeData, projectId, parentId, level, parentPath, taskId);
//                allNodes.add(node);
//
//                // Process children recursively
//                @SuppressWarnings("unchecked")
//                List<Map<String, Object>> children = (List<Map<String, Object>>) nodeData.get("children");
//                if (children != null) {
//                    List<String> currentPath = new ArrayList<>(parentPath);
//                    currentPath.add(node.getId());
//                    parseNodes(projectId, node.getId(), children, level + 1, currentPath, allNodes, taskId);
//                }
//            } catch (Exception e) {
//                logger.error("Error processing node: {}", nodeData, e);
//            }
//        }
//    }

    private TreeNode createNode(Map<String, Object> nodeData, String projectId, String parentId,
                                int level, List<String> parentPath, String taskId) {
        TreeNode node = new TreeNode();
        node.setId((String) nodeData.get("id"));
        node.setProjectId(projectId);
        node.setTaskId(taskId);
        node.setParentId(parentId);
        node.setName((String) nodeData.get("name"));
        node.setType(TreeNode.NodeType.valueOf((String) nodeData.get("type")));
        node.setLevel(level);

        List<String> currentPath = new ArrayList<>(parentPath);
        currentPath.add(node.getId());
        node.setPath(currentPath);

        Date now = new Date();
        node.setCreateTime(now);
        node.setUpdateTime(now);

        return node;
    }

    private HttpHeaders createHeaders(Map<String, String> headers) {
        HttpHeaders httpHeaders = new HttpHeaders();
        if (headers != null) {
            headers.forEach(httpHeaders::add);
        }
        return httpHeaders;
    }

    private TaskResult buildTaskResult(String taskId, TaskStatus status, String message) {
        return buildTaskResult(taskId, status, message, null);
    }

    private TaskResult buildTaskResult(String taskId, TaskStatus status, String message, Integer statusCode) {
        return TaskResult.builder()
                .taskId(taskId)
                .status(status)
                .message(message)
                .statusCode(statusCode)
                .build();
    }

    @Override
    public TaskStatus getTaskStatus(String taskId) {
        Object status = redisTemplate.opsForValue().get(STATUS_PREFIX + taskId);
        return status != null ? (TaskStatus) status : TaskStatus.UNKNOWN;
    }

    @Override
    public void stopTask(String taskId) {
        redisTemplate.delete(LOCK_PREFIX + taskId);
        redisTemplate.opsForValue().set(STATUS_PREFIX + taskId, TaskStatus.STOPPED);
        logger.info("Task {} has been stopped", taskId);
    }

//    private List<TreeNode> processTreeData(String jsonData, String taskId) throws Exception {
//        try {
//            // 先记录原始数据便于调试
//            logger.debug("Processing tree data for task {}: {}", taskId, jsonData);
//
//            // 使用TypeReference确保正确的类型转换
//            Map<String, Object> treeData = objectMapper.readValue(jsonData,
//                    new TypeReference<Map<String, Object>>() {});
//
//            if (treeData == null) {
//                throw new IllegalArgumentException("Tree data is null");
//            }
//
//            String projectId = (String) treeData.get("projectId");
//            if (projectId == null) {
//                throw new IllegalArgumentException("Project ID is missing in tree data");
//            }
//
//            @SuppressWarnings("unchecked")
//            List<Map<String, Object>> nodes = (List<Map<String, Object>>) treeData.get("nodes");
//            if (nodes == null || nodes.isEmpty()) {
//                logger.warn("No nodes found in tree data for task {}", taskId);
//                return Collections.emptyList();
//            }
//
//            // Clear existing nodes
//            clearExistingNodes(taskId);
//
//            // Process nodes
//            List<TreeNode> allNodes = new ArrayList<>();
//            parseNodes(projectId, null, nodes, 0, new ArrayList<>(), allNodes, taskId);
//
//            if (allNodes.isEmpty()) {
//                logger.warn("No nodes were parsed from the tree data for task {}", taskId);
//                return Collections.emptyList();
//            }
//
//            // Save nodes in batches
//            List<TreeNode> savedNodes = saveNodesInBatches(allNodes, 100);
//            logger.info("Successfully saved {} nodes for task {}", savedNodes.size(), taskId);
//
//            return savedNodes;
//
//        } catch (Exception e) {
//            logger.error("Error processing tree data for task {}: {}", taskId, e.getMessage());
//            throw e;
//        }
//    }

//    private List<TreeNode> processTreeData(String jsonData, String taskId) throws Exception {
//        try {
//            logger.debug("Processing tree data for task {}: {}", taskId, jsonData);
//
//            // 先解析外层响应结构
//            Map<String, Object> response = objectMapper.readValue(jsonData,
//                    new TypeReference<Map<String, Object>>() {});
//
//            if (response == null || !response.containsKey("data")) {
//                throw new IllegalArgumentException("Invalid response format");
//            }
//
//            // 获取data对象
//            @SuppressWarnings("unchecked")
//            Map<String, Object> data = (Map<String, Object>) response.get("data");
//            if (data == null) {
//                throw new IllegalArgumentException("Response data is null");
//            }
//
//            // 从data中获取projectId
//            String projectId = (String) data.get("projectId");
//            if (projectId == null || projectId.isEmpty()) {
//                throw new IllegalArgumentException("Project ID is missing in data");
//            }
//
//            // 从data中获取nodes数组
//            @SuppressWarnings("unchecked")
//            List<Map<String, Object>> nodes = (List<Map<String, Object>>) data.get("nodes");
//            if (nodes == null || nodes.isEmpty()) {
//                logger.warn("No nodes found in tree data for task {}", taskId);
//                return Collections.emptyList();
//            }
//
//            // 清理旧数据
//            clearExistingNodes(taskId);
//
//            // 解析节点
//            List<TreeNode> allNodes = new ArrayList<>();
//            parseNodes(projectId, null, nodes, 0, new ArrayList<>(), allNodes, taskId);
//
//            if (allNodes.isEmpty()) {
//                logger.warn("No nodes were parsed from the tree data for task {}", taskId);
//                return Collections.emptyList();
//            }
//
//            // 批量保存
//            List<TreeNode> savedNodes = saveNodesInBatches(allNodes, 100);
//            logger.info("Successfully saved {} nodes for task {}", savedNodes.size(), taskId);
//
//            return savedNodes;
//
//        } catch (Exception e) {
//            logger.error("Error processing tree data for task {}: {}", taskId, e.getMessage());
//            throw e;
//        }
//    }

    private void clearExistingNodes(String taskId) {
        Query query = Query.query(Criteria.where("taskId").is(taskId));
        mongoTemplate.remove(query, TreeNode.class);
        logger.info("Cleared existing nodes for task {}", taskId);
    }
//
//    private List<TreeNode> saveNodesInBatches(List<TreeNode> nodes, int batchSize) {
//        List<TreeNode> savedNodes = new ArrayList<>();
//
//        for (int i = 0; i < nodes.size(); i += batchSize) {
//            int end = Math.min(nodes.size(), i + batchSize);
//            List<TreeNode> batch = nodes.subList(i, end);
//            try {
//                savedNodes.addAll(mongoTemplate.insert(batch, TreeNode.class));
//                logger.debug("Saved batch of {} nodes, total saved: {}",
//                        batch.size(), savedNodes.size());
//            } catch (Exception e) {
//                logger.error("Error saving batch of nodes: {}", e.getMessage());
//                throw e;
//            }
//        }
//        return savedNodes;
//    }

    private void parseNodes(String projectId, String parentId, List<Map<String, Object>> nodes,
                            int level, List<String> parentPath, List<TreeNode> allNodes, String taskId) {
        if (nodes == null) return;

        for (Map<String, Object> nodeData : nodes) {
            try {
                // 验证必要的字段
                String id = (String) nodeData.get("id");
                String name = (String) nodeData.get("name");
                String type = (String) nodeData.get("type");

                if (id == null || name == null || type == null) {
                    logger.error("Missing required fields in node data: {}", nodeData);
                    continue;
                }

                // 创建节点
                TreeNode node = new TreeNode();
                node.setId(id);
                node.setProjectId(projectId);
                node.setTaskId(taskId);
                node.setParentId(parentId);
                node.setName(name);
                try {
                    node.setType(TreeNode.NodeType.valueOf(type));
                } catch (IllegalArgumentException e) {
                    logger.error("Invalid node type: {} for node: {}", type, id);
                    continue;
                }
                node.setLevel(level);

                // 设置路径
                List<String> currentPath = new ArrayList<>(parentPath);
                currentPath.add(node.getId());
                node.setPath(currentPath);

                // 设置时间戳
                Date now = new Date();
                node.setCreateTime(now);
                node.setUpdateTime(now);

                // 添加到结果列表
                allNodes.add(node);
                logger.debug("Successfully parsed node: {}, type: {}, level: {}",
                        id, type, level);

                // 递归处理子节点
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> children = (List<Map<String, Object>>) nodeData.get("children");
                if (children != null && !children.isEmpty()) {
                    parseNodes(projectId, node.getId(), children, level + 1, currentPath, allNodes, taskId);
                }

            } catch (Exception e) {
                logger.error("Error parsing node data: {}", nodeData, e);
            }
        }
    }

//    private List<TreeNode> saveNodesInBatches(List<TreeNode> nodes, int batchSize) {
//        List<TreeNode> savedNodes = new ArrayList<>();
//        for (int i = 0; i < nodes.size(); i += batchSize) {
//            int end = Math.min(nodes.size(), i + batchSize);
//            List<TreeNode> batch = nodes.subList(i, end);
//            try {
//                savedNodes.addAll(mongoTemplate.insert(batch, TreeNode.class));
//                logger.debug("Saved batch of {} nodes", batch.size());
//            } catch (Exception e) {
//                logger.error("Error saving batch of nodes: {}", e.getMessage());
//                throw e;
//            }
//        }
//        return savedNodes;
//    }

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
                    new TypeReference<Map<String, Object>>() {
                    });

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