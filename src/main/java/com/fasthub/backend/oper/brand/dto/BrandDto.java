package com.fasthub.backend.oper.brand.dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class BrandDto {
    private Long id;
    private String brandNm;
    private String brandNum;
    private String brandLocation;
    private String brandDc;
    private String brandImg;
}
