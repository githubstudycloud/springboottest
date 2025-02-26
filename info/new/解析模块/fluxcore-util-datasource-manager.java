package com.platform.fluxcore.util;

import com.platform.fluxcore.config.DynamicDataSource;
import com.platform.fluxcore.exception.FluxCoreException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据源管理工具类
 */
@Slf4j
@Component
public class DataSourceManager implements ApplicationContextAware {
    
    private static ApplicationContext applicationContext;
    
    // 缓存数据源别名
    private static final Map<String, String> DATASOURCE_ALIASES = new ConcurrentHashMap<>();
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        DataSourceManager.applicationContext = applicationContext;
        
        // 初始化时从配置中获取业务数据库别名列表
        Environment env = applicationContext.getEnvironment();
        String prefix = env.getProperty("fluxcore.datasource.business.prefix", "biz_");
        
        // 通过配置获取所有的业务数据库并加载到缓存中
        String databasesPath = "fluxcore.datasource.business.databases";
        Set<String> databaseKeys = getPropertyKeys(env, databasesPath);
        
        for (String key : databaseKeys) {
            String alias = env.getProperty(databasesPath + "." + key + ".alias");
            if (StringUtils.hasText(alias)) {
                DATASOURCE_ALIASES.put(key, alias);
                log.debug("Loaded datasource alias: {} -> {}", key, alias);
            }
        }
        
        log.info("Initialized {} business datasources", DATASOURCE_ALIASES.size());
    }
    
    /**
     * 获取指定路径下的所有属性键
     */
    private Set<String> getPropertyKeys(Environment env, String path) {
        try {
            // 尝试获取配置项下的所有键
            Map<String, Object> properties = new HashMap<>();
            
            // 这里简化实现，实际中应该有更好的方法来遍历配置项
            String db1Path = path + ".db1";
            if (env.containsProperty(db1Path + ".url")) {
                properties.put("db1", null);
            }
            
            String db2Path = path + ".db2";
            if (env.containsProperty(db2Path + ".url")) {
                properties.put("db2", null);
            }
            
            String db3Path = path + ".db3";
            if (env.containsProperty(db3Path + ".url")) {
                properties.put("db3", null);
            }
            
            // 扫描db4-db10
            for (int i = 4; i <= 10; i++) {
                String dbPath = path + ".db" + i;
                if (env.containsProperty(dbPath + ".url")) {
                    properties.put("db" + i, null);
                }
            }
            
            return properties.keySet();
        } catch (Exception e) {
            log.error("Failed to get property keys for path: {}", path, e);
            return Collections.emptySet();
        }
    }
    
    /**
     * 切换到指定别名的数据源
     * @param alias 数据源别名
     * @return 是否切换成功
     */
    public static boolean switchTo(String alias) {
        if (DATASOURCE_ALIASES.containsKey(alias) || DATASOURCE_ALIASES.containsValue(alias)) {
            DynamicDataSource.setDataSource(alias);
            log.debug("Switched to datasource: {}", alias);
            return true;
        }
        log.warn("Failed to switch to datasource: {}, alias not found", alias);
        return false;
    }
    
    /**
     * 恢复到默认数据源
     */
    public static void resetDataSource() {
        DynamicDataSource.clearDataSource();
        log.debug("Reset to default datasource");
    }
    
    /**
     * 获取所有可用的数据源别名
     * @return 数据源别名集合
     */
    public static Map<String, String> getAvailableDataSources() {
        return new HashMap<>(DATASOURCE_ALIASES);
    }
    
    /**
     * 执行指定数据源的操作
     * @param alias 数据源别名
     * @param action 要执行的操作
     * @param <T> 返回值类型
     * @return 操作结果
     */
    public static <T> T executeWithDataSource(String alias, DataSourceAction<T> action) {
        try {
            if (!switchTo(alias)) {
                throw new FluxCoreException("Invalid datasource alias: " + alias);
            }
            return action.execute();
        } finally {
            resetDataSource();
        }
    }
    
    /**
     * 数据源操作接口
     * @param <T> 返回值类型
     */
    @FunctionalInterface
    public interface DataSourceAction<T> {
        /**
         * 执行数据源操作
         * @return 操作结果
         */
        T execute();
    }
    
    /**
     * 动态添加新的数据源
     * @param alias 数据源别名
     * @param url 数据源URL
     * @param username 用户名
     * @param password 密码
     * @return 是否添加成功
     */
    public static boolean addDataSource(String alias, String url, String username, String password) {
        // 此方法仅作为示例，实际实现需要更多逻辑
        // 通常需要创建新的DataSource并添加到dynamicDataSource中的targetDataSources
        DATASOURCE_ALIASES.put(alias, alias);
        log.info("Added new datasource: {}", alias);
        return true;
    }
}
