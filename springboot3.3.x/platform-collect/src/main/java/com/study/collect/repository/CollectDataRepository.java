package com.study.collect.repository;

import com.study.collect.entity.CollectData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface CollectDataRepository extends MongoRepository<CollectData, String> {

    List<CollectData> findByDeviceIdAndCollectTimeBetween(
            String deviceId, Date startTime, Date endTime);

    @Query("{'temperature': {$gte: ?0}}")
    List<CollectData> findByTemperatureGreaterThan(Double temperature);
}