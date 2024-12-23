package com.study.collect.core.storage.event.impl;

import com.study.collect.core.storage.entity.BaseEntity;
import com.study.collect.core.storage.event.EntityEvent;
import lombok.Getter;

public class EntityEvents {

    @Getter
    public static class BeforeSaveEvent<T extends BaseEntity> extends EntityEvent<T> {
        public BeforeSaveEvent(T entity) {
            super(entity);
        }
    }

    @Getter
    public static class AfterSaveEvent<T extends BaseEntity> extends EntityEvent<T> {
        public AfterSaveEvent(T entity) {
            super(entity);
        }
    }

    @Getter
    public static class BeforeUpdateEvent<T extends BaseEntity> extends EntityEvent<T> {
        public BeforeUpdateEvent(T entity) {
            super(entity);
        }
    }

    @Getter
    public static class AfterUpdateEvent<T extends BaseEntity> extends EntityEvent<T> {
        public AfterUpdateEvent(T entity) {
            super(entity);
        }
    }

    @Getter
    public static class BeforeDeleteEvent<T extends BaseEntity> extends EntityEvent<T> {
        public BeforeDeleteEvent(T entity) {
            super(entity);
        }
    }

    @Getter
    public static class AfterDeleteEvent<T extends BaseEntity> extends EntityEvent<T> {
        public AfterDeleteEvent(T entity) {
            super(entity);
        }
    }

    @Getter
    public static class VersionUpgradeEvent<T extends BaseEntity> extends EntityEvent<T> {
        private final String oldVersion;
        private final String newVersion;

        public VersionUpgradeEvent(T entity, String oldVersion, String newVersion) {
            super(entity);
            this.oldVersion = oldVersion;
            this.newVersion = newVersion;
        }
    }
}