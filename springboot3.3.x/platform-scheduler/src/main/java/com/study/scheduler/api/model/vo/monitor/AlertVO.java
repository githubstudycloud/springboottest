package com.study.scheduler.api.model.vo.monitor;

import lombok.Data;

@Data
public class AlertVO {
    private String alertLevel;
    private String message;
}
