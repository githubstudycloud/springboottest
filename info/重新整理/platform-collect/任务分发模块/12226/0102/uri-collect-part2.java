// core/config/MongoConfig.java
package com.study.collect.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.study.collect.repository")
public class MongoConfig extends AbstractMongoClientConfiguration {
    @Override
    protected String getDatabaseName() {
        return "uri_collect";
    }
    
    @Override
    protected void configureClientSettings(Builder builder) {
        builder.applyToClusterSettings(settings -> 
            settings.applyConnectionString(new ConnectionString("mongodb://localhost:27017")));
    }
}

// core/config/ThreadPoolConfig.java
package com.study.collect.core.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.*;

@Configuration
public class ThreadPoolConfig {
    @Bean(name = "collectExecutor")
    public ExecutorService collectExecutor() {
        return new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors() * 2,
            Runtime.getRuntime().availableProcessors() * 4,
            60L,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(5000),
            new ThreadFactoryBuilder()
                .setNameFormat("uri-collect-pool-%d")
                .setDaemon(true)
                .build(),
            new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }
}

// core/config/ObjectPoolConfig.java
package com.study.collect.core.config;

import com.study.collect.domain.entity.UriEntity;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.ObjectPool;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObjectPoolConfig {
    @Bean
    public ObjectPool<UriEntity> uriEntityPool() {
        return new GenericObjectPool<>(new BasePooledObjectFactory<>() {
            @Override
            public UriEntity create() {
                return new UriEntity();
            }

            @Override
            public PooledObject<UriEntity> wrap(UriEntity entity) {
                return new DefaultPooledObject<>(entity);
            }
            
            @Override
            public void passivateObject(PooledObject<UriEntity> p) {
                UriEntity entity = p.getObject();
                entity.setUri(null);
                entity.setUriHash(null);
                entity.setRootNode(null);
                entity.setVersionType(null);
                entity.setUriVersion(null);
                entity.setDetails(null);
                entity.setVersion(0L);
            }
        });
    }
}

// core/constant/VersionType.java
package com.study.collect.core.constant;

public enum VersionType {
    TRUNK,
    BRANCH
}

// core/util/HashUtil.java
package com.study.collect.core.util;

import org.apache.commons.codec.digest.DigestUtils;

public class HashUtil {
    public static String hash(String input) {
        return DigestUtils.sha256Hex(input);
    }
}

// core/util/PageUtil.java
package com.study.collect.core.util;

import com.study.collect.domain.param.PageParam;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class PageUtil {
    public static Pageable toPageable(PageParam param) {
        return PageRequest.of(param.getPage() - 1, param.getSize());
    }
}