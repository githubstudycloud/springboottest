// DataEntity.java
package com.platform.fluxcore.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 业务数据实体
 */
@Data
public class DataEntity implements Serializable {
    private Long id;
    private String dataCode;
    private String dataName;
    private String dataContent;
    private String dataFormat;
    private Date createTime;
    private Date updateTime;
    private String sourceDb;
}

// SourceData.java
package com.platform.fluxcore.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 采集数据实体
 */
@Data
public class SourceData implements Serializable {
    private Long id;
    private String sourceType;
    private String sourceContent;
    private String contentFormat;
    private Date collectTime;
    private String sourceLocation;
    private String status;
}

// SystemConfig.java
package com.platform.fluxcore.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 系统配置实体
 */
@Data
public class SystemConfig implements Serializable {
    private Long id;
    private String configKey;
    private String configValue;
    private String description;
    private Date createTime;
    private Date updateTime;
    private Integer status;
}
