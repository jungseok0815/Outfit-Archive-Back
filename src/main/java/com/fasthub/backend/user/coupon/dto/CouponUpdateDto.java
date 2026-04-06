package com.fasthub.backend.user.coupon.dto;

import com.fasthub.backend.cmm.enums.CouponDiscountType;
import com.fasthub.backend.cmm.enums.ProductCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private List<ProductCategory> targetCategories = new ArrayList<>();  // 적용 카테고리 (비어있으면 전체)
    private List<Long> targetBrandIds = new ArrayList<>();               // 적용 브랜드 ID (비어있으면 전체)
}
