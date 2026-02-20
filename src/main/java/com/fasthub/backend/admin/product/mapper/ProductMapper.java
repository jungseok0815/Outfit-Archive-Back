package com.fasthub.backend.admin.product.mapper;

import com.fasthub.backend.admin.product.dto.InsertProductDto;
import com.fasthub.backend.admin.product.dto.ResponseProductDto;
import com.fasthub.backend.admin.product.dto.UpdateProductDto;
import com.fasthub.backend.admin.product.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    ResponseProductDto productToProductDto(Product product);

    Product productDtoToProduct(UpdateProductDto productDto);

    @Mapping(target = "id", ignore = true)
    Product InsertproductDtoToProduct(InsertProductDto productDto);
}
