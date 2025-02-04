package com.fasthub.backend.oper.product.mapper;

import com.fasthub.backend.oper.product.dto.InsertProductDto;
import com.fasthub.backend.oper.product.dto.ProductDto;
import com.fasthub.backend.oper.product.dto.ResponseProductDto;
import com.fasthub.backend.oper.product.dto.UpdateProductDto;
import com.fasthub.backend.oper.product.entity.Product;
import org.hibernate.sql.Update;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    ResponseProductDto productToProductDto(Product product);

    Product productDtoToProduct(UpdateProductDto productDto);

    @Mapping(target = "id", ignore = true)
    Product InsertproductDtoToProduct(InsertProductDto productDto);
}
