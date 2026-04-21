package com.fasthub.backend.user.coupon.entity;

import com.fasthub.backend.cmm.enums.CouponDiscountType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@Table(name = "coupon")
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponDiscountType discountType;

    @Column(nullable = false)
    private int discountValue;

    @Column(nullable = false)
    private int minOrderPrice;

    @Column(nullable = false)
    private int maxDiscountPrice;

    @Column(nullable = false)
    private int totalQuantity;

    @Column(nullable = false)
    private int issuedCount;

    @Column(nullable = false)
    private LocalDateTime startAt;

    @Column(nullable = false)
    private LocalDateTime endAt;

    // 적용 가능 카테고리 ID (비어있으면 전체 적용)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "coupon_target_categories", joinColumns = @JoinColumn(name = "coupon_id"))
    @Column(name = "category_id")
    @Builder.Default
    private List<Long> targetCategoryIds = new ArrayList<>();

    // 적용 가능 브랜드 ID (비어있으면 전체 적용)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "coupon_target_brand_ids", joinColumns = @JoinColumn(name = "coupon_id"))
    @Column(name = "brand_id")
    @Builder.Default
    private List<Long> targetBrandIds = new ArrayList<>();

    public void increaseIssuedCount() {
        this.issuedCount++;
    }

    public void update(String name, CouponDiscountType discountType, int discountValue,
                       int minOrderPrice, int maxDiscountPrice, int totalQuantity,
                       LocalDateTime startAt, LocalDateTime endAt,
                       List<Long> targetCategoryIds, List<Long> targetBrandIds) {
        this.name = name;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.minOrderPrice = minOrderPrice;
        this.maxDiscountPrice = maxDiscountPrice;
        this.totalQuantity = totalQuantity;
        this.startAt = startAt;
        this.endAt = endAt;
        this.targetCategoryIds.clear();
        this.targetCategoryIds.addAll(targetCategoryIds);
        this.targetBrandIds.clear();
        this.targetBrandIds.addAll(targetBrandIds);
    }

    public boolean isAvailable() {
        LocalDateTime now = LocalDateTime.now();
        return issuedCount < totalQuantity
                && now.isAfter(startAt)
                && now.isBefore(endAt);
    }

    public int calculateDiscount(int orderPrice) {
        if (discountType == CouponDiscountType.FIXED) {
            return discountValue;
        }
        int discount = orderPrice * discountValue / 100;
        return Math.min(discount, maxDiscountPrice);
    }

    public boolean isApplicableToProduct(Long categoryId, Long productBrandId) {
        if (targetCategoryIds.isEmpty() && targetBrandIds.isEmpty()) return true;
        if (!targetCategoryIds.isEmpty() && targetCategoryIds.contains(categoryId)) return true;
        if (!targetBrandIds.isEmpty() && targetBrandIds.contains(productBrandId)) return true;
        return false;
    }
}
