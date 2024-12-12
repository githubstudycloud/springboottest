package com.study.scheduler.core.manager;

import com.study.scheduler.domain.entity.job.JobTemplate;
import com.study.scheduler.domain.repository.JobTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JobTemplateManager {

    @Autowired
    private JobTemplateRepository jobTemplateRepository;

    public JobTemplate createTemplate(JobTemplate template) {
        return jobTemplateRepository.save(template);
    }

    public JobTemplate getTemplate(Long id) {
        return jobTemplateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Template not found with id: " + id));
    }

    public List<JobTemplate> listTemplates() {
        return jobTemplateRepository.findAll();
    }

    public void deleteTemplate(Long id) {
        jobTemplateRepository.deleteById(id);
    }
}
