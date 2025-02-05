package com.fasthub.backend.oper.brand.brandMapper;

import com.fasthub.backend.oper.brand.dto.InsertBrandDto;
import com.fasthub.backend.oper.brand.dto.UpdateBrandDto;
import com.fasthub.backend.oper.brand.entity.Brand;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-02-05T16:24:56+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.13 (Amazon.com Inc.)"
)
@Component
public class BrandMapperImpl implements BrandMapper {

    @Override
    public Brand brandDtoToBrand(InsertBrandDto insertBrandDto) {
        if ( insertBrandDto == null ) {
            return null;
        }

        Brand brand = new Brand();

        return brand;
    }

    @Override
    public Brand bradndDtoToBrand(UpdateBrandDto updateBrandDto) {
        if ( updateBrandDto == null ) {
            return null;
        }

        Brand brand = new Brand();

        return brand;
    }
}
