import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.collect.business.testcase.utils.HttpUtil;
import com.study.collect.business.testcase.utils.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class UriHttpService {
    private final ObjectMapper objectMapper;
    private final RateLimiter rateLimiter;

    @Qualifier("httpExecutor")
    private final ThreadPoolTaskExecutor httpExecutor;

    /**
     * 获取版本列表
     */
    public CompletableFuture<List<VersionInfo>> getVersions(String serverUrl, String rootNode, int page, int size) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                rateLimiter.acquire();
                String response = HttpUtil.post(
                        serverUrl + "/api/versions",
                        String.format("{\"rootNode\":\"%s\",\"page\":%d,\"size\":%d}", rootNode, page, size)
                ).getBody();

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

                return versions;
            } catch (Exception e) {
                log.error("Failed to get versions for rootNode: {}", rootNode, e);
                throw new RuntimeException("Failed to get versions", e);
            }
        }, httpExecutor);
    }

    /**
     * 获取URI列表
     */
    public CompletableFuture<List<String>> getUriList(String serverUrl, String version) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                rateLimiter.acquire();
                String response = HttpUtil.post(
                        serverUrl + "/api/uris",
                        String.format("{\"version\":\"%s\"}", version)
                ).getBody();

                JsonNode root = objectMapper.readTree(response);
                List<String> uris = new ArrayList<>();
                root.path("result").path("value").forEach(uri -> uris.add(uri.asText()));
                return uris;
            } catch (Exception e) {
                log.error("Failed to get URI list for version: {}", version, e);
                throw new RuntimeException("Failed to get URI list", e);
            }
        }, httpExecutor);
    }

    /**
     * 获取URI总数
     */
    public CompletableFuture<Integer> getUriCount(String serverUrl, String version) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                rateLimiter.acquire();
                String response = HttpUtil.post(
                        serverUrl + "/api/uri/count",
                        String.format("{\"version\":\"%s\"}", version)
                ).getBody();

                JsonNode root = objectMapper.readTree(response);
                return root.path("result").path("value").asInt();
            } catch (Exception e) {
                log.error("Failed to get URI count for version: {}", version, e);
                throw new RuntimeException("Failed to get URI count", e);
            }
        }, httpExecutor);
    }

    /**
     * 批量获取URI详情
     */
    public CompletableFuture<List<UriDetail>> getUriDetails(String serverUrl, List<String> uris) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                rateLimiter.acquire();
                String response = HttpUtil.post(
                        serverUrl + "/api/details",
                        objectMapper.writeValueAsString(Map.of("uris", uris))
                ).getBody();

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
                log.error("Failed to get URI details for {} URIs", uris.size(), e);
                throw new RuntimeException("Failed to get URI details", e);
            }
        }, httpExecutor);
    }

    private VersionInfo parseVersionInfo(JsonNode node) {
        return VersionInfo.builder()
                .version(node.path("version").asText())
                .name(node.path("name").asText())
                .updateTime(parseDateTime(node.path("updateTime").asText()))
                .build();
    }

    private LocalDateTime parseDateTime(String dateTime) {
        try {
            return LocalDateTime.parse(dateTime);
        } catch (Exception e) {
            log.warn("Failed to parse datetime: {}", dateTime);
            return null;
        }
    }
}