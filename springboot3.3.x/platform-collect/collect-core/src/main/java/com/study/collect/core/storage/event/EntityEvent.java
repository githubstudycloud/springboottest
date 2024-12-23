package com.study.collect.core.storage.event;

import com.study.collect.core.storage.entity.BaseEntity;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 实体事件基类
 */
@Getter
public abstract class EntityEvent<T extends BaseEntity> extends ApplicationEvent {

    protected final T entity;

    public EntityEvent(T entity) {
        super(entity);
        this.entity = entity;
    }
}