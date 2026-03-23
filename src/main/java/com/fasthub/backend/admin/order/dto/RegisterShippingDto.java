package com.fasthub.backend.admin.order.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class RegisterShippingDto {
    private Long id;
    private String trackingNumber;
}
