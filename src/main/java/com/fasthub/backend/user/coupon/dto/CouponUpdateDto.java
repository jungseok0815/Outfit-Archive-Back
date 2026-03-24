package com.fasthub.backend.user.coupon.dto;

import com.fasthub.backend.cmm.enums.CouponDiscountType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CouponUpdateDto {
    private String name;
    private CouponDiscountType discountType;
    private int discountValue;
    private int minOrderPrice;
    private int maxDiscountPrice;
    private int totalQuantity;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
}
