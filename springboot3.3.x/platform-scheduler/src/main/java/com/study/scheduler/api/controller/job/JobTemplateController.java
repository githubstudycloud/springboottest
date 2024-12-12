package com.study.scheduler.api.controller.job;

import com.study.scheduler.api.model.request.job.JobTemplateRequest;
import com.study.scheduler.api.model.vo.job.JobTemplateVO;
import com.study.scheduler.core.manager.JobTemplateManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs/templates")
public class JobTemplateController {

    @Autowired
    private JobTemplateManager jobTemplateManager;

    @PostMapping
    public JobTemplateVO createTemplate(@RequestBody JobTemplateRequest request) {
        return JobTemplateVO.fromEntity(jobTemplateManager.createTemplate(request.toEntity()));
    }

    @GetMapping("/{id}")
    public JobTemplateVO getTemplate(@PathVariable Long id) {
        return JobTemplateVO.fromEntity(jobTemplateManager.getTemplate(id));
    }

    @GetMapping
    public List<JobTemplateVO> listTemplates() {
        return JobTemplateVO.fromEntities(jobTemplateManager.listTemplates());
    }

    @DeleteMapping("/{id}")
    public void deleteTemplate(@PathVariable Long id) {
        jobTemplateManager.deleteTemplate(id);
    }
}
