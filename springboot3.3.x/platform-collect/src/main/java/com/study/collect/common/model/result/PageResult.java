package com.study.collect.common.model.result;

// 分页结果

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;

/**
 * 分页响应结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {
    /**
     * 当前页码
     */
    private Integer pageNum;

    /**
     * 每页大小
     */
    private Integer pageSize;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 总页数
     */
    private Integer pages;

    /**
     * 数据列表
     */
    private List<T> list;

    public static <T> PageResult<T> empty() {
        return PageResult.<T>builder()
                .pageNum(1)
                .pageSize(10)
                .total(0L)
                .pages(0)
                .list(Collections.emptyList())
                .build();
    }
}

