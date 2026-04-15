package com.fasthub.backend.cmm.naver.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class NaverShoppingItem {

    private String title;       // 상품명 (HTML 태그 포함될 수 있음)
    private String image;       // 이미지 URL
    private String lprice;      // 최저가
    private String brand;       // 브랜드명
    private String productId;   // 네이버 상품 고유 ID (중복 방지용)
    private String category1;   // 대분류 (예: 패션의류)
    private String category2;   // 중분류 (예: 상의)
    private String category3;   // 소분류 (예: 반팔티셔츠)

    // HTML 태그 제거한 순수 상품명 반환 (<b>반팔티</b> → 반팔티)
    public String getCleanTitle() {
        if (title == null) return "";
        return title.replaceAll("<[^>]*>", "").trim();
    }

    public int getPriceAsInt() {
        try {
            return Integer.parseInt(lprice);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
