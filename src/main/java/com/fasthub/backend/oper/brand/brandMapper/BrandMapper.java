package com.fasthub.backend.oper.brand.brandMapper;

import com.fasthub.backend.oper.brand.dto.InsertBrandDto;
import com.fasthub.backend.oper.brand.dto.UpdateBrandDto;
import com.fasthub.backend.oper.brand.entity.Brand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BrandMapper {

    @Mapping(target = "id", ignore = true)
    Brand brandDtoToBrand(InsertBrandDto insertBrandDto);

    Brand bradndDtoToBrand(UpdateBrandDto updateBrandDto);
}
