package com.study.scheduler.job;

import com.study.common.util.JsonUtils;
import com.study.scheduler.entity.TreeCollectRequest;
import com.study.scheduler.utils.HttpClientUtil;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class TreeCollectorJob extends BaseJob {

    @Value("${collect.service.url}")
    private String collectServiceUrl;

    @Override
    protected void doExecute(JobExecutionContext context) throws Exception {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        String url = dataMap.getString("url");
        String method = dataMap.getString("method");

        // 构建采集请求
        TreeCollectRequest request = new TreeCollectRequest();
        request.setUrl(url);
        request.setMethod(method);

        // 创建采集任务
        String response = HttpClientUtil.doPost(
                collectServiceUrl + "/api/tree/collect",
                JsonUtils.toJson(request)
        );

        logger.info("Tree collection task created: {}", response);
    }
}