package com.study.collect.business.testcase.model.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EnterpriseGenerateRequest {
    @NotNull(message = "起始编码不能为空")
    private Integer startCode;

    @NotNull(message = "生成数量不能为空")
    @Max(value = 1000, message = "单次生成数量不能超过1000")
    private Integer count;

    private String industry;
    private String regAuthority;
}