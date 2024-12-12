package com.study.scheduler.core.manager;

import com.study.scheduler.domain.entity.job.JobDefinition;
import com.study.scheduler.domain.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JobManager {

    @Autowired
    private JobRepository jobRepository;

    public JobDefinition createJob(JobDefinition jobDefinition) {
        return jobRepository.save(jobDefinition);
    }

    public JobDefinition updateJob(Long id, JobDefinition updatedJob) {
        JobDefinition existingJob = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found with id: " + id));
        existingJob.updateFrom(updatedJob);
        return jobRepository.save(existingJob);
    }

    public void deleteJob(Long id) {
        jobRepository.deleteById(id);
    }

    public JobDefinition getJob(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found with id: " + id));
    }

    public List<JobDefinition> listJobs() {
        return jobRepository.findAll();
    }
}
