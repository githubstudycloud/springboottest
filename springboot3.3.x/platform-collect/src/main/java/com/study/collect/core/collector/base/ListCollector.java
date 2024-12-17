package com.study.collect.core.collector.base;

// 列表采集基类

import com.study.collect.common.enums.collect.CollectType;
import com.study.collect.core.engine.CollectContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 列表采集器抽象基类
 */
@Slf4j
public abstract class ListCollector extends AbstractCollector {

    @Override
    public CollectType getType() {
        return CollectType.LIST;
    }

    @Override
    public boolean supports(CollectType type) {
        return CollectType.LIST.equals(type);
    }

    @Override
    protected Object doCollect(CollectContext context) {
        // 获取总数
        long total = getTotal(context);
        if (total <= 0) {
            return Collections.emptyList();
        }

        // 分页采集
        int pageSize = getPageSize(context);
        int pageCount = (int) Math.ceil((double) total / pageSize);

        List<Object> result = new ArrayList<>();
        for (int page = 1; page <= pageCount; page++) {
            List<Object> pageData = collectPage(page, pageSize, context);
            if (CollectionUtils.isEmpty(pageData)) {
                break;
            }
            result.addAll(pageData);
        }

        return result;
    }

    /**
     * 获取数据总数
     */
    protected abstract long getTotal(CollectContext context);

    /**
     * 获取分页大小
     */
    protected int getPageSize(CollectContext context) {
        Object pageSizeObj = context.getParams().get("pageSize");
        return pageSizeObj != null ? Integer.parseInt(pageSizeObj.toString()) : 100;
    }

    /**
     * 采集分页数据
     */
    protected abstract List<Object> collectPage(int page, int pageSize, CollectContext context);
}
