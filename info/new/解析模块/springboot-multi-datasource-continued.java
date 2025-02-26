// 数据源切换工具类
package com.example.multidatasource.util;

import com.example.multidatasource.config.DynamicDataSource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DataSourceSwitchUtil implements ApplicationContextAware {
    
    private static ApplicationContext applicationContext;
    
    // 缓存数据源别名
    private static final Map<String, String> DATASOURCE_ALIASES = new HashMap<>();
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        DataSourceSwitchUtil.applicationContext = applicationContext;
        
        // 初始化时从配置中获取业务数据库别名列表
        Environment env = applicationContext.getEnvironment();
        String prefix = env.getProperty("datasource.business.prefix");
        
        // 这里应该获取所有配置的业务数据库并加载到缓存中
        // 简化实现，实际应该遍历配置文件中所有以datasource.business.databases开头的配置项
        DATASOURCE_ALIASES.put("db1", "db1");
        DATASOURCE_ALIASES.put("db2", "db2");
        DATASOURCE_ALIASES.put("db3", "db3");
        DATASOURCE_ALIASES.put("db4", "db4");
        DATASOURCE_ALIASES.put("db5", "db5");
        DATASOURCE_ALIASES.put("db6", "db6");
        DATASOURCE_ALIASES.put("db7", "db7");
        DATASOURCE_ALIASES.put("db8", "db8");
        DATASOURCE_ALIASES.put("db9", "db9");
        DATASOURCE_ALIASES.put("db10", "db10");
    }
    
    /**
     * 切换到指定别名的数据源
     * @param alias 数据源别名
     * @return 是否切换成功
     */
    public static boolean switchTo(String alias) {
        if (DATASOURCE_ALIASES.containsKey(alias)) {
            DynamicDataSource.setDataSource(alias);
            return true;
        }
        return false;
    }
    
    /**
     * 恢复到默认数据源
     */
    public static void resetDataSource() {
        DynamicDataSource.clearDataSource();
    }
    
    /**
     * 获取所有可用的数据源别名
     * @return 数据源别名集合
     */
    public static Map<String, String> getAvailableDataSources() {
        return new HashMap<>(DATASOURCE_ALIASES);
    }
}

// 12. 实体类
// User.java
package com.example.multidatasource.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class User implements Serializable {
    private Long id;
    private String username;
    private String email;
    private Date createTime;
    private String dbSource;  // 记录数据来源
}

// DataCollection.java
package com.example.multidatasource.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class DataCollection implements Serializable {
    private Long id;
    private String dataType;
    private String dataContent;
    private Date collectTime;
}

// Config.java
package com.example.multidatasource.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class Config implements Serializable {
    private Long id;
    private String configKey;
    private String configValue;
    private String description;
}

// 13. DAO接口
// UserDao.java
package com.example.multidatasource.dao.business;

import com.example.multidatasource.entity.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDao {
    List<User> findAll();
    User findById(@Param("id") Long id);
    int insert(User user);
}

// DataCollectionDao.java
package com.example.multidatasource.dao.collection;

import com.example.multidatasource.entity.DataCollection;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataCollectionDao {
    List<DataCollection> findAll();
    DataCollection findById(@Param("id") Long id);
    int insert(DataCollection dataCollection);
}

// ConfigDao.java
package com.example.multidatasource.dao.pub;

import com.example.multidatasource.entity.Config;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConfigDao {
    List<Config> findAll();
    Config findByKey(@Param("configKey") String configKey);
    int insert(Config config);
}

// 14. MyBatis XML映射文件
// UserMapper.xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.example.multidatasource.dao.business.UserDao">
    <resultMap id="userMap" type="com.example.multidatasource.entity.User">
        <id column="id" property="id"/>
        <result column="username" property="username"/>
        <result column="email" property="email"/>
        <result column="create_time" property="createTime"/>
        <result column="db_source" property="dbSource"/>
    </resultMap>
    
    <select id="findAll" resultMap="userMap">
        SELECT * FROM tb_user
    </select>
    
    <select id="findById" resultMap="userMap">
        SELECT * FROM tb_user WHERE id = #{id}
    </select>
    
    <insert id="insert" parameterType="com.example.multidatasource.entity.User" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO tb_user(username, email, create_time, db_source)
        VALUES(#{username}, #{email}, #{createTime}, #{dbSource})
    </insert>
</mapper>

// DataCollectionMapper.xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.example.multidatasource.dao.collection.DataCollectionDao">
    <resultMap id="dataCollectionMap" type="com.example.multidatasource.entity.DataCollection">
        <id column="id" property="id"/>
        <result column="data_type" property="dataType"/>
        <result column="data_content" property="dataContent"/>
        <result column="collect_time" property="collectTime"/>
    </resultMap>
    
    <select id="findAll" resultMap="dataCollectionMap">
        SELECT * FROM tb_data_collection
    </select>
    
    <select id="findById" resultMap="dataCollectionMap">
        SELECT * FROM tb_data_collection WHERE id = #{id}
    </select>
    
    <insert id="insert" parameterType="com.example.multidatasource.entity.DataCollection" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO tb_data_collection(data_type, data_content, collect_time)
        VALUES(#{dataType}, #{dataContent}, #{collectTime})
    </insert>
</mapper>

// ConfigMapper.xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.example.multidatasource.dao.pub.ConfigDao">
    <resultMap id="configMap" type="com.example.multidatasource.entity.Config">
        <id column="id" property="id"/>
        <result column="config_key" property="configKey"/>
        <result column="config_value" property="configValue"/>
        <result column="description" property="description"/>
    </resultMap>
    
    <select id="findAll" resultMap="configMap">
        SELECT * FROM tb_config
    </select>
    
    <select id="findByKey" resultMap="configMap">
        SELECT * FROM tb_config WHERE config_key = #{configKey}
    </select>
    
    <insert id="insert" parameterType="com.example.multidatasource.entity.Config" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO tb_config(config_key, config_value, description)
        VALUES(#{configKey}, #{configValue}, #{description})
    </insert>
</mapper>

// 15. 业务服务类
package com.example.multidatasource.service;

import com.example.multidatasource.dao.business.UserDao;
import com.example.multidatasource.dao.collection.DataCollectionDao;
import com.example.multidatasource.dao.pub.ConfigDao;
import com.example.multidatasource.entity.Config;
import com.example.multidatasource.entity.DataCollection;
import com.example.multidatasource.entity.User;
import com.example.multidatasource.util.DataSourceSwitchUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class TestService {
    
    @Autowired
    private UserDao userDao;
    
    @Autowired
    private DataCollectionDao dataCollectionDao;
    
    @Autowired
    private ConfigDao configDao;
    
    /**
     * 获取所有用户数据（默认数据源）
     */
    public List<User> getAllUsers() {
        return userDao.findAll();
    }
    
    /**
     * 根据数据源别名获取用户数据
     */
    public List<User> getUsersByDbAlias(String dbAlias) {
        try {
            // 切换数据源
            if (DataSourceSwitchUtil.switchTo(dbAlias)) {
                return userDao.findAll();
            } else {
                throw new RuntimeException("数据源切换失败，别名不存在: " + dbAlias);
            }
        } finally {
            // 恢复默认数据源
            DataSourceSwitchUtil.resetDataSource();
        }
    }
    
    /**
     * 获取所有数据采集记录
     */
    public List<DataCollection> getAllDataCollections() {
        return dataCollectionDao.findAll();
    }
    
    /**
     * 获取所有配置信息
     */
    public List<Config> getAllConfigs() {
        return configDao.findAll();
    }
    
    /**
     * 添加用户到指定数据源
     */
    public User addUserToDb(User user, String dbAlias) {
        try {
            // 切换数据源
            if (DataSourceSwitchUtil.switchTo(dbAlias)) {
                user.setCreateTime(new Date());
                user.setDbSource(dbAlias);
                userDao.insert(user);
                return user;
            } else {
                throw new RuntimeException("数据源切换失败，别名不存在: " + dbAlias);
            }
        } finally {
            // 恢复默认数据源
            DataSourceSwitchUtil.resetDataSource();
        }
    }
    
    /**
     * 添加采集数据
     */
    public DataCollection addDataCollection(DataCollection dataCollection) {
        dataCollection.setCollectTime(new Date());
        dataCollectionDao.insert(dataCollection);
        return dataCollection;
    }
    
    /**
     * 添加配置信息
     */
    public Config addConfig(Config config) {
        configDao.insert(config);
        return config;
    }
    
    /**
     * 获取所有可用数据源
     */
    public Map<String, String> getAllDataSourceAliases() {
        return DataSourceSwitchUtil.getAvailableDataSources();
    }
}

// 16. 测试接口
package com.example.multidatasource.controller;

import com.example.multidatasource.entity.Config;
import com.example.multidatasource.entity.DataCollection;
import com.example.multidatasource.entity.User;
import com.example.multidatasource.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TestController {
    
    @Autowired
    private TestService testService;
    
    /**
     * 获取所有数据源别名
     */
    @GetMapping("/datasources")
    public Map<String, Object> getAllDataSources() {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "获取成功");
        result.put("data", testService.getAllDataSourceAliases());
        return result;
    }
    
    /**
     * 从默认业务库获取用户列表
     */
    @GetMapping("/users")
    public Map<String, Object> getUsers() {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "获取成功");
        result.put("data", testService.getAllUsers());
        return result;
    }
    
    /**
     * 从指定业务库获取用户列表
     */
    @GetMapping("/users/{dbAlias}")
    public Map<String, Object> getUsersByDbAlias(@PathVariable String dbAlias) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<User> users = testService.getUsersByDbAlias(dbAlias);
            result.put("code", 200);
            result.put("message", "获取成功");
            result.put("data", users);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    /**
     * 获取所有采集数据
     */
    @GetMapping("/collections")
    public Map<String, Object> getDataCollections() {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "获取成功");
        result.put("data", testService.getAllDataCollections());
        return result;
    }
    
    /**
     * 获取所有配置信息
     */
    @GetMapping("/configs")
    public Map<String, Object> getConfigs() {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "获取成功");
        result.put("data", testService.getAllConfigs());
        return result;
    }
    
    /**
     * 添加用户到指定数据源
     */
    @PostMapping("/users/{dbAlias}")
    public Map<String, Object> addUser(@RequestBody User user, @PathVariable String dbAlias) {
        Map<String, Object> result = new HashMap<>();
        try {
            User savedUser = testService.addUserToDb(user, dbAlias);
            result.put("code", 200);
            result.put("message", "添加成功");
            result.put("data", savedUser);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    /**
     * 添加采集数据
     */
    @PostMapping("/collections")
    public Map<String, Object> addDataCollection(@RequestBody DataCollection dataCollection) {
        Map<String, Object> result = new HashMap<>();
        try {
            DataCollection savedData = testService.addDataCollection(dataCollection);
            result.put("code", 200);
            result.put("message", "添加成功");
            result.put("data", savedData);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }
        return result;
    }
    
    /**
     * 添加配置信息
     */
    @PostMapping("/configs")
    public Map<String, Object> addConfig(@RequestBody Config config) {
        Map<String, Object> result = new HashMap<>();
        try {
            Config savedConfig = testService.addConfig(config);
            result.put("code", 200);
            result.put("message", "添加成功");
            result.put("data", savedConfig);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", e.getMessage());
        }
        return result;
    }
}

// 17. 初始化SQL (测试表)
-- 创建公共库
CREATE DATABASE IF NOT EXISTS public_db;
USE public_db;

-- 配置表
CREATE TABLE IF NOT EXISTS tb_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    config_key VARCHAR(50) NOT NULL UNIQUE,
    config_value VARCHAR(500) NOT NULL,
    description VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 插入测试数据
INSERT INTO tb_config(config_key, config_value, description) VALUES
('system.name', 'Multi DataSource System', '系统名称'),
('db.version', '1.0.0', '数据库版本');

-- 创建采集库
CREATE DATABASE IF NOT EXISTS collection_db;
USE collection_db;

-- 数据采集表
CREATE TABLE IF NOT EXISTS tb_data_collection (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    data_type VARCHAR(50) NOT NULL,
    data_content TEXT NOT NULL,
    collect_time DATETIME NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 插入测试数据
INSERT INTO tb_data_collection(data_type, data_content, collect_time) VALUES
('LOG', '系统启动', NOW()),
('SENSOR', '温度传感器数据：25.6℃', NOW());

-- 创建业务库1-10
-- 业务库1
CREATE DATABASE IF NOT EXISTS biz_db1;
USE biz_db1;

-- 用户表
CREATE TABLE IF NOT EXISTS tb_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    create_time DATETIME NOT NULL,
    db_source VARCHAR(20)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 插入测试数据
INSERT INTO tb_user(username, email, create_time, db_source) VALUES
('user1', 'user1@example.com', NOW(), 'db1'),
('user2', 'user2@example.com', NOW(), 'db1');

-- 业务库2
CREATE DATABASE IF NOT EXISTS biz_db2;
USE biz_db2;

-- 创建相同结构的用户表
CREATE TABLE IF NOT EXISTS tb_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    create_time DATETIME NOT NULL,
    db_source VARCHAR(20)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 插入测试数据
INSERT INTO tb_user(username, email, create_time, db_source) VALUES
('userA', 'userA@example.com', NOW(), 'db2'),
('userB', 'userB@example.com', NOW(), 'db2');

-- 业务库3
CREATE DATABASE IF NOT EXISTS biz_db3;
USE biz_db3;

-- 创建相同结构的用户表
CREATE TABLE IF NOT EXISTS tb_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    create_time DATETIME NOT NULL,
    db_source VARCHAR(20)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 插入测试数据
INSERT INTO tb_user(username, email, create_time, db_source) VALUES
('userX', 'userX@example.com', NOW(), 'db3'),
('userY', 'userY@example.com', NOW(), 'db3');

-- 以此类推创建业务库4-10...
