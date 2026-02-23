package com.fasthub.backend.admin.product.dto;

import com.fasthub.backend.cmm.enums.ProductCategory;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class InsertProductDto {
    private String productNm;
    private String productCode;
    private int productPrice;
    private int productQuantity;
    private Long brandId;
    private ProductCategory category;
    private List<MultipartFile> image;
}
