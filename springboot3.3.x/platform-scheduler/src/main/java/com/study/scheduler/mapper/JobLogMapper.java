package com.study.scheduler.mapper;


import com.study.scheduler.entity.JobLog;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

@Mapper
public interface JobLogMapper {
    @Insert("""
                INSERT INTO schedule_job_log(
                    job_id, job_name, job_group, job_class, parameter,
                    message, status, exception_info, start_time,
                    end_time, duration, server_ip
                ) VALUES (
                    #{jobId}, #{jobName}, #{jobGroup}, #{jobClass}, #{parameter},
                    #{message}, #{status}, #{exceptionInfo}, #{startTime},
                    #{endTime}, #{duration}, #{serverIp}
                )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(JobLog jobLog);

    @Select("""
                SELECT * FROM schedule_job_log
                WHERE job_name = #{jobName} AND job_group = #{jobGroup}
                ORDER BY start_time DESC
                LIMIT #{limit}
            """)
    List<JobLog> findRecentLogs(@Param("jobName") String jobName,
                                @Param("jobGroup") String jobGroup,
                                @Param("limit") int limit);

    @Select("""
                SELECT * FROM schedule_job_log
                WHERE job_name = #{jobName}
                  AND job_group = #{jobGroup}
                  AND start_time BETWEEN #{startTime} AND #{endTime}
                ORDER BY start_time DESC
            """)
    List<JobLog> findLogsByTimeRange(@Param("jobName") String jobName,
                                     @Param("jobGroup") String jobGroup,
                                     @Param("startTime") Date startTime,
                                     @Param("endTime") Date endTime);

    @Select("SELECT * FROM schedule_job_log WHERE id = #{id}")
    JobLog findById(@Param("id") Long id);

    @Delete("DELETE FROM schedule_job_log WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    @Delete("""
                DELETE FROM schedule_job_log
                WHERE start_time < #{beforeTime}
            """)
    int cleanupOldLogs(@Param("beforeTime") Date beforeTime);
}

