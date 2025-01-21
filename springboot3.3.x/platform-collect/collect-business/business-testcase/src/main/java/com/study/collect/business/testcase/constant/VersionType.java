package com.study.collect.business.testcase.constant;

/**
 * 版本类型枚举
 */
public enum VersionType {
    TRUNK("主干版本"),
    BRANCH("分支版本");

    private final String description;

    VersionType(String description) {
        this.description = description;
    }

    public static VersionType fromString(String version) {
        return version != null && version.toLowerCase().contains("branch") ?
                BRANCH : TRUNK;
    }

    public String getDescription() {
        return description;
    }
}