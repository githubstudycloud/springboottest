package com.study.collect.core.collector.base;

import com.study.collect.common.exception.collect.CollectException;
import com.study.collect.core.engine.CollectContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

/**
 * HTTP树形采集器
 */
@Component("httpTreeCollector")
@RequiredArgsConstructor
@Slf4j
public class HttpTreeCollector extends TreeCollector {

    private final RestTemplate restTemplate;

    @Override
    protected TreeNode collectRoot(CollectContext context) {
        String url = context.getParams().get("url").toString();
        HttpMethod method = HttpMethod.valueOf(context.getParams().get("method").toString());

        try {
            ResponseEntity<TreeNode> response = restTemplate.exchange(
                    url,
                    method,
                    buildHttpEntity(context),
                    TreeNode.class
            );

            return response.getBody();
        } catch (Exception e) {
            log.error("Collect root node failed", e);
            throw new CollectException("Collect root node failed", e);
        }
    }

    @Override
    protected List<TreeNode> doCollectChildren(TreeNode parent, CollectContext context) {
        String url = buildChildrenUrl(parent, context);
        if (url == null) {
            return Collections.emptyList();
        }

        try {
            ResponseEntity<List<TreeNode>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    buildHttpEntity(context),
                    new ParameterizedTypeReference<List<TreeNode>>() {}
            );

            return response.getBody();
        } catch (Exception e) {
            log.error("Collect children failed for node: {}", parent.getId(), e);
            throw new CollectException("Collect children failed", e);
        }
    }

    private HttpEntity<?> buildHttpEntity(CollectContext context) {
        HttpHeaders headers = new HttpHeaders();
        // 添加请求头
        if (context.getParams().containsKey("headers")) {
            Map<String, String> headerMap = (Map<String, String>) context.getParams().get("headers");
            headerMap.forEach(headers::add);
        }
        return new HttpEntity<>(headers);
    }

    private String buildChildrenUrl(TreeNode parent, CollectContext context) {
        String baseUrl = context.getParams().get("url").toString();
        return baseUrl + "/" + parent.getId() + "/children";
    }
}
