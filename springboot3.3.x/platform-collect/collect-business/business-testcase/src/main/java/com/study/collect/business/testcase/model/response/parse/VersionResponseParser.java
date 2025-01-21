package com.study.collect.business.testcase.model.response.parse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.collect.business.testcase.model.response.PageResponse;
import com.study.collect.business.testcase.model.response.VersionInfo;
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
public class VersionResponseParser implements HttpResponseParser<PageResponse<VersionInfo>> {

    private final ObjectMapper objectMapper;

    @Override
    public PageResponse<VersionInfo> parse(String response) throws IOException {
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode value = root.path("result").path("value");

            List<VersionInfo> versions = new ArrayList<>();
            // 解析嵌套的children结构
            value.path("children").forEach(child -> {
                if ("children".equals(child.path("elementName").asText())) {
                    child.path("children").forEach(version -> {
                        versions.add(parseVersionInfo(version));
                    });
                }
            });

            return PageResponse.<VersionInfo>builder()
                    .items(versions)
                    .build();
        } catch (Exception e) {
            log.error("Failed to parse version response: {}", response, e);
            throw new IOException("Failed to parse version response", e);
        }
    }

    private VersionInfo parseVersionInfo(JsonNode node) {
        return VersionInfo.builder()
                .version(node.path("version").asText())
                .name(node.path("name").asText())
                .type(node.path("type").asText())
                .updateTime(parseDateTime(node.path("updateTime").asText()))
                .build();
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {
        try {
            return LocalDateTime.parse(dateTimeStr);
        } catch (Exception e) {
            log.warn("Failed to parse datetime: {}", dateTimeStr);
            return null;
        }
    }
}