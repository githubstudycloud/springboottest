package com.study.collect.business.testcase.model.response.parse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class UriDetailResponseParser implements HttpResponseParser<List<Map<String, Object>>> {
    private final ObjectMapper objectMapper;

    @Override
    public List<Map<String, Object>> parse(String response) throws IOException {
        JsonNode root = objectMapper.readTree(response);
        List<Map<String, Object>> details = new ArrayList<>();

        JsonNode items = root.path("items");
        if (items.isArray()) {
            items.forEach(item -> {
                try {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> detail = objectMapper.convertValue(item, Map.class);
                    details.add(detail);
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException("Failed to parse URI detail", e);
                }
            });
        }

        return details;
    }
}