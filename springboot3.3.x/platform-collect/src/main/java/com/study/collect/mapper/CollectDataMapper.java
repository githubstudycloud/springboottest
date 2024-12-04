package com.study.collect.mapper;

import com.study.collect.entity.CollectData;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

@Mapper
public interface CollectDataMapper {

    @Insert("""
                INSERT INTO collect_data (
                    device_id, device_name, temperature, humidity,
                    location, collect_time, create_time
                ) VALUES (
                    #{deviceId}, #{deviceName}, #{temperature}, #{humidity},
                    #{location}, #{collectTime}, #{createTime}
                )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "mysqlId")
    int insert(CollectData data);

    @Select("""
                SELECT * FROM collect_data 
                WHERE device_id = #{deviceId}
                AND collect_time BETWEEN #{startTime} AND #{endTime}
            """)
    List<CollectData> findByDeviceAndTimeRange(
            @Param("deviceId") String deviceId,
            @Param("startTime") Date startTime,
            @Param("endTime") Date endTime
    );
}