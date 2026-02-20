package com.fasthub.backend.admin.brand.dto;

import com.fasthub.backend.admin.brand.entity.BrandImg;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
