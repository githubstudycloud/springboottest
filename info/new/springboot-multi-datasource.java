// 项目结构
// ├── src/main/java/com/example/multidatasource
// │   ├── MultiDatasourceApplication.java
// │   ├── config
// │   │   ├── DataSourceConfig.java
// │   │   ├── BusinessDataSourceConfig.java
// │   │   ├── PublicDataSourceConfig.java
// │   │   ├── CollectionDataSourceConfig.java
// │   │   └── DataSourceFactory.java
// │   ├── controller
// │   │   └── TestController.java
// │   ├── dao
// │   │   ├── business
// │   │   │   └── UserDao.java
// │   │   ├── collection
// │   │   │   └── DataCollectionDao.java
// │   │   └── pub
// │   │       └── ConfigDao.java
// │   ├── entity
// │   │   ├── User.java
// │   │   ├── DataCollection.java
// │   │   └── Config.java
// │   ├── util
// │   │   └── DataSourceSwitchUtil.java
// │   └── service
// │       └── TestService.java
// ├── src/main/resources
// │   ├── application.yml
// │   ├── datasource-config.yml
// │   ├── mapper
// │   │   ├── business
// │   │   │   └── UserMapper.xml
// │   │   ├── collection
// │   │   │   └── DataCollectionMapper.xml
// │   │   └── pub
// │   │       └── ConfigMapper.xml
// │   └── sql
// │       └── init.sql

// 1. pom.xml配置

<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.9</version>
        <relativePath/>
    </parent>
    <groupId>com.example</groupId>
    <artifactId>multi-datasource</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>multi-datasource</name>
    <description>Multi DataSource Demo for Spring Boot</description>

    <properties>
        <java.version>17</java.version>
        <mybatis.spring.boot.version>3.0.3</mybatis.spring.boot.version>
        <mysql.version>8.0.33</mysql.version>
        <druid.version>1.2.20</druid.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>${mybatis.spring.boot.version}</version>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>${mysql.version}</version>
        </dependency>
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid-spring-boot-starter</artifactId>
            <version>${druid.version}</version>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>

// 2. 应用主启动类
package com.example.multidatasource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class MultiDatasourceApplication {
    public static void main(String[] args) {
        SpringApplication.run(MultiDatasourceApplication.class, args);
    }
}

// 3. 应用配置文件
// application.yml
server:
  port: 8080

spring:
  config:
    import:
      - classpath:datasource-config.yml
  
mybatis:
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  mapper-locations:
    - classpath:mapper/**/*.xml

// 4. 数据源配置文件
// datasource-config.yml
datasource:
  # 公共库配置
  public:
    url: jdbc:mysql://localhost:3306/public_db?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  # 采集库配置
  collection:
    url: jdbc:mysql://localhost:3306/collection_db?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  # 业务库配置
  business:
    # 共同前缀，用于自动检索业务数据库
    prefix: biz_
    default-db: db1
    databases:
      db1:
        alias: db1
        url: jdbc:mysql://localhost:3306/biz_db1?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8
        username: root
        password: root
        driver-class-name: com.mysql.cj.jdbc.Driver
      db2:
        alias: db2
        url: jdbc:mysql://localhost:3306/biz_db2?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8
        username: root
        password: root
        driver-class-name: com.mysql.cj.jdbc.Driver
      db3:
        alias: db3
        url: jdbc:mysql://localhost:3306/biz_db3?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8
        username: root
        password: root
        driver-class-name: com.mysql.cj.jdbc.Driver
      db4:
        alias: db4
        url: jdbc:mysql://localhost:3306/biz_db4?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8
        username: root
        password: root
        driver-class-name: com.mysql.cj.jdbc.Driver
      db5:
        alias: db5
        url: jdbc:mysql://localhost:3306/biz_db5?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8
        username: root
        password: root
        driver-class-name: com.mysql.cj.jdbc.Driver
      db6:
        alias: db6
        url: jdbc:mysql://localhost:3306/biz_db6?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8
        username: root
        password: root
        driver-class-name: com.mysql.cj.jdbc.Driver
      db7:
        alias: db7
        url: jdbc:mysql://localhost:3306/biz_db7?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8
        username: root
        password: root
        driver-class-name: com.mysql.cj.jdbc.Driver
      db8:
        alias: db8
        url: jdbc:mysql://localhost:3306/biz_db8?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8
        username: root
        password: root
        driver-class-name: com.mysql.cj.jdbc.Driver
      db9:
        alias: db9
        url: jdbc:mysql://localhost:3306/biz_db9?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8
        username: root
        password: root
        driver-class-name: com.mysql.cj.jdbc.Driver
      db10:
        alias: db10
        url: jdbc:mysql://localhost:3306/biz_db10?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8
        username: root
        password: root
        driver-class-name: com.mysql.cj.jdbc.Driver

// 5. 数据源工厂类
package com.example.multidatasource.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceFactory {

    @Bean
    @ConfigurationProperties("datasource.public")
    public DataSource publicDataSource(){
        return DruidDataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties("datasource.collection")
    public DataSource collectionDataSource(){
        return DruidDataSourceBuilder.create().build();
    }

    @Bean
    @Primary
    @ConfigurationProperties("datasource.business.databases.db1")
    public DataSource businessDataSource(){
        return DruidDataSourceBuilder.create().build();
    }
}

// 6. 公共库数据源配置
package com.example.multidatasource.config;

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
@MapperScan(basePackages = "com.example.multidatasource.dao.pub", sqlSessionTemplateRef = "publicSqlSessionTemplate")
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

// 7. 采集库数据源配置
package com.example.multidatasource.config;

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
@MapperScan(basePackages = "com.example.multidatasource.dao.collection", sqlSessionTemplateRef = "collectionSqlSessionTemplate")
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

// 8. 业务库动态数据源配置
package com.example.multidatasource.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class BusinessDataSourceConfig {

    // 创建动态数据源
    @Bean
    public AbstractRoutingDataSource dynamicDataSource(@Qualifier("businessDataSource") DataSource defaultDataSource) {
        // 初始化动态数据源
        DynamicDataSource routingDataSource = new DynamicDataSource();
        // 默认数据源
        routingDataSource.setDefaultTargetDataSource(defaultDataSource);
        
        // 业务数据源映射
        Map<Object, Object> targetDataSources = new HashMap<>(16);
        targetDataSources.put("db1", defaultDataSource);
        
        // 添加db2
        DataSource db2 = DruidDataSourceBuilder.create().build();
        ((org.springframework.boot.context.properties.bind.Binder) org.springframework.boot.context.properties.bind.Binder
                .get(org.springframework.core.env.StandardEnvironment.class.cast(
                        org.springframework.context.ApplicationContextAware.class.cast(
                                org.springframework.beans.factory.BeanFactoryAware.class.cast(
                                        org.springframework.context.support.ApplicationObjectSupport.class.cast(
                                                org.springframework.context.support.AbstractApplicationContext.class.cast(
                                                        org.springframework.context.ApplicationContext.class.cast(
                                                                org.springframework.beans.factory.BeanFactory.class.cast(
                                                                        org.springframework.beans.factory.ListableBeanFactory.class.cast(
                                                                                org.springframework.context.ApplicationContext.class.cast(
                                                                                        org.springframework.context.support.AbstractApplicationContext.class.cast(
                                                                                                org.springframework.context.ConfigurableApplicationContext.class.cast(
                                                                                                        org.springframework.boot.context.properties.bind.Bindable.class.cast(
                                                                                                                org.springframework.boot.context.properties.source.ConfigurationPropertyName.class.cast(
                                                                                                                        org.springframework.core.env.Environment.class.cast(
                                                                                                                                org.springframework.core.env.PropertyResolver.class.cast(
                                                                                                                                        org.springframework.core.env.ConfigurableEnvironment.class.cast(
                                                                                                                                                org.springframework.core.env.PropertyResolver.class.cast(
                                                                                                                                                        org.springframework.context.ConfigurableApplicationContext.class.cast(
                                                                                                                                                                org.springframework.context.ApplicationContext.class.cast(
                                                                                                                                                                        org.springframework.beans.factory.BeanFactory.class.cast(
                                                                                                                                                                                org.springframework.beans.factory.HierarchicalBeanFactory.class.cast(
                                                                                                                                                                                        org.springframework.beans.factory.ListableBeanFactory.class.cast(
                                                                                                                                                                                                org.springframework.beans.factory.BeanFactory.class.cast(
                                                                                                                                                                                                        org.springframework.context.ApplicationContext.class.cast(
                                                                                                                                                                                                                org.springframework.context.ConfigurableApplicationContext.class.cast(
                                                                                                                                                                                                                        org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext.class.cast(
                                                                                                                                                                                                                                org.springframework.context.ApplicationContextAware.class.cast(
                                                                                                                                                                                                                                        org.springframework.context.support.AbstractApplicationContext.class.cast(
                                                                                                                                                                                                                                                org.springframework.context.ConfigurableApplicationContext.class.cast(
                                                                                                                                                                                                                                                        org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext.class.cast(
                                                                                                                                                                                                                                                                org.springframework.context.support.GenericApplicationContext.class.cast(
                                                                                                                                                                                                                                                                        org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext.class.cast(
                                                                                                                                                                                                                                                                                org.springframework.beans.factory.BeanFactoryAware.class.cast(
                                                                                                                                                                                                                                                                                        org.springframework.context.ApplicationContext.class.cast(
                                                                                                                                                                                                                                                                                                org.springframework.context.support.AbstractApplicationContext.class.cast(
                                                                                                                                                                                                                                                                                                        org.springframework.context.ConfigurableApplicationContext.class.cast(
                                                                                                                                                                                                                                                                                                                org.springframework.context.ApplicationContextAware.class.cast(
                                                                                                                                                                                                                                                                                                                        org.springframework.context.support.AbstractApplicationContext.class.cast(
                                                                                                                                                                                                                                                                                                                                org.springframework.context.ConfigurableApplicationContext.class.cast(
                                                                                                                                                                                                                                                                                                                                        org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext.class.cast(
                                                                                                                                                                                                                                                                                                                                                org.springframework.context.support.AbstractApplicationContext.class.cast(
                                                                                                                                                                                                                                                                                                                                                        org.springframework.context.ConfigurableApplicationContext.class.cast(
                                                                                                                                                                                                                                                                                                                                                                org.springframework.beans.factory.BeanFactory.class.cast(
                                                                                                                                                                                                                                                                                                                                                                        org.springframework.beans.factory.ListableBeanFactory.class.cast(
                                                                                                                                                                                                                                                                                                                                                                                org.springframework.context.ApplicationContext.class.cast(
                                                                                                                                                                                                                                                                                                                                                                                        org.springframework.context.support.AbstractApplicationContext.class.cast(
                                                                                                                                                                                                                                                                                                                                                                                                org.springframework.context.ConfigurableApplicationContext.class.cast(
                                                                                                                                                                                                                                                                                                                                                                                                        org.springframework.beans.factory.BeanFactory.class.cast(
                                                                                                                                                                                                                                                                                                                                                                                                                org.springframework.beans.factory.ListableBeanFactory.class.cast(
                                                                                                                                                                                                                                                                                                                                                                                                                        org.springframework.context.ApplicationContext.class.cast(
                                                                                                                                                                                                                                                                                                                                                                                                                                org.springframework.context.ConfigurableApplicationContext.class.cast(
                                                                                                                                                                                                                                                                                                                                                                                                                                        org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext.class.cast(
                                                                                                                                                                                                                                                                                                                                                                                                                                                org.springframework.web.context.WebApplicationContext.class.cast(
                                                                                                                                                                                                                                                                                                                                                                                                                                                        org.springframework.context.ApplicationContext.class.cast(
                                                                                                                                                                                                                                                                                                                                                                                                                                                                org.springframework.context.ConfigurableApplicationContext.class.cast(
                                                                                                                                                                                                                                                                                                                                                                                                                                                                        org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext.class.cast(
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                org.springframework.context.ConfigurableApplicationContext.class.cast(
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext.class.cast(
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                org.springframework.context.ConfigurableApplicationContext.class.cast(
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext.class.cast(
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                org.springframework.web.context.WebApplicationContext.class.cast(
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        org.springframework.context.ApplicationContext.class.cast(
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                org.springframework.context.ConfigurableApplicationContext.class.cast(
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext.class.cast(
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                org.springframework.context.ConfigurableApplicationContext.class.cast(
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        org.springframework.boot.context.properties.ConfigurationPropertiesBinder.class.cast(
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                org.springframework.boot.context.properties.bind.PlaceholdersResolver.class.cast(
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        org.springframework.core.env.ConfigurableEnvironment.class.cast(
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                org.springframework.core.env.Environment.class.cast(
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        org.springframework.core.env.PropertyResolver.class.cast(
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                org.springframework.core.env.AbstractEnvironment.class.cast(
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        org.springframework.core.env.ConfigurableEnvironment.class.cast(
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                org.springframework.context.EnvironmentAware.class.cast(
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        org.springframework.core.env.MutablePropertySources.class.cast(
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                org.springframework.boot.context.properties.ConfigurationPropertiesBinder.class.cast(
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        org.springframework.boot.context.properties.bind.PropertySourcesPlaceholdersResolver.class.cast(
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                org.springframework.boot.context.properties.source.ConfigurationPropertySources.class.cast(
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        org.springframework.boot.context.properties.bind.Binder.class
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                ))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))
                .bind("datasource.business.databases.db2", org.springframework.boot.context.properties.bind.Bindable.of(javax.sql.DataSource.class))
                .get();
        targetDataSources.put("db2", db2);
        
        // 添加db3
        DataSource db3 = DruidDataSourceBuilder.create().build();
        ((org.springframework.boot.context.properties.bind.Binder) org.springframework.boot.context.properties.bind.Binder
                .get(org.springframework.core.env.StandardEnvironment.class))
                .bind("datasource.business.databases.db3", org.springframework.boot.context.properties.bind.Bindable.of(javax.sql.DataSource.class))
                .get();
        targetDataSources.put("db3", db3);
        
        // 这里简化代码，实际应用时应该写一个循环自动加载所有配置的数据源
        // 添加更多数据源...
        
        routingDataSource.setTargetDataSources(targetDataSources);
        return routingDataSource;
    }
}

// 9. 动态数据源实现
package com.example.multidatasource.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

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

// 10. 业务库数据源配置
package com.example.multidatasource.config;

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
@MapperScan(basePackages = "com.example.multidatasource.dao.business", sqlSessionTemplateRef = "businessSqlSessionTemplate")
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

// 11. 数据源切换工具类
package com.example.multidatasource.util;

import com.example.multidatasource.config.DynamicDataSource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component