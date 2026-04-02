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
    private String tossOrderId;  // 토스 결제창에 전달할 주문번호
    private String productNm;
    private String brandNm;
    private String productImgPath;  // 상품 대표 이미지
    private int quantity;
    private int totalPrice;
    private int usedPoint;
    private int couponDiscount;
    private int earnedPoint;
    private int actualPayment;  // totalPrice - usedPoint - couponDiscount
    private OrderStatus status;
    private LocalDateTime orderDate;
    private String sizeNm;
    private String trackingNumber;
    private LocalDateTime deliveredDate;

    public static ResponseUserOrderDto of(Order order, int earnedPoint) {
        String imgPath = order.getProduct().getImages() != null && !order.getProduct().getImages().isEmpty()
                ? order.getProduct().getImages().get(0).getImgPath()
                : null;

        return ResponseUserOrderDto.builder()
                .orderId(order.getId())
                .tossOrderId(order.getTossOrderId())
                .productNm(order.getProduct().getProductNm())
                .brandNm(order.getProduct().getBrand() != null ? order.getProduct().getBrand().getBrandNm() : null)
                .productImgPath(imgPath)
                .quantity(order.getQuantity())
                .totalPrice(order.getTotalPrice())
                .usedPoint(order.getUsedPoint())
                .couponDiscount(order.getCouponDiscount())
                .earnedPoint(earnedPoint)
                .actualPayment(order.getTotalPrice() - order.getUsedPoint() - order.getCouponDiscount())
                .status(order.getStatus())
                .orderDate(order.getOrderDate())
                .sizeNm(order.getSizeNm())
                .trackingNumber(order.getTrackingNumber())
                .deliveredDate(order.getDeliveredAt())
                .build();
    }
}
