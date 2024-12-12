package com.study.scheduler.repository;

import com.study.common.model.task.TaskExecution;
import com.study.scheduler.model.job.JobExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobExecutionRepository extends JpaRepository<TaskExecution, String> {
    List<TaskExecution> findByJobDefId(String jobDefId);
}