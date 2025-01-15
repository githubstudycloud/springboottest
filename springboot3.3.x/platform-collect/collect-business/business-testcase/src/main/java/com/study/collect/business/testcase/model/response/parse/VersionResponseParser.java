package com.study.collect.business.testcase.model.response.parse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.collect.business.testcase.model.response.PageResponse;
import com.study.collect.business.testcase.model.response.VersionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

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