package com.study.collect.business.testcase.model.request;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class EnterpriseQueryRequest {
    private String code;           // 企业编码
    private String name;           // 企业名称
    private String industry;       // 行业
    private String regAuthority;   // 注册机构

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate estDateStart;  // 成立日期开始

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate estDateEnd;    // 成立日期结束

    private String version;         // 数据版本

    private Integer pageNum = 1;    // 页码
    private Integer pageSize = 10;  // 每页大小
}