package com.fasthub.backend.oper.product.dto;

import com.fasthub.backend.cmm.enums.ProductCategory;
import com.fasthub.backend.oper.product.entity.ProductImg;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

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
    private String productBrand;
    private ProductCategory category;
    private List<ProductImg> images;


    public ResponseProductDto(Long id, String productNm, String productCode, int productPrice, int productQuantity, String productBrand, ProductCategory category, List<ProductImg> images) {
        this.id = id;
        this.productNm = productNm;
        this.productCode = productCode;
        this.productPrice = productPrice;
        this.productQuantity = productQuantity;
        this.productBrand = productBrand;
        this.category = category;
        this.images = images;
    }
}
