package com.fasthub.backend.user.recommend.dto;

import com.fasthub.backend.cmm.enums.ProductCategory;
import com.fasthub.backend.admin.product.entity.Product;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecommendProductDto {
    private Long productId;
    private String productNm;
    private String productCode;
    private int productPrice;
    private ProductCategory category;
    private String brandNm;
    private long orderCount;  // 추천 근거 수치
    private String reason;    // 추천 이유 텍스트

    public static RecommendProductDto of(Product product, long orderCount) {
        return RecommendProductDto.builder()
                .productId(product.getId())
                .productNm(product.getProductNm())
                .productCode(product.getProductCode())
                .productPrice(product.getProductPrice())
                .category(product.getCategory())
                .brandNm(product.getBrand() != null ? product.getBrand().getBrandNm() : null)
                .orderCount(orderCount)
                .reason("최근 30일 인기 상품")
                .build();
    }
}
