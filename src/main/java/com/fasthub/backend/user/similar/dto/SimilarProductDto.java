package com.fasthub.backend.user.similar.dto;

import com.fasthub.backend.admin.product.entity.Product;
import com.fasthub.backend.cmm.enums.ProductCategory;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SimilarProductDto {

    private Long productId;
    private String productNm;
    private String productCode;
    private int productPrice;
    private ProductCategory category;
    private String brandNm;
    private double similarity;

    public static SimilarProductDto of(Product product, double similarity) {
        return SimilarProductDto.builder()
                .productId(product.getId())
                .productNm(product.getProductNm())
                .productCode(product.getProductCode())
                .productPrice(product.getProductPrice())
                .category(product.getCategory())
                .brandNm(product.getBrand() != null ? product.getBrand().getBrandNm() : null)
                .similarity(Math.round(similarity * 1000.0) / 1000.0)
                .build();
    }
}
