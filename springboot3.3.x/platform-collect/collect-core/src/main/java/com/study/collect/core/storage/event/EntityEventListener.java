package com.study.collect.core.storage.event;

import com.study.collect.core.storage.entity.BaseEntity;
import com.study.collect.core.storage.entity.VersionEntity;
import com.study.collect.core.storage.event.impl.EntityEvents;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.data.mongodb.core.mapping.event.AfterConvertEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class EntityEventListener<T extends BaseEntity> extends AbstractMongoEventListener<T> {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public void onBeforeConvert(BeforeConvertEvent<T> event) {
        T entity = event.getSource();

        // 处理审计字段
        LocalDateTime now = LocalDateTime.now();
        if (entity.getCreateTime() == null) {
            entity.setCreateTime(now);
            entity.setUpdateTime(now);
            // 发布保存前事件
            eventPublisher.publishEvent(new EntityEvents.BeforeSaveEvent<>(entity));
        } else {
            entity.setUpdateTime(now);
            // 发布更新前事件
            eventPublisher.publishEvent(new EntityEvents.BeforeUpdateEvent<>(entity));
        }

        // 处理版本
        if (entity instanceof VersionEntity versionEntity) {
            String oldVersion = versionEntity.getVersionCode();
            if (oldVersion == null) {
                versionEntity.initVersion();
            } else {
                versionEntity.upgradeVersion();
                // 发布版本更新事件
                eventPublisher.publishEvent(new EntityEvents.VersionUpgradeEvent<>(
                        entity, oldVersion, versionEntity.getVersionCode()));
            }
        }
    }

    @Override
    public void onAfterConvert(AfterConvertEvent<T> event) {
        T entity = event.getSource();
        // 发布更新后事件
        eventPublisher.publishEvent(new EntityEvents.AfterUpdateEvent<>(entity));
    }
}