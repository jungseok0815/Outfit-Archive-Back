package com.fasthub.backend.oper.brand.mapper;

import com.fasthub.backend.oper.brand.dto.BrandDto;
import com.fasthub.backend.oper.brand.dto.InsertBrandDto;
import com.fasthub.backend.oper.brand.dto.ResponseBrandDto;
import com.fasthub.backend.oper.brand.dto.UpdateBrandDto;
import com.fasthub.backend.oper.brand.entity.Brand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BrandMapper {

    @Mapping(target = "id", ignore = true)
    Brand insertBrandDtoToBrand(InsertBrandDto insertBrandDto);

    Brand updateBrandDtoToBrand(UpdateBrandDto updateBrandDto);

    ResponseBrandDto brandEntityToResponseBrandDto(Brand brand);
}
