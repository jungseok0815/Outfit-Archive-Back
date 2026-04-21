package com.fasthub.backend.user.recommend.dto;

import com.fasthub.backend.admin.category.dto.ResponseCategoryDto;
import com.fasthub.backend.admin.category.entity.Category;
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
    private ResponseCategoryDto category;
    private String brandNm;
    private String imgPath;
    private long orderCount;
    private long reviewCount;
    private double avgRating;
    private String reason;

    public static RecommendProductDto of(Product product, long orderCount) {
        String imgPath = (product.getImages() != null && !product.getImages().isEmpty())
                ? product.getImages().get(0).getImgPath() : null;
        return RecommendProductDto.builder()
                .productId(product.getId())
                .productNm(product.getProductNm())
                .productCode(product.getProductCode())
                .productPrice(product.getProductPrice())
                .category(toDto(product.getCategory()))
                .brandNm(product.getBrand() != null ? product.getBrand().getBrandNm() : null)
                .imgPath(imgPath)
                .orderCount(orderCount)
                .reviewCount(0L)
                .avgRating(0.0)
                .reason("최근 등록 상품")
                .build();
    }

    public static ResponseCategoryDto toDto(Category category) {
        if (category == null) return null;
        ResponseCategoryDto dto = new ResponseCategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setKorName(category.getKorName());
        dto.setEngName(category.getEngName());
        return dto;
    }
}
