package com.study.collect.business.testcase.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PageResult<T> {
    private long total;       // 总记录数
    private int page;         // 当前页码
    private int size;         // 每页大小
    private int totalPages;   // 总页数
    private List<T> items;    // 当前页数据
}