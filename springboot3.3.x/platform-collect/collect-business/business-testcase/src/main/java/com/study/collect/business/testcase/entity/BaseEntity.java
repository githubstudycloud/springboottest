package com.study.collect.business.testcase.entity;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 基础实体类
 */
@Data
@NoArgsConstructor
public abstract class BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    protected String id;

    @CreatedDate
    @Field("create_time")
    protected LocalDateTime createTime;

    @LastModifiedDate
    @Field("update_time")
    protected LocalDateTime updateTime;

    @CreatedBy
    @Field("create_by")
    protected String createBy;

    @LastModifiedBy
    @Field("update_by")
    protected String updateBy;

    @Version
    protected Long version;

    @Field("is_deleted")
    protected Boolean deleted = false;

    /**
     * 构造函数
     */
    protected BaseEntity(String id) {
        this.id = id;
        this.createTime = LocalDateTime.now();
        this.updateTime = this.createTime;
        this.version = 0L;
        this.deleted = false;
    }

    /**
     * 创建前处理
     */
    @PrePersist
    public void prePersist() {
        if (this.createTime == null) {
            this.createTime = LocalDateTime.now();
        }
        if (this.updateTime == null) {
            this.updateTime = this.createTime;
        }
        if (this.version == null) {
            this.version = 0L;
        }
        if (this.deleted == null) {
            this.deleted = false;
        }
    }

    /**
     * 更新前处理
     */
    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 重置实体状态
     */
    public void reset() {
        this.id = null;
        this.createTime = null;
        this.updateTime = null;
        this.createBy = null;
        this.updateBy = null;
        this.version = 0L;
        this.deleted = false;
    }
}