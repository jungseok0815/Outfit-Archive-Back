package com.fasthub.backend.admin.product.mapper;

import com.fasthub.backend.admin.product.dto.InsertProductDto;
import com.fasthub.backend.admin.product.dto.ResponseProductDto;
import com.fasthub.backend.admin.product.dto.UpdateProductDto;
import com.fasthub.backend.admin.product.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ResponseProductDto productToProductDto(Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "images", ignore = true)
    Product insertProductDtoToProduct(InsertProductDto productDto);
}
