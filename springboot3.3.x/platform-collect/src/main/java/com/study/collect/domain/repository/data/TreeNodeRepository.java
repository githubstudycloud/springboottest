package com.study.collect.domain.repository.data;

import com.study.collect.domain.entity.data.tree.TreeNode;
import com.study.collect.domain.repository.BaseRepository;

import java.util.List;

/**
 * 树节点仓储接口
 */
public interface TreeNodeRepository extends BaseRepository<TreeNode, String> {
    /**
     * 查询子节点
     */
    List<TreeNode> findByParentId(String parentId);

    /**
     * 根据路径查询节点
     */
    List<TreeNode> findByPathStartingWith(String path);

    /**
     * 根据类型查询节点
     */
    List<TreeNode> findByType(String type);

    /**
     * 批量更新节点
     */
    void updateBatch(List<TreeNode> nodes);
}