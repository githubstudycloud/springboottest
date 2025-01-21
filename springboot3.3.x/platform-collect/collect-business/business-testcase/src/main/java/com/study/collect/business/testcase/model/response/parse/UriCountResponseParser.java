package com.study.collect.business.testcase.model.response.parse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class UriCountResponseParser implements HttpResponseParser<Integer> {

    private final ObjectMapper objectMapper;

    @Override
    public Integer parse(String response) throws IOException {
        try {
            JsonNode root = objectMapper.readTree(response);
            return root.path("result").path("value").asInt();
        } catch (Exception e) {
            log.error("Failed to parse URI count response: {}", response, e);
            throw new IOException("Failed to parse URI count response", e);
        }
    }
}