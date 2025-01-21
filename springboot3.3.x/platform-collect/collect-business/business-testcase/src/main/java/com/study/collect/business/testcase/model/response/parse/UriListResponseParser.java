package com.study.collect.business.testcase.model.response.parse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UriListResponseParser implements HttpResponseParser<List<String>> {

    private final ObjectMapper objectMapper;

    @Override
    public List<String> parse(String response) throws IOException {
        try {
            JsonNode root = objectMapper.readTree(response);
            List<String> uris = new ArrayList<>();

            root.path("result").path("value").forEach(uri ->
                    uris.add(uri.asText())
            );

            return uris;
        } catch (Exception e) {
            log.error("Failed to parse URI list response: {}", response, e);
            throw new IOException("Failed to parse URI list response", e);
        }
    }
}