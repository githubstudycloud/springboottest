package com.study.collect.business.testcase.model.response;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.collect.business.testcase.model.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UriListResponseParser implements HttpResponseParser<PageResponse<String>> {
    private final ObjectMapper objectMapper;

    @Override
    public PageResponse<String> parse(String response) throws IOException {
        JsonNode root = objectMapper.readTree(response);

        PageResponse<String> pageResponse = new PageResponse<>();
        pageResponse.setTotal(root.path("total").asLong());

        List<String> uris = new ArrayList<>();
        JsonNode items = root.path("items");
        if (items.isArray()) {
            items.forEach(item -> uris.add(item.path("uri").asText()));
        }

        pageResponse.setItems(uris);
        return pageResponse;
    }
}
