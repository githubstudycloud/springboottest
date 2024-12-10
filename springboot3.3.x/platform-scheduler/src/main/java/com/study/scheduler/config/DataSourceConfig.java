//package com.study.scheduler.config;
//
//import org.apache.commons.dbcp2.BasicDataSource;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//
//import javax.sql.DataSource;
//
//@Configuration
//public class DataSourceConfig {
//    private static final Logger logger = LoggerFactory.getLogger(DataSourceConfig.class);
//
//    @Bean
//    @Primary
//    public DataSource dataSource() {
//        logger.info("Initializing DBCP2 DataSource...");
//
//        BasicDataSource dataSource = new BasicDataSource();
//
//        // 基本连接配置
//        dataSource.setDriverClassName("org.mariadb.jdbc.Driver");
//        dataSource.setUrl("jdbc:mariadb://192.168.80.131:3306/test?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai");
//        dataSource.setUsername("root");
//        dataSource.setPassword("123456");
//
//        // 连接池配置
//        dataSource.setInitialSize(5);                    // 初始连接数
//        dataSource.setMaxTotal(20);                      // 最大连接数
//        dataSource.setMaxIdle(10);                       // 最大空闲连接数
//        dataSource.setMinIdle(5);                        // 最小空闲连接数
//        dataSource.setMaxWaitMillis(30000);             // 最大等待时间
//
//        // 连接属性配置
//        dataSource.setTestOnBorrow(true);               // 借用连接时测试
//        dataSource.setTestOnReturn(false);              // 归还连接时不测试
//        dataSource.setTestWhileIdle(true);              // 空闲时测试
//        dataSource.setValidationQuery("SELECT 1");      // 验证查询语句
//        dataSource.setValidationQueryTimeout(5);        // 验证查询超时时间（秒）
//
//        // 空闲连接回收器配置
//        dataSource.setTimeBetweenEvictionRunsMillis(30000);  // 空闲连接回收器线程运行间隔（毫秒）
//        dataSource.setMinEvictableIdleTimeMillis(1800000);   // 连接在池中最小生存时间（毫秒）
//
//        // 性能优化配置
//        dataSource.setPoolPreparedStatements(true);          // 开启PSCache
//        dataSource.setMaxOpenPreparedStatements(100);        // PSCache的大小
//
//        // 配置连接属性
//        dataSource.addConnectionProperty("useUnicode", "true");
//        dataSource.addConnectionProperty("characterEncoding", "UTF-8");
//        dataSource.addConnectionProperty("serverTimezone", "Asia/Shanghai");
//        dataSource.addConnectionProperty("rewriteBatchedStatements", "true");
//        dataSource.addConnectionProperty("useSSL", "false");
//
//        logger.info("DBCP2 DataSource Configuration:");
//        logger.info("URL: {}", dataSource.getUrl());
//        logger.info("Username: {}", dataSource.getUsername());
//        logger.info("Initial Size: {}", dataSource.getInitialSize());
//        logger.info("Max Total: {}", dataSource.getMaxTotal());
//        logger.info("Max Idle: {}", dataSource.getMaxIdle());
//        logger.info("Min Idle: {}", dataSource.getMinIdle());
//
//        return dataSource;
//    }
//}