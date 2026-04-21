package com.fasthub.backend.user.coupon.dto;

import com.fasthub.backend.cmm.enums.CouponDiscountType;
import com.fasthub.backend.user.coupon.entity.UserCoupon;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

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
    @JsonProperty("isUsed")
    private boolean isUsed;
    private LocalDateTime issuedAt;
    private LocalDateTime expiredAt;
    private List<Long> targetCategoryIds;
    private List<Long> targetBrandIds;

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
                .targetCategoryIds(uc.getCoupon().getTargetCategoryIds())
                .targetBrandIds(uc.getCoupon().getTargetBrandIds())
                .build();
    }
}
