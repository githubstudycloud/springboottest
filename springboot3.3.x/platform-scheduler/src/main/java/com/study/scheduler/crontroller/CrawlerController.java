package com.study.scheduler.crontroller;

import com.study.scheduler.entity.CrawlerRecord;
import com.study.scheduler.entity.CrawlerTask;
import com.study.scheduler.service.CrawlerService;
import com.study.scheduler.utils.MongoDBUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/crawler")
public class CrawlerController {
    private final CrawlerService crawlerService;
    private final MongoDBUtils mongoDBUtils;

    public CrawlerController(CrawlerService crawlerService, MongoDBUtils mongoDBUtils) {
        this.crawlerService = crawlerService;
        this.mongoDBUtils = mongoDBUtils;
    }

    @PostMapping("/tasks")
    public CrawlerTask createTask(@RequestBody CrawlerTask task) {
        return crawlerService.createTask(task);
    }

    @PostMapping("/tasks/{taskId}/execute")
    public CrawlerRecord executeCrawling(@PathVariable String taskId) {
        CrawlerTask task = mongoDBUtils.findById(taskId, CrawlerTask.class);
        return crawlerService.crawl(task);
    }

    @GetMapping("/records/failed")
    public List<CrawlerRecord> getFailedRecords() {
        return crawlerService.getFailedRecords();
    }

    @PostMapping("/records/{recordId}/retry")
    public void retryFailedRecord(@PathVariable String recordId) {
        crawlerService.retryCrawling(recordId);
    }
}
