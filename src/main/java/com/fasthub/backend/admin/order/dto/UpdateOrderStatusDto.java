package com.fasthub.backend.admin.order.dto;

import com.fasthub.backend.cmm.enums.OrderStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class UpdateOrderStatusDto {
    private Long id;
    private OrderStatus status;
}
