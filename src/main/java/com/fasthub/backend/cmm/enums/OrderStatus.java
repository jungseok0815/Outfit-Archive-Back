package com.fasthub.backend.cmm.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {
    PAYMENT_COMPLETE("결제완료"),
    SHIPPING("배송중"),
    DELIVERED("배송완료"),
    CANCELLED("취소");

    private final String korName;
}
