package com.fasthub.backend.admin.order.dto;

import com.fasthub.backend.cmm.enums.OrderStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
public class ResponseOrderDto {
    private Long id;
    private String userId;
    private String userNm;
    private String productNm;
    private int quantity;
    private int totalPrice;
    private OrderStatus status;
    private LocalDateTime orderDate;
    private String shippingAddress;
    private String recipientName;
    private String recipientPhone;
}
