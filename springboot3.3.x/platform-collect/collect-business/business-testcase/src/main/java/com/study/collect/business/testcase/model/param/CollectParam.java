package com.study.collect.business.testcase.model.param;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CollectParam {
    private String rootNode;
    private String version;
    private Boolean incremental = false;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
