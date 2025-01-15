package com.study.collect.business.testcase.model.response.parse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class UriDetailResponseParser implements HttpResponseParser<List<Map<String, Object>>> {
    private final ObjectMapper objectMapper;

    @Override
    public List<Map<String, Object>> parse(String response) throws IOException {
        try {
            JsonNode root = objectMapper.readTree(response);
            validateResponse(root);

            List<Map<String, Object>> details = new ArrayList<>();
            JsonNode items = root.path("items");
            if (items.isArray()) {
                items.forEach(item -> {
                    try {
                        Map<String, Object> detail = convertToMap(item);
                        if (detail != null && !detail.isEmpty()) {
                            details.add(detail);
                        }
                    } catch (Exception e) {
                        log.error("Failed to parse URI detail item: {}", item, e);
                    }
                });
            }

            return details;
        } catch (Exception e) {
            log.error("Failed to parse URI details response: {}", response, e);
            throw new IOException("Failed to parse URI details response", e);
        }
    }

    private Map<String, Object> convertToMap(JsonNode node) {
        Map<String, Object> result = new LinkedHashMap<>();
        node.fields().forEachRemaining(entry -> {
            String key = entry.getKey();
            JsonNode valueNode = entry.getValue();
            Object value = convertJsonNode(valueNode);
            if (value != null) {
                result.put(key, value);
            }
        });
        return result;
    }

    private Object convertJsonNode(JsonNode node) {
        if (node.isNull()) {
            return null;
        } else if (node.isTextual()) {
            return node.asText();
        } else if (node.isNumber()) {
            return node.numberValue();
        } else if (node.isBoolean()) {
            return node.asBoolean();
        } else if (node.isArray()) {
            List<Object> list = new ArrayList<>();
            node.forEach(item -> {
                Object value = convertJsonNode(item);
                if (value != null) {
                    list.add(value);
                }
            });
            return list;
        } else if (node.isObject()) {
            return convertToMap(node);
        } else {
            return node.toString();
        }
    }

    private void validateResponse(JsonNode root) throws IOException {
        if (!root.has("items")) {
            throw new IOException("Invalid response format: missing items field");
        }
    }

    @Override
    public String parseError(String errorResponse) {
        try {
            JsonNode root = objectMapper.readTree(errorResponse);
            return root.path("message").asText("Unknown error");
        } catch (Exception e) {
            log.error("Failed to parse error response: {}", errorResponse, e);
            return "Failed to parse error response";
        }
    }
}