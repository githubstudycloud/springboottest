package com.study.collect.domain.entity.task;

// 任务统计

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * 任务统计实体
 */
@Data
@Document(collection = "task_stats")
public class TaskStats {
    @Id
    private String id;

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 总数量
     */
    private Long totalCount;

    /**
     * 成功数量
     */
    private Long successCount;

    /**
     * 失败数量
     */
    private Long failCount;

    /**
     * 处理速度(条/秒)
     */
    private Double processSpeed;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
