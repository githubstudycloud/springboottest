// DataSourceService.java
package com.platform.fluxcore.service;

import com.platform.fluxcore.entity.SystemConfig;
import com.platform.fluxcore.exception.FluxCoreException;
import com.platform.fluxcore.dao.pub.SystemConfigDao;
import com.platform.fluxcore.util.DataSourceManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据源服务
 */
@Slf4j
@Service
public class DataSourceService {
    
    @Autowired
    private SystemConfigDao systemConfigDao;
    
    /**
     * 获取所有可用数据源
     * @return 数据源别名映射
     */
    public Map<String, String> getAllDataSources() {
        return DataSourceManager.getAvailableDataSources();
    }
    
    /**
     * 切换到指定数据源
     * @param alias 数据源别名
     * @return 是否切换成功
     */
    public boolean switchDataSource(String alias) {
        return DataSourceManager.switchTo(alias);
    }
    
    /**
     * 恢复默认数据源
     */
    public void resetDataSource() {
        DataSourceManager.resetDataSource();
    }
    
    /**
     * 添加新的数据源配置
     * @param alias 数据源别名
     * @param url 数据库URL
     * @param username 用户名
     * @param password 密码
     * @return 是否添加成功
     */
    public boolean addDataSource(String alias, String url, String username, String password) {
        try {
            // 1. 添加到配置表
            SystemConfig urlConfig = new SystemConfig();
            urlConfig.setConfigKey("fluxcore.datasource.business.databases." + alias + ".url");
            urlConfig.setConfigValue(url);
            urlConfig.setDescription("数据源 " + alias + " URL配置");
            systemConfigDao.insert(urlConfig);
            
            SystemConfig usernameConfig = new SystemConfig();
            usernameConfig.setConfigKey("fluxcore.datasource.business.databases." + alias + ".username");
            usernameConfig.setConfigValue(username);
            usernameConfig.setDescription("数据源 " + alias + " 用户名配置");
            systemConfigDao.insert(usernameConfig);
            
            SystemConfig passwordConfig = new SystemConfig();
            passwordConfig.setConfigKey("fluxcore.datasource.business.databases." + alias + ".password");
            passwordConfig.setConfigValue(password);
            passwordConfig.setDescription("数据源 " + alias + " 密码配置");
            systemConfigDao.insert(passwordConfig);
            
            // 2. 动态注册数据源
            boolean result = DataSourceManager.addDataSource(alias, url, username, password);
            
            if (result) {
                log.info("Successfully added datasource: {}", alias);
            } else {
                log.warn("Failed to add datasource: {}", alias);
            }
            
            return result;
        } catch (Exception e) {
            log.error("Error adding datasource: {}", alias, e);
            throw new FluxCoreException("添加数据源失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取数据源配置
     * @param alias 数据源别名
     * @return 数据源配置
     */
    public Map<String, String> getDataSourceConfig(String alias) {
        Map<String, String> config = new HashMap<>();
        
        try {
            String prefix = "fluxcore.datasource.business.databases." + alias;
            List<SystemConfig> configs = systemConfigDao.findByPrefix(prefix);
            
            for (SystemConfig conf : configs) {
                String key = conf.getConfigKey().replace(prefix + ".", "");
                config.put(key, conf.getConfigValue());
            }
            
            return config;
        } catch (Exception e) {
            log.error("Error getting datasource config: {}", alias, e);
            throw new FluxCoreException("获取数据源配置失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除数据源配置
     * @param alias 数据源别名
     * @return 是否删除成功
     */
    public boolean removeDataSource(String alias) {
        try {
            String prefix = "fluxcore.datasource.business.databases." + alias;
            List<SystemConfig> configs = systemConfigDao.findByPrefix(prefix);
            
            for (SystemConfig conf : configs) {
                systemConfigDao.deleteByKey(conf.getConfigKey());
            }
            
            // 注意：这里仅从配置表中删除，没有从运行时移除数据源
            // 完整实现需要从AbstractRoutingDataSource中移除目标数据源
            
            log.info("Successfully removed datasource configuration: {}", alias);
            return true;
        } catch (Exception e) {
            log.error("Error removing datasource: {}", alias, e);
            throw new FluxCoreException("删除数据源配置失败: " + e.getMessage());
        }
    }
}

// DataTransferService.java
package com.platform.fluxcore.service;

import com.platform.fluxcore.dao.business.DataEntityDao;
import com.platform.fluxcore.dao.collection.SourceDataDao;
import com.platform.fluxcore.entity.DataEntity;
import com.platform.fluxcore.entity.SourceData;
import com.platform.fluxcore.exception.FluxCoreException;
import com.platform.fluxcore.util.DataParserUtil;
import com.platform.fluxcore.util.DataSourceManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;