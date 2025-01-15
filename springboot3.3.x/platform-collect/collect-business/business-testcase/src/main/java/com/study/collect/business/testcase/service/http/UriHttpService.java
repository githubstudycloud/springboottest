package com.study.collect.business.testcase.service.http;

import com.study.collect.business.testcase.model.response.PageResponse;
import com.study.collect.business.testcase.model.response.VersionResponse;
import com.study.collect.business.testcase.model.response.parse.HttpResponseParser;
import com.study.collect.business.testcase.model.param.PageParam;
import com.study.collect.business.testcase.utils.HttpUtil;
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

