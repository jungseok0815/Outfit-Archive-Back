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
public class CouponCreateDto {
    private String code;                         // 쿠폰 코드 (ex. SUMMER2024)
    private String name;                         // 쿠폰명 (ex. 여름 할인 쿠폰)
    private CouponDiscountType discountType;     // FIXED or PERCENT
    private int discountValue;                   // 정액: 할인 금액 / 정률: 할인율(%)
    private int minOrderPrice;                   // 최소 주문금액
    private int maxDiscountPrice;               // 최대 할인금액 (정률일 때 캡)
    private int totalQuantity;                   // 총 발급 가능 수량
    private LocalDateTime startAt;              // 쿠폰 유효 시작일
    private LocalDateTime endAt;               // 쿠폰 유효 종료일
    private List<ProductCategory> targetCategories = new ArrayList<>();  // 적용 카테고리 (비어있으면 전체)
    private List<Long> targetBrandIds = new ArrayList<>();               // 적용 브랜드 ID (비어있으면 전체)
}
