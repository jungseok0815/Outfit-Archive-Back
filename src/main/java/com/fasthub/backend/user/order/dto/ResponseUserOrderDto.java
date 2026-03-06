package com.fasthub.backend.user.order.dto;

import com.fasthub.backend.admin.order.entity.Order;
import com.fasthub.backend.cmm.enums.OrderStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ResponseUserOrderDto {
    private Long orderId;
    private String productNm;
    private String brandNm;
    private int quantity;
    private int totalPrice;
    private int usedPoint;
    private int earnedPoint;
    private int actualPayment;  // totalPrice - usedPoint
    private OrderStatus status;
    private LocalDateTime orderDate;

    public static ResponseUserOrderDto of(Order order, int earnedPoint) {
        return ResponseUserOrderDto.builder()
                .orderId(order.getId())
                .productNm(order.getProduct().getProductNm())
                .brandNm(order.getProduct().getBrand() != null ? order.getProduct().getBrand().getBrandNm() : null)
                .quantity(order.getQuantity())
                .totalPrice(order.getTotalPrice())
                .usedPoint(order.getUsedPoint())
                .earnedPoint(earnedPoint)
                .actualPayment(order.getTotalPrice() - order.getUsedPoint())
                .status(order.getStatus())
                .orderDate(order.getOrderDate())
                .build();
    }
}
