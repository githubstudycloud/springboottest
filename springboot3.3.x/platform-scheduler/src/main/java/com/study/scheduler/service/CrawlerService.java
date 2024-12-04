package com.study.scheduler.service;

import com.study.scheduler.entity.CrawlerRecord;
import com.study.scheduler.entity.CrawlerTask;
import com.study.scheduler.utils.HttpClientUtil;
import com.study.scheduler.utils.MongoDBUtils;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CrawlerService {
    private final MongoDBUtils mongoDBUtils;

    public CrawlerService(MongoDBUtils mongoDBUtils) {
        this.mongoDBUtils = mongoDBUtils;
    }

    public CrawlerTask createTask(CrawlerTask task) {
        task.setCreateTime(new Date());
        task.setUpdateTime(new Date());
        return mongoDBUtils.save(task);
    }

    public CrawlerRecord crawl(CrawlerTask task) {
        CrawlerRecord record = new CrawlerRecord();
        record.setTaskId(task.getId());
        record.setUrl(task.getUrl());
        record.setCreateTime(new Date());
        record.setUpdateTime(new Date());

        try {
            String response;
            if ("POST".equalsIgnoreCase(task.getMethod())) {
                response = HttpClientUtil.doPost(task.getUrl(), task.getRequestBody());
            } else {
                response = HttpClientUtil.doGet(task.getUrl());
            }

            record.setResponseBody(response);
            record.setSuccess(true);
            record.setStatusCode(200);
        } catch (Exception e) {
            record.setSuccess(false);
            record.setErrorMessage(e.getMessage());
            record.setStatusCode(500);

            if (task.getRetryCount() < task.getMaxRetries()) {
                scheduleRetry(task);
            }
        }

        return mongoDBUtils.save(record);
    }

    private void scheduleRetry(CrawlerTask task) {
        Query query = new Query(Criteria.where("id").is(task.getId()));
        Update update = new Update()
                .inc("retryCount", 1)
                .set("updateTime", new Date());
        mongoDBUtils.update(query, update, CrawlerTask.class);
    }

    public List<CrawlerRecord> getFailedRecords() {
        Query query = new Query(Criteria.where("success").is(false));
        return mongoDBUtils.find(query, CrawlerRecord.class);
    }

    public void retryCrawling(String recordId) {
        CrawlerRecord record = mongoDBUtils.findById(recordId, CrawlerRecord.class);
        if (record != null) {
            CrawlerTask task = mongoDBUtils.findById(record.getTaskId(), CrawlerTask.class);
            if (task != null) {
                crawl(task);
            }
        }
    }
}