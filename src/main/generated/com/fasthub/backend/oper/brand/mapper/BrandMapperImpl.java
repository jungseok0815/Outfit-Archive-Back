package com.fasthub.backend.oper.brand.mapper;

import com.fasthub.backend.oper.brand.dto.InsertBrandDto;
import com.fasthub.backend.oper.brand.dto.UpdateBrandDto;
import com.fasthub.backend.oper.brand.entity.Brand;
import com.fasthub.backend.oper.brand.entity.BrandImg;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-02-06T13:43:30+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.13 (Amazon.com Inc.)"
)
@Component
public class BrandMapperImpl implements BrandMapper {

    @Override
    public Brand insertBrandDtoToBrand(InsertBrandDto insertBrandDto) {
        if ( insertBrandDto == null ) {
            return null;
        }

        Brand.BrandBuilder brand = Brand.builder();

        brand.brandNm( insertBrandDto.getBrandNm() );
        brand.brandNum( insertBrandDto.getBrandNum() );
        brand.brandLocation( insertBrandDto.getBrandLocation() );
        brand.brandDc( insertBrandDto.getBrandDc() );
        brand.brandImg( multipartFileToBrandImg( insertBrandDto.getBrandImg() ) );

        return brand.build();
    }

    @Override
    public Brand updateBrandDtoToBrand(UpdateBrandDto updateBrandDto) {
        if ( updateBrandDto == null ) {
            return null;
        }

        Brand.BrandBuilder brand = Brand.builder();

        brand.id( updateBrandDto.getId() );
        brand.brandNm( updateBrandDto.getBrandNm() );
        brand.brandNum( updateBrandDto.getBrandNum() );
        brand.brandLocation( updateBrandDto.getBrandLocation() );
        brand.brandDc( updateBrandDto.getBrandDc() );
        brand.brandImg( multipartFileToBrandImg( updateBrandDto.getBrandImg() ) );

        return brand.build();
    }

    protected BrandImg multipartFileToBrandImg(MultipartFile multipartFile) {
        if ( multipartFile == null ) {
            return null;
        }

        BrandImg brandImg = new BrandImg();

        return brandImg;
    }
}
