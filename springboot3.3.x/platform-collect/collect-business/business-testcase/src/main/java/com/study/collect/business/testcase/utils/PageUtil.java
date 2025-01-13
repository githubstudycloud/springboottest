package com.study.collect.business.testcase.utils;


import com.study.collect.business.testcase.param.PageParam;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class PageUtil {
    public static Pageable toPageable(PageParam param) {
        return PageRequest.of(param.getPage() - 1, param.getSize());
    }
}