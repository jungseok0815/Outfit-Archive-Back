package com.fasthub.backend.admin.order.mapper;

import com.fasthub.backend.admin.order.dto.ResponseOrderDto;
import com.fasthub.backend.admin.order.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "userId", source = "user.userId")
    @Mapping(target = "userNm", source = "user.userNm")
    @Mapping(target = "productNm", source = "product.productNm")
    @Mapping(target = "productImgPath", expression = "java(getFirstImgPath(order))")
    @Mapping(target = "trackingNumber", source = "trackingNumber")
    ResponseOrderDto orderToResponseDto(Order order);

    default String getFirstImgPath(Order order) {
        if (order.getProduct().getImages() == null || order.getProduct().getImages().isEmpty()) return null;
        return order.getProduct().getImages().get(0).getImgPath();
    }
}
