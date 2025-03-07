package com.fasthub.backend.oper.brand.mapper;

import com.fasthub.backend.oper.brand.dto.InsertBrandDto;
import com.fasthub.backend.oper.brand.entity.Brand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BrandMapper {

    Brand brandDtoToBrand(InsertBrandDto insertBrandDto);
}
