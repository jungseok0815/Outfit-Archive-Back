package com.fasthub.backend.user.coupon.entity;

import com.fasthub.backend.cmm.enums.CouponDiscountType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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
    private int discountValue;      // 정액: 할인 금액 / 정률: 할인율(%)

    @Column(nullable = false)
    private int minOrderPrice;      // 최소 주문금액

    @Column(nullable = false)
    private int maxDiscountPrice;   // 최대 할인금액 (정률일 때 캡, 정액이면 무시)

    @Column(nullable = false)
    private int totalQuantity;      // 총 발급 가능 수량

    @Column(nullable = false)
    private int issuedCount;        // 현재 발급된 수량

    @Column(nullable = false)
    private LocalDateTime startAt;

    @Column(nullable = false)
    private LocalDateTime endAt;

    public void increaseIssuedCount() {
        this.issuedCount++;
    }

    public void update(String name, CouponDiscountType discountType, int discountValue,
                       int minOrderPrice, int maxDiscountPrice, int totalQuantity,
                       LocalDateTime startAt, LocalDateTime endAt) {
        this.name = name;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.minOrderPrice = minOrderPrice;
        this.maxDiscountPrice = maxDiscountPrice;
        this.totalQuantity = totalQuantity;
        this.startAt = startAt;
        this.endAt = endAt;
    }

    // 발급 가능 여부: 수량 & 기간 모두 충족해야 함
    public boolean isAvailable() {
        LocalDateTime now = LocalDateTime.now();
        return issuedCount < totalQuantity
                && now.isAfter(startAt)
                && now.isBefore(endAt);
    }

    // 주문금액 기준 할인 금액 계산
    public int calculateDiscount(int orderPrice) {
        if (discountType == CouponDiscountType.FIXED) {
            return discountValue;
        }
        // 정률: 비율 적용 후 최대 할인금액 캡
        int discount = orderPrice * discountValue / 100;
        return Math.min(discount, maxDiscountPrice);
    }
}
