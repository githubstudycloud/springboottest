package com.study.collect.domain.entity.data;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * 采集数据实体
 */
@Data
@Document(collection = "collect_data")
public class CollectData {
    @Id
    private String id;

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 数据类型
     */
    private String type;

    /**
     * 源数据
     */
    private String rawData;

    /**
     * 处理后数据
     */
    private String processedData;

    /**
     * 数据版本
     */
    private Long version;

    /**
     * 采集时间
     */
    private LocalDateTime collectTime;

    /**
     * 处理时间
     */
    private LocalDateTime processTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}