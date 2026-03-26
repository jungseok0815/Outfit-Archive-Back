package com.fasthub.backend.admin.product.mapper;

import com.fasthub.backend.admin.product.dto.ProductSizeDto;
import com.fasthub.backend.admin.product.dto.ResponseProductDto;
import com.fasthub.backend.admin.product.dto.ResponseProductImgDto;
import com.fasthub.backend.admin.product.entity.Product;
import com.fasthub.backend.admin.product.entity.ProductImg;
import com.fasthub.backend.admin.product.entity.ProductSize;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "brandId", source = "brand.id")
    @Mapping(target = "brandNm", source = "brand.brandNm")
    @Mapping(target = "images", source = "images")
    @Mapping(target = "sizes", source = "sizes")
    ResponseProductDto productToProductDto(Product product);

    ResponseProductImgDto productImgToDto(ProductImg productImg);

    ProductSizeDto productSizeToDto(ProductSize productSize);
}
