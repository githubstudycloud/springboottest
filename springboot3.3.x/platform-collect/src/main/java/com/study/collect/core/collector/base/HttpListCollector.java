package com.study.collect.core.collector.base;

import com.study.collect.common.exception.collect.CollectException;
import com.study.collect.core.engine.CollectContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * HTTP列表采集器
 */
@Component("httpListCollector")
@RequiredArgsConstructor
@Slf4j
public class HttpListCollector extends ListCollector {

    private final RestTemplate restTemplate;

    @Override
    protected long getTotal(CollectContext context) {
        String url = buildTotalUrl(context);

        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    buildHttpEntity(context),
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            return Long.parseLong(response.getBody().get("total").toString());
        } catch (Exception e) {
            log.error("Get total count failed", e);
            throw new CollectException("Get total count failed", e);
        }
    }

    @Override
    protected List<Object> collectPage(int page, int pageSize, CollectContext context) {
        String url = buildPageUrl(context, page, pageSize);

        try {
            ResponseEntity<List<Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    buildHttpEntity(context),
                    new ParameterizedTypeReference<List<Object>>() {}
            );

            return response.getBody();
        } catch (Exception e) {
            log.error("Collect page data failed, page: {}", page, e);
            throw new CollectException("Collect page data failed", e);
        }
    }

    private HttpEntity<?> buildHttpEntity(CollectContext context) {
        HttpHeaders headers = new HttpHeaders();
        if (context.getParams().containsKey("headers")) {
            Map<String, String> headerMap = (Map<String, String>) context.getParams().get("headers");
            headerMap.forEach(headers::add);
        }
        return new HttpEntity<>(headers);
    }

    private String buildTotalUrl(CollectContext context) {
        return context.getParams().get("url") + "/count";
    }

    private String buildPageUrl(CollectContext context, int page, int pageSize) {
        return context.getParams().get("url") + "?page=" + page + "&size=" + pageSize;
    }
}