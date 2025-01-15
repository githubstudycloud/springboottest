package com.study.collect.business.testcase.model.response.parse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.collect.business.testcase.model.response.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UriListResponseParser implements HttpResponseParser<PageResponse<String>> {
    private final ObjectMapper objectMapper;

    @Override
    public PageResponse<String> parse(String response) throws IOException {
        try {
            JsonNode root = objectMapper.readTree(response);
            validateResponse(root);

            PageResponse<String> pageResponse = new PageResponse<>();
            pageResponse.setCode(root.path("code").asText());
            pageResponse.setMessage(root.path("message").asText());
            pageResponse.setTotal(root.path("total").asLong());

            List<String> uris = new ArrayList<>();
            JsonNode items = root.path("items");
            if (items.isArray()) {
                items.forEach(item -> {
                    String uri = item.path("uri").asText();
                    if (uri != null && !uri.isEmpty()) {
                        uris.add(uri);
                    }
                });
            }

            pageResponse.setItems(uris);
            return pageResponse;
        } catch (Exception e) {
            log.error("Failed to parse URI list response: {}", response, e);
            throw new IOException("Failed to parse URI list response", e);
        }
    }

    private void validateResponse(JsonNode root) throws IOException {
        if (!root.has("code") || !root.has("total") || !root.has("items")) {
            throw new IOException("Invalid response format: missing required fields");
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