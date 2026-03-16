package com.fasthub.backend.user.payment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentConfirmRequestDto {
    private String paymentKey;  // 토스에서 받은 결제 키
    private String orderId;     // 주문 준비 시 생성한 tossOrderId
    private int amount;         // 실제 결제 금액 (프론트에서 전달)
}
