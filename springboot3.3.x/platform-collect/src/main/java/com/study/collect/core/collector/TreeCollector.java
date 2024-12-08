package com.study.collect.core.collector;

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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component("treeCollector")
@CollectorFor(CollectorType.TREE)  // 添加CollectorFor注解指定类型
public class TreeCollector implements Collector {

    private static final Logger logger = LoggerFactory.getLogger(TreeCollector.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private RestTemplate restTemplate;

    public TreeCollector() {
        logger.info("TreeCollector initialized with type: {}", CollectorType.TREE);
    }

    @Override
    public TaskResult collect(CollectTask task) {
        String lockKey = "collect:task:lock:" + task.getId();

        // 尝试获取分布式锁
        Boolean acquired = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, "LOCKED", 10, TimeUnit.MINUTES);

        if (Boolean.TRUE.equals(acquired)) {
            try {
                return doCollect(task);
            } finally {
                redisTemplate.delete(lockKey);
            }
        } else {
            return TaskResult.builder()
                    .taskId(task.getId())
                    .status(TaskStatus.FAILED)
                    .message("Task is already running")
                    .build();
        }
    }

    private TaskResult doCollect(CollectTask task) {
        try {
            // 获取树状结构数据
            ResponseEntity<String> response = restTemplate.exchange(
                    task.getUrl(),
                    HttpMethod.valueOf(task.getMethod()),
                    new HttpEntity<>(task.getRequestBody(), createHeaders(task.getHeaders())),
                    String.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                return TaskResult.builder()
                        .taskId(task.getId())
                        .status(TaskStatus.FAILED)
                        .message("Failed to fetch tree data: " + response.getStatusCode())
                        .build();
            }

            // 解析并保存树状结构
            List<TreeNode> nodes = parseAndSaveTreeNodes(response.getBody());

            return TaskResult.builder()
                    .taskId(task.getId())
                    .status(TaskStatus.COMPLETED)
                    .statusCode(response.getStatusCode().value())
                    .message("Successfully collected " + nodes.size() + " nodes")
                    .build();

        } catch (Exception e) {
            logger.error("Error collecting tree structure for task: " + task.getId(), e);
            return TaskResult.builder()
                    .taskId(task.getId())
                    .status(TaskStatus.FAILED)
                    .message("Collection failed: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public TaskStatus getTaskStatus(String taskId) {
        String statusKey = "collect:task:status:" + taskId;
        Object status = redisTemplate.opsForValue().get(statusKey);
        return status != null ? (TaskStatus) status : TaskStatus.UNKNOWN;
    }

    @Override
    public void stopTask(String taskId) {
        String lockKey = "collect:task:lock:" + taskId;
        redisTemplate.delete(lockKey);

        String statusKey = "collect:task:status:" + taskId;
        redisTemplate.opsForValue().set(statusKey, TaskStatus.STOPPED);
    }

    private HttpHeaders createHeaders(Map<String, String> headers) {
        HttpHeaders httpHeaders = new HttpHeaders();
        if (headers != null) {
            headers.forEach(httpHeaders::add);
        }
        return httpHeaders;
    }


    @Autowired
    private ObjectMapper objectMapper;

    private List<TreeNode> parseAndSaveTreeNodes(String jsonData) throws Exception {
        List<TreeNode> allNodes = new ArrayList<>();

        // 解析JSON数据
        Map<String, Object> treeData = objectMapper.readValue(jsonData, Map.class);
        String projectId = (String) treeData.get("projectId");
        List<Map<String, Object>> nodes = (List<Map<String, Object>>) treeData.get("nodes");

        // 解析所有节点
        parseNodes(projectId, null, nodes, 0, new ArrayList<>(), allNodes);

        // 批量保存节点
        return new ArrayList<>(mongoTemplate.insert(allNodes, TreeNode.class));
    }

    private void parseNodes(String projectId, String parentId, List<Map<String, Object>> nodes,
                            int level, List<String> parentPath, List<TreeNode> allNodes) {
        if (nodes == null) return;

        for (Map<String, Object> nodeData : nodes) {
            // 创建节点
            TreeNode node = new TreeNode();
            node.setId((String) nodeData.get("id"));
            node.setProjectId(projectId);
            node.setParentId(parentId);
            node.setName((String) nodeData.get("name"));
            node.setType(TreeNode.NodeType.valueOf((String) nodeData.get("type")));
            node.setLevel(level);

            // 构建路径
            List<String> currentPath = new ArrayList<>(parentPath);
            currentPath.add(node.getId());
            node.setPath(currentPath);

            // 设置时间戳
            Date now = new Date();
            node.setCreateTime(now);
            node.setUpdateTime(now);

            // 添加到结果列表
            allNodes.add(node);

            // 递归处理子节点
            List<Map<String, Object>> children = (List<Map<String, Object>>) nodeData.get("children");
            if (children != null) {
                parseNodes(projectId, node.getId(), children, level + 1, currentPath, allNodes);
            }
        }
    }
}
