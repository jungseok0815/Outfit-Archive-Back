package com.fasthub.backend.admin.brand.mapper;

import com.fasthub.backend.admin.brand.dto.InsertBrandDto;
import com.fasthub.backend.admin.brand.dto.ResponseBrandDto;
import com.fasthub.backend.admin.brand.entity.Brand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BrandMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "images", ignore = true)
    Brand insertDtoToBrand(InsertBrandDto insertBrandDto);

    @Mapping(target = "imgPath", expression = "java(brand.getImages().isEmpty() ? null : brand.getImages().get(0).getImgPath())")
    @Mapping(target = "imgNm", expression = "java(brand.getImages().isEmpty() ? null : brand.getImages().get(0).getImgNm())")
    @Mapping(target = "imgOriginNm", expression = "java(brand.getImages().isEmpty() ? null : brand.getImages().get(0).getImgOriginNm())")
    ResponseBrandDto brandToResponseDto(Brand brand);
}
