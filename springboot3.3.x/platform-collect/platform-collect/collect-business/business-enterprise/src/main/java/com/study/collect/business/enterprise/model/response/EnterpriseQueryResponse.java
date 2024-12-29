package com.study.collect.business.enterprise.model.response;

import com.study.collect.business.enterprise.model.Enterprise;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

@Data
public class EnterpriseQueryResponse {
    private List<Enterprise> list;      // 数据列表
    private long total;                 // 总数量
    private int pages;                  // 总页数
    private int pageNum;                // 当前页
    private int pageSize;               // 每页大小

    private Map<String, Object> summary;  // 汇总信息

    // 构造方法
    public static EnterpriseQueryResponse of(Page<Enterprise> page) {
        EnterpriseQueryResponse response = new EnterpriseQueryResponse();
        response.setList(page.getContent());
        response.setTotal(page.getTotalElements());
        response.setPages(page.getTotalPages());
        response.setPageNum(page.getNumber() + 1);
        response.setPageSize(page.getSize());
        return response;
    }
}