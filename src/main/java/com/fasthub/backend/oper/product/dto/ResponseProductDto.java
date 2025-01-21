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
    private ProductCategory category;
    private int productPrice;
    private List<ProductImg> images;
    private int productAuantity;

    public ResponseProductDto(Long id, String productNm, ProductCategory category, int productPrice, List<ProductImg> images, int productAuantity) {
        this.id = id;
        this.productNm = productNm;
        this.category = category;
        this.productPrice = productPrice;
        this.images = images;
        this.productAuantity = productAuantity;
    }
}
