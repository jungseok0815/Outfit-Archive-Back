package com.fasthub.backend.user.similar.dto;

import com.fasthub.backend.admin.category.dto.ResponseCategoryDto;
import com.fasthub.backend.admin.category.entity.Category;
import com.fasthub.backend.admin.product.entity.Product;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SimilarProductDto {

    private Long productId;
    private String productNm;
    private String productCode;
    private int productPrice;
    private ResponseCategoryDto category;
    private String brandNm;
    private String imgPath;
    private double similarity;

    public static SimilarProductDto of(Product product, double similarity) {
        String imgPath = (product.getImages() != null && !product.getImages().isEmpty())
                ? product.getImages().get(0).getImgPath()
                : null;
        return SimilarProductDto.builder()
                .productId(product.getId())
                .productNm(product.getProductNm())
                .productCode(product.getProductCode())
                .productPrice(product.getProductPrice())
                .category(toDto(product.getCategory()))
                .brandNm(product.getBrand() != null ? product.getBrand().getBrandNm() : null)
                .imgPath(imgPath)
                .similarity(Math.round(similarity * 1000.0) / 1000.0)
                .build();
    }

    private static ResponseCategoryDto toDto(Category category) {
        if (category == null) return null;
        ResponseCategoryDto dto = new ResponseCategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setKorName(category.getKorName());
        dto.setEngName(category.getEngName());
        return dto;
    }
}
