// service/UriCollectService.java
package com.study.collect.service;

import com.study.collect.domain.entity.UriEntity;
import com.study.collect.domain.param.CollectParam;
import java.util.List;

public interface UriCollectService {
    void collectData(CollectParam param);
    List<UriEntity> queryUri(String rootNode, String version, String versionType);
}

// service/http/UriHttpService.java
package com.study.collect.service.http;

import com.study.collect.api.response.PageResponse;
import com.study.collect.api.response.VersionResponse;
import com.study.collect.domain.param.PageParam;
import com.study.collect.service.http.response.HttpResponseParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class UriHttpService {
    private final HttpUtil httpUtil;
    private final HttpResponseParser<PageResponse<VersionResponse>> versionParser;
    private final HttpResponseParser<PageResponse<String>> uriListParser;
    private final HttpResponseParser<List<Map<String, Object>>> uriDetailParser;
    
    public PageResponse<VersionResponse> getVersions(String rootNode, PageParam pageParam) throws IOException {
        String response = httpUtil.post("/api/versions", 
            Map.of("rootNode", rootNode,
                   "page", pageParam.getPage(),
                   "size", pageParam.getSize()));
        return versionParser.parse(response);
    }
    
    public PageResponse<String> getUriList(String version, PageParam pageParam) throws IOException {
        String response = httpUtil.post("/api/uris", 
            Map.of("version", version,
                   "page", pageParam.getPage(),
                   "size", pageParam.getSize()));
        return uriListParser.parse(response);
    }
    
    public List<Map<String, Object>> getUriDetails(List<String> uris) throws IOException {
        String response = httpUtil.post("/api/details", Map.of("uris", uris));
        return uriDetailParser.parse(response);
    }
}

// service/http/response/HttpResponseParser.java
package com.study.collect.service.http.response;

import java.io.IOException;

public interface HttpResponseParser<T> {
    T parse(String response) throws IOException;
}

// service/http/response/impl/VersionResponseParser.java
package com.study.collect.service.http.response.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.collect.api.response.PageResponse;
import com.study.collect.api.response.VersionResponse;
import com.study.collect.service.http.response.HttpResponseParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VersionResponseParser implements HttpResponseParser<PageResponse<VersionResponse>> {
    private final ObjectMapper objectMapper;

    @Override
    public PageResponse<VersionResponse> parse(String response) throws IOException {
        return objectMapper.readValue(response, 
            objectMapper.getTypeFactory().constructParametricType(
                PageResponse.class, VersionResponse.class));
    }
}