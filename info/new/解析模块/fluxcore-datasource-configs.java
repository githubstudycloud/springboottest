// DynamicDataSource.java
package com.platform.fluxcore.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 动态数据源实现
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
    
    // ThreadLocal保存当前线程的数据源key
    private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<>();
    
    @Override
    protected Object determineCurrentLookupKey() {
        return getDataSource();
    }
    
    /**
     * 设置数据源
     */
    public static void setDataSource(String dataSource) {
        CONTEXT_HOLDER.set(dataSource);
    }
    
    /**
     * 获取数据源
     */
    public static String getDataSource() {
        return CONTEXT_HOLDER.get();
    }
    
    /**
     * 清除数据源
     */
    public static void clearDataSource() {
        CONTEXT_HOLDER.remove();
    }
}

// DataSourceFactory.java
package com.platform.fluxcore.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceFactory {

    @Bean
    @ConfigurationProperties("fluxcore.datasource.public")
    public DataSource publicDataSource(){
        return DruidDataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties("fluxcore.datasource.collection")
    public DataSource collectionDataSource(){
        return DruidDataSourceBuilder.create().build();
    }

    @Bean
    @Primary
    @ConfigurationProperties("fluxcore.datasource.business.databases.db1")
    public DataSource businessDataSource(){
        return DruidDataSourceBuilder.create().build();
    }
}

// PublicDataSourceConfig.java
package com.platform.fluxcore.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = "com.platform.fluxcore.dao.pub", sqlSessionTemplateRef = "publicSqlSessionTemplate")
public class PublicDataSourceConfig {

    @Bean
    public SqlSessionFactory publicSqlSessionFactory(@Qualifier("publicDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/pub/*.xml"));
        return sqlSessionFactoryBean.getObject();
    }

    @Bean
    public SqlSessionTemplate publicSqlSessionTemplate(@Qualifier("publicSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}

// CollectionDataSourceConfig.java
package com.platform.fluxcore.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = "com.platform.fluxcore.dao.collection", sqlSessionTemplateRef = "collectionSqlSessionTemplate")
public class CollectionDataSourceConfig {

    @Bean
    public SqlSessionFactory collectionSqlSessionFactory(@Qualifier("collectionDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/collection/*.xml"));
        return sqlSessionFactoryBean.getObject();
    }

    @Bean
    public SqlSessionTemplate collectionSqlSessionTemplate(@Qualifier("collectionSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}

// BusinessDataSourceConfig.java
package com.platform.fluxcore.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = "com.platform.fluxcore.dao.business", sqlSessionTemplateRef = "businessSqlSessionTemplate")
public class BusinessDataSourceConfig {

    @Bean
    @Primary
    public SqlSessionFactory businessSqlSessionFactory(@Qualifier("dynamicDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/business/*.xml"));
        return sqlSessionFactoryBean.getObject();
    }

    @Bean
    @Primary
    public SqlSessionTemplate businessSqlSessionTemplate(@Qualifier("businessSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}

// DataSourceConfig.java
package com.platform.fluxcore.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Configuration
public class DataSourceConfig {
    
    @Autowired
    private Environment environment;

    // 创建动态数据源
    @Primary
    @Bean
    public AbstractRoutingDataSource dynamicDataSource(@Qualifier("businessDataSource") DataSource defaultDataSource) {
        // 初始化动态数据源
        DynamicDataSource routingDataSource = new DynamicDataSource();
        // 默认数据源
        routingDataSource.setDefaultTargetDataSource(defaultDataSource);
        
        // 获取默认数据库别名
        String defaultDbAlias = environment.getProperty("fluxcore.datasource.business.default-db", "db1");
        
        // 业务数据源映射
        Map<Object, Object> targetDataSources = new HashMap<>(16);
        targetDataSources.put(defaultDbAlias, defaultDataSource);
        
        // 动态加载所有配置的业务数据库
        String prefix = "fluxcore.datasource.business.databases";
        Binder binder = Binder.get(environment);
        
        // 遍历datasource.yml中配置的所有业务数据源
        try {
            // 获取业务数据库配置的所有键
            String[] keys = environment.getProperty(prefix, String[].class);
            
            if (keys != null) {
                for (String key : keys) {
                    if (!key.equals(defaultDbAlias)) { // 跳过默认数据库
                        String dbConfigPath = prefix + "." + key;
                        DataSource ds = DruidDataSourceBuilder.create().build();
                        binder.bind(dbConfigPath, DataSource.class).ifBound(bound -> {
                            try {
                                ds.getConnection(); // 测试连接
                                targetDataSources.put(key, ds);
                            } catch (Exception e) {
                                // 日志记录连接失败
                            }
                        });
                    }
                }
            }
        } catch (Exception e) {
            // 处理异常
        }
        
        routingDataSource.setTargetDataSources(targetDataSources);
        routingDataSource.afterPropertiesSet();
        return routingDataSource;
    }
}
