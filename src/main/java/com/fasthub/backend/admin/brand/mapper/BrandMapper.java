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

    @Mapping(target = "brandImg", expression = "java(brand.getImages().isEmpty() ? null : brand.getImages().get(0))")
    ResponseBrandDto brandToResponseDto(Brand brand);
}
