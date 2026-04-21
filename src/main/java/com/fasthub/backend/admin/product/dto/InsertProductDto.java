package com.fasthub.backend.admin.product.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class InsertProductDto {
    private String productNm;
    private String productEnNm;
    private String productCode;
    private int productPrice;
    private int productQuantity;
    private Long brandId;
    private Long categoryId;
    private List<MultipartFile> image;
    private String sizesJson;
}
