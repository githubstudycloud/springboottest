package com.study.collect.business.testcase.aspect.mongodb;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class CollectionStrategy implements ApplicationContextAware {
    private static final ThreadLocal<String> versionHolder = new ThreadLocal<>();
    private static ApplicationContext applicationContext;

    public static void setVersion(String version) {
        versionHolder.set(version);
    }

    public static void clearVersion() {
        versionHolder.remove();
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        applicationContext = context;
    }

    public String getCollectionName(String baseCollection) {
        String version = versionHolder.get();
        return version != null ? baseCollection + "_" + version : baseCollection;
    }
}