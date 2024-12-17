package com.study.collect.domain.entity.data.tree;

// 树节点

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 树节点实体
 */
@Data
@Document(collection = "tree_nodes")
public class TreeNode {
    @Id
    private String id;

    /**
     * 父节点ID
     */
    private String parentId;

    /**
     * 节点路径
     */
    private String path;

    /**
     * 节点类型
     */
    private String type;

    /**
     * 节点名称
     */
    private String name;

    /**
     * 节点数据
     */
    private Map<String, Object> data;

    /**
     * 是否叶子节点
     */
    private Boolean leaf;

    /**
     * 节点层级
     */
    private Integer level;

    /**
     * 排序序号
     */
    private Integer orderNum;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}