package com.study.collect.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@Document(collection = "tree_nodes")
public class TreeNode {
    @Id
    private String id;           // 节点路径作为ID
    private String projectId;    // 项目ID
    private String parentId;     // 父节点ID
    private String name;         // 节点名称
    private NodeType type;       // 节点类型
    private Integer level;       // 节点层级
    private List<String> path;   // 完整路径
    private Date createTime;
    private Date updateTime;
    private List<TreeNode> children;
    private String taskId;


    public enum NodeType {
        PROJECT,
        ROOT,
        BASELINE_VERSION,
        EXECUTE_VERSION,
        CASE_DIRECTORY,
        SCENARIO,
        NORMAL_DIRECTORY,
        TEST_CASE
    }
}
