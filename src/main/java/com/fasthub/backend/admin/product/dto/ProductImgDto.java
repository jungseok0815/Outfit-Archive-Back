package com.fasthub.backend.admin.product.dto;

import com.fasthub.backend.admin.product.entity.Product;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class ProductImgDto {
    private Long id;
    private String imgPath;
    private String imgNm;
    private String imgOriginNm;
    private Product product;
}
