package com.fasthub.backend.user.order.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class InsertUserOrderDto {
    private Long productId;
    private int quantity;
    private String shippingAddress;
    private String recipientName;
    private String recipientPhone;
    private int usePoint;        // 사용할 포인트 (0이면 미사용)
    private Long userCouponId;   // 사용할 쿠폰 ID (null이면 미사용)
    private String sizeNm;       // 선택 사이즈 (사이즈 없는 상품은 null)
}
