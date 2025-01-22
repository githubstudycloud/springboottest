package com.study.collect.business.testcase.model.response.parse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.collect.business.testcase.model.response.UriDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                Map<String, Object> detailsMap = new HashMap<>();
                detailsMap.putAll(objectMapper.convertValue(detail,Map.class));
                details.add(UriDetail.builder()
                        .uri(detail.path("uri").asText())
                        .realUri(detail.path("realURI").asText())
                        .number(detail.path("number").asText())
                        .name(detail.path("name").asText())
                                .details(detailsMap)
                        .updateTime(parseDateTime(detail.path("lastModified").asText()))
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
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, formatter);
            return dateTime;
        } catch (Exception e) {
            log.warn("Failed to parse datetime: {}", dateTimeStr);
            return null;
        }
    }
}