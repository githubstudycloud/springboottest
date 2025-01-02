// UriListResponseParser.java
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

// UriDetailResponseParser.java
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