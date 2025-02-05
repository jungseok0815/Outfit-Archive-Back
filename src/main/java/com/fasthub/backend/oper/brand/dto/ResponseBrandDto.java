package com.fasthub.backend.oper.brand.dto;

import com.fasthub.backend.oper.brand.entity.BrandImg;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;
@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class ResponseBrandDto {
    private Long id;
    private String brandNm;
    private String brandNum;
    private String brandLocation;
    private String brandDc;
    private BrandImg brandImg;
}
