package com.study.collect.model.request.collect;

// 对比请求

import com.study.collect.common.enums.data.CompareType;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * 数据对比请求
 */
@Data
@Builder
public class CompareRequest {

    @NotNull(message = "源数据ID不能为空")
    private String sourceId;

    @NotNull(message = "目标数据ID不能为空")
    private String targetId;

    private CompareType compareType;

    private Map<String, Object> params;
}

