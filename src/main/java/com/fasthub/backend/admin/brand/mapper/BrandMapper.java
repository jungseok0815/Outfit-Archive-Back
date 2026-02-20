package com.fasthub.backend.admin.brand.mapper;

import com.fasthub.backend.admin.brand.dto.InsertBrandDto;
import com.fasthub.backend.admin.brand.entity.Brand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BrandMapper {

    Brand brandDtoToBrand(InsertBrandDto insertBrandDto);
}
