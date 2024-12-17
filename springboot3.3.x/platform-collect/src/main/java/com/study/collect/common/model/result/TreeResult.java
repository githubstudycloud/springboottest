package com.study.collect.common.model.result;

// 树形结果

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 树形结构响应结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TreeResult<T> {
    /**
     * 节点ID
     */
    private String id;

    /**
     * 父节点ID
     */
    private String parentId;

    /**
     * 节点数据
     */
    private T data;

    /**
     * 子节点
     */
    private List<TreeResult<T>> children;

    /**
     * 是否叶子节点
     */
    private Boolean leaf;

    /**
     * 节点层级
     */
    private Integer level;

    public static <T> TreeResult<T> of(T data) {
        return TreeResult.<T>builder()
                .data(data)
                .children(new ArrayList<>())
                .leaf(true)
                .level(0)
                .build();
    }
}
