package com.study.collect.business.testcase.model.response.parse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.collect.business.testcase.model.response.PageResponse;
import com.study.collect.business.testcase.model.response.VersionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class VersionResponseParser implements HttpResponseParser<PageResponse<VersionResponse>> {
    private final ObjectMapper objectMapper;

    @Override
    public PageResponse<VersionResponse> parse(String response) throws IOException {
        try {
            JsonNode root = objectMapper.readTree(response);
            validateResponse(root);

            PageResponse<VersionResponse> pageResponse = new PageResponse<>();
            pageResponse.setCode(root.path("code").asText());
            pageResponse.setMessage(root.path("message").asText());
            pageResponse.setTotal(root.path("total").asLong());

            List<VersionResponse> versions = new ArrayList<>();
            JsonNode items = root.path("items");
            if (items.isArray()) {
                items.forEach(item -> {
                    try {
                        VersionResponse version = parseVersionItem(item);
                        if (version != null) {
                            versions.add(version);
                        }
                    } catch (Exception e) {
                        log.error("Failed to parse version item: {}", item, e);
                    }
                });
            }

            pageResponse.setItems(versions);
            return pageResponse;
        } catch (Exception e) {
            log.error("Failed to parse version response: {}", response, e);
            throw new IOException("Failed to parse version response", e);
        }
    }

    private VersionResponse parseVersionItem(JsonNode item) {
        return VersionResponse.builder()
                .version(item.path("version").asText())
                .versionType(item.path("versionType").asText())
                .description(item.path("description").asText())
                .createTime(parseDateTime(item.path("createTime").asText()))
                .updateTime(parseDateTime(item.path("updateTime").asText()))
                .status(item.path("status").asText())
                .sort(item.path("sort").asInt())
                .build();
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {
        try {
            return LocalDateTime.parse(dateTimeStr);
        } catch (Exception e) {
            log.debug("Failed to parse datetime: {}", dateTimeStr);
            return null;
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