package com.fasthub.backend.user.coupon.dto;

import com.fasthub.backend.cmm.enums.CouponDiscountType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class CouponCreateDto {
    private String code;
    private String name;
    private CouponDiscountType discountType;
    private int discountValue;
    private int minOrderPrice;
    private int maxDiscountPrice;
    private int totalQuantity;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private List<Long> targetCategoryIds = new ArrayList<>();
    private List<Long> targetBrandIds = new ArrayList<>();
}
