package com.fasthub.backend.admin.product.dto;

import com.fasthub.backend.cmm.enums.ProductCategory;
import com.fasthub.backend.admin.product.entity.ProductImg;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class ResponseProductDto {
    private Long id;
    private String productNm;
    private String productCode;
    private int productPrice;
    private int productQuantity;
    private Long brandId;
    private String brandNm;
    private ProductCategory category;
    private List<ProductImg> images;
}
