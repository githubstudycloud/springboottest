package com.study.collect.model.request.collect;

// 采集请求

import lombok.AllArgsConstructor;

/**
 * 采集请求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollectRequest {

    @NotBlank(message = "任务名称不能为空")
    private String name;

    @NotBlank(message = "采集类型不能为空")
    private String type;

    @NotNull(message = "采集参数不能为空")
    private Map<String, Object> params;

    private Integer priority = 0;

    private Integer maxRetryTimes = 3;
}