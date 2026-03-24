package com.fasthub.backend.user.coupon.dto;

import com.fasthub.backend.cmm.enums.CouponDiscountType;
import com.fasthub.backend.user.coupon.entity.Coupon;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CouponDto {
    private Long id;
    private String code;
    private String name;
    private CouponDiscountType discountType;
    private int discountValue;
    private int minOrderPrice;
    private int maxDiscountPrice;
    private int totalQuantity;
    private int issuedCount;
    private LocalDateTime startAt;
    private LocalDateTime endAt;

    public static CouponDto of(Coupon coupon) {
        return CouponDto.builder()
                .id(coupon.getId())
                .code(coupon.getCode())
                .name(coupon.getName())
                .discountType(coupon.getDiscountType())
                .discountValue(coupon.getDiscountValue())
                .minOrderPrice(coupon.getMinOrderPrice())
                .maxDiscountPrice(coupon.getMaxDiscountPrice())
                .totalQuantity(coupon.getTotalQuantity())
                .issuedCount(coupon.getIssuedCount())
                .startAt(coupon.getStartAt())
                .endAt(coupon.getEndAt())
                .build();
    }
}
