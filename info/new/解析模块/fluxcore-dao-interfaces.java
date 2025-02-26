// DataEntityDao.java
package com.platform.fluxcore.dao.business;

import com.platform.fluxcore.entity.DataEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataEntityDao {
    List<DataEntity> findAll();
    DataEntity findById(@Param("id") Long id);
    List<DataEntity> findByFormat(@Param("dataFormat") String dataFormat);
    int insert(DataEntity dataEntity);
    int update(DataEntity dataEntity);
    int deleteById(@Param("id") Long id);
}

// SourceDataDao.java
package com.platform.fluxcore.dao.collection;

import com.platform.fluxcore.entity.SourceData;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Date;

@Repository
public interface SourceDataDao {
    List<SourceData> findAll();
    SourceData findById(@Param("id") Long id);
    List<SourceData> findByType(@Param("sourceType") String sourceType);
    List<SourceData> findByDateRange(
        @Param("startDate") Date startDate, 
        @Param("endDate") Date endDate
    );
    int insert(SourceData sourceData);
    int update(SourceData sourceData);
    int updateStatus(@Param("id") Long id, @Param("status") String status);
    int deleteById(@Param("id") Long id);
}

// SystemConfigDao.java
package com.platform.fluxcore.dao.pub;

import com.platform.fluxcore.entity.SystemConfig;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SystemConfigDao {
    List<SystemConfig> findAll();
    SystemConfig findByKey(@Param("configKey") String configKey);
    List<SystemConfig> findByPrefix(@Param("prefix") String prefix);
    int insert(SystemConfig config);
    int update(SystemConfig config);
    int updateValue(@Param("configKey") String configKey, @Param("configValue") String configValue);
    int deleteByKey(@Param("configKey") String configKey);
}
