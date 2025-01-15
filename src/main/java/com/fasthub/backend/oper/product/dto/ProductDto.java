package com.fasthub.backend.oper.product.dto;

import com.fasthub.backend.cmm.enums.ProductCategory;
import com.fasthub.backend.oper.product.entity.ProductImg;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class ProductDto {
    private String productNm;
    private ProductCategory productCategory;
    private int productPrice;
    private int productAuantity;
    private List<ProductImg> images = new ArrayList<>();
}
