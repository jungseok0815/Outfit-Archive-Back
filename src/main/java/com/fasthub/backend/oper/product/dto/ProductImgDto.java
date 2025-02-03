package com.fasthub.backend.oper.product.dto;

import com.fasthub.backend.oper.product.entity.Product;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

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
