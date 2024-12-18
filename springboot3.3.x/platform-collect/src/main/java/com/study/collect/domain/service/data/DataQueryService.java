package com.study.collect.domain.service.data;

// 数据查询服务

import com.study.collect.common.model.result.PageResult;
import com.study.collect.domain.entity.data.CollectData;
import com.study.collect.domain.repository.data.CollectDataRepository;
import com.study.collect.domain.repository.data.TreeNodeRepository;
import com.study.collect.model.request.query.DataQueryRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 数据查询服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataQueryService {

    private final CollectDataRepository dataRepository;
    private final TreeNodeRepository treeRepository;
    private final CacheManager cacheManager;

    /**
     * 分页查询数据
     */
    public PageResult<CollectData> queryByPage(DataQueryRequest request) {
        try {
            // 1. 构建查询条件
            Query query = buildQuery(request);

            // 2. 查询总数
            long total = dataRepository.count(query);
            if (total == 0) {
                return PageResult.empty();
            }

            // 3. 分页查询
            query.skip((request.getPageNum() - 1) * request.getPageSize())
                    .limit(request.getPageSize());
            List<CollectData> list = dataRepository.find(query);

            // 4. 构建结果
            return PageResult.<CollectData>builder()
                    .pageNum(request.getPageNum())
                    .pageSize(request.getPageSize())
                    .total(total)
                    .list(list)
                    .build();

        } catch (Exception e) {
            log.error("Query data failed", e);
            throw new DataQueryException("Query data failed: " + e.getMessage());
        }
    }

    /**
     * 树形数据查询
     */
    public TreeResult<TreeNode> queryTreeData(String rootId) {
        try {
            // 1. 获取根节点
            TreeNode root = getTreeNode(rootId);
            if (root == null) {
                return null;
            }

            // 2. 递归查询子节点
            buildChildrenTree(root);

            // 3. 构建树形结果
            return buildTreeResult(root);

        } catch (Exception e) {
            log.error("Query tree data failed", e);
            throw new DataQueryException("Query tree data failed: " + e.getMessage());
        }
    }

    private Query buildQuery(DataQueryRequest request) {
        Criteria criteria = new Criteria();

        if (StringUtils.hasText(request.getTaskId())) {
            criteria.and("taskId").is(request.getTaskId());
        }
        if (StringUtils.hasText(request.getType())) {
            criteria.and("type").is(request.getType());
        }
        if (request.getStartTime() != null) {
            criteria.and("collectTime").gte(request.getStartTime());
        }
        if (request.getEndTime() != null) {
            criteria.and("collectTime").lte(request.getEndTime());
        }

        return Query.query(criteria);
    }

    private TreeNode getTreeNode(String nodeId) {
        // 1. 尝试从缓存获取
        TreeNode node = cacheManager.get("tree:node:" + nodeId, TreeNode.class);
        if (node != null) {
            return node;
        }

        // 2. 从数据库查询
        node = treeRepository.findById(nodeId).orElse(null);
        if (node != null) {
            cacheManager.set("tree:node:" + nodeId, node, 1800); // 30分钟缓存
        }

        return node;
    }

    private void buildChildrenTree(TreeNode parent) {
        // 1. 查询直接子节点
        List<TreeNode> children = treeRepository.findByParentId(parent.getId());
        if (CollectionUtils.isEmpty(children)) {
            return;
        }

        // 2. 递归处理每个子节点
        children.forEach(child -> {
            child.setLevel(parent.getLevel() + 1);
            buildChildrenTree(child);
        });

        // 3. 设置子节点列表
        parent.setChildren(children);
        parent.setLeaf(false);
    }
}
