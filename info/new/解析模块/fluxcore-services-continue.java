// DataTransferService.java (续)
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
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 数据传输和解析服务
 */
@Slf4j
@Service
public class DataTransferService {
    
    @Autowired
    private SourceDataDao sourceDataDao;
    
    @Autowired
    private DataEntityDao dataEntityDao;
    
    @Autowired
    private DataParserUtil dataParserUtil;
    
    /**
     * 收集数据并存储到采集库
     * @param sourceType 数据源类型
     * @param content 数据内容
     * @param contentFormat 内容格式(json/xml/csv等)
     * @param sourceLocation 数据来源位置
     * @return 收集的数据记录
     */
    @Transactional(rollbackFor = Exception.class)
    public SourceData collectData(String sourceType, String content, String contentFormat, String sourceLocation) {
        try {
            // 验证内容格式是否支持
            if (!dataParserUtil.isSupported(contentFormat)) {
                throw new FluxCoreException("不支持的内容格式: " + contentFormat);
            }
            
            // 创建数据采集记录
            SourceData sourceData = new SourceData();
            sourceData.setSourceType(sourceType);
            sourceData.setSourceContent(content);
            sourceData.setContentFormat(contentFormat);
            sourceData.setCollectTime(new Date());
            sourceData.setSourceLocation(sourceLocation);
            sourceData.setStatus("COLLECTED");
            
            // 保存到采集库
            sourceDataDao.insert(sourceData);
            log.info("Successfully collected data: id={}, type={}", sourceData.getId(), sourceType);
            
            return sourceData;
        } catch (Exception e) {
            log.error("Error collecting data, type={}, format={}", sourceType, contentFormat, e);
            throw new FluxCoreException("数据采集失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 解析数据
     * @param sourceDataId 数据采集记录ID
     * @return 解析后的数据对象
     */
    public Object parseData(Long sourceDataId) {
        try {
            // 获取数据采集记录
            SourceData sourceData = sourceDataDao.findById(sourceDataId);
            if (sourceData == null) {
                throw new FluxCoreException("数据采集记录不存在: " + sourceDataId);
            }
            
            // 解析数据
            String content = sourceData.getSourceContent();
            String format = sourceData.getContentFormat();
            
            Object parsedData = dataParserUtil.parse(content, format);
            
            // 更新数据状态
            sourceData.setStatus("PARSED");
            sourceDataDao.updateStatus(sourceDataId, "PARSED");
            
            log.info("Successfully parsed data: id={}, format={}", sourceDataId, format);
            return parsedData;
        } catch (Exception e) {
            log.error("Error parsing data, id={}", sourceDataId, e);
            throw new FluxCoreException("数据解析失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 将数据处理后存储到业务库
     * @param sourceDataId 数据采集记录ID
     * @param targetDbAlias 目标数据库别名
     * @return 存储的业务数据实体
     */
    @Transactional(rollbackFor = Exception.class)
    public DataEntity processAndStore(Long sourceDataId, String targetDbAlias) {
        try {
            // 获取并解析数据
            Object parsedData = parseData(sourceDataId);
            SourceData sourceData = sourceDataDao.findById(sourceDataId);
            
            // 创建业务数据实体
            DataEntity dataEntity = new DataEntity();
            dataEntity.setDataCode("SD" + sourceDataId);
            dataEntity.setDataName(sourceData.getSourceType() + "_" + sourceDataId);
            
            // 根据不同格式处理数据内容
            String dataContent;
            if (parsedData instanceof Map) {
                dataContent = dataParserUtil.toJson(parsedData);
            } else if (parsedData instanceof List) {
                dataContent = dataParserUtil.toJson(Map.of("data", parsedData));
            } else {
                dataContent = parsedData.toString();
            }
            
            dataEntity.setDataContent(dataContent);
            dataEntity.setDataFormat("JSON"); // 统一转换为JSON格式存储
            dataEntity.setCreateTime(new Date());
            dataEntity.setUpdateTime(new Date());
            dataEntity.setSourceDb(targetDbAlias);
            
            // 切换到目标数据库并存储
            return DataSourceManager.executeWithDataSource(targetDbAlias, () -> {
                dataEntityDao.insert(dataEntity);
                log.info("Successfully stored data to business DB: id={}, dbAlias={}", 
                        dataEntity.getId(), targetDbAlias);
                return dataEntity;
            });
        } catch (Exception e) {
            log.error("Error processing and storing data, id={}, dbAlias={}", 
                    sourceDataId, targetDbAlias, e);
            throw new FluxCoreException("数据处理存储失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 从业务库获取数据并转换为指定格式
     * @param dataId 业务数据ID
     * @param dbAlias 数据库别名
     * @param targetFormat 目标格式
     * @return 转换后的数据字符串
     */
    public String retrieveAndConvert(Long dataId, String dbAlias, String targetFormat) {
        try {
            // 切换到目标数据库并获取数据
            DataEntity dataEntity = DataSourceManager.executeWithDataSource(dbAlias, () -> 
                    dataEntityDao.findById(dataId));
            
            if (dataEntity == null) {
                throw new FluxCoreException("业务数据不存在: id=" + dataId + ", db=" + dbAlias);
            }
            
            // 解析数据内容
            String content = dataEntity.getDataContent();
            String sourceFormat = dataEntity.getDataFormat();
            
            // 先解析为对象
            Object parsedData;
            if ("JSON".equalsIgnoreCase(sourceFormat)) {
                parsedData = dataParserUtil.parseJson(content);
            } else if ("XML".equalsIgnoreCase(sourceFormat)) {
                parsedData = dataParserUtil.parseXml(content);
            } else if ("CSV".equalsIgnoreCase(sourceFormat)) {
                parsedData = dataParserUtil.parseCsv(content);
            } else {
                throw new FluxCoreException("不支持的源数据格式: " + sourceFormat);
            }
            
            // 再转换为目标格式
            String result;
            if ("JSON".equalsIgnoreCase(targetFormat)) {
                result = dataParserUtil.toJson(parsedData);
            } else if ("XML".equalsIgnoreCase(targetFormat)) {
                result = dataParserUtil.toXml(parsedData);
            } else {
                throw new FluxCoreException("不支持的目标格式: " + targetFormat);
            }
            
            log.info("Successfully retrieved and converted data: id={}, db={}, format={} -> {}", 
                    dataId, dbAlias, sourceFormat, targetFormat);
            return result;
        } catch (Exception e) {
            log.error("Error retrieving and converting data, id={}, db={}, format={}", 
                    dataId, dbAlias, targetFormat, e);
            throw new FluxCoreException("数据获取转换失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 查询业务库数据列表
     * @param dbAlias 数据库别名
     * @param format 数据格式过滤(可选)
     * @return 数据实体列表
     */
    public List<DataEntity> queryBusinessData(String dbAlias, String format) {
        try {
            return DataSourceManager.executeWithDataSource(dbAlias, () -> {
                if (format != null && !format.isEmpty()) {
                    return dataEntityDao.findByFormat(format);
                } else {
                    return dataEntityDao.findAll();
                }
            });
        } catch (Exception e) {
            log.error("Error querying business data, db={}, format={}", dbAlias, format, e);
            throw new FluxCoreException("查询业务数据失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 查询采集数据列表
     * @param sourceType 数据源类型过滤(可选)
     * @return 采集数据列表
     */
    public List<SourceData> querySourceData(String sourceType) {
        try {
            if (sourceType != null && !sourceType.isEmpty()) {
                return sourceDataDao.findByType(sourceType);
            } else {
                return sourceDataDao.findAll();
            }
        } catch (Exception e) {
            log.error("Error querying source data, type={}", sourceType, e);
            throw new FluxCoreException("查询采集数据失败: " + e.getMessage(), e);
        }
    }
}
