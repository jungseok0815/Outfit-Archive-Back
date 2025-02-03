package com.fasthub.backend.oper.product.dto;

import com.fasthub.backend.cmm.enums.ProductCategory;
import com.fasthub.backend.oper.product.entity.Product;
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


    public ResponseProductDto(Product product) {
        this.id = product.getId();
        this.productNm = product.getProductNm();
        this.productCode = product.getProductCode();
        this.productPrice = product.getProductPrice();
        this.productQuantity = product.getProductQuantity();
        this.productBrand = product.getProductBrand();
        this.category = product.getCategory();
        this.images = product.getImages();
    }
}
