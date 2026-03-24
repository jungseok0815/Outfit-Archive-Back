package com.fasthub.backend.user.coupon.dto;

import com.fasthub.backend.cmm.enums.CouponDiscountType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CouponCreateDto {
    private String code;             // 쿠폰 코드 (ex. SUMMER2024)
    private String name;             // 쿠폰명 (ex. 여름 할인 쿠폰)
    private CouponDiscountType discountType;   // FIXED or PERCENT
    private int discountValue;       // 정액: 할인 금액 / 정률: 할인율(%)
    private int minOrderPrice;       // 최소 주문금액
    private int maxDiscountPrice;    // 최대 할인금액 (정률일 때 캡)
    private int totalQuantity;       // 총 발급 가능 수량
    private LocalDateTime startAt;   // 쿠폰 유효 시작일
    private LocalDateTime endAt;     // 쿠폰 유효 종료일
}
