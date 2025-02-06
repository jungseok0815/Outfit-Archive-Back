package com.fasthub.backend.oper.order.mapper;

import com.fasthub.backend.oper.order.entity.Order;
import com.fasthub.backend.oper.product.dto.InsertProductDto;
import com.fasthub.backend.oper.product.dto.UpdateProductDto;
import com.fasthub.backend.oper.product.entity.Product;
import com.fasthub.backend.oper.order.dto.ResponseOrderDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(target = "id", ignore = true)
    ResponseOrderDto productToProductDto(Order sale);

    Product productDtoToProduct(UpdateProductDto productDto);

    @Mapping(target = "id", ignore = true)
    Product InsertproductDtoToProduct(InsertProductDto productDto);
}
