package com.study.collect.core.processor.impl.transform;

import com.study.collect.common.enums.collect.ProcessType;
import com.study.collect.common.exception.collect.ProcessException;
import com.study.collect.core.processor.ProcessContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

// 树形转换器
@Slf4j
@Component
public class TreeTransformer extends AbstractProcessor {

    @Override
    public ProcessType getType() {
        return ProcessType.TRANSFORM;
    }

    @Override
    public int getOrder() {
        return 100;
    }

    @Override
    protected void doProcess(ProcessContext context) {
        Object rawData = context.getRawData();
        if (!(rawData instanceof TreeNode)) {
            throw new ProcessException("Data is not a tree node");
        }

        try {
            TreeNode node = (TreeNode) rawData;

            // 转换树节点
            TreeNode transformed = transformNode(node);

            // 递归转换子节点
            if (transformed.getChildren() != null) {
                List<TreeNode> transformedChildren = transformed.getChildren().stream()
                        .map(this::transformNode)
                        .collect(Collectors.toList());
                transformed.setChildren(transformedChildren);
            }

            context.setResult(transformed);

        } catch (Exception e) {
            log.error("Transform tree data failed", e);
            throw new ProcessException("Transform tree data failed", e);
        }
    }

    /**
     * 转换树节点
     */
    protected TreeNode transformNode(TreeNode node) {
        // 默认实现,子类可覆盖
        return node;
    }
}