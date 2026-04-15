package com.fasthub.backend.cmm.naver;

import com.fasthub.backend.cmm.naver.dto.NaverShoppingItem;
import com.fasthub.backend.cmm.naver.dto.NaverShoppingResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@Slf4j
public class NaverShoppingClient {

    private static final String NAVER_SHOPPING_URL = "https://openapi.naver.com";

    private final RestClient restClient;

    public NaverShoppingClient(
            @Value("${naver.api.client-id}") String clientId,
            @Value("${naver.api.client-secret}") String clientSecret) {
        this.restClient = RestClient.builder()
                .baseUrl(NAVER_SHOPPING_URL)
                .defaultHeader("X-Naver-Client-Id", clientId)
                .defaultHeader("X-Naver-Client-Secret", clientSecret)
                .build();
    }

    /**
     * 네이버 쇼핑 검색 API 호출
     *
     * @param keyword 검색 키워드 (예: "반팔티", "청바지")
     * @param display 가져올 건수 (최대 100)
     * @return 상품 목록
     */
    public List<NaverShoppingItem> search(String keyword, int display) {
        try {
            NaverShoppingResponse response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1/search/shop.json")
                            .queryParam("query", keyword)
                            .queryParam("display", display)
                            .queryParam("start", 1)
                            .queryParam("sort", "sim")
                            .build())
                    .retrieve()
                    .body(NaverShoppingResponse.class);

            if (response == null || response.getItems() == null) {
                log.warn("[NaverClient] 응답 없음 - keyword={}", keyword);
                return List.of();
            }

            log.info("[NaverClient] 검색 완료 - keyword={}, 수신={}건", keyword, response.getItems().size());
            return response.getItems();

        } catch (Exception e) {
            log.error("[NaverClient] API 호출 실패 - keyword={}, error={}", keyword, e.getMessage());
            return List.of();
        }
    }
}
