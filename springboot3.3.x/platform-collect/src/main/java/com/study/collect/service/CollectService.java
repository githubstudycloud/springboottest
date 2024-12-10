package com.study.collect.service;

import com.study.collect.entity.CollectData;
import com.study.collect.mapper.CollectDataMapper;
import com.study.collect.repository.CollectDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class CollectService {
    private static final Logger logger = LoggerFactory.getLogger(CollectService.class);

    @Autowired
    private CollectDataMapper mysqlMapper;

    @Autowired
    private CollectDataRepository mongoRepository;

    @Transactional
    public void saveData(CollectData data) {
        try {
            // 设置时间
            Date now = new Date();
            if (data.getCollectTime() == null) {
                data.setCollectTime(now);
            }
            data.setCreateTime(now);

            // 保存到MySQL
            mysqlMapper.insert(data);
            logger.info("Data saved to MySQL with id: {}", data.getMysqlId());

            // 保存到MongoDB
            mongoRepository.save(data);
            logger.info("Data saved to MongoDB with id: {}", data.getId());

        } catch (Exception e) {
            logger.error("Error saving data", e);
            throw e;
        }
    }

    public List<CollectData> queryData(String deviceId, Date startTime, Date endTime) {
        // 从MySQL查询
        List<CollectData> mysqlData = mysqlMapper.findByDeviceAndTimeRange(
                deviceId, startTime, endTime);
        logger.info("Found {} records in MySQL", mysqlData.size());

        // 从MongoDB查询
        List<CollectData> mongoData = mongoRepository
                .findByDeviceIdAndCollectTimeBetween(deviceId, startTime, endTime);
        logger.info("Found {} records in MongoDB", mongoData.size());

        return mongoData; // 这里返回MongoDB的数据，因为它可能包含更多信息
    }

    public List<CollectData> findHighTemperatureData(Double threshold) {
        return mongoRepository.findByTemperatureGreaterThan(threshold);
    }
}