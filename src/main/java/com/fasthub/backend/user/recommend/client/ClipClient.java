package com.fasthub.backend.user.recommend.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
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
                .requestFactory(new SimpleClientHttpRequestFactory())
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
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("url", imageUrl))
                    .retrieve()
                    .body(VectorResponse.class);
            return response != null ? response.getVector() : List.of();
        } catch (Exception e) {
            log.error("[ClipClient] 벡터 추출 실패 productId={}, error={}", productId, e.getMessage());
            return List.of();
        }
    }

    /**
     * 이미지 URL → 단독 상품 이미지 여부 판별
     * FastAPI: POST /api/v1/image/detect-clean-product
     */
    public boolean detectCleanProduct(String imageUrl) {
        try {
            CleanProductResponse response = restClient.post()
                    .uri("/api/v1/image/detect-clean-product")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("url", imageUrl))
                    .retrieve()
                    .body(CleanProductResponse.class);
            log.info("response : {}", response.isCleanProduct());
            return response != null && response.isCleanProduct();
        } catch (Exception e) {
            log.error("[ClipClient] 단독 상품 판별 실패 imageUrl={}, error={}", imageUrl, e.getMessage());
            return false;
        }
    }

    public List<Boolean> detectCleanProductBatch(List<String> imageUrls) {
        try {
            CleanProductBatchResponse response = restClient.post()
                    .uri("/api/v1/image/detect-clean-product-batch")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("urls", imageUrls))
                    .retrieve()
                    .body(CleanProductBatchResponse.class);
            return response != null ? response.getResults() : List.of();
        } catch (Exception e) {
            log.error("[ClipClient] 배치 단독 상품 판별 실패 error={}", e.getMessage());
            return List.of();
        }
    }

    @lombok.Getter
    public static class VectorResponse {
        private List<Double> vector;
        private int dimension;
    }

    @lombok.Getter
    public static class CleanProductResponse {
        @com.fasterxml.jackson.annotation.JsonProperty("is_clean_product")
        private boolean isCleanProduct;
    }

    @lombok.Getter
    public static class CleanProductBatchResponse {
        @com.fasterxml.jackson.annotation.JsonProperty("results")
        private List<Boolean> results;
    }
}
