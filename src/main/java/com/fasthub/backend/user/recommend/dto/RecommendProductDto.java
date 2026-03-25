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
    private String imgPath;    // 첫 번째 이미지 경로
    private long orderCount;   // 기간 내 주문 수
    private long reviewCount;  // 전체 리뷰 수
    private double avgRating;  // 평균 평점 (소수점 1자리)
    private String reason;     // 추천 이유 텍스트

    public static RecommendProductDto of(Product product, long orderCount) {
        return RecommendProductDto.builder()
                .productId(product.getId())
                .productNm(product.getProductNm())
                .productCode(product.getProductCode())
                .productPrice(product.getProductPrice())
                .category(product.getCategory())
                .brandNm(product.getBrand() != null ? product.getBrand().getBrandNm() : null)
                .orderCount(orderCount)
                .reviewCount(0L)
                .avgRating(0.0)
                .reason("최근 등록 상품")
                .build();
    }
}
