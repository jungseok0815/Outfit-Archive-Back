package com.fasthub.backend.user.coupon.dto;

import com.fasthub.backend.cmm.enums.CouponDiscountType;
import com.fasthub.backend.user.coupon.entity.UserCoupon;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserCouponDto {
    private Long userCouponId;
    private String couponName;
    private String couponCode;
    private CouponDiscountType discountType;
    private int discountValue;
    private int minOrderPrice;
    private int maxDiscountPrice;
    private boolean isUsed;
    private LocalDateTime issuedAt;
    private LocalDateTime expiredAt;

    public static UserCouponDto of(UserCoupon uc) {
        return UserCouponDto.builder()
                .userCouponId(uc.getId())
                .couponName(uc.getCoupon().getName())
                .couponCode(uc.getCoupon().getCode())
                .discountType(uc.getCoupon().getDiscountType())
                .discountValue(uc.getCoupon().getDiscountValue())
                .minOrderPrice(uc.getCoupon().getMinOrderPrice())
                .maxDiscountPrice(uc.getCoupon().getMaxDiscountPrice())
                .isUsed(uc.isUsed())
                .issuedAt(uc.getIssuedAt())
                .expiredAt(uc.getExpiredAt())
                .build();
    }
}
