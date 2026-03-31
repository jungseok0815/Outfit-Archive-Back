package com.fasthub.backend.user.recommend.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class ClipClient {

    private final RestClient restClient;

    public ClipClient(@Value("${clip.server.url:http://localhost:8000}") String clipServerUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(clipServerUrl)
                .build();
    }

    /**
     * S3 이미지 URL → CLIP 벡터 추출
     * FastAPI: POST /api/v1/image/vectorize-url
     */
    public List<Double> extractVector(Long productId, String imageUrl) {
        try {
            VectorResponse response = restClient.post()
                    .uri("/api/v1/image/vectorize-url")
                    .body(Map.of("url", imageUrl))
                    .retrieve()
                    .body(VectorResponse.class);
            return response != null ? response.getVector() : List.of();
        } catch (Exception e) {
            log.error("[ClipClient] 벡터 추출 실패 productId={}, error={}", productId, e.getMessage());
            return List.of();
        }
    }

    @lombok.Getter
    public static class VectorResponse {
        private List<Double> vector;
        private int dimension;
    }
}
