package com.study.scheduler.mapper;


import com.study.scheduler.entity.JobLog;
import com.study.scheduler.entity.JobInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Date;

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

@Mapper
public interface JobInfoMapper {
    @Insert("""
                INSERT INTO schedule_job_info(
                    job_name, job_group, job_class, cron_expression,
                    parameter, description, concurrent, status,
                    next_fire_time, prev_fire_time, create_time, update_time
                ) VALUES (
                    #{jobName}, #{jobGroup}, #{jobClass}, #{cronExpression},
                    #{parameter}, #{description}, #{concurrent}, #{status},
                    #{nextFireTime}, #{prevFireTime}, #{createTime}, #{updateTime}
                )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(JobInfo jobInfo);

    @Update("""
                UPDATE schedule_job_info
                SET cron_expression = #{cronExpression},
                    parameter = #{parameter},
                    description = #{description},
                    concurrent = #{concurrent},
                    status = #{status},
                    next_fire_time = #{nextFireTime},
                    prev_fire_time = #{prevFireTime},
                    update_time = #{updateTime}
                WHERE job_name = #{jobName} AND job_group = #{jobGroup}
            """)
    int updateByJobKey(@Param("jobName") String jobName,
                       @Param("jobGroup") String jobGroup,
                       @Param("jobInfo") JobInfo jobInfo);

    @Delete("DELETE FROM schedule_job_info WHERE job_name = #{jobName} AND job_group = #{jobGroup}")
    int deleteByJobKey(@Param("jobName") String jobName,
                       @Param("jobGroup") String jobGroup);

    @Select("SELECT * FROM schedule_job_info WHERE job_name = #{jobName} AND job_group = #{jobGroup}")
    JobInfo findByJobKey(@Param("jobName") String jobName,
                         @Param("jobGroup") String jobGroup);

    @Select("SELECT * FROM schedule_job_info WHERE id = #{id}")
    JobInfo findById(@Param("id") Long id);

    @Select("SELECT * FROM schedule_job_info ORDER BY create_time DESC")
    List<JobInfo> findAll();

    @Select("SELECT * FROM schedule_job_info WHERE status = #{status}")
    List<JobInfo> findByStatus(@Param("status") Integer status);
}
