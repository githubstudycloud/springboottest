package com.study.collect.business.testcase.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VersionInfo {
    private String version;
    private String name;
    private String type;  // TRUNK/BRANCH
    private String description;
    private LocalDateTime updateTime;
    private Integer sort;
    private String status;
}