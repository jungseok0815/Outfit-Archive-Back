package com.fasthub.backend.oper.product.dto;

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
    private MultipartFile img;
}
