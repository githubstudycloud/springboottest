package com.study.collect.business.testcase.model.response.parse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.collect.business.testcase.model.response.UriDetail;
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
public class UriDetailResponseParser implements HttpResponseParser<List<UriDetail>> {

    private final ObjectMapper objectMapper;

    @Override
    public List<UriDetail> parse(String response) throws IOException {
        try {
            JsonNode root = objectMapper.readTree(response);
            List<UriDetail> details = new ArrayList<>();

            root.path("result").path("value").forEach(detail -> {
                details.add(UriDetail.builder()
                        .uri(detail.path("uri").asText())
                        .realUri(detail.path("realUri").asText())
                        .number(detail.path("number").asText())
                        .name(detail.path("name").asText())
                        .updateTime(parseDateTime(detail.path("updateTime").asText()))
                        .build());
            });

            return details;
        } catch (Exception e) {
            log.error("Failed to parse URI details response: {}", response, e);
            throw new IOException("Failed to parse URI details response", e);
        }
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