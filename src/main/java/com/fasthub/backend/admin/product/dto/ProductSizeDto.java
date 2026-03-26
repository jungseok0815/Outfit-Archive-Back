package com.fasthub.backend.admin.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductSizeDto {
    private Long id;
    private String sizeNm;
    private int quantity;
}
